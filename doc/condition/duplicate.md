# Condition source content duplicate during Step 2 Phase 3 representative conversion

This restricted internal register preserves complete original entries for audit. Do not publish or distribute excerpts without a licensing review.
### condition:phb:exhaustion — entry 3

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.condition[6]`
- Source entry path: `$.condition[6].entries[3]`
- Canonical output: `condition/phb-exhaustion.json`
- DUPLICATE: complete source entry — The current level includes all lower levels, already represented by cumulative level mechanics and the threshold effects.

Complete original source entry:

```json
"A creature suffers the effect of its current level of exhaustion as well as all lower levels. For example, a creature suffering level 2 exhaustion has its speed halved and has disadvantage on ability checks."
```

### condition:phb:exhaustion — entry 4

- Source file: `src/main/resources/data/conditionsdiseases.json`
- Source object: `$.condition[6]`
- Source entry path: `$.condition[6].entries[4]`
- Canonical output: `condition/phb-exhaustion.json`
- DUPLICATE: complete source entry — All effects end below level 1, already represented by endsAtLevel=0 and each effect threshold.

Complete original source entry:

```json
"An effect that removes exhaustion reduces its level as specified in the effect's description, with all exhaustion effects ending if a creature's exhaustion level is reduced below 1."
```
