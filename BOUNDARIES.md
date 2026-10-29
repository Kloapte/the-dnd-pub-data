# The DnD Pub — Canonical Data Boundaries

This document defines what information belongs in the canonical compendium, what counts as a mechanic, and how incomplete interpretations are handled. `ROADMAP.md` owns the roadmap, phases, and approvals. `AGENTS.md` owns implementation architecture, tooling, file placement, and working conventions. If policies conflict, stop and request a decision.

## 1. Canonical data scope

The canonical compendium is a collection of identifiable, retrievable, strongly typed D&D domain records. It is not a copy of 5eTools JSON, a rendering format, or an executable rules engine.

- Preserve relevant structured game facts and source attribution in normalized fields.
- Interpret mechanical meaning from `entries` and other descriptive source content into typed semantic structures where supported.
- Exclude original `entries`, prose, rich-text trees, presentation markup, and generic untyped text/mechanics fallbacks from canonical JSON.
- Keep each in-scope entity referenceable even when some or all descriptive mechanics cannot be interpreted. A Rule with no interpretable mechanic still has canonical identity and source metadata.
- Preserve only narrowly justified text fields when an explicit domain contract authorizes them; do not use text fields to bypass interpretation.
- Exclude purely presentational material (e.g., fluff images, fonts, navigation markup) unless a separately approved domain requires it.
- The approved metadata-only Source Registry is not retroactively changed by these boundaries.

## 2. Schema completeness

Schema completeness concerns **the original structured source fields**, not interpretation of prose.

- Inventory all fields and structural variations across the entire in-scope source domain before designing its schema.
- First identify mechanics already encoded structurally by the source: for example source attribution, ability modifiers, AC, HP, speed, spell level, casting time, range, damage fields, and other domain-specific values.
- Normalize and retain every relevant structured game fact in an appropriate typed canonical representation. Do not copy source-specific formatting or dynamic property shapes merely to claim coverage.
- Distinguish mechanical/domain facts from source-only presentation, navigation, redundant fields, and out-of-scope material. Record intentional exclusions with reasons.
- A relevant structured field without an adequate canonical representation is a **schema gap** requiring review, not a successful mechanical omission.
- Source `entries` and their nested prose are excluded from schema completeness; their meaning is addressed by mechanical completeness.
- Do not invent missing values or infer source metadata, edition equivalence, or mechanical defaults without evidence.

## 3. Mechanical completeness

Mechanical completeness concerns the **game meaning of entries and semantic prose**, including mechanical implications not already captured by structured fields.

- Inspect all in-scope entries and nested content, not only representative examples. Account for each meaningful mechanical portion.
- Interpret supported requirements, actions, triggers, actors, targets, checks, saves, modifiers, effects, conditions, durations, scaling, restrictions, exceptions, and relationships as applicable.
- Prefer already-approved semantic structures and source-provided mechanics over inventing new specialized representations.
- Do not claim a mechanic is unsupported until the full relevant schema, approved examples, interpretation conventions, and source evidence have been considered.
- When an interpretation is uncertain, unsupported, nonmechanical, or out of scope, document the precise reason externally. Do not invent a canonical effect or retain original prose as a fallback.
- Preserve the parent entity's canonical identity and referenceability even if no semantic mechanics can be represented. Do not insert fake or placeholder mechanics merely to make a record appear complete.
- A partially interpreted entry may contain both represented and omitted portions; account for both.
- Mechanical completeness is assessed as coverage and documented limitations, not a demand to encode every obscure mechanic at any complexity cost.

## 4. What qualifies as a mechanic

A mechanic is a source-supported statement that defines or changes game behavior, choices, requirements, constraints, quantities, timing, relationships, or outcomes. Examples include attack bonuses, advantage or disadvantage, damage and healing, movement limits, concentration ending conditions, prerequisites, saving throws, status changes, and conditional effects.

- Treat descriptive flavor, narrative background, stylistic presentation, and navigation as nonmechanical unless they contain a distinct actionable game rule.
- Separate already structured mechanical facts from mechanics that require semantic interpretation.
- Model meaning rather than simulating execution. A semantic effect may describe a conditional bonus without implementing an evaluator that calculates it during play.
- Prefer compact, domain-appropriate typed structures. Reuse an existing semantic component across mechanics or domains when the underlying meaning genuinely matches and reuse reduces complexity.
- Keep domain-specific structures when apparently similar concepts have materially different semantics. Avoid a universal mechanics object, catch-all effect map, speculative foundation, or one-off type for every source sentence.
- Preserve distinct source/edition behaviors and meaningful exceptions. Do not merge concepts based only on similar names or phrasing.
- If a mechanic is exceptionally complex or of limited practical value, document the tradeoff and propose omission or deferral rather than expanding the model without approval.

## 5. Entry accountability, omissions, and duplicates

Every meaningful in-scope entry portion must have one of these dispositions:

- **Interpreted:** Its supported game meaning is represented in canonical fields.
- **Duplicate:** It contributes no additional fact or mechanic beyond an identified existing canonical representation.
- **Omitted:** It is nonmechanical, out of scope, ambiguous, unsupported by an approved contract, or otherwise deliberately excluded with a documented reason.

Do not mark an expressible but unfinished interpretation as omitted merely to finish conversion. Do not mark a represented mechanic as omitted. A source entry may require multiple dispositions for different portions.

Maintain persistent `/doc/<model>/omitted.md` and `/doc/<model>/duplicate.md` registers with an index in `/doc/INDEX.md`. Each record must identify the source entity (canonical ID where available, otherwise type, exact source code, and name), original source file and **precise locator** (including nested path/index where needed), affected portion or mechanic, reason, and relevant canonical field or dependency if applicable. **Do not copy complete original entries or long source excerpts into these registers.** The immutable source is the authoritative place to inspect the original wording.

Use distinguishable reasons such as `SCHEMA_UNSUPPORTED`, `SOURCE_AMBIGUOUS`, `REFERENCE_UNRESOLVED`, `EXTERNAL_DEPENDENCY`, `OUT_OF_SCOPE`, and `NON_MECHANICAL`. Reasons belong in audit records, not canonical JSON. Track interpreted, duplicated, and omitted coverage without double-counting portions.

## 6. Identity, references, and dependencies

- Every in-scope canonical entity has stable identity and source attribution, regardless of mechanic interpretation coverage.
- Use source-qualified IDs of the form `<type>:<source>:<slug>` with deterministic lowercase/slug conventions. Retain the original display name and exact registered source code; detect collisions rather than silently merging them.
- Keep distinct edition/source identities and only verified reprint or alias relationships. Never assume equivalent mechanics from shared names.
- Represent meaningful relationships with typed references rather than copied target records or source markup.
- Resolve references only from adequate source evidence; name similarity alone is insufficient.
- References to domains not yet normalized may remain unresolved until the designated later step. Preserve the source-supported reference intent in an approved typed contract where possible, and retain exact unresolved source locators/tokens externally for later resolution. Do not fabricate target IDs.
- Distinguish canonical entity references from book sections, navigation links, filters, and display labels. Do not create fictional entities to satisfy references.
- Later reference resolution should validate and complete relationships, not routinely reinterpret mechanics or redesign approved domain schemas.

## 7. Provenance and licensing

- Original source datasets remain immutable. Canonical records must be traceable to their original file and record locator, source identity, and approved interpretation decisions.
- Maintain external provenance and coverage information, including source versions or hashes and schema versions where available. Do not put workflow or review-status flags in canonical domain records.
- Omission/duplicate registers refer to source locations rather than reproducing original passages.
- Normalizing game facts and omitting prose does not by itself establish permission to publish them. Check applicable licensing, attribution, and distribution rights before exposing content publicly. Do not treat private audit access as public redistribution authorization.

## 8. Validation boundaries

- Structural schema validity alone does not establish semantic correctness or completeness.
- Verify structured-field coverage, interpreted mechanic meaning, entry accounting, identity uniqueness, source/edition distinctions, reference integrity, and documented exceptions.
- Reproducibility means equivalent source evidence, approved schema, and interpretation conventions lead to consistent, reviewable canonical results. It does **not** require or permit an automatic source-to-canonical interpretation converter.
- Scripts may inventory, extract exact source data for inspection, format, and validate outputs, but must not decide mechanical meaning or perform source-to-canonical semantic conversion.
- The runtime backend retrieves and exposes canonical data; it does not execute D&D mechanics, interpret source prose, or implement a rules engine.
- Apply new policies prospectively. Existing approved artifacts are not silently rewritten; material changes require the approvals defined by `ROADMAP.md`.
