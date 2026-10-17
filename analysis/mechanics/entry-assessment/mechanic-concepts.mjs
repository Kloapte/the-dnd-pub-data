// Analysis vocabulary; observed syntax is not an applied mechanical effect.
export const concepts = [
    {
        "id": "damage",
        "name": "Damage expressions and types",
        "fields": [
            "dmg1",
            "dmg2",
            "dmgType",
            "damageInflict",
            "damageTags",
            "damageTagsLegendary",
            "damageTagsSpell"
        ],
        "tags": [
            "damage",
            "scaledamage"
        ],
        "marker": "\\bdamage\\b",
        "scope": "Expression/classification, not applied damage"
    },
    {
        "id": "healing",
        "name": "Healing",
        "fields": [],
        "tags": [],
        "marker": "\\b(healing|regain(?:s)? hit points|restore(?:s)? hit points)\\b",
        "scope": "A healing effect requires reviewed context"
    },
    {
        "id": "temporary-hp",
        "name": "Temporary hit points",
        "fields": [],
        "tags": [],
        "marker": "\\btemporary hit points\\b",
        "scope": "Temporary HP are distinct from healing and base HP"
    },
    {
        "id": "attacks",
        "name": "Attack labels and modifiers",
        "fields": [
            "spellAttack",
            "bonusWeapon",
            "bonusWeaponAttack",
            "bonusWeaponDamage",
            "bonusSpellAttack",
            "critThreshold"
        ],
        "tags": [
            "atk",
            "atkr",
            "hit",
            "d20",
            "hityourspellattack",
            "h",
            "hom"
        ],
        "marker": "\\battack roll",
        "scope": "Attack syntax/property, not a complete attack"
    },
    {
        "id": "saves",
        "name": "Saving throws and DCs",
        "fields": [
            "savingThrow",
            "savingThrowForced",
            "savingThrowForcedLegendary",
            "savingThrowForcedSpell",
            "save",
            "bonusSpellSaveDc",
            "bonusSavingThrow",
            "bonusSavingThrowConcentration"
        ],
        "tags": [
            "actsave",
            "dc",
            "dcyourspellsave",
            "actsavefail",
            "actsavesuccess",
            "actsavesuccessorfail",
            "actsavefailby"
        ],
        "marker": "\\bsaving throw",
        "scope": "DC tags alone do not distinguish a save from a check"
    },
    {
        "id": "checks",
        "name": "Ability and skill checks",
        "fields": [
            "abilityCheck",
            "skill",
            "bonusAbilityCheck"
        ],
        "tags": [
            "skillcheck"
        ],
        "marker": "\\b(ability check|skill check|check using|check made)\\b",
        "scope": "A skill reference is not a check or proficiency grant"
    },
    {
        "id": "advantage",
        "name": "Advantage and disadvantage",
        "fields": [],
        "tags": [],
        "marker": "\\b(advantage|disadvantage)\\b",
        "scope": "Prose candidates; positive, negative and example contexts differ"
    },
    {
        "id": "conditions",
        "name": "Condition classification/application",
        "fields": [
            "conditionInflict",
            "conditionInflictLegendary",
            "conditionInflictSpell"
        ],
        "tags": [],
        "marker": "\\b(becomes? |is |are |knocked |remains? )(prone|stunned|blinded|poisoned|restrained|incapacitated|frightened)",
        "scope": "Condition tags are counted as reference leads, not proof of application",
        "referenceTags": [
            "condition",
            "status",
            "disease"
        ]
    },
    {
        "id": "defenses",
        "name": "Resistance, vulnerability and immunities",
        "fields": [
            "resist",
            "vulnerable",
            "immune",
            "conditionImmune",
            "damageResist",
            "damageVulnerable",
            "damageImmune"
        ],
        "tags": [],
        "marker": "\\b(resistance|resistant|vulnerability|vulnerable|immunity|immune)\\b",
        "scope": "Authored defense versus an inferred bestowed defense"
    },
    {
        "id": "ac",
        "name": "Armor Class and modifiers",
        "fields": [
            "ac",
            "bonusAc",
            "vehAc",
            "dexterityMax"
        ],
        "tags": [],
        "marker": "\\b(armor class|AC)\\b",
        "scope": "Property/stat, not a recalculated character AC"
    },
    {
        "id": "hp",
        "name": "Hit points, hit dice and thresholds",
        "fields": [
            "hp",
            "hd",
            "vehHp",
            "vehDmgThresh",
            "dt",
            "mt"
        ],
        "tags": [],
        "marker": "\\b(hit points|hit dice|damage threshold|mishap threshold)\\b",
        "scope": "Hit dice, base/component HP and thresholds are distinct values, not healing/damage application"
    },
    {
        "id": "movement",
        "name": "Movement and speeds",
        "fields": [
            "speed",
            "pace",
            "modifySpeed",
            "vehSpeed"
        ],
        "tags": [],
        "marker": "\\b(speed|movement|difficult terrain|teleport)\\b",
        "scope": "Modes and source flags do not supply missing numeric speeds"
    },
    {
        "id": "range-area",
        "name": "Range, reach and area",
        "fields": [
            "range",
            "areaTags"
        ],
        "tags": [],
        "marker": "\\b(reach|radius|cone|cube|line|sphere|emanation|within [0-9]+ feet)\\b",
        "scope": "Range metadata is distinct from an effect area/target"
    },
    {
        "id": "duration",
        "name": "Duration and concentration",
        "fields": [
            "duration"
        ],
        "tags": [],
        "marker": "\\b(concentration|lasts? for|until the end|for [0-9]+ (round|minute|hour))",
        "scope": "A definition duration is not automatically every effect duration"
    },
    {
        "id": "targeting",
        "name": "Targets and restrictions",
        "fields": [
            "affectsCreatureType"
        ],
        "tags": [],
        "marker": "\\b(target|creature you can see|willing creature|each creature)\\b",
        "scope": "Creature-type classifications only partially describe targeting"
    },
    {
        "id": "resources",
        "name": "Costs, charges, uses and recharge",
        "fields": [
            "charges",
            "recharge",
            "rechargeAmount",
            "consumes",
            "reload"
        ],
        "tags": [
            "recharge"
        ],
        "marker": "\\b(charges?|uses?|once per|times per|expend)\\b",
        "scope": "Amount, pool, consumption and reset are separate facts"
    },
    {
        "id": "action-economy",
        "name": "Action economy and initiative",
        "fields": [
            "time",
            "legendaryActions",
            "legendaryActionsLair",
            "initiative",
            "actionThresholds"
        ],
        "tags": [],
        "marker": "\\b(bonus action|reaction|take an action|as an action|initiative|legendary action)\\b",
        "scope": "Action bucket identifies owner kind, not arbitrary resource costs"
    },
    {
        "id": "spellcasting",
        "name": "Spellcasting and spell availability",
        "fields": [
            "spellcasting",
            "spellcastingAbility",
            "additionalSpells",
            "attachedSpells",
            "preparedSpells",
            "casterProgression",
            "spellScrollLevel"
        ],
        "tags": [],
        "marker": "\\b(spellcasting|spell slot)\\b",
        "referenceTags": [
            "spell"
        ],
        "scope": "Spell mentions remain distinct from grants and schedules"
    },
    {
        "id": "scaling",
        "name": "Level/slot progression",
        "fields": [
            "scalingLevelDice",
            "cantripProgression",
            "preparedSpellsProgression",
            "spellsKnownProgression",
            "classTableGroups",
            "subclassTableGroups",
            "optionalfeatureProgression",
            "featProgression"
        ],
        "tags": [
            "scaledice",
            "scaledamage"
        ],
        "marker": "\\b(higher level|higher levels|each level|level or higher|proficiency bonus)\\b",
        "scope": "Explicit table axis differs from prose scaling"
    },
    {
        "id": "proficiency",
        "name": "Proficiency and expertise grants",
        "fields": [
            "skillProficiencies",
            "toolProficiencies",
            "languageProficiencies",
            "weaponProficiencies",
            "armorProficiencies",
            "savingThrowProficiencies",
            "skillToolLanguageProficiencies",
            "expertise",
            "startingProficiencies",
            "proficiency",
            "grantsProficiency",
            "grantsLanguage"
        ],
        "tags": [],
        "marker": "\\b(proficiency|proficiencies|proficient|expertise)\\b",
        "scope": "Requirements and grants are separate roles"
    },
    {
        "id": "prerequisites",
        "name": "Prerequisites and equipment restrictions",
        "fields": [
            "prerequisite",
            "reqAttune",
            "reqAttuneTags",
            "requires",
            "excludes",
            "multiclassing"
        ],
        "tags": [],
        "marker": "\\b(prerequisite|requires? attunement|must be proficient)\\b",
        "scope": "Explicit predicates without character eligibility evaluation"
    },
    {
        "id": "choices",
        "name": "Choices and alternatives",
        "fields": [
            "choose",
            "options",
            "from",
            "fromItems",
            "fromGeneric",
            "fromGroup",
            "fromMatching"
        ],
        "tags": [
            "filter"
        ],
        "marker": "\\b(choose|choice|one of the following|alternatively)\\b",
        "scope": "Tag filter is selector syntax, never a chosen grant"
    },
    {
        "id": "summoning",
        "name": "Summoning and transformations",
        "fields": [
            "summonedBySpell",
            "summonedByClass",
            "summonedBySpellLevel"
        ],
        "tags": [],
        "marker": "\\b(summon|summoned|transform|transformation|polymorph)\\b",
        "scope": "Monster provenance does not define a complete summoning effect"
    },
    {
        "id": "sequences",
        "name": "Multiattack and action sequences",
        "fields": [],
        "tags": [],
        "marker": "\\b(multiattack|makes? [a-z0-9]+ attacks?|replaces? [a-z0-9]+ attacks?)\\b",
        "scope": "Sequence graphs require semantic review"
    },
    {
        "id": "triggers",
        "name": "Triggers and reactions",
        "fields": [
            "trigger"
        ],
        "tags": [
            "acttrigger",
            "actresponse"
        ],
        "marker": "\\b(when|whenever|in response|trigger)\\b",
        "scope": "Trap trigger field is role-anchored prose, not a typed event",
        "descriptiveFields": [
            "trigger"
        ]
    },
    {
        "id": "ongoing",
        "name": "Ongoing and conditional effects",
        "fields": [],
        "tags": [],
        "marker": "\\b(at the start|at the end|each turn|while|unless|provided that)\\b",
        "scope": "Markers cannot establish scope, subject or conditional effect"
    },
    {
        "id": "recovery",
        "name": "Rest-based recovery",
        "fields": [
            "rest",
            "restLong"
        ],
        "tags": [],
        "marker": "\\b(short rest|long rest|finish(?:es)? a rest|dawn|recharge)\\b",
        "scope": "Structured spell usage bucket differs from a generalized resource reset"
    },
    {
        "id": "exploration",
        "name": "Environmental/exploration context",
        "fields": [
            "terrain",
            "environment",
            "space",
            "rating",
            "light",
            "carryingCapacity",
            "containerCapacity"
        ],
        "tags": [],
        "marker": "\\b(suffocat|starvation|dehydration|drowning|weather|foraging|travel pace|countermeasure|disarm)",
        "scope": "Context, light/capacity and rating are not environmental simulation"
    },
    {
        "id": "vehicles-facilities",
        "name": "Vehicle and facility mechanics",
        "fields": [
            "hull",
            "movement",
            "weapon",
            "control",
            "station",
            "actionStation",
            "capCrew",
            "capCargo",
            "capPassenger",
            "capCreature",
            "crew",
            "hirelings",
            "orders"
        ],
        "tags": [],
        "marker": "\\b(hireling|bastion|vehicle|crew|cargo)\\b",
        "scope": "Component/label metadata differs from execution or upgrades"
    },
    {
        "id": "quantities-tables",
        "name": "Economy, quantities and roll tables",
        "fields": [
            "coins",
            "crMin",
            "crMax",
            "value",
            "cost"
        ],
        "tags": [
            "dice",
            "chance"
        ],
        "marker": "\\b(roll on|roll a|roll [0-9]|random)\\b",
        "scope": "Cost/currency units, quantities, examples and name/recipe data are not automatically effects"
    },
    {
        "id": "ability-senses",
        "name": "Ability statistics/changes and senses",
        "fields": [
            "str",
            "dex",
            "con",
            "int",
            "wis",
            "cha",
            "ability",
            "primaryAbility",
            "darkvision",
            "blindsight",
            "senses",
            "bonusSenses"
        ],
        "tags": [],
        "marker": "\\b(ability score|darkvision|blindsight|truesight|tremorsense)\\b",
        "scope": "Base scores, associated ability, score increase, sense definition and sense grant differ"
    }
];

export const nonGameplay = new Set([
    "Books",
    "Adventure Index",
    "Name Tables",
    "Recipes",
    "Deities",
    "Languages"
]);

// Manually inspected examples, not a semantic gold set.
export const reviewedProse = {
    "healing": [
        [
            "Spells",
            "Healing Word",
            "PHB"
        ],
        [
            "Spells",
            "Cure Wounds",
            "XPHB"
        ]
    ],
    "temporary-hp": [
        [
            "Spells",
            "False Life",
            "PHB"
        ]
    ],
    "advantage": [
        [
            "Conditions",
            "Blinded",
            "PHB"
        ]
    ],
    "conditions": [
        [
            "Spells",
            "Hold Person",
            "PHB"
        ]
    ],
    "movement": [
        [
            "Conditions",
            "Grappled",
            "PHB"
        ]
    ],
    "range-area": [
        [
            "Spells",
            "Fireball",
            "PHB"
        ]
    ],
    "targeting": [
        [
            "Spells",
            "Magic Missile",
            "PHB"
        ]
    ],
    "resources": [
        [
            "Optional Features",
            "Pushing Attack",
            "PHB"
        ]
    ],
    "sequences": [
        [
            "Monsters",
            "Adult Red Dragon",
            "MM"
        ]
    ],
    "triggers": [
        [
            "Monsters",
            "Fiendish Icon",
            "ABH"
        ]
    ],
    "ongoing": [
        [
            "Spells",
            "Spirit Guardians",
            "PHB"
        ]
    ],
    "recovery": [
        [
            "Feats",
            "Lucky",
            "PHB"
        ]
    ],
    "exploration": [
        [
            "Traps",
            "Poison Needle",
            "DMG"
        ]
    ],
    "vehicles-facilities": [
        [
            "Bastion Facilities",
            "Arcane Study",
            "XDMG"
        ]
    ]
};
