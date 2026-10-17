import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import { readFile, readdir, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const directory = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(directory, '../../..');
const json = async file => JSON.parse(await readFile(path.join(root, file), 'utf8'));
const digest = bytes => createHash('sha256').update(bytes).digest('hex');
const audit = await json('analysis/mechanics/batch-5-syntax-foundation/source-audit.json');
for (const file of audit.sourceHashes) {
    assert.equal(digest(await readFile(path.join(root, file.file))), file.sha256, file.file);
}
for (const file of audit.assessment) {
    const original = path.join(root, 'analysis/mechanics/entry-assessment', file.file);
    assert.equal(digest(await readFile(original)), file.sha256, file.file);
}
const protectedFiles = await json('analysis/mechanics/entry-assessment/protected-files.json');
for (const file of protectedFiles) {
    assert.equal(digest(await readFile(path.join(root, file.file))), file.sha256, file.file);
}

async function files(directory) {
    const result = [];
    for (const child of await readdir(directory, { withFileTypes: true })) {
        const file = path.join(directory, child.name);
        if (child.isDirectory()) {
            result.push(...await files(file));
        } else {
            result.push(file);
        }
    }
    return result;
}
const protectedPaths = new Set(protectedFiles.map(file => file.file));
const added = [];
for (const file of await files(path.join(root, 'src'))) {
    const relative = path.relative(root, file).replaceAll('\\', '/');
    if (!protectedPaths.has(relative)) {
        assert.ok(relative.startsWith('src/main/java/com/thedndpub/data/mechanics/syntax/')
            || relative.startsWith('src/test/java/com/thedndpub/data/mechanics/syntax/'), relative);
        added.push(relative);
    }
}
for (const file of [...added.map(file => path.join(root, file)), ...await files(directory)]) {
    if (!/\.(java|mjs|md|json)$/.test(file)) {
        continue;
    }
    const content = await readFile(file, 'utf8');
    assert.ok(!/[\t ]+$/m.test(content), `Trailing whitespace: ${file}`);
}
const report = await json('target/batch-5-syntax-corpus.json');
const focused = await json('analysis/mechanics/batch-5-syntax-foundation/focused-corpus-report.json');
assert.deepEqual(report, focused, 'Focused and full-suite corpus output differs');
await writeFile(path.join(directory, 'corpus-verification.json'), JSON.stringify(report, null, 2) + '\n');
const result = {
    sourceJsonHashesUnchanged: audit.sourceHashes.length,
    originalProtectedFilesUnchanged: protectedFiles.length,
    assessmentFilesUnchanged: audit.assessment.length,
    addedJavaAndTests: added.sort(),
    deterministicCorpusReport: true
};
await writeFile(path.join(directory, 'scope-verification.json'), JSON.stringify(result, null, 2) + '\n');
console.log(JSON.stringify(result, null, 2));
