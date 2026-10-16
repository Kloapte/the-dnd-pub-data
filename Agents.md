
# D&D 5e Backend

## Technology
- Java 21+ and Spring Boot 4
- Jackson for JSON deserialization
- Java records for immutable data transfer objects
- JUnit 5 for testing

## Data Mapping Conventions
- Use Java records for JSON data representations.
- Name JSON mapping records with the Dte suffix.
- Use @JsonProperty when JSON names differ from Java names.
- Prefer strongly typed fields over Object and Map.
- Use custom deserializers only when necessary.
- Preserve source data during deserialization.
- Never silently discard unknown fields.

## Working with Source JSON
- Source files may contain over 100,000 lines.
- Do not load entire large files into model context.
- Use scripts to discover schemas and field variations.
- Examine representative samples and exceptional cases.
- Never assume a field has a consistent type.
- Check existing records before creating new ones.

## Implementation
- Analyze before modifying code.
- Implement one logical data structure at a time.
- Prefer existing project conventions.
- Do not modify source JSON.
- Add tests for unusual JSON structures.
- Validate mappings against the complete dataset.
- Report unmapped fields and failed entries.


## Code Style and Readability

All generated and modified code must prioritize human readability, maintainability, and consistency with the existing project.

Code must be written as production-quality source code intended to be read, reviewed, debugged, and maintained by other developers.

### Formatting

- Never compress methods, constructors, loops, conditionals, switch statements, or exception handling into a single line.
- Use conventional indentation, spacing, and line breaks appropriate to the language.
- Use braces for control-flow statements, including single-statement `if`, `else`, `for`, and `while` blocks.
- Put statements on separate lines.
- Break long method calls, constructor arguments, record declarations, and chained expressions across multiple lines when this improves readability.
- Keep line lengths around 120 characters where practical.
- Avoid deeply nested ternary expressions and overly long lambda expressions.
- Do not sacrifice readability to reduce line count, token usage, or file size.
- Follow existing project formatting rules where they are more specific.

### Java

- Follow conventional Java formatting with four-space indentation.
- Use descriptive variable and method names. Avoid single-letter variables except for simple, conventional loop indices.
- Format Java records with multiple components across multiple lines when appropriate.
- Format stream operations and fluent chains across multiple lines when they become difficult to scan.
- Use clear block structures for conditionals, loops, and exception handling.
- Prefer readable intermediate variables over excessively complex expressions.
- Keep logical sections of longer methods visually separated.
- Avoid putting multiple independent operations on one line.
- Use imports rather than unnecessarily verbose fully qualified class names, except where ambiguity requires them.

### Comments and Documentation

- Write comments to explain non-obvious intent, constraints, and important design decisions.
- Do not add comments that merely repeat what the code already says.
- Preserve existing meaningful comments and documentation.
- Prefer self-explanatory code over unnecessary commentary.

### Generated Code Requirements

These standards apply equally to newly generated code and modifications to existing code.

Before completing a task:

1. Review all created or modified files for readability.
2. Reformat compressed or overly dense code.
3. Verify that indentation and line breaks are consistent.
4. Ensure no unrelated code was changed.
5. Run existing formatting tools when available.
6. Ensure the final diff is easy for a human reviewer to understand.

**Readable code is a requirement, not an optional cleanup step. Never intentionally produce minified or compressed source code.**

## Java Class Organization

- Use one public top-level class, record, enum, or interface per Java source file.
- Do not declare DTO records, classes, enums, or interfaces inside other DTOs.
- Prefer descriptive, domain-specific names such as `SpellIdentityDto`, `MonsterStatisticsDto`, and `ActionEffectDto`.
- Organize related DTOs into logical packages without excessive package fragmentation.
- Use Java records for immutable data-transfer objects when appropriate.
- Use sealed interfaces for well-defined polymorphic models.
- Prefer strongly typed fields over generic maps, `Object`, or `JsonNode` in application-facing DTOs.
- Use defensive copies for mutable collections supplied to immutable records.
- Keep source-specific import models separate from application-facing DTOs.
- Keep internal import provenance and diagnostics separate from public API responses.
- Follow the existing Java readability standards: four-space indentation, descriptive names, braces for control flow, and readable multiline methods.

## Jackson Serialization

- Annotate all public API DTO records with `@JsonInclude(JsonInclude.Include.NON_EMPTY)`.
- Do not use `NON_NULL` for new public API DTOs unless there is a documented reason.
- Omit null values, empty strings, empty lists, empty sets, and empty maps from public JSON.
- Preserve meaningful `false` and `0` values.
- Avoid constructing optional nested DTO records that contain no meaningful values and would serialize as `{}`.
- Do not modify global Jackson serialization settings merely to clean up one DTO.
- Preserve source-level distinctions between absent, explicit-null, and empty values internally when relevant.
- Use dedicated public DTO projections when internal normalized models contain fields that must not be exposed.
- Add serialization tests for null, empty, nested, and polymorphic DTO behavior.

## D&D Data Import and Normalization

- Treat external D&D datasets as source data, not as the application's permanent domain model.
- Use the `$dnd-json-mapper` skill when introducing a new imported D&D entity type.
- Analyze the complete source schema before implementing DTEs or DTOs.
- Inspect source relationships, copy inheritance, version definitions, and references to other entity types.
- Design the application-facing DTO model before implementing the mapper.
- Prefer reusable domain concepts and stable references over duplicating definitions across entities.
- Preserve the distinction between a reusable definition, a reference to that definition, and an entity-specific configuration.
- Never invent mechanical values that cannot be established from source data or explicitly documented rules.
- Preserve unsupported or ambiguous mechanics internally and report them rather than guessing.
- Do not expose raw source text, internal import metadata, or third-party media paths through public APIs without an explicit publication decision.
- Record source and licensing metadata where needed for publication decisions. Do not assume that mechanically structured content is automatically free of copyright restrictions.

## REST API Conventions

- Keep controllers thin and delegate business logic to services.
- Separate source loading, normalization, catalog indexing, querying, and HTTP handling.
- Use summary DTOs for collection endpoints and detail DTOs for individual resources.
- Paginate collection endpoints when result sets can grow significantly.
- Use deterministic sorting and stable identifiers.
- Use consistent validation and error handling.
- Prefer explicit, stable API response models rather than exposing framework implementation details.
- Avoid unnecessary persistence layers for static reference catalogs.
- Test both service behavior and REST serialization.

## Quality Gates

Before completing data-import work:

1. Confirm the full discovered source corpus is accounted for.
2. Verify that no supported source records are silently skipped.
3. Validate deterministic output and unique definition identities.
4. Test representative polymorphic and unusual source structures.
5. Test public JSON serialization, including omitted empty properties.
6. Run focused tests and the full applicable test suite.
7. Document unsupported source structures, unresolved references, and known limitations.
8. Report unrelated pre-existing failures separately.

Never compress generated code to reduce token usage or file size.