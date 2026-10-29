// Validate the full Source expansion, provenance, references, and source preservation.
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');
const assert = require('node:assert/strict');
const Ajv2020 = require('ajv/dist/2020').default;
const addFormats = require('ajv-formats').default;

const workspace = path.resolve(__dirname, '../..');
const dataRoot = path.join(workspace, 'src/main/resources/data');
const outputRoot = path.join(workspace, 'datasets/step-1/v1');
const runtimeDatasetPath = path.join(workspace, 'src/main/resources/normalized/sources/sources.json');
const evidence = JSON.parse(fs.readFileSync(path.join(workspace, 'tools/evidence/step-1/source-registry-evidence.json'), 'utf8'));

function sha256(bytes) {
    return crypto.createHash('sha256').update(bytes).digest('hex');
}

function readJson(file) {
    const bytes = fs.readFileSync(file);
    return { bytes, value: JSON.parse(bytes.toString('utf8').replace(/^\uFEFF/, '')) };
}

function jsonPointer(root, pointer) {
    return pointer.split('/').slice(1).reduce((value, rawPart) => {
        const part = rawPart.replaceAll('~1', '/').replaceAll('~0', '~');
        return value[part];
    }, root);
}

function canonicalPublication(kind, record, sourceCode) {
    const result = { kind };
    if (record.id !== sourceCode) result.publicationId = record.id;
    for (const key of ['name', 'published', 'revised', 'group', 'parentSource']) {
        if (record[key] !== undefined && record[key] !== null) result[key] = record[key];
    }
    if (Array.isArray(record.alias) && record.alias.length > 0) result.titleAliases = record.alias;
    return result;
}

const registryInput = readJson(path.join(dataRoot, 'sources/registry.json'));
const booksInput = readJson(path.join(dataRoot, 'books.json'));
const adventuresInput = readJson(path.join(dataRoot, 'adventures.json'));
const registry = registryInput.value.sources;
const books = booksInput.value.book;
const adventures = adventuresInput.value.adventure;
const inputMap = new Map([
    ['sources/registry.json', registryInput],
    ['books.json', booksInput],
    ['adventures.json', adventuresInput]
]);
for (const [file, input] of inputMap) {
    const expected = evidence.analysisFiles.find(entry => entry.file === file);
    assert.ok(expected, `Phase 1 hash missing for ${file}`);
    assert.equal(sha256(input.bytes), expected.sha256, `${file} changed from the approved source baseline`);
}

const schema = JSON.parse(fs.readFileSync(
    path.join(workspace, 'src/main/resources/schemas/source/schema.json'), 'utf8'));
const ajv = new Ajv2020({ allErrors: true, strict: true, validateSchema: true });
addFormats(ajv);
assert.equal(ajv.validateSchema(schema), true, 'Draft 2020-12 schema meta-validation failed');
const validateSource = ajv.compile(schema);

const batch = JSON.parse(fs.readFileSync(runtimeDatasetPath, 'utf8'));
const provenance = JSON.parse(fs.readFileSync(
    path.join(outputRoot, 'source-registry-provenance.json'), 'utf8'));
const parentReview = JSON.parse(fs.readFileSync(
    path.join(outputRoot, 'source-registry-parent-reference-review.json'), 'utf8'));

assert.ok(Array.isArray(batch));
assert.equal(batch.length, registry.length);
const groupedSource = batch.find(source => source.publications?.some(publication => publication.group));
const invalidGroupCandidate = structuredClone(groupedSource);
invalidGroupCandidate.publications.find(publication => publication.group).group = 'unreviewed-new-group';
assert.equal(validateSource(invalidGroupCandidate), false,
    'the closed group enum must reject unapproved future values');
assert.equal(provenance.schemaId, schema.$id);
assert.equal(provenance.sourceCorpusSha256, evidence.sourceCorpusSha256);
assert.equal(parentReview.sourceCorpusSha256, evidence.sourceCorpusSha256);

const sourceByCode = new Map();
for (const source of batch) {
    assert.equal(validateSource(source), true,
        `Schema rejected ${source.code}: ${JSON.stringify(validateSource.errors)}`);
    assert.ok(!sourceByCode.has(source.code), `duplicate Source code ${source.code}`);
    sourceByCode.set(source.code, source);
    assert.ok(!('status' in source), `${source.code} must not carry workflow status`);
}

const registryByCode = new Map(registry.map((entry, index) => [entry.code, { entry, index }]));
assert.equal(registryByCode.size, registry.length, 'source registry contains duplicate codes');
const codeLower = new Map(registry.map(entry => [entry.code.toLocaleLowerCase('en-US'), entry.code]));
assert.equal(codeLower.size, registry.length, 'case-insensitive Source code collision');
const aliasOwners = new Map();
for (const entry of registry) {
    for (const alias of entry.aliases ?? []) {
        const key = alias.toLocaleLowerCase('en-US');
        assert.ok(!codeLower.has(key), `Source-code alias ${alias} collides with a code`);
        assert.ok(!aliasOwners.has(key), `duplicate Source-code alias ${alias}`);
        aliasOwners.set(key, entry.code);
    }
}

const publicationsByCode = new Map();
for (const [kind, records] of [['book', books], ['adventure', adventures]]) {
    records.forEach((record, index) => {
        assert.ok(registryByCode.has(record.source), `${kind} ${record.id} has unknown source ${record.source}`);
        if (!publicationsByCode.has(record.source)) publicationsByCode.set(record.source, []);
        publicationsByCode.get(record.source).push({ kind, record, index });
    });
}

let codeOnlyCount = 0;
let bookCount = 0;
let adventureCount = 0;
const identityKeys = new Set();
for (const [code, { entry }] of registryByCode) {
    const canonical = sourceByCode.get(code);
    assert.ok(canonical, `missing normalized Source ${code}`);
    assert.deepEqual(canonical.codeAliases ?? [], entry.aliases ?? [], `alias mismatch for ${code}`);
    const records = publicationsByCode.get(code) ?? [];
    const expectedPublications = [
        ...books.filter(record => record.source === code).map(record => canonicalPublication('book', record, code)),
        ...adventures.filter(record => record.source === code)
            .map(record => canonicalPublication('adventure', record, code))
    ];
    assert.deepEqual(canonical.publications ?? [], expectedPublications,
        `source-derived publication fields mismatch for ${code}`);
    if (records.length === 0) codeOnlyCount++;
    for (const publication of canonical.publications ?? []) {
        if (publication.kind === 'book') bookCount++;
        if (publication.kind === 'adventure') adventureCount++;
        const identityKey = `${code}\0${publication.kind}\0${publication.publicationId ?? code}`;
        assert.ok(!identityKeys.has(identityKey), `publication identity collision at ${identityKey}`);
        identityKeys.add(identityKey);
    }
}

const provenanceByCode = new Map(provenance.entries.map(entry => [entry.sourceCode, entry]));
assert.equal(provenanceByCode.size, batch.length, 'each Source must have exactly one provenance entry');
for (const source of batch) {
    const provenanceEntry = provenanceByCode.get(source.code);
    assert.ok(provenanceEntry, `missing provenance for ${source.code}`);
    const registryInfo = registryByCode.get(source.code);
    const registryPointer = provenanceEntry.registryEntry;
    assert.equal(registryPointer.sourceFile, 'src/main/resources/data/sources/registry.json');
    assert.equal(registryPointer.sourceFileSha256, sha256(registryInput.bytes));
    assert.equal(jsonPointer(registryInput.value, registryPointer.jsonPointer).code, source.code);

    const expectedRecords = [
        ...books.map((record, index) => ({ kind: 'book', record, index, file: 'books.json' }))
            .filter(item => item.record.source === source.code),
        ...adventures.map((record, index) => ({ kind: 'adventure', record, index, file: 'adventures.json' }))
            .filter(item => item.record.source === source.code)
    ];
    assert.equal(provenanceEntry.publicationRecords.length, expectedRecords.length,
        `publication provenance count mismatch for ${source.code}`);
    for (const pointerRecord of provenanceEntry.publicationRecords) {
        const input = inputMap.get(pointerRecord.kind === 'book' ? 'books.json' : 'adventures.json');
        const record = jsonPointer(input.value, pointerRecord.jsonPointer);
        assert.equal(record.id, pointerRecord.sourceId, 'publication source ID mismatch at provenance pointer');
        assert.equal(record.source, source.code, 'publication source association mismatch at provenance pointer');
        assert.equal(pointerRecord.sourceFileSha256, sha256(input.bytes));
    }
    assert.equal(registryInfo.index, Number(registryPointer.jsonPointer.match(/\/(\d+)$/)?.[1]),
        `registry pointer position mismatch for ${source.code}`);
}

const reviewByOwner = new Map();
for (const reference of parentReview.references) {
    const key = `${reference.owner.sourceCode}\0${reference.owner.kind}\0${reference.owner.publicationId}`;
    assert.ok(!reviewByOwner.has(key), `duplicate parent review entry ${key}`);
    reviewByOwner.set(key, reference);
}

const expectedParentOwners = [];
const publicationIds = new Map();
for (const [kind, records] of [['book', books], ['adventure', adventures]]) {
    for (const record of records) {
        if (!publicationIds.has(record.id)) publicationIds.set(record.id, []);
        publicationIds.get(record.id).push({ kind, sourceCode: record.source, publicationId: record.id });
        if (record.parentSource !== undefined && record.parentSource !== null) {
            expectedParentOwners.push({
                sourceCode: record.source,
                kind,
                publicationId: record.id,
                rawValue: record.parentSource
            });
        }
    }
}
assert.equal(reviewByOwner.size, expectedParentOwners.length,
    'parent tracker must account for every raw parentSource value');
let resolvedParentCount = 0;
let unresolvedParentCount = 0;
let ambiguousParentCount = 0;
for (const owner of expectedParentOwners) {
    const key = `${owner.sourceCode}\0${owner.kind}\0${owner.publicationId}`;
    const reference = reviewByOwner.get(key);
    assert.ok(reference, `parent tracker missing ${key}`);
    assert.equal(reference.rawValue, owner.rawValue, `parent token changed for ${key}`);
    const target = sourceByCode.get(owner.rawValue);
    const publicationCandidates = publicationIds.get(owner.rawValue) ?? [];
    if (reference.disposition === 'resolved') {
        assert.ok(target, `resolved parent ${owner.rawValue} is not a Source code`);
        assert.ok((target.publications?.length ?? 0) <= 1,
            `resolved parent ${owner.rawValue} has multiple publication targets`);
        resolvedParentCount++;
    } else if (reference.disposition === 'unresolved') {
        assert.equal(target, undefined, `unresolved parent ${owner.rawValue} matches a Source code`);
        assert.equal(publicationCandidates.length, 0,
            `unresolved parent ${owner.rawValue} matches a publication ID`);
        unresolvedParentCount++;
    } else {
        assert.equal(reference.disposition, 'ambiguous', `unknown parent disposition ${reference.disposition}`);
        assert.ok(target || publicationCandidates.length > 0,
            `ambiguous parent ${owner.rawValue} has no identity candidates`);
        ambiguousParentCount++;
    }
}

const representative = JSON.parse(fs.readFileSync(
    path.join(outputRoot, 'representative-batch.json'), 'utf8'));
for (const approvedSource of representative) {
    assert.deepEqual(sourceByCode.get(approvedSource.code), approvedSource,
        `full dataset changed approved representative ${approvedSource.code}`);
}

process.stdout.write(`${JSON.stringify({
    schemaMetaValidation: 'passed',
    schemaValidatedSourceCount: batch.length,
    uniqueSourceCodes: sourceByCode.size,
    codeOnlyCount,
    bookRecordCount: bookCount,
    adventureRecordCount: adventureCount,
    publicationIdentityKeyCount: identityKeys.size,
    codeAliasCount: aliasOwners.size,
    provenanceSourceEntries: provenance.entries.length,
    publicationProvenanceEntries: provenance.entries
        .reduce((total, entry) => total + entry.publicationRecords.length, 0),
    parentReferenceCount: expectedParentOwners.length,
    resolvedParentCount,
    unresolvedParentCount,
    ambiguousParentCount,
    approvedRepresentativeSourcesUnchanged: representative.length
}, null, 2)}\n`);
