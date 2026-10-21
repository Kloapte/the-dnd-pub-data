package com.thedndpub.data.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.inspection.SpellMechanicsInspectionDto;
import com.thedndpub.data.mechanics.inspection.SpellMechanicsInspectionMapper;
import com.thedndpub.data.mechanics.inspection.ExperimentalSpellMechanicsDto;
import com.thedndpub.data.mechanics.model.FormulaObservationReference;
import com.thedndpub.data.mechanics.model.MechanicsCatalogValidation;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.spell.SpellMechanicsDocumentFactory;
import com.thedndpub.data.mechanics.spell.SpellMechanicsExtractionService;
import com.thedndpub.data.mechanics.spell.ExperimentalSpellMechanicsDryRunService;
import com.thedndpub.data.mechanics.experimental.ExperimentalMechanicsV2Dto;
import com.thedndpub.data.mechanics.experimental.GenericMechanicsRecognitionPipeline;
import com.thedndpub.data.mechanics.spell.SpellMechanicsSourceOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Complete immutable owner join. Integrity failures stop startup rather than imitating empty inspection. */
@Component
public final class SpellMechanicsInspectionCatalog {
    private final Map<SpellId, SpellMechanicsInspectionDto> byId;
    private final List<MechanicsDocument> documents;

    @Autowired
    public SpellMechanicsInspectionCatalog(
            ObjectMapper mapper,
            SpellCatalog spells,
            SpellFormulaAnnotationCatalog formulas,
            SourceRegistry sources) {
        this(new SpellMechanicsDocumentFactory(mapper), new SpellMechanicsExtractionService(mapper),
                spells.findAll(), formulas.index(), sources, mapper);
    }

    private SpellMechanicsInspectionCatalog(
            SpellMechanicsDocumentFactory factory,
            SpellMechanicsExtractionService extraction,
            List<SpellDefinition> definitions,
            SpellFormulaAnnotationIndex formulaIndex,
            SourceRegistry sources,
            ObjectMapper mapper) {
        this(factory, extraction, definitions, formulaIndex, sources, factory.loadOwners(), mapper);
    }

    private SpellMechanicsInspectionCatalog(
            SpellMechanicsDocumentFactory factory,
            SpellMechanicsExtractionService extraction,
            List<SpellDefinition> definitions,
            SpellFormulaAnnotationIndex formulaIndex,
            SourceRegistry sources,
            List<SpellMechanicsSourceOwner> manifest,
            ObjectMapper mapper) {
        this(buildCatalog(factory, extraction, definitions, formulaIndex, manifest, mapper),
                definitions, formulaIndex, sources, manifest);
    }

    private SpellMechanicsInspectionCatalog(
            CatalogBuild build,
            List<SpellDefinition> definitions,
            SpellFormulaAnnotationIndex formulaIndex,
            SourceRegistry sources,
            List<SpellMechanicsSourceOwner> manifest) {
        this(build.canonicalDocuments(), definitions, formulaIndex, sources, manifest,
                build.experimental(), build.experimentalV2());
    }

    /** Adapter boundary for future canonical documents; no extraction or publication policy lives here. */
    public SpellMechanicsInspectionCatalog(
            List<MechanicsDocument> documents,
            List<SpellDefinition> definitions,
            SpellFormulaAnnotationIndex formulaIndex,
            SourceRegistry sources,
            List<SpellMechanicsSourceOwner> manifest) {
        this(documents, definitions, formulaIndex, sources, manifest, Map.of(), Map.of());
    }

    private SpellMechanicsInspectionCatalog(
            List<MechanicsDocument> documents,
            List<SpellDefinition> definitions,
            SpellFormulaAnnotationIndex formulaIndex,
            SourceRegistry sources,
            List<SpellMechanicsSourceOwner> manifest,
            Map<SpellId, ExperimentalSpellMechanicsDto> experimental,
            Map<SpellId, ExperimentalMechanicsV2Dto> experimentalV2) {
        Map<SpellId, SpellDefinition> owners = new TreeMap<>(Comparator.comparing(SpellId::value));
        for (SpellDefinition definition : definitions) {
            ModelChecks.require(owners.putIfAbsent(definition.id(), definition) == null, "Duplicate Spell definition owner");
        }
        ModelChecks.require(owners.keySet().equals(formulaIndex.bySpellId().keySet()), "Formula owner set mismatch");
        Map<SpellId, SpellMechanicsSourceOwner> sourceOwners = new TreeMap<>(Comparator.comparing(SpellId::value));
        Map<String, SourceArtifactRevision> artifacts = new TreeMap<>();
        for (SpellMechanicsSourceOwner sourceOwner : manifest) {
            ModelChecks.require(sourceOwners.putIfAbsent(sourceOwner.id(), sourceOwner) == null,
                    "Duplicate source manifest owner");
            SourceArtifactRevision prior = artifacts.putIfAbsent(
                    sourceOwner.revision().resourcePath(), sourceOwner.revision());
            ModelChecks.require(prior == null || prior.equals(sourceOwner.revision()), "Conflicting source fingerprints");
        }
        ModelChecks.require(sourceOwners.keySet().equals(owners.keySet()), "Source manifest owner set mismatch");
        Map<SpellId, SpellMechanicsInspectionDto> projections = new TreeMap<>(Comparator.comparing(SpellId::value));
        var projectionMapper = new SpellMechanicsInspectionMapper();
        for (MechanicsDocument document : documents) {
            SpellId id = new SpellId(document.owner().id());
            ModelChecks.require(owners.containsKey(id), "Unknown inspection owner");
            MechanicsCatalogValidation.validateSpell(document, definitions, formulaIndex, sources);
            SpellMechanicsSourceOwner sourceOwner = sourceOwners.get(id);
            ModelChecks.require(document.owner().source().equals(sourceOwner.source())
                    && document.sources().contains(sourceOwner.revision()), "Inspection source owner mismatch");
            for (SourceArtifactRevision artifact : document.sources()) {
                ModelChecks.require(artifact.equals(artifacts.get(artifact.resourcePath())), "Unverified source revision");
            }
            List<Integer> occurrenceIndices = document.observations().stream()
                    .filter(FormulaObservationReference.class::isInstance)
                    .map(FormulaObservationReference.class::cast)
                    .map(FormulaObservationReference::occurrenceIndex).toList();
            List<Integer> expected = formulaIndex.bySpellId().get(id).stream()
                    .map(annotation -> annotation.occurrenceIndex()).toList();
            ModelChecks.require(expected.equals(occurrenceIndices), "Incomplete or reordered formula references");
            SpellMechanicsInspectionDto projection = projectionMapper.project(document);
            ExperimentalSpellMechanicsDto experimentalDto = experimental.get(id);
            if (experimentalDto != null) {
                projection = withExperimental(projection, experimentalDto, experimentalV2.get(id));
            } else if (experimentalV2.containsKey(id)) {
                projection = withExperimental(projection, null, experimentalV2.get(id));
            }
            ModelChecks.require(projections.putIfAbsent(id, projection) == null,
                    "Duplicate inspection owner");
        }
        ModelChecks.require(projections.keySet().equals(owners.keySet()), "Missing inspection owners");
        byId = Collections.unmodifiableMap(projections);
        var ordered = new ArrayList<>(documents);
        ordered.sort(Comparator.comparing(document -> document.owner().id()));
        this.documents = List.copyOf(ordered);
    }

    private static CatalogBuild buildCatalog(
            SpellMechanicsDocumentFactory factory,
            SpellMechanicsExtractionService extraction,
            List<SpellDefinition> definitions,
            SpellFormulaAnnotationIndex formulaIndex,
            List<SpellMechanicsSourceOwner> manifest,
            ObjectMapper mapper) {
        List<MechanicsDocument> unattempted = factory.unattempted(definitions, formulaIndex, manifest);
        List<MechanicsDocument> canonical = extraction.extract(unattempted, definitions, manifest);
        Map<SpellId, ExperimentalSpellMechanicsDto> experimental =
                new ExperimentalSpellMechanicsDryRunService(mapper)
                        .evaluate(definitions, unattempted, canonical, manifest);
        Map<SpellId, ExperimentalMechanicsV2Dto> experimentalV2 =
                new GenericMechanicsRecognitionPipeline(mapper).evaluate(definitions, canonical, manifest);
        return new CatalogBuild(canonical, experimental, experimentalV2);
    }

    private static SpellMechanicsInspectionDto withExperimental(
            SpellMechanicsInspectionDto canonical, ExperimentalSpellMechanicsDto experimental,
            ExperimentalMechanicsV2Dto experimentalV2) {
        return new SpellMechanicsInspectionDto(canonical.schemaVersion(), canonical.projection(),
                canonical.availability(), canonical.processing(), canonical.sourceRevisions(),
                canonical.interpretations(), canonical.observations(), canonical.unsupported(), canonical.evidence(),
                canonical.coverage(), canonical.reviewSummary(), canonical.diagnostics(),
                canonical.readyForHumanReviewWithinScope(), experimental, canonical.origin(), experimentalV2);
    }

    private record CatalogBuild(
            List<MechanicsDocument> canonicalDocuments,
            Map<SpellId, ExperimentalSpellMechanicsDto> experimental,
            Map<SpellId, ExperimentalMechanicsV2Dto> experimentalV2) {
    }

    public SpellMechanicsInspectionDto findById(SpellId id) {
        SpellMechanicsInspectionDto inspection = byId.get(id);
        if (inspection == null) {
            throw new IllegalStateException("Spell inspection owner is not indexed");
        }
        return inspection;
    }

    public List<MechanicsDocument> documents() {
        return documents;
    }

    public int size() {
        return byId.size();
    }
}
