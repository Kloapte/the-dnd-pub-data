package com.thedndpub.data.dto.object.evidence;

/** Source membership only; none of these relations describes an applied effect. */
public enum ObjectMembershipKind {
    HAS_ATTACK_SECTION,
    HAS_HIT_SECTION,
    HAS_AUTHORED_ATTACK_TYPE,
    SECTION_CONTAINS_MODIFIER_TAG,
    SECTION_CONTAINS_FORMULA_TAG,
    SECTION_CONTAINS_ENTRY,
    ENTRY_CONTAINS_SYNTAX
}
