# Data Model

## Current state

The EBX module descriptor is `src/main/webapp/WEB-INF/ebx/module.xml`. The
repository currently provides sample Java extension points, but no committed
business data-model XSD has been identified.

## Adding a model

Before creating or changing an XSD:

1. Capture the business entities, fields, types, constraints, and relationships.
2. Identify primary keys and decide which fields are required or optional.
3. Check for existing EBX schemas and naming conventions to reuse.
4. Assess compatibility and migration impact for existing data.
5. Validate the schema with EBX tooling before deploying it.

The Excel-to-XSD utility in `tools/schema-generator/` is currently a scaffold.
It must not be treated as a source of production schemas until its mapping
rules and validation behavior are implemented and reviewed.
