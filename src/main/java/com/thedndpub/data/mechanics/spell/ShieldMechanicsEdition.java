package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.AttackEventKind;
import com.thedndpub.data.mechanics.model.ModelChecks;

/** Pinned input boundary, not a general edition inference rule or human mechanics approval. */
enum ShieldMechanicsEdition {
    PHB("spell:phb:shield", SpellMechanicsSourceEdition.PHB, AttackEventKind.ATTACK_HIT),
    XPHB("spell:xphb:shield", SpellMechanicsSourceEdition.XPHB, AttackEventKind.ATTACK_ROLL_HIT);

    private final String ownerId;
    private final SpellMechanicsSourceEdition sourceEdition;
    private final AttackEventKind attackEvent;

    ShieldMechanicsEdition(String ownerId, SpellMechanicsSourceEdition sourceEdition, AttackEventKind attackEvent) {
        this.ownerId = ownerId;
        this.sourceEdition = sourceEdition;
        this.attackEvent = attackEvent;
    }

    static ShieldMechanicsEdition forId(SpellId id) {
        for (ShieldMechanicsEdition edition : values()) {
            if (edition.ownerId.equals(id.value())) {
                return edition;
            }
        }
        return null;
    }

    AttackEventKind attackEvent() {
        return attackEvent;
    }

    String ownerId() {
        return ownerId;
    }

    void validatePinned(SpellMechanicsSourceOwner owner) {
        ModelChecks.require(owner.id().value().equals(ownerId), "Shield owner identity mismatch");
        validateSourceRevision(owner);
    }

    void validateSourceRevision(SpellMechanicsSourceOwner owner) {
        sourceEdition.validateSourceRevision(owner);
    }
}
