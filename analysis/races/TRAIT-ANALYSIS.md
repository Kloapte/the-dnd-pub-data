# Racial Trait Analysis

Named traits are generally nested under `entries`, often as `type: entries` sections. These entries may contain lists, tables, and 5eTools tags. The source also supplies `traitTags` as classification metadata and `overwrite` on subraces as replacement metadata. Some traits are represented by separate fields (movement, ability, proficiencies, senses, protections, spells, feats) and should be normalized independently of prose.

`additionalSpells` preserves structured innate/known/prepared/expanded spell grants, including level keys, ability selection, and usage keys. Entries-only spell/trait details are descriptive and are not reinterpreted. There is no general structured trait catalog reference in this corpus. Public responses should expose safe typed mechanics and trait names/metadata while excluding entry prose and raw markup.
