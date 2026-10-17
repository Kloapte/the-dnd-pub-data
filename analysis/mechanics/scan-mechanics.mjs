import { createHash } from 'node:crypto';
import { mkdir, readFile, readdir, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const outputDirectory = path.dirname(fileURLToPath(import.meta.url));
const repositoryRoot = path.resolve(outputDirectory, '../..');
const dataRoot = path.join(repositoryRoot, 'src/main/resources/data');

const definitionRoots = new Set([
    'action', 'adventure', 'background', 'facility', 'book', 'charoption', 'class', 'subclass',
    'classFeature', 'subclassFeature', 'condition', 'disease', 'cult', 'boon', 'deity',
    'encounter', 'feat', 'item', 'baseitem', 'language', 'magicvariant', 'monster',
    'name', 'object', 'optionalfeature', 'race', 'subrace', 'recipe', 'reward', 'sense',
    'skill', 'spell', 'trap', 'hazard', 'variantrule', 'vehicle', 'vehicleUpgrade',
    'individual', 'hoard', 'dragon', 'gems', 'artObjects', 'magicItems',
]);
const supportRoots = new Set([
    '_meta', 'status', 'itemGroup', 'itemEntry', 'itemMastery', 'itemProperty', 'itemType',
    'itemTypeAdditionalEntries', 'languageScript', 'linkedLootTables', 'legendaryGroup',
    'monsterTemplate', 'legendaryGroupTemplate', 'dragonMundaneItems', 'sources',
]);
const formulaTags = new Set(['dice', 'damage', 'scaledice', 'scaledamage', 'd20', 'hit', 'dc', 'recharge']);
const mechanicalTags = new Set([
    ...formulaTags, 'atk', 'atkr', 'h', 'hit', 'hitYourSpellAttack', 'actSave',
    'actSaveSuccess', 'actSaveFail', 'actSaveSuccessOrFail', 'actSaveFailBy', 'actSaveFailBy5',
    'actTrigger', 'actResponse', 'condition', 'chance', 'skill', 'status',
    'dcYourSpellSave', 'skillCheck', 'hom',
].map(value => value.toLowerCase()));
const markers = {
    conditional: /\b(if|when|whenever|unless|while|provided that|only if)\b/i,
    saveBranch: /\b(succeed|succeeds|successful|success|fail|fails|failed|failure|saving throw)\b/i,
    scaling: /\b(higher level|higher levels|each level|every level|at level|level or higher)\b/i,
    resource: /\b(charges?|recharge|short rest|long rest|once per|times per)\b/i,
    restriction: /\b(cannot|can't|must|requires?|immune|resistan|countermeasure|disarm)\b/i,
    healing: /\b(heal|heals|healing|regain|regains|restore|restores)\b/i,
};

function digest(value) {
    return createHash('sha256').update(value).digest('hex');
}

function sortedObject(entries) {
    return Object.fromEntries([...entries].sort(([left], [right]) => left.localeCompare(right, 'en')));
}

function increment(target, key, amount = 1) {
    target[key] = (target[key] ?? 0) + amount;
}

async function filesUnder(directory, extension) {
    const children = await readdir(directory, { withFileTypes: true });
    const files = [];
    for (const child of children.sort((left, right) => left.name.localeCompare(right.name, 'en'))) {
        const childPath = path.join(directory, child.name);
        if (child.isDirectory()) {
            files.push(...await filesUnder(childPath, extension));
        } else if (!extension || child.name.endsWith(extension)) {
            files.push(childPath);
        }
    }
    return files;
}

function relative(file) {
    return path.relative(repositoryRoot, file).replaceAll('\\', '/');
}

function nodeType(value) {
    if (value === null) {
        return 'null';
    }
    return Array.isArray(value) ? 'array' : typeof value;
}

function argumentShape(argument) {
    if (argument.length === 0) {
        return 'empty';
    }
    if (/^[+-]?\d+$/.test(argument)) {
        return 'integer-token';
    }
    if (/^[\dDd+\-*/().\s]+$/.test(argument)) {
        return 'numeric-or-dice-characters';
    }
    if (argument.includes('{@')) {
        return 'nested-markup';
    }
    return 'other-text-or-expression';
}

function tableShape(node) {
    if (node.type === 'table') {
        return 'entry-type-table';
    }
    if (Array.isArray(node.table)) {
        return 'table-array';
    }
    if (Array.isArray(node.rows)) {
        return 'rows-array';
    }
    if (Array.isArray(node.rowsSpellProgression)) {
        return 'spell-progression-rows';
    }
    return null;
}

function splitArguments(body) {
    const values = [];
    let start = 0;
    let depth = 0;
    for (let index = 0; index < body.length; index += 1) {
        if (body[index] === '{') {
            depth += 1;
        } else if (body[index] === '}') {
            depth -= 1;
        } else if (body[index] === '|' && depth === 0) {
            values.push(body.slice(start, index));
            start = index + 1;
        }
    }
    if (body.length > 0) {
        values.push(body.slice(start));
    }
    return values;
}

// Balanced parsing preserves tag boundaries and top-level arguments, including nested tags.
function readTags(text, baseOffset = 0, nested = false, result = []) {
    let cursor = 0;
    while (cursor < text.length) {
        const start = text.indexOf('{@', cursor);
        if (start < 0) {
            break;
        }
        let depth = 1;
        let end = start + 2;
        for (; end < text.length; end += 1) {
            if (text[end] === '{') {
                depth += 1;
            } else if (text[end] === '}') {
                depth -= 1;
                if (depth === 0) {
                    break;
                }
            }
        }
        if (depth !== 0) {
            result.push({ malformed: true, offset: baseOffset + start });
            break;
        }
        const inner = text.slice(start + 2, end);
        const nameEnd = inner.search(/[\s|]/);
        const name = (nameEnd < 0 ? inner : inner.slice(0, nameEnd)).toLowerCase();
        const body = nameEnd < 0 ? '' : inner.slice(nameEnd).trimStart();
        result.push({
            name,
            arguments: splitArguments(body),
            offset: baseOffset + start,
            endOffset: baseOffset + end + 1,
            nested,
            tokenHash: digest(text.slice(start, end + 1)),
        });
        readTags(inner, baseOffset + start + 2, true, result);
        cursor = end + 1;
    }
    return result;
}

function contextFor(locator, inherited) {
    if (/\._(copy|mod|versions|implementations|templates|abstract)(\.|\[|$)/.test(locator)) {
        return 'transform';
    }
    if (/\.entriesHigherLevel(\.|\[|$)/.test(locator)) {
        return 'higher-level-entry';
    }
    if (/\.(rows|table)(\.|\[|$)/.test(locator)) {
        return 'table-content';
    }
    if (/\.(entries|entry|items|action|actionEntries|trait|reaction|bonus|legendary|mythic|variant|effect|trigger|countermeasures|instructions|ingredients|noteCook)(\.|\[|$)/.test(locator)) {
        return 'entry-content';
    }
    return inherited;
}

function ownerExample(record, locator) {
    return { file: record.file, root: record.root, index: record.index, name: record.name,
        source: record.source, authoredContext: record.authoredContext, locator };
}

function catalogFor(root) {
    return ['individual', 'hoard', 'dragon', 'gems', 'artObjects', 'magicItems'].includes(root)
        ? `treasure.${root}` : root;
}

function createGroup(root, role) {
    return {
        root, role, sourceRecords: 0, sources: {}, rootFields: {}, fieldPaths: new Map(),
        tagCounts: {}, tagRecords: {}, entryTagCounts: {}, tagVariants: {}, tagExamples: {},
        tagContexts: {}, tagArgumentShapes: {}, tagCodeValues: {}, tagVariantExamples: {},
        malformedTags: [], entryTypes: {}, tableNodes: 0, tableRows: 0,
        tableExamples: [], tableStructures: {}, tableStructureExamples: {}, markerStrings: {}, markerRecords: {}, markerExamples: {},
        diceLikeStrings: 0, syntaxOnlyFormulaStrings: 0, choices: {}, publicationFlags: {},
        records: [],
    };
}

function scanRecord(value, group, record) {
    const recordTags = new Set();
    const recordMarkers = new Set();
    const fieldPaths = new Set();
    let tagCount = 0;
    let tables = 0;

    function visit(node, locator, schemaPath, context = 'structured-field') {
        context = contextFor(locator, context);
        const field = group.fieldPaths.get(schemaPath) ?? {
            types: {}, occurrences: 0, records: 0, example: ownerExample(record, locator),
        };
        increment(field.types, nodeType(node));
        field.occurrences += 1;
        if (!fieldPaths.has(schemaPath)) {
            field.records += 1;
            fieldPaths.add(schemaPath);
        }
        group.fieldPaths.set(schemaPath, field);

        if (typeof node === 'string') {
            const tags = readTags(node);
            for (const tag of tags) {
                if (tag.malformed) {
                    group.malformedTags.push({ ...ownerExample(record, locator), offset: tag.offset });
                    continue;
                }
                tagCount += 1;
                increment(group.tagCounts, tag.name);
                if (context === 'entry-content' || context === 'higher-level-entry' || context === 'table-content') {
                    increment(group.entryTagCounts, tag.name);
                }
                recordTags.add(tag.name);
                group.tagContexts[tag.name] ??= {};
                increment(group.tagContexts[tag.name], context);
                group.tagVariants[tag.name] ??= {};
                group.tagArgumentShapes[tag.name] ??= [];
                tag.arguments.forEach((argument, index) => {
                    group.tagArgumentShapes[tag.name][index] ??= {};
                    increment(group.tagArgumentShapes[tag.name][index], argumentShape(argument));
                });
                if (['atk', 'atkr', 'actsave', 'actsavefailby', 'recharge'].includes(tag.name)) {
                    group.tagCodeValues[tag.name] ??= {};
                    increment(group.tagCodeValues[tag.name], tag.arguments.join('|'));
                }
                const variantKey = `${tag.arguments.length}args${tag.nested ? ':nested' : ''}`;
                increment(group.tagVariants[tag.name], variantKey);
                group.tagVariantExamples[tag.name] ??= {};
                group.tagVariantExamples[tag.name][variantKey] ??= {
                    ...ownerExample(record, locator), offset: tag.offset, context,
                    arguments: tag.arguments.map(argument => argument.length <= 100 ? argument : `[sha256:${digest(argument)}]`),
                };
                group.tagExamples[tag.name] ??= [];
                if (group.tagExamples[tag.name].length < 3) {
                    // Only short syntax samples, never surrounding source prose.
                    group.tagExamples[tag.name].push({
                        ...ownerExample(record, locator),
                        offset: tag.offset, context, nested: tag.nested,
                        arguments: tag.arguments.map(argument => argument.length <= 100 ? argument : `[sha256:${digest(argument)}]`),
                        tokenHash: tag.tokenHash,
                    });
                }
            }
            if (/\b\d*d\d+\b/i.test(node)) {
                group.diceLikeStrings += 1;
                if (/^\s*[\ddD+\-*/() .]+\s*$/.test(node)) {
                    group.syntaxOnlyFormulaStrings += 1;
                }
            }
            if (context !== 'transform') {
                for (const [name, pattern] of Object.entries(markers)) {
                    if (pattern.test(node)) {
                        increment(group.markerStrings, name);
                        recordMarkers.add(name);
                        group.markerExamples[name] ??= [];
                        if (group.markerExamples[name].length < 3) {
                            group.markerExamples[name].push(ownerExample(record, locator));
                        }
                    }
                }
            }
            return;
        }
        if (Array.isArray(node)) {
            node.forEach((child, index) => visit(child, `${locator}[${index}]`, `${schemaPath}[]`, context));
            return;
        }
        if (node !== null && typeof node === 'object') {
            const shape = tableShape(node);
            if (shape !== null) {
                increment(group.tableStructures, shape);
                group.tableStructureExamples[shape] ??= [];
                if (group.tableStructureExamples[shape].length < 3) {
                    group.tableStructureExamples[shape].push(ownerExample(record, locator));
                }
            }
            if (typeof node.type === 'string') {
                increment(group.entryTypes, node.type);
            }
            if (node.type === 'table') {
                tables += 1;
                group.tableNodes += 1;
                group.tableRows += Array.isArray(node.rows) ? node.rows.length : 0;
                if (group.tableExamples.length < 3) {
                    group.tableExamples.push({ ...ownerExample(record, locator), rows: node.rows?.length ?? 0 });
                }
            }
            for (const [key, child] of Object.entries(node)) {
                if (['choose', 'from', 'fromItems', 'fromGeneric', 'fromGroup', 'fromMatching', 'optional', 'options', 'count', 'weights'].includes(key)) {
                    increment(group.choices, key);
                }
                visit(child, `${locator}.${key}`, `${schemaPath}.${key}`, context);
            }
        }
    }

    for (const [key, child] of Object.entries(value ?? {})) {
        const field = group.rootFields[key] ?? { records: 0, types: {}, example: ownerExample(record, `${record.locator}.${key}`) };
        field.records += 1;
        increment(field.types, nodeType(child));
        group.rootFields[key] = field;
    }
    for (const flag of ['srd', 'srd52', 'basicRules', 'basicRules2024', 'freeRules2024']) {
        if (value?.[flag] !== undefined) {
            group.publicationFlags[flag] ??= {};
            increment(group.publicationFlags[flag], JSON.stringify(value[flag]));
        }
    }
    visit(value, record.locator, '$');
    for (const tag of recordTags) {
        increment(group.tagRecords, tag);
    }
    for (const marker of recordMarkers) {
        increment(group.markerRecords, marker);
    }
    group.records.push({ ...record, tags: tagCount, tables, markerCandidates: [...recordMarkers].sort() });
}

await mkdir(outputDirectory, { recursive: true });
const protectedFiles = await filesUnder(path.join(repositoryRoot, 'src'));
const protectedHashes = [];
for (const file of protectedFiles) {
    protectedHashes.push({ file: relative(file), sha256: digest(await readFile(file)) });
}
const protectedSnapshot = { fileCount: protectedHashes.length, sha256: digest(JSON.stringify(protectedHashes)), files: protectedHashes };
const baselinePath = path.join(outputDirectory, 'protected-files-baseline.json');
if (process.argv.includes('--capture-baseline')) {
    await writeFile(baselinePath, JSON.stringify(protectedSnapshot, null, 2) + '\n', { flag: 'wx' });
} else {
    const baseline = JSON.parse(await readFile(baselinePath, 'utf8'));
    if (baseline.sha256 !== protectedSnapshot.sha256) {
        throw new Error('Protected production/source/test files changed since the analysis baseline');
    }
}

const files = await filesUnder(dataRoot, '.json');
const manifests = [];
const groups = new Map();
const excluded = [];
for (const file of files) {
    const bytes = await readFile(file);
    const document = JSON.parse(bytes.toString('utf8').replace(/^\uFEFF/, ''));
    manifests.push({ file: relative(file), sha256: digest(bytes), roots: Object.keys(document).sort() });
    for (const [root, value] of Object.entries(document)) {
        if (root === 'crochetPattern') {
            excluded.push({ file: relative(file), root, count: value.length, reason: 'OUT_OF_SCOPE_PRODUCT_DECISION' });
            continue;
        }
        if (!definitionRoots.has(root) && !supportRoots.has(root)) {
            throw new Error(`Unclassified collection ${relative(file)}:${root}`);
        }
        const key = catalogFor(root);
        const group = groups.get(key) ?? createGroup(root, definitionRoots.has(root) ? 'definition-source' : 'support');
        const records = Array.isArray(value) ? value : [value];
        for (const [index, record] of records.entries()) {
            group.sourceRecords += 1;
            increment(group.sources, record?.source ?? '[not-authored]');
            scanRecord(record, group, {
                file: relative(file), root, index,
                locator: Array.isArray(value) ? `$.${root}[${index}]` : `$.${root}`,
                name: record?.name ?? null, source: record?.source ?? null,
                authoredContext: Object.fromEntries([
                    'className', 'classSource', 'subclassShortName', 'subclassSource',
                    'raceName', 'raceSource', 'pantheon',
                ].filter(field => record?.[field] !== undefined).map(field => [field, record[field]])),
            });
        }
        groups.set(key, group);
    }
}

const catalogEvidence = sortedObject([...groups].map(([key, group]) => [key, {
    ...group,
    fieldPaths: sortedObject(group.fieldPaths),
    rootFields: sortedObject(Object.entries(group.rootFields)),
    sources: sortedObject(Object.entries(group.sources)),
    tagCounts: sortedObject(Object.entries(group.tagCounts)),
}]));
const allTags = {};
const definitionTags = {};
let definitionRecords = 0;
let supportRecords = 0;
let malformedCount = 0;
for (const group of groups.values()) {
    if (group.role === 'definition-source') {
        definitionRecords += group.sourceRecords;
    } else {
        supportRecords += group.sourceRecords;
    }
    for (const [tag, count] of Object.entries(group.tagCounts)) {
        increment(allTags, tag, count);
        if (group.role === 'definition-source') {
            increment(definitionTags, tag, count);
        }
    }
    malformedCount += group.malformedTags.length;
}
const summary = {
    datasetContext: '5eTools v2.37.0 (repository-pinned context; byte hashes are authoritative)',
    countSemantics: 'Authored root records, before copy/version expansion; tag occurrences include nested tags.',
    files: files.length,
    definitionSourceCollections: [...groups.values()].filter(group => group.role === 'definition-source').length,
    definitionRecords, supportRecords,
    excluded,
    malformedTagCount: malformedCount,
    tags: sortedObject(Object.entries(allTags)),
    definitionTags: sortedObject(Object.entries(definitionTags)),
    mechanicalSyntaxTags: [...mechanicalTags].filter(tag => allTags[tag]).sort(),
    inputFingerprint: digest(JSON.stringify(manifests)),
    protectedFilesSha256: protectedSnapshot.sha256,
};
await writeFile(path.join(outputDirectory, 'corpus-evidence.json'), JSON.stringify({ summary, files: manifests, catalogs: catalogEvidence }, null, 2) + '\n');
console.log(JSON.stringify(summary, null, 2));
