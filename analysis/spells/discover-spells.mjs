import { readdir, readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const projectRoot = path.resolve(scriptDirectory, '../..');
const spellDirectory = path.join(projectRoot, 'src/main/resources/data/spells');
const inventoryOutput = path.join(scriptDirectory, 'normalized-field-inventory.json');
const summaryOutput = path.join(scriptDirectory, 'discovery-summary.json');

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

function jsonType(value) {
    if (value === null) {
        return 'null';
    }
    if (Array.isArray(value)) {
        return 'array';
    }
    return typeof value === 'object' ? 'object' : typeof value;
}

function visit(value, currentPath, parentPath, paths, relativePath, primarySource = null) {
    const record = paths.get(currentPath) ?? {
        path: currentPath,
        parentPath,
        occurrences: 0,
        types: {},
        files: {},
        primarySources: {},
    };
    record.occurrences += 1;
    const type = jsonType(value);
    record.types[type] = (record.types[type] ?? 0) + 1;
    const fileRecord = record.files[relativePath] ?? { occurrences: 0, types: {} };
    fileRecord.occurrences += 1;
    fileRecord.types[type] = (fileRecord.types[type] ?? 0) + 1;
    record.files[relativePath] = fileRecord;
    if (primarySource) {
        record.primarySources[primarySource] = (record.primarySources[primarySource] ?? 0) + 1;
    }
    paths.set(currentPath, record);
    if (Array.isArray(value)) {
        for (const item of value) {
            const itemSource = currentPath === 'spell' && item && typeof item === 'object'
                ? item.source ?? null
                : primarySource;
            visit(item, `${currentPath}[]`, currentPath, paths, relativePath, itemSource);
        }
        return;
    }
    if (value !== null && typeof value === 'object') {
        for (const [key, child] of Object.entries(value)) {
            visit(child, `${currentPath}.${key}`, currentPath, paths, relativePath, primarySource);
        }
    }
}

function semanticCategory(pathName) {
    const segment = pathName.split('.').at(-1).replaceAll('[]', '').toLowerCase();
    const metadata = ['source', 'page', 'sources', 'additionalsources', 'othersources', 'referencesources',
        'reprintedas', '_meta', '_copy', '_versions', 'srd', 'srd52', 'basicrules', 'hasfluff'];
    if (metadata.includes(segment)) {
        return 'source or import metadata';
    }
    const descriptive = ['entries', 'entrieshigherlevel', 'text', 'name', 'alias'];
    if (descriptive.includes(segment)) {
        return 'identity or descriptive content';
    }
    const mechanics = ['level', 'school', 'time', 'range', 'components', 'duration', 'meta', 'damage',
        'scalingleveldice', 'savingthrow', 'spellattack', 'conditioninflict', 'conditionimmune',
        'damageinflict', 'damageimmune', 'damageresist', 'damagevulnerable', 'abilitycheck'];
    if (mechanics.includes(segment)) {
        return 'structured mechanical property';
    }
    return 'classification, targeting, or auxiliary property; review semantics';
}

const files = await findJsonFiles(spellDirectory);
const fieldPaths = new Map();
const fileRecords = [];
const spells = [];
const rootCollections = new Map();
const primarySources = new Map();
const relatedSources = new Map();
const rootProperties = new Map();

for (const absolutePath of files) {
    const relativePath = path.relative(projectRoot, absolutePath).replaceAll('\\', '/');
    const document = JSON.parse(await readFile(absolutePath, 'utf8'));
    const collections = {};
    for (const [key, value] of Object.entries(document)) {
        rootProperties.set(key, (rootProperties.get(key) ?? 0) + 1);
        if (Array.isArray(value)) {
            collections[key] = value.length;
            rootCollections.set(key, (rootCollections.get(key) ?? 0) + value.length);
            visit(value, key, '$', fieldPaths, relativePath);
            if (key === 'spell') {
                for (const spell of value) {
                    spells.push({ file: relativePath, ...spell });
                    for (const source of [spell.source].filter(Boolean)) {
                        primarySources.set(source, (primarySources.get(source) ?? 0) + 1);
                    }
                    for (const reference of [
                        ...(spell.additionalSources ?? []),
                        ...(spell.otherSources ?? []),
                        ...(spell.reprintedAs ?? []),
                        ...(spell.referenceSources ?? []),
                    ]) {
                        const source = typeof reference === 'string'
                            ? reference.split('|')[1] ?? reference
                            : reference.source ?? reference.uid?.split('|')[1];
                        if (source) {
                            relatedSources.set(source, (relatedSources.get(source) ?? 0) + 1);
                        }
                    }
                }
            }
        } else {
            visit(value, key, '$', fieldPaths, relativePath);
        }
    }
    fileRecords.push({ file: relativePath, rootProperties: Object.keys(document), collections });
}

const identityGroups = new Map();
const idGroups = new Map();
for (const spell of spells) {
    const identity = `${spell.name}|${spell.source}`;
    const ids = `spell:${String(spell.source).toLowerCase()}:${String(spell.name).toLowerCase()
        .normalize('NFKD').replace(/[\u0300-\u036f]/g, '').replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '')}`;
    identityGroups.set(identity, [...(identityGroups.get(identity) ?? []), spell]);
    idGroups.set(ids, [...(idGroups.get(ids) ?? []), spell]);
}

const copyRecords = spells.filter((spell) => spell._copy);
const versionRecords = spells.filter((spell) => spell._versions);
const copyBases = copyRecords.map((spell) => ({
    name: spell.name,
    source: spell.source,
    copy: spell._copy,
}));
const identityCollisions = [...identityGroups.entries()]
    .filter(([, records]) => records.length > 1)
    .map(([identity, records]) => ({
        identity,
        records: records.map(({ name, source, file }) => ({ name, source, file })),
    }));
const readableIdCollisions = [...idGroups.entries()]
    .filter(([, records]) => records.length > 1)
    .map(([id, records]) => ({
        id,
        records: records.map(({ name, source, file }) => ({ name, source, file })),
    }));
const sameNameGroups = new Map();
for (const spell of spells) {
    sameNameGroups.set(spell.name, [...(sameNameGroups.get(spell.name) ?? []), spell]);
}
const sameNameAcrossSources = [...sameNameGroups.entries()]
    .filter(([, records]) => new Set(records.map((record) => record.source)).size > 1)
    .map(([name, records]) => ({
        name,
        sources: [...new Set(records.map((record) => record.source))].sort(),
        records: records.map(({ source, file }) => ({ source, file })),
    }));

const registryText = await readFile(
    path.join(projectRoot, 'src/main/resources/data/sources/registry.json'),
    'utf8',
);
const registry = JSON.parse(registryText.replace(/^\uFEFF/, ''));
const registeredCodes = new Set();
for (const source of registry.sources) {
    registeredCodes.add(source.code.toLowerCase());
    for (const alias of source.aliases ?? []) {
        registeredCodes.add(alias.toLowerCase());
    }
}
const referencedSources = [...new Set([...primarySources.keys(), ...relatedSources.keys()])].sort();
const missingRegisteredSources = referencedSources.filter((source) => !registeredCodes.has(source.toLowerCase()));
const spellIdentities = new Map();
for (const spell of spells) {
    const identity = `${spell.name}|${spell.source}`;
    spellIdentities.set(identity, (spellIdentities.get(identity) ?? 0) + 1);
}
const reprintReferences = spells.flatMap((spell) => (spell.reprintedAs ?? []).map((target) => ({
    from: `${spell.name}|${spell.source}`,
    to: target,
})));
const unresolvedReprints = reprintReferences.filter((reference) => !spellIdentities.has(reference.to));
const classReferences = JSON.parse(await readFile(
    path.join(projectRoot, 'analysis/classes/cross-catalog-references.json'),
    'utf8',
)).structured.filter((reference) => reference.kind === 'spell');
const spellIds = new Set([...identityGroups.keys()].map((identity) => identity.toLowerCase()));
const spellNames = new Map();
for (const spell of spells) {
    const nameKey = spell.name.toLowerCase();
    spellNames.set(nameKey, [...(spellNames.get(nameKey) ?? []), spell]);
}
const classReferenceResults = classReferences.reduce((result, reference) => {
    const [rawName, source = ''] = reference.uid.split('|');
    const name = rawName.split('#')[0];
    const explicitSource = source.split('#')[0];
    if (explicitSource) {
        result.sourceQualified += 1;
        const identity = `${name}|${explicitSource}`.toLowerCase();
        if (spellIds.has(identity)) {
            result.sourceQualifiedExactMatches += 1;
        } else {
            result.sourceQualifiedUnresolved.push(reference.uid);
        }
        return result;
    }

    result.unqualified += 1;
    const candidates = spellNames.get(name.toLowerCase()) ?? [];
    if (candidates.length === 0) {
        result.unqualifiedMissing.push(reference.uid);
    } else if (candidates.length === 1) {
        result.unqualifiedUniqueCandidates.push(reference.uid);
    } else {
        result.unqualifiedAmbiguous += 1;
    }
    return result;
}, {
    sourceQualified: 0,
    sourceQualifiedExactMatches: 0,
    sourceQualifiedUnresolved: [],
    unqualified: 0,
    unqualifiedAmbiguous: 0,
    unqualifiedUniqueCandidates: [],
    unqualifiedMissing: [],
});

const fieldInventory = {
    generatedBy: 'analysis/spells/discover-spells.mjs',
    corpusRoot: 'src/main/resources/data/spells',
    sourceFileCount: files.length,
    rootCollectionCounts: Object.fromEntries([...rootCollections.entries()].sort()),
    rootPropertyFileCounts: Object.fromEntries([...rootProperties.entries()].sort()),
    uniqueFieldPathCount: fieldPaths.size,
    fields: [...fieldPaths.values()]
        .map((field) => {
            const parentOccurrences = field.parentPath === '$'
                ? files.length
                : (fieldPaths.get(field.parentPath)?.occurrences ?? 0);
            return {
                ...field,
                parentOccurrences,
                missingFromObservedParent: Math.max(0, parentOccurrences - field.occurrences),
                optional: field.occurrences < parentOccurrences,
                structuralVariants: Object.keys(field.types).length,
                semanticCategory: semanticCategory(field.path),
                primarySourceOccurrences: field.primarySources,
                editionOccurrences: {
                    PHB: field.primarySources.PHB ?? 0,
                    XPHB: field.primarySources.XPHB ?? 0,
                },
                optionalityScope: field.path.endsWith('[]')
                    ? 'array member count; parent count difference may indicate an empty array'
                    : 'property count relative to observed parent nodes',
            };
        })
        .sort((left, right) => left.path.localeCompare(right.path)),
};

const summary = {
    sourceFileCount: files.length,
    files: fileRecords,
    rootCollectionCounts: Object.fromEntries([...rootCollections.entries()].sort()),
    spellCount: spells.length,
    uniquePrimarySourceCount: primarySources.size,
    primarySourceCounts: Object.fromEntries([...primarySources.entries()].sort()),
    relatedSourceCounts: Object.fromEntries([...relatedSources.entries()].sort()),
    referencedSources,
    distinctSourceCount: new Set([...primarySources.keys(), ...relatedSources.keys()]).size,
    sourceRegistry: {
        registeredIdentityCount: registry.sources.length,
        missingIdentityCount: missingRegisteredSources.length,
        missingIdentities: missingRegisteredSources,
    },
    copyCount: copyRecords.length,
    copyBases,
    versionCount: versionRecords.length,
    reprintReferenceCount: reprintReferences.length,
    unresolvedReprintCount: unresolvedReprints.length,
    unresolvedReprints,
    classSpellReferenceFeasibility: classReferenceResults,
    identityCollisionCount: identityCollisions.length,
    identityCollisions,
    readableIdCollisionCount: readableIdCollisions.length,
    readableIdCollisions,
    repeatedNameAcrossSourcesCount: sameNameAcrossSources.length,
    repeatedNameAcrossSources: sameNameAcrossSources,
    uniqueFieldPathCount: fieldPaths.size,
};

await writeFile(inventoryOutput, `${JSON.stringify(fieldInventory, null, 2)}\n`);
await writeFile(summaryOutput, `${JSON.stringify(summary, null, 2)}\n`);
console.log(JSON.stringify({
    sourceFileCount: files.length,
    spellCount: spells.length,
    primarySourceCount: primarySources.size,
    distinctSourceCount: summary.distinctSourceCount,
    sourceRegistryMissingCount: missingRegisteredSources.length,
    copyCount: copyRecords.length,
    versionCount: versionRecords.length,
    uniqueFieldPathCount: fieldPaths.size,
    identityCollisionCount: identityCollisions.length,
    readableIdCollisionCount: readableIdCollisions.length,
    repeatedNameAcrossSourcesCount: sameNameAcrossSources.length,
    reprintReferenceCount: reprintReferences.length,
    unresolvedReprintCount: unresolvedReprints.length,
}, null, 2));
