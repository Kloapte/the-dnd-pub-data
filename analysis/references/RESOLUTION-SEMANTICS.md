# Resolution Semantics

## Proposed outcomes

Use a typed resolution result whose outcome and candidate cardinality are separate concepts.

| Outcome | Meaning |
|---|---|
| `RESOLVED` | The authored reference carries sufficient authoritative identity and exactly matches one catalog definition. The result may expose that definition's catalog-owned stable ID. |
| `AMBIGUOUS` | The authored identity is not authoritative enough to select one target and has multiple candidates. Retain all deterministically ordered candidate IDs internally. |
| `UNRESOLVED` | The value asserts a definition identity, but no exact target exists in the selected catalog snapshot. Preserve the original name/source/UID. |
| `DEFERRED` | The value is not currently resolvable as one definition: a choice/filter, group, external catalog, unsupported target kind, or deliberately unqualified identity. Keep its typed expression and reason. |

An unqualified name with exactly one present candidate should be represented as `DEFERRED` with cardinality `ONE_CANDIDATE`, or as a distinct `CANDIDATES_ONLY` status if implementation ergonomics favor it. It must not become `RESOLVED`: catalog additions could change uniqueness, and the source did not author the candidate's source. `AMBIGUOUS` is reserved for multiple candidates. This avoids overloading “unresolved” for a valid authored but non-authoritative reference.

## Reference semantics

The source values must be retained verbatim alongside parsed identity components. Parsing a UID does not authorize normalization of the stored original. Case-insensitive lookup can be used only where that catalog's established identity contract permits it; the resolved result must be the target catalog's original stable ID.

- Explicit source-qualified definition UID → exact adapter lookup; zero candidates is `UNRESOLVED`, one is `RESOLVED`, more than one is an invariant failure/`AMBIGUOUS` depending on catalog guarantees.
- Unqualified definition name → candidate enumeration; never inherit owner source or edition. Zero is `UNRESOLVED`, one is `DEFERRED/ONE_CANDIDATE`, many is `AMBIGUOUS`.
- Choice/filter expression → `DEFERRED`, preserving operator and constraints. Do not expand to current matches.
- Group/type → `DEFERRED` unless that category itself has an authoritative catalog identity and the caller requested that target kind.
- Markup → retain as internal link metadata; do not convert to a grant without typed owning-field semantics.
- Descriptive mention → no resolution attempt.
- Unknown source code → a source-registry validation diagnostic; it is distinct from an entity target being absent.

Never prefer PHB, XPHB, newest source, owner source, or first sorted candidate. Sorting candidates is for determinism, not precedence.

## Diagnostics

Internal diagnostics should identify owner ID, owner field/path, target category, authored token, authored source when present, outcome, and candidate IDs. Diagnostics are immutable and separate from public DTOs. Legitimate authored choices and external categories are not startup errors. Exact qualified references that become unresolved should be reported with policy-specific severity; truly malformed source syntax and duplicate target IDs should fail validation.
