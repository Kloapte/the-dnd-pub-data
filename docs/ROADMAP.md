# The DnD Pub — Compendium Backend — Roadmap

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

The roadmap consists of an initial dependency analysis followed by domain-specific normalization steps.

**Step 0 determines whether the proposed roadmap order is viable.**

The current ordering reflects the existing understanding of the source structures and must remain unchanged until Step 0 has been completed and its recommendations reviewed.

Step 0 may recommend reordering, splitting, or combining steps, but must not apply these changes without approval.

Each normalization domain in Steps 1–Z follows the six-phase normalization workflow described below.

Every phase is a separate milestone requiring approval before proceeding.

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

**Approval checkpoint:** Stop and request approval before proceeding to Phase 6.

---

## Phase 6 — Backend integration

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

**Approval checkpoint:** Present the findings and proposed changes. Do not modify the roadmap order without approval.

---

## Step 1 — Interpreting the game

### Objective

Establish the foundational source metadata, game actions, and rules that other domains may reference.

Semantic enrichment is intended from this step onward.

### Source files

- `books.json`
- `actions.json`
- `variantrules.json`

### Focus

**Books**

- Establish source identity and relationships.
- Preserve source metadata and existing identifiers.
- Provide consistent source references for other normalized entities.

**Actions**

- Identify available game actions and their mechanical meaning.
- Interpret action requirements, restrictions, and outcomes.
- Identify reusable action mechanics and references.
- Evaluate whether distinct action categories require submodels.

**Variant rules**

- Analyze optional and alternative game rules.
- Identify mechanics that modify or extend standard behavior.
- Identify dependencies on actions, conditions, abilities, and other game concepts.
- Determine how rule applicability, activation, and exceptions should be represented.
- Evaluate whether variant rules require specialized submodels or reusable mechanics components.
- Preserve distinctions between standard rules and optional variants.

### Special consideration

Variant rules may be semantically complex.

If full semantic enrichment proves unreliable or counterproductive, Codex must identify the affected structures, explain the limitations, and propose a reduced or alternative normalization approach.

Do not automatically exclude variant rules from semantic enrichment.

### Completion criteria

- Foundational source references are established.
- Actions and variant rules have approved canonical contracts.
- Semantic enrichment coverage is documented.
- Unsupported or ambiguous rules are explicitly identified.
- Approved datasets are integrated and verified.

---

## Step 2 — Interpreting the basic building blocks

### Objective

Normalize foundational game concepts that are reused by other domains.

### Source domains

- Skills
- `conditionsdiseases`
- Senses
- Languages

### Focus

**Skills**

- Identify core skills and their relationships with abilities.
- Interpret skill-related mechanics and checks.
- Establish stable references for other models.

**Conditions and diseases**

- Identify conditions, diseases, and their mechanical effects.
- Interpret restrictions, penalties, advantages, disadvantages, and exceptions.
- Evaluate whether conditions and diseases should be independent models.
- Identify reusable condition effects and references.

**Senses**

- Identify vision and perception capabilities.
- Interpret ranges, limitations, and interactions with conditions.
- Evaluate whether different sense types require specialized structures.

**Languages**

- Establish language identity and relationships.
- Identify language categories and relevant metadata.
- Preserve references used by creatures and characters.

### Completion criteria

- Foundational models have approved schemas.
- Relevant mechanics are normalized.
- Shared references are established.
- Semantic coverage and limitations are documented.
- Approved datasets are integrated and verified.

---

## Step 3 — Interpreting spells

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

### Completion criteria

- Spell model and effect contracts are approved.
- Representative spell categories and outliers are validated.
- All source entries are accounted for.
- Semantic coverage and unsupported mechanics are documented.
- Approved datasets are integrated and verified.

---

## Step 4 — Interpreting monsters

### Objective

Normalize creatures and their combat, movement, perception, and special mechanics.

### Source files

- `/monsters/**.json`

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

### Submodel analysis

Evaluate whether creature categories, action types, traits, or spellcasting capabilities justify specialized models or reusable components.

Avoid excessive creature-specific implementations.

### Completion criteria

- Creature and associated mechanic contracts are approved.
- Representative creature structures are validated.
- All source entries are accounted for.
- Semantic coverage and unsupported mechanics are documented.
- Approved datasets are integrated and verified.

---

## Step 5 — Interpreting classes

### Objective

Normalize classes, subclasses, progression, and character capabilities.

### Source files

- `/classes/**.json`

### Focus

- Class and subclass identity.
- Class prerequisites and proficiencies.
- Hit dice and starting capabilities.
- Level progression.
- Class and subclass features.
- Spellcasting progression.
- Resource pools and recovery.
- Feature choices and optional capabilities.
- Scaling mechanics.
- Dependencies between features.
- Conditional and replacement features.
- Interactions with spells and other game rules.

### Submodel analysis

Evaluate class progression, feature types, resource mechanics, and subclass relationships.

Prefer reusable feature and progression components over large class-specific structures.

### Completion criteria

- Class, subclass, and progression contracts are approved.
- Representative progression structures are validated.
- All source entries are accounted for.
- Semantic coverage and unsupported mechanics are documented.
- Approved datasets are integrated and verified.

---

## Step 6 — Character building

### Objective

Normalize character creation and customization data.

### Source files

- `races.json`
- `feats.json`
- `backgrounds.json`
- `optionalfeatures.json`
- `names.json`

### Focus

- Species and racial traits.
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

Evaluate whether traits, feats, backgrounds, and optional features can share reusable mechanics without losing their distinct domain identities.

### Completion criteria

- Character-building contracts are approved.
- Representative character creation structures are validated.
- All source entries are accounted for.
- Semantic coverage and unsupported mechanics are documented.
- Approved datasets are integrated and verified.

---

## Step 7 — World building

### Objective

Normalize equipment, rewards, encounters, objects, vehicles, hazards, and other world-related game entities.

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
- Semantic coverage and unsupported mechanics are documented.
- Approved datasets are integrated and verified.

---

## Step Z — Bonus data

### Objective

Normalize optional, auxiliary, or supplementary data that does not justify inclusion in the main normalization sequence.

### Scope

Determine the final scope based on Step 0 findings and discoveries made during earlier roadmap steps.

Avoid duplicating domains already normalized elsewhere.

### Completion criteria

- Auxiliary datasets are accounted for.
- Approved contracts are implemented where appropriate.
- Normalized data is validated and integrated.
- Remaining exclusions and limitations are documented.

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
- Unsupported and ambiguous constructs are visible and traceable.
- Normalized datasets pass applicable schema and integrity validation.
- Approved data is integrated into the backend.
- Runtime services no longer require unnecessary source-specific interpretation.
- Relevant application and mechanics tests pass.
- System documentation reflects the implemented architecture.
- Future source updates can be processed incrementally without requiring full dataset regeneration.