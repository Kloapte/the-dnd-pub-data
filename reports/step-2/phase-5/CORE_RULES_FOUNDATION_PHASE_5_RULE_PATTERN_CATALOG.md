# Core Rules Foundation Phase 5 Rule Pattern Catalog

## Scope and method

This catalog consolidates source-backed semantic findings for all 231 Rule records in the immutable src/main/resources/data/variantrules.json catalog, collection $.variantrule. Every record is REVIEWED; these are discovery classifications, not canonical conversion outcomes. Counts below are distinct records assigned to a pattern, not unique records across patterns or mechanical-occurrence counts. Exact paths identify source portions inspected. Contract fit describes schema expressiveness and does not change canonical conversion status or source-accounting dispositions. No schemas or canonical Rule records were changed.

## Pattern inventory

Pattern assignments in the inventory are record-level. For records assigned to multiple patterns, the table lists all inspected mechanical observation paths for that record; the observation descriptions identify which clauses support which patterns. The inventory does not yet tag each individual observation with its own pattern ID, so these rows must not be read as asserting that every listed path supports every assigned pattern.

### R-PAT-01 - D20 resolution procedures

Ability checks, attack rolls, saving throws, DC procedures, and their distinct outcomes.

- Reviewed records assigned: 27.
- Contract fit among assigned records: SUPPORTED 8, PARTIAL 17, UNSUPPORTED 2.
- Source codes represented: DMG, PHB, VRGR, XGE, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Record observation paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:xphb:ability-check | $.variantrule[0] | SUPPORTED | NO_GAP |
| rule:xphb:attack-roll | $.variantrule[14] | SUPPORTED | NO_GAP |
| rule:xphb:cover | $.variantrule[32].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:critical-hit | $.variantrule[38] | SUPPORTED | NO_GAP |
| rule:xphb:d20-test | $.variantrule[44] | SUPPORTED | NO_GAP |
| rule:xphb:difficulty-class | $.variantrule[54] | UNSUPPORTED | R-GAP-05 |
| rule:dmg:fear-and-horror | $.variantrule[109].entries[1].entries[0]<br>$.variantrule[109].entries[2].entries[1] | PARTIAL | NO_GAP |
| rule:vrgr:fear-and-stress | $.variantrule[110].entries[1].entries[0]<br>$.variantrule[110].entries[1].entries[1]<br>$.variantrule[110].entries[1].entries[3].entries[0]<br>$.variantrule[110].entries[2].entries[0] | PARTIAL | R-GAP-16 |
| rule:vrgr:haunted-traps | $.variantrule[118].entries[0]<br>$.variantrule[118].entries[1]<br>$.variantrule[118].entries[2] | PARTIAL | R-GAP-29 |
| rule:dmg:hero-points | $.variantrule[123].entries[1]<br>$.variantrule[123].entries[2]<br>$.variantrule[123].entries[3] | PARTIAL | R-GAP-46 |
| rule:dmg:initiative-variants | $.variantrule[137].entries[1].entries[0]<br>$.variantrule[137].entries[2].entries[1]<br>$.variantrule[137].entries[3].entries[1]<br>$.variantrule[137].entries[3].entries[2]<br>$.variantrule[137].entries[3].entries[3]<br>$.variantrule[137].entries[3].entries[7]<br>$.variantrule[137].entries[3].entries[8] | PARTIAL | R-GAP-09 |
| rule:dmg:madness | $.variantrule[149].entries[1].entries[0]<br>$.variantrule[149].entries[2].entries[1]<br>$.variantrule[149].entries[2].entries[4]<br>$.variantrule[149].entries[2].entries[5]<br>$.variantrule[149].entries[2].entries[6]<br>$.variantrule[149].entries[3] | PARTIAL | R-GAP-24 |
| rule:dmg:massive-damage | $.variantrule[151].entries[1]<br>$.variantrule[151].entries[2] | PARTIAL | R-GAP-42 |
| rule:dmg:morale | $.variantrule[155].entries[1]<br>$.variantrule[155].entries[2]<br>$.variantrule[155].entries[3] | PARTIAL | R-GAP-44 |
| rule:phb:multiclassing | $.variantrule[157].entries[0]<br>$.variantrule[157].entries[1]<br>$.variantrule[157].entries[2] | PARTIAL | R-GAP-06, R-GAP-30 |
| rule:dmg:new-ability-scores-honor-and-sanity | $.variantrule[159].entries[0]<br>$.variantrule[159].entries[3]<br>$.variantrule[159].entries[4]<br>$.variantrule[159].entries[5].entries[2]<br>$.variantrule[159].entries[5].entries[3]<br>$.variantrule[159].entries[5].entries[4]<br>$.variantrule[159].entries[6].entries[2]<br>$.variantrule[159].entries[6].entries[3] | UNSUPPORTED | R-GAP-26 |
| rule:xphb:passive-perception | $.variantrule[166] | PARTIAL | R-GAP-10 |
| rule:dmg:planar-effects | $.variantrule[168].entries[10]<br>$.variantrule[168].entries[11]<br>$.variantrule[168].entries[12]<br>$.variantrule[168].entries[13]<br>$.variantrule[168].entries[14]<br>$.variantrule[168].entries[15]<br>$.variantrule[168].entries[16]<br>$.variantrule[168].entries[17]<br>$.variantrule[168].entries[18]<br>$.variantrule[168].entries[19]<br>$.variantrule[168].entries[1]<br>$.variantrule[168].entries[20]<br>$.variantrule[168].entries[21]<br>$.variantrule[168].entries[2]<br>$.variantrule[168].entries[3]<br>$.variantrule[168].entries[4]<br>$.variantrule[168].entries[5]<br>$.variantrule[168].entries[6]<br>$.variantrule[168].entries[7]<br>$.variantrule[168].entries[8]<br>$.variantrule[168].entries[9] | PARTIAL | R-GAP-18, R-GAP-24, R-GAP-32 |
| rule:xphb:round-down | $.variantrule[185].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:save | $.variantrule[186].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:saving-throw | $.variantrule[187] | SUPPORTED | NO_GAP |
| rule:xphb:spell-attack | $.variantrule[207] | PARTIAL | R-GAP-12 |
| rule:xge:spellcasting | $.variantrule[209].entries[0]<br>$.variantrule[209].entries[1]<br>$.variantrule[209].entries[2]<br>$.variantrule[209].entries[3] | PARTIAL | R-GAP-35, R-GAP-36 |
| rule:vrgr:survivors | $.variantrule[215].entries[3]<br>$.variantrule[215].entries[4]<br>$.variantrule[215].entries[5] | PARTIAL | R-GAP-37 |
| rule:xge:tool-proficiencies | $.variantrule[222].entries[2].entries[10]<br>$.variantrule[222].entries[2].entries[11]<br>$.variantrule[222].entries[2].entries[12]<br>$.variantrule[222].entries[2].entries[13]<br>$.variantrule[222].entries[2].entries[14]<br>$.variantrule[222].entries[2].entries[15]<br>$.variantrule[222].entries[2].entries[16]<br>$.variantrule[222].entries[2].entries[17]<br>$.variantrule[222].entries[2].entries[18]<br>$.variantrule[222].entries[2].entries[19]<br>$.variantrule[222].entries[2].entries[1]<br>$.variantrule[222].entries[2].entries[20]<br>$.variantrule[222].entries[2].entries[21]<br>$.variantrule[222].entries[2].entries[22]<br>$.variantrule[222].entries[2].entries[23]<br>$.variantrule[222].entries[2].entries[24]<br>$.variantrule[222].entries[2].entries[25]<br>$.variantrule[222].entries[2].entries[26]<br>$.variantrule[222].entries[2].entries[27]<br>$.variantrule[222].entries[2].entries[2]<br>$.variantrule[222].entries[2].entries[3]<br>$.variantrule[222].entries[2].entries[4]<br>$.variantrule[222].entries[2].entries[5]<br>$.variantrule[222].entries[2].entries[6]<br>$.variantrule[222].entries[2].entries[7]<br>$.variantrule[222].entries[2].entries[8]<br>$.variantrule[222].entries[2].entries[9] | PARTIAL | R-GAP-06, R-GAP-38 |
| rule:xge:tying-knots | $.variantrule[224].entries[0]<br>$.variantrule[224].entries[1]<br>$.variantrule[224].entries[2] | PARTIAL | R-GAP-19 |
| rule:xphb:unarmed-strike | $.variantrule[225] | SUPPORTED | NO_GAP |

### R-PAT-02 - Advantage and test modifiers

Advantage, Disadvantage, and numerical modifiers applied to identified tests or rolls.

- Reviewed records assigned: 11.
- Contract fit among assigned records: SUPPORTED 2, PARTIAL 9, UNSUPPORTED 0.
- Source codes represented: DMG, PHB, VRGR, XGE, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:xphb:advantage | $.variantrule[5] | SUPPORTED | NO_GAP |
| rule:xphb:disadvantage | $.variantrule[56] | SUPPORTED | NO_GAP |
| rule:vrgr:fear-and-stress | $.variantrule[110].entries[1].entries[0]<br>$.variantrule[110].entries[1].entries[1]<br>$.variantrule[110].entries[1].entries[3].entries[0]<br>$.variantrule[110].entries[2].entries[0] | PARTIAL | R-GAP-16 |
| rule:xphb:friendly-attitude | $.variantrule[116].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:heroic-inspiration | $.variantrule[124].entries[0]<br>$.variantrule[124].entries[1] | PARTIAL | R-GAP-25 |
| rule:xphb:hostile-attitude | $.variantrule[129].entries[0] | PARTIAL | NO_GAP |
| rule:dmg:inspiration | $.variantrule[139].entries[0]<br>$.variantrule[139].entries[1].entries[0]<br>$.variantrule[139].entries[1].entries[1]<br>$.variantrule[139].entries[1].entries[2]<br>$.variantrule[139].entries[1].entries[3]<br>$.variantrule[139].entries[1].entries[4]<br>$.variantrule[139].entries[1].entries[5]<br>$.variantrule[139].entries[2]<br>$.variantrule[139].entries[5].entries[3] | PARTIAL | R-GAP-25 |
| rule:phb:inspiration | $.variantrule[140].entries[0]<br>$.variantrule[140].entries[1].entries[1]<br>$.variantrule[140].entries[2].entries[0]<br>$.variantrule[140].entries[2].entries[1] | PARTIAL | R-GAP-25 |
| rule:xphb:lightly-obscured | $.variantrule[144].entries[0] | PARTIAL | NO_GAP |
| rule:xge:spellcasting | $.variantrule[209].entries[0]<br>$.variantrule[209].entries[1]<br>$.variantrule[209].entries[2]<br>$.variantrule[209].entries[3] | PARTIAL | R-GAP-35, R-GAP-36 |
| rule:vrgr:survivors | $.variantrule[215].entries[3]<br>$.variantrule[215].entries[4]<br>$.variantrule[215].entries[5] | PARTIAL | R-GAP-37 |

### R-PAT-03 - Movement and spatial procedures

Movement modes, costs, traversal, jumping, falling, occupancy, reach, grids, and teleportation.

- Reviewed records assigned: 34.
- Contract fit among assigned records: SUPPORTED 9, PARTIAL 23, UNSUPPORTED 2.
- Source codes represented: AAG, DMG, GoS, PHB, XGE, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:dmg:action-options | $.variantrule[3].entries[0]<br>$.variantrule[3].entries[1].entries[0]<br>$.variantrule[3].entries[2].entries[0]<br>$.variantrule[3].entries[3].entries[0]<br>$.variantrule[3].entries[4].entries[0]<br>$.variantrule[3].entries[5].entries[0]<br>$.variantrule[3].entries[6].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:burrow-speed | $.variantrule[21].entries[0] | PARTIAL | R-GAP-41 |
| rule:xphb:climb-speed | $.variantrule[28].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:climbing | $.variantrule[29].entries[0] | PARTIAL | R-GAP-41 |
| rule:aag:crashing | $.variantrule[33].entries[0]<br>$.variantrule[33].entries[1]<br>$.variantrule[33].entries[2]<br>$.variantrule[33].entries[3] | PARTIAL | R-GAP-31 |
| rule:xphb:crawling | $.variantrule[34].entries[0] | SUPPORTED | NO_GAP |
| rule:dmg:diagonals | $.variantrule[52].entries[0]<br>$.variantrule[52].entries[1]<br>$.variantrule[52].entries[2] | PARTIAL | R-GAP-45 |
| rule:xphb:difficult-terrain | $.variantrule[53] | SUPPORTED | NO_GAP |
| rule:dmg:facing | $.variantrule[105].entries[0]<br>$.variantrule[105].entries[1]<br>$.variantrule[105].entries[2]<br>$.variantrule[105].entries[3]<br>$.variantrule[105].entries[4]<br>$.variantrule[105].entries[5] | PARTIAL | R-GAP-14 |
| rule:xge:falling | $.variantrule[106].entries[0]<br>$.variantrule[106].entries[1].entries[1]<br>$.variantrule[106].entries[2].entries[0]<br>$.variantrule[106].entries[2].entries[1]<br>$.variantrule[106].entries[2].entries[2] | PARTIAL | R-GAP-15 |
| rule:dmg:flanking | $.variantrule[113] | UNSUPPORTED | R-GAP-07 |
| rule:xphb:fly-speed | $.variantrule[114].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:flying | $.variantrule[115].entries[0] | PARTIAL | R-GAP-41 |
| rule:xphb:grappling | $.variantrule[117].entries[0]<br>$.variantrule[117].entries[1].entries[0].entries[0]<br>$.variantrule[117].entries[1].entries[1].entries[0]<br>$.variantrule[117].entries[1].entries[2].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:high-jump | $.variantrule[125].entries[0]<br>$.variantrule[125].entries[1] | PARTIAL | R-GAP-41 |
| rule:xphb:hover | $.variantrule[130].entries[0] | PARTIAL | R-GAP-41 |
| rule:xphb:initiative | $.variantrule[136] | UNSUPPORTED | R-GAP-09 |
| rule:xphb:jumping | $.variantrule[141].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:long-jump | $.variantrule[146].entries[0]<br>$.variantrule[146].entries[1]<br>$.variantrule[146].entries[2] | PARTIAL | R-GAP-41 |
| rule:xphb:occupied-space | $.variantrule[162].entries[0] | SUPPORTED | NO_GAP |
| rule:gos:ocean-environs | $.variantrule[163].entries[10]<br>$.variantrule[163].entries[11]<br>$.variantrule[163].entries[12]<br>$.variantrule[163].entries[13]<br>$.variantrule[163].entries[1]<br>$.variantrule[163].entries[2]<br>$.variantrule[163].entries[3]<br>$.variantrule[163].entries[4]<br>$.variantrule[163].entries[5]<br>$.variantrule[163].entries[6]<br>$.variantrule[163].entries[7]<br>$.variantrule[163].entries[8]<br>$.variantrule[163].entries[9] | PARTIAL | R-GAP-31 |
| rule:phb:playing-on-a-grid | $.variantrule[172].entries[0].entries[1].entries[0]<br>$.variantrule[172].entries[0].entries[2].entries[0]<br>$.variantrule[172].entries[0].entries[3].entries[0]<br>$.variantrule[172].entries[0].entries[4].entries[0]<br>$.variantrule[172].entries[0].entries[5].entries[0] | PARTIAL | R-GAP-45 |
| rule:xphb:reach | $.variantrule[179].entries[0] | SUPPORTED | NO_GAP |
| rule:gos:ship-stat-blocks | $.variantrule[192].entries[1]<br>$.variantrule[192].entries[2]<br>$.variantrule[192].entries[3]<br>$.variantrule[192].entries[4] | PARTIAL | R-GAP-31 |
| rule:aag:ship-to-ship-combat | $.variantrule[193].entries[1]<br>$.variantrule[193].entries[2]<br>$.variantrule[193].entries[3] | PARTIAL | R-GAP-31 |
| rule:gos:ships-in-combat | $.variantrule[194].entries[1]<br>$.variantrule[194].entries[2]<br>$.variantrule[194].entries[3] | PARTIAL | R-GAP-31 |
| rule:xphb:size | $.variantrule[200].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:speed | $.variantrule[205].entries[0]<br>$.variantrule[205].entries[1].entries[0].entries[0]<br>$.variantrule[205].entries[1].entries[1].entries[0] | PARTIAL | R-GAP-41 |
| rule:gos:superior-ship-upgrades | $.variantrule[214].entries[0]<br>$.variantrule[214].entries[1]<br>$.variantrule[214].entries[2] | PARTIAL | R-GAP-31 |
| rule:xphb:swim-speed | $.variantrule[216].entries[0] | SUPPORTED | R-GAP-41 |
| rule:xphb:swimming | $.variantrule[217].entries[0] | PARTIAL | R-GAP-41 |
| rule:xphb:teleportation | $.variantrule[220].entries[0]<br>$.variantrule[220].entries[1]<br>$.variantrule[220].entries[2]<br>$.variantrule[220].entries[3] | PARTIAL | R-GAP-21 |
| rule:gos:travel-at-sea | $.variantrule[223].entries[1]<br>$.variantrule[223].entries[2]<br>$.variantrule[223].entries[3].entries[0]<br>$.variantrule[223].entries[3].entries[3]<br>$.variantrule[223].entries[3].entries[4]<br>$.variantrule[223].entries[3].entries[5]<br>$.variantrule[223].entries[3].entries[6]<br>$.variantrule[223].entries[3].entries[7] | PARTIAL | R-GAP-31, R-GAP-28 |
| rule:xphb:unoccupied-space | $.variantrule[226].entries[0] | SUPPORTED | NO_GAP |

### R-PAT-04 - Action economy and turn ordering

Action, Bonus Action, Reaction, turn, round, and simultaneous-event procedures.

- Reviewed records assigned: 10.
- Contract fit among assigned records: SUPPORTED 3, PARTIAL 7, UNSUPPORTED 0.
- Source codes represented: AAG, DMG, GoS, VRGR, XGE, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:xphb:action | $.variantrule[2].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:bonus-action | $.variantrule[18].entries[0] | SUPPORTED | NO_GAP |
| rule:vrgr:haunted-traps | $.variantrule[118].entries[0]<br>$.variantrule[118].entries[1]<br>$.variantrule[118].entries[2] | PARTIAL | R-GAP-29 |
| rule:dmg:initiative-variants | $.variantrule[137].entries[1].entries[0]<br>$.variantrule[137].entries[2].entries[1]<br>$.variantrule[137].entries[3].entries[1]<br>$.variantrule[137].entries[3].entries[2]<br>$.variantrule[137].entries[3].entries[3]<br>$.variantrule[137].entries[3].entries[7]<br>$.variantrule[137].entries[3].entries[8] | PARTIAL | R-GAP-09 |
| rule:xphb:reaction | $.variantrule[180].entries[0] | PARTIAL | NO_GAP |
| rule:aag:ship-to-ship-combat | $.variantrule[193].entries[1]<br>$.variantrule[193].entries[2]<br>$.variantrule[193].entries[3] | PARTIAL | R-GAP-31 |
| rule:gos:ships-in-combat | $.variantrule[194].entries[1]<br>$.variantrule[194].entries[2]<br>$.variantrule[194].entries[3] | PARTIAL | R-GAP-31 |
| rule:xphb:short-rest | $.variantrule[195].entries[0]<br>$.variantrule[195].entries[2].entries[0].items[0].entries[0]<br>$.variantrule[195].entries[2].entries[0].items[1].entries[0]<br>$.variantrule[195].entries[4] | PARTIAL | R-GAP-18 |
| rule:xge:simultaneous-effects | $.variantrule[198].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:simultaneous-effects | $.variantrule[199].entries[0] | SUPPORTED | NO_GAP |

### R-PAT-05 - Area geometry and obstruction

Area shapes, dimensions, origin, line/space geometry, and obstruction.

- Reviewed records assigned: 9.
- Contract fit among assigned records: SUPPORTED 5, PARTIAL 4, UNSUPPORTED 0.
- Source codes represented: GoS, XGE, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:xphb:area-of-effect | $.variantrule[11].entries[0] | PARTIAL | R-GAP-36 |
| rule:xphb:cone-area-of-effect | $.variantrule[31].entries[0]<br>$.variantrule[31].entries[1] | SUPPORTED | NO_GAP |
| rule:xphb:cube-area-of-effect | $.variantrule[39].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:cylinder-area-of-effect | $.variantrule[43].entries[0]<br>$.variantrule[43].entries[1] | SUPPORTED | NO_GAP |
| rule:xphb:emanation-area-of-effect | $.variantrule[95].entries[0]<br>$.variantrule[95].entries[1]<br>$.variantrule[95].entries[2] | PARTIAL | R-GAP-36 |
| rule:xphb:line-area-of-effect | $.variantrule[145].entries[0]<br>$.variantrule[145].entries[1] | SUPPORTED | NO_GAP |
| rule:gos:ocean-environs | $.variantrule[163].entries[10]<br>$.variantrule[163].entries[11]<br>$.variantrule[163].entries[12]<br>$.variantrule[163].entries[13]<br>$.variantrule[163].entries[1]<br>$.variantrule[163].entries[2]<br>$.variantrule[163].entries[3]<br>$.variantrule[163].entries[4]<br>$.variantrule[163].entries[5]<br>$.variantrule[163].entries[6]<br>$.variantrule[163].entries[7]<br>$.variantrule[163].entries[8]<br>$.variantrule[163].entries[9] | PARTIAL | R-GAP-31 |
| rule:xge:spellcasting | $.variantrule[209].entries[0]<br>$.variantrule[209].entries[1]<br>$.variantrule[209].entries[2]<br>$.variantrule[209].entries[3] | PARTIAL | R-GAP-35, R-GAP-36 |
| rule:xphb:sphere-area-of-effect | $.variantrule[211].entries[0]<br>$.variantrule[211].entries[1] | SUPPORTED | NO_GAP |

### R-PAT-06 - Equipment and object interactions

Object/material properties, equipment, item use, charges, and state changes.

- Reviewed records assigned: 20.
- Contract fit among assigned records: SUPPORTED 4, PARTIAL 15, UNSUPPORTED 1.
- Source codes represented: AAG, DMG, GoS, PHB, XGE, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:dmg:action-options | $.variantrule[3].entries[0]<br>$.variantrule[3].entries[1].entries[0]<br>$.variantrule[3].entries[2].entries[0]<br>$.variantrule[3].entries[3].entries[0]<br>$.variantrule[3].entries[4].entries[0]<br>$.variantrule[3].entries[5].entries[0]<br>$.variantrule[3].entries[6].entries[0] | PARTIAL | NO_GAP |
| rule:xge:adamantine-weapons | $.variantrule[4].entries[1] | PARTIAL | NO_GAP |
| rule:xphb:attunement | $.variantrule[16].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:breaking-objects | $.variantrule[19].entries[0]<br>$.variantrule[19].entries[2]<br>$.variantrule[19].entries[4]<br>$.variantrule[19].entries[5].entries[0].entries[0]<br>$.variantrule[19].entries[5].entries[1].entries[0]<br>$.variantrule[19].entries[5].entries[2].entries[0] | PARTIAL | R-GAP-39 |
| rule:phb:equipment-sizes | $.variantrule[101].entries[0]<br>$.variantrule[101].entries[1]<br>$.variantrule[101].entries[2] | PARTIAL | NO_GAP |
| rule:dmg:firearms | $.variantrule[112].entries[1].entries[0]<br>$.variantrule[112].entries[2].entries[1].entries[0]<br>$.variantrule[112].entries[2].entries[2].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:improvised-weapons | $.variantrule[134].entries[0]<br>$.variantrule[134].entries[1].entries[0].entries[0]<br>$.variantrule[134].entries[1].entries[1].entries[0]<br>$.variantrule[134].entries[1].entries[2].entries[0]<br>$.variantrule[134].entries[1].entries[3].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:object | $.variantrule[161].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:occupied-space | $.variantrule[162].entries[0] | SUPPORTED | NO_GAP |
| rule:gos:random-ships | $.variantrule[178].entries[1]<br>$.variantrule[178].entries[3]<br>$.variantrule[178].entries[4]<br>$.variantrule[178].entries[5] | PARTIAL | R-GAP-31, R-GAP-28 |
| rule:xge:shared-campaign-variant-rules | $.variantrule[190].entries[1]<br>$.variantrule[190].entries[2]<br>$.variantrule[190].entries[3]<br>$.variantrule[190].entries[4] | UNSUPPORTED | R-GAP-33 |
| rule:gos:ship-stat-blocks | $.variantrule[192].entries[1]<br>$.variantrule[192].entries[2]<br>$.variantrule[192].entries[3]<br>$.variantrule[192].entries[4] | PARTIAL | R-GAP-31 |
| rule:aag:ship-to-ship-combat | $.variantrule[193].entries[1]<br>$.variantrule[193].entries[2]<br>$.variantrule[193].entries[3] | PARTIAL | R-GAP-31 |
| rule:xphb:spellcasting-focus | $.variantrule[210].entries[0] | PARTIAL | NO_GAP |
| rule:gos:superior-ship-upgrades | $.variantrule[214].entries[0]<br>$.variantrule[214].entries[1]<br>$.variantrule[214].entries[2] | PARTIAL | R-GAP-31 |
| rule:xphb:teleportation | $.variantrule[220].entries[0]<br>$.variantrule[220].entries[1]<br>$.variantrule[220].entries[2]<br>$.variantrule[220].entries[3] | PARTIAL | R-GAP-21 |
| rule:xge:tool-proficiencies | $.variantrule[222].entries[2].entries[10]<br>$.variantrule[222].entries[2].entries[11]<br>$.variantrule[222].entries[2].entries[12]<br>$.variantrule[222].entries[2].entries[13]<br>$.variantrule[222].entries[2].entries[14]<br>$.variantrule[222].entries[2].entries[15]<br>$.variantrule[222].entries[2].entries[16]<br>$.variantrule[222].entries[2].entries[17]<br>$.variantrule[222].entries[2].entries[18]<br>$.variantrule[222].entries[2].entries[19]<br>$.variantrule[222].entries[2].entries[1]<br>$.variantrule[222].entries[2].entries[20]<br>$.variantrule[222].entries[2].entries[21]<br>$.variantrule[222].entries[2].entries[22]<br>$.variantrule[222].entries[2].entries[23]<br>$.variantrule[222].entries[2].entries[24]<br>$.variantrule[222].entries[2].entries[25]<br>$.variantrule[222].entries[2].entries[26]<br>$.variantrule[222].entries[2].entries[27]<br>$.variantrule[222].entries[2].entries[2]<br>$.variantrule[222].entries[2].entries[3]<br>$.variantrule[222].entries[2].entries[4]<br>$.variantrule[222].entries[2].entries[5]<br>$.variantrule[222].entries[2].entries[6]<br>$.variantrule[222].entries[2].entries[7]<br>$.variantrule[222].entries[2].entries[8]<br>$.variantrule[222].entries[2].entries[9] | PARTIAL | R-GAP-06, R-GAP-38 |
| rule:xphb:unoccupied-space | $.variantrule[226].entries[0] | SUPPORTED | NO_GAP |
| rule:dmg:wands-that-don-t-recharge | $.variantrule[228].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:weapon | $.variantrule[229].entries[0] | SUPPORTED | NO_GAP |

### R-PAT-07 - Repeated checks and outcome bands

Multi-check procedures, thresholds, table bands, and check-result-dependent branches.

- Reviewed records assigned: 34.
- Contract fit among assigned records: SUPPORTED 0, PARTIAL 33, UNSUPPORTED 1.
- Source codes represented: AI, CoS, DMG, GoS, PHB, XGE.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:dmg:action-options | $.variantrule[3].entries[0]<br>$.variantrule[3].entries[1].entries[0]<br>$.variantrule[3].entries[2].entries[0]<br>$.variantrule[3].entries[3].entries[0]<br>$.variantrule[3].entries[4].entries[0]<br>$.variantrule[3].entries[5].entries[0]<br>$.variantrule[3].entries[6].entries[0] | PARTIAL | NO_GAP |
| rule:dmg:alien-technology | $.variantrule[7].entries[0] | PARTIAL | NO_GAP |
| rule:xge:downtime-activity-carousing | $.variantrule[60].entries[1].entries[0]<br>$.variantrule[60].entries[2].entries[0]<br>$.variantrule[60].entries[2].entries[1]<br>$.variantrule[60].entries[2].entries[2]<br>$.variantrule[60].entries[3] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-crime | $.variantrule[64].entries[1].entries[0]<br>$.variantrule[64].entries[2].entries[0]<br>$.variantrule[64].entries[2].entries[1]<br>$.variantrule[64].entries[2].entries[2]<br>$.variantrule[64].entries[3] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-gambling | $.variantrule[66].entries[1].entries[0]<br>$.variantrule[66].entries[2].entries[0]<br>$.variantrule[66].entries[2].entries[1]<br>$.variantrule[66].entries[3] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-pit-fighting | $.variantrule[68].entries[1].entries[0]<br>$.variantrule[68].entries[2].entries[0]<br>$.variantrule[68].entries[3] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-religious-service | $.variantrule[72].entries[1].entries[0]<br>$.variantrule[72].entries[2].entries[0]<br>$.variantrule[72].entries[2].entries[1]<br>$.variantrule[72].entries[2].entries[2]<br>$.variantrule[72].entries[3] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-research | $.variantrule[73].entries[1].entries[0]<br>$.variantrule[73].entries[2].entries[0]<br>$.variantrule[73].entries[2].entries[1]<br>$.variantrule[73].entries[3] | PARTIAL | R-GAP-13 |
| rule:phb:downtime-activity-researching | $.variantrule[74].entries[1]<br>$.variantrule[74].entries[2] | PARTIAL | R-GAP-13 |
| rule:dmg:downtime-activity-sowing-rumors | $.variantrule[79].entries[1]<br>$.variantrule[79].entries[2]<br>$.variantrule[79].entries[3] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-work | $.variantrule[83].entries[1].entries[0]<br>$.variantrule[83].entries[2].entries[0]<br>$.variantrule[83].entries[2].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-explore-territory | $.variantrule[84].entries[0]<br>$.variantrule[84].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-franchise-restructuring | $.variantrule[85].entries[0]<br>$.variantrule[85].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-headquarters-modification | $.variantrule[86].entries[0]<br>$.variantrule[86].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-marketeering | $.variantrule[87].entries[0]<br>$.variantrule[87].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-philanthropic-enterprise | $.variantrule[88].entries[0]<br>$.variantrule[88].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-running-a-franchise | $.variantrule[89].entries[0]<br>$.variantrule[89].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-schmoozing | $.variantrule[90].entries[0]<br>$.variantrule[90].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-scrutineering | $.variantrule[91].entries[0]<br>$.variantrule[91].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-shady-business-practice | $.variantrule[92].entries[0]<br>$.variantrule[92].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-team-building | $.variantrule[93].entries[0]<br>$.variantrule[93].entries[1] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-revisited | $.variantrule[94].entries[0]<br>$.variantrule[94].entries[1] | PARTIAL | R-GAP-13 |
| rule:gos:encounters-at-sea | $.variantrule[98].entries[1] | PARTIAL | R-GAP-31, R-GAP-28 |
| rule:dmg:madness | $.variantrule[149].entries[1].entries[0]<br>$.variantrule[149].entries[2].entries[1]<br>$.variantrule[149].entries[2].entries[4]<br>$.variantrule[149].entries[2].entries[5]<br>$.variantrule[149].entries[2].entries[6]<br>$.variantrule[149].entries[3] | PARTIAL | R-GAP-24 |
| rule:phb:multiclassing | $.variantrule[157].entries[0]<br>$.variantrule[157].entries[1]<br>$.variantrule[157].entries[2] | PARTIAL | R-GAP-06, R-GAP-30 |
| rule:gos:mysterious-islands | $.variantrule[158].entries[1]<br>$.variantrule[158].entries[2]<br>$.variantrule[158].entries[3] | PARTIAL | R-GAP-28 |
| rule:gos:ocean-environs | $.variantrule[163].entries[10]<br>$.variantrule[163].entries[11]<br>$.variantrule[163].entries[12]<br>$.variantrule[163].entries[13]<br>$.variantrule[163].entries[1]<br>$.variantrule[163].entries[2]<br>$.variantrule[163].entries[3]<br>$.variantrule[163].entries[4]<br>$.variantrule[163].entries[5]<br>$.variantrule[163].entries[6]<br>$.variantrule[163].entries[7]<br>$.variantrule[163].entries[8]<br>$.variantrule[163].entries[9] | PARTIAL | R-GAP-31 |
| rule:gos:officers-and-crew | $.variantrule[164].entries[0]<br>$.variantrule[164].entries[1]<br>$.variantrule[164].entries[2] | PARTIAL | R-GAP-31 |
| rule:dmg:planar-effects | $.variantrule[168].entries[10]<br>$.variantrule[168].entries[11]<br>$.variantrule[168].entries[12]<br>$.variantrule[168].entries[13]<br>$.variantrule[168].entries[14]<br>$.variantrule[168].entries[15]<br>$.variantrule[168].entries[16]<br>$.variantrule[168].entries[17]<br>$.variantrule[168].entries[18]<br>$.variantrule[168].entries[19]<br>$.variantrule[168].entries[1]<br>$.variantrule[168].entries[20]<br>$.variantrule[168].entries[21]<br>$.variantrule[168].entries[2]<br>$.variantrule[168].entries[3]<br>$.variantrule[168].entries[4]<br>$.variantrule[168].entries[5]<br>$.variantrule[168].entries[6]<br>$.variantrule[168].entries[7]<br>$.variantrule[168].entries[8]<br>$.variantrule[168].entries[9] | PARTIAL | R-GAP-18, R-GAP-24, R-GAP-32 |
| rule:cos:prophet-s-gambit | $.variantrule[177].entries[1]<br>$.variantrule[177].entries[2]<br>$.variantrule[177].entries[3]<br>$.variantrule[177].entries[4] | UNSUPPORTED | R-GAP-22 |
| rule:dmg:scroll-mishaps | $.variantrule[188].entries[0]<br>$.variantrule[188].entries[1] | PARTIAL | NO_GAP |
| rule:gos:ships-in-combat | $.variantrule[194].entries[1]<br>$.variantrule[194].entries[2]<br>$.variantrule[194].entries[3] | PARTIAL | R-GAP-31 |
| rule:xge:tool-proficiencies | $.variantrule[222].entries[2].entries[10]<br>$.variantrule[222].entries[2].entries[11]<br>$.variantrule[222].entries[2].entries[12]<br>$.variantrule[222].entries[2].entries[13]<br>$.variantrule[222].entries[2].entries[14]<br>$.variantrule[222].entries[2].entries[15]<br>$.variantrule[222].entries[2].entries[16]<br>$.variantrule[222].entries[2].entries[17]<br>$.variantrule[222].entries[2].entries[18]<br>$.variantrule[222].entries[2].entries[19]<br>$.variantrule[222].entries[2].entries[1]<br>$.variantrule[222].entries[2].entries[20]<br>$.variantrule[222].entries[2].entries[21]<br>$.variantrule[222].entries[2].entries[22]<br>$.variantrule[222].entries[2].entries[23]<br>$.variantrule[222].entries[2].entries[24]<br>$.variantrule[222].entries[2].entries[25]<br>$.variantrule[222].entries[2].entries[26]<br>$.variantrule[222].entries[2].entries[27]<br>$.variantrule[222].entries[2].entries[2]<br>$.variantrule[222].entries[2].entries[3]<br>$.variantrule[222].entries[2].entries[4]<br>$.variantrule[222].entries[2].entries[5]<br>$.variantrule[222].entries[2].entries[6]<br>$.variantrule[222].entries[2].entries[7]<br>$.variantrule[222].entries[2].entries[8]<br>$.variantrule[222].entries[2].entries[9] | PARTIAL | R-GAP-06, R-GAP-38 |
| rule:gos:travel-at-sea | $.variantrule[223].entries[1]<br>$.variantrule[223].entries[2]<br>$.variantrule[223].entries[3].entries[0]<br>$.variantrule[223].entries[3].entries[3]<br>$.variantrule[223].entries[3].entries[4]<br>$.variantrule[223].entries[3].entries[5]<br>$.variantrule[223].entries[3].entries[6]<br>$.variantrule[223].entries[3].entries[7] | PARTIAL | R-GAP-31, R-GAP-28 |

### R-PAT-08 - Creature and character state

Eligibility, conditions, character/creature statistics, resources, and state-dependent outcomes.

- Reviewed records assigned: 44.
- Contract fit among assigned records: SUPPORTED 2, PARTIAL 40, UNSUPPORTED 2.
- Source codes represented: AAG, AI, CoS, DMG, GoS, MM, PHB, PSI, VRGR, XGE, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:xphb:challenge-rating | $.variantrule[25].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:condition | $.variantrule[30].entries[0]<br>$.variantrule[30].entries[1] | PARTIAL | NO_GAP |
| rule:aag:crew | $.variantrule[37].entries[0]<br>$.variantrule[37].entries[1] | PARTIAL | R-GAP-31 |
| rule:xphb:dead | $.variantrule[50].entries[0]<br>$.variantrule[50].entries[1] | PARTIAL | NO_GAP |
| rule:xphb:death-saving-throw | $.variantrule[51] | PARTIAL | R-GAP-04 |
| rule:ai:downtime-and-franchise-activity-running-a-franchise | $.variantrule[89].entries[0]<br>$.variantrule[89].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-team-building | $.variantrule[93].entries[0]<br>$.variantrule[93].entries[1] | PARTIAL | R-GAP-13 |
| rule:psi:emrakul-s-madness | $.variantrule[96].entries[0]<br>$.variantrule[96].entries[1] | PARTIAL | R-GAP-26, R-GAP-27 |
| rule:dmg:fear-and-horror | $.variantrule[109].entries[1].entries[0]<br>$.variantrule[109].entries[2].entries[1] | PARTIAL | NO_GAP |
| rule:vrgr:fear-and-stress | $.variantrule[110].entries[1].entries[0]<br>$.variantrule[110].entries[1].entries[1]<br>$.variantrule[110].entries[1].entries[3].entries[0]<br>$.variantrule[110].entries[2].entries[0] | PARTIAL | R-GAP-16 |
| rule:xphb:flying | $.variantrule[115].entries[0] | PARTIAL | R-GAP-41 |
| rule:xphb:grappling | $.variantrule[117].entries[0]<br>$.variantrule[117].entries[1].entries[0].entries[0]<br>$.variantrule[117].entries[1].entries[1].entries[0]<br>$.variantrule[117].entries[1].entries[2].entries[0] | PARTIAL | NO_GAP |
| rule:vrgr:haunted-traps | $.variantrule[118].entries[0]<br>$.variantrule[118].entries[1]<br>$.variantrule[118].entries[2] | PARTIAL | R-GAP-29 |
| rule:xphb:heavily-obscured | $.variantrule[122].entries[0] | PARTIAL | NO_GAP |
| rule:dmg:hero-points | $.variantrule[123].entries[1]<br>$.variantrule[123].entries[2]<br>$.variantrule[123].entries[3] | PARTIAL | R-GAP-46 |
| rule:xphb:heroic-inspiration | $.variantrule[124].entries[0]<br>$.variantrule[124].entries[1] | PARTIAL | R-GAP-25 |
| rule:xphb:immunity | $.variantrule[133].entries[0] | SUPPORTED | NO_GAP |
| rule:dmg:injuries | $.variantrule[138].entries[1]<br>$.variantrule[138].entries[3]<br>$.variantrule[138].entries[4].rows[0]<br>$.variantrule[138].entries[4].rows[1]<br>$.variantrule[138].entries[4].rows[2]<br>$.variantrule[138].entries[4].rows[3]<br>$.variantrule[138].entries[4].rows[4]<br>$.variantrule[138].entries[4].rows[5]<br>$.variantrule[138].entries[4].rows[6]<br>$.variantrule[138].entries[4].rows[7]<br>$.variantrule[138].entries[4].rows[8]<br>$.variantrule[138].entries[5] | PARTIAL | R-GAP-23 |
| rule:dmg:inspiration | $.variantrule[139].entries[0]<br>$.variantrule[139].entries[1].entries[0]<br>$.variantrule[139].entries[1].entries[1]<br>$.variantrule[139].entries[1].entries[2]<br>$.variantrule[139].entries[1].entries[3]<br>$.variantrule[139].entries[1].entries[4]<br>$.variantrule[139].entries[1].entries[5]<br>$.variantrule[139].entries[2]<br>$.variantrule[139].entries[5].entries[3] | PARTIAL | R-GAP-25 |
| rule:phb:inspiration | $.variantrule[140].entries[0]<br>$.variantrule[140].entries[1].entries[1]<br>$.variantrule[140].entries[2].entries[0]<br>$.variantrule[140].entries[2].entries[1] | PARTIAL | R-GAP-25 |
| rule:xphb:knocking-out-a-creature | $.variantrule[142].entries[0]<br>$.variantrule[142].entries[1] | PARTIAL | NO_GAP |
| rule:dmg:madness | $.variantrule[149].entries[1].entries[0]<br>$.variantrule[149].entries[2].entries[1]<br>$.variantrule[149].entries[2].entries[4]<br>$.variantrule[149].entries[2].entries[5]<br>$.variantrule[149].entries[2].entries[6]<br>$.variantrule[149].entries[3] | PARTIAL | R-GAP-24 |
| rule:gos:mysterious-islands | $.variantrule[158].entries[1]<br>$.variantrule[158].entries[2]<br>$.variantrule[158].entries[3] | PARTIAL | R-GAP-28 |
| rule:gos:ocean-environs | $.variantrule[163].entries[10]<br>$.variantrule[163].entries[11]<br>$.variantrule[163].entries[12]<br>$.variantrule[163].entries[13]<br>$.variantrule[163].entries[1]<br>$.variantrule[163].entries[2]<br>$.variantrule[163].entries[3]<br>$.variantrule[163].entries[4]<br>$.variantrule[163].entries[5]<br>$.variantrule[163].entries[6]<br>$.variantrule[163].entries[7]<br>$.variantrule[163].entries[8]<br>$.variantrule[163].entries[9] | PARTIAL | R-GAP-31 |
| rule:gos:officers-and-crew | $.variantrule[164].entries[0]<br>$.variantrule[164].entries[1]<br>$.variantrule[164].entries[2] | PARTIAL | R-GAP-31 |
| rule:xphb:per-day | $.variantrule[167].entries[0] | PARTIAL | NO_GAP |
| rule:dmg:planar-effects | $.variantrule[168].entries[10]<br>$.variantrule[168].entries[11]<br>$.variantrule[168].entries[12]<br>$.variantrule[168].entries[13]<br>$.variantrule[168].entries[14]<br>$.variantrule[168].entries[15]<br>$.variantrule[168].entries[16]<br>$.variantrule[168].entries[17]<br>$.variantrule[168].entries[18]<br>$.variantrule[168].entries[19]<br>$.variantrule[168].entries[1]<br>$.variantrule[168].entries[20]<br>$.variantrule[168].entries[21]<br>$.variantrule[168].entries[2]<br>$.variantrule[168].entries[3]<br>$.variantrule[168].entries[4]<br>$.variantrule[168].entries[5]<br>$.variantrule[168].entries[6]<br>$.variantrule[168].entries[7]<br>$.variantrule[168].entries[8]<br>$.variantrule[168].entries[9] | PARTIAL | R-GAP-18, R-GAP-24, R-GAP-32 |
| rule:mm:player-characters-as-lycanthropes | $.variantrule[170].entries[0]<br>$.variantrule[170].entries[1]<br>$.variantrule[170].entries[3].entries[0]<br>$.variantrule[170].entries[3].entries[1]<br>$.variantrule[170].entries[3].entries[2]<br>$.variantrule[170].entries[3].entries[5].entries[0]<br>$.variantrule[170].entries[3].entries[6].entries[0]<br>$.variantrule[170].entries[3].entries[7].entries[0] | PARTIAL | NO_GAP |
| rule:mm:player-characters-as-vampires | $.variantrule[171].entries[0]<br>$.variantrule[171].entries[1].entries[0] | PARTIAL | NO_GAP |
| rule:dmg:plot-points | $.variantrule[173].entries[1].entries[0]<br>$.variantrule[173].entries[2]<br>$.variantrule[173].entries[3]<br>$.variantrule[173].entries[4] | PARTIAL | NO_GAP |
| rule:xphb:possession | $.variantrule[174].entries[0] | PARTIAL | NO_GAP |
| rule:cos:prophet-s-gambit | $.variantrule[177].entries[1]<br>$.variantrule[177].entries[2]<br>$.variantrule[177].entries[3]<br>$.variantrule[177].entries[4] | UNSUPPORTED | R-GAP-22 |
| rule:gos:random-ships | $.variantrule[178].entries[1]<br>$.variantrule[178].entries[3]<br>$.variantrule[178].entries[4]<br>$.variantrule[178].entries[5] | PARTIAL | R-GAP-31, R-GAP-28 |
| rule:xphb:shape-shifting | $.variantrule[189].entries[0] | PARTIAL | NO_GAP |
| rule:xge:shared-campaign-variant-rules | $.variantrule[190].entries[1]<br>$.variantrule[190].entries[2]<br>$.variantrule[190].entries[3]<br>$.variantrule[190].entries[4] | UNSUPPORTED | R-GAP-33 |
| rule:gos:ship-stat-blocks | $.variantrule[192].entries[1]<br>$.variantrule[192].entries[2]<br>$.variantrule[192].entries[3]<br>$.variantrule[192].entries[4] | PARTIAL | R-GAP-31 |
| rule:gos:ships-in-combat | $.variantrule[194].entries[1]<br>$.variantrule[194].entries[2]<br>$.variantrule[194].entries[3] | PARTIAL | R-GAP-31 |
| rule:xphb:size | $.variantrule[200].entries[0] | PARTIAL | NO_GAP |
| rule:xge:sleep | $.variantrule[204].entries[0]<br>$.variantrule[204].entries[1].entries[0]<br>$.variantrule[204].entries[2].entries[0]<br>$.variantrule[204].entries[3].entries[0] | PARTIAL | R-GAP-18 |
| rule:dmg:spell-points | $.variantrule[208].entries[0]<br>$.variantrule[208].entries[1]<br>$.variantrule[208].entries[2]<br>$.variantrule[208].entries[3]<br>$.variantrule[208].entries[4]<br>$.variantrule[208].entries[5]<br>$.variantrule[208].entries[7]<br>$.variantrule[208].entries[8] | PARTIAL | NO_GAP |
| rule:xphb:stable | $.variantrule[212].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:stat-block | $.variantrule[213].entries[0]<br>$.variantrule[213].entries[1] | PARTIAL | NO_GAP |
| rule:vrgr:survivors | $.variantrule[215].entries[3]<br>$.variantrule[215].entries[4]<br>$.variantrule[215].entries[5] | PARTIAL | R-GAP-37 |
| rule:gos:travel-at-sea | $.variantrule[223].entries[1]<br>$.variantrule[223].entries[2]<br>$.variantrule[223].entries[3].entries[0]<br>$.variantrule[223].entries[3].entries[3]<br>$.variantrule[223].entries[3].entries[4]<br>$.variantrule[223].entries[3].entries[5]<br>$.variantrule[223].entries[3].entries[6]<br>$.variantrule[223].entries[3].entries[7] | PARTIAL | R-GAP-31, R-GAP-28 |

### R-PAT-09 - Ability, Skill, and proficiency

Ability/Skill pairings, training, proficiency, task checks, and related modifiers.

- Reviewed records assigned: 22.
- Contract fit among assigned records: SUPPORTED 0, PARTIAL 16, UNSUPPORTED 6.
- Source codes represented: AI, DMG, ESK, GoS, PHB, TCE, XGE, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:xphb:armor-training | $.variantrule[13].entries[0] | PARTIAL | NO_GAP |
| rule:tce:customizing-your-origin | $.variantrule[42].entries[1]<br>$.variantrule[42].entries[2]<br>$.variantrule[42].entries[3]<br>$.variantrule[42].entries[4] | PARTIAL | NO_GAP |
| rule:xge:downtime-activity-training | $.variantrule[81].entries[0]<br>$.variantrule[81].entries[1].entries[0]<br>$.variantrule[81].entries[2].entries[0] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-work | $.variantrule[83].entries[1].entries[0]<br>$.variantrule[83].entries[2].entries[0]<br>$.variantrule[83].entries[2].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-franchise-restructuring | $.variantrule[85].entries[0]<br>$.variantrule[85].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-headquarters-modification | $.variantrule[86].entries[0]<br>$.variantrule[86].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-marketeering | $.variantrule[87].entries[0]<br>$.variantrule[87].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-shady-business-practice | $.variantrule[92].entries[0]<br>$.variantrule[92].entries[1] | PARTIAL | R-GAP-13 |
| rule:xphb:expertise | $.variantrule[103] | UNSUPPORTED | R-GAP-06 |
| rule:phb:multiclassing | $.variantrule[157].entries[0]<br>$.variantrule[157].entries[1]<br>$.variantrule[157].entries[2] | PARTIAL | R-GAP-06, R-GAP-30 |
| rule:dmg:new-ability-scores-honor-and-sanity | $.variantrule[159].entries[0]<br>$.variantrule[159].entries[3]<br>$.variantrule[159].entries[4]<br>$.variantrule[159].entries[5].entries[2]<br>$.variantrule[159].entries[5].entries[3]<br>$.variantrule[159].entries[5].entries[4]<br>$.variantrule[159].entries[6].entries[2]<br>$.variantrule[159].entries[6].entries[3] | UNSUPPORTED | R-GAP-26 |
| rule:gos:officers-and-crew | $.variantrule[164].entries[0]<br>$.variantrule[164].entries[1]<br>$.variantrule[164].entries[2] | PARTIAL | R-GAP-31 |
| rule:xphb:proficiency | $.variantrule[175] | UNSUPPORTED | R-GAP-06 |
| rule:dmg:proficiency-dice | $.variantrule[176] | UNSUPPORTED | R-GAP-06 |
| rule:esk:sidekicks | $.variantrule[196].entries[0]<br>$.variantrule[196].entries[1] | PARTIAL | R-GAP-34 |
| rule:tce:sidekicks | $.variantrule[197].entries[0]<br>$.variantrule[197].entries[1]<br>$.variantrule[197].entries[2] | PARTIAL | R-GAP-34 |
| rule:xphb:skill | $.variantrule[201] | UNSUPPORTED | R-GAP-06 |
| rule:dmg:skill-variants | $.variantrule[202] | UNSUPPORTED | R-GAP-11 |
| rule:phb:skills-with-different-abilities | $.variantrule[203] | PARTIAL | R-GAP-11 |
| rule:xge:tool-proficiencies | $.variantrule[222].entries[2].entries[10]<br>$.variantrule[222].entries[2].entries[11]<br>$.variantrule[222].entries[2].entries[12]<br>$.variantrule[222].entries[2].entries[13]<br>$.variantrule[222].entries[2].entries[14]<br>$.variantrule[222].entries[2].entries[15]<br>$.variantrule[222].entries[2].entries[16]<br>$.variantrule[222].entries[2].entries[17]<br>$.variantrule[222].entries[2].entries[18]<br>$.variantrule[222].entries[2].entries[19]<br>$.variantrule[222].entries[2].entries[1]<br>$.variantrule[222].entries[2].entries[20]<br>$.variantrule[222].entries[2].entries[21]<br>$.variantrule[222].entries[2].entries[22]<br>$.variantrule[222].entries[2].entries[23]<br>$.variantrule[222].entries[2].entries[24]<br>$.variantrule[222].entries[2].entries[25]<br>$.variantrule[222].entries[2].entries[26]<br>$.variantrule[222].entries[2].entries[27]<br>$.variantrule[222].entries[2].entries[2]<br>$.variantrule[222].entries[2].entries[3]<br>$.variantrule[222].entries[2].entries[4]<br>$.variantrule[222].entries[2].entries[5]<br>$.variantrule[222].entries[2].entries[6]<br>$.variantrule[222].entries[2].entries[7]<br>$.variantrule[222].entries[2].entries[8]<br>$.variantrule[222].entries[2].entries[9] | PARTIAL | R-GAP-06, R-GAP-38 |
| rule:gos:travel-at-sea | $.variantrule[223].entries[1]<br>$.variantrule[223].entries[2]<br>$.variantrule[223].entries[3].entries[0]<br>$.variantrule[223].entries[3].entries[3]<br>$.variantrule[223].entries[3].entries[4]<br>$.variantrule[223].entries[3].entries[5]<br>$.variantrule[223].entries[3].entries[6]<br>$.variantrule[223].entries[3].entries[7] | PARTIAL | R-GAP-31, R-GAP-28 |
| rule:xge:tying-knots | $.variantrule[224].entries[0]<br>$.variantrule[224].entries[1]<br>$.variantrule[224].entries[2] | PARTIAL | R-GAP-19 |

### R-PAT-10 - Capacity and load limits

Strength-, size-, or weight-based carrying thresholds, encumbrance, and speed limits.

- Reviewed records assigned: 2.
- Contract fit among assigned records: SUPPORTED 0, PARTIAL 2, UNSUPPORTED 0.
- Source codes represented: PHB, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:xphb:carrying-capacity | $.variantrule[24].entries[0] | PARTIAL | R-GAP-17 |
| rule:phb:encumbrance | $.variantrule[99].entries[0]<br>$.variantrule[99].entries[1]<br>$.variantrule[99].entries[2] | PARTIAL | R-GAP-17 |

### R-PAT-11 - Glossary definitions and references

Rules terminology, cross-references, and definitions that clarify other procedures without creating a separate procedure.

- Reviewed records assigned: 18.
- Contract fit among assigned records: SUPPORTED 11, PARTIAL 4, UNSUPPORTED 3.
- Source codes represented: DMG, SCAG, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:xphb:ability-score-and-modifier | $.variantrule[1] | UNSUPPORTED | R-GAP-01 |
| rule:xphb:action | $.variantrule[2].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:adventure | $.variantrule[6].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:alignment | $.variantrule[8].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:armor-class | $.variantrule[12] | UNSUPPORTED | R-GAP-02 |
| rule:dmg:automatic-success | $.variantrule[17] | UNSUPPORTED | R-GAP-03 |
| rule:xphb:campaign | $.variantrule[22].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:character-sheet | $.variantrule[26].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:creature | $.variantrule[35].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:creature-type | $.variantrule[36].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:encounter | $.variantrule[97].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:hazard | $.variantrule[119].entries[0] | PARTIAL | NO_GAP |
| rule:scag:human-languages | $.variantrule[131].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:monster | $.variantrule[154].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:player-character | $.variantrule[169].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:save | $.variantrule[186].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:stat-block | $.variantrule[213].entries[0]<br>$.variantrule[213].entries[1] | PARTIAL | NO_GAP |
| rule:xphb:target | $.variantrule[218].entries[0] | SUPPORTED | NO_GAP |

### R-PAT-12 - Character generation and advancement

Character creation choices, score assignment, level advancement, and optional growth procedures.

- Reviewed records assigned: 18.
- Contract fit among assigned records: SUPPORTED 0, PARTIAL 16, UNSUPPORTED 2.
- Source codes represented: DMG, ESK, PHB, PSI, TCE, VRGR, XGE, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:dmg:alternatives-to-epic-boons | $.variantrule[10].entries[1] | PARTIAL | NO_GAP |
| rule:phb:customizing-ability-scores | $.variantrule[41].entries[0]<br>$.variantrule[41].entries[1]<br>$.variantrule[41].entries[2] | PARTIAL | NO_GAP |
| rule:tce:customizing-your-origin | $.variantrule[42].entries[1]<br>$.variantrule[42].entries[2]<br>$.variantrule[42].entries[3]<br>$.variantrule[42].entries[4] | PARTIAL | NO_GAP |
| rule:dmg:downtime-activity-training-to-gain-levels | $.variantrule[82].entries[0]<br>$.variantrule[82].entries[1] | PARTIAL | R-GAP-13 |
| rule:psi:emrakul-s-madness | $.variantrule[96].entries[0]<br>$.variantrule[96].entries[1] | PARTIAL | R-GAP-26, R-GAP-27 |
| rule:xphb:experience-points | $.variantrule[102].entries[0] | PARTIAL | NO_GAP |
| rule:phb:feats | $.variantrule[111].entries[0]<br>$.variantrule[111].entries[1]<br>$.variantrule[111].entries[2] | PARTIAL | NO_GAP |
| rule:dmg:level-advancement-without-xp | $.variantrule[143].entries[0]<br>$.variantrule[143].entries[2].entries[0]<br>$.variantrule[143].entries[3].entries[0] | PARTIAL | NO_GAP |
| rule:dmg:milestones | $.variantrule[152].entries[0]<br>$.variantrule[152].entries[2] | PARTIAL | NO_GAP |
| rule:phb:multiclassing | $.variantrule[157].entries[0]<br>$.variantrule[157].entries[1]<br>$.variantrule[157].entries[2] | PARTIAL | R-GAP-06, R-GAP-30 |
| rule:dmg:new-ability-scores-honor-and-sanity | $.variantrule[159].entries[0]<br>$.variantrule[159].entries[3]<br>$.variantrule[159].entries[4]<br>$.variantrule[159].entries[5].entries[2]<br>$.variantrule[159].entries[5].entries[3]<br>$.variantrule[159].entries[5].entries[4]<br>$.variantrule[159].entries[6].entries[2]<br>$.variantrule[159].entries[6].entries[3] | UNSUPPORTED | R-GAP-26 |
| rule:tce:optional-class-features | $.variantrule[165].entries[0]<br>$.variantrule[165].entries[1]<br>$.variantrule[165].entries[2] | PARTIAL | NO_GAP |
| rule:dmg:plot-points | $.variantrule[173].entries[1].entries[0]<br>$.variantrule[173].entries[2]<br>$.variantrule[173].entries[3]<br>$.variantrule[173].entries[4] | PARTIAL | NO_GAP |
| rule:dmg:renown | $.variantrule[181].entries[0]<br>$.variantrule[181].entries[1]<br>$.variantrule[181].entries[2].entries[0]<br>$.variantrule[181].entries[2].entries[1]<br>$.variantrule[181].entries[3].entries[0] | PARTIAL | NO_GAP |
| rule:xge:shared-campaign-variant-rules | $.variantrule[190].entries[1]<br>$.variantrule[190].entries[2]<br>$.variantrule[190].entries[3]<br>$.variantrule[190].entries[4] | UNSUPPORTED | R-GAP-33 |
| rule:esk:sidekicks | $.variantrule[196].entries[0]<br>$.variantrule[196].entries[1] | PARTIAL | R-GAP-34 |
| rule:tce:sidekicks | $.variantrule[197].entries[0]<br>$.variantrule[197].entries[1]<br>$.variantrule[197].entries[2] | PARTIAL | R-GAP-34 |
| rule:vrgr:survivors | $.variantrule[215].entries[3]<br>$.variantrule[215].entries[4]<br>$.variantrule[215].entries[5] | PARTIAL | R-GAP-37 |

### R-PAT-13 - Social roles, attitudes, and relationships

Attitudes, social influence, reputation, role relationships, and DM/player adjudication boundaries.

- Reviewed records assigned: 31.
- Contract fit among assigned records: SUPPORTED 5, PARTIAL 26, UNSUPPORTED 0.
- Source codes represented: AAG, AI, DMG, GoS, PHB, XGE, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:xphb:ally | $.variantrule[9].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:attitude | $.variantrule[15].entries[0] | SUPPORTED | NO_GAP |
| rule:phb:downtime-activity-practicing-a-profession | $.variantrule[69].entries[0]<br>$.variantrule[69].entries[1]<br>$.variantrule[69].entries[2] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-religious-service | $.variantrule[72].entries[1].entries[0]<br>$.variantrule[72].entries[2].entries[0]<br>$.variantrule[72].entries[2].entries[1]<br>$.variantrule[72].entries[2].entries[2]<br>$.variantrule[72].entries[3] | PARTIAL | R-GAP-13 |
| rule:dmg:downtime-activity-sowing-rumors | $.variantrule[79].entries[1]<br>$.variantrule[79].entries[2]<br>$.variantrule[79].entries[3] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-explore-territory | $.variantrule[84].entries[0]<br>$.variantrule[84].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-franchise-restructuring | $.variantrule[85].entries[0]<br>$.variantrule[85].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-headquarters-modification | $.variantrule[86].entries[0]<br>$.variantrule[86].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-marketeering | $.variantrule[87].entries[0]<br>$.variantrule[87].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-philanthropic-enterprise | $.variantrule[88].entries[0]<br>$.variantrule[88].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-running-a-franchise | $.variantrule[89].entries[0]<br>$.variantrule[89].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-schmoozing | $.variantrule[90].entries[0]<br>$.variantrule[90].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-scrutineering | $.variantrule[91].entries[0]<br>$.variantrule[91].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-shady-business-practice | $.variantrule[92].entries[0]<br>$.variantrule[92].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-team-building | $.variantrule[93].entries[0]<br>$.variantrule[93].entries[1] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-revisited | $.variantrule[94].entries[0]<br>$.variantrule[94].entries[1] | PARTIAL | R-GAP-13 |
| rule:gos:encounters-at-sea | $.variantrule[98].entries[1] | PARTIAL | R-GAP-31, R-GAP-28 |
| rule:xphb:enemy | $.variantrule[100].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:friendly-attitude | $.variantrule[116].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:hostile-attitude | $.variantrule[129].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:indifferent-attitude | $.variantrule[135].entries[0] | SUPPORTED | NO_GAP |
| rule:dmg:inspiration | $.variantrule[139].entries[0]<br>$.variantrule[139].entries[1].entries[0]<br>$.variantrule[139].entries[1].entries[1]<br>$.variantrule[139].entries[1].entries[2]<br>$.variantrule[139].entries[1].entries[3]<br>$.variantrule[139].entries[1].entries[4]<br>$.variantrule[139].entries[1].entries[5]<br>$.variantrule[139].entries[2]<br>$.variantrule[139].entries[5].entries[3] | PARTIAL | R-GAP-25 |
| rule:dmg:loyalty | $.variantrule[148].entries[1].entries[0]<br>$.variantrule[148].entries[2].entries[1]<br>$.variantrule[148].entries[2].entries[2] | PARTIAL | NO_GAP |
| rule:dmg:morale | $.variantrule[155].entries[1]<br>$.variantrule[155].entries[2]<br>$.variantrule[155].entries[3] | PARTIAL | R-GAP-44 |
| rule:gos:mysterious-islands | $.variantrule[158].entries[1]<br>$.variantrule[158].entries[2]<br>$.variantrule[158].entries[3] | PARTIAL | R-GAP-28 |
| rule:xphb:nonplayer-character | $.variantrule[160].entries[0] | SUPPORTED | NO_GAP |
| rule:gos:officers-and-crew | $.variantrule[164].entries[0]<br>$.variantrule[164].entries[1]<br>$.variantrule[164].entries[2] | PARTIAL | R-GAP-31 |
| rule:gos:random-ships | $.variantrule[178].entries[1]<br>$.variantrule[178].entries[3]<br>$.variantrule[178].entries[4]<br>$.variantrule[178].entries[5] | PARTIAL | R-GAP-31, R-GAP-28 |
| rule:dmg:renown | $.variantrule[181].entries[0]<br>$.variantrule[181].entries[1]<br>$.variantrule[181].entries[2].entries[0]<br>$.variantrule[181].entries[2].entries[1]<br>$.variantrule[181].entries[3].entries[0] | PARTIAL | NO_GAP |
| rule:aag:ship-to-ship-combat | $.variantrule[193].entries[1]<br>$.variantrule[193].entries[2]<br>$.variantrule[193].entries[3] | PARTIAL | R-GAP-31 |
| rule:gos:travel-at-sea | $.variantrule[223].entries[1]<br>$.variantrule[223].entries[2]<br>$.variantrule[223].entries[3].entries[0]<br>$.variantrule[223].entries[3].entries[3]<br>$.variantrule[223].entries[3].entries[4]<br>$.variantrule[223].entries[3].entries[5]<br>$.variantrule[223].entries[3].entries[6]<br>$.variantrule[223].entries[3].entries[7] | PARTIAL | R-GAP-31, R-GAP-28 |

### R-PAT-14 - Light, visibility, and perception

Lighting, obscuration, visibility, perception, and awareness restrictions.

- Reviewed records assigned: 9.
- Contract fit among assigned records: SUPPORTED 3, PARTIAL 6, UNSUPPORTED 0.
- Source codes represented: GoS, VRGR, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:xphb:bright-light | $.variantrule[20].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:cover | $.variantrule[32].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:darkness | $.variantrule[49].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:dim-light | $.variantrule[55].entries[0] | SUPPORTED | NO_GAP |
| rule:vrgr:haunted-traps | $.variantrule[118].entries[0]<br>$.variantrule[118].entries[1]<br>$.variantrule[118].entries[2] | PARTIAL | R-GAP-29 |
| rule:xphb:heavily-obscured | $.variantrule[122].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:lightly-obscured | $.variantrule[144].entries[0] | PARTIAL | NO_GAP |
| rule:gos:ocean-environs | $.variantrule[163].entries[10]<br>$.variantrule[163].entries[11]<br>$.variantrule[163].entries[12]<br>$.variantrule[163].entries[13]<br>$.variantrule[163].entries[1]<br>$.variantrule[163].entries[2]<br>$.variantrule[163].entries[3]<br>$.variantrule[163].entries[4]<br>$.variantrule[163].entries[5]<br>$.variantrule[163].entries[6]<br>$.variantrule[163].entries[7]<br>$.variantrule[163].entries[8]<br>$.variantrule[163].entries[9] | PARTIAL | R-GAP-31 |
| rule:gos:travel-at-sea | $.variantrule[223].entries[1]<br>$.variantrule[223].entries[2]<br>$.variantrule[223].entries[3].entries[0]<br>$.variantrule[223].entries[3].entries[3]<br>$.variantrule[223].entries[3].entries[4]<br>$.variantrule[223].entries[3].entries[5]<br>$.variantrule[223].entries[3].entries[6]<br>$.variantrule[223].entries[3].entries[7] | PARTIAL | R-GAP-31, R-GAP-28 |

### R-PAT-15 - Rest, recovery, and healing

Rest completion, healing, resource recovery, and recovery constraints.

- Reviewed records assigned: 13.
- Contract fit among assigned records: SUPPORTED 2, PARTIAL 11, UNSUPPORTED 0.
- Source codes represented: DMG, VRGR, XGE, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:xphb:dead | $.variantrule[50].entries[0]<br>$.variantrule[50].entries[1] | PARTIAL | NO_GAP |
| rule:vrgr:fear-and-stress | $.variantrule[110].entries[1].entries[0]<br>$.variantrule[110].entries[1].entries[1]<br>$.variantrule[110].entries[1].entries[3].entries[0]<br>$.variantrule[110].entries[2].entries[0] | PARTIAL | R-GAP-16 |
| rule:dmg:healing | $.variantrule[120].entries[1].entries[0]<br>$.variantrule[120].entries[2].entries[1]<br>$.variantrule[120].entries[3].entries[0] | PARTIAL | R-GAP-18 |
| rule:xphb:healing | $.variantrule[121].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:hit-point-dice | $.variantrule[126].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:long-rest | $.variantrule[147].entries[0]<br>$.variantrule[147].entries[1]<br>$.variantrule[147].entries[2].entries[1].items[0]<br>$.variantrule[147].entries[2].entries[1].items[1]<br>$.variantrule[147].entries[2].entries[1].items[2]<br>$.variantrule[147].entries[2].entries[1].items[3]<br>$.variantrule[147].entries[2].entries[3]<br>$.variantrule[147].entries[2].entries[4] | PARTIAL | R-GAP-18 |
| rule:dmg:milestones | $.variantrule[152].entries[0]<br>$.variantrule[152].entries[2] | PARTIAL | NO_GAP |
| rule:xphb:per-day | $.variantrule[167].entries[0] | PARTIAL | NO_GAP |
| rule:dmg:planar-effects | $.variantrule[168].entries[10]<br>$.variantrule[168].entries[11]<br>$.variantrule[168].entries[12]<br>$.variantrule[168].entries[13]<br>$.variantrule[168].entries[14]<br>$.variantrule[168].entries[15]<br>$.variantrule[168].entries[16]<br>$.variantrule[168].entries[17]<br>$.variantrule[168].entries[18]<br>$.variantrule[168].entries[19]<br>$.variantrule[168].entries[1]<br>$.variantrule[168].entries[20]<br>$.variantrule[168].entries[21]<br>$.variantrule[168].entries[2]<br>$.variantrule[168].entries[3]<br>$.variantrule[168].entries[4]<br>$.variantrule[168].entries[5]<br>$.variantrule[168].entries[6]<br>$.variantrule[168].entries[7]<br>$.variantrule[168].entries[8]<br>$.variantrule[168].entries[9] | PARTIAL | R-GAP-18, R-GAP-24, R-GAP-32 |
| rule:dmg:rest-variants | $.variantrule[183].entries[1]<br>$.variantrule[183].entries[2] | PARTIAL | R-GAP-18 |
| rule:xphb:short-rest | $.variantrule[195].entries[0]<br>$.variantrule[195].entries[2].entries[0].items[0].entries[0]<br>$.variantrule[195].entries[2].entries[0].items[1].entries[0]<br>$.variantrule[195].entries[4] | PARTIAL | R-GAP-18 |
| rule:xge:sleep | $.variantrule[204].entries[0]<br>$.variantrule[204].entries[1].entries[0]<br>$.variantrule[204].entries[2].entries[0]<br>$.variantrule[204].entries[3].entries[0] | PARTIAL | R-GAP-18 |
| rule:xge:tool-proficiencies | $.variantrule[222].entries[2].entries[10]<br>$.variantrule[222].entries[2].entries[11]<br>$.variantrule[222].entries[2].entries[12]<br>$.variantrule[222].entries[2].entries[13]<br>$.variantrule[222].entries[2].entries[14]<br>$.variantrule[222].entries[2].entries[15]<br>$.variantrule[222].entries[2].entries[16]<br>$.variantrule[222].entries[2].entries[17]<br>$.variantrule[222].entries[2].entries[18]<br>$.variantrule[222].entries[2].entries[19]<br>$.variantrule[222].entries[2].entries[1]<br>$.variantrule[222].entries[2].entries[20]<br>$.variantrule[222].entries[2].entries[21]<br>$.variantrule[222].entries[2].entries[22]<br>$.variantrule[222].entries[2].entries[23]<br>$.variantrule[222].entries[2].entries[24]<br>$.variantrule[222].entries[2].entries[25]<br>$.variantrule[222].entries[2].entries[26]<br>$.variantrule[222].entries[2].entries[27]<br>$.variantrule[222].entries[2].entries[2]<br>$.variantrule[222].entries[2].entries[3]<br>$.variantrule[222].entries[2].entries[4]<br>$.variantrule[222].entries[2].entries[5]<br>$.variantrule[222].entries[2].entries[6]<br>$.variantrule[222].entries[2].entries[7]<br>$.variantrule[222].entries[2].entries[8]<br>$.variantrule[222].entries[2].entries[9] | PARTIAL | R-GAP-06, R-GAP-38 |

### R-PAT-16 - Damage and attack outcomes

Attack consequences, damage, damage mitigation, and state changes caused by damage.

- Reviewed records assigned: 35.
- Contract fit among assigned records: SUPPORTED 9, PARTIAL 25, UNSUPPORTED 1.
- Source codes represented: AAG, DMG, GoS, MM, XGE, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:dmg:action-options | $.variantrule[3].entries[0]<br>$.variantrule[3].entries[1].entries[0]<br>$.variantrule[3].entries[2].entries[0]<br>$.variantrule[3].entries[3].entries[0]<br>$.variantrule[3].entries[4].entries[0]<br>$.variantrule[3].entries[5].entries[0]<br>$.variantrule[3].entries[6].entries[0] | PARTIAL | NO_GAP |
| rule:xge:adamantine-weapons | $.variantrule[4].entries[1] | PARTIAL | NO_GAP |
| rule:xphb:breaking-objects | $.variantrule[19].entries[0]<br>$.variantrule[19].entries[2]<br>$.variantrule[19].entries[4]<br>$.variantrule[19].entries[5].entries[0].entries[0]<br>$.variantrule[19].entries[5].entries[1].entries[0]<br>$.variantrule[19].entries[5].entries[2].entries[0] | PARTIAL | R-GAP-39 |
| rule:dmg:cleaving-through-creatures | $.variantrule[27].entries[1] | PARTIAL | R-GAP-40 |
| rule:aag:crashing | $.variantrule[33].entries[0]<br>$.variantrule[33].entries[1]<br>$.variantrule[33].entries[2]<br>$.variantrule[33].entries[3] | PARTIAL | R-GAP-31 |
| rule:xphb:damage | $.variantrule[45].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:damage-roll | $.variantrule[46].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:damage-threshold | $.variantrule[47].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:damage-types | $.variantrule[48].entries[0] | SUPPORTED | NO_GAP |
| rule:dmg:explosives | $.variantrule[104].entries[1].entries[0]<br>$.variantrule[104].entries[2].entries[1]<br>$.variantrule[104].entries[3].entries[0]<br>$.variantrule[104].entries[3].entries[1]<br>$.variantrule[104].entries[4].entries[0] | PARTIAL | NO_GAP |
| rule:xge:falling | $.variantrule[106].entries[0]<br>$.variantrule[106].entries[1].entries[1]<br>$.variantrule[106].entries[2].entries[0]<br>$.variantrule[106].entries[2].entries[1]<br>$.variantrule[106].entries[2].entries[2] | PARTIAL | R-GAP-15 |
| rule:dmg:firearms | $.variantrule[112].entries[1].entries[0]<br>$.variantrule[112].entries[2].entries[1].entries[0]<br>$.variantrule[112].entries[2].entries[2].entries[0] | PARTIAL | NO_GAP |
| rule:dmg:healing | $.variantrule[120].entries[1].entries[0]<br>$.variantrule[120].entries[2].entries[1]<br>$.variantrule[120].entries[3].entries[0] | PARTIAL | R-GAP-18 |
| rule:xphb:hit-points | $.variantrule[127].entries[0] | SUPPORTED | NO_GAP |
| rule:dmg:hitting-cover | $.variantrule[128] | UNSUPPORTED | R-GAP-08 |
| rule:xphb:immunity | $.variantrule[133].entries[0] | SUPPORTED | NO_GAP |
| rule:xphb:improvised-weapons | $.variantrule[134].entries[0]<br>$.variantrule[134].entries[1].entries[0].entries[0]<br>$.variantrule[134].entries[1].entries[1].entries[0]<br>$.variantrule[134].entries[1].entries[2].entries[0]<br>$.variantrule[134].entries[1].entries[3].entries[0] | PARTIAL | NO_GAP |
| rule:dmg:injuries | $.variantrule[138].entries[1]<br>$.variantrule[138].entries[3]<br>$.variantrule[138].entries[4].rows[0]<br>$.variantrule[138].entries[4].rows[1]<br>$.variantrule[138].entries[4].rows[2]<br>$.variantrule[138].entries[4].rows[3]<br>$.variantrule[138].entries[4].rows[4]<br>$.variantrule[138].entries[4].rows[5]<br>$.variantrule[138].entries[4].rows[6]<br>$.variantrule[138].entries[4].rows[7]<br>$.variantrule[138].entries[4].rows[8]<br>$.variantrule[138].entries[5] | PARTIAL | R-GAP-23 |
| rule:xphb:knocking-out-a-creature | $.variantrule[142].entries[0]<br>$.variantrule[142].entries[1] | PARTIAL | NO_GAP |
| rule:dmg:massive-damage | $.variantrule[151].entries[1]<br>$.variantrule[151].entries[2] | PARTIAL | R-GAP-42 |
| rule:dmg:mixing-potions | $.variantrule[153].entries[1]<br>$.variantrule[153].entries[2] | PARTIAL | R-GAP-43 |
| rule:gos:ocean-environs | $.variantrule[163].entries[10]<br>$.variantrule[163].entries[11]<br>$.variantrule[163].entries[12]<br>$.variantrule[163].entries[13]<br>$.variantrule[163].entries[1]<br>$.variantrule[163].entries[2]<br>$.variantrule[163].entries[3]<br>$.variantrule[163].entries[4]<br>$.variantrule[163].entries[5]<br>$.variantrule[163].entries[6]<br>$.variantrule[163].entries[7]<br>$.variantrule[163].entries[8]<br>$.variantrule[163].entries[9] | PARTIAL | R-GAP-31 |
| rule:mm:player-characters-as-lycanthropes | $.variantrule[170].entries[0]<br>$.variantrule[170].entries[1]<br>$.variantrule[170].entries[3].entries[0]<br>$.variantrule[170].entries[3].entries[1]<br>$.variantrule[170].entries[3].entries[2]<br>$.variantrule[170].entries[3].entries[5].entries[0]<br>$.variantrule[170].entries[3].entries[6].entries[0]<br>$.variantrule[170].entries[3].entries[7].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:resistance | $.variantrule[182].entries[0] | PARTIAL | R-GAP-20 |
| rule:aag:ship-repairs | $.variantrule[191].entries[0]<br>$.variantrule[191].entries[1] | PARTIAL | R-GAP-31 |
| rule:gos:ship-stat-blocks | $.variantrule[192].entries[1]<br>$.variantrule[192].entries[2]<br>$.variantrule[192].entries[3]<br>$.variantrule[192].entries[4] | PARTIAL | R-GAP-31 |
| rule:aag:ship-to-ship-combat | $.variantrule[193].entries[1]<br>$.variantrule[193].entries[2]<br>$.variantrule[193].entries[3] | PARTIAL | R-GAP-31 |
| rule:gos:ships-in-combat | $.variantrule[194].entries[1]<br>$.variantrule[194].entries[2]<br>$.variantrule[194].entries[3] | PARTIAL | R-GAP-31 |
| rule:xphb:stable | $.variantrule[212].entries[0] | SUPPORTED | NO_GAP |
| rule:gos:superior-ship-upgrades | $.variantrule[214].entries[0]<br>$.variantrule[214].entries[1]<br>$.variantrule[214].entries[2] | PARTIAL | R-GAP-31 |
| rule:xphb:temporary-hit-points | $.variantrule[221].entries[0] | SUPPORTED | NO_GAP |
| rule:xge:tool-proficiencies | $.variantrule[222].entries[2].entries[10]<br>$.variantrule[222].entries[2].entries[11]<br>$.variantrule[222].entries[2].entries[12]<br>$.variantrule[222].entries[2].entries[13]<br>$.variantrule[222].entries[2].entries[14]<br>$.variantrule[222].entries[2].entries[15]<br>$.variantrule[222].entries[2].entries[16]<br>$.variantrule[222].entries[2].entries[17]<br>$.variantrule[222].entries[2].entries[18]<br>$.variantrule[222].entries[2].entries[19]<br>$.variantrule[222].entries[2].entries[1]<br>$.variantrule[222].entries[2].entries[20]<br>$.variantrule[222].entries[2].entries[21]<br>$.variantrule[222].entries[2].entries[22]<br>$.variantrule[222].entries[2].entries[23]<br>$.variantrule[222].entries[2].entries[24]<br>$.variantrule[222].entries[2].entries[25]<br>$.variantrule[222].entries[2].entries[26]<br>$.variantrule[222].entries[2].entries[27]<br>$.variantrule[222].entries[2].entries[2]<br>$.variantrule[222].entries[2].entries[3]<br>$.variantrule[222].entries[2].entries[4]<br>$.variantrule[222].entries[2].entries[5]<br>$.variantrule[222].entries[2].entries[6]<br>$.variantrule[222].entries[2].entries[7]<br>$.variantrule[222].entries[2].entries[8]<br>$.variantrule[222].entries[2].entries[9] | PARTIAL | R-GAP-06, R-GAP-38 |
| rule:gos:travel-at-sea | $.variantrule[223].entries[1]<br>$.variantrule[223].entries[2]<br>$.variantrule[223].entries[3].entries[0]<br>$.variantrule[223].entries[3].entries[3]<br>$.variantrule[223].entries[3].entries[4]<br>$.variantrule[223].entries[3].entries[5]<br>$.variantrule[223].entries[3].entries[6]<br>$.variantrule[223].entries[3].entries[7] | PARTIAL | R-GAP-31, R-GAP-28 |
| rule:xphb:vulnerability | $.variantrule[227].entries[0] | PARTIAL | R-GAP-20 |
| rule:xphb:weapon-attack | $.variantrule[230].entries[0] | SUPPORTED | NO_GAP |

### R-PAT-17 - Spell and magic procedures

Spellcasting, magical effects, spell components, and magic-specific procedures.

- Reviewed records assigned: 33.
- Contract fit among assigned records: SUPPORTED 2, PARTIAL 31, UNSUPPORTED 0.
- Source codes represented: AAG, AI, DMG, ESK, GoS, MM, PHB, TCE, VGM, VRGR, XGE, XPHB.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:xphb:armor-training | $.variantrule[13].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:cantrip | $.variantrule[23].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:curses | $.variantrule[40].entries[0] | PARTIAL | NO_GAP |
| rule:ai:downtime-and-franchise-activity-team-building | $.variantrule[93].entries[0]<br>$.variantrule[93].entries[1] | PARTIAL | R-GAP-13 |
| rule:dmg:explosives | $.variantrule[104].entries[1].entries[0]<br>$.variantrule[104].entries[2].entries[1]<br>$.variantrule[104].entries[3].entries[0]<br>$.variantrule[104].entries[3].entries[1]<br>$.variantrule[104].entries[4].entries[0] | PARTIAL | NO_GAP |
| rule:mm:familiars | $.variantrule[107].entries[0]<br>$.variantrule[107].entries[1] | PARTIAL | NO_GAP |
| rule:vgm:familiars | $.variantrule[108].entries[0]<br>$.variantrule[108].entries[1] | PARTIAL | NO_GAP |
| rule:vrgr:haunted-traps | $.variantrule[118].entries[0]<br>$.variantrule[118].entries[1]<br>$.variantrule[118].entries[2] | PARTIAL | R-GAP-29 |
| rule:xphb:illusions | $.variantrule[132].entries[0]<br>$.variantrule[132].entries[1] | PARTIAL | NO_GAP |
| rule:dmg:madness | $.variantrule[149].entries[1].entries[0]<br>$.variantrule[149].entries[2].entries[1]<br>$.variantrule[149].entries[2].entries[4]<br>$.variantrule[149].entries[2].entries[5]<br>$.variantrule[149].entries[2].entries[6]<br>$.variantrule[149].entries[3] | PARTIAL | R-GAP-24 |
| rule:xphb:magical-effect | $.variantrule[150].entries[0] | SUPPORTED | NO_GAP |
| rule:dmg:mixing-potions | $.variantrule[153].entries[1]<br>$.variantrule[153].entries[2] | PARTIAL | R-GAP-43 |
| rule:dmg:more-difficult-identification | $.variantrule[156].entries[0] | PARTIAL | NO_GAP |
| rule:phb:multiclassing | $.variantrule[157].entries[0]<br>$.variantrule[157].entries[1]<br>$.variantrule[157].entries[2] | PARTIAL | R-GAP-06, R-GAP-30 |
| rule:gos:ocean-environs | $.variantrule[163].entries[10]<br>$.variantrule[163].entries[11]<br>$.variantrule[163].entries[12]<br>$.variantrule[163].entries[13]<br>$.variantrule[163].entries[1]<br>$.variantrule[163].entries[2]<br>$.variantrule[163].entries[3]<br>$.variantrule[163].entries[4]<br>$.variantrule[163].entries[5]<br>$.variantrule[163].entries[6]<br>$.variantrule[163].entries[7]<br>$.variantrule[163].entries[8]<br>$.variantrule[163].entries[9] | PARTIAL | R-GAP-31 |
| rule:dmg:planar-effects | $.variantrule[168].entries[10]<br>$.variantrule[168].entries[11]<br>$.variantrule[168].entries[12]<br>$.variantrule[168].entries[13]<br>$.variantrule[168].entries[14]<br>$.variantrule[168].entries[15]<br>$.variantrule[168].entries[16]<br>$.variantrule[168].entries[17]<br>$.variantrule[168].entries[18]<br>$.variantrule[168].entries[19]<br>$.variantrule[168].entries[1]<br>$.variantrule[168].entries[20]<br>$.variantrule[168].entries[21]<br>$.variantrule[168].entries[2]<br>$.variantrule[168].entries[3]<br>$.variantrule[168].entries[4]<br>$.variantrule[168].entries[5]<br>$.variantrule[168].entries[6]<br>$.variantrule[168].entries[7]<br>$.variantrule[168].entries[8]<br>$.variantrule[168].entries[9] | PARTIAL | R-GAP-18, R-GAP-24, R-GAP-32 |
| rule:mm:player-characters-as-vampires | $.variantrule[171].entries[0]<br>$.variantrule[171].entries[1].entries[0] | PARTIAL | NO_GAP |
| rule:xphb:possession | $.variantrule[174].entries[0] | PARTIAL | NO_GAP |
| rule:dmg:rest-variants | $.variantrule[183].entries[1]<br>$.variantrule[183].entries[2] | PARTIAL | R-GAP-18 |
| rule:xphb:ritual | $.variantrule[184].entries[0] | PARTIAL | NO_GAP |
| rule:dmg:scroll-mishaps | $.variantrule[188].entries[0]<br>$.variantrule[188].entries[1] | PARTIAL | NO_GAP |
| rule:xphb:shape-shifting | $.variantrule[189].entries[0] | PARTIAL | NO_GAP |
| rule:aag:ship-repairs | $.variantrule[191].entries[0]<br>$.variantrule[191].entries[1] | PARTIAL | R-GAP-31 |
| rule:esk:sidekicks | $.variantrule[196].entries[0]<br>$.variantrule[196].entries[1] | PARTIAL | R-GAP-34 |
| rule:tce:sidekicks | $.variantrule[197].entries[0]<br>$.variantrule[197].entries[1]<br>$.variantrule[197].entries[2] | PARTIAL | R-GAP-34 |
| rule:xphb:spell | $.variantrule[206].entries[0] | SUPPORTED | NO_GAP |
| rule:dmg:spell-points | $.variantrule[208].entries[0]<br>$.variantrule[208].entries[1]<br>$.variantrule[208].entries[2]<br>$.variantrule[208].entries[3]<br>$.variantrule[208].entries[4]<br>$.variantrule[208].entries[5]<br>$.variantrule[208].entries[7]<br>$.variantrule[208].entries[8] | PARTIAL | NO_GAP |
| rule:xge:spellcasting | $.variantrule[209].entries[0]<br>$.variantrule[209].entries[1]<br>$.variantrule[209].entries[2]<br>$.variantrule[209].entries[3] | PARTIAL | R-GAP-35, R-GAP-36 |
| rule:xphb:spellcasting-focus | $.variantrule[210].entries[0] | PARTIAL | NO_GAP |
| rule:gos:superior-ship-upgrades | $.variantrule[214].entries[0]<br>$.variantrule[214].entries[1]<br>$.variantrule[214].entries[2] | PARTIAL | R-GAP-31 |
| rule:vrgr:survivors | $.variantrule[215].entries[3]<br>$.variantrule[215].entries[4]<br>$.variantrule[215].entries[5] | PARTIAL | R-GAP-37 |
| rule:xphb:telepathy | $.variantrule[219].entries[0]<br>$.variantrule[219].entries[1]<br>$.variantrule[219].entries[2] | PARTIAL | NO_GAP |
| rule:dmg:wands-that-don-t-recharge | $.variantrule[228].entries[0] | PARTIAL | NO_GAP |

### R-PAT-18 - Downtime procedures

Activities resolved across downtime with time, resources, checks, branches, or campaign outcomes.

- Reviewed records assigned: 39.
- Contract fit among assigned records: SUPPORTED 0, PARTIAL 39, UNSUPPORTED 0.
- Source codes represented: AI, DMG, PHB, XGE.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:dmg:downtime-activity-building-a-stronghold | $.variantrule[57].entries[0]<br>$.variantrule[57].entries[1]<br>$.variantrule[57].entries[2] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-buying-a-magic-item | $.variantrule[58].entries[1].entries[0]<br>$.variantrule[58].entries[2].entries[0]<br>$.variantrule[58].entries[2].entries[1]<br>$.variantrule[58].entries[3]<br>$.variantrule[58].entries[4]<br>$.variantrule[58].entries[5] | PARTIAL | R-GAP-13 |
| rule:dmg:downtime-activity-carousing | $.variantrule[59].entries[0]<br>$.variantrule[59].entries[1]<br>$.variantrule[59].entries[2] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-carousing | $.variantrule[60].entries[1].entries[0]<br>$.variantrule[60].entries[2].entries[0]<br>$.variantrule[60].entries[2].entries[1]<br>$.variantrule[60].entries[2].entries[2]<br>$.variantrule[60].entries[3] | PARTIAL | R-GAP-13 |
| rule:phb:downtime-activity-crafting | $.variantrule[61].entries[0]<br>$.variantrule[61].entries[1]<br>$.variantrule[61].entries[2]<br>$.variantrule[61].entries[3] | PARTIAL | R-GAP-13 |
| rule:dmg:downtime-activity-crafting-a-magic-item | $.variantrule[62] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-crafting-an-item | $.variantrule[63].entries[1].entries[0]<br>$.variantrule[63].entries[1].entries[1]<br>$.variantrule[63].entries[1].entries[2]<br>$.variantrule[63].entries[1].entries[4]<br>$.variantrule[63].entries[2]<br>$.variantrule[63].entries[3]<br>$.variantrule[63].entries[4] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-crime | $.variantrule[64].entries[1].entries[0]<br>$.variantrule[64].entries[2].entries[0]<br>$.variantrule[64].entries[2].entries[1]<br>$.variantrule[64].entries[2].entries[2]<br>$.variantrule[64].entries[3] | PARTIAL | R-GAP-13 |
| rule:dmg:downtime-activity-gaining-renown | $.variantrule[65].entries[0] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-gambling | $.variantrule[66].entries[1].entries[0]<br>$.variantrule[66].entries[2].entries[0]<br>$.variantrule[66].entries[2].entries[1]<br>$.variantrule[66].entries[3] | PARTIAL | R-GAP-13 |
| rule:dmg:downtime-activity-performing-sacred-rites | $.variantrule[67].entries[2] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-pit-fighting | $.variantrule[68].entries[1].entries[0]<br>$.variantrule[68].entries[2].entries[0]<br>$.variantrule[68].entries[3] | PARTIAL | R-GAP-13 |
| rule:phb:downtime-activity-practicing-a-profession | $.variantrule[69].entries[0]<br>$.variantrule[69].entries[1]<br>$.variantrule[69].entries[2] | PARTIAL | R-GAP-13 |
| rule:phb:downtime-activity-recuperating | $.variantrule[70].entries[0]<br>$.variantrule[70].entries[1]<br>$.variantrule[70].entries[2].items[0]<br>$.variantrule[70].entries[2].items[1] | PARTIAL | R-GAP-13, R-GAP-18 |
| rule:xge:downtime-activity-relaxation | $.variantrule[71].entries[1].entries[0]<br>$.variantrule[71].entries[2].entries[0]<br>$.variantrule[71].entries[3] | PARTIAL | R-GAP-13, R-GAP-18 |
| rule:xge:downtime-activity-religious-service | $.variantrule[72].entries[1].entries[0]<br>$.variantrule[72].entries[2].entries[0]<br>$.variantrule[72].entries[2].entries[1]<br>$.variantrule[72].entries[2].entries[2]<br>$.variantrule[72].entries[3] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-research | $.variantrule[73].entries[1].entries[0]<br>$.variantrule[73].entries[2].entries[0]<br>$.variantrule[73].entries[2].entries[1]<br>$.variantrule[73].entries[3] | PARTIAL | R-GAP-13 |
| rule:phb:downtime-activity-researching | $.variantrule[74].entries[1]<br>$.variantrule[74].entries[2] | PARTIAL | R-GAP-13 |
| rule:dmg:downtime-activity-running-a-business | $.variantrule[75].entries[1]<br>$.variantrule[75].entries[2]<br>$.variantrule[75].entries[3] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-scribing-a-spell-scroll | $.variantrule[76].entries[1].entries[0]<br>$.variantrule[76].entries[1].entries[1]<br>$.variantrule[76].entries[2] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-selling-a-magic-item | $.variantrule[77].entries[1].entries[0]<br>$.variantrule[77].entries[2].entries[0]<br>$.variantrule[77].entries[3] | PARTIAL | R-GAP-13 |
| rule:dmg:downtime-activity-selling-magic-items | $.variantrule[78].entries[1]<br>$.variantrule[78].entries[2]<br>$.variantrule[78].entries[3]<br>$.variantrule[78].entries[4]<br>$.variantrule[78].entries[5] | PARTIAL | R-GAP-13 |
| rule:dmg:downtime-activity-sowing-rumors | $.variantrule[79].entries[1]<br>$.variantrule[79].entries[2]<br>$.variantrule[79].entries[3] | PARTIAL | R-GAP-13 |
| rule:phb:downtime-activity-training | $.variantrule[80].entries[0]<br>$.variantrule[80].entries[1]<br>$.variantrule[80].entries[2] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-training | $.variantrule[81].entries[0]<br>$.variantrule[81].entries[1].entries[0]<br>$.variantrule[81].entries[2].entries[0] | PARTIAL | R-GAP-13 |
| rule:dmg:downtime-activity-training-to-gain-levels | $.variantrule[82].entries[0]<br>$.variantrule[82].entries[1] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-activity-work | $.variantrule[83].entries[1].entries[0]<br>$.variantrule[83].entries[2].entries[0]<br>$.variantrule[83].entries[2].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-explore-territory | $.variantrule[84].entries[0]<br>$.variantrule[84].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-franchise-restructuring | $.variantrule[85].entries[0]<br>$.variantrule[85].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-headquarters-modification | $.variantrule[86].entries[0]<br>$.variantrule[86].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-marketeering | $.variantrule[87].entries[0]<br>$.variantrule[87].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-philanthropic-enterprise | $.variantrule[88].entries[0]<br>$.variantrule[88].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-running-a-franchise | $.variantrule[89].entries[0]<br>$.variantrule[89].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-schmoozing | $.variantrule[90].entries[0]<br>$.variantrule[90].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-scrutineering | $.variantrule[91].entries[0]<br>$.variantrule[91].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-shady-business-practice | $.variantrule[92].entries[0]<br>$.variantrule[92].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-team-building | $.variantrule[93].entries[0]<br>$.variantrule[93].entries[1] | PARTIAL | R-GAP-13 |
| rule:xge:downtime-revisited | $.variantrule[94].entries[0]<br>$.variantrule[94].entries[1] | PARTIAL | R-GAP-13 |
| rule:dmg:renown | $.variantrule[181].entries[0]<br>$.variantrule[181].entries[1]<br>$.variantrule[181].entries[2].entries[0]<br>$.variantrule[181].entries[2].entries[1]<br>$.variantrule[181].entries[3].entries[0] | PARTIAL | NO_GAP |

### R-PAT-19 - Table-selected state outcomes

A bounded roll selects one of several conditions, restrictions, modifiers, behavioral outcomes, durations, or recovery paths. The selected outcome can define a continuing state and its own triggers, unlike a simple check-result band.

- Reviewed records assigned: 7.
- Contract fit among assigned records: SUPPORTED 0, PARTIAL 6, UNSUPPORTED 1.
- Source codes represented: DMG, GoS, PSI, XGE.
- Counts are distinct records within this pattern; records may appear in multiple families.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:psi:emrakul-s-madness | $.variantrule[96].entries[0]<br>$.variantrule[96].entries[1] | PARTIAL | R-GAP-26, R-GAP-27 |
| rule:gos:encounters-at-sea | $.variantrule[98].entries[1] | PARTIAL | R-GAP-31, R-GAP-28 |
| rule:dmg:madness | $.variantrule[149].entries[1].entries[0]<br>$.variantrule[149].entries[2].entries[1]<br>$.variantrule[149].entries[2].entries[4]<br>$.variantrule[149].entries[2].entries[5]<br>$.variantrule[149].entries[2].entries[6]<br>$.variantrule[149].entries[3] | PARTIAL | R-GAP-24 |
| rule:gos:mysterious-islands | $.variantrule[158].entries[1]<br>$.variantrule[158].entries[2]<br>$.variantrule[158].entries[3] | PARTIAL | R-GAP-28 |
| rule:dmg:planar-effects | $.variantrule[168].entries[10]<br>$.variantrule[168].entries[11]<br>$.variantrule[168].entries[12]<br>$.variantrule[168].entries[13]<br>$.variantrule[168].entries[14]<br>$.variantrule[168].entries[15]<br>$.variantrule[168].entries[16]<br>$.variantrule[168].entries[17]<br>$.variantrule[168].entries[18]<br>$.variantrule[168].entries[19]<br>$.variantrule[168].entries[1]<br>$.variantrule[168].entries[20]<br>$.variantrule[168].entries[21]<br>$.variantrule[168].entries[2]<br>$.variantrule[168].entries[3]<br>$.variantrule[168].entries[4]<br>$.variantrule[168].entries[5]<br>$.variantrule[168].entries[6]<br>$.variantrule[168].entries[7]<br>$.variantrule[168].entries[8]<br>$.variantrule[168].entries[9] | PARTIAL | R-GAP-18, R-GAP-24, R-GAP-32 |
| rule:gos:random-ships | $.variantrule[178].entries[1]<br>$.variantrule[178].entries[3]<br>$.variantrule[178].entries[4]<br>$.variantrule[178].entries[5] | PARTIAL | R-GAP-31, R-GAP-28 |
| rule:xge:shared-campaign-variant-rules | $.variantrule[190].entries[1]<br>$.variantrule[190].entries[2]<br>$.variantrule[190].entries[3]<br>$.variantrule[190].entries[4] | UNSUPPORTED | R-GAP-33 |

### R-PAT-20 - Role-based group-check aggregation

Several named participants roll assigned checks, a group result is determined from the number of successes, and total success/failure may have distinct consequences. This is distinct from repeated checks by one actor.

- Reviewed records assigned: 4.
- Contract fit among assigned records: SUPPORTED 0, PARTIAL 4, UNSUPPORTED 0.
- Source codes represented: GoS.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:gos:ocean-environs | $.variantrule[163].entries[10]<br>$.variantrule[163].entries[11]<br>$.variantrule[163].entries[12]<br>$.variantrule[163].entries[13]<br>$.variantrule[163].entries[1]<br>$.variantrule[163].entries[2]<br>$.variantrule[163].entries[3]<br>$.variantrule[163].entries[4]<br>$.variantrule[163].entries[5]<br>$.variantrule[163].entries[6]<br>$.variantrule[163].entries[7]<br>$.variantrule[163].entries[8]<br>$.variantrule[163].entries[9] | PARTIAL | R-GAP-31 |
| rule:gos:officers-and-crew | $.variantrule[164].entries[0]<br>$.variantrule[164].entries[1]<br>$.variantrule[164].entries[2] | PARTIAL | R-GAP-31 |
| rule:gos:ships-in-combat | $.variantrule[194].entries[1]<br>$.variantrule[194].entries[2]<br>$.variantrule[194].entries[3] | PARTIAL | R-GAP-31 |
| rule:gos:travel-at-sea | $.variantrule[223].entries[1]<br>$.variantrule[223].entries[2]<br>$.variantrule[223].entries[3].entries[0]<br>$.variantrule[223].entries[3].entries[3]<br>$.variantrule[223].entries[3].entries[4]<br>$.variantrule[223].entries[3].entries[5]<br>$.variantrule[223].entries[3].entries[6]<br>$.variantrule[223].entries[3].entries[7] | PARTIAL | R-GAP-31, R-GAP-28 |

### R-PAT-21 - Table-driven generation and branching

A roll or DM selection chooses a result from a table, which may select later tables or generate an encounter, location, state, or procedure. Similar table formatting alone does not establish the same mechanics.

- Reviewed records assigned: 8.
- Contract fit among assigned records: SUPPORTED 0, PARTIAL 8, UNSUPPORTED 0.
- Source codes represented: AI, GoS, VRGR.

| Source-qualified identity | Exact observed source paths | Contract fit | Gap families |
| --- | --- | --- | --- |
| rule:ai:downtime-and-franchise-activity-explore-territory | $.variantrule[84].entries[0]<br>$.variantrule[84].entries[1] | PARTIAL | R-GAP-13 |
| rule:ai:downtime-and-franchise-activity-running-a-franchise | $.variantrule[89].entries[0]<br>$.variantrule[89].entries[1] | PARTIAL | R-GAP-13 |
| rule:gos:encounters-at-sea | $.variantrule[98].entries[1] | PARTIAL | R-GAP-31, R-GAP-28 |
| rule:vrgr:haunted-traps | $.variantrule[118].entries[0]<br>$.variantrule[118].entries[1]<br>$.variantrule[118].entries[2] | PARTIAL | R-GAP-29 |
| rule:gos:mysterious-islands | $.variantrule[158].entries[1]<br>$.variantrule[158].entries[2]<br>$.variantrule[158].entries[3] | PARTIAL | R-GAP-28 |
| rule:gos:ocean-environs | $.variantrule[163].entries[10]<br>$.variantrule[163].entries[11]<br>$.variantrule[163].entries[12]<br>$.variantrule[163].entries[13]<br>$.variantrule[163].entries[1]<br>$.variantrule[163].entries[2]<br>$.variantrule[163].entries[3]<br>$.variantrule[163].entries[4]<br>$.variantrule[163].entries[5]<br>$.variantrule[163].entries[6]<br>$.variantrule[163].entries[7]<br>$.variantrule[163].entries[8]<br>$.variantrule[163].entries[9] | PARTIAL | R-GAP-31 |
| rule:gos:random-ships | $.variantrule[178].entries[1]<br>$.variantrule[178].entries[3]<br>$.variantrule[178].entries[4]<br>$.variantrule[178].entries[5] | PARTIAL | R-GAP-31, R-GAP-28 |
| rule:gos:travel-at-sea | $.variantrule[223].entries[1]<br>$.variantrule[223].entries[2]<br>$.variantrule[223].entries[3].entries[0]<br>$.variantrule[223].entries[3].entries[3]<br>$.variantrule[223].entries[3].entries[4]<br>$.variantrule[223].entries[3].entries[5]<br>$.variantrule[223].entries[3].entries[6]<br>$.variantrule[223].entries[3].entries[7] | PARTIAL | R-GAP-31, R-GAP-28 |

