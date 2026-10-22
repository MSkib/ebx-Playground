---
name: grill-with-docs
description: A relentless interview to sharpen a plan or design based on an Azure DevOps work item, which also creates docs (ADR's and glossary) as we go. Call with a work item ID and optional scope override (FE|BE).
argument-hint: "<work-item-id> [optional scope override: FE|BE]"
disable-model-invocation: true
requires: [grilling, domain-modeling, azure-devops-cli]
allowed-tools: runInTerminal getTerminalOutput killTerminal readFile viewImage codebase fileSearch textSearch listDirectory usages todo fetch
---

# Grill with Docs - Azure DevOps Integration

> **🛑 DESIGN / PLAN MODE ONLY — HARD STOP AFTER PLANNING 🛑**
>
> This skill is **exclusively for creating a design or implementation plan**. After all questions are answered, produce a structured plan/design document.
>
> **ABSOLUTELY DO NOT:**
>
> - ❌ Edit or write code
> - ❌ Run `dotnet` commands or any terminal commands
> - ❌ Create or modify code files
> - ❌ Update source files or configurations
> - ❌ Modify .csproj files or dependencies
> - ❌ Implement features
> - ❌ Apply refactorings
>
> **ONLY:**
>
> - ✅ Ask clarifying questions
> - ✅ Create/update the project's glossary/ADR documentation (CONTEXT.md, ADRs) at the configured docs root
> - ✅ Produce a design/plan document
> - ✅ Present the plan to the user
> - ✅ **STOP and wait for explicit user approval before any work begins**
>
> The output is always a document the user can review and approve before any work begins. No implementation happens in this skill.

Interview the user with `/grilling`, document glossary vocabulary and ADRs with `/domain-modeling` and use guidance by `/ponytail`.

## Prerequisites

Before starting, ensure Azure DevOps CLI is configured. See the `azure-devops-cli` skill's "Workspace Defaults" section for the `az devops configure --defaults` command to run.

### Fetch Azure DevOps Work Item Context

When the user provides a work item ID, fetch comprehensive information including comments and discussions.

**Command reference:** `azure-devops-cli` skill

Use `references/work-item-context.md` to:

- Fetch work item details and relations
- Fetch the full comment thread (Comments REST API, auth gotcha, API-version gotcha, HTML stripping)

Additionally, check if there are items related via a specific domain tag that is not `Frontend`, `Backend`, or `Infrastructure`, e.g. `MessageCenter`.

### Extract and Organize Information

From the fetched data, compile a comprehensive picture:

**Work Item Details:**

- Title & Description
- Acceptance Criteria
- State, Priority, Iteration
- **Tags** - analyze to determine technical scope

**Scope resolution order for `/grilling`:**

1. User override from command (`FE` or `BE`)
2. Tag analysis from work item metadata
3. If there are multiple scope tags and the user did not provide a scope override, ask the user to clarify which scope to focus on

**All Related Items:**

- Child tasks with descriptions and acceptance criteria
- Parent features or epics
- Dependent work items
- Blocked/blocking relationships

**Discussion History:**

- All comments from the main work item
- All comments from each child task
- All comments from related items
- Note: Comments often reveal important context, clarifications, concerns, and team decisions

**Extract key insights from comments:**

- Clarifications on requirements
- Edge cases mentioned by team members
- Technical concerns raised
- Decisions made during discussions
- Open questions or unresolved issues

## Example Session Flow

**Example 1: FE + BE Ticket**

```
User: "@workspace /grill-with-docs xxxxxx"

Agent:
1. Fetches work item #xxxxxx with full JSON output
2. Analyzes tags: ['frontend', 'backend', 'api-integration']
   → Categorized as FE + BE ticket
3. Calls the Comments API (az devops invoke --resource comments) to extract all comments
5. Fetches relations and discovers 5 child tasks
6. Fetches each child task with their comments
7. Reads current CONTEXT.md for existing domain language
8. Presents comprehensive summary
9. Begins grilling with BACKEND + CROSS-TEAM focus
10. Creates ADR for API contract decisions (references work item #12345, Figma mockup)
11. Updates CONTEXT.md with domain terms
12. **Presents the final design/plan document and stops — does NOT proceed to implementation**
```

**Example 2: BE-Only Refactoring Ticket**

```
User: "@workspace /grill-with-docs xxxxx"

Agent:
1. Fetches work item #xxxxx
2. Analyzes tags: ['backend', 'refactoring', 'tech-debt']
   → Categorized as BE-only refactoring
3. No mockup check needed (refactoring doesn't change UI)
4. Presents summary:
   - Work item: "Refactor legacy backend service to use new architecture"
   - **Scope**: BE-only, code refactoring
   - **No mockup needed** (no UI changes)
   - 3 child tasks

5. Begins grilling with BE-ONLY focus:
   - [Applies /grilling "Pure Backend Work" focus areas]
   - "Which services are in scope for this refactoring?"
   - "Are there any legacy dependencies that need to be addressed?"
   - "What's the testing strategy to ensure no behavior changes?"
   - "Should we refactor all at once or incrementally?"
   - [Continues with /grilling refactoring-specific questions]

6. Updates CONTEXT.md if new testing terminology emerges
7. Creates ADR if there's a significant architectural choice (e.g., state management pattern)
8. **Presents the final design/plan document and stops — does NOT proceed to implementation**
```

## 🛑 Hard Stop After Planning — Non-negotiable

Once the grilling is complete and all ADRs/glossary updates are produced:

1. Present the resulting plan/design document
2. **Explicitly state: "This is the plan. I am waiting for your approval to proceed."**
3. **Wait for explicit user approval** before any implementation step
4. **NEVER transition from planning to doing within this skill**
5. **NEVER auto-start implementation** even if the plan seems obviously correct

If you find yourself about to edit code, run a command, or modify files — STOP and re-read this directive. Grilling produces designs, not implementations. The user will tell you when to switch gears.
