const assert = require('node:assert/strict');
const crypto = require('node:crypto');
const fs = require('node:fs');
const path = require('node:path');
const Ajv2020 = require('ajv/dist/2020').default;

const workspace = path.resolve(__dirname, '..');
const contractRoot = path.join(workspace, 'schemas/core-rules/v1');
const exampleRoot = path.join(contractRoot, 'examples');
const phase3Root = path.join(workspace, 'datasets/phase-3/step-2/core-rules/v1');
const commonSchemaId = 'urn:thedndpub:schemas:core-rules:common:v1';
const ajv = new Ajv2020({ allErrors: true, strict: true, strictRequired: false });
const testResults = [];
const sourceConditionData = readJson(path.join(
    workspace,
    'src/main/resources/data/conditionsdiseases.json'
));

function canonicalSlug(value) {
    return value.normalize('NFKD').replace(/[\u0300-\u036f]/g, '').toLowerCase()
        .replace(/[^a-z0-9]+/g, '-')
        .replace(/^-+|-+$/g, '');
}

const sourceConditionIds = new Set(sourceConditionData.condition.map(condition =>
    `condition:${condition.source.toLowerCase()}:${canonicalSlug(condition.name)}`
));

function readJson(file) {
    return JSON.parse(fs.readFileSync(file, 'utf8'));
}

function canonicalize(value) {
    if (Array.isArray(value)) return value.map(canonicalize);
    if (value && typeof value === 'object') {
        return Object.fromEntries(Object.keys(value).sort().map(key => [key, canonicalize(value[key])]));
    }
    return value;
}

function componentFingerprint(component) {
    const { componentId, ...definition } = component;
    return JSON.stringify(canonicalize(definition));
}

function check(name, callback) {
    callback();
    testResults.push(name);
}

function rejects(name, callback) {
    const acceptedMessage = `${name} was accepted`;
    try {
        assert.equal(callback(), false, acceptedMessage);
    } catch (error) {
        if (error.message === acceptedMessage) throw error;
    }
    testResults.push(name);
}

function fail(message) {
    throw new Error(message);
}

function assertValidCompositionNode(
    node,
    componentIds,
    referencedIds,
    ruleId,
    orPath = new Map(),
    location = 'root',
    activeGroups = new WeakSet()
) {
    if (node.kind === 'COMPONENT') {
        if (Object.keys(node).sort().join(',') !== 'componentId,kind') {
            fail(`${ruleId}: composition contains a duplicated component definition`);
        }
        if (!componentIds.has(node.componentId)) fail(`${ruleId}: unknown component ${node.componentId}`);
        if (referencedIds.has(node.componentId)) fail(`${ruleId}: component referenced more than once: ${node.componentId}`);
        referencedIds.add(node.componentId);
        return [{ componentId: node.componentId, orPath: new Map(orPath) }];
    }

    if (node.kind !== 'AND' && node.kind !== 'OR') fail(`${ruleId}: unsupported composition group`);
    if (activeGroups.has(node)) fail(`${ruleId}: circular composition group`);
    activeGroups.add(node);
    const childResults = [];
    const immediateLeaves = new Set();
    node.members.forEach((member, index) => {
        const nextPath = new Map(orPath);
        if (node.kind === 'OR') nextPath.set(location, index);
        const leaves = assertValidCompositionNode(
            member,
            componentIds,
            referencedIds,
            ruleId,
            nextPath,
            `${location}/${node.kind}-${index}`,
            activeGroups
        );
        for (const leaf of leaves) {
            if (immediateLeaves.has(leaf.componentId)) fail(`${ruleId}: duplicate reference in ${node.kind} group`);
            immediateLeaves.add(leaf.componentId);
        }
        childResults.push(...leaves);
    });
    activeGroups.delete(node);
    return childResults;
}

function assertRuleIntegrity(rule) {
    const mechanics = rule.mechanics ?? [];
    const componentIds = mechanics.map(component => component.componentId);
    if (new Set(componentIds).size !== componentIds.length) fail(`${rule.id}: duplicate componentId`);
    const definitions = new Set();
    for (const component of mechanics) {
        const fingerprint = componentFingerprint(component);
        if (definitions.has(fingerprint)) fail(`${rule.id}: duplicate mechanical component definition`);
        definitions.add(fingerprint);
    }
    const components = new Map(mechanics.map(component => [component.componentId, component]));
    const compositionLeaves = new Map();

    if (rule.composition) {
        const leaves = assertValidCompositionNode(rule.composition, new Set(componentIds), new Set(), rule.id);
        for (const leaf of leaves) compositionLeaves.set(leaf.componentId, leaf.orPath);
    }

    const dependencyGraph = new Map(componentIds.map(id => [id, []]));
    for (const component of mechanics) {
        const applicability = component.appliesWhen;
        if (!applicability) continue;
        const target = components.get(applicability.componentId);
        if (!target) fail(`${rule.id}: unresolved applicability component ${applicability.componentId}`);
        const expectedSubtype = applicability.kind === 'ON_FAILED_SAVE'
            ? 'SAVING_THROW'
            : 'ATTACK_ROLL';
        if (applicability.kind === 'ON_FAILED_SAVE') {
            if (target.kind !== 'D20_TEST' || target.subtype !== expectedSubtype) {
                fail(`${rule.id}: ON_FAILED_SAVE must reference an ordinary saving-throw component`);
            }
        } else {
            if (target.kind !== 'D20_TEST' || target.subtype !== expectedSubtype) {
                fail(`${rule.id}: ${applicability.kind} must reference an attack-roll component`);
            }
            if (applicability.kind === 'ON_CRITICAL_HIT' && !target.roll?.criticalOn) {
                fail(`${rule.id}: ON_CRITICAL_HIT target does not define a critical result`);
            }
        }
        dependencyGraph.get(component.componentId).push(target.componentId);

        const sourcePath = compositionLeaves.get(component.componentId);
        const targetPath = compositionLeaves.get(target.componentId);
        if (sourcePath && targetPath) {
            for (const [groupId, branch] of sourcePath) {
                if (targetPath.has(groupId) && targetPath.get(groupId) !== branch) {
                    fail(`${rule.id}: applicability contradicts OR composition`);
                }
            }
        }
    }

    const visiting = new Set();
    const visited = new Set();
    function visit(componentId) {
        if (visiting.has(componentId)) fail(`${rule.id}: circular applicability dependency at ${componentId}`);
        if (visited.has(componentId)) return;
        visiting.add(componentId);
        for (const dependency of dependencyGraph.get(componentId) ?? []) visit(dependency);
        visiting.delete(componentId);
        visited.add(componentId);
    }
    for (const componentId of componentIds) visit(componentId);
}

function operationApplicability(action, operation) {
    const appliesWhen = operation.appliesWhen;
    if (!appliesWhen) return;
    const operations = action.operations ?? [];
    if (appliesWhen.operationId) {
        const target = operations.find(candidate => candidate.operationId === appliesWhen.operationId);
        if (!target) fail(`${action.id}: unresolved operation ${appliesWhen.operationId}`);
        if (appliesWhen.kind === 'CHECK_SUCCEEDED' && target.kind !== 'CHECK_OR_CONTEST') {
            fail(`${action.id}: CHECK_SUCCEEDED must reference a check operation`);
        }
        if (appliesWhen.kind === 'SAVE_FAILED' && target.kind !== 'SAVING_THROW') {
            fail(`${action.id}: SAVE_FAILED must reference a saving-throw operation`);
        }
    }
}

function assertActionIntegrity(action) {
    const operations = action.operations ?? [];
    const byId = new Map();
    for (const operation of operations) {
        if (operation.operationId) {
            if (byId.has(operation.operationId)) fail(`${action.id}: duplicate operationId ${operation.operationId}`);
            byId.set(operation.operationId, operation);
        }
        operationApplicability(action, operation);
    }

    const groups = action.choiceGroups ?? [];
    const groupById = new Map();
    const memberOwner = new Map();
    for (const group of groups) {
        if (groupById.has(group.id)) fail(`${action.id}: duplicate choice group ${group.id}`);
        groupById.set(group.id, group);
        const { minimumSelected, maximumSelected } = group.cardinality;
        if (minimumSelected > maximumSelected || maximumSelected > group.members.length) {
            fail(`${action.id}: invalid cardinality for ${group.id}`);
        }
        const memberIds = group.members.map(member => member.operationId);
        if (new Set(memberIds).size !== memberIds.length) fail(`${action.id}: duplicate member in ${group.id}`);
        if (group.trigger.operationId && !byId.has(group.trigger.operationId)) {
            fail(`${action.id}: unresolved trigger operation ${group.trigger.operationId}`);
        }
        if (group.trigger.kind === 'CHECK_SUCCEEDED'
                && byId.get(group.trigger.operationId)?.kind !== 'CHECK_OR_CONTEST') {
            fail(`${action.id}: choice trigger must reference a check`);
        }
        if (group.trigger.kind === 'SAVE_FAILED'
                && byId.get(group.trigger.operationId)?.kind !== 'SAVING_THROW') {
            fail(`${action.id}: choice trigger must reference a saving throw`);
        }
        for (const operationId of memberIds) {
            const member = byId.get(operationId);
            if (!member) fail(`${action.id}: unresolved choice member ${operationId}`);
            if (memberOwner.has(operationId)) fail(`${action.id}: operation belongs to incompatible choice groups`);
            memberOwner.set(operationId, group.id);
            if (member.appliesWhen?.kind !== 'CHOICE_SELECTED'
                    || member.appliesWhen.choiceGroupId !== group.id) {
                fail(`${action.id}: choice member applicability contradicts ${group.id}`);
            }
        }
    }

    for (const operation of operations) {
        if (operation.appliesWhen?.kind === 'CHOICE_SELECTED') {
            const group = groupById.get(operation.appliesWhen.choiceGroupId);
            if (!group || !memberOwner.has(operation.operationId)
                    || memberOwner.get(operation.operationId) !== group.id) {
                fail(`${action.id}: operation references a missing or incompatible choice group`);
            }
        }
    }
}

function assertCanonicalReferences(entity) {
    if (!/^[a-z][a-z0-9-]*:[a-z0-9-]+:[a-z0-9-]+$/.test(entity.id)) {
        fail(`${entity.id}: invalid canonical identity`);
    }
    const visit = value => {
        if (Array.isArray(value)) return value.forEach(visit);
        if (!value || typeof value !== 'object') return;
        if (value.targetType && value.id) {
            if (!value.id.startsWith(`${value.targetType}:`)
                    || !/^[a-z][a-z0-9-]*:[a-z0-9-]+:[a-z0-9-]+$/.test(value.id)) {
                fail(`${entity.id}: invalid typed reference ${value.id}`);
            }
            if (value.targetType === 'condition' && !sourceConditionIds.has(value.id)) {
                fail(`${entity.id}: unresolved source-backed Condition reference ${value.id}`);
            }
        }
        for (const child of Object.values(value)) visit(child);
    };
    visit(entity);
}

function assertApprovedDomainBaseline() {
    const baseline = readJson(path.join(__dirname, 'core-rules-approved-domain-baseline.json'));
    for (const { file, sha256 } of baseline.entries) {
        const value = readJson(path.join(contractRoot, file));
        const actual = crypto.createHash('sha256')
            .update(JSON.stringify(canonicalize(value)))
            .digest('hex');
        if (actual !== sha256) fail(`approved domain contract changed: ${file}`);
    }
}

function loadSchemas() {
    const files = fs.readdirSync(contractRoot).filter(file => file.endsWith('.schema.json')).sort();
    const schemas = [];
    for (const file of files) {
        const schema = readJson(path.join(contractRoot, file));
        if (ajv.validateSchema(schema) !== true) fail(`${file}: invalid Draft 2020-12 schema`);
        ajv.addSchema(schema);
        schemas.push([file, schema]);
    }
    for (const [file, schema] of schemas) {
        check(`Draft 2020-12 schema compiles ${file}`, () => {
            assert.ok(ajv.getSchema(schema.$id), `${file}: schema was not registered`);
        });
    }
    return files;
}

function loadExamples() {
    return fs.readdirSync(exampleRoot).filter(file => file.endsWith('.json')).sort()
        .map(file => [file, readJson(path.join(exampleRoot, file))]);
}

function expectedSourceMetadata(model, sourceRecord) {
    const metadata = { source: sourceRecord.source };
    if (Number.isInteger(sourceRecord.page)) metadata.page = sourceRecord.page;
    const flags = {};
    for (const key of ['srd', 'srd52', 'basicRules', 'basicRules2024']) {
        if (typeof sourceRecord[key] === 'boolean') flags[key] = sourceRecord[key];
    }
    if (Object.keys(flags).length) metadata.flags = flags;
    const citations = [...(sourceRecord.additionalSources ?? []), ...(sourceRecord.otherSources ?? [])]
        .map(({ source, page }) => ({ source, page }));
    if (citations.length) metadata.citations = citations;
    if (sourceRecord.reprintedAs?.length) {
        metadata.reprintedAs = sourceRecord.reprintedAs.map(value => {
            let targetType = model;
            let uid = value;
            if (typeof value === 'object') {
                uid = value.uid;
                if (value.tag === 'variantrule') targetType = 'rule';
            }
            const [name, source] = uid.split('|');
            return {
                targetType,
                id: `${targetType}:${canonicalSlug(source || sourceRecord.source)}:${canonicalSlug(name)}`
            };
        });
    }
    return metadata;
}

function phase3Files(directory = phase3Root) {
    if (!fs.existsSync(directory)) return [];
    return fs.readdirSync(directory, { withFileTypes: true }).flatMap(entry => {
        const absolute = path.join(directory, entry.name);
        return entry.isDirectory() ? phase3Files(absolute) : entry.name.endsWith('.json') ? [absolute] : [];
    }).sort();
}

function nestedSourcePaths(entry, parentPath, output = []) {
    if (!entry || typeof entry !== 'object') return output;
    for (const key of ['entries', 'items']) {
        if (!Array.isArray(entry[key])) continue;
        entry[key].forEach((child, index) => {
            const childPath = `${parentPath}.${key}[${index}]`;
            output.push(childPath);
            nestedSourcePaths(child, childPath, output);
        });
    }
    return output;
}

function sourceRecordForCanonical(entity) {
    const [sourceFile, property] = {
        action: ['actions.json', 'action'],
        rule: ['variantrules.json', 'variantrule'],
        skill: ['skills.json', 'skill'],
        condition: ['conditionsdiseases.json', 'condition'],
        disease: ['conditionsdiseases.json', 'disease'],
        status: ['conditionsdiseases.json', 'status'],
        sense: ['senses.json', 'sense'],
        language: ['languages.json', 'language']
    }[entity.id.split(':')[0]];
    if (!sourceFile) fail(`${entity.id}: unsupported Phase 3 model`);
    const data = readJson(path.join(workspace, 'src/main/resources/data', sourceFile));
    const source = entity.sourceMetadata.source;
    const matches = data[property].filter(record => record.name === entity.name && record.source === source);
    if (matches.length !== 1) fail(`${entity.id}: expected one exact source record, found ${matches.length}`);
    return matches[0];
}

function resolveSourcePath(sourceData, locator) {
    let current = sourceData;
    const tokens = locator.match(/\.[A-Za-z0-9_]+|\[\d+\]/g) ?? [];
    for (const token of tokens) {
        current = token.startsWith('[')
            ? current[Number(token.slice(1, -1))]
            : current[token.slice(1)];
        if (current === undefined) fail(`source locator does not resolve: ${locator}`);
    }
    return current;
}

function assertNoSourceFallback(entity) {
    const forbidden = new Set(['entries', 'richText', 'fonts', 'font', 'hasFluff', 'hasFluffImages', 'reviewStatus', 'interpretationStatus']);
    function visit(value, location) {
        if (Array.isArray(value)) return value.forEach((child, index) => visit(child, `${location}[${index}]`));
        if (!value || typeof value !== 'object') return;
        for (const [key, child] of Object.entries(value)) {
            if (forbidden.has(key)) fail(`${entity.id}: forbidden canonical property ${location}.${key}`);
            visit(child, `${location}.${key}`);
        }
    }
    visit(entity, '$');
}

function validatePhase3Records(schemaFiles) {
    const files = phase3Files();
    const manifestPath = path.join(workspace, 'reports/step-2/CORE_RULES_PHASE_3_SOURCE_ACCOUNTING.json');
    const manifest = readJson(manifestPath);
    const canonicalIds = new Set();
    const entities = files.map(file => ({ file, entity: readJson(file) }));
    const outputById = new Map(entities.map(({ entity }) => [entity.id, entity]));
    const sourceIndexes = new Map();
    const sourceDefinitions = {
        action: ['actions.json', 'action'],
        rule: ['variantrules.json', 'variantrule'],
        skill: ['skills.json', 'skill'],
        condition: ['conditionsdiseases.json', 'condition'],
        disease: ['conditionsdiseases.json', 'disease'],
        status: ['conditionsdiseases.json', 'status'],
        sense: ['senses.json', 'sense'],
        language: ['languages.json', 'language'],
        spell: ['spells/spells-phb.json', 'spell'],
        creature: ['monsters/bestiary-mm.json', 'monster']
    };
    const sourceIds = targetType => {
        if (sourceIndexes.has(targetType)) return sourceIndexes.get(targetType);
        const [file, property] = sourceDefinitions[targetType] ?? [];
        if (!file) fail(`no source index for reference target type ${targetType}`);
        const records = readJson(path.join(workspace, 'src/main/resources/data', file))[property];
        const ids = new Set(records.map(record =>
            `${targetType}:${canonicalSlug(record.source)}:${canonicalSlug(record.name)}`));
        sourceIndexes.set(targetType, ids);
        return ids;
    };
    const assertSourceReferences = entity => {
        const visit = value => {
            if (Array.isArray(value)) return value.forEach(visit);
            if (!value || typeof value !== 'object') return;
            if (value.targetType && value.id) {
                assert.ok(sourceIds(value.targetType).has(value.id),
                    `${entity.id}: typed reference does not resolve to a pinned source object: ${value.id}`);
            }
            for (const child of Object.values(value)) visit(child);
        };
        visit(entity);
    };

    for (const { file, entity } of entities) {
        const model = entity.id.split(':')[0];
        const validator = ajv.getSchema(`urn:thedndpub:schemas:core-rules:${model}:v1`);
        if (!validator) fail(`${file}: no schema registered for ${model}`);
        check(`Phase 3 canonical record ${path.relative(phase3Root, file)}`, () => {
            assert.equal(validator(entity), true, JSON.stringify(validator.errors));
            assert.equal(canonicalIds.has(entity.id), false, `duplicate canonical ID ${entity.id}`);
            canonicalIds.add(entity.id);
            assertCanonicalReferences(entity);
            assertSourceReferences(entity);
            assertNoSourceFallback(entity);
            assert.equal(entity.id, `${model}:${canonicalSlug(entity.sourceMetadata.source)}:${canonicalSlug(entity.name)}`);
            assert.deepEqual(entity.sourceMetadata, expectedSourceMetadata(model, sourceRecordForCanonical(entity)));
            if (model === 'action') assertActionIntegrity(entity);
            if (model === 'rule') assertRuleIntegrity(entity);
            const raw = fs.readFileSync(file, 'utf8');
            assert.equal(raw, `${JSON.stringify(entity, null, 2)}\n`, file);
        });
    }

    check('Phase 3 source-entry accounting and omission/duplicate registers', () => {
        assert.equal(manifest.selectionCount, manifest.records.length);
        assert.equal(manifest.records.length, 34);
        assert.equal(manifest.convertedRecordCount, 16);
        assert.equal(manifest.partiallyConvertedRecordCount, 17);
        assert.equal(manifest.unsupportedRecordCount, 1);
        assert.equal(manifest.canonicalRecordCount, entities.length);
        assert.equal(manifest.interpretationMethod.startsWith('Codex directly interpreted'), true);
        assert.equal(manifest.previousPhase3Conversion.includes('retired'), true);
        const classifications = manifest.records.flatMap(record =>
            record.entries.flatMap(entry => entry.classifications));
        assert.deepEqual(manifest.dispositionCounts, {
            interpreted: classifications.filter(item => item.classification === 'INTERPRETED').length,
            duplicate: classifications.filter(item => item.classification === 'DUPLICATE').length,
            omitted: classifications.filter(item => item.classification === 'OMITTED').length
        });
        assert.equal(manifest.sourceEntryRootCount,
            manifest.records.reduce((count, record) => count + record.sourceEntryCount, 0));
        assert.equal(manifest.sourceFieldIssueCount, manifest.sourceFieldIssues.length);
        for (const issue of manifest.sourceFieldIssues) {
            assert.equal(issue.disposition, 'OMITTED');
            assert.ok([
                'SCHEMA_UNSUPPORTED',
                'REFERENCE_UNRESOLVED',
                'SOURCE_AMBIGUOUS',
                'EXTERNAL_DEPENDENCY',
                'NON_MECHANICAL',
                'OUT_OF_APPROVED_SCOPE'
            ].includes(issue.cause));
            assert.ok(issue.sourceObjectPath && issue.sourceField && issue.reason);
            const issueSource = resolveSourcePath(
                readJson(path.join(workspace, 'src/main/resources/data', issue.sourceFile)),
                issue.sourceObjectPath
            );
            if (issue.sourceField.startsWith('inlineReference.')) {
                assert.ok(String(issueSource).includes(issue.sourceValue), issue.canonicalId);
            } else {
                assert.deepEqual(issueSource[issue.sourceField], issue.sourceValue, issue.canonicalId);
            }
        }
        const seenSources = new Set();
        const coveredOutputIds = new Set();
        for (const record of manifest.records) {
            const sourceKey = `${record.model}|${record.source}|${record.name}`;
            assert.equal(seenSources.has(sourceKey), false, `duplicate selected source record ${sourceKey}`);
            seenSources.add(sourceKey);
            const sourceRecord = sourceRecordForCanonical({
                id: record.canonicalId,
                name: record.name,
                sourceMetadata: { source: record.source }
            });
            const sourceEntries = sourceRecord.entries ?? [];
            assert.equal(record.sourceEntryCount, sourceEntries.length, sourceKey);
            assert.ok(['converted', 'partial', 'unsupported'].includes(record.conversionStatus), sourceKey);
            assert.deepEqual(record.entries.map(entry => entry.sourceEntryIndex), sourceEntries.map((_, index) => index));
            const registerFiles = {
                omitted: path.join(workspace, 'doc', record.model, 'omitted.md'),
                duplicate: path.join(workspace, 'doc', record.model, 'duplicate.md')
            };
            for (const entry of record.entries) {
                assert.ok(entry.classifications.length > 0, `${sourceKey} entry ${entry.sourceEntryIndex} unaccounted`);
                const sourceEntry = sourceEntries[entry.sourceEntryIndex];
                assert.deepEqual(entry.nestedSourcePaths,
                    nestedSourcePaths(sourceEntry, entry.sourcePath), `${sourceKey} nested source accounting`);
                for (const classification of entry.classifications) {
                    assert.ok(['INTERPRETED', 'OMITTED', 'DUPLICATE'].includes(classification.classification));
                    assert.ok(classification.portion && classification.reason);
                    if (classification.classification === 'OMITTED' || classification.classification === 'DUPLICATE') {
                        if (classification.classification === 'OMITTED') {
                            assert.ok([
                                'SCHEMA_UNSUPPORTED',
                                'REFERENCE_UNRESOLVED',
                                'SOURCE_AMBIGUOUS',
                                'EXTERNAL_DEPENDENCY',
                                'NON_MECHANICAL',
                                'OUT_OF_APPROVED_SCOPE'
                            ].includes(classification.cause), `${sourceKey}: missing/invalid omission cause`);
                        }
                        const registerKey = classification.classification.toLowerCase();
                        const registerFile = registerFiles[registerKey];
                        assert.ok(fs.existsSync(registerFile), `missing ${registerKey} register for ${sourceKey}`);
                        const registerText = fs.readFileSync(registerFile, 'utf8');
                        assert.ok(registerText.includes(`entry ${entry.sourceEntryIndex}`), `missing register entry ${sourceKey}`);
                        assert.ok(registerText.includes(JSON.stringify(sourceEntry, null, 2)),
                            `register does not preserve complete source entry ${sourceKey} entry ${entry.sourceEntryIndex}`);
                    }
                }
            }
            if (record.canonicalOutput) {
                assert.ok(outputById.has(record.canonicalId), `missing canonical output ${record.canonicalId}`);
                coveredOutputIds.add(record.canonicalId);
            } else {
                assert.equal(record.disposition, 'unsupported', sourceKey);
                assert.ok(record.reason);
                assert.equal(outputById.has(record.canonicalId), false, `unsupported record has canonical shell ${record.canonicalId}`);
            }
        }
        assert.deepEqual([...coveredOutputIds].sort(), [...outputById.keys()].sort());
        assert.equal(manifest.records.filter(record => record.disposition === 'unsupported').length, 1);
    });

    check('Phase 3 Gith speaker references match explicit source creatures', () => {
        const gith = outputById.get('language:mm:gith');
        assert.ok(gith);
        const monsterData = readJson(path.join(workspace, 'src/main/resources/data/monsters/bestiary-mm.json'));
        const creatureIds = new Set(monsterData.monster.map(monster =>
            `creature:${canonicalSlug(monster.source)}:${canonicalSlug(monster.name)}`));
        for (const speaker of gith.typicalSpeakers) assert.ok(creatureIds.has(speaker.id), speaker.id);
    });

    check('Phase 3 source-backed Action, Condition, Rule, Disease, and Status semantics', () => {
        const dodge = outputById.get('action:xphb:dodge');
        assert.deepEqual(dodge.operations[0].when, {
            kind: 'CAN_SEE', actor: 'affected-creature', target: 'attacker'
        });
        const shove = outputById.get('action:phb:shove');
        assert.equal(shove.operations[0].automaticSuccessWhen.condition.id, 'condition:phb:incapacitated');
        assert.deepEqual(shove.choiceGroups[0].cardinality, { minimumSelected: 1, maximumSelected: 1 });
        const exhaustedPhb = outputById.get('condition:phb:exhaustion');
        assert.equal(exhaustedPhb.effects.find(effect => effect.kind === 'HIT_POINT_MAX_MODIFIER').minimumLevel, 4);
        const exhausted2024 = outputById.get('condition:xphb:exhaustion');
        assert.equal(exhausted2024.effects.find(effect => effect.kind === 'CHECK_ROLL_MODIFIER').amountPerLevel, 2);
        assert.equal(exhausted2024.effects.find(effect => effect.kind === 'MOVEMENT_MODIFIER').amountPerLevel, -5);
        const phbConcentration = outputById.get('status:phb:concentration');
        assert.ok(phbConcentration.mechanics.some(mechanic => mechanic.trigger === 'environmental-phenomenon'));
        const xphbConcentration = outputById.get('status:xphb:concentration');
        assert.equal(xphbConcentration.mechanics.find(mechanic => mechanic.trigger === 'damage').dc.rounding, 'down');
        assert.equal(xphbConcentration.mechanics.find(mechanic => mechanic.trigger === 'damage').dc.maximum, 30);
        const unarmed = outputById.get('rule:xphb:unarmed-strike');
        assert.equal(unarmed.composition.kind, 'OR');
        assert.equal(unarmed.mechanics.filter(mechanic => mechanic.subtype === 'SAVING_THROW').length, 1);
        assert.ok(unarmed.references.some(reference =>
            reference.id === 'condition:xphb:grappled'
            && reference.relationship === 'grapple-escape-attempts-use-this-dc'));
        const mark = outputById.get('action:dmg:mark');
        assert.equal(mark.operations[0].reaction.availabilityRequired, true);
        assert.equal(mark.operations[0].reaction.consumed, false);
        assert.equal(outputById.get('action:phb:shove').operations[0].skill.id, 'skill:phb:athletics');
        const castSpell = outputById.get('action:phb:cast-a-spell');
        assert.equal(castSpell.timing.kind, 'alternatives');
        assert.deepEqual(castSpell.timing.options.map(option => option.kind), [
            'action', 'reaction', 'varied_gametime'
        ]);
        assert.equal(outputById.get('action:xphb:dodge').references.length, 4);
        assert.equal(outputById.get('condition:xphb:blinded').references.length, 3);
        assert.deepEqual(outputById.get('rule:xphb:d20-test').references.map(reference => reference.id), [
            'rule:xphb:ability-check',
            'rule:xphb:attack-roll',
            'rule:xphb:saving-throw'
        ]);
        assert.ok(outputById.get('rule:xphb:critical-hit').mechanics[0].roll.forms.includes('unarmed'));
        const sewerPlague = outputById.get('disease:xdmg:sewer-plague');
        assert.equal(sewerPlague.infection.savingThrow.dc, 11);
        assert.equal(sewerPlague.classification, 'magical-contagion');
        assert.ok(sewerPlague.symptoms.some(symptom =>
            symptom.condition.id === 'condition:xphb:exhaustion'));
        const crafting = outputById.get('rule:dmg:downtime-activity-crafting-a-magic-item');
        assert.equal(crafting.mechanics[0].kind, 'DOWNTIME_PROCEDURE');
        assert.equal(crafting.mechanics[0].procedure, 'craft');
        const tremorsense = outputById.get('sense:xphb:tremorsense').capabilities[0];
        assert.ok(tremorsense.detects.includes('movement'));
        assert.ok(tremorsense.restrictions.includes('requires-contact'));
        assert.equal(outputById.has('disease:dmg:cackle-fever'), false);
    });

    return { files: files.length, entities };
}

function testNegativeCases(examples) {
    const get = name => structuredClone(examples.find(([file]) => file === name)[1]);
    const actionValidator = ajv.getSchema('urn:thedndpub:schemas:core-rules:action:v1');
    const ruleValidator = ajv.getSchema('urn:thedndpub:schemas:core-rules:rule:v1');

    const stringCondition = get('action-grapple.json');
    stringCondition.operations[0].automaticSuccessWhen = 'target-incapacitated';
    rejects('Action condition strings rejected', () => actionValidator(stringCondition));

    const unresolvedCondition = get('action-grapple.json');
    unresolvedCondition.operations[0].automaticSuccessWhen.condition.id = 'condition:phb:missing-condition';
    rejects('unresolved typed Condition reference rejected', () => assertCanonicalReferences(unresolvedCondition));

    const malformedTrigger = get('action-escape-grapple.json');
    malformedTrigger.operations[2].appliesWhen.event = 'grappler-releases';
    rejects('string Action event trigger rejected', () => actionValidator(malformedTrigger));

    const duplicateOperation = get('action-shove.json');
    duplicateOperation.operations[2].operationId = 'shove-push';
    rejects('duplicate Action operation IDs rejected', () => assertActionIntegrity(duplicateOperation));

    const missingOperation = get('action-shove.json');
    missingOperation.choiceGroups[0].members[0].operationId = 'missing-operation';
    rejects('missing choice operation reference rejected', () => assertActionIntegrity(missingOperation));

    const missingOperationId = get('action-shove.json');
    delete missingOperationId.operations.find(operation => operation.operationId === 'shove-push').operationId;
    rejects('missing member operation ID rejected', () => assertActionIntegrity(missingOperationId));

    const badCardinality = get('action-shove.json');
    badCardinality.choiceGroups[0].cardinality = { minimumSelected: 2, maximumSelected: 1 };
    rejects('invalid Action choice cardinality rejected', () => assertActionIntegrity(badCardinality));

    const tooManyChoices = get('action-shove.json');
    tooManyChoices.choiceGroups[0].cardinality = { minimumSelected: 1, maximumSelected: 3 };
    rejects('Action choice maximum cannot exceed member count', () => assertActionIntegrity(tooManyChoices));

    const duplicateMember = get('action-shove.json');
    duplicateMember.choiceGroups[0].members[1].operationId = 'shove-push';
    rejects('duplicate Action choice member rejected', () => assertActionIntegrity(duplicateMember));

    const incompatibleGroup = get('action-shove.json');
    incompatibleGroup.operations[2].appliesWhen.choiceGroupId = 'another-choice';
    rejects('Action choice applicability mismatch rejected', () => assertActionIntegrity(incompatibleGroup));

    const duplicateGroup = get('action-shove.json');
    duplicateGroup.choiceGroups.push(structuredClone(duplicateGroup.choiceGroups[0]));
    rejects('duplicate Action choice group ID rejected', () => assertActionIntegrity(duplicateGroup));

    const multipleGroups = get('action-shove.json');
    multipleGroups.choiceGroups.push({
        ...structuredClone(multipleGroups.choiceGroups[0]),
        id: 'other-choice'
    });
    rejects('operation cannot belong to incompatible choice groups', () => assertActionIntegrity(multipleGroups));

    const wrongGroupTrigger = get('action-shove.json');
    wrongGroupTrigger.choiceGroups[0].trigger.operationId = 'shove-push';
    rejects('choice trigger must match a check operation', () => assertActionIntegrity(wrongGroupTrigger));

    const missingComponent = get('rule.json');
    missingComponent.composition.members[0].componentId = 'missing';
    rejects('missing Rule component reference rejected', () => assertRuleIntegrity(missingComponent));

    const duplicateRuleComponent = get('rule.json');
    duplicateRuleComponent.mechanics[1].componentId = duplicateRuleComponent.mechanics[0].componentId;
    rejects('duplicate Rule component ID rejected', () => assertRuleIntegrity(duplicateRuleComponent));

    const duplicateDefinition = get('rule.json');
    duplicateDefinition.mechanics.push({
        ...structuredClone(duplicateDefinition.mechanics[0]),
        componentId: 'critical-attack-copy'
    });
    rejects('duplicate Rule component definition rejected', () => assertRuleIntegrity(duplicateDefinition));

    const duplicateRuleReference = get('rule.json');
    duplicateRuleReference.composition.members[1].componentId = 'critical-attack';
    rejects('duplicate Rule component reference rejected', () => assertRuleIntegrity(duplicateRuleReference));

    const circularComposition = get('rule.json');
    const cyclicGroup = {
        kind: 'AND',
        members: [
            { kind: 'COMPONENT', componentId: 'critical-attack' },
            { kind: 'COMPONENT', componentId: 'critical-damage' }
        ]
    };
    cyclicGroup.members.push(cyclicGroup);
    circularComposition.composition = cyclicGroup;
    rejects('circular Rule composition rejected', () => assertRuleIntegrity(circularComposition));

    const circularDependency = get('rule.json');
    circularDependency.mechanics = [
        {
            componentId: 'attack-a',
            kind: 'D20_TEST',
            subtype: 'ATTACK_ROLL',
            roll: { kind: 'ATTACK_ROLL', forms: ['melee'] },
            appliesWhen: { kind: 'ON_HIT', componentId: 'attack-b' }
        },
        {
            componentId: 'attack-b',
            kind: 'D20_TEST',
            subtype: 'ATTACK_ROLL',
            roll: { kind: 'ATTACK_ROLL', forms: ['ranged'] },
            appliesWhen: { kind: 'ON_HIT', componentId: 'attack-a' }
        }
    ];
    delete circularDependency.composition;
    rejects('circular Rule applicability dependency rejected', () => assertRuleIntegrity(circularDependency));

    const wrongSave = get('rule-unarmed-strike.json');
    wrongSave.mechanics.find(item => item.componentId === 'unarmed-grapple-outcome')
        .appliesWhen.componentId = 'unarmed-attack';
    rejects('ON_FAILED_SAVE requires saving-throw component', () => assertRuleIntegrity(wrongSave));

    const wrongHit = get('rule.json');
    wrongHit.mechanics[1].damageModifier.appliesWhen = { kind: 'ON_HIT', componentId: 'critical-damage' };
    rejects('ON_HIT requires attack-roll component', () => assertRuleIntegrity(wrongHit));

    const wrongCritical = get('rule.json');
    wrongCritical.mechanics[0].roll.criticalOn = undefined;
    rejects('ON_CRITICAL_HIT requires critical result component', () => assertRuleIntegrity(wrongCritical));

    const contradictory = get('rule.json');
    contradictory.composition = {
        kind: 'OR',
        members: [
            { kind: 'COMPONENT', componentId: 'critical-attack' },
            { kind: 'COMPONENT', componentId: 'critical-damage' }
        ]
    };
    contradictory.mechanics[1].damageModifier.appliesWhen = {
        kind: 'ON_HIT',
        componentId: 'critical-attack'
    };
    rejects('contradictory Rule OR/applicability rejected', () => assertRuleIntegrity(contradictory));

    const inlineDefinition = get('rule.json');
    inlineDefinition.composition.members[0].damage = { damageTypes: ['fire'] };
    rejects('inline component definitions in composition rejected', () => ruleValidator(inlineDefinition));
}

function run() {
    const schemaFiles = loadSchemas();
    const phase3Result = validatePhase3Records(schemaFiles);
    const examples = loadExamples();
    const validators = new Map();
    for (const [file, example] of examples) {
        const model = example.id.split(':')[0];
        const validator = ajv.getSchema(`urn:thedndpub:schemas:core-rules:${model}:v1`);
        if (!validator) fail(`${file}: no schema registered for ${model}`);
        check(`schema example ${file}`, () => assert.equal(validator(example), true,
            JSON.stringify(validator.errors)));
        validators.set(file, validator);
    }

    const actionExamples = examples.filter(([, example]) => example.id.startsWith('action:'));
    const ruleExamples = examples.filter(([, example]) => example.id.startsWith('rule:'));
    check('Action operation IDs, triggers, conditions, and choice groups', () => {
        for (const [, action] of actionExamples) {
            assertCanonicalReferences(action);
            assertActionIntegrity(action);
        }
    });
    check('Rule component composition and applicability', () => {
        for (const [, rule] of ruleExamples) {
            assertCanonicalReferences(rule);
            assertRuleIntegrity(rule);
        }
    });

    const conditionValidator = ajv.compile({ $ref: `${commonSchemaId}#/$defs/conditionPredicate` });
    const triggerValidator = ajv.compile({ $ref: `${commonSchemaId}#/$defs/actionTrigger` });
    check('typed Action state and event examples', () => {
        const grapple = readJson(path.join(exampleRoot, 'action-grapple.json'));
        const dodge = readJson(path.join(exampleRoot, 'action-dodge.json'));
        const escape = readJson(path.join(exampleRoot, 'action-escape-grapple.json'));
        assert.equal(conditionValidator(grapple.operations[0].automaticSuccessWhen), true);
        assert.equal(conditionValidator(dodge.operations[0].when), true);
        assert.equal(conditionValidator(escape.operations[2].appliesWhen.condition), true);
        assert.equal(triggerValidator(grapple.operations[2].appliesWhen.event), true);
    });
    check('Shove has exactly one post-check outcome', () => {
        const shove = readJson(path.join(exampleRoot, 'action-shove.json'));
        assert.deepEqual(shove.choiceGroups[0].cardinality, { minimumSelected: 1, maximumSelected: 1 });
        assert.equal(shove.choiceGroups[0].trigger.operationId, 'shove-check');
    });
    check('nested Rule compositions preserve source-supported groups', () => {
        assert.equal(readJson(path.join(exampleRoot, 'rule.json')).composition.kind, 'AND');
        assert.equal(readJson(path.join(exampleRoot, 'rule-unarmed-strike.json')).composition.kind, 'OR');
    });
    check('canonical identity and typed reference integrity', () => {
        const ids = examples.map(([, example]) => example.id);
        assert.equal(new Set(ids).size, ids.length);
        for (const [, example] of examples) assertCanonicalReferences(example);
    });
    check('six approved domain contracts match the semantic baseline', assertApprovedDomainBaseline);
    check('schema and example JSON uses two-space formatting', () => {
        const files = [...schemaFiles.map(file => path.join(contractRoot, file)),
            ...examples.map(([file]) => path.join(exampleRoot, file))];
        for (const file of files) {
            const raw = fs.readFileSync(file, 'utf8');
            assert.equal(raw, `${JSON.stringify(readJson(file), null, 2)}\n`, file);
        }
    });
    testNegativeCases(examples);

    console.log(JSON.stringify({
        draft202012SchemasCompiled: schemaFiles.length,
        examplesValidated: examples.length,
        phase3CanonicalRecordsValidated: phase3Result.files,
        permanentChecksPassed: testResults.length,
        ruleComponentsChecked: ruleExamples.reduce((sum, [, rule]) => sum + rule.mechanics.length, 0),
        actionRecordsChecked: actionExamples.length
    }, null, 2));
}

run();
