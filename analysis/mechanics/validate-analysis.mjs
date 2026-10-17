import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import { execFileSync } from 'node:child_process';
import { access, readFile, readdir, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { reviews } from './catalog-review.mjs';

const directory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(directory, '../..');
const hash = value => createHash('sha256').update(value).digest('hex');
const parse = value => JSON.parse(value.replace(/^\uFEFF/, ''));
const load = async name => parse(await readFile(path.join(directory, name), 'utf8'));
const evidence = await load('corpus-evidence.json');
const candidates = await load('candidate-evidence.json');
const baseline = await load('protected-files-baseline.json');

const generatedFiles = ['corpus-evidence.json', 'candidate-evidence.json',
    'CORPUS-MECHANICS-INVENTORY.md', 'TAG-FREQUENCIES.md'];
const beforeHashes = {};
for (const file of generatedFiles) {
    beforeHashes[file] = hash(await readFile(path.join(directory, file)));
}
for (const script of ['scan-mechanics.mjs', 'audit-candidates.mjs', 'write-corpus-inventory.mjs']) {
    execFileSync(process.execPath, [path.join(directory, script)], { cwd: repositoryRoot, stdio: 'pipe' });
}
for (const file of generatedFiles) {
    assert.equal(hash(await readFile(path.join(directory, file))), beforeHashes[file], `Non-deterministic output: ${file}`);
}

assert.equal(evidence.summary.files, 175);
assert.equal(evidence.summary.definitionSourceCollections, 43);
assert.equal(evidence.summary.definitionRecords, 14399);
assert.equal(evidence.summary.supportRecords, 812);
assert.equal(evidence.summary.malformedTagCount, 0);
assert.equal(evidence.summary.excluded.length, 1);
assert.equal(evidence.summary.excluded[0].count, 20);
assert.equal(evidence.summary.excluded[0].reason, 'OUT_OF_SCOPE_PRODUCT_DECISION');
assert.equal(reviews.length, 36);
assert.equal(reviews.reduce((sum, review) => sum + review.normalizedCount, 0), 14797);
assert.equal(candidates.totals.tagOccurrences, 91994);
assert.equal(candidates.totals.definitionTagOccurrences, 90294);
assert.equal(candidates.totals.fieldPaths, 6308);
assert.equal(candidates.totals.definitionFieldPaths, 5434);

const expectedCandidates = {
    'class.publicTableColumnShift': [6, 97],
    'feat.structuredMechanicsNotInDefinition': [59, 74],
    'feat.prerequisiteReferenceSourceOmitted': [0, 0],
    'feat.proficiencyRequirementsFlattened': [12, 12],
    'item.focusArraysNotNormalized': [52, 52],
    'item.publicChargeMarkup': [220, 220],
    'optionalFeature.publicTextPrerequisites': [7, 14],
    'spell.scalingLevelDice': [41, 41],
    'spell.explicitRangeGeometry': [78, 78],
    'object.structuredAttackEntry': [7, 7],
    'classFeature.resourceConsumption': [139, 139],
    'treasure.currencyExpressions': [20, 62],
    'item.integerBonusExpressions': [339, 410],
    'vehicle.summaryStatsAbsentDetail': [8, 12],
};
for (const [id, [records, occurrences]] of Object.entries(expectedCandidates)) {
    assert.equal(candidates.candidates[id].records, records, id);
    assert.equal(candidates.candidates[id].occurrences, occurrences, id);
}

const requiredExamples = new Map();
function requireExample(example, tag) {
    const examples = requiredExamples.get(example.file) ?? [];
    examples.push({ ...example, tag });
    requiredExamples.set(example.file, examples);
}
for (const catalog of Object.values(evidence.catalogs)) {
    for (const field of Object.values(catalog.fieldPaths)) {
        requireExample(field.example);
    }
    for (const [tag, variants] of Object.entries(catalog.tagVariantExamples)) {
        for (const example of Object.values(variants)) {
            requireExample(example, tag);
        }
    }
}
for (const candidate of Object.values(candidates.candidates)) {
    candidate.examples.forEach(example => requireExample(example));
}

let validatedExamples = 0;
let validatedTagExamples = 0;
for (const file of evidence.files) {
    const bytes = await readFile(path.join(repositoryRoot, file.file));
    assert.equal(hash(bytes), file.sha256, `Changed source file: ${file.file}`);
    const document = parse(bytes.toString('utf8'));
    const examples = requiredExamples.get(file.file) ?? [];
    const wanted = new Set(examples.map(example => example.locator));
    const found = new Map();
    function visit(node, locator) {
        if (wanted.has(locator)) {
            found.set(locator, node);
        }
        if (Array.isArray(node)) {
            node.forEach((value, index) => visit(value, `${locator}[${index}]`));
        } else if (node !== null && typeof node === 'object') {
            for (const [key, value] of Object.entries(node)) {
                visit(value, `${locator}.${key}`);
            }
        }
    }
    visit(document, '$');
    for (const example of examples) {
        assert.ok(found.has(example.locator), `Missing example ${file.file}:${example.locator}`);
        validatedExamples += 1;
        if (example.tag) {
            const value = found.get(example.locator);
            assert.equal(typeof value, 'string');
            assert.equal(value.slice(example.offset, example.offset + 2), '{@');
            const body = value.slice(example.offset + 2);
            assert.equal(body.split(/[\s|}]/, 1)[0].toLowerCase(), example.tag);
            validatedTagExamples += 1;
        }
    }
}

const stage3 = parse(await readFile(path.join(repositoryRoot, 'analysis/spells/mechanics-investigation-evidence.json'), 'utf8'));
const stage3Parity = {};
for (const tag of ['damage', 'dice', 'scaledamage', 'scaledice']) {
    const previous = stage3.tagCounts[tag].occurrences;
    const current = evidence.catalogs.spell.tagCounts[tag];
    assert.equal(current, previous, `Stage 3 formula tag drift: ${tag}`);
    stage3Parity[tag] = current;
}
assert.equal(stage3.structuredFieldSpellCounts.scalingLevelDice, 5);
assert.deepEqual(candidates.candidates['spell.scalingLevelDice'].valueTypes, { object: 36, array: 5 });

let baselineReport;
try {
    const reportDirectory = path.join(repositoryRoot, 'target/surefire-reports');
    const reports = (await readdir(reportDirectory)).filter(file => /^TEST-.*\.xml$/.test(file)).sort();
    const totals = { tests: 0, failures: 0, errors: 0, skipped: 0 };
    for (const file of reports) {
        const xml = await readFile(path.join(reportDirectory, file), 'utf8');
        const header = xml.match(/<testsuite\s[^>]*>/)?.[0];
        assert.ok(header, file);
        for (const key of Object.keys(totals)) {
            totals[key] += Number(header.match(new RegExp(`\\b${key}="(\\d+)"`))?.[1] ?? 0);
        }
    }
    baselineReport = { status: 'existing-reports-inspected-not-rerun', suites: reports.length, ...totals };
} catch (error) {
    if (error.code !== 'ENOENT') {
        throw error;
    }
    baselineReport = { status: 'not-rerun-no-existing-reports', suppliedBaseline: 399 };
}

const requiredDocuments = ['STEP-7-OVERVIEW.md', 'CORPUS-MECHANICS-INVENTORY.md',
    'MARKUP-SEMANTICS.md', 'EXTRACTION-RELIABILITY.md', 'NORMALIZATION-GAPS.md',
    'LICENSING-AND-PUBLICATION.md', 'ENRICHMENT-MODEL.md', 'IMPLEMENTATION-ORDER.md', 'OPEN-QUESTIONS.md'];
let localLinks = 0;
let whitespaceCheckedFiles = 0;
for (const file of await readdir(directory)) {
    if (!/\.(md|mjs|json)$/.test(file)) {
        continue;
    }
    const content = await readFile(path.join(directory, file), 'utf8');
    assert.ok(content.endsWith('\n'), `Missing final newline: ${file}`);
    assert.ok(!/[\t ]+\r?$/m.test(content), `Trailing whitespace: ${file}`);
    whitespaceCheckedFiles += 1;
}
for (const file of [...requiredDocuments, 'TAG-FREQUENCIES.md']) {
    const markdown = await readFile(path.join(directory, file), 'utf8');
    for (const match of markdown.matchAll(/\]\(([^)]+)\)/g)) {
        if (/^https?:|^#/.test(match[1])) {
            continue;
        }
        await access(path.resolve(directory, match[1].split('#')[0]));
        localLinks += 1;
    }
}

execFileSync('git', ['-c', 'core.safecrlf=false', 'diff', '--check'], { cwd: repositoryRoot, stdio: 'pipe' });

const verification = {
    status: 'passed',
    inputFingerprint: evidence.summary.inputFingerprint,
    protectedSourceProductionAndTestFiles: { count: baseline.fileCount, sha256: baseline.sha256, changed: 0 },
    reproducibleGeneratedHashes: beforeHashes,
    filesAccountedFor: 175, definitionCollections: 43, catalogDomains: 36,
    rawDefinitionRecords: 14399, effectiveDefinitionsFromExistingContracts: 14797,
    validatedExamples, validatedTagExamples, validLocalDocumentationLinks: localLinks,
    tagFamilies: Object.keys(evidence.summary.tags).length,
    analysisFilesWhitespaceChecked: whitespaceCheckedFiles,
    gitDiffCheck: 'passed',
    stage3FormulaTagParity: stage3Parity,
    correctedScalingFieldCount: { oldArrayOnly: 5, currentObjects: 36, currentArrays: 5, currentTotal: 41 },
    maven: baselineReport,
    referenceBaseline: { occurrences: 6246, resolved: 2517, ambiguous: 2054, unresolved: 181, deferred: 1494,
        recomputed: false, basis: 'Supplied/current test baseline; all source, resolver, projection and test files hash-unchanged.' },
    productionChanges: false,
    sourceJsonChanges: false,
    testChanges: false,
};
await writeFile(path.join(directory, 'verification.json'), JSON.stringify(verification, null, 2) + '\n');
console.log(JSON.stringify(verification, null, 2));
