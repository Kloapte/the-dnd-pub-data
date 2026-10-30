# Step 3 Phase 4 — Spell Component and Equipment Coverage

**Status:** Focused source-catalog comparison complete for review. Step 3 Phase 4 remains open. No Item classifications, schemas, fixtures, normalized data, or production code were changed.

## Method and counts

Compared all 969 spell records in the 18 files under `src/main/resources/data/spells/` with the 636 manifest records classified Step 3 and the Step 9 dispositions in the same manifest. Counted each spell source record once; material component text was compared by source identity and described form, retaining edition differences. This is a focused availability check, not an assertion that every listed component has or needs a standalone Item identity.

| Measure | Count |
|---|---:|
| Spell source records reviewed | 969 |
| Records with a material component (`components.m`) | 523 |
| Distinct exact material-component descriptions | 444 |
| Records with a specified monetary cost | 172 |
| Records marked consumed | 82 |
| Records with either a cost or consumption | 179 |
| Records with neither cost nor consumption | 344 |

The 75 records that have both a cost and a consumption flag are included in both relevant counts. For the 344 records without cost or consumption, the Step 3 Component Pouch is a direct equipment match: it holds the ordinary spell materials excluded from costed or consumed components. Eligible spellcasting focuses provide another source-supported substitution path; these are not separate matches for every ingredient.

## Coverage categories and evidence

At the spell-record level, 344 of the 523 material-bearing records have no cost or consumption and are coverable through the Step 3 Component Pouch. Of the remaining 179 records, some contain direct Step 3 matches and others contain costly or consumed requirements that need an individually described component. Component categories can overlap within a compound requirement, so the examples below are not additive counts.

### Covered by Step 3

The approved catalog covers all four standard focus families and the Component Pouch, including edition-specific identities:

- Component Pouch: `items.json $.item[480]` (PHB) and `$.item[481]` (XPHB).
- Arcane Focus group and physical forms: `items.json $.itemGroup[1..2]`; crystal, orb, rod, staff, and wand forms are in the Step 3 base-item catalog.
- Druidic Focus: `items.json $.itemGroup[28..29]`; physical options include Wooden Staff in `items-base.json $.baseitem[224..225]`, Sprig of Mistletoe at `items.json $.item[2151..2152]`, Totem at `$.item[2334]`, and Yew Wand at `$.item[2505..2506]`.
- Holy Symbol: `items.json $.itemGroup[48..49]`; source forms include Amulet (`$.item[75..76]`), Emblem (`$.item[690..691]`), and Reliquary (`$.item[1739..1740]`).
- Musical instruments: the Step 3 instrument base-item records include the source-supported instrument focus options.

Costed or consumed requirements also have direct Step 3 matches where their described form is present, including Book (`items.json $.item[267..268]`), Chest (`$.item[413..414]`), Holy Water (`$.item[1007..1008]`), Ink (`$.item[1057..1058]`), Quiver (`$.item[1718..1719]`), Rope (`$.item[990]` Hempen Rope, PHB; `$.item[1868]` Rope, XPHB), and source-defined weapons and ammunition. Examples include Borrowed Knowledge (`spells-scc.json $.spell[0].components.m`), Snare (`spells-xge.json $.spell[69].components.m`), and the XPHB Holy Symbol component in Bless (`spells-xphb.json $.spell[35].components.m`). Compound requirements may still include additional materials that have no Item match.

### Covered by Step 9

Two Scrying source records have the explicitly named Crystal Ball option described by Step 9. One additional spell record permits any magic item as an alternative; it specifies no canonical identity.

Scrying explicitly lists a crystal ball as one possible costly focus. The Step 9 Crystal Ball (`items.json $.item[524..525]`) directly describes casting Scrying; see PHB `spells-phb.json $.spell[286].components.m` and XPHB `spells-xphb.json $.spell[296].components.m`. This covers that named option, not every 1,000 gp focus alternative.

Dream of the Blue Veil allows a magic item as one alternative to a willing creature (`spells-tce.json $.spell[2].components.m`). That is a Step 9 category-level option, with no specific Item identity required by the spell.

### Spell-specific component without a standalone Item record

Many distinct ingredients and specially prepared objects are component requirements rather than equipment identities. Examples include consumed diamond dust, ruby dust, incense, rare oils, body parts, ritual offerings, and the costly custom-made vessels or miniatures. Their costs and consumption are present in the spell source fields and must remain represented in the future Spell contract; this comparison does not create Item identities for them.

For example, Revivify requires a consumed diamond worth 300 gp in PHB and XPHB (`spells-phb.json $.spell[281].components.m`; `spells-xphb.json $.spell[291].components.m`). The source has Diamond records at `items.json $.item[587..588]`, but both are out of Step 3 scope in the manifest as gemstone trade material without equipment use. This is a cross-domain component coverage decision for review, not an automatic reclassification.

### Potentially missing equipment

Two Clairvoyance records (PHB and XPHB) have the same possible equipment gap described below.

Clairvoyance specifies a focus worth 100 gp, either a jeweled horn or a glass eye, in both editions (`spells-phb.json $.spell[49].components.m`; `spells-xphb.json $.spell[51].components.m`). Step 3 has a Horn instrument, but no clearly corresponding jeweled-horn focus or glass-eye Item identity. The existing generic Arcane Focus category does not satisfy this costed component. Review whether these specially valued forms should remain spell-specific components or warrant a source-supported equipment identity.

Scrying's other 1,000 gp focus options include a silver mirror and a font filled with holy water (PHB `$.spell[286].components.m`; XPHB `$.spell[296].components.m`). The Step 3 Mirror and Steel Mirror records (`items.json $.item[1344]`, `$.item[2212]`) do not establish the specified silver mirror or font form. Only the crystal-ball option has a clear Step 9 match.

### Ambiguous

PHB Imprisonment requires a special component that varies with the chosen version of the spell but does not identify that component (`spells-phb.json $.spell[190].components.m`). Keep its identity unresolved; do not infer a matching Item. XPHB Imprisonment has a separately specified statuette component (`spells-xphb.json $.spell[196].components.m`).

## Review points

1. Confirm how the Spell domain should retain exact cost, consumption, and compound-component information when a substance has no standalone Item record.
2. Decide whether the two Clairvoyance focus forms need Item identities or should remain spell-specific component descriptions.
3. Preserve the explicit source ambiguity in PHB Imprisonment. Do not treat gemstone records as equipment solely because spells consume them.

This report does not close Phase 4, begin broader conversion, or authorize Phase 5.


