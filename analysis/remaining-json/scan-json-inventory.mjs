import { createHash } from 'node:crypto';
import { readFile, readdir, stat, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(scriptDirectory, '../..');
const dataRoot = path.join(repositoryRoot, 'src/main/resources/data');
const outputDirectory = scriptDirectory;

const implementedRootFamilies = new Set([
    'adventures',
    'actions',
    'backgrounds',
    'books',
    'charcreationoptions',
    'conditionsdiseases',
    'cultsboons',
    'feats',
    'items',
    'items-base',
    'languages',
    'magicvariants',
    'names',
    'optionalfeatures',
    'races',
    'senses',
    'skills'
]);

const knownCollections = new Set([
    'monster', 'spell', 'class', 'subclass', 'classFeature', 'subclassFeature', 'background',
    'race', 'subrace', 'feat', 'optionalfeature', 'item', 'baseitem', 'magicvariant', 'action',
    'skill', 'language', 'sense', 'condition', 'disease', 'book', 'adventure', 'bastion',
    'charoption', 'cult', 'boon', 'deity', 'encounter', 'facility', 'crochetPattern', 'dragon',
    'hazard', 'hoard', 'individual', 'name', 'object', 'recipe', 'reward', 'trap', 'variantrule',
    'vehicle', 'vehicleUpgrade', 'artObjects', 'gems', 'linkedLootTables', 'magicItems', 'status',
    'itemGroup', 'itemProperty', 'itemType', 'itemMastery', 'languageScript', 'legendaryGroup',
    'itemEntry', 'dragonMundaneItems'
]);

const unimplementedRootFamilies = new Set([
    'bastions',
    'encounters',
    'homecrafts',
    'loot',
    'objects',
    'recipes',
    'rewards',
    'trapshazards',
    'vehicles'
]);

function classifyFile(relativePath, document) {
    const normalizedPath = relativePath.replaceAll('\\', '/');
    const filename = path.posix.basename(normalizedPath, '.json');

    if (normalizedPath === 'sources/registry.json') {
        return { family: 'source-registry', role: 'registry', status: 'implemented-support' };
    }
    if (normalizedPath.startsWith('classes/')) {
        return { family: 'classes', role: 'class-root-and-feature-data', status: 'implemented' };
    }
    if (normalizedPath.startsWith('spells/')) {
        return { family: 'spells', role: 'spell-root-data', status: 'implemented' };
    }
    if (normalizedPath.startsWith('monsters/')) {
        if (filename === 'template') {
            return { family: 'monsters', role: 'copy-template-support', status: 'implemented-support' };
        }
        if (filename === 'legendarygroups') {
            return { family: 'monsters', role: 'legendary-group-support', status: 'implemented-support' };
        }
        return { family: 'monsters', role: 'monster-root-data', status: 'implemented' };
    }
    if (filename.startsWith('fluff-')) {
        return { family: filename.slice('fluff-'.length), role: 'fluff', status: 'auxiliary-unimplemented' };
    }

    const root = path.posix.basename(normalizedPath, '.json');
    if (implementedRootFamilies.has(root)) {
        return { family: root, role: 'domain-root-data', status: 'implemented' };
    }
    if (root === 'variantrules') {
        return { family: 'variant-rules', role: 'root-domain-data', status: 'unimplemented' };
    }
    if (root === 'deities') {
        return { family: 'deities', role: 'root-domain-data', status: 'unimplemented' };
    }
    if (unimplementedRootFamilies.has(root)) {
        return { family: root, role: 'root-domain-data', status: 'unimplemented' };
    }

    return {
        family: 'unclassified',
        role: 'requires-manual-classification',
        status: 'unclassified',
        observedRootKeys: Object.keys(document).sort()
    };
}

function inferCollectionRole(key, filename) {
    if (key === '_meta') {
        return 'metadata';
    }
    if (filename === 'adventures' && key === 'adventure') {
        return 'adventure-publication-index';
    }
    if (filename === 'books' && key === 'book') {
        return 'book-publication-index';
    }
    if (filename === 'names' && key === 'name') {
        return 'name-table';
    }
    if (filename === 'loot') {
        return 'treasure-table-family';
    }
    if (filename === 'bastions' && key === 'facility') {
        return 'bastion-facility-definition';
    }
    if (/fluff/i.test(key) || filename.startsWith('fluff-')) {
        return 'fluff';
    }
    if (/index/i.test(key) || /index/i.test(filename)) {
        return 'index';
    }
    if (knownCollections.has(key)) {
        return 'definition-or-support-collection';
    }
    return 'auxiliary-or-unclassified-collection';
}

function jsonType(value) {
    if (value === null) {
        return 'null';
    }
    if (Array.isArray(value)) {
        return 'array';
    }
    if (typeof value === 'object') {
        return 'object';
    }
    return typeof value;
}

function addPath(paths, pathName, value) {
    const type = jsonType(value);
    const current = paths.get(pathName) ?? { occurrences: 0, types: new Map() };
    current.occurrences += 1;
    current.types.set(type, (current.types.get(type) ?? 0) + 1);
    paths.set(pathName, current);
}

function scanValue(value, pathName, paths, metadata, depth = 0) {
    if (depth > 80) {
        return;
    }
    addPath(paths, pathName, value);

    if (Array.isArray(value)) {
        for (const item of value) {
            scanValue(item, `${pathName}[]`, paths, metadata, depth + 1);
        }
        return;
    }
    if (value === null || typeof value !== 'object') {
        if (typeof value === 'string') {
            if (/\{@[\w-]+\s/.test(value)) {
                for (const match of value.matchAll(/\{@([\w-]+)\s/g)) {
                    metadata.markupTags.set(match[1], (metadata.markupTags.get(match[1]) ?? 0) + 1);
                }
            }
            if (/^[a-z0-9-]+:\s*[^|]+\|[^|]+\|/i.test(value)) {
                metadata.uidLikeStrings += 1;
            }
        }
        return;
    }

    for (const [key, child] of Object.entries(value)) {
        if (key === 'source' && typeof child === 'string') {
            metadata.allSourceProperties.set(child, (metadata.allSourceProperties.get(child) ?? 0) + 1);
        }
        if (key === '_copy') {
            metadata.copyDeclarations += 1;
        }
        if (key === '_versions') {
            metadata.versionEntryCount += Array.isArray(child) ? child.length : 1;
        }
        if (key === '_abstract') {
            metadata.abstractDeclarations += 1;
        }
        if (key === '_implementations') {
            metadata.implementationEntryCount += Array.isArray(child) ? child.length : 1;
        }
        if (key === 'reprintedAs' && Array.isArray(child)) {
            metadata.reprintReferenceCount += child.length;
        }
        if (key === 'otherSources' && Array.isArray(child)) {
            metadata.secondarySourceReferenceCount += child.length;
        }
        scanValue(child, `${pathName}.${key}`, paths, metadata, depth + 1);
    }
}

async function findJsonFiles(directory) {
    const entries = await readdir(directory, { withFileTypes: true });
    const files = [];
    for (const entry of entries) {
        const fullPath = path.join(directory, entry.name);
        if (entry.isDirectory()) {
            files.push(...await findJsonFiles(fullPath));
        } else if (entry.isFile() && entry.name.toLowerCase().endsWith('.json')) {
            files.push(fullPath);
        }
    }
    return files.sort((left, right) => left.localeCompare(right));
}

function summarizeMap(map) {
    return [...map.entries()]
        .sort(([left], [right]) => left.localeCompare(right))
        .map(([key, count]) => ({ key, count }));
}

const files = await findJsonFiles(dataRoot);
const fileRecords = [];
const familySummaries = new Map();
const rootCollectionTotals = new Map();
const globalSourceCounts = new Map();
const allSourcePropertyCounts = new Map();
const rootPathSummaries = new Map();
let parseFailures = 0;

for (const absolutePath of files) {
    const relativePath = path.relative(dataRoot, absolutePath).replaceAll('\\', '/');
    const sourceBytes = await readFile(absolutePath);
    const document = JSON.parse(sourceBytes.toString('utf8').replace(/^\uFEFF/, ''));
    const fileStats = await stat(absolutePath);
    const classification = classifyFile(relativePath, document);
    const localPaths = new Map();
    const metadata = {
        copyDeclarations: 0,
        versionEntryCount: 0,
        abstractDeclarations: 0,
        implementationEntryCount: 0,
        reprintReferenceCount: 0,
        secondarySourceReferenceCount: 0,
        uidLikeStrings: 0,
        markupTags: new Map(),
        allSourceProperties: new Map()
    };

    scanValue(document, '$', localPaths, metadata);
    for (const [source, count] of metadata.allSourceProperties) {
        allSourcePropertyCounts.set(source, (allSourcePropertyCounts.get(source) ?? 0) + count);
    }

    const rootCollections = [];
    for (const [key, value] of Object.entries(document)) {
        if (Array.isArray(value)) {
            const sourceCounts = new Map();
            const sourceNameCounts = new Map();
            for (const record of value) {
                if (record && typeof record === 'object' && !Array.isArray(record)) {
                    const source = record.source;
                    const name = record.name;
                    if (typeof source === 'string') {
                        sourceCounts.set(source, (sourceCounts.get(source) ?? 0) + 1);
                        globalSourceCounts.set(source, (globalSourceCounts.get(source) ?? 0) + 1);
                    }
                    if (typeof source === 'string' && typeof name === 'string') {
                        const identity = `${source}|${name.toLocaleLowerCase('en-US')}`;
                        sourceNameCounts.set(identity, (sourceNameCounts.get(identity) ?? 0) + 1);
                    }
                }
            }
            const collection = {
                key,
                role: inferCollectionRole(key, path.basename(absolutePath, '.json')),
                rootRecordCount: value.length,
                recordsWithSource: [...sourceCounts.values()].reduce((sum, count) => sum + count, 0),
                distinctPrimarySources: [...sourceCounts.keys()].sort(),
                primarySourceCounts: summarizeMap(sourceCounts),
                sourceNameCollisionGroups: [...sourceNameCounts.entries()]
                    .filter(([, count]) => count > 1)
                    .sort(([left], [right]) => left.localeCompare(right))
                    .map(([identity, count]) => ({ identity, count }))
            };
            rootCollections.push(collection);
            const collectionKey = `${classification.family}:${key}`;
            const total = rootCollectionTotals.get(collectionKey) ?? {
                family: classification.family,
                key,
                count: 0,
                files: 0
            };
            total.count += value.length;
            total.files += 1;
            rootCollectionTotals.set(collectionKey, total);
        } else if (key !== '_meta') {
            const collectionKey = `${classification.family}:${key}`;
            const total = rootCollectionTotals.get(collectionKey) ?? {
                family: classification.family,
                key,
                count: 0,
                files: 0,
                nonArrayRootValue: true
            };
            total.files += 1;
            rootCollectionTotals.set(collectionKey, total);
        }
    }

    const pathSummary = [...localPaths.entries()]
        .map(([pathName, value]) => ({
            path: pathName,
            occurrences: value.occurrences,
            types: summarizeMap(value.types)
        }))
        .sort((left, right) => left.path.localeCompare(right.path));
    const familyPathMap = rootPathSummaries.get(classification.family) ?? new Map();
    for (const pathEntry of pathSummary) {
        const current = familyPathMap.get(pathEntry.path) ?? { occurrences: 0, files: 0, types: new Map() };
        current.occurrences += pathEntry.occurrences;
        current.files += 1;
        for (const typeEntry of pathEntry.types) {
            current.types.set(typeEntry.key, (current.types.get(typeEntry.key) ?? 0) + typeEntry.count);
        }
        familyPathMap.set(pathEntry.path, current);
    }
    rootPathSummaries.set(classification.family, familyPathMap);

    const family = familySummaries.get(classification.family) ?? {
        family: classification.family,
        status: classification.status,
        fileCount: 0,
        rootCollections: new Map(),
        copyDeclarations: 0,
        versionEntryCount: 0,
        abstractDeclarations: 0,
        implementationEntryCount: 0,
        reprintReferenceCount: 0,
        secondarySourceReferenceCount: 0,
        uidLikeStrings: 0,
        markupTags: new Map()
    };
    family.fileCount += 1;
    family.copyDeclarations += metadata.copyDeclarations;
    family.versionEntryCount += metadata.versionEntryCount;
    family.abstractDeclarations += metadata.abstractDeclarations;
    family.implementationEntryCount += metadata.implementationEntryCount;
    family.reprintReferenceCount += metadata.reprintReferenceCount;
    family.secondarySourceReferenceCount += metadata.secondarySourceReferenceCount;
    family.uidLikeStrings += metadata.uidLikeStrings;
    for (const collection of rootCollections) {
        const total = family.rootCollections.get(collection.key) ?? { count: 0, files: 0 };
        total.count += collection.rootRecordCount;
        total.files += 1;
        family.rootCollections.set(collection.key, total);
    }
    for (const [tag, count] of metadata.markupTags) {
        family.markupTags.set(tag, (family.markupTags.get(tag) ?? 0) + count);
    }
    familySummaries.set(classification.family, family);

    fileRecords.push({
        path: relativePath,
        byteCount: fileStats.size,
        sha256: createHash('sha256').update(sourceBytes).digest('hex'),
        classification,
        rootCollections,
        metadata: {
            copyDeclarations: metadata.copyDeclarations,
            versionEntryCount: metadata.versionEntryCount,
            abstractDeclarations: metadata.abstractDeclarations,
            implementationEntryCount: metadata.implementationEntryCount,
            reprintReferenceCount: metadata.reprintReferenceCount,
            secondarySourceReferenceCount: metadata.secondarySourceReferenceCount,
            uidLikeStrings: metadata.uidLikeStrings,
            markupTagOccurrences: summarizeMap(metadata.markupTags)
        },
        fieldPaths: pathSummary
    });
}

const sourceRegistryPath = path.join(dataRoot, 'sources/registry.json');
const sourceRegistryText = (await readFile(sourceRegistryPath, 'utf8')).replace(/^\uFEFF/, '');
const sourceRegistry = JSON.parse(sourceRegistryText);
const registryCodes = new Set();
function collectSourceCodes(value) {
    if (Array.isArray(value)) {
        for (const item of value) {
            collectSourceCodes(item);
        }
    } else if (value && typeof value === 'object') {
        for (const [key, child] of Object.entries(value)) {
            const isSourceIdentifierKey = ['code', 'json', 'source', 'id'].includes(key);
            if (isSourceIdentifierKey && typeof child === 'string') {
                registryCodes.add(child);
            }
            collectSourceCodes(child);
        }
    }
}
collectSourceCodes(sourceRegistry);

const unclassifiedFiles = fileRecords.filter((record) => record.classification.status === 'unclassified');
if (unclassifiedFiles.length > 0) {
    throw new Error(`Unclassified source files: ${unclassifiedFiles.map((record) => record.path).join(', ')}`);
}

const families = [...familySummaries.values()]
    .map((family) => ({
        family: family.family,
        status: family.status,
        fileCount: family.fileCount,
        rootCollections: [...family.rootCollections.entries()]
            .sort(([left], [right]) => left.localeCompare(right))
            .map(([key, value]) => ({ key, ...value })),
        transformations: {
            copyDeclarations: family.copyDeclarations,
            versionEntryCount: family.versionEntryCount,
            abstractDeclarations: family.abstractDeclarations,
            implementationEntryCount: family.implementationEntryCount,
            reprintReferenceCount: family.reprintReferenceCount,
            secondarySourceReferenceCount: family.secondarySourceReferenceCount
        },
        uidLikeStringCount: family.uidLikeStrings,
        markupTagOccurrences: summarizeMap(family.markupTags)
    }))
    .sort((left, right) => left.family.localeCompare(right.family));

const fieldInventory = [...rootPathSummaries.entries()]
    .sort(([left], [right]) => left.localeCompare(right))
    .map(([family, paths]) => ({
        family,
        uniqueFieldPathCount: paths.size,
        paths: [...paths.entries()]
            .sort(([left], [right]) => left.localeCompare(right))
            .map(([fieldPath, details]) => ({
                path: fieldPath,
                occurrences: details.occurrences,
                contributingFiles: details.files,
                types: summarizeMap(details.types)
            }))
    }));

const inventory = {
    snapshot: '5etools v2.37.0 (project-pinned snapshot; no separate version marker found in resource files)',
    dataRoot: 'src/main/resources/data',
    scannedAt: 'reproducible; timestamp intentionally omitted',
    fileCount: files.length,
    parseFailureCount: parseFailures,
    familyCount: families.length,
    sourceCodesObservedInRootRecords: summarizeMap(globalSourceCounts),
    sourceCodesObservedInAnySourceProperty: summarizeMap(allSourcePropertyCounts),
    sourceRegistryCodeCount: registryCodes.size,
    sourceCodesNotInRegistry: [...allSourcePropertyCounts.keys()].filter((code) => !registryCodes.has(code)).sort(),
    rootCollectionTotals: [...rootCollectionTotals.values()]
        .sort((left, right) => `${left.family}:${left.key}`.localeCompare(`${right.family}:${right.key}`)),
    families,
    fieldInventory
};

await writeFile(path.join(outputDirectory, 'inventory-evidence.json'), `${JSON.stringify(inventory, null, 2)}\n`);
await writeFile(path.join(outputDirectory, 'per-file-classification.json'), `${JSON.stringify({ files: fileRecords }, null, 2)}\n`);
process.stdout.write(JSON.stringify({
    fileCount: inventory.fileCount,
    familyCount: inventory.familyCount,
    parseFailureCount: parseFailures,
    outputFiles: ['inventory-evidence.json', 'per-file-classification.json']
}, null, 2));
