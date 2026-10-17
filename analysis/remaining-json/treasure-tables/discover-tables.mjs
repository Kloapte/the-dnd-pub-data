import fs from 'node:fs';
import crypto from 'node:crypto';

const sourcePath = 'src/main/resources/data/loot.json';
const sourceText = fs.readFileSync(sourcePath, 'utf8');
const source = JSON.parse(sourceText);
const stats = new Map();
const collections = {};
const markupCounts = new Map();
const sampleStrings = new Map();
const emptyArrays = new Map();
const rangeGroups = [];

function jsonType(value) {
    if (value === null) return 'null';
    if (Array.isArray(value)) return 'array';
    return typeof value;
}

function observe(path, value) {
    const entry = stats.get(path) ?? { types: {}, count: 0, strings: new Set(), keys: new Set() };
    entry.types[jsonType(value)] = (entry.types[jsonType(value)] ?? 0) + 1;
    entry.count += 1;
    if (typeof value === 'string') {
        if (entry.strings.size < 100) entry.strings.add(value);
        if (sampleStrings.size < 300) sampleStrings.set(`${path}\t${value}`, true);
        for (const match of value.matchAll(/\{@([a-zA-Z][\w]*)\s+([^}]*)}/g)) {
            markupCounts.set(match[1], (markupCounts.get(match[1]) ?? 0) + 1);
        }
    }
    if (value && typeof value === 'object' && !Array.isArray(value)) {
        Object.keys(value).forEach(key => entry.keys.add(key));
    }
    stats.set(path, entry);
}

function visit(path, value) {
    observe(path, value);
    if (Array.isArray(value)) {
        if (value.length === 0) emptyArrays.set(path, (emptyArrays.get(path) ?? 0) + 1);
        if (value.length > 0 && value.every(member => member && typeof member === 'object'
                && !Array.isArray(member) && typeof member.min === 'number')) {
            let gaps = 0;
            let overlaps = 0;
            let outOfOrder = 0;
            for (let index = 1; index < value.length; index++) {
                const previous = value[index - 1];
                const current = value[index];
                if (current.min < previous.min) outOfOrder += 1;
                if (typeof previous.max === 'number') {
                    if (current.min <= previous.max) overlaps += 1;
                    if (current.min > previous.max + 1) gaps += 1;
                }
            }
            rangeGroups.push({ path, rowCount: value.length, gaps, overlaps, outOfOrder });
        }
        for (const member of value) visit(`${path}[]`, member);
    } else if (value && typeof value === 'object') {
        for (const [key, child] of Object.entries(value)) visit(`${path}.${key}`, child);
    }
}

for (const [collection, records] of Object.entries(source)) {
    const names = new Map();
    const sources = new Map();
    let namedCount = 0;
    let pageCount = 0;
    let tableCount = 0;
    let rowCount = 0;
    let itemUidCount = 0;
    const tags = {};
    const duplicateNames = [];
    for (const record of records) {
        if (record.name) {
            namedCount += 1;
            const key = `${record.source ?? '<no-source>'}\t${record.name}`;
            names.set(key, (names.get(key) ?? 0) + 1);
        }
        if (record.page !== undefined) pageCount += 1;
        if (record.source) sources.set(record.source, (sources.get(record.source) ?? 0) + 1);
        tableCount += Array.isArray(record.table) ? 1 : 0;
        rowCount += Array.isArray(record.table) ? record.table.length : 0;
        if (Array.isArray(record.table)) {
            for (const [index, row] of record.table.entries()) {
                if (Array.isArray(row.table)) {
                    tableCount += 1;
                    rowCount += row.table.length;
                    for (const nestedRow of row.table) {
                        if (typeof nestedRow.item === 'string') {
                            for (const match of nestedRow.item.matchAll(/\{@([a-zA-Z][\w]*)\s+([^}]*)}/g)) {
                                tags[match[1]] = (tags[match[1]] ?? 0) + 1;
                            }
                        }
                    }
                }
                const itemText = typeof row.item === 'string' ? row.item : '';
                const resultTexts = typeof row === 'string' ? row : '';
                for (const text of [itemText, resultTexts]) {
                    for (const match of text.matchAll(/\{@([a-zA-Z][\w]*)\s+([^}]*)}/g)) {
                        tags[match[1]] = (tags[match[1]] ?? 0) + 1;
                    }
                }
                if (Array.isArray(row.choose?.fromItems)) {
                    itemUidCount += row.choose.fromItems.length;
                }
            }
        }
        if (collection === 'dragon') {
            for (const group of ['gems', 'artObjects']) {
                tableCount += record[group]?.typeTable ? 1 : 0;
                rowCount += record[group]?.typeTable?.length ?? 0;
            }
            for (const group of record.magicItems ?? []) {
                tableCount += group.typeTable ? 1 : 0;
                rowCount += group.typeTable?.length ?? 0;
            }
        }
        if (collection === 'dragonMundaneItems') {
            tableCount = records.length > 0 ? 1 : 0;
            rowCount = records.length;
        }
    }
    visit(collection, records);
    for (const [key, count] of names) {
        if (count > 1) duplicateNames.push({ identity: key, count });
    }
    collections[collection] = {
        count: records.length,
        namedCount,
        pageCount,
        sourceDistribution: Object.fromEntries([...sources].sort()),
        duplicateNameSourcePairs: duplicateNames,
        observedTopLevelFields: [...new Set(records.flatMap(record => Object.keys(record)))].sort(),
        tableCount,
        rowCount,
        explicitItemChoices: itemUidCount,
        inlineMarkupTagCounts: tags,
        examples: records.slice(0, 3).map(record => ({
            name: record.name,
            source: record.source,
            page: record.page,
            keys: Object.keys(record),
            roll: record.dice ?? record.roll ?? record.cr ?? record.level,
            tableCount: Array.isArray(record.table) ? record.table.length : undefined,
            firstRow: record.table?.[0]?.table?.[0],
        })),
    };
}

const fieldPaths = [...stats.entries()].sort(([left], [right]) => left.localeCompare(right)).map(([path, entry]) => ({
    path,
    count: entry.count,
    types: entry.types,
    dynamicPropertyNames: [...entry.keys].sort(),
    distinctStringSamples: [...entry.strings].slice(0, 30),
}));
const chooseShapes = {};
for (const row of source.magicItems.flatMap(definition => definition.table)) {
    if (row.choose) {
        const shape = Object.keys(row.choose).sort().join('+');
        chooseShapes[shape] = (chooseShapes[shape] ?? 0) + 1;
    }
}
const nestedMagicRows = source.magicItems.flatMap(definition => definition.table)
    .flatMap(row => row.table ?? []);
const reprints = [...source.gems, ...source.artObjects].flatMap(table =>
    (table.reprintedAs ?? []).map(target => ({
        collection: source.gems.includes(table) ? 'gems' : 'artObjects',
        name: table.name,
        source: table.source,
        target,
    })));
const audit = {
    sourceFile: sourcePath,
    sha256: crypto.createHash('sha256').update(sourceText).digest('hex'),
    rootCollections: Object.keys(source),
    totalDefinitions: Object.values(collections).reduce((sum, collection) => sum + collection.count, 0),
    collections,
    fieldPathCount: fieldPaths.length,
    fieldPaths,
    emptyArrays: Object.fromEntries([...emptyArrays].sort()),
    rangeGroups,
    chooseShapes,
    nestedMagicRowsMissingMax: nestedMagicRows.filter(row => row.max === undefined).length,
    reprints,
    inlineMarkupTagCounts: Object.fromEntries([...markupCounts].sort()),
    stringExamples: [...sampleStrings.keys()].slice(0, 300),
};
fs.writeFileSync('analysis/remaining-json/treasure-tables/source-audit.json', `${JSON.stringify(audit, null, 2)}\n`);
console.log(JSON.stringify({
    sha256: audit.sha256,
    totalDefinitions: audit.totalDefinitions,
    fieldPathCount: audit.fieldPathCount,
    collections: Object.fromEntries(Object.entries(collections).map(([name, value]) => [name, {
        count: value.count,
        sourceDistribution: value.sourceDistribution,
        namedCount: value.namedCount,
        pageCount: value.pageCount,
        duplicateNameSourcePairs: value.duplicateNameSourcePairs,
        tableCount: value.tableCount,
        rowCount: value.rowCount,
        examples: value.examples,
    }])),
    inlineMarkupTagCounts: audit.inlineMarkupTagCounts,
    collectionSpecificTags: Object.fromEntries(Object.entries(collections).map(([name, value]) =>
        [name, { tags: value.inlineMarkupTagCounts, explicitItemChoices: value.explicitItemChoices }])),
}, null, 2));

const collectionLines = Object.entries(collections).map(([name, value]) =>
    `| \`${name}\` | ${value.count} | ${Object.entries(value.sourceDistribution)
        .map(([sourceCode, count]) => `${sourceCode} ${count}`).join(', ') || 'none authored'} | `
        + `${value.namedCount} | ${value.pageCount} | ${value.tableCount} | ${value.rowCount} |`);
const pathLines = fieldPaths.map(field => `| \`${field.path}\` | ${field.count} | `
    + `${Object.entries(field.types).map(([type, count]) => `${type} ${count}`).join(', ')} | `
    + `${field.dynamicPropertyNames.join(', ') || '—'} |`);
const fieldCoverage = `# Treasure and Loot Tables Field Coverage\n\n`
    + `Source: \`${sourcePath}\`; SHA-256: \`${audit.sha256}\`. Recursive field-path count: `
    + `${fieldPaths.length}. The exact path and JSON-type occurrence inventory is generated by `
    + `\`discover-tables.mjs\` and retained in \`source-audit.json\`. Counts below are observed `
    + `value occurrences, not definition counts.\n\n`
    + `## Root collections\n\n| Collection | Array records | Primary source distribution | Named | With page | Tables | Rows |\n`
    + `|---|---:|---|---:|---:|---:|---:|\n${collectionLines.join('\n')}\n\n`
    + `\`dragonMundaneItems\` is an unlabelled shared result-row array; its source cells contain `
    + `no name, source, or page. Its 25 rows are table data, not named definitions.\n\n`
    + `## Complete nested paths\n\n| Path | Occurrences | Observed JSON types | Dynamic property names |\n`
    + `|---|---:|---|---|\n${pathLines.join('\n')}\n\n`
    + `## Optional and empty variants\n\nNo explicit null values or empty arrays occur. Optional fields are `
    + `absent from records that do not author them; path occurrence counts above record present `
    + `values. Six nested magic-item subtable rows omit \`max\`; their missing bound is preserved. `
    + `Choose objects occur in shapes: ${Object.entries(chooseShapes).map(([shape, count]) => `\`${shape}\` ${count}`).join(', ')}. `
    + `The only observed empty-array paths, if any, are recorded in \`source-audit.json\`.\n\n`
    + `## Roll range audit\n\nThe authored range arrays, in source order, and their gap/overlap/order counts are `
    + `recorded under \`rangeGroups\` in \`source-audit.json\`. The importer preserves all observed `
    + `bounds and order; it does not repair discontinuities or infer omitted maxima.\n`;
fs.writeFileSync('analysis/remaining-json/treasure-tables/FIELD-COVERAGE.md', fieldCoverage);
