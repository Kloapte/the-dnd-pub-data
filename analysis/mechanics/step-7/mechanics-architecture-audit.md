# Mechanics Architecture and Maintainability Audit

**Scope:** repository state before Step 7.4.4D. This is an analysis-only review. It makes no
production, test, source-data, inventory, or roadmap changes. The authoritative roadmap remains at
7.4.4C; its 106 processed Spell owners and 228 mechanics are context, not acceptance criteria for
this audit.

## Executive assessment

The mechanics implementation has useful boundaries around canonical claims, source evidence,
review/licensing, private inspection, and candidate-only benchmark evaluation. Keep those boundaries.
There is no runtime dependency on `analysis/` reports and no benchmark or inventory Spring bean.
However, the production source tree contains a significant amount of offline-only inventory,
comparison, serialization, and benchmark code. The inventory API has also leaked into the
production extraction interface, and two independently built pattern lists can drift.

The main maintainability issue inside production extraction is
`PhbSpellMechanicsExtractor` (1,632 lines): it owns owner eligibility, several unrelated mechanic
families, evidence parsing, helper records, and condition/removal logic. `SpellMechanicsExtractionService`
also contains pilot-specific type checks and owner-count assertions. These are safe targets for
small, carefully tested follow-up batches. The immediate test-isolation issue is more concrete:
`SpellCapabilityInventoryTests` reads a historical report and rewrites three tracked analysis
artifacts during a normal test run.

## Current architecture and execution

The application constructs a `SpellMechanicsInspectionCatalog` Spring component from the normalized
Spell catalog, formula-observation index, source registry, and a raw-source owner manifest. The
catalog uses `SpellMechanicsDocumentFactory` to make a source-aware unattempted document for every
Spell, `SpellMechanicsExtractionService` to process explicitly supported owners, canonical
`MechanicsCatalogValidation` to check joins and revisions, and `SpellMechanicsInspectionMapper` to
project the resulting documents to typed private-inspection DTOs. `SpellsController` and
`SpellService` expose this projection only on the existing opt-in interpreted-mechanics detail path.
The P1D publication catalog is not consulted by extraction or private inspection.

`SpellMechanicsPatternRegistry` selects between the Shield edition adapter and the bounded PHB
adapter, rejecting overlapping matches. Both adapters use `SpellMechanicsEvidenceSource` to verify
normalized values against fingerprint-checked pinned bytes and to create exact structured-field or
text-span evidence. `SpellMechanicsAssembly` turns successful bounded interpretations and explicit
abstentions into canonical documents. The inventory runner uses the same adapters in assess-only
mode, but does not mutate the catalog or production documents.

### Four representative paths

| Path | Major classes and why each exists |
| --- | --- |
| Simple damage (for example, an owner handled by the generalized damage path) | `SpellMechanicsInspectionCatalog` joins all owners and validates the finished catalog; `SpellMechanicsDocumentFactory` creates the baseline document and formula references; `SpellMechanicsExtractionService` loads verified original bytes and dispatches; `SpellMechanicsPatternRegistry` enforces unique pattern ownership; `PhbSpellMechanicsExtractor` selects the bounded PHB path; `PhbSaveDamageGeneralizer` parses a local save/damage clause; `SpellMechanicsEvidenceSource` verifies the source and constructs spans/field evidence; `SpellMechanicsAssembly` constructs claim IDs, derivation, context, assessment, coverage and abstentions; canonical model validation and inspection mapping preserve and project that result. These are mostly real boundaries, although the two extractors currently share too many mechanics responsibilities. |
| Condition application with save | Same catalog, document factory, dispatcher, source verifier, assembler and projection. `PhbSpellMechanicsExtractor` locates condition tags, save clauses, local target/ending text, constructs a typed `ConditionChangeMechanic` plus `SavingThrowRequirementMechanic`, and joins them by revision references/context bindings and evidence associations. The common canonical document/validation layer ensures the relationship, evidence, review and rights states remain inspectable. This path should not be flattened into one DTO or text-only claim. |
| Lesser Restoration exclusive removal | Same production path. PHB condition-removal handling in `PhbSpellMechanicsExtractor` recognizes the bounded source phrase and choice list, creates individual `REMOVE` condition claims, then a `ConditionChoiceMechanic` referencing those revisions. The separate choice claim preserves the one-of-four semantics and the unmodeled disease alternative; it is not duplicate representation. Evidence pointers remain attached to the chosen conditions and selection. This representation and its stable IDs need to remain intact. |
| Spell with no supported mechanics | Factory still creates the owner document with `NOT_ATTEMPTED`, source revision, authored formula observations, and `NOT_ASSESSED` coverage. Dispatcher leaves unsupported owners unchanged. Catalog validation and `SpellMechanicsInspectionMapper` expose that state and its observations without inventing claims. This explicit empty/partial state is necessary and should remain. |

## Package and class responsibility audit

### `mechanics.model` — canonical domain and provenance (152 types)

Production runtime and model/inspection tests. These are passive immutable payloads, values,
references, validation and serialization support; they are not analysis reports. No class here should
be deleted because a current extractor does not yet emit its type.

| Cohesive responsibility | Classes and actual role |
| --- | --- |
| Claim identity, payload, revisions, document, state and validation | `MechanicId`, `MechanicFamily`, `MechanicPayload`, `MechanicRecord`, `MechanicRevision`, `MechanicRevisionContent`, `MechanicRevisionReference`, `MechanicsDocument`, `MechanicsDocumentContent`, `MechanicsDocumentRevision`, `MechanicsProcessing`, `ProcessingState`, `MechanicsCoverage`, `CoverageStatus`, `MechanicsCompleteness`, `MechanicsSupport`, `MechanicsAssessment`, `MechanicsQuality`, `MechanicsCatalogValidation`, `MechanicsSourceValidation`, `MechanicsDocumentValidator`, `MechanicsBindings`, `MechanicsClaimPaths`, `MechanicsHashes`, `MechanicsJson`, `ModelChecks`. Used by extractors, catalog validation, inspection projection, review and tests. |
| Owner, catalog and source identity | `CatalogDomain`, `MechanicsOwnerReference`, `SourceArtifactRevision`, `MechanicsOrigin`, `MechanicsReferenceDisposition`, `MechanicsTransformationReference`. Used in every extracted document and cross-reference. |
| Evidence and exact lineage | `MechanicsEvidence`, `TextMechanicsEvidence`, `StructuredFieldMechanicsEvidence`, `TransformedMechanicsEvidence`, `UnavailableOriginMechanicsEvidence`, `MechanicEvidenceAssociation`, `EvidenceRole`, `EvidenceRevisionReference`, `MechanicDerivation`, `DerivationMethod`, `MechanicsObservationReference`, `FormulaObservationReference`, `StructuredDeclarationReference`, `OtherSyntaxObservationReference`, `ObservationRevisionReference`, `MechanicsObservationReference`, `MechanicContext`, `MechanicContextBinding`. Used by extractors, validator, inspection and benchmark evidence comparison. Evidence subtype distinctions encode provenance and should remain. |
| Review quality and diagnostics | `MechanicsFinding`, `MechanicsFindingCode`, `FindingSeverity`, `UnsupportedMechanicsConstruct`, `UnsupportedReason`, `UnknownValueReason`, `MechanicsCompleteness`, `MechanicsSupport`, `MechanicsCoverage`, `CoverageStatus`. Used in extraction abstentions, catalog validation, review summary and inspection. |
| Independent rights/licensing vocabulary and references | `MechanicsLicensing`, `MechanicsLicensingStatus`, `SrdMechanicsLicensing`, `OtherMechanicsLicensing`, `OpenMechanicsLicensing`, `UnassessedMechanicsLicensing`, `LicenseCoverage`, `ClaimRightsScope`, `MechanicsLicensingScopes`, `LicenseReference`, `LicensedDocumentReference`, `AttributionReference`, `LicensingReviewReason`, `RightsDecisionReference`, `RightsEvidenceReference`, `EstablishedReuseBasis`. Used in mechanic records and inspection; keep independent from mechanics review and publication authorization. |
| Typed effect payloads | `DamageMechanic`, `HealingMechanic`, `ArmorClassModifierMechanic`, `StatisticModifierMechanic`, `DamageResistanceMechanic`, `SpellDamagePreventionMechanic`, `ConditionChangeMechanic`, `ConditionChoiceMechanic`, `SavingThrowRequirementMechanic`, `ReactionActivationMechanic`, `EffectDurationMechanic`, `SpellSlotScalingMechanic`. Production extractors emit these; benchmark semantics inspect them. |
| Amounts, damage, modifiers, abilities and scaling | `AmountExpression`, `IntegerAmount`, `DiceAmount`, `SymbolicAmount`, `SumAmount`, `AmountSymbol`, `DamageOutcome`, `DamageApplicability`, `FixedDamageType`, `UnknownDamageType`, `DamageTypeSelection`, `DamageTypeChoice`, `DamageProtectionOperation`, `ModifierAmount`, `IntegerModifier`, `SymbolicModifier`, `ModifiedStatistic`, `ModifierOperation`, `ModifierOnSaveOutcome`, `ScalingEffect`, `Ability`, `FixedAbility`, `UnknownAbility`, `AbilityChoice`, `AbilitySelection`. These are explicit value types used in effect payloads and extraction/benchmark comparison. |
| Subjects, targets, conditions, timing and triggers | `SubjectReference`, `CasterSubject`, `SpellTargetSubject`, `UnknownSubject`, `CreatureTypeRestriction`, `TargetScope`, `ConditionName`, `ConditionOperation`, `ConditionOnSaveOutcome`, `ConditionEndsWithSpell`, `ConditionEndsOnSaveSuccess`, `SaveOutcome`, `AppliesOnSaveOutcome`, `AppliesWhen`, `ApplicabilityCondition`, `SaveDifficulty`, `FixedSaveDifficulty`, `UnknownSaveDifficulty`, `DurationSpecification`, `DurationUnit`, `FixedDuration`, `UnknownDuration`, `ReferencedDuration`, `DeclaredSpellDurationReference`, `RequiresConcentration`, `RepeatSaveTiming`, `TurnActor`, `TurnBoundary`, `UntilTurnBoundary`, `TriggerExpression`, `AnyTrigger`, `AllTriggers`, `ActivatedBy`, `HitByAttackTrigger`, `TargetedBySpellTrigger`, `IncludesTriggeringAttack`, `AttackEventKind`, `ReactionActionCost`, `SpellMentionReference`, `SpellMentionForm`. These typed values preserve explicit relationships and uncertainty. |
| Benchmark pointer | `MechanicsBenchmarkReference` is an optional reference carried by `MechanicsAssessment`; it is not a dependency on fixtures or an approval mechanism. Keep this narrow reference if benchmark traceability is part of the canonical contract. |

The model package is large, but its breadth reflects the shared canonical contract and is not by
itself evidence of unnecessary abstractions. A future organization pass could group evidence into a
single package, but that would create broad import/API churn with little responsibility improvement;
do not prioritize it.

### `mechanics.syntax` — authored markup and text evidence

Used by runtime extraction/evidence, source syntax catalogs and syntax tests. `BalancedMarkupParser`,
`MarkupTag`, `MarkupParseResult`, and `MarkupSyntaxException` parse balanced inline markup;
`AuthoredSyntaxTextWalker` walks typed source entries; `SyntaxObservationClassifier` distinguishes
formula, modifier, label and opaque observations represented by `SyntaxObservation`,
`FormulaSyntaxObservation`, `ModifierSyntaxObservation`, `LabelSyntaxObservation`, and
`OpaqueSyntaxObservation`. `SyntaxExpression`/`SyntaxExpressionKind` model recognized formula forms;
`OpaqueTagCategory` retains unknown tags. `SyntaxEvidence`, `SyntaxSpan`, `SyntaxText`,
`SyntaxTextLocation`, `SyntaxSourceOwner`, `SyntaxContext`, `SyntaxOrigin`, and `SyntaxPointers`
carry exact source locations and hierarchy. These classes are a meaningful evidence boundary;
retain them. `SyntaxCorpusTests` is a full-corpus offline/evidence test and reads
`analysis/mechanics/corpus-evidence.json`.

### `mechanics.review` — review state and validation

Production projection/model behavior and focused tests. `HumanMechanicsReviewDecision` records a
decision; `MechanicsReviewAction`, `MechanicsReviewStatus`, `MechanicsReviewerAuthority`, and
`MechanicsReviewPolicy` represent action/state/trust; `MechanicsReviewState` computes recorded and
effective state; `MechanicsReviewSummary` aggregates documents. `ValidatedHumanMechanicsReview` and
`ValidatedHumanMechanicsReviewDeserializer` validate trusted serialized input. This boundary prevents
extractor output or a benchmark match from silently becoming human verification. Keep unchanged.

### `mechanics.inspection` — private REST projection

Production runtime and projection/API tests. `SpellMechanicsInspectionDto` is the envelope;
`MechanicInspectionDto`, `MechanicsDerivationDto`, `MechanicsEvidenceAssociationDto`,
`MechanicsSourceRevisionDto`, `MechanicsObservationDto`, `FormulaObservationInspectionDto`,
`StructuredObservationInspectionDto`, `SyntaxObservationInspectionDto`,
`EvidenceLocatorDto`, `TextEvidenceLocatorDto`, `StructuredEvidenceLocatorDto`,
`TransformedEvidenceLocatorDto`, `UnavailableEvidenceLocatorDto`, `MechanicsReviewDto`,
`MechanicsLicensingDto`, and `MechanicsLicenseCoverageDto` are typed detail projections.
`MechanicsInspectionAvailability`, `MechanicsInspectionDiagnostic`, and
`MechanicsInspectionProjection` make state/boundary explicit. `SpellMechanicsInspectionMapper` maps
canonical values to these DTOs without parsing source or asking the publication gate. These DTO
types duplicate serialization shape intentionally to keep REST separate from internal models;
do not merge them. The single-value projection enum is small but encodes that this response is
private inspection, not an authorization credential; retain it unless the API contract is redesigned.

### `mechanics.spell` — mixed production extraction and offline tooling

| Class | Actual responsibility, context, and assessment |
| --- | --- |
| `SpellMechanicsSourceOwner` | Typed source manifest row: identity, source, artifact revision/hash and authored JSON pointer. Runtime and tests; belongs with source/extraction. |
| `SpellMechanicsDocumentFactory` | Builds NOT_ATTEMPTED documents with formula references and loads/validates all 969 owner roots from raw pinned resources. Runtime startup and tests. Name is broad: it is both a manifest loader and baseline-document factory, but those steps jointly establish the immutable source-aware starting document. Keep for now; only split if manifest loading becomes independently reused. |
| `SpellMechanicsEvidenceSource` | Per-owner verifier over fingerprint-checked raw JSON bytes; produces exact evidence and validates normalized fields. Runtime extraction, benchmark fixture tests. Its JSON tree is internal source evidence, not an application-facing model. Keep. Legacy Shield evidence aliases are compatibility logic and require ID regression protection. |
| `SpellMechanicsPattern` | Package-private extraction adapter contract. Runtime and inventory. `supportsForInventory`/`extractForInventory` are offline concerns leaking into production; remove from the production seam as part of moving inventory, or isolate candidate assessment behind a development adapter. |
| `SpellMechanicsPatternRegistry` | Ordered pattern selection and overlap guard. Runtime and inventory. Useful guard, but registry composition is duplicated in the service and inventory runner. Consolidate composition in one production factory or provide the same immutable registry to both; do not add a general plugin framework. |
| `SpellMechanicsExtractionService` | Runtime startup dispatch, source hash/resource load, owner-set integrity, Shield revision validation, sorting. It also embeds exact pilot implementation checks: `instanceof` on extractors, expected Shield count, PHB count `>= 29`. Those assumptions make each added pattern require dispatcher edits and keep implementation-era acceptance values in runtime. Move revision validation to pattern/edition metadata and replace count/type checks with per-pattern contracts or remove only after equivalent checks are covered. The class is a real orchestrator, not a pass-through. |
| `SpellMechanicsAssembly` | Package-private extraction construction state; catches bounded `SpellPatternMismatch`, builds unsupported records and canonical claims/coverage. Centralizing ID/derivation/evidence joins is appropriate. The default `shield-pilot` adapter name and prefix special case are historical compatibility behavior, not generic assembly; preserve output IDs when simplifying. |
| `SpellPatternMismatch` | Internal bounded-abstention signal containing reason/pointer. Used by extractors, assembler, and inventory diagnostics. Keep; do not replace expected unsupported syntax with broad exceptions. |
| `ShieldMechanicsEdition` | PHB/XPHB-specific Shield identity, expected wording and pinned revision validation. Runtime and tests. Meaningful edition boundary; keep, preserve both IDs/output. |
| `ShieldMechanicsExtractor` | Five narrowly bounded Shield claims and edition-specific joins; runtime and tests. Relatively cohesive despite the specialized parsing. It is an adapter and a separately tested compatibility-sensitive pilot; keep separate from PHB rules. |
| `PhbSpellMechanicsExtractor` | Runtime PHB matcher and a 1,632-line catch-all for damage/healing, condition application/removal/choices/save dependencies/endings, modifiers/protection, and pilot-specific patterns. It owns unrelated family responsibilities and historical owner lists. Strongest consolidation/splitting candidate. Split by cohesive mechanic family while retaining a small adapter/dispatcher; keep shared evidence and `SpellMechanicsAssembly` joins. Do not change condition choice semantics or existing claim slots as part of a move. |
| `PhbSaveDamageGeneralizer` | Package-private generalized save/damage parser (save, target, damage components, outcomes, amount, optional scaling). Called by the PHB extractor; runtime and extractor tests. Cohesive and already separated; keep. |
| `SpellCapabilityInventoryRunner` | Offline corpus assessment of every Spell using wider candidate matching, diagnostics, counts and classification. Test-only consumer in practice; currently `src/main` and shares the production registry. Move to development tooling with its report DTOs. Its catch-per-owner behavior is deliberate for diagnostics and unlike startup extraction failure policy. |
| `SpellCapabilityRecord`, `SpellCapabilityInventory`, `SpellCapabilityClassification` | Offline inventory row, aggregate and classification enum; used by runner/reports/tests and not runtime. Move with runner. |
| `SpellCapabilityInventoryReports` | Offline JSON/Markdown writer; writes report content to caller paths. Not runtime. Move out of `src/main`; it labels output 7.4.4A. |
| `SpellCapabilityInventoryComparison` | Offline before/after aggregate, including hardcoded 7.4.4A report filenames. No production caller. Move out of `src/main`; roadmap-specific data does not belong in the mechanics runtime package. |
| `SpellCapabilityInventoryComparisonReports` | Offline 7.4.4B Markdown comparison renderer. Move with report tools. |
| `SpellConditionInventoryComparisonReports` | Offline 7.4.4C renderer with batch-specific prose/metrics. Move with report tools. |

The inventory classes are not merely named “analysis”: they call the widened pattern API and
construct candidate canonical documents, but their only callers are tests. This is legitimate
development tooling, not a runtime behavior, and should be isolated from production source and the
production extraction interface.

### `mechanics.benchmark` — candidate and approved benchmark domain

All 32 classes are used by tests/fixture validation only; no Spring component, controller, production
service, or runtime catalog calls them. Fixture JSON is under `src/main/resources/mechanics/benchmarks`
and is loaded in test code. The package location is conceptually appropriate, but production source
packaging places it in the backend artifact.

`SpellMechanicsBenchmark` compares an actual canonical document with expectations; `BenchmarkSemantics`
implements family-specific semantic/evidence matching; `BenchmarkFixtures` loads and validates
fixture identities, source revisions, evidence and review state. `SpellBenchmarkFixture` and
`FixtureContent` are the fixture and immutable content; `FixtureRevision` fingerprints content;
`FixtureReview`, `FixtureLifecycle`, `BenchmarkHumanDecision`, `BenchmarkReviewerAuthority`, and
`BenchmarkReviewPolicy` keep human authority separate and pending fixtures from acceptance.
`ExtractionScope`/`ExtractionPattern` scope a run; `ExpectedMechanic`, `ExpectedFormula`,
`ExpectedActivation`, `ExpectedBinding`, `ExpectedContext`, `ExpectedDuration`, `ExpectedEvidence`,
`ExpectedSaveOutcome`, and `ExpectedTriggeringAttack` are typed expectations. `DeferredExpectation`
and `BenchmarkProhibition`/`ProhibitionRule` encode abstentions and disallowed assertions.
`BenchmarkMode` and `BenchmarkRunState` distinguish candidate diagnostics from approved evaluation;
`BenchmarkMetric`/`MetricAvailability`, `BenchmarkReport`, `BenchmarkPatternResult`, and
`BenchmarkClaimResult` carry results. `BenchmarkSemantics` is sizable but belongs here because it
implements comparison, not production interpretation.

This is not an unnecessary duplicate of canonical mechanics: expected claims, prohibitions, deferrals,
and reviewer decisions have distinct authority and lifecycle. Keep the model intact and keep candidate
results from being treated as verification. Relocate the tooling after preserving fixture paths and
benchmark reference compatibility.

### Related application/service classes

`SpellMechanicsInspectionCatalog` is the production immutable owner join/index and startup integrity
gate. Its public constructor also provides a test/adapter seam for supplied canonical documents.
`SpellMechanicsInspectionMapper` is the only mechanics-to-inspection projection. `SpellsController`
and `SpellService` handle the opt-in request and response composition; `SpellCatalog`,
`SpellFormulaAnnotationCatalog`, and `SourceRegistry` provide owner, observation and source joins.
These are appropriate layered responsibilities. The controller remains thin. The factory and
extraction service are constructed by the catalog, so they are not redundant Spring services.

## Findings by severity

### High — a normal test rewrites tracked evidence

`SpellCapabilityInventoryTests.reportSerializationAndMarkdownAreDeterministic` writes to
`analysis/mechanics/step-7/7.4.4A-spell-capability-inventory.json`, the matching `.md`, and
`7.4.4C-condition-extraction-comparison.md` after its temporary-directory assertions. `git ls-files`
confirms all three are tracked. The test also reads the preserved 7.4.4B JSON baseline as input.
Therefore a regular test can silently replace current evidence with output from current production
patterns, and a test result depends on a historical analysis artifact. Keep deterministic writer
checks in `@TempDir`; regenerate the authoritative reports only through an explicit offline command
or a separately reviewed evidence update. Do not alter the baseline implicitly.

### High — offline capability code is shipped in production source and extends runtime interfaces

`SpellCapabilityInventoryRunner`, `SpellCapabilityInventory*` models/reporters and
`SpellConditionInventoryComparisonReports` live in `src/main/java/com/thedndpub/data/mechanics/spell`.
The runner reuses `SpellMechanicsPatternRegistry`, and `SpellMechanicsPattern` exposes
`supportsForInventory`/`extractForInventory`; `PhbSpellMechanicsExtractor` has separate broader
eligibility and extraction methods solely for the offline inventory. The runner is not wired at
startup, but this is a real source-level coupling and package-placement problem. Move the inventory
and report layer to an offline development source area and remove that offline branch from the
runtime interface, keeping production eligibility bounded. Preserve current generated evidence and
candidate semantics during that later change.

### Medium — one extractor combines distinct family rules

`PhbSpellMechanicsExtractor` is 1,632 lines. Method groups at roughly lines 263–480 cover condition
removal/application and choices; 481–737 cover modifiers/protection; 748–1,228 cover condition
dependencies, targets, endings and traversal; 1,231–1,600 cover generalized damage/healing/scaling
plus original Fireball/Healing Word rules. The class also maintains several unrelated owner sets and
both production and inventory eligibility. It is doing too much. Split into package-private family
rule units behind one thin PHB adapter (or dispatch to focused damage/healing, conditions and
modifier/protection rules). Keep shared assembly and evidence source. This is the largest safe
maintainability improvement, but requires full identity/evidence/REST regression checks.

### Medium — runtime extraction has pilot-era hardcoded assumptions

`SpellMechanicsExtractionService` identifies Shield and PHB implementations using concrete
`instanceof`, validates Shield specially, and asserts there are exactly two Shield owners and at least
29 PHB owners. These checks guard current pilot assumptions but make extension and refactoring brittle
and put historical batch shape in runtime. Relocate source-revision validation to the relevant
edition/pattern metadata, and express any required-owner completeness as an explicit pattern contract.
Do not simply delete the guards before equivalent source and owner integrity checks exist.

### Medium — benchmark and inventory/report code sit in `src/main`

`mechanics.benchmark` (32 classes), all inventory/comparison classes in `mechanics.spell`, and
`com.thedndpub.data.analysis.SchemaDiscovery` are offline/development code. `SchemaDiscovery` is a
standalone `main` that scans source JSON and writes reports; no production code calls it. Benchmark
engine and inventory classes are referenced from tests, not beans/controllers/services. Their
responsibilities are legitimate, but they increase production source/artifact surface. Isolate offline
tools in a `tools/` tree or development/test source area appropriate to how they are invoked; do not
create a Maven module solely for this cleanup.

### Low — duplicated pattern-list construction and historical labels

Both `SpellMechanicsExtractionService` and `SpellCapabilityInventoryRunner` construct
`List.of(new ShieldMechanicsExtractor(), new PhbSpellMechanicsExtractor())`; then each builds a
registry. This can make inventory and runtime assess different adapters as patterns are added.
When inventory moves, share the production registry/composition once. Keep overlap detection in the
registry if a registry remains useful; with only two patterns it is a small, justified guard rather
than an abstraction that needs a plugin system.

Several offline classes embed roadmap-step labels and report names: `SpellCapabilityInventoryReports`
labels 7.4.4A; `SpellCapabilityInventoryComparisonReports` labels 7.4.4B;
`SpellConditionInventoryComparisonReports` labels 7.4.4C; and
`SpellCapabilityInventoryComparison` fixes A report filenames. Historical metrics also occur in
`SpellCapabilityInventoryTests` assertions. These are acceptable in versioned evidence tooling, but
they should not live in runtime packages or become production acceptance values. No current
mechanics production class reads a generated inventory/report as a source of truth.

## Offline tooling, dependencies, and test isolation

| Item | Finding |
| --- | --- |
| `com.thedndpub.data.analysis.SchemaDiscovery` | Standalone schema discovery/report writer under `src/main/java`; offline, no runtime caller. Isolate with development tools. |
| `mechanics.spell.SpellCapabilityInventoryRunner`, inventory/report/comparison classes | Corpus-wide assess-only tool and renderers under `src/main/java`; invoked by `SpellCapabilityInventoryTests`. Only code with an offline branch added to production extraction. |
| `mechanics.benchmark.*` | Candidate/approved comparison model and engine under `src/main/java`; tests only. No production caller. Fixture resources are not generated reports and are not loaded by startup. |
| Runtime analysis-artifact reads | None found in `src/main/java`. Mechanics startup reads classpath source JSON and catalog inputs, not `analysis/`. |
| Generated reports as production truth | None found. `MechanicsBenchmarkReference` is a typed optional provenance reference, not a fixture/report loader. |
| Analysis-reading tests | `SyntaxCorpusTests` reads `analysis/mechanics/corpus-evidence.json`; `SpellCapabilityInventoryTests` reads the 7.4.4B baseline; `SpellFormulaProjectionStatusTests` reads P1A assessment files; `SpellPublicationCatalogTests` reads P1C review data; `ObjectEvidenceMembershipTests` reads gold verification and gold-set artifacts; `MonsterMapperTests` reads normalized field inventory; `SpellFieldCoverageTests` reads normalized Spell field inventory; `ItemChargeSyntaxCatalogTests` reads item review cases. These are evidence/fixture tests and normal Maven runs require checked-out analysis artifacts. The mechanics-relevant ones are syntax corpus, inventory and Spell formula/publication tests. |
| Test report writes | `SpellCapabilityInventoryTests` writes the three tracked analysis artifacts listed above (high severity). `SyntaxCorpusTests` writes a generated corpus report under `target`; `ShieldMechanicsCandidateBenchmarkTests` and `SpellExpansionCandidateBenchmarkTests` write candidate JSON under `target`; these are build outputs. `BenchmarkFixtureAuthoring` writes to a caller-provided path and its test uses a temp path. No other tracked-file write was found in mechanics test sources. |

Analysis test dependencies are not automatically wrong: they validate report fingerprints, reviewer
inputs, inventories or preserved gold evidence. They do make those tests checkout-sensitive, so test
fixtures should not be casually relocated or regenerated. The tracked-output side effect is a
separate defect from read-only evidence dependency.

## Abstraction review and duplication

- `SpellMechanicsDocumentFactory` and `SpellMechanicsAssembly` have different jobs: one creates a
  source-complete baseline across all owners; the other builds each extracted canonical result.
  Neither is a pass-through.
- `SpellMechanicsExtractionService` is a real catalog-level orchestrator; it is not redundant with
  individual extractors. It should lose pilot-specific type/count checks over time.
- `SpellMechanicsPatternRegistry` performs actual disjoint selection and detects overlaps. Keep that
  invariant, but centralize pattern construction. Avoid replacing it with Spring plugin discovery or
  extra strategy interfaces.
- `PhbSpellMechanicsExtractor` plus `PhbSaveDamageGeneralizer` have a partially duplicated dispatch
  boundary. The latter is cohesive, while the former has both legacy hand-written owner patterns and
  generalized extraction families. A family split should make dispatch explicit and avoid parsing
  the same text twice; do not merge the generalizer into the already large extractor.
- `SpellMechanicsEvidenceSource` and `MechanicsEvidence` are not duplicate representations: one
  verifies/extracts against original bytes; the other stores immutable provenance in the canonical
  document. `SpellMechanicsInspectionMapper` and inspection DTOs are not duplicate domain models;
  they form the separate REST projection required for safe private inspection.
- `ConditionChangeMechanic` plus `ConditionChoiceMechanic` are intentionally separate for exclusive
  choices. Claim revision references, assessment/completeness, evidence associations, review state,
  and licensing are separate because they represent different facts and independent trust decisions.
- Benchmark expected values and output reports are separate from mechanics documents by design.
  Candidate comparison may report a match but cannot set human review or publication rights.
- `SpellMechanicsSourceOwner` and `MechanicsOwnerReference` overlap in owner identity only; the
  former adds source-file root/revision for raw evidence, while the latter is canonical domain owner
  identity. Keep both boundaries.

## Classes that should remain unchanged in the first refactoring batches

Keep the canonical `mechanics.model` contract; `mechanics.syntax` evidence/parser values;
`mechanics.review` trust and effective-review rules; `mechanics.inspection` DTO boundary and mapper;
`ShieldMechanicsEdition` and `ShieldMechanicsExtractor`; `SpellMechanicsEvidenceSource`;
`SpellMechanicsAssembly` claim/evidence joining; `PhbSaveDamageGeneralizer`; benchmark fixture
authority/approval semantics; publication `SpellPublicationCatalog` and P1D gate; and
`SpellMechanicsInspectionCatalog` owner/revision/formula completeness validation. These types
protect stable IDs, source identity, evidence, review/licensing independence, or existing REST
compatibility. Moving code can happen without changing their contracts.

## Recommended package/source structure

Keep the current simple domain packages and make only these separations:

```text
src/main/java/com/thedndpub/data/mechanics/
  model/          canonical mechanics and evidence/provenance values
  syntax/         markup parsing and syntax evidence
  review/         human review state and validation
  inspection/     private REST projection DTOs and mapper
  spell/          runtime source manifest, evidence verifier, dispatcher, assembly, extractors

tools/ or src/test/java/.../development/
  mechanics/inventory/       corpus assessment records and runner
  mechanics/benchmark/       benchmark fixtures, comparator and result model
  mechanics/reports/         versioned comparison and report renderers
  schema/                    standalone SchemaDiscovery
```

Use one tooling location based on invocation/build conventions; do not add Maven modules. Move
inventory/benchmark code as a cohesive development area rather than scattering one-class subpackages
through production. Keep report renderers and roadmap-specific constants next to their tool, not in
runtime `mechanics.spell`. The canonical `MechanicsBenchmarkReference` may remain because it is a
domain-level optional link; benchmark implementation must remain outside production runtime. The
7.2.4 consolidation report records zero runtime/reproducibility relocations and identifies many
dependency-sensitive report paths. This proposal concerns Java implementation sources only: preserve
the tracked report, baseline, fixture and evidence paths/content; any later source relocation should
first account for the repository's protected-input/dependency manifests and update their consumers
deliberately.

## Prioritized small-batch refactoring plan (requires separate approval)

1. **Test isolation only:** change inventory report determinism coverage to use `@TempDir` for all
   writes and preserve the current checked-in artifacts. Confirm no tracked file changes after test.
   Keep baseline assertions read-only. This is the smallest correction and prevents accidental
   evidence churn.
2. **Move offline code:** relocate the inventory runner/records/reporters and benchmark engine/types
   to a development source location; move `SchemaDiscovery` out of application packages. Keep
   versioned fixtures/reports at their established paths pending documentation-consolidation review.
   Remove `supportsForInventory`/`extractForInventory` from production pattern interfaces by
   replacing inventory's wider candidate matching with a development-only adapter that calls the
   bounded rule primitives. Do not widen production eligibility to satisfy inventory.
3. **Remove pattern composition duplication:** establish one production pattern composition and give
   offline tooling a read-only view or explicit test adapter. Preserve overlap rejection and per-owner
   inventory fault isolation.
4. **Split PHB family responsibilities:** first extract condition rules (including removal choice and
   save dependencies) behind package-private collaborators, then modifier/protection, then damage /
   healing dispatch only if that remains clearer. Keep `PhbSpellMechanicsExtractor` as a small
   supported-owner adapter. Preserve owner allowlists and exact IDs in the first move-only pass;
   change behavior only in separately reviewed diffs.
5. **Simplify runtime assertions:** replace implementation-type and magic owner-count checks with
   explicit source/pattern contract checks after steps 2–4 have equivalent tests. Retain manifest,
   fingerprint, unique owner, both Shield edition and completeness guarantees.

At each approved batch, review diffs for claim IDs, revision hashes, evidence associations/spans,
processing/coverage states, review and licensing statuses, source hashes, benchmark candidate
semantics, and ordinary plus opt-in REST JSON compatibility. Run focused and full Maven tests and
`git diff --check` as required by the roadmap; these are verification recommendations for later
implementation, not actions taken in this analysis task.

## Risks to manage

- **Stable mechanic IDs:** `SpellMechanicsAssembly.claim` derives IDs from source owner plus slot.
  Renaming a slot or changing owner identity changes IDs and downstream review references. Moves
  must leave owner IDs, slots, adapter/pattern versions and revision construction unchanged.
- **Evidence integrity:** evidence IDs hash resource revision, owner pointer, JSON pointer and spans;
  Shield also retains legacy evidence aliases for compatibility. Keep original bytes, exact pointers,
  hierarchy and source hashes unchanged.
- **Benchmark results:** moving comparison code or changing family dispatch can change candidate
  metrics. Pending fixtures remain candidate diagnostics; no current report is a verified gold set.
  Preserve fixture IDs, revisions, scope, and the boundary that only independently authorized human
  decisions can enable approved evaluation.
- **Review/licensing safety:** extraction must continue to emit pending review and independent
  `NEEDS_REVIEW` rights states. Benchmark matches, inventory classifications and report generation
  must not create `VERIFIED`, clear rights or authorize publication. Keep P1D policy isolated.
- **REST compatibility:** inspection DTOs, opt-in include behavior, default Spell payloads and
  private-inspection boundary must remain byte/contract compatible. Do not expose evidence internals
  or change the publication-oriented projection while moving packages.
- **Historical evidence:** tests currently use analysis files as intentional evidence/baseline inputs.
  Preserve their paths/content unless separately authorized; only remove the normal test's writes to
  the checked-in current reports.

## Audit limits

This audit used source/caller searches and targeted code inspection. It did not run tests, regenerate
reports, compare source hashes, or change files other than creating this audit. The working tree
already contained a modification to `AGENTS.md` before the audit; it was left untouched.
