import assert from 'node:assert/strict';
import fs from 'node:fs';
import { buildArtifacts, directory, formatted, read } from './review-artifacts.mjs';

const checkOnly = process.argv.includes('--check');
if (fs.existsSync(`${directory}/decision-journal.json`)) {
    const journal = read(`${directory}/decision-journal.json`);
    assert.equal(journal.events.length, 0, 'Refusing to overwrite human decision events; create a new review revision');
}
for (const file of ['candidate-manifest.json', 'attribution-proposal.json']) {
    const path = `${directory}/${file}`;
    if (fs.existsSync(path)) {
        const existing = read(path);
        const reviews = file === 'candidate-manifest.json'
            ? existing.entries.map((entry) => entry.review) : [existing.review];
        for (const review of reviews) {
            assert.equal(review.status, 'PENDING_REVIEW', 'Refusing to reset a reviewed artifact');
            assert.ok(Object.entries(review).every(([key, value]) => key === 'status' || value === null),
                'Refusing to erase populated decision metadata');
        }
    }
}

for (const [file, value] of buildArtifacts()) {
    const path = `${directory}/${file}`;
    const text = formatted(value);
    if (checkOnly) {
        assert.equal(fs.readFileSync(path, 'utf8'), text, `Generated artifact differs: ${file}`);
    } else {
        fs.writeFileSync(path, text);
    }
}
console.log(checkOnly
    ? 'All four review artifacts reproduce byte-for-byte; no files written.'
    : 'Prepared 250 pending candidates, 458 exclusions, an empty decision journal and pending attribution.');
