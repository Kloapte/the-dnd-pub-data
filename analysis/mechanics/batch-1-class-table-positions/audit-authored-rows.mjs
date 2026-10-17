import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import { readdir, readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const directory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(directory, '../../..');
const sourceDirectory = path.join(repositoryRoot, 'src/main/resources/data/classes');
const files = (await readdir(sourceDirectory)).filter(file => /^class-.*\.json$/.test(file)).sort();
const owners = [];
const fingerprints = [];
for (const file of files) {
    const bytes = await readFile(path.join(sourceDirectory, file));
    fingerprints.push({ file, sha256: createHash('sha256').update(bytes).digest('hex') });
    const document = JSON.parse(bytes.toString('utf8').replace(/^\uFEFF/, ''));
    for (const [root, field] of [['class', 'classTableGroups'], ['subclass', 'subclassTableGroups']]) {
        for (const [ownerIndex, owner] of (document[root] ?? []).entries()) {
            const affectedRows = [];
            for (const [tableIndex, table] of (owner[field] ?? []).entries()) {
                for (const [rowIndex, row] of (table.rows ?? []).entries()) {
                    const firstWithheld = row.findIndex(cell => typeof cell === 'string');
                    const lastRetained = row.findLastIndex(cell => typeof cell !== 'string');
                    if (firstWithheld < 0 || firstWithheld >= lastRetained) {
                        continue;
                    }
                    affectedRows.push({
                        locator: `$.${root}[${ownerIndex}].${field}[${tableIndex}].rows[${rowIndex}]`,
                        tableIndex,
                        rowIndex,
                        sourceWidth: row.length,
                        retainedColumnIndexes: row.flatMap((cell, index) => typeof cell === 'string' ? [] : [index]),
                    });
                }
            }
            if (affectedRows.length > 0) {
                owners.push({ file, root, ownerIndex, name: owner.name, source: owner.source,
                    className: owner.className, classSource: owner.classSource,
                    count: affectedRows.length, affectedRows });
            }
        }
    }
}
const affectedRowCount = owners.reduce((sum, owner) => sum + owner.count, 0);
assert.equal(files.length, 15);
assert.equal(owners.length, 6);
assert.equal(affectedRowCount, 97);
await writeFile(path.join(directory, 'authored-rows.json'), JSON.stringify({
    countSemantics: 'Authored rows with a retained cell following a withheld string; no copy expansion.',
    affectedRowCount, owners, files: fingerprints,
}, null, 2) + '\n');
console.log(JSON.stringify(owners.map(({ affectedRows, ...owner }) => owner), null, 2));
