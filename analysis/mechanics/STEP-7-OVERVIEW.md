# Step 7 Mechanics and Publication Analysis

## Decision and scope

Begin Step 7 with preservation and projection correctness, followed by internal syntax annotations.
The first implementation batch should restore class progression table column positions. The source
and current mapper establish a reproducible defect on 97 authored rows across six Class/Subclass
roots. No prose interpretation or new reference integration is needed to correct the positions.

This delivery is analysis only. Production Java, public DTOs, endpoints, tests, source JSON and
reference resolution are unchanged. Homecraft's 20 crochet records remain OUT OF SCOPE by product
decision. Recipes remain food/cocktail metadata content with no gameplay enrichment proposed.

## Evidence and denominators

| Measure | Result | Meaning |
|---|---:|---|
| Resource files parsed/fingerprinted | 175 | Complete checked-out `data/` tree, including excluded/admin file classification |
| Catalog domains reviewed | 36 | Item/Base Item, Race/Subrace and six Treasure variants share their existing API domains |
| Definition-source collections | 43 | Before copy/version expansion; not 43 new APIs |
| Authored root records | 14,399 | Includes five nameless Subrace support records, excluded from effective definitions |
| Existing effective definitions | 14,797 | From existing catalog contracts; not newly expanded by the scanner |
| Supporting root units | 812 | Registry records, metadata objects, templates, auxiliary records and shared rows |
| Collection-relative field paths | 6,308 | 5,434 on definition collections; every observed type is counted |
| Parsed markup occurrences | 91,994 | 90,294 on definition roots; includes nested/transform syntax |
| Distinct observed tag names | 73 | Case-folded syntax families, including non-mechanical tags |
| Malformed/unbalanced markup | 0 | Syntactic result, not semantic validation |
| Explicit `type=table` nodes | 973 | Definition roots only; not all table dialects and not 973 verified mechanical tables |

The source-to-effective difference is +291 Monsters, +9 Feats and +98 Races/Subraces
(103 expansions minus five auxiliary records). Current catalogs have 4,851 Monsters,
314 Feats and 356 Races/Subraces. All other definition counts in the inventory retain their existing
contracts. The pinned v2.37.0 designation is project context; exact input bytes are fingerprinted.

The existing baseline is 399 tests and 6,246 resolver occurrences: 2,517 RESOLVED,
2,054 AMBIGUOUS, 181 UNRESOLVED and 1,494 DEFERRED. A generic markup scan is not a resolver scan:
91,994 syntax occurrences must never be substituted for the selected 6,246 owner references.

## Classification of findings

| Class | What it means | Corpus example | Appropriate treatment |
|---|---|---|---|
| A: already structured | Authored fields with an explicit source representation | Monster AC/HP; Spell duration; Facility hirelings; Item bonus fields | Check DTE, normalized and public destinations before proposing extraction |
| B: structured markup | Balanced tags with explicit arguments or markers | `@damage`, `@dice`, `@actSave`, `@condition` | Parse annotations and retain locator/arguments; do not invent an effect |
| C: contextual expression | Syntax whose relationship needs a constrained enclosing structure | Object attack/hit entry separation; Monster save-result labels | Preserve explicit boundary; semantic association requires a verified contract |
| D: prose candidate | Meaning depends on natural language | Trap countermeasures, vehicle-upgrade benefits, condition exceptions | Heuristic/manual review; no automatic public effect |
| E: non-mechanical | Content whose numbers/references do not establish game mechanics | Recipes, name rows, narrative encounter results, publication headings | Retain existing content boundary; no gameplay extraction |

Classification, extraction confidence and publication eligibility are separate axes. A perfectly
parsed tag can have unknown effect semantics and an unreviewed publication status.

## Findings that change the implementation priorities

1. **Class table positions:** filtering `PRESENTATION_TEXT` cells removes columns without an
   index. PHB Barbarian's source row is a text-valued Rages cell followed by a typed bonus cell;
   the surviving bonus is moved into the first returned position. Six authored roots and 97 rows
   are affected. Restore positional fidelity without publishing previously withheld text.
2. **Item focus arrays:** 52 Item roots author class-name arrays in `focus`. The DTE preserves
   the array, but `ItemMapper.focusEnabled()` reads only the Boolean variant, losing the array
   from normalized/public mechanics. Two other roots author Boolean values.
3. **Feat structured grants:** 59 roots carry 74 occurrences across 14 grant/defense/sense/
   progression field families outside `FeatDefinition`. They remain typed in import records.
   Another 12 roots flatten prerequisite proficiency category keys into strings.
4. **Existing publication exposure differs by catalog:** Monster details expose rich entry
   text; 220 Item roots forward tagged charge expressions; seven Optional Feature roots forward
   14 prerequisite text fields. These are observed contracts to review, not permission to broaden
   publication or silently alter existing APIs.
5. **Prior spell findings need two corrections:** `scalingLevelDice` occurs on 41 roots
   (36 object, five array), while the earlier counter reported only five array variants.
   Seventy-eight spells already encode non-point range geometry with numeric distance. Those
   values are already normalized/public; no new extraction is required.

The existing structured classes/spell/item/race metadata is substantial. It would be misleading to
present those fields as newly extracted mechanics. Benefits in Conditions, Diseases, Facility
orders, Upgrades and Variant Rules remain largely descriptive despite recognizable tags.

## Deliverables

- [Complete catalog inventory](CORPUS-MECHANICS-INVENTORY.md): all 36 domains, source/effective
  counts, root fields, normalized/public handling, choices, scaling, tables, dependencies and locators.
- [Markup semantics](MARKUP-SEMANTICS.md) and [full frequencies](TAG-FREQUENCIES.md).
- [Reliability and coverage](EXTRACTION-RELIABILITY.md).
- [Normalization/projection gaps](NORMALIZATION-GAPS.md).
- [Licensing and publication](LICENSING-AND-PUBLICATION.md).
- [Proposed enrichment model](ENRICHMENT-MODEL.md).
- [Ranked backlog and batches](IMPLEMENTATION-ORDER.md).
- [Open decisions](OPEN-QUESTIONS.md).
- `scan-mechanics.mjs`, `audit-candidates.mjs`, `catalog-review.mjs`,
  `write-corpus-inventory.mjs`, `inspect-evidence.mjs`, `validate-analysis.mjs`.
- `corpus-evidence.json`, `candidate-evidence.json`, `protected-files-baseline.json` and
  `verification.json`.

## Reproduction

From the repository root, with Node.js 24 (no extra packages):

```text
node analysis/mechanics/scan-mechanics.mjs
node analysis/mechanics/audit-candidates.mjs
node analysis/mechanics/write-corpus-inventory.mjs
node analysis/mechanics/validate-analysis.mjs
git diff --check
```

`--capture-baseline` is only for creating a new explicit analysis baseline; it refuses to overwrite
an existing baseline. Normal scans check that every file under `src/` retains its original hash.
The final validator checks reproducibility, count reconciliation, example locators, Step 3 parity
and unchanged protected inputs. It does not run or modify production normalization.

## Relationship to earlier work

The [spell Stage D report](../spells/STAGE-D-MECHANICS-INVESTIGATION.md) and
[extraction design](../spells/MECHANICS-EXTRACTION-DESIGN.md) already distinguish annotations from
effects. That boundary is retained and expanded to the complete corpus. Counts here explicitly
include support/transform syntax and identify their scope, rather than projecting spell-only
heuristics onto every catalog. Historical Step 6 planning tables contain superseded proposals
(including Homecraft and Status wording); current implementations and the final scope decisions
take precedence. No old investigation or production file is rewritten by this analysis.

## Final analysis verification

- All generated corpus/candidate inventories and frequency documentation reproduce identical
  hashes on a second run.
- 7,406 saved source examples/locators and 1,013 tag-variant examples validate against the
  fingerprinted input. Local documentation links are checked by the validator.
- All 1,655 files under `src/` remain byte-identical to the captured analysis baseline, including
  production Java, DTOs, controllers, resolver code, tests and source JSON.
- `git diff --check` passes. New analysis files also receive a direct whitespace/newline check,
  because Git's ordinary diff does not include untracked files.
- Maven was not rerun for this analysis-only change. Existing Surefire reports record 399 tests
  across 50 suites, with zero failures/errors/skips. No test suite was changed.
- Reference resolution was not rerun or modified; the supplied/current 6,246-occurrence baseline
  remains the contract. The validator explicitly reports this distinction rather than claiming
  a fresh runtime resolution measurement.

Exact output fingerprints and validation results are in [verification.json](verification.json).
