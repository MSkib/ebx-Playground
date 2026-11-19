# Script-task references

## EBX extension point

The project example extends `com.orchestranetworks.workflow.ScriptTask` and implements `executeScript(ScriptTaskContext)` with `OperationException`. Use the version of these APIs supplied by the target project; inspect the available API documentation or source before choosing context accessors, parameter mechanisms, or exception handling. Do not copy the example's placeholder behavior.

Confirm how the task obtains its session, repository data, and configured inputs from the actual EBX contract. Do not infer those details from a method name or another extension point.

## Data retrieval and updates

When available in the workspace, inspect `#sym:EbxUtil` for existing data-retrieval helpers and `#sym:UpdateUtil` for attribute-update patterns. Use the narrowest matching helper rather than duplicating its behavior. If these helpers are absent or do not fit, inspect the project's existing EBX API usage and follow the applicable API contract.

In the current helper implementation:

- `EbxUtil` includes helpers for dataspace and dataset lookup, table lookup, field value access, and query results. Check nullability and error behavior for the specific method before using it.
- `UpdateUtil` supports updates through a `ProcedureContext` and through a `ProgrammaticService` created for a `Session`. Confirm which context is valid in a script task before selecting a path.
- The `UpdateUtil` instance methods that use `ProgrammaticService` return `CompletableFuture<Void>`. They are asynchronous; do not assume an update completed when the call returns. Decide whether and how to observe completion based on the workflow and EBX contracts, and surface failures.
- Use EBX `Path` values and model bindings where available; do not invent schema paths.

These are workspace-specific helper examples, not portable EBX APIs. Re-read their current implementations and the configured EBX API version before relying on them.

## Validation

Compile and test Java changes with the repository's configured Maven commands where practical. A successful local build does not verify behavior that depends on a live EBX repository or workflow configuration; call out that runtime verification when relevant.
