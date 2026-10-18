import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import { mkdirSync, readFileSync, existsSync, writeFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const project = fileURLToPath(new URL('../../../../../', import.meta.url));
const review = resolve(project, 'analysis/mechanics/public-v1/spells');
const output = resolve(project, 'src/main/resources/publication/spell-formulas/v1');
const read = name => JSON.parse(readFileSync(resolve(review, name), 'utf8'));
const hash = bytes => createHash('sha256').update(bytes).digest('hex');
const artifactHash = name => hash(readFileSync(resolve(review, name)));
const canonical = value => {
    if (Array.isArray(value)) {
        return value.map(canonical);
    }
    if (value !== null && typeof value === 'object') {
        return Object.fromEntries(Object.keys(value).sort().map(key => [key, canonical(value[key])]));
    }
    return value;
};
const canonicalHash = value => hash(JSON.stringify(canonical(value)));
const manifest = read('p1c/candidate-manifest.json');
const attribution = read('p1c/attribution-proposal.json');
const documents = read('p1b/official-documents.json');
const crosswalk = read('p1b/occurrence-crosswalk.json');
assert.equal(manifest.entries.length, 250);
assert.equal(new Set(manifest.entries.map(entry => entry.occurrenceId)).size, 250);
assert(manifest.entries.every(entry => entry.review.status === 'PENDING_REVIEW'));
const atPointer = (value, pointer) => pointer.slice(1).split('/').reduce(
    (current, segment) => current[segment.replaceAll('~1', '/').replaceAll('~0', '~')], value);
for (const entry of manifest.entries) {
    const row = atPointer(crosswalk, entry.eligibility.p1bReference.jsonPointer);
    assert.equal(row.occurrenceId, entry.occurrenceId);
    assert.equal(row.publicationReviewClassification, 'VERIFIED_LICENSED_MATCH');
    assert.equal(canonicalHash(row), entry.eligibility.p1bReference.valueSha256);
    assert.equal(canonicalHash(atPointer(crosswalk, entry.licensedMatch.matchReference.jsonPointer)),
        entry.licensedMatch.matchReference.valueSha256);
}

const candidates = {
    schemaVersion: 1,
    candidates: manifest.entries.map(entry => ({
        spellId: entry.spellId,
        sourceCode: entry.sourceCode,
        occurrenceIndex: entry.formulaOccurrenceIndex,
        sourceEvidence: {
            location: {
                owner: {
                    resourcePath: entry.sourceEvidence.resource,
                    resourceSha256: entry.sourceEvidence.resourceSha256,
                    collection: 'spell',
                    jsonPointer: entry.sourceEvidence.ownerPointer
                },
                jsonPointer: entry.sourceEvidence.jsonPointer,
                hierarchy: entry.sourceEvidence.entryHierarchy,
                origin: entry.sourceEvidence.origin
            },
            span: {
                startInclusive: entry.sourceEvidence.spanUtf16[0],
                endExclusive: entry.sourceEvidence.spanUtf16[1]
            }
        },
        expression: {
            authoredToken: entry.expression.authoredToken,
            kind: entry.expression.lexicalKind
        },
        notation: entry.expression.notation,
        tagSha256: entry.sourceEvidence.tagTokenSha256,
        reviewBindingSha256: entry.bindingSha256,
        evidenceId: entry.occurrenceId,
        noticeId: entry.attribution.noticeId,
        initialReviewState: 'PENDING_REVIEW'
    }))
};
const usedDocuments = new Set(manifest.entries.map(entry => entry.licensedMatch.documentId));
const evidence = {
    schemaVersion: 1,
    crosswalkSha256: artifactHash('p1b/occurrence-crosswalk.json'),
    documents: documents.documents.filter(document => usedDocuments.has(document.id)).map(document => ({
        id: document.id,
        version: document.version,
        url: document.url,
        sha256: document.sha256,
        licenseId: document.license,
        licenseUrl: documents.licenseUrl
    })),
    matches: manifest.entries.map(entry => ({
        id: entry.occurrenceId,
        classification: entry.eligibility.classification,
        occurrenceId: entry.occurrenceId,
        reviewBindingSha256: entry.bindingSha256,
        documentId: entry.licensedMatch.documentId,
        spellHeading: entry.licensedMatch.spellHeading,
        pdfPage: entry.licensedMatch.evidence.pdfPage,
        column: entry.licensedMatch.evidence.column,
        expression: entry.expression.authoredToken,
        matchSha256: entry.licensedMatch.matchReference.valueSha256,
        noticeId: entry.attribution.noticeId
    }))
};
const notices = {
    schemaVersion: 1,
    attributionProposalSha256: artifactHash('p1c/attribution-proposal.json'),
    approval: null,
    notices: attribution.notices.map(notice => ({
        id: notice.id,
        documentId: notice.documentId,
        requiredAttribution: notice.requiredAttribution,
        requiredAttributionSha256: notice.requiredAttributionSha256,
        changesDisclosure: attribution.changesDisclosure,
        changesDisclosureSha256: attribution.changesDisclosureSha256,
        disclaimerReference: notice.disclaimerReference
    }))
};
const files = new Map();
const add = (name, value) => files.set(name, JSON.stringify(value, null, 2) + '\n');
add('candidates.json', candidates);
add('evidence.json', evidence);
add('notices.json', notices);
add('policy.json', {
    schemaVersion: 1,
    datasetVersion: manifest.datasetVersion,
    scopeId: manifest.publicationScope.id,
    scopeSha256: canonicalHash(manifest.publicationScope),
    candidateManifestSha256: artifactHash('p1c/candidate-manifest.json'),
    attributionProposalSha256: artifactHash('p1c/attribution-proposal.json'),
    candidateCount: candidates.candidates.length,
    candidatesSha256: hash(files.get('candidates.json')),
    evidenceSha256: hash(files.get('evidence.json')),
    noticesSha256: hash(files.get('notices.json')),
    decisionsSha256: null,
    authorities: [],
    releaseApproval: null
});

const check = process.argv.includes('--check');
if (!check && existsSync(resolve(output, 'policy.json'))) {
    const current = JSON.parse(readFileSync(resolve(output, 'policy.json'), 'utf8'));
    assert.equal(current.releaseApproval, null, 'Cannot overwrite reviewed release policy');
    assert.equal(current.authorities.length, 0, 'Cannot overwrite reviewer assignments');
}
assert(!existsSync(resolve(output, 'decisions.json')), 'Initial export must not contain decisions');
mkdirSync(output, { recursive: true });
for (const [name, bytes] of files) {
    if (check) {
        assert.equal(readFileSync(resolve(output, name), 'utf8'), bytes, `Runtime artifact drift: ${name}`);
    } else {
        writeFileSync(resolve(output, name), bytes);
    }
}
console.log(JSON.stringify({ candidates: 250, owners: 195, decisions: 0, authorized: 0, checked: check }));
