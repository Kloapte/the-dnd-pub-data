import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import fs from 'node:fs';

const directory = 'analysis/mechanics/public-v1/spells';
const outputDirectory = `${directory}/p1b`;
const cache = 'target/p1b-official';
const read = (path) => JSON.parse(fs.readFileSync(path, 'utf8'));
const hash = (value) => crypto.createHash('sha256').update(value).digest('hex');

// Typography normalization is for document lookup only, never for the authored publication token.
function typography(text) {
    return text.normalize('NFKC')
        .replace(/[\u00ad\u0002]/g, '')
        .replace(/[\u2010\u2011]/g, '-')
        .replace(/-+/g, '-')
        .replace(/[\u2018\u2019]/g, "'")
        .replace(/[\u201c\u201d]/g, '"')
        .replace(/\s+/g, ' ').trim();
}

const schools = '(?:abjuration|conjuration|divination|enchantment|evocation|illusion|necromancy|transmutation)';
const oldDescriptor = new RegExp(`^(?:[1-9](?:st|nd|rd|th)-level ${schools}|${schools} cantrip)`, 'i');
const newDescriptor = new RegExp(`^(?:Level [1-9] ${schools}|${schools} Cantrip)`, 'i');

function extractDocument(version) {
    const segments = [];
    let text = '';
    const headings = [];
    for (const file of fs.readdirSync(`${cache}/${version}`).filter((name) => /^\d+-/.test(name)).sort()) {
        const original = fs.readFileSync(`${cache}/${version}/${file}`, 'utf8');
        const lines = original.split(/\n/).map(typography);
        const page = Number(file.slice(0, 3));
        const column = file.includes('left') ? 'left' : 'right';
        // Remove only the document's running header/footer, not numeric spell content.
        const cleaned = lines.filter((line) => !/^(?:\d+\s*)?System Reference Document/.test(line));
        const segmentStart = text.length;
        for (let index = 0; index < cleaned.length; index++) {
            const line = cleaned[index];
            if ((version === '5.1' ? oldDescriptor : newDescriptor).test(line) && index > 0) {
                const name = cleaned[index - 1];
                const position = text.length - name.length - 1;
                headings.push({ name, descriptor: line, start: position, page, column });
            }
            text += line + ' ';
        }
        segments.push({ page, column, start: segmentStart, end: text.length,
            extractedTextSha256: hash(original) });
    }
    const entries = headings.map((heading, index) => {
        const followingChapter = version === '5.1' ? 'Magic Items ' : 'Rules Glossary ';
        const lastEnd = text.indexOf(followingChapter, heading.start + heading.name.length);
        return { ...heading, end: headings[index + 1]?.start ?? lastEnd };
    });
    assert.equal(entries.length, version === '5.1' ? 319 : 339);
    assert.equal(new Set(entries.map((entry) => entry.name.toLowerCase())).size, entries.length);
    assert.ok(entries.every((entry) => entry.end > entry.start));
    return { version, text, segments, entries };
}

const documents = ['5.1', '5.2.0', '5.2.1'].map(extractDocument);

if (process.argv.includes('--inspect')) {
    fs.writeFileSync(`${cache}/headings.json`, JSON.stringify(documents.map(({ version, entries }) =>
        ({ version, entries })), null, 2) + '\n');
}

const officialManifest = read(`${outputDirectory}/official-documents.json`);
for (const document of documents) {
    const manifest = officialManifest.documents.find((entry) => entry.version === document.version);
    assert.equal(hash(fs.readFileSync(`${cache}/srd-${document.version}.pdf`)), manifest.sha256);
}

const runtime = read(`${directory}/runtime-evidence.json`);
const coverage = read(`${directory}/coverage.json`);
const pinned = read('analysis/mechanics/batch-10a-object-membership/pre-implementation-hashes.json');
const pinnedPaths = Object.keys(pinned).filter((path) =>
    path.startsWith('src/main/resources/data/') && path.endsWith('.json')).sort();
assert.equal(pinnedPaths.length, 175);
for (const path of pinnedPaths) {
    assert.equal(hash(fs.readFileSync(path)), pinned[path], path);
}

const sourceDocuments = new Map();
for (const resource of Object.keys(runtime.resourceHashes)) {
    const bytes = fs.readFileSync(`src/main/resources/${resource}`);
    assert.equal(hash(bytes), runtime.resourceHashes[resource]);
    sourceDocuments.set(resource, JSON.parse(bytes));
}

function atPointer(document, pointer) {
    return pointer.split('/').slice(1).reduce((value, segment) =>
        value[segment.replace(/~1/g, '/').replace(/~0/g, '~')], document);
}

function words(text) {
    return typography(text).toLowerCase().match(/[a-z0-9]+(?:'[a-z]+)?/g) ?? [];
}

function countMatching(left, right, fromEnd) {
    let count = 0;
    while (count < Math.min(left.length, right.length)) {
        const leftIndex = fromEnd ? left.length - count - 1 : count;
        const rightIndex = fromEnd ? right.length - count - 1 : count;
        if (left[leftIndex] !== right[rightIndex]) {
            break;
        }
        count++;
    }
    return count;
}

function sourceContext(observation, text) {
    const [start, end] = observation.spanUtf16;
    // Stop at markup boundaries. Do not render or interpret another tag to manufacture an anchor.
    const before = text.slice(0, start).split(/[{}]/).at(-1);
    const after = text.slice(end).split(/[{}]/)[0];
    return { before: words(before).slice(-10), after: words(after).slice(0, 10) };
}

const schoolNames = { A: 'abjuration', C: 'conjuration', D: 'divination', E: 'enchantment',
    V: 'evocation', I: 'illusion', N: 'necromancy', T: 'transmutation' };

function descriptorMatches(entry, source) {
    const descriptor = entry.descriptor.toLowerCase();
    const levelMatch = descriptor.match(/^(?:level )?([1-9])(?:st|nd|rd|th)?/);
    const level = levelMatch ? Number(levelMatch[1]) : 0;
    return level === source.level && descriptor.includes(schoolNames[source.school]);
}

function expressionMatches(document, entry, observation, context) {
    const escaped = observation.expression.trim().split(/\s+/)
        .map((part) => part.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')).join('\\s*');
    const pattern = new RegExp(`(?<![a-z0-9])${escaped}(?![a-z0-9])`, 'gi');
    const entryText = document.text.slice(entry.start, entry.end);
    const matches = [];
    for (const match of entryText.matchAll(pattern)) {
        const before = words(entryText.slice(0, match.index)).slice(-10);
        const after = words(entryText.slice(match.index + match[0].length)).slice(0, 10);
        const left = countMatching(context.before, before, true);
        const right = countMatching(context.after, after, false);
        const position = entry.start + match.index;
        const segment = document.segments.find((value) => position >= value.start && position < value.end);
        matches.push({
            pdfPage: segment.page,
            column: segment.column,
            normalizedDocumentSpanUtf16: [position, position + match[0].length],
            normalizedEntrySpanUtf16: [match.index, match.index + match[0].length],
            matchedExpression: match[0],
            matchedBeforeWords: left,
            matchedAfterWords: right,
            contextAnchorSha256: hash(JSON.stringify({
                before: before.slice(before.length - left), after: after.slice(0, right),
            })),
            strongLiteralAnchor: left >= 2 && right >= 2 && left + right >= 10,
        });
    }
    return matches;
}

function examine(document, source, observation, context) {
    const hints = [source.name, source.srd, source.srd52].filter((value) => typeof value === 'string');
    const names = new Set(hints.map((value) => typography(value).toLowerCase()));
    const matches = document.entries.filter((entry) => names.has(entry.name.toLowerCase()));
    return {
        documentId: `SRD-${document.version}`,
        editionRoute: source.source === 'PHB' && document.version === '5.1'
            ? '2014_CORE_TO_SRD_5_1'
            : source.source === 'XPHB' && document.version.startsWith('5.2')
                ? '2024_CORE_TO_SRD_5_2' : 'EDITION_OR_SOURCE_CORRESPONDENCE_UNESTABLISHED',
        entries: matches.map((entry) => ({
            heading: entry.name,
            headingPdfPage: entry.page,
            headingColumn: entry.column,
            descriptor: entry.descriptor,
            metadataMatches: descriptorMatches(entry, source),
            normalizedEntrySha256: hash(document.text.slice(entry.start, entry.end)),
            expressionMatches: expressionMatches(document, entry, observation, context),
        })),
    };
}

const candidates = runtime.formulas.filter((observation) => ['damage', 'dice'].includes(observation.tag)
    && observation.arguments.length === 1
    && ['DICE_NOTATION', 'SIGNED_INTEGER', 'UNSIGNED_INTEGER'].includes(observation.expressionKind));
assert.equal(candidates.length, 708);
assert.equal(new Set(candidates.map((value) => value.spellId)).size, 402);
assert.equal(candidates.length, coverage.formulas.hypotheticalLiteralSubset.observations);
const occurrenceIds = new Set();
const crosswalk = [];
for (const observation of candidates) {
    const sourceDocument = sourceDocuments.get(observation.resource);
    const source = atPointer(sourceDocument, observation.ownerPointer);
    const text = atPointer(sourceDocument, observation.pointer);
    const owner = runtime.records.find((record) => record.id === observation.spellId);
    assert.equal(owner.name, source.name);
    assert.equal(owner.source, source.source);
    assert.equal(hash(text), observation.textSha256);
    assert.equal(text.slice(...observation.spanUtf16), observation.originalTag);
    assert.equal(observation.arguments[0], observation.expression);
    assert.equal(observation.origin, 'AUTHORED');
    const id = `${observation.spellId}#formula-${observation.occurrenceIndex}`;
    assert.ok(!occurrenceIds.has(id), id);
    occurrenceIds.add(id);
    const context = sourceContext(observation, text);
    const searches = documents.map((document) => examine(document, source, observation, context));
    const verified = searches.flatMap((search) => search.entries.flatMap((entry) => {
        const strong = entry.expressionMatches.filter((match) => match.strongLiteralAnchor);
        return entry.metadataMatches && strong.length === 1
            && search.editionRoute !== 'EDITION_OR_SOURCE_CORRESPONDENCE_UNESTABLISHED'
            ? [{ documentId: search.documentId, entry: entry.heading, evidence: strong[0] }] : [];
    }));
    const routedEntries = searches.filter((search) =>
        search.editionRoute !== 'EDITION_OR_SOURCE_CORRESPONDENCE_UNESTABLISHED')
        .flatMap((search) => search.entries);
    const anyEntries = searches.flatMap((search) => search.entries);
    let decision;
    let rationale;
    let reasonCode;
    if (verified.length > 0) {
        decision = 'VERIFIED_LICENSED_MATCH';
        reasonCode = 'CORRESPONDING_EDITION_EXPRESSION_AND_UNIQUE_LITERAL_ANCHOR';
        rationale = 'Exact expression and unique literal local anchor in a corresponding SRD edition entry; level and school agree. Evidence only, not release authorization.';
    } else if (routedEntries.length > 0 && routedEntries.every((entry) =>
        entry.expressionMatches.length === 0)) {
        decision = 'VERIFIED_NOT_COVERED';
        reasonCode = 'EXPRESSION_ABSENT_FROM_EXAMINED_CORRESPONDING_ENTRIES';
        rationale = 'Expression absent from every examined corresponding-edition counterpart entry. This is bounded negative evidence, not a claim about all possible licenses or mechanical facts.';
    } else if (anyEntries.length > 0) {
        decision = 'AMBIGUOUS_MATCH';
        reasonCode = routedEntries.length === 0 ? 'SOURCE_OR_EDITION_CORRESPONDENCE_UNESTABLISHED'
            : routedEntries.every((entry) => !entry.metadataMatches) ? 'HEADER_METADATA_MISMATCH'
                : routedEntries.some((entry) => entry.expressionMatches.filter((match) =>
                    match.strongLiteralAnchor).length > 1) ? 'NON_UNIQUE_LITERAL_ANCHOR'
                    : 'INSUFFICIENT_LITERAL_ANCHOR';
        rationale = 'Plausible counterpart exists, but edition/source correspondence, header metadata, occurrence alignment or literal context does not meet the verification rule.';
    } else {
        decision = 'NO_VERIFIABLE_EVIDENCE';
        reasonCode = 'NO_COUNTERPART_IDENTIFIED_IN_EXAMINED_ROSTERS';
        rationale = 'No counterpart identified by authored name or a source-provided rename hint in the three examined SRD spell rosters; no alternate rights basis established.';
    }
    crosswalk.push({
        occurrenceId: id,
        spellId: observation.spellId,
        name: source.name,
        sourceCode: source.source,
        sourceResource: observation.resource,
        sourceResourceSha256: runtime.resourceHashes[observation.resource],
        ownerPointer: observation.ownerPointer,
        formulaOccurrenceIndex: observation.occurrenceIndex,
        jsonPointer: observation.pointer,
        entrySection: observation.section,
        entryHierarchy: observation.hierarchy,
        sourceOrigin: observation.origin,
        spanUtf16: observation.spanUtf16,
        sourceTextSha256: observation.textSha256,
        authoredToken: observation.originalTag,
        authoredTokenSha256: hash(observation.originalTag),
        exactExpression: observation.expression,
        exactExpressionSha256: hash(observation.expression),
        originalArguments: observation.arguments,
        notation: observation.tag === 'damage' ? 'DAMAGE_TAG' : 'DICE_TAG',
        lexicalKind: observation.expressionKind,
        technicalEligibility: 'BOUNDED_FIRST_RELEASE_CANDIDATE',
        technicalReliability: 'TAG_SYNTAX_ONLY_NO_EFFECT_ROLE',
        sourceEdition: source.source === 'PHB' ? '2014_CORE_SOURCE'
            : source.source === 'XPHB' ? '2024_CORE_SOURCE' : 'SOURCE_SPECIFIC_EDITION_NOT_ASSERTED',
        sourceRenameHints: [source.srd, source.srd52].filter((value) => typeof value === 'string'),
        sourceRelationships: Object.fromEntries(['additionalSources', 'otherSources', 'referenceSources', 'reprintedAs']
            .filter((field) => Object.hasOwn(source, field)).map((field) => [field, source[field]])),
        documentSearches: searches,
        verifiedMatches: verified,
        potentialReleaseBasis: verified.length === 0 ? null : {
            documentId: source.source === 'PHB' ? 'SRD-5.1' : 'SRD-5.2.1',
            status: 'EVIDENCE_ONLY_REQUIRES_RELEASE_AUTHORIZATION',
        },
        attributionIds: verified.map((match) => `wizards-${match.documentId.toLowerCase()}`),
        publicationReviewClassification: decision,
        decisionReasonCode: reasonCode,
        decisionRationale: rationale,
        releaseAuthorization: 'NOT_GRANTED',
    });
}

const decisions = ['VERIFIED_LICENSED_MATCH', 'VERIFIED_NOT_COVERED', 'AMBIGUOUS_MATCH',
    'NO_VERIFIABLE_EVIDENCE', 'REQUIRES_SEPARATE_PERMISSION'];
const classified = Object.fromEntries(decisions.map((decision) => {
    const rows = crosswalk.filter((row) => row.publicationReviewClassification === decision);
    return [decision, { observations: rows.length, owners: new Set(rows.map((row) => row.spellId)).size }];
}));
const verifiedRows = crosswalk.filter((row) => row.publicationReviewClassification === 'VERIFIED_LICENSED_MATCH');
const verifiedIds = new Set(verifiedRows.map((row) => row.occurrenceId));
const potentialStatuses = {};
for (const owner of runtime.records) {
    const formulas = runtime.formulas.filter((row) => row.spellId === owner.id);
    const published = formulas.filter((row) => verifiedIds.has(`${row.spellId}#formula-${row.occurrenceIndex}`)).length;
    const status = formulas.length === 0 ? 'NONE_OBSERVED' : published === 0 ? 'WITHHELD'
        : published === formulas.length ? 'COMPLETE' : 'PARTIAL';
    potentialStatuses[status] = (potentialStatuses[status] ?? 0) + 1;
}

const sourceDistribution = {};
for (const row of crosswalk) {
    sourceDistribution[row.sourceCode] ??= {};
    const values = sourceDistribution[row.sourceCode];
    values[row.publicationReviewClassification] = (values[row.publicationReviewClassification] ?? 0) + 1;
}
const report = {
    dataset: '5eTools v2.37.0',
    inputEvidenceSha256: hash(fs.readFileSync(`${directory}/runtime-evidence.json`)),
    candidateCount: crosswalk.length,
    candidateOwnerCount: new Set(crosswalk.map((row) => row.spellId)).size,
    uniqueOccurrenceCount: occurrenceIds.size,
    pinnedSourceHashesVerified: pinnedPaths.length,
    classifications: classified,
    reasonCounts: Object.fromEntries([...new Set(crosswalk.map((row) => row.decisionReasonCode))].sort()
        .map((reason) => [reason, crosswalk.filter((row) => row.decisionReasonCode === reason).length])),
    sourceDistribution,
    potentialReleaseObservations: verifiedRows.length,
    potentialReleaseOwners: new Set(verifiedRows.map((row) => row.spellId)).size,
    authorizedReleaseObservations: 0,
    unresolvedCandidateOwners: new Set(crosswalk.filter((row) =>
        row.publicationReviewClassification !== 'VERIFIED_LICENSED_MATCH').map((row) => row.spellId)).size,
    verifiedInBoth52Revisions: verifiedRows.filter((row) =>
        row.verifiedMatches.some((match) => match.documentId === 'SRD-5.2.0')
        && row.verifiedMatches.some((match) => match.documentId === 'SRD-5.2.1')).length,
    currentStatuses: { NONE_OBSERVED: 547, WITHHELD: 422, PARTIAL: 0, COMPLETE: 0 },
    hypotheticalStatusesIfAllVerifiedMatchesSeparatelyApproved: potentialStatuses,
    reviewedDocuments: documents.map((document) => ({
        documentId: `SRD-${document.version}`, spellRosterEntries: document.entries.length,
        headingInventorySha256: hash(JSON.stringify(document.entries)),
        normalizedDocumentSha256: hash(document.text),
    })),
};
fs.writeFileSync(`${outputDirectory}/occurrence-crosswalk.json`, JSON.stringify(crosswalk, null, 2) + '\n');
fs.writeFileSync(`${outputDirectory}/coverage.json`, JSON.stringify(report, null, 2) + '\n');
fs.writeFileSync(`${outputDirectory}/srd-spell-rosters.json`, JSON.stringify(documents.map(({ version, entries }) =>
    ({ version, entries })), null, 2) + '\n');
console.log(JSON.stringify(report, null, 2));
