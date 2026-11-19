---
name: value-function
description: "Create or customize TIBCO EBX value functions that calculate field values from record attributes, criteria, time, or other logic. Use when users ask for a computed value or changes to an EBX ValueFunction."
argument-hint: "Describe the calculated value, its source attributes or criteria, and expected null and time behavior"
---

# EBX Value Functions

Implement a focused EBX `ValueFunction` for the requested calculation. Do not invent formulas, data paths, null behavior, or recalculation assumptions.

## Workflow

1. Read the applicable Java instructions, configured EBX API version, and an existing value-function example. Treat examples as structural references, not business requirements.
2. Identify the target field's value type, calculation inputs, exact criteria, expected behavior for missing values, and whether time-dependent results must reflect the current time. Ask a focused question if any of these affect correctness.
3. Inspect the EBX `ValueFunction` and `ValueFunctionContext` APIs available to the project. Preserve the required lifecycle methods and follow the target field's type and EBX configuration conventions.
4. Search existing bindings and helpers before adding logic. Use `#sym:EbxUtil` when a matching helper is needed to retrieve repository data; verify the helper's behavior and null/error contract before relying on it.
5. Keep `getValue` focused on computing and returning the value. Use record/model APIs and typed paths where available; do not write records or add unrelated side effects.
6. Handle absent values and failures according to the user's requirements and EBX API contract. Do not silently substitute arbitrary defaults, swallow exceptions, or log sensitive record values.
7. Review the diff and run the narrowest applicable validation. For Java changes, run the project's Maven tests when practical; runtime behavior may require a compatible EBX environment.

## Implementation expectations

- Keep the class in the established value-function package and preserve bean-style getters/setters for configured parameters when required.
- Return a value compatible with the target field's EBX type; do not rely on implicit string conversions without checking the contract.
- For time-dependent results, confirm how and when EBX evaluates or caches the function before promising freshness or adding time-based behavior.
- Avoid expensive repeated lookups or repository-wide work in a function that may be evaluated frequently. Retrieve additional data only when the requirement needs it and a suitable API/helper exists.
- Keep the implementation focused; add tests for calculation behavior that can be tested independently of a live EBX repository.
