# Copy and version mechanisms

Status: Stage A declaration/dependency findings, with the Stage B copy behavior now implemented and tested. See [COPY-RESOLUTION-SPEC.md](COPY-RESOLUTION-SPEC.md) for the normative application metadata policy and ordering. No version expansion was needed.

## Complete declaration inventory

| Mechanism | Class | Subclass | Class feature | Subclass feature |
| --- | ---: | ---: | ---: | ---: |
| `_copy` | 0 | 124 | 0 | 75 |
| `_versions` | 0 | 0 | 0 | 0 |
| `_mod` | 0 | 0 | 0 | 0 |
| `_abstract` | 0 | 0 | 0 | 0 |
| `_implementations` | 0 | 0 | 0 | 0 |
| `_variables` | 0 | 0 | 0 | 0 |

Every nested object was inspected, not just roots. The only transformation declarations are the 199 `_copy` objects and their `_preserve` metadata. [inheritance-evidence.json](inheritance-evidence.json) records all declarations, complete payloads, owners, and JSON pointers. Nested `preserve` on optional feature references is separate reference rendering metadata, not `_preserve` or a copy operation.

## Target resolution and expected candidates

All 199 copy targets resolve uniquely within the corpus. There are no copy chains whose target is another copy, no missing/ambiguous bases, and no cycles. No cross-file copy edge occurs here, although the planned resolver should use complete root indexes.

124 subclass copies consist of 120 PHB-parent → XPHB-parent adaptations and four TCE Artificer-parent → EFA Artificer-parent adaptations. They retain the original subclass source code. Seventy-five subclass features copy PHB-parent definitions into XPHB-parent contexts, with explicit target and destination identity components.

The expected post-copy definition count remains **2,536**: 30 classes, 330 subclasses, 677 class features, 1,499 subclass features. A copy fills an authored root, not an additional generated variant. Version declarations, template implementations, and extra candidate expansions are all zero. The copy roots and bases have no full-identity or proposed-ID collisions.

## Observed semantics and planned resolver contract

1. Resolve `_copy` using the base's complete kind-specific identity, including parent source and feature level. Subclass copy payloads additionally include `shortName`; validate it against the target rather than ignore a mismatch.
2. Treat the outer root as the destination identity. For example, a PHB subclass attached to XPHB names a base with `classSource:PHB` but keeps `classSource:XPHB` on the authored destination.
3. Inherit absent data from the target, subject to source-defined metadata preservation rules. Explicit destination fields override the base; do not recursively union lists. Of 124 subclass copies, 57 provide `subclassFeatures` overrides and 67 omit that list. The overrides encode revised feature schedules; absent lists are expected to inherit.
4. Preserve explicit feature UID parent/source slots. Borrowed later-level PHB feature references remain PHB references; a parent class change is not a global UID rewrite.
5. For copied feature roots, inherit the base content while retaining the destination level and parent identity. All 75 omit `entries` and `page`; `_preserve.page:true` explicitly requests retaining the base page.
6. Record base identity, destination identity, copy locator, explicit overrides, and preservation instructions in internal provenance, separately from public DTOs.

Subclass `_preserve` flags explicitly list `page`, `otherSources`, `referenceSources`, `srd`, `basicRules`, and `reprintedAs`; all observed values are true. `referenceSources` appears only as a preservation instruction, not a populated root field. All 75 subclass-feature copies request `page:true`; six additionally request `srd:true`, and three of those request `basicRules:true`. The original Stage A prose missed these rare flags although they were present in its complete field inventory. Stage B preserves and tests them. No text replacement, array mutation, arithmetic modification, wildcard path operation, or variable substitution occurs.

The repository does not include the upstream class-copy consumer. The flags establish requested preservation, but absence/default behavior for unlisted metadata cannot be proven solely from these declarative JSON records. **Stage B now explicitly protects unrequested publication/source/edition/media metadata**, while ordinary absent content inherits and explicit outer values always win. This is a documented application import policy, not a claim about upstream defaults; see [COPY-RESOLUTION-SPEC.md](COPY-RESOLUTION-SPEC.md). Authored base material and lineage remain typed internal provenance. The destination's parent edition and the base's declared edition remain separate facts.

## Unsupported and future operations

No observed modification/version operation is unsupported because none is declared. Stage B resolves all 199 copies with the documented metadata policy. A future unfamiliar `_mod`, `_versions`, template, or overwrite structure fails during strict source mapping instead of being silently ignored or routed through another entity's expander.

`isClassFeatureVariant` is classification without an explicit replacement target. Reprints do not implement inheritance. No additional automatic “subclass inheritance” should be inferred from equal names, levels, source edition, or descriptive text.
