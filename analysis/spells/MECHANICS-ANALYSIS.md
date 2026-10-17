# Structured mechanics analysis

The source encodes useful mechanic classifications directly, but usually not a complete executable spell effect. Preserve authored fields as data and do not build a rules/effects engine from the corpus.

| Property | Root occurrence count | Meaning and limit |
| --- | ---: | --- |
| `spellAttack` | 65 | Source attack-type codes; no attack resolution values. |
| `savingThrow` | 406 | Source ability/save labels; no DC formula is given here. |
| `abilityCheck` | 57 | Source ability-check labels. |
| `damageInflict` | 361 | Damage type tags, not damage amounts or dice. |
| `damageImmune` | 17 | Structured damage immunity references/tags. |
| `damageResist` | 30 | Structured resistance tags. |
| `damageVulnerable` | 2 | Structured vulnerability tags. |
| `conditionInflict` | 184 | Structured condition tags, not full condition application timing. |
| `conditionImmune` | 12 | Structured condition immunity tags. |
| `affectsCreatureType` | 77 | Affected creature-type selectors. |
| `miscTags`, `areaTags` | 721, 643 | Source classification/search tags; do not derive mechanics from these labels. |

There is no dedicated root damage-dice, healing amount, save-DC, attack-bonus, or resource-use schema. Numeric damage/healing and effect details often occur only in `entries` or `entriesHigherLevel`, which are recursive descriptive entry content and may contain markup. `scalingLevelDice` is a structured exception described in [SCALING-ANALYSIS.md](SCALING-ANALYSIS.md). Do not derive damage or healing values from prose, tag names, or spell names.

Summoning, transformations, resistance/immunity interactions, and many resource mechanics are described in entries rather than a complete structured effect representation. Preserve such entries internally for fidelity, but keep them outside public mechanical projections unless a separate publication and interpretation decision is made. No generalized effect model is warranted by the observed fields.
