"""Starter module for a future Excel-to-EBX-XSD converter.

The workbook format, mapping rules, and output conventions must be agreed
before implementing conversion. This scaffold intentionally does not generate
an XSD.
"""

import argparse
from collections.abc import Sequence
from pathlib import Path


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Scaffold for converting an Excel workbook to an EBX XSD."
    )
    parser.add_argument("workbook", type=Path, help="Path to the source workbook")
    parser.add_argument("output", type=Path, help="Path for the generated XSD")
    return parser


def main(argv: Sequence[str] | None = None) -> int:
    build_parser().parse_args(argv)
    raise NotImplementedError(
        "Excel-to-XSD conversion is a scaffold; define mapping rules first."
    )


if __name__ == "__main__":
    main()
