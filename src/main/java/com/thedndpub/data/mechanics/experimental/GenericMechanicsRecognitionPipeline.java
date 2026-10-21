package com.thedndpub.data.mechanics.experimental;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.spell.SpellMechanicsSourceOwner;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Startup-cached, source-characteristic pipeline for the experimental v2 projection. */
public final class GenericMechanicsRecognitionPipeline {
    public static final String ENGINE_VERSION = "generic-strategy-pipeline-v2.4";
    private static final String DATASET = "5eTools v2.37.0";
    private final ObjectMapper mapper;
    private final ResourceLoader resources;
    private final MechanicSourceNormalizer normalizer;
    private final MechanicTypeDeterminer determiner = new MechanicTypeDeterminer();
    private final MechanicStrategyRegistry strategies = MechanicStrategyRegistry.standard();
    private final MechanicRelationshipStrategyRegistry relationshipStrategies =
            MechanicRelationshipStrategyRegistry.standard();

    public GenericMechanicsRecognitionPipeline(ObjectMapper mapper) {
        this(mapper, new DefaultResourceLoader());
    }

    public GenericMechanicsRecognitionPipeline(ObjectMapper mapper, ResourceLoader resources) {
        this.mapper = ModelChecks.required(mapper, "mapper");
        this.resources = ModelChecks.required(resources, "resources");
        normalizer = new MechanicSourceNormalizer(mapper);
    }

    public Map<SpellId, ExperimentalMechanicsV2Dto> evaluate(List<SpellDefinition> spells,
            List<MechanicsDocument> canonical, List<SpellMechanicsSourceOwner> owners) {
        Map<SpellId, SpellMechanicsSourceOwner> ownerIndex = new HashMap<>();
        owners.forEach(owner -> ModelChecks.require(ownerIndex.putIfAbsent(owner.id(), owner) == null,
                "Duplicate source owner in generic pipeline"));
        Map<SpellId, MechanicsDocument> canonicalIndex = new HashMap<>();
        canonical.forEach(document -> canonicalIndex.put(new SpellId(document.owner().id()), document));
        ModelChecks.require(ownerIndex.keySet().equals(spells.stream().map(SpellDefinition::id).collect(java.util.stream.Collectors.toSet())),
                "Generic pipeline source owners do not match spell catalogue");
        Map<String, JsonNode> sourceTrees = new HashMap<>();
        Map<SpellId, ExperimentalMechanicsV2Dto> output = new TreeMap<>(Comparator.comparing(SpellId::value));
        for (SpellDefinition spell : spells.stream().sorted(Comparator.comparing(value -> value.id().value())).toList()) {
            SpellMechanicsSourceOwner owner = ownerIndex.get(spell.id());
            try {
                JsonNode sourceTree = sourceTrees.computeIfAbsent(owner.revision().resourcePath(),
                        path -> readTree(owner.revision()));
                List<SourceFragment> fragments = normalizer.normalize(spell, owner, sourceTree);
                List<MechanicCandidate> candidates = determiner.determine(fragments);
                output.put(spell.id(), compose(spell, owner, canonicalIndex.get(spell.id()), fragments, candidates));
            } catch (RuntimeException exception) {
                var metadata = metadata(owner, canonicalIndex.get(spell.id()), List.of(), List.of());
                output.put(spell.id(), new ExperimentalMechanicsV2Dto(2, "EXPERIMENTAL", "EXTRACTION_ERROR",
                        List.of(), List.of(), List.of(), List.of(new ExperimentalDiagnosticDto("SOURCE_OR_EXTRACTION_ERROR", "ERROR",
                                null, owner.jsonPointer(), safeMessage(exception))), metadata));
            }
        }
        ModelChecks.require(output.size() == spells.size(), "Generic pipeline omitted spell owners");
        return Map.copyOf(output);
    }

    private ExperimentalMechanicsV2Dto compose(SpellDefinition spell, SpellMechanicsSourceOwner owner,
            MechanicsDocument canonical, List<SourceFragment> fragments, List<MechanicCandidate> candidates) {
        Map<String, SourceFragment> fragmentsById = new HashMap<>();
        fragments.forEach(fragment -> fragmentsById.put(fragment.id(), fragment));
        Map<String, MechanicBuild> mechanics = new TreeMap<>();
        Map<String, EvidenceBuild> evidence = new LinkedHashMap<>();
        List<ExperimentalDiagnosticDto> diagnostics = new ArrayList<>();
        List<String> recognizerIds = new ArrayList<>();
        List<ExtractedMechanic> extractedMechanics = new ArrayList<>();

        for (MechanicCandidate candidate : candidates) {
            SourceFragment fragment = fragmentsById.get(candidate.fragmentId());
            List<MechanicExtractionStrategy> familyStrategies = strategies.strategies(candidate.type());
            if (familyStrategies.isEmpty()) {
                diagnostics.add(new ExperimentalDiagnosticDto("NO_EXTRACTION_STRATEGY", "WARNING", null,
                        fragment.pointer(), "A mechanic candidate was classified, but no extraction strategy is registered."));
                continue;
            }
            for (MechanicExtractionStrategy strategy : familyStrategies) {
                recognizerIds.add(strategy.strategyId() + ":" + strategy.version());
                StrategyExtraction extraction = strategy.extract(candidate, fragment, spell);
                if (!extraction.supported()) {
                    diagnostics.add(new ExperimentalDiagnosticDto(extraction.diagnosticCode(), "WARNING", null,
                            fragment.pointer(), extraction.explanation()));
                    continue;
                }
                int payloadIndex = 0;
                for (ExtractedMechanicPayload extracted : extraction.payloads()) {
                    MechanicPayload payload = extracted.payload();
                    String payloadJson = json(payload);
                    String mechanicId = "exp-" + MechanicsHashes.sha256(spell.id().value() + "|"
                            + candidate.type() + "|" + fragment.id() + "|" + payloadJson + "|" + payloadIndex)
                            .substring(0, 20);
                    payloadIndex++;
                    MechanicBuild build = new MechanicBuild(mechanicId, candidate.type().name(), payload,
                            extracted.context(), fragment.id());
                    if (mechanics.putIfAbsent(mechanicId, build) != null) {
                        diagnostics.add(new ExperimentalDiagnosticDto("DUPLICATE_MECHANIC_ID", "ERROR", mechanicId,
                                fragment.pointer(), "Two extraction results produced the same stable mechanic identity."));
                        continue;
                    }
                    extractedMechanics.add(new ExtractedMechanic(mechanicId, candidate.type(), payload,
                            extracted.context(), fragment.id(), extracted.textStart(), extracted.textEnd()));
                    if (payload instanceof com.thedndpub.data.mechanics.model.DamageMechanic damage
                            && damage.damageType() instanceof com.thedndpub.data.mechanics.model.UnknownDamageType) {
                        diagnostics.add(new ExperimentalDiagnosticDto("DAMAGE_TYPE_UNRESOLVED", "WARNING", mechanicId,
                                fragment.pointer(), "A damage formula was recognized, but its authored type could not be resolved."));
                    }
                    String evidenceKey = owner.revision().resourcePath() + "|" + fragment.pointer();
                    EvidenceBuild evidenceBuild = evidence.computeIfAbsent(evidenceKey,
                            key -> evidence(owner, fragment));
                    evidenceBuild.mechanicIds.add(mechanicId);
                }
            }
        }

        Map<String, SourceFragment> evidenceFragments = new HashMap<>();
        Map<String, String> evidenceIdByFragment = new HashMap<>();
        for (SourceFragment fragment : fragments) {
            String evidenceKey = owner.revision().resourcePath() + "|" + fragment.pointer();
            EvidenceBuild evidenceBuild = evidence.get(evidenceKey);
            if (evidenceBuild != null) {
                evidenceFragments.put(fragment.id(), fragment);
                evidenceIdByFragment.put(fragment.id(), evidenceBuild.id);
            }
        }
        MechanicInterpretationContext relationshipContext = new MechanicInterpretationContext(owner.id(),
                owner.revision(), evidenceFragments, evidenceIdByFragment);
        List<ExperimentalMechanicRelationshipDto> relationships = new ArrayList<>();
        for (MechanicRelationshipStrategy strategy : relationshipStrategies.allStrategies()) {
            recognizerIds.add(strategy.strategyId() + ":" + strategy.version());
            RelationshipResolutionResult result = strategy.resolve(extractedMechanics, relationshipContext);
            relationships.addAll(result.relationships());
            diagnostics.addAll(result.diagnostics());
            for (ExperimentalMechanicRelationshipDto relationship : result.relationships()) {
                for (String evidenceId : relationship.evidenceIds()) {
                    evidence.values().stream().filter(item -> item.id.equals(evidenceId)).findFirst()
                            .ifPresent(item -> {
                                item.relationshipIds.add(relationship.id());
                                item.mechanicIds.add(relationship.sourceMechanicId());
                                item.mechanicIds.add(relationship.targetMechanicId());
                            });
                }
            }
        }
        java.util.Set<String> damageWithOutcome = relationships.stream()
                .filter(item -> item.type() == MechanicRelationshipType.SAVING_THROW_DAMAGE_OUTCOME
                        || item.type() == MechanicRelationshipType.ATTACK_DAMAGE_OUTCOME)
                .map(ExperimentalMechanicRelationshipDto::targetMechanicId)
                .collect(java.util.stream.Collectors.toSet());
        for (ExtractedMechanic mechanic : extractedMechanics) {
            if (mechanic.payload() instanceof com.thedndpub.data.mechanics.model.DamageMechanic
                    && !damageWithOutcome.contains(mechanic.id())) {
                SourceFragment fragment = fragmentsById.get(mechanic.sourceFragmentId());
                diagnostics.add(new ExperimentalDiagnosticDto("DAMAGE_APPLICATION_OUTCOME_UNRESOLVED", "WARNING",
                        mechanic.id(), fragment.pointer(), "No supported attack- or save-outcome relationship was established for this damage mechanic."));
            }
        }

        for (SourceFragment fragment : fragments) {
            if (fragment.signals().isEmpty()) {
                diagnostics.add(new ExperimentalDiagnosticDto("UNACCOUNTED_SOURCE_FRAGMENT", "INFO", null,
                        fragment.pointer(), "Source fragment was retained but no current mechanic recognizer classified it."));
            }
        }
        boolean conflict = diagnostics.stream().anyMatch(item -> "ERROR".equals(item.severity()));
        String outcome = candidates.isEmpty() ? "NO_PATTERN_MATCH" : conflict ? "CANDIDATE_CONFLICT" : "CANDIDATE_PARTIAL";
        if (candidates.isEmpty()) diagnostics.add(new ExperimentalDiagnosticDto("NO_PATTERN_MATCH", "INFO", null,
                owner.jsonPointer(), "No existing generic mechanic strategy matched; this does not mean the spell has no mechanics."));
        return new ExperimentalMechanicsV2Dto(2, "EXPERIMENTAL", outcome,
                mechanics.values().stream().map(MechanicBuild::dto).toList(),
                relationships.stream().sorted(Comparator.comparing(ExperimentalMechanicRelationshipDto::id)).toList(),
                evidence.values().stream().map(EvidenceBuild::dto).toList(), diagnostics.stream()
                .sorted(Comparator.comparing(ExperimentalDiagnosticDto::code).thenComparing(item -> item.pointer() == null ? "" : item.pointer()))
                .toList(), metadata(owner, canonical, recognizerIds, mechanics.values()));
    }

    private EvidenceBuild evidence(SpellMechanicsSourceOwner owner, SourceFragment fragment) {
        String id = "ev-" + MechanicsHashes.sha256(owner.revision().resourceSha256() + "|" + owner.id()
                + "|" + fragment.pointer()).substring(0, 20);
        boolean text = fragment.kind() == SourceFragment.Kind.AUTHORED_TEXT;
        return new EvidenceBuild(id, owner, fragment.pointer(), text ? 0 : null,
                text ? fragment.text().length() : null, text ? "AUTHORED_TEXT" : "STRUCTURED_FIELD");
    }

    private ExperimentalMechanicsMetadataDto metadata(SpellMechanicsSourceOwner owner,
            MechanicsDocument canonical, List<String> recognizers,
            java.util.Collection<MechanicBuild> experimental) {
        List<String> compare = new ArrayList<>();
        if (canonical != null) {
            compare.add("canonicalClaims=" + canonical.interpretations().size());
            List<String> canonicalPayloads = canonical.interpretations().stream().map(MechanicRecord::payload)
                    .map(this::json).toList();
            List<String> genericPayloads = experimental.stream().map(MechanicBuild::payload).map(this::json).toList();
            List<String> unmatchedCanonical = new ArrayList<>(canonicalPayloads);
            List<String> unmatchedGeneric = new ArrayList<>();
            for (String generic : genericPayloads) {
                int match = unmatchedCanonical.indexOf(generic);
                if (match >= 0) unmatchedCanonical.remove(match);
                else unmatchedGeneric.add(generic);
            }
            int reproduced = genericPayloads.size() - unmatchedGeneric.size();
            int changed = (int) unmatchedGeneric.stream().filter(generic -> canonical.interpretations().stream()
                    .anyMatch(claim -> json(claim.payload()).equals(generic) || claim.payload().getClass().getSimpleName()
                            .equals(experimental.stream().filter(item -> json(item.payload()).equals(generic))
                                    .findFirst().orElseThrow().payload().getClass().getSimpleName()))).count();
            compare.add("reproducedExactPayloads=" + reproduced);
            compare.add("missingCanonicalPayloads=" + unmatchedCanonical.size());
            compare.add("additionalExperimentalPayloads=" + unmatchedGeneric.size());
            compare.add("changedPayloadsSameFamily=" + changed);
            compare.add("genericStrategies=" + recognizers.stream().distinct().count());
        }
        return new ExperimentalMechanicsMetadataDto(owner.id(), ENGINE_VERSION, DATASET, owner.revision().resourcePath(),
                owner.revision().resourceSha256(), CoverageStatus.PARTIAL, "RECOGNIZED_FRAGMENTS_ONLY",
                "PENDING_REVIEW", "NEEDS_REVIEW", false, recognizers, compare);
    }

    private byte[] read(SourceArtifactRevision revision) {
        try {
            byte[] bytes = resources.getResource("classpath:" + revision.resourcePath()).getContentAsByteArray();
            if (!DATASET.equals(revision.datasetVersion()) || !MechanicsHashes.sha256(bytes).equals(revision.resourceSha256())) {
                throw new IllegalStateException("Pinned source fingerprint mismatch");
            }
            return bytes;
        } catch (IOException exception) {
            throw new IllegalStateException("Pinned source resource unavailable", exception);
        }
    }

    private JsonNode readTree(SourceArtifactRevision revision) {
        byte[] bytes = read(revision);
        try { return mapper.readTree(bytes); }
        catch (IOException exception) { throw new IllegalStateException("Pinned source JSON is malformed", exception); }
    }

    private String json(MechanicPayload payload) {
        try { return mapper.writerFor(com.thedndpub.data.mechanics.model.MechanicPayload.class).writeValueAsString(payload); }
        catch (JsonProcessingException exception) { throw new IllegalArgumentException("Cannot serialize candidate payload", exception); }
    }

    private static String safeMessage(RuntimeException exception) {
        String message = exception.getMessage();
        return message == null ? exception.getClass().getSimpleName() : message;
    }

    private static final class MechanicBuild {
        private final String id;
        private final String type;
        private final MechanicPayload payload;
        private final com.thedndpub.data.mechanics.model.MechanicContext context;
        private final String fragmentId;
        MechanicBuild(String id, String type, MechanicPayload payload,
                com.thedndpub.data.mechanics.model.MechanicContext context, String fragmentId) {
            this.id = id;
            this.type = type;
            this.payload = payload;
            this.context = context;
            this.fragmentId = fragmentId;
        }
        ExperimentalMechanicDto dto() {
            return new ExperimentalMechanicDto(id, type, payload, context);
        }
        MechanicPayload payload() { return payload; }
    }
    private static final class EvidenceBuild {
        private final String id; private final SpellMechanicsSourceOwner owner; private final String pointer;
        private final Integer start; private final Integer end; private final String role;
        private final List<String> mechanicIds = new ArrayList<>();
        private final List<String> relationshipIds = new ArrayList<>();
        EvidenceBuild(String id, SpellMechanicsSourceOwner owner, String pointer, Integer start, Integer end, String role) {
            this.id = id; this.owner = owner; this.pointer = pointer; this.start = start; this.end = end; this.role = role;
        }
        ExperimentalEvidenceDto dto() { return new ExperimentalEvidenceDto(id, mechanicIds, relationshipIds,
                owner.revision().resourcePath(), owner.revision().datasetVersion(), owner.revision().resourceSha256(),
                owner.jsonPointer() + pointer, start, end, owner.source(), "SUPPORTS_CANDIDATE", "generic-source-signals", "1"); }
    }
}
