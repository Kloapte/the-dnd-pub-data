import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import fs from 'node:fs';

export const root = 'analysis/mechanics/public-v1/spells';
export const directory = `${root}/p1c`;
export const read = (path) => JSON.parse(fs.readFileSync(path, 'utf8'));
export const sha256 = (value) => crypto.createHash('sha256').update(value).digest('hex');
export const fileHash = (path) => sha256(fs.readFileSync(path));
export const formatted = (value) => JSON.stringify(value, null, 2) + '\n';

/** Sorted object keys, unchanged array order/string contents, JSON scalar serialization; no normalization. */
export function canonical(value) {
    if (Array.isArray(value)) {
        return '[' + value.map(canonical).join(',') + ']';
    }
    if (value !== null && typeof value === 'object') {
        return '{' + Object.keys(value).sort().map((key) =>
            JSON.stringify(key) + ':' + canonical(value[key])).join(',') + '}';
    }
    return JSON.stringify(value);
}

export const valueHash = (value) => sha256(canonical(value));

export function atPointer(value, pointer) {
    if (pointer === '') {
        return value;
    }
    assert.ok(pointer.startsWith('/'), 'JSON Pointer must be absolute');
    return pointer.slice(1).split('/').reduce((node, segment) => {
        const key = segment.replace(/~1/g, '/').replace(/~0/g, '~');
        assert.ok(node !== undefined && Object.hasOwn(node, key), `Missing evidence pointer: ${pointer}`);
        return node[key];
    }, value);
}

export function pendingReview() {
    return {
        status: 'PENDING_REVIEW',
        decision: null,
        reviewerId: null,
        reviewerAuthorityRef: null,
        decisionId: null,
        rationale: null,
        reviewedAt: null,
        releaseScopeId: null,
        decisionRecordRef: null,
    };
}

export function publicationScope() {
    return {
        id: 'spell-formula-observations-v1-literals',
        publicFieldPaths: [
            'SpellDto.formulaObservations[].occurrenceIndex',
            'SpellDto.formulaObservations[].notation',
            'SpellDto.formulaObservations[].expression.authoredToken',
            'SpellDto.formulaObservations[].expression.lexicalKind',
            'SpellDto.formulaObservations[].attributionId',
        ],
        allowedNotation: ['DAMAGE_TAG', 'DICE_TAG'],
        meaning: 'Authored syntax observations only; no gameplay effects or inferred relationships',
        excludedContent: [
            'Raw markup and descriptive entry text',
            'Source evidence pointers, spans, diagnostics and reviewer metadata',
            'Inline scaling arguments, display arguments and opaque expressions',
            'Damage, healing, targeting, save, duration and scaling relationships',
        ],
    };
}

function inputArtifacts() {
    return [
        ['batch6-runtime-evidence', `${root}/runtime-evidence.json`],
        ['p1b-crosswalk', `${root}/p1b/occurrence-crosswalk.json`],
        ['p1b-documents', `${root}/p1b/official-documents.json`],
        ['p1b-attribution', `${root}/p1b/attribution-review.json`],
        ['p1b-coverage', `${root}/p1b/coverage.json`],
        ['p1b-verification', `${root}/p1b/verification.json`],
    ].map(([id, reviewArtifactPath]) => ({ id, reviewArtifactPath, sha256: fileHash(reviewArtifactPath) }));
}

export function buildArtifacts() {
    const artifacts = inputArtifacts();
    const artifactById = new Map(artifacts.map((artifact) => [artifact.id, artifact]));
    const artifactValues = new Map(artifacts.map((artifact) => [artifact.id, read(artifact.reviewArtifactPath)]));
    const crosswalk = artifactValues.get('p1b-crosswalk');
    const documents = artifactValues.get('p1b-documents');
    const attribution = artifactValues.get('p1b-attribution');
    const scope = publicationScope();

    function reference(artifactId, jsonPointer) {
        const artifact = artifactById.get(artifactId);
        return {
            artifactId,
            artifactSha256: artifact.sha256,
            jsonPointer,
            valueSha256: valueHash(atPointer(artifactValues.get(artifactId), jsonPointer)),
        };
    }

    const selectedDocuments = documents.documents.filter((document) =>
        ['SRD-5.1', 'SRD-5.2.1'].includes(document.id));
    const attributionProposal = {
        artifactKind: 'ATTRIBUTION_DELIVERY_REVIEW_PROPOSAL',
        schemaVersion: '1.0.0',
        publicationAuthorization: 'NOT_GRANTED',
        scopeId: scope.id,
        appliesTo: 'Only separately authorized SpellDto.formulaObservations, not the complete Spell response',
        delivery: {
            proposedRoute: '/api/attributions/spell-formulas-v1',
            responseLinkRelation: 'describedby',
            bundledNoticeName: 'THIRD-PARTY-NOTICES.md',
            catalogVersion: 'spell-formulas-v1-notices-1',
            blanketResponseLicenseHeader: false,
        },
        notices: selectedDocuments.map((document) => {
            const index = attribution.notices.findIndex((notice) => notice.documentId === document.id);
            const notice = attribution.notices[index];
            return {
                id: notice.id,
                documentId: document.id,
                documentVersion: document.version,
                documentUrl: document.url,
                documentSha256: document.sha256,
                licenseId: document.license,
                licenseUrl: documents.licenseUrl,
                requiredAttribution: notice.requiredAttribution,
                requiredAttributionSha256: sha256(notice.requiredAttribution),
                disclaimerReference: 'https://creativecommons.org/licenses/by/4.0/legalcode.en#s5',
                disclaimerNotice: 'Section 5 of CC-BY-4.0 includes a Disclaimer of Warranties and Limitation of Liability.',
                p1bNoticeReference: reference('p1b-attribution', `/notices/${index}`),
            };
        }),
        changesDisclosure: attribution.proposedChangeNotice,
        changesDisclosureSha256: sha256(attribution.proposedChangeNotice),
        downstreamNote: 'API discovery does not by itself establish compliance for every downstream redistribution.',
        review: pendingReview(),
    };
    const attributionPlanSha256 = sha256(formatted(attributionProposal));
    artifacts.push({
        id: 'p1c-attribution-proposal',
        reviewArtifactPath: `${directory}/attribution-proposal.json`,
        sha256: attributionPlanSha256,
    });

    const entries = [];
    const excludedEntries = [];
    for (const [index, row] of crosswalk.entries()) {
        const p1bReference = reference('p1b-crosswalk', `/${index}`);
        if (row.publicationReviewClassification !== 'VERIFIED_LICENSED_MATCH') {
            excludedEntries.push({
                occurrenceId: row.occurrenceId,
                spellId: row.spellId,
                sourceCode: row.sourceCode,
                originalClassification: row.publicationReviewClassification,
                disposition: 'OUTSIDE_APPROVAL_SET_NOT_A_HUMAN_REJECTION',
                p1bReference,
            });
            continue;
        }
        const documentId = row.potentialReleaseBasis.documentId;
        const matchIndex = row.verifiedMatches.findIndex((match) => match.documentId === documentId);
        assert.ok(matchIndex >= 0, row.occurrenceId);
        const match = row.verifiedMatches[matchIndex];
        const documentIndex = documents.documents.findIndex((document) => document.id === documentId);
        const document = documents.documents[documentIndex];
        const search = row.documentSearches.find((value) => value.documentId === documentId);
        const entry = search.entries.find((value) => value.heading === match.entry);
        const noticeIndex = attribution.notices.findIndex((notice) => notice.documentId === documentId);
        const notice = attribution.notices[noticeIndex];
        const evidence = {
            occurrenceId: row.occurrenceId,
            spellId: row.spellId,
            sourceCode: row.sourceCode,
            formulaOccurrenceIndex: row.formulaOccurrenceIndex,
            publicationScopeId: scope.id,
            publicationScopeSha256: valueHash(scope),
            sourceEvidence: {
                resource: row.sourceResource,
                resourceSha256: row.sourceResourceSha256,
                ownerPointer: row.ownerPointer,
                jsonPointer: row.jsonPointer,
                entrySection: row.entrySection,
                entryHierarchy: row.entryHierarchy,
                origin: row.sourceOrigin,
                offsetEncoding: 'JAVA_UTF16_HALF_OPEN',
                spanUtf16: row.spanUtf16,
                textSha256: row.sourceTextSha256,
                tagTokenSha256: row.authoredTokenSha256,
            },
            expression: {
                authoredToken: row.exactExpression,
                sha256: row.exactExpressionSha256,
                notation: row.notation,
                lexicalKind: row.lexicalKind,
            },
            licensedMatch: {
                documentId,
                documentVersion: document.version,
                documentUrl: document.url,
                documentSha256: document.sha256,
                spellHeading: match.entry,
                descriptor: entry.descriptor,
                licenseId: document.license,
                licenseUrl: documents.licenseUrl,
                evidence: match.evidence,
                matchReference: reference('p1b-crosswalk', `/${index}/verifiedMatches/${matchIndex}`),
                documentReference: reference('p1b-documents', `/documents/${documentIndex}`),
            },
            attribution: {
                noticeId: notice.id,
                requiredAttributionSha256: sha256(notice.requiredAttribution),
                noticeReference: reference('p1b-attribution', `/notices/${noticeIndex}`),
                proposalSha256: attributionPlanSha256,
                changesDisclosureSha256: attributionProposal.changesDisclosureSha256,
            },
            eligibility: {
                classification: row.publicationReviewClassification,
                rationale: row.decisionRationale,
                technicalReliability: row.technicalReliability,
                p1bReference,
            },
        };
        entries.push({ ...evidence, bindingSha256: valueHash(evidence), review: pendingReview() });
    }
    assert.equal(entries.length, 250);
    assert.equal(excludedEntries.length, 458);
    const manifest = {
        artifactKind: 'SPELL_FORMULA_REVIEW_CANDIDATES_NOT_RUNTIME_AUTHORIZATION',
        schemaVersion: '1.0.0',
        manifestId: 'spell-formulas-v1-p1c-candidates-1',
        datasetVersion: '5eTools v2.37.0',
        publicationAuthorization: 'NOT_GRANTED',
        bindingHashAlgorithm: 'SHA256_SORTED_KEY_JSON_V1',
        publicationScope: scope,
        inputArtifacts: artifacts,
        entries,
    };
    const manifestSha256 = sha256(formatted(manifest));
    const decisionJournal = {
        artifactKind: 'REVIEW_DECISION_JOURNAL_NOT_RUNTIME_ALLOWLIST',
        schemaVersion: '1.0.0',
        candidateManifestSha256: manifestSha256,
        headEventSha256: null,
        events: [],
    };
    const exclusions = {
        artifactKind: 'P1C_SCOPE_RECONCILIATION_EXCLUSIONS',
        candidateManifestSha256: manifestSha256,
        reviewedCandidateCount: 250,
        excludedCandidateCount: 458,
        originalCandidateCount: crosswalk.length,
        entries: excludedEntries,
    };
    return new Map([
        ['attribution-proposal.json', attributionProposal],
        ['candidate-manifest.json', manifest],
        ['decision-journal.json', decisionJournal],
        ['excluded-candidates.json', exclusions],
    ]);
}
