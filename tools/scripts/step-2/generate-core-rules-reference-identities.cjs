const fs = require('node:fs');
const path = require('node:path');

const workspace = path.resolve(__dirname, '../..');
const canonicalRoot = path.join(workspace, 'src/main/resources/normalized/core-rules');
const sourceRoot = path.join(workspace, 'src/main/resources/data');
const outputFile = path.join(canonicalRoot, 'source-identities.json');

function canonicalSlug(value) {
    return value.normalize('NFKD').replace(/[\u0300-\u036f]/g, '').toLowerCase()
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/^-+|-+$/g, '');
}

function readJson(file) {
    return JSON.parse(fs.readFileSync(file, 'utf8'));
}

function filesUnder(directory) {
    return fs.readdirSync(directory, { withFileTypes: true }).flatMap(entry => {
        const absolute = path.join(directory, entry.name);
        return entry.isDirectory() ? filesUnder(absolute) : entry.name.endsWith('.json') ? [absolute] : [];
    });
}

function collectReferences(value, references) {
    if (Array.isArray(value)) {
        value.forEach(child => collectReferences(child, references));
        return;
    }
    if (!value || typeof value !== 'object') return;
    if (typeof value.targetType === 'string' && typeof value.id === 'string') {
        references.add(value.id);
    }
    Object.values(value).forEach(child => collectReferences(child, references));
}

function sourceIds(targetType) {
    const definitions = {
        action: [['actions.json', 'action']],
        rule: [['variantrules.json', 'variantrule']],
        skill: [['skills.json', 'skill']],
        condition: [['conditionsdiseases.json', 'condition']],
        disease: [['conditionsdiseases.json', 'disease']],
        status: [['conditionsdiseases.json', 'status']],
        sense: [['senses.json', 'sense']],
        language: [['languages.json', 'language']],
        race: [['races.json', 'race']],
        spell: filesUnder(path.join(sourceRoot, 'spells')).map(file => [file, 'spell']),
        creature: filesUnder(path.join(sourceRoot, 'monsters'))
            .filter(file => path.basename(file).startsWith('bestiary-'))
            .map(file => [file, 'monster'])
    };
    const definitionsForType = definitions[targetType];
    if (!definitionsForType) throw new Error(`No pinned source catalog for reference type ${targetType}`);
    return new Set(definitionsForType.flatMap(([relativeOrAbsolute, property]) => {
        const file = path.isAbsolute(relativeOrAbsolute)
            ? relativeOrAbsolute
            : path.join(sourceRoot, relativeOrAbsolute);
        return readJson(file)[property].map(record =>
            `${targetType}:${canonicalSlug(record.source)}:${canonicalSlug(record.name)}`);
    }));
}

const references = new Set();
for (const file of filesUnder(canonicalRoot)) {
    if (file === outputFile || path.basename(file) === 'source-identities.json') continue;
    collectReferences(readJson(file), references);
}

const targetTypes = new Set([...references].map(id => id.split(':')[0]));
const sourceIndexes = new Map([...targetTypes].map(targetType => [targetType, sourceIds(targetType)]));

const missing = [...references].filter(id => !sourceIndexes.get(id.split(':')[0]).has(id));
if (missing.length > 0) {
    throw new Error(`Canonical references absent from pinned source catalogs: ${missing.join(', ')}`);
}

const output = [...new Set([...sourceIndexes.values()].flatMap(identities => [...identities]))]
    .sort((left, right) => left.localeCompare(right));
fs.writeFileSync(outputFile, `${JSON.stringify(output, null, 2)}\n`);
process.stdout.write(`${output.length} pinned source identities indexed for ${targetTypes.size} referenced types\n`);
