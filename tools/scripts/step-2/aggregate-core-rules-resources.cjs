const fs = require('node:fs');
const path = require('node:path');

const workspace = path.resolve(__dirname, '../..');
const inputs = [
    path.join(workspace, 'datasets/step-2/core-rules/v1')
];
const outputRoot = path.join(workspace, 'src/main/resources/normalized/core-rules');
const domains = new Map([
    ['action', 'actions.json'],
    ['condition', 'conditions.json'],
    ['disease', 'diseases.json'],
    ['language', 'languages.json'],
    ['rule', 'rules.json'],
    ['sense', 'senses.json'],
    ['skill', 'skills.json'],
    ['status', 'statuses.json']
]);

function collectFiles(directory) {
    return fs.readdirSync(directory, { withFileTypes: true }).flatMap(entry => {
        const absolute = path.join(directory, entry.name);
        return entry.isDirectory()
            ? collectFiles(absolute)
            : entry.name.endsWith('.json') ? [absolute] : [];
    }).sort((left, right) => left.localeCompare(right));
}

const recordsByDomain = new Map([...domains.keys()].map(domain => [domain, []]));
for (const inputRoot of inputs) {
    for (const file of collectFiles(inputRoot)) {
        const record = JSON.parse(fs.readFileSync(file, 'utf8'));
        if (!record || typeof record.id !== 'string' || typeof record.name !== 'string'
                || !record.sourceMetadata || typeof record.sourceMetadata.source !== 'string') {
            throw new Error(`Canonical record has an incomplete identity or provenance: ${file}`);
        }
        const domain = record.id.split(':')[0];
        if (!recordsByDomain.has(domain)) throw new Error(`Unexpected canonical domain ${domain} in ${file}`);
        recordsByDomain.get(domain).push(record);
    }
}

const allIdentities = new Set();
for (const [domain, filename] of domains) {
    const records = recordsByDomain.get(domain).sort((left, right) => left.id.localeCompare(right.id));
    if (records.length === 0) throw new Error(`No canonical records found for ${domain}`);
    for (const record of records) {
        if (!record.id.startsWith(`${domain}:`)) throw new Error(`Wrong-domain identity ${record.id}`);
        if (allIdentities.has(record.id)) throw new Error(`Duplicate canonical identity ${record.id}`);
        allIdentities.add(record.id);
    }
    fs.mkdirSync(outputRoot, { recursive: true });
    fs.writeFileSync(path.join(outputRoot, filename), `${JSON.stringify(records, null, 2)}\n`);
    process.stdout.write(`${domain}: ${records.length}\n`);
}
process.stdout.write(`Aggregated ${allIdentities.size} canonical records into ${domains.size} resources\n`);
