# Book and publication source inventory

## Dataset

- File: `src/main/resources/data/books.json` (291,996 bytes).
- Root object: one `book` array containing 66 records.
- `id` is present on all records and unique (66/66). `source` is present on all
  records and also unique (66/66). The fields differ for six Plane Shift
  records: `PS-Z -> PSZ`, `PS-I -> PSI`, `PS-K -> PSK`, `PS-A -> PSA`,
  `PS-X -> PSX`, and `PS-D -> PSD`. Existing entity references use the
  `source` spelling, so it is the useful canonical catalog key; `id` should be
  retained as a source alias rather than creating a synthetic identifier.
- Available `source` codes: PHB, MM, DMG, Screen, SCAG, PSZ, PSI, AL, VGM,
  PSK, PSA, OGA, XGE, PSX, MTF, PSD, GGR, SAC, AI, AWM, RMR, ERLW, EGW, MOT,
  ScreenDungeonKit, HF, ScreenWildernessKit, TCE, MGELFT, VRGR, DoD, MaBJoV,
  FTD, SCC, MPMM, MCV1SC, TD, ScreenSpelljammer, AAG, BAM, HAT-TG, BGG,
  MCV4EC, MPP, SatO, AATM, HFFotM, BMT, DMTCRG, PaF, XPHB, XDMG, XScreen,
  XMM, XSAC, ABH, FRAiF, FRHoF, NF, LFL, EFA, CaBoMP, XScreenRHW, RHW, AU,
  HFLotT.

## Field and nested-shape inventory

| Path | Type | Coverage | Notes |
|---|---|---:|---|
| `book[].name` | string | 66 | Full display title |
| `book[].id` | string | 66 | Unique alternate/source identifier |
| `book[].source` | string | 66 | Unique source code; canonical reference key candidate |
| `book[].group` | string | 66 | Editorial grouping, not consistently a publication-type enum |
| `book[].cover` | object | 66 | `{type,path}`; all observed `type` values are `internal` |
| `book[].coverCredit` | string | 3 | Cover artist credit; publication/rights metadata |
| `book[].published` | ISO date string | 66 | Publication date |
| `book[].author` | string | 66 | Author/organization credit |
| `book[].revised` | ISO date string | 2 | Revision date, distinct from publication date |
| `book[].alias` | string[] | 4 | Alternate product title; not generally a source code |
| `book[].parentSource` | string | 4 | Parent product/source relationship |
| `book[].contents` | object[] | 66 | Table-of-contents metadata; 455 entries |
| `contents[].name` | string | 455 | Chapter/section title |
| `contents[].ordinal` | object | 239 | `type` is chapter/appendix; `identifier` is number (195) or string (39) |
| `contents[].headers` | (string \| object)[] | 329 arrays | 1,923 strings and 2,343 structured headers |
| `headers[].header` | string | structured headers | Header details vary: `depth`, `index`, `source`, `statblock`, `tag`, `uid` |

No explicit null values were found. Optional book fields are absent. Nested
`headers` are polymorphic. Structured header properties vary. The `contents`
array represents table-of-contents metadata, not a mechanical definition.

## Publication categories and editions

The 10 observed `group` labels are `core`, `screen`, `setting`, `setting-alt`,
`organized-play`, `supplement`, `supplement-alt`, `other`, `recipe`, and
`homecraft`. These are repository editorial groupings and mix product kinds;
they should not be presented as an authoritative source publication type.
Records include core rulebooks, adventures/setting books, supplements, screens,
organized play, recipe books, card references, and homecraft publications.

Titles explicitly identify `Player's Handbook (2014)`, `Player's Handbook
(2024)`, `Dungeon Master's Guide (2014)`, `Dungeon Master's Guide (2024)`, and
`Monster Manual (2014)` / `Monster Manual (2025)`. The dataset has no explicit
rules-edition field. Do not infer an edition for other records based only on
their code, date, or title. `published` and `revised` are separate metadata.

Aliases and `parentSource` appear on four records (AAG, BAM, MPP, SatO). In the
Planescape pair a shared alias and parent source describe a larger product,
not a unique publication identity. The `source` values remain individual.

Author values: Wizards RPG Team (56), Christopher Lindsay (1), Emi Tanji (2),
Darrington Press (1), Kyle Newman (3), Clarkson Potter (1), Andrew Wheeler
(1), and Stacy King (1). The three alias title strings are shared across four
records, so alias-to-publication lookup can be ambiguous. `contents` includes
201 `chapter` ordinals and 38 `appendix` ordinals; five of the 239 ordinal
objects omit `identifier`.

## Existing source-code coverage

The only publication registry file found under `src/main/resources/data/` is
`books.json`.

| Consumer and role | Unique referenced codes | Resolved by `book.source` or `book.id` | Unresolved |
|---|---:|---:|---:|
| Languages: primary `source` | 24 | 17 | 7 |
| Languages: `additionalSources` / `otherSources` | 6 | 4 | 2 |
| Languages: `reprintedAs` targets | 3 | 3 | 0 |
| Monsters: primary `source` | 110 | 44 | 66 |
| Monsters: `otherSources` / `referenceSources` | 26 | 5 | 21 |
| Monsters: `reprintedAs` targets | 9 | 8 | 1 |

Language unresolved primary codes: DSotDQ, HotDQ, IDRotF, OotA, RoT, SKT,
TftYP. Secondary: EEPC, ToD.

Monster unresolved primary codes: AUD, AitFR-DN, AitFR-FCD, AitFR-ISF,
AitFR-THP, BGDIA, CM, CRCotN, CoA, CoS, DC, DIP, DSotDQ, DitLCoT, DoSI,
DrDe-BD, ESK, GoS, GotSF, HftT, HoL, HotB, HotDQ, IDRotF, IMR, JttRC, KKW,
KftGV, LLK, LMoP, LR, LRDT, LoX, MCV2DC, MCV3MC, MFF, MisMV1, NRH-ASS,
NRH-AT, NRH-AVitW, NRH-AWoL, NRH-CoI, NRH-TCMC, OoW, OotA, PaBTSO, PotA,
QftIS, RMBRE, RWG, RoT, RtG, SADS, SDW, SKT,
SLW, TTP, TftYP, ToA, ToFW, VD, VEoR, WBtW, WDH, WDMM, WttHC.

The unresolved set includes adventures, organized-play/short-form releases,
older supplements, anthology sources, and setting references, not only likely
book records. This analysis does not invent records or classify absent codes.
No alternate source registry exists in the repository to resolve them.

Monster corpus inspected: 113 `bestiary-*.json` files and 4,560 base source
records before the existing copy/version processing. Language corpus inspected:
201 language entries and 6 language-script entries. Monster identity already
uses a stable source/name ID and carries page at the occurrence. Language IDs
are source-qualified and must remain unchanged.

## API suitability and rights

Suitable public metadata after a publication decision: source code, title,
publication date, author credit, and selected group metadata with its meaning
clearly qualified. Do not expose raw TOC headers, internal cover paths, or
cover credits by default. Publication/author/cover data and TOC titles may have
publication restrictions. Import correctness is separate from publication
rights.
