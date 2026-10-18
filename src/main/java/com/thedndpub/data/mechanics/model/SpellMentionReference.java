package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.spell.SpellId;

import java.util.Locale;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellMentionReference(
        String authoredName,
        SourceIdentity authoredSource,
        SpellMentionForm form,
        MechanicsReferenceDisposition resolution,
        SpellId targetId,
        String resolutionEvidenceSha256) {
    public SpellMentionReference {
        ModelChecks.text(authoredName, "authoredName");
        ModelChecks.required(form, "form");
        ModelChecks.required(resolution, "resolution");
        boolean resolved = resolution == MechanicsReferenceDisposition.RESOLVED;
        ModelChecks.require(resolved == (targetId != null) && resolved == (resolutionEvidenceSha256 != null),
                "Only authoritative resolved references carry target IDs and resolution evidence");
        if (resolved) {
            ModelChecks.require(form == SpellMentionForm.TAG_REFERENCE, "Plain names cannot acquire inferred target IDs");
            ModelChecks.digest(resolutionEvidenceSha256);
            if (authoredSource != null) {
                ModelChecks.require(targetId.value().startsWith("spell:" + authoredSource.code().toLowerCase(Locale.ROOT) + ":"),
                        "Resolved target conflicts with authored source qualification");
            }
        }
    }
}
