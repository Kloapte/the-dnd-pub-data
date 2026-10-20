# Batch 4 mechanics orchestration cleanup

## Changes

`SpellMechanicsExtractionService` no longer branches on `PhbSpellMechanicsExtractor` or
`ShieldMechanicsExtractor` implementation types, counts PHB owners against a historical minimum, or
counts Shield implementations. It builds the supported owner-to-pattern map once through the shared
`SpellMechanicsPatternRegistry`, whose overlap check remains authoritative. It verifies that every
supported owner yields exactly one processed document with its original owner identity and that the
processed set equals the registry-derived supported set. It also requires each Shield owner declared
by `ShieldMechanicsEdition` to remain supported.

Pinned source artifact metadata now lives in `SpellMechanicsSourceEdition`, keyed by PHB/XPHB source
identity. `ShieldMechanicsEdition` retains Shield owner IDs and attack event distinctions while
delegating revision validation to that source edition metadata. PHB production matches continue to
require the same exact pinned PHB artifact; the two Shield owners continue to require their exact
owner identity, source code, dataset version, path and hash. The evidence source still verifies the
loaded original bytes against each manifest fingerprint.

The registry's unused `extract` pass-through was removed. The runtime and offline inventory continue
to use the same `standard()` composition, and the registry still rejects overlapping pattern
ownership. No production/test pattern order or eligibility changed.

## Retained pilot behavior and deferred work

- `SpellMechanicsAssembly` keeps the `shield-pilot` adapter name and maps its unsupported IDs to the
  legacy `shield` prefix. This is retained because it affects unsupported diagnostic IDs and claim
  derivation metadata; it was not changed in this batch.
- `PhbSpellMechanicsExtractor.supportsForInventory` and `extractForInventory` remain package-private
  production methods called only by the offline inventory runner. Separating candidate assessment
  from execution still lacks a clean rule boundary that avoids duplicating extraction logic or
  broadening production eligibility.
- Damage/healing dispatch and `PhbSaveDamageGeneralizer` are unchanged.
- The explicit Shield PHB/XPHB owner IDs remain intentional integrity contracts rather than
  implementation-type checks or counts. PHB supported-owner completeness is now derived from the
  registry and checked against actual extraction results.

## Verification

Before and after snapshots were written under `target/mechanics-step4/`. The canonical document set
contains PHB/XPHB Shield, Fireball, Hold Person, Lesser Restoration, Greater Restoration, Power Word
Heal, Bless, Mage Armor, Protection from Energy and Aura of Purity (no extracted interpretations).
The private API capture contains default and `include=interpretedMechanics` responses for those
owners, plus by-name Shield responses without a source filter and with PHB/XPHB filters. Both bundles
are byte-identical:

- Canonical documents SHA-256: `CA2081A2C9D1398EACA156CB060E3E9865699E5C6D1C0CCCF63551422DD8845C`
- Private inspection responses SHA-256: `E43906084A3453A38EF8D33F8112D641AA1F91E8388F4899E54D0169512EB766`

Focused extraction, inventory, benchmark, fixture, benchmark review, private projection and REST
inspection tests passed. The full Maven suite passed: 791 tests, 0 failures, 0 errors, 1 skipped.
Explicit inventory regeneration wrote to `target/mechanics-inventory-preview-batch4`; the 7.4.4A
JSON and Markdown reports and the 7.4.4C comparison each matched their tracked versions by SHA-256.
No tracked analysis artifact changed, and `git diff --check` passed.

No mechanics or candidate eligibility was expanded. Review/licensing and publication behavior remain
unchanged; no new mechanic was authorized or published.
