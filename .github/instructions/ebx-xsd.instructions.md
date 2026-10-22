---
description: "Use when creating or editing EBX data model XSD files, including schema validation and compatibility."
name: "EBX XSD"
applyTo: "src/main/webapp/WEB-INF/ebx/schemas/**/*.xsd"
---

# EBX XSD Guidelines

- Preserve valid EBX schema structure and namespace declarations; inspect
  neighboring schemas before making changes.
- Keep table, field, and primary-key names stable unless the requirement
  explicitly calls for a breaking change.
- Check required versus optional values, field types, facets, and references
  against the data requirements.
- Do not guess EBX-specific annotations or spreadsheet-to-schema mappings.
- Review the resulting diff and validate the schema with the project's
  available EBX tooling before deployment.
