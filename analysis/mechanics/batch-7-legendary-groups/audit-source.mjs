import fs from 'node:fs';
import crypto from 'node:crypto';

const outputDirectory = 'analysis/mechanics/batch-7-legendary-groups';
const dataDirectory = 'src/main/resources/data/monsters';
const hash = value => crypto.createHash('sha256').update(value).digest('hex');
const read = file => JSON.parse(fs.readFileSync(file, 'utf8').replace(/^\uFEFF/, ''));
const groups = read(`${dataDirectory}/legendarygroups.json`).legendaryGroup;
const templateDocument = read(`${dataDirectory}/template.json`);
const templates = templateDocument.legendaryGroupTemplate;
const histogram = values => Object.fromEntries([...new Set(values)].sort()
    .map(value => [value, values.filter(candidate => candidate === value).length]));
const kind = value => value === null ? 'null' : Array.isArray(value) ? 'array' : typeof value;
const schemas = {};
const variants = {};
const sourceCodes = new Set();
const tagCounts = {};

function inspect(value, pointer, originalPointer, owner, schema) {
    const key = `${pointer}:${kind(value)}`;
    const occurrence = schema[key] ??= {count: 0, empty: 0, example: `${owner}${originalPointer}`};
    occurrence.count++;
    if (value === '' || (Array.isArray(value) && value.length === 0)
        || (value && typeof value === 'object' && Object.keys(value).length === 0)) {
        occurrence.empty++;
    }
    if (Array.isArray(value)) {
        value.forEach((child, index) => inspect(child, `${pointer}/*`, `${originalPointer}/${index}`, owner, schema));
    } else if (value && typeof value === 'object') {
        if (value.type) {
            variants[value.type] = (variants[value.type] ?? 0) + 1;
        }
        for (const [key, child] of Object.entries(value)) {
            if (key === 'source' && typeof child === 'string') {
                sourceCodes.add(child);
            }
            inspect(child, `${pointer}/${key}`, `${originalPointer}/${key}`, owner, schema);
        }
    }
}

function countTags(value, counts = {}) {
    if (typeof value === 'string') {
        for (const match of value.matchAll(/\{@([a-zA-Z0-9]+)\b/g)) {
            counts[match[1]] = (counts[match[1]] ?? 0) + 1;
        }
    } else if (value && typeof value === 'object') {
        Object.values(value).forEach(child => countTags(child, counts));
    }
    return counts;
}

for (const [collection, records] of [['legendaryGroup', groups], ['legendaryGroupTemplate', templates]]) {
    const schema = schemas[collection] = {};
    records.forEach((record, index) => inspect(record, '', '', `${collection}[${index}] ${record.name}|${record.source}`, schema));
    tagCounts[collection] = countTags(records);
}

const identity = group => `${group.name}|${group.source}`;
const identities = new Set(groups.map(identity));
const versions = groups.flatMap((group, index) => (group._versions ?? []).map((version, versionIndex) => ({
    parent: identity(group), identity: identity(version), pointer: `/legendaryGroup/${index}/_versions/${versionIndex}`,
    templates: version._templates.map(identity)
})));
const allIdentities = new Set([...identities, ...versions.map(version => version.identity)]);
const references = [];
const monsterSupportInstructions = [];
function inspectMonsterSupportInstructions(value, pointer, filename) {
    if (!value || typeof value !== 'object') {
        return;
    }
    for (const [key, child] of Object.entries(value)) {
        const location = `${pointer}/${key}`;
        if (key === 'legendaryGroup' && pointer.split('/').length > 3) {
            monsterSupportInstructions.push({file: filename, pointer: location, value: child});
        }
        inspectMonsterSupportInstructions(child, location, filename);
    }
}
for (const filename of fs.readdirSync(dataDirectory).filter(name => /^bestiary-.*\.json$/.test(name)).sort()) {
    const document = read(`${dataDirectory}/${filename}`);
    inspectMonsterSupportInstructions(document, '', filename);
    (document.monster ?? []).forEach((monster, index) => {
        if (monster.legendaryGroup) {
            references.push({file: filename, pointer: `/monster/${index}/legendaryGroup`,
                monster: identity(monster), target: identity(monster.legendaryGroup),
                status: allIdentities.has(identity(monster.legendaryGroup)) ? 'RESOLVED' : 'UNRESOLVED'});
        }
    });
}
inspectMonsterSupportInstructions(templateDocument.monsterTemplate, '/monsterTemplate', 'template.json');
const registry = new Set(read('src/main/resources/data/sources/registry.json').sources.map(source => source.code));
const report = {
    resource: 'data/monsters/legendarygroups.json',
    sha256: hash(fs.readFileSync(`${dataDirectory}/legendarygroups.json`)),
    templateSha256: hash(fs.readFileSync(`${dataDirectory}/template.json`)),
    roots: groups.length,
    sourceDistribution: histogram(groups.map(group => group.source)),
    bodyFields: Object.fromEntries(['lairActions', 'regionalEffects', 'mythicEncounter']
        .map(field => [field, groups.filter(group => Object.hasOwn(group, field)).length])),
    bodyCombinations: histogram(groups.map(group => ['lairActions', 'regionalEffects', 'mythicEncounter']
        .filter(field => Object.hasOwn(group, field)).join('+') || 'none')),
    copies: groups.flatMap((group, index) => group._copy ? [{identity: identity(group), parent: identity(group._copy),
        pointer: `/legendaryGroup/${index}/_copy`, modifications: Object.fromEntries(
            Object.entries(group._copy._mod).map(([field, operation]) => [field, {
                mode: operation.mode, items: operation.items.length
            }]))}] : []),
    versions,
    templates: templates.map((template, index) => ({identity: identity(template),
        pointer: `/legendaryGroupTemplate/${index}`, fields: Object.keys(template)})),
    duplicateAuthoredIdentities: groups.length - identities.size,
    duplicateEffectiveIdentities: groups.length + versions.length - allIdentities.size,
    entryVariantsIncludingTransformsAndTemplates: variants,
    schemas,
    lexicalTagCounts: tagCounts,
    sourceCodes: [...sourceCodes].sort(),
    unknownSourceCodes: [...sourceCodes].filter(source => !registry.has(source)).sort(),
    authoredMonsterReferences: references.length,
    authoredMonsterReferenceOutcomes: histogram(references.map(reference => reference.status)),
    monsterSupportInstructions,
    references
};
fs.mkdirSync(outputDirectory, {recursive: true});
fs.writeFileSync(`${outputDirectory}/source-audit.json`, `${JSON.stringify(report, null, 2)}\n`);
const coverage = [
    '# Legendary-group field coverage', '',
    'Generated by `audit-source.mjs` from the pinned source bytes. Counts are authored node visits,',
    'including copy/version/template payloads; wildcard paths aggregate array positions. They are',
    'not effective-definition counts. Every path has a concrete first-example locator in `source-audit.json`.',
    'No null or empty value was observed. JSON numbers here are integral page values.', '',
    'Group identity/body/metadata fields map to LegendaryGroupSourceDte; `_copy` and `_versions`',
    'map to their dedicated records. Template `apply._mod` shares only the identical append/prepend',
    'instruction representation. Entry contents map to shared EntryDte records with deep immutable',
    'snapshots. Unknown fields, unobserved operation modes and unobserved explicit nulls fail loading.',
    'There are no dynamic map keys in the supported source schema.', ''
];
for (const [collection, schema] of Object.entries(schemas)) {
    coverage.push(`## ${collection}`, '', '| Path | JSON type | Occurrences | Empty |', '|---|---|---:|---:|');
    for (const [key, occurrence] of Object.entries(schema).sort(([left], [right]) => left.localeCompare(right, 'en'))) {
        const separator = key.lastIndexOf(':');
        coverage.push(`| \`${key.slice(0, separator) || '/ (root)'}\` | ${key.slice(separator + 1)} | ${occurrence.count} | ${occurrence.empty} |`);
    }
    coverage.push('');
}
coverage.push('## Collection metadata and ownership', '',
    '`legendarygroups.json` also has `_meta.internalCopies: string[]`, exactly `["legendaryGroup"]`.',
    '`template.json` has `_meta.internalCopies`, exactly `["monsterTemplate"]`. Both metadata shapes',
    'are validated using LegendaryGroupMetadataDte. The separate `monsterTemplate` array remains',
    'owned by the existing MonsterTemplateRepository. Unexpected root collections fail this loader.', '',
    'No body-level structured timing, damage, save-outcome, reset or effect-execution fields occur.',
    'Those concepts remain in typed descriptive entries and markup; no mechanics are inferred.', '');
fs.writeFileSync(`${outputDirectory}/FIELD-COVERAGE.md`, coverage.join('\n'));

function files(directory) {
    return fs.readdirSync(directory, {withFileTypes: true}).flatMap(entry => {
        const location = `${directory}/${entry.name}`;
        return entry.isDirectory() ? files(location) : [location];
    });
}
const baselineFile = `${outputDirectory}/pre-implementation-hashes.json`;
if (process.argv.includes('--capture')) {
    if (fs.existsSync(baselineFile)) {
        throw new Error('Refusing to overwrite pre-implementation snapshot');
    }
    const locations = files('src').concat(files('analysis/mechanics/entry-assessment'));
    fs.writeFileSync(baselineFile, `${JSON.stringify(Object.fromEntries(locations.sort()
        .map(file => [file, hash(fs.readFileSync(file))])), null, 2)}\n`);
}
if (fs.existsSync(baselineFile)) {
    const previous = read(baselineFile);
    const changed = Object.entries(previous).filter(([file, expected]) => hash(fs.readFileSync(file)) !== expected)
        .map(([file]) => file);
    if (changed.length) {
        throw new Error(`Pre-existing protected files changed: ${changed.join(', ')}`);
    }
}
console.log(JSON.stringify({roots: report.roots, bodyFields: report.bodyFields,
    bodyCombinations: report.bodyCombinations, copies: report.copies.length, versions: versions.length,
    templates: templates.length, variants, unknownSources: report.unknownSourceCodes,
    rawMonsterReferences: references.length, rawReferenceOutcomes: report.authoredMonsterReferenceOutcomes}, null, 2));
