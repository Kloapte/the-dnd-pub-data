import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import fs from 'node:fs';

const directory = 'analysis/mechanics/public-v1/spells/p1b';
const digest = (path) => crypto.createHash('sha256').update(fs.readFileSync(path)).digest('hex');
const read = (path) => JSON.parse(fs.readFileSync(path, 'utf8'));
const generatedFiles = ['occurrence-crosswalk.json', 'coverage.json', 'srd-spell-rosters.json'];
const beforePath = 'target/p1b-analysis-output-hashes.json';

function existingFiles() {
    const result = [];
    function walk(path) {
        for (const entry of fs.readdirSync(path, { withFileTypes: true })) {
            const child = `${path}/${entry.name}`;
            if (entry.isDirectory()) {
                walk(child);
            } else {
                result.push(child);
            }
        }
    }
    walk('src');
    walk('analysis');
    return result.sort();
}

function isP1bFile(path) {
    return path.startsWith(`${directory}/`)
        || path === 'analysis/mechanics/public-v1/spells/P1B-PUBLICATION-CROSSWALK.md';
}

if (process.argv.includes('--capture')) {
    fs.writeFileSync(beforePath, JSON.stringify(Object.fromEntries(generatedFiles.map((file) =>
        [file, digest(`${directory}/${file}`)])), null, 2) + '\n');
    console.log('Captured generated evidence fingerprints for an independent repeat run.');
    process.exit(0);
}

const outputHashes = Object.fromEntries(generatedFiles.map((file) => [file, digest(`${directory}/${file}`)]));
assert.deepEqual(outputHashes, read(beforePath), 'Generated analysis output is not deterministic');

// This baseline was captured before creating any P1B files. It includes the user's dirty workspace.
const protectedPath = `${directory}/protected-input-hashes.json`;
const protectedHashes = read(protectedPath);
for (const [path, hash] of Object.entries(protectedHashes)) {
    assert.equal(digest(path), hash, `Pre-existing file changed: ${path}`);
}
for (const path of existingFiles()) {
    if (!Object.hasOwn(protectedHashes, path)) {
        assert.ok(isP1bFile(path), `Unexpected new file outside P1B: ${path}`);
        fs.readFileSync(path, 'utf8').split(/\r?\n/).forEach((line, index) => {
            assert.ok(!/[\t ]+$/.test(line), `Trailing whitespace at ${path}:${index + 1}`);
        });
    }
}

const rows = read(`${directory}/occurrence-crosswalk.json`);
const coverage = read(`${directory}/coverage.json`);
const manifest = read(`${directory}/official-documents.json`);
const notices = read(`${directory}/attribution-review.json`);
for (const source of manifest.supportingPrimarySources.filter((value) => value.cacheFile)) {
    assert.equal(digest(`target/p1b-official/${source.cacheFile}`), source.sha256);
}
const normalizeNotice = (text) => text.normalize('NFKC').replace(/[\u00ad\u0002]/g, '')
    .replace(/[\u2018\u2019]/g, "'").replace(/[\u201c\u201d]/g, '"').replace(/\s+/g, '');
for (const document of manifest.documents) {
    const path = `target/p1b-official/${document.version}/legal-notice.txt`;
    assert.equal(digest(path), document.legalNoticeExtractSha256);
    const notice = notices.notices.find((value) => value.documentId === document.id);
    assert.ok(normalizeNotice(fs.readFileSync(path, 'utf8')).includes(normalizeNotice(notice.requiredAttribution)),
        `Attribution does not match official legal notice: ${document.id}`);
}
assert.equal(rows.length, 708);
assert.equal(new Set(rows.map((row) => row.occurrenceId)).size, 708);
assert.equal(new Set(rows.map((row) => row.spellId)).size, 402);
assert.equal(Object.values(coverage.classifications).reduce((sum, row) => sum + row.observations, 0), 708);
assert.equal(rows.filter((row) => row.releaseAuthorization !== 'NOT_GRANTED').length, 0);
const licensedPositions = new Set();
for (const row of rows) {
    assert.ok(row.sourceResourceSha256);
    assert.ok(row.authoredTokenSha256);
    assert.ok(row.exactExpressionSha256);
    assert.equal(row.sourceOrigin, 'AUTHORED');
    if (row.publicationReviewClassification === 'VERIFIED_LICENSED_MATCH') {
        assert.ok(row.verifiedMatches.length > 0);
        assert.ok(['PHB', 'XPHB'].includes(row.sourceCode));
        assert.ok(row.verifiedMatches.some((match) => match.documentId === row.potentialReleaseBasis.documentId));
        for (const match of row.verifiedMatches) {
            const document = manifest.documents.find((value) => value.id === match.documentId);
            assert.ok(document);
            assert.equal(document.license, 'CC-BY-4.0');
            const notice = notices.notices.find((value) => value.documentId === match.documentId);
            assert.ok(notice);
            assert.ok(row.attributionIds.includes(notice.id));
            assert.ok(match.evidence.strongLiteralAnchor);
            const position = `${row.spellId}:${match.documentId}:${match.evidence.normalizedDocumentSpanUtf16}`;
            assert.ok(!licensedPositions.has(position), `Reused licensed position within an owner: ${position}`);
            licensedPositions.add(position);
        }
    } else {
        assert.equal(row.potentialReleaseBasis, null);
    }
}

const result = {
    allCandidateOccurrencesReconciled: 708,
    candidateOwners: 402,
    missingOrDuplicateOccurrenceIdentities: 0,
    pinnedSourceHashesVerifiedByBuilder: coverage.pinnedSourceHashesVerified,
    preExistingFileHashesUnchanged: Object.keys(protectedHashes).length,
    protectedInputManifestSha256: digest(protectedPath),
    generatedOutputsDeterministic: true,
    generatedOutputSha256: outputHashes,
    productionAndTestsUnchanged: true,
    existingAssessmentEvidenceUnchanged: true,
    authorizedReleaseObservations: 0,
    exactVersionSpecificAttributionStatementsVerified: 3,
    reusedLicensedPositionsWithinAnOwner: 0,
    mavenRun: false,
};
fs.writeFileSync(`${directory}/verification.json`, JSON.stringify(result, null, 2) + '\n');
console.log(JSON.stringify(result, null, 2));
