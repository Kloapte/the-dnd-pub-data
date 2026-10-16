# Background Edition Analysis

The source marks 70 definitions with `edition: "one"`; this includes the 16 XPHB backgrounds and backgrounds explicitly using the newer rules presentation from other sources. The remaining 101 omit the field. Edition is preserved as source data and is not inferred from source publication dates.

There are 20 PHB roots and 16 XPHB roots. All 16 XPHB records contain ability score alternatives (`choose.weighted`), feat grants, skill proficiencies, tool proficiencies, and A/B equipment packages with explicit monetary values. None contains a `languageProficiencies` root field. PHB has no `ability` or `feats` root fields; it has skill proficiency on 15 records, tool proficiency on 12, language proficiency on 7, and starting equipment on 13. Other sources mix these patterns; therefore edition is a nullable property and each grant component is independently optional in one unified model. PHB and XPHB versions with equal names retain separate IDs and identities.

No edition mechanics are synthesized. The `edition` value is source authored and preserved exactly.
