# Conditions and diseases source inventory

Source: `src/main/resources/data/conditionsdiseases.json`, analyzed in full. The root has three collections: `condition` (30), `disease` (29), and `status` (5). Each collection has unique `(name, source)` identities.

## Source identities and relationships

There are 17 distinct source codes across primary sources, secondary references, and reprint targets: CM, DMG, EGW, FRAiF, GoS, HftT, IDRotF, OoW, OotA, PHB, RMR, TftYP, ToA, VRGR, WDMM, XDMG, and XPHB. All resolve through the shared Source Registry (including registry aliases); no additions are required. Conditions use PHB (15) and XPHB (15). Every legacy PHB condition has RMR and HftT secondary references; its explicit reprint target is the same-named XPHB definition. Diseases use IDRotF, PHB, ToA, GoS, DMG, XDMG, EGW, OoW, OotA, FRAiF, CM, WDMM, TftYP, and VRGR. Statuses use PHB (2) and XPHB (3). Disease and status records include explicit reprints on three and two records respectively. There are no copy, template, or version directives.

## Complete observed field inventory

`normalized-field-inventory.json` records every observed root and nested property path, occurrence count, JSON value types, collection counts, unique identities, and registry coverage. The record-level fields are `name`, `source`, `page`, `entries`, optional `reprintedAs`, and source-specific optional fields `type`, `otherSources`, `srd`, `srd52`, `basicRules`, `basicRules2024`, and `hasFluffImages`. No explicit null values occur. The optional `disease.type` is the source classification “Magical Contagion” on seven records. Availability flags and image availability are source metadata.

Entries use shared 5etools text, list, named-entry, item, nested-entry, and table forms. Tables include labels, styles, and rows of cells. Diseases may have named sections such as Transmission, Infection, Symptoms, Recovery, and Immunity, and symptom item lists. Their presence does not form one consistent staged-disease schema. The inventory JSON details all nested properties including these entry forms.

## Mechanics and descriptive content

No condition has dedicated structured category, severity, saving throw, movement, attack, modifier, or condition-reference fields. Exhaustion is explicitly level-based in the rules entries: legacy PHB includes a six-row table and XPHB describes cumulative levels and level-scaled effects. These are source entry content, not a common mechanical schema, and are not converted into effect DTOs.

Disease saving throws, symptoms, duration, transmission, recovery, immunity, and progression are described in prose and entry blocks rather than dedicated fields. They are not normalized into fabricated mechanics. Inline `{@condition ...}` references occur in both condition and disease entries, but remain inside raw import entries; no markup parsing or speculative references are added. Disease mechanics that grant exhaustion or another condition do not make a disease definition an active condition.

The source descriptions may be copyrighted or otherwise publication-restricted. Public API definitions omit all entry text and markup. Import provenance retains the typed entry blocks internally. `srd`, `srd52`, `basicRules`, `basicRules2024`, and `hasFluffImages` are excluded from public output.

