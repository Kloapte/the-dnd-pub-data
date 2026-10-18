import crypto from 'node:crypto';
import fs from 'node:fs';

const directory = 'analysis/mechanics/batch-9a-class-choice-completeness';
const baselineHashes = JSON.parse(fs.readFileSync(`${directory}/pre-implementation-hashes.json`, 'utf8'));
const baselineRuntime = JSON.parse(fs.readFileSync(`${directory}/baseline-runtime.json`, 'utf8'));
const packagedRuntime = JSON.parse(fs.readFileSync(`${directory}/packaged-runtime.json`, 'utf8'));
const audit = JSON.parse(fs.readFileSync(
  'analysis/mechanics/batch-9-class-completeness/source-occurrences.json', 'utf8',
));

function requireTrue(condition, message) {
  if (!condition) {
    throw new Error(message);
  }
}

const expectedChanges = [
  'src/main/java/com/thedndpub/data/dto/classes/ClassFeatureDto.java',
  'src/main/java/com/thedndpub/data/dto/classes/ClassPublicMapper.java',
  'src/main/java/com/thedndpub/data/dto/classes/SubclassFeatureDto.java',
];
const changedFiles = [];
let sourceJsonCount = 0;
for (const [file, expectedHash] of Object.entries(baselineHashes)) {
  const currentHash = crypto.createHash('sha256').update(fs.readFileSync(file)).digest('hex');
  if (file.startsWith('src/main/resources/data/') && file.endsWith('.json')) {
    sourceJsonCount += 1;
    requireTrue(currentHash === expectedHash, `Source JSON changed: ${file}`);
  }
  if (currentHash !== expectedHash) {
    changedFiles.push(file);
  }
}
requireTrue(sourceJsonCount === 175, `Unexpected source count: ${sourceJsonCount}`);
requireTrue(JSON.stringify(changedFiles.sort()) === JSON.stringify(expectedChanges.sort()),
  `Unexpected pre-existing file changes: ${changedFiles.join(', ')}`);

const fingerprintFields = [
  'classDomainDefinitions',
  'classDomainIdentitiesSha256',
  'normalizedClassDomainSha256',
  'legacyPublicClassDomainSha256',
  'resolverOccurrences',
  'resolverSha256',
];
for (const field of fingerprintFields) {
  requireTrue(baselineRuntime[field] === packagedRuntime[field], `Runtime baseline changed: ${field}`);
}
requireTrue(packagedRuntime.classResourceUrl.startsWith('jar:'), 'Class source did not load from the JAR');
const expectedStatuses = { COMPLETE: 36, NONE: 2136, PARTIAL: 2, WITHHELD: 2 };
requireTrue(JSON.stringify(packagedRuntime.choiceProjectionStatuses) === JSON.stringify(expectedStatuses),
  'Packaged feature status distribution changed');

const reconciliation = audit.optionOccurrences.map((option) => {
  const sourcePointer = `data/classes/${option.file}#${option.pointer}`;
  const owners = packagedRuntime.choiceReconciliation.filter((owner) =>
    sourcePointer.startsWith(`${owner.sourceLocator}/`));
  requireTrue(owners.length === 1, `Expected exactly one public owner: ${sourcePointer}`);
  const owner = owners[0];
  let expectedStatus = 'COMPLETE';
  if (option.references.length === 0) {
    expectedStatus = 'WITHHELD';
  } else if (option.references.some((reference) =>
    reference.name !== null || reference.data?.isRequiredOption !== undefined)) {
    expectedStatus = 'PARTIAL';
  }
  requireTrue(owner.choiceProjectionStatus === expectedStatus, `Unexpected status: ${sourcePointer}`);
  return {
    sourcePointer,
    featureId: owner.id,
    status: owner.choiceProjectionStatus,
    existingPublicReferenceAlternatives: option.references.length,
  };
});
requireTrue(reconciliation.length === 40, 'Not all 40 choices were reconciled');
requireTrue(audit.inheritedObservationOwners.length === 0, 'Unexpected inherited options');
requireTrue(reconciliation.reduce((total, choice) => total + choice.existingPublicReferenceAlternatives, 0) === 222,
  'Expected 222 unchanged public reference alternatives');

const testTotals = { tests: 0, failures: 0, errors: 0, skipped: 0 };
for (const file of fs.readdirSync('target/surefire-reports').filter((name) => /^TEST-.*\.xml$/.test(name))) {
  const xml = fs.readFileSync(`target/surefire-reports/${file}`, 'utf8');
  const attributes = xml.match(/<testsuite\b[^>]*>/)?.[0] ?? '';
  for (const field of Object.keys(testTotals)) {
    testTotals[field] += Number(attributes.match(new RegExp(`\\b${field}="(\\d+)"`))?.[1] ?? 0);
  }
}
requireTrue(testTotals.tests === 504 && testTotals.failures + testTotals.errors + testTotals.skipped === 0,
  `Unexpected Maven results: ${JSON.stringify(testTotals)}`);

const report = {
  unchangedSourceJsonCount: sourceJsonCount,
  changedExistingFiles: changedFiles,
  originalAssessmentEvidenceUnchanged: true,
  runtimeFingerprintsUnchanged: fingerprintFields,
  packagedResourceLoading: true,
  featureStatuses: packagedRuntime.choiceProjectionStatuses,
  testTotals,
  reconciliation,
};
fs.writeFileSync(`${directory}/verification.json`, `${JSON.stringify(report, null, 2)}\n`);
console.log(JSON.stringify({ ...report, reconciliation: `${reconciliation.length} option nodes` }, null, 2));
