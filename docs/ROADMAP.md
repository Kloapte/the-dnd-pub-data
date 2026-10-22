# The DnD Pub — Compendium Backend — Roadmap

**Status: Finalized and approved.** The approved sequence and scope are established below. Phase and step approval checkpoints remain in force.

## Goal

The goal of this project is to transform D&D 5eTools source data into a normalized, strongly typed, semantically enriched compendium for The DnD Pub.

Codex will interpret the original JSON sources and generate canonical JSON datasets conforming to versioned JSON Schema contracts.

The backend will consume these normalized datasets without requiring source-specific interpretation at runtime.

The intended outcome is:

- Clean, consistent, and maintainable domain models.
- Explicit, versioned JSON Schema contracts.
- Structured, reusable game mechanics derived from source information.
- Minimal source-specific complexity in Java.
- Deterministic mechanics execution in the backend.
- Traceable and reviewable normalization results.
- Incremental processing of future source changes.

Semantic enrichment is the preferred approach from Step 1 onward. When semantic interpretation is unreliable, unnecessarily complex, or counterproductive, Codex must report its findings and propose an alternative for approval.

## Core principles

- Preserve the original source data without modification.
- Treat JSON Schema Draft 2020-12 as the authoritative structural contract.
- Interpret source data offline rather than during application runtime.
- Prefer explicit, strongly typed canonical structures.
- Avoid unnecessary polymorphism, dynamic properties, and source-specific representations.
- Allow meaningful domain submodels when they improve clarity and maintainability.
- Evaluate inheritance, composition, and independent models before committing to a structure.
- Preserve all meaningful source information and traceability.
- Never invent mechanical values, source relationships, references, or provenance.
- Preserve ambiguity and unsupported constructs explicitly.
- Prefer reusable mechanics over entity-specific implementations.
- Validate both structural correctness and semantic accuracy.
- Require explicit approval before proceeding between normalization phases and roadmap steps.
- Do not introduce speculative frameworks or unnecessary abstraction layers.

## Roadmap execution

Step 0 is completed and approved. The sequence below incorporates the approved dependency analysis and planning decisions.

Evidence:

- [Step 0 discovery report](../discover/STEP_0_DEPENDENCY_ANALYSIS.md).
- [Dependency tables and source inventory](../discover/step-0/dependency-tables.md).

The discovery artifacts retain the original step numbering as historical evidence. The step numbers in this roadmap govern future work. Do not repeat Step 0 to execute this revision.

| Step | Domain |
| --- | --- |
| 1 | Source Registry |
| 2 | Core Rules Foundation |
| 3 | Equipment Foundation |
| 4 | Spells |
| 5 | Creatures / Monsters |
| 6 | Spells Completion |
| 7 | Classes and Optional Features |
| 8 | Character Building |
| 9 | World Building |
| Z | Bonus / Deferred Data |

Each normalization domain in Steps 1–Z follows the six-phase normalization workflow described below.

Every phase is a separate milestone requiring approval before proceeding.

Coordinating domains within a step preserves separate cohesive domain contracts and all phase approval checkpoints. Step 6 applies the same six phases to incremental enrichment of existing spells, with contract changes reviewed before conversion.

The approved roadmap sequence and scope remain unchanged. Step 0 is complete and approved. Step 1 is complete and approved through all six phases. Step 2, Phase 1 is complete and awaiting review; Phase 2 and all later work remain subject to their approval checkpoints.

## Identity, references, and interpretation coverage

- Start source identity work from `sources/registry.json`, subject to validation against the supplied metadata and source usage.
- Preserve code-only sources without inventing publication names, dates, versions, relationships, or other unavailable metadata.
- Include adventure metadata only where useful for source identification. Publication content and stories are outside this roadmap's scope; preserve relevant citations and rule excerpts already present in domain datasets.
- Review source-format defaults, edition differences, aliases, and reprints before resolving canonical identities. Shared names, omitted source components, and reprint links do not establish mechanical equivalence.
- Use predictable identifiers following `<type>:<source>:<name>`. Allow contextual qualification of the name component when needed to distinguish classes, subclasses, feature levels, pantheons, parent species, or other source-supported identity context.
- Preserve stable identities wherever possible. Review compatibility and migration implications before changing an existing identifier; do not create a new identity solely because an interpretation is enriched later.
- Preserve source references to later domains without requiring immediate mechanics execution. Resolve canonical targets only when evidence establishes their identities; retain unresolved source references explicitly until then.
- Treat 5eTools page navigation, including `{@5etools ...}` links, as noncanonical navigation metadata, not resolved entity references. Preserve useful source navigation separately from canonical relationships.
- Preserve filter criteria and choice requirements without assuming that a page, selector, or display label supplies a canonical entity or complete eligible set.
- Preserve existing SRD and SRD52 flags without inventing, inferring, or reassigning status.
- Keep provenance and the original source location for every normalized entity and later enrichment. Do not silently overwrite approved interpretations or duplicate previously normalized external models.

Track interpretation coverage at the affected rule or mechanic, since one entity may have several outcomes:

| Interpretation status | Meaning and required accounting |
| --- | --- |
| Complete | The interpretation is supported by source evidence and validated within the approved scope; applicable execution behavior is verified during integration. |
| Deferred | A known dependency or later milestone prevents completion. Record the affected behavior, preserved references, reason, and intended revisit step or explicit future approval checkpoint. |
| Unsupported | Required content is unavailable or behavior cannot be represented or executed under the approved contract. Preserve evidence and state the limitation explicitly. |
| Ambiguous | Source evidence permits conflicting interpretations or is insufficient to choose one reliably. Preserve the uncertainty and the decision requiring review. |

Do not present deferred, unsupported, or ambiguous mechanics as complete. A step may finish with an explicitly reviewed partial dataset and documented execution limits; report each outstanding dependency and its disposition.

Document missing publication content and catalogs as explicitly unsupported where they prevent interpretation. Step 0 identified gaps involving quick-reference content, standalone tables, decks/cards, psionics, general class spell membership, and referenced publication prose. Related inline material must be reviewed before claiming a particular target's content is unavailable. Neither reordering nor a valid reference supplies missing mechanics.

Account for all source domains and ancillary arrays. Unlisted sources are not automatically out of scope: assign them during the relevant analysis, or record an explicit deferral or approved exclusion. Maintain the distinction between content outside the approved scope and missing content required by an in-scope mechanic.

---

# Normalization workflow

## Phase 1 — Source and domain analysis

### Objective

Understand the source structures, their game semantics, dependencies, and potential canonical domain models before designing contracts or generating normalized data.

This phase is primarily investigative. Do not modify production code, approved schemas, or normalized datasets.

### 1.1 Source inventory and structural analysis

Inspect all relevant source files for the domain.

Determine:

- Total source files and entries.
- Every observed property and its data types.
- Required, optional, conditional, and mutually exclusive properties.
- Nested structures and their variations.
- Dynamic property names and polymorphic representations.
- Inconsistent representations of equivalent information.
- Source-specific formatting and encoded structures.
- Inheritance, copying, overrides, and duplicated source entries.
- Embedded tags and references, including `{@...}` structures.
- Structures requiring preprocessing or reference resolution.
- Outliers and structures that cannot be represented consistently.

Quantify property and structural variation frequencies wherever possible.

Do not assume that similarly named properties have identical semantics.

### 1.2 Semantic and mechanics analysis

Determine what the source information represents in terms of D&D rules and gameplay.

Investigate:

- Explicit and implied game mechanics.
- Actions, reactions, bonus actions, and other activation requirements.
- Preconditions, prerequisites, and restrictions.
- Targeting, ranges, areas, and affected entities.
- Damage, healing, conditions, and other effects.
- Durations, concentration, and termination conditions.
- Scaling, progression, and resource consumption.
- Saving throws, ability checks, and attack rolls.
- Rule exceptions, conditional behavior, and interactions.
- Dependencies on other rules and game entities.
- Ambiguous, contradictory, or incomplete source information.

Not every domain will use every mechanic category.

Distinguish source facts from interpretations. Do not infer unsupported values.

Determine whether full semantic enrichment is feasible for the domain.

If semantic normalization would introduce excessive complexity, unreliable assumptions, or disproportionate maintenance costs, explain the issue and propose an alternative for approval.

### 1.3 Dependency analysis

Identify relationships between source entities, normalized models, and game rules.

Investigate:

- Explicit references to other source entries.
- Embedded 5eTools references, including `{@...}` tags.
- References to rules, actions, conditions, spells, items, and other model types.
- Internal relationships between entities of the same domain.
- External relationships with other domains.
- Dependencies requiring prior normalization.
- Dependencies that can remain unresolved until later steps.
- Circular references and ambiguous references.
- Shared concepts that may deserve independent models.
- Potential effects on the approved roadmap order.

Distinguish actual source references from inferred semantic relationships.

Report dependencies that could prevent reliable normalization.

### 1.4 Domain model and submodel analysis

Determine whether the source domain should be represented by one canonical model or multiple related models.

Evaluate:

- Common properties and behavior across source entries.
- Meaningful semantic categories.
- Distinct structures or behaviors that justify specialized models.
- Abstract parent models with shared identity or properties.
- Composition using reusable components.
- Independent models connected through references.
- Reusable mechanics, effects, and value objects.
- Entities belonging to multiple semantic categories.
- The impact of submodels on JSON Schema and Java DTOs.
- The impact on references, validation, and mechanics execution.
- Future extensibility and maintainability.

Explicitly compare the following approaches where relevant:

1. A single canonical model.
2. A common abstract model with specialized submodels.
3. A canonical model composed of reusable components.
4. Multiple independent models with shared references.

For example, spells may be evaluated as:

- A common Spell model with specialized DamagingSpell, HealingSpell, or UtilitySpell models.
- A common Spell model containing reusable DamageEffect, HealingEffect, and other effect components.
- A combination of specialized models and reusable effects.

Do not assume these are mutually exclusive semantic categories.

Avoid introducing inheritance solely because source structures differ.

Avoid excessively broad models with many unrelated optional properties.

Avoid excessive fragmentation into models without clear domain responsibilities.

Recommend the simplest maintainable representation that faithfully preserves the source semantics.

For significant alternatives, document advantages, disadvantages, and expected maintenance impact.

### 1.5 Coverage and outlier analysis

Quantify the analysis wherever possible.

Report:

- Total source entries inspected.
- Number of entries matching common structural patterns.
- Number of entries requiring specialized handling.
- Percentage coverage of identified structural patterns.
- Observed property frequencies.
- Structural variants and their occurrence counts.
- Semantic categories and their occurrence counts.
- Proposed submodels and their associated entry counts.
- Entries belonging to multiple categories.
- Unsupported structures and their occurrence counts.
- Ambiguous or incomplete mechanics.
- Unresolved dependencies and references.
- Representative examples of common cases, edge cases, and outliers.
- Estimated coverage achievable with the proposed canonical model.

Distinguish complete inventories from sampled findings.

Do not fabricate statistics or claim complete coverage without sufficient evidence.

### 1.6 Analysis deliverables

Produce a discovery report under `/discover` containing:

1. Source inventory.
2. Structural analysis and occurrence statistics.
3. Semantic and mechanics analysis.
4. Dependency analysis.
5. Proposed domain models and submodel alternatives.
6. Coverage and outlier analysis.
7. Normalization feasibility assessment.
8. Recommended approach.
9. Risks, limitations, and unresolved decisions.

Include representative source examples where useful.

The analysis must provide sufficient information to make an informed decision about the canonical model.

**Approval checkpoint:** Stop and request approval before proceeding to Phase 2.

---

## Phase 2 — Canonical model and JSON Schema

### Objective

Design the canonical domain model and its JSON Schema contracts based on the approved analysis.

### Activities

- Define the canonical model boundaries.
- Determine shared properties and reusable components.
- Define approved submodels and their relationships.
- Choose inheritance, composition, or independent models where appropriate.
- Define stable identifiers and cross-model references.
- Define explicit field names, data types, and constraints.
- Define required and optional properties.
- Define enumerations and reusable schema definitions.
- Define representations for conditional and unsupported mechanics.
- Define how ambiguity and unresolved references are preserved.
- Define provenance and versioning requirements.
- Create JSON Schema Draft 2020-12 contracts.
- Provide representative canonical JSON examples.
- Document mappings between source structures and canonical properties.
- Evaluate compatibility with existing normalized models.
- Validate the schemas and examples.

Do not introduce speculative fields unsupported by the source or approved domain design.

Do not begin source conversion during this phase.

### Deliverables

- Canonical model proposal and approved design decisions.
- Versioned JSON Schema contracts.
- Representative JSON examples.
- Source-to-canonical mapping documentation.
- Identified limitations and unresolved decisions.

Store machine-readable schemas in the designated schema directory and system documentation under `/doc`.

**Approval checkpoint:** Stop and request approval before proceeding to Phase 3.

---

## Phase 3 — Representative conversion

### Objective

Validate the proposed contracts by converting a small, representative set of source entries.

### Activities

- Select representative source entries.
- Include common structures and meaningful submodel variations.
- Include optional properties and conditional structures.
- Include known edge cases and outliers.
- Include entries with dependencies and references.
- Convert the selected entries into canonical JSON.
- Apply semantic enrichment according to the approved model.
- Preserve source identity, references, and provenance.
- Record unsupported mechanics and ambiguous interpretations.
- Do not silently change approved contracts.

### Deliverables

- Representative normalized JSON dataset.
- Conversion coverage report.
- List of unresolved or unsupported interpretations.
- Identified contract limitations.

**Approval checkpoint:** Stop and request approval before proceeding to Phase 4.

---

## Phase 4 — Validation and feedback

### Objective

Verify that the proposed domain model accurately and maintainably represents the source information.

### Activities

- Validate generated JSON against approved schemas.
- Verify source entry coverage.
- Verify preservation of meaningful source information.
- Verify identifiers and cross-model references.
- Verify provenance and source traceability.
- Compare semantic interpretations against source evidence.
- Identify missing mechanics, restrictions, and exceptions.
- Evaluate whether submodel boundaries remain appropriate.
- Identify unnecessary duplication or complexity.
- Identify recurring exceptions suggesting weaknesses in the contract.
- Evaluate whether inheritance or composition remains suitable.
- Assess the maintainability of the generated models.
- Propose contract improvements where necessary.
- Repeat representative conversion and validation after approved changes.

Schema compliance alone does not establish semantic correctness.

### Deliverables

- Validation results.
- Semantic correctness assessment.
- Coverage and completeness report.
- Domain model suitability assessment.
- Recommended contract refinements.
- Remaining risks and limitations.

Continue the feedback loop until the representative conversion is accepted.

**Approval checkpoint:** Stop and request approval before proceeding to Phase 5.

---

## Phase 5 — Dataset expansion

**Status:** Completed and approved. The canonical Source dataset is stored at `src/main/resources/normalized/sources/sources.json`; provenance, conversion tooling, and reports remain outside runtime resources.

### Objective

Normalize the remaining source entries using the approved contracts and interpretation conventions.

### Activities

- Process remaining source entries in manageable batches.
- Apply established normalization conventions consistently.
- Perform semantic enrichment according to approved contracts.
- Validate each generated batch.
- Verify identifiers, references, and provenance.
- Track successfully converted entries.
- Track unsupported, ambiguous, and unresolved entries.
- Detect duplicate identifiers and missing references.
- Detect unexpected source structures and new semantic variations.
- Preserve previously approved interpretations.
- Report significant contract problems before introducing changes.
- Perform final coverage and integrity validation.

Do not silently omit entries that cannot be normalized.

Do not claim full completion when entries remain unaccounted for.

### Deliverables

- Complete normalized dataset or explicitly documented partial dataset.
- Dataset validation results.
- Final normalization coverage report.
- Unsupported and unresolved entry report.
- Provenance and versioning metadata.
- Identified integration limitations.

**Approval checkpoint:** Phase 5 is approved. Stop here; Phase 6 requires separate authorization.

---

## Phase 6 — Backend integration

**Status:** Implementation completed; awaiting review. Phase 6 is not yet approved.

### Objective

Integrate the approved canonical datasets into the backend using strongly typed Java models and deterministic application behavior.

### Activities

- Create or update Java DTOs matching the canonical JSON contracts.
- Prefer straightforward Jackson serialization and deserialization.
- Avoid source-specific runtime interpretation.
- Integrate normalized datasets into existing application services.
- Implement deterministic mechanics execution where supported by approved contracts.
- Validate application-facing references and relationships.
- Add or update deserialization and contract tests.
- Add relevant domain and mechanics tests.
- Verify compatibility with existing API behavior.
- Identify obsolete extraction and interpretation code.
- Remove obsolete code only when explicitly authorized.
- Update system documentation under `/doc`.
- Verify integration using the applicable test suite.

### Deliverables

- Integrated canonical data models.
- Updated application services and APIs where required.
- Relevant automated tests.
- Updated system documentation.
- Integration and test coverage report.
- Remaining limitations and technical debt.

**Approval checkpoint:** Stop after integration and verification. Do not automatically proceed to the next roadmap domain.

---

# Roadmap steps

## Step 0 — Dependency analysis and roadmap validation

### Status and evidence

Completed and approved. The [discovery report](../discover/STEP_0_DEPENDENCY_ANALYSIS.md) and [dependency tables](../discover/step-0/dependency-tables.md) are the evidence for the approved sequence. Retain the original Step 0 scope and deliverables below for traceability; no repeat analysis is authorized by this update.

### Objective

Determine whether the proposed roadmap order is viable based on actual dependencies within the 5eTools source data.

This step validates the roadmap rather than performing normalization.

### Scope

Inspect source relationships across all planned domains.

Pay particular attention to embedded `{@...}` references, source inheritance, copied entries, and dependencies on other game rules.

### Activities

- Inventory the relevant source domains.
- Identify cross-domain references and dependencies.
- Analyze embedded `{@...}` references.
- Identify dependencies on books, actions, variant rules, and shared mechanics.
- Identify circular dependencies.
- Distinguish required normalization dependencies from references that can be resolved later.
- Identify shared foundational concepts.
- Evaluate whether the current roadmap order is viable.
- Identify domains that may need to be split, combined, or reordered.
- Assess whether any dependencies prevent meaningful semantic enrichment.
- Recommend changes only when supported by actual source evidence.

### Deliverables

Produce a report under `/discover` containing:

- Dependency inventory.
- Cross-domain dependency matrix.
- Significant dependency chains.
- Circular and unresolved dependencies.
- Evaluation of the existing roadmap order.
- Proposed adjustments, if necessary.
- Risks and unresolved questions.

Do not normalize datasets, create canonical schemas, or modify the roadmap during this step.

**Approval checkpoint:** Step 0 findings and the planning decisions reflected below are completed and approved.

---

## Step 1 — Source Registry

**Status:** Step 1 is completed and approved. Phases 1–6 are each completed and approved.

### Objective

Establish validated source identity and metadata relationships that all other domains can reference.

Semantic enrichment is intended from this step onward.

### Source files

- `sources/registry.json` — starting point for source identity, subject to validation.
- `books.json` — publication metadata useful for identifying sources.
- `adventures.json` — metadata only where useful for source identification.

Source paths in Steps 1–Z are relative to `src/main/resources/data`.

### Focus

**Registry and publication metadata**

- Establish source identity and relationships.
- Validate existing registry codes and aliases against relevant book/adventure metadata and source usage.
- Preserve source metadata and existing identifiers.
- Provide consistent source references for other normalized entities.
- Preserve code-only sources where richer metadata is unavailable.
- Preserve explicit distinctions between publication IDs, source codes, aliases, and parent relationships.
- Record unresolved or ambiguous metadata without inferring missing values.

### Scope boundary

Publication content and stories are out of scope. Adventure metadata is included only for source identification. Metadata lookup does not supply referenced chapter prose or establish rule equivalence between editions.

### Submodel analysis

Evaluate source identity, publication metadata, aliases, and parent relationships as cohesive responsibilities. Avoid requiring complete publication metadata for every valid source code.

### Completion criteria

- The registry and relevant metadata have approved canonical contracts and validated source relationships.
- Foundational source references and code-only sources are accounted for.
- Unresolved, ambiguous, and unavailable metadata are documented.
- Approved datasets are integrated and verified.

---

## Step 2 — Core Rules Foundation

**Phase 1 status:** Source and domain analysis is completed and approved.

**Phase 2 status:** Canonical model and JSON Schema design is completed and awaiting review. Phase 3 has not begun.

### Objective

Coordinate the original Steps 1 and 2 game-rule domains as a shared foundation while maintaining separate cohesive domain contracts.

Semantic enrichment includes actions, rules, skills, conditions, diseases, statuses, senses, and languages. Shared references and reusable mechanics must support their circular relationships without recursively embedding complete external models.

### Source files

- `actions.json`
- `variantrules.json`
- `skills.json`
- `conditionsdiseases.json` — conditions, diseases, and statuses.
- `senses.json`
- `languages.json` — languages and language scripts.

### Focus

**Actions**

- Identify available game actions and their mechanical meaning.
- Interpret action requirements, restrictions, and outcomes.
- Identify reusable action mechanics and references.
- Evaluate whether distinct action categories require submodels.

**Core and variant rules**

- Distinguish core definitions, optional rules, and alternative rules using source evidence; `variantrules.json` is not exclusively optional content.
- Analyze optional and alternative game rules without automatically excluding them from semantic enrichment.
- Identify mechanics that modify or extend standard behavior.
- Identify dependencies on actions, conditions, abilities, and other game concepts.
- Determine rule applicability, activation, exceptions, and edition-specific behavior.
- Evaluate specialized submodels and reusable mechanics components.
- Preserve distinctions between standard rules and optional variants.

**Skills**

- Identify core skills and their relationships with abilities.
- Interpret skill-related mechanics and checks.
- Establish stable references for other models.

**Conditions, diseases, and statuses**

- Identify conditions, diseases, statuses, and their mechanical effects.
- Interpret restrictions, penalties, advantages, disadvantages, and exceptions.
- Evaluate whether conditions, diseases, and statuses should be independent models.
- Identify reusable condition effects and references.
- Preserve disease dependencies on spells, world effects, or missing rules as explicit interpretation outcomes.

**Senses**

- Identify vision and perception capabilities.
- Interpret ranges, limitations, and interactions with conditions.
- Evaluate whether different sense types require specialized structures.

**Languages**

- Establish language identity and relationships.
- Identify language categories and relevant metadata.
- Preserve language scripts and their relationships.
- Preserve references used by creatures and characters.

### Coordination and submodel analysis

Analyze shared checks, saves, attacks, proficiency, advantage/disadvantage, action timing, movement, targeting, duration, concentration, and effects where supported by the source. Coordinate dependent rule definitions, conditions, and senses while keeping their domain identities and contracts cohesive.

Determine manageable analysis and conversion batches during Phase 1. Coordination does not bypass phase approvals or require complete execution of later-domain references.

Variant rules may be semantically complex. If full semantic enrichment proves unreliable or counterproductive, identify the affected structures, explain the limitations, and propose a reduced or alternative approach for approval. Do not automatically exclude those rules.

### Completion criteria

- Foundational models have approved schemas.
- Relevant mechanics are normalized.
- Shared references are established.
- Complete, deferred, unsupported, and ambiguous interpretations and execution limits are documented.
- Approved datasets are integrated and verified.

---

## Step 3 — Equipment Foundation

### Objective

Normalize foundational equipment identities and mechanics used by spells, creatures, classes, and character building.

### Source files

- `items-base.json` — foundational base items, item types, properties, masteries, type supplements, and required entry templates.
- `items.json` — foundational equipment and identity relationships required by the approved analysis scope.
- References into `magicvariants.json` and other equipment content must remain traceable; advanced equipment and magical variants are deferred to Step 9.

### Focus

- Foundational equipment identities and source-qualified references.
- Weapon categories, damage, range, ammunition, and relevant attack rules.
- Armor, shields, armor class, requirements, and relevant restrictions.
- Item types, item properties, and weapon masteries.
- Base-item relationships and foundational equipment choices, packs, and contents where applicable.
- Required item-entry templates, substitutions, inherited descriptions, and source-specific expansion performed offline.
- Dependencies on core rules, conditions, and later mechanics.

### Scope and submodel analysis

Evaluate base items, weapons, armor, types, properties, masteries, and entry templates as cohesive models or reusable components. Define the boundary between foundational and advanced equipment during Phase 1, accounting for every affected entry or structure. Record content assigned to Step 9 explicitly.

Do not expand advanced equipment or magical variants in this step. Reuse foundation identities in later equipment enrichment; do not create duplicate models solely to complete deferred mechanics.

### Completion criteria

- Foundational equipment contracts and required templates are approved.
- Representative weapons, armor, properties, masteries, and template-dependent structures are validated.
- Foundational identities and references are established, with deferred equipment content assigned to Step 9.
- Semantic coverage, interpretation statuses, and execution limits are documented.
- Approved datasets are integrated and verified.

---

## Step 4 — Spells

### Objective

Normalize spells and interpret their mechanical effects into reusable, structured representations.

### Source files

- `/spells/**.json`

### Focus

- Spell identity, source, level, and school.
- Casting requirements and activation.
- Components and material requirements.
- Range, targeting, and areas of effect.
- Duration and concentration.
- Damage, healing, and conditions.
- Saving throws and attack rolls.
- Scaling and higher-level casting.
- Resource consumption.
- Summoning and transformations.
- Conditional and delayed effects.
- Interactions with other spells and game rules.
- Unsupported or ambiguous mechanics.

### Submodel analysis

Explicitly evaluate whether spells should use:

- One canonical Spell model.
- An abstract Spell model with specialized spell types.
- A Spell model composed of reusable effects.
- A hybrid approach combining specialized models and reusable components.

Consider spells with multiple simultaneous effects.

Avoid forcing mutually compatible spell behaviors into exclusive subtypes.

### Deferred dependencies

Normalize spells before creatures. Preserve stable references for summoning, transformations, creature statistics, and species-dependent effects without requiring immediate mechanics execution.

Record each deferred interpretation, affected normalized spell identity, source evidence, dependency, and execution limitation. Assign creature-dependent work to Step 6 after Step 5. Species and other dependencies that remain unavailable at Step 6 must retain an explicit later revisit checkpoint; Step 6 does not authorize beginning Character Building or another domain.

Missing publication content or catalogs must be documented as unsupported where applicable. Ambiguous source mechanics remain identifiable for review.

### Completion criteria

- Spell model and effect contracts are approved.
- Representative spell categories and outliers are validated.
- All source entries are accounted for.
- Semantic coverage and complete, deferred, unsupported, and ambiguous interpretations are documented.
- Creature/species-dependent mechanics have stable source references and explicit revisit checkpoints.
- Approved datasets are integrated and verified.

---

## Step 5 — Creatures / Monsters

### Objective

Normalize creatures and their combat, movement, perception, and special mechanics.

### Source files

- `/monsters/**.json`
- Include bestiary files, `monsters/template.json`, and `monsters/legendarygroups.json`, including their ancillary template arrays.

### Focus

- Creature identity, type, size, and source.
- Ability scores and derived statistics.
- Armor class, hit points, and movement.
- Skills, saving throws, senses, and languages.
- Resistances, immunities, and vulnerabilities.
- Traits and passive abilities.
- Actions, bonus actions, and reactions.
- Legendary, lair, and other special actions.
- Spellcasting and spell references.
- Multiattack and attack mechanics.
- Conditions, transformations, and special effects.
- Challenge rating and relevant progression data.
- Source variants, inherited data, and overrides.
- Monster templates, legendary-group templates, generated versions, and their identity relationships.
- Ordered copy, inheritance, preservation, template, and override operations interpreted offline.
- Summoned creatures and scaling that requires spell or class context.

### Submodel analysis

Evaluate whether creature categories, action types, traits, or spellcasting capabilities justify specialized models or reusable components.

Avoid excessive creature-specific implementations.

### Completion criteria

- Creature and associated mechanic contracts are approved.
- Representative creature structures are validated.
- All source entries are accounted for.
- Inheritance targets, templates, versions, legendary-group references, and source traceability are verified.
- Semantic coverage and all interpretation statuses are documented, including spell/class-dependent execution limits.
- Creature dependencies available for Step 6 are identified without duplicating referenced spell entities.
- Approved datasets are integrated and verified.

---

## Step 6 — Spells Completion

### Objective

Revisit deferred spell mechanics after creature normalization, enriching existing normalized spell entities while preserving their stable identities and approved interpretations.

### Source files and existing artifacts

- `/spells/**.json` — original source evidence for the affected spells.
- `/monsters/**.json` — approved creature, template, legendary-group, version, and inheritance evidence relevant to those spells.
- Existing approved spell contracts, normalized spell entities, provenance, and deferred-interpretation records from Step 4.
- Approved creature contracts and normalized entities from Step 5.

### Focus

- Reconcile the Step 4 deferred interpretations with the creature models now available.
- Enrich summoning, transformations, form selection, creature-dependent scaling, and related effects where supported.
- Resolve eligible references with source and edition context; reuse creature identities without embedding full external models.
- Preserve each spell's identity and traceability. Do not generate duplicate spell entities or regenerate unaffected approved interpretations.
- Review contract suitability and submodel boundaries for newly supported behavior; require approval before contract changes or conversion.
- Validate newly supported mechanics and compatibility with existing spell and creature behavior.
- Preserve species, class, advanced-equipment, supplementary, or missing-content dependencies that remain unavailable.

### Workflow and remaining dependencies

Apply all six normalization phases to the affected spell interpretations. Phase 1 identifies the affected set and needed modeling refinements; Phase 2 reviews contract compatibility and obtains approval before representative enrichment. Representative conversion, validation, expansion to the remaining affected entries, and integration retain their separate approval checkpoints.

Every Step 4 deferral must be accounted for as complete, still deferred, unsupported, or ambiguous. If species mechanics still depend on Step 8, record that dependency and a future approval checkpoint for enrichment once available. Apply the same rule to other later dependencies. A deferred reference is not a claim of executable mechanics, and this step does not authorize work in a later domain.

### Completion criteria

- Deferred spells have been reviewed against approved creature models and original source evidence.
- Supported creature-dependent mechanics are enriched in existing spell entities and validated.
- Stable spell identities and prior approved interpretations are preserved, with no duplicate entities.
- All original deferrals have a reviewed disposition; remaining dependencies and revisit checkpoints are documented.
- Approved enrichment is integrated and verified, with its execution coverage and limitations reported.

---

## Step 7 — Classes and Optional Features

### Objective

Normalize classes, subclasses, features, progression, and optional-feature pools together while preserving their distinct domain identities.

### Source files

- `/classes/**.json`
- `optionalfeatures.json`

### Focus

- Class and subclass identity.
- Class prerequisites and proficiencies.
- Hit dice and starting capabilities.
- Level progression.
- Class and subclass features.
- Spellcasting progression.
- Resource pools and recovery.
- Feature choices and optional capabilities.
- Optional-feature pools, feature types, eligibility, prerequisites, and selection progression.
- Optional-feature relationships to classes, subclasses, feats, and other features.
- Scaling mechanics.
- Dependencies between features.
- Conditional and replacement features.
- Interactions with spells and other game rules.

### Submodel analysis

Evaluate class progression, feature types, optional-feature pools, resource mechanics, and subclass relationships. Review contextual identity qualification for repeated feature names, levels, and class/subclass source combinations.

Prefer reusable feature and progression components over large class-specific structures.

Preserve references to feats and character-building domains normalized in Step 8. Treat spell selectors as selection requirements; do not infer complete class spell membership from page/filter labels. Document missing psionic talent/discipline catalogs, general class spell membership, or other required content as unsupported where applicable.

### Completion criteria

- Class, subclass, feature, progression, and optional-feature contracts are approved.
- Representative progression, eligibility, and optional-feature selection structures are validated.
- All source entries are accounted for.
- Semantic coverage and unsupported mechanics are documented.
- Approved datasets are integrated and verified.

---

## Step 8 — Character Building

### Objective

Normalize character creation and customization data.

### Source files

- `races.json`
- `feats.json`
- `backgrounds.json`
- `names.json`
- Reuse approved optional features from Step 7 through references.

### Focus

- Species, subraces, parent relationships, inherited traits, and source variants.
- Character backgrounds.
- Feats and prerequisites.
- Optional and selectable features.
- Ability score modifications.
- Proficiencies and languages.
- Feature choices and restrictions.
- Character creation dependencies.
- Mechanical effects and progression.
- Naming data and related metadata.

### Submodel analysis

Evaluate whether traits, feats, and backgrounds can share reusable mechanics with the approved optional features from Step 7 without losing their distinct domain identities. Preserve contextual subrace identity and parent traceability; do not duplicate normalized optional-feature pools.

Identify any spell interpretations still deferred for species mechanics. Record which approved species entities enable later enrichment; completing those spell changes requires a separately authorized milestone following the six-phase workflow.

### Completion criteria

- Character-building contracts are approved.
- Representative character creation structures are validated.
- All source entries are accounted for.
- Semantic coverage and unsupported mechanics are documented.
- Approved datasets are integrated and verified.

---

## Step 9 — World Building

### Objective

Normalize all remaining original world-building content, including advanced equipment, magical variants, rewards, encounters, objects, vehicles, and hazards. Reuse and enrich equipment foundation identities established in Step 3.

### Source files

- `items.json`
- `items-base.json`
- `magicvariants.json`
- `loot.json`
- `rewards.json`
- `encounters.json`
- `recipes.json`
- `deities.json`
- `cultsboons.json`
- `objects.json`
- `vehicles.json`
- `trapshazards.json`

Preserve all these original source assignments. For `items.json` and `items-base.json`, account for the content already normalized in Step 3 and all remaining content. Do not duplicate foundation entities or omit ancillary arrays when splitting the work.

### Internal order

Determine internal domain and batch order during the relevant Phase 1 analysis, using approved dependency evidence and the actual scope of each world domain. Bring the proposed order and boundaries for review before conversion; this roadmap does not prescribe a fixed order among the remaining world sources.

Evaluate dependencies such as advanced items and variants on base equipment, loot results on items, encounters on creatures/vehicles, and rewards on facilities. Preserve later references to Step Z facilities without automatically moving bastions into this step or requiring immediate execution of their mechanics.

### Focus

- Item and equipment properties.
- Weapons, armor, and magical effects.
- Item variants and inherited properties.
- Loot and reward structures.
- Encounter definitions and references.
- Crafting and recipe requirements.
- Deities and supernatural relationships.
- Cults, boons, and granted abilities.
- Interactive objects and their mechanics.
- Vehicles, movement, and combat capabilities.
- Traps, hazards, triggers, and effects.

### Submodel analysis

Evaluate specialized item types, equipment categories, magical properties, interactive objects, vehicle components, and hazard effects.

Prefer reusable mechanics and references where appropriate.

### Completion criteria

- Relevant world-building contracts are approved.
- Representative structures and outliers are validated.
- All source entries are accounted for.
- Foundational and advanced equipment coverage is reconciled across Steps 3 and 9 without duplicate identities or unassigned content.
- Semantic coverage and unsupported mechanics are documented.
- Approved datasets are integrated and verified.

---

## Step Z — Bonus / Deferred Data

### Objective

Normalize bastions, homecrafts, supplementary data, and explicitly deferred source domains within approved scopes, preserving their relationships with the main sequence.

### Source files

- `bastions.json` — facilities and their mechanics.
- `homecrafts.json` — crochet patterns and relevant source relationships.
- Supplementary or explicitly deferred source domains identified and accounted for during earlier analyses.

### Scope

Determine the final scope based on Step 0 findings and discoveries made during earlier roadmap steps.

Avoid duplicating domains already normalized elsewhere.

Account for each unlisted source explicitly; do not automatically exclude it or assign it to Z without review. Preserve facility/reward, item, creature, spell, and rule references. Evaluate supplementary submodels through the same Phase 1 alternatives and six-phase approval workflow.

Do not assume missing publication content or absent catalogs are available for normalization. Record affected interpretations as unsupported where applicable. Any proposal to obtain additional content or change an approved domain's coverage requires separate approval; publication content and stories remain out of scope.

When a later domain makes an earlier deferred mechanic supportable, identify the existing normalized entities and the incremental enrichment needed. Schedule that work through an explicit approval checkpoint, preserving identities and prior approved interpretations.

### Completion criteria

- Auxiliary datasets are accounted for.
- Approved contracts are implemented where appropriate.
- Normalized data is validated and integrated.
- Remaining exclusions and limitations are documented.
- Earlier deferred dependencies affected by this step have reviewed dispositions and explicit enrichment checkpoints where required.

---

# Future source updates

Once the initial normalization is complete, future source updates should use incremental processing rather than full regeneration.

## Update workflow

1. Identify the source revision and compare it with the previously processed version.
2. Detect added, modified, and removed source entries using deterministic comparisons.
3. Identify affected normalized entities and dependent models.
4. Ask Codex to interpret relevant source differences.
5. Generate proposed updates to canonical JSON.
6. Validate proposed changes against approved schemas.
7. Review semantic changes, ambiguities, and compatibility impacts.
8. Approve and commit the updated normalized datasets and provenance.
9. Run applicable integration and regression tests.

Do not silently overwrite approved interpretations.

Do not treat removal from a source file as automatic authorization to delete normalized entities.

Do not change canonical schemas without reviewing compatibility and migration implications.

---

# Roadmap completion

The roadmap is complete when:

- All approved source domains have been analyzed.
- Canonical JSON Schema contracts are established.
- Source entries are accounted for through normalized entities or explicit exclusions.
- Semantic mechanics coverage is documented.
- Complete, deferred, unsupported, and ambiguous interpretations are visible and traceable.
- Deferred interpretations have reviewed dispositions and explicit revisit checkpoints; remaining execution limitations are documented.
- Normalized datasets pass applicable schema and integrity validation.
- Approved data is integrated into the backend.
- Runtime services no longer require unnecessary source-specific interpretation.
- Relevant application and mechanics tests pass.
- System documentation reflects the implemented architecture.
- Future source updates can be processed incrementally without requiring full dataset regeneration.
