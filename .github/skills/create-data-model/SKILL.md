---
name: create-data-model
description: Creates EBX data model XSDs from text descriptions or uploaded files. Use when the user asks to create, scaffold, or generate an EBX data model, table set, entities, fields, primary keys, or table references.
---

# Create Data Model

## Quick start

Turn the supplied entities and fields into a valid EBX XSD:

1. Identify tables, primary keys, fields, relationships, and required values.
2. Inspect repository EBX XSD instructions and existing data models for local conventions.
3. Create or update the XSD in `src/main/webapp/WEB-INF/ebx/schemas/`.
4. Preserve valid EBX namespaces, table annotations, and table-reference annotations.
5. Validate that the XML is well formed and run the project's relevant Maven checks.

## Gather requirements

Before generating a model, determine:

- Entity/table names and primary-key fields
- Field names and business types
- Required versus optional fields
- Relationships and referenced tables
- Code-list or lookup expectations
- Whether an uploaded file is a source description, spreadsheet, or existing schema

Ask focused questions for missing requirements that materially affect the model.
When a field or relationship is ambiguous and does not require the user to choose a
domain-specific type, prefer `xs:string` and record the assumption.

## Type guidance

- Use `xs:string` as the safe default, especially for identifier fields.
- Use `xs:decimal` for decimal quantities.
- Use `xs:date` for dates.
- Use `xs:dateTime` for timestamps.
- Use `xs:int` or `xs:long` only when an integer type is explicitly required.
- Represent a reference with `xs:string` plus EBX `osd:tableRef` metadata.
- Represent code fields as `xs:string` unless an established code-list convention exists.

Do not infer spreadsheet-to-XSD mappings or EBX-specific facets beyond the source
requirements and repository conventions.

## XSD structure

- Include the EBX common and session namespace imports used by neighboring schemas.
- Put tables under a `root` element.
- Mark each table with `osd:table` and `/FieldName` primary-key metadata.
- Name Java binding paths consistently only when repository instructions require them.
- Keep all table and field names stable once identified.
- Make primary keys and essential identity fields required (`minOccurs="1"`).
- Mark uncertain optional fields with `minOccurs="0"` unless requirements indicate otherwise.

Use existing repository schemas only to learn structural and naming conventions.
Do not treat a temporary or example schema as a data-model reference for future output.

## Verification

Before completing the task:

- Parse the XSD to confirm it is well formed.
- Verify every table reference points to an existing table path.
- Verify primary-key fields exist and are required.
- Review the diff for unrelated changes.
- Run `mvn test` for Java-project changes and `mvn package` when practical.
