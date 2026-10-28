"""Watch Tomcat's daily Catalina log and report distinct runtime errors."""

from __future__ import annotations

import argparse
import hashlib
import json
import os
import re
import sys
import time
from datetime import datetime
from pathlib import Path
from typing import Sequence

DEFAULT_LOG_DIRECTORY = Path(
    r"C:\Program Files\Apache Software Foundation\Tomcat 10.1\logs"
)
DEFAULT_OUTPUT_DIRECTORY = Path("logs/ebx-errors")

TIMESTAMPED_ENTRY = re.compile(
    r"^(?:"
    r"\d{2}-[A-Za-z]{3}-\d{4}\s+\d{2}:\d{2}:\d{2}"
    r"|\d{4}-\d{2}-\d{2}[T ]\d{2}:\d{2}:\d{2}"
    r")(?:[.,]\d+)?(?:Z|[+-]\d{2}:?\d{2})?"
)
SEVERITY = re.compile(r"\b(?:ERROR|SEVERE|FATAL)\b", re.IGNORECASE)
EXCEPTION = re.compile(
    r"(?:\b[A-Za-z_$][\w$]*(?:Exception|Error)\b|Exception in thread)"
)
UUID = re.compile(
    r"\b[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\b",
    re.IGNORECASE,
)
HEX_ADDRESS = re.compile(r"\b0x[0-9a-f]+\b", re.IGNORECASE)
LINE_NUMBER = re.compile(r"(?<=\.java:)\d+\b")
THREAD_NAME = re.compile(r"\[[^\]\r\n]*\]")


def is_error_entry(line: str) -> bool:
    return bool(SEVERITY.search(line) or EXCEPTION.search(line))


def normalize_line(line: str) -> str:
    normalized = TIMESTAMPED_ENTRY.sub("", line).strip()
    normalized = THREAD_NAME.sub("[thread]", normalized)
    normalized = UUID.sub("<uuid>", normalized)
    normalized = HEX_ADDRESS.sub("<address>", normalized)
    return LINE_NUMBER.sub("<line>", normalized)


def error_signature(lines: Sequence[str]) -> tuple[str, str]:
    normalized = [normalize_line(line) for line in lines if line.strip()]
    identifying_lines = normalized[:1]
    stack_lines = [
        line
        for line in normalized[1:]
        if re.search(r"\bat\s+|^(?:Caused by|Suppressed):", line)
    ][:5]
    identifying_lines.extend(stack_lines)
    if len(identifying_lines) == 1:
        identifying_lines.extend(normalized[1:8])

    signature = "\n".join(identifying_lines) or "Unknown runtime error"
    fingerprint = hashlib.sha256(signature.encode("utf-8")).hexdigest()[:16]
    return fingerprint, signature


def troubleshooting_hints(error_text: str) -> list[str]:
    text = error_text.lower()
    hints: list[str] = []

    if "outofmemoryerror" in text or "java heap space" in text:
        hints.append(
            "Review heap usage and the surrounding workload; inspect a heap dump "
            "before changing JVM memory limits."
        )
    if "classnotfoundexception" in text or "noclassdeffounderror" in text:
        hints.append(
            "Check that the required library is available to the deployed EBX "
            "module and that its version matches the target EBX runtime."
        )
    if "nosuchmethoderror" in text or "nosuchfielderror" in text or "linkageerror" in text:
        hints.append(
            "Check for conflicting or incompatible library versions across the "
            "WAR and Tomcat/EBX runtime."
        )
    if "sqlexception" in text or "sqlstate" in text or "connection refused" in text:
        hints.append(
            "Check database availability, datasource configuration, credentials, "
            "and connection-pool health."
        )
    if "accessdeniedexception" in text or "permission denied" in text:
        hints.append(
            "Check filesystem permissions for the Tomcat service account and the "
            "affected path."
        )
    if "filenotfoundexception" in text or "no such file" in text:
        hints.append(
            "Verify that the referenced file or directory exists and is readable "
            "by the Tomcat service account."
        )
    if "address already in use" in text or "bindexception" in text:
        hints.append(
            "Check whether another process is using the configured Tomcat port "
            "and verify the connector configuration."
        )
    if "nullpointerexception" in text:
        hints.append(
            "Inspect the first application or EBX frame in the stack trace and "
            "check the relevant value and record state."
        )
    if "operationexception" in text or "validationexception" in text:
        hints.append(
            "Inspect the nested cause and EBX validation details; verify the "
            "affected data and the operation's permissions."
        )

    if not hints:
        hints.append(
            "Review the complete stack trace and nearby Tomcat/EBX log entries "
            "to identify the failing component and its underlying cause."
        )

    return hints


def markdown_fence(text: str) -> str:
    longest_run = max((len(run) for run in re.findall(r"`+", text)), default=0)
    fence = "`" * max(3, longest_run + 1)
    return fence


class ErrorReportStore:
    def __init__(self, output_directory: Path) -> None:
        self.output_directory = output_directory
        self.output_directory.mkdir(parents=True, exist_ok=True)
        self.index_path = self.output_directory / "index.json"

    def record(self, lines: Sequence[str], source: Path) -> Path:
        event = "\n".join(lines).strip()
        fingerprint, signature = error_signature(lines)
        now = datetime.now().astimezone().isoformat(timespec="seconds")
        index = self._read_index()
        entry = index.get(fingerprint)

        if entry is None:
            entry = {
                "signature": signature,
                "occurrences": 0,
                "first_seen": now,
                "samples": [],
            }

        entry["occurrences"] += 1
        entry["last_seen"] = now
        if event not in entry["samples"] and len(entry["samples"]) < 5:
            entry["samples"].append(event)
        index[fingerprint] = entry

        report_path = self.output_directory / f"ebx-error-{fingerprint}.md"
        report_path.write_text(
            self._render_report(fingerprint, entry, source),
            encoding="utf-8",
        )
        self._write_json_atomically(self.index_path, index)
        return report_path

    def _read_index(self) -> dict[str, dict[str, object]]:
        if not self.index_path.exists():
            return {}
        with self.index_path.open("r", encoding="utf-8") as index_file:
            value = json.load(index_file)
        if not isinstance(value, dict):
            raise ValueError(f"Invalid error report index: {self.index_path}")
        return value

    @staticmethod
    def _write_json_atomically(path: Path, value: object) -> None:
        temporary_path = path.with_suffix(".json.tmp")
        temporary_path.write_text(
            json.dumps(value, indent=2, ensure_ascii=False),
            encoding="utf-8",
        )
        os.replace(temporary_path, path)

    @staticmethod
    def _render_report(
        fingerprint: str, entry: dict[str, object], source: Path
    ) -> str:
        samples = entry["samples"]
        assert isinstance(samples, list)
        sample_text = "\n\n---\n\n".join(str(sample) for sample in samples)
        fence = markdown_fence(sample_text)
        hints = troubleshooting_hints(str(entry["signature"]))
        hint_list = "\n".join(f"- {hint}" for hint in hints)
        return (
            f"# EBX/Tomcat runtime error `{fingerprint}`\n\n"
            f"- **Fingerprint:** `{fingerprint}`\n"
            f"- **Occurrences:** {entry['occurrences']}\n"
            f"- **First seen:** {entry['first_seen']}\n"
            f"- **Last seen:** {entry['last_seen']}\n"
            f"- **Source log:** `{source}`\n\n"
            "## Possible troubleshooting steps\n\n"
            "These are diagnostic suggestions based on matching error text, "
            "not guaranteed fixes.\n\n"
            f"{hint_list}\n\n"
            "## Captured log samples\n\n"
            f"{fence}text\n{sample_text}\n{fence}\n"
        )


class TomcatLogWatcher:
    def __init__(
        self,
        log_directory: Path,
        report_store: ErrorReportStore,
        poll_interval: float,
    ) -> None:
        self.log_directory = log_directory
        self.report_store = report_store
        self.poll_interval = poll_interval
        self.active_entry: list[str] = []
        self.active_source: Path | None = None
        self.reading_path: Path | None = None

    def run(self) -> None:
        watched_path: Path | None = None
        byte_offset = 0
        file_identity: tuple[int, int] | None = None

        try:
            while True:
                current_path = self.log_directory / (
                    f"catalina.{datetime.now():%Y-%m-%d}.log"
                )
                if current_path != watched_path:
                    is_initial_file = watched_path is None
                    self._flush_entry()
                    watched_path = current_path
                    byte_offset = 0
                    file_identity = None
                    if current_path.exists():
                        stat = current_path.stat()
                        file_identity = (stat.st_dev, stat.st_ino)
                        if is_initial_file:
                            byte_offset = stat.st_size
                    print(f"Watching {current_path}", flush=True)

                if current_path.exists():
                    stat = current_path.stat()
                    current_identity = (stat.st_dev, stat.st_ino)
                    if (
                        file_identity != current_identity
                        or stat.st_size < byte_offset
                    ):
                        self._flush_entry()
                        byte_offset = 0
                        file_identity = current_identity
                    byte_offset = self._read_new_lines(current_path, byte_offset)
                time.sleep(self.poll_interval)
        finally:
            self._flush_entry()

    def _read_new_lines(self, path: Path, byte_offset: int) -> int:
        self.reading_path = path
        with path.open("rb") as log_file:
            log_file.seek(byte_offset)
            while True:
                raw_line = log_file.readline()
                if not raw_line:
                    break
                if not raw_line.endswith(b"\n"):
                    log_file.seek(log_file.tell() - len(raw_line))
                    break
                self._accept_line(raw_line.decode("utf-8", errors="replace").rstrip())
            return log_file.tell()

    def _accept_line(self, line: str) -> None:
        if TIMESTAMPED_ENTRY.match(line):
            self._flush_entry()
            if is_error_entry(line):
                self.active_entry = [line]
                self.active_source = self.reading_path or self.current_source
            return

        if self.active_entry:
            self.active_entry.append(line)
        elif is_error_entry(line):
            self.active_entry = [line]
            self.active_source = self.reading_path or self.current_source

    @property
    def current_source(self) -> Path:
        return self.log_directory / f"catalina.{datetime.now():%Y-%m-%d}.log"

    def _flush_entry(self) -> None:
        if self.active_entry:
            report = self.report_store.record(
                self.active_entry,
                self.active_source or self.current_source,
            )
            print(f"Recorded runtime error: {report}", flush=True)
            self.active_entry = []
            self.active_source = None


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description=(
            "Watch Tomcat's daily Catalina log and create one report per "
            "distinct runtime error."
        )
    )
    parser.add_argument(
        "--log-directory",
        type=Path,
        default=DEFAULT_LOG_DIRECTORY,
        help="Directory containing catalina.YYYY-MM-dd.log",
    )
    parser.add_argument(
        "--output-directory",
        type=Path,
        default=DEFAULT_OUTPUT_DIRECTORY,
        help="Directory for fingerprinted error reports",
    )
    parser.add_argument(
        "--poll-interval",
        type=float,
        default=0.5,
        help="Seconds between checks for new log lines (default: 0.5)",
    )
    return parser


def main(argv: Sequence[str] | None = None) -> int:
    args = build_parser().parse_args(argv)
    if args.poll_interval <= 0:
        print("--poll-interval must be greater than zero", file=sys.stderr)
        return 2
    if not args.log_directory.is_dir():
        print(
            f"Tomcat log directory does not exist: {args.log_directory}",
            file=sys.stderr,
        )
        return 2

    try:
        store = ErrorReportStore(args.output_directory)
        print(
            "TOMCAT_LOG_MONITOR_READY "
            f"(reports: {args.output_directory.resolve()})",
            flush=True,
        )
        TomcatLogWatcher(
            args.log_directory,
            store,
            args.poll_interval,
        ).run()
    except KeyboardInterrupt:
        print("Tomcat log monitor stopped.", flush=True)
        return 0
    except (OSError, ValueError, json.JSONDecodeError) as error:
        print(f"Tomcat log monitor failed: {error}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
