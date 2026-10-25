const fs = require('node:fs');
const path = require('node:path');

const workspace = path.resolve(__dirname, '..');
const reportPath = path.join(
    workspace,
    'reports/step-2/CORE_RULES_PHASE_3_SOURCE_ACCOUNTING.json'
);
const dataRoot = path.join(workspace, 'src/main/resources/data');
const documentationRoot = path.join(workspace, 'doc');
const manifests = [JSON.parse(fs.readFileSync(reportPath, 'utf8'))];
const phase5ManifestPath = path.join(
    workspace,
    'reports/step-2/CORE_RULES_PHASE_5_SOURCE_ACCOUNTING.json'
);
if (fs.existsSync(phase5ManifestPath)) {
    manifests.push(JSON.parse(fs.readFileSync(phase5ManifestPath, 'utf8')));
}
const records = manifests.flatMap(manifest => manifest.records);
const sourceData = new Map();

function sourceObject(record) {
    if (!sourceData.has(record.sourceFile)) {
        sourceData.set(
            record.sourceFile,
            JSON.parse(fs.readFileSync(path.join(dataRoot, record.sourceFile), 'utf8'))
        );
    }

    const property = {
        'actions.json': 'action',
        'variantrules.json': 'variantrule',
        'skills.json': 'skill',
        'conditionsdiseases.json': record.model,
        'senses.json': 'sense',
        'languages.json': 'language'
    }[record.sourceFile];
    const sourceArray = sourceData.get(record.sourceFile)[property];
    const source = sourceArray[Number(record.sourceObjectPath.match(/\[(\d+)\]$/)[1])];

    if (source.name !== record.name || source.source !== record.source) {
        throw new Error(`Source identity mismatch for ${record.canonicalId}`);
    }
    return source;
}

function registerSection(record, entryPlan, entry, classification) {
    const details = entryPlan.classifications
        .filter(item => item.classification === classification)
        .map(item => {
            const cause = item.cause ? `; cause: \`${item.cause}\`` : '';
            return `- ${item.classification}: ${item.portion} — ${item.reason}${cause}`;
        });
    const sourceIndex = entryPlan.sourceEntryIndex;

    return [
        `### ${record.canonicalId} — entry ${sourceIndex}`,
        '',
        `- Source file: \`src/main/resources/data/${record.sourceFile}\``,
        `- Source object: \`${record.sourceObjectPath}\``,
        `- Source entry path: \`${entryPlan.sourcePath}\``,
        `- Canonical output: \`${record.canonicalOutput ?? 'none'}\``,
        ...details,
        '',
        'Complete original source entry:',
        '',
        '```json',
        JSON.stringify(entry, null, 2),
        '```',
        ''
    ].join('\n');
}

const registers = new Map();
for (const model of new Set(records.map(record => record.model))) {
    registers.set(model, { OMITTED: [], DUPLICATE: [] });
}

for (const record of records) {
    const source = sourceObject(record);
    const entries = source.entries ?? [];
    for (const entryPlan of record.entries) {
        const entry = entries[entryPlan.sourceEntryIndex];
        for (const classification of ['OMITTED', 'DUPLICATE']) {
            if (entryPlan.classifications.some(item => item.classification === classification)) {
                registers.get(record.model)[classification].push(
                    registerSection(record, entryPlan, entry, classification)
                );
            }
        }
    }
}

for (const [model, sections] of registers) {
    const directory = path.join(documentationRoot, model);
    for (const [classification, fileName] of [
        ['OMITTED', 'omitted.md'],
        ['DUPLICATE', 'duplicate.md']
    ]) {
        const file = path.join(directory, fileName);
        if (sections[classification].length === 0) {
            if (fs.existsSync(file)) fs.unlinkSync(file);
            continue;
        }

        fs.mkdirSync(directory, { recursive: true });
        const header = [
            `# ${model[0].toUpperCase()}${model.slice(1)} source content ${classification.toLowerCase()} during Step 2 Core Rules conversion`,
            '',
            'This restricted internal register preserves complete original entries for audit. Do not publish or distribute excerpts without a licensing review.',
            ''
        ].join('\n');
        fs.writeFileSync(file, header + sections[classification].join('\n'), 'utf8');
    }
}

console.log(JSON.stringify({
    modelRegisters: registers.size,
    sourceRecords: records.length,
    omittedEntries: [...registers.values()].reduce((sum, value) => sum + value.OMITTED.length, 0),
    duplicateEntries: [...registers.values()].reduce((sum, value) => sum + value.DUPLICATE.length, 0)
}, null, 2));
