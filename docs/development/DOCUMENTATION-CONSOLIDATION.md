# 7.2.4 — Repository documentation consolidation

Maintenance milestone, 18 October 2026. Step 7.2 is complete; Step 7.3 remains unstarted.
The [backend roadmap](../THE_DND_PUB_BACKEND_ROADMAP.md) remains the single progress/scope authority.
This report records repository maintenance, not a mechanics design or implementation batch.

## 1. Inventory and method

The inventory was captured before reorganization, including pre-existing uncommitted 7.2 work.
Existing staged/unstaged implementation changes were preserved rather than attributed to this task.

| Pre-consolidation inventory | Count |
| --- | ---: |
| Artifacts classified | 626 |
| Analysis files | 430 |
| Markdown files, including ignored local HELP | 239 |
| JSON files, including source resources | 305 |
| Actual JSON Schemas (`*.schema.json`) | 3 |
| Scripts, Java probes/helpers and utilities | 73 |
| Dependency edges | 15,355 |
| Markdown paths named in fingerprint evidence | 231 |

These categories overlap; schema-summary exports are not themselves JSON Schemas. The six primary
classification counts sum to 626:

| Classification | Count | Treatment |
| --- | ---: | --- |
| AUTHORITATIVE | 3 | Agent guidance, roadmap and approved detailed mechanics design |
| ACTIVE_REFERENCE | 17 | Current implementation/source references and repository guidance/configuration |
| RUNTIME_DEPENDENCY | 195 | Classpath/build/test inputs, including analysis files consumed by Java tests |
| REPRODUCIBILITY | 186 | Evidence, schemas, manifests, fingerprints, scripts and probes |
| HISTORICAL | 224 | Completed reports, indexed without relocating their pinned dependencies |
| REDUNDANT | 1 | Ignored local Spring-generated HELP scaffold; retained, not committed or deleted |

Machine-readable records:

- [Original inventory](documentation-consolidation/inventory-before.json): every current path,
  classification, SHA-256, proposed action/path, justification and dependency consumers.
- [Migration plan](documentation-consolidation/migration-plan.json): final action for every original
  artifact, exact-original archive mappings and an explicit empty deleted-file list.
- [Original dependency graph](documentation-consolidation/dependencies-before.json) and
  [current dependency graph](documentation-consolidation/dependencies-current.json).
- [Original links](documentation-consolidation/links-before.json) and
  [current links](documentation-consolidation/links-current.json).
- [Current inventory](documentation-consolidation/inventory-current.json).
- [Final verification summary](documentation-consolidation/verification.json).
- [Protected input hashes](documentation-consolidation/protected-inputs.json): 2,172 original
  non-Markdown/source inputs, including all production/test Java, source resources, runtime approvals,
  original scripts, evidence, schemas and fixtures.

The audit parses JSON metadata, scans Java/test/tool/configuration paths, resolves literal directory
plus filename references, and records conservative dynamic directory scopes. Maven's standard
resource layout is unchanged. No CI configuration exists under `.github/` in this checkout.
Reference discovery is static; it does not claim to execute every historical generator or infer all
runtime-generated filenames. Uncertain/dynamic dependencies were retained.
Dependency graphs group target paths by consumer and reference kind, avoiding repeated source-path
overhead while retaining every counted edge.

## 2. Authoritative hierarchy and actual organization

```text
README.md                         project entry point
AGENTS.md                         agent conventions and precedence
docs/
  THE_DND_PUB_BACKEND_ROADMAP.md    sole progress and future-scope authority
  README.md                       current documentation index
  architecture/
    DATA-PIPELINE.md
    MECHANICS-MODEL.md
  api/
    SPELL-INSPECTION.md
  development/
    WORKFLOW.md
    DOCUMENTATION-CONSOLIDATION.md
    documentation-consolidation/  inventories and maintenance validators
analysis/
  README.md                       active versus historical entry point
  active/mechanics/README.md       current design/implementation references
  archive/
    README.md
    HISTORICAL-ARTIFACTS.md         exhaustive retained-report list
    catalogs/feats/                one exact-original snapshot
    development/                   one exact-original snapshot
  artifacts/README.md              evidence/manifests/schema/tool index
  ...                             original dependency-sensitive families retained
```

The current navigation guides cover architecture/normalization, source identity/provenance,
mechanics model, independent review/rights semantics and the actual Spell inspection contract.
They link the existing implementation and approved 7.2.1 design rather than replacing them with a
new data model. No duplicate `docs/ROADMAP.md` or empty benchmark/extraction directories were created.

AGENTS links the roadmap, documentation/analysis indexes and this safety report. Earlier analysis
implementation orders and the old Public Mechanics roadmap remain historical plans; they no longer
compete for current sequencing. The roadmap was shortened by replacing its speculative cleanup tree
and procedure with links to the completed structure and maintenance evidence. Step 7.3's numbering,
scope and planned status are unchanged.

## 3. Moves, corrections and deletions

| Action | Result and reason |
| --- | --- |
| Git relocation | **1 case-only rename:** `Agents.md` → `AGENTS.md`, through a temporary name using `git mv`. This fixes case-sensitive agent discovery and existing uppercase references. |
| Historical report relocations | **0.** All 224 HISTORICAL reports are fingerprint-referenced; preserve their original layout and index them instead. |
| Original snapshots added | **2**, exact bytes of the original agent guidance and Feat source inventory, before edits. |
| Broken link correction | **1:** the Feat inventory's `nnormalized-field-inventory.json` target now correctly names the existing normalized inventory. No source result/count was changed. |
| Runtime/reproducibility relocations | **0.** No non-Markdown source, gold set, schema, manifest, script or runtime resource moved. |
| Files deleted | **0.** No safely disposable tracked evidence was established. |

The [archive index](../../analysis/archive/README.md) links the two verbatim `.md.txt` snapshots.
The [complete historical list](../../analysis/archive/HISTORICAL-ARTIFACTS.md) explicitly lists all
224 retained reports: **159 catalogs/normalization, 48 mechanics, 13 references, 4 publication**.
Of these, 223 remain byte-identical; the live Feat inventory changes only the one-character link typo.

No original fingerprint map was rewritten. Agent guidance additions and the Feat link correction
intentionally change those live Markdown hashes; archived originals retain their previous hashes.
For a historical reconstruction, use the manifest's original-path/archive-path mapping in a separate
workspace. Do not restore old instructions over the current root guidance. A strict historical
whole-workspace verifier still requires its own historical code revision, not today's implementation.

The sole REDUNDANT classification is an already-ignored local HELP file generated for an older
Spring setup. It contains no unique project design, but is an owner-local file excluded by the existing
`.gitignore`; it was retained. Generated JSON inventories often look repetitive but are not proven
redundant: Java tests and fingerprint/provenance records consume them.

## 4. Runtime and reproducibility preservation

Examples of direct test dependencies retained at their original paths include:

- Monster/Spell normalized field inventories.
- Mechanics corpus evidence used by syntax corpus tests.
- Object relationship gold set and membership verification output.
- Item charge review cases.
- Spell formula assessment runtime evidence/gold labels.
- P1C candidate manifest and excluded-candidate data used by publication tests.

The [artifact index](../../analysis/artifacts/README.md) links the runtime publication inputs,
approval schemas, evidence crosswalks, journal, fixtures, source hashes and verification scripts.
The exhaustive inventories identify all other retained assets and consumers.

No publication decision, approved source identity, revision ID or resource fingerprint was renamed.
P1D resources remain under their original classpath location; analysis fixtures remain repository
inputs rather than being copied into the JAR. Source and test code were not edited in this milestone.

## 5. Public repository hygiene findings

The [redacted pattern report](documentation-consolidation/hygiene-current.json) scans all **2,239
currently tracked text files**, plus nonignored working additions. It excludes its own audit directory
to avoid self-matching detector patterns/outputs. No matched values, credential contents, personal
directory components or network addresses are exported. This is a current-tree review, not a Git
history audit, credential-validity check or legal clearance.

| Finding | Evidence and disposition |
| --- | --- |
| Credential-looking configuration | `src/main/resources/application.properties`, one active assignment and one commented example. Default-looking database settings require owner review; their live validity/use was not tested. No values are reproduced and no configuration was changed. |
| Wrapper matches | Three credential-pattern hits in `mvnw` are environment-variable handling, not embedded credential values. Retain the wrapper. |
| Personal paths | Five locations in five existing files: three tracked historical reports/exports and two already-existing uncommitted implementation reports. Do not republish their values in this report; future sanitization must account for frozen evidence/hashes. |
| Local development endpoints | 21 loopback references in 17 existing files. They are context for local build/smoke tooling, not evidence of a secret. No non-loopback private-network literal was detected by the bounded pattern scan. |
| Source distribution scope | All 175 tracked source JSON files, plus 30 analysis JSON files containing derived source markup, need a separate distribution/rights decision for the public repository. Root application licensing and the zero-approval P1D state do not establish blanket dataset clearance. |
| High-confidence secret patterns | No private-key marker, token-shaped value or email-shaped metadata detected by the declared patterns. This does not prove absence of every secret or personal identifier. |

No files were automatically deleted, no Git history rewritten, no credentials rotated and no licensing
notice changed. Existing copyright/author attribution remains intact and is not treated as accidental
secret data. If the owner determines a credential was real, exposure/history review and remediation
must be a separately authorized task. Source/evidence rights decisions also remain separate.

## 6. Verification

Final checks cover current relative Markdown links/case/heading anchors, known dependency targets,
protected bytes, pinned source hashes, runtime publication state and existing behavior. External
URLs are counted but not fetched or claimed valid. Verbatim archival `.md.txt` snapshots are plain
text evidence, not live Markdown pages; their historical link syntax is intentionally not rebased.

- One actual pre-existing broken relative link was corrected. Four apparent directory-link failures
  in the first detector draft were scanner errors, fixed before the recorded baseline; they were not
  document changes. The final validator supports directories as well as files.
- Preservation verifies **2,172 protected inputs**, **622 untouched original artifacts**, both exact
  original snapshots and all **15,355 original dependency edges**. Four original Markdown artifacts
  are intentionally updated: agent guidance, root README, roadmap and the Feat link.
- All **175** pinned source hashes remain unchanged. Existing runtime/evidence/configuration paths,
  production code, tests and prior reports other than the declared link correction are preserved.
- The complete Maven build and packaged HTTP verification confirm the existing **653 tests**,
  **969 Spell identities**, **930 formula observations**, **6,246 resolver outcomes**, unchanged
  default and opt-in API semantics, and the **547 NONE_OBSERVED / 422 WITHHELD** distribution.
- P1D remains **250 pending candidates, zero authorized observations**; the original exporter runs
  with `--check` and no production decision artifact is introduced.
- Relative link validation, deterministic output comparison, artifact reference validation,
  whitespace checks and `git diff --check` pass.

Commands, run from the repository root:

```powershell
node docs/development/documentation-consolidation/build-history-index.mjs
node docs/development/documentation-consolidation/audit-repository.mjs --verify
node docs/development/documentation-consolidation/verify-preservation.mjs
node docs/development/documentation-consolidation/audit-hygiene.mjs
node analysis/mechanics/batch-11b-item-charge-syntax/verify-source-hashes.mjs
node analysis/mechanics/public-v1/spells/p1d/export-runtime.mjs --check
mvn package
& ./analysis/mechanics/step-7/7.2.3/verify-packaged.ps1 -JavaExecutable <installed-java>
git diff --check
```

The supplied packaged script requires a real installed Java path for that placeholder. Its historical
machine-specific default was preserved, not silently replaced. All process output stays in ignored
`target/`. Machine-readable maintenance outputs contain no timestamp/CWD and are deterministic for
the same tree; current inventories exclude their own changing output files to avoid self-hashing.
The frozen baseline and preservation manifest describe this milestone, not a mandate to prevent
future authorized Java changes.

## 7. Deferred decisions and stop boundary

- Wholesale physical relocation would require coordinated changes to consumers and historical
  fingerprint reconstruction. Keep the indexed archive unless a later migration justifies that cost.
- Review public dataset/evidence redistribution, default-looking configuration and historical
  personal paths separately; retain uncertain assets meanwhile.
- No report merge, approval promotion, database work, Keycloak, frontend change or source upgrade
  was performed. Existing implementation work present at task start remains untouched.
- The next development scope is still the separately authorized Step 7.3. This maintenance report
  does not start Shield extraction, independent benchmark authoring or any mechanics implementation.
