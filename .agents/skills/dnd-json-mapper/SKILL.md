---
name: dnd-json-mapper
description: Map 5eTools D&D JSON collections into strongly typed Java DTE records using discovered schemas, Jackson deserializers, and full-dataset validation. Use when creating, extending, or fixing 5eTools JSON mappings.
---

# 5eTools JSON mapping workflow

Use this skill for raw import mappings from 5eTools JSON into Java DTE records. Do not use it for unrelated D&D domain questions. Do not generate DTE records unless the user asks for mapping implementation; schema analysis alone does not authorize DTO generation.

Keep three layers separate: raw DTE import types, transformation into The DnD Pub domain models, and gameplay mechanics. Keep DTE types independent of Spring where practical. Jackson deserializers should interpret JSON shapes, not perform gameplay calculations.

## A. Inspect schemas and source

Before designing a collection mapping:

1. Locate the source files under `src/main/resources/data`, and identify each top-level collection key. Treat `(relative domain directory, top-level collection)` as the collection identity; identical keys in different domains are separate.
2. Read the domain's `schema-summary.json`, `schema-conflicts.json`, `schema-dynamic-keys.json`, and `schema-gameplay.json`, plus `analysis/coverage-manifest.json` when available.
3. Review all relevant conflicts, dynamic-key candidates, heterogeneous arrays, structural variants, optional fields, and gameplay classifications.
4. Inspect representative and exceptional objects in the original JSON. Summary reports are indexes, not substitutes for understanding complex data.
5. Preserve unknown and ambiguous fields during analysis. Never omit fields just because the advisory gameplay classifier labels them metadata or unknown.

For large files, inspect targeted samples or use streaming tools; do not load an entire dataset into memory or model context.

## B. Design typed DTEs

Design the model before implementing it. Use Java records named with the `Dte` suffix. Prefer `String` for text, appropriate boxed numeric and boolean types for optional values, `List<T>` for arrays, nested records for stable structures, and typed maps for genuinely dynamic keys. Use enums only for value sets shown to be stable and complete. Use `@JsonProperty` for source names that are not natural Java identifiers.

Avoid raw `Object`, `Map<String, Object>`, unnecessary `JsonNode` in final DTE records, and records that combine unrelated structures. Do not put multiple JSON names in one `@JsonProperty`. Use aliases only when names truly represent one semantic property; otherwise model separate properties or a typed map. Reuse shared types when semantics match, not merely because JSON shapes look alike.

## C. Preserve gameplay information

Preserve content potentially needed for actual play: combat statistics, attacks, damage and healing, saves and checks, conditions, defenses, spellcasting and components, actions and reactions, recharge and limited uses, movement and targeting, class features, item mechanics, and rules descriptions. Keep source identifiers/references when they distinguish entities or resolve links. Internal 5eTools metadata can be omitted only after confirming it is not needed for interpretation, identity, or reference resolution.

Do not translate descriptive rules into executable mechanics during initial DTE mapping. First preserve them faithfully in typed structures. In particular, nested `entries` and inline tags such as `{@damage ...}` and `{@spell ...}` may contain rules and links; do not flatten or discard them.

## D. Handle polymorphism and recursive entries

For each polymorphic property, inspect every observed type and representative value, decide whether variants share semantics, and model concrete variants with reusable types where appropriate. Add a focused reusable Jackson deserializer when static typing cannot select variants. Reject unknown variants with an error that includes the type and property path. Do not leave a permanently raw `Object` field as the solution.

Build reusable entry representations for the variants actually present: plain text, named sections, nested entries, lists, tables, and other structured forms. Keep recursion typed and bounded by the data model; preserve markup and unknown variants or fail visibly. Do not create separate incompatible entry frameworks for spells, monsters, and items without evidence that their semantics differ.

## E. Validate the complete collection

Every implemented mapping needs automated edge-case tests and full-dataset validation over all relevant source files and entities. Validation should report successful and failed entity counts and identify each failure by source file, entity name or index, and JSON path. Exercise optional fields, every observed polymorphic variant, recursive entries, and unusual values independently.

Do not treat successful deserialization as proof of coverage. Detect ignored or unsupported fields, unsupported variants, and loss of gameplay-relevant properties. Compare mapped fields against `schema-gameplay.json`; fail when known gameplay-critical fields are unsupported unless a documented exception explains why. Keep unknown-property handling visible and deliberate; never silently ignore unknown JSON.

Use existing Maven and test conventions. Keep scanner and import code runnable without starting Spring where possible. Do not modify source JSON. Do not modify unrelated DTOs or domain code.

## F. Report the mapping review

After implementation and validation, summarize collections and files covered, successful entities, failures, reusable types and deserializers added, gameplay-relevant fields intentionally excluded, limitations, and recommended next mappings. Claim complete coverage only after running the relevant full-dataset validation and tests.

## Repository report references

The scanner is `com.thedndpub.data.analysis.SchemaDiscovery`; report semantics and execution are documented in [`analysis/SCHEMA-REPORTS.md`](../../../analysis/SCHEMA-REPORTS.md). The default scanner command is `mvn exec:java -Dexec.args="src/main/resources/data analysis"`. Reports are advisory; retain source information until the mapping is understood and validated.
