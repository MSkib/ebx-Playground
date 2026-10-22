# Build and Test

## Prerequisites

- Java 21
- Maven
- Python 3.10+ for the schema-generator scaffold tests
- Access to the EBX 6.2.3 dependencies configured for this project

## Maven project

Run the Java tests:

```powershell
mvn test
```

Build the deployable WAR:

```powershell
mvn package
```

The WAR is written to `target/ebx-Playground.war`. A successful local build
does not replace deployment validation in a compatible EBX environment.

## Schema-generator scaffold

The Python files under `tools/schema-generator/` and
`tests/schema-generator/` are starter files. Once conversion behavior is
implemented, install the listed Python dependency and run the tests with:

```powershell
python -m pip install -r tools/schema-generator/requirements.txt
python -m unittest discover -s tests/schema-generator -p "test_*.py"
```
