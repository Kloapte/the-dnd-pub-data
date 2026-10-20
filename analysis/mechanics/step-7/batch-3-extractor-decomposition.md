# Batch 3 PHB extractor decomposition

## Changes

`PhbSpellMechanicsExtractor` remains the production pattern, owner eligibility gate and extraction
orchestrator. Two package-private collaborators now hold cohesive PHB rule families:

- `PhbConditionMechanicsRules` owns explicit condition removals, exclusive choices, condition
  applications and immunities, saving throw dependencies, target/context bindings, condition endings,
  and the nested-entry text traversal used by those rules. Its condition-tag check also remains the
  single implementation used by the offline candidate eligibility hook.
- `PhbModifierAndProtectionRules` owns AC and statistic modifiers, modifier saving throw dependencies,
  damage resistance and protection claims, including their applicability and evidence associations.

Both collaborators use the existing `SpellMechanicsEvidenceSource` and `SpellMechanicsAssembly` passed
by the orchestrator. They share the extractor's existing package-level `require`, `association`,
`unknownContext` and `ability` helpers. No interfaces, factories, registries or parsing copies were
introduced. `PhbSaveDamageGeneralizer` remains unchanged and separate; the legacy damage and healing
rules remain in the extractor because they are interleaved with its existing dispatch and scaling flow.

The package-private `supportsForInventory` and `extractForInventory` candidate hooks remain on the
extractor. Splitting candidate assessment from production eligibility would require a broader rule
boundary, so that cleanup is deferred. Production owner allowlists and public `supports` behavior are
unchanged.

## Compatibility checks

A temporary Spring test captured deterministic serialized `MechanicsDocument` outputs before and
after the refactor to `target/mechanics-step3/`. The documents included PHB Shield, XPHB Shield,
Fireball, Hold Person, Lesser Restoration, Greater Restoration, Power Word Heal, Bless, Mage Armor,
Protection from Energy, and Aura of Purity (the no-interpretations case). The before and after files
are byte-identical, with SHA-256:

`CA2081A2C9D1398EACA156CB060E3E9865699E5C6D1C0CCCF63551422DD8845C`

Focused tests passed for PHB extraction, Shield extraction, inventory serialization/classification,
benchmark fixture and candidate evaluation, benchmark comparison/review, inspection projection and
the private inspection API. The full Maven suite passed: 791 tests, 0 failures, 0 errors, 1 skipped.

The documented opt-in inventory regeneration test ran with output directed to
`target/mechanics-inventory-preview`. SHA-256 checks matched all three current inventory/comparison
reports. Tracked analysis artifacts remained unchanged, and `git diff --check` passed.

## Remaining work and risks

Damage/healing dispatch remains in `PhbSpellMechanicsExtractor`, and inventory candidate eligibility
still calls its package-private hooks. Those boundaries should only move in a later focused batch if a
small extraction can preserve production owner eligibility and the wider inventory assessment. Keep
claim slots, owner identity, source revisions, evidence association construction, pending review and
licensing defaults stable in any future edits. Existing extraction, inventory, benchmark, inspection
and REST contract tests remain the compatibility guardrails.
