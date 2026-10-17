# Objects Catalog Implementation

## Source audit and identities

The pinned v2.37.0 `objects.json` root contains 37 definitions. Distribution by primary source:

| Source | Count | Source | Count |
|---|---:|---|---:|
| BMT | 1 | DMG | 8 |
| DSotDQ | 2 | EFA | 1 |
| HBTD | 6 | IDRotF | 1 |
| JttRC | 1 | RMBRE | 2 |
| TCE | 3 | TftYP | 1 |
| WDMM | 1 | XDMG | 10 |

The three authored categories are `U` (10), `SW` (26), and `GEN` (1). All 37 primary sources and every source code
in token, alternate-art, and reprint provenance resolve through the existing Source Registry. No registry mutation
was needed. The complete `(source, name)` identity has no collisions. IDs are
`object:<source>:<lowercase-name-slug>`; duplicate names across sources remain distinct. The seven reprint UIDs
retain their authored name/source values, including DMG to XDMG transitions. The corpus has no copy declarations,
versions, or templates.

## Model and loading

Strict typed Jackson DTEs cover every observed root field. AC/HP, speed, defense values, and action entry content use
explicit sealed polymorphic structures. Existing recursive `EntryDte` is reused for Object descriptive entry shapes,
which match its supported text/list/table forms. This does not reuse the Monster or Item entity models.

`ObjectDefinition` preserves identity, source page, category, NPC marker, size, AC/HP, speed, abilities, senses,
damage and condition defenses, actions, descriptive entries, reprints, token references, alternate art, and SRD flag.
The catalog retains the complete source DTE by stable ID for importer-only provenance such as token credit and
availability flags. All indexed definitions are immutable, deterministically ordered, source-validated, and available
by stable ID or exact name.

No dimensions, damage threshold, other source references, or copy/version metadata occur. Damage/action effects are
descriptive source content; mechanics are not inferred from prose and no damage application or combat execution is
implemented. Prose-only mechanics remain deferred to Step 7 semantic analysis.

## References and API

Four inline markup occurrences are preserved with Object owner locators and processed independently by the existing
resolver: two spell links and two item links. Current outcomes: 2 RESOLVED, 1 AMBIGUOUS, 0 UNRESOLVED, 1 DEFERRED.
The deferred item tag has a third authored configuration/display component unsupported by the current UID parser;
its full token and context remain preserved and it receives no target ID. Existing 3,950 occurrences retain their
outcomes: 1,702 RESOLVED, 1,177 AMBIGUOUS, 30 UNRESOLVED, and 1,041 DEFERRED. New totals are 3,954 occurrences:
1,704 RESOLVED, 1,178 AMBIGUOUS, 30 UNRESOLVED, and 1,042 DEFERRED.

Summary exposes ID, name, source/page, category, size, AC, and HP. Numeric stats expose their number; special stats
expose only a `specialValuePresent` marker, which prevents the special HP source text and markup from leaking.
Special defense values are likewise markers. Detail adds NPC, speed, ability scores, damage and condition defenses,
SRD flag, and supported lightweight references. Action text, descriptive entries, senses, source markup,
token/media metadata, and importer diagnostics are excluded from public JSON. Routes:

- `GET /api/objects?name=&source=&type=`
- `GET /api/objects/{id}`
- `GET /api/objects/by-name/{name}?source=`

The metadata corpus is fixed at 37 records, so list pagination is not used. Name lookup returns all source-distinct
matches in deterministic order. No Object-to-Item or Object-to-Monster relationship is inferred.

## Validation

The Objects tests cover strict full-corpus deserialization, source validation, unique IDs, duplicate-name behavior,
structured special AC/HP and movement shapes, zero speed, immutable indexes, safe JSON projection, unknown-field
rejection, REST filters/lookups, source-aware markup references, prior resolver outcome stability, and classpath JAR
resource loading. All 8 focused Objects tests passed. The full Maven suite passed: 325 tests, 0 failures, 0 errors,
0 skipped. `git diff --check` passed.
