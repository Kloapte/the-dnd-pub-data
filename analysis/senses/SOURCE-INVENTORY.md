# Senses Source Inventory

## Corpus

`data/senses.json` contains one root collection, `sense`, with 8 definitions. Each record has a unique `(name, source)` identity: Blindsight, Darkvision, Tremorsense, and Truesight each occur in PHB and XPHB; Tremorsense also occurs in MM (thus 3 Tremorsense records and 8 records total). No records are skipped.

## Fields

Every record has `name`, `source`, positive integer `page`, and `entries`. `entries` is an array of strings or structured 5eTools entries. Seven records use string entries only; XPHB Truesight has a string and a `list` entry with five named items. The shared typed `EntryDte` hierarchy captures this markup recursively. The properties `srd`, `basicRules`, `srd52`, and `reprintedAs` are optional and appear on the four 2014 definitions; each is absent from the four XPHB definitions. The availability flags are booleans. `reprintedAs` is a string array containing one `Name|XPHB` target per PHB/MM definition.

## Sources and relationships

Distinct source codes: `MM`, `PHB`, `XPHB` (3/3 already present in the Source Registry). Each has publication metadata through the existing Book Catalog. The PHB definitions point to same-name XPHB definitions; MM Tremorsense points to XPHB Tremorsense. These targets exist in this corpus. No aliases are present, and there are no ambiguous source relationships. No new source identities or publication records are needed.

Pages are preserved: PHB 183 (all four), MM 9 (Tremorsense), XPHB 361 (Blindsight), 365 (Darkvision), and 377 (Tremorsense and Truesight).

## Mechanics and presentation

The source provides explanatory rules prose in `entries`, not structured range values, measurement units, detection modes, or mechanical modifiers. Phrases such as “specific range” and the effects described in prose are not normalized into mechanics. XPHB text includes 5eTools references to conditions and rules; Truesight additionally uses structured list markup. Definitions are source-specific and are not merged across publications.

## Copy/version/reprint

There are no `copy` or `versions` fields. The four explicit PHB/MM `reprintedAs` links identify XPHB versions. Availability flags are source metadata, not public mechanics. All source fields are retained in internal import provenance; prose and flags are not projected publicly.

## Monster cross-entity observation

Monster source definitions currently store `senses` as `List<String>` and the public Monster `Statistics` DTO exposes those strings unchanged. They can encode configured creature capabilities, including ranges or qualifiers in the source text, but the current shape does not provide stable references to this catalog. This task leaves that model and its behavior untouched.
