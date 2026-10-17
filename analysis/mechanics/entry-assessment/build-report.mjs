import { readFile, writeFile, readdir, access } from 'node:fs/promises';
import { createHash } from 'node:crypto';
import { fileURLToPath } from 'node:url';
import path from 'node:path';
import { modelReview } from './model-review.mjs';

const directory = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(directory, '../../..');
const evidence = JSON.parse(await readFile(path.join(directory, 'entry-evidence.json'), 'utf8'));
const models = JSON.parse(await readFile(path.join(directory, 'java-model-index.json'), 'utf8'));
const link = (file, label = path.basename(file)) => `[${label}](../../../${file})`;
const location = sample => `${link(sample.file, path.basename(sample.file))} ${sample.name ?? sample.root ?? ''} ${sample.source ?? ''}: \`${sample.locator}\``;
const total = values => values.reduce((sum, value) => sum + value, 0);
const sum = object => total(Object.values(object));
function disposition(value) {
    if (value.family === 'ClassEntryDte') {
        if (value.javaType === 'ClassReferenceEntryDte') {
            return 'Typed feature/external graph reference; existing public reference projection. Not necessarily a grant.';
        }
        if (value.javaType === 'ClassOptionsEntryDte') {
            return 'ClassFeatureChoice retains count/style/all children internally; public reference-backed subset only.';
        }
        if (value.javaType === 'ClassAbilityEntryDte') {
            return 'ClassFeatureAbilityDeclaration retains kind/name/attributes; withheld publicly; no arithmetic inferred.';
        }
        if (value.javaType === 'ClassTableEntryDte') {
            return 'ClassFeatureTable preserves ordered cells internally; feature-entry table withheld publicly.';
        }
        return 'Typed ClassFeatureNode content retained; prose/statblock body not public; no effect interpretation.';
    }
    if (value.family === 'ClassTableCellDte') {
        return 'ClassTableCell kind/value retained. Safe progression values have positioned public projection; text and feature tables withheld.';
    }
    if (value.family === 'MonsterEntryDte') {
        return 'Shared wrapper delegates rich content; spellcasting wrapper maps usage block. Existing Monster detail exposes content; no complete effect association.';
    }
    if (value.family === 'EntryTableCellDte') {
        return 'Range/text/numeric/recursive cell structure retained internally. Observed Monster text/structured cells use public rich content; Item/Spell rows withheld.';
    }
    if (value.family === 'EntryDte') {
        if (['ImageEntryDte', 'LinkEntryDte', 'ReadaloudEntryDte', 'InlineEntryDte'].includes(value.javaType)) {
            return 'Source content retained internally in observed Deity/Rule contexts; no mechanical effect; public content withheld.';
        }
        if (value.javaType === 'RewardInlineStatblockEntryDte') {
            return 'Explicit inline Object statistics retained in Reward content; withheld publicly; not an independent Object definition.';
        }
        return 'Ordered shared content retained in definitions/import records; Monster maps rich content publicly, later catalogs withhold entry bodies. Syntax/prose not normalized as effects.';
    }
    if (value.javaType.startsWith('Recipe')) {
        return 'Dedicated culinary structure retained in RecipeDefinition; public metadata/counts only; no gameplay meaning.';
    }
    if (value.javaType.startsWith('Object')) {
        return 'Typed Object action/attack/list structure retained; actions withheld; section roles explicit but effect bodies textual.';
    }
    if (value.javaType.startsWith('Trap')) {
        return 'Role-specific Trap stage/text/item structure retained; body withheld; role does not encode a complete effect.';
    }
    if (value.javaType.startsWith('Vehicle')) {
        return 'Vehicle action/item content retained; public action prose withheld; no station execution or resource inference.';
    }
    throw new Error(`Missing variant disposition: ${value.javaType}`);
}
const javaFiles = [];
async function walk(directory) {
    const result = [];
    for (const child of (await readdir(directory, { withFileTypes: true })).sort((a, b) => a.name.localeCompare(b.name, 'en'))) {
        const file = path.join(directory, child.name);
        if (child.isDirectory()) {
            result.push(...await walk(file));
        } else {
            result.push(file);
        }
    }
    return result;
}
javaFiles.push(...(await walk(path.join(root, 'src/main/java'))).filter(file => file.endsWith('.java')));
function typeLink(type) {
    const file = javaFiles.find(file => path.basename(file) === `${type}.java`);
    if (!file) {
        throw new Error(`Unknown reviewed Java type ${type}`);
    }
    return link(path.relative(root, file).replaceAll('\\', '/'), type);
}
for (const [domain, review] of Object.entries(modelReview)) {
    if (!evidence.catalogs[domain]) {
        throw new Error(`Unknown domain ${domain}`);
    }
    await access(path.join(root, review.mapping));
    review.normalized.forEach(typeLink);
    review.public.forEach(typeLink);
}

const inventory = [
    '# Entry structure inventory', '',
    'Analysis of the pinned repository bytes. Reproduce with `node analysis/mechanics/entry-assessment/scan-entries.mjs` then `node analysis/mechanics/entry-assessment/build-report.mjs`.', '',
    '**13 shared EntryDte variants, seven ClassEntryDte variants and two MonsterEntryDte wrapper variants are observed.** Including table-cell variants and dedicated Object/Trap/Vehicle/Recipe content, there are **48 distinct Java carrier types** across **73 family/type/JSON-shape combinations**. Those are different denominators; wrappers are not additional source nodes.', '',
    'Coverage: 175 fingerprinted JSON files, 43 definition collections, 14,399 authored records, 36 domains and 14,797 effective definitions in existing catalog contracts. The scanner does not expand copies or versions. Its node counts are authored occurrences (with transformation payloads separately labeled); they are never effective occurrence counts.', '',
    '## Family counts', '', '| Binding family | Java variants used | Shape combinations | Authored visits | Transform visits |', '|---|---:|---:|---:|---:|',
];
for (const family of [...new Set(Object.values(evidence.variants).map(value => value.family))].sort()) {
    const variants = Object.values(evidence.variants).filter(value => value.family === family);
    inventory.push(`| ${family} | ${new Set(variants.map(value => value.javaType)).size} | ${variants.length} | ${total(variants.map(value => value.contexts.authored ?? 0))} | ${total(variants.map(value => value.contexts.transform ?? 0))} |`);
}
inventory.push('', 'RecipeIngredientLeafDte reuses two ingredient carriers; family type counts therefore must not be added to obtain unique Java types. Monster shared wrappers delegate to EntryDte, so counting both layers counts visits rather than unique JSON nodes.', '',
    '## All observed bound shapes', '',
    'Exact optional/null/empty field-type histograms, catalog counts and additional examples are in `entry-evidence.json:variants`. Text is retained at the JSON-string level; no recipe/prose content is reproduced here.', '',
    '| Family / JSON form | Java carrier | Authored / transform visits | Authored owners | Catalogs | Normalization / mechanical meaning / publication | Example |', '|---|---|---:|---:|---|---|---|');
for (const [key, value] of Object.entries(evidence.variants)) {
    const shape = key.slice(key.lastIndexOf(':') + 1);
    inventory.push(`| ${value.family} / ${shape} | ${typeLink(value.javaType)} | ${value.contexts.authored ?? 0} / ${value.contexts.transform ?? 0} | ${value.ownerCounts.authored ?? 0} | ${Object.keys(value.catalogs).join(', ')} | ${disposition(value)} | ${location(value.examples[0])} |`);
}
inventory.push('', '## Interpretation and normalization', '',
    '- Shared containers preserve named sections, list nesting, ordered tables and special blocks. Their Java types describe document structure. Named text, an inset or a quote does not establish an effect.',
    '- Class ability declarations, options and reference entries are stronger structures: kinds, authored attributes, counts and groups already reach the ClassFeatureNode graph. The public mapper selects reference-backed choices/consumption; it withholds prose, ability declarations and feature tables.',
    '- MonsterEntryDte has a dedicated spellcasting branch. Its other branch wraps shared entries. The runtime MonsterMapper instead reads the resolved content tree into Monster content records; it retains source extras rather than deriving attack effects.',
    '- The seven Object attack nodes retain separate attackEntries and hitEntries. Trap stage groups retain role-specific containers; Vehicle actions retain text/list/named alternatives. None supplies a complete generic effect schema.',
    '- Recipe quantities and ordered instructions use dedicated culinary types. Their structure is not evidence of crafting mechanics.', '',
    '## Confirmed absences and counting traps', '',
    'No `type=tableGroup` entry occurs on definition roots; encounter/name/treasure table grouping uses separate DTEs. General shared EntryDte has no options or abilityDc subtype: those are Class-specific. Primitive numeric/Boolean entries are not generic EntryDte text; numeric table cells and Class cells have their own bindings.', '',
    'The earlier corpus-wide `type` histogram includes root classification, images and spellcasting blocks. It is not an EntryDte inventory. For example, a Variant Rule root with type=section and a deity symbolImg with type=image are not automatically SectionEntryDte/ImageEntryDte.', '',
    '## Exhaustive source audit versus binding limits', '',
    'All JSON is parsed and fingerprinted. Definition-root raw entry-shaped discriminators are additionally inventoried independently of static Java binding. The static adapter follows record components and reviewed union dispatch; it is not Jackson and does not execute custom copy-modification deserializers. Its explicit exception ledger is `unboundShapes`. In particular, five definition-root table nodes in copy payloads are unbound by that adapter; this is not a finding that production discards them.', '',
    'The 973 definition-root type=table nodes reconcile as **824 shared EntryDte tables + 144 Class entry tables + five copy-payload tables**. Tables in support collections have a separate denominator. `unresolvedBindings` is empty for the traversed union slots; this does not claim every custom importer path was executed.', '',
    '## Supporting source content', '', '| Support root | Units | Typed rule-content observations / disposition |', '|---|---:|---|');
const supportNotes = {
    legendaryGroup: '188 roots; lairActions 115, regionalEffects 158, mythicEncounter 3, copies 16, versions 21. Current runtime group-body ingestion was not found; see MODEL-GAP-ANALYSIS.',
    monsterTemplate: '66 transformation records; existing MonsterTemplateRepository validates/applies JsonNode instructions. Not EntryDte instances or standalone effective definitions.',
    legendaryGroupTemplate: 'One support template; runtime body use not found. Do not assume MonsterTemplateRepository handles this different root.',
    status: 'Five StatusDte entries parsed and source-validated by ConditionCatalog; count retained, descriptions not normalized there.',
    itemEntry: '13 typed entry templates; not concrete Items.', itemMastery: 'Eight typed definitions; Item normalized references are UID/note, not mastery effect execution.',
    itemProperty: '27 property records, 26 with entries; source semantics are not duplicated in ItemPropertyDto UID/note.',
    itemType: '67 type records, including typed entries and descriptive metadata.',
    itemTypeAdditionalEntries: 'Two auxiliary type-entry rules.',
};
for (const [name, value] of Object.entries(evidence.supportCollections)) {
    inventory.push(`| ${name} | ${value.records} | ${supportNotes[name] ?? (value.role === 'OUT_OF_SCOPE' ? '20 Homecraft crochet records, OUT OF SCOPE by product decision; fingerprinted, no gameplay analysis.' : 'Auxiliary metadata/selection/provenance data; complete field/type/tag counts retained in supportCollections.')} |`);
}
inventory.push('', 'Supporting root units total 812, separately from the 20 excluded Homecraft records. Support raw type/tag counts are exhaustive and are not relabeled as production EntryDte bindings. All relevant support syntax is considered even where no runtime body model exists.', '');
await writeFile(path.join(directory, 'ENTRY-STRUCTURE-INVENTORY.md'), inventory.join('\n'));

const taxonomy = ['# Mechanics taxonomy', '',
    'A mechanical concept is broader than a token. Each row identifies the fact actually supported by source syntax and the boundary that remains. Existing typed statistics are not extraction opportunities.', '',
    '| ID | Concept | Source-field selectors (catalog-specific roles) | Syntax signals | Interpretation boundary |', '|---|---|---|---|---|'];
for (const [index, concept] of evidence.concepts.entries()) {
    taxonomy.push(`| C${String(index + 1).padStart(2, '0')} | ${concept.name} | ${concept.fields.map(field => `\`${field}\``).join(', ') || 'No selected direct field'} | ${concept.tags.map(tag => `@${tag}`).join(', ') || 'Reviewed context required'} | ${concept.scope} |`);
}
taxonomy.push('', '## Classification rules', '',
    'DIRECT values preserve an authored field or typed structural relationship. SYNTAX observations preserve a tag/formula/label without effect interpretation. LOCAL_CONTEXT requires a bounded association and reviewed gold set. PROSE_INFERENCE describes natural-language interpretation. UNSUPPORTED means the fact cannot currently be established.', '',
    'A damage expression is not an applied effect. A condition/spell/item reference can express removal, immunity, an example, a prerequisite or an option. Reference-only tags are therefore review leads for application/grant categories. Dice and chance tags establish syntax only and also occur in recipe, name and narrative content.', '',
    'Additional source-grounded categories beyond a combat-only vocabulary are base/component HP and thresholds; source ability/sense declarations; and quantity/table structure. Light, carrying/container capacity and encumbrance belong to exploration/equipment metadata and are already retained in ItemDefinition; no prose parser is proposed.', '',
    '## Existing mechanics versus document structures', '',
    'Class abilityDc/abilityAttackMod nodes, choice counts, Spell duration structures, proficiency groups and typed Item bonuses already carry mechanics. Entry list/table/section types carry structure. Their presence alone cannot distinguish rules, examples, lore or food instructions. Tables must retain order and exact range representations even when their content is withheld publicly.', '',
    'This vocabulary does not define an executable effect language, target defaults, prerequisite evaluation, roll system or event engine.', '');
await writeFile(path.join(directory, 'MECHANICS-TAXONOMY.md'), taxonomy.join('\n'));

const statusCode = { ABSENT: 'A', STRUCTURED: 'S', PARTIAL: 'P', ENTRY_ONLY: 'E', MIXED: 'M', UNCERTAIN: '?' };
const matrix = ['# Cross-catalog mechanics matrix', '',
    '**All 36 domains and 31 concepts are considered: 1,116 intersections.** Every intersection has structured-field owner/occurrence counts, syntax owner/occurrence counts, reference leads, lexical string counts and exact example locators in `entry-evidence.json:catalogs.<domain>.concepts.<id>`.', '',
    'Counts exhaustively measure the declared scanner predicates, not complete game effects. Direct selectors inspect authored root fields (or Magic Variant inherits fields), Class saving-throw/Monster action roles, reviewed typed entry nodes and constrained nested choice/spell-usage fields. They do not count all nested mechanics in every schema. The per-catalog model review below supplements these lower-bound direct counts.', '',
    '`S` STRUCTURED: selected explicit role survives a normalized typed structure, without confirmed additional entry semantics. `P` PARTIAL: structured evidence exists plus unresolved descriptive/role coverage. `E` ENTRY_ONLY: explicit formula/label syntax or a manually reviewed effect occurs in EntryDte or related textual content without a normalized semantic effect model. `M` MIXED: structured role evidence and such content evidence coexist. `?` UNCERTAIN: only lexical/reference leads exist. `A` ABSENT: no evidence under the declared probes; not proof that arbitrary prose contains no such mechanic.', '',
    'SYNTAX counts include related fields such as Monster action names and Encounter results, not just EntryDte leaves. `syntaxInsideBoundContent` records the narrower typed-content count separately. A dedicated textual table result is not relabeled as a shared EntryDte node.', '',
    'A source field whose value is prose, such as Trap.trigger, is not counted as a structured trigger event. ClassAbilityEntryDte remains structured despite being nested inside entries. Conditions/spell references alone never confirm condition application or spell granting. Non-gameplay indexes/Recipes do not become effect catalogs due to tag/keyword hits.', ''];
for (let start = 0; start < evidence.concepts.length; start += 6) {
    const group = evidence.concepts.slice(start, start + 6);
    matrix.push(`## Concepts C${String(start + 1).padStart(2, '0')}–C${String(start + group.length).padStart(2, '0')}`, '',
        group.map((concept, index) => `C${String(start + index + 1).padStart(2, '0')} ${concept.name}`).join('; '), '',
        `| Catalog | ${group.map((_, index) => `C${String(start + index + 1).padStart(2, '0')}`).join(' | ')} |`,
        `|---|${group.map(() => '---|').join('')}`);
    for (const [name, catalog] of Object.entries(evidence.catalogs)) {
        matrix.push(`| ${name} | ${group.map(concept => statusCode[catalog.concepts[concept.id].status]).join(' | ')} |`);
    }
    matrix.push('');
}
matrix.push('## Current DTE → normalized → public dispositions', '',
    'The following reviews reflect Batches 1–4. Source/effective denominators are distinct. Java links identify actual source files, not proposed production types. Each catalog also has exhaustive per-tag counts and saved type examples in the machine evidence.', '');
for (const [name, catalog] of Object.entries(evidence.catalogs)) {
    const review = modelReview[name];
    matrix.push(`### ${name}`, '', `**${catalog.authoredRecords} authored / ${catalog.effectiveDefinitions} effective.**`, '',
        `DTEs: ${catalog.sourceRoots.map(collection => typeLink(evidence.rootTypes[collection.replace('treasure.', '')])).join(', ')}. Normalized: ${review.normalized.map(typeLink).join(', ')}. Public: ${review.public.map(typeLink).join(', ')}. Mapping/projection: ${link(review.mapping)}.`, '',
        `Internal: ${review.retained}`, '', `Public: ${review.exposed}`, '', `Gap/boundary: ${review.gap}`, '',
        '| Concept with confirmed direct/syntax or reviewed sample evidence | Direct owners / occurrences | Syntax owners / occurrences | Example |', '|---|---:|---:|---|');
    for (const concept of evidence.concepts) {
        const cell = catalog.concepts[concept.id];
        if (!cell.structuredOwners && !cell.syntaxOwners && !cell.reviewedExamples.length) {
            continue;
        }
        matrix.push(`| ${concept.name} | ${cell.structuredOwners} / ${cell.structuredOccurrences} | ${cell.syntaxOwners} / ${cell.syntaxOccurrences} | ${location(cell.examples.structured ?? cell.examples.syntax ?? cell.reviewedExamples[0])} |`);
    }
    if (!Object.values(catalog.concepts).some(cell => cell.structuredOwners || cell.syntaxOwners || cell.reviewedExamples.length)) {
        matrix.push('| No confirmed game-effect evidence from these probes | 0 | 0 | Existing metadata/content domain retained |');
    }
    matrix.push('', 'Lexical/reference-only cells remain UNCERTAIN. Absence of a row in this evidence table does not upgrade an uncertain cell to absent.', '');
}
await writeFile(path.join(directory, 'CROSS-CATALOG-MATRIX.md'), matrix.join('\n'));
await writeFile(path.join(directory, 'model-review.json'), JSON.stringify(modelReview, null, 2) + '\n');

const documentation = (await walk(path.join(root, 'analysis'))).filter(file => file.endsWith('.md') && !file.startsWith(directory));
const documentEvidence = [];
for (const file of documentation) {
    const bytes = await readFile(file);
    const text = bytes.toString('utf8');
    documentEvidence.push({ file: path.relative(root, file).replaceAll('\\', '/'), sha256: createHash('sha256').update(bytes).digest('hex'),
        headings: text.split(/\r?\n/).filter(line => /^#{1,3} /.test(line)) });
}
const docsFile = path.join(directory, 'prior-documentation.json');
if (process.argv.includes('--capture-documentation')) {
    await writeFile(docsFile, JSON.stringify(documentEvidence, null, 2) + '\n', { flag: 'wx' });
} else {
    const baseline = JSON.parse(await readFile(docsFile, 'utf8'));
    if (JSON.stringify(baseline) !== JSON.stringify(documentEvidence)) {
        throw new Error('Prior analysis documentation changed');
    }
}
console.log(JSON.stringify({ documents: documentation.length, domains: Object.keys(modelReview).length, concepts: evidence.concepts.length, intersections: evidence.concepts.length * Object.keys(modelReview).length }));
