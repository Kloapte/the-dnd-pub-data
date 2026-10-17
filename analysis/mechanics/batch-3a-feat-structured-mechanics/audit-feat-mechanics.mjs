import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import { readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const directory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(directory, '../../..');
const bytes = await readFile(path.join(repositoryRoot, 'src/main/resources/data/feats.json'));
const roots = JSON.parse(bytes.toString('utf8').replace(/^\uFEFF/, '')).feat;
const expected = {
    skillProficiencies: 12, expertise: 7, languageProficiencies: 7, toolProficiencies: 10,
    weaponProficiencies: 4, armorProficiencies: 6, savingThrowProficiencies: 2,
    skillToolLanguageProficiencies: 2, conditionImmune: 2, immune: 2, resist: 11,
    senses: 4, bonusSenses: 1, optionalfeatureProgression: 4,
};

const jsonType = value => value === null ? 'null' : Array.isArray(value) ? 'array' : typeof value;
function inventory(value, locator, paths) {
    const types = paths[locator] ??= {};
    const type = jsonType(value);
    types[type] = (types[type] ?? 0) + 1;
    if (Array.isArray(value)) {
        value.forEach(child => inventory(child, `${locator}[]`, paths));
    } else if (type === 'object') {
        for (const [key, child] of Object.entries(value)) {
            inventory(child, `${locator}.${key}`, paths);
        }
    }
}

const families = {};
const affectedRoots = [];
const versions = [];
let copyDeclarations = 0;
function countCopies(value) {
    if (Array.isArray(value)) {
        value.forEach(countCopies);
    } else if (value !== null && typeof value === 'object') {
        if (Object.hasOwn(value, '_copy')) {
            copyDeclarations++;
        }
        Object.values(value).forEach(countCopies);
    }
}
countCopies(roots);
for (const [field, count] of Object.entries(expected)) {
    const occurrences = [];
    const paths = {};
    for (const [index, root] of roots.entries()) {
        if (Object.hasOwn(root, field)) {
            const value = root[field];
            occurrences.push({ name: root.name, source: root.source,
                locator: `$.feat[${index}].${field}`, value });
            inventory(value, field, paths);
        }
    }
    assert.equal(occurrences.length, count, field);
    families[field] = { count, paths, occurrences };
}
for (const [index, root] of roots.entries()) {
    const fields = Object.keys(expected).filter(field => Object.hasOwn(root, field));
    if (fields.length > 0) {
        affectedRoots.push({ name: root.name, source: root.source, locator: `$.feat[${index}]`, fields });
    }
    for (const [versionIndex, version] of (root._versions ?? []).entries()) {
        const overrides = Object.keys(expected).filter(field => Object.hasOwn(version, field));
        versions.push({ name: version.name, source: version.source, parent: root.name,
            parentSource: root.source, locator: `$.feat[${index}]._versions[${versionIndex}]`,
            inheritedMechanicalFields: fields, overrides });
    }
}
const authoredOccurrences = Object.values(families).reduce((sum, family) => sum + family.count, 0);
assert.equal(roots.length, 305);
assert.equal(affectedRoots.length, 59);
assert.equal(authoredOccurrences, 74);
assert.equal(versions.length, 9);
assert.ok(versions.every(version => version.inheritedMechanicalFields.length === 0 && version.overrides.length === 0));
assert.equal(copyDeclarations, 0);

const evidence = {
    file: 'src/main/resources/data/feats.json', sha256: createHash('sha256').update(bytes).digest('hex'),
    roots: roots.length, authoredAffectedRoots: affectedRoots.length, authoredOccurrences,
    effectiveDefinitions: roots.length + versions.length,
    effectiveAffectedDefinitions: affectedRoots.length, effectiveOccurrences: authoredOccurrences,
    copyDeclarations, families, affectedRoots, versions,
};
await writeFile(path.join(directory, 'field-evidence.json'), JSON.stringify(evidence, null, 2) + '\n');
console.log(JSON.stringify({ roots: roots.length, affectedRoots: affectedRoots.length, authoredOccurrences,
    fields: expected, effectiveDefinitions: evidence.effectiveDefinitions,
    effectiveAffectedDefinitions: evidence.effectiveAffectedDefinitions, versions: versions.length, copyDeclarations }, null, 2));
