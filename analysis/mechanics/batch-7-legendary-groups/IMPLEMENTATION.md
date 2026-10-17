# Step 7 Batch 7: legendary-group support preservation

Implemented after the [source/runtime audit](SOURCE-AUDIT.md) and
[internal design checkpoint](DTO-DESIGN.md). The new dedicated startup support catalog preserves
authored lair, regional and mythic bodies, resolves the observed copy/template dialect, and indexes
exact existing Monster associations. It exposes no public API.

## Source coverage and runtime results

| Measure | Result |
|---|---:|
| Authored support roots | 188 |
| Authored lair / regional / mythic fields | 115 / 158 / 3 |
| Copy declarations / concrete versions / dedicated templates | 16 / 21 / 1 |
| Copy-effective roots before version expansion | 188 |
| Expanded support groups | 209 |
| Effective lair / regional / mythic bodies | 152 / 184 / 3 |
| Effective ordered top-level body entries | 1,005 |
| Direct authored / effective Monster references | 240 / 307 |
| Associated distinct groups / unassociated retained groups | 182 / 27 |
| Effective associations to generated support variants | 1 |
| Missing / ambiguous support candidates | 0 / 0 |
| Existing effective Monster definitions | 4,851, unchanged |
| Existing full-catalog effective definitions | 14,797, unchanged |

Support groups are not new Monster definitions or members of the 36 primary catalog domains.
All 32 applicable source codes validate against SourceRegistry without modification. Source
distribution, overlapping field counts and absent structures are documented in SOURCE-AUDIT.
FIELD-COVERAGE contains all 105 group and 35 template path/type combinations.

## Java implementation

- `dte/legendarygroup/`: strict typed source roots, metadata, source occurrences, copies, versions,
  template declarations/applications, explicit append/prepend modes and immutable array payloads.
- `LegendaryGroupEntryCopies`: deep immutable snapshots of the six observed shared EntryDte forms
  (text plus five object discriminators), preserving nested lists, named blocks and string-cell tables.
  Unsupported future entry/cell variants fail explicitly rather than being flattened.
- `dto/monster/support/`: `LegendaryGroupId`, `LegendaryGroupDefinition`, `LegendaryGroupBodyEntry`,
  provenance, authored group/template inputs, and support-local association types.
- `LegendaryGroupSourceLoader`: reads two classpath resources once, strictly checks resource/root
  metadata and known fields, validates all authored sources, rejects scalar coercions/duplicates/
  trailing JSON/explicit nulls, and retains original source SHA-256 and SyntaxText leaves.
- `LegendaryGroupResolver`: resolves parent graphs and the observed append/prepend operations;
  expands only explicitly declared versions using the dedicated group template index.
- `LegendaryGroupSupportCatalog`: immutable sorted definition and association maps. It depends
  on the existing MonsterCatalog; the existing Monster pipeline does not depend on this support
  component. No circular dependency, mutable cache or request-time parsing/resolution is introduced.

Exact support keys use `legendary-group:<source>:<name>`, preserving authored case and labels.
Both authored and expanded identity sets are unique. Templates have a separate identity index.
Same names across source codes stay distinct. No inferred source, slug repair or array-index
persistence identity is needed.

## Copy/version and evidence decisions

Child-authored bodies replace inherited bodies, including an explicitly empty body. Missing body
lists inherit from the parent; declared append/prepend payloads then apply in their authored order.
Only the two observed array operation modes and the two observed modification targets are supported.
Unknown fields/modes, missing parents/templates, cycles and duplicate authored/generated/template
identities fail. No pinned transformation is unsupported.

Each effective top-level body entry retains its original `SyntaxSourceOwner` and JSON Pointer.
Hag/CoA copy additions point into their `_copy._mod` payloads; inherited entries keep their parent
locations; version additions point into `legendaryGroupTemplate[0].apply._mod`. Nested content is
preserved as typed entries. Copy lineage, version parent, version-declaration pointer and template
identities remain distinct. No child/effective owner receives fabricated authored text origins.

Pages/additional sources on root declarations remain their own provenance. Versions have no
authored page/additionalSources fields, so those values remain absent; their parent/template
publication locators remain accessible through provenance and original records. The catalog does
not claim a separate renderer-compatible metadata-inheritance contract for those absent fields.

Batch 5's AuthoredSyntaxTextWalker supplies exact original strings, hierarchy/type labels and
origin flags. BalancedMarkupParser successfully traverses all **1,081** source tags, including
25 transform-context tags, and reconstructs UTF-16 spans. Shared syntax classification is not
materialized as Monster annotations here. No effective tag denominator or effect coverage is claimed.

## Public/reference compatibility

The existing Monster name/source group reference remains unchanged. Exact support associations
are stored separately and never replace the Monster DTO's reference or embed support prose.
Missing exact candidates remain unresolved; duplicate support identities prevent ambiguous matches
from reaching runtime. No matching by an unqualified name or by inferred dragon kind occurs.

No public DTO, endpoint, existing normalized definition, Monster copy/version resolver or
cross-catalog projection changed. All 6,246 shared resolver occurrences retain their prior results:
2,517 RESOLVED / 2,054 AMBIGUOUS / 181 UNRESOLVED / 1,494 DEFERRED.
New resolver occurrences/target kinds: zero.

Lair/regional/mythic prose, tables, raw markup, template references and evidence remain internal.
This preservation work supplies no new publication authorization. It creates no damage/save/
timing/reset/effect engine and performs no rule interpretation.

## Tests and verification

Fourteen new focused tests cover complete source/body counts, all 16 copies/21 versions, nested
source forms, exact original entry reconstruction and file hashes, original syntax pointers/spans,
exact Monster associations, missing-source negative controls, nested immutability, strict unknown/
unsupported shapes, duplicate templates/versions, cycles/missing parents, nested copy provenance,
empty-versus-absent bodies, meaningful page zero, and deterministic ordering.

- Full Maven package suite: **484 tests passed**, zero failures/errors/skips (470 baseline + 14).
- Existing normalization/public API/reference regression suites pass unchanged.
- All **1,712 pre-existing src files**, including all **175 JSON resources**, retain their bytes.
- Original entry-assessment artifacts retain their bytes; original scanner evidence is unchanged.
- Isolated packaged-archive probe loads the new classes and resources directly from BOOT-INF,
  starts the application and verifies 209 support groups / 307 associations. No target/classes or
  source-resource fallback is present.
- Executable `java -jar` smoke also passes with existing Item/Magic Variant/public compatibility checks.
- Deterministic audit reruns, new-file whitespace/readability checks and `git diff --check` pass.

Reproduction:

```powershell
node analysis/mechanics/batch-7-legendary-groups/audit-source.mjs
mvn -Dmaven.repo.local=C:\Users\Erik\.m2\repository package
javac -encoding UTF-8 -d target/batch-7-probe-classes analysis/mechanics/batch-7-legendary-groups/LegendaryGroupJarProbe.java
java -cp target/batch-7-probe-classes LegendaryGroupJarProbe target/dnd-pub-data-0.0.1-SNAPSHOT.jar
& ./analysis/mechanics/batch-2-item-focus/verify-packaged.ps1
node analysis/mechanics/batch-7-legendary-groups/verify.mjs
git diff --check
```

`source-audit.json`, `pre-implementation-hashes.json` and `verification.json` retain reproducible
counts/hash evidence. The lexical scanner does not replace the production balanced parser or
infer mechanical meaning.

## Remaining limitations

The resolver intentionally supports the audited body-array dialect, not arbitrary Monster copy
operations. Explicit nulls are absent from the pinned support inputs and rejected rather than
collapsed into absence. Future modifications require a fresh schema/source-semantic assessment.
Support is now preserved independently; no lair/regional/mythic data is automatically attached as
Monster annotations, and no public body representation is approved. Separate Monster syntax work
can now consume original support locations after its own authored/effective provenance design.
