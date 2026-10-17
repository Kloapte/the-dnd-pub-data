package com.thedndpub.data.dto.monster.syntax;

/** Identifies an authored Monster source container without interpreting its rules. */
public enum MonsterSyntaxContextKind {
    TRAIT,
    ACTION,
    BONUS,
    REACTION,
    LEGENDARY,
    MYTHIC,
    VARIANT,
    SPELLCASTING,
    OTHER
}
