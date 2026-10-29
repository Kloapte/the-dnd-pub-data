# Core Rules Foundation Phase 5 Language Contract Review

## Executive findings

This review examines the approved Language v1 contract against the catalog findings in the Language expansion report and the original `src/main/resources/data/languages.json`. It is design analysis only; no contract or canonical record was changed.

The current baseline is **201 source records, 66 FULL, 135 PARTIAL, 0 UNSUPPORTED, and 201 canonical outputs**. Twelve affected `typicalSpeakers` fields across 12 records contain **14 selector expressions**: 12 tag expressions across ten records and two `search` expressions. These are genuine limitations of the current schema: its filter form requires `creatureTypes`. The tag criteria could be preserved by a bounded exact-tag selector without mapping tags to creature types. The two `search` expressions should remain unresolved until their matching semantics are established.

The communication entries do not support one general communication engine. A small, bounded concealed-message profile could cover the overlapping Druidic and PHB Thieves' Cant concepts while retaining their distinct detection, comprehension, and time-cost details. Spoken mutual intelligibility is a separate relationship currently stored without its spoken-only scope. Qualith is a distinct inscription and interpretation procedure; the single catalog example does not justify an inscription runtime in Language.

The existing deferrals for non-creature speaker identities, origins, dialect identities, and LanguageScript remain justified. The recommended next decision is whether to approve the narrowly bounded exact-tag selector and concealed-message/intelligibility structures. Search filters, Loross prerequisite identities, and Qualith should remain deferred pending source-semantics or owning-domain evidence.

## Evidence and method

Reviewed the immutable Language source objects and their nested entries; the approved Language and shared schemas; Phase 2 Language mapping decisions in `CORE_RULES_FOUNDATION_PHASE_2_SCHEMA_VALIDATION.md`; the Phase 3 Language representatives; the Phase 5 expansion report, canonical records, and accounting findings; and the relevant reference and ownership boundaries. Source paths below use the original Language array index.

The approved filter allows a creature reference or a filter with required `creatureTypes`, optional group/value tags, and optional source codes. It has no standalone tag or search selector. The shared reference can identify `language`, `background`, `class`, and `class-feature` targets, but canonical identities from those future domains must exist before a reference can be resolved. Its generic `relationship` string does not encode communication mode.

## Speaker-filter audit

The selector wording (for example, “demons” or “satyrs”) is a source display label. The actual source selector criteria are the structured `tag`, `source`, or `search` parameters below. Do not treat a display label or tag as an independently verified creature type. For omitted `source`, preserve the absence; do not add an edition scope.

| Source identity and locator | Exact source selector | Evidence-supported selection | Current limitation and assessment |
| --- | --- | --- | --- |
| `language:ggr:abyssal`, `$.language[3].typicalSpeakers[0..1]` | `tag=demon`, `source=GGR`; `tag=devil`, `source=GGR` | Two separately source-scoped tag selectors labelled demons and devils. | Filter requires creature type. Preserve as two alternatives; do not infer `fiend` or merge the tags. |
| `language:phb:abyssal`, `$.language[4].typicalSpeakers` | `tag=demon`; no source parameter | A demon-tag selector with no explicit source constraint. | Filter requires creature type. Do not add the GGR scope or infer a type. |
| `language:ggr:celestial`, `$.language[17].typicalSpeakers` | `tag=angel`, `source=GGR` | Angel tag scoped to GGR. | Filter requires creature type. Do not infer `celestial` creature type from language name or tag. |
| `language:psi:demonic`, `$.language[42].typicalSpeakers[0..1]` | `tag=demon` and `tag=devil`; each has `source=` empty | Two source selectors with an explicitly empty source parameter. | The empty value is not a source identity. Preserve the distinction from a non-empty source; exact selection behavior remains dependent on the source filter implementation. |
| `language:ggr:goblin`, `$.language[81].typicalSpeakers` | `tag=any race`, `source=GGR` | A GGR tag selector whose literal tag value is `any race`; the display label says goblins. | The label and tag name appear semantically non-equivalent. Do not translate it to `goblin` or a creature type without authoritative selector evidence. |
| `language:phb:goblin`, `$.language[82].typicalSpeakers` | `tag=goblinoid`; no source | Exact `goblinoid` tag criterion. | Filter requires creature type. Do not equate this tag with the `goblinoid` label or a type not found in the approved enum. |
| `language:phb:infernal`, `$.language[98].typicalSpeakers` | `tag=devil`; no source | Exact `devil` tag criterion. | Filter requires creature type. Preserve no source constraint. |
| `language:ggr:loxodon`, `$.language[114].typicalSpeakers` | `tag=any race`, `source=GGR` | GGR tag criterion `any race`; display label says loxodons. | Same label/tag discrepancy as GGR Goblin. This source selector must not be rewritten to an inferred loxodon tag. |
| `language:ggr:merfolk`, `$.language[116].typicalSpeakers` | `tag=any race;merfolk`, `source=GGR` | A compound source tag value with an apparent group/value delimiter. | The current group/value tag object cannot preserve the literal encoded selector without confirming delimiter semantics. Do not split it unless the repository's tag contract confirms the encoding. |
| `language:phb:orc`, `$.language[135].typicalSpeakers` | `tag=orc`; no source | Exact `orc` tag criterion. | Filter requires creature type. Do not map to creature identity or type. |
| `language:erlw:quori`, `$.language[146].typicalSpeakers[2]` | `search=quori`, `source=ERLW` | A search selector scoped to ERLW; the same field also has a separately resolved Inspired creature reference. | Canonical output retains Inspired but omits search. The source does not define what fields are searched, matching rules, or result cardinality. Do not replace with guessed Quori identities. |
| `language:mot:sylvan`, `$.language[167].typicalSpeakers[1]` | `search=satyr`, `source=MOT` | A search selector scoped to MOT; the same field separately names Centaurs. | Canonical output retains the explicit Centaur reference but omits search. Search semantics and exact result set are not established. |

### LANG-1 — Exact tag-only creature selectors

**Recommendation: RECOMMENDED.** This is an additive, bounded extension that addresses the ten literal tag selectors. Add a selector alternative that stores the source criterion verbatim and optionally stores a non-empty exact source code. Keep tag values separate from creature types. For the semicolon-form Merfolk selector, accept a group/value pair only after confirming the source encoding; otherwise preserve the whole token as an exact tag value.

Illustrative positive shape:

```json
{
  "filter": {
    "targetType": "creature",
    "selector": {
      "kind": "TAG",
      "value": "demon",
      "source": "GGR"
    }
  }
}
```

For PHB Abyssal, `source` would be absent. A negative fixture should reject a `creatureType` inferred solely from `demon`, an empty `source` string coerced to `GGR`, and a filter containing neither a creature type nor a selector. Alternatives remain separate list elements; the contract must specify whether the list is a union of speaker selectors.

**Ownership and compatibility:** Language declares an observed speaker-selection criterion; Creature owns the underlying entities and tags. The extension is additive to existing creature-type filters. It does not establish that a tag query is executable without the source tag index. Preserve exact tag string and source scope to avoid broadening or narrowing. Migration is limited to the affected selectors; no existing creature types should be inferred.

### LANG-2 — Search-based speaker selectors

**Recommendation: DEFER.** A `SEARCH` enum alone would hide uncertainty rather than resolve it. The source data establishes the literal query term and source scope for Quori and Satyr, but not whether the search is over names, aliases, descriptions, tags, or a broader index, nor whether matching is exact, tokenized, or substring-based. Do not store it as an executable speaker filter until those semantics are established from authoritative repository evidence. A non-executable preserved source selector would be source syntax rather than interpreted canonical mechanics and is not recommended.

Positive review evidence: retain Quori's explicit `creature:erlw:inspired` reference independently. Negative evidence: do not synthesize all creatures named Quori or Satyr from the display term or use unrestricted substring matching. If approved source tooling later defines `search`, use a bounded selector with a finite target collection and verified matching semantics, plus an exact source scope.

## Communication and writing audit

There are seven relevant entry roots across six records. The PHB Thieves' Cant representative was already present in Phase 3; the other six roots belong to five Phase 5 records.

| Source identity and path | Source-supported behavior | Current canonical coverage | Domain and conclusion |
| --- | --- | --- | --- |
| `language:phb:druidic`, `$.language[51].entries[0]` | Secret language; can be spoken or used to leave hidden messages. A knower automatically spots a message. Another observer can detect its presence with DC 15 Wisdom (Perception), but cannot decipher it without magic. | Category `secret` represented; all entry mechanics omitted. | Language can declare the bounded message convention. Perception check resolution belongs to Rule; magical deciphering can reference a future Spell effect only if an exact identity is stated. |
| `language:phb:thieves-cant`, `$.language[173].entries[0]` | A secret mix of dialect, jargon, and code hides a message in ordinary conversation. Only a creature that knows the Cant understands it. Conveyance takes four times as long as speaking the same idea plainly. | Category `secret` represented; all entry mechanics omitted. | Language can declare comprehension eligibility and relative communication time. Do not transfer these mechanics to XPHB Thieves' Cant, whose entry does not state them. |
| `language:psx:coalition-pidgin`, `$.language[25].entries[0]` | Pidgin combines Orc, Siren, and a native human language described as similar to PSX Vampire at about Italian/Spanish relatedness; Orcs, sirens, and goblins also speak the pidgin. | Identity-only canonical output; source entry omitted. Explicit source language and races are partly resolvable only if exact identities are established. | Language composition/relationship is potentially typed, but the passage does not assert mutual intelligibility with Vampire. Race and setting-community targets depend on their owning domains. Do not infer a dialect or intelligibility edge. |
| `language:psx:common-trade-pidgin`, `$.language[34].entries[0]` | Used when different peoples interact; almost impossible to discuss matters of depth. | Identity-only output; entry omitted. | The “depth” limit is qualitative, with no source-defined topics, test, or threshold. Do not encode an arbitrary complexity scale. Speaker peoples depend on external identities. |
| `language:ggr:goblin`, `$.language[81].entries[0]` | Simplistic language with limited vocabulary and fluid grammar, unsuitable for sophisticated conversation. | Category and script represented; entry omitted. | Descriptive limitation has no defined operational threshold or effect. Preserve as source description externally; do not create a deterministic sophistication mechanic. |
| `language:idrotf:loross`, `$.language[113].entries[0]` | Dead language; Draconic alphabet; spoken Loross and Elvish are mutually intelligible in both directions. A Cloistered Scholar or Sage background, or Warlock with Eyes of the Rune Keeper, can translate written Loross without a check. | Script represented. A generic reference to PHB Elvish with relationship `mutually-intelligible` exists, but its spoken-only mode is not represented. Written translation and prerequisites are omitted. | Separate spoken intelligibility from written translation. Background/class-feature identities belong to Character Building/Class domains; the no-check procedure can be declared only after those exact references resolve. |
| `language:vgm:qualith`, `$.language[145].entries[0]` | Mind-flayer inscriptions transmit creator thoughts to a mind flayer reading by tentacle-touch. Inscription is imprinted on held nonmagical, nonliving material; abrasion/degradation can cause fading/failure. | Script name only; entry omitted. | The writing procedure crosses Language, Creature, Object, and runtime object state. It is not a general language capability. |
| `language:vgm:qualith`, `$.language[145].entries[1]` | Touch gives others fragmentary insight. A non-illithid may make an Intelligence check with DC based on thought complexity; multiple successes can reveal different aspects. Failure causes headache and, in extreme cases, madness. `Comprehend Languages` grants roughly mind-flayer-equivalent understanding. | Script name only; all entry mechanics omitted. | Requires Rule check procedure, object inscription state, Creature capability, Spell resolution, and a qualitative “extreme cases” outcome. No precise DC or madness threshold is supplied. |

### LANG-3 — Concealed messages

**Recommendation: RECOMMENDED, bounded to Druidic and PHB Thieves' Cant.** They share hidden meaning in a language-dependent communication channel, but not one identical procedure. Reuse a small `CONCEALED_MESSAGE` capability and common `KNOWS_LANGUAGE` comprehension prerequisite. Keep detection and time cost as separate optional typed details:

```json
{
  "communicationCapabilities": [
    {
      "kind": "CONCEALED_MESSAGE",
      "channel": "SPOKEN_CONVERSATION",
      "comprehension": {
        "kind": "KNOWS_THIS_LANGUAGE"
      },
      "detection": {
        "kind": "AUTOMATIC_IF_KNOWLEDGE",
        "otherwise": {
          "kind": "CHECK_FOR_PRESENCE",
          "ability": "wisdom",
          "skill": {
            "targetType": "skill",
            "id": "skill:phb:perception"
          },
          "dc": 15,
          "result": "PRESENCE_ONLY"
        }
      },
      "deciphering": {
        "kind": "REQUIRES_MAGIC"
      }
    }
  ]
}
```

This fragment illustrates Druidic only. PHB Thieves' Cant should use the same message capability with ordinary-conversation concealment, knowledge-based comprehension, and a `4`-times communication duration; it should not acquire Druidic's Perception check or magic-only deciphering restriction. Positive tests distinguish the two profiles; negative tests reject a Druidic check on Thieves' Cant, automatic detection by non-knowers, or omission of the four-times cost from PHB Cant. A check/DC value is source evidence; its resolution remains a Rule procedure.

Potential whole-record improvement is limited: two records' entry mechanics are addressed, while other unresolved fields and source portions may keep them partial. XPHB Thieves' Cant remains unchanged.

### LANG-4 — Communication mode and intelligibility

**Recommendation: RECOMMENDED for explicit spoken intelligibility; DEFER composition/qualitative limits.** Add a typed relationship that names mode and direction rather than relying on the generic reference relationship string:

```json
{
  "intelligibility": [
    {
      "kind": "MUTUAL_INTELLIGIBILITY",
      "mode": "SPOKEN",
      "language": {
        "targetType": "language",
        "id": "language:phb:elvish"
      }
    }
  ]
}
```

This captures the bidirectional spoken relationship stated for Loross/Elvish. A negative fixture should reject a written-mode inference from the Draconic alphabet or a relation to an unresolved/nonexistent language identity. Migrate the current generic Loross relationship rather than retaining two competing edges. Coalition Pidgin's comparison to Vampire is a similarity statement, not mutual intelligibility; do not create the relation. Common Trade Pidgin and Goblin limits remain descriptive because source supplies no finite operational taxonomy.

### LANG-5 — Written translation prerequisites

**Recommendation: DEFER.** Loross written translation is a real mechanic but its eligibility references two backgrounds and a Warlock invocation. `common.reference` can carry background/class-feature target types, but the exact approved target identities are not part of current Core Rules and no source record identifies a specific ability check beyond saying no check is needed. Do not create guessed background, invocation, or feature IDs. Revisit after the owning Character Building/Class and optional feature inventories establish canonical identities. A future bounded capability could state `mode: WRITTEN`, required typed grant references, and `check: NONE`; it must not conflate it with spoken mutual intelligibility.

### LANG-6 — Qualith inscription mechanics

**Recommendation: DEFER; no Language schema change now.** Qualith is the only inspected catalog case for a thought-imprinting inscription with material state, tactile interpretation, complexity-dependent DC, progressive insights, degradation, and spell alternative. Any reusable procedure would need explicit owner contracts for inscription/object state, creator/reader capabilities, Rule checks, spell resolution, and possibly Conditions/madness. The source does not define a numeric complexity-to-DC mapping or an operational threshold for “extreme” madness. A generic inscription engine would exceed evidence and ownership. Preserve the complete source entry in the omission register and keep Qualith partial.

## Ownership and deferred dependencies

| Issue | Assessment | Decision |
| --- | --- | --- |
| 76 non-creature `typicalSpeakers` values | Values include peoples, communities, races, classes, and populations. Current references support several target types, but exact source identities and consistent ownership are not established for all. | Keep as external/reference dependencies. Do not add free-text speaker targets or a generic population target. Revisit after Creature, Classes, Character Building, and World Building inventories establish identity contracts. |
| 35 unresolved origins in the new batch | Values identify places, planes, peoples, or groups, not uniformly Languages. Some are recognizable places, but no approved exact target identity is established under the present reference evidence. | Reference-resolution issue, not a Language mechanic/schema gap. Preserve external accounting until World Building/Place identities exist. |
| Two unresolved dialect lists (Gith and Primordial variants) | Source names Githyanki/Githzerai and Auran/Aquan/Ignan/Terran variants, but there are no exact canonical Language records for these dialect identities. | Keep as unresolved identities. Do not create dialect Language shells or infer that dialects are standalone languages. |
| LanguageScript exclusion | LanguageScript records contain presentation/font data, while relevant script names are already retained on Language where present. No currently relevant canonical mechanics were established. | No change. Do not create a LanguageScript domain. |
| Loross backgrounds/invocation | Exact prerequisite identities and owning feature semantics require future Character Building/Class domains. | Defer LANG-5. |
| Qualith object, creature, spell, and check behavior | Crosses future Object/Creature/Spell plus existing Rule and Condition contracts; unique in inspected Language source. | Defer LANG-6; retain partial record and accounting. |

## Coverage forecast

Starting point: **66 FULL / 135 PARTIAL / 0 UNSUPPORTED**.

| Proposal | Source records / portions | Recoverable mechanics | Conservative whole-record forecast |
| --- | --- | --- | --- |
| LANG-1 exact tag selectors | 10 records / 12 tag expressions; Quori and Sylvan search remain excluded | Preserve exact tag literal and any explicit source scope without guessed creature types. | Up to 10 speaker-field omissions recovered. No guaranteed FULL change until each record's other omissions are reconciled; potentially a subset of those records can become FULL. |
| LANG-2 search selectors | Quori and Sylvan, 2 selectors | None safely executable until source search semantics are verified. | 0 coverage change; remain partial for this clause. |
| LANG-3 concealed messages | PHB Druidic and PHB Thieves' Cant, 2 records | Detection/comprehension and Druidic DC; Cant four-times communication time. | 2 entry roots recoverable; records may remain partial due to other source fields. |
| LANG-4 spoken intelligibility | Loross, 1 record | Explicit spoken mutual-intelligibility relation to PHB Elvish. | Existing generic edge can be clarified; likely no whole-record count change. |
| LANG-5 written translation | Loross, 1 record | None until prerequisite identities resolve. | 0 coverage change pending external domains. |
| LANG-6 Qualith | Qualith, 2 roots | No safely reusable mechanics without Object/Creature/Spell/Rule coordination and a source-defined complexity procedure. | Remains partial. |

No unconditional numeric FULL/ PARTIAL change is claimed. The expansion report's per-record rows show current classifications and many co-occurring unresolved fields; after an approved refinement, recompute record-level classifications from complete accounting rather than treating one recovered field as full normalization. No record is forecast to become unsupported.

## Validation recommendations

Structural validation should cover:

- Tag selector requires exactly one selector kind and a non-empty literal value; optional source must be a non-empty known source code. Reject inferred creature types and empty-string source values.
- Keep tag selectors distinct from creature type arrays. Preserve compound/group tag forms only under a verified encoding. Reject unsupported `search` selectors until their matching semantics are approved.
- Validate reference targets and edition/source identity for Language, Skill, background, class-feature, Spell, and creature references. Do not auto-resolve by name.
- For concealed messages, constrain channel, detection, comprehension, deciphering, and communication time to typed finite structures. Validate PHB Druidic DC 15 Wisdom (Perception), knowledge behavior, and magic restriction; validate PHB Cant's four-times duration and its distinct profile.
- For intelligibility, require mode and direction in the relation; reject a written-intelligibility claim sourced only from a shared alphabet.
- For written translation, require resolved eligibility references and explicit no-check semantics only when source evidence supports them.
- For Qualith, any future tests must keep material/creature capabilities separate from language identity and reject invented numeric complexity thresholds or madness outcomes.
- Retain accounting assertions for every relevant source entry and structured selector, including partial entries with supported and omitted portions.
- Regression-check the existing 201 Language identities, metadata, references, categories, scripts, and untouched source/edition distinctions.

Structural schema checks cannot establish that a tag query returns the intended creatures, that source search uses a particular matching algorithm, or that qualitative “depth,” complexity, and madness language has a deterministic game threshold. Those require authoritative source/tool semantics or continued explicit partial classification.

## Decisions requiring human approval

1. Approve or reject LANG-1, an additive exact-tag speaker selector that preserves tag literal and explicit source scope without type inference.
2. Keep LANG-2 search selectors deferred unless an authoritative source contract defines the indexed fields and matching semantics.
3. Approve or reject LANG-3's bounded concealed-message capability, retaining Druidic and PHB Thieves' Cant as distinct profiles.
4. Approve or reject LANG-4's typed spoken-intelligibility relation and migration of Loross's current generic relationship edge.
5. Defer LANG-5 pending exact background and class-feature identities in their owning domains.
6. Defer LANG-6 and retain Qualith as partial; do not create an inscription runtime from the single exceptional case.
7. Keep non-creature speakers, origins, dialect identities, and LanguageScript decisions unchanged.

**Stop point:** This report is a Phase 5 contract review only. No schema, canonical record, example, source data, validator, accounting manifest, register, expansion report, or durable architecture document was modified. Language coverage remains at the stated baseline pending human review.
