import crypto from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';

const directory = 'analysis/mechanics/batch-9b-class-choice-review';
const dataDirectory = 'src/main/resources/data';
const protectedPaths = [
  'src',
  'analysis/mechanics/batch-9-class-completeness',
  'analysis/mechanics/batch-9a-class-choice-completeness',
  'analysis/mechanics/entry-assessment',
  'analysis/mechanics/IMPLEMENTATION-ORDER.md',
];

function filesUnder(target) {
  if (!fs.statSync(target).isDirectory()) {
    return [target];
  }
  return fs.readdirSync(target, { withFileTypes: true }).flatMap((entry) =>
    filesUnder(path.posix.join(target, entry.name))).sort();
}

function sha256(value) {
  return crypto.createHash('sha256').update(value).digest('hex');
}

function scopeSnapshot() {
  return Object.fromEntries(protectedPaths.map((target) => {
    const entries = filesUnder(target).map((file) => [file, sha256(fs.readFileSync(file))]);
    return [target, { fileCount: entries.length, sha256: sha256(JSON.stringify(entries)) }];
  }));
}

fs.mkdirSync(directory, { recursive: true });
if (process.argv.includes('--snapshot')) {
  if (fs.existsSync(`${directory}/scope-baseline.json`)) {
    throw new Error('Refusing to replace the scope baseline');
  }
  fs.writeFileSync(`${directory}/scope-baseline.json`, `${JSON.stringify(scopeSnapshot(), null, 2)}\n`);
  console.log('Recorded protected source, code, test and prior evidence fingerprints');
  process.exit(0);
}

const baseline = JSON.parse(fs.readFileSync(`${directory}/scope-baseline.json`, 'utf8'));
const current = scopeSnapshot();
if (JSON.stringify(baseline) !== JSON.stringify(current)) {
  throw new Error('A protected source/code/test/prior evidence tree changed');
}

function read(file) {
  return JSON.parse(fs.readFileSync(`${dataDirectory}/${file}`, 'utf8').replace(/^\uFEFF/, ''));
}

const optionalFeatures = read('optionalfeatures.json').optionalfeature;
const feats = read('feats.json').feat;
const sourceRegistry = read('sources/registry.json').sources;
const previousAudit = JSON.parse(fs.readFileSync(
  'analysis/mechanics/batch-9-class-completeness/source-occurrences.json', 'utf8',
));
const slug = (value) => value.normalize('NFD').replace(/\p{M}/gu, '').toLowerCase()
  .replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');

function optionalFeatureSummary(record) {
  return {
    pointer: `/optionalfeature/${optionalFeatures.indexOf(record)}`,
    id: `optional-feature:${slug(record.source)}:${slug(record.name)}`,
    name: record.name,
    source: record.source,
    page: record.page ?? null,
    srd: record.srd ?? null,
    srd52: record.srd52 ?? null,
    featureType: record.featureType ?? [],
    otherSources: record.otherSources ?? [],
    reprintedAs: record.reprintedAs ?? [],
    prerequisite: record.prerequisite ?? [],
  };
}

const omittedLabels = [];
const requiredMarkers = [];
for (const [file, index, optionIndex] of [
  ['class-fighter.json', 55, 5],
  ['class-monk.json', 8, 1],
]) {
  const owner = read(`classes/${file}`).subclassFeature[index];
  owner.entries[optionIndex].entries.forEach((reference, referenceIndex) => {
    const pointer = `/subclassFeature/${index}/entries/${optionIndex}/entries/${referenceIndex}`;
    const parts = reference.optionalfeature.split('|');
    const target = optionalFeatures.find((record) =>
      record.name === parts[0] && record.source === (parts[1] || 'PHB'));
    if (!target) {
      throw new Error(`Missing target for ${file}#${pointer}`);
    }
    if (reference.name !== undefined) {
      omittedLabels.push({
        file,
        pointer: `${pointer}/name`,
        ownerName: owner.name,
        ownerSource: owner.source,
        token: reference.optionalfeature,
        authoredLabel: reference.name,
        target: optionalFeatureSummary(target),
      });
    }
    if (reference.data?.isRequiredOption !== undefined) {
      requiredMarkers.push({
        file,
        pointer: `${pointer}/data/isRequiredOption`,
        ownerName: owner.name,
        token: reference.optionalfeature,
        value: reference.data.isRequiredOption,
        target: optionalFeatureSummary(target),
      });
    }
  });
}

const wizard = read('classes/class-wizard.json');
const withheldChoices = [27, 58].map((index) => {
  const owner = wizard.subclassFeature[index];
  const options = owner.entries[1];
  return {
    file: 'class-wizard.json',
    pointer: `/subclassFeature/${index}/entries/1`,
    ownerName: owner.name,
    ownerSource: owner.source,
    page: owner.page,
    authoredCountPresent: Object.hasOwn(options, 'count'),
    authoredStylePresent: Object.hasOwn(options, 'style'),
    authoredAlternativeCount: options.entries.length,
    children: options.entries.map((entry, entryIndex) => ({
      pointer: `/subclassFeature/${index}/entries/1/entries/${entryIndex}`,
      type: entry.type,
      name: entry.name,
      entryCount: entry.entries?.length ?? 0,
    })),
    publicStatus: 'WITHHELD',
  };
});

const bardOwner = read('classes/class-bard.json').subclassFeature[38];
const xgeReferences = bardOwner.entries[1].entries.map((reference, index) => {
  const name = reference.optionalfeature;
  const matches = optionalFeatures.filter((target) => target.name.toLowerCase() === name.toLowerCase());
  return {
    file: 'class-bard.json',
    pointer: `/subclassFeature/38/entries/1/entries/${index}/optionalfeature`,
    ownerName: bardOwner.name,
    ownerSource: bardOwner.source,
    ownerPage: bardOwner.page,
    ownerClass: bardOwner.className,
    ownerSubclass: bardOwner.subclassShortName,
    ownerLevel: bardOwner.level,
    token: name,
    authoredSource: null,
    defaultedSource: 'PHB',
    optionalFeatureNameMatches: matches.map(optionalFeatureSummary),
    sameNameFeatRecords: feats.flatMap((target, targetIndex) => target.name.toLowerCase() === name.toLowerCase()
      ? [{ pointer: `/feat/${targetIndex}`, name: target.name, source: target.source, page: target.page }]
      : []),
    explicitSourceLookups: Object.fromEntries(['PHB', 'XGE', 'XPHB'].map((source) => [source,
      matches.filter((target) => target.source === source).map(optionalFeatureSummary)])),
  };
});

const unqualifiedReferences = previousAudit.optionOccurrences.flatMap((option) =>
  option.references.filter((reference) => reference.type === 'refOptionalfeature' && reference.authoredSource === null)
    .map((reference) => ({
      file: option.file,
      pointer: reference.pointer,
      ownerName: option.owner.name,
      ownerSource: option.owner.source,
      token: reference.token,
      sameNamePrimarySources: optionalFeatures.filter((target) =>
        target.name.toLowerCase() === reference.token.toLowerCase()).map((target) => target.source).sort(),
    })));

const oldHashes = JSON.parse(fs.readFileSync(
  'analysis/mechanics/batch-9a-class-choice-completeness/pre-implementation-hashes.json', 'utf8',
));
const sourceHashes = Object.entries(oldHashes).filter(([file]) =>
  file.startsWith(`${dataDirectory}/`) && file.endsWith('.json'));
for (const [file, expected] of sourceHashes) {
  if (sha256(fs.readFileSync(file)) !== expected) {
    throw new Error(`Pinned source changed: ${file}`);
  }
}
const evidence = {
  omittedLabels,
  requiredMarkers,
  withheldChoices,
  xgeReferences,
  registeredSources: sourceRegistry.filter((source) => ['PHB', 'TCE', 'XGE', 'XPHB'].includes(source.code)),
  unqualifiedReferenceCount: unqualifiedReferences.length,
  uniqueUnqualifiedNames: new Set(unqualifiedReferences.map((reference) => reference.token)).size,
  unqualifiedWithMultiplePrimarySourceMatches: unqualifiedReferences.filter((reference) =>
    reference.sameNamePrimarySources.length > 1).length,
  unqualifiedReferences,
};
if (omittedLabels.length !== 11 || requiredMarkers.length !== 1 || unqualifiedReferences.length !== 60
    || sourceHashes.length !== 175) {
  throw new Error('Source inventory did not reconcile to the established baseline');
}
fs.writeFileSync(`${directory}/source-evidence.json`, `${JSON.stringify(evidence, null, 2)}\n`);
fs.writeFileSync(`${directory}/verification.json`, `${JSON.stringify({
  unchangedSourceJsonFiles: sourceHashes.length,
  protectedTreesUnchanged: current,
  productionTestResolverAndPriorAuditChanges: 0,
}, null, 2)}\n`);
console.log(JSON.stringify({
  omittedLabels: omittedLabels.length,
  requiredMarkers: requiredMarkers.length,
  withheldChoiceGroups: withheldChoices.length,
  xgeReferenceTokens: xgeReferences.map((reference) => reference.token),
  unqualifiedReferenceCount: evidence.unqualifiedReferenceCount,
  uniqueUnqualifiedNames: evidence.uniqueUnqualifiedNames,
  unqualifiedWithMultiplePrimarySourceMatches: evidence.unqualifiedWithMultiplePrimarySourceMatches,
  unchangedSourceJsonFiles: sourceHashes.length,
  protectedFilesUnchanged: true,
}, null, 2));
