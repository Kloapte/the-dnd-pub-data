import { createHash } from 'node:crypto';
import { readFileSync, writeFileSync } from 'node:fs';

const resource = 'src/main/resources/data/recipes.json';
const raw = readFileSync(resource);
const data = JSON.parse(raw.toString('utf8'));
const records = data.recipe;

const paths = new Map();
const sourceCounts = new Map();
const valueCounts = new Map();
const tags = new Map();
const markupSamples = new Map();
const ingredientVariants = new Map();
const servingVariants = new Map();
const makesVariants = new Map();
const otherSourceCounts = new Map();
const rootFields = new Map();
const names = new Set();
let duplicateNameSourceCount = 0;
let explicitNullCount = 0;
let emptyStringCount = 0;
let emptyArrayCount = 0;
let emptyObjectCount = 0;
let instructionCount = 0;
let ingredientCount = 0;
let equipmentCount = 0;
let noteCookCount = 0;
const explicitMarkup = [];

const typeOf = value => value === null ? 'null'
  : Array.isArray(value) ? 'array'
    : typeof value === 'object' ? 'object' : typeof value;

function addCount(map, key, amount = 1) {
  map.set(key, (map.get(key) ?? 0) + amount);
}

function scan(value, path) {
  const fieldPath = path.replace(/\[\d+\]/g, '[]');
  addCount(paths, `${fieldPath}\t${typeOf(value)}`);
  if (value === null) {
    explicitNullCount++;
    return;
  }
  if (typeof value === 'string') {
    if (value.length === 0) {
      emptyStringCount++;
    }
    for (const match of value.matchAll(/\{@([\w-]+)\s+([^}]*)}/g)) {
      const tag = match[1];
      addCount(tags, tag);
      if (!markupSamples.has(tag)) {
        markupSamples.set(tag, match[0].slice(0, 180));
      }
    }
    return;
  }
  if (Array.isArray(value)) {
    if (value.length === 0) {
      emptyArrayCount++;
    }
    value.forEach((entry, index) => scan(entry, `${path}[${index}]`));
    return;
  }
  if (typeof value === 'object') {
    const keys = Object.keys(value);
    if (keys.length === 0) {
      emptyObjectCount++;
    }
    for (const [key, entry] of Object.entries(value)) {
      scan(entry, `${path}.${key}`);
    }
  }
}

for (const record of records) {
  addCount(sourceCounts, record.source ?? '<absent>');
  for (const [key, value] of Object.entries(record)) {
    addCount(rootFields, `${key}\t${typeOf(value)}`);
  }
  const identity = `${record.source}|${record.name}`;
  if (names.has(identity)) {
    duplicateNameSourceCount++;
  }
  names.add(identity);
  addCount(valueCounts, `type\t${record.type ?? '<absent>'}`);
  for (const key of ['dishTypes', 'diet', 'allergenGroups', 'miscTags']) {
    const values = record[key] == null ? ['<absent>']
      : Array.isArray(record[key]) ? record[key] : [record[key]];
    for (const value of values) {
      addCount(valueCounts, `${key}\t${value}`);
    }
  }
  for (const collectionName of ['ingredients', 'equipment']) {
    for (const entry of record[collectionName] ?? []) {
      if (collectionName === 'ingredients') {
        ingredientCount++;
      } else {
        equipmentCount++;
      }
      const variant = `${collectionName}\t${typeOf(entry)}\t${entry?.type ?? '<no-type>'}`;
      addCount(ingredientVariants, variant);
    }
  }
  if (record.serves) {
    addCount(servingVariants, Object.keys(record.serves).sort().join(',') || '<empty>');
  }
  if (record.makes !== undefined) {
    addCount(makesVariants, typeOf(record.makes));
  }
  for (const reference of record.otherSources ?? []) {
    addCount(otherSourceCounts, `${reference.source}\t${reference.page ?? '<no-page>'}`);
  }
  instructionCount += record.instructions?.length ?? 0;
  noteCookCount += record.noteCook?.length ?? 0;
  scan(record, 'recipe[]');
  for (const match of JSON.stringify(record).matchAll(/\{@(recipe|creature|link)\s+([^}]*)}/g)) {
    explicitMarkup.push({ recipe: record.name, tag: match[1], token: match[2] });
  }
}

const absentFields = [
  'category', 'ingredients', 'equipment', 'instructions', 'makes', 'serves', 'preparationTime',
  'cookingTime', 'time', 'skill', 'abilityCheck', 'dc', 'cost', 'currency', 'yield', 'effects',
  'duration', 'rarity', 'prerequisite', 'output', 'entries', '_copy', '_versions', 'otherSources',
  'reprintedAs', 'additionalSources', 'media', 'images', 'copy', 'hasFluff', 'hasFluffImages',
];

const report = {
  resource,
  sha256: createHash('sha256').update(raw).digest('hex'),
  rootKeys: Object.keys(data),
  recordCount: records.length,
  recursivePathTypeCount: paths.size,
  sourceDistribution: Object.fromEntries([...sourceCounts].sort()),
  duplicateNameSourceCount,
  rootFieldCounts: Object.fromEntries([...rootFields].sort()),
  authoredValueCounts: Object.fromEntries([...valueCounts].sort()),
  ingredientAndEquipmentVariantCounts: Object.fromEntries([...ingredientVariants].sort()),
  servingVariants: Object.fromEntries([...servingVariants].sort()),
  makesVariants: Object.fromEntries([...makesVariants].sort()),
  otherSourceCounts: Object.fromEntries([...otherSourceCounts].sort()),
  explicitMarkup,
  totalIngredientEntries: ingredientCount,
  totalEquipmentEntries: equipmentCount,
  totalInstructionSteps: instructionCount,
  totalCookNotes: noteCookCount,
  markupCounts: Object.fromEntries([...tags].sort()),
  markupSamples: Object.fromEntries([...markupSamples].sort()),
  missingCandidateFields: Object.fromEntries(absentFields.map(field => [
    field,
    records.filter(record => !Object.hasOwn(record, field)).length,
  ])),
  valueShapeCounts: { explicitNullCount, emptyStringCount, emptyArrayCount, emptyObjectCount },
  pathTypes: [...paths].sort().map(([pathType, occurrences]) => {
    const [path, type] = pathType.split('\t');
    return { path, type, occurrences };
  }),
  nameExamples: records.slice(0, 12).map(record => ({
    name: record.name,
    source: record.source,
    page: record.page,
    type: record.type,
    dishTypes: record.dishTypes,
  })),
};

writeFileSync('analysis/remaining-json/recipes/source-audit.json', `${JSON.stringify(report, null, 2)}\n`);

const fieldRows = report.pathTypes.map(({ path, type, occurrences }) =>
  `| \`${path}\` | ${type} | ${occurrences} |`);
const fieldMarkdown = [
  '# Recipes Field Coverage',
  '',
  `Generated from the pinned v2.37.0 resource. SHA-256: \`${report.sha256}\`.`,
  '',
  `The inventory contains ${records.length} records and ${paths.size} recursive path/type pairs.`,
  '',
  '| Field path | JSON type | Occurrences |',
  '|---|---|---:|',
  ...fieldRows,
  '',
  `Explicit null values: ${explicitNullCount}; empty strings: ${emptyStringCount}; empty arrays: ${emptyArrayCount}; empty objects: ${emptyObjectCount}.`,
  '',
].join('\n');
writeFileSync('analysis/remaining-json/recipes/FIELD-COVERAGE.md', fieldMarkdown);

console.log(JSON.stringify({
  sha256: report.sha256,
  recordCount: report.recordCount,
  recursivePathTypeCount: report.recursivePathTypeCount,
  sourceDistribution: report.sourceDistribution,
  duplicateNameSourceCount,
  rootFieldCounts: report.rootFieldCounts,
  authoredValueCounts: report.authoredValueCounts,
  ingredientAndEquipmentVariantCounts: report.ingredientAndEquipmentVariantCounts,
  servingVariants: report.servingVariants,
  makesVariants: report.makesVariants,
  otherSourceCounts: report.otherSourceCounts,
  explicitMarkup: report.explicitMarkup,
  totalIngredientEntries: ingredientCount,
  totalEquipmentEntries: equipmentCount,
  totalInstructionSteps: instructionCount,
  totalCookNotes: noteCookCount,
  markupCounts: report.markupCounts,
  markupSamples: report.markupSamples,
  missingCandidateFields: report.missingCandidateFields,
  valueShapeCounts: report.valueShapeCounts,
}, null, 2));
