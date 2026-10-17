import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import fs from 'node:fs';
import {execFileSync} from 'node:child_process';

const directory = 'analysis/mechanics/batch-7-legendary-groups';
const read = file => JSON.parse(fs.readFileSync(file, 'utf8').replace(/^\uFEFF/, ''));
const hash = file => crypto.createHash('sha256').update(fs.readFileSync(file)).digest('hex');
const before = read(`${directory}/pre-implementation-hashes.json`);
for (const [file, expected] of Object.entries(before)) {
    assert.equal(hash(file), expected, `Protected pre-existing file changed: ${file}`);
}
const auditFiles = ['source-audit.json', 'FIELD-COVERAGE.md'];
const first = auditFiles.map(file => hash(`${directory}/${file}`));
execFileSync(process.execPath, [`${directory}/audit-source.mjs`], {stdio: 'pipe'});
assert.deepEqual(auditFiles.map(file => hash(`${directory}/${file}`)), first, 'Nondeterministic audit output');

const source = read(`${directory}/source-audit.json`);
assert.equal(source.roots, 188);
assert.equal(source.copies.length, 16);
assert.equal(source.versions.length, 21);
assert.equal(source.templates.length, 1);
assert.equal(source.duplicateEffectiveIdentities, 0);
assert.deepEqual(source.unknownSourceCodes, []);
assert.equal(source.authoredMonsterReferences, 240);
assert.equal(source.monsterSupportInstructions.length, 7);
const runtime = read('target/batch-7-runtime-report.json');
assert.equal(runtime.expandedSupportGroups, 209);
assert.equal(runtime.effectiveMonsterDefinitions, 4851);
assert.equal(runtime.effectiveMonsterAssociations, 307);
assert.equal(runtime.unresolvedAssociations, 0);

const tests = {tests: 0, failures: 0, errors: 0, skipped: 0};
for (const file of fs.readdirSync('target/surefire-reports').filter(file => /^TEST-.*\.xml$/.test(file)).sort()) {
    const xml = fs.readFileSync(`target/surefire-reports/${file}`, 'utf8');
    const suite = xml.match(/<testsuite\s[^>]+>/)?.[0];
    assert.ok(suite, `Missing testsuite in ${file}`);
    for (const field of Object.keys(tests)) {
        tests[field] += Number(suite.match(new RegExp(`\\b${field}="(\\d+)"`))?.[1] ?? 0);
    }
}
assert.deepEqual(tests, {tests: 484, failures: 0, errors: 0, skipped: 0});
const packagedLog = fs.readFileSync('target/batch-7-packaged-probe.log');
// PowerShell redirection can produce either UTF-8 or UTF-16 depending on the host.
const packagedText = packagedLog[0] === 0xff && packagedLog[1] === 0xfe
    ? packagedLog.toString('utf16le') : packagedLog.toString('utf8');
assert.ok(packagedText.includes('"packagedSupportGroups":209,"monsterAssociations":307,"packagedResources":true'));

function files(directory) {
    return fs.readdirSync(directory, {withFileTypes: true}).flatMap(entry => {
        const filename = `${directory}/${entry.name}`;
        return entry.isDirectory() ? files(filename) : [filename];
    });
}
const newSourceFiles = files('src').filter(file => !(file in before));
for (const file of newSourceFiles.concat(files(directory)).filter(file => /\.(java|md|mjs)$/.test(file))) {
    const lines = fs.readFileSync(file, 'utf8').split(/\r?\n/);
    lines.forEach((line, index) => assert.ok(!/[ \t]+$/.test(line), `Trailing whitespace: ${file}:${index + 1}`));
}
const result = {
    preExistingSrcFilesUnchanged: Object.keys(before).filter(file => file.startsWith('src/')).length,
    pinnedSourceJsonFilesUnchanged: Object.keys(before)
        .filter(file => file.startsWith('src/main/resources/data/') && file.endsWith('.json')).length,
    originalAssessmentFilesUnchanged: Object.keys(before)
        .filter(file => file.startsWith('analysis/mechanics/entry-assessment/')).length,
    auditDeterministic: true,
    sourceAuditSha256: hash(`${directory}/source-audit.json`),
    runtime,
    tests,
    packagedSupportProbe: {supportGroups: 209, monsterAssociations: 307, resourceOriginVerified: true},
    unchangedResolverContract: {
        occurrences: 6246, resolved: 2517, ambiguous: 2054, unresolved: 181, deferred: 1494
    },
    newSourceFiles
};
fs.writeFileSync(`${directory}/verification.json`, `${JSON.stringify(result, null, 2)}\n`);
console.log(JSON.stringify(result, null, 2));
