# Objects Reference Inventory

Markup scan covered every string in all 37 object records, including entries and action text. Occurrences are
authored source links, not inferred entity relationships:

| Tag | Count | Target handling |
|---|---:|---|
| `@spell` | 2 | Existing Spell adapter; included in startup resolution and public detail projection |
| `@item` | 2 | One source-qualified UID uses the Item adapter; one three-part extended markup token remains deferred and has no target ID |
| `@action` | 21 | No Action target adapter; source text retained internally |
| `@creature` | 3 | No Monster target adapter; source text retained internally |
| `@variantrule` | 7 | Unsupported target kind; source text retained internally |
| `@condition` | 2 | No Condition target adapter; source text retained internally |
| `@hazard` | 3 | Unsupported target kind; source text retained internally |
| `@subclassFeature` | 4 | Unsupported target kind; source text retained internally |
| `@adventure` | 2 | Unsupported target kind; source text retained internally |
| `@book` | 2 | Source/presentation markup; not a catalog definition reference |
| `@quickref` | 2 | Quick-reference markup; no resolver target |
| `@table` | 3 | Table formatting markup, retained internally |
| `@hit`, `@damage`, `@dice`, `@dc`, `@atkr`, `@h`, `@note`, `@dcYourSpellSave`, `@actSave`, `@actSaveFail`, `@actSaveSuccess` | 84 combined | Mechanical/rendering markup retained within internal authored text; never evaluated |

The source includes explicit reprint UIDs on seven definitions, all with registered source codes. These are retained as
lineage and are not resolver references. No target IDs are inferred from names or prose. Four Object occurrences are
added independently to the resolver snapshot; the existing 3,950 occurrences and their outcome counts must remain
unchanged.
