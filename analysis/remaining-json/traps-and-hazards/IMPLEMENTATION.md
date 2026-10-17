# Traps and Hazards Catalog Implementation

## Corpus and identity

The pinned source is `src/main/resources/data/trapshazards.json`, SHA-256 `17c1b0e10e8b6ad078c41324ccc8ed1e07805e90aa017d27a8e1ed4f5b25d244`. It contains **37 traps** and **73 hazards**. The catalogs deserialize the single source file once per Spring startup and keep separate root, normalized, index, service, and HTTP models.

Trap source distribution: AZfyT 1, BMT 3, DMG 8, RtG 1, VRGR 4, XDMG 8, XGE 12. Hazard source distribution: BGDIA 1, DMG 16, FRAiF 10, IDRotF 2, NF 5, TCE 7, VGM 2, XDMG 25, XPHB 5. Every primary and authored secondary/reprint source token was checked against the Source Registry; no registry changes were needed.

IDs are `trap:<source>:<slug>` and `hazard:<source>:<slug>`, with a deterministic NFKD/ASCII lowercase slug. No `(source,name)` or generated ID collisions occur. Duplicate display names are edition/source-distinguished: two trap names repeat across DMG/XDMG, and 15 hazard names repeat across sources. Name lookup returns all matches and accepts a source filter. Catalog order is name, source, ID.

## Types and preserved mechanics

Strict Jackson records model the shared `trapshazards.json` wrapper, separate Trap and Hazard roots, rating objects, durations, secondary source references, and string `reprintedAs` values. Trap stage/countermeasure values explicitly model text and structured variants (`type`, `style`, typed named items). Descriptive content uses the existing strict recursive `EntryDte` hierarchy, including table cells. Unknown root and custom stage fields fail deserialization. Full-corpus loading exercises the remaining nested records under `FAIL_ON_UNKNOWN_PROPERTIES`.

Normalized `TrapDefinition` and `HazardDefinition` are independent immutable records. They retain source/page, authored type, rating, all entries, source flags, reprint lineage, and all trap-specific effect, trigger, countermeasure, duration, initiative, haunting, and stage fields. Hazard secondary-source/page records remain intact. Lists are defensively copied. Zero and false values remain represented internally. No activation, checks, damage, conditions, timing, reset, character state, or simulation is executed.

Ratings are the only structured threat metadata: 25 traps contain 26 tier/threat entries; 21 hazards contain 26. There are no structured detection/disarming checks, saving throws, damage blocks, level ranges, or reset mechanics. Any such content that appears in entries remains text for later Step 7 analysis and publication review.

## Public API and licensing

Summary DTOs expose ID, name, source/page reference, authored type when present, and tier/threat ratings. Trap detail also exposes authored initiative and duration type strings when present. Hazard detail uses the same safe metadata as its summary. Public projections omit descriptive entries, triggers, effects, countermeasure wording, stage instructions, raw markup, media flags, and resolver diagnostics. This avoids publishing source prose or treating tagged text as an authored mechanical grant.

Read-only endpoints:

- `GET /api/traps?source=&type=`
- `GET /api/traps/{id}`
- `GET /api/traps/by-name/{name}?source=`
- `GET /api/hazards?source=&type=`
- `GET /api/hazards/{id}`
- `GET /api/hazards/by-name/{name}?source=`

Missing IDs follow existing not-found behavior. Filters reflect authored source/type only; no level or severity filter is added because neither field is authored in these roots.

## References and boundaries

There are no structured reference fields. Entry trees contain markup, including 39 spell, 20 item, 86 skill, 113 condition, 7 creature, and 2 hazard tags (plus other presentation, damage, and rules tags). The 146 supported spell/item/skill/filter markup occurrences are extracted with exact owner and field locator through the existing resolver; filter context remains a filter, and definition tokens remain references rather than grants. No source identity is inferred. Trap outcomes: 95 total (35 resolved, 57 ambiguous, 0 unresolved, 3 deferred). Hazard outcomes: 51 total (25 resolved, 24 ambiguous, 0 unresolved, 2 deferred). The prior 4,370 outcomes are asserted unchanged. Unsupported condition, creature/Monster, hazard, and other tags stay preserved inside typed entries.

Traps/Hazards remain distinct from Objects, Monsters, Items, Conditions, and Encounters. The catalog does not implement triggering, disarming, damage/save resolution, environmental simulation, or encounter generation. These are future rules-engine work, not definition loading.

## Verification

The tests validate both complete root arrays, exact source distributions, registry provenance, IDs and duplicate-name lookup, strict unknown-field failures, stage variants, copy/reprint absence/preservation, public serialization, resolver behavior, endpoint filtering, not-found behavior, immutability, and loading the source from a JAR resource. The full Maven suite passes **355 tests** (0 failures, 0 errors, 0 skipped), up from the 349-test baseline. Resolver tests verify that filtering out the new trap/hazard owners leaves the original 4,370 occurrences and outcome totals unchanged. The packaged application JAR serves all 37 traps and 73 hazards and returns detail for both catalogs. `git diff --check` passes.

The recommended next Step 6 catalog is **Vehicles and Upgrades**, following the existing implementation order. Vehicles and upgrades should remain separate definition kinds with typed compatibility references; no automatic upgrade application.
