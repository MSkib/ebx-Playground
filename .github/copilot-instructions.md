# EBX Playground Copilot Instructions

This repository is a single Maven WAR project for a TIBCO EBX module. The project
is at the repository root; Java sources are under `src/main/java` and EBX module
resources are under `src/main/webapp/WEB-INF/ebx`.

- Read the existing implementation and the relevant EBX API version before
  changing module behavior.
- Keep changes focused on the requested feature and preserve the Maven WAR
  layout.
- Use Java 21 and EBX 6.2.3-compatible APIs. EBX runtime dependencies are
  provided by the deployment environment.
- Validate Java changes with `mvn test` and, when practical, `mvn package`.
- Do not invent EBX schema conventions or spreadsheet-to-XSD mappings. Ask for
  missing requirements or record assumptions explicitly.
- Keep generated files and build output out of source control.

See [the AI engineering documentation](../docs/ai-engineering/architecture.md)
for project-specific context.
