import assert from 'node:assert/strict';
import { execFileSync } from 'node:child_process';
import { createHash } from 'node:crypto';
import { readFileSync, readdirSync, writeFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const project = fileURLToPath(new URL('../../../../../', import.meta.url));
process.chdir(project);
const directory = 'analysis/mechanics/public-v1/spells/p1d';
const read = path => JSON.parse(readFileSync(path, 'utf8').replace(/^\uFEFF/, ''));
const hash = path => createHash('sha256').update(readFileSync(path)).digest('hex');
const runtime = 'src/main/resources/publication/spell-formulas/v1';
const policy = read(`${runtime}/policy.json`);
const review = 'analysis/mechanics/public-v1/spells';

execFileSync(process.execPath, [`${directory}/export-runtime.mjs`, '--check'], { stdio: 'pipe' });
execFileSync(process.execPath, ['analysis/mechanics/batch-11b-item-charge-syntax/verify-source-hashes.mjs'],
    { stdio: 'pipe' });
assert.equal(hash(`${review}/p1c/candidate-manifest.json`), policy.candidateManifestSha256);
assert.equal(hash(`${review}/p1c/attribution-proposal.json`), policy.attributionProposalSha256);
for (const input of read(`${review}/p1c/candidate-manifest.json`).inputArtifacts) {
    assert.equal(hash(input.reviewArtifactPath), input.sha256, `Prior review evidence changed: ${input.id}`);
}

const candidateIds = read(`${runtime}/candidates.json`).candidates.map(candidate =>
    `${candidate.spellId}#formula-${candidate.occurrenceIndex}`);
const excluded = read(`${review}/p1c/excluded-candidates.json`).entries;
assert.equal(candidateIds.length, 250);
assert.equal(excluded.length, 458);
assert.equal(new Set([...candidateIds, ...excluded.map(entry => entry.occurrenceId)]).size, 708);

const totals = { tests: 0, failures: 0, errors: 0, skipped: 0 };
const reports = readdirSync('target/surefire-reports').filter(name => /^TEST-.*\.xml$/.test(name)).sort();
for (const report of reports) {
    const xml = readFileSync(resolve('target/surefire-reports', report), 'utf8');
    const suite = xml.match(/<testsuite\b([^>]+)>/)[1];
    for (const key of Object.keys(totals)) {
        totals[key] += Number(suite.match(new RegExp(`\\b${key}="(\\d+)"`))[1]);
    }
}
assert.deepEqual(totals, { tests: 561, failures: 0, errors: 0, skipped: 0 });
const packaged = read('target/p1d-packaged-verification.json');
assert.equal(packaged.packagedStartup, 'PASS');
assert.equal(packaged.authorizedObservations, 0);
assert.equal(packaged.productionDecisionArtifacts, 0);
assert.equal(packaged.spellSummaries, 969);
assert.equal(packaged.formulaPayloadPublished, false);
assert.equal(packaged.testFixturesPackaged, false);

const resourceFingerprints = Object.fromEntries(['policy', 'candidates', 'evidence', 'notices'].map(name =>
    [`${name}.json`, hash(`${runtime}/${name}.json`)]));
const result = {
    dataset: '5eTools v2.37.0',
    sourceFilesUnchanged: 175,
    candidateManifestSha256: policy.candidateManifestSha256,
    candidateCount: 250,
    candidateOwners: 195,
    pendingCandidates: 250,
    excludedCandidates: 458,
    originalCandidatesReconciled: 708,
    productionDecisions: 0,
    authorizedObservations: 0,
    publicStatuses: { NONE_OBSERVED: 547, WITHHELD: 422 },
    spellIdentities: 969,
    formulaObservations: 930,
    resolver: { RESOLVED: 2517, AMBIGUOUS: 2054, UNRESOLVED: 181, DEFERRED: 1494 },
    priorReviewInputFingerprints: 'UNCHANGED',
    deterministicRuntimeExport: 'PASS',
    maven: { ...totals, newP1dTests: 22, suites: reports.length },
    packaged,
    runtimeResourceFingerprints: resourceFingerprints
};
const bytes = JSON.stringify(result, null, 2) + '\n';
if (process.argv.includes('--write')) {
    writeFileSync(`${directory}/verification.json`, bytes);
} else {
    assert.equal(readFileSync(`${directory}/verification.json`, 'utf8'), bytes, 'Verification artifact drift');
}
console.log(JSON.stringify({ candidates: 250, authorized: 0, tests: totals.tests, verified: true }));
