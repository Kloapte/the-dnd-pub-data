# Extraction reliability

Parse reliability, mechanical meaning and publication eligibility are independent. No semantic precision/recall estimate is claimed: this assessment inspects representative cases but does not create an independently adjudicated gold set.

| Class | Permitted claim | Required evidence |
|---|---|---|
| A DIRECT | Authored field/value/explicit structural relation | Exact source path plus inspected DTE → normalized mapping |
| B SYNTAX | Exact expression, label, code, reference or argument boundary | Balanced syntax, exact UTF-16 offsets/token hash, complete variant accounting |
| C LOCAL_CONTEXT | A narrow reviewed relation within a constrained container/pattern | Adjudicated gold set, explicit negative cases, precision/recall and abstention analysis |
| D PROSE_INFERENCE | Candidate meaning requiring natural-language interpretation | Separate semantic validation strategy; no automatic authoritative normalization proposed |
| E UNSUPPORTED | Source does not establish the requested fact | Retain source/evidence; leave fact absent |

This refines the earlier EXACT_STRUCTURED/DETERMINISTIC_CONTEXTUAL/HEURISTIC/UNSUPPORTED vocabulary. DIRECT generally corresponds to EXACT_STRUCTURED. SYNTAX is exact only for recognition; it is not automatically DETERMINISTIC_CONTEXTUAL for an effect relationship. LOCAL_CONTEXT earns that stronger relationship claim only after validation.

## Candidate reliability and coverage

| Candidate | Exact audit scope | Parse / semantic claim | False positives, false negatives and review |
|---|---|---|---|
| Already structured fields and completed Batches 1–4 | Full affected sets in linked implementation reports | DIRECT / narrow source role | No re-extraction; provenance/null/choice contracts still matter |
| Class ability declarations | 29 nodes: 16 abilityDc, 13 abilityAttackMod | DIRECT / kind and ordered attributes only | No inferred base number, proficiency arithmetic or character value; already normalized |
| Class option groups | 40 nodes on 40 authored owners | DIRECT / option boundary and authored optional count | A public subset of referenced options is not the full group; inspect effective graphs separately |
| Spell formula observations | 930 damage/dice/scaledamage/scaledice tags across **422 of 969** authored spells | SYNTAX / expression only | A dice formula may be healing, quantity or example. Higher-level clause need not identify a complete scaling effect |
| Monster syntax | All 4,560 authored roots; per-tag and per-context counts in evidence | SYNTAX / owner category and labels only | Recharge in a feature name is outside EntryDte. Copy transforms and generated owners require separate effective counts |
| Object attack containers | Seven nodes/seven roots | DIRECT for attack/hit section role; LOCAL_CONTEXT for any effect association | Multiple formulas/riders/branches prevent arbitrary pairing. Review all seven, not one convenient attack |
| Roll cells | 160 cells on nine definition owners: 131 Item cells, 29 Spell cells | DIRECT / exact range and optional entry association | Do not reinterpret padding, max=0, gaps or ordering; content remains protected/unreviewed |
| Trap stage containers | 14 group nodes across four roots | DIRECT / active/dynamic/constant/countermeasure role | Role is not structured damage, disarm check or reset event; the body still needs interpretation |
| Reward inline Object statblocks | Three nodes/three roots | DIRECT / embedded object stats only | Not separate Object IDs or guaranteed summoned/owned objects; effect linking remains prose |
| Item charge/recharge expressions | Earlier confirmed 220 tagged raw roots | SYNTAX; DIRECT owner-field role | A charge amount differs from reset timing/consumption; arbitrary expression grammar must be audited separately |
| Treasure amount expressions | Earlier confirmed 62 coin fields/20 definitions | SYNTAX after separate formula grammar audit; DIRECT denomination role | Not a roll, award or materialized Item list; no inferred common dice system |
| Monster legendary-group bodies | 188 support roots | DIRECT for fields/containers; SYNTAX for tags | Runtime body model absent; a syntax parser cannot compensate for missing source ingestion |
| Condition/save/trigger outcome assembly | Representative cases only | LOCAL_CONTEXT or PROSE_INFERENCE | Negation, removal, immunity, conditional/optional branches, repeated effects and cross-string scope require a gold set |
| Healing/temporary HP associations | Healing Word PHB, Cure Wounds XPHB, False Life PHB samples | PROSE_INFERENCE for complete effect | Formula alone omits symbolic modifiers, subject, duration or application count |
| Multiattack graph | Earlier heading predicate: 2,221 raw Monster owners / 2,223 headings | PROSE_INFERENCE | Attack-name coincidence is not an edge; replacements, alternatives, counts and prerequisites vary |
| Facility/upgrade/Variant Rule complete effects | 70/31/231 definition review scopes | PROSE_INFERENCE | Execution, order production and campaign overrides are not encoded as complete fields |
| Recipe/name/navigation gameplay | No applicable gameplay extraction target | UNSUPPORTED | Numerical/conditional content is a deliberate negative control; product boundaries remain decided |

Coverage figures from previous batches remain historical verified figures and are explicitly identified. New scanner figures are authored source counts. No effective annotation or semantic-effect count has been manufactured.

## Required semantic gold-set design

For a LOCAL_CONTEXT pilot, select by exact catalog/field/container and partition by source edition, syntax dialect and structural exception. Include examples, negation, optional choices, immunity/removal, multiple formulas, secondary damage, successful saves without half damage, multiple saves, delayed/recurring effects, alternate targets, named actions, tables and copy-derived text.

Two reviewers should label the proposed narrow relationship independently and adjudicate disagreements. Record rule/pattern ID, source evidence, extracted relation, unknown fields and abstention reason. Hold out whole owners/source families rather than duplicate inherited paragraphs across train/review splits. Report precision, recall, coverage and abstentions per relation. No acceptance percentage is invented by this assessment; product owners must choose it for the exact future use.

## Scanner precision and limits

`scan-entries.mjs` parses all JSON and uses Java record components plus explicitly reviewed union dispatch. It does not deserialize Jackson objects or execute copy/template/version resolution. Custom modification wrappers can hide typed entry payloads from static binding; all raw entry-shaped discriminators are therefore audited independently with an exception ledger.

Lexical counts are only candidates and remain UNCERTAIN unless a direct structure, explicit syntax fact or reviewed example establishes the narrow concept. They are never effect counts. Probe ABSENT means no observed evidence under these probes, not a semantic proof about every sentence.

Offsets are UTF-16 string positions after JSON unescaping. Token hashes identify exact source syntax without copying paragraphs into evidence. Node hashes/locators are snapshot evidence, not persistent IDs or legal clearance.
