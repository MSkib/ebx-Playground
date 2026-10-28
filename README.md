# EBX Playground

A single-module TIBCO EBX project built as a Maven WAR. The Maven project stays
at the repository root.

## Build

Use Java 21 and Maven:

```powershell
mvn test
mvn package
```

The generated WAR is `target/ebx-Playground.war`. EBX runtime dependencies are
provided by the deployment environment, so a successful build does not replace
testing in a compatible EBX installation.

## Project guidance

- [Architecture](docs/ai-engineering/architecture.md)
- [EBX conventions](docs/ai-engineering/ebx-conventions.md)
- [Data model](docs/ai-engineering/data-model.md)
- [Build and test](docs/ai-engineering/build-and-test.md)

GitHub Copilot workspace instructions and reusable prompts are in `.github/`.

## Tomcat runtime errors

Run the VS Code task **Watch EBX Tomcat errors** while Tomcat is running. It
watches the configured daily Catalina log and creates a separate report for
each distinct error under `logs/ebx-errors/`. See the
[build and test guide](docs/ai-engineering/build-and-test.md) for details and
requirements.

## Excel-to-XSD scaffold

`tools/schema-generator/` and `tests/schema-generator/` contain a starter
structure only. Conversion is not implemented because the workbook layout and
Excel-to-EBX mapping rules have not yet been defined.
