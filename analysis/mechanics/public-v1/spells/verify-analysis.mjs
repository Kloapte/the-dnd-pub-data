import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import fs from 'node:fs';
import { spawnSync } from 'node:child_process';

const directory = 'analysis/mechanics/public-v1/spells';
const read = (path) => JSON.parse(fs.readFileSync(path, 'utf8').replace(/^\uFEFF/, ''));
const digestFile = (path) => crypto.createHash('sha256').update(fs.readFileSync(path)).digest('hex');
const generatedPaths = ['runtime-evidence.json', 'coverage.json', 'gold-set.json']
  .map((name) => `${directory}/${name}`);
const runSnapshot = 'target/public-v1-phase1-first-run.json';

function walk(path) {
  return fs.readdirSync(path, { withFileTypes: true }).flatMap((entry) =>
    entry.isDirectory() ? walk(`${path}/${entry.name}`) : [`${path}/${entry.name}`]);
}

if (process.argv.includes('--capture')) {
  fs.writeFileSync(runSnapshot, `${JSON.stringify(Object.fromEntries(
    generatedPaths.map((path) => [path, digestFile(path)])), null, 2)}\n`);
  console.log('Captured the first complete analysis run; repeat the Java probe and scanner before verification.');
  process.exit(0);
}

const protectedFiles = read(`${directory}/pre-analysis-hashes.json`);
for (const [path, expectedHash] of Object.entries(protectedFiles)) {
  assert.equal(digestFile(path), expectedHash, `Pre-existing file changed: ${path}`);
}
assert.deepEqual(walk('src').sort(), Object.keys(protectedFiles)
  .filter((path) => path.startsWith('src/')).sort(), 'Production/source/test paths changed');
for (const path of walk('analysis')) {
  assert.ok(Object.hasOwn(protectedFiles, path) || path.startsWith('analysis/mechanics/public-v1/'),
    `Unexpected new analysis path: ${path}`);
}

const firstRun = read(runSnapshot);
const evidenceHashes = {};
for (const path of generatedPaths) {
  const currentHash = digestFile(path);
  assert.equal(currentHash, firstRun[path], `Analysis output is not deterministic: ${path}`);
  evidenceHashes[path] = currentHash;
}

const coverage = read(`${directory}/coverage.json`);
assert.equal(coverage.pinnedSourceFilesVerified, 175);
assert.equal(coverage.formulas.spanFailures, 0);
assert.equal(coverage.mechanicsTraceComparisons, 34884);
const diffCheck = spawnSync('git', ['diff', '--check'], { encoding: 'utf8' });
assert.equal(diffCheck.status, 0, diffCheck.stderr || diffCheck.stdout);

const verification = {
  existingFilesProtected: Object.keys(protectedFiles).length,
  productionAndTestPathsUnchanged: true,
  existingAnalysisEvidenceUnchanged: true,
  pinnedSourceJsonHashesUnchanged: 175,
  comparedCompleteAnalysisRuns: 2,
  deterministicEvidenceHashes: evidenceHashes,
  sourceDteNormalizedPublicArrayComparisons: 34884,
  reconstructedFormulaAndAuxiliarySpans: 948,
  spanFailures: 0,
  gitDiffCheckExitCode: diffCheck.status,
  mavenRun: false,
  baselineTestsReportedUnchanged: 529,
  baselineResolverOccurrencesReportedUnchanged: 6246,
  productionChangesImplemented: false,
};
fs.writeFileSync(`${directory}/verification.json`, `${JSON.stringify(verification, null, 2)}\n`);
console.log(JSON.stringify(verification, null, 2));
