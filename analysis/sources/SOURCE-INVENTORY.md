# Source inventory

## Scope and counts

- Language corpus: 201 language definitions and 6 script definitions; codes are collected from language and language-script primaries, additional/other sources, and reprint targets.
- Monster corpus: 111 packaged `bestiary-*.json` files; codes are collected from primary sources, `otherSources`, `referenceSources`, and reprint targets. Existing normalization yields 4,851 monsters.
- Book Catalog: 66 unique publication records; only each record's `source` is counted as a source identity. Book `id` differences are modeled as aliases.
- Distinct source identities in the union: **175** (case-insensitive deduplication; original spelling retained in the registry). 66 have publication metadata; 109 do not.
- Six publication-ID aliases resolve to existing source identities: PS-Z → PSZ, PS-I → PSI, PS-K → PSK, PS-A → PSA, PS-X → PSX, and PS-D → PSD.

## Deduplicated inventory

| Source code | Occurrence roles | Publication metadata |
|---|---|---|
| `AAG` | Publication record | Astral Adventurer's Guide |
| `AATM` | Monster primary, Monster secondary, Publication record | Adventure Atlas: The Mortuary |
| `ABH` | Monster primary, Monster secondary, Publication record | Astarion's Book of Hungers |
| `AI` | Language primary, Monster primary, Publication record | Acquisitions Incorporated |
| `AitFR-AVT` | Monster secondary | Unavailable in Book Catalog |
| `AitFR-DN` | Monster primary | Unavailable in Book Catalog |
| `AitFR-FCD` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `AitFR-ISF` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `AitFR-THP` | Monster primary | Unavailable in Book Catalog |
| `AL` | Publication record | Adventurers League |
| `AU` | Monster primary, Publication record | Arcana Unleashed |
| `AUD` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `AWM` | Monster primary, Publication record | Adventure with Muk |
| `AZfyT` | Monster secondary | Unavailable in Book Catalog |
| `BAM` | Monster primary, Publication record | Boo's Astral Menagerie |
| `BGDIA` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `BGG` | Monster primary, Publication record | Bigby Presents: Glory of the Giants |
| `BMT` | Monster primary, Monster secondary, Publication record | The Book of Many Things |
| `BQDD` | Monster secondary | Unavailable in Book Catalog |
| `BQGT` | Monster secondary | Unavailable in Book Catalog |
| `CaBoMP` | Publication record | Crochet: A Book of Many Patterns |
| `CM` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `CoA` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `CoS` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `CRCotN` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `DC` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `DD` | Monster secondary | Unavailable in Book Catalog |
| `DIP` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `DitLCoT` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `DMG` | Monster primary, Publication record | Dungeon Master's Guide (2014) |
| `DMTCRG` | Publication record | The Deck of Many Things: Card Reference Guide |
| `DoD` | Monster primary, Publication record | Domains of Delight |
| `DoSI` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `DrDe` | Monster secondary | Unavailable in Book Catalog |
| `DrDe-ACfaS` | Monster secondary | Unavailable in Book Catalog |
| `DrDe-BD` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `DrDe-BtS` | Monster secondary | Unavailable in Book Catalog |
| `DrDe-DaS` | Monster secondary | Unavailable in Book Catalog |
| `DrDe-DotSC` | Monster secondary | Unavailable in Book Catalog |
| `DrDe-FWtVC` | Monster secondary | Unavailable in Book Catalog |
| `DrDe-SD` | Monster secondary | Unavailable in Book Catalog |
| `DrDe-TDoN` | Monster secondary | Unavailable in Book Catalog |
| `DrDe-TFV` | Monster secondary | Unavailable in Book Catalog |
| `DrDe-TWoO` | Monster secondary | Unavailable in Book Catalog |
| `DSotDQ` | Language primary, Monster primary, Monster secondary | Unavailable in Book Catalog |
| `EEPC` | Language secondary | Unavailable in Book Catalog |
| `EFA` | Monster primary, Monster reprint target, Monster secondary, Publication record | Eberron: Forge of the Artificer |
| `EFR` | Monster secondary | Unavailable in Book Catalog |
| `EGW` | Language primary, Monster primary, Monster secondary, Publication record | Explorer's Guide to Wildemount |
| `ERLW` | Language primary, Monster primary, Monster secondary, Publication record | Eberron: Rising from the Last War |
| `ESK` | Monster primary | Unavailable in Book Catalog |
| `FFotR` | Monster secondary | Unavailable in Book Catalog |
| `FRAiF` | Monster primary, Monster reprint target, Monster secondary, Publication record | Forgotten Realms: Adventures in FaerÃ»n |
| `FRHoF` | Language primary, Language reprint target, Monster secondary, Publication record | Forgotten Realms: Heroes of FaerÃ»n |
| `FS` | Monster secondary | Unavailable in Book Catalog |
| `FTD` | Monster primary, Publication record | Fizban's Treasury of Dragons |
| `GGR` | Language primary, Language secondary, Monster primary, Publication record | Guildmasters' Guide to Ravnica |
| `GoS` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `GotSF` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `HAT-TG` | Monster primary, Publication record | Thieves' Gallery |
| `HBTD` | Monster secondary | Unavailable in Book Catalog |
| `HF` | Publication record | Heroes' Feast |
| `HFFotM` | Publication record | Heroes' Feast Flavors of the Multiverse |
| `HFLotT` | Publication record | Heroes' Feast Legends of the Table |
| `HFStCM` | Monster secondary | Unavailable in Book Catalog |
| `HftT` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `HoL` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `HotB` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `HotDQ` | Language primary, Monster primary, Monster secondary | Unavailable in Book Catalog |
| `IDRotF` | Language primary, Monster primary, Monster secondary | Unavailable in Book Catalog |
| `IMR` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `JttRC` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `KftGV` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `KKW` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `LFL` | Monster primary, Monster secondary, Publication record | Lorwyn: First Light |
| `LK` | Monster secondary | Unavailable in Book Catalog |
| `LLK` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `LMoP` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `LoX` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `LR` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `LRDT` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `MaBJoV` | Monster primary, Publication record | Minsc and Boo's Journal of Villainy |
| `MCV1SC` | Monster primary, Publication record | Monstrous Compendium Volume 1: Spelljammer Creatures |
| `MCV2DC` | Monster primary | Unavailable in Book Catalog |
| `MCV3MC` | Monster primary | Unavailable in Book Catalog |
| `MCV4EC` | Monster primary, Publication record | Monstrous Compendium Volume 4: Eldraine Creatures |
| `MFF` | Monster primary | Unavailable in Book Catalog |
| `MGELFT` | Monster primary, Publication record | Muk's Guide to Everything He Learned From Tasha |
| `MisMV1` | Monster primary | Unavailable in Book Catalog |
| `MM` | Language primary, Monster primary, Publication record | Monster Manual (2014) |
| `MOT` | Language primary, Monster primary, Monster secondary, Publication record | Mythic Odysseys of Theros |
| `MPMM` | Monster primary, Monster reprint target, Publication record | Mordenkainen Presents: Monsters of the Multiverse |
| `MPP` | Monster primary, Monster secondary, Publication record | Morte's Planar Parade |
| `MTF` | Language primary, Language secondary, Monster primary, Monster reprint target, Monster secondary, Publication record | Mordenkainen's Tome of Foes |
| `NF` | Monster primary, Monster secondary, Publication record | Netheril's Fall |
| `NRH-ASS` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `NRH-AT` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `NRH-AVitW` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `NRH-AWoL` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `NRH-CoI` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `NRH-TCMC` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `NRH-TLT` | Monster secondary | Unavailable in Book Catalog |
| `OGA` | Publication record | One Grung Above |
| `OotA` | Language primary, Monster primary, Monster secondary | Unavailable in Book Catalog |
| `OoW` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `PaBTSO` | Monster primary, Monster reprint target, Monster secondary | Unavailable in Book Catalog |
| `PaF` | Publication record | Puncheons and Flagons |
| `PHB` | Language primary, Language script, Language secondary, Monster primary, Publication record | Player's Handbook (2014) |
| `PotA` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `PSA` | Language primary, Monster primary, Monster secondary, Publication record | Plane Shift: Amonkhet |
| `PSD` | Language primary, Monster primary, Publication record | Plane Shift: Dominaria |
| `PSI` | Language primary, Monster primary, Monster secondary, Publication record | Plane Shift: Innistrad |
| `PSK` | Language secondary, Monster primary, Publication record | Plane Shift: Kaladesh |
| `PSX` | Language primary, Monster primary, Monster secondary, Publication record | Plane Shift: Ixalan |
| `PSZ` | Monster primary, Monster secondary, Publication record | Plane Shift: Zendikar |
| `QftIS` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `RHW` | Monster primary, Monster reprint target, Monster secondary, Publication record | Ravenloft: The Horrors Within |
| `RMBRE` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `RMR` | Publication record | Dungeons & Dragons vs. Rick and Morty: Basic Rules |
| `RoT` | Language primary, Monster primary, Monster secondary | Unavailable in Book Catalog |
| `RtG` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `RWG` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `SAC` | Publication record | Sage Advice Compendium (2014) |
| `SADS` | Monster primary | Unavailable in Book Catalog |
| `SatO` | Monster secondary, Publication record | Sigil and the Outlands |
| `SCAG` | Language primary, Publication record | Sword Coast Adventurer's Guide |
| `SCC` | Monster primary, Publication record | Strixhaven: A Curriculum of Chaos |
| `SCC-ARiR` | Monster secondary | Unavailable in Book Catalog |
| `SCC-CK` | Monster secondary | Unavailable in Book Catalog |
| `SCC-HfMT` | Monster secondary | Unavailable in Book Catalog |
| `SCC-TMM` | Monster secondary | Unavailable in Book Catalog |
| `ScoEE` | Monster secondary | Unavailable in Book Catalog |
| `Screen` | Publication record | Dungeon Master's Screen |
| `ScreenDungeonKit` | Publication record | Dungeon Master's Screen: Dungeon Kit |
| `ScreenSpelljammer` | Publication record | Dungeon Master's Screen: Spelljammer |
| `ScreenWildernessKit` | Publication record | Dungeon Master's Screen: Wilderness Kit |
| `SDW` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `SjA` | Monster secondary | Unavailable in Book Catalog |
| `SKT` | Language primary, Monster primary, Monster secondary | Unavailable in Book Catalog |
| `SLW` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `TCE` | Monster primary, Monster secondary, Publication record | Tasha's Cauldron of Everything |
| `TD` | Publication record | Tarot Deck |
| `TftYP` | Language primary, Monster primary, Monster secondary | Unavailable in Book Catalog |
| `TftYP-AtG` | Monster secondary | Unavailable in Book Catalog |
| `TftYP-DiT` | Monster secondary | Unavailable in Book Catalog |
| `TftYP-TFoF` | Monster secondary | Unavailable in Book Catalog |
| `TftYP-THSoT` | Monster secondary | Unavailable in Book Catalog |
| `TftYP-ToH` | Monster secondary | Unavailable in Book Catalog |
| `TftYP-TSC` | Monster secondary | Unavailable in Book Catalog |
| `TftYP-WPM` | Monster secondary | Unavailable in Book Catalog |
| `TLK` | Monster secondary | Unavailable in Book Catalog |
| `ToA` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `ToD` | Language secondary, Monster secondary | Unavailable in Book Catalog |
| `ToFW` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `ToR` | Monster secondary | Unavailable in Book Catalog |
| `TTP` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `US` | Monster secondary | Unavailable in Book Catalog |
| `UtHftLH` | Monster secondary | Unavailable in Book Catalog |
| `VD` | Monster primary | Unavailable in Book Catalog |
| `VEoR` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `VGM` | Language primary, Monster primary, Monster secondary, Publication record | Volo's Guide to Monsters |
| `VNotEE` | Monster secondary | Unavailable in Book Catalog |
| `VRGR` | Monster primary, Publication record | Van Richten's Guide to Ravenloft |
| `WBtW` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `WDH` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `WDMM` | Monster primary, Monster secondary | Unavailable in Book Catalog |
| `WttHC` | Monster primary | Unavailable in Book Catalog |
| `XDMG` | Monster primary, Monster reprint target, Publication record | Dungeon Master's Guide (2024) |
| `XGE` | Monster primary, Publication record | Xanathar's Guide to Everything |
| `XMM` | Language primary, Language reprint target, Monster primary, Monster reprint target, Publication record | Monster Manual (2025) |
| `XMtS` | Monster secondary | Unavailable in Book Catalog |
| `XPHB` | Language primary, Language script, Language reprint target, Monster primary, Monster reprint target, Monster secondary, Publication record | Player's Handbook (2024) |
| `XSAC` | Publication record | Sage Advice Compendium (2025) |
| `XScreen` | Publication record | Dungeon Master's Screen (2024) |
| `XScreenRHW` | Publication record | Dungeon Master's Screen; Ravenloft: The Horrors Within |

## Unresolved and ambiguous references

No title, date, publication class, or source relationship was inferred from a code alone. A code without a Book Catalog match remains a valid source identity with unavailable publication metadata. These unresolved codes include adventures, digital/organized-play releases, supplements, and other references, but the imported data does not establish a reliable classification for each individual code.

Known primary-source gaps recorded by the existing analyses:

- Languages (7): DSotDQ, HotDQ, IDRotF, OotA, RoT, SKT, TftYP.
- Monsters (66): AUD, AitFR-DN, AitFR-FCD, AitFR-ISF, AitFR-THP, BGDIA, CM, CRCotN, CoA, CoS, DC, DIP, DSotDQ, DitLCoT, DoSI, DrDe-BD, ESK, GoS, GotSF, HftT, HoL, HotB, HotDQ, IDRotF, IMR, JttRC, KKW, KftGV, LLK, LMoP, LR, LRDT, LoX, MCV2DC, MCV3MC, MFF, MisMV1, NRH-ASS, NRH-AT, NRH-AVitW, NRH-AWoL, NRH-CoI, NRH-TCMC, OoW, OotA, PaBTSO, PotA, QftIS, RMBRE, RWG, RoT, RtG, SADS, SDW, SKT, SLW, TTP, TftYP, ToA, ToFW, VD, VEoR, WBtW, WDH, WDMM, WttHC.
- Language secondary references: EEPC and ToD remain unresolved against the Book Catalog.
- Monster secondary/reference sources: 21 distinct codes remain unresolved against the Book Catalog.
- Reprint targets: one monster reprint target remains unresolved in the current Book Catalog/source analysis; no target publication definition is fabricated.
- Book `parentSource` and title aliases are publication relationships, not source-code aliases. Preserve those relationships only as stored on the publication record; shared title aliases can be ambiguous.

## Evidence and limits

- The only existing publication metadata registry is `src/main/resources/data/books.json`; no other source metadata registry was found.
- Imported languages and monsters supply source codes, page locators, secondary references, and reprint strings. They do not provide a general source title/type/date registry.
- `books.json` supplies publication metadata for 66 source codes. Its editorial `group` labels are not a complete, authoritative publication-type taxonomy.
- Registry entries intentionally retain no inferred type or metadata when evidence is absent. Source code case/spelling is preserved for display and IDs; lookups are case-insensitive.


