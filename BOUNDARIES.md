# The DnD Pub — Canonical Data Boundaries

This document is the authoritative policy for offline normalization and canonical compendium data. `AGENTS.md` defines execution conventions and approval gates; `ROADMAP.md` defines authorized milestones. Follow all three. If a requirement conflicts, stop and request a decision rather than silently choosing a different interpretation.

## 1. Purpose and scope

- The normalized backend dataset represents **interpreted, typed game mechanics and useful structured domain facts**, not a mirror of source JSON.
- Interpret mechanics from source `entries` and other source fields, then **exclude the original entry text** from canonical JSON. No generic `entries`, rich-text tree, copied prose, or untyped mechanics blob as a fallback.
- A domain may define narrowly scoped text or metadata only if a use case justifies it and its contract is explicitly approved. The approved metadata-only Source Registry is unaffected by this policy.
- Use **domain-specific** models for actions, conditions, diseases, statuses, senses, rules, languages, etc. Share only cohesive cross-domain primitives such as identity, source attribution, and references. Do not build a universal mechanics engine by default.
- Runtime Java consumes canonical contracts and performs deterministic operations; source-specific interpretation belongs offline.

## 2. Entry-by-entry accountability

Every source `entries` element, including nested content, must be inspected and accounted for; source records without `entries` are still inventoried. Classify each meaningful entry or subentry as:

1. **Interpreted** — its supported facts/mechanics are represented in canonical typed fields.
2. **Duplicate** — its complete content adds no mechanics beyond facts already represented in canonical fields or another explicitly identified interpretation.
3. **Omitted** — it cannot be interpreted reliably, is purely descriptive/nonmechanical, is out of approved scope, or has no suitable approved canonical representation.

- **Never silently discard an entry.** A source entry containing both interpretable and uninterpretable parts must have each part accounted for. Record the *complete original entry* in the relevant external report when any portion is omitted or treated as duplicate, and identify the affected portion(s).
- For omitted content, write `/doc/<model>/omitted.md`; for duplicates, `/doc/<model>/duplicate.md`. Use stable model-specific directory names, and maintain `/doc/INDEX.md` links.
- Each record must identify the source object (canonical ID when available, otherwise type + exact source code + name), original source file/locator, reason, and **complete original entry** (including nested text/structure). Also identify any related canonical field or dependency when useful.
- Keep persistent, actionable omission/duplicate registers in `/doc`; put temporary counts, audits, review findings, and phase status under `/reports` or `/discover`.
- These files are audit documentation, **not a license to republish** copyrighted material. Keep original excerpts in a private/restricted repository where required; do not automatically expose them through APIs, public sites, or generated distributions. If publication rights are unclear, obtain a licensing decision before publishing such records.
- Maintain external coverage/provenance reports so the sum of interpreted, duplicate, and omitted source content can be verified. Unsupported or ambiguous content is not represented by invented canonical placeholders.

## 3. Mechanical interpretation

- Prefer specific typed concepts within each model: action modes and timing, condition effects, disease incubation/progression/recovery, sense capabilities, status thresholds, rule procedures, etc.
- Encode prerequisites, actors, targets, triggering events, scope, units, duration, exceptions, and source-edition distinctions when they affect behavior.
- Use consistent enums/value objects only for genuinely shared semantics; do not collapse distinct mechanics merely because their source text looks similar.
- No speculative effects, default values, edition equivalences, target identities, or execution behavior. When evidence is insufficient, omit that mechanic from canonical JSON and document it.
- A field already expressed structurally in the source (e.g. skill-to-ability) should be retained as typed data. Descriptive text repeating it belongs in `duplicate.md` rather than canonical `entries`.
- Model rules only when a useful typed mechanical contract can be supported. A rule may consist of several cohesive substructures; do not invent a generic catch-all `effects` map.
- For source classification acronyms (such as `ruleType`), investigate authoritative source conventions and expand into descriptive canonical enum values only when verified. Never guess an expansion.
- Analyze all source **Status** records during representative conversion because the set is small; do not mistake distinct status names for source-record count.

## 4. Shared identity and source metadata

- Reuse small, cohesive schema definitions for **identity** (`id`, `name`) and **source metadata** (exact Source Registry code, page when present, supplied SRD/basic-rules flags, and reprint associations). Preserve meaningful `false` and `0` values.
- Canonical IDs use `<type>:<source>:<slug>`; normalize type and source components to lowercase and slugify names deterministically: Unicode normalize/transliterate where defined, lowercase, replace whitespace with `-`, remove or replace unsafe punctuation, collapse repeated hyphens, and trim separators. Never silently collide: validate uniqueness and report collisions for explicit adjudication.
- Keep the original display `name` and exact Source Registry code in metadata. Do not alter registered source codes merely because canonical IDs are lowercase.
- Preserve source/edition-specific identities and verified reprint relationships. Never automatically merge reprints or choose a latest edition.
- Discard source-only presentation fields such as `hasFluff`, `hasFluffImages`, font assets, and LanguageScript font catalogs unless a later approved use case requires them. Removing an entire source model requires explicit scope approval.

## 5. References and dependencies

- Represent meaningful relationships as typed references, not embedded tags or source prose. Prefer a stable target `id`, a target entity `type`, and a distinct relationship/operation when needed (for example, target type `STATUS` with operation `END_STATUS`).
- Distinguish references to game entities from book sections, navigation, filters, and other source constructs. Use a book-section reference only when its source and section identity are supported; do not fabricate a normalized book-content entity from Source Registry metadata.
- Resolve targets from explicit source evidence; never infer identity from name similarity. Preserve unresolved references and original tokens **outside canonical data** when an approved canonical target cannot be established, unless a specific approved reference contract explicitly permits unresolved references.
- References to future domains need not be executable yet. Do not embed complete target models.
- Interpret action timing using typed options where source evidence supports them. `Free`, `Varies`, and missing timing are not permission to guess: for `Varies`, enumerate supported options only when actually evidenced by the entry.
- Normalize source filters (such as language `typicalSpeakers`) into domain-appropriate typed predicates where possible; document uninterpretable parts rather than retaining raw tags.

## 6. Documentation, provenance, and licensing

- `/doc` describes durable architecture, contracts, use cases, implemented behavior, and persistent omitted/duplicate registers. Do **not** put phase status, roadmap progress, open review checkpoints, or temporary validation summaries in `/doc`.
- Place analysis in `/discover`, validation/coverage and outstanding review decisions in `/reports`, and approval state in `ROADMAP.md`.
- Every canonical entity must be traceable to immutable source files, source version/hash where available, raw locators, schema version, and interpretation history through **external** provenance. Never put workflow flags (`needsReview`, `implementationStatus`, etc.) in canonical domain JSON.
- Canonical IDs, mechanical facts, and omission of verbatim descriptions do not alone establish copyright clearance. Determine the applicable license and attribution obligations for each source. Do not reproduce protected expressive text in public outputs without appropriate rights; use legally reviewed source material where possible.

## 7. Validation and approval

- Schema validity is necessary but insufficient. Verify semantic correctness, source-record coverage, per-entry accounting, reference integrity, ID collisions, edition separation, and representative gameplay behavior.
- Schema design and revisions must be approved before representative conversion. Stop at each authorized roadmap phase; do not proceed automatically.
- A design that cannot encode a meaningful mechanic should report the gap and seek a contract decision, not silently preserve the original prose or generate an unsupported runtime interpreter.
- These boundaries apply to future mechanics-bearing domains; do not retroactively rewrite approved datasets without authorization.
