# Feat `_versions` analysis

## Records and counts

The source contains 305 root feat records. Two root records declare versions:

| Base record | Declarations | Source | Variant names |
| --- | ---: | --- | --- |
| Magic Initiate (XPHB) | 3 | XPHB | Magic Initiate; Cleric, Magic Initiate; Druid, Magic Initiate; Wizard |
| Strike of the Giants (BGG) | 6 | BGG | Strike of the Giants; Cloud, Fire, Frost, Hill, Stone, Storm |

The 9 declarations are not extra root JSON records. They are partial definitions which inherit from their parent and apply source overrides. After expansion, the catalog contains 305 roots plus 9 derived variants: **314 normalized definitions**.

## Expansion behavior

`FeatVersionExpander` emits each parent root unchanged, then each variant in source declaration order. The variant inherits the parent's typed properties, uses the declaration's name and source, applies entry modifications in order, and replaces `additionalSpells` when a version provides that field. Each normalized variant is independently indexed and has a source-aware ID. `FeatDefinition.variant` and `parentId` distinguish it from a root. `FeatExpansionProvenance` retains the parent source record, declaration, and version index internally.

All nine IDs are available through `GET /api/feats/{id}` and their names are searchable through `GET /api/feats/by-name/{name}?source=`. Example: `feat:xphb:magic-initiate-cleric`. The root Magic Initiate and Strike of the Giants definitions remain separately available with their original properties.

## Mechanics and source overrides

Magic Initiate has six `replaceArr` instructions, two for each variant. They replace the named `Two Cantrips` and `Level 1 Spell` entries. Each declaration also provides one class-specific `additionalSpells` grant, replacing the parent's three alternatives with Cleric, Druid, or Wizard spell grant data.

Strike of the Giants has six index-based `replaceArr` instructions on entry zero and six `removeArr` instructions by name. Each result retains the common replaced introduction plus exactly one named strike option. The strike's damage, save, and condition instructions remain entry prose; no structured effects are inferred.

The only supported operations are:

- `replaceArr` by a named entry.
- `replaceArr` by an array index.
- `removeArr` by a list of entry names.

The expander rejects unknown modes, missing/ambiguous targets, unsupported item payload shapes, invalid indices, and absent names. Operations run in declaration order. No other fields are overridden by these declarations apart from name/source and Magic Initiate's `additionalSpells`; all omitted fields are inherited.

## Public spell-grant projection

`FeatDefinition` stores typed normalized spell grants. `FeatDto` projects them as `FeatAdditionalSpellDto` with a typed ability choice and a recursive tagged grant-value structure for text/query values, arrays, keyed objects, counts, and flags. This preserves class/level selectors and variant-specific values. The corpus spell-grant values contain no inline 5eTools markup. Public DTOs do not include entries, raw prose, `_mod` instructions, version declarations, parent IDs, or expansion provenance. `FeatSummaryDto` remains concise.

## Monster infrastructure

The Monster resolver's `replaceArr` and `removeArr` behavior informed the Feat operation semantics. `MonsterVersionExpander` is not reused: it depends on Monster copy/template resolution, monster-specific identity and provenance, and Monster source-field cleanup. No Monster code was changed.

## Evidence and implementation

- Source DTE versions: `src/main/java/com/thedndpub/data/dte/feat/FeatDte.java`.
- Feat-specific operations: `src/main/java/com/thedndpub/data/dte/feat/FeatVersionExpander.java`.
- Expanded catalog indexing and root/variant counts: `src/main/java/com/thedndpub/data/services/FeatCatalog.java`.
- Variant provenance: `src/main/java/com/thedndpub/data/dto/feat/FeatImportRecord.java` and `FeatExpansionProvenance.java`.
- Public spell-grant projection: `src/main/java/com/thedndpub/data/dto/feat/FeatDto.java` and typed spell DTOs.
- Version behavior tests: `src/test/java/com/thedndpub/data/feat/FeatPipelineTests.java`.

The final full `mvn test -q` run passed **111 tests, with 0 failures and 0 errors** (the stated baseline was 107). It includes checks that each generated variant is independently retrievable by ID and exact name/source, that its class-specific spell grant is present in the public DTO, and that public responses omit entries, markup, and modification metadata.
