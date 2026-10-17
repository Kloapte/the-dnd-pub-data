// Current manually reviewed source, normalization and publication dispositions.
export const modelReview = {
    "Monsters": {
        "normalized": [
            "MonsterDto",
            "Statistics",
            "Defenses",
            "Features",
            "SpellcastingBlock"
        ],
        "public": [
            "MonsterDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/monster/MonsterMapper.java",
        "retained": "Resolved trees map to typed statistics, conditional defenses, rich content, action buckets and spell usage maps. Classification tags live separately in MonsterClassification. EntryDte validates authored content; MonsterMapper reads resolved JSON rather than mapping EntryDte instances.",
        "exposed": "Stats, defenses, rich Feature.entries and spellcasting headers/footer are existing public content. Search condition/damage tags do not establish effect edges.",
        "gap": "No formula/branch/target/Multiattack semantic model. Separate support gap: 188 legendary-group records have no discovered runtime body loader/model; only name/source group references survive."
    },
    "Spells": {
        "normalized": [
            "SpellDefinition",
            "SpellMechanics",
            "SpellScaling"
        ],
        "public": [
            "SpellDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/spell/api/SpellPublicMapper.java",
        "retained": "Casting alternatives, component variants, range geometry, duration/concentration, classifications and scaling already normalized. Shared entries and higher-level entries remain internally ordered.",
        "exposed": "Existing structured casting/range/component/duration/classification/scaling metadata. Text qualifiers use existing withholding/presence conventions.",
        "gap": "Damage/healing expressions, branch associations, target counts, secondary effects and prose area dimensions are not semantic effect models."
    },
    "Actions": {
        "normalized": [
            "ActionDefinition"
        ],
        "public": [
            "ActionDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/action/ActionMapper.java",
        "retained": "Ordered timing variants normalized; entry trees retained in import records.",
        "exposed": "Timing and identity/provenance, without rule-entry prose.",
        "gap": "Checks, outcomes, action restrictions and contest relationships remain entry semantics."
    },
    "Feats": {
        "normalized": [
            "FeatDefinition",
            "FeatMechanics",
            "FeatPrerequisiteProficiencyGroup"
        ],
        "public": [
            "FeatDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/feat/FeatMapper.java",
        "retained": "Ability/prerequisite/spell metadata plus Batch 3A grant/defense/sense/progression bundles and Batch 3B proficiency requirement groups are normalized. Entries and version lineage retained.",
        "exposed": "Existing ability/prerequisite/spell/repeatability metadata. New Batch 3A/3B fields remain internal.",
        "gap": "Prose resource resets and conditional benefits lack semantic models. G3/G4 are resolved, not current omissions."
    },
    "Optional Features": {
        "normalized": [
            "OptionalFeatureDefinition"
        ],
        "public": [
            "OptionalFeatureDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/optionalfeature/OptionalFeatureMapper.java",
        "retained": "Consumption amount/min/max, grants, senses, prerequisites and progression normalized. Entry trees stay in import records.",
        "exposed": "Existing structured fields and selected prerequisite text; seven authored roots/14 strings require publication review.",
        "gap": "A consumes declaration gives no generalized pool/reset rule or full effect."
    },
    "Classes": {
        "normalized": [
            "ClassDefinition",
            "ClassProgressionTable",
            "ClassSpellcasting"
        ],
        "public": [
            "ClassDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/classes/ClassPublicMapper.java",
        "retained": "Hit dice, saves, starting/multiclass options, feature graph, schedules and ordered progression cells normalized.",
        "exposed": "Structured mechanics plus Batch 1 positionedCells. Presentation cells remain withheld.",
        "gap": "Numeric-looking source text remains deliberately text; preparation formulas stay unevaluated. G1 is resolved."
    },
    "Subclasses": {
        "normalized": [
            "SubclassDefinition",
            "ClassProgressionTable"
        ],
        "public": [
            "SubclassDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/classes/ClassPublicMapper.java",
        "retained": "Parent context, copy-effective graph, spell overrides and progression tables normalized.",
        "exposed": "Structured graph/schedules/tables with original safe-cell column indexes.",
        "gap": "Conditional feature replacements/effects are not inferred; no remaining G1 column-position defect."
    },
    "Class Features": {
        "normalized": [
            "ClassFeatureDefinition",
            "ClassFeatureNode",
            "ClassFeatureAbilityDeclaration",
            "ClassFeatureChoice"
        ],
        "public": [
            "ClassFeatureDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/importer/classes/ClassFeatureContentNormalizer.java",
        "retained": "Typed content graph preserves sections, tables, options, references, ability declarations and consumption. Parent/level identity preserved.",
        "exposed": "References, reference-backed choices and consumption; ability declarations, prose and feature-entry tables withheld.",
        "gap": "Already typed abilityDc/abilityAttackMod declarations need no prose extraction. Non-reference choice options are absent from the public choice list; this is an incomplete public view, not a lost internal grant."
    },
    "Subclass Features": {
        "normalized": [
            "SubclassFeatureDefinition",
            "ClassFeatureNode",
            "ClassFeatureAbilityDeclaration",
            "ClassFeatureChoice"
        ],
        "public": [
            "SubclassFeatureDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/importer/classes/ClassFeatureContentNormalizer.java",
        "retained": "Same typed content graph, level/parent/copy context and consumption.",
        "exposed": "References, reference-backed choices and consumption. Full internal option content remains withheld.",
        "gap": "Do not treat a count plus a public subset of referenced options as the complete authored choice universe."
    },
    "Backgrounds": {
        "normalized": [
            "BackgroundDefinition"
        ],
        "public": [
            "BackgroundDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/background/BackgroundMapper.java",
        "retained": "Ability/proficiency/feat/equipment/spell/prerequisite groups normalized after copy handling; entries remain in import provenance.",
        "exposed": "Existing structured options; descriptive feature/characteristic-table content withheld.",
        "gap": "No demonstrated new grant-loss defect. Background feature prose does not establish unconditional mechanical grants."
    },
    "Races and Subraces": {
        "normalized": [
            "RaceDefinition"
        ],
        "public": [
            "RaceDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/race/RaceDefinitionNormalizer.java",
        "retained": "Abilities, movement numeric/flag alternatives, senses, defenses, grants, spell schedules and lineage normalized. Entry trees retained internally.",
        "exposed": "Existing structured metadata/grants, without trait prose.",
        "gap": "Conditional traits and resets remain prose. Parent identity does not itself imply automatic merging of every parent mechanic."
    },
    "Items and Base Items": {
        "normalized": [
            "ItemDefinition",
            "ItemFocus",
            "ItemSignedBonuses",
            "ItemChargeDto",
            "ItemProtectionDto"
        ],
        "public": [
            "ItemDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/item/ItemMapper.java",
        "retained": "Damage/type, AC, properties, charges, attunement, abilities, bonuses, defenses, light/capacity/movement and spell schedules normalized. Batch 2 preserves focus roles; Batch 4 adds exact signed-token/integer values.",
        "exposed": "Legacy structured equipment fields, legacy bonus strings and charge strings. New focus/signed-bonus structures remain internal. Some charge strings already contain markup.",
        "gap": "Focus and signed-bonus gaps are resolved. Item property/mastery UID/note is not a model of the referenced rule effect. Charge/reset expression semantics and conditional effects remain separate."
    },
    "Magic Variants": {
        "normalized": [
            "MagicVariantDto"
        ],
        "public": [
            "MagicVariantDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/magicvariant/MagicVariantMapper.java",
        "retained": "Requirement alternatives, exclusions and inherited typed transforms retained; matching is distinct from concrete item generation.",
        "exposed": "Existing reviewed matching/transformation metadata; inherited prose withheld.",
        "gap": "Do not apply Item signed-bonus grammar to every variant expression without a separate source audit; generated/conditional transforms differ."
    },
    "Conditions": {
        "normalized": [
            "ConditionDefinition"
        ],
        "public": [
            "ConditionDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/condition/ConditionMapper.java",
        "retained": "Definition/provenance normalized; typed rule entries retained in import records. Five Status roots are additionally parsed/validated by ConditionCatalog but only their count is retained there.",
        "exposed": "Identity/source/reprint metadata, without condition instructions.",
        "gap": "Restrictions, exceptions and condition interactions lack effect models; Status descriptions are not retained in normalized definitions."
    },
    "Diseases": {
        "normalized": [
            "DiseaseDefinition"
        ],
        "public": [
            "DiseaseDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/condition/DiseaseMapper.java",
        "retained": "Identity/classification normalized; full typed entries retained internally.",
        "exposed": "Identity/classification/provenance metadata.",
        "gap": "Infection, stages, checks, treatment and recovery are descriptive rather than a structured disease process."
    },
    "Languages": {
        "normalized": [
            "NormalizedLanguageDefinition"
        ],
        "public": [
            "LanguageDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/language/LanguageMapper.java",
        "retained": "Classification/script/speaker/dialect/source metadata; internal entries where authored.",
        "exposed": "Existing language metadata.",
        "gap": "Language descriptions/mentions are not proficiency grants; no gameplay extraction justified."
    },
    "Skills": {
        "normalized": [
            "SkillDefinition"
        ],
        "public": [
            "SkillDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/skill/SkillMapper.java",
        "retained": "Associated ability normalized; descriptive examples retained in import records.",
        "exposed": "Associated ability and identity.",
        "gap": "Example DCs or alternate abilities are not a universal check specification."
    },
    "Senses": {
        "normalized": [
            "SenseDefinition"
        ],
        "public": [
            "SenseDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/sense/SenseMapper.java",
        "retained": "Identity/reprints normalized; definition text retained in source/import content.",
        "exposed": "Metadata only.",
        "gap": "No common numerical sense range exists on these definitions; owner-specific ranges must remain distinct."
    },
    "Objects": {
        "normalized": [
            "ObjectDefinition"
        ],
        "public": [
            "ObjectDetailDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/object/ObjectDefinitionMapper.java",
        "retained": "Stats, movement, defenses and dedicated typed Object actions retained, including seven explicit attack/hit containers.",
        "exposed": "Safe stats; descriptive special values use presence conventions; actions withheld.",
        "gap": "Container boundaries are direct. Targets, damage type and riders usually remain text; no Object-to-creature equivalence."
    },
    "Bastion Facilities": {
        "normalized": [
            "FacilityDefinition"
        ],
        "public": [
            "FacilityDetailDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/bastion/FacilityMapper.java",
        "retained": "Levels, space, hireling variants, order labels and structured prerequisites normalized; entries retained internally.",
        "exposed": "Metadata and reviewed prerequisite structure.",
        "gap": "Order label does not encode production, timing, cost, granting or operating rules."
    },
    "Rewards": {
        "normalized": [
            "RewardDefinition"
        ],
        "public": [
            "RewardDetailDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/reward/RewardMapper.java",
        "retained": "Type/rarity and spell benefits normalized; typed entries include three inline Object statblocks.",
        "exposed": "Metadata and existing structured spell benefits.",
        "gap": "Other benefit prose/inline statblocks withheld; not an automatic generic reward grant."
    },
    "Traps": {
        "normalized": [
            "TrapDefinition"
        ],
        "public": [
            "TrapDetailDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/traphazard/TrapMapper.java",
        "retained": "Rating/timing and distinct trigger/effect/stage/countermeasure containers retained; stage entries retain their DTE structures.",
        "exposed": "Rating/type and selected timing labels.",
        "gap": "Stage role explicit; DC-to-check, saving throw branches, reset and damage applications require interpretation."
    },
    "Hazards": {
        "normalized": [
            "HazardDefinition"
        ],
        "public": [
            "HazardDetailDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/traphazard/HazardMapper.java",
        "retained": "Type/rating metadata plus typed descriptive entries.",
        "exposed": "Identity/type/rating only.",
        "gap": "Environmental effects and exceptions remain prose; no generic trigger/effect model supplied."
    },
    "Vehicles": {
        "normalized": [
            "VehicleDefinition",
            "VehicleMechanics"
        ],
        "public": [
            "VehicleDetailDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/vehicle/VehicleDetailDto.java",
        "retained": "Base/component stats, capacity, movement modes, thresholds and action/station content retained.",
        "exposed": "Selected safe stats/capacities/components; top-level AC/HP currently summary-only.",
        "gap": "Component mode-plus-entry records lack numerical speed leaves. Action-station effects/repair instructions stay prose."
    },
    "Vehicle Upgrades": {
        "normalized": [
            "VehicleUpgradeDefinition"
        ],
        "public": [
            "VehicleUpgradeDetailDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/vehicle/VehicleUpgradeDetailDto.java",
        "retained": "Ordered authored upgrade categories and typed entries.",
        "exposed": "Identity/categories only.",
        "gap": "No authored structured cost/compatibility/benefit schema; shared category codes do not define compatible vehicles."
    },
    "Variant Rules": {
        "normalized": [
            "VariantRuleDefinition"
        ],
        "public": [
            "VariantRuleDetailDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/variantrule/VariantRuleMapper.java",
        "retained": "Source classifications, recursive sections and typed tables retained.",
        "exposed": "Metadata and structural counts/references.",
        "gap": "Examples and alternative rule instructions are not executable overrides or always-active modifiers."
    },
    "Treasure Tables": {
        "normalized": [
            "TreasureTableDefinition",
            "IndividualTreasureDefinition",
            "TreasureHoardDefinition",
            "MagicItemTableDefinition"
        ],
        "public": [
            "TreasureTableDetailDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/treasure/TreasureTableMapper.java",
        "retained": "Six typed table variants plus auxiliary dragon rows preserve exact ranges/order, amounts, currencies, item selectors and nested results.",
        "exposed": "Safe metadata, counts and numeric range summaries; complete results/expressions withheld.",
        "gap": "Not shared EntryDte tables. Formula parsing may retain quantity syntax; no uniform roll system, item grant or filter expansion."
    },
    "Encounters": {
        "normalized": [
            "EncounterDefinition"
        ],
        "public": [
            "EncounterDetailDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/encounter/EncounterMapper.java",
        "retained": "93 dedicated tables/2,299 ordered results with dice, range, level and attitude flags retained.",
        "exposed": "Table/range/count/level metadata; result prose withheld.",
        "gap": "Dice in results do not create structured creature quantities, composition or spawn instructions."
    },
    "Character Creation Options": {
        "normalized": [
            "CharacterOptionDefinition"
        ],
        "public": [
            "CharacterCreationOptionDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/charoption/CharacterOptionMapper.java",
        "retained": "Category/prerequisite metadata and full typed entry structures retained.",
        "exposed": "Metadata/prerequisite groups.",
        "gap": "Table/entry benefits and gifts remain descriptive; no automatic eligibility/selected benefit."
    },
    "Cults": {
        "normalized": [
            "CultDefinition"
        ],
        "public": [
            "CultDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/cultboon/CultBoonMapper.java",
        "retained": "Type, goal/cultists/signature-spell wrappers and typed entries internally retained.",
        "exposed": "Identity/classification/reference metadata.",
        "gap": "Signature-spell wrapper text is not a typed spell-grant schedule; powers need prose interpretation."
    },
    "Boons": {
        "normalized": [
            "BoonDefinition"
        ],
        "public": [
            "BoonDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/cultboon/CultBoonMapper.java",
        "retained": "Type, ability/signature-spell wrappers and entries retained separately from Rewards.",
        "exposed": "Identity/classification/reference metadata.",
        "gap": "abilityEntry is descriptive text, not a typed ability-score modifier."
    },
    "Deities": {
        "normalized": [
            "DeityDefinition"
        ],
        "public": [
            "DeityDetailDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/deity/DeityMapper.java",
        "retained": "Pantheon-aware identity, alignment/domain and source metadata; lore/piety remains internal.",
        "exposed": "Existing safe grouping/symbol/name/source metadata.",
        "gap": "Piety text is not a generalized resource; domains are labels, not subclass relationships."
    },
    "Name Tables": {
        "normalized": [
            "NameTableDefinition"
        ],
        "public": [
            "NameTableDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/nametable/NameTableMapper.java",
        "retained": "Dedicated option/row types preserve 61 tables/2,822 rows and exact terminal-zero ranges.",
        "exposed": "Metadata/counts only.",
        "gap": "Rollable selection content without gameplay effects; not a shared EntryDte table."
    },
    "Adventure Index": {
        "normalized": [
            "AdventureDefinition"
        ],
        "public": [
            "AdventureDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/adventure/AdventureMapper.java",
        "retained": "Publication/navigation and authored adventure-level metadata, without chapter bodies.",
        "exposed": "Metadata index.",
        "gap": "Level recommendations/headings do not define encounter difficulty or rules mechanics."
    },
    "Books": {
        "normalized": [
            "PublicationDefinition"
        ],
        "public": [
            "PublicationDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/book/PublicationMapper.java",
        "retained": "Publication/source/navigation data, without full book bodies.",
        "exposed": "Existing publication metadata.",
        "gap": "No rules-body mechanics exist in this source index."
    },
    "Recipes": {
        "normalized": [
            "RecipeDefinition"
        ],
        "public": [
            "RecipeDetailDto"
        ],
        "mapping": "src/main/java/com/thedndpub/data/dto/recipe/RecipeMapper.java",
        "retained": "Dedicated culinary ingredient/equipment/instruction/cook-note trees, quantities and servings retained.",
        "exposed": "Existing metadata-only food/cocktail index.",
        "gap": "No gameplay crafting domain. Numerical/conditional recipe instructions are negative controls for mechanics detection."
    }
};
