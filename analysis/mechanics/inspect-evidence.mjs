import { readFile } from 'node:fs/promises';

const evidence = JSON.parse(await readFile(new URL('corpus-evidence.json', import.meta.url), 'utf8'));
const mode = process.argv[2] ?? 'summary';
if (mode === 'samples') {
    const barbarian = JSON.parse(await readFile(new URL('../../src/main/resources/data/classes/class-barbarian.json', import.meta.url), 'utf8'));
    const items = JSON.parse(await readFile(new URL('../../src/main/resources/data/items.json', import.meta.url), 'utf8'));
    const feats = JSON.parse(await readFile(new URL('../../src/main/resources/data/feats.json', import.meta.url), 'utf8'));
    console.log(JSON.stringify({
        barbarian: { name: barbarian.class[0].name, source: barbarian.class[0].source,
            columns: barbarian.class[0].classTableGroups[0].colLabels,
            firstRow: barbarian.class[0].classTableGroups[0].rows[0] },
        focus: { name: items.item[0].name, source: items.item[0].source, focus: items.item[0].focus },
        feat: { name: feats.feat[0].name, source: feats.feat[0].source,
            skillProficiencies: feats.feat[0].skillProficiencies,
            expertise: feats.feat[0].expertise, senses: feats.feat[0].senses },
    }, null, 2));
    process.exit(0);
}
if (mode === 'candidates') {
    const candidates = JSON.parse(await readFile(new URL('candidate-evidence.json', import.meta.url), 'utf8'));
    for (const [id, candidate] of Object.entries(candidates.candidates)) {
        console.log(`${id}: ${JSON.stringify(candidate.examples.slice(0, 1))}`);
    }
    process.exit(0);
}
if (mode === 'variants') {
    const selectedTags = ['dice', 'damage', 'scaledice', 'scaledamage', 'atk', 'atkr', 'hit', 'dc',
        'recharge', 'actsave', 'actsavefail', 'actsavefailby', 'actsavesuccess', 'actsavesuccessorfail',
        'acttrigger', 'actresponse', 'hom', 'd20', 'chance', 'skillcheck', 'hityourspellattack', 'dcyourspellsave'];
    for (const tag of selectedTags) {
        const variants = {};
        let example;
        for (const catalog of Object.values(evidence.catalogs)) {
            for (const [key, count] of Object.entries(catalog.tagVariants[tag] ?? {})) {
                variants[key] = (variants[key] ?? 0) + count;
            }
            example ??= catalog.tagExamples[tag]?.[0];
        }
        console.log(`${tag}: ${JSON.stringify(variants)}; ${JSON.stringify(example)}`);
    }
    process.exit(0);
}
for (const [name, catalog] of Object.entries(evidence.catalogs)) {
    if (catalog.role !== 'definition-source') {
        continue;
    }
    if (mode === 'fields') {
        console.log(`${name}: ${Object.entries(catalog.rootFields).map(([key, value]) => `${key}=${value.records}`).join(', ')}`);
    } else if (mode === 'tags') {
        console.log(`${name}: ${JSON.stringify(catalog.tagCounts)}`);
    } else {
        const tagCount = Object.values(catalog.tagCounts).reduce((sum, count) => sum + count, 0);
        console.log(`${name}: roots=${catalog.sourceRecords}, paths=${Object.keys(catalog.fieldPaths).length}, tags=${tagCount}, entryTables=${catalog.tableNodes}, tableRows=${catalog.tableRows}, markers=${JSON.stringify(catalog.markerRecords)}`);
    }
}
