package com.thedndpub.data.dto.object.evidence;

import com.thedndpub.data.mechanics.syntax.SyntaxObservation;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;
import com.thedndpub.data.mechanics.syntax.FormulaSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.ModifierSyntaxObservation;

import java.util.Objects;

/** Internal authored evidence, never a public Object or gameplay effect. */
public record ObjectEvidenceMembership(
        String objectId,
        int occurrenceIndex,
        ObjectMembershipKind kind,
        ObjectEvidenceAnchor parent,
        ObjectEvidenceAnchor child,
        String authoredValue,
        SyntaxObservation observation) {
    public ObjectEvidenceMembership {
        Objects.requireNonNull(objectId, "objectId");
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(parent, "parent");
        Objects.requireNonNull(child, "child");
        if (objectId.isBlank() || occurrenceIndex < 0) {
            throw new IllegalArgumentException("Object identity and nonnegative sequence are required");
        }
        if (!parent.location().owner().equals(child.location().owner())
                || parent.location().origin() != SyntaxOrigin.AUTHORED
                || child.location().origin() != SyntaxOrigin.AUTHORED) {
            throw new IllegalArgumentException("Membership requires the same original authored owner");
        }
        String parentPointer = parent.location().jsonPointer();
        String childPointer = child.location().jsonPointer();
        if (!childPointer.startsWith(parentPointer + "/")
                && !(childPointer.equals(parentPointer) && child.span() != null)) {
            throw new IllegalArgumentException("Child evidence must belong to the parent node");
        }
        if (parent.span() != null) {
            throw new IllegalArgumentException("Membership parent must be an original node");
        }
        boolean syntax = kind == ObjectMembershipKind.ENTRY_CONTAINS_SYNTAX
                || kind == ObjectMembershipKind.SECTION_CONTAINS_FORMULA_TAG
                || kind == ObjectMembershipKind.SECTION_CONTAINS_MODIFIER_TAG;
        if (syntax != (observation != null) || syntax != (child.span() != null)) {
            throw new IllegalArgumentException("Only syntax membership carries an observation and span");
        }
        if (observation != null && (!observation.tag().evidence().location().equals(child.location())
                || !observation.tag().evidence().span().equals(child.span()))) {
            throw new IllegalArgumentException("Observation must identify exactly the child evidence");
        }
        if (kind == ObjectMembershipKind.SECTION_CONTAINS_FORMULA_TAG
                && !(observation instanceof FormulaSyntaxObservation)) {
            throw new IllegalArgumentException("Formula membership requires a formula syntax observation");
        }
        if (kind == ObjectMembershipKind.SECTION_CONTAINS_MODIFIER_TAG
                && !(observation instanceof ModifierSyntaxObservation)) {
            throw new IllegalArgumentException("Modifier membership requires a modifier syntax observation");
        }
        if ((kind == ObjectMembershipKind.HAS_AUTHORED_ATTACK_TYPE) != (authoredValue != null)) {
            throw new IllegalArgumentException("Only explicit attack metadata carries an authored scalar");
        }
    }
}
