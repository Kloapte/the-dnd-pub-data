# Ranked Backlog and Proposed Step 7 Batches

## Prioritization

Correct source distinctions and public representation before extracting new effect semantics.
Counts are authored-source denominators unless explicitly labeled effective. Publication-risk
labels are relative engineering priorities, not legal approvals. The original analysis did not
authorize implementation. Batches 1, 2, 3A, 3B, 4 and 5 were subsequently authorized and implemented
as recorded below. Batches 6 and 7 were subsequently authorized and implemented as recorded below.
The remaining order follows the completed
[EntryDte assessment roadmap](entry-assessment/IMPLEMENTATION-ROADMAP.md); later batches remain
proposals requiring separate authorization.

| Rank | Candidate / catalogs | Applicable records or fields | Confidence | Complexity | Publication risk / public value | Dependencies and testability |
|---:|---|---|---|---|---|---|
| P0 | Publication assessment of current Monster, Item and Optional Feature exposure | 4,851 Monster contract scope; 220 Item raw roots; seven Optional Feature roots / 14 strings | Exact current mapping trace; rights unknown | Medium/high review | High uncertainty; prerequisite to broadening those fields | Rights/compatibility decision; serialized-contract audit, no automatic suppression |
| 1 (implemented) | Class/Subclass table positions | 97 authored rows across six roots | EXACT_STRUCTURED | Low/medium | Low incremental content exposure; high correctness value | Additive positioned cells; full 2,536 class-domain definition regression verified |
| 2 (implemented) | Item focus representation | 52 authored array roots / 55 effective arrays, plus two Booleans | EXACT_STRUCTURED | Low | Internal only; public JSON unchanged | Exact label/variant preservation and all 2,742 stable identities verified |
| 3 (implemented: Batch 3A) | Feat structured grant/defense normalization | 59 authored/effective owners / 74 occurrences / 14 families | EXACT_STRUCTURED | Medium | Internal only; public JSON unchanged | Reused matching grant values; all 314 identities and nine versions verified |
| 4 (implemented: Batch 3B) | Feat prerequisite category preservation | 12 authored / 18 effective roots | EXACT_STRUCTURED | Low | Internal only; existing public strings unchanged | Category and alternative indexes preserved; 314 IDs verified |
| 5 (implemented: Batch 4) | Item signed bonus normalization | 339 roots / 410 authored expressions; 423 effective expressions | Deterministic numeric parse; exact property role | Low | Internal only; public strings unchanged | Exact tokens; no stacking/character effects |
| 6 (implemented: Batch 5) | Shared evidence/span and lossless syntax foundation | 91,994 tags / 73 families audited; 42,741 occurrences in 21 recognized families | Deterministic syntax only | Medium | Internal library; no public or effective annotations | Exact spans/pointers; all 175 files; separate domain adapters |
| 7 (implemented: Batch 6) | Spell internal formula adapter | 969 authored roots; 930 formula tags on 422 owners | Deterministic syntax; effect semantics mostly unknown | Low/medium | Internal only; useful audit/search foundation | Shared foundation; exact owner/context coverage; effective annotations not materialized |
| 8 (implemented: Batch 7) | Legendary-group support preservation | 188 authored / 209 expanded support groups; 16 copies / 21 versions / one template | DIRECT ordered body preservation | Medium | Internal only; no new public content | Exact original entry provenance; 307 effective Monster associations; no resolver additions |
| 9 (implemented: Batch 8) | Monster internal syntax adapter | 4,560 raw / 4,851 effective owners; 57,534 authored-source tags | Deterministic syntax/owner context | Medium/high | Internal only; no public change | Shared foundation; effective origin intentionally not materialized |
| 10 | Class ability/choice completeness review | 29 typed declarations / 40 option nodes | DIRECT mapper audit | Low/medium | Public changes need separate review | Preserve options and distinguish withheld content from absence |
| 11 | Object attack/hit structural pilot | Seven explicit attack-container roots | Exact structure; contextual association needs gold set | Low/medium | Internal first; limited but testable | Seven-record review set; no prose-derived riders/targets |
| 12 | Item charge syntax/public projection review | 220 roots forwarding raw tag text | Syntax deterministic; field role explicit | Medium | Review required; removes ambiguity only through approved contract | Existing consumers and rights decision, typed-expression test set |
| 13 | Treasure quantity representation | 20 definitions / 62 coin fields | Typed owner role; source-specific grammar review | Low/medium | Internal normalization only; public tables require review | Exact ranges/order/amount strings; no roll or filter execution |
| 14 | Character-feature internal adapters | 677 Class Features + 1,499 Subclass Features | Syntax deterministic, effects contextual | Medium | Internal only | Shared foundation, level/choice/consumption context; existing graph unchanged |
| 15 | Vehicle summary/detail consistency | Eight roots, 12 top-level AC/HP fields | EXACT_STRUCTURED | Low | Existing summary metadata; possible additive detail value | Same projection semantics; no component speed inference |
| Defer | Condition/Disease effects, Facility order benefits, Reward non-spell effects, Trap/Hazard execution, Upgrade effects, Variant Rule changes, Multiattack graphs | Review scopes: 30/29, 70, 278, 37/73, 31, 231 and 2,221 Multiattack-bearing raw Monsters | HEURISTIC/UNSUPPORTED for complete effects | High | High uncertainty; no automatic public effect value established | Reviewed semantic gold sets, specific rights decisions; no generic engine |

No near-term task should re-extract Monster AC/HP/defenses, Spell casting/duration/range,
existing character-option schedules or source-typed Vehicle statistics. Those are already
preserved. No gameplay enrichment is proposed for Recipes, Books, Adventure/Name indexes or
Deity domain labels. Homecraft remains excluded.

## Batch 0: Record publication decisions for existing exposed content

**Scope:** review current Monster rich-content detail fields, tagged Item charge strings, and
Optional Feature prerequisite text. Produce a field/source decision list and an API compatibility
proposal. This can proceed alongside internal correctness work.

**Structures/public changes:** an internal publication-assessment document or classpath policy is
a possible future output; no new generic authorization subsystem is assumed. Public changes, if
needed, require their own approved migration scope.

**Acceptance:** every identified field has a documented approved/withheld/unreviewed disposition,
rights basis or explicit unresolved question, and a consumer compatibility plan. Existing
authorizations are investigated before proposing removals.

**Exclusions:** no legal conclusion from source flags; no mass rewriting of Monster DTOs; no
automatic license inference or content removal in an annotation batch.

## Batch 1: Preserve Class/Subclass progression column positions

**Status: IMPLEMENTED and verified (2026-10-17).**
[Implementation report](batch-1-class-table-positions/IMPLEMENTATION.md) and
[DTO compatibility decision](batch-1-class-table-positions/DTO-DESIGN.md).
The additive `positionedCells` projection preserves original zero-based indexes while retaining
the legacy compact `cells`. Verified 97 authored and 97 effective affected rows across six roots
(117 public path occurrences including repeated pact tables); all 2,536 identities and 6,246
resolver results remain unchanged. Full suite: **409 passing tests**, with executable-JAR HTTP
verification and whitespace checks passing. The original analysis evidence remains unchanged.

**Exact scope:** `ClassPublicMapper.safeTables()`, the public progression cell/row representation
and associated mapper tests. Source owners are the six roots listed in NORMALIZATION-GAPS G1.
Internal normalizers already preserve row order; keep them intact unless a separately demonstrated
mapping issue requires a change.

**Proposed structures:** a public cell projection carrying `columnIndex`, or a reviewed
position-preserving withheld-cell variant. Prefer explicit source indexes; select an additive
positioned representation or a versioned contract through a deliberate compatibility decision.
Do not silently replace the meaning of the existing compact `cells` array.

**Public value:** every retained number/bonus/dice/speed value points to its authored column.
No previously withheld source text is published. Zero and false remain meaningful.

**Tests:** PHB Barbarian's first row must retain the Rage Damage value at column one while column
zero remains withheld; exercise the remaining 96 authored rows, tables with no removed cells,
fully withheld rows, copy-effective subclasses, safe labels, deterministic row/column ordering,
immutability and JSON omission behavior. Verify all 2,536 class-domain IDs and existing APIs.

**Acceptance:** no retained cell shifts columns, no table text leaks, no feature/reference graph
changes, and the full suite passes with the same 6,246 resolver outcomes. Report effective affected
row counts independently from the 97 authored rows.

**Exclusions:** parsing numeric-looking source strings, changing progression values, recalculating
levels/slots, changing feature references or publishing feature-entry tables.

## Batch 2: Preserve Item focus variants internally

**Status: IMPLEMENTED and verified (2026-10-17).**
[Implementation report](batch-2-item-focus/IMPLEMENTATION.md) and
[internal model checkpoint](batch-2-item-focus/DTO-DESIGN.md).
The sealed `ItemFocus` value preserves exact ordered labels and explicit Booleans. Verified
52 authored arrays plus two true Booleans, and 55 effective arrays plus two Booleans after three
copy inheritances. All 2,742 identities, existing public Item JSON, Magic Variant matches and
6,246 resolver results remain unchanged. Full suite: **418 passing tests**, with executable-JAR
verification, unchanged source hashes and whitespace checks passing. Original scanner evidence
is unchanged. Seven additional group-selector arrays remain typed auxiliary source data.

**Exact scope:** add a narrow normalized Boolean/ordered-class-label focus value and map from
`ItemStringListDte`; retain the existing Item flag for compatibility where authored.

**Public changes:** none required for internal completion. A separate approved additive field can
follow; no Class IDs or inferred source identity. No source JSON or Item identity changes.

**Tests/acceptance:** all 52 arrays and both Boolean cases survive normalization, copy inheritance
is preserved, absent/empty/false/unknown shapes are explicit, all 2,742 definitions still load,
and magic-variant matching/reference outcomes remain unchanged.

**Exclusions:** eligibility, focus application, modifier calculation, Item-to-Class resolver work.

## Batch 3A: Normalize existing Feat grant/defense/sense/progression fields

**Status: IMPLEMENTED and verified (2026-10-17).**
[Implementation report](batch-3a-feat-structured-mechanics/IMPLEMENTATION.md) and
[internal model checkpoint](batch-3a-feat-structured-mechanics/DTO-DESIGN.md).
`FeatMechanics` preserves the fourteen fields in immutable proficiency, defense, sense and
progression structures, reusing existing matching named-grant/choice/resistance values.
All 74 authored/effective occurrences on 59 owners survive; all 314 identities, nine versions,
existing public JSON and 6,246 resolver results remain unchanged. Full suite: **430 passing
tests**, with packaged-JAR and source-hash verification and whitespace checks passing.
The executable-JAR smoke report separately records the existing encoded-semicolon by-name
403 limitation; version ID lookups pass. Original scanner evidence is unchanged.

**Exact scope:** fourteen DTE field families in G3; extend `FeatDefinition` with small typed
bundles and preserve their source alternatives. Compare existing grant types before reuse.

**Public changes:** withheld initially, or an explicitly reviewed subset; never serialize source
DTEs wholesale. Keep current ability/prerequisite/spell fields compatible.

**Tests/acceptance:** every one of the 74 root field occurrences maps losslessly; fixed and choice
proficiencies, expertise, combined choices, resistance variants, sense/bonus-sense semantics and
progression keys remain distinct. All 314 effective definitions retain their identities, parent
lineage and nine versions. Source records/entries and the existing resolver snapshot are unchanged.

**Exclusions:** selecting options, awarding proficiencies, applying immunity or assigning source
IDs to unqualified names. No character progression engine.

## Batch 3B: Preserve Feat prerequisite proficiency categories

**Status: IMPLEMENTED and verified (2026-10-17).**
[Implementation report](batch-3b-feat-prerequisites/IMPLEMENTATION.md).
Twelve authored proficiency requirement objects preserve their exact `armor`, `weapon` or
`weaponGroup` key/value and prerequisite alternative index. The six Strike of the Giants versions
inherit the typed prerequisite group, giving 18 affected effective definitions. Existing
prerequisite strings and public JSON remain unchanged; all 314 Feat IDs and 6,246 resolver results
are stable. The full suite passes with **436 tests**, and packaged-JAR and whitespace checks pass.

**Exact scope:** supplement the lossy joined-string compatibility field with typed
armor/weapon/weaponGroup values and original alternative indexes for the 12 source roots. Read
DTEs, not legacy output strings.

**Public changes:** explicit compatibility review for any prerequisite DTO change; an additive
structured representation is preferable to reinterpreting existing strings without notice.

**Tests/acceptance:** AND fields and OR groups, category identity, multiple values and absent
requirements survive. No new prerequisite source fields are invented; the investigated
race/background-source omission predicate has zero authored matches.

**Exclusions:** eligibility evaluation, display-prose extraction and new target adapters.

## Batch 4: Lossless Item bonus-token normalization

**Status: IMPLEMENTED and verified (2026-10-17).**
[Implementation report](batch-4-item-signed-bonuses/IMPLEMENTATION.md).
The ten typed Item bonus fields now retain exact signed tokens and strict integer values in a
separate internal `ItemSignedBonuses` projection. All 410 authored occurrences on 339 roots and
all 423 effective occurrences on 349 definitions are accounted for, including 13 values inherited
by ten copies. DTE deserialization rejects non-string JSON values for these fields. Legacy string
fields, all 2,742 IDs, Item public JSON, Magic Variant behavior and 6,246 resolver results remain
unchanged. The full suite passes with **443 tests**, and packaged-JAR, source-hash and whitespace
checks pass.

**Exact scope:** the 410 signed-integer source strings across 339 Item roots; add authored-token
plus parsed integer internally. Future source tokens outside the declared grammar remain explicit
unsupported values or fail the declared normalization contract.

**Public changes:** keep current strings unless separately approved; do not change a JSON string
to a number implicitly. This is an internal value improvement, not an applied effect system.

**Tests/acceptance:** complete source token/number round-trip, signs/zero/negative synthetic cases,
unknown grammar rejection/retention policy, copies, separate weapon/attack/damage/save/proficiency
property roles. No bonus stacking, recipient or character modifier inferred.

## Batch 5: Shared internal evidence and lossless syntax foundation

**Status: IMPLEMENTED and verified (2026-10-17).**
[Implementation report](batch-5-syntax-foundation/IMPLEMENTATION.md) and
[internal design checkpoint](batch-5-syntax-foundation/DTO-DESIGN.md).
The immutable evidence/span values, balanced parser, syntax observations and authored-input
walker form an unwired internal library. RFC 6901 leaf pointers and Java UTF-16 spans preserve
original source locations and all tag text/arguments/nesting. No effective annotation origin,
domain adapter or gameplay effect is inferred.

All **91,994** in-scope tags across 73 families parse with exact source reconstruction, including
**90,294** definition-root tags. The 21-name initial vocabulary recognizes **42,741** observations;
the other **49,253** remain opaque. Malformed tags and span failures: **zero**. Authored,
supporting and transform-instruction scopes are counted separately; no effective annotations
are materialized. All 175 source files and original assessment artifacts remain unchanged.

The full suite passes with **464 tests** (21 new), including unchanged catalog/public/reference
fingerprints and all **6,246** resolver outcomes. Both executable-JAR startup and an isolated
packaged-foundation probe pass. Source/scope hash and whitespace checks pass.

**Boundary:** no public changes, catalog wiring, Spell/Monster adapter, legendary-group loading,
formula evaluation, branch/target/damage-type association or new reference occurrence. This batch
supersedes the original direct-to-Spell Batch 5 proposal with the shared assessment foundation.

## Batch 6: Spell formula adapter — implemented

**Status:** implemented and verified. See [Batch 6 implementation report](batch-6-spell-formulas/IMPLEMENTATION.md).

**Coverage:** internal observations for `damage`, `dice`, `scaledamage`, and `scaledice` across all
969 authored Spells: 930 occurrences on 422 owners. The 18 `hit`, `d20`, and `dc` syntax observations
are retained separately and are not associated with formulas. Reuse Batch 5 evidence/parser/
observations. Preserve exact paths, offsets, arguments, hierarchy, table and higher-level context,
and source hashes. Effective formula annotations are not materialized.

**Structures:** `SpellFormulaAnnotationAdapter`, immutable `SpellFormulaAnnotationIndex`,
`SpellFormulaAnnotation`, `SpellFormulaSection`, and separate `SpellSyntaxAnnotation`. Existing
SpellDefinition structured metadata and all 41 scaling-field records remain intact.

**Public changes/cross-catalog implications:** none. No new reference occurrences, target kinds,
resolution, or public formula DTO. Existing reference projection remains independent.

**Verification:** all 930 authored formula occurrences and 422 owners accounted for, exact source
pointer/span reconstruction, UTF-16 offsets, corpus identity reconciliation, required spell examples,
unchanged public contracts and all 6,246 resolver outcomes. Full Maven suite: 470 tests passed;
packaged-JAR startup and `git diff --check` passed.

## Batch 7: Legendary-group support preservation — implemented

**Status:** implemented after the evidence-first design checkpoint. See the
[Batch 7 report](batch-7-legendary-groups/IMPLEMENTATION.md).

**Scope:** strict typed preservation of all 188 support roots, including 115 authored lair,
158 regional and three mythic fields. All 16 copies and 21 concrete versions resolve using the
audited append/prepend dialect and one dedicated legendary-group template, producing 209 internal
support groups. Original entry pointers, source hashes, copy/version/template lineage and nested
entry ordering are retained. No gameplay effects or Monster syntax annotations are introduced.

**Associations:** 240 directly authored Monster group references / 307 effective references,
all resolved by exact name/source to 182 support groups. Duplicate support identities fail loading;
missing exact associations remain unresolved. Support groups are not additional Monster definitions.

**Verification:** 484 full-suite tests passed (14 new), isolated packaged support probe and
executable-JAR smoke passed; all 175 source JSON files and 1,712 pre-existing src files retain their
bytes. Public contracts and all 6,246 shared resolver outcomes remain unchanged. Source audit
determinism, original assessment preservation and whitespace checks pass.

## Batch 8: Internal Monster syntax adapter

**Status: IMPLEMENTED and verified.** See the
[Batch 8 implementation report](batch-8-monster-syntax/IMPLEMENTATION.md).

**Coverage:** a startup-built, immutable authored-source index covers all 4,560 roots in 111
bestiary files and reconciles every root to the unchanged 4,851 effective Monster catalog IDs.
The shared Batch 5 parser/classifier yields 57,534 exact-span observations: 55,026 from authored
body/source text and 2,508 from authored transformation instructions, on 3,733 owners. The 291
effective generated versions have no copied or transformed annotations because the current
Monster resolver does not retain per-text origin mappings. Their identities are counted separately.

**Context and syntax:** trait/action/bonus/reaction/legendary/mythic/variant/spellcasting containers
retain their exact field and optional authored feature name. Formula, modifier, label and opaque
observations are separate. Recharge retains its 0/1/2-argument syntax (165/740/2); no effect
relationships, recharge execution, attack or Multiattack graph is inferred. Legendary-group
support remains separate: 209 support definitions and 307 associations; support body tags are not
Monster observations.

**Public/reference implications:** none. Monster DTOs, endpoints, source identities, normalized
statistics, spellcasting and action projections remain unchanged. No resolver occurrences or
target kinds are added.

**Verification:** nine new full-corpus tests and the full suite pass: 493 tests total, zero
failures/errors/skips. All 175 source JSON hashes, 4,851 Monster IDs, 209 support identities,
307 associations and 6,246 resolver outcomes remain unchanged. The isolated packaged-JAR probe
reads the new index directly from packaged classes/resources; the application JAR smoke and
`git diff --check` pass.

## Proposed Batch 9: Class ability/choice completeness review

**Exact scope:** trace 29 existing typed ability declarations and 40 option nodes through DTEs,
normalized structures and public projection. Confirm how preserved but withheld choices are
represented. This is a completeness review before any separately approved contract change.

**Acceptance:** source/model/projection evidence for every declaration/option and an explicit
publication/compatibility decision list. No guaranteed grant, eligibility rule or option-prose
publication is inferred.

## Proposed Batch 10: Seven-record Object attack-container pilot

**Exact scope:** retain explicit `type=attack`, `attackEntries` and `hitEntries` association in
the seven source roots as internal annotations, using existing typed entry structures.

**Public changes:** none initially. A reviewed semantic pilot may follow, but not as an automatic
generic damage/condition API.

**Tests/acceptance:** first establish a reviewed seven-root gold set and abstention rules for any
LOCAL_CONTEXT relationships. Keep DIRECT container preservation distinct from interpreted effect
associations. Validate exact source locators and field separation, negative
plain-text attacks, multiple formulas, options and unsupported riders. Do not infer targets,
damage types or effect application from prose. Object/Monster/Item identities remain distinct.

## Later bounded batches

1. **Item charge syntax/publication:** 220 raw roots, a reviewed typed-expression projection and
   explicit consumer migration; do not execute recharge or broaden raw-text exposure.
2. **Vehicle detail consistency:** eight roots / 12 AC/HP fields already projected by summary;
   evaluate an additive detail projection reusing current safe types. No component movement
   numbers are authored in `VehicleMovementSpeedDte`.
3. **Treasure quantity syntax:** 62 coin-expression fields on 20 definitions, internal only until
   field/source approval; test all seven source collections, nested rows and exact boundaries.
   No rolling, filter expansion or awards.
4. **Character feature annotations:** separately cover Class Features, then Subclass Features,
   preserving level, option groups, consumes and existing graph IDs. No prose-based resource
   reset/maximum or guaranteed grant inference.

Every later batch needs its own field/variant inventory, effective coverage and immutable startup
index. No single batch combines all spell, Monster, Item and character-option enrichment.

## Deferred semantic work

Do not schedule automatic full effects for Conditions/Diseases, Trap/Hazard countermeasures,
Facility orders, Upgrade benefits, Variant Rule changes, Multiattack graphs or arbitrary prose.
Current evidence supports internal syntax, not a complete deterministic semantic extraction rate.
Reconsider only after a narrow reviewed gold set, reliable abstention rules and publication
decision exist. No rules engine is necessary to complete the proposed preservation batches.

All batches exclude Step 8 target expansion/reference integration, persistence, synchronization,
frontend changes, campaign/character state, Homecraft and Recipe gameplay mechanics.
