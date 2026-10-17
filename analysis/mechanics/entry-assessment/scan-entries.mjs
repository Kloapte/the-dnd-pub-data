import { createHash } from 'node:crypto';
import { readFile, readdir, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { reviews } from '../catalog-review.mjs';
import { concepts, nonGameplay, reviewedProse } from './mechanic-concepts.mjs';

// Analysis only: record-header inspection supplies field bindings, not a replacement importer.
const output = path.dirname(fileURLToPath(import.meta.url));
const rootDirectory = path.resolve(output, '../../..');
const relative = file => path.relative(rootDirectory, file).replaceAll('\\', '/');
const hash = bytes => createHash('sha256').update(bytes).digest('hex');
const increment = (object, key) => {
    object[key] = (object[key] ?? 0) + 1;
};
const sorted = object => Object.fromEntries(Object.entries(object).sort(([a], [b]) => a.localeCompare(b, 'en')));

async function files(directory) {
    const result = [];
    for (const child of (await readdir(directory, { withFileTypes: true })).sort((a, b) => a.name.localeCompare(b.name, 'en'))) {
        const file = path.join(directory, child.name);
        if (child.isDirectory()) {
            result.push(...await files(file));
        } else {
            result.push(file);
        }
    }
    return result;
}

function splitTopLevel(value) {
    const parts = [];
    let depth = 0;
    let start = 0;
    for (let index = 0; index < value.length; index += 1) {
        if ('<(['.includes(value[index])) {
            depth += 1;
        }
        if ('>)]'.includes(value[index])) {
            depth -= 1;
        }
        if (value[index] === ',' && depth === 0) {
            parts.push(value.slice(start, index).trim());
            start = index + 1;
        }
    }
    parts.push(value.slice(start).trim());
    return parts.filter(Boolean);
}

const javaFiles = (await files(path.join(rootDirectory, 'src/main/java'))).filter(file => file.endsWith('.java'));
const models = {};
for (const file of javaFiles) {
    const source = await readFile(file, 'utf8');
    const record = /public record (\w+)\s*\(/.exec(source);
    if (!record) {
        continue;
    }
    let end = record.index + record[0].length;
    let depth = 1;
    for (; end < source.length && depth; end += 1) {
        if (source[end] === '(') {
            depth += 1;
        }
        if (source[end] === ')') {
            depth -= 1;
        }
    }
    const header = source.slice(record.index + record[0].length, end - 1).replace(/\/\*[\s\S]*?\*\//g, '');
    const fields = splitTopLevel(header).map(component => {
        const property = /@(?:[\w.]*\.)?JsonProperty\("([^"]+)"\)/.exec(component)?.[1];
        const clean = component.replace(/@[\w.]+(?:\([^)]*\))?/g, '').trim();
        const match = /^([\s\S]+?)\s+(\w+)$/.exec(clean);
        if (!match) {
            throw new Error(`Unparsed record component: ${relative(file)}: ${component}`);
        }
        return { name: property ?? match[2], type: match[1].replaceAll(/\s/g, '').replaceAll('java.util.', '') };
    });
    models[record[1]] = { file: relative(file), line: source.slice(0, record.index).split('\n').length, fields };
}

const rootTypes = {
    action: 'ActionDte', adventure: 'AdventureDte', background: 'BackgroundDte', facility: 'FacilityDte',
    book: 'BookDte', charoption: 'CharacterCreationOptionDte', class: 'ClassDte', subclass: 'SubclassDte',
    classFeature: 'ClassFeatureDte', subclassFeature: 'SubclassFeatureDte', condition: 'ConditionDte',
    disease: 'DiseaseDte', cult: 'CultDte', boon: 'BoonDte', deity: 'DeityDte', encounter: 'EncounterDte',
    feat: 'FeatDte', item: 'ItemSourceDte', baseitem: 'ItemSourceDte', language: 'LanguageDte',
    magicvariant: 'MagicVariantDte', monster: 'MonsterDte', name: 'NameTableDte', object: 'ObjectSourceDte',
    optionalfeature: 'OptionalFeatureDte', race: 'RaceDte', subrace: 'SubraceDte', recipe: 'RecipeDte',
    reward: 'RewardDte', sense: 'SenseDte', skill: 'SkillDte', spell: 'SpellDte', trap: 'TrapDte',
    hazard: 'HazardDte', variantrule: 'VariantRuleDte', vehicle: 'VehicleDte', vehicleUpgrade: 'VehicleUpgradeDte',
    individual: 'IndividualTreasureDte', hoard: 'TreasureHoardDte', dragon: 'DragonTreasureDte',
    gems: 'GemTableDte', artObjects: 'ArtObjectTableDte', magicItems: 'MagicItemTableDte',
};
const commonDispatch = {
    entries: 'NamedEntryDte', itemSub: 'NamedEntryDte', variantSub: 'NamedEntryDte',
    variantInner: 'NamedEntryDte', itemSpell: 'NamedEntryDte', list: 'ListEntryDte', item: 'ListItemEntryDte',
    table: 'TableEntryDte', quote: 'QuoteEntryDte', inset: 'InsetEntryDte', section: 'SectionEntryDte',
    inline: 'InlineEntryDte', link: 'LinkEntryDte', image: 'ImageEntryDte',
    insetReadaloud: 'ReadaloudEntryDte', statblockInline: 'RewardInlineStatblockEntryDte',
};
const classDispatch = {
    entries: 'ClassBlockEntryDte', inset: 'ClassBlockEntryDte', quote: 'ClassBlockEntryDte',
    list: 'ClassBlockEntryDte', item: 'ClassBlockEntryDte', refClassFeature: 'ClassReferenceEntryDte',
    refSubclassFeature: 'ClassReferenceEntryDte', refOptionalfeature: 'ClassReferenceEntryDte',
    refFeat: 'ClassReferenceEntryDte', options: 'ClassOptionsEntryDte', abilityDc: 'ClassAbilityEntryDte',
    abilityAttackMod: 'ClassAbilityEntryDte', statblock: 'ClassStatblockEntryDte', table: 'ClassTableEntryDte',
};
const unions = {
    EntryDte: { text: 'TextEntryDte', object: commonDispatch },
    ClassEntryDte: { text: 'ClassTextEntryDte', object: classDispatch },
    ObjectActionContentDte: { text: 'ObjectActionTextDte', object: { attack: 'ObjectAttackDte', list: 'ObjectActionListDte' } },
    TrapStageDte: { text: 'TrapStageTextDte', anyObject: 'TrapStageGroupDte' },
    VehicleActionDte: { text: 'VehicleActionTextDte', anyObject: 'VehicleActionObjectDte' },
    RecipeIngredientEntryDte: { text: 'RecipeIngredientTextDte', object: { ingredient: 'RecipeIngredientAmountDte', entries: 'RecipeIngredientGroupDte' } },
    RecipeIngredientLeafDte: { text: 'RecipeIngredientTextDte', object: { ingredient: 'RecipeIngredientAmountDte' } },
    RecipeInstructionEntryDte: { text: 'RecipeInstructionTextDte', object: { entries: 'RecipeInstructionSectionDte' } },
    RecipeCookNoteEntryDte: { text: 'RecipeCookNoteTextDte', object: { entries: 'RecipeCookNoteSectionDte' } },
    RecipeEquipmentEntryDte: { text: 'RecipeEquipmentTextDte', object: { ingredient: 'RecipeEquipmentAmountDte' } },
};
const specialTypes = new Set([...Object.keys(unions), 'MonsterEntryDte', 'EntryTableCellDte', 'ClassTableCellDte']);
const relatedContainers = new Set(['ObjectActionDte', 'ObjectActionItemDte', 'TrapStageItemDte',
    'VehicleActionItemDte', 'RecipeCookNoteNestedSectionDte']);
const relevant = new Set([...specialTypes, ...relatedContainers]);
let changed = true;
while (changed) {
    changed = false;
    for (const [name, model] of Object.entries(models)) {
        if (!relevant.has(name) && model.fields.some(field => [...relevant].some(type => new RegExp(`\\b${type}\\b`).test(field.type)))) {
            relevant.add(name);
            changed = true;
        }
    }
}

function parseTags(text) {
    const result = [];
    function read(fragment, base = 0) {
        let cursor = 0;
        while (cursor < fragment.length) {
            const start = fragment.indexOf('{@', cursor);
            if (start < 0) {
                break;
            }
            let end = start + 2;
            let depth = 1;
            for (; end < fragment.length && depth; end += 1) {
                if (fragment[end] === '{') {
                    depth += 1;
                }
                if (fragment[end] === '}') {
                    depth -= 1;
                }
            }
            if (depth) {
                result.push({ name: '[unbalanced]', offset: base + start });
                break;
            }
            const inner = fragment.slice(start + 2, end - 1);
            const match = /^(\S+?)(?:\s+([\s\S]*)|$)/.exec(inner);
            const name = match[1].toLowerCase();
            result.push({ name, offset: base + start, length: end - start, tokenHash: hash(fragment.slice(start, end)) });
            read(inner, base + start + 2);
            cursor = end;
        }
    }
    read(text);
    return result;
}

const domainByRoot = {};
const catalogs = {};
for (const review of reviews) {
    catalogs[review.name] = { authoredRecords: 0, effectiveDefinitions: review.normalizedCount,
        sourceRoots: review.roots, variants: {}, tags: {}, tagRecords: {}, entryTags: {}, fieldBindings: {}, examples: {}, concepts: {} };
    for (const concept of concepts) {
        catalogs[review.name].concepts[concept.id] = { structuredOccurrences: 0, structuredOwners: 0,
            syntaxOccurrences: 0, syntaxOwners: 0, syntaxInsideBoundContent: 0, referenceLeads: 0, lexicalStrings: 0,
            examples: {}, reviewedExamples: [] };
    }
    for (const name of review.roots) {
        domainByRoot[name.replace('treasure.', '')] = review.name;
    }
}
const variants = {};
const variantOwners = new Map();
const unresolvedBindings = {};
const documents = new Map();
const manifest = [];
const roots = [];
const supportCollections = {};
let currentTypedPaths = new Set();
let currentTags = new Set();
let currentConceptFields = new Set();
let currentConceptSyntax = new Set();
const rawEntryShapes = {};
const unboundShapes = {};

function evidence(owner, locator) {
    return { file: owner.file, root: owner.root, index: owner.index, name: owner.name, source: owner.source, locator };
}

function tagEvidence(owner, locator, tag) {
    return { ...evidence(owner, locator), tag: tag.name, offset: tag.offset, length: tag.length, tokenHash: tag.tokenHash };
}

function noteVariant(type, value, locator, owner, context, family) {
    currentTypedPaths.add(locator);
    const shape = typeof value === 'string' ? 'text' : typeof value === 'number' ? 'number' : value?.type ?? '[object]';
    const key = `${family}:${type}:${shape}`;
    const group = variants[key] ??= { javaType: type, java: models[type]?.file ?? null, family,
        sourceShape: typeof value === 'string' ? 'string' : sorted(Object.fromEntries(Object.entries(value ?? {}).map(([key, child]) =>
            [key, child === null ? 'null' : Array.isArray(child) ? 'array' : typeof child]))),
        contexts: {}, catalogs: {}, examples: [], fields: {} };
    increment(group.contexts, context);
    increment(group.catalogs, owner.domain);
    increment(catalogs[owner.domain].variants, key);
    const ownerSets = variantOwners.get(key) ?? {};
    ownerSets[context] ??= new Set();
    ownerSets[context].add(`${owner.file}:${owner.root}:${owner.index}`);
    variantOwners.set(key, ownerSets);
    const mechanicalConcept = type === 'ClassAbilityEntryDte' ? value.type === 'abilityDc' ? 'saves' : 'attacks'
        : type === 'ClassOptionsEntryDte' ? 'choices' : type === 'ObjectAttackDte' ? 'attacks'
        : type === 'SpellcastingMonsterEntryDte' ? 'spellcasting' : null;
    if (mechanicalConcept && context === 'authored') {
        const cell = catalogs[owner.domain].concepts[mechanicalConcept];
        cell.structuredOccurrences += 1;
        currentConceptFields.add(mechanicalConcept);
        cell.examples.structured ??= evidence(owner, locator);
    }
    for (const [field, child] of Object.entries(typeof value === 'object' && value !== null ? value : {})) {
        group.fields[field] ??= {};
        increment(group.fields[field], child === null ? 'null' : Array.isArray(child) ? `array:${child.length === 0 ? 'empty' : 'nonempty'}` : typeof child);
    }
    if (group.examples.length < 4) {
        group.examples.push(evidence(owner, locator));
    }
}

function follow(value, type, locator, owner, context = 'authored', family = 'related') {
    if (value === undefined || value === null) {
        return;
    }
    const list = /^List<(.+)>$/.exec(type);
    if (list) {
        if (Array.isArray(value)) {
            value.forEach((child, index) => follow(child, list[1], `${locator}[${index}]`, owner, context, family));
        }
        return;
    }
    const map = /^Map<(.+)>$/.exec(type);
    if (map) {
        const childType = splitTopLevel(map[1])[1];
        for (const [key, child] of Object.entries(value)) {
            follow(child, childType, `${locator}.${key}`, owner, context, family);
        }
        return;
    }
    if (type === 'MonsterDte') {
        follow(value, 'MonsterFeaturesDte', locator, owner, context);
        follow(value._copy, 'MonsterCopyReferenceDte', `${locator}._copy`, owner, 'transform');
        follow(value._versions, 'List<MonsterVersionDte>', `${locator}._versions`, owner, 'transform');
        return;
    }
    if (type === 'MonsterEntryDte') {
        const spelling = value?.type === 'spellcasting';
        noteVariant(spelling ? 'SpellcastingMonsterEntryDte' : 'SharedMonsterEntryDte', value, locator, owner, context, type);
        follow(value, spelling ? 'MonsterSpellcastingDte' : 'EntryDte', locator, owner, context);
        return;
    }
    if (type === 'EntryTableCellDte') {
        const target = typeof value === 'string' ? 'TextTableCellDte' : typeof value === 'number' ? 'NumericTableCellDte'
            : value.type === 'cell' && value.roll ? 'RollTableCellDte' : 'StructuredTableCellDte';
        noteVariant(target, value, locator, owner, context, type);
        if (typeof value === 'object') {
            follow(value, target, locator, owner, context);
        }
        return;
    }
    if (type === 'ClassTableCellDte') {
        noteVariant(type, value, locator, owner, context, type);
        return;
    }
    const union = unions[type];
    if (union) {
        const target = typeof value === 'string' ? union.text : union.object?.[value.type] ?? union.anyObject;
        if (!target) {
            increment(unresolvedBindings, `${type}:${value?.type ?? typeof value}`);
            return;
        }
        noteVariant(target, value, locator, owner, context, type);
        if (typeof value === 'object') {
            follow(value, target, locator, owner, context, type);
        }
        return;
    }
    if (relatedContainers.has(type)) {
        noteVariant(type, value, locator, owner, context, 'related-container');
    }
    if (!relevant.has(type) && !Object.values(rootTypes).includes(type)) {
        return;
    }
    for (const field of models[type]?.fields ?? []) {
        if (!Object.hasOwn(value, field.name)) {
            continue;
        }
        const fieldLocator = `${locator}.${field.name}`;
        const fieldContext = context === 'transform' || /^_(copy|mod|versions|abstract|implementations|templates)$/.test(field.name)
            ? 'transform' : context;
        if (specialTypes.has(field.type.replace(/^List<|>$/g, '')) || /EntryDte/.test(field.type)) {
            catalogs[owner.domain].fieldBindings[`${type}.${field.name}`] ??= { java: models[type].file, type: field.type, example: evidence(owner, fieldLocator) };
        }
        follow(value[field.name], field.type, fieldLocator, owner, fieldContext, family);
    }
}

// Formula/reference tags are counted on parsed JSON string leaves, with typed-entry context kept separate.
function visit(value, locator, owner, typedEntryPaths, context = 'authored') {
    if (typeof value === 'string') {
        const entryBound = [...typedEntryPaths].some(prefix => locator === prefix || locator.startsWith(`${prefix}.`) || locator.startsWith(`${prefix}[`));
        for (const tag of parseTags(value)) {
            currentTags.add(tag.name);
            increment(catalogs[owner.domain].tags, tag.name);
            if (entryBound) {
                increment(catalogs[owner.domain].entryTags, tag.name);
            }
            catalogs[owner.domain].examples[`tag:${tag.name}`] ??= { ...tagEvidence(owner, locator, tag), context };
            for (const concept of concepts) {
                const cell = catalogs[owner.domain].concepts[concept.id];
                if (context === 'authored' && concept.tags.includes(tag.name)) {
                    cell.syntaxOccurrences += 1;
                    if (entryBound) {
                        cell.syntaxInsideBoundContent += 1;
                    }
                    currentConceptSyntax.add(concept.id);
                    cell.examples.syntax ??= tagEvidence(owner, locator, tag);
                }
                if (context === 'authored' && concept.referenceTags?.includes(tag.name)) {
                    cell.referenceLeads += 1;
                    cell.examples.reference ??= tagEvidence(owner, locator, tag);
                }
            }
        }
        if (context === 'authored' && (entryBound || /\.(effect|trigger|countermeasures|result|entry|hauntBonus)(\[|\.|$)/.test(locator))) {
            for (const concept of concepts) {
                if (new RegExp(concept.marker, 'i').test(value)) {
                    const cell = catalogs[owner.domain].concepts[concept.id];
                    cell.lexicalStrings += 1;
                    cell.examples.lexical ??= evidence(owner, locator);
                }
                if (reviewedProse[concept.id]?.some(([domain, name, source]) => domain === owner.domain && name === owner.name && source === owner.source)
                    && new RegExp(concept.marker, 'i').test(value) && cellSampleMissing(concept.id, owner)) {
                    catalogs[owner.domain].concepts[concept.id].reviewedExamples.push(evidence(owner, locator));
                }
            }
        }
        return;
    }
    if (Array.isArray(value)) {
        value.forEach((child, index) => visit(child, `${locator}[${index}]`, owner, typedEntryPaths, context));
    } else if (value !== null && typeof value === 'object') {
        for (const [key, child] of Object.entries(value)) {
            visit(child, `${locator}.${key}`, owner, typedEntryPaths,
                context === 'transform' || key.startsWith('_') ? 'transform' : context);
        }
    }
}

function cellSampleMissing(concept, owner) {
    return !catalogs[owner.domain].concepts[concept].reviewedExamples.some(example => example.file === owner.file && example.index === owner.index && example.root === owner.root);
}

const dataFiles = (await files(path.join(rootDirectory, 'src/main/resources/data'))).filter(file => file.endsWith('.json'));
for (const file of dataFiles) {
    const bytes = await readFile(file);
    const document = JSON.parse(bytes.toString('utf8').replace(/^\uFEFF/, ''));
    documents.set(relative(file), document);
    manifest.push({ file: relative(file), sha256: hash(bytes), roots: Object.keys(document) });
    for (const [collection, values] of Object.entries(document)) {
        const domain = domainByRoot[collection];
        if (!domain) {
            const group = supportCollections[collection] ??= { role: collection === 'crochetPattern' ? 'OUT_OF_SCOPE' : 'support',
                records: 0, files: [], fields: {}, typeValues: {}, tags: {}, examples: {} };
            group.files.push(relative(file));
            const supportRecords = Array.isArray(values) ? values : [values];
            group.records += supportRecords.length;
            for (const [index, record] of supportRecords.entries()) {
                for (const field of Object.keys(record ?? {})) {
                    increment(group.fields, field);
                }
                if (collection === 'crochetPattern') {
                    continue;
                }
                function walkSupport(node, locator) {
                    if (typeof node === 'string') {
                        for (const tag of parseTags(node)) {
                            increment(group.tags, tag.name);
                            group.examples[`tag:${tag.name}`] ??= { file: relative(file), locator, name: record?.name ?? null, source: record?.source ?? null };
                        }
                    } else if (Array.isArray(node)) {
                        node.forEach((child, i) => walkSupport(child, `${locator}[${i}]`));
                    } else if (node !== null && typeof node === 'object') {
                        if (typeof node.type === 'string') {
                            increment(group.typeValues, node.type);
                        }
                        for (const [key, child] of Object.entries(node)) {
                            walkSupport(child, `${locator}.${key}`);
                        }
                    }
                }
                walkSupport(record, Array.isArray(values) ? `$.${collection}[${index}]` : `$.${collection}`);
            }
            continue;
        }
        if (!models[rootTypes[collection]] && collection !== 'monster') {
            throw new Error(`Missing Java root ${collection}: ${rootTypes[collection]}`);
        }
        for (const [index, value] of values.entries()) {
            const owner = { file: relative(file), root: collection, index, name: value.name ?? null,
                source: value.source ?? value.inherits?.source ?? null, domain };
            const locator = `$.${collection}[${index}]`;
            catalogs[domain].authoredRecords += 1;
            currentTypedPaths = new Set();
            currentTags = new Set();
            currentConceptFields = new Set();
            currentConceptSyntax = new Set();
            const before = Object.values(variants).reduce((sum, group) => sum + (group.catalogs[domain] ?? 0), 0);
            follow(value, rootTypes[collection], locator, owner);
            if (!nonGameplay.has(domain)) {
                for (const concept of concepts) {
                    for (const field of concept.fields) {
                        const parent = value[field] !== undefined ? value : value.inherits;
                        if (parent?.[field] == null || concept.descriptiveFields?.includes(field)) {
                            continue;
                        }
                        const cell = catalogs[domain].concepts[concept.id];
                        cell.structuredOccurrences += 1;
                        currentConceptFields.add(concept.id);
                        cell.examples.structured ??= evidence(owner, `${locator}${parent === value ? '' : '.inherits'}.${field}`);
                    }
                }
                const roleFields = domain === 'Classes' ? { saves: ['proficiency'] }
                    : domain === 'Monsters' ? { 'action-economy': ['action', 'bonus', 'reaction', 'legendary', 'mythic'] }
                    : domain === 'Vehicles' ? { 'action-economy': ['action', 'reaction', 'actionStation'] } : {};
                function directRole(concept, path) {
                    const cell = catalogs[domain].concepts[concept];
                    cell.structuredOccurrences += 1;
                    currentConceptFields.add(concept);
                    cell.examples.structured ??= evidence(owner, path);
                }
                for (const [concept, fields] of Object.entries(roleFields)) {
                    for (const field of fields) {
                        if (value[field] != null) {
                            directRole(concept, `${locator}.${field}`);
                        }
                    }
                }
                if (domain === 'Spells' && typeof value.components?.m === 'object' && value.components.m !== null) {
                    if (value.components.m.cost != null) {
                        directRole('quantities-tables', `${locator}.components.m.cost`);
                    }
                    if (value.components.m.consume != null) {
                        directRole('resources', `${locator}.components.m.consume`);
                    }
                }
                function nestedRoles(node, current, sourceField) {
                    if (!node || typeof node !== 'object') {
                        return;
                    }
                    if (Array.isArray(node)) {
                        node.forEach((child, i) => nestedRoles(child, `${current}[${i}]`, sourceField));
                        return;
                    }
                    for (const [key, child] of Object.entries(node)) {
                        if (child == null) {
                            continue;
                        }
                        const next = `${current}.${key}`;
                        if (['additionalSpells', 'spellcasting'].includes(sourceField)) {
                            if (['daily', 'rest', 'restLong', 'charges', 'recharge', 'spells'].includes(key)) {
                                directRole('resources', next);
                            }
                            if (['rest', 'restLong'].includes(key)) {
                                directRole('recovery', next);
                            }
                        }
                        if (key === 'choose' || key === 'options') {
                            directRole('choices', next);
                        }
                        nestedRoles(child, next, sourceField);
                    }
                }
                for (const [field, child] of Object.entries(value)) {
                    if (['ability', 'additionalSpells', 'spellcasting', 'startingEquipment', 'startingProficiencies',
                        'multiclassing', 'skillProficiencies', 'toolProficiencies', 'languageProficiencies',
                        'weaponProficiencies', 'armorProficiencies', 'savingThrowProficiencies',
                        'skillToolLanguageProficiencies', 'expertise', 'resist'].includes(field)) {
                        nestedRoles(child, `${locator}.${field}`, field);
                    }
                }
            }
            visit(value, locator, owner, currentTypedPaths);
            for (const tag of currentTags) {
                increment(catalogs[domain].tagRecords, tag);
            }
            for (const concept of currentConceptFields) {
                catalogs[domain].concepts[concept].structuredOwners += 1;
            }
            for (const concept of currentConceptSyntax) {
                catalogs[domain].concepts[concept].syntaxOwners += 1;
            }
            function auditRaw(node, current) {
                if (!node || typeof node !== 'object') {
                    return;
                }
                if (Array.isArray(node)) {
                    node.forEach((child, i) => auditRaw(child, `${current}[${i}]`));
                    return;
                }
                if (typeof node.type === 'string' && (commonDispatch[node.type] || classDispatch[node.type]
                    || ['attack', 'spellcasting', 'ingredient', 'tableGroup'].includes(node.type))) {
                    rawEntryShapes[node.type] ??= { occurrences: 0, catalogs: {}, examples: [] };
                    const group = rawEntryShapes[node.type];
                    group.occurrences += 1;
                    increment(group.catalogs, domain);
                    if (group.examples.length < 4) {
                        group.examples.push(evidence(owner, current));
                    }
                    if (!currentTypedPaths.has(current)) {
                        unboundShapes[node.type] ??= { occurrences: 0, examples: [] };
                        unboundShapes[node.type].occurrences += 1;
                        if (unboundShapes[node.type].examples.length < 10) {
                            unboundShapes[node.type].examples.push(evidence(owner, current));
                        }
                    }
                }
                for (const [key, child] of Object.entries(node)) {
                    auditRaw(child, `${current}.${key}`);
                }
            }
            auditRaw(value, locator);
            roots.push({ ...owner, locator, copy: Boolean(value._copy), versions: value._versions?.length ?? 0, tags: [...currentTags].sort(),
                boundNodes: Object.values(variants).reduce((sum, group) => sum + (group.catalogs[domain] ?? 0), 0) - before });
        }
    }
}

for (const [domain, catalog] of Object.entries(catalogs)) {
    for (const cell of Object.values(catalog.concepts)) {
        cell.status = cell.structuredOwners > 0
            ? cell.syntaxOwners > 0 || cell.reviewedExamples.length > 0 ? 'MIXED'
                : cell.lexicalStrings > 0 || cell.referenceLeads > 0 ? 'PARTIAL' : 'STRUCTURED'
            : cell.syntaxOwners > 0 || cell.reviewedExamples.length > 0 ? 'ENTRY_ONLY'
            : cell.lexicalStrings > 0 || cell.referenceLeads > 0 ? 'UNCERTAIN' : 'ABSENT';
        if (nonGameplay.has(domain)) {
            cell.status = cell.lexicalStrings > 0 || cell.syntaxOwners > 0 || cell.referenceLeads > 0 ? 'UNCERTAIN' : 'ABSENT';
        }
    }
}
for (const [key, group] of Object.entries(variants)) {
    group.ownerCounts = Object.fromEntries(Object.entries(variantOwners.get(key)).map(([context, owners]) => [context, owners.size]));
}

const protectedFiles = await files(path.join(rootDirectory, 'src'));
const protectedSnapshot = [];
for (const file of protectedFiles) {
    protectedSnapshot.push({ file: relative(file), sha256: hash(await readFile(file)) });
}
const baselineFile = path.join(output, 'protected-files.json');
if (process.argv.includes('--capture-baseline')) {
    await writeFile(baselineFile, JSON.stringify(protectedSnapshot, null, 2) + '\n', { flag: 'wx' });
} else {
    const baseline = JSON.parse(await readFile(baselineFile, 'utf8'));
    if (JSON.stringify(baseline) !== JSON.stringify(protectedSnapshot)) {
        throw new Error('Protected src files changed');
    }
}

const result = { schema: 1, dataset: 'repository-pinned 5eTools v2.37.0',
    countSemantics: 'Authored source only; transform nodes separate; effective counts are existing catalog contracts, not expansion by scanner.',
    summary: { files: manifest.length, catalogs: Object.keys(catalogs).length,
        authoredRecords: roots.length, effectiveDefinitions: reviews.reduce((sum, review) => sum + review.normalizedCount, 0),
        sourceCollections: Object.keys(rootTypes).length, variantShapes: Object.keys(variants).length,
        javaVariantTypes: new Set(Object.values(variants).map(group => group.javaType)).size,
        protectedFiles: protectedSnapshot.length },
    manifest, rootTypes, concepts, catalogs, variants: sorted(variants), rawEntryShapes, unboundShapes,
    supportCollections, unresolvedBindings, roots };
await writeFile(path.join(output, 'entry-evidence.json'), JSON.stringify(result, null, 2) + '\n');
await writeFile(path.join(output, 'java-model-index.json'), JSON.stringify(sorted(models), null, 2) + '\n');
console.log(JSON.stringify({ ...result.summary, unresolvedBindings }, null, 2));
