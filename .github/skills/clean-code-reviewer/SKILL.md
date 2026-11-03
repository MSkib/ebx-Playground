---
name: clean-code-reviewer
description: Reviews all changed files in the current branch against Clean Code principles before creating a PR. Checks function size, parameter count, naming, DRY violations, error handling, and language-specific conventions for Java in EBX. Use before creating a pull request to catch code quality issues your tech lead would flag.
---

# Clean Code Reviewer

## When to Use

Run this skill before creating a pull request. It reviews every changed file in your branch against Clean Code principles and your team's coding standards.

## Workflow

### Step 1: Identify Changed Files

Run `git diff --name-only main...HEAD` to get the list of files changed in the current branch compared to `main`. If the base branch is different (e.g., `develop`), ask the user which branch to compare against.

Filter to only code files: `.java`. Ignore generated files, lock files, and configuration files.

### Step 2: Review Each File

Read each changed file completely. For each file, run through the **full checklist** below. Also run `git diff main...HEAD -- <file>` to see exactly what changed — focus your review on the changed lines but consider the full file context.

### Step 3: Produce the Report

For each violation found, report:
- **File** and **line number(s)**
- **Rule violated** (short name)
- **What's wrong** (one sentence)
- **Suggested fix** (concrete, actionable)
- **Severity**: `must-fix` (will definitely be flagged in PR review) or `suggestion` (improvement, not blocking)

### Step 4: Summary

End with:
- Total files reviewed
- Total violations by severity
- Top 3 most common issues across the branch
- Overall assessment: Ready for PR / Needs work

---

## Full Review Checklist

### Functions

| # | Rule | What to Check |
|---|------|---------------|
| F1 | Single responsibility | Does the function do more than one thing? Can you extract a meaningful sub-function? |
| F2 | Function size | Is the function longer than 20 lines? |
| F3 | Parameter count | Does the function have more than 3 parameters? Should they be an object/DTO? |
| F4 | Flag arguments | Are boolean parameters used to switch behavior? Should this be two functions? |
| F5 | Side effects | Does the function modify state beyond what its name implies? |
| F6 | Command-query separation | Does the function both change state AND return a value? |
| F7 | Loop body extraction | Are for/while loop bodies more than 1–2 lines? Should they be extracted? |
| F8 | Try/catch extraction | Are try/catch blocks inline with other logic? Should the bodies be separate functions? |
| F9 | Abstraction level | Are statements at mixed levels of abstraction? |
| F10 | DRY violations | Is there duplicated logic that should be extracted into a shared function? |

### Naming

| # | Rule | What to Check |
|---|------|---------------|
| N1 | Intent-revealing | Can you understand every variable/function/class name without reading the implementation? |
| N2 | Pronounceable | Are there abbreviations or acronyms that would be hard to say in a code review? |
| N3 | No encodings | Are there Hungarian notation prefixes, `I` prefixes on interfaces, or `m_` member prefixes? |
| N4 | Correct part of speech | Are classes nouns? Methods verbs? Booleans prefixed with `is`/`has`/`can`/`should`? |
| N5 | Consistent vocabulary | Is the same concept called different things in different places (`fetch` vs `get` vs `retrieve`)? |
| N6 | No punning | Is the same word used for different concepts? |

### Structure

| # | Rule | What to Check |
|---|------|---------------|
| S1 | Single Responsibility | Does the class/module have more than one reason to change? |
| S2 | Class size | Is the class excessively large (>200 lines)? Should it be split? |
| S3 | Cohesion | Are there methods that don't use most of the class's fields? |
| S4 | Law of Demeter | Are there chained calls like `a.getB().getC().doSomething()`? |
| S5 | Magic numbers | Are there unexplained literals that should be named constants? |
| S6 | Conditional encapsulation | Are there complex boolean expressions that should be extracted into named functions? |
| S7 | Negative conditionals | Are there `!isNotX` style double-negatives that should be rewritten positively? |
| S8 | Dead code | Are there unused functions, unreachable branches, or unused imports? |

### Error Handling

| # | Rule | What to Check |
|---|------|---------------|
| E1 | No null returns | Does any function return `null` when it could return an empty collection or throw? |
| E2 | No null passing | Are `null` values passed as function arguments? |
| E3 | Proper exceptions | Are error codes or special values (-1, empty string) used instead of exceptions? |
| E4 | Exception context | Do exception messages explain what failed and why? |
| E5 | Third-party wrapping | Are external library exceptions propagated raw, or wrapped in domain exceptions? |

### Comments

| # | Rule | What to Check |
|---|------|---------------|
| C1 | Commented-out code | Is there any commented-out code? It must be deleted. |
| C2 | Redundant comments | Are there comments that just restate what the code already says? |
| C3 | Explaining bad code | Are there comments that exist because the code is unclear? The code should be rewritten. |
| C4 | Journal comments | Are there changelog-style comments? Use git history instead. |

### Testing (if test files are in the changeset)

| # | Rule | What to Check |
|---|------|---------------|
| T1 | One concept per test | Does each test verify exactly one behavior? |
| T2 | Arrange-Act-Assert | Is every test structured in clear setup/execute/verify sections? |
| T3 | Descriptive test names | Do test names describe the scenario? `shouldThrowWhenUserNotFound` not `test1`. |
| T4 | Independence | Does any test depend on another test's state or execution order? |

---

## Language-Specific Checks

### Java for TIBCO EBX (`.java` files)

- EBX behavior is implemented in the appropriate platform extension point, such as a module registration listener, value function, or table trigger
- Lifecycle callbacks and trigger behavior are explicit and preserve the EBX API's expected contracts
- EBX APIs used are compatible with the version configured by the project
- EBX operation failures are handled according to the framework's exception contracts; avoid swallowed exceptions and broad catches
- Code uses the relevant EBX context and typed APIs rather than relying on implicit or untyped values
- Runtime-provided EBX libraries are not treated as application-bundled dependencies
- Logging uses established project/platform facilities and does not expose sensitive record contents
- Tests cover behavior that can be tested independently of a running EBX repository
- Packages and naming follow the existing Java and EBX module conventions
- Enums or named constants are used instead of unexplained numeric values

---

## Example Output Format

```
## Clean Code Review Report

### user.service.ts

**[must-fix] F2 — Function too long (line 45–89)**
`processOrder` is 44 lines long. Extract the validation logic (lines 48–62) into `validateOrderItems()` and the notification logic (lines 75–85) into `notifyOrderCreated()`.

**[must-fix] F3 — Too many parameters (line 23)**
`createUser(name: string, email: string, age: number, role: string, department: string)` has 5 parameters. Create a `CreateUserDto` interface and pass a single object.

**[suggestion] N5 — Inconsistent vocabulary (lines 34, 78)**
`fetchUser` on line 34 but `getOrders` on line 78. Pick either `fetch` or `get` and use it consistently.

**[must-fix] C1 — Commented-out code (lines 92–96)**
Delete the commented-out code block. Git history preserves it if needed later.

---

### Summary
- Files reviewed: 5
- Must-fix: 7
- Suggestions: 3
- Top issues: Function size (3), Too many parameters (2), Commented-out code (2)
- **Assessment: Needs work** — address the 7 must-fix items before creating the PR.
```