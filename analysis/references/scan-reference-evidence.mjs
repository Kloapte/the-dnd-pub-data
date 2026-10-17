import { readFile } from 'node:fs/promises';
import { resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const scriptDirectory = fileURLToPath(new URL('.', import.meta.url));
const evidencePath = resolve(scriptDirectory, '../classes/cross-catalog-references.json');
const evidence = JSON.parse(await readFile(evidencePath, 'utf8'));

const counts = Object.fromEntries(
    ['markup', 'structured', 'selectors', 'foreignResolution'].map((key) => [key, evidence[key]?.length ?? 0]),
);

const expected = {
    markup: 5684,
    structured: 3997,
    selectors: 186,
    foreignResolution: 1658,
};

for (const [category, expectedCount] of Object.entries(expected)) {
    if (counts[category] !== expectedCount) {
        throw new Error(`${category}: expected ${expectedCount}, found ${counts[category]}`);
    }
}

const byKind = {};
for (const reference of evidence.structured ?? []) {
    const kind = reference.kind ?? 'unknown';
    byKind[kind] = (byKind[kind] ?? 0) + 1;
}

const byStatus = {};
for (const reference of evidence.foreignResolution ?? []) {
    const key = `${reference.kind ?? 'unknown'}:${reference.status ?? 'unknown'}`;
    byStatus[key] = (byStatus[key] ?? 0) + 1;
}

console.log(JSON.stringify({ counts, byKind, byStatus }, null, 2));
