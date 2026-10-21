package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.inspection.ExperimentalMechanicsComparisonDto;
import com.thedndpub.data.mechanics.inspection.ExperimentalMechanicsOutcome;
import com.thedndpub.data.mechanics.inspection.ExperimentalSpellMechanicsDto;
import com.thedndpub.data.mechanics.inspection.MechanicInspectionDto;
import com.thedndpub.data.mechanics.inspection.SpellMechanicsInspectionDto;
import com.thedndpub.data.mechanics.inspection.SpellMechanicsInspectionMapper;
import com.thedndpub.data.mechanics.model.CatalogDomain;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsOwnerReference;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.MechanicsSourceContent;
import com.thedndpub.data.mechanics.model.MechanicsSourceEvidenceVerifier;
import com.thedndpub.data.mechanics.model.MechanicsSourceOwnerPointer;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct;
import com.thedndpub.data.services.SourceRegistry;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Startup-only, isolated evaluation of existing mechanic patterns across the complete Spell catalogue. */
public final class ExperimentalSpellMechanicsDryRunService {
    public static final String ENGINE_VERSION = "generic-first-dry-run-v1";

    private static final String DATASET_VERSION = "5eTools v2.37.0";

    private final ObjectMapper mapper;
    private final ResourceLoader resources;
    private final SpellMechanicsPatternRegistry patterns = SpellMechanicsPatternRegistry.standard();
    private final SpellMechanicsInspectionMapper inspectionMapper = new SpellMechanicsInspectionMapper();

    public ExperimentalSpellMechanicsDryRunService(ObjectMapper mapper) {
        this(mapper, new DefaultResourceLoader());
    }

    public ExperimentalSpellMechanicsDryRunService(ObjectMapper mapper, ResourceLoader resources) {
        this.mapper = ModelChecks.required(mapper, "mapper");
        this.resources = ModelChecks.required(resources, "resources");
    }

    public Map<SpellId, ExperimentalSpellMechanicsDto> evaluate(
            List<SpellDefinition> definitions,
            List<MechanicsDocument> unattempted,
            List<MechanicsDocument> canonical,
            List<SpellMechanicsSourceOwner> manifest) {
        Map<SpellId, MechanicsDocument> unattemptedById = index(unattempted, "unattempted");
        Map<SpellId, MechanicsDocument> canonicalById = index(canonical, "canonical");
        Map<SpellId, SpellMechanicsSourceOwner> sourcesById = new HashMap<>();
        for (SpellMechanicsSourceOwner owner : manifest) {
            ModelChecks.require(sourcesById.putIfAbsent(owner.id(), owner) == null, "Duplicate experimental source owner");
        }
        ModelChecks.require(unattemptedById.keySet().equals(canonicalById.keySet())
                        && canonicalById.keySet().equals(sourcesById.keySet()),
                "Experimental inputs do not contain the same owner set");

        Map<String, byte[]> sourceBytes = new HashMap<>();
        Map<SpellId, ExperimentalSpellMechanicsDto> results = new TreeMap<>(Comparator.comparing(SpellId::value));
        for (SpellDefinition definition : definitions.stream()
                .sorted(Comparator.comparing(item -> item.id().value())).toList()) {
            SpellId id = definition.id();
            SpellMechanicsSourceOwner sourceOwner = sourcesById.get(id);
            MechanicsDocument original = unattemptedById.get(id);
            MechanicsDocument canonicalDocument = canonicalById.get(id);
            List<SpellMechanicsPattern> applicable = patterns.matching(definition,
                    pattern -> supportsExperimentally(pattern, definition));
            List<MechanicsDocument> candidates = new ArrayList<>();
            List<String> recognizers = new ArrayList<>();
            List<String> diagnostics = new ArrayList<>();
            boolean failed = false;

            for (SpellMechanicsPattern pattern : applicable) {
                recognizers.add(pattern.patternId());
                try {
                    byte[] bytes = sourceBytes.computeIfAbsent(sourceOwner.revision().resourcePath(),
                            path -> readAndVerify(path, sourceOwner.revision()));
                    MechanicsDocument candidate = extract(pattern, definition,
                            new SpellMechanicsEvidenceSource(definition, sourceOwner, bytes, mapper), original);
                    verifyEvidence(candidate, manifest, sourceBytes);
                    candidates.add(candidate);
                } catch (RuntimeException exception) {
                    failed = true;
                    diagnostics.add(pattern.patternId() + ":" + failureCode(exception));
                }
            }

            results.put(id, project(definition, sourceOwner, canonicalDocument, candidates,
                    recognizers, diagnostics, failed));
        }
        ModelChecks.require(results.size() == definitions.size(), "Experimental dry run omitted Spell owners");
        return Map.copyOf(results);
    }

    private ExperimentalSpellMechanicsDto project(
            SpellDefinition definition,
            SpellMechanicsSourceOwner sourceOwner,
            MechanicsDocument canonical,
            List<MechanicsDocument> candidates,
            List<String> recognizers,
            List<String> diagnostics,
            boolean failed) {
        Map<String, MechanicRecord> claims = new TreeMap<>();
        Set<String> duplicateClaimIds = new HashSet<>();
        List<UnsupportedMechanicsConstruct> unsupported = new ArrayList<>();
        Set<String> unassessedPaths = new HashSet<>();
        List<MechanicsCoverage> coverages = new ArrayList<>();
        List<com.thedndpub.data.mechanics.inspection.EvidenceLocatorDto> evidence = new ArrayList<>();
        for (MechanicsDocument candidate : candidates) {
            for (MechanicRecord claim : candidate.interpretations()) {
                MechanicRecord existing = claims.putIfAbsent(claim.id().value(), claim);
                if (existing != null) {
                    duplicateClaimIds.add(claim.id().value());
                }
            }
            unsupported.addAll(candidate.unsupported());
            unassessedPaths.addAll(candidate.coverage().unassessedSourcePaths());
            coverages.add(candidate.coverage());
            evidence.addAll(inspectionMapper.project(candidate).evidence());
        }

        boolean overlap = !duplicateClaimIds.isEmpty();
        if (overlap) {
            diagnostics.add("DUPLICATE_OR_OVERLAPPING_CLAIMS:" + String.join(",", duplicateClaimIds.stream().sorted().toList()));
        }
        ExperimentalMechanicsOutcome outcome;
        if (failed && candidates.isEmpty()) {
            outcome = ExperimentalMechanicsOutcome.EXTRACTION_ERROR;
        } else if (overlap || (failed && !candidates.isEmpty())) {
            outcome = ExperimentalMechanicsOutcome.CANDIDATE_CONFLICT;
        } else if (candidates.isEmpty()) {
            outcome = ExperimentalMechanicsOutcome.NO_PATTERN_MATCH;
            String root = sourceOwner.jsonPointer();
            unassessedPaths.add(root + "/entries");
            unassessedPaths.add(root + "/entriesHigherLevel");
            addStructuredUnassessedPaths(definition, sourceOwner.jsonPointer(), unassessedPaths);
            diagnostics.add("NO_EXISTING_RECOGNIZER_MATCHED; SOURCE_MECHANICS_UNASSESSED");
        } else if (coverages.stream().allMatch(ExperimentalSpellMechanicsDryRunService::completeWithinScope)) {
            outcome = ExperimentalMechanicsOutcome.CANDIDATE_COMPLETE_WITHIN_SCOPE;
        } else {
            outcome = ExperimentalMechanicsOutcome.CANDIDATE_PARTIAL;
        }

        MechanicsCoverage coverage = mergeCoverage(coverages, unsupported.size(), unassessedPaths);
        SpellMechanicsInspectionDto projected = candidates.size() == 1
                ? inspectionMapper.project(candidates.getFirst()) : null;
        if (candidates.size() > 1) {
            diagnostics.add("MULTIPLE_RECOGNIZERS_COMPOSED; CLAIMS_RETAINED_INDEPENDENTLY");
        }
        List<MechanicInspectionDto> projectedClaims = claims.values().stream()
                .map(inspectionMapper::projectClaim)
                .toList();
        if (projected != null) {
            projectedClaims = projected.interpretations();
        }

        ExperimentalMechanicsComparisonDto comparison = compare(canonical, claims);
        return new ExperimentalSpellMechanicsDto(
                "EXPERIMENTAL", ENGINE_VERSION, DATASET_VERSION,
                sourceOwner.revision().resourcePath(), sourceOwner.revision().resourceSha256(), outcome,
                recognizers, projectedClaims, unsupported.stream().distinct().toList(),
                evidence.stream().distinct().toList(),
                unassessedPaths.stream().sorted().toList(), diagnostics, coverage, comparison, false, false);
    }

    private static boolean supportsExperimentally(SpellMechanicsPattern pattern, SpellDefinition spell) {
        if (pattern instanceof PhbSpellMechanicsExtractor phb) {
            return phb.supportsForInventory(spell);
        }
        return pattern.supports(spell);
    }

    private static MechanicsDocument extract(
            SpellMechanicsPattern pattern,
            SpellDefinition definition,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        if (pattern instanceof PhbSpellMechanicsExtractor phb) {
            return phb.extractForInventory(definition, source, original);
        }
        return pattern.extract(definition, source, original);
    }

    private void verifyEvidence(
            MechanicsDocument document,
            List<SpellMechanicsSourceOwner> manifest,
            Map<String, byte[]> sourceBytes) {
        List<MechanicsSourceContent> content = document.sources().stream().map(revision -> {
            byte[] bytes = sourceBytes.computeIfAbsent(revision.resourcePath(),
                    path -> readAndVerify(path, revision));
            return new MechanicsSourceContent(revision, bytes);
        }).toList();
        Set<SourceArtifactRevision> required = Set.copyOf(document.sources());
        List<SourceArtifactRevision> revisions = manifest.stream()
                .map(SpellMechanicsSourceOwner::revision)
                .filter(required::contains)
                .distinct()
                .sorted(Comparator.comparing(SourceArtifactRevision::resourcePath))
                .toList();
        List<MechanicsSourceOwnerPointer> pointers = manifest.stream()
                .filter(owner -> required.contains(owner.revision()))
                .map(owner -> new MechanicsSourceOwnerPointer(
                        new MechanicsOwnerReference(CatalogDomain.SPELL, owner.id().value(), owner.source()),
                        owner.revision(), owner.jsonPointer()))
                .toList();
        ModelChecks.require(new HashSet<>(revisions).equals(required), "Experimental evidence revision is not pinned");
        MechanicsSourceEvidenceVerifier.verify(document, content, revisions, pointers, mapper);
    }

    private byte[] readAndVerify(String path, SourceArtifactRevision revision) {
        try {
            byte[] bytes = resources.getResource("classpath:" + path).getContentAsByteArray();
            if (!revision.datasetVersion().equals(DATASET_VERSION)
                    || !revision.resourcePath().equals(path)
                    || !MechanicsHashes.sha256(bytes).equals(revision.resourceSha256())) {
                throw new IllegalStateException("Experimental source fingerprint is stale");
            }
            return bytes;
        } catch (IOException exception) {
            throw new IllegalStateException("Experimental source resource is unavailable", exception);
        }
    }

    private static String failureCode(RuntimeException exception) {
        String message = exception.getMessage();
        if (message != null && message.contains("fingerprint is stale")) {
            return "STALE_SOURCE_FINGERPRINT";
        }
        if (message != null && message.contains("resource is unavailable")) {
            return "SOURCE_RESOURCE_UNAVAILABLE";
        }
        return "EXTRACTION_ERROR";
    }

    private static void addStructuredUnassessedPaths(
            SpellDefinition definition, String ownerPointer, Set<String> unassessedPaths) {
        if (!definition.mechanics().spellAttacks().isEmpty()) {
            unassessedPaths.add(ownerPointer + "/spellAttack");
        }
        if (!definition.mechanics().savingThrows().isEmpty()) {
            unassessedPaths.add(ownerPointer + "/savingThrow");
        }
        if (!definition.mechanics().damageInflicted().isEmpty()) {
            unassessedPaths.add(ownerPointer + "/damageInflict");
        }
        if (!definition.mechanics().conditionsInflicted().isEmpty()) {
            unassessedPaths.add(ownerPointer + "/conditionInflict");
        }
        if (!definition.mechanics().conditionImmunities().isEmpty()) {
            unassessedPaths.add(ownerPointer + "/conditionImmune");
        }
    }

    private static MechanicsCoverage mergeCoverage(
            List<MechanicsCoverage> coverages, int unsupportedCount, Set<String> unassessedPaths) {
        if (coverages.isEmpty()) {
            return new MechanicsCoverage(CoverageStatus.NOT_ASSESSED, null, null,
                    List.of(), List.of(), unsupportedCount);
        }
        Set<String> considered = new HashSet<>();
        coverages.forEach(coverage -> considered.addAll(coverage.consideredSourcePaths()));
        if (coverages.size() == 1) {
            MechanicsCoverage only = coverages.getFirst();
            if (only.status() == CoverageStatus.COMPLETE_WITHIN_SCOPE) {
                return only;
            }
        }
        List<String> paths = unassessedPaths.stream().sorted().toList();
        if (paths.isEmpty()) {
            paths = List.of("/entries");
        }
        return new MechanicsCoverage(CoverageStatus.PARTIAL, "experimental-composed-scope", "1",
                considered.stream().sorted().toList(), paths, unsupportedCount);
    }

    private static boolean completeWithinScope(MechanicsCoverage coverage) {
        return coverage.status() == CoverageStatus.COMPLETE_WITHIN_SCOPE;
    }

    private static ExperimentalMechanicsComparisonDto compare(
            MechanicsDocument canonical, Map<String, MechanicRecord> candidates) {
        Map<String, MechanicRecord> existing = new TreeMap<>();
        canonical.interpretations().forEach(claim -> existing.put(claim.id().value(), claim));
        List<String> reproduced = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        List<String> additional = new ArrayList<>();
        List<String> changed = new ArrayList<>();
        for (Map.Entry<String, MechanicRecord> entry : existing.entrySet()) {
            MechanicRecord candidate = candidates.get(entry.getKey());
            if (candidate == null) {
                missing.add(entry.getKey());
            } else if (candidate.revision().contentSha256().equals(entry.getValue().revision().contentSha256())) {
                reproduced.add(entry.getKey());
            } else {
                changed.add(entry.getKey());
            }
        }
        candidates.keySet().stream().filter(id -> !existing.containsKey(id)).forEach(additional::add);
        return new ExperimentalMechanicsComparisonDto(reproduced, missing, additional, changed);
    }

    private static Map<SpellId, MechanicsDocument> index(List<MechanicsDocument> documents, String label) {
        Map<SpellId, MechanicsDocument> indexed = new HashMap<>();
        for (MechanicsDocument document : documents) {
            SpellId id = new SpellId(document.owner().id());
            ModelChecks.require(indexed.putIfAbsent(id, document) == null, "Duplicate " + label + " document");
        }
        return indexed;
    }
}
