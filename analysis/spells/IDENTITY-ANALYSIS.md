# Spell identity and edition analysis

## Identity evidence

All 969 roots have a source and name. There are **zero duplicate `(name, source)` identities** and zero collisions for the proposed readable ID pattern `spell:{lowercase-source}:{normalized-name}`. The source component must remain in the ID: **386 names** occur under more than one source, including **359 names** with both PHB and XPHB roots. Examples include Acid Splash, Aid, Alarm, and Animal Friendship. These are distinct source-authored definitions, not duplicate records to merge.

Recommended identity is a stable ID derived from the exact source code and normalized name, while retaining original `name` and `source` fields unchanged. Normalize source case for the ID key only; do not rewrite the source identity. The complete collision group list is in `discovery-summary.json` and is reproducible with the discovery script.

`alias` occurs on two roots and is an alternate name, not an identity override. Both PHB and XPHB Fire Bolt declare the alias `Firebolt`; alias lookup therefore has a real cross-edition ambiguity. `reprintedAs` carries explicit target UIDs; there are 389 authored reprint references and every target resolves to exactly one spell root by exact name/source. `otherSources` and `additionalSources` are source/page provenance references, not additional definitions. Spell roots contain no parent field, copy declaration, or version expansion; no spell parent/child identity hierarchy is supported by this corpus.

## Edition behavior

PHB contains 361 roots and XPHB contains 391. The 359 same-name PHB/XPHB pairs must remain separate and can carry distinct levels, school, casting time, range, duration, components, and descriptive entry structures. Cross-edition continuity is represented by explicit `reprintedAs` edges where authored; it is not a license to merge IDs or fill missing fields from the other edition.

## Collision outcomes

| Check | Result |
| --- | ---: |
| Duplicate source-aware name/source identities | 0 |
| Readable ID collisions | 0 |
| Repeated names across sources | 386 groups |
| PHB/XPHB same-name pairs | 359 |
| Duplicate alias value across PHB/XPHB | `Firebolt` on both Fire Bolt definitions |
| Reprint references | 389, all exact targets resolved |

Do not add edition defaults based only on familiar spell names. A Stage B identity record should preserve primary source, additional/other source references, reprint edges, and any future copy/version provenance as separate relationships.
