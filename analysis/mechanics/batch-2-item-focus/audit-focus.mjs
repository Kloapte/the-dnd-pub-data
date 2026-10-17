import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import { readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const directory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(directory, '../../..');
const files = [];
const documents = {};
for (const file of ['items.json', 'items-base.json']) {
    const bytes = await readFile(path.join(repositoryRoot, 'src/main/resources/data', file));
    files.push({ file, sha256: createHash('sha256').update(bytes).digest('hex') });
    documents[file] = JSON.parse(bytes.toString('utf8').replace(/^\uFEFF/, ''));
}

function summarize(records) {
    const result = { records: records.length, absent: 0, arrays: 0, emptyArrays: 0,
        trueValues: 0, falseValues: 0, explicitNulls: 0 };
    for (const record of records) {
        if (!Object.hasOwn(record, 'focus')) {
            result.absent++;
        } else if (record.focus === null) {
            result.explicitNulls++;
        } else if (Array.isArray(record.focus)) {
            assert.ok(record.focus.every(label => typeof label === 'string'));
            result.arrays++;
            if (record.focus.length === 0) {
                result.emptyArrays++;
            }
        } else {
            assert.equal(typeof record.focus, 'boolean');
            result[record.focus ? 'trueValues' : 'falseValues']++;
        }
    }
    return result;
}

const items = documents['items.json'].item;
const groups = documents['items.json'].itemGroup;
const baseItems = documents['items-base.json'].baseitem;
const identity = record => `${record.name.toLowerCase()}|${record.source.toLowerCase()}`;
const byIdentity = new Map(items.map((record, index) => [identity(record), { record, index }]));
assert.equal(byIdentity.size, items.length);

function effectiveFocus(record, chain = []) {
    assert.ok(!chain.includes(identity(record)), 'Cyclic Item copy chain');
    if (record.focus != null) {
        return { focus: record.focus, origin: identity(record), copyChain: chain };
    }
    if (record._copy == null) {
        return null;
    }
    const parent = byIdentity.get(identity(record._copy));
    assert.ok(parent, 'Missing authored Item copy parent');
    return effectiveFocus(parent.record, [...chain, identity(record)]);
}

const authored = items.flatMap((record, index) => record.focus == null ? [] : [{
    name: record.name, source: record.source, locator: `$.item[${index}].focus`, focus: record.focus,
}]);
const effective = items.flatMap((record, index) => {
    const value = effectiveFocus(record);
    return value === null ? [] : [{ name: record.name, source: record.source,
        locator: `$.item[${index}]`, inherited: record.focus == null, ...value }];
});
const auxiliary = groups.flatMap((record, index) => record.focus == null ? [] : [{
    name: record.name, source: record.source, locator: `$.itemGroup[${index}].focus`, focus: record.focus,
}]);
const sourceDistribution = {};
for (const record of authored) {
    sourceDistribution[record.source] = (sourceDistribution[record.source] ?? 0) + 1;
}

let versionDeclarations = 0;
function countVersions(value) {
    if (Array.isArray(value)) {
        value.forEach(countVersions);
    } else if (value !== null && typeof value === 'object') {
        if (Object.hasOwn(value, '_versions')) {
            versionDeclarations++;
        }
        Object.values(value).forEach(countVersions);
    }
}
Object.values(documents).forEach(countVersions);

assert.equal(summarize(items).arrays, 52);
assert.equal(summarize(items).trueValues, 2);
assert.equal(summarize(baseItems).absent, 230);
assert.equal(summarize(groups).arrays, 7);
assert.equal(effective.filter(record => Array.isArray(record.focus)).length, 55);
assert.equal(effective.filter(record => record.inherited).length, 3);
assert.equal(versionDeclarations, 0);

const evidence = {
    files,
    raw: { item: summarize(items), baseitem: summarize(baseItems), itemGroup: summarize(groups) },
    sourceDistribution,
    copyRoots: items.filter(record => record._copy != null).length,
    versionDeclarations,
    effectiveDefinitions: items.length + baseItems.length,
    effectiveFocusDefinitions: effective.length,
    effectiveAbsentFocusDefinitions: items.length + baseItems.length - effective.length,
    authored,
    effective,
    auxiliary,
};
await writeFile(path.join(directory, 'focus-evidence.json'), JSON.stringify(evidence, null, 2) + '\n');
console.log(JSON.stringify({ raw: evidence.raw, sourceDistribution, copyRoots: evidence.copyRoots,
    effectiveFocusDefinitions: effective.length, inherited: effective.filter(record => record.inherited) }, null, 2));
