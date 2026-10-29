// Convert representative or complete Source datasets from pinned source metadata.
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');
const assert = require('node:assert/strict');

const workspace = path.resolve(__dirname, '../..');
const dataRoot = path.join(workspace, 'src/main/resources/data');
const evidencePath = path.join(workspace, 'tools/evidence/step-1/source-registry-evidence.json');
const outputRoot = path.join(workspace, 'datasets/step-1/v1');
const runtimeDatasetPath = path.join(workspace, 'src/main/resources/normalized/sources/sources.json');
const schemaId = 'urn:thedndpub:schemas:source:v1';
const approvedGroups = new Set([
    'core', 'screen', 'setting', 'setting-alt', 'organized-play',
    'supplement', 'supplement-alt', 'other', 'recipe', 'homecraft'
]);
const selectedCodes = ['PHB', 'EEPC', 'MOT', 'PSZ', 'AAG', 'EFR', 'ERLW', 'SAC'];
const fullDataset = process.argv.includes('--full');

function sha256(bytes) {
    return crypto.createHash('sha256').update(bytes).digest('hex');
}

function readJson(relativePath) {
    const bytes = fs.readFileSync(path.join(dataRoot, relativePath));
    const parsed = JSON.parse(bytes.toString('utf8').replace(/^\uFEFF/, ''));
    return { bytes, parsed };
}

function compactOptional(record, sourceKey, target, targetKey = sourceKey) {
    const value = record[sourceKey];
    if (value === undefined || value === null) {
        return;
    }
    assert.equal(typeof value, 'string', `${sourceKey} must be a string when supplied`);
    target[targetKey] = value;
}

function publicationValue(kind, record, sourceCode) {
    assert.equal(record.source, sourceCode, 'publication must be assigned to its source code');
    const publication = { kind };

    if (record.id !== undefined && record.id !== null && record.id !== sourceCode) {
        assert.equal(typeof record.id, 'string');
        publication.publicationId = record.id;
    }

    compactOptional(record, 'name', publication);
    compactOptional(record, 'published', publication);
    compactOptional(record, 'revised', publication);
    if (record.group !== undefined && record.group !== null) {
        assert.ok(approvedGroups.has(record.group), `unapproved group enum value: ${record.group}`);
    }
    compactOptional(record, 'group', publication);

    if (Array.isArray(record.alias) && record.alias.length > 0) {
        publication.titleAliases = [...record.alias];
    }

    compactOptional(record, 'parentSource', publication);
    return publication;
}

const evidence = JSON.parse(fs.readFileSync(evidencePath, 'utf8'));
const registryFile = 'sources/registry.json';
const booksFile = 'books.json';
const adventuresFile = 'adventures.json';
const registryInput = readJson(registryFile);
const booksInput = readJson(booksFile);
const adventuresInput = readJson(adventuresFile);
const registry = registryInput.parsed.sources;
const books = booksInput.parsed.book;
const adventures = adventuresInput.parsed.adventure;

assert.ok(Array.isArray(registry));
assert.ok(Array.isArray(books));
assert.ok(Array.isArray(adventures));

const inputFiles = [
    { file: registryFile, bytes: registryInput.bytes, records: registry },
    { file: booksFile, bytes: booksInput.bytes, records: books },
    { file: adventuresFile, bytes: adventuresInput.bytes, records: adventures }
];
for (const input of inputFiles) {
    const expected = evidence.analysisFiles.find(entry => entry.file === input.file);
    assert.ok(expected, `Phase 1 hash is available for ${input.file}`);
    assert.equal(sha256(input.bytes), expected.sha256, `${input.file} must match Phase 1 source evidence`);
}

const codeOccurrences = new Map();
const registryCodeKeys = new Set();
for (const entry of registry) {
    codeOccurrences.set(entry.code, (codeOccurrences.get(entry.code) || 0) + 1);
    registryCodeKeys.add(entry.code.toLocaleLowerCase('en-US'));
}

const codeAliases = new Map();
for (const entry of registry) {
    for (const alias of entry.aliases ?? []) {
        const key = alias.toLocaleLowerCase('en-US');
        assert.ok(!codeAliases.has(key), `duplicate registry code alias: ${alias}`);
        codeAliases.set(key, entry.code);
    }
}

const sourceCodes = fullDataset ? registry.map(entry => entry.code) : selectedCodes;
const normalized = [];
const provenanceEntries = [];
const sourceRecords = new Map();
const publicationIdentityKeys = new Set();

for (const code of sourceCodes) {
    assert.equal(codeOccurrences.get(code), 1, `registry must contain exactly one ${code}`);
    const registryIndex = registry.findIndex(entry => entry.code === code);
    const registryEntry = registry[registryIndex];
    const source = { code };

    if (Array.isArray(registryEntry.aliases) && registryEntry.aliases.length > 0) {
        source.codeAliases = [...registryEntry.aliases];
    }

    const sourcePublications = [
        ...books.map((record, index) => ({ kind: 'book', record, index, file: booksFile })),
        ...adventures.map((record, index) => ({ kind: 'adventure', record, index, file: adventuresFile }))
    ].filter(entry => entry.record.source === code);

    if (sourcePublications.length > 0) {
        source.publications = sourcePublications.map(entry =>
            publicationValue(entry.kind, entry.record, code));
    }

    assert.equal(source.code, code);
    assert.ok(!sourceRecords.has(source.code), `duplicate normalized Source code: ${source.code}`);
    sourceRecords.set(source.code, source);

    for (const alias of source.codeAliases ?? []) {
        const key = alias.toLocaleLowerCase('en-US');
        assert.ok(!registryCodeKeys.has(key), `code alias collides with registry code: ${alias}`);
    }

    for (const publication of source.publications ?? []) {
        const identityKey = `${source.code}\0${publication.kind}\0${publication.publicationId ?? source.code}`;
        assert.ok(!publicationIdentityKeys.has(identityKey),
            `publication identity collision for ${source.code}/${publication.kind}`);
        publicationIdentityKeys.add(identityKey);
    }

    const publicationProvenance = sourcePublications.map(entry => ({
        kind: entry.kind,
        sourceId: entry.record.id,
        sourceFile: `src/main/resources/data/${entry.file}`,
        jsonPointer: `/${entry.kind === 'book' ? 'book' : 'adventure'}/${entry.index}`,
        sourceFileSha256: sha256(inputFiles.find(input => input.file === entry.file).bytes)
    }));

    provenanceEntries.push({
        sourceCode: code,
        registryEntry: {
            sourceFile: `src/main/resources/data/${registryFile}`,
            jsonPointer: `/sources/${registryIndex}`,
            sourceFileSha256: sha256(registryInput.bytes)
        },
        publicationRecords: publicationProvenance
    });
    normalized.push(source);
}

assert.equal(normalized.length, sourceCodes.length);
if (!fullDataset) {
    assert.equal(normalized.filter(source => !source.publications).length, 1,
        'the representative batch must include one code-only Source');
}

for (const source of normalized) {
    const registryEntry = registry.find(entry => entry.code === source.code);
    const expectedAliases = (registryEntry.aliases ?? []).filter(alias => alias.length > 0);
    assert.deepEqual(source.codeAliases ?? [], expectedAliases,
        `registry aliases must be preserved for ${source.code}`);

    const expectedPublications = [
        ...books.filter(record => record.source === source.code)
            .map(record => publicationValue('book', record, source.code)),
        ...adventures.filter(record => record.source === source.code)
            .map(record => publicationValue('adventure', record, source.code))
    ];
    assert.deepEqual(source.publications ?? [], expectedPublications,
        `all source-derived publication values must be preserved for ${source.code}`);

    const provenance = provenanceEntries.find(entry => entry.sourceCode === source.code);
    assert.ok(provenance, `provenance must exist for Source ${source.code}`);
    assert.equal(provenance.publicationRecords.length, expectedPublications.length,
        `every publication record must have provenance for ${source.code}`);
}

assert.ok(!registryCodeKeys.has('SAiS'.toLocaleLowerCase('en-US')));
assert.ok(!books.some(record => record.id === 'SAiS' || record.source === 'SAiS'));
assert.ok(!adventures.some(record => record.id === 'SAiS' || record.source === 'SAiS'));
assert.equal(sourceRecords.get('EFR').publications[0].parentSource, 'ERLW');
assert.ok(sourceRecords.has('ERLW'), 'resolved parent Source ERLW must be included in the batch');

const selectedParentReferences = [];
for (const source of normalized) {
    for (const publication of source.publications ?? []) {
        if (publication.parentSource === undefined) continue;
        const target = sourceRecords.get(publication.parentSource);
        const allMatchingPublicationIds = [
            ...books.filter(record => record.id === publication.parentSource),
            ...adventures.filter(record => record.id === publication.parentSource)
        ].map(record => record.id);

        if (target) {
            selectedParentReferences.push({
                owner: {
                    sourceCode: source.code,
                    kind: publication.kind,
                    publicationId: publication.publicationId ?? source.code
                },
                property: 'parentSource',
                rawValue: publication.parentSource,
                sourceCodeTarget: target.code,
                publicationIdCandidates: [...new Set(allMatchingPublicationIds)]
            });
        }
    }
}

const publicationTargetsById = new Map();
for (const kind of ['book', 'adventure']) {
    const records = kind === 'book' ? books : adventures;
    for (const record of records) {
        if (!publicationTargetsById.has(record.id)) publicationTargetsById.set(record.id, []);
        publicationTargetsById.get(record.id).push({ kind, sourceCode: record.source, publicationId: record.id });
    }
}

const parentReferences = [];
for (const source of normalized) {
    for (const publication of source.publications ?? []) {
        if (publication.parentSource === undefined) continue;
        const rawValue = publication.parentSource;
        const sourceTarget = sourceRecords.get(rawValue);
        const publicationTargets = publicationTargetsById.get(rawValue) ?? [];
        const owner = {
            sourceCode: source.code,
            kind: publication.kind,
            publicationId: publication.publicationId ?? source.code
        };

        if (!sourceTarget && publicationTargets.length === 0) {
            parentReferences.push({
                owner, property: 'parentSource', rawValue, disposition: 'unresolved',
                candidatePublicationIds: [],
                reason: 'The token matches no registry Source code or supplied publication ID.'
            });
        } else if (sourceTarget && (sourceTarget.publications?.length ?? 0) > 1) {
            parentReferences.push({
                owner, property: 'parentSource', rawValue, disposition: 'ambiguous',
                sourceCodeTarget: sourceTarget.code,
                candidatePublicationIds: publicationTargets.map(target => target.publicationId),
                reason: 'The token matches a Source with multiple publication records; the raw field does not establish target granularity.'
            });
        } else if (!sourceTarget) {
            parentReferences.push({
                owner, property: 'parentSource', rawValue, disposition: 'ambiguous',
                candidatePublicationIds: publicationTargets.map(target => target.publicationId),
                reason: 'The token matches publication metadata but is not a registered Source code; target role is not established.'
            });
        } else {
            parentReferences.push({
                owner, property: 'parentSource', rawValue, disposition: 'resolved',
                sourceCodeTarget: sourceTarget.code,
                candidatePublicationIds: publicationTargets.map(target => target.publicationId),
                reason: 'The token identifies one registered Source; its raw value is preserved in canonical JSON.'
            });
        }
    }
}

const existingRepresentativePath = path.join(outputRoot, 'representative-batch.json');
if (fullDataset && fs.existsSync(existingRepresentativePath)) {
    const representative = JSON.parse(fs.readFileSync(existingRepresentativePath, 'utf8'));
    for (const approvedSource of representative) {
        const expandedSource = normalized.find(source => source.code === approvedSource.code);
        assert.deepEqual(expandedSource, approvedSource,
            `full conversion would change approved representative Source ${approvedSource.code}`);
    }
}

const fullPrefix = fullDataset ? 'source-registry' : 'representative';
const reviewReferences = fullDataset
    ? parentReferences
    : parentReferences.filter(reference => reference.disposition !== 'resolved')
        .map(reference => {
            const review = { ...reference };
            if (review.disposition === 'unresolved') {
                delete review.candidatePublicationIds;
                review.reason = 'The token is not a registry code or a supplied book/adventure ID. Its identity role is not established.';
            } else {
                review.reason = `The code resolves to Source ${review.sourceCodeTarget} and publication ID ${review.candidatePublicationIds.join(', ')}; the raw field does not establish target granularity.`;
            }
            return review;
        })
        .sort((left, right) => left.owner.sourceCode.localeCompare(right.owner.sourceCode, 'en-US'));
const outputFiles = [
    {
        file: fullDataset ? runtimeDatasetPath : path.join(outputRoot, `${fullPrefix}-batch.json`),
        value: normalized
    },
    {
        file: path.join(outputRoot, `${fullPrefix}-provenance.json`),
        value: {
            manifestVersion: 1,
            sourceCorpusSha256: evidence.sourceCorpusSha256,
            schemaId,
            entries: provenanceEntries
        }
    },
    {
        file: path.join(outputRoot, `${fullPrefix}-parent-reference-review.json`),
        value: {
            sourceCorpusSha256: evidence.sourceCorpusSha256,
            references: reviewReferences
        }
    }
];

fs.mkdirSync(outputRoot, { recursive: true });
for (const output of outputFiles) {
    const serialized = `${JSON.stringify(output.value, null, 2)}\n`;
    if (fullDataset && fs.existsSync(output.file)) {
        assert.equal(fs.readFileSync(output.file, 'utf8'), serialized,
            `refusing to overwrite changed full dataset artifact: ${path.basename(output.file)}; review source changes first`);
    }
    fs.mkdirSync(path.dirname(output.file), { recursive: true });
    fs.writeFileSync(output.file, serialized);
}

console.log(JSON.stringify({
    mode: fullDataset ? 'full' : 'representative',
    sourceCount: normalized.length,
    codeOnlyCount: normalized.filter(source => !source.publications).length,
    bookRecords: normalized.flatMap(source => source.publications ?? []).filter(pub => pub.kind === 'book').length,
    adventureRecords: normalized.flatMap(source => source.publications ?? []).filter(pub => pub.kind === 'adventure').length,
    parentReferences: selectedParentReferences,
    parentReferenceCount: parentReferences.length,
    unresolvedParentCount: parentReferences.filter(reference => reference.disposition === 'unresolved').length,
    ambiguousParentCount: parentReferences.filter(reference => reference.disposition === 'ambiguous').length,
    files: outputFiles.map(output => path.relative(workspace, output.file).replaceAll('\\', '/'))
}, null, 2));
