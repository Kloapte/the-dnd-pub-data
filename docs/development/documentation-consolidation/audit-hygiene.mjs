import { execFileSync } from 'node:child_process';
import fs from 'node:fs';

// Findings contain paths and line numbers only. Never print matched credentials or private path values.
const directory = 'docs/development/documentation-consolidation';
const gitFiles = argumentsList => execFileSync('git', argumentsList, { maxBuffer: 8 * 1024 * 1024 })
    .toString('utf8').split('\0').filter(Boolean);
const tracked = new Set(gitFiles(['ls-files', '-z']));
const files = [...new Set(gitFiles(['ls-files', '--cached', '--others', '--exclude-standard', '-z']))]
    .filter(file => fs.existsSync(file) && !file.startsWith(directory + '/')).sort();
const findings = [];
const checks = [
    ['PRIVATE_KEY_MARKER', /-----BEGIN (?:RSA |EC |OPENSSH |DSA |ENCRYPTED )?PRIVATE KEY-----|-----BEGIN PGP PRIVATE KEY BLOCK-----/],
    ['TOKEN_SHAPED_VALUE', /\b(?:AKIA[0-9A-Z]{16}|gh[pousr]_[A-Za-z0-9]{30,}|github_pat_[A-Za-z0-9_]{40,}|sk-[A-Za-z0-9]{24,})\b/],
    ['CREDENTIAL_CONFIGURATION', /(?:password|passwd|api[_-]?key|client[_-]?secret|aws_secret_access_key)\s*["']?\s*[:=]\s*\S+/i],
    ['PERSONAL_ABSOLUTE_PATH', /(?:[A-Za-z]:[\\/]Users[\\/][^\\/\s]+|\/(?:home|Users)\/[^/\s]+)/],
    ['EMAIL_SHAPED_METADATA', /\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}\b/i],
    ['PRIVATE_NETWORK_LITERAL', /\b(?:10(?:\.\d{1,3}){3}|192\.168(?:\.\d{1,3}){2}|172\.(?:1[6-9]|2\d|3[01])(?:\.\d{1,3}){2})\b/],
    ['LOOPBACK_DEVELOPMENT_CONFIGURATION', /(?:localhost|127\.0\.0\.1)(?::\d+)?/i]
];
let scannedTracked = 0;
let scannedWorking = 0;
let binaryFiles = 0;
for (const file of files) {
    const bytes = fs.readFileSync(file);
    if (bytes.subarray(0, 8192).includes(0)) {
        binaryFiles++;
        continue;
    }
    scannedWorking++;
    if (tracked.has(file)) {
        scannedTracked++;
    }
    const text = bytes.toString('utf8');
    for (const [index, line] of text.split('\n').entries()) {
        for (const [category, expression] of checks) {
            if (expression.test(line)) {
                findings.push({ file, line: index + 1, category, tracked: tracked.has(file) });
            }
        }
    }
    if (file.startsWith('src/main/resources/data/') && file.endsWith('.json')) {
        findings.push({ file, category: 'THIRD_PARTY_DATASET_DISTRIBUTION_REVIEW', tracked: tracked.has(file) });
    } else if (file.startsWith('analysis/') && file.endsWith('.json') && text.includes('{@')) {
        findings.push({ file, category: 'DERIVED_SOURCE_CONTENT_DISTRIBUTION_REVIEW', tracked: tracked.has(file) });
    }
}
const categories = [...new Set(findings.map(item => item.category))].sort();
const summary = Object.fromEntries(categories.map(category => [category, {
    occurrences: findings.filter(item => item.category === category).length,
    files: new Set(findings.filter(item => item.category === category).map(item => item.file)).size
}]));
const report = {
    scope: 'Current tracked files plus nonignored working additions; no Git history or external credential validation',
    limitations: ['Pattern findings are review candidates, not proof of live secrets or legal clearance.',
        'Binary files are counted and skipped; ignored local files and Git history are outside this scan.',
        'No matched values, credentials, personal path contents or network addresses are exported.'],
    trackedFiles: tracked.size, scannedTrackedTextFiles: scannedTracked, scannedWorkingTextFiles: scannedWorking,
    binaryFiles, summary, findings
};
fs.writeFileSync(`${directory}/hygiene-current.json`, JSON.stringify(report, null, 2) + '\n');
console.log(JSON.stringify({ trackedFiles: tracked.size, scannedTrackedTextFiles: scannedTracked,
    scannedWorkingTextFiles: scannedWorking, binaryFiles, summary }));
