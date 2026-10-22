// Read-only Phase 4 audit. Projects every source identity and metadata record in memory.
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');
const assert = require('node:assert/strict');
const Ajv2020 = require('ajv/dist/2020').default;
const addFormats = require('ajv-formats').default;

const workspace = path.resolve(__dirname, '../..');
const dataRoot = path.join(workspace, 'src/main/resources/data');
const evidence = JSON.parse(fs.readFileSync(path.join(__dirname, 'source-registry-evidence.json'), 'utf8'));
const expectedGroups = new Set([
    'core', 'screen', 'setting', 'setting-alt', 'organized-play',
    'supplement', 'supplement-alt', 'other', 'recipe', 'homecraft'
]);

function sha256(bytes) {
    return crypto.createHash('sha256').update(bytes).digest('hex');
}

function readInput(file) {
    const bytes = fs.readFileSync(path.join(dataRoot, file));
    return {
        bytes,
        value: JSON.parse(bytes.toString('utf8').replace(/^\uFEFF/, ''))
    };
}

function optionalString(record, key, target, targetKey = key) {
    const value = record[key];
    if (value === undefined || value === null) return;
    assert.equal(typeof value, 'string', `${key} must be a string when supplied`);
    target[targetKey] = value;
}

function mapPublication(kind, record, sourceCode) {
    assert.equal(record.source, sourceCode);
    const publication = { kind };
    if (record.id !== sourceCode) publication.publicationId = record.id;
    optionalString(record, 'name', publication);
    optionalString(record, 'published', publication);
    optionalString(record, 'revised', publication);
    optionalString(record, 'group', publication);
    if (Array.isArray(record.alias) && record.alias.length > 0) {
        publication.titleAliases = [...record.alias];
    }
    optionalString(record, 'parentSource', publication);
    return publication;
}

const inputs = {
    registry: readInput('sources/registry.json'),
    books: readInput('books.json'),
    adventures: readInput('adventures.json')
};
const inputFiles = [
    ['sources/registry.json', inputs.registry.bytes],
    ['books.json', inputs.books.bytes],
    ['adventures.json', inputs.adventures.bytes]
];
for (const [file, bytes] of inputFiles) {
    const expected = evidence.analysisFiles.find(item => item.file === file);
    assert.ok(expected, `Phase 1 evidence must include ${file}`);
    assert.equal(sha256(bytes), expected.sha256, `${file} differs from Phase 1 evidence`);
}

const registry = inputs.registry.value.sources;
const books = inputs.books.value.book;
const adventures = inputs.adventures.value.adventure;
assert.ok(Array.isArray(registry) && Array.isArray(books) && Array.isArray(adventures));

const schema = JSON.parse(fs.readFileSync(
    path.join(workspace, 'schemas/source/v1/source.schema.json'), 'utf8'));
const ajv = new Ajv2020({ allErrors: true, strict: true, validateSchema: true });
addFormats(ajv);
assert.equal(ajv.validateSchema(schema), true, 'Draft 2020-12 meta-validation failed');
const validateSource = ajv.compile(schema);

const registryByCode = new Map();
const normalizedByCode = new Map();
const codesLower = new Map();
const codeAliases = new Map();
const codeAliasCollisions = [];
const metadataSourcesMissingFromRegistry = [];
const publicationsBySource = new Map();
const groupCounts = new Map();
const propertyCounts = new Map();
const publicationIdsDifferingFromSource = [];
const publicationIdentityOwners = new Map();
const titleAliasFrequency = new Map();
const parentTokenCounts = new Map();
const parentResolutions = { registryCode: 0, publicationId: 0, unresolved: 0, ambiguous: 0 };
const parentOccurrenceMatches = { registryCode: 0, outsideRegistry: 0 };
const sourcePropertyCounts = { codeAliases: 0, publications: 0, codeOnly: 0 };

for (const [index, entry] of registry.entries()) {
    assert.equal(typeof entry.code, 'string');
    const lowerCode = entry.code.toLocaleLowerCase('en-US');
    assert.ok(!registryByCode.has(entry.code), `duplicate registry code ${entry.code}`);
    assert.ok(!codesLower.has(lowerCode), `case-insensitive registry code collision ${entry.code}`);
    registryByCode.set(entry.code, { entry, index });
    codesLower.set(lowerCode, entry.code);
}

for (const { entry } of registryByCode.values()) {
    for (const alias of entry.aliases ?? []) {
        const key = alias.toLocaleLowerCase('en-US');
        if (codesLower.has(key)) codeAliasCollisions.push({ alias, code: entry.code, collidesWith: codesLower.get(key) });
        if (codeAliases.has(key)) codeAliasCollisions.push({
            alias, code: entry.code, collidesWithAliasOf: codeAliases.get(key)
        });
        codeAliases.set(key, entry.code);
    }
}

const publicationRows = [
    ...books.map((record, index) => ({ kind: 'book', record, index, file: 'books.json' })),
    ...adventures.map((record, index) => ({ kind: 'adventure', record, index, file: 'adventures.json' }))
];
for (const row of publicationRows) {
    if (!registryByCode.has(row.record.source)) {
        metadataSourcesMissingFromRegistry.push({ kind: row.kind, index: row.index, source: row.record.source });
        continue;
    }
    if (!publicationsBySource.has(row.record.source)) publicationsBySource.set(row.record.source, []);
    publicationsBySource.get(row.record.source).push(row);
    for (const key of Object.keys(row.record)) {
        propertyCounts.set(`${row.kind}.${key}`, (propertyCounts.get(`${row.kind}.${key}`) ?? 0) + 1);
    }
    if (row.record.group !== undefined && row.record.group !== null) {
        groupCounts.set(row.record.group, (groupCounts.get(row.record.group) ?? 0) + 1);
        assert.ok(expectedGroups.has(row.record.group), `unapproved group enum value ${row.record.group}`);
    }
    if (row.record.id !== row.record.source) {
        publicationIdsDifferingFromSource.push({ kind: row.kind, id: row.record.id, source: row.record.source });
    }
    const identityKey = `${row.record.source}\0${row.kind}\0${row.record.id}`;
    assert.ok(!publicationIdentityOwners.has(identityKey), `publication identity collision ${row.record.id}`);
    publicationIdentityOwners.set(identityKey, row);
    for (const alias of row.record.alias ?? []) {
        titleAliasFrequency.set(alias, (titleAliasFrequency.get(alias) ?? 0) + 1);
    }
    if (typeof row.record.parentSource === 'string') {
        parentTokenCounts.set(row.record.parentSource, (parentTokenCounts.get(row.record.parentSource) ?? 0) + 1);
        if (codesLower.has(row.record.parentSource.toLocaleLowerCase('en-US'))) {
            parentOccurrenceMatches.registryCode++;
        } else {
            parentOccurrenceMatches.outsideRegistry++;
        }
    }
}

for (const { entry } of registryByCode.values()) {
    const source = { code: entry.code };
    if (Array.isArray(entry.aliases) && entry.aliases.length > 0) {
        source.codeAliases = [...entry.aliases];
        sourcePropertyCounts.codeAliases++;
    }
    const rows = publicationsBySource.get(entry.code) ?? [];
    if (rows.length > 0) {
        source.publications = rows.map(row => mapPublication(row.kind, row.record, entry.code));
        sourcePropertyCounts.publications++;
    } else {
        sourcePropertyCounts.codeOnly++;
    }
    assert.equal(validateSource(source), true,
        `Schema rejected Source ${entry.code}: ${JSON.stringify(validateSource.errors)}`);
    normalizedByCode.set(source.code, source);
}

const publicationIdSources = new Map();
for (const row of publicationRows) {
    if (!publicationIdSources.has(row.record.id)) publicationIdSources.set(row.record.id, []);
    publicationIdSources.get(row.record.id).push(row);
}
for (const token of parentTokenCounts.keys()) {
    const codeTarget = codesLower.get(token.toLocaleLowerCase('en-US'));
    const publicationTargets = publicationIdSources.get(token) ?? [];
    if (codeTarget && publicationTargets.length > 0) parentResolutions.ambiguous++;
    else if (codeTarget) parentResolutions.registryCode++;
    else if (publicationTargets.length === 1) parentResolutions.publicationId++;
    else if (publicationTargets.length === 0) parentResolutions.unresolved++;
    else parentResolutions.ambiguous++;
}

assert.equal(normalizedByCode.size, registry.length);
assert.equal(publicationRows.length, books.length + adventures.length);
assert.equal(metadataSourcesMissingFromRegistry.length, 0);
assert.equal(codeAliasCollisions.length, 0);

const result = {
    sourceHashes: Object.fromEntries(inputFiles.map(([file, bytes]) => [file, sha256(bytes)])),
    schema: { id: schema.$id, draft: schema.$schema, metaValidation: 'passed' },
    coverage: {
        registrySources: registry.length,
        schemaValidatedSources: normalizedByCode.size,
        codeOnlySources: sourcePropertyCounts.codeOnly,
        sourcesWithCodeAliases: sourcePropertyCounts.codeAliases,
        sourcesWithPublications: sourcePropertyCounts.publications,
        bookMetadataRecords: books.length,
        adventureMetadataRecords: adventures.length,
        associatedPublicationRecords: publicationRows.length,
        sourceAssociationsMissingFromRegistry: metadataSourcesMissingFromRegistry.length
    },
    identity: {
        uniqueSourceCodes: registryByCode.size,
        uniqueSourceCodesCaseInsensitive: codesLower.size,
        codeAliasCount: codeAliases.size,
        codeAliasCollisions: codeAliasCollisions.length,
        publicationIdentityKeys: publicationIdentityOwners.size,
        publicationIdsDifferingFromSource: publicationIdsDifferingFromSource.length,
        differingPublicationIds: publicationIdsDifferingFromSource,
        titleAliasValues: titleAliasFrequency.size,
        repeatedTitleAliases: [...titleAliasFrequency.entries()]
            .filter(([, count]) => count > 1).map(([alias, count]) => ({ alias, count }))
    },
    metadataProperties: Object.fromEntries([...propertyCounts.entries()].sort()),
    groups: Object.fromEntries([...groupCounts.entries()].sort()),
    parentReferences: {
        occurrenceCount: [...parentTokenCounts.values()].reduce((sum, count) => sum + count, 0),
        distinctTokens: parentTokenCounts.size,
        tokenOccurrences: Object.fromEntries([...parentTokenCounts.entries()].sort()),
        occurrenceRegistryMembership: parentOccurrenceMatches,
        uniqueTokenResolutionClasses: parentResolutions
    },
    outputWritten: false
};

process.stdout.write(`${JSON.stringify(result, null, 2)}\n`);
