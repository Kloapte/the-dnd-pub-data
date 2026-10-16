# Implemented class copy resolution contract

Status: Stage B production behavior. Stage A recorded the complete declarations and an unresolved metadata default, but did not include a separate normative copy specification. This document closes that decision with the source evidence and tested interpretation.

## Supported operations

Only kind-specific `_copy` plus the observed `_preserve` flags are supported. There are 124 subclass copies and 75 subclass feature copies. No `_mod`, path mutation, versions, templates, abstract definitions, implementations, or variable substitution occurs. Such fields are rejected during strict deserialization; no other entity's expander is invoked.

## Resolution order

1. Index all authored roots by complete case-insensitive source identity, retaining original name/source spelling and file location. Reject duplicate identities.
2. Validate the copy's complete base identity, including parent source, subclass short name/source and feature level where applicable.
3. Resolve the base recursively before its destination. Missing bases and an active recursion dependency fail explicitly. The current corpus has no chains, but the resolver does not depend on file or input order.
4. Verify a subclass copy's `shortName` against the referenced base. A parent class name change is rejected; explicit changes of parent source and feature level remain supported.
5. For every root component, use the authored destination value when its JSON property was present. This includes explicit null, false, zero and empty lists. Root objects and lists override atomically, without concatenating or recursively merging their contents.
6. For an absent destination component, apply the protected metadata policy below. Other absent components inherit the resolved base value.
7. Construct a new typed record through its canonical constructor, which makes defensive collection copies. Never modify the authored base or destination.
8. Recheck that the result has the destination's original full identity. Retain the authored destination, effective typed root, base locations, inherited fields and suppressed metadata in internal provenance.

Borrowed feature UIDs remain unchanged. Inheriting a PHB feature list onto an XPHB parent does not rewrite its source slots or levels. Explicit list overrides encode the source's adaptations; they take precedence over any inferred edition convention.

## Protected metadata default

The following absent destination properties do **not** inherit automatically:

`page`, `otherSources`, `referenceSources`, `reprintedAs`, `srd`, `srd52`, `basicRules`, `basicRules2024`, `edition`, `isReprinted`, `hasFluff`, `hasFluffImages`.

Observed true `_preserve` flags opt these properties into inheritance: `page`, `otherSources`, `referenceSources`, `srd`, `basicRules`, and `reprintedAs`. An explicit false preservation flag does not opt in. Explicit outer values always win. Unknown preservation names/paths are rejected.

This default follows the evidence that feature copies selectively request SRD/basic-rules retention rather than receiving it automatically. It also avoids transferring unrequested publication/edition/media assertions to a different parent context. Stage A could not prove upstream behavior for unlisted metadata; this is an explicit application import policy, not a claim about an unavailable upstream consumer. Base facts remain recoverable from typed authored records and copy lineage. The source's descriptive `fluff` content remains inheritable ordinary content; availability flags remain protected.

Subclass copy preservation objects all request the six listed flags. All 75 feature copies request page preservation; **six also request `srd`, and three of those request `basicRules`**. Stage A's prose statement that feature preservation contained only page was incomplete; the field inventory already contained these rare fields. Stage B covers and tests all of them.

`referenceSources` is a preservation instruction only in this corpus, not a populated root property. It is retained in the typed copy declaration without inventing a root collection. The declared edition of a copied subclass can remain absent; its parent source/edition and its base's authored edition are separately available.

## Rejections and preservation

Missing/ambiguous own identities, contradictory copy short names, duplicate readable IDs, copy cycles, unsupported instructions, invalid preservation paths and unresolved template tokens fail explicitly. Prepared-spell expressions are the sole observed `<$...$>` template form and are retained as validated symbolic expressions, never evaluated.

The resolver returns 2,536 effective roots without extra generated definitions. Its immutable provenance identifies all 199 copies. Dedicated tests verify corpus counts, publication flags, explicit null/false/empty overrides, source immutability, exact target checks, cycles, unsupported operations and input-order independence.
