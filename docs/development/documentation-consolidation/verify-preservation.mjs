import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import fs from 'node:fs';

const directory = 'docs/development/documentation-consolidation';
const read = name => JSON.parse(fs.readFileSync(`${directory}/${name}`, 'utf8'));
const hash = file => createHash('sha256').update(fs.readFileSync(file)).digest('hex');
const baseline = read('inventory-before.json');
const protectedInputs = read('protected-inputs.json');
const plan = read('migration-plan.json');
const graph = read('dependencies-before.json');
const dependencies = graph.groups.flatMap(group => group.targets.map(target => ({
    from: group.from, to: target, kind: group.kind
})));
assert.equal(graph.schemaVersion, 1);
assert.equal(graph.edgeCount, dependencies.length, 'Dependency graph count mismatch');
const changedDocuments = new Set(plan.actions.filter(item => item.action !== 'RETAIN_IN_PLACE')
    .map(item => item.currentPath));

for (const [file, fingerprint] of Object.entries(protectedInputs)) {
    assert(fs.existsSync(file), `Protected input missing: ${file}`);
    assert.equal(hash(file), fingerprint, `Protected input changed: ${file}`);
}
for (const artifact of baseline.artifacts.filter(item => !changedDocuments.has(item.currentPath))) {
    assert(fs.existsSync(artifact.currentPath), `Retained artifact missing: ${artifact.currentPath}`);
    assert.equal(hash(artifact.currentPath), artifact.sha256, `Retained artifact changed: ${artifact.currentPath}`);
}
for (const original of plan.originals) {
    assert.equal(hash(original.to), original.sha256, `Archived original changed: ${original.to}`);
    assert.equal(original.sha256, baseline.artifacts.find(item => item.currentPath === original.from).sha256,
        'Archived original does not match its pre-consolidation file');
}
const destinations = new Map(plan.actions.map(action => [action.currentPath, action.destination]));
for (const dependency of dependencies) {
    const target = destinations.get(dependency.to) ?? dependency.to;
    assert(fs.existsSync(target), `Existing artifact dependency lost: ${dependency.from} -> ${target}`);
}
assert.equal(plan.deletedFiles.length, 0);
assert(fs.readdirSync('.').includes('AGENTS.md'), 'Exact AGENTS.md case is required');
const normalizeLines = text => text.replaceAll('\r\n', '\n');
const currentGuidance = normalizeLines(fs.readFileSync('AGENTS.md', 'utf8'));
const originalGuidance = normalizeLines(fs.readFileSync(plan.originals.find(item => item.from === 'Agents.md').to, 'utf8'));
assert.equal(currentGuidance.replace(/\n## Documentation entry points\n[\s\S]*?(?=\n## Technology)/, '').replace(/\n$/, ''),
    originalGuidance.replace(/\n$/, ''),
    'Existing agent rules must be preserved outside the new navigation section');
const currentFeat = fs.readFileSync('analysis/feats/SOURCE-INVENTORY.md', 'utf8');
const originalFeat = fs.readFileSync(plan.originals.find(item => item.from === 'analysis/feats/SOURCE-INVENTORY.md').to, 'utf8');
assert.equal(currentFeat, originalFeat.replace('(nnormalized-field-inventory.json)', '(normalized-field-inventory.json)'),
    'Feat report must contain only the reviewed link correction');

const pinnedSources = Object.keys(protectedInputs).filter(file => file.startsWith('src/main/resources/data/')
    && file.endsWith('.json'));
assert.equal(pinnedSources.length, 175);
const candidates = JSON.parse(fs.readFileSync('src/main/resources/publication/spell-formulas/v1/candidates.json', 'utf8'));
assert.equal(candidates.candidates.length, 250);
assert(candidates.candidates.every(candidate => candidate.initialReviewState === 'PENDING_REVIEW'));
assert(!fs.existsSync('src/main/resources/publication/spell-formulas/v1/decisions.json'));
console.log(JSON.stringify({ protectedInputs: Object.keys(protectedInputs).length,
    retainedArtifacts: baseline.artifacts.length - changedDocuments.size,
    archivedOriginals: plan.originals.length, pinnedSources: pinnedSources.length,
    preservedDependencyEdges: dependencies.length, pendingPublicationCandidates: 250,
    decisionArtifacts: 0, deletedArtifacts: 0 }));
