# Step 2 Phase 5 — Rule Gaps Batch 4

## Scope and verified baseline

Batch 4 implements only R-GAP-15 (falling), R-GAP-36 (area geometry), R-GAP-41 (movement procedures), and R-GAP-45 (movement costs). The existing Batch 3 checkpoint was verified before editing: 44 Rule identities accounted including five representatives, 40 Rule outputs, 187 pending Rule identities, 360 Phase 5 canonical outputs, and 596 permanent checks.

The original source is `src/main/resources/data/variantrules.json`, collection `$.variantrule`. Codex interpreted the original source records directly. Deterministic tooling was used only for schema validation, record inventory, and accounting reconciliation.

## Implemented contract changes

The existing Rule `MOVEMENT_SPATIAL` component and `areaGeometry` component were extended. No new universal spatial model was introduced.

- `movement.details` now has bounded variants for fall damage and flight-fall triggers; burrowing; climbing, crawling, and swimming costs; climbing-speed exceptions; flying and Hover; High Jump and Long Jump; special-Speed selection and adjustment; Fly Speed; Swim Speed; and grid movement costs.
- Movement subtypes now include `BURROWING`, `HOVERING`, `SPECIAL_SPEEDS`, and `GRID_MOVEMENT`. Procedures include bounded movement-mode, speed, fall, jump, and grid-cost variants.
- Area geometry supports either one source-defined shape or the general six-shape Area of Effect rule. Shape records identify origin placement, named dimension inputs, origin inclusion, and the Cone width rule. Emanation records its source-relative origin behavior. General Area of Effect rules record Total Cover line blocking and near-side placement for an unseen origin beyond an obstruction.
- The schema rejects incompatible movement-detail discriminators, unsupported dimensions/origin combinations, extra movement-cost values, and unapproved grid-cost sequences.

No damage calculation, pathfinding, target enumeration, grid intersection, area rendering, creature Speed state, or movement execution was added.

## Source records and coverage

The 23 new Rule identities each have a Phase 5 canonical output. `FULL` means all in-scope source mechanics for that record are represented; unrelated procedures explicitly excluded by this batch remain outside this classification only where they are separate source entries/procedures accounted as omitted.

| Gap | Source-qualified identities and source paths | Result |
|---|---|---|
| R-GAP-15 | `rule:xge:falling` — `$.variantrule[106]` | PARTIAL |
| R-GAP-36 | `rule:xphb:area-of-effect` — `[11]`; `rule:xphb:cone-area-of-effect` — `[31]`; `rule:xphb:cube-area-of-effect` — `[39]`; `rule:xphb:cylinder-area-of-effect` — `[43]`; `rule:xphb:emanation-area-of-effect` — `[95]`; `rule:xphb:line-area-of-effect` — `[145]`; `rule:xphb:sphere-area-of-effect` — `[211]` | 7 FULL |
| R-GAP-41 | `rule:xphb:burrow-speed` — `[21]`; `rule:xphb:climb-speed` — `[28]`; `rule:xphb:climbing` — `[29]`; `rule:xphb:crawling` — `[34]`; `rule:xphb:fly-speed` — `[114]`; `rule:xphb:flying` — `[115]`; `rule:xphb:high-jump` — `[125]`; `rule:xphb:hover` — `[130]`; `rule:xphb:jumping` — `[141]`; `rule:xphb:long-jump` — `[146]`; `rule:xphb:speed` — `[205]`; `rule:xphb:swim-speed` — `[216]`; `rule:xphb:swimming` — `[217]` | 13 FULL |
| R-GAP-45 | `rule:dmg:diagonals` — `$.variantrule[52]`; `rule:phb:playing-on-a-grid` — `$.variantrule[172]` | 2 PARTIAL |

Paths in the table are rooted at `$.variantrule`; complete entry and nested paths are recorded in `CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json`.

The Phase 1 gap inventory associated nine records with R-GAP-41. A direct source-name and clause review found four additional XPHB entries in the authorized movement family: Climb Speed, Crawling, Fly Speed, and Jumping. Their source mechanics are cost waiver, crawling cost, flight-mode behavior, and the explicit horizontal/vertical jump choice. These are included as source-backed additions, not inferred from names.

### R-GAP-15 — Falling

`rule:xge:falling` represents 1d6 bludgeoning damage per 10 feet, capped at 20d6, and Prone on landing unless fall damage is avoided. Its optional flying-fall trigger records Prone, Fly Speed zero, or otherwise being unable to move, with Hover or magical support as exceptions. This is source-specific to the XGE record and its cited PHB rule; no XPHB fall-damage rule was invented.

The source does not state how an incomplete 10-foot increment is treated. The canonical data marks this `SOURCE_UNSPECIFIED`; it does not guess floor or ceiling behavior. The optional 500-foot-per-turn cadence, subtraction of Fly Speed from fall distance, and later-turn midair fall-halting procedure remain omitted as `OUT_OF_APPROVED_SCOPE`. The source entry containing the latter flying variants is mixed: its first flying-fall trigger paragraph is represented and its separate variants are omitted.

### R-GAP-36 — Area geometry

The Area of Effect overview references the six typed XPHB shape records and represents its line-of-effect rule: a location is excluded only when all straight lines from origin are blocked, and Total Cover blocks a line. An unseen origin placed beyond an obstruction appears on its near side.

The shape records preserve source-defined geometry:

- Cone: selected direction and maximum length; width equals distance from origin; origin excluded unless the creator chooses otherwise.
- Cube: origin anywhere on a face; effect-defined side length; origin excluded unless chosen.
- Cylinder: origin at the center of the top or bottom circle; effect-defined base radius and height; origin included.
- Emanation: creature or object origin and effect-defined extent; follows its origin except for instantaneous or stationary effects; origin excluded unless chosen.
- Line: source-defined length and width along a straight path; origin excluded unless chosen.
- Sphere: point origin, effect-defined radius, extension in all directions, and included origin.

`rule:xge:spellcasting` remains pending conversion accounting. Its grid template/token procedures are intentionally excluded because this batch does not implement area rendering, square intersection, or target enumeration; its other spellcasting procedures are also outside the four approved gaps.

### R-GAP-41 — Movement procedures

Canonical mechanics describe burrowable materials and the solid-rock trait exception; climbing/crawling/swimming surcharges and their Difficult Terrain costs; speed-based cost waivers; DM-optional Athletics checks; High/Long Jump formulas and source-defined checks; XPHB flying-fall triggers and Hover; Fly Speed air travel and aloft termination conditions; and switching/applying special Speeds during a move.

Creature records remain the owner of available Speed values. Conditions remain the owner of Condition effects. The PHB/XPHB Difficult Terrain representative remains unchanged; a regression assertion confirms its +1-foot cost and non-cumulative behavior.

### R-GAP-45 — Movement costs

DMG Diagonals models the optional alternating 5-foot/10-foot diagonal movement cost, continuing across intervening straight movement. PHB Playing on a Grid models 5-foot squares and movement segments, the minimum one square remaining to enter a square, and full payment of an extra-cost square. The PHB record retains its source `VARIANT` classification.

Diagonal range measurement, grid corner blocking, and shortest-route range are omitted as `OUT_OF_APPROVED_SCOPE`. These records remain PARTIAL; no distance, grid, or pathfinding engine was added.

## Source accounting and references

Forty source-entry roots were added across the 23 records. Their disposition additions are 38 INTERPRETED, 1 DUPLICATE, and 4 OMITTED classifications. The omitted register preserves complete original source entries for XGE Falling entries 1 and 2, DMG Diagonals entry 1, and PHB Playing on a Grid entry 0. The duplicate register preserves DMG Diagonals entry 2, whose example repeats the represented alternating cost.

The XGE Falling source `reprintedAs` target and DMG Diagonals XDMG reprint target are absent from the immutable Rule catalog. Their source values remain in the Rule inventory and are recorded as two `REFERENCE_UNRESOLVED` source-field issues. No canonical target IDs were fabricated or emitted as references.

## Updated checkpoint

| Measure | Batch 3 | After Batch 4 |
|---|---:|---:|
| Rule identities accounted, including 5 representatives | 44 | 67 |
| Rule FULL / PARTIAL / UNSUPPORTED | 29 / 11 / 4 | 49 / 14 / 4 |
| Rule identities pending conversion accounting | 187 | 164 |
| Total canonical Rule outputs, including representatives | 40 | 63 |
| Phase 5 Rule canonical outputs | 35 | 58 |
| Phase 5 Rule records examined, representatives excluded | 39 | 62 |
| Phase 5 Rule FULL / PARTIAL / UNSUPPORTED | 25 / 10 / 4 | 45 / 13 / 4 |
| Rule source-entry roots | 112 | 152 |
| Rule dispositions: INTERPRETED / DUPLICATE / OMITTED | 52 / 3 / 64 | 90 / 4 / 68 |
| Total Phase 5 canonical outputs | 360 | 383 |
| Total Phase 5 FULL / PARTIAL / UNSUPPORTED | 204 / 156 / 7 | 224 / 159 / 7 |
| Total Phase 5 source-entry roots | 388 | 428 |
| Total Phase 5 dispositions: INTERPRETED / DUPLICATE / OMITTED | 256 / 27 / 254 | 294 / 28 / 258 |
| Structured-field/reference issues | 155 | 157 |

## Validation

`npm run validate:core-rules` passed: 9 Draft 2020-12 schemas compiled, 89 examples validated, 34 Phase 3/4 canonical records validated, 383 Phase 5 canonical records validated, and 638 permanent checks passed. The validator checked 49 Rule example components and 27 Action records. Positive semantic assertions cover fall damage/cap/conditions, all six area shapes and origin rules, movement procedures and costs, and edition distinctions. Negative assertions reject invented fall rounding, incorrect fall caps, non-Total-Cover line blocking, shape/origin conflicts, altered climbing/crawling surcharges, and invalid diagonal costs or jump references.

`git diff --check` is run after the report and documentation updates. These results establish schema, accounting, reference, and targeted source-backed assertion success; they do not prove semantic correctness beyond the reviewed source mechanics.

## Remaining boundaries

XGE Spellcasting grid templates and token placement, fall-rate scheduling, special fall-speed adjustments, grid range/corner procedures, encounter spatial execution, creature movement values, and runtime movement costs remain unimplemented. The 164 pending Rule identities have not been bulk-converted. Batch 4 completes the four approved Rule gap batches only; Phase 5 remains open and unapproved. Phase 6 and Step 3 have not started.
