package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dto.spell.SpellCastingTime;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellDuration;
import com.thedndpub.data.dto.spell.SpellRange;
import com.thedndpub.data.mechanics.model.CatalogDomain;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsOwnerReference;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/** Source perturbations exist in memory only. No synthetic mechanics or changed source resources are packaged. */
record ShieldExtractionTestInput(SpellDefinition spell, SpellMechanicsSourceOwner owner, byte[] bytes) {
    ShieldExtractionTestInput {
        bytes = bytes.clone();
    }

    @Override
    public byte[] bytes() {
        return bytes.clone();
    }

    static ShieldExtractionTestInput actual(SpellDefinition spell, SpellMechanicsSourceOwner owner) throws IOException {
        return new ShieldExtractionTestInput(spell, owner, new ClassPathResource(owner.revision().resourcePath()).getContentAsByteArray());
    }

    ShieldExtractionTestInput changed(ObjectMapper mapper, Consumer<ObjectNode> change) throws IOException {
        ObjectNode tree = (ObjectNode) mapper.readTree(bytes);
        ObjectNode root = (ObjectNode) tree.at(owner.jsonPointer());
        change.accept(root);
        byte[] changedBytes = mapper.writeValueAsBytes(tree);
        var revision = new SourceArtifactRevision(owner.revision().datasetVersion(), owner.revision().resourcePath(),
                MechanicsHashes.sha256(changedBytes));
        var changedOwner = new SpellMechanicsSourceOwner(owner.id(), owner.source(), revision, owner.jsonPointer());
        List<SpellCastingTime> times = root.has("time")
                ? mapper.convertValue(root.get("time"), new TypeReference<>() { }) : List.of();
        List<SpellDuration> durations = root.has("duration")
                ? mapper.convertValue(root.get("duration"), new TypeReference<>() { }) : List.of();
        List<EntryDte> entries = root.has("entries")
                ? mapper.convertValue(root.get("entries"), new TypeReference<>() { }) : List.of();
        SpellRange range = root.has("range") ? mapper.treeToValue(root.get("range"), SpellRange.class) : null;
        var definition = new SpellDefinition(spell.id(), spell.identity(), spell.source(), spell.level(), spell.school(),
                spell.aliases(), times, range, spell.components(), durations, spell.ritual(), spell.mechanics(), spell.scaling(),
                entries, spell.entriesHigherLevel(), spell.sourceRelationships(), spell.importMetadata());
        return new ShieldExtractionTestInput(definition, changedOwner, changedBytes);
    }

    MechanicsDocument extract(ObjectMapper mapper) {
        return new ShieldMechanicsExtractor().extract(spell, new SpellMechanicsEvidenceSource(spell, owner, bytes, mapper), seed());
    }

    MechanicsDocument seed() {
        return MechanicsDocument.create(new MechanicsOwnerReference(CatalogDomain.SPELL, spell.id().value(), spell.source()),
                1, List.of(owner.revision()), new MechanicsProcessing(ProcessingState.NOT_ATTEMPTED, null, null),
                List.of(), List.of(), List.of(), List.of(),
                new MechanicsCoverage(CoverageStatus.NOT_ASSESSED, null, null, List.of(), List.of(), 0));
    }
}
