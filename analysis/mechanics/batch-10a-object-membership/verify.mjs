import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import fs from 'node:fs';

const directory = 'analysis/mechanics/batch-10a-object-membership';
const read = (file) => JSON.parse(fs.readFileSync(file, 'utf8').replace(/^\uFEFF/, ''));
const hash = (file) => crypto.createHash('sha256').update(fs.readFileSync(file)).digest('hex');
const baseline = read(`${directory}/pre-implementation-hashes.json`);
let unchangedSourceFiles = 0;
let unchangedPreviousFiles = 0;
for (const [file, expected] of Object.entries(baseline)) {
  if (file === 'analysis/mechanics/IMPLEMENTATION-ORDER.md') {
    continue;
  }
  assert.equal(hash(file), expected, `Pre-existing file changed: ${file}`);
  unchangedPreviousFiles++;
  if (file.startsWith('src/main/resources/data/') && file.endsWith('.json')) {
    unchangedSourceFiles++;
  }
}
assert.equal(unchangedSourceFiles, 175);
const originalPinned = read('analysis/mechanics/batch-9a-class-choice-completeness/pre-implementation-hashes.json');
for (const [file, expected] of Object.entries(originalPinned)) {
  if (file.startsWith('src/main/resources/data/') && file.endsWith('.json')) {
    assert.equal(hash(file), expected, `Pinned source changed: ${file}`);
  }
}

const original = read(`${directory}/baseline-runtime.json`);
const current = read(`${directory}/current-runtime.json`);
const packaged = read(`${directory}/packaged-runtime.json`);
for (const runtime of [current, packaged]) {
  for (const [key, value] of Object.entries(original)) {
    assert.deepEqual(runtime[key], value, `Baseline runtime contract changed: ${key}`);
  }
  assert.equal(runtime.membershipCount, 63);
}
assert.deepEqual(packaged, current, 'Packaged and classpath runtime evidence must match');
assert.equal(packaged.resolverCount, 6246);
assert.equal(packaged.objectCount, 37);
const gold = read(`${directory}/gold-verification.json`);
assert.equal(gold.labels, 80);
assert.equal(gold.eligibleTruePositives, 35);
assert.equal(gold.eligibleFalsePositives, 0);
assert.equal(gold.eligibleFalseNegatives, 0);
assert.equal(gold.structuralTrueNegatives, 9);
assert.equal(gold.requiredAbstentions, 33);
assert.equal(gold.excludedPositiveControls, 3);
assert.equal(gold.boundaryViolations, 0);
const logBytes = fs.readFileSync('target/batch-10a-package.log');
const logEncoding = logBytes[0] === 0xff && logBytes[1] === 0xfe ? 'utf16le' : 'utf8';
const log = logBytes.toString(logEncoding);
assert.ok(/Tests run: 518, Failures: 0, Errors: 0, Skipped: 0/.test(log), 'Expected full-suite summary');
assert.ok(log.includes('BUILD SUCCESS'));
fs.writeFileSync(`${directory}/verification.json`, `${JSON.stringify({
  sourceHashesUnchanged: unchangedSourceFiles,
  preExistingFilesUnchanged: unchangedPreviousFiles,
  existingFileException: 'Approved implementation-order update only; existing production/tests unchanged',
  fullMavenTests: 518,
  addedTests: 14,
  gold: {
    labels: 80,
    eligibleTruePositives: 35,
    eligibleFalsePositives: 0,
    eligibleFalseNegatives: 0,
    structuralTrueNegatives: 9,
    abstentions: 33,
    excludedPositiveControls: 3,
    eligiblePrecision: 1,
    eligibleRecall: 1,
  },
  packagedClasspathEqual: true,
  objectDefinitionsAndPublicJsonEqualToBaseline: true,
  resolverFullFingerprintUnchanged: true,
  resolverOccurrences: packaged.resolverCount,
  resolverOutcomes: packaged.resolverOutcomes,
  memberships: packaged.membershipCount,
  membershipSha256: packaged.membershipSha256,
}, null, 2)}\n`);
console.log('Verified pinned hashes, prior files, gold labels, 518 tests and packaged/runtime fingerprints');
