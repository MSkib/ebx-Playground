---
description: "Prepare an Excel workbook for conversion into an EBX-compatible XSD."
name: "Excel to XSD"
argument-hint: "Provide the workbook path and the expected table/field mapping"
agent: "agent"
---

Use the schema-generator scaffold at `tools/schema-generator/` to work on:

- Workbook: `${input:workbook:Path to the Excel workbook}`
- Mapping requirements: `${input:mapping:Describe sheets, tables, fields, types, and constraints}`

Inspect the workbook structure and the existing EBX schemas before proposing a
conversion. Report ambiguities rather than guessing. Preserve the original
workbook and existing schemas; do not overwrite files unless explicitly asked.
Summarize the generated schema and any assumptions so it can be reviewed before
deployment.
