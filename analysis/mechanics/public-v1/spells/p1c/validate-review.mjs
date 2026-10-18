import assert from 'node:assert/strict';
import fs from 'node:fs';
import { createRequire } from 'node:module';
import { spawnSync } from 'node:child_process';
import {
    atPointer, buildArtifacts, directory, fileHash, formatted, pendingReview, publicationScope,
    read, root, sha256, valueHash,
} from './review-artifacts.mjs';

// Isolated analysis tooling only: no dependency is added to the application or its tests.
const require = createRequire(new URL('../../../../../target/p1c-schema-tools/package.json', import.meta.url));
const Ajv2020 = require('ajv/dist/2020.js').default;
const addFormats = require('ajv-formats');
const ajv = new Ajv2020({ allErrors: true, strict: true });
addFormats(ajv);
const schemas = new Map([
    ['manifest', 'candidate-manifest.schema.json'],
    ['attribution', 'attribution-proposal.schema.json'],
    ['journal', 'decision-journal.schema.json'],
].map(([name, file]) => [name, ajv.compile(read(`${directory}/${file}`))]));
const crosswalk = read(`${root}/p1b/occurrence-crosswalk.json`);
const p1bById = new Map(crosswalk.map((row) => [row.occurrenceId, row]));
const verifiedIds = crosswalk.filter((row) => row.publicationReviewClassification === 'VERIFIED_LICENSED_MATCH')
    .map((row) => row.occurrenceId);
const runtime = read(`${root}/runtime-evidence.json`);
const formulaById = new Map(runtime.formulas.map((row) => [`${row.spellId}#formula-${row.occurrenceIndex}`, row]));
const runtimeOwners = new Map(runtime.records.map((row) => [row.id, row]));
const official = read(`${root}/p1b/official-documents.json`);
const p1bAttribution = read(`${root}/p1b/attribution-review.json`);
const expectedInputs = buildArtifacts().get('candidate-manifest.json').inputArtifacts;
const sourceCache = new Map();
const artifactCache = new Map();

function source(resource) {
    if (!sourceCache.has(resource)) {
        sourceCache.set(resource, read(`src/main/resources/${resource}`));
    }
    return sourceCache.get(resource);
}

function schemaValid(name, value) {
    const validate = schemas.get(name);
    assert.ok(validate(value), `${name} schema: ${ajv.errorsText(validate.errors)}`);
}

function validatePendingBundle(bundle) {
    const { manifest, attribution, journal, exclusions } = bundle;
    schemaValid('manifest', manifest);
    schemaValid('attribution', attribution);
    schemaValid('journal', journal);
    assert.deepEqual(manifest.publicationScope, publicationScope());
    assert.deepEqual(manifest.inputArtifacts, expectedInputs);
    const byArtifactId = new Map(manifest.inputArtifacts.map((artifact) => [artifact.id, artifact]));
    for (const artifact of manifest.inputArtifacts) {
        assert.equal(fileHash(artifact.reviewArtifactPath), artifact.sha256, artifact.id);
    }
    function referenceValue(reference) {
        const artifact = byArtifactId.get(reference.artifactId);
        assert.ok(artifact, `Unknown evidence artifact: ${reference.artifactId}`);
        assert.equal(reference.artifactSha256, artifact.sha256);
        if (!artifactCache.has(artifact.id)) {
            artifactCache.set(artifact.id, read(artifact.reviewArtifactPath));
        }
        const value = atPointer(artifactCache.get(artifact.id), reference.jsonPointer);
        assert.equal(valueHash(value), reference.valueSha256, 'Evidence value fingerprint mismatch');
        return value;
    }

    const ids = manifest.entries.map((entry) => entry.occurrenceId);
    assert.equal(new Set(ids).size, 250, 'Duplicate candidate occurrence');
    assert.deepEqual(ids, verifiedIds, 'Candidate scope, completeness or ordering changed');
    const planHash = sha256(formatted(attribution));
    for (const candidate of manifest.entries) {
        const original = p1bById.get(candidate.occurrenceId);
        assert.equal(original.publicationReviewClassification, 'VERIFIED_LICENSED_MATCH');
        assert.equal(candidate.spellId, original.spellId);
        assert.equal(candidate.sourceCode, original.sourceCode);
        assert.equal(candidate.formulaOccurrenceIndex, original.formulaOccurrenceIndex);
        assert.equal(candidate.publicationScopeId, manifest.publicationScope.id);
        assert.equal(candidate.publicationScopeSha256, valueHash(manifest.publicationScope));
        assert.deepEqual(candidate.review, pendingReview());
        const { review, bindingSha256, ...boundEvidence } = candidate;
        assert.equal(valueHash(boundEvidence), bindingSha256, 'Candidate binding changed');

        const formula = formulaById.get(candidate.occurrenceId);
        assert.ok(formula, 'Occurrence missing from recorded Batch 6 index');
        assert.ok(runtimeOwners.has(candidate.spellId));
        assert.equal(runtimeOwners.get(candidate.spellId).source, candidate.sourceCode);
        assert.equal(candidate.expression.authoredToken, formula.expression);
        assert.equal(candidate.expression.sha256, sha256(formula.expression));
        assert.equal(candidate.expression.lexicalKind, formula.expressionKind);
        assert.equal(candidate.expression.notation, formula.tag === 'damage' ? 'DAMAGE_TAG' : 'DICE_TAG');
        assert.equal(formula.arguments.length, 1);
        const evidence = candidate.sourceEvidence;
        assert.equal(evidence.resource, formula.resource);
        assert.equal(evidence.resourceSha256, runtime.resourceHashes[formula.resource]);
        assert.equal(fileHash(`src/main/resources/${evidence.resource}`), evidence.resourceSha256);
        assert.equal(evidence.ownerPointer, formula.ownerPointer);
        assert.equal(evidence.jsonPointer, formula.pointer);
        assert.equal(evidence.entrySection, formula.section);
        assert.deepEqual(evidence.entryHierarchy, formula.hierarchy);
        assert.equal(evidence.origin, formula.origin);
        assert.deepEqual(evidence.spanUtf16, formula.spanUtf16);
        const text = atPointer(source(evidence.resource), evidence.jsonPointer);
        assert.equal(sha256(text), evidence.textSha256);
        assert.ok(evidence.spanUtf16[1] > evidence.spanUtf16[0]);
        assert.equal(text.slice(...evidence.spanUtf16), formula.originalTag);
        assert.equal(sha256(formula.originalTag), evidence.tagTokenSha256);
        assert.equal(atPointer(source(evidence.resource), evidence.ownerPointer).source, candidate.sourceCode);

        assert.deepEqual(referenceValue(candidate.eligibility.p1bReference), original);
        const licensed = candidate.licensedMatch;
        const match = referenceValue(licensed.matchReference);
        assert.ok(original.verifiedMatches.some((value) => valueHash(value) === valueHash(match)));
        assert.equal(match.documentId, licensed.documentId);
        assert.equal(match.entry, licensed.spellHeading);
        assert.deepEqual(licensed.evidence, match.evidence);
        assert.equal(licensed.documentId, original.potentialReleaseBasis.documentId);
        const document = referenceValue(licensed.documentReference);
        assert.ok(official.documents.some((value) => valueHash(value) === valueHash(document)));
        assert.equal(document.id, licensed.documentId);
        assert.equal(document.version, licensed.documentVersion);
        assert.equal(document.sha256, licensed.documentSha256);
        assert.equal(document.url, licensed.documentUrl);
        assert.equal(licensed.licenseId, document.license);
        assert.equal(licensed.licenseUrl, official.licenseUrl);
        const descriptor = original.documentSearches.find((search) => search.documentId === document.id)
            .entries.find((entry) => entry.heading === match.entry);
        assert.equal(licensed.descriptor, descriptor.descriptor);

        const notice = referenceValue(candidate.attribution.noticeReference);
        assert.equal(notice.documentId, document.id);
        assert.equal(notice.id, candidate.attribution.noticeId);
        assert.equal(sha256(notice.requiredAttribution), candidate.attribution.requiredAttributionSha256);
        assert.equal(candidate.attribution.proposalSha256, planHash);
        assert.equal(candidate.attribution.changesDisclosureSha256, sha256(attribution.changesDisclosure));
        const configured = attribution.notices.find((value) => value.id === notice.id);
        assert.ok(configured, 'Required attribution is not configured');
        assert.equal(configured.requiredAttribution, notice.requiredAttribution);
    }
    assert.deepEqual(attribution.review, pendingReview());
    assert.equal(attribution.notices.length, 2);
    assert.equal(new Set(attribution.notices.map((notice) => notice.id)).size, 2);
    for (const notice of attribution.notices) {
        const original = referenceValue(notice.p1bNoticeReference);
        assert.equal(notice.requiredAttribution, original.requiredAttribution);
        assert.equal(notice.requiredAttributionSha256, sha256(original.requiredAttribution));
        assert.ok(p1bAttribution.notices.some((value) => valueHash(value) === valueHash(original)));
    }
    assert.equal(attribution.changesDisclosure, p1bAttribution.proposedChangeNotice);
    assert.equal(journal.candidateManifestSha256, sha256(formatted(manifest)));
    assert.equal(journal.events.length, 0, 'Initial review must not contain human decision events');
    assert.equal(journal.headEventSha256, null);
    assert.equal(exclusions.candidateManifestSha256, sha256(formatted(manifest)));
    assert.equal(exclusions.originalCandidateCount, 708);
    assert.equal(exclusions.reviewedCandidateCount, 250);
    assert.equal(exclusions.excludedCandidateCount, 458);
    assert.equal(new Set(exclusions.entries.map((entry) => entry.occurrenceId)).size, 458);
    const excludedOriginal = crosswalk.filter((row) => row.publicationReviewClassification !== 'VERIFIED_LICENSED_MATCH');
    assert.deepEqual(exclusions.entries.map((entry) => entry.occurrenceId), excludedOriginal.map((row) => row.occurrenceId));
    for (const excluded of exclusions.entries) {
        const original = p1bById.get(excluded.occurrenceId);
        assert.equal(excluded.originalClassification, original.publicationReviewClassification);
        assert.deepEqual(referenceValue(excluded.p1bReference), original);
        assert.ok(!ids.includes(excluded.occurrenceId));
    }
    assert.equal(new Set([...ids, ...exclusions.entries.map((entry) => entry.occurrenceId)]).size, 708);
}

const bundle = {
    manifest: read(`${directory}/candidate-manifest.json`),
    attribution: read(`${directory}/attribution-proposal.json`),
    journal: read(`${directory}/decision-journal.json`),
    exclusions: read(`${directory}/excluded-candidates.json`),
};
validatePendingBundle(bundle);
const corruptions = [
    ['duplicate-occurrence', (value) => {
        value.manifest.entries[1] = structuredClone(value.manifest.entries[0]);
    }],
    ['unknown-spell-id', (value) => {
        value.manifest.entries[0].spellId = 'spell:phb:unknown';
    }],
    ['expression-changed', (value) => {
        value.manifest.entries[0].expression.authoredToken = '99d99';
    }],
    ['notation-changed', (value) => {
        const expression = value.manifest.entries[0].expression;
        expression.notation = expression.notation === 'DAMAGE_TAG' ? 'DICE_TAG' : 'DAMAGE_TAG';
    }],
    ['source-identity-changed', (value) => {
        const entry = value.manifest.entries[0];
        entry.sourceCode = entry.sourceCode === 'PHB' ? 'XPHB' : 'PHB';
    }],
    ['resource-fingerprint-changed', (value) => {
        value.manifest.entries[0].sourceEvidence.resourceSha256 = '0'.repeat(64);
    }],
    ['source-pointer-changed', (value) => {
        value.manifest.entries[0].sourceEvidence.jsonPointer = '/spell/999/entries/0';
    }],
    ['source-span-changed', (value) => {
        value.manifest.entries[0].sourceEvidence.spanUtf16[0]++;
    }],
    ['source-revision-changed', (value) => {
        value.manifest.datasetVersion = '5eTools v2.37.1';
    }],
    ['missing-evidence-reference', (value) => {
        delete value.manifest.entries[0].licensedMatch.matchReference;
    }],
    ['unknown-evidence-artifact', (value) => {
        value.manifest.entries[0].licensedMatch.matchReference.artifactId = 'unknown';
    }],
    ['evidence-fingerprint-changed', (value) => {
        value.manifest.entries[0].licensedMatch.matchReference.valueSha256 = '0'.repeat(64);
    }],
    ['document-version-changed', (value) => {
        value.manifest.entries[0].licensedMatch.documentVersion = '5.2.1';
    }],
    ['unknown-license', (value) => {
        value.manifest.entries[0].licensedMatch.licenseId = 'MIT';
    }],
    ['missing-attribution', (value) => {
        value.attribution.notices.pop();
    }],
    ['attribution-statement-changed', (value) => {
        value.attribution.notices[0].requiredAttribution += ' Changed';
    }],
    ['missing-review-state', (value) => {
        delete value.manifest.entries[0].review;
    }],
    ['unknown-review-state', (value) => {
        value.manifest.entries[0].review.status = 'AUTO_APPROVED';
    }],
    ['pending-with-decision', (value) => {
        value.manifest.entries[0].review.decision = 'AUTHORIZE_EXACT_SCOPE';
    }],
    ['withdrawn-is-not-pending', (value) => {
        value.manifest.entries[0].review.status = 'WITHDRAWN';
    }],
    ['rejected-is-not-pending', (value) => {
        value.manifest.entries[0].review.status = 'REJECTED';
    }],
    ['unknown-journal-action', (value) => {
        value.journal.events.push({ action: 'PUBLISH_EVERYTHING' });
    }],
    ['payload-scope-widened', (value) => {
        value.manifest.publicationScope.publicFieldPaths.push('SpellDto.entries');
    }],
    ['unreviewed-public-field', (value) => {
        value.manifest.entries[0].damageType = 'fire';
    }],
    ['excluded-classification-promoted', (value) => {
        value.exclusions.entries[0].originalClassification = 'VERIFIED_LICENSED_MATCH';
    }],
    ['missing-candidate', (value) => {
        value.manifest.entries.pop();
    }],
    ['changed-order', (value) => {
        value.manifest.entries.reverse();
    }],
];
const mutationResults = [];
for (const [id, mutate] of corruptions) {
    const changed = structuredClone(bundle);
    mutate(changed);
    assert.notDeepEqual(changed, bundle, `Validation mutation did not change its input: ${id}`);
    assert.throws(() => validatePendingBundle(changed), undefined, `Corruption was not rejected: ${id}`);
    mutationResults.push({ id, expected: 'REJECT_INVALID_INITIAL_REVIEW_ARTIFACT', result: 'REJECTED' });
}

const pinned = read('analysis/mechanics/batch-10a-object-membership/pre-implementation-hashes.json');
const sourcePaths = Object.keys(pinned).filter((path) =>
    path.startsWith('src/main/resources/data/') && path.endsWith('.json'));
assert.equal(sourcePaths.length, 175);
for (const path of sourcePaths) {
    assert.equal(fileHash(path), pinned[path], path);
}
const protectedInputs = read(`${directory}/protected-input-hashes.json`);
const roadmap = 'analysis/mechanics/public-v1/ROADMAP.md';
for (const [path, hash] of Object.entries(protectedInputs)) {
    if (path !== roadmap) {
        assert.equal(fileHash(path), hash, `Pre-existing file changed: ${path}`);
    }
}
const newFiles = [];
function checkWorkspace(path) {
    for (const entry of fs.readdirSync(path, { withFileTypes: true })) {
        const child = `${path}/${entry.name}`;
        if (entry.isDirectory()) {
            checkWorkspace(child);
        } else if (!Object.hasOwn(protectedInputs, child)) {
            assert.ok(child.startsWith(`${directory}/`) || child === `${root}/P1C-APPROVAL-DESIGN.md`,
                `Unexpected new file outside P1C: ${child}`);
            newFiles.push(child);
            fs.readFileSync(child, 'utf8').split(/\r?\n/).forEach((line, index) => {
                assert.ok(!/[\t ]+$/.test(line), `Trailing whitespace: ${child}:${index + 1}`);
            });
        }
    }
}
checkWorkspace('src');
checkWorkspace('analysis');
const generated = buildArtifacts();
for (const [file, value] of generated) {
    assert.equal(fs.readFileSync(`${directory}/${file}`, 'utf8'), formatted(value), `Non-deterministic artifact: ${file}`);
}
const diffCheck = spawnSync('git', ['-c', 'core.safecrlf=false', 'diff', '--check'], { encoding: 'utf8' });
assert.equal(diffCheck.status, 0, diffCheck.stdout + diffCheck.stderr);

const report = {
    artifactRole: 'P1C_INITIAL_REVIEW_VALIDATION_NOT_PUBLICATION_AUTHORIZATION',
    candidateCount: bundle.manifest.entries.length,
    candidateOwners: new Set(bundle.manifest.entries.map((entry) => entry.spellId)).size,
    pendingReviewCount: 250,
    populatedHumanDecisionFields: 0,
    decisionEvents: 0,
    excludedCount: 458,
    excludedClassifications: { AMBIGUOUS_MATCH: 147, NO_VERIFIABLE_EVIDENCE: 311 },
    allOriginalCandidatesReconciled: 708,
    selectedLicensedDocuments: { 'SRD-5.1': 130, 'SRD-5.2.1': 120 },
    configuredNoticeCount: 2,
    authorizedObservations: 0,
    publicStatusChanges: 0,
    currentPublicStatuses: { NONE_OBSERVED: 547, WITHHELD: 422, PARTIAL: 0, COMPLETE: 0 },
    schemasValidated: 3,
    invalidArtifactCasesRejected: mutationResults.length,
    pinnedSourceHashesVerified: sourcePaths.length,
    protectedFilesUnchanged: Object.keys(protectedInputs).length - 1,
    solePermittedExistingFileEdit: roadmap,
    productionAndTestsUnchanged: true,
    p1bEvidenceUnchanged: true,
    deterministicGeneration: true,
    gitDiffCheck: 'PASS',
    mavenRun: false,
    schemaTools: { ajv: require('ajv/package.json').version, ajvFormats: require('ajv-formats/package.json').version },
    generatedSha256: Object.fromEntries([...generated.keys()].map((file) => [file, fileHash(`${directory}/${file}`)])),
};
fs.writeFileSync(`${directory}/validation-cases.json`, formatted(mutationResults));
fs.writeFileSync(`${directory}/verification.json`, formatted(report));
console.log(formatted(report));
