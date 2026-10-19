# Active mechanics references

The [backend roadmap](../../../docs/THE_DND_PUB_BACKEND_ROADMAP.md) selects the next authorized work.
The [benchmark index](benchmarks/README.md) locates the 7.3.1 fixtures and executable comparison
harness. All six fixtures await human review. Bounded PHB/XPHB Shield, PHB Fireball, PHB Healing
Word, condition, modifier, and protective effect interpretations are implemented; 95 of 969 Spell
owners are processed.

| Role | Document |
| --- | --- |
| Current model semantics | [Mechanics model guide](../../../docs/architecture/MECHANICS-MODEL.md) |
| Current API contract | [Spell inspection](../../../docs/api/SPELL-INSPECTION.md) |
| Approved detailed design | [7.2.1](../../mechanics/step-7/7.2.1-mechanics-model-and-api-contract.md) |
| Canonical model implementation and invariants | [7.2.2](../../mechanics/step-7/7.2.2-canonical-mechanics-model-implementation.md) |
| Inspection implementation, examples and handoff | [7.2.3](../../mechanics/step-7/7.2.3-spell-inspection-api-implementation.md) |
| Benchmark infrastructure and pending human review | [7.3.1](../../mechanics/step-7/7.3.1-spell-benchmark-implementation.md) |
| Shield extraction, actual API output and candidate diagnostics | [7.3.2](../../mechanics/step-7/7.3.2-shield-extraction-implementation.md) |
| Fireball/Healing Word expansion and four-fixture candidate diagnostics | [7.3.3](../../mechanics/step-7/7.3.3-spell-extraction-expansion.md) |
| Partial PHB condition interpretations and dependency/ending relationships | [7.4.3A](../../mechanics/step-7/7.4.3A-spell-conditions-status-effects.md), [7.4.3B](../../mechanics/step-7/7.4.3B-condition-dependencies-endings.md) |
| Partial PHB modifier, resistance, and protection interpretations | [7.4.3C](../../mechanics/step-7/7.4.3C-spell-modifiers-resistances-protection.md) |
| Publication preparation | [P1C approval design](../../mechanics/public-v1/spells/P1C-APPROVAL-DESIGN.md) |
| Existing independent release gate | [P1D implementation](../../mechanics/public-v1/spells/P1D-IMPLEMENTATION.md) |

The original [EntryDte assessment](../../mechanics/entry-assessment/ASSESSMENT-SUMMARY.md) and
[relationship gaps](../../mechanics/entry-assessment/RELATIONSHIP-GAPS.md) remain useful evidence,
but their proposed batch order is superseded for sequencing by the backend roadmap. Publication
decisions remain independent from mechanics interpretation, human review and private inspection.
