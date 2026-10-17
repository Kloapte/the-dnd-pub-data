import { createHash } from 'node:crypto';
import { readFile, readdir, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const directory = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(directory, '../../..');
const previous = JSON.parse(await readFile(path.join(root, 'analysis/mechanics/corpus-evidence.json'), 'utf8'));
const sourceHashes = [];
const audit = { tags: 0, names: {}, backslashTags: 0, whitespaceOnlyBodies: 0, directPipeBodies: 0,
    nonAsciiSeparators: 0, examples: {}, modifierTokens: {} };

function count(object, key) {
    object[key] = (object[key] ?? 0) + 1;
}

function walk(value, file, locator) {
    if (Array.isArray(value)) {
        value.forEach((child, index) => walk(child, file, `${locator}[${index}]`));
    } else if (value !== null && typeof value === 'object') {
        for (const [key, child] of Object.entries(value)) {
            if (key !== 'crochetPattern') {
                walk(child, file, `${locator}.${key}`);
            }
        }
    } else if (typeof value === 'string') {
        for (let start = value.indexOf('{@'); start >= 0; start = value.indexOf('{@', start + 2)) {
            let depth = 1;
            let end = start + 2;
            for (; end < value.length && depth > 0; end += 1) {
                if (value[end] === '{') {
                    depth += 1;
                } else if (value[end] === '}') {
                    depth -= 1;
                }
            }
            if (depth !== 0) {
                throw new Error(`Unbalanced tag: ${file}:${locator}:${start}`);
            }
            const inner = value.slice(start + 2, end - 1);
            const match = /^([A-Za-z0-9_-]+)([\s\S]*)$/.exec(inner);
            if (!match) {
                throw new Error(`Unknown tag name grammar: ${file}:${locator}:${start}`);
            }
            const [, name, rawArguments] = match;
            audit.tags += 1;
            count(audit.names, name.toLowerCase());
            audit.backslashTags += Number(inner.includes('\\'));
            audit.whitespaceOnlyBodies += Number(rawArguments.length > 0 && rawArguments.trim().length === 0);
            audit.directPipeBodies += Number(rawArguments.startsWith('|'));
            audit.nonAsciiSeparators += Number(/^\s/.test(rawArguments) && !/^[ \t\r\n\f]/.test(rawArguments));
            if (['hit', 'd20'].includes(name.toLowerCase())) {
                count(audit.modifierTokens, rawArguments.trimStart());
            }
            if (inner.includes('\\')) {
                audit.examples.backslash ??= { file, locator, offset: start };
            }
        }
    }
}

for (const file of previous.files) {
    const bytes = await readFile(path.join(root, file.file));
    const sha256 = createHash('sha256').update(bytes).digest('hex');
    if (sha256 !== file.sha256) {
        throw new Error(`Pinned source changed: ${file.file}`);
    }
    sourceHashes.push({ file: file.file, sha256 });
    walk(JSON.parse(bytes.toString('utf8').replace(/^\uFEFF/, '')), file.file, '$');
}
const assessment = [];
for (const file of (await readdir(path.join(root, 'analysis/mechanics/entry-assessment'))).sort()) {
    const bytes = await readFile(path.join(root, 'analysis/mechanics/entry-assessment', file));
    assessment.push({ file, sha256: createHash('sha256').update(bytes).digest('hex') });
}
await writeFile(path.join(directory, 'source-audit.json'), JSON.stringify({ sourceHashes, assessment, audit }, null, 2) + '\n');
console.log(JSON.stringify(audit, null, 2));
