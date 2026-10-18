import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import fs from 'node:fs';
import path from 'node:path';
import { execFileSync } from 'node:child_process';

// Run from the repository root. This tool reads source data but never writes it.
const outputDirectory = 'docs/development/documentation-consolidation';
const mode = process.argv[2] ?? '--current';
assert(['--capture', '--current', '--verify'].includes(mode), 'Unknown audit mode');
const sha256 = bytes => createHash('sha256').update(bytes).digest('hex');
const tracked = execFileSync('git', ['ls-files', '-z'], { maxBuffer: 8 * 1024 * 1024 })
    .toString('utf8').split('\0').filter(Boolean).sort();
const existing = new Set();
const directories = new Set();

function walk(directory) {
    if (!fs.existsSync(directory)) {
        return;
    }
    directories.add(directory);
    for (const entry of fs.readdirSync(directory, { withFileTypes: true })) {
        const file = path.posix.join(directory, entry.name);
        if (entry.isDirectory()) {
            walk(file);
        } else {
            existing.add(file);
        }
    }
}

for (const directory of ['src', 'analysis', 'docs', '.agents', '.mvn', '.github']) {
    walk(directory);
}
for (const file of fs.readdirSync('.')) {
    if (fs.statSync(file).isFile()) {
        existing.add(file);
    }
}
for (const file of tracked) {
    if (fs.existsSync(file)) {
        existing.add(file);
    }
}

const canonicalPaths = new Map([...existing, ...directories].map(file => [file.toLowerCase(), file]));
const read = file => fs.readFileSync(file, 'utf8');
const json = value => JSON.stringify(value, null, 2) + '\n';
const persist = (name, value) => fs.writeFileSync(`${outputDirectory}/${name}`, json(value));

function dependencyGraph(edges) {
    const groups = new Map();
    for (const edge of edges) {
        const key = `${edge.from}\0${edge.kind}`;
        if (!groups.has(key)) {
            groups.set(key, { from: edge.from, kind: edge.kind, targets: [] });
        }
        groups.get(key).targets.push(edge.to);
    }
    return { schemaVersion: 1, edgeCount: edges.length, groups: [...groups.values()] };
}
const isTool = file => /\.(?:mjs|js|ps1|py|sh|java)$/.test(file);
const isArtifact = file => /^(?:analysis|docs|\.agents|\.mvn|\.github)\//.test(file)
    || /^src\/(?:main|test)\/resources\//.test(file)
    || /^src\/test\/java\/.*(?:Fixtures|TestSources|GoldSet)\.java$/.test(file)
    || (!file.includes('/') && (/\.(?:md|xml|sh)$/.test(file)
        || ['LICENSE', '.gitignore', '.gitattributes', 'mvnw', 'mvnw.cmd'].includes(file)));
const liveOutputs = new Set(['inventory-current.json', 'dependencies-current.json', 'links-current.json',
    'verification.json', 'hygiene-current.json']);
const artifactFiles = [...existing].filter(isArtifact)
    .filter(file => !liveOutputs.has(path.posix.relative(outputDirectory, file)))
    .filter(file => mode !== '--capture' || !file.startsWith(outputDirectory + '/')).sort();
const artifactSet = new Set(artifactFiles);
const consumers = [...existing].filter(file => !file.startsWith('src/main/resources/data/')
    && /\.(?:java|mjs|js|ps1|py|sh|xml|properties|yml|yaml|md|json)$/.test(file)
    && !file.startsWith(outputDirectory + '/')).sort();
const references = [];
const referenceKeys = new Set();

function reference(from, to, kind) {
    const key = `${from}\0${to}\0${kind}`;
    if (!referenceKeys.has(key)) {
        references.push({ from, to, kind });
        referenceKeys.add(key);
    }
}

function canonical(value) {
    return canonicalPaths.get(value.replaceAll('\\', '/').replace(/^\.\//, '').replace(/\/$/, '').toLowerCase());
}

function inspectJson(value, consumer) {
    if (Array.isArray(value)) {
        value.forEach(child => inspectJson(child, consumer));
    } else if (value !== null && typeof value === 'object') {
        for (const [key, child] of Object.entries(value)) {
            const target = canonical(key);
            if (target && artifactSet.has(target)) {
                reference(consumer, target,
                    typeof child === 'string' && /^[a-f0-9]{64}$/i.test(child) ? 'HASH_PIN' : 'JSON_PATH_KEY');
            }
            inspectJson(child, consumer);
        }
    } else if (typeof value === 'string') {
        const target = canonical(value);
        if (target && artifactSet.has(target)) {
            reference(consumer, target, 'JSON_PATH_VALUE');
        }
    }
}

for (const consumer of consumers) {
    const text = read(consumer);
    if (consumer.endsWith('.json')) {
        inspectJson(JSON.parse(text.replace(/^\uFEFF/, '')), consumer);
    }
    for (const token of text.matchAll(/(?:analysis|docs|src\/(?:main|test)\/resources|\.agents|\.mvn)\/[\w./@+${}:-]+/g)) {
        const raw = token[0].replace(/[.:]+$/, '');
        const target = canonical(raw);
        if (target && artifactSet.has(target)) {
            reference(consumer, target, isTool(consumer) || consumer === 'pom.xml' ? 'TOOL_PATH' : 'LITERAL_PATH');
        } else if (fs.existsSync(raw) && fs.statSync(raw).isDirectory() && isTool(consumer)) {
            // Directory-based reads/writes may construct filenames dynamically. Retain conservatively.
            for (const file of artifactFiles.filter(file => file.startsWith(raw + '/'))) {
                reference(consumer, file, 'TOOL_DIRECTORY_SCOPE');
            }
        }
    }
    if (isTool(consumer)) {
        const bases = [...text.matchAll(/["']((?:analysis|docs)\/[\w./-]+)["']/g)]
            .map(match => match[1]).filter(base => fs.existsSync(base) && fs.statSync(base).isDirectory());
        const names = [...text.matchAll(/["']([\w.-]+\.json)["']/g)].map(match => match[1]);
        for (const base of bases) {
            for (const name of names) {
                const target = canonical(path.posix.join(base, name));
                if (target && artifactSet.has(target)) {
                    reference(consumer, target, 'DERIVED_TOOL_FILE');
                }
            }
        }
        for (const match of text.matchAll(/(?:data|publication)\/[\w./*+-]+/g)) {
            const resource = 'src/main/resources/' + match[0].replace(/[.]+$/, '');
            const target = canonical(resource);
            if (target && artifactSet.has(target)) {
                reference(consumer, target, 'CLASSPATH_RESOURCE');
            } else {
                const prefix = resource.split('*')[0].replace(/\/$/, '');
                for (const file of artifactFiles.filter(file => file === prefix || file.startsWith(prefix + '/'))) {
                    reference(consumer, file, 'CLASSPATH_RESOURCE_SCOPE');
                }
            }
        }
    }
}

function withoutFences(text) {
    let fence = null;
    return text.split('\n').map(line => {
        const start = line.match(/^\s{0,3}(`{3,}|~{3,})/);
        if (start) {
            if (fence === null) {
                fence = start[1][0];
            } else if (start[1][0] === fence) {
                fence = null;
            }
            return ' '.repeat(line.length);
        }
        return fence === null ? line : ' '.repeat(line.length);
    }).join('\n');
}

function markdownLinks(file) {
    const text = withoutFences(read(file));
    const links = [];
    for (const match of text.matchAll(/!?\[[^\]\n]*\]\(/g)) {
        let cursor = match.index + match[0].length;
        const start = cursor;
        let depth = 1;
        let angle = false;
        while (cursor < text.length && depth > 0) {
            const character = text[cursor];
            if (character === '<') {
                angle = true;
            } else if (character === '>') {
                angle = false;
            } else if (!angle && character === '(') {
                depth++;
            } else if (!angle && character === ')') {
                depth--;
            }
            cursor++;
        }
        if (depth === 0) {
            const destination = text.substring(start, cursor - 1).trim();
            const target = destination.startsWith('<')
                ? destination.slice(1, destination.indexOf('>'))
                : destination.replace(/\s+["'][\s\S]*$/, '');
            links.push({ target, offset: match.index });
        }
    }
    for (const match of text.matchAll(/^ {0,3}\[[^\]\n]+\]:\s*(<[^>]+>|\S+)/gm)) {
        links.push({ target: match[1].replace(/^<|>$/g, ''), offset: match.index });
    }
    return links.map(link => ({ ...link, line: text.slice(0, link.offset).split('\n').length }));
}

function headingIds(file) {
    const used = new Map();
    const ids = new Set();
    for (const match of withoutFences(read(file)).matchAll(/^ {0,3}#{1,6}\s+(.+?)\s*#*$/gm)) {
        const slug = match[1].toLowerCase().replace(/<[^>]*>/g, '')
            .replace(/[^\p{L}\p{N}_\-\s]/gu, '').replace(/\s/g, '-');
        const count = used.get(slug) ?? 0;
        ids.add(count === 0 ? slug : `${slug}-${count}`);
        used.set(slug, count + 1);
    }
    for (const match of read(file).matchAll(/<a\s+(?:id|name)=["']([^"']+)["']/gi)) {
        ids.add(match[1]);
    }
    return ids;
}

const links = [];
const headingCache = new Map();
for (const file of artifactFiles.filter(file => file.endsWith('.md'))) {
    for (const link of markdownLinks(file)) {
        if (/^[a-z][a-z0-9+.-]*:/i.test(link.target) || link.target.startsWith('//')) {
            // Do not duplicate local absolute paths or potential URL credentials into the report.
            links.push({ from: file, line: link.line, status: 'EXTERNAL_OR_ABSOLUTE_NOT_FETCHED' });
            continue;
        }
        const [targetPart, fragment] = link.target.split('#', 2);
        const decoded = decodeURIComponent(targetPart.split('?')[0]).replace(/:\d+$/, '');
        const requested = (decoded === '' ? file : path.posix.normalize(path.posix.join(path.posix.dirname(file), decoded)))
            .replace(/\/$/, '');
        const target = canonical(requested);
        let status = target ? 'VALID' : 'MISSING';
        if (target && target !== requested) {
            status = 'CASE_MISMATCH';
        }
        if (status === 'VALID' && fragment && target.endsWith('.md')) {
            if (!headingCache.has(target)) {
                headingCache.set(target, headingIds(target));
            }
            if (!headingCache.get(target).has(decodeURIComponent(fragment))) {
                status = 'MISSING_ANCHOR';
            }
        }
        links.push({ from: file, line: link.line, target: requested, ...(fragment ? { fragment } : {}), status });
        if (target) {
            reference(file, target, 'MARKDOWN_LINK');
        }
    }
}

const authoritative = new Set([
    'Agents.md', 'AGENTS.md', 'docs/THE_DND_PUB_BACKEND_ROADMAP.md',
    'analysis/mechanics/step-7/7.2.1-mechanics-model-and-api-contract.md'
]);
const active = new Set([
    'README.md', 'analysis/domain/DOMAIN-DISCOVERIES.md',
    'analysis/remaining-json/SOURCE-COVERAGE-MATRIX.md', 'analysis/remaining-json/LICENSING-BOUNDARIES.md',
    'analysis/sources/SOURCE-REGISTRY-DESIGN.md', 'analysis/sources/SOURCE-REGISTRY-IMPLEMENTATION.md',
    'analysis/mechanics/step-7/7.2.2-canonical-mechanics-model-implementation.md',
    'analysis/mechanics/step-7/7.2.3-spell-inspection-api-implementation.md',
    'analysis/mechanics/public-v1/spells/P1C-APPROVAL-DESIGN.md',
    'analysis/mechanics/public-v1/spells/P1D-IMPLEMENTATION.md'
]);
const incoming = new Map(artifactFiles.map(file => [file, references.filter(reference => reference.to === file)]));
const inventory = artifactFiles.map(file => {
    const inputs = incoming.get(file);
    let classification;
    if (authoritative.has(file) || /^docs\/(?:architecture|api)\//.test(file)) {
        classification = 'AUTHORITATIVE';
    } else if (active.has(file) || file.startsWith('.agents/') || file.startsWith('docs/') || file === 'LICENSE'
            || /^analysis\/(?:README\.md|active\/|archive\/(?:README|HISTORICAL-ARTIFACTS)\.md|artifacts\/README\.md)/.test(file)) {
        classification = 'ACTIVE_REFERENCE';
    } else if (file.startsWith('src/') || file.startsWith('.mvn/') || file === 'pom.xml'
            || inputs.some(reference => reference.from.startsWith('src/')
                && ['TOOL_PATH', 'DERIVED_TOOL_FILE'].includes(reference.kind))) {
        classification = 'RUNTIME_DEPENDENCY';
    } else if (file === 'HELP.md') {
        classification = 'REDUNDANT';
    } else if (file.startsWith('analysis/') && file.endsWith('.md')) {
        classification = 'HISTORICAL';
    } else if (file.startsWith('analysis/')) {
        classification = 'REPRODUCIBILITY';
    } else {
        classification = 'ACTIVE_REFERENCE';
    }
    const pinned = inputs.filter(reference => reference.kind === 'HASH_PIN').map(reference => reference.from);
    const tools = inputs.filter(reference => ['TOOL_PATH', 'TOOL_DIRECTORY_SCOPE', 'DERIVED_TOOL_FILE',
        'CLASSPATH_RESOURCE', 'CLASSPATH_RESOURCE_SCOPE'].includes(reference.kind));
    let justification = 'Current guidance, runtime resource or reproducibility asset; preserve existing location.';
    if (file === 'Agents.md') {
        justification = 'Canonical AGENTS.md discovery on case-sensitive platforms; preserve existing guidance.';
    } else if (pinned.length > 0) {
        justification = 'Referenced by fingerprint evidence; retain original bytes and path.';
    } else if (tools.length > 0) {
        justification = 'Referenced by code/tool paths or dynamic directory scope; preserve dependency.';
    } else if (classification === 'HISTORICAL') {
        justification = 'Unique historical findings retained; archive index separates them from current authority.';
    } else if (classification === 'REDUNDANT') {
        justification = 'Ignored local generated scaffold; retain rather than delete owner files.';
    }
    return {
        currentPath: file,
        sha256: sha256(fs.readFileSync(file)),
        bytes: fs.statSync(file).size,
        tracked: tracked.includes(file),
        classification,
        proposedAction: file === 'Agents.md' ? 'NORMALIZE_CASE_TO_AGENTS' : 'RETAIN_IN_PLACE',
        proposedPath: file === 'Agents.md' ? 'AGENTS.md' : file,
        justification,
        fingerprintConsumers: [...new Set(pinned)].sort(),
        dependencyConsumers: [...new Set(tools.map(reference => reference.from))].sort()
    };
});

references.sort((left, right) => `${left.from}\0${left.to}\0${left.kind}`.localeCompare(`${right.from}\0${right.to}\0${right.kind}`, 'en'));
const counts = values => Object.fromEntries([...new Set(values)].sort().map(value => [value, values.filter(item => item === value).length]));
const summary = {
    artifacts: inventory.length,
    analysisFiles: inventory.filter(item => item.currentPath.startsWith('analysis/')).length,
    markdownFiles: inventory.filter(item => item.currentPath.endsWith('.md')).length,
    classificationCounts: counts(inventory.map(item => item.classification)),
    dependencyEdges: references.length,
    hashPinnedMarkdown: inventory.filter(item => item.currentPath.endsWith('.md') && item.fingerprintConsumers.length > 0).length,
    linkStatuses: counts(links.map(link => link.status))
};
fs.mkdirSync(outputDirectory, { recursive: true });
if (mode === '--capture') {
    if (fs.existsSync(`${outputDirectory}/inventory-before.json`)) {
        const prior = JSON.parse(read(`${outputDirectory}/inventory-before.json`));
        assert.deepEqual(prior.artifacts.map(item => [item.currentPath, item.sha256]),
            inventory.map(item => [item.currentPath, item.sha256]),
            'Pre-existing tree changed; refusing to recapture the baseline');
    }
    persist('inventory-before.json', {
        scope: 'Artifact inventory before consolidation, including pre-existing uncommitted work',
        summary,
        artifacts: inventory
    });
    persist('dependencies-before.json', dependencyGraph(references));
    persist('links-before.json', links);
} else {
    persist('inventory-current.json', { scope: 'Current documentation and dependency inventory', summary, artifacts: inventory });
    persist('dependencies-current.json', dependencyGraph(references));
    persist('links-current.json', links);
}
console.log(JSON.stringify(summary));
if (mode === '--verify') {
    const broken = links.filter(link => ['MISSING', 'MISSING_ANCHOR', 'CASE_MISMATCH'].includes(link.status));
    assert.equal(broken.length, 0, 'Relative Markdown link validation failed; inspect links-current.json');
}
