package com.thedndpub.data.dto.classes;

/** Completeness of typed feature choices in the public projection, independent of eligibility. */
public enum ClassChoiceProjectionStatus {
    /** No typed option nodes occur in the effective feature content. */
    NONE,
    /** All typed choice structure and relevant metadata survive the existing public projection. */
    COMPLETE,
    /** Alternatives are exposed, but some choice structure or metadata is omitted. */
    PARTIAL,
    /** Typed choices exist, but none of their alternatives is exposed. */
    WITHHELD
}
