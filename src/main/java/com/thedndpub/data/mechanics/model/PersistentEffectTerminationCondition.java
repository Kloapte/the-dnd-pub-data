package com.thedndpub.data.mechanics.model;

/** Source-bounded termination conditions for the PHB Witch Bolt link. */
public enum PersistentEffectTerminationCondition {
    CONCENTRATION_ENDS,
    DURATION_EXPIRES,
    TARGET_OUTSIDE_SPELL_RANGE_FROM_CASTER,
    TARGET_HAS_TOTAL_COVER_FROM_CASTER,
    CASTER_USES_SUBSEQUENT_ACTION_FOR_ANOTHER_PURPOSE
}
