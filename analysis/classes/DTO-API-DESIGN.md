# Proposed public DTOs and REST API

Status: implemented in Stage C. See [STAGE-C-IMPLEMENTATION.md](STAGE-C-IMPLEMENTATION.md) for actual component names, projection limits, routes, and verification. The design below records the source-derived decisions that guided implementation.

## Public representation boundary

Public records use `@JsonInclude(JsonInclude.Include.NON_EMPTY)`. They expose source-aware identities, typed choices, references, levels, numeric progressions, and explicit mechanics. Nulls, empty strings/collections/maps are omitted; meaningful false and zero are preserved. Do not construct nested objects with no meaningful fields. Use boxed optional flags to distinguish absence from explicit false. Defensive copies apply to all mutable collection arguments.

Rules prose, entry markup, source templates, copy instructions, original UIDs, pointer locators, normalization diagnostics, media paths, and internal provenance are excluded. There is no automatic public `entries` field or raw generic JSON fallback. Safe structural preservation and publication approval are separate: importing an entire record does not authorize publishing all of it.

## Detail DTO components

| Proposed public record | Components |
| --- | --- |
| `ClassDto` | `String id`, `String name`, `SourceReference source`, `String declaredEdition`, optional sidekick flag, hit dice, saving throw abilities, grouped primary abilities, starting options, multiclass options, feature unlocks, spellcasting, optional/feat progression groups, approved structured table projections, secondary sources, reprint references |
| `SubclassDto` | id, name, shortName, source, parent class reference, optional declared edition, feature unlocks, optional spellcasting/progressions/tables, secondary sources, reprint references |
| `ClassFeatureDto` | id, name, source, parent class reference, level, optional variant flag, optional consumption, ordered structured feature nodes |
| `SubclassFeatureDto` | id, name, source, parent class and subclass references, level, optional variant flag, optional consumption, ordered structured feature nodes |

Feature details never recursively embed other complete feature definitions. A class/subclass detail includes references and unlock metadata; clients retrieve referenced feature detail separately. Borrowed references retain their verified original definition ID and parent context. No public copy provenance is needed to make that reference usable.

Summary records contain only id, name, source, declared classification where available, and parent references/level for relevant kinds. Subclass summaries also carry shortName. Avoid returning all feature graphs or spell/equipment option trees in collection responses.

## Typed child DTOs

- `ClassReferenceDto(id,name,source)` and `SubclassReferenceDto(id,name,shortName,source,parentClass)`; source is the shared lightweight source form.
- `ClassFeatureUnlockDto(feature,level,gainSubclassFeature,gainSubclassFeatureHasContent)` and `SubclassFeatureUnlockDto(feature,level)`; references include verified target ID/name/source, and arrays preserve source order.
- Sealed `FeatureStructureDto` nodes for feature/optional-feature/feat references, explicit option groups, and source-authored ability DC/attack attribute declarations. Only those structures are projected from the rich content tree. Rendering `header`, `style`, and `preserve` are excluded.
- `FeatureOptionGroupDto(kind,count,options)` preserves explicit count versus absence. Child references preserve the required-option flag. An options object without count has an explicit group kind, so it is not an empty object after serialization.
- `FeatureConsumptionDto(resourceName,amount,minimumAmount,maximumAmount)` exposes only explicit declarations. It does not bind an inferred resource ID or invent an amount of one.
- `LevelCountDto(level,count)`, `SpellLevelCountDto(classLevel,spellLevel,count)`, and `SpellSlotProgressionDto(rows)` expose structured source counts; row entries carry explicit class level, spell level, and count. Zero slot counts survive serialization.
- `ClassSpellcastingDto` follows the components in the normalized design: distinct progressions, source caster code, ability, preparation timing, safe symbolic preparation expression, slot data, and spell grant groups. Absent slot data is allowed, including Pact Magic.
- `SpellReferenceDto(name,source,configuration)` has no fabricated ID. Unqualified names keep source absent. Configuration is a typed qualifier derived from observed suffixes, not a raw expression string.
- Sealed `SpellSelectionDto` variants distinguish direct spell references, unrestricted choose, filtered choose, and all-matching selection. Filter DTOs carry typed level sets, class-name selectors, school codes, and source codes, plus explicit count only when provided. An empty source `choose` filter becomes a meaningful unrestricted variant.
- `SpellGrantDto(category,gate,usage,selection)` distinguishes class-level/spell-level gates and the authored known/prepared/expanded/innate category. Usage DTOs retain daily/rest/per-spell/ability-count/resource/ritual distinctions. No generic `Map<String,Object>` or `JsonNode` escapes.
- `StartingOptionGroupDto(kind,branches)` and `StartingOptionBranchDto(key,entries)` preserve independent groups, unconditional bundles, and lettered alternatives. Typed equipment entry variants carry exact item identities, retained item-group identities, category code(s), optional quantities, copper values, or an unbound special name.
- `ProficiencyChoiceDto` variants preserve fixed grants, `any`/category selectors, source-unqualified names, count, and package alternatives. Multiclass requirements are grouped typed ability thresholds, not source strings or a character eligibility result.
- Optional-feature/feat progression DTOs preserve target catalog kind, category codes, source-authored level milestones and required references. Do not flatten their categories into one undifferentiated “feature type”.

All proposed public types are separate top-level files in a coherent `dto.classes` package (or a small number of domain subpackages where genuinely necessary). None is nested inside another DTO record.

## Tables and safe publication

Internally preserve all 59 owned level table groups and all typed cells. Public JSON may expose numeric spell matrices and independently meaningful source-typed bonus/dice/speed cells with explicit level/column association. Unreviewed presentation columns remain internal; do not publish raw labels with markup, numeric-looking strings as inferred counts, roll-table prose, or `Unlimited` as a mechanical value. If a table cannot be understood without unapproved presentation labels, omit that public table projection while retaining its complete internal form and diagnostic coverage.

Names and labels needed for choices require an explicit safe projection; do not substitute source renderer instructions or strip markup and claim the remaining prose is approved. The builder can use structured unlocks and known progressions without every descriptive feature mechanic being machine executable.

## Recommended routes

| Route | Response / purpose |
| --- | --- |
| `GET /api/classes` | Paginated `ClassSummaryDto` |
| `GET /api/classes/{id}` | `ClassDto` |
| `GET /api/classes/by-name/{name}?source=...` | Exact source-aware class lookup |
| `GET /api/classes/{id}/subclasses` | Paginated subclasses with that exact parent ID |
| `GET /api/subclasses` | Paginated subclass summaries; optional parent filter |
| `GET /api/subclasses/{id}` | `SubclassDto` |
| `GET /api/class-features` | Paginated summaries; parent/level/variant filters |
| `GET /api/class-features/{id}` | `ClassFeatureDto` |
| `GET /api/subclass-features` | Paginated summaries; parent class/subclass/level/variant filters |
| `GET /api/subclass-features/{id}` | `SubclassFeatureDto` |

Prefer separate feature routes, matching separate source identity grammars and parent types. A unified `/api/features` endpoint would need an additional discriminator and overlap in meaning with Optional Features; no current requirement justifies it. A future composite search may combine summaries without merging definitions.

Feature/subclass by-name lookup should be added only with complete parent and level qualifiers, or return a list of exact matches. Never choose the first global `(name,source)` match: 296 collision groups demonstrate why that is unsafe. IDs are the canonical frontend navigation key.

## Queries and errors

Use name and source filtering consistent with existing catalogs: case-insensitive name substring and exact case-insensitive source lookup. Add exact `parentClassId`, `parentSubclassId`, numeric `level`, and optional variant/edition filters where supported. Parent filters use the definition's authored parent; do not substitute the owner class of a borrowed reference.

Recommend page default 0, size default 20, maximum 100 on all collection endpoints, including classes for one consistent client contract. Responses use the existing explicit `PagedResponse` shape (`content`, `page`, `size`, `totalElements`, `totalPages`), with a neutral reusable placement or an equivalent class-owned record; never expose Spring `Page` JSON. No unrelated pagination refactor is part of this stage.

Default deterministic ordering: name ignoring case, source ignoring case, full parent identity, feature level where applicable, ID as final tie-breaker. Validate page/size/filter kinds. Return 400 for invalid filters/pagination, 404 for missing exact IDs or required parents, and an empty page for a valid query without matches. Services own validation/query logic and projection; controllers only bind HTTP parameters and delegate. Reuse existing error response conventions.

## Stage B serialization and service acceptance criteria

Test missing/empty/explicit false/zero values, no `{}` optional children, every polymorphic option/table/spell/feature node, an unrestricted spell selector, direct versus deferred spell identities, PHB/XPHB parent distinctions, copied feature levels, item-group choices, multiple equipment categories, independent group branches, and no internal source text or directives in JSON. Verify summary pagination, stable sort, parent filters, exact ID retrieval, and classpath loading. These are future implementation criteria; no production DTO serialization is claimed to have been exercised during Stage A.
