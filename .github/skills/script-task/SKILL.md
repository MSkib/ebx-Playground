---
name: script-task
description: "Create and customize Java tasks that run inside TIBCO EBX workflows. Use when users ask to implement, scaffold, or change an EBX ScriptTask or its workflow behavior."
argument-hint: "Describe the workflow task behavior, inputs, and records or attributes involved"
---

# EBX Script Tasks

Implement a focused EBX workflow `ScriptTask` for the requested behavior. Preserve the EBX lifecycle and avoid inventing workflow rules, context APIs, or data-model paths.

## Workflow

1. Read the applicable Java instructions, configured EBX API version, and an existing script-task example. Treat examples as structural references, not business requirements.
2. Identify the workflow behavior, available inputs, records to retrieve, attributes to change, and expected failure behavior. Ask a focused question if a missing detail affects correctness.
3. Inspect the EBX `ScriptTask` and `ScriptTaskContext` APIs available to the project before accessing workflow context, session, or parameters. Preserve `executeScript(ScriptTaskContext)` and its declared exception contract.
4. Search for existing bindings, utility methods, and logging patterns. If the project provides `EbxUtil`, use its applicable methods to retrieve data; if it provides `UpdateUtil`, use it for attribute updates. See [REFERENCE.md](REFERENCE.md).
5. Implement only the requested behavior. Use typed EBX APIs and existing helpers; do not assume the script-task context exposes a particular value or that an update can be performed synchronously.
6. Handle missing data and operation failures explicitly using the API's contract. Do not swallow exceptions, log sensitive record contents, or return success after a failed operation.
7. Review the diff and run the narrowest applicable validation. For Java changes, run the project's Maven tests when practical; runtime behavior may require a compatible EBX environment.

## Implementation expectations

- Keep the task class in the established script-task package and follow EBX instantiation and JavaBean conventions for configured parameters.
- Give parameters descriptive names and preserve required getters/setters when EBX configuration relies on them.
- Do not retain EBX contexts or record objects beyond their documented lifetime or pass them to background work unless the API explicitly allows it.
- Put TODOs only at genuinely unspecified business-rule points; keep the class compilable and preserve existing behavior outside the request.
- Avoid adding helpers or dependencies when an existing project utility directly fits the operation.
