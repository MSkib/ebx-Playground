---
description: "Analyze an EBX feature requirement and produce an implementation-ready plan."
name: "Analyze EBX Requirement"
argument-hint: "Describe the requirement, affected data, and expected behavior"
agent: "agent"
---

Analyze this requirement for the EBX Playground:

`${input:requirement:Describe the requirement}`

Inspect the relevant Java sources, EBX schemas, and build configuration. Return:

1. A concise restatement of the expected behavior.
2. The affected files and EBX components.
3. Data-model, compatibility, and migration implications.
4. Acceptance criteria and focused tests.
5. Any unresolved questions or assumptions.

Do not modify files. Do not infer unspecified EBX behavior.
