---
description: "Use when creating or modifying Java code for the TIBCO EBX module in this Maven project."
name: "EBX Java"
applyTo: "src/main/java/**/*.java"
---

# EBX Java Guidelines

- Follow the repository's Java package structure and existing EBX API patterns.
- Target Java 21 and the EBX 6.2.3 APIs declared by the Maven project.
- Treat EBX platform libraries as container-provided; do not bundle provided
  dependencies into the WAR.
- Keep EBX lifecycle callbacks and trigger behavior explicit. Avoid logging
  sensitive record data.
- Add or update tests for behavior that can be tested independently of a
  running EBX repository.
- Prefer clear, typed code and handle EBX operation failures through the
  framework's established exception contracts.
