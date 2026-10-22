# Normalization terminology

This page defines the terms used in the DnD Pub source normalization work. The key distinction is that the canonical model describes the intended domain shape, the JSON Schema checks that shape, normalized JSON contains data in that shape, and Java DTOs are the application's typed representation of that data.

## Source material and canonical data

### Source data / raw source JSON

The original JSON supplied by an upstream project, kept unchanged for auditing and future updates. For example, the source registry's `code: "PHB"` is a source fact. The original file is not itself the backend's canonical contract.

### Canonical model

A domain design that states what an entity means and which properties represent it. For example, the proposed `Source` model identifies an origin by a stable code and can carry zero or more source-derived publication references.

### Canonical JSON

One JSON instance that follows an approved canonical model and its schema. A `Source` instance could be `{"code":"EEPC"}` when only the code is known.

### Normalized dataset

A reviewable collection of canonical JSON instances for a domain. It is separate from the original source files. A complete Source dataset would contain one canonical Source entry per source code, including entries with only a known code.

### JSON Schema / schema contract

A machine-readable set of structural rules for canonical JSON: property names, data types, required fields, allowed values, and constraints. It does not establish that a rule interpretation is true or that a cross-file reference points to the intended entity. The Source schema allows `code` and optional publication metadata; it cannot by itself decide whether a `parentSource` token identifies a particular publication.

### DTO

A typed Java object used by the application to read or work with data. A DTO should match the approved canonical contract, while source-specific interpretation happens offline. The DTO is not the schema and does not replace the normalized JSON dataset.

### Semantic enrichment

Interpreting source descriptions as domain meaning or mechanics, while preserving evidence and uncertainty. For example, turning a spell description into an executable saving-throw effect would be semantic enrichment. Recording the literal source code `PHB` is structural identity work and does not require a mechanics interpretation.

## Identity and references

### Source identity

The identity of the origin in the supplied source data. In the proposed Source model, the exact source code (for example `PHB` or `PSZ`) is the primary identity. Keep its case and spelling stable.

### Canonical identity

The stable identifier assigned to a normalized domain entity. For Source, the proposal uses the supplied source code directly, avoiding a second identifier that repeats it. Other domains may follow the project convention `<type>:<source>:<name>` when a source code alone does not identify an entity.

### Source reference

A value that points to a Source identity from another normalized entity. A monster whose source is `MM` can reference Source code `MM`; it should not copy a full Source object into the monster. A raw `parentSource` value is preserved as supplied and is only a resolved canonical relationship after evidence establishes its target.

### Provenance

Traceability from a normalized entity back to the exact source file, entry, source revision, and content hash that supported it. Provenance belongs in a manifest or equivalent metadata separate from application DTOs. For a Source derived from the registry and a book record, provenance records both original locations rather than embedding workflow metadata in the Source object.

## Model structure

### Domain model and submodel

A domain model defines a meaningful kind of entity and its data. A submodel is a specialized shape used when a real difference in meaning or behavior requires it. The Source proposal has one primary Source model; small publication-reference objects are composed inside it so book and adventure records do not become separate domain models.

### Composition

Building a model by containing other related values. A Source composes an optional list of publication references, each with its own kind, publication ID, name, date, and title aliases. This preserves multiple records that share one source code, such as the book and adventure records associated with `MOT`.

### Inheritance

Defining specialized models from a common parent model. It is useful only when the domain has meaningful subtype behavior. The current Source evidence does not justify separate Book and Adventure subclasses; a `kind` value on each contained publication reference preserves the observed classification with less complexity.

## Validation and incomplete interpretations

### Structural validation

Checking that JSON has the shape required by its schema: for example, `code` is a non-empty string and `published` uses a date value. Passing structural validation does not prove that a publication date is correct or that a parent reference is right.

### Semantic validation

Checking that an interpretation matches the source and relevant game rules. For Source, this includes confirming whether an alias is a source-code alias or a human title, and whether a parent token identifies a source context or a particular publication record.

### Deferred dependency

A known reference or mechanic intentionally left for a later approved step because its target or rules are not yet available. Keep the reference stable and record what later work can resolve it. For example, a spell may retain a creature reference for the later creature step.

### Unsupported mechanics

Behavior that cannot be represented or completed because needed source content is unavailable or the approved contract cannot express it. Preserve the evidence and limitation; do not replace it with a guess. Missing publication prose is unsupported when an in-scope rule depends on that prose.

### Ambiguous mechanics or references

Source evidence supports more than one reasonable interpretation or does not establish a unique target. Preserve the source value and document the decision needed. For example, the `parentSource` token `SAiS` appears on three records but is not a registry code or a supplied parent metadata record.

### Implementation status versus domain data

Implementation, conversion, review, and completeness statuses describe project workflow, not a D&D source entity. Keep them in reports, manifests, or documentation. Do not add fields such as `reviewStatus` or `isComplete` to canonical JSON.

## How the artifacts fit together

| Artifact | What it answers | Source example |
| --- | --- | --- |
| Canonical model | What does a Source mean, and what information belongs to it? | A source-code identity with optional source-derived publication metadata. |
| JSON Schema | Does this JSON have the approved Source structure? | `schemas/source/v1/source.schema.json` |
| Normalized JSON | What Source identities and metadata were derived from this source revision? | One JSON object per source code. |
| Java DTO | How does the backend represent an approved Source instance in typed Java? | To be designed only after the contract is approved. |
| Provenance manifest | Which original source entries and revision support each normalized identity? | Registry entry and optional book/adventure metadata locations with hashes. |

The current Source schema and examples are a Phase 2 proposal. Their presence does not authorize full dataset conversion or Java integration; those are later milestones with separate approval checkpoints.
