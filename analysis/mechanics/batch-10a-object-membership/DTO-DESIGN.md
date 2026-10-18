# Internal design checkpoint

Approved scope: source membership only, using existing Object identities and Batch 5 syntax.
No changes to ObjectDefinition, public DTOs, controllers, source or reference processing.

- ObjectEvidenceAnchor: existing SyntaxTextLocation plus optional SyntaxSpan. The location identifies an
  original node (including arrays/metadata); null span means node identity only, never invented byte offsets.
- ObjectEvidenceMembership: Object ID, per-owner sequence number, relationship kind, parent/child anchors,
  optional exact metadata scalar and optional existing SyntaxObservation. Tags retain exact shared evidence.
- ObjectMembershipKind: the five eligible gold relation kinds, SECTION_CONTAINS_ENTRY and ENTRY_CONTAINS_SYNTAX.
- ObjectEvidenceMembershipIndex: immutable source-order list and immutable per-existing-Object lookup, including
  empty lists for all other Objects. No new definition identity or semantic eligibility method.
- ObjectEvidenceMembershipAdapter: strict source DTE validation plus original JSON traversal with Batch 5 walker,
  parser and classifier; reconcile against existing normalized definitions. Only explicit ObjectAttackDte
  containers at their original action positions qualify. No text/list promotion or transformed origin.
- ObjectEvidenceMembershipCatalog: one startup-loaded internal bean, no public integration.

For the pinned corpus, expect 35 gold-positive edges plus 14 section/member and 14 member/tag edges = 63.
The three syntax-positive control labels are outside adapter scope, not false negatives. Nine rejected claims
and 33 abstentions must yield no matching relationship. No runtime gold data or semantic label registry is needed.
The gold set remains unchanged and is used by tests only. Additional tag families remain existing opaque syntax,
not inferred effects. Synthetic multiple-member/nested-tag fixtures test structure, not invented corpus coverage.
