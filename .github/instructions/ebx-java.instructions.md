---
description: "Use when creating or modifying Java code for the TIBCO EBX module in this Maven project."
name: "EBX Java"
applyTo: "src/main/java/**/*.java"
---

# EBX Java Guidelines

## Platform and architecture

- Follow the repository's Java package structure and existing EBX API patterns.
- Target the Java version and EBX API version configured in `pom.xml`. Confirm compatibility with the deployed EBX environment before changing either version.
- Implement behavior in the EBX extension point that matches its purpose, such as module registration, a value function, a table trigger, or a user service. Do not introduce Spring MVC, Spring services, or JPA conventions into this EBX module.
- Keep lifecycle callbacks focused. Move reusable business rules into small, focused classes or methods where doing so preserves the extension point's context and lifecycle requirements.
- Use EBX contexts and data-model APIs according to their documented contracts. Do not retain context or record objects beyond their documented lifetime or pass them to background work unless the API explicitly allows it.
- Keep value functions focused on computing their result. Avoid repository writes or unrelated side effects unless the specific EBX contract requires them.
- Follow the project's package layout under `src/main/java/ebxPlayground/`. Keep module descriptors and data-model resources in their established EBX locations.

## EBX data and configuration

- Use the EBX data model and typed EBX APIs to access records and values; do not introduce ORM entities or repository abstractions from unrelated frameworks.
- Put data constraints in the EBX data model when they are model-level rules. Use Java validation only for behavior that belongs in the relevant extension point.
- Follow the extension class's EBX instantiation and configuration requirements. Preserve required constructors, visibility, parameter names, and bean-style accessors where the platform expects them.
- Keep schema and Java changes focused, and consider compatibility impacts when changing fields, paths, or values consumed by Java code.

## Error handling and logging

- Preserve the EBX API's exception and operation contracts. For example, propagate or report operation failures using the exception types and mechanisms declared by the relevant callback.
- Do not swallow exceptions, use broad catches as a fallback, or turn failed operations into success-shaped results. Add useful context when reporting failures without obscuring their cause.
- Do not add Spring exception handlers or translate EBX failures into HTTP responses unless implementing a specifically requested HTTP integration.
- Use the platform or project logging facilities. Do not use `System.out`/`System.err` for production behavior or log sensitive record contents.
- Handle null and absent values according to the relevant EBX API contract. Do not assume every EBX lookup should use `Optional` or replace a meaningful platform value with an arbitrary default.

## Java design and conventions

- Give classes and methods one clear responsibility. Keep methods cohesive and extract meaningful helpers when logic becomes difficult to follow.
- Use descriptive Java names and follow the naming and visibility patterns already established in the module and EBX extension APIs.
- Prefer enums or named constants over unexplained numeric values. Keep constants `static final` and use the project's established naming style.
- Prefer explicit, readable types and control flow. Use streams, `var`, records, and newer language features only when they improve clarity and remain compatible with EBX instantiation and configuration requirements.
- Avoid unnecessary abstractions and duplicated logic; introduce shared helpers only when they make behavior clearer and remain appropriate to the EBX lifecycle.

## Dependencies and testing

- Respect the dependency scopes in `pom.xml` and the EBX deployment environment. Do not change platform dependency scopes or bundle runtime-provided EBX libraries without confirming the deployment requirements.
- Add or update tests for behavior that can be tested independently of a running EBX repository. Use the test framework configured in the project; do not prescribe Spring test annotations or assume Spring infrastructure exists.
- When practical, validate Java changes with `mvn test` and the WAR with `mvn package`. Behavior that depends on a live EBX repository may also require verification in a compatible EBX environment.
