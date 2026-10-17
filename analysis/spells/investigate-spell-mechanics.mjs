import { readdir, readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const projectRoot = path.resolve(scriptDirectory, '../..');
const spellDirectory = path.join(projectRoot, 'src/main/resources/data/spells');
const outputFile = path.join(scriptDirectory, 'mechanics-investigation-evidence.json');

const targetSpells = [
    ['Fireball', 'XPHB'], ['Fireball', 'PHB'],
    ['Healing Word', 'XPHB'], ['Healing Word', 'PHB'],
    ['Shield', 'XPHB'], ['Shield', 'PHB'],
    ['Booming Blade', 'TCE'], ['Cure Wounds', 'XPHB'],
    ['Magic Missile', 'XPHB'], ['Scorching Ray', 'XPHB'], ['Counterspell', 'XPHB'],
];

async function findJsonFiles(directory) {
    const entries = await readdir(directory, { withFileTypes: true });
    const nested = await Promise.all(entries.map(async (entry) => {
        const entryPath = path.join(directory, entry.name);
        if (entry.isDirectory()) {
            return findJsonFiles(entryPath);
        }
        return entry.isFile() && entry.name.toLowerCase().endsWith('.json') ? [entryPath] : [];
    }));
    return nested.flat().sort();
}

function isRecord(value) {
    return value !== null && typeof value === 'object' && !Array.isArray(value);
}

function walk(value, currentPath, visitString) {
    if (typeof value === 'string') {
        visitString(value, currentPath);
        return;
    }
    if (Array.isArray(value)) {
        value.forEach((child, index) => walk(child, `${currentPath}[${index}]`, visitString));
        return;
    }
    if (isRecord(value)) {
        Object.entries(value).forEach(([key, child]) => walk(child, `${currentPath}.${key}`, visitString));
    }
}

function parseTag(text, start) {
    let depth = 0;
    let end = -1;
    for (let index = start; index < text.length; index += 1) {
        if (text[index] === '{') {
            depth += 1;
        } else if (text[index] === '}') {
            depth -= 1;
            if (depth === 0) {
                end = index;
                break;
            }
        }
    }
    if (end < 0) {
        return null;
    }

    const raw = text.slice(start + 2, end);
    const separator = raw.search(/[\s|}]/);
    const name = (separator < 0 ? raw : raw.slice(0, separator)).toLowerCase();
    const body = separator < 0 ? '' : raw.slice(separator).trimStart();
    const argumentsList = splitTopLevel(body, '|');
    return { name, raw, body, argumentsList, end };
}

function splitTopLevel(text, delimiter) {
    const parts = [];
    let depth = 0;
    let partStart = 0;
    for (let index = 0; index < text.length; index += 1) {
        if (text[index] === '{') {
            depth += 1;
        } else if (text[index] === '}') {
            depth -= 1;
        } else if (text[index] === delimiter && depth === 0) {
            parts.push(text.slice(partStart, index));
            partStart = index + 1;
        }
    }
    if (text.length > 0) {
        parts.push(text.slice(partStart));
    }
    return parts;
}

function collectTags(text, pathName, output, nested = false) {
    let cursor = 0;
    while (cursor < text.length) {
        const start = text.indexOf('{@', cursor);
        if (start < 0) {
            break;
        }
        const tag = parseTag(text, start);
        if (tag === null) {
            output.malformed.push({ path: pathName, sample: text.slice(start, start + 100) });
            break;
        }
        const context = text.slice(Math.max(0, start - 120), Math.min(text.length, tag.end + 121));
        output.tags.push({
            name: tag.name,
            body: tag.body,
            argumentCount: tag.argumentsList.length,
            argumentsList: tag.argumentsList,
            path: pathName,
            inHigherLevel: pathName.includes('entriesHigherLevel'),
            inTable: /\.table(?:s)?\b|\.rows\b|\.row\b|\.cells\b|\.cell\b/i.test(pathName),
            nested,
            conditionalContext: /\b(if|when|unless|otherwise|succeed|fail|failed|success|failure)\b/i.test(context),
            sample: context.replaceAll(/\s+/g, ' ').slice(0, 260),
        });
        collectTags(tag.raw, pathName, output, true);
        cursor = tag.end + 1;
    }
}

function hasPath(root, pathName) {
    return pathName in root;
}

const files = await findJsonFiles(spellDirectory);
const spells = [];
for (const file of files) {
    const document = JSON.parse(await readFile(file, 'utf8'));
    for (const spell of document.spell ?? []) {
        spells.push({ file: path.relative(projectRoot, file).replaceAll('\\', '/'), ...spell });
    }
}

const tags = [];
const malformed = [];
const schoolCounts = new Map();
const structuredFieldSpellCounts = new Map();
const targetEvidence = [];
const structuredMechanicsFields = [
    'damageInflict', 'damageImmune', 'damageResist', 'damageVulnerable',
    'conditionInflict', 'conditionImmune', 'savingThrow', 'spellAttack',
    'abilityCheck', 'areaTags', 'scalingLevelDice', 'miscTags',
];
const rootHasMechanics = (spell) => [
    'damageInflict', 'damageImmune', 'damageResist', 'damageVulnerable',
    'conditionInflict', 'conditionImmune', 'savingThrow', 'spellAttack', 'abilityCheck',
].some((field) => Array.isArray(spell[field]) && spell[field].length > 0);

for (const spell of spells) {
    if (spell.school) {
        schoolCounts.set(spell.school, (schoolCounts.get(spell.school) ?? 0) + 1);
    }
    for (const field of structuredMechanicsFields) {
        if (Array.isArray(spell[field]) && spell[field].length > 0) {
            structuredFieldSpellCounts.set(field, (structuredFieldSpellCounts.get(field) ?? 0) + 1);
        }
    }
    const spellTags = [];
    const spellMarkup = { tags: spellTags, malformed };
    walk(spell.entries ?? [], 'entries', (text, pathName) => collectTags(text, pathName, spellMarkup));
    walk(spell.entriesHigherLevel ?? [], 'entriesHigherLevel', (text, pathName) =>
        collectTags(text, pathName, spellMarkup));
    tags.push(...spellTags.map((tag) => ({ ...tag, spellName: spell.name, spellSource: spell.source })));

    const isTarget = targetSpells.some(([name, source]) => spell.name === name && spell.source === source);
    if (isTarget) {
        targetEvidence.push({
            name: spell.name,
            source: spell.source,
            file: spell.file,
            level: spell.level,
            school: spell.school,
            range: spell.range ?? null,
            damageInflict: spell.damageInflict ?? [],
            savingThrow: spell.savingThrow ?? [],
            spellAttack: spell.spellAttack ?? [],
            conditionInflict: spell.conditionInflict ?? [],
            areaTags: spell.areaTags ?? [],
            scalingLevelDice: spell.scalingLevelDice ?? null,
            hasHigherLevelEntries: Array.isArray(spell.entriesHigherLevel) && spell.entriesHigherLevel.length > 0,
            mechanicsTags: spellTags.map(({ sample, ...tag }) => tag),
        });
    }
}

function extractText(value, texts = []) {
    if (typeof value === 'string') {
        texts.push(value);
    } else if (Array.isArray(value)) {
        value.forEach((child) => extractText(child, texts));
    } else if (isRecord(value)) {
        Object.values(value).forEach((child) => extractText(child, texts));
    }
    return texts;
}

const bySpell = new Map();
for (const tag of tags) {
    const spellKey = `${tag.spellName}|${tag.spellSource}`;
    const entry = bySpell.get(spellKey) ?? { tags: [], root: spells.find((spell) =>
        spell.name === tag.spellName && spell.source === tag.spellSource) };
    entry.tags.push(tag);
    bySpell.set(spellKey, entry);
}

const tagCounts = new Map();
for (const tag of tags) {
    const current = tagCounts.get(tag.name) ?? {
        occurrences: 0,
        spells: new Set(),
        argumentCounts: {},
        pipeArgumentShapes: {},
        argumentValues: [],
        inHigherLevel: 0,
        inTable: 0,
        nested: 0,
        conditionalContext: 0,
        samples: [],
    };
    current.occurrences += 1;
    current.spells.add(`${tag.spellName}|${tag.spellSource}`);
    current.argumentCounts[tag.argumentCount] = (current.argumentCounts[tag.argumentCount] ?? 0) + 1;
    const shape = String(tag.argumentCount);
    current.pipeArgumentShapes[shape] = (current.pipeArgumentShapes[shape] ?? 0) + 1;
    for (let argumentIndex = 0; argumentIndex < tag.argumentsList.length; argumentIndex += 1) {
        const argumentRecord = current.argumentValues[argumentIndex]
            ?? { distinctValues: new Set(), samples: [] };
        const argumentValue = tag.argumentsList[argumentIndex].trim();
        argumentRecord.distinctValues.add(argumentValue);
        if (argumentRecord.samples.length < 8 && !argumentRecord.samples.includes(argumentValue)) {
            argumentRecord.samples.push(argumentValue);
        }
        current.argumentValues[argumentIndex] = argumentRecord;
    }
    current.inHigherLevel += Number(tag.inHigherLevel);
    current.inTable += Number(tag.inTable);
    current.nested += Number(tag.nested);
    current.conditionalContext += Number(tag.conditionalContext);
    if (current.samples.length < 8) {
        current.samples.push({
            spell: `${tag.spellName}|${tag.spellSource}`,
            path: tag.path,
            arguments: tag.argumentsList,
        });
    }
    tagCounts.set(tag.name, current);
}

const formulasBySpell = new Map();
const damageFormulasBySpell = new Map();
for (const tag of tags.filter((value) => ['damage', 'scaledamage'].includes(value.name))) {
    const key = `${tag.spellName}|${tag.spellSource}`;
    formulasBySpell.set(key, (formulasBySpell.get(key) ?? 0) + 1);
    const formulas = damageFormulasBySpell.get(key) ?? new Set();
    formulas.add(tag.argumentsList[0] ?? '');
    damageFormulasBySpell.set(key, formulas);
}

const areaTagged = spells.filter((spell) => Array.isArray(spell.areaTags) && spell.areaTags.length > 0);
const higherLevel = spells.filter((spell) => Array.isArray(spell.entriesHigherLevel)
    && spell.entriesHigherLevel.length > 0);
const structuredDamageType = spells.filter((spell) => Array.isArray(spell.damageInflict)
    && spell.damageInflict.length > 0);
const textualMechanicCandidates = spells.filter((spell) => !rootHasMechanics(spell)
    && /\b(damage|heal(?:s|ing)?|hit points|saving throw|condition|resistance|immunity|attack roll|temporary hit points)\b/i
        .test(extractText([spell.entries ?? [], spell.entriesHigherLevel ?? []]).join(' ')));
const healingTaggedSpellIds = new Set(tags.filter((tag) =>
    ['healing', 'scaledhealing'].includes(tag.name)).map((tag) => `${tag.spellName}|${tag.spellSource}`));
const healingDiceCandidates = new Set(tags.filter((tag) => tag.name === 'dice'
    && /heal|regain|hit points|hit point|hp\b/i.test(tag.sample))
    .map((tag) => `${tag.spellName}|${tag.spellSource}`));
const scalingTagSpellIds = new Set(tags.filter((tag) => ['scaledamage', 'scaledice'].includes(tag.name))
    .map((tag) => `${tag.spellName}|${tag.spellSource}`));
const ambiguousFormulaSpellIds = new Set(tags.filter((tag) =>
    ['damage', 'scaledamage', 'healing', 'scaledhealing', 'dice', 'scaledice'].includes(tag.name)
    && (tag.conditionalContext || tag.nested || tag.argumentCount !== 1))
    .map((tag) => `${tag.spellName}|${tag.spellSource}`));

const publicFormulaCount = 0;
const summary = {
    sourceFiles: files.length,
    spellRoots: spells.length,
    schoolCounts: Object.fromEntries([...schoolCounts.entries()].sort(([left], [right]) => left.localeCompare(right))),
    structuredFieldSpellCounts: Object.fromEntries(
        structuredMechanicsFields.map((field) => [field, structuredFieldSpellCounts.get(field) ?? 0])),
    tagCounts: Object.fromEntries([...tagCounts.entries()].sort(([left], [right]) => left.localeCompare(right))
        .map(([name, value]) => [name, {
            occurrences: value.occurrences,
            spellCount: value.spells.size,
            argumentCounts: value.argumentCounts,
            arguments: value.argumentValues.map((argument) => ({
                distinctValues: argument.distinctValues.size,
                samples: argument.samples,
            })),
            inHigherLevel: value.inHigherLevel,
            inTable: value.inTable,
            nested: value.nested,
            conditionalContext: value.conditionalContext,
            samples: value.samples,
        }])),
    metrics: {
        spellsWithDamageTags: new Set(tags.filter((tag) => ['damage', 'scaledamage'].includes(tag.name))
            .map((tag) => `${tag.spellName}|${tag.spellSource}`)).size,
        spellsWithHealingTags: healingTaggedSpellIds.size,
        spellsWithHealingRelatedDiceCandidates: healingDiceCandidates.size,
        spellsWithMultipleDamageFormulaTags: [...formulasBySpell.values()].filter((count) => count > 1).length,
        spellsWithMultipleDistinctDamageFormulaArguments: [...damageFormulasBySpell.values()]
            .filter((formulas) => formulas.size > 1).length,
        areaTaggedSpells: areaTagged.length,
        areaTaggedSpellsWithoutSeparateAreaField: areaTagged.filter((spell) =>
            !hasPath(spell, 'area') && !hasPath(spell, 'areaDimensions') && !hasPath(spell, 'areaSize')).length,
        structuredDamageTypeSpells: structuredDamageType.length,
        structuredDamageTypeSpellsWithoutPublicDamageFormula: structuredDamageType.length,
        spellsWithHigherLevelEntries: higherLevel.length,
        higherLevelEntriesWithoutScalingLevelDice: higherLevel.filter((spell) => !spell.scalingLevelDice).length,
        higherLevelEntriesWithoutScalingFieldOrMarkup:
            higherLevel.filter((spell) => !spell.scalingLevelDice
                && !scalingTagSpellIds.has(`${spell.name}|${spell.source}`)).length,
        spellsWithFormulaContextsMarkedConditionalNestedOrMultiArgument: ambiguousFormulaSpellIds.size,
        proseMechanicCandidatesWithoutRootMechanicTags: textualMechanicCandidates.length,
        publicDamageFormulaCount: publicFormulaCount,
        publicHealingFormulaCount: 0,
    },
    targetEvidence,
    malformedMarkup: malformed,
};

const allSpellsWithDamageCount = summary.metrics.spellsWithDamageTags;
const allSpellsWithHealingCount = summary.metrics.spellsWithHealingTags;
if (files.length !== 18 || spells.length !== 969 || malformed.length > 0) {
    throw new Error(`Unexpected spell scan: files=${files.length}, spells=${spells.length}, malformed=${malformed.length}`);
}
if (targetEvidence.length !== targetSpells.length) {
    throw new Error(`Expected ${targetSpells.length} target examples, found ${targetEvidence.length}`);
}

await writeFile(outputFile, `${JSON.stringify(summary, null, 2)}\n`);
console.log(JSON.stringify({
    output: path.relative(projectRoot, outputFile).replaceAll('\\', '/'),
    sourceFiles: files.length,
    spellRoots: spells.length,
    schools: summary.schoolCounts,
    tags: Object.fromEntries(Object.entries(summary.tagCounts).map(([name, value]) =>
        [name, { occurrences: value.occurrences, spellCount: value.spellCount }])),
    metrics: summary.metrics,
    targets: targetEvidence.map((spell) => `${spell.name}|${spell.source}`),
    damageTagSpellCount: allSpellsWithDamageCount,
    healingTagSpellCount: allSpellsWithHealingCount,
}, null, 2));
