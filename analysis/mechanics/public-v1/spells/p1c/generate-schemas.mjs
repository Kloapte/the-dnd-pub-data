import assert from 'node:assert/strict';
import fs from 'node:fs';
import { directory, formatted, publicationScope } from './review-artifacts.mjs';

const reference = (name) => ({ $ref: `#/$defs/${name}` });
const list = (items) => ({ type: 'array', items });
const text = { type: 'string', minLength: 1, pattern: '\\S' };
const nullableText = { anyOf: [text, { type: 'null' }] };
const hash = { type: 'string', pattern: '^[a-f0-9]{64}$' };
const nullableHash = { anyOf: [hash, { type: 'null' }] };
const nonnegative = { type: 'integer', minimum: 0 };
const uri = { type: 'string', format: 'uri' };
const pointer = { type: 'string', pattern: '^(?:/(?:[^~/]|~0|~1)*)*$' };

function object(properties) {
    return { type: 'object', additionalProperties: false, required: Object.keys(properties), properties };
}

const scope = publicationScope();
const definitions = {
    hash,
    span: { type: 'array', prefixItems: [nonnegative, nonnegative], items: false, minItems: 2, maxItems: 2 },
    evidenceReference: object({
        artifactId: text,
        artifactSha256: hash,
        jsonPointer: pointer,
        valueSha256: hash,
    }),
    pendingReview: object({
        status: { const: 'PENDING_REVIEW' },
        decision: { type: 'null' },
        reviewerId: { type: 'null' },
        reviewerAuthorityRef: { type: 'null' },
        decisionId: { type: 'null' },
        rationale: { type: 'null' },
        reviewedAt: { type: 'null' },
        releaseScopeId: { type: 'null' },
        decisionRecordRef: { type: 'null' },
    }),
    sourceEvidence: object({
        resource: text,
        resourceSha256: hash,
        ownerPointer: pointer,
        jsonPointer: pointer,
        entrySection: { enum: ['ENTRIES', 'ENTRIES_HIGHER_LEVEL'] },
        entryHierarchy: list(object({ jsonPointer: pointer, authoredType: nullableText })),
        origin: { const: 'AUTHORED' },
        offsetEncoding: { const: 'JAVA_UTF16_HALF_OPEN' },
        spanUtf16: reference('span'),
        textSha256: hash,
        tagTokenSha256: hash,
    }),
    expression: object({
        authoredToken: text,
        sha256: hash,
        notation: { enum: ['DAMAGE_TAG', 'DICE_TAG'] },
        lexicalKind: { enum: ['DICE_NOTATION', 'SIGNED_INTEGER', 'UNSIGNED_INTEGER'] },
    }),
    licensedEvidence: object({
        pdfPage: { type: 'integer', minimum: 1 },
        column: { enum: ['left', 'right'] },
        normalizedDocumentSpanUtf16: reference('span'),
        normalizedEntrySpanUtf16: reference('span'),
        matchedExpression: text,
        matchedBeforeWords: nonnegative,
        matchedAfterWords: nonnegative,
        contextAnchorSha256: hash,
        strongLiteralAnchor: { const: true },
    }),
    licensedMatch: object({
        documentId: { enum: ['SRD-5.1', 'SRD-5.2.1'] },
        documentVersion: { enum: ['5.1', '5.2.1'] },
        documentUrl: uri,
        documentSha256: hash,
        spellHeading: text,
        descriptor: text,
        licenseId: { const: 'CC-BY-4.0' },
        licenseUrl: uri,
        evidence: reference('licensedEvidence'),
        matchReference: reference('evidenceReference'),
        documentReference: reference('evidenceReference'),
    }),
    attribution: object({
        noticeId: { enum: ['wizards-srd-5.1', 'wizards-srd-5.2.1'] },
        requiredAttributionSha256: hash,
        noticeReference: reference('evidenceReference'),
        proposalSha256: hash,
        changesDisclosureSha256: hash,
    }),
    candidate: object({
        occurrenceId: text,
        spellId: { type: 'string', pattern: '^spell:(phb|xphb):.+$' },
        sourceCode: { enum: ['PHB', 'XPHB'] },
        formulaOccurrenceIndex: nonnegative,
        publicationScopeId: { const: scope.id },
        publicationScopeSha256: hash,
        sourceEvidence: reference('sourceEvidence'),
        expression: reference('expression'),
        licensedMatch: reference('licensedMatch'),
        attribution: reference('attribution'),
        eligibility: object({
            classification: { const: 'VERIFIED_LICENSED_MATCH' },
            rationale: text,
            technicalReliability: { const: 'TAG_SYNTAX_ONLY_NO_EFFECT_ROLE' },
            p1bReference: reference('evidenceReference'),
        }),
        bindingSha256: hash,
        review: reference('pendingReview'),
    }),
};

function schema(id, description, shape, defs = definitions) {
    return {
        $schema: 'https://json-schema.org/draft/2020-12/schema',
        $id: `urn:dnd-pub:review:${id}:1`,
        description,
        ...shape,
        $defs: defs,
    };
}

const candidateSchema = schema('spell-formula-candidates',
    'Immutable pending-review evidence, not a publication allowlist. Record human decisions in a separate journal.',
    object({
        artifactKind: { const: 'SPELL_FORMULA_REVIEW_CANDIDATES_NOT_RUNTIME_AUTHORIZATION' },
        schemaVersion: { const: '1.0.0' },
        manifestId: { const: 'spell-formulas-v1-p1c-candidates-1' },
        datasetVersion: { const: '5eTools v2.37.0' },
        publicationAuthorization: { const: 'NOT_GRANTED' },
        bindingHashAlgorithm: { const: 'SHA256_SORTED_KEY_JSON_V1' },
        publicationScope: { const: scope },
        inputArtifacts: list(object({ id: text, reviewArtifactPath: text, sha256: hash })),
        entries: { ...list(reference('candidate')), minItems: 250, maxItems: 250 },
    }));

const attributionSchema = schema('spell-formula-attribution',
    'Pending attribution/delivery design; approval of candidate expressions does not approve this proposal.',
    object({
        artifactKind: { const: 'ATTRIBUTION_DELIVERY_REVIEW_PROPOSAL' },
        schemaVersion: { const: '1.0.0' },
        publicationAuthorization: { const: 'NOT_GRANTED' },
        scopeId: { const: scope.id },
        appliesTo: text,
        delivery: object({
            proposedRoute: { const: '/api/attributions/spell-formulas-v1' },
            responseLinkRelation: { const: 'describedby' },
            bundledNoticeName: { const: 'THIRD-PARTY-NOTICES.md' },
            catalogVersion: { const: 'spell-formulas-v1-notices-1' },
            blanketResponseLicenseHeader: { const: false },
        }),
        notices: { ...list(object({
            id: text,
            documentId: { enum: ['SRD-5.1', 'SRD-5.2.1'] },
            documentVersion: { enum: ['5.1', '5.2.1'] },
            documentUrl: uri,
            documentSha256: hash,
            licenseId: { const: 'CC-BY-4.0' },
            licenseUrl: uri,
            requiredAttribution: text,
            requiredAttributionSha256: hash,
            disclaimerReference: uri,
            disclaimerNotice: text,
            p1bNoticeReference: reference('evidenceReference'),
        })), minItems: 2, maxItems: 2 },
        changesDisclosure: text,
        changesDisclosureSha256: hash,
        downstreamNote: text,
        review: reference('pendingReview'),
    }));

const journalEvent = object({
    eventId: text,
    sequence: { type: 'integer', minimum: 1 },
    occurrenceId: text,
    candidateManifestSha256: hash,
    bindingSha256: hash,
    publicationScopeId: { const: scope.id },
    attributionProposalSha256: hash,
    action: { enum: ['AUTHORIZE_EXACT_SCOPE', 'REJECT', 'WITHDRAW'] },
    reviewerId: text,
    reviewerAuthorityRef: text,
    rationale: text,
    reviewedAt: { type: 'string', format: 'date-time', pattern: 'Z$' },
    supersedesEventId: nullableText,
    previousEventSha256: nullableHash,
    eventSha256: hash,
});
journalEvent.allOf = [{
    if: { properties: { action: { const: 'WITHDRAW' } }, required: ['action'] },
    then: { properties: { supersedesEventId: text } },
}];
const journalSchema = schema('spell-formula-decision-journal',
    'Human decision record design only. Schema validity is not reviewer authentication or release authorization.',
    object({
        artifactKind: { const: 'REVIEW_DECISION_JOURNAL_NOT_RUNTIME_ALLOWLIST' },
        schemaVersion: { const: '1.0.0' },
        candidateManifestSha256: hash,
        headEventSha256: nullableHash,
        events: list(journalEvent),
    }), {});

for (const [file, value] of [
    ['candidate-manifest.schema.json', candidateSchema],
    ['attribution-proposal.schema.json', attributionSchema],
    ['decision-journal.schema.json', journalSchema],
]) {
    const path = `${directory}/${file}`;
    if (process.argv.includes('--check')) {
        assert.equal(fs.readFileSync(path, 'utf8'), formatted(value), `Schema differs: ${file}`);
    } else {
        fs.writeFileSync(path, formatted(value));
    }
}
console.log('Three closed JSON Schema 2020-12 review contracts prepared/checked; no decisions granted.');
