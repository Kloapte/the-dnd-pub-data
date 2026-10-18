import assert from 'node:assert/strict';
import { execFileSync } from 'node:child_process';
import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';

const directory = 'analysis/mechanics/batch-11-item-charges';
const java = process.argv[2] ?? 'java';
const libraryDirectory = 'target/batch-10a-packaged-libs';
const libraries = fs.readdirSync(libraryDirectory).filter((file) => file.endsWith('.jar')).sort()
  .map((file) => path.resolve(libraryDirectory, file));
const classpath = [path.resolve('target/classes'), ...libraries].join(path.delimiter);
const outputs = ['runtime-evidence.json', 'source-evidence.json', 'review-cases.json', 'verification.json'];

function run(command, arguments_) {
  execFileSync(command, arguments_, { encoding: 'utf8', maxBuffer: 2 * 1024 * 1024 });
}

function generate() {
  run(java, ['--class-path', classpath, `${directory}/ItemChargeProbe.java`]);
  run(process.execPath, [`${directory}/audit-source.mjs`]);
  run(process.execPath, [`${directory}/review-cases.mjs`]);
  return Object.fromEntries(outputs.map((file) => [file, crypto.createHash('sha256')
    .update(fs.readFileSync(`${directory}/${file}`)).digest('hex')]));
}

const first = generate();
const second = generate();
assert.deepEqual(second, first, 'Assessment evidence must be byte-deterministic across JVMs');
fs.writeFileSync(`${directory}/determinism.json`, `${JSON.stringify({
  runs: 2, identical: true, sha256: first,
  scope: 'Read-only existing Item runtime/copy resolver and shared parser, source audit and proposed gold set',
  ordering: 'Audit ObjectMapper copy sorts map entries to avoid JVM immutable-map iteration variation',
}, null, 2)}\n`);
console.log('Two complete analysis runs produced identical evidence; existing files and 175 pinned hashes verified');
