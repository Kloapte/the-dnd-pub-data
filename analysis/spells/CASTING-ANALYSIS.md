# Casting metadata analysis

The spell roots provide structured level, school, casting time, range, components, duration, and ritual metadata. They do not encode the entire spell rule: entry trees remain source text/markup and must not be parsed for missing mechanics.

| Structure | Corpus evidence | Projection/design consequence |
| --- | --- | --- |
| Level and school | Present on all 969 spells; levels 0–9 and eight source school codes | Preserve numeric level (including cantrip `0`) and source school code. |
| Casting time | 969 arrays, 971 time records; units action 733, bonus 83, reaction 16, minute 104, hour 35 | Preserve the ordered list, number, unit, optional condition and note. Two Plant Growth definitions have two alternatives; PHB/XPHB notes differ. |
| Range | 969 range objects; `range.type` values include point (890), emanation (26), cone (17), radius (16), line (10), sphere (5), cube (3), hemisphere (1), special (1) | Preserve source geometry/type and optional distance structure. Do not flatten area to a distance string. |
| Distance | 968 distance objects with source type (feet, touch, self, miles, sight, unlimited, and other observed codes); numeric `amount` on 639 | Keep amount optional and unit/type open. Self, touch, sight, and unlimited are not numeric distances. |
| Components | Components object on all 969; V true 919, S true 838, R true 3 | Preserve flags as nullable/optional; absence must not become false. |
| Material | 523 material components: 342 strings, 181 objects | Preserve text and object variants. Object form can carry numeric `cost` (172 occurrences) and text (181). |
| Material consumption | 85 occurrences: boolean on 82 and string on 3 | Keep a typed union; do not coerce explanatory text to a boolean. |
| Duration | 969 arrays, 972 duration elements; timed 662, instant 279, permanent 28, special 3 | Preserve alternatives, exact type, optional amount/unit/up-to, ends, and concentration. |
| Concentration | Explicit `true` on 423 duration elements; never explicit false | Absent differs from false; only source-authored true is known. |
| Ritual | `meta.ritual` is true on 68 spells | Preserve optional ritual metadata; absence is not false. |

Casting-time `condition` occurs on 27 time records; optional notes occur twice. The source does not normalize every trigger into an action-rule model. A Stage B value type should preserve the raw typed condition string and notes without interpreting them. Area shapes and dimensions are in structured range data; `areaTags` is a separate tag list and not a substitute for the range object.

Do not infer verbal/somatic/material components from entries, reaction triggers from prose, or duration/concentration from descriptive text. Keep source-authored entry data internal for future review.
