// Read-only source census for ROADMAP Step 0. Outputs are discovery evidence, not normalized data.
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');

const root = path.resolve(__dirname, '../..');
const dataRoot = path.join(root, 'src/main/resources/data');
const relative = file => path.relative(dataRoot, file).replaceAll('\\', '/');
const increment = (object, key, amount = 1) => { object[key] = (object[key] || 0) + amount; };
const sorted = object => Object.fromEntries(Object.entries(object).sort(([a], [b]) => a.localeCompare(b)));
const lower = value => String(value).toLowerCase();
const files = directory => fs.readdirSync(directory, { withFileTypes: true })
    .flatMap(entry => entry.isDirectory() ? files(path.join(directory, entry.name))
        : [path.join(directory, entry.name)]).sort();

function domainFor(file) {
    if (file.startsWith('sources/')) return 'metadata';
    if (file.startsWith('spells/')) return 'spells';
    if (file.startsWith('monsters/')) return 'monsters';
    if (file.startsWith('classes/')) return 'classes';
    return file.replace('.json', '');
}

const inventory = [];
const entities = [];
const tags = {};
const properties = {};
const structures = {};
const documents = [];
const inputHash = crypto.createHash('sha256');
for (const file of files(dataRoot)) {
    const bytes = fs.readFileSync(file);
    const name = relative(file);
    const hash = crypto.createHash('sha256').update(bytes).digest('hex');
    inputHash.update(`${name}\0${hash}\n`);
    const document = JSON.parse(bytes.toString('utf8').replace(/^\uFEFF/, ''));
    const domain = domainFor(name);
    const counts = {};
    for (const [type, values] of Object.entries(document)) {
        if (Array.isArray(values)) {
            counts[type] = values.length;
            for (const [index, value] of values.entries()) {
                const entity = { file: name, domain, type, index, value, key: `${name}#/${type}/${index}` };
                entities.push(entity);
                if (value && typeof value === 'object') {
                    for (const property of Object.keys(value)) {
                        properties[type] ||= {};
                        increment(properties[type], property);
                    }
                }
            }
        }
    }
    inventory.push({ file: name, domain, bytes: bytes.length, sha256: hash, arrays: counts,
        otherKeys: Object.keys(document).filter(key => !Array.isArray(document[key])) });
    documents.push({ file: name, domain, document });
}

const occurrences = [];
const structured = [];
const entryReferences = [];
function walk(value, location, owner, parentKey = '') {
    if (typeof value === 'string') {
        for (const match of value.matchAll(/\{#itemEntry\s+([^{}]+)\}/g)) {
            entryReferences.push({ location, owner: owner.key, domain: owner.domain, body: match[1] });
        }
        const pattern = /\{@([^\s{}]+)(?:\s|(?=\}))/g;
        for (const match of value.matchAll(pattern)) {
            let depth = 1;
            let end = match.index + 2;
            while (end < value.length && depth > 0) {
                if (value[end] === '{') depth++;
                if (value[end] === '}') depth--;
                end++;
            }
            const tag = match[1];
            const raw = value.slice(match.index, end);
            const body = raw.slice(2 + tag.length, -1).trim();
            tags[tag] ||= { count: 0, domains: {}, examples: [] };
            tags[tag].count++;
            increment(tags[tag].domains, owner.domain);
            if (tags[tag].examples.length < 3) tags[tag].examples.push({ location, raw });
            occurrences.push({ tag, body, raw, location, owner: owner.key, domain: owner.domain });
        }
        return;
    }
    if (!value || typeof value !== 'object') return;
    if (Array.isArray(value)) {
        value.forEach((child, index) => walk(child, `${location}/${index}`, owner, parentKey));
        return;
    }
    if (typeof value.type === 'string') {
        structures[value.type] ||= {};
        increment(structures[value.type], owner.domain);
    }
    for (const [key, child] of Object.entries(value)) {
        if (['_copy', '_meta', '_mod', '_templates', 'inherits', 'requires', 'excludes', 'baseItem',
            'legendaryGroup', 'classFeatures', 'subclassFeatures', 'classFeature', 'subclassFeature',
            'optionalfeature', 'additionalSpells', 'spellcasting', 'attachedSpells', 'attachedItems',
            'item', 'items', 'race', 'class', 'subclass', 'feat', 'feature', 'otherSources',
            'reprintedAs', 'seeAlsoAction', 'seeAlsoSpell', 'seeAlsoFeature', 'seeAlsoDeck',
            'weaponProficiencies', 'armorProficiencies', 'startingEquipment', 'prerequisite',
            'optionalfeatureProgression', 'languageProficiencies', 'skillProficiencies',
            'conditionImmune', 'conditionInflict', 'mastery', 'type', 'property', 'packContents',
            'parentName', 'parentSource', 'className', 'classSource', 'subclassShortName',
            'subclassSource', 'srd', 'srd52', '_versions', 'fromVariant', 'featProgression',
            'signatureSpells', 'summonedBySpell', 'summonedByClass', 'script', 'optionalfeatures',
            'languageTags', 'skill', 'languageProficiencies'].includes(key)) {
            structured.push({ key, location: `${location}/${key}`, owner: owner.key, domain: owner.domain,
                value: child });
        }
        walk(child, `${location}/${key}`, owner, key);
    }
}
for (const entity of entities) walk(entity.value, entity.key, entity);
for (const { file, domain, document } of documents) {
    for (const [key, value] of Object.entries(document)) {
        if (!Array.isArray(value)) walk(value, `${file}#/${key}`, { key: `${file}#`, domain });
    }
}

const copies = entities.filter(entity => entity.value?._copy).map(entity => {
    const copy = entity.value._copy;
    const identityFields = Object.keys(copy).filter(key => !key.startsWith('_'));
    const matches = entities.filter(candidate => candidate.type === entity.type
        && identityFields.every(key => lower(key === 'source'
            ? candidate.value?.source || candidate.value?.inherits?.source : candidate.value?.[key]) === lower(copy[key])));
    return { owner: entity.key, domain: entity.domain, name: entity.value.name, type: entity.type,
        target: Object.fromEntries(identityFields.map(key => [key, copy[key]])),
        matches: matches.map(candidate => candidate.key), operations: Object.keys(copy).filter(key => key.startsWith('_')) };
});

const targetTypes = {
    action: ['action'], variantrule: ['variantrule'], skill: ['skill'], condition: ['condition'],
    disease: ['disease'], status: ['status'], sense: ['sense'], language: ['language'],
    spell: ['spell'], creature: ['monster'], class: ['class'], subclass: ['subclass'],
    classFeature: ['classFeature'], subclassFeature: ['subclassFeature'],
    optfeature: ['optionalfeature'], feat: ['feat'], background: ['background'], race: ['race', 'subrace'],
    item: ['item', 'baseitem', 'itemGroup', 'magicvariant'], itemProperty: ['itemProperty'],
    itemMastery: ['itemMastery'], object: ['object'], vehicle: ['vehicle'], vehupgrade: ['vehicleUpgrade'],
    reward: ['reward'], deity: ['deity'], facility: ['facility'], recipe: ['recipe'],
    trap: ['trap'], hazard: ['hazard'], legroup: ['legendaryGroup'],
    book: ['book'], adventure: ['adventure'], table: [], deck: [], card: [], quickref: []
};
const tagTargets = {
    action: 'actions', variantrule: 'variantrules', skill: 'skills', condition: 'conditionsdiseases',
    disease: 'conditionsdiseases', status: 'conditionsdiseases', sense: 'senses', language: 'languages',
    spell: 'spells', creature: 'monsters', legroup: 'monsters', class: 'classes', subclass: 'classes',
    classFeature: 'classes', subclassFeature: 'classes', optfeature: 'optionalfeatures', feat: 'feats',
    background: 'backgrounds', race: 'races', item: 'equipment', itemProperty: 'equipment',
    itemMastery: 'equipment', object: 'objects', vehicle: 'vehicles', vehupgrade: 'vehicles',
    reward: 'rewards', deity: 'deities', facility: 'bastions', recipe: 'recipes', trap: 'trapshazards',
    hazard: 'trapshazards', book: 'books', adventure: 'adventures', table: 'missing-tables',
    deck: 'missing-decks', card: 'missing-decks', quickref: 'missing-quickref'
};
const normalizeDomain = domain => ['items', 'items-base', 'magicvariants'].includes(domain) ? 'equipment' : domain;
const byType = {};
for (const entity of entities) (byType[entity.type] ||= []).push(entity);
const sourceOf = entity => entity.value?.source || entity.value?.inherits?.source;
const nameOf = entity => entity.value?.abbreviation || entity.value?.name;
const tagChecks = {};
const referenceMatrix = {};
const missingReferenceGroups = {};
const resolvedReferenceEdges = [];
for (const occurrence of occurrences) {
    const { tag, body, location, owner, domain } = occurrence;
    if (!Object.hasOwn(targetTypes, tag)) continue;
    const from = normalizeDomain(domain);
    const to = tagTargets[tag];
    referenceMatrix[from] ||= {};
    increment(referenceMatrix[from], to);
    tagChecks[tag] ||= { total: 0, exactUnique: 0, missing: 0, ambiguous: 0,
        omittedSource: 0, missingCatalog: 0, compoundDeferred: 0 };
    const check = tagChecks[tag];
    check.total++;
    const parts = body.split('|');
    let matches = [];
    let state;
    if (targetTypes[tag].length === 0) {
        state = 'missingCatalog';
    } else if (['subclass', 'classFeature', 'subclassFeature'].includes(tag)) {
        const expected = tag === 'subclass' ? 4 : tag === 'classFeature' ? 5 : 7;
        if (parts.length < expected || parts.slice(0, expected).some(part => !part)) {
            state = 'compoundDeferred';
        } else {
            const [name, className, classSource] = parts;
            const identity = tag === 'subclass' ? { shortName: name, className, classSource, source: parts[3] }
                : tag === 'classFeature' ? { name, className, classSource, level: parts[3], source: parts[4] }
                    : { name, className, classSource, subclassShortName: parts[3], subclassSource: parts[4],
                        level: parts[5], source: parts[6] };
            matches = (byType[tag] || []).filter(entity => Object.entries(identity)
                .every(([key, expectedValue]) => lower(entity.value[key]) === lower(expectedValue)));
            state = matches.length === 1 ? 'exactUnique' : matches.length ? 'ambiguous' : 'missing';
        }
    } else {
        const name = ['book', 'adventure'].includes(tag) ? parts[1] : parts[0];
        const source = ['book', 'adventure'].includes(tag) ? name : tag === 'deity' ? parts[2] : parts[1];
        const candidates = targetTypes[tag].flatMap(type => byType[type] || []);
        if (!source) {
            state = 'omittedSource';
        } else {
            matches = candidates.filter(entity => ['book', 'adventure'].includes(tag)
                ? lower(entity.value.id) === lower(name)
                : lower(nameOf(entity)) === lower(name) && lower(sourceOf(entity)) === lower(source)
                    && (tag !== 'deity' || lower(entity.value.pantheon) === lower(parts[1])));
            state = matches.length === 1 ? 'exactUnique' : matches.length ? 'ambiguous' : 'missing';
        }
    }
    check[state]++;
    if (state === 'exactUnique') resolvedReferenceEdges.push([owner, matches[0].key]);
    if (['missing', 'ambiguous', 'missingCatalog'].includes(state)) {
        const key = `${tag}|${body}`;
        missingReferenceGroups[key] ||= { tag, body, state, count: 0, locations: [],
            matches: matches.map(entity => entity.key) };
        const group = missingReferenceGroups[key];
        group.count++;
        if (group.locations.length < 3) group.locations.push(location);
    }
}

function stronglyConnected(edges) {
    const adjacency = new Map();
    for (const [from, to] of edges) {
        if (!adjacency.has(from)) adjacency.set(from, new Set());
        if (!adjacency.has(to)) adjacency.set(to, new Set());
        adjacency.get(from).add(to);
    }
    let next = 0;
    const stack = [];
    const indices = new Map();
    const low = new Map();
    const onStack = new Set();
    const components = [];
    function visit(node) {
        indices.set(node, next);
        low.set(node, next++);
        stack.push(node);
        onStack.add(node);
        for (const target of adjacency.get(node)) {
            if (!indices.has(target)) {
                visit(target);
                low.set(node, Math.min(low.get(node), low.get(target)));
            } else if (onStack.has(target)) low.set(node, Math.min(low.get(node), indices.get(target)));
        }
        if (low.get(node) === indices.get(node)) {
            const component = [];
            let member;
            do {
                member = stack.pop();
                onStack.delete(member);
                component.push(member);
            } while (member !== node);
            if (component.length > 1 || adjacency.get(node).has(node)) components.push(component.sort());
        }
    }
    for (const node of adjacency.keys()) if (!indices.has(node)) visit(node);
    return components.sort((a, b) => b.length - a.length);
}
const copyEdges = copies.filter(copy => copy.matches.length === 1).map(copy => [copy.owner, copy.matches[0]]);
const copyByOwner = new Map(copies.map(copy => [copy.owner, copy]));
const copyChains = copies.map(copy => {
    const chain = [copy.owner];
    let current = copy;
    const visited = new Set(chain);
    while (current?.matches.length === 1 && !visited.has(current.matches[0])) {
        const target = current.matches[0];
        chain.push(target);
        visited.add(target);
        current = copyByOwner.get(target);
    }
    return chain;
}).sort((a, b) => b.length - a.length);

const structuredCounts = {};
for (const occurrence of structured) {
    structuredCounts[occurrence.key] ||= {};
    increment(structuredCounts[occurrence.key], occurrence.domain);
}
const structuredReferences = [];
function checkStructured(owner, location, kind, types, identity) {
    const candidates = types.flatMap(type => byType[type] || []);
    const matches = candidates.filter(candidate => Object.entries(identity)
        .every(([key, value]) => lower(candidate.value?.[key]) === lower(value)));
    const declaredVersionMatches = kind === 'legendaryGroup' && !matches.length
        ? candidates.flatMap(candidate => (candidate.value._versions || []).map((version, index) => ({
            key: `${candidate.key}/_versions/${index}`, value: version
        }))).filter(candidate => Object.entries(identity)
            .every(([key, expected]) => lower(candidate.value[key]) === lower(expected))).map(candidate => candidate.key)
        : [];
    structuredReferences.push({ owner, location, kind, identity,
        matches: matches.map(candidate => candidate.key),
        ...(declaredVersionMatches.length ? { declaredVersionMatches } : {}) });
}
for (const entity of entities) {
    const value = entity.value;
    if (!value || typeof value !== 'object') continue;
    if (value._copy?._templates) for (const [index, template] of value._copy._templates.entries()) {
        checkStructured(entity.key, `${entity.key}/_copy/_templates/${index}`, 'template',
            ['monsterTemplate'], template);
    }
    if (entity.type === 'subrace') checkStructured(entity.key, entity.key, 'subraceParent', ['race'],
        { name: value.raceName || value._copy?.raceName, source: value.raceSource || value._copy?.raceSource });
    if (entity.type === 'subclass') checkStructured(entity.key, entity.key, 'subclassParent', ['class'],
        { name: value.className, source: value.classSource });
    if (['classFeature', 'subclassFeature'].includes(entity.type)) {
        checkStructured(entity.key, entity.key, 'featureClassParent', ['class'],
            { name: value.className, source: value.classSource });
    }
}
for (const occurrence of structured) {
    const { key, value, owner, location } = occurrence;
    if (key === 'legendaryGroup' && value && typeof value === 'object' && value.name && value.source) {
        checkStructured(owner, location, 'legendaryGroup', ['legendaryGroup'], value);
    }
    if (key === 'baseItem' && typeof value === 'string') {
        const [name, source] = value.split('|');
        if (source) checkStructured(owner, location, 'baseItem', ['baseitem', 'item'], { name, source });
    }
    if (key === '_templates' && !location.includes('/_copy/')) {
        for (const [index, template] of value.entries()) checkStructured(owner, `${location}/${index}`,
            'versionTemplate', owner.includes('#/legendaryGroup/') ? ['legendaryGroupTemplate'] : ['monsterTemplate'],
            template);
    }
}

const uidChecks = { explicit: [], deferred: {} };
for (const occurrence of structured) {
    if (!['classFeatures', 'subclassFeatures', 'classFeature', 'subclassFeature'].includes(occurrence.key)) continue;
    const subclass = occurrence.key.startsWith('subclass');
    const type = subclass ? 'subclassFeature' : 'classFeature';
    const values = Array.isArray(occurrence.value) ? occurrence.value : [occurrence.value];
    for (let index = 0; index < values.length; index++) {
        const value = typeof values[index] === 'string' ? values[index] : values[index]?.[type];
        if (typeof value !== 'string') continue;
        const parts = value.split('|');
        if (parts.length < (subclass ? 7 : 5) || parts.some(part => !part)) {
            increment(uidChecks.deferred, type);
            continue;
        }
        const [name, className, classSource] = parts;
        const identity = subclass ? { name, className, classSource, subclassShortName: parts[3],
            subclassSource: parts[4], level: parts[5], source: parts[6] }
            : { name, className, classSource, level: parts[3], source: parts[4] };
        const matches = (byType[type] || []).filter(entity => Object.entries(identity)
            .every(([key, expected]) => lower(entity.value[key]) === lower(expected)));
        uidChecks.explicit.push({ location: `${occurrence.location}/${index}`, type, value,
            matches: matches.map(entity => entity.key) });
    }
}

const registry = new Set((byType.sources || []).map(entity => lower(entity.value.code)));
const books = new Set((byType.book || []).map(entity => lower(entity.value.source)));
const adventures = new Set((byType.adventure || []).map(entity => lower(entity.value.source)));
const sourceUsage = {};
for (const entity of entities) {
    const source = sourceOf(entity);
    if (!source) continue;
    sourceUsage[lower(source)] ||= { code: source, count: 0, domains: {}, examples: [] };
    const use = sourceUsage[lower(source)];
    use.count++;
    increment(use.domains, entity.domain);
    if (use.examples.length < 2) use.examples.push(entity.key);
}
const metadata = documents.filter(document => document.document._meta)
    .map(document => ({ file: document.file, value: document.document._meta }));
const metaDependencyChecks = metadata.flatMap(document => Object.entries(document.value.dependencies || {})
    .flatMap(([type, sources]) => sources.map(source => ({ file: document.file, type, source,
        targetMembers: (byType[type] || []).filter(entity => lower(sourceOf(entity)) === lower(source)).length }))));
const duplicateIdentities = {};
const simpleIdentityGroups = {};
for (const entity of entities) {
    if (!entity.value?.name || !sourceOf(entity)) continue;
    const simpleIdentity = [entity.type, sourceOf(entity), entity.value.name].map(lower).join('|');
    (simpleIdentityGroups[simpleIdentity] ||= []).push(entity.key);
    const fields = ['type', 'source', 'name'];
    const value = { ...entity.value, type: entity.type, source: sourceOf(entity) };
    if (entity.type === 'deity') fields.push('pantheon');
    if (['classFeature', 'subclassFeature', 'subclass'].includes(entity.type)) fields.push('className', 'classSource');
    if (['classFeature', 'subclassFeature'].includes(entity.type)) fields.push('level');
    if (entity.type === 'subclassFeature') fields.push('subclassShortName', 'subclassSource');
    if (entity.type === 'subrace') fields.push('raceName', 'raceSource');
    const key = fields.map(field => lower(value[field])).join('|');
    (duplicateIdentities[key] ||= []).push(entity.key);
}
const ruleTypes = {};
for (const entity of byType.variantrule) increment(ruleTypes, entity.value.ruleType || '(absent)');
const selectors = {};
for (const occurrence of occurrences.filter(value => ['filter', '5etools'].includes(value.tag))) {
    const target = occurrence.body.split('|')[1];
    selectors[occurrence.tag] ||= {};
    selectors[occurrence.tag][target] ||= { count: 0, domains: {}, examples: [] };
    const selector = selectors[occurrence.tag][target];
    selector.count++;
    increment(selector.domains, occurrence.domain);
    if (selector.examples.length < 2) selector.examples.push({ location: occurrence.location, raw: occurrence.raw });
}
const stepFor = domain => {
    if (['books', 'actions', 'variantrules', 'metadata'].includes(domain)) return '1';
    if (['skills', 'conditionsdiseases', 'senses', 'languages'].includes(domain)) return '2';
    if (domain === 'spells') return '3';
    if (domain === 'monsters') return '4';
    if (domain === 'classes') return '5';
    if (['races', 'feats', 'backgrounds', 'optionalfeatures', 'names'].includes(domain)) return '6';
    if (['equipment', 'loot', 'rewards', 'encounters', 'recipes', 'deities', 'cultsboons', 'objects',
        'vehicles', 'trapshazards'].includes(domain)) return '7';
    return 'Z/gap';
};
const stepMatrix = {};
for (const [from, targets] of Object.entries(referenceMatrix)) {
    const fromStep = stepFor(from);
    stepMatrix[fromStep] ||= {};
    for (const [to, count] of Object.entries(targets)) increment(stepMatrix[fromStep], stepFor(to), count);
}
const summary = { methodVersion: 5, corpusSha256: inputHash.digest('hex'),
    files: inventory.length, bytes: inventory.reduce((sum, file) => sum + file.bytes, 0),
    topLevelArrayMembers: entities.length,
    inventory, properties: sorted(properties), tags: sorted(tags), structures: sorted(structures), copies,
    ruleTypes, metadata, metaDependencyChecks, entryReferences, uidChecks, selectors, stepMatrix,
    sourceCoverage: { registry: registry.size, books: books.size, adventures: adventures.size,
        usedPrimaryCodes: Object.keys(sourceUsage).length,
        absentFromBooks: Object.entries(sourceUsage).filter(([code]) => !books.has(code)).map(([, use]) => use),
        absentFromBooksAndAdventures: Object.entries(sourceUsage)
            .filter(([code]) => !books.has(code) && !adventures.has(code)).map(([, use]) => use),
        absentFromRegistry: Object.entries(sourceUsage).filter(([code]) => !registry.has(code)).map(([, use]) => use) },
    tagChecks, referenceMatrix: sorted(referenceMatrix), missingReferenceGroups: Object.values(missingReferenceGroups),
    structuredCounts, structuredReferences,
    duplicateIdentities: Object.fromEntries(Object.entries(duplicateIdentities).filter(([, values]) => values.length > 1)),
    simpleIdentityCollisions: Object.fromEntries(Object.entries(simpleIdentityGroups)
        .filter(([, values]) => values.length > 1)),
    copyCycles: stronglyConnected(copyEdges), longestCopyChains: copyChains.slice(0, 10),
    explicitTagCycles: stronglyConnected(resolvedReferenceEdges),
    domainCycles: stronglyConnected(Object.entries(referenceMatrix)
        .flatMap(([from, targets]) => Object.keys(targets).filter(to => to !== from).map(to => [from, to]))) };
fs.writeFileSync(path.join(root, 'tools/evidence/step-0/dependency-evidence.json'), `${JSON.stringify(summary, null, 4)}\n`);
const table = (headers, rows) => [
    `| ${headers.join(' | ')} |`, `| ${headers.map(() => '---').join(' | ')} |`,
    ...rows.map(row => `| ${row.map(value => String(value).replaceAll('|', '\\|')).join(' | ')} |`)
].join('\n');
const groupedInventory = {};
for (const file of inventory) {
    groupedInventory[file.domain] ||= { files: 0, counts: {} };
    const group = groupedInventory[file.domain];
    group.files++;
    for (const [type, count] of Object.entries(file.arrays)) increment(group.counts, type, count);
}
const groups = ['1', '2', '3', '4', '5', '6', '7', 'Z/gap'];
const matrixRows = [...new Set(inventory.map(file => normalizeDomain(file.domain)))].sort().map(domain => {
    const counts = {};
    for (const [target, count] of Object.entries(referenceMatrix[domain] || {})) increment(counts, stepFor(target), count);
    return [domain, ...groups.map(group => counts[group] || 0)];
});
const tableText = [
    '# Step 0 dependency tables',
    'Generated by `node tools/scripts/step-0/analyze-dependencies.cjs`. See [the dependency analysis report](../../reports/step-0/STEP_0_DEPENDENCY_ANALYSIS.md) for interpretation and limitations.',
    '## Source inventory',
    'Counts are top-level array members, including metadata, templates and table containers; they are not counts of canonical entities or expanded variants.',
    table(['Domain', 'Files', 'Top-level arrays and member counts'], Object.entries(groupedInventory).sort()
        .map(([domain, group]) => [domain, group.files, Object.entries(group.counts)
            .map(([type, count]) => `${type}: ${count}`).join('; ')])),
    '## Cross-domain matrix',
    'Each row is a referencing source domain. Columns are target roadmap steps. Values count occurrences of entity/rule tags, including nested and repeated tags, not unique relationships. Equipment combines items, items-base and magicvariants. A zero means no counted tag, not no semantic or structured dependency. Source metadata fields, filter/page selectors, untagged prose, UID fields, copies and item-entry directives are excluded. Books and adventures tags validate metadata identities, not the referenced chapter text.',
    'Target columns: 1 = books/actions/variantrules; 2 = skills/conditions/diseases/statuses/senses/languages; 3 = spells; 4 = monsters; 5 = classes/features; 6 = character building; 7 = world building; Z/gap = bastions or missing tables/decks/cards/quickref catalogs. Adventures target metadata is assigned to Z/gap because the existing roadmap does not assign it to Step 1.',
    table(['Referencing domain', ...groups], matrixRows),
    '## Matrix aggregated by roadmap step',
    'Metadata registry is grouped with Step 1 for this aggregation; adventures, bastions and homecrafts are grouped with Z/gap. All currently unassigned sources are inventoried explicitly above.',
    table(['From / to', ...groups], groups.map(group => [group,
        ...groups.map(target => stepMatrix[group]?.[target] || 0)])),
    '## Entity and rule tag checks',
    'Exact = a unique literal source identity in the available top-level arrays; case is ignored. Item checks also inspect explicit inherits.source. No omitted-source default, alias, reprint, subrace display-name composition or generated variant is resolved. Compound identities with omitted components remain deferred. Missing direct matches are investigation items, not confirmed broken source links. Missing catalogs may have related inline material, which has not been assigned an invented catalog identity.',
    table(['Tag', 'Occurrences', 'Exact', 'No direct match', 'Ambiguous', 'Source omitted', 'Catalog absent', 'Compound deferred'],
        Object.entries(tagChecks).sort().map(([tag, count]) => [tag, count.total, count.exactUnique,
            count.missing, count.ambiguous, count.omittedSource, count.missingCatalog, count.compoundDeferred])),
    '## All observed tag types',
    table(['Tag', 'Occurrences'], Object.entries(tags).sort().map(([tag, value]) => [tag, value.count])),
    '## Copy declarations',
    'All copy targets are matched against the same source array type and every supplied non-control identity field. For magicvariant, source may be explicitly stored in inherits.source. This verifies target presence, not successful application of copy operations.',
    table(['Array type', 'Copies'], Object.entries(copies.reduce((counts, copy) => {
        increment(counts, copy.type); return counts;
    }, {})).sort()),
    '## Selectors and page links',
    'Selectors describe candidate sets or pages; they are not resolved entity IDs and are excluded from the numeric reference matrix.',
    table(['Tag', 'Target page', 'Occurrences', 'Referencing domains'], Object.entries(selectors)
        .flatMap(([tag, targets]) => Object.entries(targets).map(([target, value]) => [tag, target,
            value.count, Object.entries(value.domains).map(([domain, count]) => `${domain}: ${count}`).join('; ')])))
].join('\n\n') + '\n';
fs.writeFileSync(path.join(root, 'reports/step-0/dependency-tables.md'), tableText);
console.log(JSON.stringify({ files: summary.files, bytes: summary.bytes, members: entities.length,
    corpusSha256: summary.corpusSha256, tags: occurrences.length, tagTypes: Object.keys(tags).length,
    referenceTags: Object.values(tagChecks).reduce((sum, check) => sum + check.total, 0),
    ruleTypes, primarySources: summary.sourceCoverage.usedPrimaryCodes,
    sourcesAbsentFromBooks: summary.sourceCoverage.absentFromBooks.length,
    sourcesAbsentFromBooksAndAdventures: summary.sourceCoverage.absentFromBooksAndAdventures.map(value => value.code),
    sourcesAbsentFromRegistry: summary.sourceCoverage.absentFromRegistry,
    missingMetaDependencies: metaDependencyChecks.filter(check => check.targetMembers === 0),
    copyCycles: summary.copyCycles, longestCopyChain: copyChains[0], domainCycles: summary.domainCycles,
    copies: { total: copies.length, missing: copies.filter(copy => copy.matches.length === 0),
        ambiguous: copies.filter(copy => copy.matches.length > 1) }
}, null, 2));
