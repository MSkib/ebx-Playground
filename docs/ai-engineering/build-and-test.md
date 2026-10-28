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

## Tomcat runtime error monitor

In VS Code, run **Tasks: Run Task** and select **Watch EBX Tomcat errors**.
The task follows
`C:\Program Files\Apache Software Foundation\Tomcat 10.1\logs\catalina.YYYY-MM-dd.log`
and writes reports to `logs/ebx-errors/`. It starts at the end of the current
daily log, follows the date-based log after midnight, and groups repeated
errors by a normalized fingerprint. Stop the task with the VS Code task
termination command.

Each distinct error has an `ebx-error-<fingerprint>.md` report. Repeated
occurrences update its count, last-seen time, and up to five distinct samples.
`index.json` keeps the counts across watcher restarts. Troubleshooting steps
are heuristic suggestions; review the complete Tomcat/EBX context before
changing a deployment. Reports can contain log data, so redact sensitive
information before sharing them.

The monitor requires Python 3.10+ available as `python` in the VS Code task
environment. If Tomcat is installed elsewhere, update the task's
`--log-directory` argument or run the script with a custom path.

Run the monitor's tests with:

```powershell
python -m unittest discover -s tests/tomcat-log-monitor -p "test_*.py"
```

## Schema-generator scaffold

The Python files under `tools/schema-generator/` and
`tests/schema-generator/` are starter files. Once conversion behavior is
implemented, install the listed Python dependency and run the tests with:

```powershell
python -m pip install -r tools/schema-generator/requirements.txt
python -m unittest discover -s tests/schema-generator -p "test_*.py"
```
