import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';

const sourcePath = 'src/main/resources/data/cultsboons.json';
const source = JSON.parse(fs.readFileSync(sourcePath, 'utf8').replace(/^\uFEFF/, ''));
const familyNames = ['cult', 'boon'];
const report = {
    sourcePath,
    sha256: crypto.createHash('sha256').update(fs.readFileSync(sourcePath)).digest('hex'),
    rootCollections: {},
    fieldPaths: {},
    identities: {},
    sourceOccurrences: {},
    reprintReferences: [],
    markupTags: {},
    markupSourceCodes: {},
    referenceShapes: {},
    transformationFields: {},
    explicitNullOccurrences: 0,
    emptyValueOccurrences: {}
};

recordPath('$', source);
for (const family of familyNames) {
    recordPath(`$.${family}`, source[family]);
}

function jsonType(value) {
    if (value === null) return 'null';
    if (Array.isArray(value)) return 'array';
    return typeof value;
}

function recordPath(pathName, value) {
    const type = jsonType(value);
    const existing = report.fieldPaths[pathName] ?? { occurrences: 0, types: {} };
    existing.occurrences += 1;
    existing.types[type] = (existing.types[type] ?? 0) + 1;
    report.fieldPaths[pathName] = existing;
}

function walk(value, pathName, rootName) {
    recordPath(pathName, value);
    if (value === null) {
        report.explicitNullOccurrences += 1;
    }
    if (value === '' || Array.isArray(value) && value.length === 0
            || value && typeof value === 'object' && !Array.isArray(value) && Object.keys(value).length === 0) {
        report.emptyValueOccurrences[pathName] = (report.emptyValueOccurrences[pathName] ?? 0) + 1;
    }
    if (typeof value === 'string') {
        const matcher = /\{@([a-zA-Z][\w-]*)\s+([^}]*)}/g;
        for (const match of value.matchAll(matcher)) {
            const item = report.markupTags[match[1]] ?? {
                occurrences: 0,
                roots: 0,
                samples: [],
                argumentPartCounts: {}
            };
            item.occurrences += 1;
            const partCount = match[2].split('|').length;
            item.argumentPartCounts[partCount] = (item.argumentPartCounts[partCount] ?? 0) + 1;
            if (!item.samples.includes(match[2]) && item.samples.length < 5) item.samples.push(match[2]);
            report.markupTags[match[1]] = item;
            const args = match[2].split('|');
            let authoredSource = null;
            if (['spell', 'item', 'creature'].includes(match[1]) && args.length > 1 && args[1].trim()) {
                authoredSource = args[1].trim();
            } else if (match[1] === 'deity' && args.length > 2 && args.at(-1).trim()) {
                authoredSource = args.at(-1).trim();
            } else if (match[1] === 'filter') {
                const sourceFilter = args.find((argument) => argument.trim().startsWith('source='));
                authoredSource = sourceFilter?.trim().slice('source='.length) ?? null;
            }
            if (authoredSource) {
                const sourceRecord = report.markupSourceCodes[authoredSource]
                    ?? { occurrences: 0, tags: {} };
                sourceRecord.occurrences += 1;
                sourceRecord.tags[match[1]] = (sourceRecord.tags[match[1]] ?? 0) + 1;
                report.markupSourceCodes[authoredSource] = sourceRecord;
            }
            if (/(?:\|[A-Z][A-Za-z0-9]{1,12})/.test(match[2])) {
                report.referenceShapes[match[1]] = (report.referenceShapes[match[1]] ?? 0) + 1;
            }
        }
        return;
    }
    if (Array.isArray(value)) {
        value.forEach((item) => walk(item, `${pathName}[]`, rootName));
        return;
    }
    if (value && typeof value === 'object') {
        for (const [key, item] of Object.entries(value)) {
            if (key === 'source' && typeof item === 'string') {
                const sourcesAtPath = report.sourceOccurrences[`${pathName}.${key}`] ?? {};
                sourcesAtPath[item] = (sourcesAtPath[item] ?? 0) + 1;
                report.sourceOccurrences[`${pathName}.${key}`] = sourcesAtPath;
            }
            if (key === 'reprintedAs' && Array.isArray(item)) {
                for (const reference of item) {
                    if (typeof reference === 'string') {
                        report.reprintReferences.push({ family: rootName, uid: reference, shape: 'string' });
                    } else if (reference && typeof reference === 'object') {
                        report.reprintReferences.push({
                            family: rootName,
                            uid: reference.uid,
                            tag: reference.tag,
                            shape: 'tagged-object'
                        });
                    }
                }
            }
            if (['_copy', '_versions', '_abstract', '_implementations', '_variables'].includes(key)) {
                report.transformationFields[key] = (report.transformationFields[key] ?? 0) + 1;
            }
            walk(item, `${pathName}.${key}`, rootName);
        }
    }
}

for (const family of familyNames) {
    const definitions = source[family] ?? [];
    const sourceCounts = {};
    const typeCounts = {};
    const identityCounts = {};
    const nameCounts = {};
    const optionalFields = {};
    for (const definition of definitions) {
        sourceCounts[definition.source] = (sourceCounts[definition.source] ?? 0) + 1;
        typeCounts[definition.type] = (typeCounts[definition.type] ?? 0) + 1;
        const identity = `${definition.source}\u0000${definition.name}`;
        identityCounts[identity] = (identityCounts[identity] ?? 0) + 1;
        const nameKey = definition.name.toLowerCase();
        const nameRecord = nameCounts[nameKey] ?? { count: 0, sources: [] };
        nameRecord.count += 1;
        if (!nameRecord.sources.includes(definition.source)) nameRecord.sources.push(definition.source);
        nameCounts[nameKey] = nameRecord;
        for (const key of Object.keys(definition)) optionalFields[key] = (optionalFields[key] ?? 0) + 1;
        walk(definition, `$.${family}[]`, family);
    }
    report.rootCollections[family] = {
        definitionCount: definitions.length,
        sourceCounts,
        typeCounts,
        rootFieldOccurrences: optionalFields,
        duplicateNameSourceIdentities: Object.entries(identityCounts)
            .filter(([, count]) => count > 1)
            .map(([identity, count]) => ({ identity: identity.replace('\u0000', '|'), count })),
        namesRepeatedAcrossSources: Object.entries(nameCounts)
            .filter(([, value]) => value.count > 1)
            .map(([name, value]) => ({ name, count: value.count, sources: value.sources.sort() }))
    };
}

for (const tag of Object.values(report.markupTags)) delete tag.rootsSeen;

const fieldRows = Object.entries(report.fieldPaths)
    .sort(([left], [right]) => left.localeCompare(right))
    .map(([fieldPath, observation]) => {
        const types = Object.entries(observation.types)
            .map(([type, count]) => `${type}: ${count}`)
            .join(', ');
        return `| \`${fieldPath}\` | ${observation.occurrences} | ${types} |`;
    });
const fieldMarkdown = [
    '# Cults and Boons field coverage',
    '',
    'Generated by `node analysis/remaining-json/cults-and-boons/discover-cults-boons.mjs` from the pinned v2.37.0 source.',
    '',
    `The corpus has **${report.rootCollections.cult.definitionCount} Cults** and **${report.rootCollections.boon.definitionCount} Boons** with **${Object.keys(report.fieldPaths).length} distinct collection-qualified field paths**. Occurrence and JSON type counts include arrays and objects at every recursive level. There are ${report.explicitNullOccurrences} explicit nulls and ${Object.values(report.emptyValueOccurrences).reduce((sum, count) => sum + count, 0)} empty-value occurrences. The separate collection prefixes retain the independent root schemas.`,
    '',
    '| JSON path | Occurrences | Observed JSON types |',
    '|---|---:|---|',
    ...fieldRows,
    ''
].join('\n');

const outputPath = 'analysis/remaining-json/cults-and-boons/discovery-evidence.json';
fs.mkdirSync(path.dirname(outputPath), { recursive: true });
fs.writeFileSync(outputPath, `${JSON.stringify(report, null, 2)}\n`);
fs.writeFileSync('analysis/remaining-json/cults-and-boons/FIELD-COVERAGE.md', fieldMarkdown);
console.log(JSON.stringify({
    roots: Object.fromEntries(familyNames.map((family) => [family, report.rootCollections[family].definitionCount])),
    uniqueFieldPaths: Object.keys(report.fieldPaths).length,
    transformationFields: report.transformationFields,
    outputPath
}, null, 2));
