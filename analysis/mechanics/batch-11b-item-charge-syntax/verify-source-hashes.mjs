import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import fs from 'node:fs';

const baselinePath = 'analysis/mechanics/batch-10a-object-membership/pre-implementation-hashes.json';
const baseline = JSON.parse(fs.readFileSync(baselinePath, 'utf8'));
const sourcePaths = Object.keys(baseline)
  .filter((path) => path.startsWith('src/main/resources/data/') && path.endsWith('.json'))
  .sort();

assert.equal(sourcePaths.length, 175, 'Pinned source file count changed');
for (const path of sourcePaths) {
  const actual = crypto.createHash('sha256').update(fs.readFileSync(path)).digest('hex');
  assert.equal(actual, baseline[path], `Pinned source changed: ${path}`);
}

console.log(`Verified ${sourcePaths.length} pinned source JSON hashes.`);
