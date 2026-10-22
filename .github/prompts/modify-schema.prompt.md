---
description: "Plan and implement a requested EBX XSD change with compatibility checks."
name: "Modify EBX Schema"
argument-hint: "Describe the data-model change and compatibility requirements"
agent: "agent"
---

Modify the EBX data model according to:

`${input:requirement:Describe the schema change}`

Inspect the existing schemas and repository conventions first. Make the smallest
complete change, preserve existing names and behavior where possible, and
explain any compatibility or migration impact. Validate the resulting XSD with
available project tooling; if that tooling is unavailable, state that clearly.
Do not invent missing constraints or silently make breaking changes.
