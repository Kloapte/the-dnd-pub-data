# Step 5 — Public Cross-Catalog Reference Integration

## DTO/API audit and decision

The existing detail DTOs already preserve their source-specific owning structures: Class and Subclass spellcasting/equipment/choices, Background proficiency and equipment packages, Race/Subrace grant groups, and Feat/Optional Feature prerequisite and progression data. Class feature relationships already use the domain-owned `ClassReferenceResolver` IDs. Background feat references and item equipment also have older catalog-local projections, but those do not expose a common locator and resolution contract.

Step 5 adds an optional `crossCatalogReferences` list to detail DTOs only. Each entry identifies the stable owner field locator and carries the target kind, semantic role, original token/name/source, and a catalog-owned `targetId` only when the startup snapshot says `RESOLVED`. Existing owner fields stay in place. Summary DTOs, routes, list filters, and same-domain feature IDs are unchanged. The owner-level locator avoids replacing source-specific nested choice structures and allows repeated occurrences to remain distinguishable.

The additive public record is `PublicCrossCatalogReferenceDto`:

| Property | Meaning |
|---|---|
| `fieldLocator` | Stable source-structure locator emitted by the B1/B2 extractor. |
| `targetKind` | Actual resolved definition kind when resolved; authored target category otherwise. Includes selector/group kinds. |
| `role` | `GRANT`, `CHOICE`, `PREREQUISITE`, `FILTER`, `GROUP`, or `REFERENCE`. |
| `authoredToken` | Original reference token or retained selector expression. |
| `authoredName` | Parsed name where available, otherwise the authored token. |
| `authoredSource` | Original source code when present. |
| `targetId` | Present only for an authoritative exact resolution. |

No outcome enum, candidate count/list, reason, severity, resolver internals, or target DTO is exposed. A missing `targetId` is intentional and is not itself an error. This keeps ambiguity and missing-target diagnostics internal while preserving the source identity for clients.

## Production components

- `PublicReferenceProjection` builds one immutable owner index from `CrossCatalogResolutionCoordinator.snapshot()` at startup. It does not query target catalogs or parse UIDs on request.
- `PublicCrossCatalogReferenceDto`, `PublicReferenceRole`, and `PublicReferenceTargetKind` form the public projection vocabulary.
- Existing detail services attach references by owner catalog and stable owner ID. The projection reuses the snapshot's deterministic order and preserves every repeated occurrence/locator.
- All public DTOs copy the nested reference list and use `@JsonInclude(NON_EMPTY)`. No descriptions, markup, provenance, candidate data, or diagnostics are copied.

The Spring dependency direction remains acyclic: the coordinator is created from immutable catalog snapshots; the projection depends on its snapshot; query services depend on the projection. Source normalizers and target catalogs do not depend on cross-catalog public projection.

## Public coverage and corpus counts

All **3,875** snapshot occurrences are available from their existing owner detail response. These totals count occurrences, not unique authored tokens. No public diagnostic endpoint was added.

| Detail owner category | Occurrences | Per target kind |
|---|---:|---|
| Background | 1,225 | FEAT 90; ITEM 726; LANGUAGE 83; SKILL 326 |
| Class | 471 | FEAT 18; ITEM 165; ITEM_GROUP 3; OPTIONAL_FEATURE 9; SKILL 256; SPELL 20 |
| Subclass | 1,254 | FEAT 1; OPTIONAL_FEATURE 16; SPELL 1,237 |
| Class Feature | 79 | FEAT 2; OPTIONAL_FEATURE 77 |
| Subclass Feature | 84 | OPTIONAL_FEATURE 84 |
| Feat | 155 | FEAT 45; LANGUAGE 8; OPTIONAL_FEATURE 17; PROFICIENCY 4; SKILL 81 |
| Optional Feature | 10 | FEAT 1; OPTIONAL_FEATURE 7; SKILL 2 |
| Race | 302 | FEAT 2; LANGUAGE 155; SKILL 145 |
| Subrace | 54 | FEAT 1; LANGUAGE 27; PROFICIENCY 6; SKILL 20 |
| Item (including Base Item records) | 241 | BASE_ITEM 241 |
| **Total** | **3,875** | |

Aggregate target-kind totals are SPELL 1,257; ITEM 891; BASE_ITEM 241; ITEM_GROUP 3; FEAT 160; OPTIONAL_FEATURE 210; SKILL 830; LANGUAGE 273; and PROFICIENCY 10. These values are independently checked against every occurrence in the immutable snapshot. B1's authored ClassReferenceResolver graph remains outside these counts: its 2,339 authored and 2,725 effective same-domain feature edges are still represented by their established IDs.

The public projection preserves the resolver distribution exactly: **1,697 RESOLVED**, **1,108 AMBIGUOUS**, **30 UNRESOLVED**, and **1,040 DEFERRED**. Only the resolved rows have `targetId`; the other three states are represented by their authored identity, locator, and semantic role without exposing the internal status or candidate list.

Base Items use the existing `/api/items/{id}` detail response and identify themselves with `definitionKind: BASE_ITEM`; no second endpoint was introduced. Class/Subclass and both feature catalogs use their existing detail endpoints. Race and Subrace use `/api/races/{id}`. Backgrounds, Feats, Optional Features, and Items use their existing detail routes.

## Example response fragments from the corpus

Resolved Background equipment reference (`background:aag:astral-drifter`):

```json
{
  "fieldLocator": "startingEquipment[0].options[_][0].item",
  "targetKind": "ITEM",
  "role": "CHOICE",
  "authoredToken": "traveler's clothes|phb",
  "authoredName": "traveler's clothes",
  "authoredSource": "phb",
  "targetId": "item:phb:traveler-s-clothes"
}
```

Ambiguous Background skill reference (`insight`, with PHB and XPHB candidates):

```json
{
  "fieldLocator": "skillProficiencies[0].insight",
  "targetKind": "SKILL",
  "role": "GRANT",
  "authoredToken": "insight",
  "authoredName": "insight"
}
```

The skill reference intentionally has no `targetId` and no public candidate list. An unresolved source-qualified item UID such as `holy symbol|xphb` is likewise retained without a target ID. Class item-group selectors remain `ITEM_GROUP`/`GROUP` entries, not fabricated concrete Items. Choice candidates, prerequisite links, and filters keep their role and locator; they are never turned into unconditional grants.

## Compatibility and intentional boundaries

The change is additive in JSON: existing field names, values, types, routes, stable IDs, and summary responses remain. New detail responses may include `crossCatalogReferences`; existing API consumers can ignore the optional property. The pre-existing Background `feats` and `startingEquipment` projections remain intact. The new locator-bearing list complements them with common authoritative resolution semantics rather than replacing their source-specific shape.

Some target categories currently have no exact source-qualified resolutions in these owner fields. In particular, current Skill/Language references are unqualified and remain without target IDs. The two unqualified `branding smite` references remain candidate-only; the resolver never promotes a unique current candidate to an identity. Existing 8 unresolved Background item occurrences and three deferred class item groups remain represented without invented definitions.

The current resolver snapshot does not include Monster spellcasting, Magic Variant applicability, Action/Condition markup references, or general owner references from catalogs that were not selected by B1/B2. Those owner APIs are intentionally unchanged. The `PROFICIENCY` target is a combined selector category, not a Skill or Language target. Spell-list filters remain expressions. Class/subclass feature graph edges and domain-owned reprints are not duplicated.

## Tests and verification

`PublicReferenceIntegrationTests` verifies the full 3,875-occurrence snapshot and unchanged outcome distribution, matches all projected target IDs to the startup result, confirms unresolved/ambiguous/deferred references have no `targetId`, checks choice/prerequisite/filter/group roles, exercises detail services across every supported owner category, serializes the public reference DTO without diagnostics/candidates/markup, checks immutable projection lists, and exercises an existing Background detail route. Existing catalog/controller tests cover the unchanged routes and response contracts. Full Maven verification passes **285 tests, 0 failures, 0 errors, and 0 skips** (279-test baseline plus six Step 5 tests).
