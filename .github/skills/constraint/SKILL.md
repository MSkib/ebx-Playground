---
name: constraint
description: "Create or customize TIBCO EBX table constraints with record-level validation, field-specific warning messages, and user-facing documentation. Use when users ask to add, scaffold, or change an EBX constraint."
---

# EBX Constraint

Scaffold a focused EBX table constraint. Use `checkRecord` for record-level rules; `checkTable` is usually unused and should remain a no-op unless the requirement calls for table-wide validation.

## Workflow

1. Read the applicable Java instructions, configured EBX API version, and an existing constraint example. Treat examples as structural references, not business requirements.
2. Identify the constrained table, the record field(s) involved, and the exact validation rule. Search existing bindings for the field path.
3. Implement record-level validation in `checkRecord`. Obtain the record `ValueContext`, resolve the relevant `SchemaNode`, and add a `UserMessage.createWarning(...)` on that node when the rule fails.
4. Return a clear, user-facing description from `toUserDocumentation`; do not leave placeholder or class-name-only documentation.
5. Keep `checkTable` empty when no table-wide rule was requested. Add setup logic only when configuration or setup-time validation is needed.
6. Put TODOs at unspecified rule, path, or message details. Do not invent business logic. Preserve EBX callback signatures and exception contracts.
7. Review the diff and run the narrowest applicable validation; run Maven tests for Java changes when practical.

## Basic scaffold

Adapt package, class, imports, and field path to the use case and the EBX API version configured by the project.

```java
public class ExampleConstraint implements ConstraintOnTableWithRecordLevelCheck {

    @Override
    public void setup(ConstraintContextOnTable context) {
        // TODO: Add setup-time validation or configuration only if needed.
    }

    @Override
    public String toUserDocumentation(Locale locale, ValueContext valueContext)
            throws InvalidSchemaException {
        return "TODO: Describe the rule users must satisfy.";
    }

    @Override
    public void checkTable(ValueContextForValidationOnTable context) {
        // Usually unused for record-level constraints.
    }

    @Override
    public void checkRecord(ValueContextForValidationOnRecord validationContext) {
        ValueContext record = validationContext.getRecord();
        SchemaNode node = record.getNode().getNode(null); // TODO: Resolve the field to validate.

        boolean violatesRule = false; // TODO: Replace with the requested validation rule.
        if (violatesRule) {
            validationContext.addMessage(
                    node,
                    UserMessage.createWarning("TODO: Explain how to correct this value."));
        }
    }
}
```

Add the imports required by the implemented methods and keep the class in the project's established constraint package. Do not add a broad catch block just to produce a warning; handle only exceptions required by the actual rule and API contract.
