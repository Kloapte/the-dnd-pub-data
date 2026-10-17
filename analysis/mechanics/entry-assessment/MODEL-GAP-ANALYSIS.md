# Normalized model coverage and gaps

This assessment concerns representation, not rules execution. COMPLETE means complete for a stated narrow source contract; it never certifies a complete D&D mechanic. Java/source links and per-domain paths are in [CROSS-CATALOG-MATRIX.md](CROSS-CATALOG-MATRIX.md) and `model-review.json`.

## Completed preservation work

The historical [NORMALIZATION-GAPS.md](../NORMALIZATION-GAPS.md) is pre-implementation evidence. Its G1–G4 must not be reopened as current defects.

| Completed batch | Current representation | Verified coverage from its implementation report |
|---|---|---|
| 1, progression positions | Additive public positionedCells plus unchanged compact cells | 97 authored and 97 effective affected rows; six roots; 117 public path occurrences including repeated pact tables |
| 2, Item focus | ItemFocus Boolean/ordered-label variants, plus legacy classifications/Boolean | 52 authored arrays, 55 effective arrays; two true Booleans; three inherited arrays |
| 3A, Feat mechanics | FeatMechanics grants/defenses/senses/progression bundles | 74 authored/effective field occurrences on 59 owners; all 314 Feats/nine versions remain present |
| 3B, Feat prerequisite categories | Typed category keys with prerequisite alternative indexes | 12 authored owners; 18 effective owners after six Strike of the Giants versions |
| 4, Item bonuses | ItemSignedBonus token/integer values separated by field | 410 authored occurrences/339 roots; 423 effective occurrences/349 Items; 13 inherited values |

Reports: [1](../batch-1-class-table-positions/IMPLEMENTATION.md), [2](../batch-2-item-focus/IMPLEMENTATION.md), [3A](../batch-3a-feat-structured-mechanics/IMPLEMENTATION.md), [3B](../batch-3b-feat-prerequisites/IMPLEMENTATION.md), [4](../batch-4-item-signed-bonuses/IMPLEMENTATION.md).

Batch 2 also corrected the earlier claim that focus labels were wholly lost: they already survived in mixed classifications. Its fix preserves their distinct role and variant. Feat wildcard progression keys remain literal `*`, not invented character levels. Existing public contracts intentionally do not publish the new internal focus/Feat/bonus structures.

## Coverage by mechanical concept

| Concept / source contract | Coverage | Current model sufficiency and missing information |
|---|---|---|
| Monster/Object/Vehicle base statistics; Item damage/AC | COMPLETE for authored stat variants | Already typed and retained. This does not model resulting attack damage, character AC or damage application |
| Spell casting/range/components/duration/scaling | COMPLETE for authored fields | SpellDefinition retains structured variants; 41 scaling roots already normalized. Range geometry is not necessarily effect geometry |
| Feat/Race/Background proficiency choices | COMPLETE for the inspected source grant contracts | Existing domain-specific groups preserve fixed versus choice/category roles. Batch 3A reuses matching Race value types; no selected outcomes inferred |
| Class feature ability declarations | COMPLETE internally; public subset withheld | 16 abilityDc and 13 abilityAttackMod nodes retain kind/name/attributes via ClassFeatureAbilityDeclaration. No need to extract these from prose or compute renderer defaults |
| Generic formula syntax in entries | MISSING as a normalized annotation index | Exact strings remain retained in shared entries, ClassFeatureText or Monster content. This is a missing searchable representation, not discarded source |
| Damage formula plus damage type | PARTIAL | Item dmg1/dmgType role is explicitly structured; Monster/Spell tags and root classifications do not bind each expression to a type |
| Healing and temporary HP | MISSING semantic effect models | Healing Word PHB and False Life PHB illustrate prose role, symbolic modifiers and duration. Dice tags alone cannot fill an effect model |
| Saving throw/check and outcome effects | PARTIAL | Ability/save classifications and Class DC declarations exist; most DC-to-check/save and success/failure consequence relations are prose or label syntax |
| Condition applications/removals | MISSING | conditionInflict is classification, not a per-effect application; condition tags are links, not imposed effects |
| Resource usage and reset | PARTIAL | OptionalFeatureConsumptionDto, ClassFeatureConsumption and spellcasting frequency buckets encode specific roles. Pool maximum, reset and usage trigger often remain text |
| Action economy | PARTIAL | Action timing, Monster action category and legendary counts, Vehicle thresholds already exist. Textual action costs/trigger rules are not generally structured |
| Choices | COMPLETE internally for Class options; public subset PARTIAL | 40 ClassOptionsEntryDte nodes: 25 author count, three author style. Public mapping exposes reference-backed choices but omits text/statblock/ability options |
| Scaling | PARTIAL across concepts | SpellScaling and Class tables are typed; prose scaling, modifier dependencies and effect-specific axes are not universally modeled |
| Targeting, trigger, conditional/ongoing effects | MISSING general semantics; narrow structures PARTIAL | Trap role containers and Object attack/hit separation preserve stronger boundaries than plain proximity. They do not provide generic targets/events |
| Conditions/Disease rules, facility/upgrade/rule benefits | REQUIRES_REVIEW | Typed content retained, but semantic models require reviewed interpretation and publication decisions |
| Quantity/roll tables | COMPLETE for current dedicated table shapes; PARTIAL expression interpretation | Exact ranges/order/amount strings remain. Not a uniform roll or loot-effect model |
| Culinary recipes, navigation, deity lore, language descriptions | NOT_APPLICABLE to gameplay effects | Existing metadata/content boundaries remain valid. No gameplay model proposed |

## Full taxonomy model-status ledger

The broad categories below include entry-authored mechanics across catalogs. PARTIAL does not imply a defect in the narrower existing field contracts. MISSING means no normalized semantic relation, even when exact text is retained. REQUIRES_REVIEW applies to proposed interpretation/publication, not to the ability to retain a source string.

| Concept | Broad model status | Preserved subset / missing relation |
|---|---|---|
| Damage expressions/types | PARTIAL | Item property pairing and classifications retained; complete effect/formula/type/recipient association missing |
| Healing | MISSING | Dice text retained; healing amount/modifier/subject association not normalized |
| Temporary HP | MISSING | Content retained; separate temporary-HP effect model absent |
| Attack labels/modifiers | PARTIAL | Item modifiers, Class declarations and Object boundaries retained; complete attacks need context |
| Saving throws/DCs | PARTIAL | Save metadata/declarations retained; branching consequences not generally linked |
| Ability/skill checks | PARTIAL | Skill/check metadata retained; DC/contested-check outcomes usually prose |
| Advantage/disadvantage | MISSING | No general subject/roll/condition effect model; narrative retained |
| Condition classification/application | PARTIAL | Classifications retained; application/removal/negation relation absent |
| Defenses | PARTIAL | Exact stat/choice contracts retained; bestowed/conditional effect associations not universal |
| Armor Class/modifiers | PARTIAL | Existing fields complete; conditional modifier application not modeled |
| HP/hit dice/thresholds | PARTIAL | Authored values retained; no recovery/damage/advancement execution |
| Movement/speeds | PARTIAL | Numeric/flag/mode stats retained; prose restrictions/teleports not universal effects |
| Range/reach/area | PARTIAL | Spell geometry/Item range retained; prose effect area not universally typed |
| Duration/concentration | PARTIAL | Spell/Trap source variants retained; per-effect scope association incomplete |
| Targeting | PARTIAL | Creature-type classifications retained; counts/restrictions/alternatives often prose |
| Resource costs/uses/recharge | PARTIAL | Catalog-specific amount/schedule fields retained; pool/reset/trigger links incomplete |
| Action economy | PARTIAL | Timing/buckets/counts retained; textual costs and conditional action availability incomplete |
| Spellcasting | PARTIAL | Typed grants/usage/slots retained; mention-to-grant and effect interpretation not inferred |
| Scaling | PARTIAL | Source schedules/tables complete; prose axis/effect associations absent |
| Proficiency/expertise | PARTIAL | Direct grant/choice models complete; prose-derived grants/restrictions remain unmodeled |
| Prerequisites | PARTIAL | Structured alternatives/categories retained; descriptive conditions not evaluated |
| Choices | PARTIAL | Internal source groups retained; public subset/completeness and prose choices need review |
| Summoning/transformations | PARTIAL | Monster provenance/inline stats retained; full transformation/summoning effect absent |
| Multiattack/sequences | MISSING | Action text retained without typed sequence graph |
| Triggers/reactions | PARTIAL | Bucket/role/label structures retained; arbitrary event/effect linkage absent |
| Ongoing/conditional effects | MISSING | Narrative retained without general scope/condition relation |
| Rest recovery | PARTIAL | Explicit spell rest buckets retained; generalized resource recovery prose unmodeled |
| Exploration/environment | PARTIAL | Terrain/rating/light/capacity metadata retained; environmental effects need review |
| Vehicle/facility mechanics | PARTIAL | Components/capacity/orders/prerequisites retained; prose benefits/operations unmodeled |
| Economy/quantity/table mechanics | PARTIAL | Exact ranges/amount strings/order complete; expression meaning/generation not inferred |
| Ability statistics/changes/senses | PARTIAL | Direct scores/grants/ranges retained; conditional bonus application not universal |

No gameplay effect model is applicable to Recipe cooking instructions, book/adventure navigation or name-selection content. Deity/language labels likewise provide no new effect model. Model/publication coverage for arbitrary prose and proposed semantic associations remains REQUIRES_REVIEW.

## New support-model discrepancy: legendary-group bodies

The Step 6 source coverage matrix labels `monsters/legendarygroups.json` as implemented support. Static runtime inspection does not support that statement for group bodies:

- The source contains **188** group roots, **115** lairActions fields, **158** regionalEffects fields and **three** mythicEncounter fields. There are **16** copies and **21** version declarations.
- [MonsterCatalog](../../../src/main/java/com/thedndpub/data/services/MonsterCatalog.java) loads `classpath*:data/monsters/bestiary-*.json`.
- [MonsterTemplateRepository](../../../src/main/java/com/thedndpub/data/dte/monster/MonsterTemplateRepository.java) loads `template.json:monsterTemplate`; this is a different root and purpose.
- [MonsterIdentityDte](../../../src/main/java/com/thedndpub/data/dte/monster/MonsterIdentityDte.java) and normalized [Identity](../../../src/main/java/com/thedndpub/data/dto/monster/identity/Identity.java) retain only a legendary-group name/source reference. No runtime loader or typed body model for lairActions/regionalEffects/mythicEncounter was found across production Java.
- Reproduce the content evidence at `monsters/legendarygroups.json`, Aboleth MM, `$.legendaryGroup[0].lairActions` and `.regionalEffects`. The independent `supportCollections.legendaryGroup` audit records **281 dc**, **115 damage** and **131 dice** tags, including authored transformation content. Those are syntax counts, not lair-effect totals.

Model coverage is **MISSING for group bodies**, while the existing Monster group reference is **COMPLETE for its name/source contract**. This discrepancy is outside the 14,399/14,797 primary-definition denominator; it does not justify changing Monster IDs, declaring 188 new Monsters or integrating new resolver kinds. A separate preservation investigation should establish intended support scope, copy/version semantics, source validation and publication policy before code changes.

The single `legendaryGroupTemplate` support root likewise must not be assumed covered by MonsterTemplateRepository's different `monsterTemplate` loader.

## Public choice completeness deserves a bounded audit

[ClassFeatureContentNormalizer](../../../src/main/java/com/thedndpub/data/importer/classes/ClassFeatureContentNormalizer.java) maps every Class option into the internal graph. [ClassPublicMapper.mapChoiceNodes](../../../src/main/java/com/thedndpub/data/dto/classes/ClassPublicMapper.java) selects reference and nested-choice nodes and traverses sections; text/ability/statblock options are not added to public options.

Source example: Totem Spirit PHB, `classes/class-barbarian.json`, `$.subclassFeature[18].entries[2]`. Therefore the public `count` and options subset must not be treated as an exhaustive selectable universe. This assessment does not assert a numeric count of misleading responses or authorize publishing withheld options. Audit all 40 authored option nodes and copy-effective graphs before proposing a completeness indicator or other compatibility change.

## Lower-priority representation differences

- ClassFeatureSection retains hierarchy/name/text but not every DTE source/page/style/columns field. These are contextual/presentation losses, not proof that mechanical effects disappear. The import provenance remains available; future syntax locators should reference it directly.
- Feature-entry table metadata differs from progression metadata. Batch 1 did not publish feature-entry tables, and its correction must not be generalized into a new table-publication policy.
- Five StatusDte records are parsed and source-validated in ConditionCatalog, which retains only their count. They are not among the 36 primary domains; do not claim their bodies are normalized as Condition definitions.
- ItemPropertyDto and ItemMasteryDto are UID/note references. They intentionally do not duplicate the auxiliary property/mastery rule descriptions. Cross-reference expansion is a later reference task, not a reason to infer an effect.
- Vehicle top-level summary/detail AC/HP consistency remains the bounded earlier opportunity: eight raw roots/12 fields. It is unrelated to interpreting component speed prose.

No new production defect is inferred from synthetically supported but absent variants. For example, MonsterMapper's numeric-cell text conversion is not established as a current numeric Monster table defect: the scanned authored Monster cells have no NumericTableCellDte occurrences.
