# Public API Design Proposal

No public API changes are made in Stage A. Existing response contracts remain unchanged.

## Future projection

Where useful, a public lightweight reference could expose target category and original authored name, with original source only when present and a stable `targetId` only for an authoritative exact resolution. It must not embed the target DTO. Unqualified, ambiguous, missing, group, and selector references should retain the source-authored expression without internal stack traces, import diagnostics, or an unsolicited candidate list.

Candidate lists need a deliberate product contract because they can be large, change as catalogs grow, and expose catalog availability. If later needed, offer an explicit resolution endpoint/query with pagination and a documented “candidate only” meaning; do not silently add candidates to existing DTOs.

Source codes and stable IDs must retain current casing/format. Public reprint/source relationships remain domain-owned. Existing name lookup endpoints continue to return ambiguity-aware results. No API should promise that a choice/filter is an enumerated set of all valid future choices.

Before adoption, version the breaking changes, add serialization snapshots, and verify old clients ignore added optional fields. A structured reference replacing an existing string is breaking and requires a versioned representation or migration plan.
