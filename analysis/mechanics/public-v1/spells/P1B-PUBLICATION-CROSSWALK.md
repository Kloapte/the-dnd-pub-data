# P1B — Spell formula publication crosswalk

## 1. Decision and evidence boundary

**Analysis only; no release authorization.** All **708** bounded candidate occurrences on **402**
source-aware Spell owners have an occurrence-level review record. The conservative matching
procedure identifies **250 VERIFIED_LICENSED_MATCH observations on 195 owners**. This is evidence
for a potential limited release, not approval to publish it. **Zero new observations are authorized.**
The current P1A distribution remains **547 NONE_OBSERVED / 422 WITHHELD**.

The other 458 candidates remain ambiguous or lack verifiable evidence. This assessment does not
decide whether a number or formula is independently copyrightable, give a general license to a
source book, or certify the rights of existing APIs. A verified expression match does not license
the source Spell's complete description, original proper name, arrangement, media or markup.

Read this alongside the approved [Spell assessment](ASSESSMENT.md),
[P1A implementation](P1A-IMPLEMENTATION.md), and existing
[publication boundary](../../entry-assessment/PUBLICATION-BOUNDARIES.md).

## 2. Primary documents and licensing basis

Primary sources were retrieved/reviewed on **2026-10-18**. Exact PDF fingerprints, legal-notice
extraction fingerprints and URLs are in [official-documents.json](p1b/official-documents.json).
Downloads come directly from Wizards/D&D Beyond; no community mirror supplies licensing evidence.
PDFs and full extracted text remain under ignored `target/p1b-official/`, not in this assessment.

| Document | Version-specific primary source | Evidence and scope |
|---|---|---|
| SRD 5.1, CC release | [Official PDF](https://media.dndbeyond.com/compendium-images/srd/5.1/SRD_CC_v5.1.pdf), legal information, physical PDF page 1 | 403 PDF pages; notice grants use of that document under CC BY 4.0. [Official January 27, 2023 announcement](https://www.dndbeyond.com/posts/1439-ogl-1-0a-creative-commons) establishes the CC release; the underlying rules predate that announcement. OGL is an alternative, not the route selected here |
| SRD 5.2.0, document titled 5.2 | [Official PDF](https://media.dndbeyond.com/compendium-images/srd/5.2/SRD_CC_v5.2.pdf), page 1 | 361 PDF pages; CC BY 4.0 notice specific to SRD 5.2. The [official download page](https://www.dndbeyond.com/srd) lists April 22, 2025 |
| SRD 5.2.1 | [Official PDF](https://media.dndbeyond.com/compendium-images/srd/5.2/SRD_CC_v5.2.1.pdf), page 1 | 364 PDF pages; separate version-specific CC BY 4.0 notice. The official page's publication label is May 01, 2025; this records that label, not a reconstructed release history |
| CC BY 4.0 | [Creative Commons legal code](https://creativecommons.org/licenses/by/4.0/legalcode.en) | Sections 2 and 3 establish scope and conditions, including attribution, modifications and rights not granted. Neither extraction confidence nor an application MIT license substitutes for these terms |
| Current D&D Beyond Basic Rules | [Official Creator FAQ](https://www.dndbeyond.com/creator-faq), Basic Rules question | The FAQ distinguishes Basic Rules from the CC SRD and says the Basic Rules cannot be used in content creation. Free access is not the SRD license |
| Earlier Basic Rules | [Official 2018 PDF](https://media.wizards.com/2018/dnd/downloads/DnD_BasicRules_2018.pdf), credits/notice page 1 | Version 1.0, November 2018. Its first-page notice reserves reproduction/unauthorized-use permission; a complete text scan identified no CC/OGL grant. No candidate receives permission from a `basicRules` flag or this free-access document; overlap is verified against the actual CC SRD instead |
| Conversion guide | [Official guide](https://media.dndbeyond.com/compendium-images/srd/guide/converting-to-srd-5.2.1.pdf), page 11 | Distinguishes new, renamed and revised spells, including Feeblemind/Befuddlement and Branding Smite/Shining Smite. It is supporting version context, not the licensed formula source used here |

The SRD landing page states it was last updated March 02, 2026 and separately links both 5.2
revisions and 5.1. Its FAQ distinguishes the older/newer rules and explains renamed content.
There is no inference that PHB is SRD 5.1, XPHB is SRD 5.2, or every supplemental spell belongs to
either license. Those books retain their own source identity; only separately matched expressions
from the reviewed licensed documents supply a possible publication basis.

## 3. Source identity, edition and existing implementation trace

The existing source-to-runtime evidence remains authoritative for what was imported:

- `runtime-evidence.json`: 969 Spell identities, 930 exact formula observations and resource hashes.
- `coverage.json`: the existing 708-observation/402-owner bounded candidate rule.
- `SpellFormulaAnnotationAdapter` and `SpellFormulaAnnotationIndex`: original authored owners,
  occurrence indexes, source resources, pointers and UTF-16 spans; no new markup parser is added.
- [SourceRegistry](../../../../src/main/java/com/thedndpub/data/services/SourceRegistry.java),
  `SourceRecord` and `SourceMetadata`: source identity, aliases and optional
  publication metadata. These contain no expression-level publication authorization.
- [SpellSourceRelationships](../../../../src/main/java/com/thedndpub/data/dto/spell/SpellSourceRelationships.java):
  `additionalSources`, `otherSources`, `referenceSources` and
  `reprintedAs` retain different provenance meanings. A reprint is not inheritance or permission.

The Book corpus identifies PHB as *Player's Handbook (2014)* and XPHB as *Player's Handbook (2024)*.
The matching procedure uses PHB→5.1 and XPHB→5.2 as **version comparison routes**, then requires
matching level, school, exact expression and a unique literal contextual anchor. These routes
alone never verify a candidate. Supplemental sources retain their exact authored codes, including
AU; no edition is invented for them and no automatic reprint-to-SRD authorization is performed.

Every candidate is directly authored in this pin. The original audit found no Spell copy/version/
template declarations. No inherited or effective-origin formula observations are manufactured.

## 4. Exhaustive candidate reconciliation

[occurrence-crosswalk.json](p1b/occurrence-crosswalk.json) contains **708 unique occurrence records**,
keyed by existing Spell ID plus its original formula occurrence index. All 402 owners join the
original evidence. These counts are exhaustive for the approved bounded subset, not all Spell
mechanics and not all 930 formula tags.

Eligibility is reproduced exactly from the prior assessment: `damage` or `dice`, exactly one
argument, and the existing classifier's `DICE_NOTATION`, `SIGNED_INTEGER` or `UNSIGNED_INTEGER`
lexical kind. No scaling, opaque expression or additional display argument is admitted by P1B.
The scanner consumes the existing parsed observations; its PDF expression lookup is literal
matching, not another 5eTools grammar or effect extractor.

Each record retains:

- authoritative Spell ID, exact source code, resource and SHA-256;
- owner pointer, formula occurrence index, original JSON Pointer, section and hierarchy;
- authored text SHA-256 and UTF-16 `[start,end)` span, validated against the original string;
- exact tag/token, arguments, expression and expression/token fingerprints;
- notation and technical eligibility/reliability, independently of rights evidence;
- each examined SRD document, candidate entry, descriptor, expression matches and edition route;
- PDF page/column, normalized document/entry offsets and contextual evidence fingerprints;
- review classification, reason, possible attribution IDs and **NOT_GRANTED** release authorization.

| Source code | Candidate observations | Unique candidate owners | Verified | Ambiguous | No verifiable evidence |
|---|---:|---:|---:|---:|---:|
| AI | 2 | 1 | 0 | 0 | 2 |
| AU | 32 | 17 | 0 | 0 | 32 |
| BMT | 2 | 2 | 0 | 0 | 2 |
| EGW | 18 | 10 | 0 | 0 | 18 |
| FRHoF | 19 | 16 | 0 | 0 | 19 |
| FTD | 4 | 4 | 0 | 0 | 4 |
| IDRotF | 2 | 1 | 0 | 0 | 2 |
| PHB | 204 | 123 | 130 | 46 | 28 |
| SCC | 2 | 2 | 0 | 0 | 2 |
| TCE | 36 | 9 | 0 | 0 | 36 |
| XGE | 110 | 61 | 0 | 6 | 104 |
| XPHB | 277 | 156 | 120 | 95 | 62 |
| **Total** | **708** | **402** | **250** | **147** | **311** |

## 5. Exact expression and version matching method

The reproducible procedure is [build-crosswalk.mjs](p1b/build-crosswalk.mjs), using the isolated
analysis helper [ExtractOfficialSrd.java](p1b/ExtractOfficialSrd.java).

1. Download official PDFs, require their recorded SHA-256 values, and extract each page's left
   and right columns separately with pinned PDFBox 3.0.4. Preserve physical PDF page numbering
   (one-based) and column identity. Legal notices are extracted independently from page 1.
2. Identify spell headings only when followed by an explicit school/level descriptor. The complete
   extracted rosters contain **319** entries in 5.1 and **339** in each 5.2 revision, with no duplicate
   names. [srd-spell-rosters.json](p1b/srd-spell-rosters.json) preserves this inventory and boundaries.
3. Use the authored spell name and any string-valued `srd`/`srd52` rename hints to locate possible
   entries. Hints are search keys only: false/missing flags do not exclude a match, and true/string
   flags do not verify one. The located entry's own published descriptor/content must match.
4. Within the exact entry boundary, locate the literal expression with alphanumeric boundaries,
   allowing only whitespace variation between its authored pieces. Dice counts/operators are not
   evaluated, simplified or substituted. The original expression/token remains byte-exact in the
   crosswalk, regardless of comparison normalization.
5. Compare literal word suffix/prefix anchors immediately before/after each expression. Retain up
   to ten words per side and stop source flanks at existing markup boundaries. A qualifying anchor
   requires at least two equal words on each side and ten in total; exactly one expression location
   in that entry must qualify. Level and school must also agree on the corresponding edition route.
6. Record all weaker/other-edition matches for review. A bare expression or name match is never
   promoted to verified evidence. No save outcome, healing role, damage association or effect is
   inferred from the matched prose.

The anchor rule is a deliberately conservative retrieval criterion, **not a calibrated semantic
accuracy threshold or legal test**. It makes every positive literal alignment inspectable rather
than granting rights from a keyword. The evidence can support a release decision only after
review; it does not replace that decision. Non-matches can be false negatives.

Comparison normalization handles NFKC, whitespace, quotation marks, selected PDF hyphen/control
characters and case for lexical anchors. It does not reconstruct words broken across PDF lines,
render source tags, repair OCR, alter mathematical values or infer equivalent expressions. PDF
column boundaries, layout and tag boundaries can therefore leave valid content ambiguous.
The 20 candidate formulas in table rows/column labels remain subject to the same rule; table
membership or a licensed table title alone never verifies a result expression.

PDF evidence offsets are explicitly **UTF-16 indexes into the deterministically normalized
extracted text**, not byte offsets into a PDF and not authored JSON offsets. Original JSON spans
remain independently exact. Page/column, document fingerprint and extraction algorithm make the
licensed evidence reproducible without storing complete rule paragraphs in this repository.

### Representative reviewed correspondences and abstentions

These examples explain the exhaustive records; they are not an independently adjudicated gold set.

| Existing owner / occurrence | Expression | Primary evidence / result |
|---|---|---|
| `spell:phb:fireball#formula-0` | `8d6` | 5.1, Fireball, PDF p144 right; verified exact expression plus literal anchor |
| `spell:xphb:fireball#formula-0` | `8d6` | 5.2.0 and 5.2.1, Fireball, p131 right; verified independently of the PHB observation |
| `spell:phb:magic-missile#formula-0` | `1d4 + 1` | 5.1, Magic Missile, p161 right; verified expression, no projectile-count/target inference |
| `spell:phb:healing-word#formula-0` | `1d4` | 5.1, Healing Word, p153 right; verified formula text, not a HealingEffect |
| `spell:xphb:healing-word#formula-0` | `2d4` | 5.2 revisions, p139 right; expression exists but short tag-bounded anchors and a repeated token fail the conservative rule. Ambiguous, not evidence of prohibition |
| `spell:phb:bigby-s-hand#formula-0` | `4d8` | 5.1, renamed Arcane Hand, p118 right; published name, header and local expression evidence verified |
| `spell:xphb:bigby-s-hand#formula-0` | `5d8` | 5.2 revisions, Arcane Hand, p110 left; changed formula verified separately, never inherited from PHB |
| `spell:phb:melf-s-acid-arrow#formula-1` | `2d4` | 5.1, Acid Arrow, p114 left; rename hint corroborated by matching descriptor and local expression |
| `spell:xge:vitriolic-sphere#formula-0` | `10d4` | 5.2 revisions, Vitriolic Sphere, p172 left; exact anchored expression exists, but this older supplemental occurrence's source/version correspondence is not established. Ambiguous |
| `spell:phb:acid-splash#formula-2` | `2d6` | 5.1 p114 left; expression exists, but short progression syntax fails the anchor rule. No scaling relation is invented |
| `spell:xphb:flaming-sphere#formula-0` | `2d6` | Source school `V`; 5.2 spell-description header on p132 says Conjuration, whereas its class lists on pp44,68,80 say Evocation. SRD-internal inconsistency; retain ambiguity, do not declare a source defect or silently override school |
| `spell:ai:jim-s-magic-missile#formula-0` | `2d4` | No corresponding reviewed SRD entry. Similarity to Magic Missile is not publication evidence |

All **120** verified XPHB occurrences independently satisfy the rule in **both** 5.2.0 and 5.2.1.
No verified candidate depends solely on the superseded revision. A future release can prefer
5.2.1 as its evidence/attribution version, while retaining the comparison with 5.2.0. There are
370 document-occurrence match links (130 + 120 + 120), but only **250 distinct candidate occurrences**.
No verified observations within one source owner reuse the same licensed formula position.
Fifteen verified observations use renamed entry headings; each passes the same content/header
checks, rather than receiving permission from its rename hint.

## 6. Publication review classifications

| Classification | Occurrences | Unique owners | Interpretation |
|---|---:|---:|---|
| `VERIFIED_LICENSED_MATCH` | 250 | 195 | Versioned CC source, matching expression, header and unique local literal anchor identified; still no release authorization |
| `VERIFIED_NOT_COVERED` | 0 | 0 | No candidate has sufficient positive negative-scope evidence for this classification under the defined corresponding-entry comparison |
| `AMBIGUOUS_MATCH` | 147 | 69 | Plausible counterpart, uncertain occurrence/version/header alignment |
| `NO_VERIFIABLE_EVIDENCE` | 311 | 168 | No counterpart identified in the examined SRD rosters; no alternate authoritative rights basis established |
| `REQUIRES_SEPARATE_PERMISSION` | 0 | 0 | No bespoke permission requirement is positively established by this review; absence of a verified CC match is not itself proof of such a requirement |

Owners can have occurrences in more than one category; owner columns must not be added. There
are **237 distinct owners with at least one unverified candidate**, some also among the 195 with
verified candidates. All candidate occurrences belong to exactly one review category.

The 147 ambiguous occurrences comprise **129 insufficient literal anchors**, **17 unestablished
source/edition correspondences**, and **one header mismatch**. The reason code and all found
expression locations remain visible per occurrence. No match is silently approved by a source flag.

Zero `VERIFIED_NOT_COVERED` does not mean all 708 are covered. Zero separate-permission findings
does not mean all 708 may be used without permission. Failure to find a counterpart, particularly
in a supplemental source, is insufficient to make either broad legal assertion. Publishing those
unresolved expressions requires new authoritative evidence or an independently reviewed basis.

## 7. Attribution and release obligations

[attribution-review.json](p1b/attribution-review.json) preserves the exact requested attribution
statements from the first pages of all three SRDs, identified by document-specific notice IDs.
The 5.2.0 notice names **SRD 5.2**, not an invented “5.2.0” attribution title. A future release
based on 5.1 and 5.2.1 should use those documents' statements. Reviewing 5.2.0 does not by itself
require its notice when none of its material is used as a separate publication basis.

For every verified occurrence, `verifiedMatches`, `attributionIds` and the document manifest
identify the applicable document, license and first-page notice. Before release:

1. Use the publisher's requested attribution and preserve applicable supplied notices, including
   license/disclaimer references, as required by CC BY 4.0 Section 3(a)(1)(A).
2. Link the actual licensed material and CC BY 4.0 legal code in a reasonably practicable manner.
   The PDF statements themselves specify SRD and license URLs; keep those intact.
3. Indicate extraction/selection/reformatting and applicable prior modifications under Section
   3(a)(1)(B-C). Removing surrounding prose/markup must not imply verbatim reproduction of a whole
   rule or an authoritative interpretation. The JSON file proposes a change notice for review.
4. Respect the PDFs' request not to add other attribution to Wizards beyond their statement.
   Avoid endorsement claims. CC BY 4.0 does not grant trademark/patent rights; its disclaimer and
   downstream restrictions clauses remain relevant. Mechanical extractability changes none of this.
5. Keep application-code MIT licensing distinct from the data's attribution/license notices.

Proposed placement: a versioned `THIRD-PARTY-NOTICES` document bundled with release artifacts,
linked prominently from API documentation, with a discoverable reviewed notice link for API
consumers (for example, a response `Link` header). The allowlist links each occurrence to its
notice ID. These are design proposals only: no notice, header or endpoint is implemented here.
The publication owner must approve that the chosen delivery satisfies attribution in the API's
actual medium and downstream use. Per-occurrence internal pointers/hashes need not be exposed.

## 8. Proposed future occurrence allowlist

The crosswalk is **not** a runtime allowlist. A future reviewed release manifest should contain
only explicitly authorized entries and bind at least these independent dimensions:

| Dimension | Required binding |
|---|---|
| Schema and corpus | Allowlist schema version; pinned 5eTools version; approved release identifier |
| Authored identity | Existing `SpellId`, exact source code, resource path, owner pointer and occurrence index |
| Source integrity | Resource SHA-256, original text SHA-256, JSON Pointer, UTF-16 span and exact token SHA-256 |
| Exact permitted value | Literal expression, expression SHA-256 and permitted notation/argument shape; no display/scaling/prose expansion |
| Licensed evidence | Selected document ID/version/URL/SHA-256; spell heading and PDF page/column; matched expression and anchor fingerprint |
| Review evidence | Crosswalk revision/hash; `VERIFIED_LICENSED_MATCH` classification and rationale; any additional adjudication |
| Attribution | Selected version-specific notice ID and approved release notice artifact/hash |
| Authorization | Named publication decision owner, explicit decision/date/scope; no default approval and no inference from extraction confidence |

For example, the PHB Fireball occurrence would point to the crosswalk's exact
`spell:phb:fireball#formula-0` record and SRD 5.1 evidence. It is not an approved entry until the
separate authorization field is actually granted. A schema or a matching hash is not authorization.

Future startup validation should require every binding to match exactly. Missing, duplicate,
unknown, moved or changed occurrences fail closed: no payload for that occurrence, with an
explicit startup failure or documented unavailability policy. Never fall back to name, source
flag, identical formula elsewhere or an adjacent occurrence. Preserve original occurrence indexes;
do not compact/reidentify them after filtering. Release manifests are immutable and auditable.

A new pinned source revision, changed resource fingerprint, entry movement, changed expression,
new copy/version transform, new licensed-document revision, or broader payload field requires
renewed review and an explicit replacement manifest. CC license irrevocability is distinct from
the application's decision to require new integrity review. Old manifests cannot automatically
authorize changed content. Extraction confidence and rights/authorization remain separate fields.

## 9. Potential release coverage and status effects

The defensible **potential** first increment is at most **250 observations on 195 owners**, after
an explicit reviewed allowlist and attribution decision. Prefer SRD 5.1 for the 130 PHB matches
and SRD 5.2.1 for the 120 XPHB matches. A narrower human-approved subset is also valid.

| Detail status | Current production | Hypothetical: all 250 verified matches separately approved and published |
|---|---:|---:|
| `NONE_OBSERVED` | 547 | 547 |
| `WITHHELD` | 422 | 227 |
| `PARTIAL` | 0 | 113 |
| `COMPLETE` | 0 | 82 |

The hypothetical calculation uses **all 930 original formula observations** as the completeness
denominator, not only the 708 candidates. `COMPLETE` still means indexed formula observations
are represented by the approved contract, not that a Spell's full mechanics are published.
Scaling/opaque/display-argument observations outside the candidate subset remain withheld.
Actual status changes in this task: **zero**. Actual authorized expression payloads: **zero**.

## 10. Verification and reproduction

New analysis artifacts are confined to this document and `p1b/`:

- `official-documents.json`, `attribution-review.json`: primary sources, exact fingerprints and
  version-specific notice requirements.
- `ExtractOfficialSrd.java`, `fetch-and-extract.ps1`: isolated document acquisition/extraction.
- `build-crosswalk.mjs`: exhaustive source/span and document matching, with no production integration.
- `occurrence-crosswalk.json`, `coverage.json`, `srd-spell-rosters.json`: occurrence evidence,
  coverage and licensed-entry boundaries.
- `protected-input-hashes.json`: the 2,163-file pre-task fingerprint snapshot, copied unchanged
  from the initial capture; this includes existing dirty work and is not a new license manifest.
- `verify-crosswalk.mjs`, `verification.json`: protection and repeatability checks.

Run from the repository root with Java 21, Node and PowerShell. Network downloads write only
to `target/` and reject changed document hashes. No production dependency is added.

```powershell
# The recorded protected-input-hashes.json is the pre-task baseline. Do not regenerate it.
& ./analysis/mechanics/public-v1/spells/p1b/fetch-and-extract.ps1
node analysis/mechanics/public-v1/spells/p1b/build-crosswalk.mjs
node analysis/mechanics/public-v1/spells/p1b/verify-crosswalk.mjs --capture
node analysis/mechanics/public-v1/spells/p1b/build-crosswalk.mjs
node analysis/mechanics/public-v1/spells/p1b/verify-crosswalk.mjs
git diff --check
```

Verification results are recorded in `p1b/verification.json`. The scanner validates every source
resource fingerprint, every candidate's exact original span and all 175 pinned JSON hashes.
No missing/duplicate occurrence identities remain. Repeated generation is byte-identical.
Pre-existing production/test/source/analysis files retain their pre-task fingerprints, including
P1A and original assessments; resolver code and outcomes are not modified. `git diff --check`
passes. Maven is not run for this analysis-only task: **539 tests and 6,246 resolver occurrences**
remain the previously verified baseline, not a newly executed Maven result.

## 11. Remaining blockers and recommendation

A limited release is technically feasible, subject to a **separate publication-owner decision**
on a concrete occurrence allowlist and attribution delivery. A verified licensed match is evidence
under an existing public license, not a requirement to obtain bespoke Wizards permission for
that licensed material; project release approval and license compliance are still necessary.

Ranked next actions:

1. Review/select the 250 matched occurrences and approve exact fields, notices, change indication
   and API attribution delivery. Preserve a signed-off manifest; do not authorize all 708 by default.
2. Separately authorize a bounded implementation combining the fail-closed allowlist, notices and
   the already proposed Spell observation projection. Reuse existing observations, immutable DTOs
   and original indexes; test allowed/denied cases, fingerprints, attribution and status counts.
3. Review the 129 short/layout-limited anchors and 17 cross-source/edition cases individually if
   broader coverage is useful. Review Flaming Sphere's inconsistent primary metadata separately.
   Stronger manually documented occurrence evidence can replace a conservative abstention; it
   must not silently lower the rule for the whole corpus.
4. Obtain new authoritative evidence for the 311 unmatched candidates before considering release.
   Do not treat a supplemental name, Basic Rules marker or common dice formula as a substitute.

Open questions: who owns the final publication decision and notice review; how downstream API
consumers receive attribution reliably; whether to release the full matched subset or a smaller
reviewed sample; and which ambiguous candidates merit individual adjudication. No formula
publication, Monster/Item API work, Step 8 or frontend implementation starts in this assessment.
