"""Tests for Tomcat error grouping and report generation."""

import importlib.util
import json
import tempfile
import unittest
from pathlib import Path

SCRIPT_PATH = (
    Path(__file__).resolve().parents[2]
    / "tools"
    / "tomcat-log-monitor"
    / "watch_tomcat_errors.py"
)
SPEC = importlib.util.spec_from_file_location("watch_tomcat_errors", SCRIPT_PATH)
if SPEC is None or SPEC.loader is None:
    raise ImportError(f"Cannot load Tomcat log monitor from {SCRIPT_PATH}")

MONITOR = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(MONITOR)


class ErrorReportStoreTests(unittest.TestCase):
    def test_fingerprint_ignores_timestamp_thread_and_line_number(self) -> None:
        first = [
            "23-Oct-2026 10:00:00.001 SEVERE [http-nio-8080-exec-1] "
            "java.lang.NullPointerException: missing value",
            "\tat example.Service.run(Service.java:42)",
        ]
        repeated = [
            "23-Oct-2026 10:05:17.999 SEVERE [http-nio-8080-exec-9] "
            "java.lang.NullPointerException: missing value",
            "\tat example.Service.run(Service.java:88)",
        ]

        self.assertEqual(
            MONITOR.error_signature(first)[0],
            MONITOR.error_signature(repeated)[0],
        )

    def test_repeated_error_updates_same_report_and_count(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            store = MONITOR.ErrorReportStore(Path(directory))
            source = Path("catalina.2026-10-23.log")
            first_event = [
                "23-Oct-2026 10:00:00.000 SEVERE [main] "
                "java.lang.NullPointerException: missing value",
                "\tat example.Service.run(Service.java:42)",
            ]
            repeated_event = [
                "23-Oct-2026 10:05:00.000 SEVERE [main] "
                "java.lang.NullPointerException: missing value",
                "\tat example.Service.run(Service.java:88)",
            ]

            first_report = store.record(first_event, source)
            second_report = store.record(repeated_event, source)

            self.assertEqual(first_report, second_report)
            report = second_report.read_text(encoding="utf-8")
            self.assertIn("**Occurrences:** 2", report)
            self.assertIn("NullPointerException", report)
            index = json.loads((Path(directory) / "index.json").read_text())
            self.assertEqual(len(index), 1)

    def test_different_error_signatures_create_separate_reports(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            store = MONITOR.ErrorReportStore(Path(directory))
            source = Path("catalina.2026-10-23.log")

            first = store.record(
                ["23-Oct-2026 10:00:00.000 SEVERE SQL connection failed"],
                source,
            )
            second = store.record(
                ["23-Oct-2026 10:01:00.000 SEVERE Port bind failed"],
                source,
            )

            self.assertNotEqual(first, second)
            self.assertTrue(first.exists())
            self.assertTrue(second.exists())

    def test_hints_include_specific_cause_when_recognized(self) -> None:
        hints = MONITOR.troubleshooting_hints(
            "java.lang.ClassNotFoundException: missing dependency"
        )

        self.assertTrue(any("library" in hint for hint in hints))

    def test_watcher_waits_for_complete_lines_and_flushes_pending_error(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            log_path = root / "catalina.2026-10-23.log"
            log_path.write_bytes(
                b"23-Oct-2026 10:00:00.000 SEVERE [main] "
                b"java.lang.NullPointerException: missing value"
            )
            store = MONITOR.ErrorReportStore(root / "reports")
            watcher = MONITOR.TomcatLogWatcher(root, store, 0.1)

            offset = watcher._read_new_lines(log_path, 0)
            self.assertEqual(offset, 0)
            self.assertEqual(watcher.active_entry, [])

            with log_path.open("ab") as log_file:
                log_file.write(b"\n\tat example.Service.run(Service.java:42)\n")
            watcher._read_new_lines(log_path, offset)
            watcher._flush_entry()

            reports = list((root / "reports").glob("ebx-error-*.md"))
            self.assertEqual(len(reports), 1)
            self.assertIn(str(log_path), reports[0].read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
