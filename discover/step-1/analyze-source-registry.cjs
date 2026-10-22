// Read-only Phase 1 census for Source Registry identity and references.
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');

const root = path.resolve(__dirname, '../..');
const dataRoot = path.join(root, 'src/main/resources/data');
const fileBytes = new Map();
const sha256 = bytes => crypto.createHash('sha256').update(bytes).digest('hex');
const readJson = file => {
    const bytes = fs.readFileSync(file);
    fileBytes.set(path.relative(dataRoot, file).replaceAll('\\', '/'), bytes);
    return JSON.parse(bytes.toString('utf8').replace(/^\uFEFF/, ''));
};
const inc = (map, key, amount = 1) => { map[key] = (map[key] || 0) + amount; };
const norm = value => String(value).toLowerCase();
const sorted = value => Object.fromEntries(Object.entries(value).sort(([a], [b]) => a.localeCompare(b)));
const filesUnder = directory => fs.readdirSync(directory, { withFileTypes: true })
    .flatMap(entry => entry.isDirectory() ? filesUnder(path.join(directory, entry.name))
        : [path.join(directory, entry.name)]).sort();
const inventoryFiles = filesUnder(dataRoot);
const registryFile = path.join(dataRoot, 'sources/registry.json');
const booksFile = path.join(dataRoot, 'books.json');
const adventuresFile = path.join(dataRoot, 'adventures.json');
const registryDoc = readJson(registryFile);
const booksDoc = readJson(booksFile);
const adventuresDoc = readJson(adventuresFile);
const registry = registryDoc.sources;
const books = booksDoc.book;
const adventures = adventuresDoc.adventure;

function duplicateGroups(items, identity) {
    const groups = {};
    for (const [index, item] of items.entries()) {
        const value = identity(item);
        if (value === undefined || value === '') continue;
        (groups[norm(value)] ||= []).push({ index, value });
    }
    return Object.fromEntries(Object.entries(groups).filter(([, values]) => values.length > 1));
}

const registryCodeGroups = duplicateGroups(registry, item => item.code);
const registryCodes = new Map();
const registryCodeCaseVariants = {};
for (const [index, item] of registry.entries()) {
    if (typeof item.code === 'string') {
        const key = norm(item.code);
        (registryCodes.get(key) || registryCodes.set(key, []).get(key)).push({ index, value: item.code });
        (registryCodeCaseVariants[key] ||= new Set()).add(item.code);
    }
}
const aliasOccurrences = [];
const aliasOwners = {};
const aliasesPerEntry = {};
const aliasShapeIssues = [];
for (const [index, item] of registry.entries()) {
    const aliases = item.aliases;
    if (!Array.isArray(aliases)) {
        aliasShapeIssues.push({ index, code: item.code, observedType: Array.isArray(aliases) ? 'array' : typeof aliases });
        continue;
    }
    inc(aliasesPerEntry, String(aliases.length));
    const seen = new Set();
    for (const alias of aliases) {
        aliasOccurrences.push({ index, code: item.code, alias });
        if (typeof alias !== 'string' || !alias.trim()) {
            aliasShapeIssues.push({ index, code: item.code, alias, observedType: typeof alias });
            continue;
        }
        const key = norm(alias);
        (aliasOwners[key] ||= []).push({ index, code: item.code, alias });
        if (seen.has(key)) aliasShapeIssues.push({ index, code: item.code, alias, issue: 'duplicate within entry' });
        seen.add(key);
    }
}
const aliasDuplicates = Object.fromEntries(Object.entries(aliasOwners).filter(([, owners]) => owners.length > 1));
const aliasCodeConflicts = Object.entries(aliasOwners).flatMap(([alias, owners]) =>
    (registryCodes.get(alias) || []).filter(code => !owners.some(owner => owner.index === code.index))
        .map(code => ({ alias, aliasOwners: owners, codeOwner: code })));
const aliasConflicts = Object.fromEntries(Object.entries(aliasOwners)
    .filter(([alias, owners]) => (registryCodes.get(alias) || []).some(code => !owners.some(owner => owner.index === code.index))));

function fieldSummary(items, excluded = new Set()) {
    const fields = {};
    const types = {};
    for (const item of items) {
        for (const [key, value] of Object.entries(item)) {
            if (excluded.has(key)) continue;
            inc(fields, key);
            const type = value === null ? 'null' : Array.isArray(value) ? 'array' : typeof value;
            types[key] ||= {};
            inc(types[key], type);
        }
    }
    return { frequencies: sorted(fields), topLevelTypes: sorted(Object.fromEntries(
        Object.entries(types).map(([key, value]) => [key, sorted(value)]))) };
}

const booksBySource = new Map();
const adventuresBySource = new Map();
const booksById = new Map();
const adventuresById = new Map();
for (const [index, book] of books.entries()) {
    if (typeof book.source === 'string') (booksBySource.get(norm(book.source)) || booksBySource.set(norm(book.source), []).get(norm(book.source))).push(index);
    if (typeof book.id === 'string') (booksById.get(norm(book.id)) || booksById.set(norm(book.id), []).get(norm(book.id))).push(index);
}
for (const [index, adventure] of adventures.entries()) {
    if (typeof adventure.source === 'string') (adventuresBySource.get(norm(adventure.source)) || adventuresBySource.set(norm(adventure.source), []).get(norm(adventure.source))).push(index);
    if (typeof adventure.id === 'string') (adventuresById.get(norm(adventure.id)) || adventuresById.set(norm(adventure.id), []).get(norm(adventure.id))).push(index);
}
const indexMap = (items, field) => {
    const map = new Map();
    for (const [index, item] of items.entries()) if (typeof item[field] === 'string') {
        const key = norm(item[field]);
        (map.get(key) || map.set(key, []).get(key)).push({ index, item });
    }
    return map;
};
const registryByCode = indexMap(registry, 'code');
const bookById = indexMap(books, 'id');
const bookBySource = indexMap(books, 'source');
const adventureById = indexMap(adventures, 'id');
const adventureBySource = indexMap(adventures, 'source');
const bookByAlias = new Map();
for (const [index, book] of books.entries()) for (const alias of book.alias || []) {
    if (typeof alias !== 'string') continue;
    const key = norm(alias);
    (bookByAlias.get(key) || bookByAlias.set(key, []).get(key)).push({ index, item: book, alias });
}
const registrySourceAliases = new Map();
for (const [alias, owners] of Object.entries(aliasOwners)) registrySourceAliases.set(alias, owners);

function resolutions(value) {
    if (typeof value !== 'string' || !value.trim()) return { code: [], book: [], adventure: [], bookAlias: [] };
    const key = norm(value);
    return { code: registryByCode.get(key) || [], book: bookBySource.get(key) || [],
        adventure: adventureBySource.get(key) || [], bookAlias: bookByAlias.get(key) || [] };
}
function parentResolution(value) {
    if (typeof value !== 'string' || !value.trim()) return { code: [], book: [], adventure: [], bookAlias: [] };
    const key = norm(value);
    return { code: registryByCode.get(key) || [], book: [...(bookById.get(key) || []), ...(bookBySource.get(key) || [])],
        adventure: [...(adventureById.get(key) || []), ...(adventureBySource.get(key) || [])],
        bookAlias: bookByAlias.get(key) || [] };
}
const records = [
    ...books.map((item, index) => ({ kind: 'book', file: 'books.json', index, item })),
    ...adventures.map((item, index) => ({ kind: 'adventure', file: 'adventures.json', index, item }))
];
const idSourceDifferences = records.filter(record => record.item.id !== record.item.source)
    .map(record => ({ kind: record.kind, index: record.index, id: record.item.id, source: record.item.source }));
const sourceCodeResolution = records.map(record => {
    const result = resolutions(record.item.source);
    return { kind: record.kind, index: record.index, id: record.item.id, source: record.item.source,
        codeMatches: result.code.map(match => match.item.code),
        sourceMatches: [...result.book.map(match => `book:${match.item.id}`),
            ...result.adventure.map(match => `adventure:${match.item.id}`)],
        aliasMatches: result.bookAlias.map(match => `book:${match.item.id}`) };
});
const metadataAliasEdges = records.flatMap(record => (record.item.alias || []).map(alias => ({ kind: record.kind,
    index: record.index, id: record.item.id, source: record.item.source, alias,
    registryCodeMatches: registryByCode.get(norm(alias)) || [], registryAliasMatches: registrySourceAliases.get(norm(alias)) || [] })));
const parentEdges = records.filter(record => record.item.parentSource).map(record => {
    const result = parentResolution(record.item.parentSource);
    const uniqueBookMatches = [...new Map(result.book.map(match => [match.index, match])).values()];
    const uniqueAdventureMatches = [...new Map(result.adventure.map(match => [match.index, match])).values()];
    return { kind: record.kind, index: record.index, id: record.item.id, source: record.item.source,
        parentSource: record.item.parentSource, codeMatches: result.code.map(match => match.item.code),
        bookMatches: uniqueBookMatches.map(match => `book:${match.item.id}`),
        adventureMatches: uniqueAdventureMatches.map(match => `adventure:${match.item.id}`),
        aliasMatches: [...new Map(result.bookAlias.map(match => [match.index, match])).values()]
            .map(match => `book:${match.item.id}`) };
});

const usageByCode = {};
const unknownSourceFields = [];
const sourceFieldFrequency = {};
const sourceCodeCaseDifferences = {};
const nestedRefs = [];
const tagRefCounts = {};
const tagRefByCode = {};
const sourceRefExamples = {};
const tagPattern = /\{@([^\s{}]+)(?:\s|(?=\}))/g;
const targetedTypes = new Set(inventoryFiles.filter(file => ![registryFile, booksFile, adventuresFile].includes(file))
    .map(file => path.relative(dataRoot, file).replaceAll('\\', '/')));
const readOnlyFileData = new Map();
function fileData(file) {
    if (!readOnlyFileData.has(file)) readOnlyFileData.set(file, readJson(file));
    return readOnlyFileData.get(file);
}
for (const file of inventoryFiles) {
    const relativeFile = path.relative(dataRoot, file).replaceAll('\\', '/');
    if (!targetedTypes.has(relativeFile)) continue;
    const doc = fileData(file);
    function walk(value, location, domain) {
        if (typeof value === 'string') {
            for (const match of value.matchAll(tagPattern)) {
                let depth = 1;
                let end = match.index + 2;
                while (end < value.length && depth > 0) {
                    if (value[end] === '{') depth++;
                    if (value[end] === '}') depth--;
                    end++;
                }
                const tag = match[1];
                const raw = value.slice(match.index, end);
                const body = raw.slice(tag.length + 2, -1).trim();
                const parts = body.split('|');
                if (tag === 'book' || tag === 'adventure') {
                    inc(tagRefCounts, tag);
                    const targetId = parts[1];
                    const targets = tag === 'book' ? [...(bookById.get(norm(targetId)) || []), ...(bookBySource.get(norm(targetId)) || [])]
                        : [...(adventureById.get(norm(targetId)) || []), ...(adventureBySource.get(norm(targetId)) || [])];
                    const sourceCandidates = targets.map(target => target.item.source);
                    if (targets.length) for (const code of sourceCandidates) inc(tagRefByCode, code);
                    nestedRefs.push({ tag, body, location: `${relativeFile}#${location}`,
                        targetIds: targets.map(target => target.item.id), targetSources: sourceCandidates });
                }
            }
            return;
        }
        if (!value || typeof value !== 'object') return;
        if (Array.isArray(value)) {
            value.forEach((child, index) => walk(child, `${location}/${index}`, domain));
            return;
        }
        for (const [key, child] of Object.entries(value)) {
            if ((key === 'source' || key === 'parentSource') && typeof child === 'string') {
                inc(sourceFieldFrequency, key);
                const keyNormalized = norm(child);
                usageByCode[keyNormalized] ||= { codeExamples: new Set(), domains: {}, count: 0,
                    sourceFields: {}, examples: [] };
                const usage = usageByCode[keyNormalized];
                usage.codeExamples.add(child);
                usage.count++;
                inc(usage.domains, domain);
                inc(usage.sourceFields, key);
                if (usage.examples.length < 2) usage.examples.push(`${relativeFile}#${location}/${key}`);
                const codeMatches = registryByCode.get(keyNormalized) || [];
                const aliasMatches = registrySourceAliases.get(keyNormalized) || [];
                if (!codeMatches.length && !aliasMatches.length) {
                    unknownSourceFields.push({ field: key, value: child, location: `${relativeFile}#${location}/${key}` });
                } else if (codeMatches.length === 1 && codeMatches[0].item.code !== child) {
                    sourceCodeCaseDifferences[keyNormalized] ||= { registered: codeMatches[0].item.code, observed: new Set(), count: 0, examples: [] };
                    const difference = sourceCodeCaseDifferences[keyNormalized];
                    difference.observed.add(child);
                    difference.count++;
                    if (difference.examples.length < 2) difference.examples.push(`${relativeFile}#${location}/${key}`);
                }
            }
            walk(child, `${location}/${key}`, domain);
        }
    }
    for (const [key, value] of Object.entries(doc)) {
        if (Array.isArray(value)) value.forEach((entry, index) => walk(entry, `/${key}/${index}`, relativeFile.split('/')[0]));
        else walk(value, `/${key}`, relativeFile.split('/')[0]);
    }
}

const codeEntries = [...registryByCode.entries()];
const registryBookCoverage = codeEntries.filter(([code]) => booksBySource.has(code)).length;
const registryAdventureCoverage = codeEntries.filter(([code]) => adventuresBySource.has(code)).length;
const registryPublicationCoverage = codeEntries.filter(([code]) => booksBySource.has(code) || adventuresBySource.has(code)).length;
const registryPublicationIdCoverage = codeEntries.filter(([code]) => booksById.has(code) || adventuresById.has(code)).length;
const registryAnyPublicationCoverage = codeEntries.filter(([code]) => booksBySource.has(code)
    || adventuresBySource.has(code) || booksById.has(code) || adventuresById.has(code)).length;
const registryUsage = codeEntries.map(([code, entries]) => {
    const value = usageByCode[code];
    return { code: entries[0].item.code, registryIndex: entries[0].index,
        books: (booksBySource.get(code) || []).map(index => books[index].id),
        adventures: (adventuresBySource.get(code) || []).map(index => adventures[index].id),
        bookIds: (booksById.get(code) || []).map(index => books[index].id),
        adventureIds: (adventuresById.get(code) || []).map(index => adventures[index].id),
        corpusReferences: value?.count || 0, corpusDomains: value?.domains || {},
        aliases: entries[0].item.aliases || [] };
});
const allBookIds = books.map(book => book.id);
const allAdventureIds = adventures.map(adventure => adventure.id);
const uncoveredBookSources = books.filter(book => !(registryByCode.get(norm(book.source)) || []).length
    && !(registrySourceAliases.get(norm(book.source)) || []).length)
    .map(book => ({ id: book.id, source: book.source }));
const uncoveredAdventureSources = adventures.filter(adventure => !(registryByCode.get(norm(adventure.source)) || []).length
    && !(registrySourceAliases.get(norm(adventure.source)) || []).length)
    .map(adventure => ({ id: adventure.id, source: adventure.source }));
const parentRelationSummary = parentEdges.reduce((summary, edge) => {
    summary.total++;
    const metadataTargetCount = edge.bookMatches.length + edge.adventureMatches.length + edge.aliasMatches.length;
    inc(summary, edge.codeMatches.length === 1 ? 'registryCodeTarget' : 'unregisteredParentValue');
    inc(summary, metadataTargetCount === 1 ? 'oneMetadataTarget' : metadataTargetCount === 0
        ? 'noMetadataTarget' : 'multipleMetadataTargets');
    return summary;
}, { total: 0, registryCodeTarget: 0, unregisteredParentValue: 0,
    oneMetadataTarget: 0, noMetadataTarget: 0, multipleMetadataTargets: 0 });
const parentValueGroups = {};
for (const edge of parentEdges) {
    const key = norm(edge.parentSource);
    parentValueGroups[key] ||= { parentSource: edge.parentSource, records: [], registryCodeMatches: edge.codeMatches };
    parentValueGroups[key].records.push(`${edge.kind}:${edge.id}`);
}
const dateFormats = { books: {}, adventures: {} };
for (const [kind, items] of [['books', books], ['adventures', adventures]]) for (const item of items) {
    const date = item.published;
    const format = typeof date !== 'string' ? typeof date : /^\d{4}-\d{2}-\d{2}$/.test(date) ? 'YYYY-MM-DD' : 'other-string';
    inc(dateFormats[kind], format);
}
const adventureLevelShapes = {};
for (const adventure of adventures) {
    const level = adventure.level;
    const shape = level && typeof level === 'object' && !Array.isArray(level)
        ? Object.keys(level).sort().join(',') : Array.isArray(level) ? 'array' : typeof level;
    inc(adventureLevelShapes, shape);
}
const groupFrequencies = {};
for (const [kind, items] of [['books', books], ['adventures', adventures]]) {
    groupFrequencies[kind] = {};
    for (const item of items) inc(groupFrequencies[kind], item.group ?? '(absent)');
}
const metadataFields = {
    registry: fieldSummary(registry),
    books: fieldSummary(books, new Set(['contents', 'cover'])),
    adventures: fieldSummary(adventures, new Set(['contents', 'cover', 'storyline', 'level']))
};
const corpHash = crypto.createHash('sha256');
for (const file of inventoryFiles) {
    const relativeFile = path.relative(dataRoot, file).replaceAll('\\', '/');
    const bytes = fileBytes.get(relativeFile) || fs.readFileSync(file);
    corpHash.update(`${relativeFile}\0${sha256(bytes)}\n`);
}
const report = {
    methodVersion: 1,
    observedAt: '2026-10-22',
    gitRevision: require('node:child_process').execFileSync('git', ['rev-parse', 'HEAD'], { cwd: root, encoding: 'utf8' }).trim(),
    sourceCorpusSha256: corpHash.digest('hex'),
    inspectedFiles: inventoryFiles.length,
    analysisFiles: [
        { file: 'sources/registry.json', bytes: fileBytes.get('sources/registry.json').length,
            sha256: sha256(fileBytes.get('sources/registry.json')), records: registry.length },
        { file: 'books.json', bytes: fileBytes.get('books.json').length, sha256: sha256(fileBytes.get('books.json')), records: books.length },
        { file: 'adventures.json', bytes: fileBytes.get('adventures.json').length,
            sha256: sha256(fileBytes.get('adventures.json')), records: adventures.length }
    ],
    metadataFields,
    registry: {
        codeCount: registry.length,
        uniqueCodesCaseInsensitive: registryByCode.size,
        duplicateCodes: registryCodeGroups,
        aliasesTotal: aliasOccurrences.length,
        aliasesPerEntry: sorted(aliasesPerEntry),
        uniqueAliasesCaseInsensitive: Object.keys(aliasOwners).length,
        duplicateAliases: aliasDuplicates,
        aliasCodeConflicts,
        aliasShapeIssues,
        emptyAliasEntries: registry.filter(item => Array.isArray(item.aliases) && item.aliases.length === 0).length,
        caseVariants: Object.fromEntries(Object.entries(registryCodeCaseVariants)
            .filter(([, values]) => values.size > 1).map(([code, values]) => [code, [...values]]))
    },
    books: {
        count: books.length,
        duplicateIds: duplicateGroups(books, item => item.id),
        duplicateSources: duplicateGroups(books, item => item.source),
        duplicateDisplayNames: duplicateGroups(books, item => item.name),
        idsDifferentFromSources: idSourceDifferences.filter(item => item.kind === 'book'),
        aliasOccurrences: metadataAliasEdges.filter(item => item.kind === 'book'),
        parentEdges: parentEdges.filter(item => item.kind === 'book'),
        publishedDateFormats: dateFormats.books,
        groupCounts: groupFrequencies.books,
        authorMissing: books.map((item, index) => ({ item, index })).filter(({ item }) => !item.author)
            .map(({ index, item }) => ({ index, id: item.id, source: item.source }))
    },
    adventures: {
        count: adventures.length,
        duplicateIds: duplicateGroups(adventures, item => item.id),
        duplicateSources: duplicateGroups(adventures, item => item.source),
        duplicateDisplayNames: duplicateGroups(adventures, item => item.name),
        idsDifferentFromSources: idSourceDifferences.filter(item => item.kind === 'adventure'),
        aliasOccurrences: metadataAliasEdges.filter(item => item.kind === 'adventure'),
        parentEdges: parentEdges.filter(item => item.kind === 'adventure'),
        parentRelationSummary,
        parentSourceValues: Object.values(parentValueGroups),
        publishedDateFormats: dateFormats.adventures,
        publishedOrderCounts: { present: adventures.filter(item => item.publishedOrder !== undefined).length,
            absent: adventures.filter(item => item.publishedOrder === undefined).length,
            duplicateWithinParent: (() => {
                const byParent = {};
                for (const item of adventures.filter(item => item.parentSource && item.publishedOrder !== undefined)) {
                    const key = `${norm(item.parentSource)}|${item.publishedOrder}`;
                    (byParent[key] ||= []).push(item.id);
                }
                return Object.fromEntries(Object.entries(byParent).filter(([, ids]) => ids.length > 1));
            })() },
        levelShapes: sorted(adventureLevelShapes),
        groupCounts: groupFrequencies.adventures,
        authorMissing: adventures.map((item, index) => ({ item, index })).filter(({ item }) => !item.author)
            .map(({ index, item }) => ({ index, id: item.id, source: item.source }))
    },
    sourceCoverage: {
        registryCodes: registryByCode.size,
        withBookMetadata: registryBookCoverage,
        withAdventureMetadata: registryAdventureCoverage,
        withBookOrAdventureMetadata: registryPublicationCoverage,
        matchingBookOrAdventureId: registryPublicationIdCoverage,
        withSourceOrPublicationId: registryAnyPublicationCoverage,
        withoutEitherSourceOrPublicationId: registryByCode.size - registryAnyPublicationCoverage,
        distinctRegistryCodesUsedElsewhere: Object.keys(usageByCode).filter(code => registryByCode.has(code)).length,
        unregisteredSourceFields: unknownSourceFields,
        sourceCaseDifferences: Object.fromEntries(Object.entries(sourceCodeCaseDifferences).map(([key, value]) => [key,
            { registered: value.registered, observed: [...value.observed], count: value.count, examples: value.examples }])),
        sourceFieldFrequency: sorted(sourceFieldFrequency),
        usageByRegistryCode: registryUsage,
        unregisteredBookSources: uncoveredBookSources,
        unregisteredAdventureSources: uncoveredAdventureSources,
        sourceFieldOccurrences: Object.values(usageByCode).reduce((sum, value) => sum + value.count, 0)
    },
    tagReferences: {
        bookOccurrences: tagRefCounts.book || 0,
        adventureOccurrences: tagRefCounts.adventure || 0,
        sourceCountsFromResolvedTags: sorted(tagRefByCode),
        unmatchedTargets: nestedRefs.filter(ref => !ref.targetIds.length)
    }
};
const outputFile = path.join(__dirname, 'source-registry-evidence.json');
fs.writeFileSync(outputFile, `${JSON.stringify(report, null, 4)}\n`);
console.log(JSON.stringify({ corpusSha256: report.sourceCorpusSha256, inspectedFiles: report.inspectedFiles,
    registry: report.registry.codeCount, bookRecords: books.length, adventureRecords: adventures.length,
    emptyAliasEntries: report.registry.emptyAliasEntries,
    duplicateCodes: Object.keys(report.registry.duplicateCodes).length,
    duplicateAliases: Object.keys(report.registry.duplicateAliases).length,
    registryBookMetadata: registryBookCoverage, registryAdventureMetadata: registryAdventureCoverage,
    registryPublicationMetadata: registryPublicationCoverage,
    unregisteredSourceFieldValues: unknownSourceFields.length,
    parentRelations: parentRelationSummary,
    idSourceDifferences: idSourceDifferences.length,
    bookTagReferences: report.tagReferences.bookOccurrences,
    adventureTagReferences: report.tagReferences.adventureOccurrences,
    unmatchedTags: report.tagReferences.unmatchedTargets.length
}, null, 2));
