import fs from 'node:fs';
import path from 'node:path';
import { createHash } from 'node:crypto';
import { fileURLToPath } from 'node:url';

const here = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(here, '../..');
const dataRoot = path.join(root, 'src/main/resources/data');
const cmp = (a, b) => a < b ? -1 : a > b ? 1 : 0;
const canonical = value => Array.isArray(value) ? value.map(canonical)
  : value && typeof value === 'object'
    ? Object.fromEntries(Object.keys(value).sort(cmp).map(key => [key, canonical(value[key])]))
    : value;
const sortObject = map => Object.fromEntries([...map].sort(([a], [b]) => cmp(String(a), String(b))));
const keyOf = value => `${String(value?.name ?? '').trim().toLowerCase()}|${String(value?.source ?? '').trim().toLowerCase()}`;

const manifest = JSON.parse(fs.readFileSync(path.join(root, 'analysis/coverage-manifest.json'), 'utf8'));
const collection = manifest.collections.find(value => value.domain === 'monsters' && value.collection === 'monster');
if (!collection) throw new Error('No monsters/monster collection in analysis/coverage-manifest.json');
const sourceFiles = [...collection.sourceFiles].sort(cmp);
const refs = [];
const sourceModes = new Map();
const unresolvedSourceModes = [];
const unresolvedTemplateModes = [];
let scannedMonsterCount = 0;
const datasetMetaKeys = new Set();
const datasetRevisionLikeMetaKeys = new Set();

function countMode(mode, target, counter) {
  const byTarget = counter.get(mode) ?? new Map();
  byTarget.set(target, (byTarget.get(target) ?? 0) + 1);
  counter.set(mode, byTarget);
}

function collectTemplateReferences(value, currentPath, monster, file, index) {
  if (!value || typeof value !== 'object') return;
  if (Array.isArray(value)) {
    value.forEach((child, childIndex) => collectTemplateReferences(child, `${currentPath}[${childIndex}]`, monster, file, index));
    return;
  }
  for (const [property, child] of Object.entries(value)) {
    const propertyPath = `${currentPath}.${property}`;
    if (property === '_templates') {
      if (!Array.isArray(child)) {
        unresolvedSourceModes.push({ path: `${file}:monster[${index}]${propertyPath}`, shape: child, problem: '_templates must be an array' });
        continue;
      }
      for (const template of child) refs.push({ name: template.name ?? null, source: template.source ?? null,
        path: propertyPath, monster: { name: monster.name ?? null, source: monster.source ?? null, file, index } });
      continue;
    }
    collectTemplateReferences(child, propertyPath, monster, file, index);
  }
}

function getOperations(target, value, context) {
  if (value === 'remove') return [{ mode: 'removeProperty', payload: value }];
  if (value && !Array.isArray(value) && typeof value === 'object' && typeof value.mode === 'string') {
    return [{ mode: value.mode, payload: value }];
  }
  if (Array.isArray(value)) {
    return value.map((payload, index) => {
      if (!payload || typeof payload !== 'object' || typeof payload.mode !== 'string') {
        const unresolved = context.startsWith('template:') ? unresolvedTemplateModes : unresolvedSourceModes;
        unresolved.push({ path: `${context}[${index}]`, target, shape: payload });
        return null;
      }
      return { mode: payload.mode, payload };
    }).filter(Boolean);
  }
  if (value && !Array.isArray(value) && typeof value === 'object') {
    const entries = Object.entries(value);
    if (entries.length && entries.every(([, payload]) => payload && typeof payload === 'object')) {
      return entries.map(([mode, payload]) => ({ mode, payload }));
    }
  }
  const unresolved = context.startsWith('template:') ? unresolvedTemplateModes : unresolvedSourceModes;
  unresolved.push({ path: context, target, shape: value });
  return [];
}

for (const file of sourceFiles) {
  const document = JSON.parse(fs.readFileSync(path.join(dataRoot, file), 'utf8'));
  scannedMonsterCount += document.monster?.length ?? 0;
  for (const key of Object.keys(document._meta ?? {})) {
    datasetMetaKeys.add(key);
    if (/revision|commit|version|generated|timestamp|release/i.test(key)) datasetRevisionLikeMetaKeys.add(key);
  }
  for (const [index, monster] of (document.monster ?? []).entries()) {
    collectTemplateReferences(monster, 'monster', monster, file, index);
    const mods = [];
    if (monster._copy?._mod) mods.push({ path: '_copy._mod', value: monster._copy._mod });
    for (const [versionIndex, version] of (monster._versions ?? []).entries()) {
      if (version?._mod) mods.push({ path: `_versions[${versionIndex}]._mod`, value: version._mod });
      if (version?._abstract?._mod) mods.push({ path: `_versions[${versionIndex}]._abstract._mod`, value: version._abstract._mod });
    }
    for (const mod of mods) for (const [target, value] of Object.entries(mod.value)) {
      for (const operation of getOperations(target, value, `${file}:monster[${index}].${mod.path}.${target}`)) {
        countMode(operation.mode, target, sourceModes);
      }
    }
  }
}

const templatePath = path.join(dataRoot, 'monsters/template.json');
const templateDocument = JSON.parse(fs.readFileSync(templatePath, 'utf8'));
const templateDefinitions = templateDocument.monsterTemplate ?? [];
const templateSha256 = createHash('sha256').update(fs.readFileSync(templatePath)).digest('hex').toUpperCase();
const expectedTemplateSha256 = 'F3675FA8AB755D520A4A22D7837B84B474E0E50CACD1294DB50F6FC3FFAE2902';
if (templateSha256 !== expectedTemplateSha256) throw new Error(`Template asset SHA-256 mismatch: ${templateSha256}`);
const templateAssetMetaKeys = Object.keys(templateDocument._meta ?? {}).sort(cmp);
const defsByKey = new Map();
for (const [index, definition] of templateDefinitions.entries()) {
  const key = keyOf(definition);
  const list = defsByKey.get(key) ?? [];
  list.push({ definition, index });
  defsByKey.set(key, list);
}
const duplicateTemplateKeys = [...defsByKey].filter(([, values]) => values.length > 1).map(([key]) => key).sort(cmp);

const resolving = new Set();
const resolvedTemplates = new Map();
const templateCopyModes = new Map();
const templateCopyEdges = [];
const templateResolutionErrors = [];
const preserveGatedKeys = new Set(['page', 'otherSources', 'referenceSources', 'srd', 'srd52', 'basicRules',
  'basicRules2024', 'reprintedAs', 'hasFluff', 'hasFluffImages', 'hasToken', 'tokenCredit', 'tokenCustom',
  'foundryTokenScale', 'altArt', '_versions']);
function resolveTemplate(key) {
  if (resolvedTemplates.has(key)) return structuredClone(resolvedTemplates.get(key));
  const matches = defsByKey.get(key) ?? [];
  if (matches.length !== 1) throw new Error(`Template ${key} resolves to ${matches.length} definitions`);
  if (resolving.has(key)) throw new Error(`Template _copy cycle at ${key}`);
  resolving.add(key);
  const { definition, index } = matches[0];
  const child = structuredClone(definition);
  if (child._copy) {
    const parentKey = keyOf(child._copy);
    templateCopyEdges.push({ child: key, parent: parentKey, definitionIndex: index });
    if (!defsByKey.has(parentKey)) throw new Error(`Template ${key} is missing _copy parent ${parentKey}`);
    const parent = resolveTemplate(parentKey);
    const originalChildKeys = new Set(Object.keys(child));
    for (const [property, value] of Object.entries(parent)) {
      if (child[property] === null) { delete child[property]; continue; }
      if (child[property] == null) {
        if (preserveGatedKeys.has(property)) {
          if (child._copy._preserve?.['*'] || child._copy._preserve?.[property]) child[property] = value;
        } else child[property] = value;
      }
    }
    for (const [target, value] of Object.entries(child._copy._mod ?? {})) {
      for (const operation of getOperations(target, value, `template:${key}._copy._mod.${target}`)) {
        countMode(operation.mode, target, templateCopyModes);
        if (operation.mode === 'setProp') {
          const pathParts = String(operation.payload.prop).split('.');
          let cursor = child;
          for (const part of pathParts.slice(0, -1)) cursor = cursor[part] ??= {};
          cursor[pathParts.at(-1)] = structuredClone(operation.payload.value);
        } else {
          templateResolutionErrors.push({ template: key, unsupportedInternalCopyMode: operation.mode, target });
        }
      }
    }
    delete child._copy;
    child.__resolvedFromInternalCopy = parentKey;
    child.__originalChildProperties = [...originalChildKeys].sort(cmp);
  }
  resolving.delete(key);
  resolvedTemplates.set(key, structuredClone(child));
  return child;
}

for (const definition of templateDefinitions.filter(value => value._copy)) resolveTemplate(keyOf(definition));

const referenceCounts = new Map();
for (const ref of refs) {
  const key = keyOf(ref);
  const record = referenceCounts.get(key) ?? { name: ref.name, source: ref.source, count: 0, monsters: [] };
  record.count++;
  record.monsters.push(ref.monster);
  referenceCounts.set(key, record);
}
const referencedKeys = [...referenceCounts.keys()].sort(cmp);
const referenceLocationCounts = new Map();
for (const ref of refs) referenceLocationCounts.set(ref.path, (referenceLocationCounts.get(ref.path) ?? 0) + 1);
const missing = [], ambiguous = [];
const templateModesByDefinition = new Map();
const templateModesByReference = new Map();
const templateModeExamples = new Map();
const referencedDefinitions = [];
const templateRootFields = new Map();
const applyShapeCounts = new Map();
const propertyPaths = new Map();

for (const key of referencedKeys) {
  const refsForKey = referenceCounts.get(key);
  const candidates = defsByKey.get(key) ?? [];
  if (!candidates.length) { missing.push(refsForKey); continue; }
  if (candidates.length > 1) { ambiguous.push({ ...refsForKey, definitions: candidates.map(candidate => candidate.index) }); continue; }
  const { definition, index } = candidates[0];
  const effective = resolveTemplate(key);
  const rootFields = Object.keys(effective.apply?._root ?? {}).sort(cmp);
  const modFields = Object.keys(effective.apply?._mod ?? {}).sort(cmp);
  const signature = { name: definition.name, source: definition.source, index,
    internalCopyParent: definition._copy ? { name: definition._copy.name, source: definition._copy.source } : null,
    applyKeys: Object.keys(effective.apply ?? {}).sort(cmp), rootFields, modifierTargets: modFields };
  referencedDefinitions.push(signature);
  const shape = Object.keys(effective.apply ?? {}).sort(cmp).join('+') || '(no apply)';
  applyShapeCounts.set(shape, (applyShapeCounts.get(shape) ?? 0) + 1);
  for (const field of rootFields) templateRootFields.set(field, (templateRootFields.get(field) ?? 0) + 1);
  for (const [target, value] of Object.entries(effective.apply?._mod ?? {})) {
    const operations = getOperations(target, value, `template:${definition.name}|${definition.source}.apply._mod.${target}`);
    for (const operation of operations) {
      countMode(operation.mode, target, templateModesByDefinition);
      const modeExamples = templateModeExamples.get(operation.mode) ?? [];
      if (modeExamples.length < 12) modeExamples.push({ template: { name: definition.name, source: definition.source }, target,
        referencingMonsters: refsForKey.monsters.slice(0, 5) });
      templateModeExamples.set(operation.mode, modeExamples);
      for (let occurrence = 0; occurrence < refsForKey.count; occurrence++) {
        countMode(operation.mode, target, templateModesByReference);
      }
    }
  }
  for (const field of Object.keys(definition).sort(cmp)) propertyPaths.set(field, (propertyPaths.get(field) ?? 0) + 1);
}

const modes = new Set([...sourceModes.keys(), ...templateModesByDefinition.keys(), ...templateCopyModes.keys()]);
const allModifierModes = [...modes].sort(cmp).map(mode => ({
  mode,
  monsterDefinedOccurrences: [...(sourceModes.get(mode)?.values() ?? [])].reduce((a, b) => a + b, 0),
  templateDefinitionOccurrences: [...(templateModesByDefinition.get(mode)?.values() ?? [])].reduce((a, b) => a + b, 0),
  templateApplicationOccurrences: [...(templateModesByReference.get(mode)?.values() ?? [])].reduce((a, b) => a + b, 0),
  templateInternalCopyOccurrences: [...(templateCopyModes.get(mode)?.values() ?? [])].reduce((a, b) => a + b, 0),
  templateTargets: sortObject(templateModesByDefinition.get(mode) ?? new Map()),
  monsterTargets: sortObject(sourceModes.get(mode) ?? new Map()),
}));

const sortRefs = values => values.sort((a, b) => cmp(`${a.file}|${String(a.index).padStart(5, '0')}|${a.name}|${a.source}`,
  `${b.file}|${String(b.index).padStart(5, '0')}|${b.name}|${b.source}`));
const write = (name, data) => fs.writeFileSync(path.join(here, name), `${JSON.stringify(data, null, 2)}\n`);
write('template-inventory.json', {
  dataset: { manifestMonsterCount: collection.entityCount, scannedMonsterCount, sourceFileCount: sourceFiles.length,
    upstreamDatasetRevision: null, upstreamDatasetRevisionStatus: 'not present in source data or coverage manifest',
    monsterMetaKeys: [...datasetMetaKeys].sort(cmp), revisionLikeMonsterMetaKeys: [...datasetRevisionLikeMetaKeys].sort(cmp),
    coverageManifestTopLevelKeys: Object.keys(manifest).sort(cmp) },
  templateAsset: { path: 'src/main/resources/data/monsters/template.json', upstreamRepository: 'https://github.com/5etools-mirror-3/5etools-src',
    releaseTag: 'v2.37.0', revision: '8c026b807fac21862a309379a0c3228a0683198b',
    upstreamPath: 'data/bestiary/template.json', sha256: templateSha256,
    templateDefinitionCount: templateDefinitions.length, assetMetaKeys: templateAssetMetaKeys },
  referenceCounts: { occurrences: refs.length, distinctNameSourcePairs: referencedKeys.length,
    resolvedPairs: referencedDefinitions.length, missingPairs: missing.length, ambiguousPairs: ambiguous.length,
    locations: sortObject(referenceLocationCounts) },
  references: [...referenceCounts.values()].sort((a, b) => cmp(keyOf(a), keyOf(b))).map(record => ({ ...record, monsters: sortRefs(record.monsters) })),
  missing, ambiguous, duplicateTemplateKeys,
  internalCopyReferenceCounts: { definitions: templateDefinitions.filter(value => value._copy).length,
    edges: templateCopyEdges.length, resolvedEdges: templateCopyEdges.length - templateResolutionErrors.length,
    resolutionErrors: templateResolutionErrors.length },
  referencedDefinitions: referencedDefinitions.sort((a, b) => cmp(`${a.name}|${a.source}`, `${b.name}|${b.source}`)),
  templateStructure: { rootPropertyCounts: sortObject(templateRootFields), referencedDefinitionPropertyCounts: sortObject(propertyPaths),
    effectiveApplyShapeCounts: sortObject(applyShapeCounts), malformedModifierInstructions: unresolvedTemplateModes,
    internalCopyEdges: templateCopyEdges.sort((a, b) => cmp(a.child, b.child)),
    internalCopyModeCounts: sortObject(new Map([...templateCopyModes].map(([mode, counts]) => [mode, [...counts.values()].reduce((a, b) => a + b, 0)]))),
    internalCopyResolutionErrors: templateResolutionErrors,
    unclassifiedMonsterModifierInstructions: unresolvedSourceModes },
  modifierCounts: { modes: allModifierModes,
    templateDefinedModes: [...new Set([...templateModesByDefinition.keys(), ...templateCopyModes.keys()])].sort(cmp),
    templateIntroducedModes: [...new Set([...templateModesByDefinition.keys(), ...templateCopyModes.keys()])]
      .filter(mode => !sourceModes.has(mode)).sort(cmp),
    templateModeExamples: Object.fromEntries([...templateModeExamples].sort(([a], [b]) => cmp(a, b))) },
});

if (scannedMonsterCount !== collection.entityCount || missing.length || ambiguous.length || duplicateTemplateKeys.length
    || unresolvedTemplateModes.length || unresolvedSourceModes.length || templateResolutionErrors.length) {
  process.exitCode = 1;
}
