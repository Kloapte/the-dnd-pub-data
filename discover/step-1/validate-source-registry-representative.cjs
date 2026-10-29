// Validate the focused conversion, provenance pointers, and selected identity references.
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');
const assert = require('node:assert/strict');
const Ajv2020 = require('ajv/dist/2020').default;
const addFormats = require('ajv-formats').default;

const workspace = path.resolve(__dirname, '../..');
const dataRoot = path.join(workspace, 'src/main/resources/data');
const normalizedRoot = path.join(workspace, 'normalized/source/v1');
const evidence = JSON.parse(fs.readFileSync(
    path.join(__dirname, 'source-registry-evidence.json'), 'utf8'));

function sha256(bytes) {
    return crypto.createHash('sha256').update(bytes).digest('hex');
}

function readJson(file) {
    const bytes = fs.readFileSync(file);
    return {
        bytes,
        value: JSON.parse(bytes.toString('utf8').replace(/^\uFEFF/, ''))
    };
}

function jsonPointer(root, pointer) {
    return pointer.split('/').slice(1).reduce((value, rawPart) => {
        const part = rawPart.replaceAll('~1', '/').replaceAll('~0', '~');
        return value[part];
    }, root);
}

function canonicalPublication(kind, record, sourceCode) {
    const value = { kind };
    if (record.id !== sourceCode) value.publicationId = record.id;
    for (const key of ['name', 'published', 'revised', 'group', 'parentSource']) {
        if (record[key] !== undefined && record[key] !== null) value[key] = record[key];
    }
    if (Array.isArray(record.alias) && record.alias.length > 0) {
        value.titleAliases = record.alias;
    }
    return value;
}

const registryInput = readJson(path.join(dataRoot, 'sources/registry.json'));
const booksInput = readJson(path.join(dataRoot, 'books.json'));
const adventuresInput = readJson(path.join(dataRoot, 'adventures.json'));
const registry = registryInput.value.sources;
const books = booksInput.value.book;
const adventures = adventuresInput.value.adventure;
const focusedInputs = [
    ['sources/registry.json', registryInput.bytes],
    ['books.json', booksInput.bytes],
    ['adventures.json', adventuresInput.bytes]
];
for (const [file, bytes] of focusedInputs) {
    const expected = evidence.analysisFiles.find(entry => entry.file === file);
    assert.ok(expected, `Phase 1 evidence must include ${file}`);
    assert.equal(sha256(bytes), expected.sha256, `${file} must be byte-identical to Phase 1 input`);
}

const registryCodeKeys = new Set(registry.map(entry => entry.code.toLocaleLowerCase('en-US')));
const codeAliasOwners = new Map();
for (const entry of registry) {
    for (const alias of entry.aliases ?? []) {
        const aliasKey = alias.toLocaleLowerCase('en-US');
        assert.ok(!registryCodeKeys.has(aliasKey), `code alias collides with code ${alias}`);
        assert.ok(!codeAliasOwners.has(aliasKey), `duplicate source-code alias ${alias}`);
        codeAliasOwners.set(aliasKey, entry.code);
    }
}

const schemaPath = path.join(workspace, 'src/main/resources/schemas/source/schema.json');
const schema = JSON.parse(fs.readFileSync(schemaPath, 'utf8'));
const ajv = new Ajv2020({ allErrors: true, strict: true, validateSchema: true });
addFormats(ajv);
assert.equal(ajv.validateSchema(schema), true, 'schema must pass Draft 2020-12 meta-validation');
const validateSource = ajv.compile(schema);

const batch = JSON.parse(fs.readFileSync(
    path.join(normalizedRoot, 'representative-batch.json'), 'utf8'));
const provenance = JSON.parse(fs.readFileSync(
    path.join(normalizedRoot, 'representative-provenance.json'), 'utf8'));
const parentReview = JSON.parse(fs.readFileSync(
    path.join(normalizedRoot, 'representative-parent-reference-review.json'), 'utf8'));

assert.ok(Array.isArray(batch));
assert.equal(provenance.sourceCorpusSha256, evidence.sourceCorpusSha256);
assert.equal(parentReview.sourceCorpusSha256, evidence.sourceCorpusSha256);
assert.equal(provenance.schemaId, schema.$id);
assert.equal(new Set(batch.map(source => source.code)).size, batch.length,
    'Source codes must be unique within the representative batch');

const sourceByCode = new Map(batch.map(source => [source.code, source]));
const provenanceByCode = new Map(provenance.entries.map(entry => [entry.sourceCode, entry]));
assert.equal(provenanceByCode.size, batch.length, 'each Source must have one provenance entry');

let bookRecordCount = 0;
let adventureRecordCount = 0;
let codeOnlyCount = 0;
let publicationIdentityCount = 0;
let resolvedParentCount = 0;
let trackedParentCount = 0;

const parentReviewByOwner = new Map(parentReview.references.map(reference => [
    `${reference.owner.sourceCode}\0${reference.owner.kind}\0${reference.owner.publicationId}`,
    reference
]));

for (const source of batch) {
    assert.equal(validateSource(source), true,
        `${source.code} must validate: ${JSON.stringify(validateSource.errors)}`);

    const sourceMatches = registry.filter(entry => entry.code === source.code);
    assert.equal(sourceMatches.length, 1, `${source.code} must map to one registry entry`);
    const rawRegistryEntry = sourceMatches[0];
    const expectedCodeAliases = (rawRegistryEntry.aliases ?? []).filter(alias => alias.length > 0);
    assert.deepEqual(source.codeAliases ?? [], expectedCodeAliases,
        `${source.code} code aliases must be preserved`);

    const expectedPublications = [
        ...books.filter(record => record.source === source.code)
            .map(record => canonicalPublication('book', record, source.code)),
        ...adventures.filter(record => record.source === source.code)
            .map(record => canonicalPublication('adventure', record, source.code))
    ];
    assert.deepEqual(source.publications ?? [], expectedPublications,
        `${source.code} must preserve every associated publication metadata record`);

    if (source.publications === undefined) codeOnlyCount++;
    const identityKeys = new Set();
    for (const publication of source.publications ?? []) {
        if (publication.kind === 'book') bookRecordCount++;
        if (publication.kind === 'adventure') adventureRecordCount++;
        const identityKey = `${publication.kind}\0${publication.publicationId ?? source.code}`;
        assert.ok(!identityKeys.has(identityKey),
            `publication identity collision: ${source.code}/${identityKey}`);
        identityKeys.add(identityKey);
        publicationIdentityCount++;

        if (publication.parentSource === undefined) continue;
        const candidates = [
            ...books.filter(record => record.id === publication.parentSource),
            ...adventures.filter(record => record.id === publication.parentSource)
        ];
        const ownerId = publication.publicationId ?? source.code;
        const reviewKey = `${source.code}\0${publication.kind}\0${ownerId}`;
        const review = parentReviewByOwner.get(reviewKey);
        const targetSource = sourceByCode.get(publication.parentSource);

        if (review) {
            assert.equal(review.rawValue, publication.parentSource,
                'tracked parent token must exactly match the source field');
            assert.ok(review.disposition === 'unresolved' || review.disposition === 'ambiguous');
            if (review.disposition === 'unresolved') {
                assert.equal(targetSource, undefined, 'unresolved parent must not resolve to a batch Source');
                assert.equal(registryCodeKeys.has(publication.parentSource.toLocaleLowerCase('en-US')), false);
                assert.equal(candidates.length, 0, 'unresolved parent has no publication ID candidate');
            } else {
                assert.ok(targetSource, 'ambiguous parent Source-code candidate must be in the batch');
                assert.deepEqual(review.candidatePublicationIds, candidates.map(record => record.id));
            }
            trackedParentCount++;
        } else {
            assert.ok(targetSource, 'untracked parent must resolve to a Source in the batch');
            assert.equal(registryCodeKeys.has(publication.parentSource.toLocaleLowerCase('en-US')), true);
            assert.equal(candidates.length, 1, 'resolved parent token must identify one publication record');
            resolvedParentCount++;
        }
    }

    const provenanceEntry = provenanceByCode.get(source.code);
    assert.ok(provenanceEntry, `provenance is missing for ${source.code}`);
    const registryPointerTarget = jsonPointer(registryInput.value, provenanceEntry.registryEntry.jsonPointer);
    assert.equal(registryPointerTarget.code, source.code, 'registry provenance pointer must identify the Source');
    assert.equal(provenanceEntry.registryEntry.sourceFileSha256, sha256(registryInput.bytes));

    const expectedRecords = [
        ...books.map((record, index) => ({ kind: 'book', record, index, file: 'books.json' }))
            .filter(entry => entry.record.source === source.code),
        ...adventures.map((record, index) => ({ kind: 'adventure', record, index, file: 'adventures.json' }))
            .filter(entry => entry.record.source === source.code)
    ];
    assert.equal(provenanceEntry.publicationRecords.length, expectedRecords.length,
        `every publication for ${source.code} must have a provenance pointer`);

    for (const provenanceRecord of provenanceEntry.publicationRecords) {
        const fileName = provenanceRecord.kind === 'book' ? 'books.json' : 'adventures.json';
        const input = fileName === 'books.json' ? booksInput : adventuresInput;
        const rawTarget = jsonPointer(input.value, provenanceRecord.jsonPointer);
        assert.equal(rawTarget.id, provenanceRecord.sourceId,
            'publication provenance pointer must identify the recorded source ID');
        assert.equal(rawTarget.source, source.code,
            'publication provenance pointer must agree with its canonical Source');
        assert.equal(provenanceRecord.sourceFileSha256, sha256(input.bytes));
    }
}

assert.equal(codeOnlyCount, 1);
assert.equal(bookRecordCount, 6);
assert.equal(adventureRecordCount, 2);
assert.equal(publicationIdentityCount, 8);
assert.equal(resolvedParentCount, 1);
assert.equal(trackedParentCount, 2);
assert.equal(parentReviewByOwner.size, 2, 'every unresolved or ambiguous parent must be tracked');

console.log(JSON.stringify({
    schemaMetaValidation: 'passed',
    schemaValidatedSourceCount: batch.length,
    uniqueSourceCodes: batch.length,
    codeOnlyCount,
    bookRecordCount,
    adventureRecordCount,
    publicationIdentityCount,
    codeAliasCount: batch.reduce((count, source) => count + (source.codeAliases?.length ?? 0), 0),
    titleAliasCount: batch.flatMap(source => source.publications ?? [])
        .reduce((count, publication) => count + (publication.titleAliases?.length ?? 0), 0),
    resolvedParentCount,
    unresolvedOrAmbiguousParentCount: trackedParentCount,
    provenanceEntries: provenance.entries.length,
    publicationProvenanceEntries: provenance.entries
        .reduce((count, entry) => count + entry.publicationRecords.length, 0)
}, null, 2));
