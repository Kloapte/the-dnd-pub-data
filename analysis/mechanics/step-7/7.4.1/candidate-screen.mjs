import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';

const dataDirectory = 'src/main/resources/data/spells';
const selectedPatterns = new Map([
  ['spell:phb:burning-hands', 'damage-on-failed-save-v1'],
  ['spell:phb:cone-of-cold', 'damage-on-failed-save-v1'],
  ['spell:phb:shatter', 'damage-on-failed-save-v1'],
  ['spell:phb:lightning-bolt', 'damage-on-failed-save-v1'],
  ['spell:phb:circle-of-death', 'damage-on-failed-save-v1'],
  ['spell:phb:cure-wounds', 'healing-amount-v1'],
  ['spell:phb:mass-cure-wounds', 'healing-amount-v1'],
  ['spell:phb:mass-healing-word', 'healing-amount-v1'],
  ['spell:phb:prayer-of-healing', 'healing-amount-v1'],
]);

function strings(value, result = []) {
  if (typeof value === 'string') {
    result.push(value);
  } else if (Array.isArray(value)) {
    for (const child of value) {
      strings(child, result);
    }
  } else if (value && typeof value === 'object') {
    for (const child of Object.values(value)) {
      strings(child, result);
    }
  }
  return result;
}

function spellId(spell) {
  const slug = spell.name.toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/-$/g, '');
  return `spell:${spell.source.toLowerCase()}:${slug}`;
}

const screened = [];
const selected = [];
const spellFiles = fs.readdirSync(dataDirectory)
  .filter((name) => name.endsWith('.json'))
  .sort();

for (const fileName of spellFiles) {
  const filePath = path.join(dataDirectory, fileName);
  const resource = JSON.parse(fs.readFileSync(filePath, 'utf8'));
  for (const [sourceIndex, spell] of (resource.spell ?? []).entries()) {
    const text = strings([spell.entries, spell.entriesHigherLevel]).join('\n');
    const hasFormulaSyntax = /\{@(?:damage|dice|scaledamage|scaledice)\b/.test(text);
    const hasMechanicalContext = (spell.damageInflict ?? []).length > 0
      || (spell.savingThrow ?? []).length > 0
      || /regains? (?:a number of )?hit points/i.test(text);
    if (!hasFormulaSyntax || !hasMechanicalContext) {
      continue;
    }

    const id = spellId(spell);
    screened.push(id);
    if (selectedPatterns.has(id)) {
      selected.push({
        id,
        source: spell.source,
        name: spell.name,
        resourcePath: filePath.replaceAll('\\', '/'),
        jsonPointer: `/spell/${sourceIndex}`,
        patternId: selectedPatterns.get(id),
      });
    }
  }
}

assert.equal(screened.length, 372, 'Broad formula-plus-context candidate screen changed');
assert.equal(selected.length, selectedPatterns.size, 'Selected owner set did not reconcile');
assert.equal(new Set(screened).size, screened.length, 'Candidate owner IDs must be unique');

process.stdout.write(`${JSON.stringify({
  schemaVersion: 1,
  corpusResourceCount: spellFiles.length,
  broadScreenCount: screened.length,
  selectedAdditionalOwnerCount: selected.length,
  selected,
  screeningIsNotExtractionEvidence: true,
}, null, 2)}\n`);
