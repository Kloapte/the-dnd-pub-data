package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.AttackEventKind;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.Locale;

/** Pinned input boundary, not a general edition inference rule or human mechanics approval. */
enum ShieldMechanicsEdition {
    PHB("spell:phb:shield", "PHB", AttackEventKind.ATTACK_HIT,
            "c9e54cce55e7ce0fc988feb1b604bf505faa5c042635542fa6b6ed36724e297a"),
    XPHB("spell:xphb:shield", "XPHB", AttackEventKind.ATTACK_ROLL_HIT,
            "442544a91aad1181cbad78d49cfa48f9ced7d8b048dbc781e32fa6fefc6278b9");

    private final String ownerId;
    private final String sourceCode;
    private final AttackEventKind attackEvent;
    private final String resourceSha256;

    ShieldMechanicsEdition(String ownerId, String sourceCode, AttackEventKind attackEvent, String resourceSha256) {
        this.ownerId = ownerId;
        this.sourceCode = sourceCode;
        this.attackEvent = attackEvent;
        this.resourceSha256 = resourceSha256;
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

    void validatePinned(SpellMechanicsSourceOwner owner) {
        ModelChecks.require(owner.id().value().equals(ownerId)
                        && owner.source().identity().code().equals(sourceCode), "Shield owner identity mismatch");
        validateSourceRevision(owner);
    }

    void validateSourceRevision(SpellMechanicsSourceOwner owner) {
        ModelChecks.require(owner.source().identity().code().equals(sourceCode)
                        && owner.revision().datasetVersion().equals("5eTools v2.37.0")
                        && owner.revision().resourcePath().equals("data/spells/spells-" + name().toLowerCase(Locale.ROOT) + ".json")
                        && owner.revision().resourceSha256().equals(resourceSha256),
                "Shield input differs from the audited pinned source revision");
    }
}
