const fs = require('node:fs');
const path = require('node:path');

const root = path.resolve(__dirname, '..');
const source = JSON.parse(
  fs.readFileSync(path.join(root, 'src/main/resources/data/variantrules.json'), 'utf8')
).variantrule;
const inventory = JSON.parse(
  fs.readFileSync(
    path.join(root, 'reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_SEMANTIC_INVENTORY.json'),
    'utf8'
  )
);
const sourceAccounting = JSON.parse(
  fs.readFileSync(path.join(root, 'reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json'), 'utf8')
);
const expectedIds = sourceAccounting.ruleCatalogInventory;
const patternCatalogText = fs.readFileSync(path.join(root, 'reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_PATTERN_CATALOG.md'), 'utf8');
const gapInventoryText = fs.readFileSync(path.join(root, 'reports/step-2/CORE_RULES_FOUNDATION_PHASE_5_RULE_GAP_INVENTORY.md'), 'utf8');
const statuses = new Set(['REVIEWED', 'PENDING']);
const fits = new Set(['SUPPORTED', 'PARTIAL', 'UNSUPPORTED', 'NOT_ASSESSED']);
const ruleTypes = new Set(['CORE', 'OPTIONAL', 'VARIANT', 'VARIANT_OPTIONAL']);
const errors = [];

function resolveSourcePath(locator) {
  const match = locator.match(/^\$\.variantrule\[(\d+)\](.*)$/);
  if (!match) return undefined;
  let value = source[Number(match[1])];
  const suffix = match[2];
  const tokenPattern = /\.(entries|items|rows)\[(\d+)\]/g;
  let consumed = 0;
  let token;
  while ((token = tokenPattern.exec(suffix)) !== null) {
    if (suffix.slice(consumed, token.index) !== '') return undefined;
    const list = value?.[token[1]];
    value = Array.isArray(list) ? list[Number(token[2])] : undefined;
    if (value === undefined) return undefined;
    consumed = tokenPattern.lastIndex;
  }
  return consumed === suffix.length ? value : undefined;
}

if (source.length !== 231) errors.push(`Source catalog contains ${source.length}; expected 231.`);
if (inventory.totalSourceRecords !== 231) errors.push('Inventory totalSourceRecords is not 231.');
if (inventory.records.length !== 231) errors.push(`Inventory has ${inventory.records.length} records; expected 231.`);
if (new Set(inventory.records.map((record) => record.id)).size !== inventory.records.length) {
  errors.push('Inventory contains duplicate Rule identities.');
}

for (const patternId of inventory.patternIds) {
  if (!patternCatalogText.includes('### ' + patternId + ' ')) errors.push('Pattern ID has no catalog entry: ' + patternId + '.');
  for (const record of inventory.records.filter((candidate) => candidate.mechanicalPatterns.includes(patternId))) {
    const paths = [...new Set(record.mechanics.map((mechanic) => mechanic.sourcePath))].sort().join('<br>');
    const gaps = record.gapIds.join(', ') || 'NO_GAP';
    const row = `| ${record.id} | ${paths} | ${record.contractFit} | ${gaps} |`;
    if (!patternCatalogText.includes(row)) {
      errors.push(`${patternId}: pattern catalog row is stale or missing for ${record.id}.`);
    }
  }
}
for (const gapId of inventory.gapIds) {
  if (!gapInventoryText.includes('| ' + gapId + ' |')) errors.push('Gap ID has no gap-inventory entry: ' + gapId + '.');
  const affected = inventory.records.filter((record) => record.gapIds.includes(gapId)).map((record) => record.id).sort();
  const row = `| ${gapId} | ${affected.length} | ${affected.join('<br>')} |`;
  if (!gapInventoryText.includes(row)) errors.push(`${gapId}: gap incidence row is stale or missing.`);
}
const patternIds = new Set(inventory.patternIds);
const gapIds = new Set(inventory.gapIds);
const reviewed = inventory.records.filter((record) => record.reviewStatus === 'REVIEWED').length;
const pending = inventory.records.filter((record) => record.reviewStatus === 'PENDING').length;
const fitCounts = { SUPPORTED: 0, PARTIAL: 0, UNSUPPORTED: 0, NOT_ASSESSED: 0 };

for (const [index, record] of inventory.records.entries()) {
  const expected = expectedIds[index];
  if (!expected || record.id !== expected.canonicalId) {
    errors.push(`Record ${index} does not match the expected source-qualified identity.`);
  }
  if (record.sourcePath !== `$.variantrule[${index}]` || record.sourcePath !== expected?.sourceObjectPath) {
    errors.push(`${record.id}: sourcePath does not match source index ${index}.`);
  }
  if (record.source !== source[index]?.source) errors.push(`${record.id}: source code mismatch.`);
  const expectedRuleType = expected?.canonicalRuleType ?? null;
  if (record.ruleType !== expectedRuleType) errors.push(`${record.id}: canonical ruleType mismatch.`);
  if (record.ruleType !== null && !ruleTypes.has(record.ruleType)) {
    errors.push(`${record.id}: invalid canonical ruleType ${record.ruleType}.`);
  }
  if (!statuses.has(record.reviewStatus)) errors.push(`${record.id}: invalid reviewStatus.`);
  if (!fits.has(record.contractFit)) errors.push(`${record.id}: invalid contractFit.`);
  fitCounts[record.contractFit] += 1;

  for (const field of ['mechanicalPatterns', 'mechanics', 'inputs', 'conditions', 'outcomes', 'exceptions', 'dependencies', 'gapIds']) {
    if (!Array.isArray(record[field])) errors.push(`${record.id}: ${field} must be an array.`);
  }
  for (const patternId of record.mechanicalPatterns) {
    if (!patternIds.has(patternId)) errors.push(`${record.id}: unknown pattern ${patternId}.`);
  }
  for (const gapId of record.gapIds) {
    if (!gapIds.has(gapId)) errors.push(`${record.id}: unknown gap ${gapId}.`);
  }
  for (const mechanic of record.mechanics) {
    if (!mechanic.sourcePath || !mechanic.description) {
      errors.push(`${record.id}: every mechanic needs sourcePath and description.`);
    } else if (!resolveSourcePath(mechanic.sourcePath)) {
      errors.push(`${record.id}: mechanic source path does not resolve: ${mechanic.sourcePath}.`);
    }
  }

  const mechanicObservations = new Set();
  for (const mechanic of record.mechanics) {
    const observation = `${mechanic.sourcePath}\u0000${mechanic.description}`;
    if (mechanicObservations.has(observation)) {
      errors.push(`${record.id}: duplicate mechanical observation at ${mechanic.sourcePath}.`);
    }
    mechanicObservations.add(observation);
  }

  if (record.reviewStatus === 'PENDING') {
    if (record.contractFit !== 'NOT_ASSESSED') errors.push(`${record.id}: pending record has assessed contractFit.`);
    if (record.mechanicalPatterns.length || record.mechanics.length || record.inputs.length ||
        record.conditions.length || record.outcomes.length || record.exceptions.length ||
        record.dependencies.length || record.gapIds.length) {
      errors.push(`${record.id}: pending record contains semantic findings.`);
    }
  } else if (record.contractFit === 'NOT_ASSESSED') {
    errors.push(`${record.id}: reviewed record has NOT_ASSESSED contractFit.`);
  } else {
    if (!record.mechanicalPatterns.length || !record.mechanics.length) {
      errors.push(`${record.id}: reviewed record needs source-backed mechanics and a pattern association.`);
    }
    if (record.contractFit === 'PARTIAL' && !record.gapIds.length &&
        (typeof record.fitRationale !== 'string' || !record.fitRationale.trim())) {
      errors.push(`${record.id}: PARTIAL record without a gap family needs an auditable fitRationale.`);
    }
    if (record.contractFit === 'PARTIAL' && !record.gapIds.length &&
        /remaining source-specific behavior is identified in this record|no separate rule-owned reusable gap family was assigned/i
          .test(record.fitRationale || '')) {
      errors.push(`${record.id}: fitRationale is generic and does not explain the record's contract fit.`);
    }
    if (record.fitRationale !== undefined &&
        (typeof record.fitRationale !== 'string' || !record.fitRationale.trim())) {
      errors.push(`${record.id}: fitRationale must be a non-empty string when present.`);
    }
  }
}

if (reviewed + pending !== 231) errors.push(`Review status counts reconcile to ${reviewed + pending}, not 231.`);
if (inventory.reviewedCount !== reviewed) errors.push('Top-level reviewedCount does not match record statuses.');
if (inventory.pendingCount !== pending) errors.push('Top-level pendingCount does not match record statuses.');
for (const fit of Object.keys(fitCounts)) {
  if (inventory.contractFitCounts?.[fit] !== fitCounts[fit]) {
    errors.push('Top-level contractFitCounts mismatch for ' + fit + '.');
  }
}

if (errors.length) {
  console.error(`Rule semantic inventory validation failed (${errors.length} issue(s)):`);
  for (const error of errors) console.error(`- ${error}`);
  process.exitCode = 1;
} else {
  console.log(
    `Rule semantic inventory valid: 231 identities; ${reviewed} REVIEWED, ${pending} PENDING; ` +
    `contract fit ${JSON.stringify(fitCounts)}; ${inventory.patternIds.length} patterns; ` +
    `${inventory.gapIds.length} gap families.`
  );
}
