import fs from 'node:fs';
import path from 'node:path';

const rootDirectory = 'src/main/resources/data/classes';
const classFiles = fs.readdirSync(rootDirectory)
  .filter((file) => file.endsWith('.json'))
  .sort();

const abilityOccurrences = [];
const optionOccurrences = [];
const sourceRecords = [];

function walkEntries(entries, owner, pointer, file, optionAncestors = []) {
  (entries ?? []).forEach((entry, index) => {
    const entryPointer = `${pointer}/${index}`;
    if (typeof entry === 'string' || entry === null || typeof entry !== 'object') {
      return;
    }

    if (entry.type === 'abilityDc' || entry.type === 'abilityAttackMod') {
      abilityOccurrences.push({
        file,
        owner,
        pointer: entryPointer,
        tag: entry.type,
        name: entry.name ?? null,
        attributes: entry.attributes ?? null,
      });
    }

    if (entry.type === 'options') {
      optionOccurrences.push(summarizeOption(entry, owner, entryPointer, file, optionAncestors));
    }

    const children = entry.entries ?? entry.items;
    if (Array.isArray(children)) {
      walkEntries(
        children,
        owner,
        `${entryPointer}/${entry.entries ? 'entries' : 'items'}`,
        file,
        entry.type === 'options'
          ? [...optionAncestors, { pointer: entryPointer, count: entry.count ?? null, style: entry.style ?? null }]
          : optionAncestors,
      );
    }
  });
}

function summarizeOption(option, owner, pointer, file, ancestors) {
  const references = [];
  const textLeafPointers = [];
  const otherNodeTypes = [];
  const structuredNodes = [];

  function visit(entries, entryPointer) {
    (entries ?? []).forEach((entry, index) => {
      const currentPointer = `${entryPointer}/${index}`;
      if (typeof entry === 'string') {
        textLeafPointers.push(currentPointer);
        return;
      }
      if (!entry || typeof entry !== 'object') {
        otherNodeTypes.push({ pointer: currentPointer, type: entry === null ? 'null' : typeof entry });
        return;
      }

      if (entry.type !== 'options') {
        structuredNodes.push({
          pointer: currentPointer,
          type: entry.type ?? 'unknown',
          name: entry.name ?? null,
          hasEntryText: typeof entry.entry === 'string',
          entryCount: entry.entries?.length ?? null,
          itemCount: entry.items?.length ?? null,
        });
      }

      const referenceFields = {
        refClassFeature: 'classFeature',
        refSubclassFeature: 'subclassFeature',
        refOptionalfeature: 'optionalfeature',
        refFeat: 'feat',
      };
      const referenceField = referenceFields[entry.type];
      if (referenceField) {
        references.push({
          pointer: currentPointer,
          type: entry.type,
          token: entry[referenceField],
          name: entry.name ?? null,
          preserve: entry.preserve ?? null,
          data: entry.data ?? null,
        });
        return;
      }

      const children = entry.entries ?? entry.items;
      if (Array.isArray(children)) {
        visit(children, `${currentPointer}/${entry.entries ? 'entries' : 'items'}`);
        return;
      }
      otherNodeTypes.push({ pointer: currentPointer, type: entry.type ?? 'unknown' });
    });
  }

  visit(option.entries, `${pointer}/entries`);

  return {
    file,
    owner,
    pointer,
    count: option.count ?? null,
    style: option.style ?? null,
    immediateChildCount: option.entries?.length ?? 0,
    nestedUnder: ancestors,
    references,
    textLeafPointers,
    structuredNodes,
    otherNodeTypes,
  };
}

for (const file of classFiles) {
  const document = JSON.parse(fs.readFileSync(path.join(rootDirectory, file), 'utf8'));
  for (const collection of ['classFeature', 'subclassFeature']) {
    (document[collection] ?? []).forEach((record, index) => {
      const owner = {
        collection,
        name: record.name,
        source: record.source,
        className: record.className,
        classSource: record.classSource,
        subclassShortName: record.subclassShortName ?? null,
        subclassSource: record.subclassSource ?? null,
        level: record.level ?? null,
      };
      sourceRecords.push({ file, collection, index, record, owner });
      walkEntries(record.entries, owner, `/${collection}/${index}/entries`, file);
    });
  }
}

function featureTargetKey(kind, name, source, className, classSource, subclassShortName, subclassSource, level) {
  return [kind, name, source, className, classSource, subclassShortName ?? '', subclassSource ?? '', level ?? '']
    .join('|')
    .toLowerCase();
}

const classFeatureIdentities = new Set(sourceRecords.map(({ collection, record }) => featureTargetKey(
  collection === 'classFeature' ? 'refClassFeature' : 'refSubclassFeature',
  record.name,
  record.source,
  record.className,
  record.classSource,
  record.subclassShortName,
  record.subclassSource,
  record.level,
)));
const optionalFeatureDocument = JSON.parse(
  fs.readFileSync('src/main/resources/data/optionalfeatures.json', 'utf8'),
);
const optionalFeatureMatches = new Map();
for (const record of optionalFeatureDocument.optionalfeature ?? []) {
  const key = `${record.name}|${record.source}`.toLowerCase();
  optionalFeatureMatches.set(key, (optionalFeatureMatches.get(key) ?? 0) + 1);
}

function annotateReferenceResolution(reference) {
  const parts = reference.token.split('|');
  const authoredSourceIndex = reference.type === 'refClassFeature' ? 4
    : reference.type === 'refSubclassFeature' ? 6 : 1;
  const authoredSource = parts.length > authoredSourceIndex && parts[authoredSourceIndex]
    ? parts[authoredSourceIndex]
    : null;
  if (reference.type === 'refOptionalfeature') {
    const source = authoredSource ?? 'PHB';
    const matchCount = optionalFeatureMatches.get(`${parts[0]}|${source}`.toLowerCase()) ?? 0;
    return {
      ...reference,
      authoredSource,
      resolvedSource: source,
      resolutionStatus: matchCount === 1 ? 'RESOLVED' : matchCount === 0 ? 'UNRESOLVED' : 'AMBIGUOUS',
      exactSourceRecordMatches: matchCount,
      resolutionNote: authoredSource ? null : 'ClassExternalReferenceResolver defaults optional-feature source to PHB',
    };
  }

  const subclass = reference.type === 'refSubclassFeature';
  const className = parts[1] ?? '';
  const classSource = parts[2] || 'PHB';
  const subclassShortName = subclass ? parts[3] ?? '' : '';
  const subclassSource = subclass ? parts[4] || 'PHB' : '';
  const level = parts[subclass ? 5 : 3] ?? '';
  const source = parts[subclass ? 6 : 4] || (subclass ? subclassSource : classSource);
  const key = featureTargetKey(reference.type, parts[0], source, className, classSource,
    subclassShortName, subclassSource, level);
  const match = classFeatureIdentities.has(key);
  return {
    ...reference,
    authoredSource,
    resolvedSource: source,
    resolutionStatus: match ? 'RESOLVED' : 'UNRESOLVED',
    exactSourceRecordMatches: match ? 1 : 0,
    resolutionNote: 'ClassReferenceResolver applies its documented PHB fallback to blank UID source fields',
  };
}

for (const option of optionOccurrences) {
  option.references = option.references.map(annotateReferenceResolution);
}

function recordIdentity(record) {
  return [
    record.collection,
    record.record.name,
    record.record.source,
    record.record.className,
    record.record.classSource,
    record.record.subclassShortName ?? '',
    record.record.subclassSource ?? '',
    record.record.level ?? '',
  ].join('|').toLowerCase();
}

const sourceRecordIndex = new Map(sourceRecords.map((record) => [recordIdentity(record), record]));

function effectiveRecord(record, active = new Set()) {
  const identity = recordIdentity(record);
  if (active.has(identity)) {
    throw new Error(`Copy cycle while auditing ${record.file} ${record.record.name}`);
  }

  const copy = record.record._copy;
  if (!copy) {
    return { entries: record.record.entries, origin: record, inheritedEntries: false };
  }

  const baseIdentity = [
    record.collection,
    copy.name,
    copy.source,
    copy.className,
    copy.classSource,
    copy.subclassShortName ?? '',
    copy.subclassSource ?? '',
    copy.level ?? '',
  ].join('|').toLowerCase();
  const base = sourceRecordIndex.get(baseIdentity);
  if (!base) {
    throw new Error(`Missing copy base while auditing ${record.file} ${record.record.name}`);
  }

  active.add(identity);
  const inherited = effectiveRecord(base, active);
  active.delete(identity);
  if (Object.hasOwn(record.record, 'entries')) {
    return { entries: record.record.entries, origin: record, inheritedEntries: false };
  }
  return { ...inherited, inheritedEntries: true };
}

function countObservedEntries(entries) {
  const counts = { abilityDc: 0, abilityAttackMod: 0, options: 0 };
  function visit(values) {
    (values ?? []).forEach((entry) => {
      if (!entry || typeof entry !== 'object') {
        return;
      }
      if (entry.type === 'abilityDc' || entry.type === 'abilityAttackMod') {
        counts[entry.type] += 1;
      }
      if (entry.type === 'options') {
        counts.options += 1;
      }
      visit(entry.entries);
      visit(entry.items);
    });
  }
  visit(entries);
  return counts;
}

const effectiveOwners = [];
const inheritedObservationOwners = [];
for (const record of sourceRecords) {
  const effective = effectiveRecord(record);
  const counts = countObservedEntries(effective.entries);
  effectiveOwners.push({ file: record.file, owner: record.owner, counts });
  if (effective.inheritedEntries && (counts.abilityDc + counts.abilityAttackMod + counts.options > 0)) {
    inheritedObservationOwners.push({
      file: record.file,
      owner: record.owner,
      originFile: effective.origin.file,
      originOwner: effective.origin.owner,
      counts,
    });
  }
}

const effectiveCounts = effectiveOwners.reduce((total, current) => ({
  abilityDc: total.abilityDc + current.counts.abilityDc,
  abilityAttackMod: total.abilityAttackMod + current.counts.abilityAttackMod,
  options: total.options + current.counts.options,
}), { abilityDc: 0, abilityAttackMod: 0, options: 0 });

const allOptions = optionOccurrences;
const allOptionReferences = allOptions.flatMap((option) => option.references);
const optionReferenceCounts = Object.fromEntries(
  [...new Set(allOptionReferences.map((reference) => reference.type))]
    .sort()
    .map((type) => [type, allOptionReferences.filter((reference) => reference.type === type).length]),
);
const optionReferenceResolutions = Object.fromEntries(
  [...new Set(allOptionReferences.map((reference) => reference.resolutionStatus))]
    .sort()
    .map((status) => [status, allOptionReferences.filter((reference) => reference.resolutionStatus === status).length]),
);
const optionCountDistribution = Object.fromEntries(
  [...new Set(allOptions.map((option) => option.count ?? '(absent)'))]
    .sort((left, right) => String(left).localeCompare(String(right), undefined, { numeric: true }))
    .map((count) => [count, allOptions.filter((option) => (option.count ?? '(absent)') === count).length]),
);
const publicShapeClasses = {
  referenceOnly: allOptions.filter((option) => option.references.length > 0
    && option.textLeafPointers.length === 0 && option.otherNodeTypes.length === 0).length,
  textOnly: allOptions.filter((option) => option.references.length === 0
    && option.textLeafPointers.length > 0).length,
  mixedOrOther: allOptions.filter((option) => option.references.length > 0
    && (option.textLeafPointers.length > 0 || option.otherNodeTypes.length > 0)
    || option.references.length === 0 && option.textLeafPointers.length === 0).length,
};

const result = {
  sourceFileCount: classFiles.length,
  abilityOccurrenceCount: abilityOccurrences.length,
  abilityTagCounts: Object.fromEntries(['abilityDc', 'abilityAttackMod'].map((tag) => [
    tag,
    abilityOccurrences.filter((occurrence) => occurrence.tag === tag).length,
  ])),
  abilityOccurrences,
  optionOccurrenceCount: allOptions.length,
  optionCountDistribution,
  optionReferenceCount: allOptionReferences.length,
  optionReferenceCounts,
  optionReferenceResolutions,
  publicShapeClasses,
  optionOccurrences: allOptions,
  effectiveOwnerCount: effectiveOwners.length,
  effectiveCounts,
  inheritedObservationOwners,
};

const outputDirectory = 'analysis/mechanics/batch-9-class-completeness';
fs.mkdirSync(outputDirectory, { recursive: true });
fs.writeFileSync(`${outputDirectory}/source-occurrences.json`, `${JSON.stringify(result, null, 2)}\n`);
console.log(JSON.stringify({
  sourceFileCount: result.sourceFileCount,
  abilityOccurrenceCount: result.abilityOccurrenceCount,
  abilityTagCounts: result.abilityTagCounts,
  optionOccurrenceCount: result.optionOccurrenceCount,
  optionCountDistribution: result.optionCountDistribution,
  optionReferenceCount: result.optionReferenceCount,
  optionReferenceCounts: result.optionReferenceCounts,
  optionReferenceResolutions: result.optionReferenceResolutions,
  publicShapeClasses: result.publicShapeClasses,
  effectiveOwnerCount: result.effectiveOwnerCount,
  effectiveCounts: result.effectiveCounts,
  inheritedObservationOwnerCount: result.inheritedObservationOwners.length,
}, null, 2));
