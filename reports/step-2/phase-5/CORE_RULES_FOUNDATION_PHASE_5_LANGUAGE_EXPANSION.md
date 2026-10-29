# Core Rules Foundation Phase 5 Language Expansion

## Status

**Language expansion with approved LANG-1, LANG-3, and LANG-4 refinements is complete for human review. Step 2 Phase 5 remains in progress and is not approved. Rule expansion and Phase 6 have not started.** The original expansion baseline is preserved below; the approved-refinement addendum records the current contracts and coverage.

## Source inventory and representative baseline

The immutable `src/main/resources/data/languages.json` contains **201 Language records** across **24 source codes**, matching the currently inventoried Step 2 Language catalog. This count excludes the six LanguageScript records; no LanguageScript canonical model or outputs were created. Repeated names and reprints remain separate source-qualified identities.

Four records were already reviewed in Phase 3: PHB Common, XPHB Common, MM Gith, and PHB Thieves' Cant. The other **197 records** were examined and converted in the original Phase 5 batch. PHB Thieves' Cant was subsequently migrated under approved LANG-3; the other three Phase 3 Language records remain unchanged. Source locators use the original array index.

| Source | Records | Full | Partial | Unsupported |
| --- | ---: | ---: | ---: | ---: |
| AI | 1 | 1 | 0 | 0 |
| DSotDQ | 19 | 0 | 19 | 0 |
| EGW | 3 | 3 | 0 | 0 |
| ERLW | 16 | 5 | 11 | 0 |
| FRHoF | 17 | 0 | 17 | 0 |
| GGR | 16 | 4 | 12 | 0 |
| HotDQ | 1 | 1 | 0 | 0 |
| IDRotF | 1 | 0 | 1 | 0 |
| MM | 21 | 20 | 1 | 0 |
| MOT | 9 | 0 | 9 | 0 |
| MTF | 1 | 1 | 0 | 0 |
| OotA | 1 | 1 | 0 | 0 |
| PHB | 18 | 6 | 12 | 0 |
| PSA | 2 | 0 | 2 | 0 |
| PSD | 3 | 1 | 2 | 0 |
| PSI | 4 | 1 | 3 | 0 |
| PSX | 6 | 0 | 6 | 0 |
| RoT | 1 | 1 | 0 | 0 |
| SCAG | 20 | 0 | 20 | 0 |
| SKT | 2 | 2 | 0 | 0 |
| TftYP | 1 | 1 | 0 | 0 |
| VGM | 4 | 3 | 1 | 0 |
| XMM | 15 | 15 | 0 | 0 |
| XPHB | 19 | 0 | 19 | 0 |
| **Total before approved Language refinements** | **201** | **66** | **135** | **0** |

At initial Phase 5 expansion, 65 of the 197 new records were full and 132 were partial. The four Phase 3 outputs were one full and three partial. The approved Language refinements changed six Phase 5 classifications to full; current totals are 72 full, 129 partial, and zero unsupported across all 201 records. Every source identity still has a canonical Language output. Identity-only outputs preserve source identity without invented category, script, or speaker mechanics.

## Method

Codex inspected all 201 source objects and every Language entry root and nested clause. Deterministic operations were limited to inventory/counting, approved source-field mappings, serialization, exact source-reference lookup for explicit creature links, register generation, and validation. Semantic behavior and omission decisions were based on original source evidence. No source dataset, schema, Phase 2 example, or non-Language canonical record was changed.

IDs follow the existing lowercase source/name slug convention. Source metadata preserves supplied source, page, flags, citations, and exact reprint targets. Source `type` maps to the approved Language category enumeration; named script values are retained. The literal `script: "none"` denotes absence. Source prose, rich-text, fonts, workflow state, and LanguageScript data are absent from canonical JSON.

## Catalog-wide mechanic patterns and existing contract coverage

| Pattern | Catalog evidence | Current Language v1 coverage |
| --- | --- | --- |
| Language identity and reprints | 201 records; 46 explicit reprint relations | Supported; all identities remain source-qualified and all supplied reprints are retained in metadata. |
| Category | 105 records: 68 standard, 16 exotic, 17 rare, 4 secret | Supported by the existing finite enumeration. |
| Script name | 99 source properties; two are the value `none`, leaving 97 named scripts | Supported as a name string. Script layout, direction, and writing mechanics are not modeled. |
| Typical creature speakers | 159 records have `typicalSpeakers`; 102 explicit creature links and 21 source-defined filters were represented by the new batch | Exact creature references, type filters, and the ten verified exact-tag selector expressions are represented. Two search selectors and the PSI Demonic selectors with explicitly empty source values remain omitted. Cross-domain speakers remain external dependencies. |
| Origins | 36 records across the catalog; 35 new-batch values | Values describe places, planes, populations, or broad groups without exact approved target identities; no references were guessed. |
| Dialects | MM Gith and PHB/XPHB Primordial | The schema accepts Language references, but the named dialect identities do not occur as exact Language source records. No identities were invented. |
| Communication and writing behavior | Seven entry roots across six records | No bounded covert-message, intelligibility, communication-limit, or inscription procedure exists in Language v1. |
| Character language acquisition | 20 SCAG inset roots | Character language grants belong to the Character Building domain, not the Language entity. |

The batch produced **102 exact creature speaker references and 21 typed creature filters**. Together with the existing Phase 3 Gith references and PHB Common filter, the complete catalog outputs contain 106 creature references and 22 filters. The contract represents the supported speaker cases without raw tag syntax. It does not model population, race, class, organization, community, or place targets.

## Communication and writing cases

- **Druidic (PHB, `$.language[51].entries[0]`):** the revised contract represents hidden-message use, automatic recognition for a knower, DC 15 Wisdom (Perception) detection of presence only for another observer, and the magic restriction on deciphering by non-knowers.
- **Thieves' Cant (PHB representative, `$.language[173].entries[0]`):** the revised contract represents ordinary-conversation concealment, knowledge-gated comprehension, and four-times communication duration. XPHB has no equivalent entry and was not assigned the PHB mechanics.
- **Loross (IDRotF, `$.language[113].entries[0]`):** mutual spoken intelligibility with PHB Elvish is now a typed, mode- and direction-qualified relation. Written-translation prerequisites remain deferred because their exact approved background and class-feature identities are external dependencies.
- **Qualith (VGM, `$.language[145].entries[0..1]`):** the source describes psionic inscription creation, tactile reading, mind-flayer comprehension, partial checks with complexity-dependent DCs, repeated attempts, headaches/madness on failure, inscription degradation, and a spell alternative. These are not reducible to a script-name value.
- **Coalition Pidgin and Common Trade Pidgin (PSX, `$.language[25]` and `$.language[34]`):** source describes pidgin composition/intelligibility and limits on deep conversation. No composition or communication-limit fields were invented.
- **Gith (MM representative):** the existing output retains the script name and source-context creature speakers. The Githyanki/Githzerai dialect identities, circular tir'su layout, and opposite writing directions remain outside the approved structure.
- **SCAG human languages:** 20 repeated entries describe optional character language grants and common script history. They were not copied into Language records or used to infer speaker filters.

Other language entries describing sound, literary traditions, culture, vocabulary, or history were classified as nonmechanical where they add no independent game procedure. Gnomish and Orc entries repeat their canonical Dwarvish script association and are registered as duplicates for that portion.

## Source-entry accounting

The 197-record Phase 5 batch accounts for **40 entry roots and 40 nested source paths**. Three mixed roots have multiple dispositions, producing 43 classification parts:

| Disposition | Parts | Treatment |
| --- | ---: | --- |
| INTERPRETED | 3 | Guran derives from the exact SCAG Roushoum and Rashemi Language identities; Loross is mutually intelligible with the exact PHB Elvish identity; Druidic concealed-message mechanics are typed. |
| DUPLICATE | 2 | Gnomish and Orc repeat the canonical Dwarvish script value. |
| OMITTED / OUT_OF_APPROVED_SCOPE | 20 | Optional ethnic-language acquisition and shared human-script history. |
| OMITTED / SCHEMA_UNSUPPORTED | 5 | Pidgin composition/topic limits, the Goblin conversation limit, and Qualith inscription behavior. |
| OMITTED / EXTERNAL_DEPENDENCY | 2 | Halfling language-acquisition and Common-language character context; Loross written-translation prerequisite identities. |
| OMITTED / NON_MECHANICAL | 11 | Descriptive source prose without a separate typed mechanic. |

The complete source entries, including omitted portions of partially interpreted entries, are preserved in the Language omission register. Duplicate entries and source identities are preserved in the duplicate register. The original structured-field issue log contained 157 Language issues. After ten exact-tag expressions were represented, 148 remain: 76 external population/speaker targets, three unsupported tag/search cases (two search selectors and PSI's explicit empty source parameters), six unresolved creature links, 35 unresolved origins, two unresolved dialect lists, and 26 excluded `hasFluffImages` presentation flags. The four Phase 3 representatives retain their separate source-field accounting.

The updated Phase 5 accounting manifest totals **325 canonical records** across completed batches (179 full, 146 partial, three unsupported), 276 entry roots, and 204 interpreted / 24 duplicate / 190 omitted classification parts. These are expansion-to-date totals, not complete Step 2 coverage. Rule remains unexamined.

## Contract gaps and human decisions

No schema changes were made. Review these possible refinements before any later Language migration:

1. **Tag-only and search-based creature filters — focused review recommended.** Twelve speaker fields use tag-only or `search` criteria without a source-defined creature type. A small additive filter form may permit explicit tag/search selection while preserving source query scope. Ten records use tags such as demon, devil, angel, goblinoid, orc, loxodon, and merfolk; Quori and Satyr sources use search selectors. The current filter requires a creature type, so the alternatives are to approve a bounded structure or retain partial classification. Risk: source query semantics may broaden if tags or sources are dropped.

   Current supported shape requires `creatureTypes`:

   ```json
   {
     "filter": {
       "targetType": "creature",
       "creatureTypes": ["humanoid"],
       "tags": [
         {
           "group": "any race",
           "value": "human"
         }
       ]
     }
   }
   ```

   A possible additive tag-only form for the source's GGR Abyssal selector is:

   ```json
   {
     "filter": {
       "targetType": "creature",
       "sourceTags": ["demon"],
       "sources": ["GGR"]
     }
   }
   ```

   This is a proposal for review only; `sourceTags` is not part of the approved schema. The `search=quori` and `search=satyr` selectors may require a separate bounded alternative rather than a generic query-string field.
2. **Non-creature speaker targets — defer.** Seventy-six fields name populations, races, classes, organizations, ethnic groups, or communities. Review target types only after the owning Character Building, Classes, creature, and setting/place inventories establish stable identities. No free-text fallback or generic population predicate is proposed.
3. **Communication and writing behavior — contract review required before future expansion.** Seven roots across six records contain mechanics outside the identity/speaker contract. Evaluate a small set of domain-specific procedures only if reuse warrants it. Do not introduce a universal communication engine or a LanguageScript model by default.
4. **Origins and dialects — defer identity changes.** Thirty-five new origin values and two dialect lists lack exact target records. Revisit after the relevant place, variant-language, race, and creature identities exist.

The possible filter extension could improve speaker representation for up to 12 partial records, but does not make those records fully represented if other omissions remain. Up to 76 speaker fields may improve only when exact owning-domain identities become available. The seven unsupported entry roots affect six records and do not necessarily share one reusable execution model. No full-coverage gain is forecast until every source field and entry has been reviewed against approved structures.

## Validation

The original Language expansion validation compiled all nine Draft 2020-12 schemas, validated 54 examples, all 34 Phase 3/4 records, and all 325 Phase 5 canonical records. The approved refinement validation now includes the Language-specific checks described below and in the implementation report.

Original expansion validation: **9 schemas compiled, 54 examples validated, 34 Phase 3/4 records validated, 325 Phase 5 records validated, and 481 permanent checks passed.** This is the pre-refinement historical result. The refinement run and its updated counts are recorded in the implementation report.

**Phase status:** Language expansion and approved LANG-1/LANG-3/LANG-4 refinements are presented for review. Step 2 Phase 5 remains in progress and is not approved. Rule expansion, Phase 6, and later steps have not started.

## Approved Language contract refinement addendum

The reviewer approved LANG-1, LANG-3, and LANG-4 after the separate contract review. The approved additions are exact tag-only creature selectors (without inferred creature types), finite concealed-message structures, and typed mutual spoken-intelligibility relationships. The previous counts and schema-gap statements in this report describe the original expansion and are retained as historical evidence.

Nine Language records now represent ten exact-tag selectors: GGR and PHB Abyssal, GGR Celestial, GGR Goblin, PHB Goblin, PHB Infernal, GGR Loxodon, GGR Merfolk, and PHB Orc. GGR Merfolk retains the literal `any race;merfolk` selector without parsing it. PSI Demonic retains no canonical selector because both source filters explicitly specify an empty source parameter; the complete criteria remain in source accounting as `SCHEMA_UNSUPPORTED`. Quori and Sylvan search selectors remain omitted with `SOURCE_AMBIGUOUS` cause; their exact indexed result sets are unresolved. No source selector was converted to a creature type.

PHB Druidic and PHB Thieves' Cant now carry separate `CONCEALED_MESSAGE` profiles. Druidic encodes the left-message channel, spoken and left-message modes, automatic recognition for language knowers, DC 15 Wisdom (Perception) detection of presence only for others, and the magic restriction on non-knower deciphering. PHB Thieves' Cant encodes ordinary-conversation concealment, language-knowledge comprehension, and a four-times communication duration. XPHB Thieves' Cant was not changed. Loross now uses a typed mutual `SPOKEN` intelligibility edge to PHB Elvish; written-translation prerequisites remain an external dependency.

Current Language totals are **72 FULL, 129 PARTIAL, 0 UNSUPPORTED** across 201 canonical records. The Phase 5 Language batch is 71 full and 126 partial among its 197 records. The Phase 5 manifest now has 204 interpreted, 24 duplicate, and 190 omitted source-entry parts; it has 154 structured-field issues overall, including 148 Language issues. The PHB Thieves' Cant Phase 3 source-entry accounting was also updated from partial omission to full entry interpretation; its unresolved typical-speaker field remains external. The combined Phase 5 dataset totals are 179 full, 146 partial, and 3 unsupported.

Latest refinement validation: `npm run validate:core-rules` compiled 9 schemas, validated 58 examples, 34 Phase 3/4 records, and 325 Phase 5 records, and passed 507 permanent checks. The result is structural and assertion coverage, not a claim that every Language source interpretation has undergone human semantic review.

Deferred items remain LANG-2 search selectors, LANG-5 written translation prerequisites, LANG-6 Qualith inscription mechanics, non-creature speaker identities, unresolved origins and dialect identities, and LanguageScript modeling. Step 2 Phase 5 remains open and unapproved.

## Record-by-record coverage matrix

The following matrix accounts for all source objects and distinguishes the reviewed Phase 3 representatives from the 197 Phase 5 records.

| Index | Source path | Canonical ID | Source | Batch | Result | Canonical information | Remaining accounting |
| ---: | --- | --- | --- | --- | --- | --- | --- |
| 0 | `$.language[0]` | `language:mm:aarakocra` | MM | P5 | full | typicalSpeakers | ? |
| 1 | `$.language[1]` | `language:xmm:aarakocra` | XMM | P5 | full | typicalSpeakers | ? |
| 2 | `$.language[2]` | `language:dsotdq:abanasinian` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 3 | `$.language[3]` | `language:ggr:abyssal` | GGR | P5 | full | category, script, exact-tag speakers | ? |
| 4 | `$.language[4]` | `language:phb:abyssal` | PHB | P5 | full | category, script, exact-tag speakers | ? |
| 5 | `$.language[5]` | `language:xphb:abyssal` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 6 | `$.language[6]` | `language:frhof:aglarondan` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 7 | `$.language[7]` | `language:frhof:alzhedo` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 8 | `$.language[8]` | `language:scag:alzhedo` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 9 | `$.language[9]` | `language:psd:aven` | PSD | P5 | partial | identity only | typicalSpeakers:external_dependency |
| 10 | `$.language[10]` | `language:mm:blink-dog` | MM | P5 | full | typicalSpeakers | ? |
| 11 | `$.language[11]` | `language:xmm:blink-dog` | XMM | P5 | full | typicalSpeakers | ? |
| 12 | `$.language[12]` | `language:frhof:bothii` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 13 | `$.language[13]` | `language:skt:bothii` | SKT | P5 | full | typicalSpeakers | ? |
| 14 | `$.language[14]` | `language:mm:bullywug` | MM | P5 | full | typicalSpeakers | ? |
| 15 | `$.language[15]` | `language:xmm:bullywug` | XMM | P5 | full | typicalSpeakers | ? |
| 16 | `$.language[16]` | `language:erlw:celestial` | ERLW | P5 | full | category, script, typicalSpeakers | ? |
| 17 | `$.language[17]` | `language:ggr:celestial` | GGR | P5 | full | category, script, exact-tag speakers | ? |
| 18 | `$.language[18]` | `language:mot:celestial` | MOT | P5 | partial | script | typicalSpeakers:external_dependency |
| 19 | `$.language[19]` | `language:phb:celestial` | PHB | P5 | full | category, script, typicalSpeakers | ? |
| 20 | `$.language[20]` | `language:xphb:celestial` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 21 | `$.language[21]` | `language:frhof:chessentan` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 22 | `$.language[22]` | `language:scag:chessentan` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 23 | `$.language[23]` | `language:frhof:chondathan` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 24 | `$.language[24]` | `language:scag:chondathan` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 25 | `$.language[25]` | `language:psx:coalition-pidgin` | PSX | P5 | partial | identity only | typicalSpeakers:external_dependency, schema_unsupported |
| 26 | `$.language[26]` | `language:dsotdq:common` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 27 | `$.language[27]` | `language:erlw:common` | ERLW | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 28 | `$.language[28]` | `language:ggr:common` | GGR | P5 | full | category, script, typicalSpeakers | ? |
| 29 | `$.language[29]` | `language:mot:common` | MOT | P5 | partial | script | typicalSpeakers:external_dependency |
| 30 | `$.language[30]` | `language:phb:common` | PHB | P3 | full | category, script, typicalSpeakers | ? |
| 31 | `$.language[31]` | `language:psi:common` | PSI | P5 | partial | typicalSpeakers | typicalSpeakers:external_dependency |
| 32 | `$.language[32]` | `language:xphb:common` | XPHB | P3 | partial | category | origin:reference_unresolved |
| 33 | `$.language[33]` | `language:xphb:common-sign-language` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 34 | `$.language[34]` | `language:psx:common-trade-pidgin` | PSX | P5 | partial | identity only | typicalSpeakers:external_dependency, schema_unsupported |
| 35 | `$.language[35]` | `language:erlw:daelkyr` | ERLW | P5 | partial | category, script, typicalSpeakers | typicalSpeakers:external_dependency |
| 36 | `$.language[36]` | `language:frhof:damaran` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 37 | `$.language[37]` | `language:scag:damaran` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 38 | `$.language[38]` | `language:scag:dambrathan` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, out_of_approved_scope |
| 39 | `$.language[39]` | `language:ai:deep-crow` | AI | P5 | full | typicalSpeakers | ? |
| 40 | `$.language[40]` | `language:phb:deep-speech` | PHB | P5 | partial | category | typicalSpeakers:reference_unresolved |
| 41 | `$.language[41]` | `language:xphb:deep-speech` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 42 | `$.language[42]` | `language:psi:demonic` | PSI | P5 | partial | identity only | typicalSpeakers:empty_source_scope_unrepresentable |
| 43 | `$.language[43]` | `language:dsotdq:draconic` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 44 | `$.language[44]` | `language:erlw:draconic` | ERLW | P5 | full | category, script, typicalSpeakers | ? |
| 45 | `$.language[45]` | `language:ggr:draconic` | GGR | P5 | full | category, script, typicalSpeakers | ? |
| 46 | `$.language[46]` | `language:mot:draconic` | MOT | P5 | partial | script | typicalSpeakers:external_dependency |
| 47 | `$.language[47]` | `language:phb:draconic` | PHB | P5 | partial | category, script, typicalSpeakers | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, non_mechanical |
| 48 | `$.language[48]` | `language:psi:draconic` | PSI | P5 | full | typicalSpeakers | ? |
| 49 | `$.language[49]` | `language:xphb:draconic` | XPHB | P5 | partial | category, script | origin:reference_unresolved |
| 50 | `$.language[50]` | `language:ggr:druidic` | GGR | P5 | partial | category | typicalSpeakers:reference_unresolved |
| 51 | `$.language[51]` | `language:phb:druidic` | PHB | P5 | partial | category, concealed-message capability | typicalSpeakers:reference_unresolved |
| 52 | `$.language[52]` | `language:xphb:druidic` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 53 | `$.language[53]` | `language:dsotdq:dwarvish` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 54 | `$.language[54]` | `language:erlw:dwarvish` | ERLW | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 55 | `$.language[55]` | `language:phb:dwarvish` | PHB | P5 | full | category, script, typicalSpeakers | hasFluffImages:out_of_approved_scope, non_mechanical |
| 56 | `$.language[56]` | `language:xphb:dwarvish` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 57 | `$.language[57]` | `language:dsotdq:elvish` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 58 | `$.language[58]` | `language:erlw:elvish` | ERLW | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 59 | `$.language[59]` | `language:ggr:elvish` | GGR | P5 | full | category, script, typicalSpeakers | ? |
| 60 | `$.language[60]` | `language:phb:elvish` | PHB | P5 | full | category, script, typicalSpeakers | hasFluffImages:out_of_approved_scope, non_mechanical |
| 61 | `$.language[61]` | `language:xphb:elvish` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 62 | `$.language[62]` | `language:dsotdq:ergot` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 63 | `$.language[63]` | `language:erlw:giant` | ERLW | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 64 | `$.language[64]` | `language:ggr:giant` | GGR | P5 | partial | category, script, typicalSpeakers | typicalSpeakers:reference_unresolved |
| 65 | `$.language[65]` | `language:mot:giant` | MOT | P5 | partial | script | typicalSpeakers:external_dependency |
| 66 | `$.language[66]` | `language:phb:giant` | PHB | P5 | partial | category, script, typicalSpeakers | typicalSpeakers:reference_unresolved, hasFluffImages:out_of_approved_scope |
| 67 | `$.language[67]` | `language:xphb:giant` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 68 | `$.language[68]` | `language:mm:giant-eagle` | MM | P5 | full | typicalSpeakers | ? |
| 69 | `$.language[69]` | `language:mm:giant-elk` | MM | P5 | full | typicalSpeakers | ? |
| 70 | `$.language[70]` | `language:mm:giant-owl` | MM | P5 | full | typicalSpeakers | ? |
| 71 | `$.language[71]` | `language:mm:gith` | MM | P3 | partial | script, typicalSpeakers | dialects:reference_unresolved, schema_unsupported |
| 72 | `$.language[72]` | `language:xmm:gith` | XMM | P5 | full | typicalSpeakers | ? |
| 73 | `$.language[73]` | `language:mm:gnoll` | MM | P5 | full | typicalSpeakers | ? |
| 74 | `$.language[74]` | `language:xmm:gnoll` | XMM | P5 | full | typicalSpeakers | ? |
| 75 | `$.language[75]` | `language:dsotdq:gnomish` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 76 | `$.language[76]` | `language:erlw:gnomish` | ERLW | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 77 | `$.language[77]` | `language:phb:gnomish` | PHB | P5 | full | category, script, typicalSpeakers | hasFluffImages:out_of_approved_scope, non_mechanical |
| 78 | `$.language[78]` | `language:xphb:gnomish` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 79 | `$.language[79]` | `language:dsotdq:goblin` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 80 | `$.language[80]` | `language:erlw:goblin` | ERLW | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 81 | `$.language[81]` | `language:ggr:goblin` | GGR | P5 | partial | category, script, exact-tag speakers | entry:schema_unsupported |
| 82 | `$.language[82]` | `language:phb:goblin` | PHB | P5 | partial | category, script, exact-tag speakers | hasFluffImages:out_of_approved_scope |
| 83 | `$.language[83]` | `language:xphb:goblin` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 84 | `$.language[84]` | `language:mm:grell` | MM | P5 | full | typicalSpeakers | ? |
| 85 | `$.language[85]` | `language:vgm:grung` | VGM | P5 | full | typicalSpeakers | ? |
| 86 | `$.language[86]` | `language:scag:guran` | SCAG | P5 | partial | script, origin | typicalSpeakers:external_dependency, out_of_approved_scope |
| 87 | `$.language[87]` | `language:erlw:halfling` | ERLW | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 88 | `$.language[88]` | `language:phb:halfling` | PHB | P5 | partial | category, script, typicalSpeakers | external_dependency |
| 89 | `$.language[89]` | `language:xphb:halfling` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 90 | `$.language[90]` | `language:scag:halruaan` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, out_of_approved_scope |
| 91 | `$.language[91]` | `language:psd:homarid` | PSD | P5 | full | typicalSpeakers | ? |
| 92 | `$.language[92]` | `language:mm:hook-horror` | MM | P5 | full | typicalSpeakers | ? |
| 93 | `$.language[93]` | `language:xmm:hook-horror` | XMM | P5 | full | typicalSpeakers | ? |
| 94 | `$.language[94]` | `language:rot:ice-toad` | RoT | P5 | full | typicalSpeakers | ? |
| 95 | `$.language[95]` | `language:frhof:illuskan` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 96 | `$.language[96]` | `language:scag:illuskan` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 97 | `$.language[97]` | `language:erlw:infernal` | ERLW | P5 | full | category, script, typicalSpeakers | ? |
| 98 | `$.language[98]` | `language:phb:infernal` | PHB | P5 | full | category, script, exact-tag speakers | ? |
| 99 | `$.language[99]` | `language:xphb:infernal` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 100 | `$.language[100]` | `language:dsotdq:istarian` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 101 | `$.language[101]` | `language:psx:itzocan` | PSX | P5 | partial | identity only | typicalSpeakers:external_dependency |
| 102 | `$.language[102]` | `language:oota:ixitxachitl` | OotA | P5 | full | typicalSpeakers | ? |
| 103 | `$.language[103]` | `language:psd:keldon` | PSD | P5 | partial | identity only | typicalSpeakers:external_dependency |
| 104 | `$.language[104]` | `language:dsotdq:kenderspeak` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 105 | `$.language[105]` | `language:dsotdq:kharolian` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 106 | `$.language[106]` | `language:psa:khenra` | PSA | P5 | partial | identity only | typicalSpeakers:external_dependency |
| 107 | `$.language[107]` | `language:dsotdq:khur` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 108 | `$.language[108]` | `language:dsotdq:kothian` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 109 | `$.language[109]` | `language:ggr:kraul` | GGR | P5 | partial | category, script, typicalSpeakers | typicalSpeakers:external_dependency |
| 110 | `$.language[110]` | `language:mtf:kruthik` | MTF | P5 | full | typicalSpeakers | ? |
| 111 | `$.language[111]` | `language:frhof:lantanese` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 112 | `$.language[112]` | `language:mot:leonin` | MOT | P5 | partial | script | typicalSpeakers:external_dependency |
| 113 | `$.language[113]` | `language:idrotf:loross` | IDRotF | P5 | partial | script, spoken intelligibility | written_translation:external_dependency |
| 114 | `$.language[114]` | `language:ggr:loxodon` | GGR | P5 | full | category, script, exact-tag speakers | ? |
| 115 | `$.language[115]` | `language:egw:marquesian` | EGW | P5 | full | identity only | non_mechanical |
| 116 | `$.language[116]` | `language:ggr:merfolk` | GGR | P5 | full | category, script, exact-tag speakers | ? |
| 117 | `$.language[117]` | `language:psx:merfolk` | PSX | P5 | partial | script | typicalSpeakers:external_dependency |
| 118 | `$.language[118]` | `language:frhof:midani` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 119 | `$.language[119]` | `language:scag:midani` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 120 | `$.language[120]` | `language:ggr:minotaur` | GGR | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 121 | `$.language[121]` | `language:mot:minotaur` | MOT | P5 | partial | script | typicalSpeakers:external_dependency |
| 122 | `$.language[122]` | `language:mm:modron` | MM | P5 | full | typicalSpeakers | ? |
| 123 | `$.language[123]` | `language:xmm:modron` | XMM | P5 | full | typicalSpeakers | ? |
| 124 | `$.language[124]` | `language:frhof:mulhorandi` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 125 | `$.language[125]` | `language:scag:mulhorandi` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 126 | `$.language[126]` | `language:psa:naga` | PSA | P5 | partial | identity only | typicalSpeakers:external_dependency |
| 127 | `$.language[127]` | `language:egw:naush` | EGW | P5 | full | identity only | non_mechanical |
| 128 | `$.language[128]` | `language:dsotdq:nerakese` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 129 | `$.language[129]` | `language:frhof:netherese` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 130 | `$.language[130]` | `language:hotdq:netherese` | HotDQ | P5 | full | script | ? |
| 131 | `$.language[131]` | `language:dsotdq:nordmaarian` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 132 | `$.language[132]` | `language:dsotdq:ogre` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 133 | `$.language[133]` | `language:tftyp:olman` | TftYP | P5 | full | identity only | ? |
| 134 | `$.language[134]` | `language:erlw:orc` | ERLW | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 135 | `$.language[135]` | `language:phb:orc` | PHB | P5 | partial | category, script, exact-tag speakers | hasFluffImages:out_of_approved_scope, non_mechanical |
| 136 | `$.language[136]` | `language:xphb:orc` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 137 | `$.language[137]` | `language:mm:otyugh` | MM | P5 | full | typicalSpeakers | ? |
| 138 | `$.language[138]` | `language:xmm:otyugh` | XMM | P5 | full | typicalSpeakers | ? |
| 139 | `$.language[139]` | `language:dsotdq:primordial` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 140 | `$.language[140]` | `language:erlw:primordial` | ERLW | P5 | full | category, script, typicalSpeakers | ? |
| 141 | `$.language[141]` | `language:mot:primordial` | MOT | P5 | partial | script | typicalSpeakers:external_dependency |
| 142 | `$.language[142]` | `language:phb:primordial` | PHB | P5 | partial | category, script, typicalSpeakers | dialects:reference_unresolved, hasFluffImages:out_of_approved_scope, non_mechanical |
| 143 | `$.language[143]` | `language:psi:primordial` | PSI | P5 | partial | identity only | typicalSpeakers:external_dependency |
| 144 | `$.language[144]` | `language:xphb:primordial` | XPHB | P5 | partial | category | origin:reference_unresolved, dialects:reference_unresolved |
| 145 | `$.language[145]` | `language:vgm:qualith` | VGM | P5 | partial | script | typicalSpeakers:reference_unresolved, schema_unsupported |
| 146 | `$.language[146]` | `language:erlw:quori` | ERLW | P5 | partial | category, script, explicit Inspired speaker | typicalSpeakers:search_source_ambiguous |
| 147 | `$.language[147]` | `language:frhof:rashemi` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 148 | `$.language[148]` | `language:scag:rashemi` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 149 | `$.language[149]` | `language:frhof:reghedjic` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 150 | `$.language[150]` | `language:erlw:riedran` | ERLW | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 151 | `$.language[151]` | `language:scag:roushoum` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 152 | `$.language[152]` | `language:mm:sahuagin` | MM | P5 | full | typicalSpeakers | ? |
| 153 | `$.language[153]` | `language:xmm:sahuagin` | XMM | P5 | full | typicalSpeakers | ? |
| 154 | `$.language[154]` | `language:frhof:sespech` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 155 | `$.language[155]` | `language:scag:shaaran` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, out_of_approved_scope |
| 156 | `$.language[156]` | `language:scag:shou` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 157 | `$.language[157]` | `language:psx:siren` | PSX | P5 | partial | identity only | typicalSpeakers:external_dependency |
| 158 | `$.language[158]` | `language:mm:slaad` | MM | P5 | full | typicalSpeakers | ? |
| 159 | `$.language[159]` | `language:xmm:slaad` | XMM | P5 | full | typicalSpeakers | ? |
| 160 | `$.language[160]` | `language:dsotdq:solamnic` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 161 | `$.language[161]` | `language:ggr:sphinx` | GGR | P5 | full | category, typicalSpeakers | ? |
| 162 | `$.language[162]` | `language:mm:sphinx` | MM | P5 | full | typicalSpeakers | ? |
| 163 | `$.language[163]` | `language:mot:sphinx` | MOT | P5 | partial | identity only | typicalSpeakers:external_dependency |
| 164 | `$.language[164]` | `language:dsotdq:sylvan` | DSotDQ | P5 | partial | category, script | typicalSpeakers:external_dependency |
| 165 | `$.language[165]` | `language:erlw:sylvan` | ERLW | P5 | full | category, script, typicalSpeakers | ? |
| 166 | `$.language[166]` | `language:ggr:sylvan` | GGR | P5 | partial | category, script, typicalSpeakers | typicalSpeakers:external_dependency, non_mechanical |
| 167 | `$.language[167]` | `language:mot:sylvan` | MOT | P5 | partial | script, explicit Centaur speaker | typicalSpeakers:search_source_ambiguous |
| 168 | `$.language[168]` | `language:phb:sylvan` | PHB | P5 | full | category, script, typicalSpeakers | hasFluffImages:out_of_approved_scope |
| 169 | `$.language[169]` | `language:xphb:sylvan` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 170 | `$.language[170]` | `language:frhof:thayan` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 171 | `$.language[171]` | `language:scag:thayan` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 172 | `$.language[172]` | `language:ggr:thieves-cant` | GGR | P5 | partial | category | typicalSpeakers:external_dependency |
| 173 | `$.language[173]` | `language:phb:thieves-cant` | PHB | P3 | partial | category, concealed-message capability | typicalSpeakers:reference_unresolved |
| 174 | `$.language[174]` | `language:xphb:thieves-cant` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 175 | `$.language[175]` | `language:mm:thri-kreen` | MM | P5 | full | typicalSpeakers | ? |
| 176 | `$.language[176]` | `language:xmm:thri-kreen` | XMM | P5 | full | typicalSpeakers | ? |
| 177 | `$.language[177]` | `language:vgm:tlincalli` | VGM | P5 | full | typicalSpeakers | ? |
| 178 | `$.language[178]` | `language:mm:troglodyte` | MM | P5 | full | typicalSpeakers | ? |
| 179 | `$.language[179]` | `language:xmm:troglodyte` | XMM | P5 | full | typicalSpeakers | ? |
| 180 | `$.language[180]` | `language:scag:tuigan` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 181 | `$.language[181]` | `language:frhof:turmic` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 182 | `$.language[182]` | `language:scag:turmic` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 183 | `$.language[183]` | `language:scag:uluik` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 184 | `$.language[184]` | `language:mm:umber-hulk` | MM | P5 | full | typicalSpeakers | ? |
| 185 | `$.language[185]` | `language:xmm:umber-hulk` | XMM | P5 | full | typicalSpeakers | ? |
| 186 | `$.language[186]` | `language:phb:undercommon` | PHB | P5 | partial | category, script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope |
| 187 | `$.language[187]` | `language:xphb:undercommon` | XPHB | P5 | partial | category | origin:reference_unresolved |
| 188 | `$.language[188]` | `language:frhof:untheric` | FRHoF | P5 | partial | category | origin:reference_unresolved |
| 189 | `$.language[189]` | `language:scag:untheric` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 190 | `$.language[190]` | `language:psx:vampire` | PSX | P5 | partial | identity only | typicalSpeakers:external_dependency |
| 191 | `$.language[191]` | `language:ggr:vedalken` | GGR | P5 | partial | category, script | typicalSpeakers:external_dependency, non_mechanical |
| 192 | `$.language[192]` | `language:vgm:vegepygmy` | VGM | P5 | full | typicalSpeakers | ? |
| 193 | `$.language[193]` | `language:scag:waelan` | SCAG | P5 | partial | script | typicalSpeakers:external_dependency, hasFluffImages:out_of_approved_scope, out_of_approved_scope |
| 194 | `$.language[194]` | `language:mm:winter-wolf` | MM | P5 | full | typicalSpeakers | ? |
| 195 | `$.language[195]` | `language:mm:worg` | MM | P5 | full | typicalSpeakers | ? |
| 196 | `$.language[196]` | `language:xmm:worg` | XMM | P5 | full | typicalSpeakers | ? |
| 197 | `$.language[197]` | `language:mm:yeti` | MM | P5 | full | typicalSpeakers | ? |
| 198 | `$.language[198]` | `language:xmm:yeti` | XMM | P5 | full | typicalSpeakers | ? |
| 199 | `$.language[199]` | `language:skt:yikaria` | SKT | P5 | full | typicalSpeakers | ? |
| 200 | `$.language[200]` | `language:egw:zemnian` | EGW | P5 | full | identity only | non_mechanical |
