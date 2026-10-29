const assert = require('node:assert/strict');
const crypto = require('node:crypto');
const fs = require('node:fs');
const path = require('node:path');
const Ajv2020 = require('ajv/dist/2020').default;

const workspace = path.resolve(__dirname, '..');
const schemaRoot = path.join(workspace, 'src/main/resources/schemas');
const coreRulesSchemaFiles = [
    'action.schema.json',
    'common.schema.json',
    'condition.schema.json',
    'disease.schema.json',
    'language.schema.json',
    'rule.schema.json',
    'sense.schema.json',
    'skill.schema.json',
    'status.schema.json'
];

function schemaPath(file) {
    const model = file.replace(/\.schema\.json$/, '');
    return path.join(schemaRoot, model, 'schema.json');
}

const canonicalFixtureIds = {
    'action-climb-onto-a-bigger-creature.json': 'action:dmg:climb-onto-a-bigger-creature',
    'action-dodge.json': 'action:phb:dodge',
    'action-escape-grapple.json': 'action:xphb:escape-a-grapple',
    'action-grapple.json': 'action:phb:grapple',
    'action-help-xphb.json': 'action:xphb:help',
    'action-other-activity.json': 'action:phb:other-activity',
    'action-overrun.json': 'action:dmg:overrun',
    'action-shove.json': 'action:phb:shove',
    'condition.json': 'condition:phb:blinded',
    'disease-cackle-fever.json': 'disease:dmg:cackle-fever',
    'rule-ability-check.json': 'rule:xphb:ability-check',
    'rule-ability-score-and-modifier.json': 'rule:xphb:ability-score-and-modifier',
    'rule-area-of-effect.json': 'rule:xphb:area-of-effect',
    'rule-carrying-capacity.json': 'rule:xphb:carrying-capacity',
    'rule-cover.json': 'rule:xphb:cover',
    'rule-crawling-movement.json': 'rule:xphb:crawling',
    'rule-death-saving-throw.json': 'rule:xphb:death-saving-throw',
    'rule-emanation-area-of-effect.json': 'rule:xphb:emanation-area-of-effect',
    'rule-expertise.json': 'rule:xphb:expertise',
    'rule-falling.json': 'rule:xge:falling',
    'rule-flanking.json': 'rule:dmg:flanking',
    'rule-grid-movement-cost.json': 'rule:dmg:diagonals',
    'rule-initiative-score.json': 'rule:xphb:initiative',
    'rule-jump-choice.json': 'rule:xphb:jumping',
    'rule-long-rest.json': 'rule:xphb:long-rest',
    'rule-movement-cost-procedure.json': 'rule:xphb:swimming',
    'rule-passive-perception.json': 'rule:xphb:passive-perception',
    'rule-proficiency-dice.json': 'rule:dmg:proficiency-dice',
    'rule-proficiency.json': 'rule:xphb:proficiency',
    'rule-reference-only.json': 'rule:xphb:action',
    'rule-resistance.json': 'rule:xphb:resistance',
    'rule-rest-recovery.json': 'rule:dmg:healing',
    'rule-skill-variants.json': 'rule:dmg:skill-variants',
    'rule-skills-with-different-abilities.json': 'rule:phb:skills-with-different-abilities',
    'rule-unarmed-strike.json': 'rule:xphb:unarmed-strike',
    'rule.json': 'rule:xphb:critical-hit',
    'skill.json': 'skill:phb:acrobatics',
    'status-concentration-phb.json': 'status:phb:concentration'
};
const canonicalRegressionIds = new Set([
    'action:dmg:activate-an-item',
    'action:dmg:climb-onto-a-bigger-creature',
    'action:dmg:disarm',
    'action:dmg:mark',
    'action:dmg:overrun',
    'action:dmg:tumble',
    'action:phb:attack',
    'action:phb:cast-a-spell',
    'action:phb:dodge',
    'action:phb:grapple',
    'action:phb:improvising-an-action',
    'action:phb:opportunity-attack',
    'action:phb:other-activity',
    'action:phb:shove',
    'action:phb:stabilize-a-creature',
    'action:phb:use-an-object',
    'action:xge:identify-a-spell',
    'action:xphb:end-concentration',
    'action:xphb:escape-a-grapple',
    'action:xphb:help',
    'action:xphb:hide',
    'action:xphb:influence',
    'action:xphb:magic',
    'action:xphb:opportunity-attack',
    'action:xphb:search',
    'action:xphb:study',
    'action:xphb:utilize',
    'condition:phb:blinded',
    'condition:phb:exhaustion',
    'condition:phb:petrified',
    'condition:xphb:blinded',
    'condition:xphb:exhaustion',
    'condition:xphb:unconscious',
    'disease:dmg:cackle-fever',
    'disease:dmg:sewer-plague',
    'disease:phb:blinding-sickness',
    'disease:xdmg:sewer-plague',
    'language:ggr:abyssal',
    'language:idrotf:loross',
    'language:phb:common',
    'language:phb:druidic',
    'language:phb:thieves-cant',
    'rule:dmg:diagonals',
    'rule:dmg:flanking',
    'rule:dmg:healing',
    'rule:dmg:hitting-cover',
    'rule:dmg:proficiency-dice',
    'rule:dmg:skill-variants',
    'rule:phb:multiclassing',
    'rule:phb:skills-with-different-abilities',
    'rule:xge:falling',
    'rule:xge:tool-proficiencies',
    'rule:xphb:ability-check',
    'rule:xphb:ability-score-and-modifier',
    'rule:xphb:advantage',
    'rule:xphb:area-of-effect',
    'rule:xphb:carrying-capacity',
    'rule:xphb:climb-speed',
    'rule:xphb:cover',
    'rule:xphb:crawling',
    'rule:xphb:critical-hit',
    'rule:xphb:d20-test',
    'rule:xphb:death-saving-throw',
    'rule:xphb:difficult-terrain',
    'rule:xphb:difficulty-class',
    'rule:xphb:emanation-area-of-effect',
    'rule:xphb:expertise',
    'rule:xphb:fly-speed',
    'rule:xphb:initiative',
    'rule:xphb:jumping',
    'rule:xphb:long-jump',
    'rule:xphb:long-rest',
    'rule:xphb:passive-perception',
    'rule:xphb:proficiency',
    'rule:xphb:resistance',
    'rule:xphb:saving-throw',
    'rule:xphb:skill',
    'rule:xphb:speed',
    'rule:xphb:spell-attack',
    'rule:xphb:swimming',
    'rule:xphb:unarmed-strike',
    'rule:xphb:vulnerability',
    'sense:phb:darkvision',
    'skill:phb:acrobatics',
    'status:phb:concentration',
    'status:phb:surprised',
    'status:xphb:bloodied',
    'status:xphb:concentration',
    'status:xphb:surprised'
]);

const canonicalResourceFiles = {
    action: 'actions.json',
    condition: 'conditions.json',
    disease: 'diseases.json',
    language: 'languages.json',
    rule: 'rules.json',
    sense: 'senses.json',
    skill: 'skills.json',
    status: 'statuses.json'
};
const canonicalResources = new Map();

function canonicalRecord(id) {
    const model = id.split(':')[0];
    const filename = canonicalResourceFiles[model];
    assert.ok(filename, `canonical resource not configured for ${model}`);
    if (!canonicalResources.has(model)) {
        canonicalResources.set(model, readJson(path.join(
            workspace,
            'src/main/resources/normalized/core-rules',
            filename
        )));
    }
    const record = canonicalResources.get(model).find(candidate => candidate.id === id);
    assert.ok(record, `canonical record not found: ${id}`);
    return record;
}

function canonicalFixture(name) {
    const id = canonicalFixtureIds[name];
    assert.ok(id, `canonical record alias not configured: ${name}`);
    return structuredClone(canonicalRecord(id));
}
const phase3Root = path.join(workspace, 'datasets/phase-3/step-2/core-rules/v1');
const phase5Root = path.join(workspace, 'datasets/phase-5/step-2/core-rules/v1');
const commonSchemaId = 'urn:thedndpub:schemas:core-rules:common:v1';
const ajv = new Ajv2020({ allErrors: true, strict: true, strictRequired: false });
const testResults = [];
const sourceConditionData = readJson(path.join(
    workspace,
    'src/main/resources/data/conditionsdiseases.json'
));
let phase5LanguageReferenceIndexes;

function canonicalSlug(value) {
    return value.normalize('NFKD').replace(/[\u0300-\u036f]/g, '').toLowerCase()
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/^-+|-+$/g, '');
}

const sourceConditionIds = new Set(sourceConditionData.condition.map(condition =>
    `condition:${condition.source.toLowerCase()}:${canonicalSlug(condition.name)}`
));
const sourceRuleIds = new Set(readJson(path.join(
    workspace,
    'src/main/resources/data/variantrules.json'
)).variantrule.map(rule => `rule:${canonicalSlug(rule.source)}:${canonicalSlug(rule.name)}`));

function readJson(file) {
    return JSON.parse(fs.readFileSync(file, 'utf8'));
}

function canonicalize(value) {
    if (Array.isArray(value)) return value.map(canonicalize);
    if (value && typeof value === 'object') {
        return Object.fromEntries(Object.keys(value).sort().map(key => [key, canonicalize(value[key])]));
    }
    return value;
}

function componentFingerprint(component) {
    const { componentId, ...definition } = component;
    return JSON.stringify(canonicalize(definition));
}

function check(name, callback) {
    callback();
    testResults.push(name);
}

function rejects(name, callback) {
    const acceptedMessage = `${name} was accepted`;
    try {
        assert.equal(callback(), false, acceptedMessage);
    } catch (error) {
        if (error.message === acceptedMessage) throw error;
    }
    testResults.push(name);
}

function fail(message) {
    throw new Error(message);
}

function assertValidCompositionNode(
    node,
    componentIds,
    referencedIds,
    ruleId,
    orPath = new Map(),
    location = 'root',
    activeGroups = new WeakSet()
) {
    if (node.kind === 'COMPONENT') {
        if (Object.keys(node).sort().join(',') !== 'componentId,kind') {
            fail(`${ruleId}: composition contains a duplicated component definition`);
        }
        if (!componentIds.has(node.componentId)) fail(`${ruleId}: unknown component ${node.componentId}`);
        if (referencedIds.has(node.componentId)) fail(`${ruleId}: component referenced more than once: ${node.componentId}`);
        referencedIds.add(node.componentId);
        return [{ componentId: node.componentId, orPath: new Map(orPath) }];
    }

    if (node.kind !== 'AND' && node.kind !== 'OR') fail(`${ruleId}: unsupported composition group`);
    if (activeGroups.has(node)) fail(`${ruleId}: circular composition group`);
    activeGroups.add(node);
    const childResults = [];
    const immediateLeaves = new Set();
    node.members.forEach((member, index) => {
        const nextPath = new Map(orPath);
        if (node.kind === 'OR') nextPath.set(location, index);
        const leaves = assertValidCompositionNode(
            member,
            componentIds,
            referencedIds,
            ruleId,
            nextPath,
            `${location}/${node.kind}-${index}`,
            activeGroups
        );
        for (const leaf of leaves) {
            if (immediateLeaves.has(leaf.componentId)) fail(`${ruleId}: duplicate reference in ${node.kind} group`);
            immediateLeaves.add(leaf.componentId);
        }
        childResults.push(...leaves);
    });
    activeGroups.delete(node);
    return childResults;
}

function assertRuleIntegrity(rule) {
    const mechanics = rule.mechanics ?? [];
    const componentIds = mechanics.map(component => component.componentId);
    if (new Set(componentIds).size !== componentIds.length) fail(`${rule.id}: duplicate componentId`);
    const definitions = new Set();
    const abilityScoreBands = [
        [1, 1, -5], [2, 3, -4], [4, 5, -3], [6, 7, -2],
        [8, 9, -1], [10, 11, 0], [12, 13, 1], [14, 15, 2],
        [16, 17, 3], [18, 19, 4], [20, 21, 5], [22, 23, 6],
        [24, 25, 7], [26, 27, 8], [28, 29, 9], [30, 30, 10]
    ];
    const proficiencyDieBands = [
        [1, 4, 2, 'd4'], [5, 8, 3, 'd6'], [9, 12, 4, 'd8'],
        [13, 16, 5, 'd10'], [17, 20, 6, 'd12']
    ];
    const proficiencyLevelBands = [
        [1, 4, 2], [5, 8, 3], [9, 12, 4], [13, 16, 5],
        [17, 20, 6], [21, 24, 7], [25, 28, 8], [29, 30, 9]
    ];
    const proficiencyChallengeRatingBands = [
        [null, 0, 4, 2], [4, null, 8, 3], [8, null, 12, 4], [12, null, 16, 5],
        [16, null, 20, 6], [20, null, 24, 7], [24, null, 28, 8], [28, null, 30, 9]
    ];
    for (const component of mechanics) {
        const fingerprint = componentFingerprint(component);
        if (definitions.has(fingerprint)) fail(`${rule.id}: duplicate mechanical component definition`);
        definitions.add(fingerprint);
        if (component.kind === 'ABILITY_SCORE_RULE' && component.subtype === 'MODIFIER_MAPPING') {
            const actual = component.scoreBands.map(band => [
                band.minimumScore, band.maximumScore, band.modifier
            ]);
            if (JSON.stringify(actual) !== JSON.stringify(abilityScoreBands)
                    || JSON.stringify(component.abilities) !== JSON.stringify([
                        'strength', 'dexterity', 'constitution', 'intelligence', 'wisdom', 'charisma'
                    ])) {
                fail(`${rule.id}: ability modifier table must cover the source-defined scores and six abilities`);
            }
        }
        if (component.kind === 'D20_TEST' && component.subtype === 'DIFFICULTY_CLASS'
                && JSON.stringify(component.targetNumberFor) !== JSON.stringify([
                    'ABILITY_CHECK', 'SAVING_THROW'
                ])) {
            fail(`${rule.id}: Difficulty Class must target ability checks and saving throws`);
        }
        if (component.kind === 'PROFICIENCY_RULE') {
            if (component.subtype === 'BONUS_SCALING'
                    && (component.multiplier !== 2 || component.requiresSkillProficiency !== true
                        || component.unlessAlreadyDoubledByAnotherFeature !== true
                        || component.assignment !== 'ONE_SKILL_IN_WHICH_PROFICIENT'
                        || component.maximumInstancesPerSkill !== 1)) {
                fail(`${rule.id}: Expertise scaling or skill assignment differs from the source`);
            }
            if (component.subtype === 'PROFICIENCY_DICE_ALTERNATIVE') {
                const actual = component.levelBands.map(band => [
                    band.minimumLevel, band.maximumLevel, band.proficiencyBonus, band.die
                ]);
                if (JSON.stringify(actual) !== JSON.stringify(proficiencyDieBands)
                        || component.expertiseDiceCount !== 2
                        || JSON.stringify(component.appliesTo) !== JSON.stringify([
                            'ABILITY_CHECK', 'ATTACK_ROLL', 'SAVING_THROW'
                        ])
                        || JSON.stringify(component.eligibleCreatureKinds) !== JSON.stringify([
                            'PLAYER_CHARACTER', 'NPC_WITH_LEVELS'
                        ])) {
                    fail(`${rule.id}: Proficiency Dice table, scope, or Expertise interaction differs from the source`);
                }
            }
            if (component.subtype === 'BONUS_APPLICATION'
                    && JSON.stringify(component.appliesTo) !== JSON.stringify([
                        'SKILL_ASSOCIATED_ABILITY_CHECK', 'SAVING_THROW', 'WEAPON_ATTACK', 'TOOL_CHECK'
                    ])) {
                fail(`${rule.id}: proficiency application must preserve the source-listed D20 Test uses`);
            }
            if (component.subtype === 'BONUS_PROGRESSION') {
                const levels = component.levelBands.map(band => [
                    band.minimumLevel, band.maximumLevel, band.bonus
                ]);
                const challengeRatings = component.challengeRatingBands.map(band => [
                    band.minimumChallengeRatingExclusive ?? null,
                    band.minimumChallengeRatingInclusive ?? null,
                    band.maximumChallengeRatingInclusive,
                    band.bonus
                ]);
                if (component.progressionSource !== 'LEVEL_OR_CHALLENGE_RATING'
                        || JSON.stringify(levels) !== JSON.stringify(proficiencyLevelBands)
                        || JSON.stringify(challengeRatings) !== JSON.stringify(proficiencyChallengeRatingBands)) {
                    fail(`${rule.id}: proficiency progression differs from the source table`);
                }
            }
            if (component.subtype === 'ABILITY_CHECK_PROFICIENCY_VARIANT'
                    && (component.replaces !== 'SKILL_PROFICIENCY'
                        || JSON.stringify(component.proficiencySources) !== JSON.stringify([
                            'CLASS_ASSOCIATED_ABILITY', 'BACKGROUND_SELECTED_ABILITY'
                        ])
                        || component.proficiencyCount !== 2
                        || component.appliesTo !== 'ABILITY_CHECK_ASSOCIATED_WITH_EITHER_PROFICIENT_ABILITY'
                        || component.startsAtLevel !== 1)) {
                fail(`${rule.id}: ability-check proficiency variant differs from the DMG source`);
            }
        }
        if (component.kind === 'INITIATIVE_PROCEDURE') {
            if (rule.id === 'rule:xphb:initiative'
                    && (component.subtype !== 'INITIATIVE_SCORE'
                        || component.base !== 10 || component.ability !== 'dexterity'
                        || component.advantageAdjustment !== 5 || component.disadvantageAdjustment !== -5)) {
                fail(`${rule.id}: XPHB Initiative score or Advantage/Disadvantage adjustment differs from source`);
            }
            if (rule.id === 'rule:dmg:initiative-variants' && component.subtype === 'SIDE_INITIATIVE'
                    && (component.roll !== 'UNMODIFIED_D20'
                        || component.tieResolution !== 'REROLL_TIED_SIDES'
                        || component.membersChooseOrder !== true
                        || component.roundEndsAfterAllSidesAct !== true
                        || component.multipleSidesOrderedByRoll !== true)) {
                fail(`${rule.id}: Side Initiative procedure differs from source`);
            }
        }
        if (component.kind === 'CARRYING_CAPACITY_RULE') {
            if (rule.id === 'rule:xphb:carrying-capacity') {
                const actual = component.sizeMultipliers.map(item => [
                    item.sizes.join(','), item.carryMultiplier, item.dragLiftPushMultiplier
                ]);
                const expected = [
                    ['TINY', 7.5, 15], ['SMALL,MEDIUM', 15, 30], ['LARGE', 30, 60],
                    ['HUGE', 60, 120], ['GARGANTUAN', 120, 240]
                ];
                if (JSON.stringify(actual) !== JSON.stringify(expected)
                        || component.dragLiftPushSpeedCapFeet !== 5
                        || component.speedCapAppliesWhenAboveCarryCapacity !== true) {
                    fail(`${rule.id}: size multipliers or drag/lift/push Speed cap differs from source`);
                }
            }
            if (rule.id === 'rule:phb:encumbrance') {
                const actual = component.encumbranceThresholds.map(item => [
                    item.weightMultiplier, item.state, item.speedReductionFeet,
                    item.abilityCheckDisadvantage.join(','), item.attackDisadvantage.join(','),
                    item.savingThrowDisadvantage.join(',')
                ]);
                if (component.thresholdComparison !== 'GREATER_THAN'
                        || JSON.stringify(actual) !== JSON.stringify([
                            [5, 'ENCUMBERED', 10, '', '', ''],
                            [10, 'HEAVILY_ENCUMBERED', 20,
                                'strength,dexterity,constitution',
                                'strength,dexterity,constitution',
                                'strength,dexterity,constitution']
                        ]) || component.heavyEncumbranceMaximum !== 'STANDARD_CARRYING_CAPACITY') {
                    fail(`${rule.id}: Encumbrance thresholds or effects differ from PHB source`);
                }
            }
        }
        if (component.kind === 'REST_PROCEDURE') {
            if (rule.id === 'rule:xphb:long-rest' && component.subtype === 'LONG_REST'
                    && (component.duration !== 8 || component.unit !== 'hours'
                        || component.durationQualifier !== 'AT_LEAST'
                        || component.minimumHitPointsToStart !== 1
                        || component.minimumHoursBetweenStarts !== 16
                        || component.physicalExertionInterruptionHours !== 1
                        || component.shortRestBenefitThresholdHours !== 1
                        || component.addedDurationPerInterruption?.amount !== 1)) {
                fail(`${rule.id}: XPHB Long Rest duration or interruption procedure differs from source`);
            }
            if (rule.id === 'rule:xphb:short-rest' && component.subtype === 'SHORT_REST'
                    && (component.duration !== 1 || component.unit !== 'hour'
                        || component.minimumHitPointsToStart !== 1
                        || JSON.stringify(component.interruptions)
                            !== JSON.stringify(['INITIATIVE', 'NON_CANTRIP_SPELL', 'DAMAGE'])
                        || component.interruptionOutcome !== 'NO_BENEFITS')) {
                fail(`${rule.id}: XPHB Short Rest duration or interruption rule differs from source`);
            }
            if (rule.id === 'rule:dmg:rest-variants') {
                const actual = [component.subtype, component.duration, component.unit];
                const allowed = [
                    ['SHORT_REST', 5, 'minutes'], ['LONG_REST', 1, 'hour'],
                    ['SHORT_REST', 8, 'hours'], ['LONG_REST', 7, 'days']
                ];
                if (!allowed.some(value => JSON.stringify(value) === JSON.stringify(actual))) {
                    fail(`${rule.id}: Rest Variant duration is not source-defined`);
                }
            }
            if (rule.id === 'rule:xge:sleep' && component.deprivationProcedure) {
                const procedure = component.deprivationProcedure;
                if (procedure.intervalHours !== 24 || procedure.savingThrow?.baseDc !== 10
                        || procedure.savingThrow?.increasePerConsecutiveInterval !== 5
                        || procedure.dcResetOnLongRest !== true
                        || procedure.failureOutcome?.kind !== 'INCREASE_CONDITION_LEVEL'
                        || procedure.failureOutcome.levels !== 1) {
                    fail(`${rule.id}: sleep-deprivation save cadence or consequence differs from source`);
                }
            }
        }
        if (component.kind === 'DAMAGE_HEALTH_STATE'
                && ['RESISTANCE', 'VULNERABILITY'].includes(component.subtype)) {
            const expected = component.subtype === 'RESISTANCE'
                ? ['HALVE', 0.5, 'DOWN'] : ['DOUBLE', 2, undefined];
            const actual = [component.resolutionModifier?.operation,
                component.resolutionModifier?.multiplier, component.resolutionModifier?.rounding];
            if (JSON.stringify(actual) !== JSON.stringify(expected)
                    || component.resolutionModifier?.oncePerDamageInstance !== true
                    || component.resolutionModifier?.sameOperationDoesNotStack !== true) {
                fail(`${rule.id}: Resistance/Vulnerability calculation differs from XPHB source`);
            }
        }
        if (component.subtype === 'FLANKING') {
            const hexSeparations = component.hexGrid?.targetSizeSeparations?.map(item => [
                item.targetSizeGroup, item.hexesBetween, item.comparison
            ]);
            const expectedHexSeparations = [
                ['TINY_SMALL_MEDIUM', 2, 'EXACTLY'],
                ['LARGE', 4, 'EXACTLY'],
                ['HUGE', 5, 'EXACTLY'],
                ['GARGANTUAN', 6, 'AT_LEAST']
            ];
            if (component.requiresMiniatures !== true
                    || component.targetRole !== 'ENEMY'
                    || JSON.stringify(component.participants) !== JSON.stringify(['ATTACKER', 'ALLY'])
                    || component.participantsMustSeeTarget !== true
                    || component.participantsCannotHaveCondition?.id !== 'condition:phb:incapacitated'
                    || component.largeOrLargerSpaceRule?.appliesToFlankerSize !== 'LARGE_OR_LARGER'
                    || component.largeOrLargerSpaceRule?.qualification !== 'AT_LEAST_ONE_SPACE_CELL_QUALIFIES'
                    || component.squareGrid?.tieBreakProcedure
                        !== 'CENTER_LINE_CROSSES_OPPOSITE_SIDES_OR_CORNERS'
                    || JSON.stringify(hexSeparations) !== JSON.stringify(expectedHexSeparations)
                    || component.benefit?.roll !== 'attack'
                    || component.benefit?.direction !== 'advantage') {
                fail(`${rule.id}: Flanking geometry, eligibility, or Advantage differs from the DMG source`);
            }
        }
        if (component.subtype === 'COVER') {
            const expectedCover = {
                half: [2, 2, 'dexterity', false],
                'three-quarters': [5, 5, 'dexterity', false],
                total: [undefined, undefined, undefined, true]
            }[component.level];
            if (!expectedCover
                    || component.armorClassBonus !== expectedCover[0]
                    || component.savingThrowBonus !== expectedCover[1]
                    || component.savingThrowAbility !== expectedCover[2]
                    || component.blocksTargeting !== expectedCover[3]
                    || component.degreeSelection !== 'MOST_PROTECTIVE_ONLY') {
                fail(`${rule.id}: Cover benefit or targeting differs from the XPHB source`);
            }
        }
        if (component.subtype === 'ABILITY_VARIATION') {
            const skillRecords = readJson(path.join(workspace, 'src/main/resources/data/skills.json')).skill;
            const abilityNames = {
                str: 'strength', dex: 'dexterity', con: 'constitution',
                int: 'intelligence', wis: 'wisdom', cha: 'charisma'
            };
            if (component.decisionMaker !== 'DM'
                    || component.eligibleRequester !== 'DM_OR_PLAYER'
                    || component.decision !== 'DISCRETIONARY') {
                fail(`${rule.id}: ability/Skill variation discretion differs from the PHB source`);
            }
            for (const pairing of component.pairings ?? []) {
                if (pairing.skill?.targetType !== 'skill') fail(`${rule.id}: pairing must reference a Skill`);
                const sourceSkill = skillRecords.find(skill =>
                    `skill:${canonicalSlug(skill.source)}:${canonicalSlug(skill.name)}` === pairing.skill.id);
                if (!sourceSkill || abilityNames[sourceSkill.ability] !== pairing.usualAbility
                        || pairing.alternativeAbility === pairing.usualAbility) {
                    fail(`${rule.id}: Skill ability pairing does not match its source Skill or vary its ability`);
                }
            }
        }
    }
    for (const component of mechanics) {
        if (component.subtype === 'ABILITY_CHECK' && component.passiveScoreConstruction) {
            if (component.passiveScore !== true) fail(`${rule.id}: passive construction requires a passive check`);
        }
    }
    const components = new Map(mechanics.map(component => [component.componentId, component]));
    const compositionLeaves = new Map();

    if (rule.composition) {
        const leaves = assertValidCompositionNode(rule.composition, new Set(componentIds), new Set(), rule.id);
        for (const leaf of leaves) compositionLeaves.set(leaf.componentId, leaf.orPath);
    }

    const dependencyGraph = new Map(componentIds.map(id => [id, []]));
    for (const component of mechanics) {
        const applicability = component.appliesWhen;
        if (!applicability) continue;
        const target = components.get(applicability.componentId);
        if (!target) fail(`${rule.id}: unresolved applicability component ${applicability.componentId}`);
        const expectedSubtype = applicability.kind === 'ON_FAILED_SAVE'
            ? 'SAVING_THROW'
            : 'ATTACK_ROLL';
        if (applicability.kind === 'ON_FAILED_SAVE') {
            if (target.kind !== 'D20_TEST' || target.subtype !== expectedSubtype) {
                fail(`${rule.id}: ON_FAILED_SAVE must reference an ordinary saving-throw component`);
            }
        } else {
            if (target.kind !== 'D20_TEST' || target.subtype !== expectedSubtype) {
                fail(`${rule.id}: ${applicability.kind} must reference an attack-roll component`);
            }
            if (applicability.kind === 'ON_CRITICAL_HIT' && !target.roll?.criticalOn) {
                fail(`${rule.id}: ON_CRITICAL_HIT target does not define a critical result`);
            }
        }
        dependencyGraph.get(component.componentId).push(target.componentId);

        const sourcePath = compositionLeaves.get(component.componentId);
        const targetPath = compositionLeaves.get(target.componentId);
        if (sourcePath && targetPath) {
            for (const [groupId, branch] of sourcePath) {
                if (targetPath.has(groupId) && targetPath.get(groupId) !== branch) {
                    fail(`${rule.id}: applicability contradicts OR composition`);
                }
            }
        }
    }

    const visiting = new Set();
    const visited = new Set();
    function visit(componentId) {
        if (visiting.has(componentId)) fail(`${rule.id}: circular applicability dependency at ${componentId}`);
        if (visited.has(componentId)) return;
        visiting.add(componentId);
        for (const dependency of dependencyGraph.get(componentId) ?? []) visit(dependency);
        visiting.delete(componentId);
        visited.add(componentId);
    }
    for (const componentId of componentIds) visit(componentId);
}

function operationApplicability(action, operation) {
    const appliesWhen = operation.appliesWhen;
    if (!appliesWhen) return;
    const operations = action.operations ?? [];
    if (appliesWhen.operationId) {
        const target = operations.find(candidate => candidate.operationId === appliesWhen.operationId);
        if (!target) fail(`${action.id}: unresolved operation ${appliesWhen.operationId}`);
        if (appliesWhen.kind === 'CHECK_SUCCEEDED' && target.kind !== 'CHECK_OR_CONTEST') {
            fail(`${action.id}: CHECK_SUCCEEDED must reference a check operation`);
        }
        if (appliesWhen.kind === 'SAVE_FAILED' && target.kind !== 'SAVING_THROW') {
            fail(`${action.id}: SAVE_FAILED must reference a saving-throw operation`);
        }
    }
}

function assertActionIntegrity(action) {
    const operations = action.operations ?? [];
    const byId = new Map();
    for (const operation of operations) {
        if (operation.operationId) {
            if (byId.has(operation.operationId)) fail(`${action.id}: duplicate operationId ${operation.operationId}`);
            byId.set(operation.operationId, operation);
        }
        operationApplicability(action, operation);
    }

    const isCheck = operation => operation?.kind === 'CHECK_OR_CONTEST';
    const skillAbilities = new Map([...phase3Files(path.join(phase3Root, 'skill')),
        ...phase3Files(path.join(phase5Root, 'skill'))]
        .map(file => readJson(file))
        .map(skill => [skill.id, skill.ability]));
    const validateAbilitySkillPairs = (owner, options) => {
        for (const option of options ?? []) {
            if (!option.skill) continue;
            const skillAbility = skillAbilities.get(option.skill.id);
            if (!skillAbility) fail(`${action.id}: unresolved Skill option ${option.skill.id}`);
            if (skillAbility !== option.ability) {
                fail(`${action.id}: ${owner} ability does not match Skill ${option.skill.id}`);
            }
        }
    };
    for (const operation of operations) {
        if (operation.kind === 'OBJECT_OPERATION' && operation.interactionAllowance
                && operation.operation !== 'ordinary-interaction') {
            fail(`${action.id}: interaction allowance only applies to ordinary object interaction`);
        }
        for (const participant of operation.participants ?? []) {
            validateAbilitySkillPairs(participant.role, participant.options);
        }
        validateAbilitySkillPairs('defender', operation.defenderOptions);
        for (const option of operation.checkOptions ?? []) {
            validateAbilitySkillPairs('check option', [option]);
        }
        if (operation.skill && operation.abilities?.length === 1) {
            validateAbilitySkillPairs('check', [{
                ability: operation.abilities[0],
                skill: operation.skill
            }]);
        }
        if (operation.opposingSkills && operation.opposingAbilities) {
            if (operation.opposingSkills.length !== operation.opposingAbilities.length) {
                fail(`${action.id}: opposing ability/Skill options must be paired`);
            }
            validateAbilitySkillPairs('opposing check', operation.opposingSkills.map((skill, index) => ({
                ability: operation.opposingAbilities[index],
                skill
            })));
        }
        const appliesWhen = operation.appliesWhen;
        if (appliesWhen?.kind === 'CHECK_RESOLUTION'
                && !isCheck(byId.get(appliesWhen.operationId))) {
            fail(`${action.id}: CHECK_RESOLUTION must reference a check operation`);
        }
        if (appliesWhen?.kind === 'ATTACK_RESOLUTION'
                && !['ATTACK_RESOLUTION', 'ATTACK_CONTESTED_BY_CHECK']
                    .includes(byId.get(appliesWhen.operationId)?.kind)) {
            fail(`${action.id}: ATTACK_RESOLUTION must reference an attack operation`);
        }
        const linkedCheckId = operation.checkId ?? operation.entryCheckId;
        if (linkedCheckId && !isCheck(byId.get(linkedCheckId))) {
            fail(`${action.id}: unresolved or incompatible check reference ${linkedCheckId}`);
        }
        if (operation.kind === 'HIDE_PROCEDURE') {
            if (!isCheck(byId.get(operation.checkId))) fail(`${action.id}: Hide must reference a check`);
            if (operation.discoveryDc.checkId !== operation.checkId) {
                fail(`${action.id}: Hide discovery DC must use the Hide check total`);
            }
        }
        if (operation.kind === 'SPELL_IDENTIFICATION'
                && !isCheck(byId.get(operation.checkId))) {
            fail(`${action.id}: Spell identification must reference a check`);
        }
        if (operation.kind === 'INFLUENCE_PROCEDURE') {
            const branches = operation.branches;
            const willingness = branches.map(branch => branch.willingness).sort();
            if (JSON.stringify(willingness) !== JSON.stringify(['HESITANT', 'UNWILLING', 'WILLING'])) {
                fail(`${action.id}: Influence must define each willingness branch exactly once`);
            }
            for (const branch of branches) {
                const checkRequired = branch.willingness === 'HESITANT';
                if (branch.checkRequired !== checkRequired) {
                    fail(`${action.id}: Influence check requirement contradicts willingness`);
                }
            }
        }
        if (operation.kind === 'ATTACK_CONTESTED_BY_CHECK' && operation.attackDamageOnSuccess !== false) {
            fail(`${action.id}: disarm's contested attack cannot deal damage`);
        }
    }

    const groups = action.choiceGroups ?? [];
    const groupById = new Map();
    const memberOwner = new Map();
    for (const group of groups) {
        if (groupById.has(group.id)) fail(`${action.id}: duplicate choice group ${group.id}`);
        groupById.set(group.id, group);
        const { minimumSelected, maximumSelected } = group.cardinality;
        if (minimumSelected > maximumSelected || maximumSelected > group.members.length) {
            fail(`${action.id}: invalid cardinality for ${group.id}`);
        }
        const memberIds = group.members.map(member => member.operationId);
        if (new Set(memberIds).size !== memberIds.length) fail(`${action.id}: duplicate member in ${group.id}`);
        if (group.trigger.operationId && !byId.has(group.trigger.operationId)) {
            fail(`${action.id}: unresolved trigger operation ${group.trigger.operationId}`);
        }
        if (group.trigger.kind === 'CHECK_SUCCEEDED'
                && byId.get(group.trigger.operationId)?.kind !== 'CHECK_OR_CONTEST') {
            fail(`${action.id}: choice trigger must reference a check`);
        }
        if (group.trigger.kind === 'SAVE_FAILED'
                && byId.get(group.trigger.operationId)?.kind !== 'SAVING_THROW') {
            fail(`${action.id}: choice trigger must reference a saving throw`);
        }
        for (const operationId of memberIds) {
            const member = byId.get(operationId);
            if (!member) fail(`${action.id}: unresolved choice member ${operationId}`);
            if (memberOwner.has(operationId)) fail(`${action.id}: operation belongs to incompatible choice groups`);
            memberOwner.set(operationId, group.id);
            if (member.appliesWhen?.kind !== 'CHOICE_SELECTED'
                    || member.appliesWhen.choiceGroupId !== group.id) {
                fail(`${action.id}: choice member applicability contradicts ${group.id}`);
            }
        }
    }

    for (const operation of operations) {
        if (operation.appliesWhen?.kind === 'CHOICE_SELECTED') {
            const group = groupById.get(operation.appliesWhen.choiceGroupId);
            if (!group || !memberOwner.has(operation.operationId)
                    || memberOwner.get(operation.operationId) !== group.id) {
                fail(`${action.id}: operation references a missing or incompatible choice group`);
            }
        }
    }
}

function assertDiseaseIntegrity(disease) {
    const symptomIds = new Set();
    const branchIds = new Set();
    const exposureIds = new Set();
    const dcTracks = new Set();
    const addUnique = (set, value, label) => {
        if (set.has(value)) fail(`${disease.id}: duplicate ${label} ${value}`);
        set.add(value);
    };
    for (const symptom of disease.symptoms ?? []) {
        if (symptom.symptomId) addUnique(symptomIds, symptom.symptomId, 'symptomId');
    }
    for (const recovery of disease.recovery ?? []) {
        if (recovery.savingThrow?.dcTrackId) dcTracks.add(recovery.savingThrow.dcTrackId);
    }
    for (const effect of disease.effects ?? []) {
        if (effect.symptomId) addUnique(symptomIds, effect.symptomId, 'symptomId');
    }
    for (const exposure of [
        ...(disease.infection?.secondaryExposures ?? []),
        ...(disease.infection?.exposures ?? []),
        ...(disease.infection?.exposureProcedures ?? [])
    ]) {
        const id = exposure.exposureId ?? exposure.kind;
        if (exposure.exposureId) addUnique(exposureIds, exposure.exposureId, 'exposureId');
        if (exposure.symptomId && !symptomIds.has(exposure.symptomId)) {
            fail(`${disease.id}: unresolved exposure symptom ${exposure.symptomId}`);
        }
        if (exposure.onSuccess?.symptomId && exposure.onSuccess.symptomId !== exposure.symptomId) {
            fail(`${disease.id}: source-relative immunity must identify the exposed symptom`);
        }
    }
    for (const branch of disease.progression ?? []) {
        addUnique(branchIds, branch.branchId, 'branchId');
        if (branch.kind === 'SAVING_THROW_BRANCH' && branch.savingThrow.dcTrackId) {
            dcTracks.add(branch.savingThrow.dcTrackId);
        }
        const outcomes = [
            ...(branch.onSuccess ?? []),
            ...(branch.onFailure ?? []),
            ...(branch.outcomes ?? []),
            ...(branch.onEnd ?? [])
        ];
        for (const outcome of outcomes) {
            if (outcome.symptomId && !symptomIds.has(outcome.symptomId)) {
                fail(`${disease.id}: unresolved progression symptom ${outcome.symptomId}`);
            }
        }
        if (branch.trigger?.symptomId && !symptomIds.has(branch.trigger.symptomId)) {
            fail(`${disease.id}: unresolved trigger symptom ${branch.trigger.symptomId}`);
        }
        if (branch.outcomeCounter && branch.outcomeCounter.branchId !== branch.branchId) {
            fail(`${disease.id}: counter is attached to a different saving-throw branch`);
        }
        if (branch.outcomeCounter?.sequence === 'UNSPECIFIED_BY_SOURCE'
                && branch.outcomeCounter.reset !== 'UNSPECIFIED_BY_SOURCE') {
            fail(`${disease.id}: unspecified counter sequence cannot have an executable reset policy`);
        }
    }
    for (const symptom of disease.symptoms ?? []) {
        const track = symptom.repeatSaveToEnd?.dcTrackId;
        if (track && !dcTracks.has(track)) fail(`${disease.id}: unresolved symptom DC track ${track}`);
    }
    for (const recovery of disease.recovery ?? []) {
        if (recovery.outcomeCounter && recovery.outcomeCounter.branchId !== recovery.id) {
            fail(`${disease.id}: recovery counter must identify its recovery procedure`);
        }
        if (recovery.outcomeCounter?.sequence === 'UNSPECIFIED_BY_SOURCE'
                && recovery.outcomeCounter.reset !== 'UNSPECIFIED_BY_SOURCE') {
            fail(`${disease.id}: unspecified recovery-counter sequence cannot have an executable reset policy`);
        }
        if (recovery.dcTrackId && !dcTracks.has(recovery.dcTrackId)) {
            fail(`${disease.id}: unresolved recovery DC track ${recovery.dcTrackId}`);
        }
        if (recovery.savingThrow?.dcTrackId && !dcTracks.has(recovery.savingThrow.dcTrackId)) {
            fail(`${disease.id}: unresolved recovery saving-throw DC track ${recovery.savingThrow.dcTrackId}`);
        }
        if (recovery.savingThrow?.dcReduction && !recovery.savingThrow.dcTrackId) {
            fail(`${disease.id}: DC reduction lacks a linked DC track`);
        }
    }
    const procedureIds = new Set([
        ...branchIds,
        ...(disease.recovery ?? []).map(recovery => recovery.id).filter(Boolean)
    ]);
    for (const branch of disease.progression ?? []) {
        const counter = branch.outcomeCounter;
        if (counter && !procedureIds.has(counter.branchId)) {
            fail(`${disease.id}: counter references missing procedure ${counter.branchId}`);
        }
    }
    for (const recovery of disease.recovery ?? []) {
        const counter = recovery.outcomeCounter;
        if (counter && !procedureIds.has(counter.branchId)) {
            fail(`${disease.id}: counter references missing procedure ${counter.branchId}`);
        }
    }
    if (disease.effectProfile) {
        if (disease.effectProfile.sourceDisease.id === disease.id) {
            fail(`${disease.id}: Disease effect profile cannot reference itself`);
        }
        if (disease.effectProfile.sourceDisease.id !== 'disease:dmg:sight-rot'
                || disease.id !== 'disease:gos:redface') {
            fail(`${disease.id}: unsupported Disease effect-profile relationship`);
        }
    }
}

function assertCanonicalReferences(entity) {
    if (!/^[a-z][a-z0-9-]*:[a-z0-9-]+:[a-z0-9-]+$/.test(entity.id)) {
        fail(`${entity.id}: invalid canonical identity`);
    }
    const visit = value => {
        if (Array.isArray(value)) return value.forEach(visit);
        if (!value || typeof value !== 'object') return;
        if (value.targetType && value.id) {
            if (!value.id.startsWith(`${value.targetType}:`)
                    || !/^[a-z][a-z0-9-]*:[a-z0-9-]+:[a-z0-9-]+$/.test(value.id)) {
                fail(`${entity.id}: invalid typed reference ${value.id}`);
            }
            if (value.targetType === 'condition' && !sourceConditionIds.has(value.id)) {
                fail(`${entity.id}: unresolved source-backed Condition reference ${value.id}`);
            }
            if (value.targetType === 'rule' && !sourceRuleIds.has(value.id)) {
                fail(`${entity.id}: unresolved source-backed Rule reference ${value.id}`);
            }
        }
        for (const child of Object.values(value)) visit(child);
    };
    visit(entity);
}

function assertPhase5DiseaseReferences(entity) {
    const referenceIds = new Map();
    const canonicalIds = records => new Set(records.map(record =>
        `${record.targetType}:${canonicalSlug(record.source)}:${canonicalSlug(record.name)}`));
    referenceIds.set('condition', sourceConditionIds);
    referenceIds.set('disease', canonicalIds(sourceConditionData.disease.map(record => ({
        ...record,
        targetType: 'disease'
    }))));
    const races = readJson(path.join(workspace, 'src/main/resources/data/races.json')).race;
    referenceIds.set('race', canonicalIds(races.map(record => ({ ...record, targetType: 'race' }))));
    const spellFiles = fs.readdirSync(path.join(workspace, 'src/main/resources/data/spells'), { recursive: true })
        .filter(file => file.endsWith('.json'));
    const spells = spellFiles.flatMap(file => {
        const value = readJson(path.join(workspace, 'src/main/resources/data/spells', file));
        return value.spell ?? [];
    });
    referenceIds.set('spell', canonicalIds(spells.map(record => ({ ...record, targetType: 'spell' }))));
    const visit = value => {
        if (Array.isArray(value)) return value.forEach(visit);
        if (!value || typeof value !== 'object') return;
        if (value.targetType && value.id && referenceIds.has(value.targetType)) {
            assert.ok(referenceIds.get(value.targetType).has(value.id),
                `${entity.id}: Disease reference does not resolve to the pinned source inventory: ${value.id}`);
        }
        Object.values(value).forEach(visit);
    };
    visit(entity);
}

function assertPhase5LanguageReferences(entity) {
    if (!phase5LanguageReferenceIndexes) {
        const languageSource = readJson(path.join(workspace, 'src/main/resources/data/languages.json')).language;
        const languageIds = new Set(languageSource.map(record =>
            `language:${canonicalSlug(record.source)}:${canonicalSlug(record.name)}`));
        const monsterDir = path.join(workspace, 'src/main/resources/data/monsters');
        const creatureIds = new Set();
        for (const file of fs.readdirSync(monsterDir)
                .filter(name => name.startsWith('bestiary-') && name.endsWith('.json'))) {
            const data = readJson(path.join(monsterDir, file));
            for (const creature of data.monster ?? []) {
                creatureIds.add(`creature:${canonicalSlug(creature.source)}:${canonicalSlug(creature.name)}`);
            }
        }
        phase5LanguageReferenceIndexes = { languageIds, creatureIds };
    }
    const visit = value => {
        if (Array.isArray(value)) return value.forEach(visit);
        if (!value || typeof value !== 'object') return;
        if (value.targetType === 'language') {
            assert.ok(phase5LanguageReferenceIndexes.languageIds.has(value.id),
                `${entity.id}: unresolved Language reference ${value.id}`);
        }
        if (value.targetType === 'creature' && value.id) {
            assert.ok(phase5LanguageReferenceIndexes.creatureIds.has(value.id),
                `${entity.id}: unresolved creature speaker ${value.id}`);
        }
        Object.values(value).forEach(visit);
    };
    visit(entity);
}

function assertApprovedDomainBaseline() {
    const baseline = readJson(path.join(__dirname, 'core-rules-approved-domain-baseline.json'));
    for (const { file, sha256 } of baseline.entries) {
        const value = readJson(schemaPath(file));
        const actual = crypto.createHash('sha256')
            .update(JSON.stringify(canonicalize(value)))
            .digest('hex');
        if (actual !== sha256) fail(`approved domain contract changed: ${file}`);
    }
}

function loadSchemas() {
    const files = [...coreRulesSchemaFiles].sort();
    const schemas = [];
    for (const file of files) {
        const schema = readJson(schemaPath(file));
        if (ajv.validateSchema(schema) !== true) fail(`${file}: invalid Draft 2020-12 schema`);
        ajv.addSchema(schema);
        schemas.push([file, schema]);
    }
    for (const [file, schema] of schemas) {
        check(`Draft 2020-12 schema compiles ${file}`, () => {
            assert.ok(ajv.getSchema(schema.$id), `${file}: schema was not registered`);
        });
    }
    return files;
}

function expectedSourceMetadata(model, sourceRecord) {
    const metadata = { source: sourceRecord.source };
    if (Number.isInteger(sourceRecord.page)) metadata.page = sourceRecord.page;
    const flags = {};
    for (const key of ['srd', 'srd52', 'basicRules', 'basicRules2024']) {
        if (typeof sourceRecord[key] === 'boolean') flags[key] = sourceRecord[key];
    }
    if (Object.keys(flags).length) metadata.flags = flags;
    const citations = [...(sourceRecord.additionalSources ?? []), ...(sourceRecord.otherSources ?? [])]
        .map(({ source, page }) => ({ source, page }));
    if (citations.length) metadata.citations = citations;
    if (sourceRecord.reprintedAs?.length) {
        metadata.reprintedAs = sourceRecord.reprintedAs.map(value => {
            let targetType = model;
            let uid = value;
            if (typeof value === 'object') {
                uid = value.uid;
                if (value.tag === 'variantrule') targetType = 'rule';
            }
            const [name, source] = uid.split('|');
            return {
                targetType,
                id: `${targetType}:${canonicalSlug(source || sourceRecord.source)}:${canonicalSlug(name)}`
            };
        });
    }
    return metadata;
}

function phase3Files(directory = phase3Root) {
    if (!fs.existsSync(directory)) return [];
    return fs.readdirSync(directory, { withFileTypes: true }).flatMap(entry => {
        const absolute = path.join(directory, entry.name);
        return entry.isDirectory() ? phase3Files(absolute) : entry.name.endsWith('.json') ? [absolute] : [];
    }).sort();
}

function assertCoreRulesReferenceIdentityIndex(records) {
    const targetTypes = new Set();
    const visit = value => {
        if (Array.isArray(value)) return value.forEach(visit);
        if (!value || typeof value !== 'object') return;
        if (typeof value.targetType === 'string' && typeof value.id === 'string') {
            targetTypes.add(value.targetType);
        }
        Object.values(value).forEach(visit);
    };
    records.forEach(visit);

    const sourceFiles = {
        action: [['actions.json', 'action']],
        rule: [['variantrules.json', 'variantrule']],
        skill: [['skills.json', 'skill']],
        condition: [['conditionsdiseases.json', 'condition']],
        disease: [['conditionsdiseases.json', 'disease']],
        status: [['conditionsdiseases.json', 'status']],
        sense: [['senses.json', 'sense']],
        language: [['languages.json', 'language']],
        race: [['races.json', 'race']]
    };
    const expected = new Set();
    for (const type of targetTypes) {
        let catalogs = sourceFiles[type];
        if (type === 'spell') {
            const directory = path.join(workspace, 'src/main/resources/data/spells');
            catalogs = fs.readdirSync(directory, { recursive: true })
                .filter(file => file.endsWith('.json'))
                .map(file => [path.join('spells', file), 'spell']);
        } else if (type === 'creature') {
            const directory = path.join(workspace, 'src/main/resources/data/monsters');
            catalogs = fs.readdirSync(directory)
                .filter(file => file.startsWith('bestiary-') && file.endsWith('.json'))
                .map(file => [path.join('monsters', file), 'monster']);
        }
        assert.ok(catalogs, `no pinned source catalog for ${type}`);
        for (const [file, property] of catalogs) {
            const recordsInSource = readJson(path.join(workspace, 'src/main/resources/data', file))[property] ?? [];
            for (const record of recordsInSource) {
                expected.add(`${type}:${canonicalSlug(record.source)}:${canonicalSlug(record.name)}`);
            }
        }
    }
    const identityIndex = path.join(
        workspace,
        'src/main/resources/normalized/core-rules/source-identities.json'
    );
    if (fs.existsSync(identityIndex)) {
        const actual = readJson(identityIndex);
        assert.deepEqual(actual, [...expected].sort((left, right) => left.localeCompare(right)));
    }
}

function assertConsolidatedCoreRulesResources(records) {
    const resourceFiles = {
        action: 'actions.json',
        condition: 'conditions.json',
        disease: 'diseases.json',
        language: 'languages.json',
        rule: 'rules.json',
        sense: 'senses.json',
        skill: 'skills.json',
        status: 'statuses.json'
    };
    const byDomain = new Map(Object.keys(resourceFiles).map(domain => [domain, []]));
    const identities = new Set();
    for (const record of records) {
        const domain = record.id.split(':')[0];
        assert.ok(byDomain.has(domain), `unexpected canonical domain ${domain}`);
        assert.equal(identities.has(record.id), false, `duplicate canonical identity ${record.id}`);
        identities.add(record.id);
        byDomain.get(domain).push(record);
    }
    for (const [domain, file] of Object.entries(resourceFiles)) {
        const expected = byDomain.get(domain).sort((left, right) => left.id.localeCompare(right.id));
        const actual = readJson(path.join(workspace, 'src/main/resources/normalized/core-rules', file));
        assert.deepEqual(actual, expected, `${file} must exactly aggregate the validated canonical records`);
    }
    assert.equal(identities.size, 585);
}

function nestedSourcePaths(entry, parentPath, output = []) {
    if (!entry || typeof entry !== 'object') return output;
    for (const key of ['entries', 'items']) {
        if (!Array.isArray(entry[key])) continue;
        entry[key].forEach((child, index) => {
            const childPath = `${parentPath}.${key}[${index}]`;
            output.push(childPath);
            nestedSourcePaths(child, childPath, output);
        });
    }
    return output;
}

function sourceRecordForCanonical(entity) {
    const [sourceFile, property] = {
        action: ['actions.json', 'action'],
        rule: ['variantrules.json', 'variantrule'],
        skill: ['skills.json', 'skill'],
        condition: ['conditionsdiseases.json', 'condition'],
        disease: ['conditionsdiseases.json', 'disease'],
        status: ['conditionsdiseases.json', 'status'],
        sense: ['senses.json', 'sense'],
        language: ['languages.json', 'language']
    }[entity.id.split(':')[0]];
    if (!sourceFile) fail(`${entity.id}: unsupported Phase 3 model`);
    const data = readJson(path.join(workspace, 'src/main/resources/data', sourceFile));
    const source = entity.sourceMetadata.source;
    const matches = data[property].filter(record => record.name === entity.name && record.source === source);
    if (matches.length !== 1) fail(`${entity.id}: expected one exact source record, found ${matches.length}`);
    return matches[0];
}

function resolveSourcePath(sourceData, locator) {
    let current = sourceData;
    const tokens = locator.match(/\.[A-Za-z0-9_]+|\[\d+\]/g) ?? [];
    for (const token of tokens) {
        current = token.startsWith('[')
            ? current[Number(token.slice(1, -1))]
            : current[token.slice(1)];
        if (current === undefined) fail(`source locator does not resolve: ${locator}`);
    }
    return current;
}

function assertNoSourceFallback(entity) {
    const forbidden = new Set(['entries', 'richText', 'fonts', 'font', 'hasFluff', 'hasFluffImages', 'reviewStatus', 'interpretationStatus']);
    function visit(value, location) {
        if (Array.isArray(value)) return value.forEach((child, index) => visit(child, `${location}[${index}]`));
        if (!value || typeof value !== 'object') return;
        for (const [key, child] of Object.entries(value)) {
            if (forbidden.has(key)) fail(`${entity.id}: forbidden canonical property ${location}.${key}`);
            visit(child, `${location}.${key}`);
        }
    }
    visit(entity, '$');
}

function validatePhase3Records(schemaFiles) {
    const files = phase3Files();
    const manifestPath = path.join(workspace, 'reports/step-2/CORE_RULES_PHASE_3_SOURCE_ACCOUNTING.json');
    const manifest = readJson(manifestPath);
    const canonicalIds = new Set();
    const entities = files.map(file => ({ file, entity: readJson(file) }));
    const outputById = new Map(entities.map(({ entity }) => [entity.id, entity]));
    const sourceIndexes = new Map();
    const sourceDefinitions = {
        action: ['actions.json', 'action'],
        rule: ['variantrules.json', 'variantrule'],
        skill: ['skills.json', 'skill'],
        condition: ['conditionsdiseases.json', 'condition'],
        disease: ['conditionsdiseases.json', 'disease'],
        status: ['conditionsdiseases.json', 'status'],
        sense: ['senses.json', 'sense'],
        language: ['languages.json', 'language'],
        spell: ['spells/spells-phb.json', 'spell'],
        creature: ['monsters/bestiary-mm.json', 'monster'],
        race: ['races.json', 'race']
    };
    const sourceIds = targetType => {
        if (sourceIndexes.has(targetType)) return sourceIndexes.get(targetType);
        const [file, property] = sourceDefinitions[targetType] ?? [];
        if (!file) fail(`no source index for reference target type ${targetType}`);
        const records = readJson(path.join(workspace, 'src/main/resources/data', file))[property];
        const ids = new Set(records.map(record =>
            `${targetType}:${canonicalSlug(record.source)}:${canonicalSlug(record.name)}`));
        sourceIndexes.set(targetType, ids);
        return ids;
    };
    const assertSourceReferences = entity => {
        const visit = value => {
            if (Array.isArray(value)) return value.forEach(visit);
            if (!value || typeof value !== 'object') return;
            if (value.targetType && value.id) {
                assert.ok(sourceIds(value.targetType).has(value.id),
                    `${entity.id}: typed reference does not resolve to a pinned source object: ${value.id}`);
            }
            for (const child of Object.values(value)) visit(child);
        };
        visit(entity);
    };

    for (const { file, entity } of entities) {
        const model = entity.id.split(':')[0];
        const validator = ajv.getSchema(`urn:thedndpub:schemas:core-rules:${model}:v1`);
        if (!validator) fail(`${file}: no schema registered for ${model}`);
        check(`Phase 3 canonical record ${path.relative(phase3Root, file)}`, () => {
            assert.equal(validator(entity), true, JSON.stringify(validator.errors));
            assert.equal(canonicalIds.has(entity.id), false, `duplicate canonical ID ${entity.id}`);
            canonicalIds.add(entity.id);
            assertCanonicalReferences(entity);
            assertSourceReferences(entity);
            assertNoSourceFallback(entity);
            assert.equal(entity.id, `${model}:${canonicalSlug(entity.sourceMetadata.source)}:${canonicalSlug(entity.name)}`);
            const expectedMetadata = expectedSourceMetadata(model, sourceRecordForCanonical(entity));
            assert.deepEqual(entity.sourceMetadata, expectedMetadata);
            if (model === 'action') assertActionIntegrity(entity);
            if (model === 'rule') {
                const ruleTypeMap = { C: 'CORE', O: 'OPTIONAL', V: 'VARIANT', VO: 'VARIANT_OPTIONAL' };
                assert.equal(entity.ruleType, ruleTypeMap[sourceRecordForCanonical(entity).ruleType]);
                assertRuleIntegrity(entity);
            }
            if (model === 'disease') assertDiseaseIntegrity(entity);
            const raw = fs.readFileSync(file, 'utf8');
            assert.equal(raw, `${JSON.stringify(entity, null, 2)}\n`, file);
        });
    }

    check('Phase 3 source-entry accounting and omission/duplicate registers', () => {
        assert.equal(manifest.selectionCount, manifest.records.length);
        assert.equal(manifest.records.length, 34);
        assert.equal(manifest.convertedRecordCount, 25);
        assert.equal(manifest.partiallyConvertedRecordCount, 9);
        assert.equal(manifest.unsupportedRecordCount, 0);
        assert.equal(manifest.canonicalRecordCount, entities.length);
        assert.equal(manifest.interpretationMethod.startsWith('Codex directly interpreted'), true);
        assert.equal(manifest.previousPhase3Conversion.includes('retired'), true);
        const classifications = manifest.records.flatMap(record =>
            record.entries.flatMap(entry => entry.classifications));
        assert.deepEqual(manifest.dispositionCounts, {
            interpreted: classifications.filter(item => item.classification === 'INTERPRETED').length,
            duplicate: classifications.filter(item => item.classification === 'DUPLICATE').length,
            omitted: classifications.filter(item => item.classification === 'OMITTED').length
        });
        assert.equal(manifest.sourceEntryRootCount,
            manifest.records.reduce((count, record) => count + record.sourceEntryCount, 0));
        assert.equal(manifest.sourceFieldIssueCount, manifest.sourceFieldIssues.length);
        for (const issue of manifest.sourceFieldIssues) {
            assert.equal(issue.disposition, 'OMITTED');
            assert.ok([
                'SCHEMA_UNSUPPORTED',
                'REFERENCE_UNRESOLVED',
                'SOURCE_AMBIGUOUS',
                'EXTERNAL_DEPENDENCY',
                'NON_MECHANICAL',
                'OUT_OF_APPROVED_SCOPE'
            ].includes(issue.cause));
            assert.ok(issue.sourceObjectPath && issue.sourceField && issue.reason);
            const issueSource = resolveSourcePath(
                readJson(path.join(workspace, 'src/main/resources/data', issue.sourceFile)),
                issue.sourceObjectPath
            );
            if (issue.sourceField.startsWith('inlineReference.')) {
                assert.ok(String(issueSource).includes(issue.sourceValue), issue.canonicalId);
            } else {
                assert.deepEqual(issueSource[issue.sourceField], issue.sourceValue, issue.canonicalId);
            }
        }
        const seenSources = new Set();
        const coveredOutputIds = new Set();
        for (const record of manifest.records) {
            const sourceKey = `${record.model}|${record.source}|${record.name}`;
            assert.equal(seenSources.has(sourceKey), false, `duplicate selected source record ${sourceKey}`);
            seenSources.add(sourceKey);
            const sourceRecord = sourceRecordForCanonical({
                id: record.canonicalId,
                name: record.name,
                sourceMetadata: { source: record.source }
            });
            const sourceEntries = sourceRecord.entries ?? [];
            assert.equal(record.sourceEntryCount, sourceEntries.length, sourceKey);
            assert.ok(['converted', 'partial', 'unsupported'].includes(record.conversionStatus), sourceKey);
            assert.deepEqual(record.entries.map(entry => entry.sourceEntryIndex), sourceEntries.map((_, index) => index));
            const registerFiles = {
                omitted: path.join(workspace, 'doc', record.model, 'omitted.md'),
                duplicate: path.join(workspace, 'doc', record.model, 'duplicate.md')
            };
            for (const entry of record.entries) {
                assert.ok(entry.classifications.length > 0, `${sourceKey} entry ${entry.sourceEntryIndex} unaccounted`);
                const sourceEntry = sourceEntries[entry.sourceEntryIndex];
                assert.deepEqual(entry.nestedSourcePaths,
                    nestedSourcePaths(sourceEntry, entry.sourcePath), `${sourceKey} nested source accounting`);
                for (const classification of entry.classifications) {
                    assert.ok(['INTERPRETED', 'OMITTED', 'DUPLICATE'].includes(classification.classification));
                    assert.ok(classification.portion && classification.reason);
                    if (classification.classification === 'OMITTED' || classification.classification === 'DUPLICATE') {
                        if (classification.classification === 'OMITTED') {
                            assert.ok([
                                'SCHEMA_UNSUPPORTED',
                                'REFERENCE_UNRESOLVED',
                                'SOURCE_AMBIGUOUS',
                                'EXTERNAL_DEPENDENCY',
                                'NON_MECHANICAL',
                                'OUT_OF_APPROVED_SCOPE'
                            ].includes(classification.cause), `${sourceKey}: missing/invalid omission cause`);
                        }
                        const registerKey = classification.classification.toLowerCase();
                        const registerFile = registerFiles[registerKey];
                        assert.ok(fs.existsSync(registerFile), `missing ${registerKey} register for ${sourceKey}`);
                        const registerText = fs.readFileSync(registerFile, 'utf8');
                        assert.ok(registerText.includes(`entry ${entry.sourceEntryIndex}`), `missing register entry ${sourceKey}`);
                        assert.ok(registerText.includes(JSON.stringify(sourceEntry, null, 2)),
                            `register does not preserve complete source entry ${sourceKey} entry ${entry.sourceEntryIndex}`);
                    }
                }
            }
            if (record.canonicalOutput) {
                assert.ok(outputById.has(record.canonicalId), `missing canonical output ${record.canonicalId}`);
                coveredOutputIds.add(record.canonicalId);
            } else {
                assert.equal(record.disposition, 'unsupported', sourceKey);
                assert.ok(record.reason);
                assert.equal(outputById.has(record.canonicalId), false, `unsupported record has canonical shell ${record.canonicalId}`);
            }
        }
        assert.deepEqual([...coveredOutputIds].sort(), [...outputById.keys()].sort());
        assert.equal(manifest.records.filter(record => record.disposition === 'unsupported').length, 0);
    });

    check('Phase 3 Gith speaker references match explicit source creatures', () => {
        const gith = outputById.get('language:mm:gith');
        assert.ok(gith);
        const monsterData = readJson(path.join(workspace, 'src/main/resources/data/monsters/bestiary-mm.json'));
        const creatureIds = new Set(monsterData.monster.map(monster =>
            `creature:${canonicalSlug(monster.source)}:${canonicalSlug(monster.name)}`));
        for (const speaker of gith.typicalSpeakers) assert.ok(creatureIds.has(speaker.id), speaker.id);
    });

    check('Phase 3 source-backed Action, Condition, Rule, Disease, and Status semantics', () => {
        const dodge = outputById.get('action:xphb:dodge');
        assert.deepEqual(dodge.operations[0].when, {
            kind: 'CAN_SEE', actor: 'affected-creature', target: 'attacker'
        });
        const shove = outputById.get('action:phb:shove');
        assert.equal(shove.operations[0].automaticSuccessWhen.condition.id, 'condition:phb:incapacitated');
        assert.deepEqual(shove.choiceGroups[0].cardinality, { minimumSelected: 1, maximumSelected: 1 });
        const exhaustedPhb = outputById.get('condition:phb:exhaustion');
        assert.equal(exhaustedPhb.effects.find(effect => effect.kind === 'HIT_POINT_MAX_MODIFIER').minimumLevel, 4);
        const exhausted2024 = outputById.get('condition:xphb:exhaustion');
        assert.equal(exhausted2024.effects.find(effect => effect.kind === 'CHECK_ROLL_MODIFIER').amountPerLevel, 2);
        assert.equal(exhausted2024.effects.find(effect => effect.kind === 'MOVEMENT_MODIFIER').amountPerLevel, -5);
        const phbConcentration = outputById.get('status:phb:concentration');
        const environmentalSave = phbConcentration.mechanics.find(
            mechanic => mechanic.trigger === 'environmental-phenomenon');
        assert.equal(environmentalSave.requirement, 'dm-may-require');
        assert.equal(phbConcentration.mechanics.find(mechanic => mechanic.trigger === 'damage').requirement,
            undefined);
        const xphbConcentration = outputById.get('status:xphb:concentration');
        assert.equal(xphbConcentration.mechanics.find(mechanic => mechanic.trigger === 'damage').dc.rounding, 'down');
        assert.equal(xphbConcentration.mechanics.find(mechanic => mechanic.trigger === 'damage').dc.maximum, 30);
        const unarmed = outputById.get('rule:xphb:unarmed-strike');
        assert.equal(unarmed.composition.kind, 'OR');
        assert.equal(unarmed.mechanics.filter(mechanic => mechanic.subtype === 'SAVING_THROW').length, 1);
        assert.deepEqual(unarmed.mechanics.filter(mechanic =>
            mechanic.componentId === 'unarmed-grapple-outcome'
            || mechanic.componentId === 'unarmed-shove-outcome').map(mechanic => mechanic.reachFeet), [5, 5]);
        assert.ok(unarmed.references.some(reference =>
            reference.id === 'condition:xphb:grappled'
            && reference.relationship === 'grapple-escape-attempts-use-this-dc'));
        const mark = outputById.get('action:dmg:mark');
        assert.equal(mark.operations[0].reaction.availabilityRequired, true);
        assert.equal(mark.operations[0].reaction.consumed, false);
        assert.equal(outputById.get('action:phb:shove').operations[0].skill.id, 'skill:phb:athletics');
        const castSpell = outputById.get('action:phb:cast-a-spell');
        assert.equal(castSpell.timing.kind, 'alternatives');
        assert.deepEqual(castSpell.timing.options.map(option => option.kind), [
            'action', 'reaction', 'varied_gametime'
        ]);
        assert.equal(outputById.get('action:xphb:dodge').references.length, 4);
        assert.equal(outputById.get('condition:xphb:blinded').references.length, 3);
        assert.ok(outputById.get('condition:xphb:blinded').effects.some(effect =>
            effect.kind === 'ATTACK_ROLL_MODIFIER'
            && effect.actor === 'affected-creature'
            && effect.target === 'target'
            && effect.direction === 'disadvantage'));
        assert.deepEqual(outputById.get('rule:xphb:d20-test').references.map(reference => reference.id), [
            'rule:xphb:ability-check',
            'rule:xphb:attack-roll',
            'rule:xphb:saving-throw'
        ]);
        assert.ok(outputById.get('rule:xphb:critical-hit').mechanics[0].roll.forms.includes('unarmed'));
        const sewerPlague = outputById.get('disease:xdmg:sewer-plague');
        assert.equal(sewerPlague.infection.savingThrow.dc, 11);
        assert.equal(sewerPlague.classification, 'magical-contagion');
        assert.deepEqual(sewerPlague.applicability.targetCreatureTypes, ['humanoid']);
        assert.deepEqual(sewerPlague.incubation, { kind: 'DICE_ROLL', count: 1, die: 4, unit: 'day' });
        assert.deepEqual(sewerPlague.infection.exposures.map(exposure => exposure.kind), [
            'WOUND_BY_CONTAGION_CARRIER', 'CONTACT_WITH_CONTAMINATED_MATERIAL'
        ]);
        assert.equal(sewerPlague.infection.exposures[1].materials.join(','), 'filth,offal');
        const sewerExhaustion = { targetType: 'condition', id: 'condition:xphb:exhaustion' };
        assert.equal(sewerPlague.symptoms.find(symptom => symptom.kind === 'CONDITION_LEVEL_CHANGE').amount, 1);
        assert.ok(sewerPlague.conditionGatedRestrictions.some(restriction =>
            restriction.kind === 'HIT_DICE_RECOVERY_MULTIPLIER'
            && restriction.condition.id === sewerExhaustion.id
            && restriction.multiplier === 0.5));
        assert.ok(sewerPlague.conditionGatedRestrictions.some(restriction =>
            restriction.kind === 'LONG_REST_PREVENTED_RECOVERY'
            && restriction.preventedRecoveries.includes('hit-points')
            && restriction.preventedRecoveries.includes('condition-level-reduction')));
        const sewerBranch = sewerPlague.progression.find(branch => branch.kind === 'SAVING_THROW_BRANCH');
        assert.deepEqual(sewerBranch.trigger, { kind: 'DAILY_AT', time: 'DAWN' });
        assert.equal(sewerBranch.savingThrow.dc, 11);
        assert.equal(sewerBranch.onFailure[0].amount, 1);
        assert.equal(sewerBranch.onSuccess[0].amount, -1);
        assert.equal(sewerBranch.endsWhen.level, 0);
        assert.equal(sewerBranch.endsWhen.condition.id, sewerExhaustion.id);
        const cackle = outputById.get('disease:dmg:cackle-fever');
        assert.deepEqual(cackle.incubation, { kind: 'DICE_ROLL', count: 1, die: 4, unit: 'hour' });
        assert.deepEqual(cackle.applicability.targetCreatureTypes, ['humanoid']);
        assert.equal(cackle.applicability.excludedTargets[0].id, 'race:phb:gnome');
        assert.equal(cackle.symptoms.find(symptom => symptom.kind === 'CONDITION_LEVEL_CHANGE')
            .cannotReduceUntilDiseaseEnds, true);
        const cackleStress = cackle.progression.find(branch => branch.branchId === 'great-stress-save');
        assert.equal(cackleStress.savingThrow.dc, 13);
        assert.deepEqual(cackleStress.onFailure.map(outcome => outcome.kind), ['DAMAGE', 'START_SYMPTOM']);
        assert.equal(cackleStress.onFailure[0].count, 1);
        assert.equal(cackleStress.onFailure[0].die, 10);
        const laughter = cackle.symptoms.find(symptom => symptom.kind === 'TEMPORARY_CONDITION');
        assert.equal(laughter.condition.id, 'condition:phb:incapacitated');
        assert.equal(laughter.repeatSaveToEnd.trigger, 'end-of-each-turn');
        assert.equal(cackle.infection.secondaryExposures[0].distance, 10);
        assert.equal(cackle.infection.secondaryExposures[0].onSuccess.duration.amount, 24);
        assert.equal(cackle.infection.secondaryExposures[0].onSuccess.source, 'exposing-creature');
        assert.equal(cackle.recovery[0].savingThrow.dcReduction.dieSize, 6);
        assert.equal(cackle.recovery[0].endsWhenDcAtOrBelow, 0);
        assert.equal(cackle.recovery[0].outcomeCounter.countedOutcome, 'FAILURE');
        assert.equal(cackle.recovery[0].outcomeCounter.threshold, 3);
        assert.equal(cackle.recovery[0].outcomeCounter.sequence, 'UNSPECIFIED_BY_SOURCE');
        assert.equal(cackle.recovery[0].outcomeCounter.reset, 'UNSPECIFIED_BY_SOURCE');
        assert.equal(cackle.recovery[0].outcomeCounter.onThreshold.reference.id, 'rule:dmg:madness');
        assert.ok(cackle.sourceMetadata.reprintedAs.some(reference =>
            reference.id === 'disease:xdmg:cackle-fever'));
        const darkvision = outputById.get('sense:phb:darkvision');
        assert.equal(darkvision.capabilities[0].darknessAs, 'dim-light');
        assert.equal(darkvision.capabilities[0].dimLightAs, 'bright-light');
        assert.equal(darkvision.capabilities[0].colorPerception, 'grayscale');
        const crafting = outputById.get('rule:dmg:downtime-activity-crafting-a-magic-item');
        assert.equal(crafting.mechanics[0].kind, 'DOWNTIME_PROCEDURE');
        assert.equal(crafting.mechanics[0].procedure, 'craft');
        const tremorsense = outputById.get('sense:xphb:tremorsense').capabilities[0];
        assert.ok(tremorsense.detects.includes('movement'));
        assert.ok(tremorsense.restrictions.includes('requires-contact'));
        assert.deepEqual(tremorsense.locationPrecision, { kind: 'DETECTED_TARGETS', targets: ['creatures', 'movement'] });
        assert.deepEqual(outputById.get('sense:mm:tremorsense').capabilities[0].locationPrecision,
            { kind: 'VIBRATION_ORIGIN' });
        const phbTruesight = outputById.get('sense:phb:truesight').capabilities[0];
        assert.ok(phbTruesight.perceives.includes('invisible-objects'));
        assert.deepEqual(phbTruesight.illusionInteraction, {
            detection: 'automatic', savingThrowOutcome: 'automatic-success'
        });
        assert.equal(outputById.get('rule:xphb:difficult-terrain').mechanics[0].qualifyingSpaces.length, 7);
        assert.equal(outputById.get('action:phb:grapple').operations.find(operation =>
            operation.kind === 'MOVE_WITH_GRAPPLED_TARGET').speedCost.multiplier, 0.5);
        assert.equal(outputById.get('action:phb:grapple').operations.find(operation =>
            operation.kind === 'MOVE_WITH_GRAPPLED_TARGET').speedCost.waivedWhenTargetAtLeastSizesSmaller, 2);
        assert.equal(outputById.get('skill:phb:acrobatics').sourceMetadata.page, 176);
        const phbUnarmed = canonicalFixture('rule-unarmed-strike.json');
        assert.ok(phbUnarmed.mechanics.filter(mechanic =>
            mechanic.componentId === 'unarmed-grapple-outcome'
            || mechanic.componentId === 'unarmed-shove-outcome').every(mechanic => mechanic.reachFeet === 5));
        const critical = canonicalFixture('rule.json');
        assert.ok(critical.mechanics[0].roll.forms.includes('unarmed'));
        const phbSource = readJson(path.join(workspace, 'src/main/resources/data/conditionsdiseases.json'));
        assert.ok(phbSource.disease.find(disease => disease.name === 'Cackle Fever'
            && disease.source === 'DMG').entries[0].includes('gnomes are strangely immune'));
        const sewerSource = phbSource.disease.find(disease => disease.name === 'Sewer Plague'
            && disease.source === 'XDMG');
        assert.ok(sewerSource.entries[0].includes('Any Humanoid'));
        assert.ok(sewerSource.entries[2].entries[0].entries[0].includes('Daily at dawn'));
    });

    return { files: files.length, entities };
}

function validatePhase5Expansion() {
    const files = phase3Files(phase5Root);
    const manifest = readJson(path.join(
        workspace,
        'reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json'
    ));
    const phase3Ids = new Set(phase3Files().map(file => readJson(file).id));
    const entities = files.map(file => ({ file, entity: readJson(file) }));
    const outputById = new Map(entities.map(({ entity }) => [entity.id, entity]));
    const uniqueSources = new Set();
    const rules = readJson(path.join(workspace, 'src/main/resources/data/variantrules.json')).variantrule;

    for (const { file, entity } of entities) {
        const model = entity.id.split(':')[0];
        const validator = ajv.getSchema(`urn:thedndpub:schemas:core-rules:${model}:v1`);
        check(`Phase 5 canonical record ${path.relative(phase5Root, file)}`, () => {
            assert.ok(validator, `${file}: no schema registered for ${model}`);
            assert.equal(validator(entity), true, JSON.stringify(validator.errors));
            assert.equal(phase3Ids.has(entity.id), false, `duplicate Phase 3 canonical ID ${entity.id}`);
            assert.equal(uniqueSources.has(entity.id), false, `duplicate Phase 5 canonical ID ${entity.id}`);
            uniqueSources.add(entity.id);
            assert.equal(entity.id,
                `${model}:${canonicalSlug(entity.sourceMetadata.source)}:${canonicalSlug(entity.name)}`);
            const expectedMetadata = expectedSourceMetadata(model, sourceRecordForCanonical(entity));
            if (entity.id === 'rule:dmg:planar-effects'
                    || entity.id === 'rule:dmg:diagonals'
                    || entity.id === 'rule:xge:falling'
                    || entity.id === 'rule:dmg:firearms'
                    || entity.id === 'rule:dmg:alien-technology'
                    || entity.id === 'rule:dmg:level-advancement-without-xp'
                    || entity.id === 'rule:dmg:renown'
                    || entity.id === 'rule:dmg:loyalty'
                    || entity.id === 'rule:dmg:explosives'
                    || entity.id === 'rule:dmg:mixing-potions'
                    || entity.id === 'rule:dmg:downtime-activity-training-to-gain-levels') {
                delete expectedMetadata.reprintedAs;
            }
            assert.deepEqual(entity.sourceMetadata, expectedMetadata);
            if (model === 'rule') {
                const ruleTypeMap = { C: 'CORE', O: 'OPTIONAL', V: 'VARIANT', VO: 'VARIANT_OPTIONAL' };
                assert.equal(entity.ruleType, ruleTypeMap[sourceRecordForCanonical(entity).ruleType]);
            }
            assertCanonicalReferences(entity);
            assertNoSourceFallback(entity);
            if (model === 'action') assertActionIntegrity(entity);
            if (model === 'disease') {
                assertDiseaseIntegrity(entity);
                assertPhase5DiseaseReferences(entity);
            }
            if (model === 'language') assertPhase5LanguageReferences(entity);
            if (model === 'skill') {
                const abilityNames = {
                    str: 'strength', dex: 'dexterity', con: 'constitution',
                    int: 'intelligence', wis: 'wisdom', cha: 'charisma'
                };
                assert.equal(entity.ability, abilityNames[sourceRecordForCanonical(entity).ability]);
            }
            const raw = fs.readFileSync(file, 'utf8');
            assert.equal(raw, `${JSON.stringify(entity, null, 2)}\n`, file);
        });
    }

    check('Phase 5 batch source coverage and omission/duplicate registers', () => {
        const expectedBatches = [
            ['skill', 33, 3, 33, 33, 0, 0, 35],
            ['condition', 26, 4, 26, 25, 1, 0, 39],
            ['sense', 4, 4, 4, 1, 3, 0, 5],
            ['action', 42, 0, 40, 27, 13, 2, 84],
            ['disease', 26, 3, 25, 22, 3, 1, 73],
            ['language', 197, 4, 197, 71, 126, 0, 40],
            ['rule', 226, 5, 226, 64, 89, 73, 630]
        ];
        assert.deepEqual(manifest.batchResults.map(batch => [
            batch.domain,
            batch.sourceRecordsExamined,
            batch.representativeRecordsExcluded,
            batch.canonicalRecordCount,
            batch.fullyRepresentedRecordCount,
            batch.partiallyRepresentedRecordCount,
            batch.unsupportedRecordCount,
            batch.sourceEntryRootCount
        ]), expectedBatches);
        for (const batch of manifest.batchResults) {
            const counts = { interpreted: 0, duplicate: 0, omitted: 0 };
            for (const record of manifest.records.filter(record => record.model === batch.domain)) {
                for (const entry of record.entries) {
                    for (const item of entry.classifications) counts[item.classification.toLowerCase()]++;
                }
            }
            assert.deepEqual(batch.dispositionCounts, counts, `${batch.domain} batch dispositions`);
        }
        assert.equal(manifest.records.length, expectedBatches.reduce((sum, batch) => sum + batch[1], 0));
        assert.equal(manifest.canonicalRecordCount, files.length);
        assert.equal(manifest.convertedRecordCount, expectedBatches.reduce((sum, batch) => sum + batch[4], 0));
        assert.equal(manifest.partiallyConvertedRecordCount,
            expectedBatches.reduce((sum, batch) => sum + batch[5], 0));
        assert.equal(manifest.unsupportedRecordCount,
            expectedBatches.reduce((sum, batch) => sum + batch[6], 0));
        assert.equal(manifest.canonicalRecordCount,
            expectedBatches.reduce((sum, batch) => sum + batch[3], 0));
        assert.equal(manifest.sourceEntryRootCount,
            manifest.records.reduce((sum, record) => sum + record.sourceEntryCount, 0));
        const classifications = manifest.records.flatMap(record =>
            record.entries.flatMap(entry => entry.classifications));
        assert.deepEqual(manifest.dispositionCounts, {
            interpreted: classifications.filter(item => item.classification === 'INTERPRETED').length,
            duplicate: classifications.filter(item => item.classification === 'DUPLICATE').length,
            omitted: classifications.filter(item => item.classification === 'OMITTED').length
        });
        const seen = new Set();
        const covered = new Set();
        for (const record of manifest.records) {
            const sourceKey = `${record.model}|${record.source}|${record.name}`;
            assert.equal(seen.has(sourceKey), false, `duplicate source record ${sourceKey}`);
            seen.add(sourceKey);
            assert.equal(phase3Ids.has(record.canonicalId), false, `representative repeated ${record.canonicalId}`);
            const sourceRecord = sourceRecordForCanonical({
                id: record.canonicalId,
                name: record.name,
                sourceMetadata: { source: record.source }
            });
            const sourceIndex = Number(record.sourceObjectPath.match(/\[(\d+)\]$/)[1]);
            assert.equal(sourceRecord.name, record.name);
            assert.equal(sourceRecord.source, record.source);
            const sourceCollection = record.model === 'rule' ? 'variantrule' : record.model;
            assert.equal(record.sourceObjectPath, `$.${sourceCollection}[${sourceIndex}]`);
            assert.equal(record.sourceEntryCount, (sourceRecord.entries ?? []).length);
            assert.deepEqual(record.entries.map(entry => entry.sourceEntryIndex),
                (sourceRecord.entries ?? []).map((_, index) => index));
            for (const entryPlan of record.entries) {
                const sourceEntry = sourceRecord.entries[entryPlan.sourceEntryIndex];
                assert.equal(entryPlan.sourcePath,
                    `${record.sourceObjectPath}.entries[${entryPlan.sourceEntryIndex}]`);
                assert.deepEqual(entryPlan.nestedSourcePaths,
                    nestedSourcePaths(sourceEntry, entryPlan.sourcePath));
                assert.ok(entryPlan.classifications.length > 0);
                for (const item of entryPlan.classifications) {
                    assert.ok(['INTERPRETED', 'DUPLICATE', 'OMITTED'].includes(item.classification));
                    assert.ok(item.portion && item.reason);
                    if (item.classification === 'OMITTED') {
                        assert.ok([
                            'SCHEMA_UNSUPPORTED', 'REFERENCE_UNRESOLVED', 'SOURCE_AMBIGUOUS',
                            'EXTERNAL_DEPENDENCY', 'NON_MECHANICAL', 'OUT_OF_APPROVED_SCOPE'
                        ].includes(item.cause));
                    }
                    if (item.classification === 'OMITTED' || item.classification === 'DUPLICATE') {
                        const file = path.join(workspace, 'doc', record.model,
                            `${item.classification.toLowerCase()}.md`);
                        assert.ok(fs.existsSync(file), `missing Skill ${item.classification} register`);
                        const content = fs.readFileSync(file, 'utf8');
                        assert.ok(content.includes(`entry ${entryPlan.sourceEntryIndex}`));
                        assert.ok(content.includes(JSON.stringify(sourceEntry, null, 2)),
                            `${record.model} register does not preserve ${record.canonicalId}`
                            + ` entry ${entryPlan.sourceEntryIndex}`);
                    }
                }
            }
            if (record.canonicalOutput) {
                assert.ok(outputById.has(record.canonicalId), `missing Phase 5 output ${record.canonicalId}`);
                covered.add(record.canonicalId);
            } else if (record.disposition === 'duplicate') {
                assert.equal(record.conversionStatus, 'fully-represented');
                assert.equal(outputById.has(record.canonicalId), false);
                assert.ok(record.duplicateOf);
                assert.ok(outputById.has(record.duplicateOf) || phase3Ids.has(record.duplicateOf),
                    `missing duplicate target ${record.duplicateOf}`);
                assert.ok(record.entries.flatMap(entry => entry.classifications)
                    .every(item => item.classification === 'DUPLICATE'));
            } else {
                assert.equal(record.disposition, 'unsupported');
                assert.equal(outputById.has(record.canonicalId), false);
                assert.ok(record.reason);
            }
        }
        assert.deepEqual([...covered].sort(), [...outputById.keys()].sort());

        check('Phase 5 source-field issues match pinned source values and manifest count', () => {
            assert.equal(manifest.sourceFieldIssueCount, manifest.sourceFieldIssues.length);
            assert.equal(manifest.sourceFieldIssues.length, 165);
            const sourceDataByFile = new Map();
            for (const issue of manifest.sourceFieldIssues) {
                if (!sourceDataByFile.has(issue.sourceFile)) {
                    sourceDataByFile.set(issue.sourceFile, readJson(path.join(
                        workspace,
                        'src/main/resources/data',
                        issue.sourceFile
                    )));
                }
                const sourceRecord = resolveSourcePath(
                    sourceDataByFile.get(issue.sourceFile),
                    issue.sourceObjectPath
                );
                assert.equal(issue.canonicalId,
                    `${issue.model}:${canonicalSlug(sourceRecord.source)}:${canonicalSlug(sourceRecord.name)}`);
                assert.ok(Object.hasOwn(sourceRecord, issue.sourceField),
                    `${issue.canonicalId}: missing source field ${issue.sourceField}`);
                assert.deepEqual(issue.sourceValue, sourceRecord[issue.sourceField],
                    `${issue.canonicalId}: source field ${issue.sourceField} differs from pinned source`);
            }
        });

        check('Phase 5 Rule inventory and approved mechanics batches', () => {
            const rules = readJson(path.join(workspace,
                'src/main/resources/data/variantrules.json')).variantrule;
            const phase3Rules = phase3Files().map(file => readJson(file))
                .filter(entity => entity.id.startsWith('rule:'));
            const phase5Rules = entities.map(({ entity }) => entity)
                .filter(entity => entity.id.startsWith('rule:'));
            assert.equal(rules.length, 231);
            assert.equal(phase3Rules.length, 5);
            assert.equal(phase5Rules.length, 226);
            assert.deepEqual(phase5Rules.map(entity => entity.id).sort(), [
                'rule:aag:crashing',
                'rule:aag:crew',
                'rule:aag:ship-repairs',
                'rule:aag:ship-to-ship-combat',
                'rule:ai:downtime-and-franchise-activity-explore-territory',
                'rule:ai:downtime-and-franchise-activity-franchise-restructuring',
                'rule:ai:downtime-and-franchise-activity-headquarters-modification',
                'rule:ai:downtime-and-franchise-activity-marketeering',
                'rule:ai:downtime-and-franchise-activity-philanthropic-enterprise',
                'rule:ai:downtime-and-franchise-activity-running-a-franchise',
                'rule:ai:downtime-and-franchise-activity-schmoozing',
                'rule:ai:downtime-and-franchise-activity-scrutineering',
                'rule:ai:downtime-and-franchise-activity-shady-business-practice',
                'rule:ai:downtime-and-franchise-activity-team-building',
                'rule:dmg:action-options',
                'rule:dmg:cleaving-through-creatures',
                'rule:dmg:diagonals',
                'rule:dmg:facing',
                'rule:dmg:flanking',
                'rule:dmg:firearms',
                'rule:dmg:healing',
                'rule:dmg:hitting-cover',
                'rule:dmg:initiative-variants',
                'rule:dmg:injuries',
                'rule:dmg:massive-damage',
                'rule:dmg:planar-effects',
                'rule:dmg:proficiency-dice',
                'rule:dmg:rest-variants',
                'rule:dmg:skill-variants',
                'rule:esk:sidekicks',
                'rule:gos:encounters-at-sea',
                'rule:gos:mysterious-islands',
                'rule:gos:ocean-environs',
                'rule:gos:officers-and-crew',
                'rule:gos:random-ships',
                'rule:gos:ship-stat-blocks',
                'rule:gos:ships-in-combat',
                'rule:gos:superior-ship-upgrades',
                'rule:gos:travel-at-sea',
                'rule:tce:sidekicks',
                'rule:xge:adamantine-weapons',
                'rule:xge:simultaneous-effects',
                'rule:phb:downtime-activity-recuperating',
                'rule:phb:encumbrance',
                'rule:phb:multiclassing',
                'rule:phb:playing-on-a-grid',
                'rule:phb:skills-with-different-abilities',
                'rule:xge:downtime-activity-relaxation',
                'rule:xge:falling',
                'rule:xge:sleep',
                'rule:xge:tool-proficiencies',
                'rule:xphb:action',
                'rule:xphb:ability-check',
                'rule:xphb:ability-score-and-modifier',
                'rule:xphb:advantage',
                'rule:xphb:adventure',
                'rule:xphb:alignment',
                'rule:xphb:area-of-effect',
                'rule:xphb:attack-roll',
                'rule:xphb:bonus-action',
                'rule:xphb:breaking-objects',
                'rule:xphb:campaign',
                'rule:xphb:character-sheet',
                'rule:xphb:burrow-speed',
                'rule:xphb:carrying-capacity',
                'rule:xphb:climb-speed',
                'rule:xphb:climbing',
                'rule:xphb:cone-area-of-effect',
                'rule:xphb:cover',
                'rule:xphb:creature',
                'rule:xphb:creature-type',
                'rule:xphb:crawling',
                'rule:xphb:cube-area-of-effect',
                'rule:xphb:cylinder-area-of-effect',
                'rule:xphb:death-saving-throw',
                'rule:xphb:damage',
                'rule:xphb:damage-roll',
                'rule:xphb:damage-threshold',
                'rule:xphb:damage-types',
                'rule:xphb:difficulty-class',
                'rule:xphb:disadvantage',
                'rule:xphb:encounter',
                'rule:xphb:emanation-area-of-effect',
                'rule:xphb:expertise',
                'rule:xphb:fly-speed',
                'rule:xphb:flying',
                'rule:xphb:grappling',
                'rule:xphb:hit-points',
                'rule:xphb:high-jump',
                'rule:xphb:hazard',
                'rule:xphb:hover',
                'rule:xphb:initiative',
                'rule:xphb:immunity',
                'rule:xphb:improvised-weapons',
                'rule:xphb:jumping',
                'rule:xphb:line-area-of-effect',
                'rule:xphb:long-jump',
                'rule:xphb:long-rest',
                'rule:xphb:monster',
                'rule:xphb:knocking-out-a-creature',
                'rule:xphb:occupied-space',
                'rule:xphb:passive-perception',
                'rule:xphb:player-character',
                'rule:xphb:proficiency',
                'rule:xphb:reach',
                'rule:xphb:reaction',
                'rule:xphb:resistance',
                'rule:xphb:saving-throw',
                'rule:xphb:simultaneous-effects',
                'rule:xphb:size',
                'rule:xphb:short-rest',
                'rule:xphb:skill',
                'rule:xphb:speed',
                'rule:xphb:stable',
                'rule:xphb:spell-attack',
                'rule:xphb:sphere-area-of-effect',
                'rule:xphb:swim-speed',
                'rule:xphb:swimming',
                'rule:xphb:teleportation',
                'rule:xphb:temporary-hit-points',
                'rule:xphb:target',
                'rule:xphb:unoccupied-space',
                'rule:xphb:vulnerability',
                'rule:mm:player-characters-as-lycanthropes',
                'rule:xphb:bright-light',
                'rule:xphb:darkness',
                'rule:xphb:dim-light',
                'rule:xphb:heavily-obscured',
                'rule:xphb:lightly-obscured',
                'rule:cos:prophet-s-gambit', 'rule:dmg:alien-technology', 'rule:dmg:fear-and-horror',
                'rule:dmg:hero-points', 'rule:phb:equipment-sizes', 'rule:scag:human-languages',
                'rule:vrgr:fear-and-stress', 'rule:xphb:attunement', 'rule:xphb:challenge-rating',
                'rule:xphb:condition', 'rule:xphb:dead', 'rule:xphb:healing',
                'rule:xphb:hit-point-dice', 'rule:xphb:object', 'rule:xphb:per-day',
                'rule:xphb:round-down', 'rule:xphb:save', 'rule:xphb:stat-block',
                'rule:xphb:weapon', 'rule:xphb:weapon-attack',
                'rule:dmg:alternatives-to-epic-boons', 'rule:dmg:level-advancement-without-xp',
                'rule:dmg:automatic-success',
                'rule:dmg:milestones', 'rule:dmg:plot-points', 'rule:dmg:renown',
                'rule:phb:customizing-ability-scores', 'rule:phb:feats',
                'rule:tce:customizing-your-origin', 'rule:tce:optional-class-features',
                'rule:xge:tying-knots', 'rule:xphb:experience-points',
                'rule:dmg:loyalty', 'rule:xphb:ally', 'rule:xphb:enemy',
                'rule:xphb:friendly-attitude', 'rule:xphb:hostile-attitude',
                'rule:xphb:indifferent-attitude', 'rule:xphb:attitude', 'rule:xphb:nonplayer-character',
                'rule:dmg:inspiration', 'rule:dmg:madness',
                'rule:dmg:new-ability-scores-honor-and-sanity', 'rule:dmg:morale',
                'rule:phb:inspiration', 'rule:psi:emrakul-s-madness',
                'rule:xge:shared-campaign-variant-rules', 'rule:xphb:heroic-inspiration',
                'rule:dmg:explosives', 'rule:dmg:mixing-potions',
                'rule:dmg:more-difficult-identification', 'rule:dmg:scroll-mishaps',
                'rule:dmg:spell-points', 'rule:dmg:wands-that-don-t-recharge',
                'rule:mm:familiars', 'rule:mm:player-characters-as-vampires',
                'rule:vgm:familiars', 'rule:vrgr:haunted-traps',
                'rule:vrgr:survivors', 'rule:xge:spellcasting',
                'rule:xphb:armor-class', 'rule:xphb:armor-training', 'rule:xphb:cantrip', 'rule:xphb:curses',
                'rule:xphb:illusions', 'rule:xphb:magical-effect', 'rule:xphb:possession',
                'rule:xphb:ritual', 'rule:xphb:shape-shifting', 'rule:xphb:spell',
                'rule:xphb:spellcasting-focus', 'rule:xphb:telepathy',
                'rule:dmg:downtime-activity-building-a-stronghold',
                'rule:dmg:downtime-activity-carousing',
                'rule:dmg:downtime-activity-gaining-renown',
                'rule:dmg:downtime-activity-performing-sacred-rites',
                'rule:dmg:downtime-activity-running-a-business',
                'rule:dmg:downtime-activity-selling-magic-items',
                'rule:dmg:downtime-activity-sowing-rumors',
                'rule:dmg:downtime-activity-training-to-gain-levels',
                'rule:phb:downtime-activity-crafting',
                'rule:phb:downtime-activity-practicing-a-profession',
                'rule:phb:downtime-activity-researching', 'rule:phb:downtime-activity-training',
                'rule:xge:downtime-activity-buying-a-magic-item', 'rule:xge:downtime-activity-carousing',
                'rule:xge:downtime-activity-crafting-an-item', 'rule:xge:downtime-activity-crime',
                'rule:xge:downtime-activity-gambling', 'rule:xge:downtime-activity-pit-fighting',
                'rule:xge:downtime-activity-religious-service', 'rule:xge:downtime-activity-research',
                'rule:xge:downtime-activity-scribing-a-spell-scroll',
                'rule:xge:downtime-activity-selling-a-magic-item',
                'rule:xge:downtime-activity-training', 'rule:xge:downtime-activity-work',
                'rule:xge:downtime-revisited'
            ].sort());
            const initiative = manifest.records.find(record =>
                record.canonicalId === 'rule:xphb:initiative');
            assert.equal(initiative.conversionStatus, 'partially-represented');
            assert.equal(initiative.canonicalOutput, 'rule/xphb-initiative.json');
            assert.equal(outputById.has('rule:xphb:initiative'), true);
            assert.equal(initiative.entries[1].classifications[0].classification, 'INTERPRETED');
            assert.equal(phase5Rules.reduce((sum, entity) => sum + (entity.mechanics?.length ?? 0), 0), 126);
            const byId = new Map(phase5Rules.map(entity => [entity.id, entity]));
            assert.deepEqual(byId.get('rule:xphb:ability-check').mechanics[0].abilities, [
                'strength', 'dexterity', 'constitution', 'intelligence', 'wisdom', 'charisma'
            ]);
            assert.deepEqual(byId.get('rule:xphb:attack-roll').mechanics[0].roll.forms,
                ['weapon', 'unarmed', 'spell']);
            assert.deepEqual(byId.get('rule:xphb:saving-throw').mechanics[0].savingThrow, {
                kind: 'SAVING_THROW',
                abilities: ['strength', 'dexterity', 'constitution', 'intelligence', 'wisdom', 'charisma'],
                abilitySelection: 'effect',
                dc: { kind: 'EFFECT_DEFINED' },
                mayChooseToFail: true,
                automaticFailureWhenAbilityScoreAbsent: true
            });
            assert.deepEqual(byId.get('rule:xphb:advantage').mechanics[0].modifier, {
                kind: 'D20_ROLL_MODIFIER', roll: 'd20-test', direction: 'advantage', diceCount: 2,
                select: 'higher', cancelsOpposite: true, multipleInstancesStack: false
            });
            assert.deepEqual(byId.get('rule:xphb:disadvantage').mechanics[0].modifier, {
                kind: 'D20_ROLL_MODIFIER', roll: 'd20-test', direction: 'disadvantage', diceCount: 2,
                select: 'lower', cancelsOpposite: true, multipleInstancesStack: false
            });
            assert.equal(byId.get('rule:xphb:death-saving-throw').mechanics[0].trigger,
                'start-of-turn-at-zero-hit-points');
            assert.equal(byId.get('rule:xphb:death-saving-throw').mechanics[0].appliesTo, 'PLAYER_CHARACTER');
            assert.equal(byId.get('rule:dmg:hitting-cover').mechanics, undefined);
            assert.deepEqual(byId.get('rule:xphb:passive-perception').mechanics[0].skill,
                { targetType: 'skill', id: 'skill:xphb:perception' });
            const ruleTypeMap = { C: 'CORE', O: 'OPTIONAL', V: 'VARIANT', VO: 'VARIANT_OPTIONAL' };
            const mappings = manifest.sourceFieldMappings.filter(mapping => mapping.model === 'rule');
            for (const mapping of mappings) {
                const sourceIndex = Number(mapping.sourceObjectPath.match(/\[(\d+)\]$/)[1]);
                const sourceRule = readJson(path.join(workspace,
                    'src/main/resources/data/variantrules.json')).variantrule[sourceIndex];
                assert.equal(sourceRule.ruleType, mapping.sourceValue);
                assert.equal(mapping.canonicalValue, ruleTypeMap[mapping.sourceValue]);
            }
            assert.deepEqual(new Set(mappings.map(mapping => mapping.sourceValue)),
                new Set(['C', 'O', 'V', 'VO']));
            const d20Test = phase3Rules.find(entity => entity.id === 'rule:xphb:d20-test');
            for (const entity of phase5Rules) {
                assert.equal(entity.ruleType,
                    ruleTypeMap[sourceRecordForCanonical(entity).ruleType]);
            }
            const validateRule = ajv.getSchema('urn:thedndpub:schemas:core-rules:rule:v1');
            for (const ruleType of Object.values(ruleTypeMap)) {
                const candidate = structuredClone(d20Test);
                candidate.ruleType = ruleType;
                assert.equal(validateRule(candidate), true, JSON.stringify(validateRule.errors));
            }
            const invalidType = structuredClone(d20Test);
            invalidType.ruleType = 'UNKNOWN';
            rejects('Rule schema rejects an unknown ruleType', () => validateRule(invalidType) === true);
            const wrongMapping = structuredClone(byId.get('rule:xphb:advantage'));
            wrongMapping.ruleType = 'OPTIONAL';
            rejects('Rule source-fidelity assertion rejects a wrong ruleType mapping', () =>
                wrongMapping.ruleType === ruleTypeMap[sourceRecordForCanonical(wrongMapping).ruleType]);
        });

        check('R-GAP-01 ability score modifier mapping matches the source range', () => {
            const sourceRule = sourceRecordForCanonical({
                id: 'rule:xphb:ability-score-and-modifier',
                name: 'Ability Score and Modifier',
                sourceMetadata: { source: 'XPHB' }
            });
            const entity = entities.map(({ entity }) => entity)
                .find(candidate => candidate.id === 'rule:xphb:ability-score-and-modifier');
            assert.deepEqual(sourceRule, rules[1]);
            assert.ok(sourceRule.entries[0].includes('six ability scores'));
            const mapping = entity.mechanics[0];
            assert.equal(mapping.minimumScore, 1);
            assert.equal(mapping.maximumScore, 30);
            assert.deepEqual(mapping.scoreBands, [
                { minimumScore: 1, maximumScore: 1, modifier: -5 },
                { minimumScore: 2, maximumScore: 3, modifier: -4 },
                { minimumScore: 4, maximumScore: 5, modifier: -3 },
                { minimumScore: 6, maximumScore: 7, modifier: -2 },
                { minimumScore: 8, maximumScore: 9, modifier: -1 },
                { minimumScore: 10, maximumScore: 11, modifier: 0 },
                { minimumScore: 12, maximumScore: 13, modifier: 1 },
                { minimumScore: 14, maximumScore: 15, modifier: 2 },
                { minimumScore: 16, maximumScore: 17, modifier: 3 },
                { minimumScore: 18, maximumScore: 19, modifier: 4 },
                { minimumScore: 20, maximumScore: 21, modifier: 5 },
                { minimumScore: 22, maximumScore: 23, modifier: 6 },
                { minimumScore: 24, maximumScore: 25, modifier: 7 },
                { minimumScore: 26, maximumScore: 27, modifier: 8 },
                { minimumScore: 28, maximumScore: 29, modifier: 9 },
                { minimumScore: 30, maximumScore: 30, modifier: 10 }
            ]);
        });

        check('R-GAP-05 reuses finite source-backed Difficulty Class values', () => {
            const sourceRule = rules[54];
            const entity = entities.map(({ entity }) => entity)
                .find(candidate => candidate.id === 'rule:xphb:difficulty-class');
            assert.equal(sourceRule.name, 'Difficulty Class');
            assert.ok(sourceRule.entries[0].includes('target number'));
            assert.deepEqual(entity.mechanics[0].targetNumberFor, ['ABILITY_CHECK', 'SAVING_THROW']);
            const validateRule = ajv.getSchema('urn:thedndpub:schemas:core-rules:rule:v1');
            const abilityCheck = canonicalFixture('rule-ability-check.json');
            const values = [
                { kind: 'FIXED', value: 15 },
                { kind: 'ABILITY_PLUS_PROFICIENCY', base: 8, ability: 'strength', proficiencyBonus: true },
                { kind: 'FIXED_PLUS_SPELL_LEVEL', base: 15, spellRole: 'identified-spell' },
                { kind: 'GREATER_OF_FIXED_AND_TARGET_ABILITY_SCORE', minimum: 15, targetRole: 'target', ability: 'charisma' }
            ];
            for (const dc of values) {
                const candidate = structuredClone(abilityCheck);
                candidate.mechanics[0].dc = dc;
                assert.equal(validateRule(candidate), true, JSON.stringify(validateRule.errors));
            }
            const identifySpell = entities.map(({ entity }) => entity)
                .find(candidate => candidate.id === 'action:xge:identify-a-spell');
            const identifyProcedure = identifySpell.operations.find(operation =>
                operation.kind === 'SPELL_IDENTIFICATION');
            assert.equal(identifySpell.operations.find(operation =>
                operation.operationId === identifyProcedure.checkId).dc.kind, 'FIXED_PLUS_SPELL_LEVEL');
            const influence = entities.map(({ entity }) => entity)
                .find(candidate => candidate.id === 'action:xphb:influence');
            assert.equal(influence.operations.find(operation => operation.kind === 'INFLUENCE_PROCEDURE')
                .branches.find(branch => branch.willingness === 'HESITANT').dc.kind,
            'GREATER_OF_FIXED_AND_TARGET_ABILITY_SCORE');
        });

        check('R-GAP-06 preserves distinct proficiency application and scaling rules', () => {
            const findRule = (name, source) => rules.find(candidate =>
                candidate.name === name && candidate.source === source);
            const findCanonical = id => entities.map(({ entity }) => entity)
                .find(candidate => candidate.id === id);
            const expertiseSource = findRule('Expertise', 'XPHB');
            assert.ok(expertiseSource.entries[0].includes('Proficiency Bonus} is doubled'));
            assert.ok(expertiseSource.entries[0].includes('unless the bonus is doubled by another feature'));
            assert.ok(expertiseSource.entries[1].includes("can't have Expertise in the same skill proficiency more than once"));
            const expertise = findCanonical('rule:xphb:expertise').mechanics[0];
            assert.equal(expertise.multiplier, 2);
            assert.equal(expertise.requiresSkillProficiency, true);
            assert.equal(expertise.unlessAlreadyDoubledByAnotherFeature, true);
            assert.equal(expertise.maximumInstancesPerSkill, 1);

            const proficiencySource = findRule('Proficiency', 'XPHB');
            assert.ok(proficiencySource.entries[0].includes('any {@variantrule D20 Test|XPHB}'));
            const proficiency = findCanonical('rule:xphb:proficiency').mechanics[0];
            assert.deepEqual(proficiency.appliesTo, [
                'SKILL_ASSOCIATED_ABILITY_CHECK', 'SAVING_THROW', 'WEAPON_ATTACK', 'TOOL_CHECK'
            ]);
            const progression = findCanonical('rule:xphb:proficiency').mechanics[1];
            assert.equal(progression.subtype, 'BONUS_PROGRESSION');
            const expectedLevelBands = [
                [1, 4, 2], [5, 8, 3], [9, 12, 4], [13, 16, 5],
                [17, 20, 6], [21, 24, 7], [25, 28, 8], [29, 30, 9]
            ];
            const expectedChallengeRatingBands = [
                [null, 0, 4, 2], [4, null, 8, 3], [8, null, 12, 4], [12, null, 16, 5],
                [16, null, 20, 6], [20, null, 24, 7], [24, null, 28, 8], [28, null, 30, 9]
            ];
            assert.deepEqual(progression.levelBands.map(band => [
                band.minimumLevel, band.maximumLevel, band.bonus
            ]), expectedLevelBands);
            assert.deepEqual(progression.challengeRatingBands.map(band => [
                band.minimumChallengeRatingExclusive ?? null,
                band.minimumChallengeRatingInclusive ?? null,
                band.maximumChallengeRatingInclusive,
                band.bonus
            ]), expectedChallengeRatingBands);

            const diceSource = findRule('Proficiency Dice', 'DMG');
            const table = diceSource.entries[3];
            assert.equal(table.type, 'table');
            const dice = findCanonical('rule:dmg:proficiency-dice').mechanics[0];
            assert.deepEqual(dice.levelBands, [
                { minimumLevel: 1, maximumLevel: 4, proficiencyBonus: 2, die: 'd4' },
                { minimumLevel: 5, maximumLevel: 8, proficiencyBonus: 3, die: 'd6' },
                { minimumLevel: 9, maximumLevel: 12, proficiencyBonus: 4, die: 'd8' },
                { minimumLevel: 13, maximumLevel: 16, proficiencyBonus: 5, die: 'd10' },
                { minimumLevel: 17, maximumLevel: 20, proficiencyBonus: 6, die: 'd12' }
            ]);
            assert.equal(dice.expertiseDiceCount, 2);
            assert.deepEqual(dice.eligibleCreatureKinds, ['PLAYER_CHARACTER', 'NPC_WITH_LEVELS']);

            const multiclassSource = findRule('Multiclassing', 'PHB');
            assert.ok(multiclassSource.entries[6].entries[0].includes('total character level'));
            assert.equal(findCanonical('rule:phb:multiclassing').mechanics[0].levelBasis,
                'TOTAL_CHARACTER_LEVEL');
            assert.equal(findCanonical('rule:phb:multiclassing').references, undefined);

            const skillSource = findRule('Skill', 'XPHB');
            assert.ok(skillSource.entries[0].includes('associated with an ability check'));
            assert.equal(findCanonical('rule:xphb:skill').mechanics[0].onProficiency,
                'ADD_PROFICIENCY_BONUS');

            const toolSource = findRule('Tool Proficiencies', 'XGE');
            assert.ok(JSON.stringify(toolSource.entries).includes('proficient with the tool and the skill'));
            assert.equal(findCanonical('rule:xge:tool-proficiencies').mechanics[0].dmMayGrantAdvantage, true);
        });

        check('R-GAP-10 passive score uses the linked check and exact adjustments', () => {
            const sourceRule = rules[166];
            const entity = entities.map(({ entity }) => entity)
                .find(candidate => candidate.id === 'rule:xphb:passive-perception');
            const checkComponent = entity.mechanics[0];
            assert.ok(sourceRule.entries[1].includes('equals 10 plus'));
            assert.ok(sourceRule.entries[1].includes('increase the score by 5'));
            assert.ok(sourceRule.entries[1].includes('decrease the score by 5'));
            assert.equal(checkComponent.passiveScore, true);
            assert.deepEqual(checkComponent.passiveScoreConstruction, {
                kind: 'PASSIVE_SCORE_CONSTRUCTION',
                base: 10,
                addsCheckBonus: true,
                advantageAdjustment: 5,
                disadvantageAdjustment: -5
            });
            assert.deepEqual(checkComponent.abilities, ['wisdom']);
            assert.deepEqual(checkComponent.skill, { targetType: 'skill', id: 'skill:xphb:perception' });
        });

        check('Batch 2 preserves death-save, Flanking, Cover, and Skill-variation source mechanics', () => {
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const deathSource = rules[51];
            assert.equal(deathSource.name, 'Death Saving Throw');
            assert.ok(deathSource.entries[0].includes('Player Character'));
            const deathSave = ruleById.get('rule:xphb:death-saving-throw').mechanics[0];
            assert.equal(deathSave.trigger, 'start-of-turn-at-zero-hit-points');
            assert.equal(deathSave.appliesTo, 'PLAYER_CHARACTER');

            const flankSource = rules[113];
            const flanking = ruleById.get('rule:dmg:flanking');
            assert.equal(flankSource.name, 'Flanking');
            assert.equal(flanking.ruleType, 'OPTIONAL');
            assertRuleIntegrity(flanking);
            assertCanonicalReferences(flanking);

            const coverSource = rules[32];
            const cover = ruleById.get('rule:xphb:cover');
            assert.equal(coverSource.name, 'Cover');
            assert.equal(coverSource.source, 'XPHB');
            assert.deepEqual(cover.mechanics.map(component => [
                component.level,
                component.armorClassBonus,
                component.savingThrowBonus,
                component.savingThrowAbility,
                component.blocksTargeting,
                component.degreeSelection
            ]), [
                ['half', 2, 2, 'dexterity', false, 'MOST_PROTECTIVE_ONLY'],
                ['three-quarters', 5, 5, 'dexterity', false, 'MOST_PROTECTIVE_ONLY'],
                ['total', undefined, undefined, undefined, true, 'MOST_PROTECTIVE_ONLY']
            ]);
            assert.equal(JSON.stringify(cover).includes('damageReduction'), false);

            const skillVariationSource = rules[203];
            const skillVariation = ruleById.get('rule:phb:skills-with-different-abilities').mechanics[0];
            assert.equal(skillVariationSource.name, 'Skills with Different Abilities');
            assert.deepEqual(skillVariation.pairings.map(pairing => [
                pairing.skill.id, pairing.usualAbility, pairing.alternativeAbility, pairing.context
            ]), [
                ['skill:phb:athletics', 'strength', 'constitution', 'SWIMMING_STAMINA'],
                ['skill:phb:intimidation', 'charisma', 'strength', 'DISPLAY_OF_RAW_STRENGTH']
            ]);
            assert.equal(skillVariation.eligibleRequester, 'DM_OR_PLAYER');

            const dmSkillVariants = ruleById.get('rule:dmg:skill-variants');
            assert.equal(dmSkillVariants.mechanics[0].subtype, 'ABILITY_CHECK_PROFICIENCY_VARIANT');
            assert.equal(dmSkillVariants.mechanics[0].startsAtLevel, 1);
            const hittingCover = ruleById.get('rule:dmg:hitting-cover');
            assert.equal(hittingCover.mechanics, undefined);
            assertCanonicalReferences({
                id: 'rule:test:reference-only-target',
                references: [{ targetType: 'rule', id: hittingCover.id }]
            });
            const hittingCoverInventory = manifest.ruleCatalogInventory.find(item =>
                item.canonicalId === 'rule:dmg:hitting-cover');
            assert.equal(hittingCoverInventory.coverage, 'UNSUPPORTED');
            assert.equal(hittingCoverInventory.canonicalOutput,
                'datasets/phase-5/step-2/core-rules/v1/rule/dmg-hitting-cover.json');
        });

        check('Batch 3 preserves initiative, capacity, rest, and damage source mechanics', () => {
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            assert.equal(rules[136].name, 'Initiative');
            assert.ok(rules[136].entries[1].includes('10 plus your Dexterity modifier'));
            assert.equal(rules[137].name, 'Initiative Variants');
            assert.ok(rules[137].entries[2].entries[1].includes('Neither roll receives any modifiers'));
            assert.equal(rules[24].name, 'Carrying Capacity');
            assert.ok(rules[24].entries[2].rows[0][1].includes('7.5'));
            assert.equal(rules[99].name, 'Encumbrance');
            assert.ok(rules[99].entries[2].includes('10 times your Strength score'));
            assert.equal(rules[147].name, 'Long Rest');
            assert.ok(rules[147].entries[0].includes('at least 8 hours'));
            assert.equal(rules[195].name, 'Short Rest');
            assert.ok(rules[195].entries[5].includes('confers no benefits'));
            assert.equal(rules[182].name, 'Resistance');
            assert.equal(rules[227].name, 'Vulnerability');
            const initiative = ruleById.get('rule:xphb:initiative').mechanics[0];
            assert.deepEqual([
                initiative.base, initiative.ability,
                initiative.advantageAdjustment, initiative.disadvantageAdjustment
            ], [10, 'dexterity', 5, -5]);
            const side = ruleById.get('rule:dmg:initiative-variants').mechanics[1];
            assert.deepEqual([
                side.roll, side.tieResolution, side.membersChooseOrder,
                side.roundEndsAfterAllSidesAct, side.multipleSidesOrderedByRoll
            ], ['UNMODIFIED_D20', 'REROLL_TIED_SIDES', true, true, true]);
            assert.equal(ruleById.get('rule:dmg:initiative-variants').mechanics.length, 2,
                'Speed Factor remains explicitly unsupported');

            const capacity = ruleById.get('rule:xphb:carrying-capacity').mechanics[0];
            assert.deepEqual(capacity.sizeMultipliers.map(row => [
                row.sizes, row.carryMultiplier, row.dragLiftPushMultiplier
            ]), [
                [['TINY'], 7.5, 15], [['SMALL', 'MEDIUM'], 15, 30], [['LARGE'], 30, 60],
                [['HUGE'], 60, 120], [['GARGANTUAN'], 120, 240]
            ]);
            assert.equal(capacity.dragLiftPushSpeedCapFeet, 5);
            const encumbrance = ruleById.get('rule:phb:encumbrance').mechanics[0];
            assert.equal(encumbrance.thresholdComparison, 'GREATER_THAN');
            assert.deepEqual(encumbrance.encumbranceThresholds.map(row => [
                row.weightMultiplier, row.speedReductionFeet, row.abilityCheckDisadvantage.length
            ]), [[5, 10, 0], [10, 20, 3]]);

            const longRest = ruleById.get('rule:xphb:long-rest').mechanics[0];
            assert.deepEqual([
                longRest.duration, longRest.durationQualifier, longRest.minimumHitPointsToStart,
                longRest.minimumHoursBetweenStarts, longRest.physicalExertionInterruptionHours,
                longRest.shortRestBenefitThresholdHours, longRest.addedDurationPerInterruption.amount
            ], [8, 'AT_LEAST', 1, 16, 1, 1, 1]);
            const shortRest = ruleById.get('rule:xphb:short-rest').mechanics[0];
            assert.deepEqual(shortRest.interruptions, ['INITIATIVE', 'NON_CANTRIP_SPELL', 'DAMAGE']);
            assert.equal(shortRest.interruptionOutcome, 'NO_BENEFITS');
            const sleep = ruleById.get('rule:xge:sleep').mechanics[1].deprivationProcedure;
            assert.deepEqual([
                sleep.intervalHours, sleep.savingThrow.baseDc,
                sleep.savingThrow.increasePerConsecutiveInterval, sleep.dcResetOnLongRest
            ], [24, 10, 5, true]);
            const healing = ruleById.get('rule:dmg:healing').mechanics;
            const shortSurgeRecovery = healing[0].recoveryOutcomes[0];
            assert.deepEqual([
                shortSurgeRecovery.kind, shortSurgeRecovery.basis,
                shortSurgeRecovery.fractionDenominator, shortSurgeRecovery.minimum,
                shortSurgeRecovery.rounding
            ], ['RESTORE_LEVEL_FRACTION', 'CHARACTER_LEVEL', 4, 1, 'DOWN']);
            const armoredSleepRecovery = ruleById.get('rule:xge:sleep').mechanics[0]
                .recoveryLimitations[0].limitedOutcome;
            assert.deepEqual([
                armoredSleepRecovery.kind, armoredSleepRecovery.numerator,
                armoredSleepRecovery.denominator, armoredSleepRecovery.minimum,
                armoredSleepRecovery.rounding
            ], ['RESTORE_FRACTION', 1, 4, 1, 'DOWN']);
            const restVariants = ruleById.get('rule:dmg:rest-variants').mechanics;
            assert.deepEqual(restVariants.map(rest => [rest.subtype, rest.duration, rest.unit]), [
                ['SHORT_REST', 5, 'minutes'], ['LONG_REST', 1, 'hour'],
                ['SHORT_REST', 8, 'hours'], ['LONG_REST', 7, 'days']
            ]);

            const resistance = ruleById.get('rule:xphb:resistance').mechanics[0].resolutionModifier;
            const vulnerability = ruleById.get('rule:xphb:vulnerability').mechanics[0].resolutionModifier;
            assert.deepEqual([
                resistance.operation, resistance.multiplier, resistance.rounding,
                resistance.oncePerDamageInstance, resistance.sameOperationDoesNotStack
            ], ['HALVE', 0.5, 'DOWN', true, true]);
            assert.deepEqual([
                vulnerability.operation, vulnerability.multiplier,
                vulnerability.oncePerDamageInstance, vulnerability.sameOperationDoesNotStack
            ], ['DOUBLE', 2, true, true]);

            const planarIssue = manifest.sourceFieldIssues.find(issue =>
                issue.canonicalId === 'rule:dmg:planar-effects' && issue.sourceField === 'reprintedAs');
            assert.ok(planarIssue);
            assert.equal(planarIssue.cause, 'REFERENCE_UNRESOLVED');
            assert.equal(ruleById.get('rule:dmg:planar-effects').mechanics, undefined);
            for (const id of ['rule:dmg:diagonals', 'rule:xge:falling']) {
                const unresolvedReprint = manifest.sourceFieldIssues.find(issue =>
                    issue.canonicalId === id && issue.sourceField === 'reprintedAs');
                assert.ok(unresolvedReprint);
                assert.equal(unresolvedReprint.cause, 'REFERENCE_UNRESOLVED');
                assert.equal(Object.hasOwn(ruleById.get(id).sourceMetadata, 'reprintedAs'), false);
            }
        });

        check('Rule catalog inventory reconciles identities, source paths, and explicit pending records', () => {
            const inventory = manifest.ruleCatalogInventory;
            const summary = manifest.ruleCatalogSummary;
            const catalog = readJson(path.join(workspace,
                'src/main/resources/data/variantrules.json')).variantrule;
            assert.equal(inventory.length, 231);
            assert.equal(new Set(inventory.map(item => item.canonicalId)).size, 231);
            assert.equal(summary.totalSourceRecords, 231);
            assert.equal(summary.sourceQualifiedIdentityCount, 231);
            assert.equal(summary.semanticReviewCompleteCount, 231);
            assert.equal(summary.semanticReviewPendingCount, 0);
            assert.equal(summary.conversionAccountedCount, 231);
            assert.equal(summary.conversionPendingCount, 0);
            assert.equal(Object.hasOwn(summary, 'pendingSemanticReviewCount'), false);
            assert.equal(summary.fullCount, 68);
            assert.equal(summary.partialCount, 90);
            assert.equal(summary.unsupportedCount, 73);
            assert.equal(summary.canonicalRuleOutputCount, 231);
            const p5RuleAccounting = new Map(manifest.records
                .filter(record => record.model === 'rule')
                .map(record => [record.canonicalId, record]));
            const p3RuleAccounting = new Map(readJson(path.join(workspace,
                'reports/step-2/CORE_RULES_PHASE_3_SOURCE_ACCOUNTING.json')).records
                .filter(record => record.model === 'rule')
                .map(record => [record.canonicalId, record]));
            const documentedGaps = fs.readFileSync(path.join(workspace,
                'reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_GAP_INVENTORY.md'), 'utf8');
            const collectNestedTextPaths = (value, sourcePath, collected) => {
                if (Array.isArray(value)) {
                    value.forEach((item, index) =>
                        collectNestedTextPaths(item, `${sourcePath}[${index}]`, collected));
                } else if (value && typeof value === 'object') {
                    for (const [key, child] of Object.entries(value)) {
                        collectNestedTextPaths(child, `${sourcePath}.${key}`, collected);
                    }
                } else if (typeof value === 'string') {
                    collected.push(sourcePath);
                }
            };
            for (const [index, item] of inventory.entries()) {
                const source = catalog[index];
                assert.equal(item.sourceObjectPath, `$.variantrule[${index}]`);
                assert.equal(item.canonicalId,
                    `rule:${canonicalSlug(source.source)}:${canonicalSlug(source.name)}`);
                assert.equal(item.name, source.name);
                assert.equal(item.source, source.source);
                assert.equal(item.page, source.page);
                assert.equal(item.ruleType, source.ruleType);
                assert.deepEqual(item.reprintedAs, source.reprintedAs);
                assert.deepEqual(item.sourceEntryPaths,
                    source.entries.map((_, entryIndex) => `${item.sourceObjectPath}.entries[${entryIndex}]`));
                assert.equal(item.sourceEntryRootCount, source.entries.length);
                const expectedNestedPaths = [];
                source.entries.forEach((entry, entryIndex) => {
                    const rootPath = `${item.sourceObjectPath}.entries[${entryIndex}]`;
                    const leaves = [];
                    collectNestedTextPaths(entry, rootPath, leaves);
                    expectedNestedPaths.push(...leaves.filter(leaf => leaf !== rootPath));
                });
                assert.deepEqual(item.nestedClausePaths, expectedNestedPaths);
                assert.equal(item.nestedClausePathCount, expectedNestedPaths.length);
                const sourceFields = Object.fromEntries(Object.entries(source)
                    .filter(([key]) => !['name', 'source', 'page', 'entries'].includes(key)));
                assert.deepEqual(item.structuredSourceFields, sourceFields);
                assert.equal(item.accountingRecordPresent,
                    p3RuleAccounting.has(item.canonicalId) || p5RuleAccounting.has(item.canonicalId));
                assert.equal(item.semanticReviewStatus, 'REVIEWED');
                assert.equal(Object.hasOwn(item, 'interpretationStatus'), false);
                if (item.semanticReviewStatus === 'PENDING') {
                    assert.equal(item.coverage, 'PENDING');
                    assert.equal(item.accountingRecordPresent, false);
                    assert.deepEqual(item.interpretedMechanics, []);
                    assert.equal(item.canonicalOutput, undefined);
                    assert.deepEqual(item.gapIds, []);
                } else {
                    assert.equal(item.accountingRecordPresent, true);
                    assert.ok(p3RuleAccounting.has(item.canonicalId) || p5RuleAccounting.has(item.canonicalId));
                    for (const gapId of item.gapIds) assert.ok(documentedGaps.includes(gapId), gapId);
                }
            }
            assert.equal(inventory.filter(item => item.coverage === 'PENDING').length, 0);
        });

        check('Rule catalog reconciliation restores all outputs and supported source references', () => {
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            const outputById = new Map(entities
                .filter(({ entity }) => entity.id.startsWith('rule:'))
                .map(({ entity }) => [entity.id, entity]));
            const expectedNewRecords = {
                'rule:xphb:armor-class': ['rule/xphb-armor-class.json', 'UNSUPPORTED'],
                'rule:dmg:automatic-success': ['rule/dmg-automatic-success.json', 'UNSUPPORTED'],
                'rule:xphb:save': ['rule/xphb-save.json', 'FULL'],
                'rule:xphb:weapon-attack': ['rule/xphb-weapon-attack.json', 'FULL']
            };
            for (const [id, [file, coverage]] of Object.entries(expectedNewRecords)) {
                const item = inventory.get(id);
                const output = outputById.get(id);
                assert.ok(item && output, `missing reconciled Rule ${id}`);
                assert.equal(item.canonicalOutput, file);
                assert.equal(item.coverage, coverage);
                assert.equal(output.sourceMetadata.source, item.source);
                assert.equal(output.sourceMetadata.page, item.page);
                assert.equal(Object.hasOwn(output, 'mechanics'), false);
            }
            assert.equal(outputById.get('rule:xphb:save').references[0].id,
                'rule:xphb:saving-throw');
            assert.equal(outputById.get('rule:xphb:weapon-attack').references[0].id,
                'rule:xphb:attack-roll');
            assert.equal(outputById.size, 226);
            assert.equal(inventory.size, 231);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);

            const representedReferenceRules = {
                'rule:xphb:curses': ['spell:xphb:remove-curse', 'spell:xphb:greater-restoration'],
                'rule:xphb:possession': ['spell:xphb:protection-from-evil-and-good',
                    'spell:xphb:dispel-evil-and-good'],
                'rule:mm:familiars': ['spell:phb:find-familiar', 'creature:mm:crawling-claw',
                    'creature:mm:imp', 'creature:mm:pseudodragon', 'creature:mm:quasit'],
                'rule:vgm:familiars': ['spell:phb:find-familiar', 'creature:vgm:cranium-rat',
                    'creature:mm:crawling-claw', 'creature:vgm:gazer', 'creature:mm:imp',
                    'creature:mm:pseudodragon', 'creature:mm:quasit'],
                'rule:mm:player-characters-as-lycanthropes': ['creature:mm:werebear',
                    'creature:mm:wereboar', 'creature:mm:wererat', 'creature:mm:weretiger',
                    'creature:mm:werewolf', 'creature:vrgr:wereraven', 'creature:wdmm:werebat',
                    'creature:vrgr:loup-garou', 'spell:phb:remove-curse'],
                'rule:mm:player-characters-as-vampires': ['creature:mm:vampire-spawn',
                    'creature:mm:vampire', 'spell:phb:wish'],
                'rule:dmg:more-difficult-identification': ['spell:phb:identify'],
                'rule:dmg:inspiration': ['rule:xphb:advantage']
            };
            for (const [id, expectedReferences] of Object.entries(representedReferenceRules)) {
                const item = inventory.get(id);
                const output = outputById.get(id);
                assert.ok(output, `missing reference-bearing Rule ${id}`);
                assert.equal(item.coverage, 'PARTIAL');
                assert.deepEqual(output.references.map(reference => reference.id), expectedReferences);
                assert.deepEqual(output.mechanics, undefined);
                assertPhase5DiseaseReferences(output);
                assertPhase5LanguageReferences(output);
            }
            assert.deepEqual([
                manifest.ruleCatalogSummary.fullCount,
                manifest.ruleCatalogSummary.partialCount,
                manifest.ruleCatalogSummary.unsupportedCount
            ], [68, 90, 73]);
            const referenceOnly = ['rule:xphb:armor-class', 'rule:dmg:automatic-success'];
            for (const id of referenceOnly) assert.deepEqual(outputById.get(id).references, undefined);
        });

        check('RULE-CONV-01 converts exactly its 12 reference-only glossary Rules', () => {
            const expectedIds = [
                'rule:xphb:action', 'rule:xphb:adventure', 'rule:xphb:alignment',
                'rule:xphb:campaign', 'rule:xphb:character-sheet', 'rule:xphb:creature',
                'rule:xphb:creature-type', 'rule:xphb:encounter', 'rule:xphb:hazard',
                'rule:xphb:monster', 'rule:xphb:player-character', 'rule:xphb:target'
            ];
            const batchIds = manifest.records.filter(record => record.model === 'rule'
                    && expectedIds.includes(record.canonicalId)).map(record => record.canonicalId).sort();
            const newlyAccountedIds = manifest.records.filter(record => expectedIds.includes(record.canonicalId))
                .map(record => record.canonicalId);
            const outputById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const ruleById = new Map([...outputById].filter(([id]) => id.startsWith('rule:')));
            assert.deepEqual(batchIds, [...expectedIds].sort());
            assert.deepEqual(newlyAccountedIds, expectedIds);
            assert.equal(new Set(batchIds).size, 12);
            assert.equal(ruleById.size, 226);
            for (const id of expectedIds) {
                const entity = ruleById.get(id);
                assert.ok(entity, `missing canonical Rule output ${id}`);
                assert.equal(Object.hasOwn(entity, 'mechanics'), false,
                    `${id} must remain reference-only without fabricated mechanics`);
                assert.equal(entity.ruleType, 'CORE');
                assert.equal(entity.sourceMetadata.source, 'XPHB');
                assert.deepEqual(entity.sourceMetadata.flags, { srd52: true, basicRules2024: true });
                const account = manifest.records.find(record => record.canonicalId === id);
                assert.equal(account.sourceFile, 'variantrules.json');
                assert.equal(account.entries.length, account.sourceEntryCount);
                for (const entry of account.entries) {
                    assert.ok(entry.classifications.length > 0, `${id} entry ${entry.sourceEntryIndex}`);
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(item => item.portion.includes(nestedPath)),
                            `${id} nested path is not accounted: ${nestedPath}`);
                    }
                }
                const inventory = manifest.ruleCatalogInventory.find(item => item.canonicalId === id);
                const accountedPortions = account.entries.flatMap(entry =>
                    entry.classifications.map(item => item.portion));
                for (const nestedPath of inventory.nestedClausePaths) {
                    assert.ok(accountedPortions.some(portion => portion.includes(nestedPath)),
                        `${id} nested source clause is not accounted: ${nestedPath}`);
                }
                assert.equal(account.sourceObjectPath, inventory.sourceObjectPath);
                assert.equal(account.sourceEntryCount, inventory.sourceEntryRootCount);
                assertPhase5DiseaseReferences(entity);
                assertPhase5LanguageReferences(entity);
                for (const reference of entity.references ?? []) {
                    if (reference.targetType === 'rule') {
                        assert.ok(ruleById.has(reference.id) || phase3Ids.has(reference.id),
                            `${id} has unresolved canonical Rule reference ${reference.id}`);
                    } else if (!['spell', 'creature'].includes(reference.targetType)) {
                        assert.ok(outputById.has(reference.id),
                            `${id} has unresolved canonical reference ${reference.id}`);
                    }
                }
            }
            assert.deepEqual(
                manifest.ruleCatalogInventory.filter(item => expectedIds.includes(item.canonicalId))
                    .reduce((counts, item) => {
                        counts[item.coverage]++;
                        return counts;
                    }, { FULL: 0, PARTIAL: 0, UNSUPPORTED: 0 }),
                { FULL: 8, PARTIAL: 3, UNSUPPORTED: 1 }
            );
            assert.equal(manifest.ruleCatalogInventory.filter(item => item.coverage === 'PENDING').length, 0);
            assert.equal(manifest.ruleCatalogSummary.semanticReviewPendingCount, 0);
        });

        check('RULE-CONV-02 preserves combat Rule identities, references, omissions, and source accounting', () => {
            const expected = [
                ['rule:dmg:action-options', '$.variantrule[3]', 'PARTIAL', 7, 22],
                ['rule:dmg:cleaving-through-creatures', '$.variantrule[27]', 'UNSUPPORTED', 2, 0],
                ['rule:dmg:facing', '$.variantrule[105]', 'UNSUPPORTED', 6, 0],
                ['rule:dmg:firearms', '$.variantrule[112]', 'UNSUPPORTED', 3, 15],
                ['rule:dmg:injuries', '$.variantrule[138]', 'UNSUPPORTED', 6, 28],
                ['rule:dmg:massive-damage', '$.variantrule[151]', 'UNSUPPORTED', 3, 16],
                ['rule:mm:player-characters-as-lycanthropes', '$.variantrule[170]', 'PARTIAL', 4, 29]
            ];
            const ids = expected.map(([id]) => id);
            const batchRecords = manifest.records.filter(item => expected.some(([id]) => item.canonicalId === id));
            assert.deepEqual(batchRecords.map(record => record.canonicalId), ids);
            assert.equal(new Set(batchRecords.map(record => record.canonicalId)).size, 7);
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            for (const [id, sourceObjectPath, coverage, roots, nestedCount] of expected) {
                const sourceIndex = Number(sourceObjectPath.match(/\[(\d+)\]/)[1]);
                const source = rules[sourceIndex];
                const inventory = manifest.ruleCatalogInventory.find(item => item.canonicalId === id);
                const record = batchRecords.find(item => item.canonicalId === id);
                const entity = ruleById.get(id);
                assert.ok(entity, `missing canonical Rule ${id}`);
                assert.equal(record.sourceObjectPath, sourceObjectPath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(inventory.nestedClausePathCount, nestedCount);
                assert.equal(inventory.coverage, coverage);
                assert.equal(inventory.accountingRecordPresent, true);
                assert.equal(entity.name, source.name);
                const expectedMetadata = expectedSourceMetadata('rule', source);
                if (id === 'rule:dmg:firearms') delete expectedMetadata.reprintedAs;
                assert.deepEqual(entity.sourceMetadata, expectedMetadata);
                assert.equal(Object.hasOwn(entity, 'mechanics'), false,
                    `${id} must not contain placeholder or skipped mechanics`);
                if (coverage === 'UNSUPPORTED') {
                    assert.equal(Object.hasOwn(entity, 'references'), false,
                        `${id} must not imply unresolved or unsupported mechanics through references`);
                }
                assert.equal(record.entries.length, roots);
                for (const entry of record.entries) {
                    assert.ok(entry.classifications.length > 0, `${id} entry ${entry.sourceEntryIndex}`);
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(item => item.portion.includes(nestedPath)),
                            `${id} nested path is unaccounted: ${nestedPath}`);
                    }
                }
                for (const path of inventory.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(item =>
                        item.portion.includes(path))), `${id} semantic nested path is unaccounted: ${path}`);
                }
                assertCanonicalReferences(entity);
            }
            const actionOptions = ruleById.get('rule:dmg:action-options');
            assert.deepEqual(actionOptions.references.map(reference => reference.id), [
                'action:dmg:climb-onto-a-bigger-creature', 'action:dmg:disarm', 'action:dmg:mark',
                'action:dmg:overrun', 'action:dmg:shove-aside', 'action:dmg:tumble'
            ]);
            assert.equal(actionOptions.references.every(reference =>
                outputById.has(reference.id) || phase3Ids.has(reference.id)), true);
            const actions = new Map([
                ...phase3Files().map(file => readJson(file)),
                ...entities.map(({ entity }) => entity)
            ].filter(entity => entity.id.startsWith('action:')).map(action => [action.id, action]));
            assert.ok(actions.get('action:dmg:climb-onto-a-bigger-creature').operations
                .some(operation => operation.operationId === 'climb-entry-contest'));
            assert.equal(actions.get('action:dmg:disarm').operations[0].attackDamageOnSuccess, false);
            assert.equal(actions.get('action:dmg:mark').operations[0].reaction.consumed, false);
            assert.equal(actions.get('action:dmg:overrun').operations[1].uses, 'ONCE_THIS_TURN');
            assert.equal(actions.get('action:dmg:shove-aside').operations[2].distance, 5);
            assert.equal(actions.get('action:dmg:tumble').operations[1].permission, 'PASS_THROUGH_SPACE');
            const gapCauses = new Map([
                ['rule:dmg:cleaving-through-creatures', 'OUT_OF_APPROVED_SCOPE'],
                ['rule:dmg:facing', 'OUT_OF_APPROVED_SCOPE'],
                ['rule:dmg:injuries', 'OUT_OF_APPROVED_SCOPE'],
                ['rule:dmg:massive-damage', 'OUT_OF_APPROVED_SCOPE']
            ]);
            for (const [id, cause] of gapCauses) {
                const record = batchRecords.find(item => item.canonicalId === id);
                assert.ok(record.entries.flatMap(entry => entry.classifications).some(item =>
                    item.classification === 'OMITTED' && item.cause === cause));
            }
            const firearmIssue = manifest.sourceFieldIssues.find(issue =>
                issue.canonicalId === 'rule:dmg:firearms' && issue.sourceField === 'reprintedAs');
            assert.equal(firearmIssue?.cause, 'REFERENCE_UNRESOLVED');
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
            assert.equal(new Set(entities.map(({ entity }) => entity.id)).size, entities.length);
        });

        check('RULE-CONV-03 preserves combat, damage, and space mechanics and accounting', () => {
            const expected = [
                ['rule:xge:adamantine-weapons', '$.variantrule[4]', 'PARTIAL', 3, 0],
                ['rule:xge:simultaneous-effects', '$.variantrule[198]', 'UNSUPPORTED', 1, 0],
                ['rule:xphb:bonus-action', '$.variantrule[18]', 'PARTIAL', 1, 0],
                ['rule:xphb:breaking-objects', '$.variantrule[19]', 'PARTIAL', 6, 58],
                ['rule:xphb:damage', '$.variantrule[45]', 'FULL', 1, 0],
                ['rule:xphb:damage-roll', '$.variantrule[46]', 'PARTIAL', 1, 0],
                ['rule:xphb:damage-threshold', '$.variantrule[47]', 'UNSUPPORTED', 1, 0],
                ['rule:xphb:damage-types', '$.variantrule[48]', 'PARTIAL', 2, 32],
                ['rule:xphb:grappling', '$.variantrule[117]', 'PARTIAL', 2, 10],
                ['rule:xphb:hit-points', '$.variantrule[127]', 'FULL', 1, 0],
                ['rule:xphb:immunity', '$.variantrule[133]', 'UNSUPPORTED', 1, 0],
                ['rule:xphb:improvised-weapons', '$.variantrule[134]', 'UNSUPPORTED', 2, 13],
                ['rule:xphb:knocking-out-a-creature', '$.variantrule[142]', 'PARTIAL', 2, 0],
                ['rule:xphb:occupied-space', '$.variantrule[162]', 'PARTIAL', 1, 0],
                ['rule:xphb:reach', '$.variantrule[179]', 'FULL', 1, 0],
                ['rule:xphb:reaction', '$.variantrule[180]', 'PARTIAL', 1, 0],
                ['rule:xphb:simultaneous-effects', '$.variantrule[199]', 'UNSUPPORTED', 1, 0],
                ['rule:xphb:size', '$.variantrule[200]', 'PARTIAL', 1, 0]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            const sourceOrder = expected.slice().sort((left, right) =>
                Number(left[1].match(/\[(\d+)\]/)[1]) - Number(right[1].match(/\[(\d+)\]/)[1]));
            assert.deepEqual(records.map(record => record.canonicalId), sourceOrder.map(([id]) => id));
            assert.equal(new Set(ids).size, 18);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 29);
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, coverage, roots, nestedCount] of expected) {
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                const entity = ruleById.get(id);
                assert.ok(entity, `missing canonical Rule ${id}`);
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(item.nestedClausePathCount, nestedCount);
                assert.equal(item.coverage, coverage);
                assert.equal(item.accountingRecordPresent, true);
                const expectedMetadata = expectedSourceMetadata('rule', sourceRecordForCanonical(entity));
                if (id === 'rule:dmg:downtime-activity-training-to-gain-levels') {
                    delete expectedMetadata.reprintedAs;
                }
                assert.deepEqual(entity.sourceMetadata, expectedMetadata);
                assert.equal(record.entries.length, roots);
                for (const entry of record.entries) {
                    assert.ok(entry.classifications.length > 0);
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(disposition => disposition.portion.includes(nestedPath)));
                    }
                }
                for (const nestedPath of item.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath))), `${id} unaccounted nested path ${nestedPath}`);
                }
                assertCanonicalReferences(entity);
                for (const reference of entity.references ?? []) {
                    const resolved = reference.targetType === 'rule'
                        ? ruleById.has(reference.id) || phase3Ids.has(reference.id)
                        : outputById.has(reference.id) || phase3Ids.has(reference.id);
                    assert.ok(resolved, `${id} has unresolved reference ${reference.id}`);
                }
            }
            assert.deepEqual(expected.reduce((counts, [id, , coverage]) => {
                counts[coverage]++;
                return counts;
            }, { FULL: 0, PARTIAL: 0, UNSUPPORTED: 0 }), { FULL: 3, PARTIAL: 10, UNSUPPORTED: 5 });
            assert.deepEqual(ruleById.get('rule:xphb:hit-points').mechanics.map(item => item.rule),
                ['current', 'maximum', 'zero-hit-points']);
            assert.equal(ruleById.get('rule:xphb:damage-roll').mechanics, undefined);
            assert.equal(ruleById.get('rule:xphb:damage-roll').references[0].id, 'rule:xphb:damage');
            assert.deepEqual(ruleById.get('rule:xphb:reach').mechanics[0].roll,
                { kind: 'ATTACK_ROLL', forms: ['melee'], reachFeet: 5 });
            assert.deepEqual(ruleById.get('rule:xphb:grappling').references.map(item => item.id), [
                'rule:xphb:unarmed-strike', 'condition:xphb:grappled', 'action:xphb:escape-a-grapple',
                'skill:xphb:athletics', 'skill:xphb:acrobatics', 'condition:xphb:incapacitated'
            ]);
            assert.ok(ruleById.get('rule:xphb:breaking-objects').references.some(reference =>
                reference.id === 'rule:xphb:armor-class'
                    && reference.relationship === 'material-armor-class-table-context'));
            assert.deepEqual(ruleById.get('rule:xphb:occupied-space').references.map(reference => reference.id), [
                'rule:xphb:creature', 'rule:xphb:object'
            ]);
            assert.deepEqual(ruleById.get('rule:xphb:unoccupied-space').references.map(reference => reference.id), [
                'rule:xphb:occupied-space', 'rule:xphb:object'
            ]);
            assert.equal(ruleById.get('rule:xphb:breaking-objects').mechanics, undefined);
            assert.equal(ruleById.get('rule:xphb:damage-threshold').mechanics, undefined);
            assert.equal(ruleById.get('rule:xphb:immunity').mechanics, undefined);
            assert.equal(ruleById.get('rule:xphb:improvised-weapons').mechanics, undefined);
            assert.equal(ruleById.get('rule:xphb:simultaneous-effects').mechanics, undefined);
            assert.equal(ruleById.get('rule:xge:simultaneous-effects').mechanics, undefined);
            assert.equal(ruleById.get('rule:xge:simultaneous-effects').sourceMetadata.reprintedAs[0].id,
                'rule:xphb:simultaneous-effects');
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-04 preserves stable, teleportation, temporary HP, and occupancy boundaries', () => {
            const expected = [
                ['rule:xphb:stable', '$.variantrule[212]', 'FULL', 1],
                ['rule:xphb:teleportation', '$.variantrule[220]', 'UNSUPPORTED', 4],
                ['rule:xphb:temporary-hit-points', '$.variantrule[221]', 'FULL', 1],
                ['rule:xphb:unoccupied-space', '$.variantrule[226]', 'PARTIAL', 1]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 7);
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, coverage, rootCount] of expected) {
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                const entity = ruleById.get(id);
                assert.ok(entity, `missing canonical Rule ${id}`);
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, rootCount);
                assert.equal(item.nestedClausePathCount, 0);
                assert.equal(item.coverage, coverage);
                assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
                assert.equal(record.entries.length, rootCount);
                for (const entry of record.entries) {
                    assert.ok(entry.classifications.length > 0);
                    assert.deepEqual(entry.nestedSourcePaths, []);
                }
                assertCanonicalReferences(entity);
                for (const reference of entity.references ?? []) {
                    assert.ok(ruleById.has(reference.id) || phase3Ids.has(reference.id),
                        `${id} has unresolved reference ${reference.id}`);
                }
            }
            assert.deepEqual(ruleById.get('rule:xphb:stable').mechanics[0], {
                componentId: 'stable-death-state',
                kind: 'DAMAGE_HEALTH_STATE',
                subtype: 'DEATH',
                procedure: 'stabilized'
            });
            assert.deepEqual(ruleById.get('rule:xphb:temporary-hit-points').mechanics[0], {
                componentId: 'temporary-hit-points-buffer',
                kind: 'DAMAGE_HEALTH_STATE',
                subtype: 'HIT_POINTS',
                rule: 'temporary-hit-points'
            });
            assert.equal(ruleById.get('rule:xphb:teleportation').mechanics, undefined);
            assert.ok(records.find(record => record.canonicalId === 'rule:xphb:teleportation')
                .entries[0].classifications.some(item => item.classification === 'OMITTED'
                    && item.cause === 'OUT_OF_APPROVED_SCOPE'));
            assert.ok(inventory.get('rule:xphb:teleportation').gapIds.includes('R-GAP-21'));
            assert.equal(ruleById.get('rule:xphb:unoccupied-space').mechanics, undefined);
            assert.ok(records.find(record => record.canonicalId === 'rule:xphb:unoccupied-space')
                .entries[0].classifications.some(item => item.classification === 'OMITTED'
                    && item.cause === 'SCHEMA_UNSUPPORTED'));
            assert.deepEqual(expected.reduce((counts, [, , coverage]) => {
                counts[coverage]++;
                return counts;
            }, { FULL: 0, PARTIAL: 0, UNSUPPORTED: 0 }), { FULL: 2, PARTIAL: 1, UNSUPPORTED: 1 });
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-05 keeps franchise downtime mechanics within skipped R-GAP-13', () => {
            const expected = [
                ['rule:ai:downtime-and-franchise-activity-explore-territory', '$.variantrule[84]', 2, 69],
                ['rule:ai:downtime-and-franchise-activity-franchise-restructuring', '$.variantrule[85]', 2, 44],
                ['rule:ai:downtime-and-franchise-activity-headquarters-modification', '$.variantrule[86]', 3, 44],
                ['rule:ai:downtime-and-franchise-activity-marketeering', '$.variantrule[87]', 2, 54],
                ['rule:ai:downtime-and-franchise-activity-philanthropic-enterprise', '$.variantrule[88]', 3, 48],
                ['rule:ai:downtime-and-franchise-activity-running-a-franchise', '$.variantrule[89]', 2, 64]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 14);
            assert.equal(expected.reduce((sum, [, , , nestedCount]) => sum + nestedCount, 0), 323);
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, roots, nestedCount] of expected) {
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                const entity = ruleById.get(id);
                assert.ok(entity, `missing referenceable Rule ${id}`);
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(item.nestedClausePathCount, nestedCount);
                assert.equal(item.coverage, 'UNSUPPORTED');
                assert.deepEqual(item.gapIds, ['R-GAP-13']);
                assert.equal(item.accountingRecordPresent, true);
                assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
                assert.equal(entity.mechanics, undefined);
                assert.equal(entity.references, undefined);
                assert.equal(record.conversionStatus, 'unsupported');
                assert.equal(record.entries.length, roots);
                for (const entry of record.entries) {
                    assert.equal(entry.classifications.length, 1);
                    const disposition = entry.classifications[0];
                    assert.equal(disposition.classification, 'OMITTED');
                    assert.ok(['NON_MECHANICAL', 'OUT_OF_APPROVED_SCOPE'].includes(disposition.cause));
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(disposition.portion.includes(nestedPath));
                    }
                }
                for (const nestedPath of item.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath))), `${id} unaccounted path ${nestedPath}`);
                }
            }
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-06 keeps four additional franchise activities within skipped R-GAP-13', () => {
            const expected = [
                ['rule:ai:downtime-and-franchise-activity-schmoozing', '$.variantrule[90]', 2, 46],
                ['rule:ai:downtime-and-franchise-activity-scrutineering', '$.variantrule[91]', 2, 44],
                ['rule:ai:downtime-and-franchise-activity-shady-business-practice', '$.variantrule[92]', 2, 53],
                ['rule:ai:downtime-and-franchise-activity-team-building', '$.variantrule[93]', 3, 54]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 9);
            assert.equal(expected.reduce((sum, [, , , nestedCount]) => sum + nestedCount, 0), 197);
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, roots, nestedCount] of expected) {
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                const entity = ruleById.get(id);
                assert.ok(entity, `missing referenceable Rule ${id}`);
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(item.nestedClausePathCount, nestedCount);
                assert.equal(item.coverage, 'UNSUPPORTED');
                assert.deepEqual(item.gapIds, ['R-GAP-13']);
                assert.equal(item.accountingRecordPresent, true);
                assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
                assert.equal(entity.mechanics, undefined);
                assert.equal(entity.references, undefined);
                assert.equal(record.conversionStatus, 'unsupported');
                assert.equal(record.entries.length, roots);
                for (const entry of record.entries) {
                    assert.equal(entry.classifications.length, 1);
                    const disposition = entry.classifications[0];
                    assert.equal(disposition.classification, 'OMITTED');
                    assert.ok(['NON_MECHANICAL', 'OUT_OF_APPROVED_SCOPE'].includes(disposition.cause));
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(disposition.portion.includes(nestedPath));
                    }
                }
                for (const nestedPath of item.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath))), `${id} unaccounted path ${nestedPath}`);
                }
            }
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-07 preserves maritime references and defers vessel procedures', () => {
            const expected = [
                ['rule:aag:crashing', '$.variantrule[33]', 4, 12],
                ['rule:aag:crew', '$.variantrule[37]', 2, 0],
                ['rule:gos:officers-and-crew', '$.variantrule[164]', 3, 27],
                ['rule:aag:ship-repairs', '$.variantrule[191]', 2, 0],
                ['rule:gos:ship-stat-blocks', '$.variantrule[192]', 5, 60],
                ['rule:aag:ship-to-ship-combat', '$.variantrule[193]', 5, 29]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 21);
            assert.equal(expected.reduce((sum, [, , , nestedCount]) => sum + nestedCount, 0), 128);
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, roots, nestedCount] of expected) {
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                const entity = ruleById.get(id);
                assert.ok(entity, `missing referenceable Rule ${id}`);
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(item.nestedClausePathCount, nestedCount);
                assert.equal(item.coverage, 'UNSUPPORTED');
                assert.ok(item.gapIds.includes('R-GAP-31'));
                assert.equal(item.accountingRecordPresent, true);
                assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
                assert.equal(entity.mechanics, undefined);
                assert.equal(record.conversionStatus, 'unsupported');
                assert.equal(record.entries.length, roots);
                for (const entry of record.entries) {
                    assert.equal(entry.classifications.length, 1);
                    const disposition = entry.classifications[0];
                    assert.equal(disposition.classification, 'OMITTED');
                    assert.ok(disposition.cause);
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(disposition.portion.includes(nestedPath));
                    }
                }
                for (const nestedPath of item.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath))), `${id} unaccounted path ${nestedPath}`);
                }
            }
            assert.deepEqual(ruleById.get('rule:aag:ship-to-ship-combat').references, [
                {
                    targetType: 'rule',
                    id: 'rule:dmg:initiative-variants',
                    relationship: 'source-referenced-side-initiative-variant'
                },
                {
                    targetType: 'action',
                    id: 'action:phb:ready',
                    relationship: 'source-referenced-ready-action-for-boarding'
                }
            ]);
            assert.deepEqual(inventory.get('rule:gos:officers-and-crew').gapIds, ['R-GAP-31', 'R-GAP-44']);
            assert.ok(records.find(record => record.canonicalId === 'rule:aag:ship-repairs')
                .entries[1].classifications[0].cause === 'REFERENCE_UNRESOLVED');
            assert.ok(records.find(record => record.canonicalId === 'rule:gos:ship-stat-blocks')
                .entries[4].classifications[0].cause === 'REFERENCE_UNRESOLVED');
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-08 accounts for ship combat and unavailable vehicle upgrades', () => {
            const expected = [
                ['rule:gos:ships-in-combat', '$.variantrule[194]', 5, 41],
                ['rule:gos:superior-ship-upgrades', '$.variantrule[214]', 7, 42]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 12);
            assert.equal(expected.reduce((sum, [, , , nestedCount]) => sum + nestedCount, 0), 83);
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, roots, nestedCount] of expected) {
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                const entity = ruleById.get(id);
                assert.ok(entity, `missing referenceable Rule ${id}`);
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(item.nestedClausePathCount, nestedCount);
                assert.equal(item.coverage, 'UNSUPPORTED');
                assert.deepEqual(item.gapIds, ['R-GAP-31']);
                assert.equal(item.accountingRecordPresent, true);
                assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
                assert.equal(entity.mechanics, undefined);
                assert.equal(entity.references, undefined);
                assert.equal(record.conversionStatus, 'unsupported');
                assert.equal(record.entries.length, roots);
                for (const entry of record.entries) {
                    assert.equal(entry.classifications.length, 1);
                    const disposition = entry.classifications[0];
                    assert.equal(disposition.classification, 'OMITTED');
                    assert.ok(['NON_MECHANICAL', 'OUT_OF_APPROVED_SCOPE', 'REFERENCE_UNRESOLVED']
                        .includes(disposition.cause));
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(disposition.portion.includes(nestedPath));
                    }
                }
                for (const nestedPath of item.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath))), `${id} unaccounted path ${nestedPath}`);
                }
            }
            assert.ok(records[1].entries.slice(2).every(entry =>
                entry.classifications[0].cause === 'REFERENCE_UNRESOLVED'));
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-09 preserves the sea encounter identity and accounts for its random tables', () => {
            const id = 'rule:gos:encounters-at-sea';
            const record = manifest.records.find(item => item.canonicalId === id);
            const item = manifest.ruleCatalogInventory.find(entry => entry.canonicalId === id);
            const entity = entities.find(({ entity: candidate }) => candidate.id === id)?.entity;
            assert.ok(entity, 'missing referenceable Rule');
            assert.equal(record.sourceObjectPath, '$.variantrule[98]');
            assert.equal(record.sourceEntryCount, 2);
            assert.equal(item.nestedClausePathCount, 162);
            assert.equal(item.coverage, 'UNSUPPORTED');
            assert.deepEqual(item.gapIds, ['R-GAP-31', 'R-GAP-28']);
            assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
            assert.equal(entity.mechanics, undefined);
            assert.equal(entity.references, undefined);
            assert.deepEqual(record.entries.map(entry => entry.classifications[0].cause), [
                'NON_MECHANICAL', 'SCHEMA_UNSUPPORTED'
            ]);
            for (const nestedPath of item.nestedClausePaths) {
                assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                    disposition.portion.includes(nestedPath))), `unaccounted nested path ${nestedPath}`);
            }
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-10 keeps island generation and conditional outcome tables reference-only', () => {
            const id = 'rule:gos:mysterious-islands';
            const record = manifest.records.find(item => item.canonicalId === id);
            const item = manifest.ruleCatalogInventory.find(entry => entry.canonicalId === id);
            const entity = entities.find(({ entity: candidate }) => candidate.id === id)?.entity;
            assert.ok(entity, 'missing referenceable Rule');
            assert.equal(record.sourceObjectPath, '$.variantrule[158]');
            assert.equal(record.sourceEntryCount, 4);
            assert.equal(item.nestedClausePathCount, 417);
            assert.equal(item.coverage, 'UNSUPPORTED');
            assert.deepEqual(item.gapIds, ['R-GAP-28']);
            assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
            assert.equal(entity.mechanics, undefined);
            assert.equal(entity.references, undefined);
            assert.deepEqual(record.entries.map(entry => entry.classifications[0].cause), [
                'NON_MECHANICAL', 'SCHEMA_UNSUPPORTED', 'NON_MECHANICAL', 'SCHEMA_UNSUPPORTED'
            ]);
            for (const nestedPath of item.nestedClausePaths) {
                assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                    disposition.portion.includes(nestedPath))), `unaccounted nested path ${nestedPath}`);
            }
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-11 represents the supported kelp terrain rule and accounts for marine hazards', () => {
            const id = 'rule:gos:ocean-environs';
            const record = manifest.records.find(item => item.canonicalId === id);
            const item = manifest.ruleCatalogInventory.find(entry => entry.canonicalId === id);
            const entity = entities.find(({ entity: candidate }) => candidate.id === id)?.entity;
            assert.ok(entity, 'missing referenceable Rule');
            assert.equal(record.sourceObjectPath, '$.variantrule[163]');
            assert.equal(record.sourceEntryCount, 14);
            assert.equal(item.nestedClausePathCount, 349);
            assert.equal(item.coverage, 'PARTIAL');
            assert.deepEqual(item.gapIds, ['R-GAP-31']);
            assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
            assert.deepEqual(entity.mechanics, [{
                componentId: 'kelp-forest-difficult-terrain',
                kind: 'MOVEMENT_SPATIAL',
                subtype: 'DIFFICULT_TERRAIN',
                procedure: 'movement-cost',
                unit: 'feet',
                extraFeetPerFoot: 1,
                cumulative: true
            }]);
            assert.equal(entity.references, undefined);
            assert.equal(record.conversionStatus, 'partially-represented');
            assert.equal(record.entries.length, 14);
            assert.ok(record.entries[6].classifications.some(disposition =>
                disposition.classification === 'INTERPRETED'
                    && disposition.portion.includes('dense kelp forest is difficult terrain')));
            assert.ok(record.entries[6].classifications.some(disposition =>
                disposition.classification === 'OMITTED' && disposition.cause === 'SCHEMA_UNSUPPORTED'
                    && disposition.portion.includes('heavily obscures vision beyond 10 feet')));
            for (const nestedPath of item.nestedClausePaths) {
                assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                    disposition.portion.includes(nestedPath))), `unaccounted nested path ${nestedPath}`);
            }
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-12 represents supported ghost-ship resistances and accounts for ship generation tables', () => {
            const id = 'rule:gos:random-ships';
            const record = manifest.records.find(item => item.canonicalId === id);
            const item = manifest.ruleCatalogInventory.find(entry => entry.canonicalId === id);
            const entity = entities.find(({ entity: candidate }) => candidate.id === id)?.entity;
            assert.ok(entity, 'missing referenceable Rule');
            assert.equal(record.sourceObjectPath, '$.variantrule[178]');
            assert.equal(record.sourceEntryCount, 6);
            assert.equal(item.nestedClausePathCount, 353);
            assert.equal(item.coverage, 'PARTIAL');
            assert.deepEqual(item.gapIds, ['R-GAP-31', 'R-GAP-28']);
            assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
            assert.deepEqual(entity.mechanics, [{
                componentId: 'ghost-ship-energy-damage-resistance',
                kind: 'DAMAGE_HEALTH_STATE',
                subtype: 'RESISTANCE',
                damageTypes: ['acid', 'fire', 'lightning', 'thunder'],
                resolutionModifier: {
                    operation: 'HALVE',
                    multiplier: 0.5,
                    rounding: 'DOWN',
                    oncePerDamageInstance: true,
                    sameOperationDoesNotStack: true
                }
            }]);
            assert.equal(entity.references, undefined);
            assert.equal(record.conversionStatus, 'partially-represented');
            assert.ok(record.entries[4].classifications.some(disposition =>
                disposition.classification === 'INTERPRETED'
                    && disposition.portion.includes('Ghost ships resist acid, fire, lightning, and thunder')));
            assert.ok(record.entries[4].classifications.some(disposition =>
                disposition.classification === 'OMITTED' && disposition.cause === 'SCHEMA_UNSUPPORTED'));
            for (const nestedPath of item.nestedClausePaths) {
                assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                    disposition.portion.includes(nestedPath))), `unaccounted nested path ${nestedPath}`);
            }
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-13 accounts for the deferred ship travel, crew, and hazard procedures', () => {
            const id = 'rule:gos:travel-at-sea';
            const record = manifest.records.find(item => item.canonicalId === id);
            const item = manifest.ruleCatalogInventory.find(entry => entry.canonicalId === id);
            const entity = entities.find(({ entity: candidate }) => candidate.id === id)?.entity;
            assert.ok(entity, 'missing referenceable Rule');
            assert.equal(record.sourceObjectPath, '$.variantrule[223]');
            assert.equal(record.sourceEntryCount, 4);
            assert.equal(item.nestedClausePathCount, 307);
            assert.equal(item.coverage, 'UNSUPPORTED');
            assert.deepEqual(item.gapIds, ['R-GAP-31', 'R-GAP-28', 'R-GAP-44']);
            assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
            assert.equal(entity.mechanics, undefined);
            assert.equal(entity.references, undefined);
            assert.deepEqual(record.entries[0].classifications.map(disposition => disposition.cause), [
                'NON_MECHANICAL', 'REFERENCE_UNRESOLVED'
            ]);
            assert.equal(record.entries.slice(1).every(entry =>
                entry.classifications[0].cause === 'OUT_OF_APPROVED_SCOPE'), true);
            for (const nestedPath of item.nestedClausePaths) {
                assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                    disposition.portion.includes(nestedPath))), `unaccounted nested path ${nestedPath}`);
            }
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-15 preserves illumination and obscuration definitions with supported spatial mechanics', () => {
            const expected = [
                ['rule:xphb:bright-light', '$.variantrule[20]', 'UNSUPPORTED', 1],
                ['rule:xphb:darkness', '$.variantrule[49]', 'FULL', 1],
                ['rule:xphb:dim-light', '$.variantrule[55]', 'FULL', 1],
                ['rule:xphb:heavily-obscured', '$.variantrule[122]', 'PARTIAL', 1],
                ['rule:xphb:lightly-obscured', '$.variantrule[144]', 'PARTIAL', 1]
            ];
            const ids = expected.map(([id]) => id);
            const byId = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, coverage, roots] of expected) {
                const entity = byId.get(id);
                const record = manifest.records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                assert.ok(entity, `missing Rule ${id}`);
                assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(item.coverage, coverage);
                assert.equal(record.entries[0].sourcePath, `${sourcePath}.entries[0]`);
            }
            assert.equal(byId.get('rule:xphb:bright-light').mechanics, undefined);
            assert.equal(byId.get('rule:xphb:darkness').references[0].id, 'rule:xphb:heavily-obscured');
            assert.equal(byId.get('rule:xphb:dim-light').references[0].id, 'rule:xphb:lightly-obscured');
            assert.deepEqual(byId.get('rule:xphb:heavily-obscured').mechanics[0], {
                componentId: 'heavy-obscuration', kind: 'MOVEMENT_SPATIAL', subtype: 'OBSCURATION',
                level: 'heavily-obscured', sightEffect: 'blocks-vision'
            });
            assert.equal(byId.get('rule:xphb:heavily-obscured').references[0].id, 'condition:xphb:blinded');
            assert.deepEqual(byId.get('rule:xphb:lightly-obscured').mechanics[0], {
                componentId: 'light-obscuration', kind: 'MOVEMENT_SPATIAL', subtype: 'OBSCURATION',
                level: 'lightly-obscured', sightEffect: 'disadvantage-on-sight-dependent-perception'
            });
            for (const id of ['rule:xphb:heavily-obscured', 'rule:xphb:lightly-obscured']) {
                const record = manifest.records.find(item => item.canonicalId === id);
                assert.ok(record.entries[0].classifications.some(item =>
                    item.classification === 'OMITTED' && item.cause === 'SCHEMA_UNSUPPORTED'));
            }
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-16 preserves supported checks, saves, definitions, and edition-safe references', () => {
            const expected = [
                ['rule:cos:prophet-s-gambit', '$.variantrule[177]', 'UNSUPPORTED', 5, 92],
                ['rule:dmg:alien-technology', '$.variantrule[7]', 'PARTIAL', 4, 14],
                ['rule:dmg:fear-and-horror', '$.variantrule[109]', 'PARTIAL', 3, 7],
                ['rule:dmg:hero-points', '$.variantrule[123]', 'UNSUPPORTED', 4, 0],
                ['rule:phb:equipment-sizes', '$.variantrule[101]', 'UNSUPPORTED', 3, 0],
                ['rule:scag:human-languages', '$.variantrule[131]', 'PARTIAL', 3, 59],
                ['rule:vrgr:fear-and-stress', '$.variantrule[110]', 'PARTIAL', 4, 65],
                ['rule:xphb:attunement', '$.variantrule[16]', 'PARTIAL', 1, 0],
                ['rule:xphb:challenge-rating', '$.variantrule[25]', 'UNSUPPORTED', 1, 0],
                ['rule:xphb:condition', '$.variantrule[30]', 'PARTIAL', 3, 17],
                ['rule:xphb:dead', '$.variantrule[50]', 'PARTIAL', 2, 0],
                ['rule:xphb:healing', '$.variantrule[121]', 'FULL', 1, 0]
            ];
            assert.equal(expected.reduce((sum, [, , , roots]) => sum + roots, 0), 34);
            assert.equal(expected.reduce((sum, [, , , , nested]) => sum + nested, 0), 254);
            const ids = expected.map(([id]) => id);
            const byId = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, coverage, roots, nested] of expected) {
                const record = manifest.records.find(item => item.canonicalId === id);
                assert.ok(byId.has(id), `missing Rule ${id}`);
                const expectedMetadata = expectedSourceMetadata('rule', sourceRecordForCanonical(byId.get(id)));
                if (id === 'rule:dmg:alien-technology') delete expectedMetadata.reprintedAs;
                assert.deepEqual(byId.get(id).sourceMetadata, expectedMetadata);
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(inventory.get(id).nestedClausePathCount, nested);
                assert.equal(inventory.get(id).coverage, coverage);
                assert.equal(record.entries.length, roots);
                for (const entry of record.entries) {
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(item => item.portion.includes(nestedPath)),
                            `${id} nested path missing from disposition ${nestedPath}`);
                    }
                }
            }
            assert.equal(byId.get('rule:dmg:alien-technology').mechanics[0].abilities[0], 'intelligence');
            assert.deepEqual(byId.get('rule:dmg:fear-and-horror').mechanics.map(item =>
                item.savingThrow.abilities[0]), ['wisdom', 'charisma']);
            assert.equal(byId.get('rule:vrgr:fear-and-stress').mechanics[0].savingThrow.dc.value, 15);
            assert.equal(byId.get('rule:xphb:attunement').mechanics[0].subtype, 'ATTUNEMENT');
            assert.equal(byId.get('rule:xphb:healing').mechanics[0].method, 'healing-effect');
            assert.equal(byId.get('rule:xphb:condition').references.length, 15);
            assert.equal(byId.get('rule:scag:human-languages').references.length, 21);
            for (const id of ['rule:cos:prophet-s-gambit', 'rule:dmg:hero-points',
                'rule:phb:equipment-sizes', 'rule:xphb:challenge-rating']) {
                assert.equal(byId.get(id).mechanics, undefined);
            }
            assert.deepEqual(inventory.get('rule:cos:prophet-s-gambit').gapIds, ['R-GAP-22']);
            assert.deepEqual(inventory.get('rule:dmg:hero-points').gapIds, ['R-GAP-46']);
            assert.deepEqual(inventory.get('rule:vrgr:fear-and-stress').gapIds, ['R-GAP-16']);
            const dead = manifest.records.find(item => item.canonicalId === 'rule:xphb:dead');
            assert.ok(dead.entries[0].classifications.some(item => item.cause === 'REFERENCE_UNRESOLVED'));
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-17 preserves the supported glossary relationships and Hit Dice healing', () => {
            const expected = [
                ['rule:xphb:hit-point-dice', '$.variantrule[126]', 'PARTIAL', 1, 0],
                ['rule:xphb:object', '$.variantrule[161]', 'UNSUPPORTED', 1, 0],
                ['rule:xphb:per-day', '$.variantrule[167]', 'FULL', 1, 0],
                ['rule:xphb:round-down', '$.variantrule[185]', 'UNSUPPORTED', 1, 0],
                ['rule:xphb:stat-block', '$.variantrule[213]', 'PARTIAL', 2, 61],
                ['rule:xphb:weapon', '$.variantrule[229]', 'PARTIAL', 1, 0]
            ];
            assert.equal(expected.reduce((sum, [, , , roots]) => sum + roots, 0), 7);
            assert.equal(expected.reduce((sum, [, , , , nested]) => sum + nested, 0), 61);
            const byId = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, coverage, roots, nested] of expected) {
                const entity = byId.get(id);
                const record = manifest.records.find(item => item.canonicalId === id);
                assert.ok(entity, `missing Rule ${id}`);
                assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(inventory.get(id).nestedClausePathCount, nested);
                assert.equal(inventory.get(id).coverage, coverage);
                for (const entry of record.entries) {
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(item => item.portion.includes(nestedPath)));
                    }
                }
            }
            assert.equal(byId.get('rule:xphb:hit-point-dice').mechanics[0].method, 'hit-dice');
            assert.deepEqual(byId.get('rule:xphb:per-day').references, [
                {targetType: 'rule', id: 'rule:xphb:long-rest', relationship: 'per-day-use-recharged-after-long-rest'}
            ]);
            assert.equal(byId.get('rule:xphb:stat-block').references.length, 22);
            assert.deepEqual(byId.get('rule:xphb:weapon').references, [
                {targetType: 'rule', id: 'rule:xphb:object', relationship: 'weapon-is-an-object'}
            ]);
            assert.equal(byId.get('rule:xphb:object').mechanics, undefined);
            assert.equal(byId.get('rule:xphb:round-down').mechanics, undefined);
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-18 preserves character-progression identities, typed relationships, and skipped knot rules', () => {
            const expected = [
                ['rule:dmg:alternatives-to-epic-boons', '$.variantrule[10]', 'PARTIAL', 2, 7],
                ['rule:dmg:level-advancement-without-xp', '$.variantrule[143]', 'PARTIAL', 4, 6],
                ['rule:dmg:milestones', '$.variantrule[152]', 'PARTIAL', 4, 8],
                ['rule:dmg:plot-points', '$.variantrule[173]', 'UNSUPPORTED', 5, 18],
                ['rule:dmg:renown', '$.variantrule[181]', 'PARTIAL', 6, 76],
                ['rule:phb:customizing-ability-scores', '$.variantrule[41]', 'UNSUPPORTED', 5, 13],
                ['rule:phb:feats', '$.variantrule[111]', 'UNSUPPORTED', 4, 0],
                ['rule:tce:customizing-your-origin', '$.variantrule[42]', 'PARTIAL', 5, 60],
                ['rule:tce:optional-class-features', '$.variantrule[165]', 'UNSUPPORTED', 3, 0],
                ['rule:xge:tying-knots', '$.variantrule[224]', 'UNSUPPORTED', 3, 0],
                ['rule:xphb:experience-points', '$.variantrule[102]', 'UNSUPPORTED', 1, 0]
            ];
            assert.equal(expected.reduce((sum, [, , , roots]) => sum + roots, 0), 42);
            assert.equal(expected.reduce((sum, [, , , , nested]) => sum + nested, 0), 188);
            const byId = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, coverage, roots, nested] of expected) {
                const entity = byId.get(id);
                const record = manifest.records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                assert.ok(entity, `missing Rule ${id}`);
                const expectedMetadata = expectedSourceMetadata('rule', sourceRecordForCanonical(entity));
                if (id === 'rule:dmg:level-advancement-without-xp' || id === 'rule:dmg:renown') {
                    delete expectedMetadata.reprintedAs;
                }
                assert.deepEqual(entity.sourceMetadata, expectedMetadata);
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(item.nestedClausePathCount, nested);
                assert.equal(item.coverage, coverage);
                assert.equal(entity.mechanics, undefined);
                for (const entry of record.entries) {
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(disposition => disposition.portion.includes(nestedPath)));
                    }
                }
            }
            assert.equal(byId.get('rule:dmg:alternatives-to-epic-boons').references.length, 2);
            assert.equal(byId.get('rule:dmg:milestones').references.length, 4);
            assert.equal(byId.get('rule:dmg:renown').references.length, 3);
            assert.equal(byId.get('rule:tce:customizing-your-origin').references.length, 16);
            assert.equal(byId.get('rule:xge:tying-knots').references[0].id,
                'rule:phb:skills-with-different-abilities');
            assert.deepEqual(inventory.get('rule:xge:tying-knots').gapIds, ['R-GAP-19']);
            for (const id of ['rule:dmg:level-advancement-without-xp', 'rule:dmg:renown']) {
                const issue = manifest.sourceFieldIssues.find(item =>
                    item.canonicalId === id && item.sourceField === 'reprintedAs');
                assert.equal(issue?.cause, 'REFERENCE_UNRESOLVED');
            }
            assert.ok(manifest.records.find(item => item.canonicalId === 'rule:xphb:experience-points')
                .entries[0].classifications.some(item => item.cause === 'EXTERNAL_DEPENDENCY'));
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-29 preserves the downtime activity index and accounts generic procedure limits', () => {
            const id = 'rule:xge:downtime-revisited';
            const expectedTargets = [
                'rule:xge:downtime-activity-buying-a-magic-item',
                'rule:xge:downtime-activity-carousing',
                'rule:xge:downtime-activity-crafting-an-item',
                'rule:xge:downtime-activity-crime',
                'rule:xge:downtime-activity-gambling',
                'rule:xge:downtime-activity-pit-fighting',
                'rule:xge:downtime-activity-relaxation',
                'rule:xge:downtime-activity-religious-service',
                'rule:xge:downtime-activity-research',
                'rule:xge:downtime-activity-scribing-a-spell-scroll',
                'rule:xge:downtime-activity-selling-a-magic-item',
                'rule:xge:downtime-activity-training',
                'rule:xge:downtime-activity-work'
            ];
            const entity = entities.find(({ entity: candidate }) => candidate.id === id)?.entity;
            const record = manifest.records.find(item => item.canonicalId === id);
            const inventory = manifest.ruleCatalogInventory.find(item => item.canonicalId === id);
            assert.ok(entity, `missing Rule ${id}`);
            assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
            assert.equal(entity.ruleType, 'VARIANT_OPTIONAL');
            assert.equal(entity.mechanics, undefined);
            assert.deepEqual(entity.references.map(reference => reference.id), expectedTargets);
            assert.ok(entity.references.every(reference =>
                reference.relationship === 'listed-as-example-downtime-activity'));
            assert.equal(record.sourceObjectPath, '$.variantrule[94]');
            assert.equal(record.sourceEntryCount, 7);
            assert.equal(record.entries.length, 7);
            assert.equal(inventory.nestedClausePathCount, 99);
            assert.deepEqual(inventory.gapIds, ['R-GAP-13']);
            assert.equal(inventory.coverage, 'PARTIAL');
            for (const entry of record.entries) {
                for (const nestedPath of entry.nestedSourcePaths) {
                    assert.ok(entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath)), `unaccounted path ${nestedPath}`);
                }
            }
            for (const nestedPath of inventory.nestedClausePaths) {
                assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                    disposition.portion.includes(nestedPath))), `unaccounted nested path ${nestedPath}`);
            }
            const externalBooks = record.entries[0].classifications.find(item =>
                item.classification === 'OMITTED' && item.cause === 'EXTERNAL_DEPENDENCY');
            assert.ok(externalBooks?.portion.includes('PHB/DMG'));
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.ruleCatalogSummary.partialCount, 90);
            assert.equal(manifest.ruleCatalogSummary.unsupportedCount, 73);
            assert.equal(manifest.canonicalRecordCount, 551);
            assert.equal(new Set(entities.map(({ entity }) => entity.id)).size, entities.length);
        });

        check('RULE-CONV-28 preserves XGE research, crafting, sale, and work portions', () => {
            const expected = [
                ['rule:xge:downtime-activity-religious-service', '$.variantrule[72]', 'UNSUPPORTED', 4, 42],
                ['rule:xge:downtime-activity-research', '$.variantrule[73]', 'PARTIAL', 4, 45],
                ['rule:xge:downtime-activity-scribing-a-spell-scroll', '$.variantrule[76]', 'PARTIAL', 3, 64],
                ['rule:xge:downtime-activity-selling-a-magic-item', '$.variantrule[77]', 'PARTIAL', 4, 57],
                ['rule:xge:downtime-activity-training', '$.variantrule[81]', 'UNSUPPORTED', 3, 25],
                ['rule:xge:downtime-activity-work', '$.variantrule[83]', 'PARTIAL', 4, 42]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 22);
            assert.equal(expected.reduce((sum, [, , , , nested]) => sum + nested, 0), 275);
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, coverage, roots, nested] of expected) {
                const entity = ruleById.get(id);
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                assert.ok(entity, `missing Rule ${id}`);
                assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
                assert.equal(entity.ruleType, 'VARIANT_OPTIONAL');
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(record.entries.length, roots);
                assert.equal(item.nestedClausePathCount, nested);
                assert.deepEqual(item.gapIds, ['R-GAP-13']);
                assert.equal(item.coverage, coverage);
                for (const entry of record.entries) {
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(disposition =>
                            disposition.portion.includes(nestedPath)), `${id} unaccounted path ${nestedPath}`);
                    }
                }
                for (const nestedPath of item.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath))), `${id} unaccounted nested clause ${nestedPath}`);
                }
                if (coverage === 'UNSUPPORTED') {
                    assert.equal(entity.mechanics, undefined, `${id} must remain reference-only`);
                    assert.equal(entity.references, undefined);
                }
            }
            const research = ruleById.get('rule:xge:downtime-activity-research').mechanics[0];
            assert.deepEqual([research.procedure, research.duration, research.check.abilities, research.outcome], [
                'research', { amount: 1, unit: 'week' }, ['intelligence'], 'information-gained'
            ]);
            const scribing = ruleById.get('rule:xge:downtime-activity-scribing-a-spell-scroll').mechanics[0];
            assert.deepEqual([scribing.procedure, scribing.outcome], ['craft', 'item-created']);
            const sale = ruleById.get('rule:xge:downtime-activity-selling-a-magic-item').mechanics[0];
            assert.deepEqual([sale.procedure, sale.duration, sale.check.abilities, sale.check.skill.id], [
                'sell', { amount: 1, unit: 'week' }, ['charisma'], 'skill:phb:persuasion'
            ]);
            assert.deepEqual([ruleById.get('rule:xge:downtime-activity-work').mechanics[0].procedure,
                ruleById.get('rule:xge:downtime-activity-work').mechanics[0].duration],
                ['work', { amount: 1, unit: 'week' }]);
            assert.equal(research.check.advantage, undefined);
            assert.equal(scribing.duration, undefined,
                'spell-level-dependent scribing durations must not be flattened');
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.ruleCatalogSummary.partialCount, 90);
            assert.equal(manifest.ruleCatalogSummary.unsupportedCount, 73);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-27 represents supported XGE downtime activity portions without table engines', () => {
            const expected = [
                ['rule:xge:downtime-activity-buying-a-magic-item', '$.variantrule[58]', 'PARTIAL', 6, 85],
                ['rule:xge:downtime-activity-carousing', '$.variantrule[60]', 'PARTIAL', 4, 105],
                ['rule:xge:downtime-activity-crafting-an-item', '$.variantrule[63]', 'PARTIAL', 5, 115],
                ['rule:xge:downtime-activity-crime', '$.variantrule[64]', 'UNSUPPORTED', 4, 52],
                ['rule:xge:downtime-activity-gambling', '$.variantrule[66]', 'PARTIAL', 4, 43],
                ['rule:xge:downtime-activity-pit-fighting', '$.variantrule[68]', 'UNSUPPORTED', 4, 43]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 27);
            assert.equal(expected.reduce((sum, [, , , , nested]) => sum + nested, 0), 443);
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, coverage, roots, nested] of expected) {
                const entity = ruleById.get(id);
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                assert.ok(entity, `missing Rule ${id}`);
                const expectedMetadata = expectedSourceMetadata('rule', sourceRecordForCanonical(entity));
                assert.deepEqual(entity.sourceMetadata, expectedMetadata);
                assert.equal(entity.ruleType, 'VARIANT_OPTIONAL');
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(record.entries.length, roots);
                assert.equal(item.nestedClausePathCount, nested);
                assert.deepEqual(item.gapIds, ['R-GAP-13']);
                assert.equal(item.coverage, coverage);
                for (const entry of record.entries) {
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(disposition =>
                            disposition.portion.includes(nestedPath)), `${id} unaccounted path ${nestedPath}`);
                    }
                }
                for (const nestedPath of item.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath))), `${id} unaccounted nested path ${nestedPath}`);
                }
                if (coverage === 'UNSUPPORTED') {
                    assert.equal(entity.mechanics, undefined, `${id} must remain reference-only`);
                    assert.equal(entity.references, undefined);
                }
            }
            const buying = ruleById.get('rule:xge:downtime-activity-buying-a-magic-item').mechanics[0];
            assert.deepEqual([buying.procedure, buying.duration], ['buy', { amount: 1, unit: 'week' }]);
            assert.deepEqual([buying.check.abilities, buying.check.skill.id],
                [['charisma'], 'skill:phb:persuasion']);
            const carousing = ruleById.get('rule:xge:downtime-activity-carousing').mechanics[0];
            assert.deepEqual([carousing.procedure, carousing.duration], ['carouse', { amount: 1, unit: 'week' }]);
            assert.equal(carousing.check.skill.id, 'skill:phb:persuasion');
            assert.deepEqual(ruleById.get('rule:xge:downtime-activity-crafting-an-item').mechanics[0], {
                componentId: 'craft-item-activity',
                kind: 'DOWNTIME_PROCEDURE',
                subtype: 'ACTIVITY',
                procedure: 'craft',
                outcome: 'item-created'
            });
            assert.deepEqual([ruleById.get('rule:xge:downtime-activity-gambling').mechanics[0].procedure,
                ruleById.get('rule:xge:downtime-activity-gambling').mechanics[0].duration],
                ['gamble', { amount: 1, unit: 'week' }]);
            assert.equal(JSON.stringify(buying).includes('Magic Item Table'), false);
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.ruleCatalogSummary.partialCount, 90);
            assert.equal(manifest.ruleCatalogSummary.unsupportedCount, 73);
            assert.equal(manifest.canonicalRecordCount, 551);
            assert.equal(new Set(entities.map(({ entity }) => entity.id)).size, entities.length);
        });

        check('RULE-CONV-26 represents independent downtime procedures and accounts deferred detail', () => {
            const expected = [
                ['rule:phb:downtime-activity-researching', '$.variantrule[74]', 3],
                ['rule:phb:downtime-activity-training', '$.variantrule[80]', 3]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 6);
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, roots] of expected) {
                const entity = ruleById.get(id);
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                assert.ok(entity, `missing Rule ${id}`);
                assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(record.entries.length, roots);
                assert.equal(item.nestedClausePathCount, 0);
                assert.deepEqual(item.gapIds, ['R-GAP-13']);
                assert.equal(item.coverage, 'PARTIAL');
                assert.ok(record.entries.every(entry => entry.classifications.length > 0));
            }
            const research = ruleById.get('rule:phb:downtime-activity-researching').mechanics[0];
            assert.deepEqual([research.procedure, research.outcome], ['research', 'information-gained']);
            assert.equal(research.duration, undefined);
            assert.equal(research.check, undefined);
            const training = ruleById.get('rule:phb:downtime-activity-training').mechanics[0];
            assert.deepEqual([training.procedure, training.duration], ['train', { amount: 250, unit: 'day' }]);
            assert.equal(training.outcome, undefined,
                'language/tool proficiency alternatives must not become a generic resource outcome');
            assert.equal(records.flatMap(record => record.entries)
                .flatMap(entry => entry.classifications).filter(item => item.classification === 'INTERPRETED').length, 2);
            assert.equal(records.flatMap(record => record.entries)
                .flatMap(entry => entry.classifications).filter(item => item.classification === 'OMITTED').length, 6);
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.ruleCatalogSummary.partialCount, 90);
            assert.equal(manifest.ruleCatalogSummary.unsupportedCount, 73);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-25 represents supported downtime checks and outcomes with complete source accounting', () => {
            const expected = [
                ['rule:dmg:downtime-activity-building-a-stronghold', '$.variantrule[57]', 'UNSUPPORTED', 4, 35],
                ['rule:dmg:downtime-activity-carousing', '$.variantrule[59]', 'UNSUPPORTED', 3, 20],
                ['rule:dmg:downtime-activity-gaining-renown', '$.variantrule[65]', 'PARTIAL', 1, 0],
                ['rule:dmg:downtime-activity-performing-sacred-rites', '$.variantrule[67]', 'PARTIAL', 3, 0],
                ['rule:dmg:downtime-activity-running-a-business', '$.variantrule[75]', 'UNSUPPORTED', 4, 20],
                ['rule:dmg:downtime-activity-selling-magic-items', '$.variantrule[78]', 'PARTIAL', 8, 43],
                ['rule:dmg:downtime-activity-sowing-rumors', '$.variantrule[79]', 'PARTIAL', 5, 12],
                ['rule:dmg:downtime-activity-training-to-gain-levels', '$.variantrule[82]', 'UNSUPPORTED', 3, 20],
                ['rule:phb:downtime-activity-crafting', '$.variantrule[61]', 'PARTIAL', 4, 0],
                ['rule:phb:downtime-activity-practicing-a-profession', '$.variantrule[69]', 'PARTIAL', 3, 0]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 38);
            assert.equal(expected.reduce((sum, [, , , , nested]) => sum + nested, 0), 150);
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, coverage, roots, nested] of expected) {
                const entity = ruleById.get(id);
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                assert.ok(entity, `missing Rule ${id}`);
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(record.entries.length, roots);
                assert.equal(item.nestedClausePathCount, nested);
                assert.equal(item.coverage, coverage);
                assert.deepEqual(item.gapIds, ['R-GAP-13']);
                const sourceMetadata = expectedSourceMetadata('rule', sourceRecordForCanonical(entity));
                if (id === 'rule:dmg:downtime-activity-training-to-gain-levels') {
                    delete sourceMetadata.reprintedAs;
                }
                assert.deepEqual(entity.sourceMetadata, sourceMetadata);
                for (const entry of record.entries) {
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(disposition =>
                            disposition.portion.includes(nestedPath)), `${id} unaccounted path ${nestedPath}`);
                    }
                }
                for (const nestedPath of item.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath))), `${id} unaccounted nested clause ${nestedPath}`);
                }
                if (coverage === 'UNSUPPORTED') {
                    assert.equal(entity.mechanics, undefined, `${id} must remain reference-only`);
                    assert.equal(entity.references, undefined, `${id} has no supported relationship to assert`);
                }
            }
            const renown = ruleById.get('rule:dmg:downtime-activity-gaining-renown');
            assert.deepEqual(renown.references, [{
                targetType: 'rule', id: 'rule:dmg:renown', relationship: 'activity-advances-renown'
            }]);
            assert.deepEqual(ruleById.get('rule:dmg:downtime-activity-performing-sacred-rites').references, [{
                targetType: 'rule', id: 'rule:phb:inspiration', relationship: 'activity-grants-inspiration'
            }]);
            const sale = ruleById.get('rule:dmg:downtime-activity-selling-magic-items').mechanics[0];
            assert.equal(sale.procedure, 'sell');
            assert.deepEqual([sale.check.abilities, sale.check.skill.id, sale.check.dc],
                [['intelligence'], 'skill:phb:investigation', 20]);
            const rumor = ruleById.get('rule:dmg:downtime-activity-sowing-rumors').mechanics[0];
            assert.equal(rumor.subtype, 'INFLUENCE');
            assert.deepEqual([rumor.check.abilities, rumor.check.dc], [['charisma'], 15]);
            assert.deepEqual(ruleById.get('rule:phb:downtime-activity-crafting').mechanics[0], {
                componentId: 'crafting-activity',
                kind: 'DOWNTIME_PROCEDURE',
                subtype: 'ACTIVITY',
                procedure: 'craft',
                duration: { amount: 1, unit: 'day' },
                outcome: 'item-created'
            });
            const profession = ruleById.get('rule:phb:downtime-activity-practicing-a-profession').mechanics[0];
            assert.deepEqual([profession.procedure, profession.minimumLifestyle], ['work', 'MODEST']);
            assert.equal(JSON.stringify(rumor).includes('friendly'), false);
            assert.equal(JSON.stringify(rumor).includes('hostile'), false);
            assert.equal(JSON.stringify(ruleById.get('rule:dmg:downtime-activity-carousing')).includes('carouse'), false);
            const trainingReprint = manifest.sourceFieldIssues.find(issue =>
                issue.canonicalId === 'rule:dmg:downtime-activity-training-to-gain-levels'
                    && issue.sourceField === 'reprintedAs');
            assert.equal(trainingReprint?.cause, 'REFERENCE_UNRESOLVED');
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.ruleCatalogSummary.partialCount, 90);
            assert.equal(manifest.ruleCatalogSummary.unsupportedCount, 73);
            assert.equal(manifest.canonicalRecordCount, 551);
            assert.equal(new Set(entities.map(({ entity }) => entity.id)).size, entities.length);
        });

        check('RULE-CONV-24 represents survivor and spell checks while preserving spell-state boundaries', () => {
            const expected = [
                ['rule:vrgr:survivors', '$.variantrule[215]', 'PARTIAL', 7, 97, ['R-GAP-37']],
                ['rule:xge:spellcasting', '$.variantrule[209]', 'PARTIAL', 5, 58, ['R-GAP-35', 'R-GAP-36']],
                ['rule:xphb:armor-training', '$.variantrule[13]', 'PARTIAL', 1, 0, []],
                ['rule:xphb:cantrip', '$.variantrule[23]', 'PARTIAL', 1, 0, []],
                ['rule:xphb:curses', '$.variantrule[40]', 'PARTIAL', 1, 0, []],
                ['rule:xphb:illusions', '$.variantrule[132]', 'UNSUPPORTED', 2, 0, []],
                ['rule:xphb:magical-effect', '$.variantrule[150]', 'UNSUPPORTED', 1, 0, []],
                ['rule:xphb:possession', '$.variantrule[174]', 'PARTIAL', 1, 0, []],
                ['rule:xphb:ritual', '$.variantrule[184]', 'UNSUPPORTED', 1, 0, []],
                ['rule:xphb:shape-shifting', '$.variantrule[189]', 'PARTIAL', 1, 0, []],
                ['rule:xphb:spell', '$.variantrule[206]', 'PARTIAL', 1, 0, []],
                ['rule:xphb:spellcasting-focus', '$.variantrule[210]', 'UNSUPPORTED', 1, 0, []],
                ['rule:xphb:telepathy', '$.variantrule[219]', 'PARTIAL', 3, 0, []]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 26);
            assert.equal(expected.reduce((sum, [, , , , nested]) => sum + nested, 0), 155);
            const byId = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, coverage, roots, nested, gaps] of expected) {
                const entity = byId.get(id);
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                assert.ok(entity, `missing Rule ${id}`);
                assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(record.entries.length, roots);
                assert.equal(item.nestedClausePathCount, nested);
                assert.equal(item.coverage, coverage);
                assert.deepEqual(item.gapIds, gaps);
                for (const entry of record.entries) {
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(disposition =>
                            disposition.portion.includes(nestedPath)), `${id} unaccounted path ${nestedPath}`);
                    }
                }
                for (const nestedPath of item.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath))), `${id} unaccounted nested clause ${nestedPath}`);
                }
            }
            const survivorBenefit = byId.get('rule:vrgr:survivors').mechanics[0];
            assert.equal(survivorBenefit.componentId, 'desperate-scream-saving-throw');
            assert.equal(survivorBenefit.test, 'saving-throw');
            assert.equal(survivorBenefit.modifier.direction, 'advantage');
            const identifyCheck = byId.get('rule:xge:spellcasting').mechanics[0];
            assert.deepEqual(identifyCheck.abilities, ['intelligence']);
            assert.equal(identifyCheck.skill.id, 'skill:phb:arcana');
            assert.deepEqual(identifyCheck.dc, {
                kind: 'FIXED_PLUS_SPELL_LEVEL',
                base: 15,
                spellRole: 'identified-spell'
            });
            assert.deepEqual(byId.get('rule:xphb:armor-training').references.map(reference => reference.id), [
                'rule:xphb:disadvantage', 'rule:xphb:d20-test'
            ]);
            assert.equal(byId.get('rule:xphb:cantrip').references[0].id, 'rule:xphb:spell');
            assert.deepEqual(byId.get('rule:xphb:shape-shifting').references.map(reference => reference.id), [
                'rule:xphb:condition', 'rule:xphb:dead'
            ]);
            assert.equal(byId.get('rule:xphb:spell').references[0].id, 'rule:xphb:magical-effect');
            assert.equal(byId.get('rule:xphb:telepathy').references[0].id, 'condition:xphb:incapacitated');
            for (const id of ['rule:xphb:curses', 'rule:xphb:illusions', 'rule:xphb:magical-effect',
                'rule:xphb:possession', 'rule:xphb:ritual', 'rule:xphb:spellcasting-focus']) {
                assert.equal(byId.get(id).mechanics, undefined, `${id} must remain reference-only`);
            }
            assert.ok(records.find(record => record.canonicalId === 'rule:xphb:possession')
                .entries[0].classifications.some(item => item.classification === 'INTERPRETED'));
            assert.equal(records.find(record => record.canonicalId === 'rule:xphb:possession')
                .entries[0].classifications.some(item => item.cause === 'REFERENCE_UNRESOLVED'), false);
            assert.ok(records.find(record => record.canonicalId === 'rule:xge:spellcasting')
                .entries[4].classifications[0].reason.includes('token/template placement'));
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.ruleCatalogSummary.partialCount, 90);
            assert.equal(manifest.ruleCatalogSummary.unsupportedCount, 73);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-23 preserves supported explosive outcomes and accounts magic/creature procedure gaps', () => {
            const expected = [
                ['rule:dmg:explosives', '$.variantrule[104]', 'PARTIAL', 5, 17, []],
                ['rule:dmg:mixing-potions', '$.variantrule[153]', 'UNSUPPORTED', 3, 22, ['R-GAP-43']],
                ['rule:dmg:more-difficult-identification', '$.variantrule[156]', 'PARTIAL', 1, 0, []],
                ['rule:dmg:scroll-mishaps', '$.variantrule[188]', 'UNSUPPORTED', 2, 18, []],
                ['rule:dmg:spell-points', '$.variantrule[208]', 'UNSUPPORTED', 9, 63, []],
                ['rule:dmg:wands-that-don-t-recharge', '$.variantrule[228]', 'UNSUPPORTED', 1, 0, []],
                ['rule:mm:familiars', '$.variantrule[107]', 'PARTIAL', 2, 0, []],
                ['rule:mm:player-characters-as-vampires', '$.variantrule[171]', 'PARTIAL', 2, 4, []],
                ['rule:vgm:familiars', '$.variantrule[108]', 'PARTIAL', 2, 0, []],
                ['rule:vrgr:haunted-traps', '$.variantrule[118]', 'UNSUPPORTED', 4, 17, ['R-GAP-29']]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 31);
            assert.equal(expected.reduce((sum, [, , , , nested]) => sum + nested, 0), 141);
            const byId = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, coverage, roots, nested, gaps] of expected) {
                const entity = byId.get(id);
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                assert.ok(entity, `missing Rule ${id}`);
                const expectedMetadata = expectedSourceMetadata('rule', sourceRecordForCanonical(entity));
                if (['rule:dmg:explosives', 'rule:dmg:mixing-potions'].includes(id)) {
                    delete expectedMetadata.reprintedAs;
                }
                assert.deepEqual(entity.sourceMetadata, expectedMetadata);
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(record.entries.length, roots);
                assert.equal(item.nestedClausePathCount, nested);
                assert.equal(item.coverage, coverage);
                assert.deepEqual(item.gapIds, gaps);
                for (const entry of record.entries) {
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(disposition =>
                            disposition.portion.includes(nestedPath)), `${id} unaccounted path ${nestedPath}`);
                    }
                }
                for (const nestedPath of item.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath))), `${id} unaccounted nested clause ${nestedPath}`);
                }
            }
            const explosives = byId.get('rule:dmg:explosives');
            assert.equal(explosives.mechanics.length, 17);
            assert.equal(explosives.composition.kind, 'OR');
            assert.equal(explosives.composition.members.length, 6);
            assert.equal(explosives.mechanics.find(item => item.componentId === 'bomb-damage')
                .damageRoll.base.count, 3);
            assert.equal(explosives.mechanics.find(item => item.componentId === 'fragmentation-grenade-save')
                .savingThrow.dc.value, 15);
            assert.equal(explosives.mechanics.find(item => item.componentId === 'smoke-grenade-obscuration')
                .level, 'heavily-obscured');
            for (const id of ids.slice(1)) assert.equal(byId.get(id).mechanics, undefined, `${id} must be reference-only`);
            const unresolvedReprints = ['rule:dmg:explosives', 'rule:dmg:mixing-potions'];
            for (const id of unresolvedReprints) {
                const issue = manifest.sourceFieldIssues.find(item =>
                    item.canonicalId === id && item.sourceField === 'reprintedAs');
                assert.equal(issue?.cause, 'REFERENCE_UNRESOLVED');
                assert.equal(Object.hasOwn(byId.get(id).sourceMetadata, 'reprintedAs'), false);
            }
            assert.equal(byId.get('rule:mm:player-characters-as-vampires').sourceMetadata.citations[0].source, 'VRGR');
            assert.ok(records.find(record => record.canonicalId === 'rule:dmg:spell-points')
                .entries[2].classifications[0].reason.includes('Resource balance'));
            assert.ok(records.find(record => record.canonicalId === 'rule:vrgr:haunted-traps')
                .entries[1].classifications[0].reason.includes('R-GAP-29'));
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.ruleCatalogSummary.partialCount, 90);
            assert.equal(manifest.ruleCatalogSummary.unsupportedCount, 73);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-22 represents Inspiration benefit and preserves social, madness, and campaign boundaries', () => {
            const expected = [
                ['rule:dmg:morale', '$.variantrule[155]', 'UNSUPPORTED', 8, 8, ['R-GAP-44']],
                ['rule:phb:inspiration', '$.variantrule[140]', 'PARTIAL', 3, 8, ['R-GAP-25']],
                ['rule:psi:emrakul-s-madness', '$.variantrule[96]', 'PARTIAL', 7, 15, ['R-GAP-26', 'R-GAP-27']],
                ['rule:xge:shared-campaign-variant-rules', '$.variantrule[190]', 'UNSUPPORTED', 5, 142, ['R-GAP-33']],
                ['rule:xphb:heroic-inspiration', '$.variantrule[124]', 'PARTIAL', 2, 0, ['R-GAP-25']]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 25);
            assert.equal(expected.reduce((sum, [, , , , nested]) => sum + nested, 0), 173);
            const byId = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, coverage, roots, nested, gaps] of expected) {
                const entity = byId.get(id);
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                assert.ok(entity, `missing Rule ${id}`);
                assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(record.entries.length, roots);
                assert.equal(item.nestedClausePathCount, nested);
                assert.equal(item.coverage, coverage);
                assert.deepEqual(item.gapIds, gaps);
                for (const entry of record.entries) {
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(disposition =>
                            disposition.portion.includes(nestedPath)), `${id} unaccounted path ${nestedPath}`);
                    }
                }
                for (const nestedPath of item.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath))), `${id} unaccounted nested clause ${nestedPath}`);
                }
            }
            assert.deepEqual(byId.get('rule:phb:inspiration').mechanics, [{
                componentId: 'inspiration-benefit',
                kind: 'D20_TEST',
                subtype: 'TEST_MODIFIER',
                test: 'd20-test',
                modifier: {
                    kind: 'D20_ROLL_MODIFIER',
                    roll: 'd20-test',
                    direction: 'advantage',
                    diceCount: 2,
                    select: 'higher',
                    cancelsOpposite: true,
                    multipleInstancesStack: false
                }
            }]);
            assert.equal(byId.get('rule:dmg:morale').mechanics, undefined);
            assert.equal(byId.get('rule:xge:shared-campaign-variant-rules').mechanics, undefined);
            assert.equal(byId.get('rule:psi:emrakul-s-madness').references[0].id,
                'rule:dmg:new-ability-scores-honor-and-sanity');
            assert.equal(byId.get('rule:xphb:heroic-inspiration').references[0].id,
                'rule:xphb:player-character');
            assert.ok(records.find(record => record.canonicalId === 'rule:psi:emrakul-s-madness')
                .entries[5].classifications[0].reason.includes('R-GAP-27'));
            assert.ok(records.find(record => record.canonicalId === 'rule:xge:shared-campaign-variant-rules')
                .entries[3].classifications[0].reason.includes('treasure-point economy'));
            assert.ok(records.find(record => record.canonicalId === 'rule:xphb:heroic-inspiration')
                .entries[0].classifications[0].reason.includes('arbitrary-die reroll'));
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-21 preserves optional Honor and Sanity identity without unsupported score mechanics', () => {
            const id = 'rule:dmg:new-ability-scores-honor-and-sanity';
            const entity = entities.map(({ entity }) => entity).find(item => item.id === id);
            const record = manifest.records.find(item => item.canonicalId === id);
            const item = manifest.ruleCatalogInventory.find(entry => entry.canonicalId === id);
            assert.ok(entity, `missing referenceable Rule ${id}`);
            assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
            assert.equal(entity.ruleType, 'OPTIONAL');
            assert.equal(entity.mechanics, undefined);
            assert.equal(entity.references, undefined);
            assert.equal(record.sourceObjectPath, '$.variantrule[159]');
            assert.equal(record.sourceEntryCount, 7);
            assert.equal(record.entries.length, 7);
            assert.equal(record.conversionStatus, 'unsupported');
            assert.equal(item.nestedClausePathCount, 46);
            assert.deepEqual(item.gapIds, ['R-GAP-26', 'R-GAP-24']);
            assert.equal(item.coverage, 'UNSUPPORTED');
            assert.ok(record.entries[3].classifications[0].reason.includes('score-generation'));
            assert.ok(record.entries[4].classifications[0].reason.includes('fallback'));
            assert.ok(record.entries[5].classifications[0].reason.includes('six-ability'));
            assert.ok(record.entries[6].classifications[0].reason.includes('R-GAP-24'));
            for (const entry of record.entries) {
                assert.equal(entry.classifications.every(disposition =>
                    disposition.classification === 'OMITTED'), true);
                for (const nestedPath of entry.nestedSourcePaths) {
                    assert.ok(entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath)), `unaccounted nested path ${nestedPath}`);
                }
            }
            for (const nestedPath of item.nestedClausePaths) {
                assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                    disposition.portion.includes(nestedPath))), `unaccounted nested clause ${nestedPath}`);
            }
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.ruleCatalogSummary.unsupportedCount, 73);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-20 references Inspiration benefit while deferring resource lifecycle and Madness outcomes', () => {
            const expected = [
                ['rule:dmg:inspiration', '$.variantrule[139]', 6, 44, 'R-GAP-25'],
                ['rule:dmg:madness', '$.variantrule[149]', 4, 99, 'R-GAP-24']
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 10);
            assert.equal(expected.reduce((sum, [, , , nested]) => sum + nested, 0), 143);
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, roots, nested, gapId] of expected) {
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                const entity = ruleById.get(id);
                assert.ok(entity, `missing referenceable Rule ${id}`);
                assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(record.entries.length, roots);
                assert.equal(item.nestedClausePathCount, nested);
                assert.equal(item.coverage, id === 'rule:dmg:inspiration' ? 'PARTIAL' : 'UNSUPPORTED');
                assert.deepEqual(item.gapIds, [gapId]);
                assert.equal(item.canonicalOutput, record.canonicalOutput);
                assert.equal(record.conversionStatus,
                    id === 'rule:dmg:inspiration' ? 'partially-represented' : 'unsupported');
                assert.equal(entity.mechanics, undefined);
                if (id === 'rule:dmg:inspiration') {
                    assert.deepEqual(entity.references, [{
                        targetType: 'rule',
                        id: 'rule:xphb:advantage',
                        relationship: 'benefit-of-spending-inspiration'
                    }]);
                } else {
                    assert.equal(entity.references, undefined);
                }
                assert.equal(entity.composition, undefined);
                for (const entry of record.entries) {
                    if (id === 'rule:dmg:inspiration' && entry.sourceEntryIndex === 0) {
                        assert.deepEqual(entry.classifications.map(disposition => disposition.classification),
                            ['INTERPRETED', 'OMITTED']);
                    } else {
                        assert.equal(entry.classifications.every(disposition =>
                            disposition.classification === 'OMITTED'), true);
                    }
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(disposition =>
                            disposition.portion.includes(nestedPath)), `${id} unaccounted path ${nestedPath}`);
                    }
                }
                for (const nestedPath of item.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath))), `${id} unaccounted nested clause ${nestedPath}`);
                }
            }
            const inspiration = records.find(record => record.canonicalId === 'rule:dmg:inspiration');
            assert.ok(inspiration.entries[0].classifications[1].reason.includes('R-GAP-25'));
            assert.ok(inspiration.entries[5].classifications[0].reason.includes('R-GAP-25'));
            const madness = records.find(record => record.canonicalId === 'rule:dmg:madness');
            assert.equal(madness.entries[0].classifications[0].cause, 'NON_MECHANICAL');
            assert.equal(madness.entries[1].classifications[0].cause, 'OUT_OF_APPROVED_SCOPE');
            assert.ok(madness.entries[1].classifications[0].reason.includes('standalone mechanic'));
            assert.ok(madness.entries[2].classifications[0].reason.includes('R-GAP-24'));
            assert.ok(madness.entries[3].classifications[0].reason.includes('R-GAP-24'));
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.ruleCatalogSummary.unsupportedCount, 73);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-19 represents social attitudes and accounts unsupported relationship resolution', () => {
            const expected = [
                ['rule:dmg:loyalty', '$.variantrule[148]', 'PARTIAL', 3, 10],
                ['rule:xphb:ally', '$.variantrule[9]', 'PARTIAL', 1, 0],
                ['rule:xphb:enemy', '$.variantrule[100]', 'PARTIAL', 1, 0],
                ['rule:xphb:friendly-attitude', '$.variantrule[116]', 'PARTIAL', 1, 0],
                ['rule:xphb:hostile-attitude', '$.variantrule[129]', 'PARTIAL', 1, 0],
                ['rule:xphb:indifferent-attitude', '$.variantrule[135]', 'FULL', 1, 0],
                ['rule:xphb:attitude', '$.variantrule[15]', 'FULL', 1, 0],
                ['rule:xphb:nonplayer-character', '$.variantrule[160]', 'PARTIAL', 1, 0]
            ];
            assert.equal(expected.reduce((sum, [, , , roots]) => sum + roots, 0), 10);
            assert.equal(expected.reduce((sum, [, , , , nested]) => sum + nested, 0), 10);
            const byId = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, coverage, roots, nested] of expected) {
                const entity = byId.get(id);
                const record = manifest.records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                assert.ok(entity, `missing Rule ${id}`);
                const source = sourceRecordForCanonical(entity);
                const expectedMetadata = expectedSourceMetadata('rule', source);
                if (id === 'rule:dmg:loyalty') delete expectedMetadata.reprintedAs;
                assert.deepEqual(entity.sourceMetadata, expectedMetadata);
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(item.nestedClausePathCount, nested);
                assert.equal(item.coverage, coverage);
                for (const entry of record.entries) {
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications.some(disposition => disposition.portion.includes(nestedPath)));
                    }
                }
            }
            assert.deepEqual(byId.get('rule:xphb:attitude').mechanics.map(item => item.attitude),
                ['friendly', 'hostile', 'indifferent']);
            assert.equal(byId.get('rule:xphb:indifferent-attitude').mechanics[0].attitude, 'indifferent');
            assert.equal(byId.get('rule:xphb:friendly-attitude').mechanics[0].attitude, 'friendly');
            assert.equal(byId.get('rule:xphb:hostile-attitude').mechanics[0].attitude, 'hostile');
            assert.equal(byId.get('rule:dmg:loyalty').mechanics[0].scoreThreshold, 10);
            assert.equal(byId.get('rule:xphb:friendly-attitude').references[1].id, 'rule:xphb:advantage');
            assert.equal(byId.get('rule:xphb:hostile-attitude').references[1].id, 'rule:xphb:disadvantage');
            assert.equal(byId.get('rule:xphb:ally').mechanics, undefined);
            assert.equal(byId.get('rule:xphb:enemy').mechanics, undefined);
            assert.equal(byId.get('rule:xphb:nonplayer-character').references[0].id, 'rule:xphb:monster');
            const reprintIssue = manifest.sourceFieldIssues.find(item =>
                item.canonicalId === 'rule:dmg:loyalty' && item.sourceField === 'reprintedAs');
            assert.equal(reprintIssue?.cause, 'REFERENCE_UNRESOLVED');
            assert.equal(byId.get('rule:dmg:renown').references.every(reference => byId.has(reference.id)), true);
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-14 preserves both Sidekick identities and defers their edition-specific progression', () => {
            const expected = [
                ['rule:esk:sidekicks', '$.variantrule[196]', 8, 120],
                ['rule:tce:sidekicks', '$.variantrule[197]', 5, 32]
            ];
            const ids = expected.map(([id]) => id);
            const records = manifest.records.filter(record => ids.includes(record.canonicalId));
            assert.deepEqual(records.map(record => record.canonicalId), ids);
            assert.equal(records.reduce((sum, record) => sum + record.sourceEntryCount, 0), 13);
            assert.equal(expected.reduce((sum, [, , , nested]) => sum + nested, 0), 152);
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const inventory = new Map(manifest.ruleCatalogInventory.map(item => [item.canonicalId, item]));
            for (const [id, sourcePath, roots, nested] of expected) {
                const record = records.find(item => item.canonicalId === id);
                const item = inventory.get(id);
                const entity = ruleById.get(id);
                assert.ok(entity, `missing referenceable Rule ${id}`);
                assert.equal(record.sourceObjectPath, sourcePath);
                assert.equal(record.sourceEntryCount, roots);
                assert.equal(item.nestedClausePathCount, nested);
                assert.equal(item.coverage, 'UNSUPPORTED');
                assert.deepEqual(item.gapIds, ['R-GAP-34']);
                assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata('rule', sourceRecordForCanonical(entity)));
                assert.equal(entity.mechanics, undefined);
                assert.equal(entity.references, undefined);
                assert.equal(record.conversionStatus, 'unsupported');
                assert.equal(record.entries.length, roots);
                for (const entry of record.entries) {
                    assert.equal(entry.classifications[0].classification, 'OMITTED');
                    assert.ok(['NON_MECHANICAL', 'OUT_OF_APPROVED_SCOPE', 'REFERENCE_UNRESOLVED']
                        .includes(entry.classifications[0].cause));
                    for (const nestedPath of entry.nestedSourcePaths) {
                        assert.ok(entry.classifications[0].portion.includes(nestedPath));
                    }
                }
                for (const nestedPath of item.nestedClausePaths) {
                    assert.ok(record.entries.some(entry => entry.classifications.some(disposition =>
                        disposition.portion.includes(nestedPath))), `${id} unaccounted path ${nestedPath}`);
                }
            }
            assert.equal(records[0].entries[5].classifications[0].cause, 'REFERENCE_UNRESOLVED');
            assert.equal(manifest.ruleCatalogSummary.conversionAccountedCount, 231);
            assert.equal(manifest.ruleCatalogSummary.conversionPendingCount, 0);
            assert.equal(manifest.ruleCatalogSummary.canonicalRuleOutputCount, 231);
            assert.equal(manifest.canonicalRecordCount, 551);
        });

        check('RULE-CONV-01 preserves XPHB Action and glossary reference boundaries', () => {
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const action = ruleById.get('rule:xphb:action');
            const actionTargets = action.references.map(reference => reference.id);
            assert.equal(actionTargets.length, 11);
            assert.ok(actionTargets.includes('action:xphb:attack'));
            assert.ok(actionTargets.includes('action:xphb:utilize'));
            assert.equal(actionTargets.includes('action:xphb:dodge'), false);
            const actionAccounting = manifest.records.find(record => record.canonicalId === action.id);
            const dodgeOmission = actionAccounting.entries[1].classifications.find(item =>
                item.classification === 'OMITTED' && item.cause === 'REFERENCE_UNRESOLVED');
            assert.ok(dodgeOmission?.portion.includes('items[1]'));
            assert.equal(actionAccounting.entries[0].classifications[0].cause, 'SCHEMA_UNSUPPORTED');

            assert.deepEqual(ruleById.get('rule:xphb:adventure').references, [
                { targetType: 'rule', id: 'rule:xphb:encounter' }
            ]);
            assert.deepEqual(ruleById.get('rule:xphb:campaign').references, [
                { targetType: 'rule', id: 'rule:xphb:adventure' }
            ]);
            assert.deepEqual(ruleById.get('rule:xphb:target').references, [
                { targetType: 'rule', id: 'rule:xphb:attack-roll' },
                { targetType: 'rule', id: 'rule:xphb:saving-throw' }
            ]);
            assert.deepEqual(ruleById.get('rule:xphb:hazard').references, undefined);
            assert.equal(manifest.records.find(record => record.canonicalId === 'rule:xphb:hazard')
                .conversionStatus, 'unsupported');
        });

        check('Batch 4 falling, area geometry, movement procedures, and movement costs match source', () => {
            const ruleById = new Map(entities.map(({ entity }) => [entity.id, entity]));
            const sourceById = new Map(rules.map(record => [
                `rule:${canonicalSlug(record.source)}:${canonicalSlug(record.name)}`, record
            ]));

            const fall = ruleById.get('rule:xge:falling');
            const fallDamage = fall.mechanics.find(component => component.details?.kind === 'FALL_DAMAGE').details;
            assert.deepEqual([
                fallDamage.distanceIncrementFeet,
                fallDamage.dicePerIncrement.diceCount,
                fallDamage.dicePerIncrement.dieSize,
                fallDamage.maximumDiceCount,
                fallDamage.damageType,
                fallDamage.partialIncrementTreatment,
                fallDamage.landingCondition.condition.id
            ], [10, 1, 6, 20, 'bludgeoning', 'SOURCE_UNSPECIFIED', 'condition:phb:prone']);
            const optionalFlight = fall.mechanics.find(component =>
                component.details?.kind === 'FLYING_FALL_RULE').details;
            assert.equal(optionalFlight.ruleStatus, 'OPTIONAL');
            assert.deepEqual(optionalFlight.fallingTriggers, ['PRONE', 'FLY_SPEED_ZERO', 'OTHERWISE_UNABLE_TO_MOVE']);
            assert.equal(optionalFlight.prevention, 'HOVER_OR_MAGIC_HOLD_ALOFT');
            assert.ok(sourceById.get(fall.id).entries[0].includes('maximum of {@damage 20d6}'));
            assert.ok(sourceById.get(fall.id).entries[2].entries[0].includes('unless it can hover'));
            assert.equal(JSON.stringify(fall).includes('500'), false);
            assert.equal(JSON.stringify(fall).includes('subtract the creature'), false);

            const area = ruleById.get('rule:xphb:area-of-effect').mechanics[0];
            assert.deepEqual(area.shapes, ['cone', 'cube', 'cylinder', 'emanation', 'line', 'sphere']);
            assert.equal(area.lineOfEffect.locationIncludedWhen, 'AT_LEAST_ONE_UNBLOCKED_LINE');
            assert.equal(area.lineOfEffect.blockedBy.coverLevel, 'total');
            assert.equal(area.unseenOriginPlacement, 'NEAR_SIDE_OF_INTERVENING_OBSTRUCTION');
            const shapeCases = [
                ['cone', 'POINT_AND_DIRECTION', ['MAXIMUM_LENGTH'], 'EXCLUDED_UNLESS_CREATOR_CHOOSES'],
                ['cube', 'ANY_CUBE_FACE', ['SIDE_LENGTH'], 'EXCLUDED_UNLESS_CREATOR_CHOOSES'],
                ['cylinder', 'CENTER_OF_TOP_OR_BOTTOM', ['BASE_RADIUS', 'HEIGHT'], 'INCLUDED'],
                ['emanation', 'CREATURE_OR_OBJECT', ['EXTENT'], 'EXCLUDED_UNLESS_CREATOR_CHOOSES'],
                ['line', 'POINT_AND_DIRECTION', ['LENGTH', 'WIDTH'], 'EXCLUDED_UNLESS_CREATOR_CHOOSES'],
                ['sphere', 'POINT_IN_SPACE', ['RADIUS'], 'INCLUDED']
            ];
            for (const [shape, placement, dimensions, included] of shapeCases) {
                const identity = shape === 'emanation' ? 'rule:xphb:emanation-area-of-effect'
                    : `rule:xphb:${shape}-area-of-effect`;
                const component = ruleById.get(identity).mechanics[0];
                assert.deepEqual([
                    component.shape, component.originPlacement,
                    component.dimensionParameters, component.originIncluded
                ], [shape, placement, dimensions, included]);
            }
            assert.equal(ruleById.get('rule:xphb:cone-area-of-effect').mechanics[0].coneWidthRule,
                'WIDTH_EQUALS_DISTANCE_FROM_ORIGIN');
            assert.deepEqual(ruleById.get('rule:xphb:emanation-area-of-effect').mechanics[0].originBehavior,
                { kind: 'EMANATION', followsOriginUnless: ['INSTANTANEOUS', 'STATIONARY'] });

            const climb = ruleById.get('rule:xphb:climbing').mechanics[0].details;
            assert.deepEqual([
                climb.additionalFeetPerFoot, climb.difficultTerrainAdditionalFeetPerFoot,
                climb.difficultTerrainCostRelationship, climb.waivedWhenUsingSpeedType,
                climb.optionalCheck.dc, climb.optionalCheck.trigger
            ], [1, 2, 'DO_NOT_ADD', 'climb', 15, 'SLIPPERY_OR_FEW_HANDHOLDS']);
            const climbSpeed = ruleById.get('rule:xphb:climb-speed').mechanics[0].details;
            assert.deepEqual([
                climbSpeed.speedType, climbSpeed.appliesTo,
                climbSpeed.replacesSpeed, climbSpeed.waivesClimbingExtraMovementCost
            ], ['climb', 'VERTICAL_SURFACE', true, true]);
            const crawling = ruleById.get('rule:xphb:crawling').mechanics[0].details;
            assert.deepEqual([
                crawling.additionalFeetPerFoot, crawling.difficultTerrainAdditionalFeetPerFoot,
                crawling.difficultTerrainCostRelationship
            ], [1, 2, 'DO_NOT_ADD']);
            const swim = ruleById.get('rule:xphb:swimming').mechanics[0].details;
            assert.deepEqual([
                swim.additionalFeetPerFoot, swim.difficultTerrainAdditionalFeetPerFoot,
                swim.difficultTerrainCostRelationship, swim.waivedWhenUsingSpeedType,
                swim.optionalCheck.dc, swim.optionalCheck.trigger
            ], [1, 2, 'DO_NOT_ADD', 'swim', 15, 'ROUGH_WATER_ANY_DISTANCE']);
            assert.deepEqual(ruleById.get('rule:xphb:burrow-speed').mechanics[0].details.permittedMaterials,
                ['sand', 'earth', 'mud', 'ice']);
            assert.equal(ruleById.get('rule:xphb:high-jump').mechanics[0].details.distanceFormula.minimumFeet, 0);
            assert.equal(ruleById.get('rule:xphb:long-jump').mechanics[0].details.obstacleCheck.failureOutcome,
                'HIT_OBSTACLE');
            assert.deepEqual(ruleById.get('rule:xphb:speed').mechanics.map(component => component.details.kind),
                ['SPECIAL_SPEED_SELECTION', 'SPECIAL_SPEED_ADJUSTMENT']);
            assert.equal(ruleById.get('rule:xphb:flying').mechanics[0].details.hoverPreventsFall, true);
            assert.deepEqual(ruleById.get('rule:xphb:fly-speed').mechanics[0].details.staysAloftUntil,
                ['LANDS', 'FALLS', 'DIES']);
            assert.deepEqual(ruleById.get('rule:xphb:jumping').mechanics[0].details, {
                kind: 'JUMP_PROCEDURE_SELECTION',
                selection: 'ONE',
                horizontal: { targetType: 'rule', id: 'rule:xphb:long-jump' },
                vertical: { targetType: 'rule', id: 'rule:xphb:high-jump' }
            });

            const difficultTerrain = readJson(path.join(
                phase3Root,
                'rule/xphb-difficult-terrain.json'
            )).mechanics[0];
            assert.deepEqual([difficultTerrain.extraFeetPerFoot, difficultTerrain.cumulative], [1, false]);
            const diagonal = ruleById.get('rule:dmg:diagonals').mechanics[0].details;
            assert.deepEqual(diagonal.diagonalPatternFeet, [5, 10]);
            assert.equal(diagonal.patternContinuesAcrossStraightMovement, true);
            assert.equal(diagonal.ruleOptional, true);
            const grid = ruleById.get('rule:phb:playing-on-a-grid').mechanics[0].details;
            assert.deepEqual([
                grid.gridUnitFeet, grid.movementUnitFeet,
                grid.minimumMovementRemaining, grid.extraCostRequiresFullPayment
            ], [5, 5, 1, true]);
            assert.equal(JSON.stringify(grid).includes('RANGE'), false);
        });

        const languageRecords = manifest.records.filter(record => record.model === 'language');
        check('Phase 5 Language source fields and linked speaker coverage', () => {
            assert.equal(languageRecords.length, 197);
            const languageOutputs = entities.map(({ entity }) => entity)
                .filter(entity => entity.id.startsWith('language:'));
            assert.equal(languageOutputs.length, 197);
            const phase3Languages = phase3Files().map(file => readJson(file))
                .filter(entity => entity.id.startsWith('language:'));
            const sourceLanguages = readJson(path.join(workspace,
                'src/main/resources/data/languages.json')).language;
            const allLanguageIds = [...phase3Languages, ...languageOutputs].map(entity => entity.id);
            assert.equal(sourceLanguages.length, 201);
            assert.equal(new Set(allLanguageIds).size, 201);
            assert.deepEqual(new Set(allLanguageIds), new Set(sourceLanguages.map(record =>
                `language:${canonicalSlug(record.source)}:${canonicalSlug(record.name)}`)));
            const knownFields = new Set([
                'name', 'source', 'page', 'reprintedAs', 'type', 'script', 'typicalSpeakers',
                'origin', 'dialects', 'additionalSources', 'otherSources', 'srd', 'srd52',
                'basicRules', 'basicRules2024', 'entries', 'fonts', 'hasFluffImages'
            ]);
            for (const accountingRecord of languageRecords) {
                const source = sourceRecordForCanonical({
                    id: accountingRecord.canonicalId,
                    name: accountingRecord.name,
                    sourceMetadata: { source: accountingRecord.source }
                });
                for (const field of Object.keys(source)) {
                    assert.ok(knownFields.has(field), `${accountingRecord.canonicalId}: unreviewed source field ${field}`);
                    const hasIssue = manifest.sourceFieldIssues.some(issue =>
                        issue.canonicalId === accountingRecord.canonicalId && issue.sourceField === field);
                    const represented = [
                        'name', 'source', 'page', 'reprintedAs', 'type', 'script',
                        'additionalSources', 'otherSources', 'srd', 'srd52',
                        'basicRules', 'basicRules2024', 'entries'
                    ].includes(field)
                        || (field === 'typicalSpeakers' && languageOutputs.find(entity =>
                            entity.id === accountingRecord.canonicalId)?.typicalSpeakers?.length > 0)
                        || (field === 'origin' && languageOutputs.find(entity =>
                            entity.id === accountingRecord.canonicalId)?.origin?.length > 0)
                        || (field === 'dialects' && languageOutputs.find(entity =>
                            entity.id === accountingRecord.canonicalId)?.dialects?.length > 0);
                    assert.ok(represented || hasIssue,
                        `${accountingRecord.canonicalId}: source field ${field} is unaccounted`);
                }
            }
            for (const entity of languageOutputs) {
                const source = sourceRecordForCanonical(entity);
                assert.equal(entity.category, source.type ?? undefined);
                assert.equal(entity.script, source.script && source.script.toLowerCase() !== 'none'
                    ? source.script : undefined);
            }
            const blinkDog = outputById.get('language:mm:blink-dog');
            assert.deepEqual(blinkDog.typicalSpeakers, [
                { targetType: 'creature', id: 'creature:mm:blink-dog' }
            ]);
            const guran = outputById.get('language:scag:guran');
            assert.deepEqual(guran.origin.map(reference => reference.id), [
                'language:scag:roushoum', 'language:scag:rashemi'
            ]);
            const loross = outputById.get('language:idrotf:loross');
            assert.deepEqual(loross.intelligibility, [{
                kind: 'MUTUAL_INTELLIGIBILITY',
                mode: 'SPOKEN',
                directionality: 'MUTUAL',
                language: { targetType: 'language', id: 'language:phb:elvish' }
            }]);
            assert.deepEqual(outputById.get('language:phb:dwarvish').typicalSpeakers, [{
                filter: {
                    targetType: 'creature', creatureTypes: ['humanoid'],
                    tags: [{ group: 'any race', value: 'dwarf' }]
                }
            }]);
            assert.deepEqual(outputById.get('language:erlw:draconic').typicalSpeakers, [
                { filter: { targetType: 'creature', creatureTypes: ['dragon'], sources: ['ERLW'] } },
                {
                    filter: {
                        targetType: 'creature', creatureTypes: ['humanoid'],
                        tags: [{ group: 'any race', value: 'dragonborn' }], sources: ['ERLW']
                    }
                }
            ]);
        });
        check('Language exact tag selectors preserve source criteria without type inference', () => {
            const expectedTags = new Map([
                ['language:ggr:abyssal', [
                    { value: 'demon', source: 'GGR' },
                    { value: 'devil', source: 'GGR' }
                ]],
                ['language:phb:abyssal', [{ value: 'demon' }]],
                ['language:ggr:celestial', [{ value: 'angel', source: 'GGR' }]],
                ['language:ggr:goblin', [{ value: 'any race', source: 'GGR' }]],
                ['language:phb:goblin', [{ value: 'goblinoid' }]],
                ['language:phb:infernal', [{ value: 'devil' }]],
                ['language:ggr:loxodon', [{ value: 'any race', source: 'GGR' }]],
                ['language:ggr:merfolk', [{ value: 'any race;merfolk', source: 'GGR' }]],
                ['language:phb:orc', [{ value: 'orc' }]]
            ]);
            for (const [id, expected] of expectedTags) {
                const selectors = outputById.get(id).typicalSpeakers.map(speaker => speaker.filter.selector);
                assert.deepEqual(selectors, expected.map(({ value, source }) => ({
                    kind: 'TAG', value, ...(source ? { source } : {})
                })), id);
                assert.ok(outputById.get(id).typicalSpeakers.every(speaker =>
                    !speaker.filter.creatureTypes && !speaker.filter.tags && !speaker.filter.sources), id);
            }
            const phbAbyssal = outputById.get('language:phb:abyssal').typicalSpeakers[0].filter.selector;
            assert.equal(Object.hasOwn(phbAbyssal, 'source'), false);
            const wrongSource = structuredClone(outputById.get('language:ggr:celestial'));
            wrongSource.typicalSpeakers[0].filter.selector.source = 'PHB';
            rejects('Language accounting detects an invented tag source scope', () => {
                const expected = { kind: 'TAG', value: 'angel', source: 'GGR' };
                return JSON.stringify(wrongSource.typicalSpeakers[0].filter.selector) === JSON.stringify(expected);
            });
            const inventedUnscopedSource = structuredClone(outputById.get('language:phb:abyssal'));
            inventedUnscopedSource.typicalSpeakers[0].filter.selector.source = 'PHB';
            rejects('Language accounting detects a source scope absent from the selector', () => {
                return Object.hasOwn(inventedUnscopedSource.typicalSpeakers[0].filter.selector, 'source') === false;
            });
            const psiDemonic = outputById.get('language:psi:demonic');
            assert.equal(psiDemonic.typicalSpeakers, undefined);
            const psiIssue = manifest.sourceFieldIssues.find(issue =>
                issue.canonicalId === 'language:psi:demonic' && issue.sourceField === 'typicalSpeakers');
            assert.ok(psiIssue);
            assert.equal(psiIssue.disposition, 'OMITTED');
            assert.equal(psiIssue.cause, 'SCHEMA_UNSUPPORTED');
            assert.match(psiIssue.reason, /empty source parameter/i);
            assert.equal(manifest.sourceFieldIssues.some(issue =>
                expectedTags.has(issue.canonicalId) && issue.sourceField === 'typicalSpeakers'), false);
        });
        check('Language schema rejects unsupported or contradictory tag selectors', () => {
            const validateLanguage = ajv.getSchema('urn:thedndpub:schemas:core-rules:language:v1');
            const abyssal = outputById.get('language:phb:abyssal');
            const invalidCases = [
                ['empty tag value', { kind: 'TAG', value: '' }],
                ['empty tag source', { kind: 'TAG', value: 'demon', source: '' }],
                ['unsupported search selector', { kind: 'SEARCH', value: 'demon' }],
                ['unknown selector kind', { kind: 'CREATURE', value: 'demon' }],
                ['inferred creature type combined with tag selector', {
                    kind: 'TAG', value: 'demon', creatureTypes: ['fiend']
                }]
            ];
            for (const [label, selector] of invalidCases) {
                const candidate = structuredClone(abyssal);
                candidate.typicalSpeakers[0].filter.selector = selector;
                rejects(`Language rejects ${label}`, () => validateLanguage(candidate) === true);
            }
            const combined = structuredClone(abyssal);
            combined.typicalSpeakers[0].filter.creatureTypes = ['fiend'];
            rejects('Language rejects a tag selector intersected with creatureTypes',
                () => validateLanguage(combined) === true);
            const missingSelectorValue = structuredClone(abyssal);
            delete missingSelectorValue.typicalSpeakers[0].filter.selector.value;
            rejects('Language rejects a tag selector without a value',
                () => validateLanguage(missingSelectorValue) === true);
        });
        check('Druidic and Thieves Cant retain distinct concealed-message procedures', () => {
            const druidic = outputById.get('language:phb:druidic');
            assert.deepEqual(druidic.communicationModes, ['SPOKEN', 'LEFT_MESSAGE']);
            assert.deepEqual(druidic.communicationCapabilities, [{
                kind: 'CONCEALED_MESSAGE',
                channel: 'LEFT_MESSAGE',
                comprehension: { kind: 'REQUIRES_LANGUAGE_KNOWLEDGE' },
                recognition: {
                    kind: 'AUTOMATIC_IF_LANGUAGE_KNOWN',
                    otherwise: {
                        kind: 'CHECK_FOR_PRESENCE',
                        ability: 'wisdom',
                        skill: { targetType: 'skill', id: 'skill:phb:perception' },
                        dc: 15,
                        outcome: 'DETECT_PRESENCE_ONLY'
                    }
                },
                deciphering: { kind: 'MAGIC_REQUIRED_FOR_NON_KNOWERS' }
            }]);
            const thievesCant = phase3Files().map(file => readJson(file))
                .find(entity => entity.id === 'language:phb:thieves-cant');
            assert.deepEqual(thievesCant.communicationModes, ['ORDINARY_CONVERSATION']);
            assert.deepEqual(thievesCant.communicationCapabilities, [{
                kind: 'CONCEALED_MESSAGE',
                channel: 'ORDINARY_CONVERSATION',
                comprehension: { kind: 'REQUIRES_LANGUAGE_KNOWLEDGE' },
                communicationDurationMultiplier: 4
            }]);
            const xphbCant = outputById.get('language:xphb:thieves-cant');
            assert.equal(xphbCant.communicationCapabilities, undefined);
            const validateLanguage = ajv.getSchema('urn:thedndpub:schemas:core-rules:language:v1');
            const invalidDruidic = structuredClone(druidic);
            invalidDruidic.communicationCapabilities[0].recognition.otherwise.outcome = 'DECIPHER_MESSAGE';
            rejects('Language rejects Druidic presence detection as comprehension',
                () => validateLanguage(invalidDruidic) === true);
            const invalidCant = structuredClone(thievesCant);
            delete invalidCant.communicationCapabilities[0].communicationDurationMultiplier;
            rejects('Language rejects PHB Thieves Cant without its four-times duration',
                () => validateLanguage(invalidCant) === true);
            const cantWithDruidicDetection = structuredClone(thievesCant);
            cantWithDruidicDetection.communicationCapabilities[0].recognition =
                druidic.communicationCapabilities[0].recognition;
            rejects('Language rejects Druidic detection mechanics assigned to Thieves Cant',
                () => validateLanguage(cantWithDruidicDetection) === true);
            const cantWithUnknownComprehension = structuredClone(thievesCant);
            cantWithUnknownComprehension.communicationCapabilities[0].comprehension.kind = 'UNCONDITIONAL';
            rejects('Language rejects unconditional Thieves Cant comprehension',
                () => validateLanguage(cantWithUnknownComprehension) === true);
            const druidicWithInventedSpell = structuredClone(druidic);
            druidicWithInventedSpell.communicationCapabilities[0].spell = {
                targetType: 'spell',
                id: 'spell:phb:comprehend-languages'
            };
            rejects('Language rejects an unsupported specific deciphering spell identity',
                () => validateLanguage(druidicWithInventedSpell) === true);
            const xphbWithPhbMechanics = structuredClone(xphbCant);
            xphbWithPhbMechanics.communicationCapabilities = thievesCant.communicationCapabilities;
            rejects('Language source assertion rejects PHB Cant mechanics copied to XPHB', () => {
                try {
                    assert.equal(xphbWithPhbMechanics.communicationCapabilities, undefined);
                    return true;
                } catch {
                    return false;
                }
            });
        });
        check('Loross encodes source-qualified mutual spoken intelligibility', () => {
            const loross = outputById.get('language:idrotf:loross');
            assert.deepEqual(loross.intelligibility, [{
                kind: 'MUTUAL_INTELLIGIBILITY',
                mode: 'SPOKEN',
                directionality: 'MUTUAL',
                language: { targetType: 'language', id: 'language:phb:elvish' }
            }]);
            assert.equal(loross.references, undefined);
            assert.equal(outputById.get('language:psx:coalition-pidgin').intelligibility, undefined);
            const validateLanguage = ajv.getSchema('urn:thedndpub:schemas:core-rules:language:v1');
            const invalidLoross = structuredClone(loross);
            invalidLoross.intelligibility[0].mode = 'WRITTEN';
            rejects('Language rejects inferred written intelligibility',
                () => validateLanguage(invalidLoross) === true);
            const invalidReference = structuredClone(loross);
            invalidReference.intelligibility[0].language.targetType = 'creature';
            rejects('Language schema rejects non-Language intelligibility targets',
                () => validateLanguage(invalidReference) === true);
        });
        check('Language source-entry accounting reflects the refinements', () => {
            const druidic = manifest.records.find(record => record.canonicalId === 'language:phb:druidic');
            assert.deepEqual(druidic.entries[0].classifications.map(item => item.classification), ['INTERPRETED']);
            const loross = manifest.records.find(record => record.canonicalId === 'language:idrotf:loross');
            const writtenTranslation = loross.entries[0].classifications.find(item =>
                item.portion === 'Written translation eligibility and no-check outcome');
            assert.equal(writtenTranslation.classification, 'OMITTED');
            assert.equal(writtenTranslation.cause, 'EXTERNAL_DEPENDENCY');
        });
        const phase3ConditionIds = phase3Files()
            .map(file => readJson(file))
            .filter(entity => entity.id.startsWith('condition:'))
            .map(entity => entity.id);
        const allConditionIds = [...phase3ConditionIds,
            ...[...outputById.keys()].filter(id => id.startsWith('condition:'))];
        assert.equal(new Set(allConditionIds).size, 30);
        assert.deepEqual([...new Set(allConditionIds)].sort(), [...sourceConditionIds].sort());
        assert.deepEqual(
            manifest.records.filter(record => record.model === 'condition')
                .reduce((counts, record) => {
                    counts[record.conversionStatus]++;
                    return counts;
                }, { converted: 0, partial: 0, unsupported: 0 }),
            { converted: 25, partial: 1, unsupported: 0 }
        );
        const diseaseClassifications = manifest.records.filter(record => record.model === 'disease')
            .flatMap(record => record.entries.flatMap(entry => entry.classifications));
        assert.ok(diseaseClassifications.some(item => item.cause === 'SCHEMA_UNSUPPORTED'));
        assert.ok(diseaseClassifications.some(item => item.cause === 'SOURCE_AMBIGUOUS'));
    });

    check('Phase 5 Disease refinements and source-fidelity corrections', () => {
        const diseases = outputById;
        const arcaneBlight = diseases.get('disease:idrotf:arcane-blight');
        assert.equal(arcaneBlight.recovery[0].savingThrow.frequency, 'each-long-rest');
        assert.equal(arcaneBlight.recovery[0].endsWhenDcAtOrBelow, 0);
        assert.equal(arcaneBlight.recovery[0].immunityAfterRecovery, true);
        assert.equal(arcaneBlight.recovery[0].outcomeCounter.reset, 'UNSPECIFIED_BY_SOURCE');
        const dmgCackleFile = phase3Files().find(file =>
            readJson(file).id === 'disease:dmg:cackle-fever');
        const dmgCackle = readJson(dmgCackleFile);
        assert.equal(dmgCackle.recovery[0].outcomeCounter.branchId, 'cackle-fever-recovery');
        assert.equal(dmgCackle.recovery[0].outcomeCounter.countedOutcome, 'FAILURE');
        assert.equal(dmgCackle.recovery[0].outcomeCounter.threshold, 3);
        assert.equal(dmgCackle.recovery[0].outcomeCounter.sequence, 'UNSPECIFIED_BY_SOURCE');
        assert.equal(dmgCackle.recovery[0].outcomeCounter.reset, 'UNSPECIFIED_BY_SOURCE');
        const blueMist = diseases.get('disease:toa:blue-mist-fever');
        assert.deepEqual(blueMist.infection.transmission, ['magical-exposure']);
        assert.equal(blueMist.infection.exposures[0].environment, 'MAGICAL_MIST');
        assert.equal(blueMist.incubation.die, 6);
        assert.equal(blueMist.recovery[0].ends, 'SYMPTOM');
        const bluerot = diseases.get('disease:gos:bluerot');
        assert.equal(bluerot.infection.savingThrow, undefined);
        assert.equal(bluerot.progression[0].savingThrow.dc, 12);
        assert.equal(bluerot.effects.filter(effect => effect.kind === 'ABILITY_SCORE_CHANGE').length, 2);
        const xphbCackle = diseases.get('disease:xdmg:cackle-fever');
        assert.equal(xphbCackle.progression[0].trigger.kind, 'DAMAGE_WHILE_CONDITION_ACTIVE');
        assert.equal(xphbCackle.progression[0].trigger.damageType, 'ANY_EXCEPT_PSYCHIC');
        assert.equal(xphbCackle.infection.exposures[1].sourceState, 'INFECTED');
        assert.equal(xphbCackle.progression[1].outcomeCounter.countedOutcome, 'SUCCESS');
        assert.equal(xphbCackle.progression[1].outcomeCounter.immunityAfterRecovery.duration.unit, 'year');
        assert.equal(xphbCackle.progression[1].outcomeCounter.reset, 'UNSPECIFIED_BY_SOURCE');
        assert.equal(xphbCackle.progression[1].outcomeCounter.threshold, 3);
        assert.equal(xphbCackle.progression[0].trigger.kind, 'DAMAGE_WHILE_CONDITION_ACTIVE');
        assert.equal(xphbCackle.progression[0].trigger.damageType, 'ANY_EXCEPT_PSYCHIC');
        const dmgSightRot = diseases.get('disease:dmg:sight-rot');
        assert.equal(dmgSightRot.recovery.some(item => item.savingThrow), false);
        assert.equal(dmgSightRot.effects[0].threshold, -5);
        assert.equal(dmgSightRot.recovery[0].treatment.material, 'EYEBRIGHT');
        const xphbSightRot = diseases.get('disease:xdmg:sight-rot');
        assert.ok(xphbSightRot.recovery.some(item => item.reference?.id === 'spell:xphb:heal'));
        assert.ok(xphbSightRot.recovery.some(item => item.reference?.id === 'spell:xphb:lesser-restoration'));
        const shaking = diseases.get('disease:fraif:shaking-plague');
        assert.ok(JSON.stringify(shaking).includes('condition:xphb:exhaustion'));
        assert.equal(JSON.stringify(shaking).includes('condition:phb:exhaustion'), false);
        assert.equal(shaking.symptoms[0].cannotReduceUntilDiseaseEnds, undefined);
        assert.equal(shaking.progression[0].endsWhen.condition.id, 'condition:xphb:exhaustion');
        assert.deepEqual(shaking.conditionGatedRestrictions[0].preventedRecoveries,
            ['condition-level-reduction']);
        const rusting = diseases.get('disease:fraif:the-rusting');
        assert.ok(JSON.stringify(rusting).includes('condition:xphb:exhaustion'));
        assert.equal(JSON.stringify(rusting).includes('condition:phb:exhaustion'), false);
        assert.equal(rusting.symptoms[0].cannotReduceUntilDiseaseEnds, undefined);
        assert.deepEqual(rusting.effects.find(effect => effect.kind === 'TRANSFORMATION_OUTCOME').immunities, [
            { kind: 'DAMAGE_TYPE', damageType: 'poison' },
            ...['exhaustion', 'petrified', 'poisoned'].map(name => ({
                kind: 'CONDITION', condition: { targetType: 'condition', id: `condition:xphb:${name}` }
            }))
        ]);
        const saprophytic = diseases.get('disease:cm:saprophytic-plague');
        assert.deepEqual(saprophytic.applicability.targetCreatureTypes, ['beast', 'humanoid']);
        assert.equal(saprophytic.symptoms[0].cannotReduceUntilDiseaseEnds, undefined);
        const mudpox = diseases.get('disease:fraif:mudpox');
        assert.deepEqual(mudpox.applicability.targetCreatureTypes, ['beast', 'humanoid']);
        assert.equal(mudpox.progression[0].outcomeCounter.sequence, 'CONSECUTIVE');
        assert.equal(mudpox.progression[0].trigger.kind, 'DAILY_AT');
        assert.equal(mudpox.progression[0].trigger.time, 'DAWN');
        assert.ok(mudpox.progression[0].onFailure.some(outcome => outcome.kind === 'VOMIT_PREVIOUS_DAY_FOOD'));
        const lichen = diseases.get('disease:fraif:lichen-plague');
        assert.equal(lichen.progression[0].outcomeCounter.sequence, 'NOT_REQUIRED');
        assert.deepEqual(lichen.progression[0].outcomeCounter.immunityAfterRecovery, {
            kind: 'DISEASE_IMMUNITY', scope: 'DISEASE', permanent: true
        });
        const frigidWoe = diseases.get('disease:egw:frigid-woe');
        assert.equal(frigidWoe.progression[0].trigger.kind, 'REPEATING_ELAPSED_TIME');
        assert.equal(frigidWoe.effects.find(effect => effect.kind === 'TRANSFORMATION_OUTCOME').trigger,
                'SPEED_REACHES_ZERO');
        const dmgSewer = diseases.get('disease:dmg:sewer-plague');
        assert.equal(dmgSewer.progression[0].trigger.kind, 'LONG_REST_COMPLETED');
        assert.equal(dmgSewer.progression[0].onFailure[0].condition.id, 'condition:phb:exhaustion');
        assert.equal(dmgSewer.progression[0].onSuccess[0].condition.id, 'condition:phb:exhaustion');
        const redface = diseases.get('disease:gos:redface');
        assert.equal(redface.effectProfile.sourceDisease.id, 'disease:dmg:sight-rot');
        assert.deepEqual(redface.effectProfile.components, ['symptoms', 'progression', 'recovery']);
        const grackle = diseases.get('disease:oota:grackle-lung');
        assert.equal(grackle.progression[0].trigger.kind, 'LONG_REST_COMPLETED');
        assert.equal(grackle.effects.filter(effect => effect.kind === 'CONDITION_GATED_CHECK').length, 2);
        assert.deepEqual(grackle.recovery[0].removesConditionLevels[0], {
            condition: { targetType: 'condition', id: 'condition:phb:exhaustion' },
            scope: 'LEVELS_ATTRIBUTABLE_TO_THIS_DISEASE'
        });
        assert.equal(grackle.effects.find(effect => effect.kind === 'CONDITION_THRESHOLD_OUTCOME')
            .outcome.immunity.duration.unit, 'week');
        const ghoulGut = diseases.get('disease:oow:ghoul-gut');
        assert.equal(ghoulGut.recovery.some(item => item.method === 'saving-throw'), false);
        assert.equal(ghoulGut.progression.filter(item => item.branchId === 'long-rest-recovery').length, 1);
        assert.deepEqual(ghoulGut.recovery[0], {
            method: 'skill-check', reference: { targetType: 'skill', id: 'skill:phb:medicine' },
            ability: 'wisdom', dc: 15, trigger: 'LONG_REST_CARE', performedBy: 'ANOTHER_CREATURE',
            outcome: 'ENDS_DISEASE', caregiverLongRestOutcome: 'NO_LONG_REST_BENEFIT'
        });
        const shivering = diseases.get('disease:toa:shivering-sickness');
        assert.ok(shivering.effects.some(effect => effect.kind === 'DISEASE_RESTRICTION'
            && effect.restriction === 'HALF_HP_FROM_HIT_DICE'
            && effect.lifecycle === 'AFTER_SYMPTOM_ONSET'));
        assert.ok(shivering.effects.some(effect => effect.kind === 'DISEASE_RESTRICTION'
            && effect.restriction === 'NO_HIT_POINTS_FROM_LONG_REST'
            && effect.lifecycle === 'AFTER_SYMPTOM_ONSET'));
        assert.equal(shivering.effects.some(effect => effect.restriction === 'NO_NONMAGICAL_HP_OR_HIT_DICE_RECOVERY'),
            false);
        const gnawing = diseases.get('disease:vrgr:the-gnawing-plague');
        assert.deepEqual(gnawing.infection.exposures[0].sourceCreatures,
            ['RAT', 'GIANT_RAT', 'SWARM_OF_RATS', 'WERERAT']);
        assert.equal(gnawing.infection.exposures[0].savingThrow.dcVariation, 'REGIONAL_PLAGUE_SEVERITY');
        assert.equal(saprophytic.applicability.automaticSuccessForDiseaseImmuneTargets, true);
        assert.ok(saprophytic.effects.filter(effect => effect.beginsAfterInfection)
            .every(effect => effect.beginsAfterInfection.amount === 24
                && effect.beginsAfterInfection.unit === 'hour'));
        assert.ok(saprophytic.infection.exposures.some(exposure =>
            exposure.kind === 'INGEST_CONTAMINATED_MATERIAL'
            && exposure.contaminationAfter?.count === 2
            && exposure.contaminationAfter?.die === 12));
        assert.throws(() => assertDiseaseIntegrity({
            ...dmgCackle,
            recovery: [{
                ...dmgCackle.recovery[0],
                outcomeCounter: { ...dmgCackle.recovery[0].outcomeCounter, reset: 'NEVER' }
            }]
        }), /unspecified recovery-counter sequence/);
        const unsupported = manifest.records.filter(record => record.model === 'disease'
            && record.disposition === 'unsupported');
        assert.deepEqual(unsupported.map(record => record.canonicalId), ['disease:tftyp:super-tetanus']);
        assert.ok(diseases.has('disease:oota:grackle-lung'));
        assert.equal(diseases.has('disease:tftyp:super-tetanus'), false);
        const phase3DiseaseManifest = readJson(path.join(workspace,
            'reports/step-2/CORE_RULES_PHASE_3_SOURCE_ACCOUNTING.json'));
        const allDiseaseAccounting = [...phase3DiseaseManifest.records.filter(record => record.model === 'disease'),
            ...manifest.records.filter(record => record.model === 'disease')];
        assert.equal(allDiseaseAccounting.length, 29);
        assert.equal(new Set(allDiseaseAccounting.map(record => record.canonicalId)).size, 29);
        const petrified = readJson(path.join(phase5Root, 'condition/phb-petrified.json'));
        assert.ok(petrified.effects.some(effect => effect.kind === 'CATEGORY_IMMUNITY'
            && effect.category === 'poison-and-disease'));
        assert.deepEqual(petrified.effects.find(effect => effect.kind === 'SUSPEND_ACTIVE_DISEASES'), {
            kind: 'SUSPEND_ACTIVE_DISEASES',
            lifecycle: 'while-condition-active',
            preservesInstance: true,
            scheduling: 'UNRESOLVED_BY_SOURCE'
        });
    });

    check('Phase 5 Condition effects preserve supported actor and target direction', () => {
        const deafened = outputById.get('condition:phb:deafened');
        assert.deepEqual(deafened.effects.map(effect => effect.kind), ['REMOVE_SENSE', 'AUTOMATIC_CHECK_FAILURE']);
        assert.equal(deafened.effects[0].sense, 'hearing');
        assert.equal(deafened.effects[1].requirement, 'requires-hearing');
        const grappled = outputById.get('condition:xphb:grappled');
        assert.equal(grappled.effects[0].movement, 'speed-flat');
        assert.equal(grappled.effects[0].amount, 0);
        const paralyzed = outputById.get('condition:xphb:paralyzed');
        assert.ok(paralyzed.effects.some(effect => effect.kind === 'ATTACK_ROLL_MODIFIER'
            && effect.direction === 'advantage'
            && effect.actor === 'attacker'
            && effect.target === 'affected-creature'));
        const restrained = outputById.get('condition:xphb:restrained');
        assert.ok(restrained.effects.some(effect => effect.kind === 'ATTACK_ROLL_MODIFIER'
            && effect.direction === 'disadvantage' && effect.actor === 'affected-creature'));
        assert.ok(restrained.effects.some(effect => effect.kind === 'CHECK_ROLL_MODIFIER'
            && effect.roll === 'saving-throw' && effect.ability === 'dexterity'));
        assert.ok(restrained.effects.some(effect => effect.kind === 'ATTACK_ROLL_MODIFIER'
            && effect.direction === 'disadvantage' && effect.actor === 'affected-creature'
            && effect.target === 'target'));
        const charmedPhb = outputById.get('condition:phb:charmed');
        const charmedXphb = outputById.get('condition:xphb:charmed');
        assert.ok(charmedPhb.effects.some(effect => effect.kind === 'RELATIONAL_RESTRICTION'
            && effect.prohibitedOperation === 'harmful-ability-or-magical-effect'));
        assert.ok(charmedXphb.effects.some(effect => effect.kind === 'RELATIONAL_RESTRICTION'
            && effect.prohibitedOperation === 'damaging-ability-or-magical-effect'));
        const frightened = outputById.get('condition:phb:frightened');
        assert.ok(frightened.effects.some(effect => effect.kind === 'CHECK_ROLL_MODIFIER'
            && effect.applicability?.kind === 'FEAR_SOURCE_IN_LINE_OF_SIGHT'));
        assert.ok(frightened.effects.some(effect => effect.kind === 'MOVEMENT_CONSTRAINT'
            && effect.constraint === 'cannot-willingly-move-closer-to-source'));
        assert.deepEqual(outputById.get('condition:phb:incapacitated').effects[0].activities,
            ['action', 'reaction']);
        assert.deepEqual(outputById.get('condition:xphb:incapacitated').effects[0].activities,
            ['action', 'bonus-action', 'reaction', 'speech']);
        const grappledXphb = outputById.get('condition:xphb:grappled');
        assert.deepEqual(grappledXphb.effects.find(effect => effect.kind === 'GRAPPLED_MOVEMENT_COST').movementCost,
            { kind: 'EXTRA_FEET_PER_FOOT', additionalFeetPerFoot: 1 });
        assert.equal(grappledXphb.effects.find(effect => effect.kind === 'GRAPPLED_MOVEMENT_COST').movementMode,
            'drag-or-carry');
        assert.equal(grappledXphb.effects.find(effect => effect.kind === 'GRAPPLED_MOVEMENT_COST').waiver,
            'target-tiny-or-two-or-more-sizes-smaller');
        assert.ok(grappledXphb.effects.some(effect => effect.kind === 'ATTACK_ROLL_MODIFIER'
            && effect.applicability?.kind === 'TARGET_IS_NOT_CREATURE'
            && effect.applicability.creature === 'grappler'));
        for (const id of ['condition:phb:invisible', 'condition:xphb:invisible']) {
            const invisible = outputById.get(id);
            assert.ok(invisible.effects.filter(effect => effect.kind === 'ATTACK_ROLL_MODIFIER')
                .every(effect => effect.applicability?.kind === 'OBSERVER_CANNOT_SEE_AFFECTED'));
        }
        for (const id of ['condition:phb:paralyzed', 'condition:xphb:paralyzed',
            'condition:phb:unconscious', 'condition:xphb:unconscious']) {
            assert.ok(outputById.get(id).effects.some(effect => effect.kind === 'CRITICAL_HIT_ON_HIT'
                && effect.requiresSuccessfulHit === true && effect.distanceFeet === 5));
        }
        const criticalRule = readJson(path.join(phase3Root, 'rule/xphb-critical-hit.json'));
        assert.equal(criticalRule.mechanics.find(mechanic => mechanic.subtype === 'DAMAGE')
            .damageModifier.appliesWhen.kind, 'ON_CRITICAL_HIT');
        for (const id of ['condition:xphb:paralyzed', 'condition:xphb:unconscious']) {
            assert.ok(outputById.get(id).effects.some(effect => effect.kind === 'CRITICAL_HIT_ON_HIT'
                && effect.rule?.id === criticalRule.id));
        }
        const prone = outputById.get('condition:xphb:prone');
        assert.ok(prone.effects.some(effect => effect.kind === 'MOVEMENT_CONSTRAINT'
            && effect.standingCost?.speedFraction === 0.5 && effect.standingCost.rounding === 'down'));
        const petrifiedPhb = outputById.get('condition:phb:petrified');
        assert.ok(petrifiedPhb.effects.some(effect => effect.kind === 'DAMAGE_MITIGATION'
            && effect.damageTypes === 'all' && effect.mitigation === 'resistance'));
        assert.ok(petrifiedPhb.effects.some(effect => effect.kind === 'CATEGORY_IMMUNITY'
            && effect.category === 'poison-and-disease'));
        assert.ok(petrifiedPhb.effects.some(effect => effect.kind === 'SUSPEND_ACTIVE_DISEASES'
            && effect.preservesInstance === true
            && effect.scheduling === 'UNRESOLVED_BY_SOURCE'));
        const petrifiedXphb = outputById.get('condition:xphb:petrified');
        assert.ok(petrifiedXphb.effects.some(effect => effect.kind === 'PHYSICAL_STATE'
            && effect.includesWornAndCarriedNonmagicalObjects === true));
        assert.ok(petrifiedXphb.effects.some(effect => effect.kind === 'CONDITION_INTERACTION'
            && effect.condition.id === 'condition:xphb:poisoned' && effect.interaction === 'immune-to'));
        assert.equal(petrifiedXphb.effects.some(effect => effect.kind === 'AWARENESS_RESTRICTION'), false);
        assert.ok(outputById.get('condition:xphb:unconscious').effects.some(effect =>
            effect.kind === 'CONDITION_INTERACTION'
            && effect.condition.id === 'condition:xphb:prone'
            && effect.lifecycle === 'persists-after-source-condition-ends'));
        assert.ok(outputById.get('condition:xphb:invisible').effects.some(effect =>
            effect.kind === 'RELATIONAL_RESTRICTION'
            && effect.appliesWhen.kind === 'OBSERVER_CANNOT_SEE_AFFECTED'));
        assert.equal([...outputById.keys()].filter(id => id.startsWith('condition:')).length, 26);
    });

    check('Phase 5 Sense capabilities preserve source-supported distinctions', () => {
        const senses = readJson(path.join(workspace, 'src/main/resources/data/senses.json')).sense;
        const blindsight = outputById.get('sense:xphb:blindsight');
        assert.ok(senses[1].entries[0].includes('isn\'t behind'));
        assert.ok(senses[1].entries[0].includes('Invisible'));
        assert.ok(blindsight.capabilities.some(capability => capability.kind === 'CONDITION_INTERACTION'
            && capability.interaction.id === 'condition:xphb:blinded'
            && capability.effect === 'bypasses-sight-restriction'));
        assert.ok(blindsight.capabilities[0].perceives.includes('invisible-objects'));
        const darkvision = outputById.get('sense:xphb:darkvision').capabilities[0];
        assert.deepEqual([darkvision.dimLightAs, darkvision.darknessAs, darkvision.colorPerception],
            ['bright-light', 'dim-light', 'grayscale']);
        const truesight = outputById.get('sense:xphb:truesight').capabilities[0];
        assert.ok(truesight.perceives.includes('invisible-objects'));
        assert.deepEqual(truesight.illusionInteraction, {
            appearance: 'transparent', detection: 'automatic', savingThrowOutcome: 'automatic-success'
        });
        assert.equal(truesight.darknessAs, undefined);
    });

    check('Phase 5 Action movement, equipment, and voluntary termination patterns', () => {
        const dash = outputById.get('action:phb:dash').operations[0];
        assert.deepEqual([dash.kind, dash.movement, dash.amount, dash.unit],
            ['MOVEMENT_INCREASE', 'current-turn-movement', 1, 'current-speed']);
        const disengage = outputById.get('action:xphb:disengage').operations[0];
        assert.deepEqual([disengage.kind, disengage.permission, disengage.duration],
            ['MOVEMENT_PERMISSION', 'no-opportunity-attack', 'current-turn']);
        assert.deepEqual(outputById.get('action:phb:don-or-doff-a-shield').operations.map(item => item.operation),
            ['don-shield', 'doff-shield']);
        for (const id of ['action:phb:end-concentration', 'action:xphb:end-concentration']) {
            assert.deepEqual(outputById.get(id).timing, { kind: 'free' });
            assert.ok(outputById.get(id).capabilities.includes('END_CONCENTRATION'));
        }
        assert.ok(outputById.get('action:xphb:dash').operations.length > 0);
    });

    check('Phase 5 Action catalog expansion preserves new source-backed patterns', () => {
        const phbDodge = outputById.get('action:phb:dodge');
        assert.deepEqual(phbDodge.operations[0].when, {
            kind: 'CAN_SEE', actor: 'affected-creature', target: 'attacker'
        });
        assert.equal(phbDodge.operations[0].modifier.direction, 'disadvantage');
        assert.equal(phbDodge.operations[1].modifier.direction, 'advantage');

        const xphbAttack = outputById.get('action:xphb:attack');
        assert.equal(xphbAttack.operations.length, 1,
            'one ATTACK_RESOLUTION operation represents the source-defined single attack');
        assert.deepEqual(xphbAttack.operations[0].attack.forms,
            ['melee', 'ranged', 'unarmed', 'weapon']);
        assert.ok(xphbAttack.references.some(reference =>
            reference.id === 'action:xphb:two-weapon-fighting'));

        for (const id of ['action:phb:escape-a-grapple', 'action:xphb:escape-a-grapple']) {
            assert.ok(outputById.get(id).operations.some(operation => operation.kind === 'END_CONDITION'));
        }
        assert.equal(outputById.get('action:xphb:escape-a-grapple').operations[0].dcSource.id,
            'rule:xphb:unarmed-strike');

        const hideCheck = outputById.get('action:xphb:hide').operations[0];
        assert.deepEqual([hideCheck.abilities[0], hideCheck.dc, hideCheck.skill.id],
            ['dexterity', 15, 'skill:xphb:stealth']);
        const identifySpell = outputById.get('action:xge:identify-a-spell');
        assert.deepEqual(identifySpell.timing.options.map(option => option.kind), ['action', 'reaction']);
        assert.equal(identifySpell.timing.options[1].trigger.kind, 'SPELL_CASTING_OBSERVED');

        const magic = outputById.get('action:xphb:magic').operations[0];
        assert.deepEqual(magic.alternatives, ['cast-spell', 'activate-feature', 'activate-magic-item']);
        assert.equal(magic.longCasting.concentrationRequired, true);
        assert.equal(magic.longCasting.spellSlotExpendedOnFailure, false);

        for (const id of ['action:phb:opportunity-attack', 'action:xphb:opportunity-attack']) {
            assert.equal(outputById.get(id).timing.kind, 'reaction');
            assert.equal(outputById.get(id).timing.trigger.kind, 'CREATURE_LEAVES_REACH');
        }
        const phbOpportunity = outputById.get('action:phb:opportunity-attack').timing.trigger;
        const xphbOpportunity = outputById.get('action:xphb:opportunity-attack').timing.trigger;
        assert.equal(phbOpportunity.requiresHostility, true);
        assert.deepEqual(phbOpportunity.excludedMovementSources,
            ['disengage', 'teleportation', 'involuntary-movement']);
        assert.deepEqual(xphbOpportunity.movementOrigins, ['action', 'bonus-action', 'reaction', 'speed']);
        assert.equal(phbOpportunity.resolutionTiming, 'immediately-before-departure');
        assert.equal(xphbOpportunity.resolutionTiming, 'immediately-before-departure');

        const overrun = outputById.get('action:dmg:overrun');
        const overrunCheck = overrun.operations.find(operation => operation.operationId === 'overrun-contest');
        const overrunMovement = overrun.operations.find(operation => operation.kind === 'TRAVERSE_CREATURE_SPACE');
        const tumble = outputById.get('action:dmg:tumble');
        const tumbleCheck = tumble.operations.find(operation => operation.operationId === 'tumble-contest');
        assert.deepEqual(overrun.timing.options.map(option => option.kind), ['action', 'bonus-action']);
        assert.deepEqual([overrunCheck.abilities[0], overrunCheck.skill.id,
            overrunCheck.opposingSkills[0].id], ['strength', 'skill:phb:athletics', 'skill:phb:athletics']);
        assert.equal(overrunCheck.relativeSizeModifier,
            'ADVANTAGE_IF_ACTOR_LARGER_DISADVANTAGE_IF_SMALLER');
        assert.deepEqual(tumbleCheck.abilities, ['dexterity']);
        assert.equal(tumbleCheck.skill.id, 'skill:phb:acrobatics');
        assert.equal(overrunMovement.checkId, 'overrun-contest');
        assert.equal(overrunMovement.uses, 'ONCE_THIS_TURN');
        assert.deepEqual(overrunMovement.appliesWhen,
            { kind: 'CHECK_SUCCEEDED', operationId: 'overrun-contest' });
        assert.equal(tumble.operations.find(operation => operation.kind === 'TRAVERSE_CREATURE_SPACE').uses,
            'ONCE_THIS_TURN');
        assert.deepEqual(tumble.operations.find(operation => operation.kind === 'TRAVERSE_CREATURE_SPACE').appliesWhen,
            { kind: 'CHECK_SUCCEEDED', operationId: 'tumble-contest' });

        const climb = outputById.get('action:dmg:climb-onto-a-bigger-creature');
        const enterSpace = climb.operations.find(operation => operation.kind === 'ENTER_CREATURE_SPACE');
        assert.equal(enterSpace.entryCheckId, 'climb-entry-contest');
        assert.deepEqual(enterSpace.appliesWhen,
            { kind: 'CHECK_SUCCEEDED', operationId: 'climb-entry-contest' });
        assert.equal(enterSpace.movesWithTarget, true);
        assert.equal(enterSpace.movementCost, 'TARGET_SPACE_IS_DIFFICULT_TERRAIN');
        assert.equal(enterSpace.attackModifier, 'ADVANTAGE_AGAINST_TARGET');
        assert.equal(enterSpace.dislodgingProcedure.actionCost, 'ACTION');
        assert.equal(enterSpace.positioning, 'DM_ADJUDICATED');

        const phbDash = outputById.get('action:phb:dash').operations[0];
        const xphbDash = outputById.get('action:xphb:dash').operations[0];
        assert.equal(phbDash.speedSelection, undefined);
        assert.equal(xphbDash.speedSelection, 'CHOOSE_SPECIAL_SPEED_FOR_EACH_USE');

        for (const id of ['action:phb:use-an-object', 'action:xphb:utilize']) {
            assert.equal(outputById.get(id).operations[0].kind, 'OBJECT_USE');
            assert.equal(outputById.get(id).operations[0].activationRequirement, 'ACTION_REQUIRED_BY_OBJECT');
        }
        const itemActivation = outputById.get('action:dmg:activate-an-item');
        assert.equal(itemActivation.operations[0].kind, 'OBJECT_OPERATION');
        assert.equal(itemActivation.operations[0].operation, 'activate-magic-item');
        assert.equal(itemActivation.operations[0].activation, 'item-defined');
        assert.ok(itemActivation.references.some(reference =>
            reference.id === 'action:phb:use-an-object'
            && reference.relationship === 'distinct-from-item-activation'));
        assert.equal(outputById.get('action:phb:use-an-object').operations[0]
            .additionalObjectInteraction, 'ACTION_AFTER_ONE_FREE_INTERACTION');
        assert.equal(outputById.get('action:xphb:utilize').operations[0].additionalObjectInteraction, undefined);
        const ordinaryInteraction = outputById.get('action:phb:other-activity').operations[0];
        assert.equal(ordinaryInteraction.operation, 'ordinary-interaction');
        assert.deepEqual(ordinaryInteraction.interactionAllowance, {
            kind: 'ONE_FREE_INTERACTION_PER_TURN',
            placement: 'DURING_MOVEMENT_OR_ACTION',
            additionalInteractionCost: 'ACTION'
        });

        for (const id of ['action:phb:help', 'action:xphb:help']) {
            const help = outputById.get(id);
            assert.deepEqual(help.choiceGroups[0].cardinality, { minimumSelected: 1, maximumSelected: 1 });
            assert.deepEqual(help.choiceGroups[0].members.map(member => member.operationId),
                ['help-task-check', 'help-attack']);
            assert.ok(help.operations.every(operation => operation.appliesWhen.choiceGroupId === 'help-mode'));
        }
        assert.equal(outputById.get('action:xphb:help').operations[0].feasibilityDecision, 'DM_DETERMINED');

        const hide = outputById.get('action:xphb:hide');
        const hideProcedure = hide.operations.find(operation => operation.kind === 'HIDE_PROCEDURE');
        assert.equal(hideProcedure.outOfEnemyLineOfSight, true);
        assert.deepEqual(hideProcedure.concealmentOptions, ['HEAVILY_OBSCURED', 'THREE_QUARTERS_COVER', 'TOTAL_COVER']);
        assert.equal(hideProcedure.condition.id, 'condition:xphb:invisible');
        assert.equal(hideProcedure.discoveryDc.kind, 'HIDE_CHECK_TOTAL');
        assert.equal(hideProcedure.endsWhen.length, 4);

        const influence = outputById.get('action:xphb:influence').operations[0];
        assert.deepEqual(influence.branches.map(branch => branch.willingness).sort(),
            ['HESITANT', 'UNWILLING', 'WILLING']);
        assert.equal(influence.branches.find(branch => branch.willingness === 'HESITANT').dc.kind,
            'GREATER_OF_FIXED_AND_TARGET_ABILITY_SCORE');
        const hesitantInfluence = influence.branches.find(branch => branch.willingness === 'HESITANT');
        assert.deepEqual([hesitantInfluence.retryAfterHours, hesitantInfluence.retryAlternative],
            [24, 'DM_DETERMINED_DURATION']);
        const identify = outputById.get('action:xge:identify-a-spell');
        assert.equal(identify.operations[0].dc.kind, 'FIXED_PLUS_SPELL_LEVEL');
        assert.deepEqual(identify.operations[1].modes.map(mode => mode.timing),
            ['REACTION_WHILE_CASTING', 'ACTION_AFTER_CASTING']);
        assert.equal(identify.operations[1].checkAbility, 'intelligence');

        const phbAttack = readJson(path.join(phase3Root, 'action/phb-attack.json'));
        assert.equal(phbAttack.operations.length, 1);
        const phase3Accounting = readJson(path.join(workspace,
            'reports/step-2/CORE_RULES_PHASE_3_SOURCE_ACCOUNTING.json'));
        const attackEntry = phase3Accounting.records.find(record => record.canonicalId === 'action:phb:attack')
            .entries[1].classifications;
        assert.ok(attackEntry.some(item => item.classification === 'INTERPRETED'
            && item.portion === 'Exactly one melee or ranged attack'));
        assert.ok(attackEntry.some(item => item.classification === 'OMITTED'
            && item.cause === 'EXTERNAL_DEPENDENCY'
            && item.portion === 'The separately defined Making an Attack procedure'));

        const disarm = outputById.get('action:dmg:disarm').operations[0];
        assert.equal(disarm.kind, 'ATTACK_CONTESTED_BY_CHECK');
        assert.equal(disarm.attackDamageOnSuccess, false);
        assert.equal(disarm.successOutcome, 'TARGET_DROPS_HELD_ITEM');
        assert.equal(disarm.defenderOptions[0].skill.id, 'skill:phb:athletics');
        assert.equal(disarm.defenderOptions[1].skill.id, 'skill:phb:acrobatics');
        assert.equal(outputById.get('action:dmg:shove-aside').operations
            .find(operation => operation.kind === 'MOVE_TARGET_TO_SPACE_WITHIN_REACH').distance, 5);
        const searchGuidance = outputById.get('action:xphb:search').skillTaskGuidance;
        assert.deepEqual(searchGuidance.map(row => [row.skill.id, row.tasks]), [
            ['skill:xphb:insight', ['CREATURE_STATE_OF_MIND']],
            ['skill:xphb:medicine', ['AILMENT_OR_CAUSE_OF_DEATH']],
            ['skill:xphb:perception', ['CONCEALED_CREATURE_OR_OBJECT']],
            ['skill:xphb:survival', ['TRACKS_OR_FOOD']]
        ]);
        const studyGuidance = outputById.get('action:xphb:study').skillTaskGuidance;
        assert.equal(studyGuidance.length, 5);
        assert.deepEqual(studyGuidance.map(row => row.skill.id), [
            'skill:xphb:arcana', 'skill:xphb:history', 'skill:xphb:investigation',
            'skill:xphb:nature', 'skill:xphb:religion'
        ]);
        assert.deepEqual(studyGuidance[0].creatureGroups,
            ['ABERRATIONS', 'CONSTRUCTS', 'ELEMENTALS', 'FEY', 'MONSTROSITIES']);
        const influenceSkills = influence.branches.find(branch => branch.willingness === 'HESITANT')
            .checkOptions.map(option => option.skill.id);
        assert.deepEqual(influenceSkills, [
            'skill:xphb:deception', 'skill:xphb:intimidation', 'skill:xphb:performance',
            'skill:xphb:persuasion', 'skill:xphb:animal-handling'
        ]);

        const stabilize = outputById.get('action:phb:stabilize-a-creature');
        assert.equal(stabilize.operations[0].dc, 10);
        assert.ok(stabilize.operations.some(operation => operation.operation === 'stabilize-creature'));
        assert.ok(outputById.get('action:xphb:help').capabilities.includes('STABILIZE'));

        for (const id of ['action:phb:two-weapon-fighting', 'action:xphb:two-weapon-fighting']) {
            assert.equal(outputById.get(id).timing.kind, 'bonus-action');
            assert.ok(outputById.get(id).operations.some(operation => operation.kind === 'ATTACK_RESOLUTION'));
        }
        assert.deepEqual(
            manifest.records.filter(record => record.model === 'action')
                .reduce((counts, record) => {
                    counts[record.conversionStatus ?? record.disposition]++;
                    return counts;
                }, {converted: 0, partial: 0, unsupported: 0}),
            {converted: 27, partial: 13, unsupported: 2}
        );
    });

    return { files: files.length, entities };
}

function testNegativeCases(canonicalFixture, allCanonicalRecords) {
    const get = name => canonicalFixture(name);
    const actionValidator = ajv.getSchema('urn:thedndpub:schemas:core-rules:action:v1');
    const ruleValidator = ajv.getSchema('urn:thedndpub:schemas:core-rules:rule:v1');
    const diseaseValidator = ajv.getSchema('urn:thedndpub:schemas:core-rules:disease:v1');
    const conditionValidator = ajv.getSchema('urn:thedndpub:schemas:core-rules:condition:v1');

    const referenceOnlyRule = get('rule-reference-only.json');
    check('reference-only Rule is valid, source-backed, and mechanically empty', () => {
        assert.equal(ruleValidator(referenceOnlyRule), true, JSON.stringify(ruleValidator.errors));
        assert.equal(Object.hasOwn(referenceOnlyRule, 'mechanics'), false);
        const sourceRecord = sourceRecordForCanonical(referenceOnlyRule);
        assert.equal(referenceOnlyRule.id,
            `rule:${canonicalSlug(sourceRecord.source)}:${canonicalSlug(sourceRecord.name)}`);
        assert.deepEqual(referenceOnlyRule.sourceMetadata, expectedSourceMetadata('rule', sourceRecord));
        assert.equal(referenceOnlyRule.ruleType, 'CORE');
        assertRuleIntegrity(referenceOnlyRule);
        assertCanonicalReferences(referenceOnlyRule);

        const containsReference = value => {
            if (Array.isArray(value)) return value.some(containsReference);
            if (!value || typeof value !== 'object') return false;
            if (value.targetType === 'rule' && value.id === referenceOnlyRule.id) return true;
            return Object.values(value).some(containsReference);
        };
        const referringRecord = allCanonicalRecords.find(containsReference);
        assert.ok(referringRecord);
        assertCanonicalReferences(referringRecord);
    });

    const referenceOnlyWithoutId = get('rule-reference-only.json');
    delete referenceOnlyWithoutId.id;
    rejects('reference-only Rule still requires canonical identity', () => ruleValidator(referenceOnlyWithoutId));

    const referenceOnlyWithoutSource = get('rule-reference-only.json');
    delete referenceOnlyWithoutSource.sourceMetadata;
    rejects('reference-only Rule still requires source metadata', () => ruleValidator(referenceOnlyWithoutSource));

    const invalidAbilityMapping = get('rule-ability-score-and-modifier.json');
    invalidAbilityMapping.mechanics[0].scoreBands[0].modifier = -4;
    rejects('ability modifier integrity rejects an incorrect boundary value', () => {
        assertRuleIntegrity(invalidAbilityMapping);
        return true;
    });

    const invalidDcFormula = get('rule-ability-check.json');
    invalidDcFormula.mechanics[0].dc = {
        kind: 'ARBITRARY_EXPRESSION',
        expression: '8 + target score'
    };
    rejects('Rule check rejects an unsupported Difficulty Class expression', () =>
        ruleValidator(invalidDcFormula));

    const invalidProficiencyDie = get('rule-proficiency-dice.json');
    invalidProficiencyDie.mechanics[0].levelBands[0].die = 'd20';
    rejects('Proficiency Dice rejects an unobserved die size', () =>
        ruleValidator(invalidProficiencyDie));

    const invalidProficiencyProgression = get('rule-proficiency.json');
    invalidProficiencyProgression.mechanics[1].levelBands[0].maximumLevel = 5;
    rejects('proficiency progression rejects a source-inconsistent level boundary', () => {
        assertRuleIntegrity(invalidProficiencyProgression);
        return true;
    });

    const invalidPassiveAdjustment = get('rule-passive-perception.json');
    invalidPassiveAdjustment.mechanics[0].passiveScoreConstruction.disadvantageAdjustment = -4;
    rejects('passive score rejects an unsupported numeric adjustment', () =>
        ruleValidator(invalidPassiveAdjustment));

    const invalidExpertise = get('rule-expertise.json');
    invalidExpertise.mechanics[0].maximumInstancesPerSkill = 2;
    rejects('Expertise rejects repeated assignment to the same skill proficiency', () =>
        ruleValidator(invalidExpertise));

    const ineligibleDeathSave = get('rule-death-saving-throw.json');
    ineligibleDeathSave.mechanics[0].appliesTo = 'CREATURE';
    rejects('Death Saving Throw rejects inferred non-player-character eligibility', () =>
        ruleValidator(ineligibleDeathSave));

    const incorrectInitiative = get('rule-initiative-score.json');
    incorrectInitiative.mechanics[0].advantageAdjustment = 4;
    rejects('Initiative semantic assertion rejects an incorrect Advantage score adjustment', () => {
        assertRuleIntegrity(incorrectInitiative);
        return true;
    });

    const incorrectCapacity = get('rule-carrying-capacity.json');
    incorrectCapacity.mechanics[0].sizeMultipliers[0].carryMultiplier = 8;
    rejects('Carrying capacity assertion rejects an altered Tiny-size multiplier', () => {
        assertRuleIntegrity(incorrectCapacity);
        return true;
    });

    const incorrectEncumbrance = get('rule-carrying-capacity.json');
    incorrectEncumbrance.id = 'rule:phb:encumbrance';
    incorrectEncumbrance.name = 'Encumbrance';
    incorrectEncumbrance.sourceMetadata = { source: 'PHB', page: 176 };
    incorrectEncumbrance.ruleType = 'VARIANT';
    incorrectEncumbrance.mechanics = [{
        componentId: 'encumbrance',
        kind: 'CARRYING_CAPACITY_RULE',
        subtype: 'OPTIONAL_ENCUMBRANCE',
        weightUnit: 'POUND',
        strengthBasis: 'STRENGTH_SCORE',
        thresholdComparison: 'GREATER_THAN',
        heavyEncumbranceMaximum: 'STANDARD_CARRYING_CAPACITY',
        encumbranceThresholds: [
            {weightMultiplier: 5, state: 'ENCUMBERED', speedReductionFeet: 10,
                abilityCheckDisadvantage: [], attackDisadvantage: [], savingThrowDisadvantage: []},
            {weightMultiplier: 10, state: 'HEAVILY_ENCUMBERED', speedReductionFeet: 21,
                abilityCheckDisadvantage: ['strength', 'dexterity', 'constitution'],
                attackDisadvantage: ['strength', 'dexterity', 'constitution'],
                savingThrowDisadvantage: ['strength', 'dexterity', 'constitution']}
        ]
    }];
    rejects('Encumbrance assertion rejects an incorrect heavy threshold penalty', () => {
        assertRuleIntegrity(incorrectEncumbrance);
        return true;
    });

    const incorrectResistance = get('rule-resistance.json');
    incorrectResistance.mechanics[0].resolutionModifier.rounding = 'UP';
    rejects('Resistance schema rejects rounding up', () => ruleValidator(incorrectResistance));

    const incorrectRest = get('rule-long-rest.json');
    incorrectRest.mechanics[0].minimumHoursBetweenStarts = 8;
    rejects('Long Rest assertion rejects an incorrect interval between starts', () => {
        assertRuleIntegrity(incorrectRest);
        return true;
    });

    const incorrectRecoveryRounding = get('rule-rest-recovery.json');
    incorrectRecoveryRounding.mechanics[0].recoveryOutcomes[0].rounding = 'UP';
    rejects('Rest recovery schema rejects rounding up for fractional Hit Dice', () =>
        ruleValidator(incorrectRecoveryRounding));

    const incorrectFlanking = get('rule-flanking.json');
    incorrectFlanking.mechanics[0].hexGrid.targetSizeSeparations[1].hexesBetween = 3;
    rejects('Flanking rejects a source-inconsistent hex separation', () => {
        assertRuleIntegrity(incorrectFlanking);
        return true;
    });

    const invalidCoverBonus = get('rule-cover.json');
    invalidCoverBonus.mechanics[0].armorClassBonus = 3;
    rejects('Cover rejects a non-source half-cover bonus', () => ruleValidator(invalidCoverBonus));

    const totalCoverWithBonus = get('rule-cover.json');
    totalCoverWithBonus.mechanics[2].armorClassBonus = 5;
    rejects('Total Cover rejects an inapplicable numerical bonus', () => ruleValidator(totalCoverWithBonus));

    const coverWithDamageReduction = get('rule-cover.json');
    coverWithDamageReduction.mechanics[0].damageReduction = 0.5;
    rejects('Cover rejects damage reduction as an unsupported mechanic', () => ruleValidator(coverWithDamageReduction));

    const incorrectSkillPairing = get('rule-skills-with-different-abilities.json');
    incorrectSkillPairing.mechanics[0].pairings[0].usualAbility = 'dexterity';
    rejects('Skill variation checks the referenced Skill usual ability', () => {
        assertRuleIntegrity(incorrectSkillPairing);
        return true;
    });

    const malformedSkillVariant = get('rule-skill-variants.json');
    malformedSkillVariant.mechanics[0].proficiencyCount = 1;
    rejects('DMG ability-check proficiency variant requires two ability sources', () =>
        ruleValidator(malformedSkillVariant));

    rejects('Rule references to unknown source identities are rejected', () => assertCanonicalReferences({
        id: 'rule:test:reference-holder',
        references: [{ targetType: 'rule', id: 'rule:xphb:missing-rule' }]
    }));

    const typedConditionEffect = get('condition.json');
    typedConditionEffect.effects.push({
        kind: 'AUTOMATIC_SAVING_THROW_FAILURE',
        actor: 'affected-creature',
        abilities: ['strength', 'dexterity'],
        appliesWhen: { kind: 'WHILE_CONDITION_ACTIVE' }
    });
    check('typed Condition saving-throw effect is valid', () => {
        assert.equal(conditionValidator(typedConditionEffect), true,
            JSON.stringify(conditionValidator.errors));
    });

    const invalidConditionSave = get('condition.json');
    invalidConditionSave.effects.push({
        kind: 'AUTOMATIC_SAVING_THROW_FAILURE',
        actor: 'affected-creature',
        abilities: ['charisma'],
        appliesWhen: { kind: 'WHILE_CONDITION_ACTIVE' }
    });
    rejects('Condition automatic-save abilities are bounded', () => conditionValidator(invalidConditionSave));

    const wrongConditionTargetType = get('condition.json');
    wrongConditionTargetType.effects.push({
        kind: 'CONDITION_INTERACTION',
        condition: { targetType: 'status', id: 'status:phb:concentration' },
        interaction: 'apply',
        lifecycle: 'while-source-condition-active'
    });
    rejects('Condition interactions require Condition references', () => conditionValidator(wrongConditionTargetType));

    const invalidCriticalHit = get('condition.json');
    invalidCriticalHit.effects.push({
        kind: 'CRITICAL_HIT_ON_HIT',
        attacker: 'attacker',
        target: 'affected-creature',
        distanceFeet: 10,
        requiresSuccessfulHit: true,
        result: 'critical-hit'
    });
    rejects('Condition critical-hit distance is source-bounded', () => conditionValidator(invalidCriticalHit));

    const stringCondition = get('action-grapple.json');
    stringCondition.operations[0].automaticSuccessWhen = 'target-incapacitated';
    rejects('Action condition strings rejected', () => actionValidator(stringCondition));

    const unresolvedCondition = get('action-grapple.json');
    unresolvedCondition.operations[0].automaticSuccessWhen.condition.id = 'condition:phb:missing-condition';
    rejects('unresolved typed Condition reference rejected', () => assertCanonicalReferences(unresolvedCondition));

    const malformedTrigger = get('action-escape-grapple.json');
    malformedTrigger.operations[2].appliesWhen.event = 'grappler-releases';
    rejects('string Action event trigger rejected', () => actionValidator(malformedTrigger));

    const duplicateOperation = get('action-shove.json');
    duplicateOperation.operations[2].operationId = 'shove-push';
    rejects('duplicate Action operation IDs rejected', () => assertActionIntegrity(duplicateOperation));

    const missingOperation = get('action-shove.json');
    missingOperation.choiceGroups[0].members[0].operationId = 'missing-operation';
    rejects('missing choice operation reference rejected', () => assertActionIntegrity(missingOperation));

    const missingOperationId = get('action-shove.json');
    delete missingOperationId.operations.find(operation => operation.operationId === 'shove-push').operationId;
    rejects('missing member operation ID rejected', () => assertActionIntegrity(missingOperationId));

    const badCardinality = get('action-shove.json');
    badCardinality.choiceGroups[0].cardinality = { minimumSelected: 2, maximumSelected: 1 };
    rejects('invalid Action choice cardinality rejected', () => assertActionIntegrity(badCardinality));

    const tooManyChoices = get('action-shove.json');
    tooManyChoices.choiceGroups[0].cardinality = { minimumSelected: 1, maximumSelected: 3 };
    rejects('Action choice maximum cannot exceed member count', () => assertActionIntegrity(tooManyChoices));

    const duplicateMember = get('action-shove.json');
    duplicateMember.choiceGroups[0].members[1].operationId = 'shove-push';
    rejects('duplicate Action choice member rejected', () => assertActionIntegrity(duplicateMember));

    const incompatibleGroup = get('action-shove.json');
    incompatibleGroup.operations[2].appliesWhen.choiceGroupId = 'another-choice';
    rejects('Action choice applicability mismatch rejected', () => assertActionIntegrity(incompatibleGroup));

    const duplicateGroup = get('action-shove.json');
    duplicateGroup.choiceGroups.push(structuredClone(duplicateGroup.choiceGroups[0]));
    rejects('duplicate Action choice group ID rejected', () => assertActionIntegrity(duplicateGroup));

    const multipleGroups = get('action-shove.json');
    multipleGroups.choiceGroups.push({
        ...structuredClone(multipleGroups.choiceGroups[0]),
        id: 'other-choice'
    });
    rejects('operation cannot belong to incompatible choice groups', () => assertActionIntegrity(multipleGroups));

    const wrongGroupTrigger = get('action-shove.json');
    wrongGroupTrigger.choiceGroups[0].trigger.operationId = 'shove-push';
    rejects('choice trigger must match a check operation', () => assertActionIntegrity(wrongGroupTrigger));

    const unresolvedTraversalCheck = get('action-overrun.json');
    unresolvedTraversalCheck.operations.find(operation => operation.kind === 'TRAVERSE_CREATURE_SPACE').checkId = 'missing-check';
    rejects('creature traversal must reference an existing check', () => assertActionIntegrity(unresolvedTraversalCheck));

    const incompatibleTraversalOutcome = get('action-overrun.json');
    incompatibleTraversalOutcome.operations.find(operation => operation.kind === 'TRAVERSE_CREATURE_SPACE')
        .appliesWhen.operationId = 'overrun-passage';
    rejects('creature traversal success must depend on a check', () => assertActionIntegrity(incompatibleTraversalOutcome));

    const mismatchedAbilitySkill = get('action-climb-onto-a-bigger-creature.json');
    mismatchedAbilitySkill.operations[0].participants[0].options[0].skill.id = 'skill:phb:acrobatics';
    rejects('Action ability and Skill options must agree', () => assertActionIntegrity(mismatchedAbilitySkill));

    const invalidInteractionAllowance = get('action-other-activity.json');
    invalidInteractionAllowance.operations[0].interactionAllowance.additionalInteractionCost = 'BONUS_ACTION';
    rejects('object interaction allowance has bounded Action cost', () => actionValidator(invalidInteractionAllowance));

    const allowanceOnShield = get('action-other-activity.json');
    allowanceOnShield.operations[0].operation = 'don-shield';
    rejects('object interaction count cannot apply to equipment state changes', () =>
        assertActionIntegrity(allowanceOnShield));

    const helpChoiceMissingMember = get('action-help-xphb.json');
    helpChoiceMissingMember.choiceGroups[0].members[1].operationId = 'unknown-help-mode';
    rejects('Help choice references must resolve to operations', () => assertActionIntegrity(helpChoiceMissingMember));

    const missingComponent = get('rule.json');
    missingComponent.composition.members[0].componentId = 'missing';
    rejects('missing Rule component reference rejected', () => assertRuleIntegrity(missingComponent));

    const duplicateRuleComponent = get('rule.json');
    duplicateRuleComponent.mechanics[1].componentId = duplicateRuleComponent.mechanics[0].componentId;
    rejects('duplicate Rule component ID rejected', () => assertRuleIntegrity(duplicateRuleComponent));

    const duplicateDefinition = get('rule.json');
    duplicateDefinition.mechanics.push({
        ...structuredClone(duplicateDefinition.mechanics[0]),
        componentId: 'critical-attack-copy'
    });
    rejects('duplicate Rule component definition rejected', () => assertRuleIntegrity(duplicateDefinition));

    const duplicateRuleReference = get('rule.json');
    duplicateRuleReference.composition.members[1].componentId = 'critical-attack';
    rejects('duplicate Rule component reference rejected', () => assertRuleIntegrity(duplicateRuleReference));

    const circularComposition = get('rule.json');
    const cyclicGroup = {
        kind: 'AND',
        members: [
            { kind: 'COMPONENT', componentId: 'critical-attack' },
            { kind: 'COMPONENT', componentId: 'critical-damage' }
        ]
    };
    cyclicGroup.members.push(cyclicGroup);
    circularComposition.composition = cyclicGroup;
    rejects('circular Rule composition rejected', () => assertRuleIntegrity(circularComposition));

    const circularDependency = get('rule.json');
    circularDependency.mechanics = [
        {
            componentId: 'attack-a',
            kind: 'D20_TEST',
            subtype: 'ATTACK_ROLL',
            roll: { kind: 'ATTACK_ROLL', forms: ['melee'] },
            appliesWhen: { kind: 'ON_HIT', componentId: 'attack-b' }
        },
        {
            componentId: 'attack-b',
            kind: 'D20_TEST',
            subtype: 'ATTACK_ROLL',
            roll: { kind: 'ATTACK_ROLL', forms: ['ranged'] },
            appliesWhen: { kind: 'ON_HIT', componentId: 'attack-a' }
        }
    ];
    delete circularDependency.composition;
    rejects('circular Rule applicability dependency rejected', () => assertRuleIntegrity(circularDependency));

    const wrongSave = get('rule-unarmed-strike.json');
    wrongSave.mechanics.find(item => item.componentId === 'unarmed-grapple-outcome')
        .appliesWhen.componentId = 'unarmed-attack';
    rejects('ON_FAILED_SAVE requires saving-throw component', () => assertRuleIntegrity(wrongSave));

    const wrongHit = get('rule.json');
    wrongHit.mechanics[1].damageModifier.appliesWhen = { kind: 'ON_HIT', componentId: 'critical-damage' };
    rejects('ON_HIT requires attack-roll component', () => assertRuleIntegrity(wrongHit));

    const wrongCritical = get('rule.json');
    wrongCritical.mechanics[0].roll.criticalOn = undefined;
    rejects('ON_CRITICAL_HIT requires critical result component', () => assertRuleIntegrity(wrongCritical));

    const contradictory = get('rule.json');
    contradictory.composition = {
        kind: 'OR',
        members: [
            { kind: 'COMPONENT', componentId: 'critical-attack' },
            { kind: 'COMPONENT', componentId: 'critical-damage' }
        ]
    };
    contradictory.mechanics[1].damageModifier.appliesWhen = {
        kind: 'ON_HIT',
        componentId: 'critical-attack'
    };
    rejects('contradictory Rule OR/applicability rejected', () => assertRuleIntegrity(contradictory));

    const inlineDefinition = get('rule.json');
    inlineDefinition.composition.members[0].damage = { damageTypes: ['fire'] };
    rejects('inline component definitions in composition rejected', () => ruleValidator(inlineDefinition));

    const unresolvedDiseaseSymptom = get('disease-cackle-fever.json');
    unresolvedDiseaseSymptom.progression[0].onFailure[1].symptomId = 'missing-symptom';
    rejects('Disease progression cannot reference an unknown symptom', () =>
        assertDiseaseIntegrity(unresolvedDiseaseSymptom));

    const unresolvedDiseaseTrack = get('disease-cackle-fever.json');
    unresolvedDiseaseTrack.recovery[0].dcTrackId = 'missing-track';
    rejects('Disease recovery cannot reference an unknown DC track', () =>
        assertDiseaseIntegrity(unresolvedDiseaseTrack));

    const metadataOnlyDisease = get('disease-cackle-fever.json');
    delete metadataOnlyDisease.infection;
    delete metadataOnlyDisease.incubation;
    delete metadataOnlyDisease.symptoms;
    delete metadataOnlyDisease.progression;
    delete metadataOnlyDisease.recovery;
    delete metadataOnlyDisease.conditionGatedRestrictions;
    rejects('metadata-only Disease records are rejected', () => diseaseValidator(metadataOnlyDisease));

    const invalidOptionalStatusSave = get('status-concentration-phb.json');
    const damageSave = invalidOptionalStatusSave.mechanics.find(mechanic => mechanic.trigger === 'damage');
    damageSave.requirement = 'dm-may-require';
    rejects('DM optionality is restricted to environmental saves', () =>
        ajv.getSchema('urn:thedndpub:schemas:core-rules:status:v1')(invalidOptionalStatusSave));

    const invalidFallCap = get('rule-falling.json');
    invalidFallCap.mechanics[0].details.maximumDiceCount = 21;
    rejects('Falling damage rejects a maximum above the source-defined 20d6 cap', () => ruleValidator(invalidFallCap));

    const guessedFallRounding = get('rule-falling.json');
    guessedFallRounding.mechanics[0].details.partialIncrementTreatment = 'ROUND_UP';
    rejects('Falling damage rejects an invented partial-distance rounding rule', () => ruleValidator(guessedFallRounding));

    const invalidAreaOrigin = get('rule-area-of-effect.json');
    invalidAreaOrigin.mechanics[0].lineOfEffect.blockedBy.coverLevel = 'three-quarters';
    rejects('Area line of effect requires Total Cover to block the line', () => ruleValidator(invalidAreaOrigin));

    const invalidEmanationOrigin = get('rule-emanation-area-of-effect.json');
    invalidEmanationOrigin.mechanics[0].originIncluded = 'INCLUDED';
    rejects('Emanation origin inclusion cannot override its source-defined default', () =>
        ruleValidator(invalidEmanationOrigin));

    const invalidClimbingCost = get('rule-movement-cost-procedure.json');
    invalidClimbingCost.mechanics[0].details.difficultTerrainAdditionalFeetPerFoot = 3;
    rejects('climbing cost rejects unsupported Difficult Terrain surcharges', () => ruleValidator(invalidClimbingCost));

    const invalidDiagonalCost = get('rule-grid-movement-cost.json');
    invalidDiagonalCost.mechanics[0].details.diagonalPatternFeet = [5, 5];
    rejects('diagonal movement cost rejects a non-source-backed sequence', () => ruleValidator(invalidDiagonalCost));

    const invalidCrawlingCost = get('rule-crawling-movement.json');
    invalidCrawlingCost.mechanics[0].details.difficultTerrainAdditionalFeetPerFoot = 1;
    rejects('crawling cost preserves the source-defined difficult-terrain surcharge', () =>
        ruleValidator(invalidCrawlingCost));

    const invalidJumpSelection = get('rule-jump-choice.json');
    invalidJumpSelection.mechanics[0].details.horizontal.id = 'rule:xphb:high-jump';
    rejects('Jumping keeps distinct horizontal and vertical procedure references', () =>
        ruleValidator(invalidJumpSelection));
}

function run() {
    const schemaFiles = loadSchemas();
    const phase3Result = validatePhase3Records(schemaFiles);
    const phase5Result = validatePhase5Expansion();
    const allCanonicalRecords = [
        ...phase3Result.entities.map(({ entity }) => entity),
        ...phase5Result.entities.map(({ entity }) => entity)
    ];
    check('consolidated application resources exactly match validated Phase 3 and Phase 5 records', () => {
        assertConsolidatedCoreRulesResources(allCanonicalRecords);
    });
    check('runtime source identity index exactly covers referenced pinned source catalogs', () => {
        assertCoreRulesReferenceIdentityIndex(allCanonicalRecords);
    });
    const canonicalRecords = allCanonicalRecords.map(record => [record.id, record]);
    const canonicalRecordIds = new Set(canonicalRecords.map(([id]) => id));
    check('all former schema-example identities exist in the canonical datasets', () => {
        for (const id of canonicalRegressionIds) assert.ok(canonicalRecordIds.has(id), id);
    });
    for (const [id, record] of canonicalRecords) {
        const model = record.id.split(':')[0];
        const validator = ajv.getSchema(`urn:thedndpub:schemas:core-rules:${model}:v1`);
        if (!validator) fail(`${id}: no schema registered for ${model}`);
        check(`canonical schema record ${id}`, () => assert.equal(validator(record), true,
            JSON.stringify(validator.errors)));
    }

    const actionRecords = canonicalRecords.filter(([id]) => id.startsWith('action:')
        && canonicalRegressionIds.has(id));
    const ruleRecords = canonicalRecords.filter(([id]) => id.startsWith('rule:')
        && canonicalRegressionIds.has(id));
    check('Action operation IDs, triggers, conditions, and choice groups', () => {
        for (const [, action] of actionRecords) {
            assertCanonicalReferences(action);
            assertActionIntegrity(action);
        }
    });
    check('Rule component composition and applicability', () => {
        for (const [, rule] of ruleRecords) {
            assertCanonicalReferences(rule);
            assertRuleIntegrity(rule);
        }
    });

    const conditionValidator = ajv.compile({ $ref: `${commonSchemaId}#/$defs/conditionPredicate` });
    const triggerValidator = ajv.compile({ $ref: `${commonSchemaId}#/$defs/actionTrigger` });
    check('typed Action state and event canonical records', () => {
        const grapple = canonicalFixture('action-grapple.json');
        const dodge = canonicalFixture('action-dodge.json');
        const escape = canonicalFixture('action-escape-grapple.json');
        assert.equal(conditionValidator(grapple.operations[0].automaticSuccessWhen), true);
        assert.equal(conditionValidator(dodge.operations[0].when), true);
        assert.equal(conditionValidator(escape.operations[2].appliesWhen.condition), true);
        assert.equal(triggerValidator(grapple.operations[2].appliesWhen.event), true);
        const phbDodge = dodge;
        assert.deepEqual(phbDodge.operations[0].appliesWhen, {
            kind: 'ATTACK_AGAINST_AFFECTED', actor: 'attacker', target: 'affected-creature'
        });
    });
    check('Shove has exactly one post-check outcome', () => {
        const shove = canonicalFixture('action-shove.json');
        assert.deepEqual(shove.choiceGroups[0].cardinality, { minimumSelected: 1, maximumSelected: 1 });
        assert.equal(shove.choiceGroups[0].trigger.operationId, 'shove-check');
    });
    check('nested Rule compositions preserve source-supported groups', () => {
        assert.equal(canonicalFixture('rule.json').composition.kind, 'AND');
        assert.equal(canonicalFixture('rule-unarmed-strike.json').composition.kind, 'OR');
    });
    check('canonical identity and typed reference integrity', () => {
        const ids = canonicalRecords.map(([, record]) => record.id);
        assert.equal(new Set(ids).size, ids.length);
        for (const [, record] of canonicalRecords) assertCanonicalReferences(record);
    });
    check('six approved domain contracts match the semantic baseline', assertApprovedDomainBaseline);
    check('approved PHB Blinded canonical record includes HftT page 48 citation', () => {
        const blinded = canonicalFixture('condition.json');
        assert.ok(blinded.sourceMetadata.citations.some(citation =>
            citation.source === 'HftT' && citation.page === 48));
    });
    check('six approved Phase 2 fixture corrections match pinned source records', () => {
        const actions = readJson(path.join(workspace, 'src/main/resources/data/actions.json')).action;
        const variables = readJson(path.join(workspace, 'src/main/resources/data/variantrules.json')).variantrule;
        const skills = readJson(path.join(workspace, 'src/main/resources/data/skills.json')).skill;
        const conditions = readJson(path.join(workspace, 'src/main/resources/data/conditionsdiseases.json'));
        const sourceRecord = (records, name, source) => records.find(record =>
            record.name === name && record.source === source);
        const dodgeSource = sourceRecord(actions, 'Dodge', 'PHB');
        assert.ok(JSON.stringify(dodgeSource.entries).includes('can see the attacker'));
        const shoveSource = sourceRecord(actions, 'Shove', 'PHB');
        assert.ok(JSON.stringify(shoveSource.entries).includes('incapacitated'));
        const unarmedSource = sourceRecord(variables, 'Unarmed Strike', 'XPHB');
        assert.ok(JSON.stringify(unarmedSource.entries).includes('any escape attempts equals'));
        assert.ok(canonicalFixture('rule-unarmed-strike.json').references.some(reference =>
            reference.relationship === 'grapple-escape-attempts-use-this-dc'));
        const criticalSource = sourceRecord(variables, 'Critical Hit', 'XPHB');
        assert.ok(JSON.stringify(criticalSource.entries).includes('attack'));
        assert.ok(canonicalFixture('rule.json').mechanics[0].roll.forms.includes('unarmed'));
        const acrobatics = sourceRecord(skills, 'Acrobatics', 'PHB');
        assert.equal(acrobatics.page, 176);
        assert.equal(canonicalFixture('skill.json').sourceMetadata.page, 176);
        const blinded = sourceRecord(conditions.condition, 'Blinded', 'PHB');
        assert.ok(blinded.otherSources.some(citation => citation.source === 'HftT' && citation.page === 48));
        assert.ok(canonicalFixture('condition.json').sourceMetadata.citations.some(citation =>
            citation.source === 'HftT' && citation.page === 48));
    });
    check('C1-C8 mechanics remain grounded in the selected source wording', () => {
        const actions = readJson(path.join(workspace, 'src/main/resources/data/actions.json')).action;
        const variableRules = readJson(path.join(workspace, 'src/main/resources/data/variantrules.json')).variantrule;
        const statuses = readJson(path.join(workspace, 'src/main/resources/data/conditionsdiseases.json')).status;
        const senses = readJson(path.join(workspace, 'src/main/resources/data/senses.json')).sense;
        const find = (records, name, source) => records.find(record =>
            record.name === name && record.source === source);
        const grappleMovement = JSON.stringify(find(actions, 'Grapple', 'PHB').entries[2]);
        assert.ok(grappleMovement.includes('drag or carry'));
        assert.ok(grappleMovement.includes('speed is halved'));
        assert.ok(grappleMovement.includes('two or more sizes smaller'));
        const unarmed = JSON.stringify(find(variableRules, 'Unarmed Strike', 'XPHB').entries);
        assert.ok(unarmed.includes('within 5 feet'));
        assert.ok(unarmed.includes('any escape attempts equals'));
        const terrain = JSON.stringify(find(variableRules, 'Difficult Terrain', 'XPHB').entries);
        for (const evidence of ['Tiny or your ally', 'Furniture', 'shin- and waist-deep',
            'one size smaller', '20 degrees or more']) assert.ok(terrain.includes(evidence), evidence);
        assert.ok(JSON.stringify(find(statuses, 'Concentration', 'PHB').entries[4]).includes('DM might also decide'));
        const darkvision = JSON.stringify(find(senses, 'Darkvision', 'PHB').entries);
        for (const evidence of ['dim light as if it were bright light',
            'darkness as if it were dim light', 'shades of gray']) assert.ok(darkvision.includes(evidence));
        assert.ok(JSON.stringify(find(senses, 'Tremorsense', 'MM').entries).includes('pinpoint the origin'));
        assert.ok(JSON.stringify(find(senses, 'Tremorsense', 'XPHB').entries)
            .includes('pinpoint the location of creatures and moving objects'));
        const phbTruesight = JSON.stringify(find(senses, 'Truesight', 'PHB').entries);
        for (const evidence of ['invisible creatures and objects', 'automatically detect visual illusions',
            'succeed on saving throws against them']) assert.ok(phbTruesight.includes(evidence));
        const xphbTruesight = JSON.stringify(find(senses, 'Truesight', 'XPHB').entries);
        assert.ok(xphbTruesight.includes('appear transparent'));
        assert.ok(xphbTruesight.includes('automatically succeed'));
    });
    check('schema JSON uses two-space formatting', () => {
        for (const file of schemaFiles.map(schemaPath)) {
            const raw = fs.readFileSync(file, 'utf8');
            assert.equal(raw, `${JSON.stringify(readJson(file), null, 2)}\n`, file);
        }
    });
    testNegativeCases(canonicalFixture, allCanonicalRecords);

    console.log(JSON.stringify({
        draft202012SchemasCompiled: schemaFiles.length,
        canonicalRecordsSchemaValidated: canonicalRecords.length,
        phase3CanonicalRecordsValidated: phase3Result.files,
        phase5CanonicalRecordsValidated: phase5Result.files,
        permanentChecksPassed: testResults.length,
        ruleComponentsChecked: ruleRecords.reduce((sum, [, rule]) => sum + (rule.mechanics?.length ?? 0), 0),
        actionRecordsChecked: actionRecords.length
    }, null, 2));
}

run();
