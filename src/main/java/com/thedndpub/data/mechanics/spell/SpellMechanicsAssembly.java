package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.DerivationMethod;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicDerivation;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicId;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsAssessment;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.MechanicsSupport;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct;
import com.thedndpub.data.mechanics.model.UnsupportedReason;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Local construction state. Only immutable, fully joined canonical values leave this assembler. */
final class SpellMechanicsAssembly {
    private final SpellMechanicsEvidenceSource source;
    private final MechanicsDocument original;
    private final String adapter;
    private final List<MechanicRecord> records = new ArrayList<>();
    private final List<UnsupportedMechanicsConstruct> unsupported = new ArrayList<>();

    SpellMechanicsAssembly(SpellMechanicsEvidenceSource source, MechanicsDocument original) {
        this(source, original, "shield-pilot");
    }

    SpellMechanicsAssembly(SpellMechanicsEvidenceSource source, MechanicsDocument original, String adapter) {
        this.source = source;
        this.original = original;
        this.adapter = adapter;
    }

    MechanicRecord attempt(String pattern, MechanicFamily family, Supplier<MechanicRecord> extraction) {
        try {
            MechanicRecord result = extraction.get();
            records.add(result);
            return result;
        } catch (SpellPatternMismatch mismatch) {
            var anchor = source.availableAncestor(mismatch.relativePointer());
            String prefix = adapter.equals("shield-pilot") ? "shield" : adapter;
            unsupported.add(new UnsupportedMechanicsConstruct(prefix + "-unsupported:" + pattern, family,
                    mismatch.reason(), List.of(anchor.id()), List.of()));
            return null;
        }
    }

    List<MechanicRecord> attemptMany(
            String pattern,
            MechanicFamily family,
            Supplier<List<MechanicRecord>> extraction) {
        try {
            List<MechanicRecord> result = List.copyOf(extraction.get());
            records.addAll(result);
            return result;
        } catch (SpellPatternMismatch mismatch) {
            var anchor = source.availableAncestor(mismatch.relativePointer());
            String prefix = adapter.equals("shield-pilot") ? "shield" : adapter;
            unsupported.add(new UnsupportedMechanicsConstruct(prefix + "-unsupported:" + pattern, family,
                    mismatch.reason(), List.of(anchor.id()), List.of()));
            return List.of();
        }
    }

    MechanicRecord claim(String slot, String pattern, MechanicPayload payload, MechanicContext context,
                         List<MechanicEvidenceAssociation> associations, boolean directStructured) {
        return claim(slot, pattern, payload, context, associations, directStructured,
                MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
    }

    MechanicRecord claim(String slot, String pattern, MechanicPayload payload, MechanicContext context,
                         List<MechanicEvidenceAssociation> associations, boolean directStructured,
                         MechanicsCompleteness completeness) {
        return MechanicRecord.propose(MechanicId.of(source.owner(), slot), payload, context, associations,
                new MechanicDerivation(directStructured ? DerivationMethod.DIRECT_STRUCTURED_MAPPING
                        : DerivationMethod.BOUNDED_CONTEXT_INTERPRETATION, pattern, "1", adapter + "-1",
                        List.of(), List.of(source.source().revision())),
                new MechanicsAssessment(completeness,
                        directStructured ? MechanicsSupport.DIRECT_STRUCTURED : MechanicsSupport.BOUNDED_CONTEXT,
                        List.of(), List.of()));
    }

    MechanicsDocument finish() {
        String root = source.source().jsonPointer();
        return finish(List.of(root + "/time", root + "/range", root + "/duration", root + "/entries"),
                List.of(root + "/entries"));
    }

    void unsupported(String id, MechanicFamily family, List<String> evidenceIds) {
        unsupported.add(new UnsupportedMechanicsConstruct(id, family,
                UnsupportedReason.FAMILY_NOT_IMPLEMENTED, evidenceIds, List.of()));
    }

    void abstain(String id, MechanicFamily family, UnsupportedReason reason, String pointer) {
        unsupported.add(new UnsupportedMechanicsConstruct(id, family, reason,
                List.of(source.availableAncestor(pointer).id()), List.of()));
    }

    MechanicsDocument finish(List<String> considered, List<String> unassessed) {
        return MechanicsDocument.create(source.owner(), 1, List.of(source.source().revision()),
                new MechanicsProcessing(ProcessingState.PROCESSED, adapter, "1"),
                records, original.observations(), unsupported, source.evidence(),
                new MechanicsCoverage(records.isEmpty() ? CoverageStatus.NONE_SUPPORTED : CoverageStatus.PARTIAL,
                        adapter, "1", considered, unassessed, unsupported.size()));
    }
}
