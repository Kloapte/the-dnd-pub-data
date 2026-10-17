# Vehicles and Vehicle Upgrades Reference Inventory

## Structured and markup evidence

The roots contain no structured UID/reference properties and no explicit link from an upgrade to a vehicle. One
vehicle contains `reprintedAs` provenance (identity lineage, not a related vehicle definition); two contain
`otherSources` publication citations, validated against the Source Registry.

The typed `entries` trees contain markup tags. Counts are source markup occurrences, not definition relationships:

| Markup tag | Count | Treatment |
|---|---:|---|
| `dc` | 35 | Check/DC notation retained in prose |
| `skill` | 2 | Extracted and sent to existing Skill adapter |
| `damage` | 84 | Damage notation retained in prose |
| `hit` | 63 | Attack notation retained in prose |
| `h` | 63 | Formatting marker retained in prose |
| `condition` | 31 | Condition mentions retained in prose; no condition adapter is used for this tag |
| `action` | 1 | Action mention retained in prose |
| `variantrule` | 6 | Variant-rule mention retained in prose |
| `atk` | 57 | Attack notation retained in prose |
| `object` | 7 | Object markup retained in prose; no target link inferred |
| `m` | 2 | Formatting marker retained in prose |
| `creature` | 4 | Creature markup retained in prose; no Monster target inferred |
| `table` | 2 | Formatting marker retained in prose |
| `atkr` | 3 | Attack notation retained in prose |
| `feat` | 4 | Feat markup retained in prose; no unsupported adapter added |
| `quickref` | 2 | Quick-reference markup retained in prose |
| `status` | 2 | Status markup retained in prose |
| `recharge` | 1 | Recharge notation retained in prose |
| `dice` | 5 | Dice notation retained in prose |
| `spell` | 2 | Extracted and sent to existing Spell adapter |
| `adventure` | 1 | Adventure markup retained in prose |

Only the 2 Spell and 2 Skill tokens form the four new resolver occurrences. The exact outcomes are:

| Target kind | RESOLVED | AMBIGUOUS | UNRESOLVED | DEFERRED | Total |
|---|---:|---:|---:|---:|---:|
| SPELL | 0 | 1 | 0 | 1 | 2 |
| SKILL | 1 | 1 | 0 | 0 | 2 |
| **Total** | **1** | **2** | **0** | **1** | **4** |

The resolved Skill token is source qualified and authoritative; only that occurrence exposes its exact stable `targetId`.
The unqualified references remain ambiguous or deferred and never expose a target ID. Original tokens, source
qualification, owner IDs, field locators, and semantic role are retained in the shared immutable resolution snapshot.

## Unsupported and non-reference forms

The existing resolver has adapters for the supported Spell and Skill targets used here. The source also includes Object,
Creature, Feat, Action, Condition/Status, Variant Rule, and Adventure tags, but the snapshot has no suitable
authoritative adapter for each of these encounter contexts. Their raw authored tokens remain embedded in internal typed
entry text. Ordinary names in prose are not promoted to relationships. No speculative resolver target kind or
vehicle/upgrade compatibility edge was added.

## Public reference projection

Detail responses may contain lightweight supported references with field locator, target kind, semantic role, authored
token/name/source, and target ID only for the exact resolved match. They omit resolution outcome, candidates,
diagnostics, full target definitions, and descriptive entry text. Vehicle Upgrade references are absent from this
corpus.
