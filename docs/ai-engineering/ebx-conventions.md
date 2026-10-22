# EBX Conventions

- Use the EBX 6.2.3 APIs declared by `pom.xml`; confirm the target environment
  before upgrading them.
- Keep module registration, value functions, and table triggers in their
  existing Java packages under `src/main/java/ebxPlayground/`.
- Keep EBX module descriptors and data-model schemas under
  `src/main/webapp/WEB-INF/ebx/`.
- Prefer small, focused schema and Java changes. Review table/field naming,
  requiredness, references, and compatibility impacts before publishing a
  changed data model.
- Do not log sensitive record contents. Use the platform's established logging
  and error-handling facilities when adding production behavior.
- The checked-in Java classes include examples; treat them as starting points,
  not a complete production implementation.
