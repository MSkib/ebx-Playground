---
name: grilling
description: Grill the user relentlessly about a plan, decision, or idea. Use when the user wants to stress-test their thinking, or uses any 'grill' trigger phrases.
disable-model-invocation: false
allowed-tools: askQuestions todo
---

⚠️ **PLANNING MODE ONLY** — This skill is exclusively for sharpening plans, designs, and decisions. **DO NOT edit code, DO NOT run commands, DO NOT implement anything.** Output only planning artifacts: design documents, decision matrices, and refined specifications. Stop after all questions are answered and the user confirms understanding.

Interview me relentlessly about every aspect of this until we reach a shared understanding. Walk down each branch of the decision tree, resolving dependencies between decisions one-by-one. For each question, provide your recommended answer.

Ask the questions one at a time, waiting for feedback on each question before continuing. Asking multiple questions at once is bewildering.

If a _fact_ can be found by exploring the environment (filesystem, tools, etc.), look it up rather than asking me. The _decisions_, though, are mine — put each one to me and wait for my answer.

Do not act on it until I confirm we have reached a shared understanding.

Use `/caveman` for the dialog.

## Question format

For every question, use this shape so the recommendation is never buried:

```
Q: <single decision question>
Recommended: <your default answer>
Why: <one-line trade-off>
```

Then wait. Do not move to the next branch until answered.

## Systematic Focus Areas

When grilling, systematically explore these dimensions:

### 1. Scope Clarity

- Is each component/task well-defined?
- Are there implicit dependencies?
- What's missing from the breakdown?
- Where are the boundaries?

### 2. Edge Cases

- What scenarios need to be handled?
- What could go wrong?
- How do components interact at the boundaries?
- What happens under load/failure/unusual input?

### 3. Technical Decisions

- What architectural choices does this imply?
- Are there reversible vs. irreversible decisions?
- What are the trade-offs?
- What alternatives were considered?

### 4. Testing Strategy

- How will this be verified?
- What test scenarios are needed?
- Are the requirements testable?
- What's the failure detection strategy?

### 5. Implementation Path

- What's the sequence of steps?
- Which parts can be done independently?
- What needs to be built first?
- What's the smallest first iteration?

## Technical Scope

Project-specific defaults — backend conventions, frontend component libraries, Figma/API-discovery locations — live in the **Backend grilling defaults** and **Frontend grilling defaults** sections of `AGENTS.md` (or the discovered instruction files). Treat them as answers you propose, not assumptions you skip: still ask the question, just make the recommendation match the codebase convention.
