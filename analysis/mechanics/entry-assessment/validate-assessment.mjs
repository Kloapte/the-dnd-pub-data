import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import { readFile, readdir, writeFile, access } from 'node:fs/promises';
import { execFileSync } from 'node:child_process';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const directory = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(directory, '../../..');
const relative = file => path.relative(root, file).replaceAll('\\', '/');
const digest = bytes => createHash('sha256').update(bytes).digest('hex');
const readJson = async file => JSON.parse(await readFile(file, 'utf8'));

async function files(directory) {
    const result = [];
    for (const child of (await readdir(directory, { withFileTypes: true })).sort((a, b) => a.name.localeCompare(b.name, 'en'))) {
        const file = path.join(directory, child.name);
        if (child.isDirectory()) {
            result.push(...await files(file));
        } else {
            result.push(file);
        }
    }
    return result;
}

const priorFiles = (await files(path.join(root, 'analysis'))).filter(file => !file.startsWith(directory));
const priorHashes = [];
for (const file of priorFiles) {
    priorHashes.push({ file: relative(file), sha256: digest(await readFile(file)) });
}
const priorManifest = path.join(directory, 'prior-analysis-hashes.json');
if (process.argv.includes('--capture-prior-analysis')) {
    await writeFile(priorManifest, JSON.stringify(priorHashes, null, 2) + '\n', { flag: 'wx' });
}
assert.deepEqual(priorHashes, await readJson(priorManifest), 'Prior analysis artifacts changed');

const protectedSnapshot = await readJson(path.join(directory, 'protected-files.json'));
const currentProtected = await files(path.join(root, 'src'));
assert.deepEqual(currentProtected.map(relative), protectedSnapshot.map(value => value.file));
for (const file of protectedSnapshot) {
    assert.equal(digest(await readFile(path.join(root, file.file))), file.sha256, file.file);
}

const generated = ['entry-evidence.json', 'java-model-index.json', 'model-review.json',
    'ENTRY-STRUCTURE-INVENTORY.md', 'MECHANICS-TAXONOMY.md', 'CROSS-CATALOG-MATRIX.md'];
const outputHashes = {};
for (const file of generated) {
    outputHashes[file] = digest(await readFile(path.join(directory, file)));
}
execFileSync(process.execPath, [path.join(directory, 'scan-entries.mjs')], { cwd: root, stdio: 'pipe' });
execFileSync(process.execPath, [path.join(directory, 'build-report.mjs')], { cwd: root, stdio: 'pipe' });
for (const file of generated) {
    assert.equal(digest(await readFile(path.join(directory, file))), outputHashes[file], `Nondeterministic output: ${file}`);
}

const evidence = await readJson(path.join(directory, 'entry-evidence.json'));
const prior = await readJson(path.join(root, 'analysis/mechanics/corpus-evidence.json'));
assert.equal(evidence.summary.files, 175);
assert.equal(evidence.summary.catalogs, 36);
assert.equal(evidence.summary.sourceCollections, 43);
assert.equal(evidence.summary.authoredRecords, 14399);
assert.equal(evidence.summary.effectiveDefinitions, 14797);
assert.equal(evidence.summary.javaVariantTypes, 48);
assert.equal(evidence.summary.variantShapes, 73);
assert.equal(Object.keys(evidence.unresolvedBindings).length, 0);
assert.equal(evidence.concepts.length, 31);
const priorSources = new Map(prior.files.map(file => [file.file, file.sha256]));
for (const file of evidence.manifest) {
    assert.equal(file.sha256, priorSources.get(file.file), file.file);
}
const definitionTags = {};
for (const [name, catalog] of Object.entries(evidence.catalogs)) {
    assert.equal(Object.keys(catalog.concepts).length, 31, name);
    for (const [tag, count] of Object.entries(catalog.tags)) {
        definitionTags[tag] = (definitionTags[tag] ?? 0) + count;
    }
    for (const cell of Object.values(catalog.concepts)) {
        assert.ok(['ABSENT', 'STRUCTURED', 'PARTIAL', 'ENTRY_ONLY', 'MIXED', 'UNCERTAIN'].includes(cell.status));
    }
}
assert.deepEqual(definitionTags, prior.summary.definitionTags);
let supportRecords = 0;
let supportTags = 0;
for (const collection of Object.values(evidence.supportCollections)) {
    if (collection.role !== 'support') {
        continue;
    }
    supportRecords += collection.records;
    supportTags += Object.values(collection.tags).reduce((sum, count) => sum + count, 0);
}
const primaryTags = Object.values(definitionTags).reduce((sum, count) => sum + count, 0);
assert.equal(supportRecords, 812);
assert.equal(primaryTags, 90294);
assert.equal(primaryTags + supportTags, 91994);
assert.equal(evidence.rawEntryShapes.table.occurrences, 973);
assert.equal(evidence.supportCollections.crochetPattern.records, 20);
assert.equal(evidence.supportCollections.crochetPattern.role, 'OUT_OF_SCOPE');

const sourceCache = new Map();
let checkedLocations = 0;
let checkedTokens = 0;
async function checkExample(example) {
    if (!sourceCache.has(example.file)) {
        sourceCache.set(example.file, await readJson(path.join(root, example.file)));
    }
    const document = sourceCache.get(example.file);
    let value = document;
    for (const key of example.locator.slice(2).replaceAll('[', '.').replaceAll(']', '').split('.')) {
        assert.ok(value != null && Object.hasOwn(value, key), `${example.file}:${example.locator} at ${key}`);
        value = value[key];
    }
    if (example.root != null && example.index != null) {
        const owner = document[example.root][example.index];
        assert.equal(owner.name ?? null, example.name);
        assert.equal(owner.source ?? owner.inherits?.source ?? null, example.source);
    }
    if (example.tokenHash) {
        assert.equal(typeof value, 'string');
        assert.equal(digest(value.slice(example.offset, example.offset + example.length)), example.tokenHash);
        checkedTokens += 1;
    }
    checkedLocations += 1;
}
async function visitExamples(value) {
    if (value === null || typeof value !== 'object') {
        return;
    }
    if (value.file?.startsWith('src/main/resources/data/') && typeof value.locator === 'string') {
        await checkExample(value);
    }
    for (const child of Object.values(value)) {
        await visitExamples(child);
    }
}
await visitExamples(evidence);

const required = ['ENTRY-STRUCTURE-INVENTORY.md', 'MECHANICS-TAXONOMY.md', 'CROSS-CATALOG-MATRIX.md',
    'MODEL-GAP-ANALYSIS.md', 'EXTRACTION-RELIABILITY.md', 'RELATIONSHIP-GAPS.md',
    'SHARED-MODEL-CANDIDATES.md', 'PUBLICATION-BOUNDARIES.md', 'IMPLEMENTATION-ROADMAP.md',
    'OPEN-QUESTIONS.md', 'ASSESSMENT-SUMMARY.md'];
let localLinks = 0;
for (const file of required) {
    const text = await readFile(path.join(directory, file), 'utf8');
    assert.ok(text.trim().length > 100, file);
    assert.ok(text.endsWith('\n'), `Missing final newline: ${file}`);
    assert.ok(!/[\t ]+$/m.test(text), `Trailing whitespace: ${file}`);
    for (const match of text.matchAll(/\]\(([^)]+)\)/g)) {
        if (/^https?:/.test(match[1]) || match[1].startsWith('#')) {
            continue;
        }
        await access(path.resolve(directory, match[1].split('#')[0]));
        localLinks += 1;
    }
}
for (const file of await files(directory)) {
    if (!/\.(md|mjs|json)$/.test(file) || path.basename(file) === 'verification.json') {
        continue;
    }
    const text = await readFile(file, 'utf8');
    assert.ok(!/[\t ]+$/m.test(text), `Trailing whitespace: ${relative(file)}`);
}

let tests = 0;
let failures = 0;
let errors = 0;
let skipped = 0;
const reportDirectory = path.join(root, 'target/surefire-reports');
for (const file of (await files(reportDirectory)).filter(file => /TEST-.*\.xml$/.test(file))) {
    const text = await readFile(file, 'utf8');
    const header = /<testsuite\s[^>]+>/.exec(text)?.[0] ?? '';
    const count = field => Number(new RegExp(`\\b${field}="(\\d+)"`).exec(header)?.[1] ?? 0);
    tests += count('tests');
    failures += count('failures');
    errors += count('errors');
    skipped += count('skipped');
}
assert.equal(tests, 443);
assert.equal(failures + errors + skipped, 0);
const verification = {
    scope: 'Analysis only; no fresh Maven/resolver execution',
    files: 175, catalogs: 36, sourceCollections: 43, authoredRecords: 14399,
    effectiveDefinitionContract: 14797, concepts: 31, intersections: 1116,
    javaCarrierTypes: 48, bindingShapes: 73, sourceTagParity: { definition: primaryTags, includingSupport: primaryTags + supportTags },
    protectedSourceProductionTestFiles: protectedSnapshot.length, protectedFilesUnchanged: true,
    priorAnalysisFilesUnchanged: priorFiles.length, deterministicOutputs: outputHashes,
    sourceLocationsValidated: checkedLocations, tokenHashesValidated: checkedTokens, localLinksValidated: localLinks,
    allElevenDocumentsPresent: true, existingSurefireReports: { tests, failures, errors, skipped },
    referenceBaseline: { occurrences: 6246, resolved: 2517, ambiguous: 2054, unresolved: 181, deferred: 1494,
        rerun: false, implementationAndSourceUnchanged: true },
    fullMavenRun: false,
};
await writeFile(path.join(directory, 'verification.json'), JSON.stringify(verification, null, 2) + '\n');
console.log(JSON.stringify(verification, null, 2));
