
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
