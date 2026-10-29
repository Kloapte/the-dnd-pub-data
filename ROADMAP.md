# The DnD Pub — Compendium Backend Roadmap

**Scope:** Canonical normalization and read-only compendium backend  
**Contract format:** JSON Schema Draft 2020-12

## 1. Goal

Transform the original 5eTools JSON datasets into a coherent, strongly typed, semantically structured and referenceable D&D compendium. Codex researches and interprets the source data **offline**, producing canonical JSON conforming to approved schemas. Java services retrieve, search, filter, and resolve references between these records; **they do not execute D&D rules, interpret prose, or implement a mechanics engine**.

**Guiding approach: Discover broadly, model coherently, convert incrementally — with the complete relevant domain and overall compendium in mind.**

The result should be easy to inspect, maintain, validate, extend, and consume independently of 5eTools' internal data format. Prefer the simplest faithful representation over a universal abstraction or executable simulation.

## 2. Repeatable six-phase workflow

Every normalization roadmap step uses these six approval-gated phases. A reference-resolution step may adapt the activities to its narrower objective, but must retain the six gates. No phase starts automatically after the previous phase is completed.

### Phase 1 — Full-domain source and mechanics discovery

**Objective:** Establish a comprehensive picture of the **entire in-scope domain**, including its structured source fields, recurring mechanics, exceptional cases, and dependencies, before designing schemas.

Activities:

- Inventory all relevant source files, arrays, records, fields, types, nested structures, copies, variants, and observed frequencies.
- Distinguish structured source-provided mechanics from prose/entry-derived mechanics; prioritize direct normalization of already structured information.
- Examine all records for recurring semantic patterns and rare but significant cases; use examples to illustrate findings, not to limit discovery.
- Identify identity, provenance, source-specific encodings, references, edition differences, ambiguities, duplicates, and dependencies on other roadmap domains.
- Identify likely reusable mechanics within the domain and genuinely useful shared structures across existing domains.
- Classify patterns that are valuable to represent, potentially omit, or defer; report uncertainty rather than designing speculative machinery.
- Consider the broader canonical model and later dependencies without prematurely implementing later domains.

**Deliverables:** Full-domain inventory, structured-field coverage inventory, mechanic-pattern catalog with counts/examples, dependency/reference analysis, exceptional-case and omission candidates, and proposed representative test batches. Store discovery evidence under `/discover` and related domain reports under `/reports` or `/doc/<model>` as applicable.

**Gate:** Explicit review and approval of the discovery coverage and scope before schema design.

### Phase 2 — Complete intended canonical model and schema design

**Objective:** Design coherent canonical contracts for all known in-scope source structures and mechanic patterns, with explicit decisions for anything not represented.

Activities:

- Design typed domain models and versioned JSON Schemas, reusing existing canonical structures where genuinely beneficial.
- Map each discovered structured field/pattern to a canonical field or an explicit exclusion.
- Map each discovered prose mechanic pattern to a semantic structure, omission, or dependency-based deferral; do not silently leave patterns unaddressed.
- Favor small, reusable, comprehensible structures; reject redundant per-mechanic objects and unnecessary universal abstractions.
- Model identity, source metadata, cross-domain references, and unresolved-reference forms without requiring target records to exist yet.
- Test design reasoning against ordinary, compound, and exceptional source examples before approving the schema.
- Define validation rules and document known limitations and interpretation conventions.

**Deliverables:** Canonical schemas under `src/main/resources/schemas/<model-type>/schema.json`, schema coverage matrix, representative schema examples, design decisions, known limitations, and proposed conversion conventions.

**Gate:** Explicit approval of the schema and coverage decisions. Approval establishes the intended model, not a promise that all prose can be interpreted.

### Phase 3 — Representative interpreted conversion and schema verification

**Objective:** Verify the schema and interpretation conventions with small, diverse batches before full conversion.

Activities:

- Convert approved representative batches, including straightforward, compound, edition-sensitive, and difficult examples.
- Normalize source-provided fields directly and interpret prose mechanics using researched conventions.
- Validate schema, semantic accuracy, identity, provenance, references, and expected omissions.
- Evaluate whether each proposed mechanic is useful and belongs in the canonical model; recommend omission when it adds complexity without sufficient value.
- Identify genuine schema defects or missing pattern coverage. Propose changes with impact analysis and obtain approval before applying changes to approved contracts.
- Record unsupported mechanics, duplicates, and uncertain interpretations in reviewable reports.

**Deliverables:** Representative canonical JSON examples under `datasets/<current-step>/`, validation results, interpretation decisions, omissions/duplicates reports, and any proposed schema corrections.

**Gate:** Explicit approval of the representative conversion and any schema changes before implementing the full conversion process.

### Phase 4 — Reproducible interpreted conversion and validation

**Objective:** Establish a consistent, reviewable **manual, Codex-assisted interpretation process** grounded in full-domain research, approved schemas, and documented conventions. Reproducibility means that equivalent source evidence and the same conventions lead to equivalent canonical decisions and output.

Activities:

- Document explicit conventions for source normalization, copy/override handling, field mapping, reference extraction, and interpretation of recurring semantic patterns.
- Codex interprets and produces canonical records from researched source evidence; do **not** implement an automatic conversion program or pipeline in Java, JavaScript, Python, or any other scripting or programming language.
- Apply the same interpretation conventions across records, with documented decisions for genuine exceptions. Do not construct a general-purpose source interpreter or D&D rules engine.
- Validate schema compliance, semantic accuracy, identity, provenance, reference integrity where possible, and omission accounting. Validation tools may check outputs but must **not** perform source-to-canonical conversion or mechanic interpretation.
- Check repeatability using independently reviewed representative outputs and consistent identities, formatting, ordering, and references.
- Propose schema corrections when necessary, with explicit review and approval, aiming for stable contracts before Phase 5.

**Deliverables:** Documented interpretation and conversion conventions, manually reviewed representative canonical outputs, validation evidence, regression examples, and a readiness report for full-domain conversion. No automatic conversion code.

**Gate:** Explicit approval of the interpretation conventions, validation evidence, and readiness for full-domain conversion.

### Phase 5 — Full-domain conversion and coverage review

**Objective:** Convert every in-scope source record and measure actual structured and semantic coverage using the approved model.

Activities:

- Convert the complete in-scope catalog in manageable batches with reviewable results.
- Validate all canonical records and references that can presently be resolved.
- Reconcile counts, field coverage, mechanic interpretation coverage, omissions, duplicates, unresolved references, and ambiguities.
- Keep canonical identities for records whose prose mechanics remain unrepresented.
- Treat newly discovered gaps primarily as documented omissions or defects. A necessary schema change is exceptional and requires a separate approved change decision, not automatic redesign.

**Deliverables:** Complete in-scope canonical datasets under `/src/main/resources/normalized`, validation evidence, coverage report, and updated `/doc/<model>/omitted.md` and `/doc/<model>/duplicate.md` where applicable.

**Gate:** Explicit approval of full conversion, known gaps, and coverage before backend integration.

### Phase 6 — Read-only backend integration and finalization

**Objective:** Make approved canonical data available through maintainable read-only Java services without interpreting or executing game mechanics.

Activities:

- Load and validate normalized datasets and expose approved retrieval, lookup, search, filtering, and reference-navigation operations.
- Implement domain-cohesive Java DTOs, mapping, services, and controllers consistent with `AGENTS.md` architecture and file-placement rules.
- Verify identity/source lookups, unresolved-reference behavior, API contracts, integration tests, and error handling.
- Ensure Java does not parse 5eTools entries, execute D&D mechanics, or compensate for incomplete offline normalization.
- Finalize reports and record approved outstanding dependencies for later roadmap steps.

**Deliverables:** Integrated read-only APIs, automated tests, integration report, and final domain documentation.

**Gate:** Explicit approval marking the roadmap step complete. Do not begin the next step without authorization.

## 3. Roadmap sequence

The step sequence reflects the previously approved dependency analysis. This revision changes the normalization method and clarifies Step 6; it does not automatically reopen completed steps or change domain scope without approval.

| Step | Domain | Primary outcome |
| --- | --- | --- |
| 0 | Dependency Analysis | Cross-domain dependencies and execution order |
| 1 | Source Registry | Canonical source identity, aliases, and metadata |
| 2 | Core Rules Foundation | Actions, rules, skills, conditions, diseases, statuses, senses, and languages |
| 3 | Equipment Foundation | Foundational equipment, items, properties, weapon/armor mechanics, and references |
| 4 | Spells | Complete intended Spell schema, all in-scope spells, interpreted mechanics, and preserved deferred references |
| 5 | Creatures / Monsters | Canonical creature/monster data and mechanics, including targets needed by spell references |
| 6 | Spells Reference Resolution & Validation | Resolve deferred spell references using available normalized domains; validate without routine spell-schema redesign |
| 7 | Classes and Optional Features | Classes, subclasses, progression, and associated features and choices |
| 8 | Character Building | Species, backgrounds, feats, and other character-building content and relationships |
| 9 | World Building | World-facing compendium domains and deferred advanced equipment content within the previously approved scope |
| Z | Bonus / Deferred Data | Explicitly deferred or optional domains, only when approved |

### Step 0 — Dependency Analysis

**Status: Completed and approved.** Preserve existing discovery evidence and dependency decisions. Do not rerun Step 0 as a prerequisite for this revision.

### Step 1 — Source Registry

**Status: Completed and approved (Phases 1–6).** Canonical source registry and lookup services are established. It is a metadata-only domain; do not retroactively reinterpret it or reopen it under new mechanic-coverage rules without explicit authorization. Source codes may exist without complete publication metadata. Preserve ambiguity rather than inventing metadata.

### Step 2 — Core Rules Foundation

**Scope:** `actions.json`, `variantrules.json`, `skills.json`, `conditionsdiseases.json`, `senses.json`, `languages.json` and relevant arrays therein.

**Domains:** Action, Rule, Skill, Condition, Disease, Status, Sense, Language. Treat core/variant classification according to actual source evidence, not filename alone. Preserve meaningful edition distinctions and referenceable Rule identities even where interpretation is incomplete.

**Focus:** Actions and timing; checks, saves, contests, attacks, modifiers, damage and healing; movement and spatial rules; conditions, concentration, statuses and diseases; skill/ability relationships; senses and languages; rules applicability, prerequisites, and exceptions. Reuse semantic structures where they genuinely fit. Avoid proliferating specialized procedure families for each source paragraph.

**Status:** In progress. Existing approvals and artifacts remain historical evidence. Adoption of the revised methodology does not silently invalidate or restart approved Phase 1/2 work or subsequent completed work; any targeted re-review or schema simplification requires an explicit scoped decision. Confirm the current phase from the repository before continuing.

### Step 3 — Equipment Foundation

**Scope:** Foundational item content from `items-base.json`, `items.json`, and required templates or source structures; record dependencies on advanced items and `magicvariants.json` for later steps.

**Focus:** Base-item identity, item types/properties, weapons and weapon masteries, armor/shields, damage, range, weight, requirements, item relationships, and offline template expansion. Do not expand advanced magical equipment merely to complete foundational references. Document the assignment of deferred equipment to the later step.

### Step 4 — Spells

**Scope:** All in-scope spell source files under `/spells/**.json`.

**Focus:** Source-provided spell identity, level, school, casting time, range, components, duration, concentration, and other structured fields; interpret prose effects including damage, healing, conditions, saving throws, attacks, scaling, targeting, summoning, transformation, and conditional effects where meaningful and supportable.

**Completion expectation:** Design the **complete intended Spell schema** from the full spell catalog and convert all in-scope spells. References to not-yet-normalized monsters or other domains may remain unresolved but must be preserved and accounted for. Do not defer known spell mechanics solely because a referenced entity does not yet exist.

### Step 5 — Creatures / Monsters

**Scope:** All in-scope creature/monster source records under `/monsters/**.json`, including copies, variants, and relevant source relationships.

**Focus:** Source-provided statistics such as AC, HP, ability scores, speeds, senses, resistances, immunities, attacks, spellcasting, and other structured mechanics; semantic interpretation of actions, traits, reactions, and other entries. Preserve edition and copy/variant distinctions and stable references.

**Dependency outcome:** Make available the canonical creature identities and data needed for subsequent spell reference validation.

### Step 6 — Spells Reference Resolution & Validation

**Scope:** Deferred references and dependency-based validation issues recorded during Step 4, especially those resolvable after Step 5. This is **not** a second full spell conversion or a routine new-mechanics phase.

Use the same six approval gates, with reference-specific deliverables:

1. Inventory unresolved/deferred spell references and available target datasets.
2. Review existing reference contracts and resolution rules; change schemas only for demonstrated defects and with approval.
3. Resolve and validate representative reference cases, including ambiguity and edition distinctions.
4. Establish documented, reproducible reference-matching and validation conventions; no automatic matching/conversion program or runtime interpreter.
5. Process all deferred references in scope and report resolved, ambiguous, missing, and still-deferred targets.
6. Integrate verified reference updates into read-only retrieval and navigation, test, and finalize.

Preserve existing spell identities and mechanics unless a separately reviewed correction is justified. Do not invent references when names are insufficient to establish equivalence.

### Step 7 — Classes and Optional Features

**Focus:** Classes, subclasses, level progression, class and subclass features, optional features, choices, prerequisites, and references to spells, equipment, and rules. Analyze all relevant source files during Phase 1 and confirm the exact in-scope catalog before schema design. Preserve choice requirements without inventing eligible sets.

### Step 8 — Character Building

**Focus:** Character-building domains such as species, backgrounds, feats, traits, proficiencies, starting choices, and references to previously normalized foundations. Determine the complete in-scope catalog in Phase 1; keep canonical models as compendium data rather than an executable character builder.

### Step 9 — World Building

**Focus:** World-facing reference content and the advanced equipment/magical variants deferred from Step 3, subject to the existing approved scope and Phase 1 inventory. Avoid duplicating foundational item identities or turning descriptive lore into fabricated mechanics. Confirm domain subdivisions from source evidence.

### Step Z — Bonus / Deferred Data

**Focus:** Optional, nonessential, or deliberately deferred content. No automatic inclusion. Inventory and approve the exact scope before work starts; apply the same six-phase workflow.

## 4. Change control and approvals

- Each phase requires its own explicit approval; each step requires explicit approval before the next begins.
- Phase 1 is discovery, not production implementation. Phase 2 is schema design, not full conversion.
- Phase 3 and Phase 4 may propose and, after approval, implement schema corrections.
- Phase 5 is expected to use stable schemas. Any contract redesign there is exceptional, documented, and separately approved.
- A completed step is not automatically reopened when roadmap policy changes. Preserve approved historical artifacts and agree on a scoped migration or re-review first.
- No silent rewriting of source data, canonical IDs, prior interpretations, approved contracts, or phase status.
- Maintain concise, evidence-backed reports, and distinguish decisions from proposals and unresolved questions.