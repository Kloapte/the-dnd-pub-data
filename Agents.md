
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

### TypeScript and Angular

- Follow the project's existing TypeScript and Angular conventions.
- Use consistent indentation and formatting.
- Keep component methods, lifecycle hooks, and callbacks readable.
- Format Angular templates with clear nesting and indentation.
- Avoid excessively long template expressions.
- Do not compress HTML, CSS, or TypeScript into dense one-line blocks.

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
