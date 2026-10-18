import assert from 'node:assert/strict';
import { execFileSync } from 'node:child_process';
import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';

const directory = 'analysis/mechanics/batch-10-object-relationships';
const java = process.argv[2] ?? 'java';
const dependencies = fs.readFileSync('target/analysis-classpath.txt', 'utf8').trim();
const classpath = `${path.resolve('target/classes')}${path.delimiter}${dependencies}`;
const outputs = ['runtime-evidence.json', 'gold-set.json', 'source-evidence.json', 'verification.json'];

function run(command, arguments_) {
  const output = execFileSync(command, arguments_, { encoding: 'utf8', maxBuffer: 1024 * 1024 });
  process.stdout.write(output);
}

function generate() {
  run(java, ['--class-path', classpath, `${directory}/ObjectRelationshipProbe.java`]);
  run(process.execPath, [`${directory}/build-gold-set.mjs`]);
  run(process.execPath, [`${directory}/audit-source.mjs`]);
  return Object.fromEntries(outputs.map((file) => [file, crypto.createHash('sha256')
    .update(fs.readFileSync(`${directory}/${file}`)).digest('hex')]));
}

const first = generate();
const second = generate();
assert.deepEqual(second, first, 'Evidence generation was not byte-deterministic');
fs.writeFileSync(`${directory}/determinism.json`, `${JSON.stringify({
  runs: 2,
  identical: true,
  sha256: first,
  scope: 'Existing Java mapper/parser probe, reviewed gold expansion and source audit; no production extractor',
}, null, 2)}\n`);
console.log('All four generated evidence files are byte-identical across two complete runs');
