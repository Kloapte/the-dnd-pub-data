import fs from 'node:fs';
import path from 'node:path';

const inventory = JSON.parse(fs.readFileSync('docs/development/documentation-consolidation/inventory-before.json', 'utf8'));
const historical = inventory.artifacts.filter(item => item.classification === 'HISTORICAL');
const groups = new Map();
for (const artifact of historical) {
    let group = 'Catalogs and normalization';
    if (artifact.currentPath.startsWith('analysis/mechanics/public-v1/')) {
        group = 'Publication';
    } else if (artifact.currentPath.startsWith('analysis/mechanics/')) {
        group = 'Mechanics';
    } else if (artifact.currentPath.startsWith('analysis/references/')) {
        group = 'References';
    }
    if (!groups.has(group)) {
        groups.set(group, []);
    }
    groups.get(group).push(artifact);
}
const lines = [
    '# Retained historical reports', '',
    `This exhaustive list covers the ${historical.length} pre-existing HISTORICAL reports in the`,
    '[consolidation inventory](../../docs/development/documentation-consolidation/inventory-before.json).',
    'They are retained in place because fingerprint evidence names their paths. Their original plans',
    'and baselines do not override the [backend roadmap](../../docs/THE_DND_PUB_BACKEND_ROADMAP.md).', '',
    'The one Feat inventory link correction has an exact original snapshot in the archive.',
    'All non-Markdown evidence, manifests, scripts, schemas and fixtures are separately enumerated',
    'in the inventory with their consumer references and preservation actions.', ''
];
for (const [group, artifacts] of [...groups].sort(([left], [right]) => left.localeCompare(right, 'en'))) {
    lines.push(`## ${group}`, '', `${artifacts.length} retained reports.`, '');
    for (const artifact of artifacts) {
        const relative = path.posix.relative('analysis/archive', artifact.currentPath);
        lines.push(`- [${artifact.currentPath}](${relative})`);
    }
    lines.push('');
}
fs.writeFileSync('analysis/archive/HISTORICAL-ARTIFACTS.md', lines.join('\n'));
console.log(JSON.stringify({ historicalReports: historical.length,
    groups: Object.fromEntries([...groups].map(([name, artifacts]) => [name, artifacts.length])) }));
