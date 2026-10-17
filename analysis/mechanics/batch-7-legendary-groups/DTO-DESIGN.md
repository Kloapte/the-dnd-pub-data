# Batch 7 evidence and internal design checkpoint

This checkpoint precedes production implementation. The requested scope explicitly authorizes
continuation after establishing source semantics. Original assessment evidence remains unchanged.

## Verified source and runtime contract

`data/monsters/legendarygroups.json` contains `_meta.internalCopies=["legendaryGroup"]` and
188 `legendaryGroup` roots. The 115 `lairActions`, 158 `regionalEffects`, and three
`mythicEncounter` counts are overlapping **authored field-presence counts**, not independent
definitions or effective body counts. Five roots have none of these authored bodies and rely on
copies. Exactly 21 roots have `_versions`, each with one concrete declaration; 16 have `_copy`.
No root combines copy and version declarations. All 188 name/source pairs are distinct.

The complete path/type/null/empty inventory and source distribution are in `source-audit.json`,
reproduced by `node analysis/mechanics/batch-7-legendary-groups/audit-source.mjs`.
Observed entry forms: strings, entries, list, item, insetReadaloud, and table. Tables have string
cells. No null or empty source values occur. Root fields are name, source, page, additionalSources,
lairActions, regionalEffects, mythicEncounter, _copy, and _versions. Body-level source qualifiers
are retained. All discovered source codes are already registered.

`MonsterCatalog` loads only `bestiary-*.json`, resolves Monster copies/versions, and maps them via
`MonsterMapper`. `MonsterIdentityDte.legendaryGroup` and normalized `Identity.legendaryGroup`
retain a name/source reference. Neither loads the group body. `MonsterTemplateRepository` reads
`template.json:monsterTemplate`; it skips the separate `legendaryGroupTemplate` root. Existing
public output contains the group reference only. There is no existing body projection to extend.

## Observed transformation dialect

Five Hag copies use appendArr on lairActions and regionalEffects; eleven CoA copies use prependArr
on lairActions and directly author their own regionalEffects. Each operation has an ordered
`items` array of shared EntryDte nodes. A child-authored body replaces that body; otherwise the
parent body is inherited, and then the declared append/prepend operation is applied. Every copied
root has its own name/source/page. No metadata inheritance assumption is necessary for this corpus.

All 21 concrete versions specify name, source and one `_templates` reference to Shadow Dragon|FTD.
`template.json:legendaryGroupTemplate[0]` contains name, source, page, ref, and apply._mod, with
appendArr on lairActions/regionalEffects. The inherited dragon bodies are followed by these exact
template entries. Two parent dragons have no lairActions; appending creates that body. No semantic
replacement of damage types or names is authored as an operation and none is inferred from prose.
The literal template `ref` is retained, not resolved through the seven-kind resolver.

The Monster resolver is deliberately not reused: it validates Monster-specific fields and
performs Monster transformations. A narrow support resolver will implement only this audited
body-array dialect, reject unknown fields/modes, missing parents/templates, cycles and duplicate
identities, and preserve the declarations alongside effective support bodies. No generic template
engine is required. Expanded support count is expected to be 188 + 21 = 209, separately from
the unchanged 4,851 Monster definitions.

## Internal representation

- Typed source records for group roots, copies, body modifications, append/prepend operations,
  versions, template references, template applications, source occurrences and collection metadata.
- `LegendaryGroupId` uses exact authored name and shared SourceIdentity. Its readable support key
  is `legendary-group:<source>:<name>`. This is an internal support identity, not a Monster ID.
  Templates occupy a separate index. Duplicate identities fail startup rather than selecting one.
- `LegendaryGroupDefinition` holds SourceReference, additional sources, three optional ordered
  bodies and explicit provenance. Absent body lists remain absent; authored empty lists remain empty.
- Each effective top-level body entry carries a deeply immutable shared EntryDte plus its original
  SyntaxSourceOwner and JSON Pointer. Inherited entries keep their original parent locations;
  appended/prepended entries point to their copy instruction or template payload. Nothing is
  relabeled as authored by a copied/versioned owner.
- A source record retains the entire typed declaration, owner/hash, and exact SyntaxText leaves
  produced by Batch 5's AuthoredSyntaxTextWalker. Original text hierarchy/UTF-16 spans can be
  traversed by the shared parser without generating Monster annotations. Effective body locations
  reference these original inputs; no fabricated effective text spans are introduced.
- Copy lineage, version parent, declaration pointer and applied template identities remain
  explicit. Version source pages/additionalSources are absent because versions do not author them;
  parent/template publication locators remain available through their original records/provenance.

## Runtime and publication boundary

A dedicated immutable startup support catalog loads two classpath resources once and validates
all primary/additional/body/copy/version/template source codes against SourceRegistry. It links
only existing exact Monster group name/source references, in a separate internal association
index. Raw source has 240 references, including one version reference; all have exact candidates
in the planned support index. Effective associations must be measured independently. Missing
exact matches are explicitly unresolved; ambiguous identities fail catalog construction.

No public DTO, route, Monster field, Monster resolver, cross-catalog target kind or reference
occurrence changes. Body prose, table contents, ref markup and provenance remain internal.
This preserves authored support content without interpreting lair actions, regional effects or
mythic rules and supplies no new publication authorization.

## Acceptance evidence

Full typed source/path coverage; all 16 copies/21 versions/one template; exact body ordering and
original locations; strict negative cases; nested immutability; authoritative associations with
no name-only fallback; syntax pointer/span reconstruction; packaged startup; full Maven suite;
unchanged 175 source hashes and all pre-existing src bytes; 6,246 resolver outcome regression.
