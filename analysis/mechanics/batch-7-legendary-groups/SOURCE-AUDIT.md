# Legendary-group source and runtime audit

Pinned input: 5eTools v2.37.0. Reproduce with `node analysis/mechanics/batch-7-legendary-groups/audit-source.mjs`.
Exact resource hashes, all 140 path/type combinations, concrete example pointers, identity/copy/version
ledgers and Monster-reference locations are retained in `source-audit.json`. This report contains
metadata and counts, not copied rule paragraphs.

## Denominators and overlap

| Scope | Count | Meaning |
|---|---:|---|
| Authored group roots | 188 | `legendarygroups.json:legendaryGroup[]` |
| Authored lairActions fields | 115 | Present before copy/template expansion |
| Authored regionalEffects fields | 158 | Present before copy/template expansion |
| Authored mythicEncounter fields | 3 | Arasta, Hythonia, Tromokratis; MOT |
| Copy declarations | 16 | Five Hag append copies; eleven CoA prepend copies |
| Version-bearing roots / declarations | 21 / 21 | One concrete version per root |
| Dedicated group templates | 1 | Shadow Dragon FTD in `template.json:legendaryGroupTemplate[0]` |
| Copies resolved, before versions | 188 | Existing roots with effective body arrays |
| Expanded internal support groups | 209 | Includes the 21 declared variants |
| Effective Monster definitions | 4,851 | Unchanged, a separate domain denominator |

Body overlap: 24 roots have only lairActions; 89 have lairActions plus regionalEffects; two have
all three bodies; one has only mythicEncounter; 67 have only regionalEffects; five have no authored
body and inherit through a copy. These totals reconcile to 188, 115, 158 and three respectively.
No group combines `_copy` and `_versions`. No duplicate authored or generated name/source identity
exists. Exact name/source keys distinguish editions and the two Sapphire Dragon versions (FTD/SADS).

## Sources

All 32 primary/additional/nested/copy/version/template source codes already occur in SourceRegistry.
No registry change is needed. Primary authored distribution:

```text
ABH 1, AI 1, AUD 1, AitFR-DN 1, BAM 2, BGG 6, BMT 5, CM 4,
CRCotN 4, CoA 13, CoS 1, EGW 1, ERLW 2, FRAiF 3, FTD 10, IMR 1,
JttRC 1, LR 1, MM 23, MOT 3, MPMM 18, MPP 3, MTF 13, MaBJoV 5,
PotA 8, QftIS 4, RHW 16, SADS 1, SCC 1, VGM 8, WDMM 4, XMM 23.
```

149 roots author integer pages; ten author additionalSources arrays, each containing one source
without a page. Entry blocks also author source qualifiers (ten lair-action blocks and nine regional
blocks). These sources remain separate from the primary publication reference. No null/empty value,
reprint field, media field, structured damage/save/timing field or dynamic-key structure occurs.
See [FIELD-COVERAGE.md](FIELD-COVERAGE.md) for every observed path and type.

## Copy and template dialect

Five VGM/MM Hags copy Hag|VGM and append entries to both bodies. Eleven CoA groups copy Archdevil|CoA,
prepend lair entries and independently author regional bodies. The only operation modes are appendArr
and prependArr, always with EntryDte arrays. There are 21 copy operations and two template operations.
Copy parents exist and have no unresolved/cyclic relationships in the pinned input.

All 21 versions declare name/source and a single Shadow Dragon|FTD template. The template contains
name/source/page/ref/apply._mod. It appends one lair entry and two regional entries. Lunar Dragon and
Solar Dragon parents have no lairActions; their variants receive the template's authored lair block.
Body descriptions may discuss discretionary rules, but no source operation rewrites a damage type
or calculates an effect. The implementation applies the declared array edits only.

Entries comprise text and five object discriminators. Including instructions and the template,
there are 310 list, 450 item, 36 entries, three insetReadaloud and nine table nodes. The nine tables
have 47 rows/94 string cells; ranges and prose remain exact strings and ordering is unchanged.

## Existing runtime path and subsequent integration

Before Batch 7, `MonsterCatalog` read only `bestiary-*.json`, then used
`DefaultMonsterCopyResolver`, `MonsterVersionExpander` and `MonsterMapper`. A Monster's
`MonsterIdentityDte.legendaryGroup` became normalized/public `Identity.legendaryGroup`, holding
only name/source. `legendarygroups.json` was never loaded by that path. Similarly,
`MonsterTemplateRepository` reads `monsterTemplate`, not `legendaryGroupTemplate`.

The runtime follow-up audit additionally verified that Monster template 48 (Shadow Dragon FTD)
contains `apply._root.legendaryGroup={name:"Shadow Dragon",source:"FTD"}`. Six Monster copy payloads
also contain `_preserve.legendaryGroup=true`; their exact locations are in `monsterSupportInstructions`.
These existing instructions concern the Monster's reference, not the support body's array edits.
The Monster pipeline stays authoritative for its effective reference. Batch 7 does not replace
that generic Shadow Dragon association with a dragon-specific variant by interpreting names.

Direct authored Monster references: 240, including one explicit group-version reference. Current
effective Monster references: 307, all resolved by exact name/source to 182 distinct support groups;
one targets a generated support version. The remaining 27 support groups are retained despite having
no effective Monster association. Zero missing/ambiguous candidates occur. Duplicate support identities
fail initialization, preventing arbitrary ambiguity resolution.

## Syntax boundaries

There are 1,079 lexical tags on authored group roots, including 24 in transformation payloads.
The dedicated template adds two tags, one in its transformation payload. Batch 5's balanced parser
independently parses all 1,081 and reconstructs every span. These are authored/support counts, not
effective annotations; inherited text is not recounted as new source content.

The analysis scanner parses JSON before finding tag openings; its lexical counts make no claims
about effects or tag semantics. The production catalog retains exact SyntaxText inputs and original
entry pointers so the existing shared parser can traverse them. It creates no Monster annotations.
