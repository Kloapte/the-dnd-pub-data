import { createHash } from 'node:crypto';
import { readFile, readdir, writeFile, mkdir } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const repositoryRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../../..');
const sourceDirectory = path.join(repositoryRoot, 'src/main/resources/data/spells');
const priorCandidateInventory = path.join(
    repositoryRoot,
    'target/closure-inventory-preview-2/7.4.4A-spell-capability-inventory.json'
);
const outputDirectory = process.argv[2]
    ? path.resolve(process.argv[2])
    : path.dirname(fileURLToPath(import.meta.url));
const jsonOutput = path.join(outputDirectory, 'generic-first-catalogue-applicability.json');
const markdownOutput = path.join(outputDirectory, 'generic-first-catalogue-applicability-summary.md');

const structuredFields = [
    'level', 'school', 'time', 'range', 'components', 'duration', 'spellAttack', 'savingThrow',
    'damageInflict', 'conditionInflict', 'conditionImmune', 'damageResist', 'damageImmune',
    'damageVulnerable', 'scalingLevelDice', 'miscTags', 'areaTags', 'affectsCreatureType', 'meta',
    'entries', 'entriesHigherLevel'
];

const structuredFamilies = {
    ATTACK: ['spellAttack'],
    SAVING_THROW: ['savingThrow'],
    DAMAGE: ['damageInflict'],
    CONDITION: ['conditionInflict', 'conditionImmune'],
    DAMAGE_PROTECTION: ['damageResist', 'damageImmune', 'damageVulnerable'],
    SCALING: ['scalingLevelDice'],
    AREA_OR_TARGET: ['areaTags', 'affectsCreatureType'],
    CASTING_AND_DURATION: ['time', 'range', 'components', 'duration'],
    SOURCE_MECHANIC_METADATA: ['meta'],
    AUTHORED_RULE_TEXT: ['entries', 'entriesHigherLevel']
};

const lexicalFamilies = {
    ATTACK: /\b(?:attack roll|spell attack|make (?:a )?(?:melee |ranged )?(?:spell )?attack|attack against)\b/giu,
    SAVING_THROW: /\b(?:saving throw|saving throws|succeeds? on (?:a )?saving throw|fails? (?:a )?saving throw)\b/giu,
    DAMAGE: /\{@damage\b|\b(?:damage|takes? .{0,50} damage|deals? .{0,50} damage)\b/giu,
    HEALING: /\b(?:heals?|healing|regain(?:s)? .{0,50} hit points|hit points? restored)\b/giu,
    CONDITION: /\{@condition\b|\b(?:blinded|charmed|deafened|frightened|grappled|incapacitated|invisible|paralyzed|petrified|poisoned|prone|restrained|stunned|unconscious)\b/giu,
    MODIFIER_OR_PROTECTION: /\b(?:bonus to|penalty to|resistance to|immune to|immunity to|can't be less than|advantage on|disadvantage on)\b/giu,
    SCALING: /\{@scaledamage\b|\{@scaledice\b|\b(?:spell slot|higher level|slot level|when you reach .{0,30} level|cantrip)\b/giu,
    PERSISTENT: /\b(?:for the duration|until the spell ends|lasts? for|concentration,? up to|while .{0,40} (?:remains|stays))\b/giu,
    TRIGGERED_OR_RECURRING: /\b(?:whenever|each time|at the start of|at the end of|on a hit|on a miss|when the target moves|when you take damage|once per turn)\b/giu,
    MULTIPLE_ATTACKS_OR_TARGETS: /\b(?:each|every|one per|up to (?:two|three|four|five|\d+)) (?:attack|ray|beam|dart|target|creature|projectile|mote|stone)\b|\b(?:several|multiple|two|three|four|five) attacks\b/giu,
    CREATED_OR_SUMMONED_ENTITY: /\b(?:summon|create|conjure|spectral weapon|familiar|servant|creature appears)\b/giu,
    MOVEMENT_OR_TELEPORT: /\b(?:move|movement|teleport|push(?:ed)?|pull(?:ed)?|fly|speed)\b/giu,
    RESOURCE_USE_OR_CONSUMPTION: /\b(?:expend|consume|spend|ends? on|uses? up|pebbles|charges|motes)\b/giu
};

function sha256(bytes) {
    return createHash('sha256').update(bytes).digest('hex');
}

function slug(value) {
    return value.normalize('NFKD').replace(/\p{M}+/gu, '').toLowerCase()
        .replace(/[^a-z0-9]+/gu, '-').replace(/^-|-$/gu, '');
}

function ownerId(spell) {
    return `spell:${slug(spell.source)}:${slug(spell.name)}`;
}

function pointerPart(value) {
    return String(value).replace(/~/gu, '~0').replace(/\//gu, '~1');
}

function nonEmpty(value) {
    if (value === null || value === undefined || value === false || value === '') return false;
    if (Array.isArray(value)) return value.length > 0;
    if (typeof value === 'object') return Object.keys(value).length > 0;
    return true;
}

function sourceStrings(value, pointer, result) {
    if (typeof value === 'string') {
        result.push({ pointer, text: value });
    } else if (Array.isArray(value)) {
        value.forEach((item, index) => sourceStrings(item, `${pointer}/${index}`, result));
    } else if (value && typeof value === 'object') {
        for (const key of Object.keys(value).sort()) {
            sourceStrings(value[key], `${pointer}/${pointerPart(key)}`, result);
        }
    }
}

function collectTextCues(spell, rootPointer) {
    const strings = [];
    sourceStrings(spell.entries ?? [], `${rootPointer}/entries`, strings);
    sourceStrings(spell.entriesHigherLevel ?? [], `${rootPointer}/entriesHigherLevel`, strings);
    const families = {};
    for (const [family, regex] of Object.entries(lexicalFamilies)) {
        const matches = [];
        for (const item of strings) {
            regex.lastIndex = 0;
            let match;
            while ((match = regex.exec(item.text)) !== null && matches.length < 40) {
                matches.push({
                    jsonPointer: item.pointer,
                    cue: match[0],
                    startUtf16: match.index,
                    endUtf16: match.index + match[0].length
                });
                if (match[0].length === 0) regex.lastIndex++;
            }
            if (matches.length >= 40) break;
        }
        if (matches.length > 0) families[family] = matches;
    }
    return families;
}

function fieldPointers(spell, rootPointer) {
    return structuredFields.filter(field => nonEmpty(spell[field]))
        .map(field => `${rootPointer}/${field}`);
}

function familyPointers(spell, rootPointer) {
    const result = {};
    for (const [family, fields] of Object.entries(structuredFamilies)) {
        const pointers = fields.filter(field => nonEmpty(spell[field]))
            .map(field => `${rootPointer}/${field}`);
        if (pointers.length > 0) result[family] = pointers;
    }
    return result;
}

function classify(inventoryRow) {
    if (inventoryRow.processingState === 'PROCESSED') {
        return {
            primary: 'CURRENTLY_PROCESSED',
            secondary: [],
            productionEligible: true,
            blockingConditions: []
        };
    }
    if (inventoryRow.patternMatched && inventoryRow.potentialMechanicCount > 0) {
        return {
            primary: 'OWNER_OR_EDITION_GATED',
            secondary: ['INVENTORY_ONLY_CANDIDATE_RULE_MATCH'],
            productionEligible: false,
            blockingConditions: ['Production owner predicate excludes this unprocessed candidate']
        };
    }
    if (inventoryRow.patternMatched) {
        return {
            primary: 'EXISTING_RULE_NEEDS_GENERALIZATION',
            secondary: ['CANDIDATE_RULE_DID_NOT_PRODUCE_SUPPORTED_CLAIMS'],
            productionEligible: false,
            blockingConditions: ['Inventory candidate rule matched, but no supported candidate claim was produced']
        };
    }
    return {
        primary: 'AMBIGUOUS_OR_UNDETERMINED',
        secondary: ['SOURCE_REVIEW_REQUIRED'],
        productionEligible: false,
        blockingConditions: ['No existing inventory candidate rule matched; lexical or structured cues are not semantic proof']
    };
}

function rowFor(spell, inventoryRow, revision) {
    const id = ownerId(spell);
    const rootPointer = `/spell/${spell.__sourceIndex}`;
    const assessment = classify(inventoryRow);
    const candidateFamilies = [...new Set([
        ...(inventoryRow.candidateMechanicFamilies ?? []),
        ...(inventoryRow.unsupportedMechanicFamilies ?? [])
    ])].sort();
    const textCues = collectTextCues(spell, rootPointer);
    const structuredMechanicFields = familyPointers(spell, rootPointer);
    const lexicalFamiliesFound = Object.keys(textCues).sort();

    return {
        ownerId: id,
        spellName: spell.name,
        sourceCode: spell.source,
        level: spell.level ?? null,
        school: spell.school ?? null,
        sourceRevision: {
            datasetVersion: '5eTools v2.37.0',
            resourcePath: revision.resourcePath,
            resourceSha256: revision.sha256
        },
        ownerJsonPointer: rootPointer,
        current: {
            processingState: inventoryRow.processingState,
            coverageStatus: inventoryRow.currentCoverageStatus,
            productionAdapter: inventoryRow.processingState === 'PROCESSED' ? inventoryRow.patternId : null
        },
        assessment: {
            primaryClassification: assessment.primary,
            secondaryFindings: assessment.secondary,
            productionRuleMatchVerified: assessment.productionEligible,
            inventoryCandidateAdapter: inventoryRow.patternMatched ? inventoryRow.patternId : null,
            inventoryCandidateClassification: inventoryRow.classification,
            inventoryCandidateMechanicFamilies: candidateFamilies,
            inventoryCandidateClaimCount: inventoryRow.potentialMechanicCount,
            unsupportedIndicators: [...(inventoryRow.unsupportedReasons ?? [])].sort(),
            unassessedSourceIndicators: [...(inventoryRow.unassessedSourceContent ?? [])].sort(),
            diagnosticCodes: [...(inventoryRow.diagnosticCodes ?? [])].sort(),
            blockingConditions: assessment.blockingConditions,
            additionalJavaImplementationRequired: assessment.primary === 'CURRENTLY_PROCESSED'
                ? false
                : assessment.primary === 'OWNER_OR_EDITION_GATED' || assessment.primary === 'EXISTING_RULE_NEEDS_GENERALIZATION'
                    ? true
                    : null,
            furtherHumanSourceAssessmentRequired: inventoryRow.processingState !== 'PROCESSED'
        },
        sourceSignals: {
            structuredFieldsPresent: fieldPointers(spell, rootPointer),
            structuredMechanicFields,
            authoredTextCues: textCues,
            signalInterpretation: 'Candidate cues only; not semantic validation or a mechanic claim'
        }
    };
}

function increment(map, key) {
    map[key] = (map[key] ?? 0) + 1;
}

function renderMarkdown(report) {
    const rows = Object.entries(report.summary.bySource).sort(([a], [b]) => a.localeCompare(b));
    const sourceTable = rows.map(([source, counts]) =>
        `| ${source} | ${counts.owners} | ${counts.processedOwners} | ${counts.notAttemptedOwners} | `
        + `${counts.EXISTING_RULE_ELIGIBLE} | ${counts.OWNER_OR_EDITION_GATED} | `
        + `${counts.EXISTING_RULE_NEEDS_GENERALIZATION} | ${counts.NEW_MECHANIC_FAMILY} | `
        + `${counts.AMBIGUOUS_OR_UNDETERMINED} |`).join('\n');
    const familyRows = Object.entries(report.summary.sourceSignalOwners).sort(([a], [b]) => a.localeCompare(b))
        .map(([family, counts]) => `| ${family} | ${counts.structured} | ${counts.authoredTextCue} |`)
        .join('\n');
    const total = report.summary.classificationCounts;
    return `# Generic-First Full Catalogue Applicability Assessment\n\n`
        + `**Assessment only.** No production extraction, canonical claim generation, review, licensing, or publication change was performed.\n\n`
        + `Machine-readable per-owner records: [generic-first-catalogue-applicability.json](generic-first-catalogue-applicability.json).\n\n`
        + `## Scope and method\n\n`
        + `The deterministic script reads all pinned spell JSON resources and joins each source-qualified owner to the pre-existing, non-authoritative Step 7.4.4 closure inventory preview at target/closure-inventory-preview-2/7.4.4A-spell-capability-inventory.json. It does not invoke a mechanics adapter. Inventory-only candidate results are reported as candidate evidence, not production eligibility. Authored phrase offsets are JavaScript UTF-16 code-unit indices in the exact scalar string at the listed source pointer. Lexical cues are leads, not proof.\n\n`
        + `All 18 source resources and ${report.summary.ownerCount} distinct owner IDs reconcile one-to-one with the prior inventory. The source revision hashes below are computed from the pinned bytes; the repository verifier separately confirms all 175 pinned JSON hashes.\n\n`
        + `## Summary\n\n`
        + `| Owner classification | Count |\n|---|---:|\n`
        + `| CURRENTLY_PROCESSED | ${total.CURRENTLY_PROCESSED} |\n`
        + `| EXISTING_RULE_ELIGIBLE | ${total.EXISTING_RULE_ELIGIBLE} |\n`
        + `| OWNER_OR_EDITION_GATED | ${total.OWNER_OR_EDITION_GATED} |\n`
        + `| EXISTING_RULE_NEEDS_GENERALIZATION | ${total.EXISTING_RULE_NEEDS_GENERALIZATION} |\n`
        + `| NEW_MECHANIC_FAMILY | ${total.NEW_MECHANIC_FAMILY} |\n`
        + `| AMBIGUOUS_OR_UNDETERMINED | ${total.AMBIGUOUS_OR_UNDETERMINED} |\n`
        + `| **Total** | **${report.summary.ownerCount}** |\n\n`
        + `Production-eligible unprocessed owners verified without code changes: **${total.EXISTING_RULE_ELIGIBLE}**. The production service automatically processes any owner that satisfies a registered production matcher, so remaining unprocessed owners are not already eligible under those predicates. The separate inventory-only PHB path reports ${total.OWNER_OR_EDITION_GATED} candidate owners with claims; these require a production eligibility change and independent source review.\n\n`
        + `The classification of 737 owners remains undetermined: the prior inventory did not produce a candidate rule result for them, and cue matching cannot establish semantic compatibility or a genuinely new mechanic family. This is explicit uncertainty, not evidence that the owners contain no supported mechanics.\n\n`
        + `## Source distribution\n\n`
        + `| Source | Owners | Processed | Not attempted | Existing-rule eligible | Owner/edition gated | Needs generalization | New family confirmed | Undetermined |\n`
        + `|---|---:|---:|---:|---:|---:|---:|---:|---:|\n${sourceTable}\n\n`
        + `PHB and XPHB are separately keyed source identities. See each machine record's owner ID, JSON pointer, resource path, and SHA-256.\n\n`
        + `## Reusable rule assessment\n\n`
        + `| Rule | Current production applicability | Additional candidate assessment | Main boundary |\n|---|---|---|---|\n`
        + `| Shield extractor | PHB/XPHB Shield owners already processed | No additional semantically validated owner | Exact owner family; same-name spells in other sources are not eligible |\n`
        + `| PHB attack rules | Current PHB owner allowlist | Attack fields/text on other owners are cues only; their exact eligible count is unknown | Cast-time, attack kind, target, range, hit binding and rider conditions need source-local matching |\n`
        + `| PHB save/damage generalizer | Fixed PHB owner subset | Candidate save/damage structures appear in inventory, but no unprocessed production-eligible owner was verified | Current grammar handles bounded save/damage forms; other outcomes, areas, repeated damage and multiple expressions need separate rules |\n`
        + `| PHB condition rules | PHB condition fields/tags and recognized authored forms | 48 inventory-only candidate owners with claims across existing internal rules | Owner/edition gating and unsupported subfamilies; output is partial and not human reviewed |\n`
        + `| Six dedicated adapters | Only their exact owner(s) | No extrapolation to similar names or mechanics | Owner-bound source text and editions; model reuse does not imply matcher reuse |\n\n`
        + `The 48 candidate owners and their rule family, unsupported indicators, source paths and lexical cue pointers are in the JSON. A candidate claim produced by the earlier inventory-only assessment is not a production interpretation, approved result, or evidence of complete spell coverage.\n\n`
        + `## Mechanic-family signals\n\n`
        + `Counts are distinct owners with a nonempty structured source field or at least one authored-text cue. These columns overlap and must not be summed. The source scan does not validate the meaning or application of any cue.\n\n`
        + `| Family | Structured-field owners | Authored-text cue owners |\n|---|---:|---:|\n${familyRows}\n\n`
        + `Other source structures preserved per owner include school/level, casting time, range, components, duration, tags, target categories, scaling declarations, entries and higher-level entries. Mechanical relevance and whether text is descriptive or operative remain subject to human source assessment.\n\n`
        + `## Source differences and execution obstacles\n\n`
        + `The catalogue contains 361 PHB 2014 and 391 XPHB 2024 owners; XGE contains 95, TCE 21 and AU 33. Production source fingerprints cover only PHB, XPHB, XGE, TCE and AU. The other 13 source codes have no current mechanics source-edition extraction policy. XPHB and PHB matching is source-qualified; Chromatic Orb identities remain separate.\n\n`
        + `Only one top-level adapter may match a spell. The registry fails on overlap and has no precedence or composition plan. The PHB extractor manually composes rules internally, while inventory-only PHB support is broader than production support. Coverage completeness depends on each rule declaring all relevant source paths. These limits prevent treating broad catalogue scanning as generic extraction.\n\n`
        + `## Reuse opportunities and next cohorts\n\n`
        + `1. Review the 48 PHB inventory candidates with claims; prioritize source cases that match the existing condition and save/damage grammars, and retain their unsupported gaps.\n`
        + `2. Compare unprocessed PHB attack/save/damage structured signals with authored text before generalizing current allowlists. The number of semantic matches is unknown.\n`
        + `3. Sample XPHB, XGE, TCE and AU candidates separately; do not reuse PHB rules across editions based on fields or similar wording.\n`
        + `4. Treat persistent effects, object consumption, multi-target/attack sequencing, areas and damage-triggered chains as candidates for new reusable families only after a diverse source cohort confirms shared semantics.\n\n`
        + `No cohorts were extracted or enabled. Step 7.4.4 remains COMPLETE; Step 7.4.3 remains PARTIAL.\n`;
}

async function main() {
    const inventoryBytes = await readFile(priorCandidateInventory);
    const inventory = JSON.parse(inventoryBytes.toString('utf8'));
    if (inventory.datasetVersion !== '5eTools v2.37.0') {
        throw new Error('The prior candidate inventory is not from the pinned dataset version');
    }
    const inventoryById = new Map(inventory.spells.map(row => [row.spellId, row]));
    if (inventoryById.size !== inventory.spells.length) throw new Error('Duplicate owner in candidate inventory');

    const files = (await readdir(sourceDirectory)).filter(name => /^spells-.+\.json$/u.test(name)).sort();
    if (files.length !== 18) throw new Error(`Expected 18 pinned spell resources, found ${files.length}`);
    const revisions = [];
    const owners = [];
    for (const fileName of files) {
        const bytes = await readFile(path.join(sourceDirectory, fileName));
        const resource = JSON.parse(bytes.toString('utf8'));
        if (!Array.isArray(resource.spell)) throw new Error(`Missing spell array in ${fileName}`);
        const revision = {
            resourcePath: `data/spells/${fileName}`,
            sha256: sha256(bytes),
            ownerCount: resource.spell.length
        };
        revisions.push(revision);
        resource.spell.forEach((spell, index) => {
            Object.defineProperty(spell, '__sourceIndex', { value: index, enumerable: false });
            const id = ownerId(spell);
            const inventoryRow = inventoryById.get(id);
            if (!inventoryRow) throw new Error(`No inventory baseline row for ${id}`);
            if (inventoryRow.source.toUpperCase() !== String(spell.source).toUpperCase()
                || inventoryRow.name !== spell.name) {
                throw new Error(`Inventory owner mismatch for ${id}`);
            }
            owners.push(rowFor(spell, inventoryRow, revision));
        });
    }
    owners.sort((a, b) => a.ownerId.localeCompare(b.ownerId));
    if (owners.length !== inventory.catalogTotal || owners.length !== inventoryById.size) {
        throw new Error('Source, prior inventory, and owner totals do not reconcile');
    }
    if (new Set(owners.map(owner => owner.ownerId)).size !== owners.length) {
        throw new Error('Source-aware owner IDs are not unique');
    }

    const classificationCounts = Object.fromEntries([
        'CURRENTLY_PROCESSED', 'EXISTING_RULE_ELIGIBLE', 'OWNER_OR_EDITION_GATED',
        'EXISTING_RULE_NEEDS_GENERALIZATION', 'NEW_MECHANIC_FAMILY', 'AMBIGUOUS_OR_UNDETERMINED'
    ].map(key => [key, 0]));
    const bySource = {};
    const sourceSignalOwners = {};
    for (const owner of owners) {
        const classification = owner.assessment.primaryClassification;
        increment(classificationCounts, classification);
        const source = owner.sourceCode.toUpperCase();
        bySource[source] ??= {
            owners: 0, processedOwners: 0, notAttemptedOwners: 0,
            CURRENTLY_PROCESSED: 0,
            EXISTING_RULE_ELIGIBLE: 0, OWNER_OR_EDITION_GATED: 0,
            EXISTING_RULE_NEEDS_GENERALIZATION: 0, NEW_MECHANIC_FAMILY: 0,
            AMBIGUOUS_OR_UNDETERMINED: 0
        };
        increment(bySource[source], 'owners');
        increment(bySource[source], classification);
        increment(bySource[source], owner.current.processingState === 'PROCESSED'
            ? 'processedOwners' : 'notAttemptedOwners');

        for (const family of Object.keys(owner.sourceSignals.structuredMechanicFields)) {
            sourceSignalOwners[family] ??= { structured: 0, authoredTextCue: 0 };
            increment(sourceSignalOwners[family], 'structured');
        }
        for (const family of Object.keys(owner.sourceSignals.authoredTextCues)) {
            sourceSignalOwners[family] ??= { structured: 0, authoredTextCue: 0 };
            increment(sourceSignalOwners[family], 'authoredTextCue');
        }
    }
    if (Object.values(classificationCounts).reduce((sum, count) => sum + count, 0) !== owners.length) {
        throw new Error('Classification totals do not reconcile');
    }
    if (classificationCounts.CURRENTLY_PROCESSED !== inventory.currentlyProcessedCount
        || owners.filter(owner => owner.current.processingState === 'NOT_ATTEMPTED').length
            !== inventory.catalogTotal - inventory.currentlyProcessedCount) {
        throw new Error('Processed and unprocessed totals differ from the baseline inventory');
    }
    const normalizedInventorySources = Object.fromEntries(Object.entries(inventory.sourceCounts)
        .map(([source, count]) => [source.toUpperCase(), count]));
    const observedSourceCounts = Object.fromEntries(Object.entries(bySource)
        .map(([source, counts]) => [source, counts.owners]));
    if (JSON.stringify(Object.entries(normalizedInventorySources).sort())
        !== JSON.stringify(Object.entries(observedSourceCounts).sort())) {
        throw new Error('Source owner counts differ from the baseline inventory');
    }

    const report = {
        schemaVersion: 1,
        assessmentType: 'READ_ONLY_CATALOGUE_APPLICABILITY',
        datasetVersion: inventory.datasetVersion,
        inputBasis: {
            pinnedSourceDirectory: 'src/main/resources/data/spells',
            priorNonAuthoritativeCandidateInventory: 'target/closure-inventory-preview-2/7.4.4A-spell-capability-inventory.json',
            adapterExecutionPerformedByThisScript: false,
            canonicalInterpretationsCreatedByThisScript: false
        },
        summary: {
            ownerCount: owners.length,
            sourceResourceCount: files.length,
            processedOwnerCount: inventory.currentlyProcessedCount,
            notAttemptedOwnerCount: inventory.catalogTotal - inventory.currentlyProcessedCount,
            existingRuleEligibleUnprocessedCount: classificationCounts.EXISTING_RULE_ELIGIBLE,
            inventoryOnlyClaimCandidateCount: classificationCounts.OWNER_OR_EDITION_GATED,
            classificationCounts,
            bySource,
            sourceSignalOwners
        },
        sourceResources: revisions,
        classificationDefinitions: {
            CURRENTLY_PROCESSED: 'Owner is PROCESSED in the prior production inventory baseline.',
            EXISTING_RULE_ELIGIBLE: 'Unprocessed owner with verified match to an existing production predicate; none inferred from fields alone.',
            OWNER_OR_EDITION_GATED: 'Unprocessed owner produced candidate claims through the prior inventory-only matcher path; production support remains gated.',
            EXISTING_RULE_NEEDS_GENERALIZATION: 'Prior inventory candidate matcher selected a rule but produced no supported claims.',
            NEW_MECHANIC_FAMILY: 'Reserved for a semantically established mechanic absent from existing recognition/model capabilities; not assigned from lexical cues.',
            AMBIGUOUS_OR_UNDETERMINED: 'No prior candidate rule result establishes safe applicability; source cues are leads only.'
        },
        owners
    };
    await mkdir(outputDirectory, { recursive: true });
    const jsonBytes = `${JSON.stringify(report, null, 2)}\n`;
    await writeFile(jsonOutput, jsonBytes, 'utf8');
    await writeFile(markdownOutput, renderMarkdown(report), 'utf8');
    process.stdout.write(JSON.stringify({
        ownerCount: report.summary.ownerCount,
        sourceResourceCount: report.summary.sourceResourceCount,
        classificationCounts: report.summary.classificationCounts,
        jsonOutput,
        markdownOutput
    }) + '\n');
}

main().catch(error => {
    process.stderr.write(`${error.stack ?? error}\n`);
    process.exitCode = 1;
});
