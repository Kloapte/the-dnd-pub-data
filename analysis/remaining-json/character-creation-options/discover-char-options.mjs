import { readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const directory = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(directory, '../../..');
const dataPath = path.join(root, 'src/main/resources/data/charcreationoptions.json');
const registryPath = path.join(root, 'src/main/resources/data/sources/registry.json');
const outputPath = path.join(directory, 'discovery-evidence.json');
const fieldCoveragePath = path.join(directory, 'FIELD-COVERAGE.md');

const source = JSON.parse((await readFile(dataPath, 'utf8')).replace(/^\uFEFF/, ''));
const registry = JSON.parse((await readFile(registryPath, 'utf8')).replace(/^\uFEFF/, ''));
const definitions = source.charoption;
const registryCodes = new Set(registry.sources.map(item => item.code.toLowerCase()));
const paths = new Map();
const entryTypes = new Map();
const markupTags = new Map();
const markupSourceMentions = new Map();
const primarySources = new Map();
const optionTypes = new Map();

function recordPath(jsonPath, value) {
    const type = value === null ? 'null' : Array.isArray(value) ? 'array' : typeof value;
    let pathRecord = paths.get(jsonPath);
    if (pathRecord === undefined) {
        pathRecord = { occurrences: 0, types: new Map() };
        paths.set(jsonPath, pathRecord);
    }
    pathRecord.occurrences += 1;
    pathRecord.types.set(type, (pathRecord.types.get(type) ?? 0) + 1);
}

function walk(value, jsonPath) {
    recordPath(jsonPath, value);
    if (Array.isArray(value)) {
        value.forEach(item => walk(item, `${jsonPath}[]`));
        return;
    }
    if (value !== null && typeof value === 'object') {
        if (typeof value.type === 'string' && jsonPath.includes('.entries')) {
            entryTypes.set(value.type, (entryTypes.get(value.type) ?? 0) + 1);
        }
        for (const [key, child] of Object.entries(value)) {
            walk(child, `${jsonPath}.${key}`);
        }
        return;
    }
    if (typeof value === 'string') {
        for (const match of value.matchAll(/\{@([a-zA-Z][\w-]*)\s([^}]+)\}/g)) {
            markupTags.set(match[1], (markupTags.get(match[1]) ?? 0) + 1);
            if (match[1] === 'book' || match[1] === 'deck') {
                const fields = match[2].split('|');
                if (fields.length > 1) {
                    const code = fields[1].trim();
                    markupSourceMentions.set(code, (markupSourceMentions.get(code) ?? 0) + 1);
                }
            }
        }
    }
}

walk(source, '$');

for (const definition of definitions) {
    primarySources.set(definition.source, (primarySources.get(definition.source) ?? 0) + 1);
    for (const type of definition.optionType) {
        optionTypes.set(type, (optionTypes.get(type) ?? 0) + 1);
    }
}

const authoredReferences = definitions.flatMap(definition => [
    ...(definition.otherSources ?? []).map(reference => ({
        owner: definition.name,
        field: 'otherSources',
        targetKind: 'SOURCE',
        token: reference.source,
        source: reference.source
    })),
    ...(definition.reprintedAs ?? []).map(reference => {
        const [name, referencedSource] = reference.uid.split('|');
        return {
            owner: definition.name,
            field: 'reprintedAs',
            targetKind: reference.tag,
            token: reference.uid,
            name,
            source: referencedSource
        };
    }),
    ...(definition.prerequisite ?? []).flatMap((prerequisite, prerequisiteIndex) =>
        (prerequisite.race ?? []).map((race, raceIndex) => ({
            owner: definition.name,
            field: `prerequisite[${prerequisiteIndex}].race[${raceIndex}]`,
            targetKind: 'RACE',
            token: race.name,
            name: race.name,
            source: null
        })))
]);

const identityKey = definition => [
    definition.source.toLowerCase(),
    [...definition.optionType].sort().join('+').toLowerCase(),
    definition.name.normalize('NFKD').replace(/\p{M}/gu, '').toLowerCase()
].join('|');
const identities = definitions.map(identityKey);
const identityCollisions = identities.filter((identity, index) => identities.indexOf(identity) !== index);
const referencedSources = [...new Set(authoredReferences
    .filter(reference => reference.targetKind === 'SOURCE' || reference.source !== null)
    .map(reference => reference.source))].sort();

const evidence = {
    snapshot: '5eTools v2.37.0',
    sourceFile: 'src/main/resources/data/charcreationoptions.json',
    rootCollection: 'charoption',
    rootCount: definitions.length,
    uniqueFieldPathCount: paths.size,
    fieldPaths: [...paths.entries()].sort(([left], [right]) => left.localeCompare(right)).map(([fieldPath, record]) => ({
        path: fieldPath,
        occurrences: record.occurrences,
        types: [...record.types.entries()].sort(([left], [right]) => left.localeCompare(right))
            .map(([type, count]) => ({ type, count }))
    })),
    primarySourceCounts: Object.fromEntries([...primarySources.entries()].sort(([left], [right]) => left.localeCompare(right))),
    optionTypeCounts: Object.fromEntries([...optionTypes.entries()].sort(([left], [right]) => left.localeCompare(right))),
    entryObjectTypeCounts: Object.fromEntries([...entryTypes.entries()].sort(([left], [right]) => left.localeCompare(right))),
    inlineMarkupTagCounts: Object.fromEntries([...markupTags.entries()].sort(([left], [right]) => left.localeCompare(right))),
    inlineMarkupSourceMentions: Object.fromEntries(
        [...markupSourceMentions.entries()].sort(([left], [right]) => left.localeCompare(right))),
    transformationDeclarationCounts: Object.fromEntries(
        ['_copy', '_versions', '_abstract', '_implementations', '_variables']
            .map(key => [key, definitions.filter(definition => key in definition).length])),
    identity: {
        scheme: 'source + sorted optionType codes + name',
        distinctIdentityCount: new Set(identities).size,
        collisionKeys: [...new Set(identityCollisions)].sort()
    },
    references: {
        occurrenceCount: authoredReferences.length,
        occurrences: authoredReferences,
        distinctReferencedSources: referencedSources,
        sourcesMissingFromRegistry: referencedSources.filter(code => !registryCodes.has(code.toLowerCase()))
    },
    allPrimarySourcesRegistered: [...primarySources.keys()].every(code => registryCodes.has(code.toLowerCase())),
    markupSourcesMissingFromRegistry: [...markupSourceMentions.keys()]
        .filter(code => !registryCodes.has(code.toLowerCase()))
};

await writeFile(outputPath, `${JSON.stringify(evidence, null, 2)}\n`);
const fieldRows = evidence.fieldPaths.map(field => {
    const types = field.types.map(value => `${value.type}: ${value.count}`).join('<br>');
    return `| \`${field.path}\` | ${field.occurrences} | ${types} |`;
}).join('\n');
const fieldCoverage = `# Character Creation Options field coverage

Generated by \`node analysis/remaining-json/character-creation-options/discover-char-options.mjs\` from the pinned v2.37.0 source. This complete path/type table includes structural document and collection nodes as well as all nested property paths. No path is omitted.

The corpus has **${evidence.rootCount}** root records and **${evidence.uniqueFieldPathCount}** unique recursive paths. Root properties map to \`CharacterCreationOptionDte\`; descriptive \`entries\` recursively reuse the strict shared \`EntryDte\` hierarchy; prerequisite, secondary source, and reprint shapes have dedicated typed DTEs. \`hasFluffImages\` is retained in importer provenance and excluded from the public API.

| JSON path | Occurrences | Observed JSON types and counts |
|---|---:|---|
${fieldRows}

## Shape notes

- \`optionType\` is an array on all roots and contains exactly one string per record. The observed codes are \`CS\`, \`DG\`, \`RF:B\`, and \`SG\`.
- \`entries[]\` is polymorphic: root entries are 64 strings and 38 objects. Across recursive entry objects, the observed typed forms are \`entries\`, \`list\`, and \`table\`; table rows are arrays of strings in this corpus.
- Prerequisites occur on two roots and contain race-name arrays plus prose notes. Race names are unqualified and remain names, not resolved race IDs.
- \`otherSources\` and \`reprintedAs\` each occur on two roots. The reprint UID format is \`Name|Source\`; both targets have tag \`background\`.
- Source-bearing \`book\` and \`deck\` tags use registry identities; their codes and occurrence counts are reported in the machine evidence. Tags remain embedded descriptions and are neither resolved into grants nor exposed publicly.
- Optional root fields are \`prerequisite\` (2), \`otherSources\` (2), \`reprintedAs\` (2), and \`hasFluffImages\` (5). All other root properties occur on all 44 roots. Null counts, where any occur, are explicit in the table.
`;
await writeFile(fieldCoveragePath, fieldCoverage);
console.log(JSON.stringify({
    rootCount: evidence.rootCount,
    uniqueFieldPathCount: evidence.uniqueFieldPathCount,
    identityCollisions: evidence.identity.identityCollisionCount ?? evidence.identity.collisionKeys.length,
    referenceOccurrences: evidence.references.occurrenceCount,
    outputPath: path.relative(root, outputPath),
    fieldCoveragePath: path.relative(root, fieldCoveragePath)
}, null, 2));
