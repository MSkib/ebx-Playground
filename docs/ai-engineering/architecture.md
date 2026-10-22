# Architecture

## Project shape

EBX Playground is one Maven WAR project located at the repository root. It is
not split into a `modules/` directory.

- `src/main/java/` contains the EBX Java module code.
- `src/main/webapp/WEB-INF/ebx/` contains EBX module resources and schemas.
- `pom.xml` builds the `ebx-Playground.war` artifact.
- `tools/schema-generator/` is a scaffold for a future spreadsheet-to-XSD
  utility; it does not currently generate schemas.

## Runtime boundary

The Maven project declares EBX 6.2.3 libraries and the Jakarta Servlet API as
provided dependencies where appropriate. The EBX deployment environment is
responsible for supplying runtime libraries. The WAR is intended to be deployed
to a compatible EBX environment; the local project alone is not a full EBX
runtime.

## Extension points

The current Java sources include a module registration listener, a sample value
function, and a sample table trigger. Keep EBX lifecycle behavior in the
appropriate extension point and verify API compatibility before introducing
new hooks.
