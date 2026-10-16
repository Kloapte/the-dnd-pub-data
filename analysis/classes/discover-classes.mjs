import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';

// Discovery only: no copy or version expansion and no source mutations.
const dataDirectory = path.resolve('src/main/resources/data');
const outputDirectory = path.resolve('analysis/classes');
const classDirectory = path.join(dataDirectory, 'classes');
const kinds = ['class', 'subclass', 'classFeature', 'subclassFeature'];
const records = [];
const files = [];
const fields = new Map();
const allSources = new Map();
const directives = [];
const markup = [];
const structuredReferences = [];
const objectsByDiscriminator = new Map();
const selectors = [];
const invalidSources = [];
function typeOf(value) {
    if (value === null) {
        return 'null';
    }
    return Array.isArray(value) ? 'array' : typeof value;
}

function resolutionStatus(matches) {
    if (matches === 1) {
        return 'resolved';
    }
    return matches > 1 ? 'ambiguous' : 'unresolved';
}
const fold = value => String(value ?? '').trim().toLowerCase();
const tuple = values => values.map(fold).join('|');
const counter = values => Object.fromEntries([...values.reduce((counts, value) => {
    counts.set(value, (counts.get(value) ?? 0) + 1);
    return counts;
}, new Map())].sort(([left], [right]) => left.localeCompare(right)));

function writeJson(filename, value) {
    fs.writeFileSync(path.join(outputDirectory, filename), JSON.stringify(value, null, 2) + '\n');
}

function walkFiles(directory) {
    return fs.readdirSync(directory, {withFileTypes: true}).flatMap(entry => {
        const filename = path.join(directory, entry.name);
        return entry.isDirectory() ? walkFiles(filename) : [filename];
    }).sort();
}

function identity(kind, record) {
    if (kind === 'class') {
        return tuple([record.name, record.source]);
    }
    if (kind === 'subclass') {
        return tuple([record.name, record.className, record.classSource, record.source]);
    }
    if (kind === 'classFeature') {
        return tuple([record.name, record.className, record.classSource, record.level, record.source]);
    }
    return tuple([
        record.name, record.className, record.classSource, record.subclassShortName,
        record.subclassSource, record.level, record.source,
    ]);
}

function addSource(code, category, locator) {
    if (typeof code !== 'string' || !code.trim()) {
        invalidSources.push({value: code, category, locator});
        return;
    }
    const key = code;
    if (!allSources.has(key)) {
        allSources.set(key, []);
    }
    allSources.get(key).push({category, locator});
}

function categoryFor(propertyPath) {
    const segments = propertyPath.split('.').map(segment => segment.replaceAll('[]', ''));
    const leaf = segments.at(-1);
    if (segments.some(segment => /^_\w+|^foundry|^hasFluff|^srd|^basicRules/.test(segment))) {
        return 'importMetadata';
    }
    if (['edition', 'isReprinted', 'page', 'header', 'style', 'preserve', 'tag',
        'subclassTitle', 'tableDisplayName', 'colStyles'].includes(leaf)
        || segments.includes('genTables')) {
        return 'importMetadata';
    }
    if (['source', 'classSource', 'subclassSource', 'className', 'subclassShortName',
        'otherSources', 'reprintedAs', 'uid', 'classFeatures', 'subclassFeatures',
        'classFeature', 'subclassFeature', 'optionalfeature', 'feat', 'item', 'subclasses'].includes(leaf)) {
        return 'sourceReference';
    }
    if (['count', 'isRequiredOption', 'attributes', 'consumes', 'amount', 'amountMin',
        'amountMax', 'gainSubclassFeature', 'gainSubclassFeatureHasContent', 'isClassFeatureVariant'].includes(leaf)) {
        return 'mechanicalOrStructuredConfiguration';
    }
    if (leaf === 'type') {
        return 'identityOrDiscriminator';
    }
    if (segments.some(segment => ['entries', 'entry', 'items', 'fluff', 'caption', 'colLabels',
        'rows', 'default', 'goldAlternative', 'description', 'footer', 'by'].includes(segment))) {
        return 'descriptiveOrPresentation';
    }
    if (['name', 'shortName', 'alias'].includes(leaf)) {
        return 'identityOrDiscriminator';
    }
    return 'mechanicalOrStructuredConfiguration';
}

function sample(value) {
    if (typeof value === 'string') {
        return value.length > 140 ? {prefix: value.slice(0, 140), length: value.length, truncated: true} : value;
    }
    if (Array.isArray(value)) {
        return {length: value.length, elementTypes: [...new Set(value.map(typeOf))].sort()};
    }
    if (value && typeof value === 'object') {
        return {keys: Object.keys(value).sort()};
    }
    return value;
}

function visit(value, propertyPath, locator, context) {
    if (!fields.has(propertyPath)) {
        fields.set(propertyPath, {
            path: propertyPath, category: categoryFor(propertyPath), occurrences: 0,
            types: {}, byPrimarySource: {}, byEdition: {}, shapes: {}, examplesByType: {},
            explicitNullCount: 0, emptyArrayCount: 0, emptyObjectCount: 0,
        });
    }
    const stat = fields.get(propertyPath);
    const valueType = typeOf(value);
    stat.occurrences++;
    stat.types[valueType] = (stat.types[valueType] ?? 0) + 1;
    const primarySource = context.source ?? '(metadata)';
    stat.byPrimarySource[primarySource] = (stat.byPrimarySource[primarySource] ?? 0) + 1;
    const edition = context.edition ?? '(unspecified on root)';
    stat.byEdition[edition] = (stat.byEdition[edition] ?? 0) + 1;
    stat.examplesByType[valueType] ??= [];
    if (stat.examplesByType[valueType].length < 2) {
        stat.examplesByType[valueType].push({locator, value: sample(value)});
    }
    if (value === null) {
        stat.explicitNullCount++;
    } else if (Array.isArray(value)) {
        if (!value.length) {
            stat.emptyArrayCount++;
        }
        value.forEach((element, index) => visit(element, propertyPath + '[]', `${locator}/${index}`, context));
    } else if (typeof value === 'object') {
        if (typeof value.type === 'string') {
            const variants = objectsByDiscriminator.get(value.type) ?? [];
            variants.push({locator, keys: Object.keys(value).sort(),
                sample: Object.fromEntries(Object.entries(value).filter(([key]) => ![
                    'entries', 'entry', 'items', 'rows', 'colLabels', 'colStyles',
                ].includes(key)))});
            objectsByDiscriminator.set(value.type, variants);
        }
        const shape = Object.keys(value).sort().join(',');
        stat.shapes[shape] = (stat.shapes[shape] ?? 0) + 1;
        if (!Object.keys(value).length) {
            stat.emptyObjectCount++;
        }
        for (const [key, child] of Object.entries(value)) {
            if (['source', 'classSource', 'subclassSource', 'raceSource'].includes(key)) {
                addSource(child, key, `${locator}/${key}`);
            }
            if (['_copy', '_versions', '_mod', '_abstract', '_implementations', '_variables'].includes(key)) {
                directives.push({owner: context.locator, kind: context.kind, name: context.name,
                    source: context.source, locator: `${locator}/${key}`, directive: key, value: child});
            }
            visit(child, `${propertyPath}.${key}`, `${locator}/${key}`, context);
        }
    } else if (typeof value === 'string') {
        for (const match of value.matchAll(/(?:^|\|)source=([^|{}]+)/gi)) {
            for (const token of match[1].split(';')) {
                const code = token.trim().replace(/^!/, '');
                addSource(code, 'sourceFilter', locator);
            }
        }
        for (const match of value.matchAll(/\{@(\w+)\s+([^{}]*)\}/g)) {
            markup.push({owner: context.locator, locator, tag: match[1], uid: match[2]});
        }
        const leaf = propertyPath.split('.').at(-1).replaceAll('[]', '');
        if (['classFeature', 'subclassFeature', 'optionalfeature', 'feat', 'classFeatures', 'subclassFeatures'].includes(leaf)) {
            const kind = {classFeatures: 'classFeature', subclassFeatures: 'subclassFeature'}[leaf] ?? leaf;
            structuredReferences.push({owner: context.locator, locator, kind, uid: value});
        }
    }
}

for (const filename of walkFiles(classDirectory)) {
    const relative = path.relative(dataDirectory, filename).replaceAll('\\', '/');
    const bytes = fs.readFileSync(filename);
    if (!filename.toLowerCase().endsWith('.json')) {
        files.push({file: relative, bytes: bytes.length, nonJson: true});
        continue;
    }
    const root = JSON.parse(bytes);
    files.push({file: relative, bytes: bytes.length,
        sha256: crypto.createHash('sha256').update(bytes).digest('hex'),
        roots: Object.fromEntries(Object.entries(root).map(([key, value]) => [key,
            {type: typeOf(value), count: Array.isArray(value) ? value.length : 1}]))});
    for (const [kind, values] of Object.entries(root)) {
        if (Array.isArray(values)) {
            values.forEach((record, index) => {
                const locator = `${relative}#/${kind}/${index}`;
                const context = {...record, kind, locator};
                records.push({kind, locator, file: relative, identity: identity(kind, record), data: record});
                visit(record, `${kind}[]`, locator, context);
            });
        } else {
            visit(values, kind, `${relative}#/${kind}`, {kind, locator: `${relative}#/${kind}`});
        }
    }
}

const byLocator = new Map(records.map(record => [record.locator, record]));
const indexes = Object.fromEntries(kinds.map(kind => [kind, new Map()]));
for (const record of records) {
    const index = indexes[record.kind];
    if (index) {
        const matches = index.get(record.identity) ?? [];
        matches.push(record);
        index.set(record.identity, matches);
    }
}

function parseFeatureUid(kind, uid) {
    const parts = uid.split('|');
    const classSource = parts[2] || 'PHB';
    if (kind === 'classFeature') {
        return tuple([parts[0], parts[1], classSource, parts[3], parts[4] || classSource]);
    }
    const subclassSource = parts[4] || 'PHB';
    return tuple([parts[0], parts[1], classSource, parts[3], subclassSource, parts[5], parts[6] || subclassSource]);
}

const edges = [];
const reprintEdges = [];
function addEdge(owner, relation, targetKind, targetIdentity, details = {}) {
    const targets = indexes[targetKind]?.get(targetIdentity) ?? [];
    edges.push({owner, relation, targetKind, targetIdentity,
        status: resolutionStatus(targets.length),
        targets: targets.map(target => target.locator), ...details});
}

for (const reference of structuredReferences) {
    if (['classFeature', 'subclassFeature'].includes(reference.kind)) {
        addEdge(reference.owner, 'structuredFeature', reference.kind,
            parseFeatureUid(reference.kind, reference.uid), {locator: reference.locator, uid: reference.uid});
    }
}
for (const reference of markup) {
    if (['classFeature', 'subclassFeature'].includes(reference.tag)) {
        addEdge(reference.owner, 'markupFeature', reference.tag,
            parseFeatureUid(reference.tag, reference.uid), {locator: reference.locator, uid: reference.uid});
    }
}
for (const record of records) {
    const data = record.data;
    if (record.kind === 'subclass') {
        addEdge(record.locator, 'parentClass', 'class', tuple([data.className, data.classSource]));
    }
    if (record.kind === 'classFeature') {
        addEdge(record.locator, 'parentClass', 'class', tuple([data.className, data.classSource]));
    }
    if (record.kind === 'subclassFeature') {
        const parents = records.filter(parent => parent.kind === 'subclass'
            && tuple([parent.data.className, parent.data.classSource, parent.data.shortName, parent.data.source])
                === tuple([data.className, data.classSource, data.subclassShortName, data.subclassSource]));
        edges.push({owner: record.locator, relation: 'parentSubclass', targetKind: 'subclass',
            targetIdentity: tuple([data.className, data.classSource, data.subclassShortName, data.subclassSource]),
            status: resolutionStatus(parents.length),
            targets: parents.map(parent => parent.locator)});
    }
    if (data._copy) {
        // Copy payloads may omit identity fields which are supplied by the owning record.
        addEdge(record.locator, 'copy', record.kind, identity(record.kind, {...data, ...data._copy}));
    }
    for (const uid of data.reprintedAs ?? []) {
        const parts = uid.split('|');
        const targets = record.kind === 'class'
            ? indexes.class.get(tuple([parts[0], parts[1]])) ?? []
            : records.filter(candidate => candidate.kind === 'subclass'
                && tuple([candidate.data.shortName, candidate.data.className,
                    candidate.data.classSource, candidate.data.source]) === tuple(parts));
        reprintEdges.push({owner: record.locator, kind: record.kind, uid,
            status: resolutionStatus(targets.length),
            targets: targets.map(target => target.locator)});
    }
}

const adjacency = new Map();
for (const edge of edges.filter(edge => ['structuredFeature', 'copy'].includes(edge.relation))) {
    adjacency.set(edge.owner, [...(adjacency.get(edge.owner) ?? []), ...edge.targets]);
}
const cycles = [];
const completed = new Set();
function visitGraph(node, stack = []) {
    const cycleStart = stack.indexOf(node);
    if (cycleStart >= 0) {
        cycles.push([...stack.slice(cycleStart), node]);
        return;
    }
    if (completed.has(node)) {
        return;
    }
    for (const next of adjacency.get(node) ?? []) {
        visitGraph(next, [...stack, node]);
    }
    completed.add(node);
}
records.forEach(record => visitGraph(record.locator));
const structuralCycles = [...cycles];
for (const edge of edges.filter(edge => edge.relation === 'markupFeature')) {
    adjacency.set(edge.owner, [...(adjacency.get(edge.owner) ?? []), ...edge.targets]);
}
completed.clear();
cycles.length = 0;
records.forEach(record => visitGraph(record.locator));
const cyclesIncludingMarkup = [...cycles];
// Reachability is structural: a prose link does not grant a feature.
adjacency.clear();
for (const edge of edges.filter(edge => ['structuredFeature', 'copy'].includes(edge.relation))) {
    adjacency.set(edge.owner, [...(adjacency.get(edge.owner) ?? []), ...edge.targets]);
}

const directlyReached = new Set(edges.filter(edge => edge.relation === 'structuredFeature')
    .flatMap(edge => edge.targets));
const reachable = new Set();
function reach(node) {
    if (reachable.has(node)) {
        return;
    }
    reachable.add(node);
    for (const next of adjacency.get(node) ?? []) {
        reach(next);
    }
}
records.filter(record => ['class', 'subclass'].includes(record.kind)).forEach(record => reach(record.locator));
const orphans = records.filter(record => ['classFeature', 'subclassFeature'].includes(record.kind)
    && !reachable.has(record.locator)).map(record => ({kind: record.kind, identity: record.identity, locator: record.locator}));

function slug(value) {
    return fold(value).normalize('NFD').replace(/\p{Diacritic}/gu, '')
        .replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
}

function proposedId(record) {
    const data = record.data;
    if (record.kind === 'class') {
        return `class:${slug(data.source)}:${slug(data.name)}`;
    }
    const parent = `${slug(data.classSource)}:${slug(data.className)}`;
    if (record.kind === 'subclass') {
        return `subclass:${slug(data.source)}:${parent}:${slug(data.name)}`;
    }
    if (record.kind === 'classFeature') {
        return `class-feature:${slug(data.source)}:${parent}:${data.level}:${slug(data.name)}`;
    }
    return `subclass-feature:${slug(data.source)}:${parent}:${slug(data.subclassSource)}:${slug(data.subclassShortName)}:${data.level}:${slug(data.name)}`;
}

function collisions(keyFunction) {
    const groups = new Map();
    for (const record of records.filter(record => kinds.includes(record.kind))) {
        const key = `${record.kind}:${keyFunction(record)}`;
        groups.set(key, [...(groups.get(key) ?? []), record.locator]);
    }
    return [...groups].filter(([, values]) => values.length > 1)
        .map(([key, locators]) => ({key, locators}));
}

// Source-qualified UID and markup sources are distinct from descriptive mentions.
const uidReferences = [...structuredReferences, ...markup.map(reference => ({...reference, kind: reference.tag}))];
const sourcePositionsByKind = new Map([
    ['classFeature', [2, 4]],
    ['subclassFeature', [2, 4, 6]],
    ['subclass', [2, 3]],
    ['deity', [2]],
]);
const simpleSourceUidKinds = new Set([
    'spell', 'item', 'itemProperty', 'itemMastery', 'feat', 'optfeature', 'optionalfeature',
    'background', 'race', 'action', 'condition', 'disease', 'sense', 'skill', 'language',
    'class', 'creature', 'book', 'variantrule', 'table', 'deck', 'reward', 'facility',
    'vehicle', 'object', 'trap', 'hazard', 'cite',
]);
for (const reference of uidReferences) {
    const parts = reference.uid.split('|');
    const positions = sourcePositionsByKind.get(reference.kind)
        ?? (simpleSourceUidKinds.has(reference.kind) ? [1] : []);
    for (const position of positions) {
        if (parts[position]) {
            addSource(parts[position], `uid:${reference.kind}`, reference.locator);
        }
    }
    if (reference.kind === 'class' && parts[4]) {
        addSource(parts[4], 'uid:class-subclassSource', reference.locator);
    }
}

function scanAdditionalReferences(value, locator, owner, propertyPath = '') {
    if (Array.isArray(value)) {
        value.forEach((child, index) => scanAdditionalReferences(child, `${locator}/${index}`, owner, propertyPath + '[]'));
    } else if (value && typeof value === 'object') {
        if (value.type === 'statblock' && value.name && value.source) {
            structuredReferences.push({kind: value.tag, uid: `${value.name}|${value.source}`,
                owner, locator, representation: 'statblock'});
        }
        for (const [key, child] of Object.entries(value)) {
            scanAdditionalReferences(child, `${locator}/${key}`, owner, `${propertyPath}.${key}`);
        }
    } else if (typeof value === 'string') {
        if (propertyPath.includes('.skills') && propertyPath.endsWith('.from[]')) {
            structuredReferences.push({kind: 'skill', uid: value, owner, locator});
        }
        if (propertyPath.includes('.reprintedAs')) {
            const parts = value.split('|');
            const sourcePositions = parts.length === 4 ? [2, 3] : [1];
            for (const position of sourcePositions) {
                if (parts[position]) {
                    addSource(parts[position], 'reprintedAs', locator);
                }
            }
        }
        if (propertyPath.includes('.defaultData') || propertyPath.endsWith('.item')) {
            if (/^[^{}|]+\|[^|]+$/.test(value)) {
                structuredReferences.push({kind: 'item', uid: value, owner, locator});
                addSource(value.split('|')[1], 'equipmentItemUid', locator);
            }
        }
        if (propertyPath.includes('.optionalfeatureProgression') && propertyPath.includes('.required')) {
            structuredReferences.push({kind: 'optionalfeature', uid: value, owner, locator});
            if (value.split('|')[1]) {
                addSource(value.split('|')[1], 'requiredOptionalFeatureUid', locator);
            }
        }
        if (propertyPath.includes('.additionalSpells') && !value.includes('{@')) {
            const isLeaf = propertyPath.endsWith('[]') && !propertyPath.endsWith('.from[]');
            if (isLeaf) {
                const parts = value.replace(/#.+$/, '').split('|');
                structuredReferences.push({kind: 'spell', uid: value, owner, locator});
                if (parts[1]) {
                    addSource(parts[1], 'additionalSpellUid', locator);
                }
            } else if (propertyPath.endsWith('.choose') || propertyPath.endsWith('.all')) {
                selectors.push({kind: 'spell', expression: value, owner, locator,
                    mode: propertyPath.endsWith('.choose') ? 'choose' : 'all'});
            }
        }
    }
}
records.forEach(record => scanAdditionalReferences(record.data, record.locator, record.locator));

const catalogFiles = {
    item: [['items.json', 'item'], ['items-base.json', 'baseitem']],
    optionalfeature: [['optionalfeatures.json', 'optionalfeature']],
    feat: [['feats.json', 'feat']], skill: [['skills.json', 'skill']],
    language: [['languages.json', 'language']], action: [['actions.json', 'action']],
    sense: [['senses.json', 'sense']], condition: [['conditionsdiseases.json', 'condition']],
    background: [['backgrounds.json', 'background']], race: [['races.json', 'race']],
    creature: walkFiles(path.join(dataDirectory, 'monsters'))
        .filter(filename => filename.endsWith('.json'))
        .map(filename => [path.relative(dataDirectory, filename), 'monster']),
};
const foreignIndexes = new Map();
for (const [kind, locations] of Object.entries(catalogFiles)) {
    const index = new Map();
    for (const [filename, collection] of locations) {
        const root = JSON.parse(fs.readFileSync(path.join(dataDirectory, filename), 'utf8').replace(/^\uFEFF/, ''));
        for (const definition of root[collection] ?? []) {
            const key = tuple([definition.name, definition.source]);
            index.set(key, (index.get(key) ?? 0) + 1);
        }
    }
    foreignIndexes.set(kind, index);
}
const foreignResolution = structuredReferences.filter(reference => !['classFeature', 'subclassFeature'].includes(reference.kind))
    .map(reference => {
        const parts = reference.uid.replace(/#.+$/, '').split('|');
        const defaultSource = reference.kind === 'optionalfeature' ? 'PHB' : null;
        const source = parts[1] || defaultSource;
        const matches = source ? foreignIndexes.get(reference.kind)?.get(tuple([parts[0], source])) ?? 0 : 0;
        let status = resolutionStatus(matches);
        if (reference.kind === 'spell') {
            status = 'deferredNoSpellCatalog';
        } else if (!source) {
            status = 'unqualified';
        }
        return {...reference, source, sourceExplicit: !!parts[1], matches, status};
    });
const itemGroups = JSON.parse(fs.readFileSync(path.join(dataDirectory, 'items.json'), 'utf8')).itemGroup;
for (const reference of foreignResolution.filter(reference => reference.kind === 'item' && reference.status === 'unresolved')) {
    const [name, source] = reference.uid.split('|');
    reference.auxiliaryItemGroupMatches = itemGroups.filter(group => tuple([group.name, group.source]) === tuple([name, source])).length;
}

const registry = JSON.parse(fs.readFileSync(path.join(dataDirectory, 'sources/registry.json'), 'utf8')
    .replace(/^\uFEFF/, '')).sources;
const registryIndex = new Map();
const registryConflicts = [];
for (const source of registry) {
    for (const code of [source.code, ...(source.aliases ?? [])]) {
        const key = fold(code);
        if (registryIndex.has(key) && registryIndex.get(key) !== source.code) {
            registryConflicts.push({code, targets: [registryIndex.get(key), source.code]});
        }
        registryIndex.set(key, source.code);
    }
}
const sourceCoverage = [...allSources].sort(([left], [right]) => left.localeCompare(right))
    .map(([code, occurrences]) => ({code, canonicalCode: registryIndex.get(fold(code)) ?? null,
        occurrences: occurrences.length, categories: counter(occurrences.map(value => value.category)),
        examples: occurrences.slice(0, 3)}));

const fieldInventory = [...fields.values()].sort((left, right) => left.path.localeCompare(right.path));
for (const stat of fieldInventory) {
    const separator = stat.path.lastIndexOf('.');
    const parentPath = separator >= 0 ? stat.path.slice(0, separator) : null;
    const parent = parentPath ? fields.get(parentPath) : null;
    const objectProperty = !stat.path.endsWith('[]') && parent !== null;
    stat.parentObjectOccurrences = objectProperty ? parent?.types.object ?? null : null;
    stat.missingFromParentObjects = objectProperty ? Math.max(0, (parent?.types.object ?? 0) - stat.occurrences) : null;
    stat.optional = objectProperty ? stat.missingFromParentObjects > 0 : null;
    stat.structuralVariants = Object.entries(stat.shapes).map(([keys, count]) => ({keys: keys ? keys.split(',') : [], count}));
    delete stat.shapes;
}

const summary = {
    discoveryComplete: true, failedFiles: [], fileCount: files.length,
    collectionCounts: counter(records.map(record => record.kind)),
    metadataCount: files.filter(file => file.roots?._meta).length,
    fieldPathCount: fieldInventory.length,
    byKindAndSource: Object.fromEntries(kinds.map(kind => [kind,
        counter(records.filter(record => record.kind === kind).map(record => record.data.source))])),
    rootFields: Object.fromEntries(kinds.map(kind => [kind,
        counter(records.filter(record => record.kind === kind).flatMap(record => Object.keys(record.data)))])),
    duplicateIdentities: collisions(record => record.identity),
    proposedIdCollisions: collisions(proposedId),
    nameSourceCollisions: collisions(record => tuple([record.data.name, record.data.source])),
    subclassShortNameCollisions: collisions(record => record.kind === 'subclass'
        ? tuple([record.data.className, record.data.classSource, record.data.shortName, record.data.source])
        : record.identity),
    edgeCounts: counter(edges.map(edge => `${edge.relation}:${edge.status}`)),
    directlyReferencedUniqueFeatures: directlyReached.size,
    reachableUniqueFeatures: records.filter(record => ['classFeature', 'subclassFeature'].includes(record.kind)
        && reachable.has(record.locator)).length,
    orphanCounts: counter(orphans.map(record => record.kind)),
    cycles: structuralCycles, cyclesIncludingMarkup,
    reprintResolutionCounts: counter(reprintEdges.map(edge => `${edge.kind}:${edge.status}`)),
    directiveCounts: counter(directives.map(directive => `${directive.kind}:${directive.directive}`)),
    markupTagCounts: counter(markup.map(reference => reference.tag)),
    structuredReferenceCounts: counter(structuredReferences.map(reference => reference.kind)),
    distinctSourceSpellings: sourceCoverage.length,
    distinctCanonicalSources: new Set(sourceCoverage.filter(source => source.canonicalCode).map(source => source.canonicalCode)).size,
    missingSources: sourceCoverage.filter(source => !source.canonicalCode), registryConflicts, invalidSources,
    foreignResolutionCounts: counter(foreignResolution.map(reference => `${reference.kind}:${reference.status}`)),
    registryIdentityCount: registry.length,
    identityCollisionCountsByKind: counter(collisions(record => tuple([record.data.name, record.data.source]))
        .map(collision => collision.key.split(':')[0])),
    edgeCountsByOwnerKind: counter(edges.map(edge => `${edge.relation}:${byLocator.get(edge.owner)?.kind}:${edge.targetKind}`)),
    crossFileEdgeCount: edges.filter(edge => edge.targets.some(target => target.split('#')[0] !== edge.owner.split('#')[0])).length,
};
writeJson('source-evidence.json', {summary, files,
    definitions: records.map(record => ({kind: record.kind, locator: record.locator,
        identity: record.identity, proposedId: proposedId(record), rootFields: Object.keys(record.data)}))});
writeJson('normalized-field-inventory.json', {
    scope: 'Source shapes only, not expanded or normalized definitions. Every nested path is traversed without recursion truncation.',
    counts: summary.collectionCounts, fileCount: files.length, metadataCount: summary.metadataCount,
    pathCount: fieldInventory.length,
    counting: 'Occurrences count values. [] aggregates array positions. Missing counts use observed parent objects. Source and edition distributions refer to owning roots, never inferred editions. Shapes enumerate every distinct object key set. Examples are bounded; statistics are not.',
    classification: 'Path categories are conservative source destinations; descriptive structures can contain explicit references. Human architecture documents specify semantic projections.',
    fields: fieldInventory,
});
writeJson('feature-graph.json', {summary: summary.edgeCounts, edges, reprintEdges, orphans,
    cycles: structuralCycles, cyclesIncludingMarkup});
writeJson('inheritance-evidence.json', {directives});
writeJson('cross-catalog-references.json', {markup, structured: structuredReferences, selectors, foreignResolution});
writeJson('mechanics-evidence.json', {
    classesAndSubclasses: records.filter(record => ['class', 'subclass'].includes(record.kind))
        .map(record => ({kind: record.kind, locator: record.locator,
            data: Object.fromEntries(Object.entries(record.data).filter(([key]) => !['fluff'].includes(key)))})),
    entryTypes: Object.fromEntries([...objectsByDiscriminator].map(([type, occurrences]) => [type, {
        count: occurrences.length,
        shapes: counter(occurrences.map(occurrence => occurrence.keys.join(','))),
        examples: occurrences.slice(0, 2),
    }])),
});
writeJson('source-registry-coverage.json', {sources: sourceCoverage, conflicts: registryConflicts,
    note: 'Includes explicitly written UID source slots, never treats a class name as spellcasting evidence or prose as a relationship.'});
writeJson('discovery-summary.json', {...summary, nameSourceCollisions: summary.nameSourceCollisions.length});
console.log(`Discovered ${files.length} files, ${records.length} definitions, ${fieldInventory.length} paths; `
    + `${summary.missingSources.length} missing source codes.`);
