# Encounters DTO Design Checkpoint

## Corpus audit

The pinned `data/encounters.json` has one root collection, `encounter`, containing 42 definitions. The source uses 10 registered codes (EFA 1, XGE 11, BAM 3, DMG 4, BGG 18, FRAiF 1, AATM 1, GoS 1, MPP 1, SatO 1). There are 93 nested tables and 2,299 ordered rows. No `(source, name)` collision exists; names can repeat across sources (`Urban`, DMG/XGE), so the source is part of identity.

## Observed source schema

A definition has `name:string`, `source:string`, optional `page:number`, `tables:array`. Each table has `diceExpression:string`, `table:array`, and optional `minlvl:number`, `maxlvl:number`, `caption:string`, `captionPrefix:string`, `captionSuffix:string`, `footnotes:array<string>`, `page:number`, and `rollAttitude:boolean`. Each row has `min:number`, `max:number`, `result:string`, and optional `resultAttitude:string`. These are the only field paths in the complete corpus. No null or empty field values were observed. Row ranges are numeric; dice expressions are authored strings and remain unparsed. The table field is the authored row collection; there is no separate table category/environment field.

## Domain and identity

A catalog definition is the authored encounter record, not an individual row. ID is `encounter:<exact-source>:<normalized-name-slug>`. Complete-corpus validation found no collisions. Repeated names across sources remain distinct. There are no `_copy`, `_versions`, reprint, or explicit source-reference metadata fields; source and page are retained as authored provenance.

## Internal/public split

Strict DTEs model every observed path, including every result string and footnote. Immutable normalized definitions retain ordered tables and rows exactly, including tagged markup. Public summary/detail projections expose ID, name, exact source, page, table count, row count, and safe table metadata (dice expression, level bounds, roll-attitude flag, and authored row range bounds/count). They omit captions, footnotes, result strings, tags, and media. This is a licensing boundary, not a source omission. No mechanics are inferred from result text.

## References and behavior

Markup appears only inside string-valued table results. The design preserves the full result internally, withholds it publicly, and selectively inventories explicit markup tags for target kinds already supported by the shared resolver. Creature names/tags are not inferred as links because no Monster adapter exists. The catalog does not roll dice, generate encounters, balance encounters, or execute outcomes.

## Checkpoint status

Source paths, types, identities, and public projection decisions have been audited before implementation. The source registry already contains all 10 exact source codes. Proceeding with the catalog/API implementation.

