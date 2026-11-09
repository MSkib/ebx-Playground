---
name: trigger
description: "Create or scaffold TIBCO EBX table triggers for a described use case, using existing project patterns and clear TODOs for unspecified behavior. Use when users ask to write, add, or customize an EBX trigger."
argument-hint: "Describe the trigger use case and desired table events"
---

# EBX Table Trigger

Create a focused EBX `TableTrigger` for the user's use case. Prefer a working, minimal scaffold over invented business behavior.

## Workflow

1. Read the applicable Java instructions, the project's configured EBX API version, and an existing trigger example. Treat the example as a structural reference, not as a source of behavior to copy blindly.
2. Identify the target table, event(s), required record values, and any side effects. Search existing bindings, utilities, and logging patterns before adding code.
3. Prefer `handleAfterCreate` and/or `handleAfterModify` for post-write behavior. Use a `before` callback only when the requirement needs pre-write validation or must prevent the operation; explain this choice because it can add work to the write path.
4. Implement only behavior supported by the request and available project APIs. Where requirements are missing, leave concise `TODO` markers at the exact customization points rather than inventing rules. Keep the class compilable and preserve EBX method signatures and exception contracts.
5. Use the project's `EbxUtil` only when an existing helper directly fits the need. Do not add a utility dependency just for a trivial operation.
6. Follow established package, naming, configuration, and logging conventions. Do not use `System.out`/`System.err`, swallow exceptions, or perform unrelated expensive work in callbacks.
7. Review the diff and run the narrowest applicable validation; for Java changes, run the project's Maven tests when practical.

## Scaffold expectations

- Keep `setup` only when configuration or setup-time validation is needed; put any required setup TODO there.
- Implement only the requested after-event callbacks. Leave unrelated callbacks out.
- Obtain the occurrence from the callback context and use typed EBX APIs for field access.
- Use a TODO for unspecified business logic, for example:

```java
// TODO: Add the requested post-create behavior.
```

- Do not retain EBX context or adaptation objects beyond the callback unless the API explicitly permits it.
- If the use case is too ambiguous to choose a table or event safely, ask a focused question before writing the trigger.
