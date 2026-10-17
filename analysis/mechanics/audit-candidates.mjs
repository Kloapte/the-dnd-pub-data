import { readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const directory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(directory, '../..');
const evidence = JSON.parse(await readFile(path.join(directory, 'corpus-evidence.json'), 'utf8'));
const documents = new Map();
for (const file of evidence.files) {
    const text = await readFile(path.join(repositoryRoot, file.file), 'utf8');
    documents.set(file.file, JSON.parse(text.replace(/^\uFEFF/, '')));
}

function walk(value, locator, schema, visit) {
    visit(value, locator, schema);
    if (Array.isArray(value)) {
        value.forEach((child, index) => walk(child, `${locator}[${index}]`, `${schema}[]`, visit));
    } else if (value !== null && typeof value === 'object') {
        for (const [key, child] of Object.entries(value)) {
            walk(child, `${locator}.${key}`, `${schema}.${key}`, visit);
        }
    }
}

const omittedFeatFields = new Set([
    'skillProficiencies', 'expertise', 'languageProficiencies', 'toolProficiencies',
    'weaponProficiencies', 'armorProficiencies', 'savingThrowProficiencies',
    'skillToolLanguageProficiencies', 'conditionImmune', 'immune', 'resist',
    'senses', 'bonusSenses', 'optionalfeatureProgression',
]);
const queries = [
    { id: 'class.publicTableColumnShift', roots: ['class', 'subclass'],
        match: (value, schema) => /^\$\.(classTableGroups|subclassTableGroups)\[\]\.rows\[\]$/.test(schema)
            && Array.isArray(value) && value.some(cell => typeof cell !== 'string')
            && value.findIndex(cell => typeof cell === 'string') >= 0
            && value.findIndex(cell => typeof cell === 'string') < value.findLastIndex(cell => typeof cell !== 'string') },
    { id: 'feat.structuredMechanicsNotInDefinition', roots: ['feat'],
        match: (value, schema) => [...omittedFeatFields].some(field => schema === `$.${field}`) },
    { id: 'feat.prerequisiteReferenceSourceOmitted', roots: ['feat'],
        match: (value, schema) => /^\$\.prerequisite\[\]\.(race|background)\[\]\.source$/.test(schema) },
    { id: 'feat.proficiencyRequirementsFlattened', roots: ['feat'],
        match: (value, schema) => schema === '$.prerequisite[].proficiency[]' },
    { id: 'item.focusArraysNotNormalized', roots: ['item', 'baseitem'],
        match: (value, schema) => schema === '$.focus' && Array.isArray(value) },
    { id: 'item.publicChargeMarkup', roots: ['item', 'baseitem'],
        match: (value, schema) => /^\$\.(charges|rechargeAmount)$/.test(schema)
            && typeof value === 'string' && value.includes('{@') },
    { id: 'optionalFeature.publicTextPrerequisites', roots: ['optionalfeature'],
        match: (value, schema) => /^\$\.prerequisite\[\]\.(otherSummary|spell\[\])\.(entry|entrySummary)$/.test(schema) },
    { id: 'spell.scalingLevelDice', roots: ['spell'],
        match: (value, schema) => schema === '$.scalingLevelDice' },
    { id: 'spell.higherLevels', roots: ['spell'],
        match: (value, schema) => schema === '$.entriesHigherLevel' },
    { id: 'spell.explicitRangeGeometry', roots: ['spell'],
        match: (value, schema) => schema === '$.range' && value !== null
            && !['point', 'special'].includes(value.type) && typeof value.distance?.amount === 'number' },
    { id: 'monster.multiattackFeature', roots: ['monster'],
        match: (value, schema) => schema === '$.action[].name' && /^multiattack\b/i.test(value) },
    { id: 'object.structuredAttackEntry', roots: ['object'],
        match: (value, schema) => schema.startsWith('$.actionEntries')
            && value !== null && typeof value === 'object' && value.type === 'attack' },
    { id: 'classFeature.resourceConsumption', roots: ['classFeature', 'subclassFeature'],
        match: (value, schema) => schema === '$.consumes' },
    { id: 'character.explicitSpellGrants', roots: ['feat', 'optionalfeature', 'class', 'subclass', 'race', 'subrace', 'background', 'reward'],
        match: (value, schema) => schema === '$.additionalSpells' },
    { id: 'treasure.currencyExpressions', roots: ['individual', 'hoard', 'dragon'],
        match: (value, schema) => /\.coins\.(cp|sp|ep|gp|pp)$/.test(schema) },
    { id: 'item.bonusValues', roots: ['item', 'baseitem'],
        match: (value, schema) => /^\$\.bonus[A-Z]/.test(schema) && typeof value !== 'object' },
    { id: 'item.integerBonusExpressions', roots: ['item', 'baseitem'],
        match: (value, schema) => /^\$\.bonus[A-Z]/.test(schema)
            && typeof value === 'string' && /^[+-]?\d+$/.test(value) },
    { id: 'vehicle.summaryStatsAbsentDetail', roots: ['vehicle'],
        match: (value, schema) => ['$.ac', '$.hp'].includes(schema) },
];

const output = {};
for (const query of queries) {
    const result = { records: 0, occurrences: 0, roots: {}, valueTypes: {}, discriminators: {}, fields: {}, examples: [], affectedRecords: [] };
    for (const [file, document] of documents) {
        for (const root of query.roots) {
            for (const [index, record] of (document[root] ?? []).entries()) {
                let found = false;
                walk(record, `$.${root}[${index}]`, '$', (value, locator, schema) => {
                    if (!query.match(value, schema)) {
                        return;
                    }
                    found = true;
                    result.occurrences += 1;
                    const type = value === null ? 'null' : Array.isArray(value) ? 'array' : typeof value;
                    result.valueTypes[type] = (result.valueTypes[type] ?? 0) + 1;
                    if (value !== null && typeof value === 'object' && typeof value.type === 'string') {
                        result.discriminators[value.type] = (result.discriminators[value.type] ?? 0) + 1;
                    }
                    result.fields[schema] = (result.fields[schema] ?? 0) + 1;
                    if (result.examples.length < 5) {
                        result.examples.push({ file, root, index, name: record.name ?? null,
                            source: record.source ?? null, locator, type,
                            scalar: typeof value === 'number' || typeof value === 'boolean'
                                || typeof value === 'string' && value.length <= 35 ? value : undefined });
                    }
                });
                if (found) {
                    result.records += 1;
                    result.roots[root] = (result.roots[root] ?? 0) + 1;
                    result.affectedRecords.push({ file, root, index, name: record.name ?? null, source: record.source ?? null });
                }
            }
        }
    }
    output[query.id] = result;
}
const totals = Object.values(evidence.catalogs).reduce((result, catalog) => {
    result.fieldPaths += Object.keys(catalog.fieldPaths).length;
    result.tagOccurrences += Object.values(catalog.tagCounts).reduce((sum, count) => sum + count, 0);
    if (catalog.role === 'definition-source') {
        result.definitionFieldPaths += Object.keys(catalog.fieldPaths).length;
        result.definitionTagOccurrences += Object.values(catalog.tagCounts).reduce((sum, count) => sum + count, 0);
        result.entryTables += catalog.tableNodes;
    }
    return result;
}, { fieldPaths: 0, definitionFieldPaths: 0, tagOccurrences: 0, definitionTagOccurrences: 0, entryTables: 0 });
await writeFile(path.join(directory, 'candidate-evidence.json'), JSON.stringify({ totals, candidates: output }, null, 2) + '\n');
console.log(JSON.stringify({ totals, candidates: Object.fromEntries(Object.entries(output).map(([id, result]) => [id, {
    records: result.records, occurrences: result.occurrences, roots: result.roots, valueTypes: result.valueTypes,
}])) }, null, 2));
