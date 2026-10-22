"""Tests for the Excel-to-XSD command scaffold."""

import importlib.util
import unittest
from pathlib import Path

SCRIPT_PATH = (
    Path(__file__).resolve().parents[2]
    / "tools"
    / "schema-generator"
    / "excel_to_xsd.py"
)
SPEC = importlib.util.spec_from_file_location("excel_to_xsd", SCRIPT_PATH)
if SPEC is None or SPEC.loader is None:
    raise ImportError(f"Cannot load schema generator from {SCRIPT_PATH}")

EXCEL_TO_XSD = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(EXCEL_TO_XSD)


class ExcelToXsdParserTests(unittest.TestCase):
    def test_parses_workbook_and_output_paths(self) -> None:
        args = EXCEL_TO_XSD.build_parser().parse_args(["model.xlsx", "model.xsd"])

        self.assertEqual(args.workbook, Path("model.xlsx"))
        self.assertEqual(args.output, Path("model.xsd"))


if __name__ == "__main__":
    unittest.main()
