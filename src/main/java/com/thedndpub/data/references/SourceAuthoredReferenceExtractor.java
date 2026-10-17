package com.thedndpub.data.references;

import com.thedndpub.data.dto.background.BackgroundDefinition;
import com.thedndpub.data.dto.classes.ClassDefinition;
import com.thedndpub.data.dto.classes.ClassChoiceProgression;
import com.thedndpub.data.dto.classes.ClassEquipmentGroup;
import com.thedndpub.data.dto.classes.ClassExternalFeatureNode;
import com.thedndpub.data.dto.classes.ClassExternalKind;
import com.thedndpub.data.dto.classes.ClassExternalReference;
import com.thedndpub.data.dto.classes.ClassFeatureChoice;
import com.thedndpub.data.dto.classes.ClassFeatureNode;
import com.thedndpub.data.dto.classes.ClassFeatureSection;
import com.thedndpub.data.dto.classes.ClassEquipmentOption;
import com.thedndpub.data.dto.classes.ClassSkillChoice;
import com.thedndpub.data.dto.classes.ClassSpellReference;
import com.thedndpub.data.dto.classes.ClassSpellSelection;
import com.thedndpub.data.dto.classes.ClassSpellSelectionKind;
import com.thedndpub.data.dto.classes.ClassSpellcasting;
import com.thedndpub.data.dto.classes.SubclassDefinition;
import com.thedndpub.data.dto.optionalfeature.OptionalFeatureProgressionDto;
import com.thedndpub.data.dto.race.RaceDefinitionKind;
import com.thedndpub.data.dto.cultboon.BoonDefinition;
import com.thedndpub.data.dto.cultboon.CultDefinition;
import com.thedndpub.data.dto.reward.RewardDefinition;
import com.thedndpub.data.dto.deity.DeityDefinition;
import com.thedndpub.data.dto.treasure.ArtObjectTableDefinition;
import com.thedndpub.data.dto.treasure.GemTableDefinition;
import com.thedndpub.data.dto.treasure.MagicItemTableDefinition;
import com.thedndpub.data.dte.background.BackgroundGrantGroupDte;
import com.thedndpub.data.dte.background.BackgroundEquipmentEntryDte;
import com.thedndpub.data.dte.classes.ClassRootDte;
import com.thedndpub.data.dte.classes.ClassSpellcastingSourceDte;
import com.thedndpub.data.dte.feat.FeatBooleanGrantDte;
import com.thedndpub.data.dte.feat.FeatChoiceGrantDte;
import com.thedndpub.data.dte.feat.FeatCountGrantDte;
import com.thedndpub.data.dte.feat.FeatGrantDte;
import com.thedndpub.data.dte.race.RaceSkillToolLanguageGrantDte;
import com.thedndpub.data.importer.classes.ClassSpellcastingNormalizer;
import com.thedndpub.data.services.BackgroundCatalog;
import com.thedndpub.data.services.DeityCatalog;
import com.thedndpub.data.services.TreasureTableCatalog;
import com.thedndpub.data.services.ClassDomainSnapshot;
import com.thedndpub.data.services.CultCatalog;
import com.thedndpub.data.services.BoonCatalog;
import com.thedndpub.data.services.FeatCatalog;
import com.thedndpub.data.services.ItemCatalog;
import com.thedndpub.data.services.OptionalFeatureCatalog;
import com.thedndpub.data.services.ObjectCatalog;
import com.thedndpub.data.services.BastionFacilityCatalog;
import com.thedndpub.data.services.EncounterCatalog;
import com.thedndpub.data.services.RewardCatalog;
import com.thedndpub.data.services.TrapCatalog;
import com.thedndpub.data.services.HazardCatalog;
import com.thedndpub.data.services.VehicleCatalog;
import com.thedndpub.data.services.VehicleUpgradeCatalog;
import com.thedndpub.data.services.VariantRuleCatalog;
import com.thedndpub.data.dte.traphazard.TrapStageDte;
import com.thedndpub.data.dte.traphazard.TrapStageGroupDte;
import com.thedndpub.data.dte.traphazard.TrapStageItemDte;
import com.thedndpub.data.dte.traphazard.TrapStageTextDte;
import com.thedndpub.data.services.RaceCatalog;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.EntryTableCellDte;
import com.thedndpub.data.dte.common.entry.InsetEntryDte;
import com.thedndpub.data.dte.common.entry.InlineEntryDte;
import com.thedndpub.data.dte.common.entry.LinkEntryDte;
import com.thedndpub.data.dte.common.entry.ListEntryDte;
import com.thedndpub.data.dte.common.entry.ListItemEntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.QuoteEntryDte;
import com.thedndpub.data.dte.common.entry.ReadaloudEntryDte;
import com.thedndpub.data.dte.common.entry.RollTableCellDte;
import com.thedndpub.data.dte.common.entry.SectionEntryDte;
import com.thedndpub.data.dte.common.entry.StructuredTableCellDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dte.common.entry.TextTableCellDte;
import com.thedndpub.data.dte.object.ObjectActionContentDte;
import com.thedndpub.data.dte.object.ObjectActionDte;
import com.thedndpub.data.dte.object.ObjectActionItemDte;
import com.thedndpub.data.dte.object.ObjectActionListDte;
import com.thedndpub.data.dte.object.ObjectActionTextDte;
import com.thedndpub.data.dte.object.ObjectSpecialStatisticDte;
import com.thedndpub.data.dto.reward.RewardSpellBenefit;
import com.thedndpub.data.dte.vehicle.VehicleActionObjectDte;
import com.thedndpub.data.dte.vehicle.VehicleActionTextDte;
import com.thedndpub.data.dte.vehicle.VehicleActionDte;
import com.thedndpub.data.dte.vehicle.VehicleDte;
import com.thedndpub.data.dte.vehicle.VehicleNamedEntriesDte;
import com.thedndpub.data.dte.treasure.FromItemsChoiceDte;
import com.thedndpub.data.dte.treasure.FromMatchingChoiceDte;
import com.thedndpub.data.dte.treasure.MagicItemTableRowDte;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SourceAuthoredReferenceExtractor {
    private static final Pattern INLINE_REFERENCE_TAG = Pattern.compile("\\{@(spell|item|skill|filter)\\s+([^}]*)}");
    private static final Pattern ANY_INLINE_TAG = Pattern.compile("\\{@([\\w]+)\\s+([^}]*)}");
    private final ClassDomainSnapshot classSnapshot;
    private final BackgroundCatalog backgrounds;
    private final ItemCatalog items;
    private final FeatCatalog feats;
    private final OptionalFeatureCatalog optionalFeatures;
    private final RaceCatalog races;
    private final CultCatalog cults;
    private final BoonCatalog boons;
    private final ObjectCatalog objects;
    private final BastionFacilityCatalog facilities;
    private final EncounterCatalog encounters;
    private final RewardCatalog rewards;
    private final TrapCatalog traps;
    private final HazardCatalog hazards;
    private final VehicleCatalog vehicles;
    private final VehicleUpgradeCatalog vehicleUpgrades;
    private final VariantRuleCatalog variantRules;
    private final DeityCatalog deities;
    private final TreasureTableCatalog treasureTables;

    public SourceAuthoredReferenceExtractor(
            ClassDomainSnapshot classSnapshot,
            BackgroundCatalog backgrounds,
            ItemCatalog items,
            FeatCatalog feats,
            OptionalFeatureCatalog optionalFeatures,
            RaceCatalog races,
            CultCatalog cults,
            BoonCatalog boons,
            ObjectCatalog objects,
            BastionFacilityCatalog facilities,
            EncounterCatalog encounters,
            RewardCatalog rewards,
            TrapCatalog traps,
            HazardCatalog hazards,
            VehicleCatalog vehicles,
            VehicleUpgradeCatalog vehicleUpgrades,
            VariantRuleCatalog variantRules,
            DeityCatalog deities,
            TreasureTableCatalog treasureTables) {
        this.classSnapshot = classSnapshot;
        this.backgrounds = backgrounds;
        this.items = items;
        this.feats = feats;
        this.optionalFeatures = optionalFeatures;
        this.races = races;
        this.cults = cults;
        this.boons = boons;
        this.objects = objects;
        this.facilities = facilities;
        this.encounters = encounters;
        this.rewards = rewards;
        this.traps = traps;
        this.hazards = hazards;
        this.vehicles = vehicles;
        this.vehicleUpgrades = vehicleUpgrades;
        this.variantRules = variantRules;
        this.deities = deities;
        this.treasureTables = treasureTables;
    }

    public List<AuthoredReference> extract() {
        List<AuthoredReference> references = new ArrayList<>();
        extractClassReferences(references);
        extractBackgroundEquipment(references);
        extractBackgroundGrants(references);
        extractRaceGrants(references);
        extractFeatPrerequisites(references);
        extractOptionalFeaturePrerequisites(references);
        extractBaseItemReferences(references);
        extractCultAndBoonMarkupReferences(references);
        extractObjectMarkupReferences(references);
        extractFacilityMarkupReferences(references);
        extractEncounterMarkupReferences(references);
        extractRewardReferences(references);
        extractTrapAndHazardMarkupReferences(references);
        extractVehicleMarkupReferences(references);
        extractVariantRuleMarkupReferences(references);
        extractDeityMarkupReferences(references);
        extractTreasureTableReferences(references);
        return references.stream()
                .sorted(Comparator.comparing((AuthoredReference value) -> value.owner().catalog())
                        .thenComparing(value -> value.owner().stableId())
                        .thenComparing(AuthoredReference::fieldLocator)
                        .thenComparing(AuthoredReference::originalToken))
                .toList();
    }

    private void extractVariantRuleMarkupReferences(List<AuthoredReference> references) {
        for (var definition : variantRules.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("variant-rule", definition.id().value());
            extractEntryReferences(references, owner, "entries", definition.authoredEntries());
        }
    }

    private void extractDeityMarkupReferences(List<AuthoredReference> references) {
        for (DeityDefinition definition : deities.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("deity", definition.id().value());
            extractEntryReferences(references, owner, "entries", definition.authoredEntries());
        }
    }

    private void extractTreasureTableReferences(List<AuthoredReference> references) {
        for (var definition : treasureTables.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("treasure-table", definition.id().value());
            if (definition instanceof GemTableDefinition gems) {
                extractStringReferences(references, owner, "table", gems.authoredData().table());
            } else if (definition instanceof ArtObjectTableDefinition artObjects) {
                extractStringReferences(references, owner, "table", artObjects.authoredData().table());
            } else if (definition instanceof MagicItemTableDefinition magicItems) {
                for (int rowIndex = 0; rowIndex < magicItems.authoredData().table().size(); rowIndex++) {
                    MagicItemTableRowDte row = magicItems.authoredData().table().get(rowIndex);
                    String rowLocator = "table[" + rowIndex + "]";
                    extractTextReferences(references, owner, rowLocator + ".item", row.item());
                    if (row.table() != null) {
                        for (int nestedIndex = 0; nestedIndex < row.table().size(); nestedIndex++) {
                            extractTextReferences(references, owner,
                                    rowLocator + ".table[" + nestedIndex + "].item",
                                    row.table().get(nestedIndex).item());
                        }
                    }
                    if (row.choose() instanceof FromItemsChoiceDte choice) {
                        for (int candidateIndex = 0; candidateIndex < choice.fromItems().size(); candidateIndex++) {
                            addReference(references, owner,
                                    rowLocator + ".choose.fromItems[" + candidateIndex + "]",
                                    ReferenceTargetKind.ITEM, ReferenceSemanticForm.CHOICE,
                                    choice.fromItems().get(candidateIndex),
                                    "Authored item choice candidate; not an unconditional result");
                        }
                    } else if (row.choose() instanceof FromMatchingChoiceDte choice) {
                        addFilter(references, owner, rowLocator + ".choose.fromMatching.rarity",
                                ReferenceTargetKind.ITEM, choice.fromMatching().rarity(),
                                "Authored rarity filter; candidate items are not expanded");
                    }
                }
            }
        }
    }

    private void extractVehicleMarkupReferences(List<AuthoredReference> references) {
        for (var definition : vehicles.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("vehicle", definition.id().value());
            VehicleDte vehicle = definition.authoredData();
            extractEntryReferences(references, owner, "entries", vehicle.entries());
            if (vehicle.control() != null) {
                for (int index = 0; index < vehicle.control().size(); index++) {
                    extractEntryReferences(references, owner, "control[" + index + "].entries",
                            vehicle.control().get(index).entries());
                }
            }
            if (vehicle.movement() != null) {
                for (int index = 0; index < vehicle.movement().size(); index++) {
                    var movement = vehicle.movement().get(index);
                    if (movement.speed() != null) {
                        for (int speedIndex = 0; speedIndex < movement.speed().size(); speedIndex++) {
                            extractEntryReferences(references, owner,
                                    "movement[" + index + "].speed[" + speedIndex + "].entries",
                                    movement.speed().get(speedIndex).entries());
                        }
                    }
                }
            }
            extractVehicleNamedEntries(references, owner, "trait", vehicle.trait());
            extractVehicleNamedEntries(references, owner, "reaction", vehicle.reaction());
            if (vehicle.weapon() != null) {
                for (int index = 0; index < vehicle.weapon().size(); index++) {
                    var weapon = vehicle.weapon().get(index);
                    String base = "weapon[" + index + "]";
                    extractEntryReferences(references, owner, base + ".entries", weapon.entries());
                    extractVehicleNamedEntries(references, owner, base + ".action", weapon.action());
                }
            }
            if (vehicle.action() != null) {
                for (int index = 0; index < vehicle.action().size(); index++) {
                    extractVehicleAction(references, owner, "action[" + index + "]", vehicle.action().get(index));
                }
            }
            if (vehicle.actionStation() != null) {
                for (int index = 0; index < vehicle.actionStation().size(); index++) {
                    var station = vehicle.actionStation().get(index);
                    extractStringReferences(references, owner, "actionStation[" + index + "].entries",
                            station.entries());
                }
            }
            if (vehicle.station() != null) {
                for (int index = 0; index < vehicle.station().size(); index++) {
                    var station = vehicle.station().get(index);
                    String base = "station[" + index + "]";
                    extractEntryReferences(references, owner, base + ".entries", station.entries());
                    if (station.action() != null) {
                        for (int actionIndex = 0; actionIndex < station.action().size(); actionIndex++) {
                            extractEntryReferences(references, owner,
                                    base + ".action[" + actionIndex + "].entries",
                                    station.action().get(actionIndex).entries());
                        }
                    }
                }
            }
        }
        for (var upgrade : vehicleUpgrades.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("vehicle-upgrade", upgrade.id().value());
            extractEntryReferences(references, owner, upgrade.authoredEntries());
        }
    }

    private static void extractVehicleNamedEntries(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String field,
            List<VehicleNamedEntriesDte> values) {
        if (values == null) {
            return;
        }
        for (int index = 0; index < values.size(); index++) {
            extractEntryReferences(references, owner, field + "[" + index + "].entries", values.get(index).entries());
        }
    }

    private static void extractVehicleAction(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            VehicleActionDte action) {
        if (action instanceof VehicleActionTextDte text) {
            extractTextReferences(references, owner, locator + ".text", text.text());
        } else if (action instanceof VehicleActionObjectDte object) {
            extractTextReferences(references, owner, locator + ".name", object.name());
            extractEntryReferences(references, owner, locator + ".entries", object.entries());
            if (object.items() != null) {
                for (int index = 0; index < object.items().size(); index++) {
                    var item = object.items().get(index);
                    extractTextReferences(references, owner, locator + ".items[" + index + "].name", item.name());
                    extractTextReferences(references, owner, locator + ".items[" + index + "].entry", item.entry());
                }
            }
        }
    }

    private void extractTrapAndHazardMarkupReferences(List<AuthoredReference> references) {
        for (var trap : traps.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("trap", trap.id().value());
            extractEntryReferences(references, owner, trap.entries());
            extractStringReferences(references, owner, "effect", trap.effect());
            extractStringReferences(references, owner, "trigger", trap.trigger());
            extractStageReferences(references, owner, "countermeasures", trap.countermeasures());
            extractStageReferences(references, owner, "eActive", trap.activeStages());
            extractStageReferences(references, owner, "eDynamic", trap.dynamicStages());
            extractStageReferences(references, owner, "eConstant", trap.constantStages());
        }
        for (var hazard : hazards.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("hazard", hazard.id().value());
            extractEntryReferences(references, owner, hazard.entries());
        }
    }

    private static void extractStringReferences(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String field,
            List<String> values) {
        for (int index = 0; index < values.size(); index++) {
            extractTextReferences(references, owner, field + "[" + index + "]", values.get(index));
        }
    }

    private static void extractStageReferences(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String field,
            List<TrapStageDte> stages) {
        for (int index = 0; index < stages.size(); index++) {
            String locator = field + "[" + index + "]";
            TrapStageDte stage = stages.get(index);
            if (stage instanceof TrapStageTextDte text) {
                extractTextReferences(references, owner, locator, text.text());
            } else if (stage instanceof TrapStageGroupDte group) {
                for (int itemIndex = 0; itemIndex < group.items().size(); itemIndex++) {
                    TrapStageItemDte item = group.items().get(itemIndex);
                    String itemLocator = locator + ".items[" + itemIndex + "]";
                    extractTextReferences(references, owner, itemLocator + ".name", item.name());
                    extractTextReferences(references, owner, itemLocator + ".entry", item.entry());
                    if (item.entries() != null) {
                        for (int entryIndex = 0; entryIndex < item.entries().size(); entryIndex++) {
                            extractEntryReferences(references, owner,
                                    itemLocator + ".entries[" + entryIndex + "]", item.entries().get(entryIndex));
                        }
                    }
                }
            }
        }
    }

    private void extractRewardReferences(List<AuthoredReference> references) {
        for (RewardDefinition reward : rewards.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("reward", reward.id().value());
            extractEntryReferences(references, owner, reward.authoredEntries());
            for (int spellIndex = 0; spellIndex < reward.additionalSpells().size(); spellIndex++) {
                extractRewardSpellReferences(references, owner, spellIndex, reward.additionalSpells().get(spellIndex));
            }
        }
    }

    private static void extractRewardSpellReferences(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            int spellIndex,
            RewardSpellBenefit spell) {
        String base = "additionalSpells[" + spellIndex + "]";
        if (!spell.knownSpellIds().isEmpty()) {
            addRewardSpellIds(references, owner, base + ".known._", spell.knownSpellIds(),
                    ReferenceSemanticForm.DEFINITION);
        }
        if (!spell.innateSpellIds().isEmpty()) {
            addRewardSpellIds(references, owner, base + ".innate._", spell.innateSpellIds(),
                    ReferenceSemanticForm.DEFINITION);
        } else {
            addRewardFrequency(references, owner, base + ".innate._.daily", spell.daily());
            addRewardFrequency(references, owner, base + ".innate._.limited", spell.limited());
            addRewardFrequency(references, owner, base + ".innate._.rest", spell.rest());
            if (!spell.ritualSpellIds().isEmpty()) {
                addRewardSpellIds(references, owner, base + ".innate._.ritual", spell.ritualSpellIds(),
                        ReferenceSemanticForm.DEFINITION);
            }
        }
    }

    private static void addRewardFrequency(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            java.util.Map<String, List<String>> frequencies) {
        if (frequencies == null) {
            return;
        }
        frequencies.forEach((frequency, spellIds) -> addRewardSpellIds(references, owner,
                locator + "." + frequency, spellIds, ReferenceSemanticForm.DEFINITION));
    }

    private static void addRewardSpellIds(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            List<String> spellIds,
            ReferenceSemanticForm form) {
        for (int index = 0; index < spellIds.size(); index++) {
            ParsedUid parsed = ReferenceUidParser.parse(spellIds.get(index));
            references.add(reference(owner, locator + "[" + index + "]", ReferenceTargetKind.SPELL, form,
                    parsed, parsed.source(), "Reward-authored spell benefit; usage context remains in locator"));
        }
    }

    private void extractEncounterMarkupReferences(List<AuthoredReference> references) {
        for (var definition : encounters.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("encounter", definition.id().value());
            for (int tableIndex = 0; tableIndex < definition.tables().size(); tableIndex++) {
                var table = definition.tables().get(tableIndex);
                for (int rowIndex = 0; rowIndex < table.rows().size(); rowIndex++) {
                    extractEncounterResultReferences(references, owner, tableIndex, rowIndex,
                            table.rows().get(rowIndex).result());
                }
            }
        }
    }

    private static void extractEncounterResultReferences(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            int tableIndex,
            int rowIndex,
            String result) {
        Matcher matcher = ANY_INLINE_TAG.matcher(result);
        int markupIndex = 0;
        while (matcher.find()) {
            String tag = matcher.group(1);
            String arguments = matcher.group(2);
            String locator = "tables[" + tableIndex + "].table[" + rowIndex + "]"
                    + ".result.markup[" + markupIndex++ + "]";
            if ("filter".equals(tag)) {
                references.add(new AuthoredReference(owner, locator, ReferenceTargetKind.ITEM,
                        ReferenceSemanticForm.FILTER, arguments, filterSource(arguments), null, null,
                        "Source-authored item filter; candidates are not expanded"));
                continue;
            }
            ReferenceTargetKind kind = switch (tag) {
                case "spell" -> ReferenceTargetKind.SPELL;
                case "item" -> ReferenceTargetKind.ITEM;
                case "skill" -> ReferenceTargetKind.SKILL;
                default -> null;
            };
            if (kind == null) {
                continue;
            }
            String[] fields = arguments.split("\\|", -1);
            if (fields[0].isBlank()) {
                throw new IllegalArgumentException("Malformed inline " + tag + " reference: " + arguments);
            }
            if (fields.length > 2) {
                references.add(new AuthoredReference(owner, locator, ReferenceTargetKind.ITEM,
                        ReferenceSemanticForm.UNSUPPORTED, arguments, null, fields[0], null,
                        "Extended markup context is preserved but not interpreted"));
                continue;
            }
            ParsedUid parsed = ReferenceUidParser.parse(arguments);
            references.add(reference(owner, locator, kind, ReferenceSemanticForm.DEFINITION, parsed,
                    parsed.source(), "Explicit source markup link; not a grant"));
        }
    }

    private void extractFacilityMarkupReferences(List<AuthoredReference> references) {
        for (var definition : facilities.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("facility", definition.id());
            extractEntryReferences(references, owner, facilities.sourceById(definition.id()).entries());
        }
    }

    private void extractObjectMarkupReferences(List<AuthoredReference> references) {
        for (var definition : objects.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("object", definition.id());
            var source = objects.sourceById(definition.id());
            extractEntryReferences(references, owner, source.entries());
            if (source.ac() instanceof ObjectSpecialStatisticDte specialAc) {
                extractTextReferences(references, owner, "ac.special", specialAc.special());
            }
            if (source.hp() instanceof ObjectSpecialStatisticDte specialHp) {
                extractTextReferences(references, owner, "hp.special", specialHp.special());
            }
            if (source.actionEntries() == null) {
                continue;
            }
            for (int actionIndex = 0; actionIndex < source.actionEntries().size(); actionIndex++) {
                ObjectActionDte action = source.actionEntries().get(actionIndex);
                String base = "actionEntries[" + actionIndex + "]";
                extractTextReferences(references, owner, base + ".name", action.name());
                for (int entryIndex = 0; entryIndex < action.entries().size(); entryIndex++) {
                    ObjectActionContentDte content = action.entries().get(entryIndex);
                    String locator = base + ".entries[" + entryIndex + "]";
                    if (content instanceof ObjectActionTextDte text) {
                        extractTextReferences(references, owner, locator, text.text());
                    } else if (content instanceof com.thedndpub.data.dte.object.ObjectAttackDte attack) {
                        extractTexts(references, owner, locator + ".attackEntries", attack.attackEntries());
                        extractTexts(references, owner, locator + ".hitEntries", attack.hitEntries());
                    } else if (content instanceof ObjectActionListDte list) {
                        for (int itemIndex = 0; itemIndex < list.items().size(); itemIndex++) {
                            ObjectActionItemDte item = list.items().get(itemIndex);
                            extractEntryListReferences(references, owner,
                                    locator + ".items[" + itemIndex + "].entries", item.entries());
                        }
                    }
                }
            }
        }
    }

    private static void extractTexts(List<AuthoredReference> references, ReferenceOwner owner,
            String locator, List<String> values) {
        if (values == null) {
            return;
        }
        for (int index = 0; index < values.size(); index++) {
            extractTextReferences(references, owner, locator + "[" + index + "]", values.get(index));
        }
    }

    private void extractCultAndBoonMarkupReferences(List<AuthoredReference> references) {
        for (CultDefinition cult : cults.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("cult", cult.id().value());
            var imported = cults.importById(cult.id().value());
            extractEntryReferences(references, owner, imported.provenance().entries());
            extractTextReferences(references, owner, "goal.entry", cult.goalEntry());
            extractTextReferences(references, owner, "cultists.entry", cult.cultistsEntry());
            extractTextReferences(references, owner, "signatureSpells.entry", cult.signatureSpellsEntry());
        }
        for (BoonDefinition boon : boons.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("boon", boon.id().value());
            var imported = boons.importById(boon.id().value());
            extractEntryReferences(references, owner, imported.provenance().entries());
            extractTextReferences(references, owner, "abilityEntry", boon.abilityEntry());
            extractTextReferences(references, owner, "signatureSpells.entry", boon.signatureSpellsEntry());
        }
    }

    private static void extractEntryReferences(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            List<EntryDte> entries) {
        if (entries == null) {
            return;
        }
        for (int index = 0; index < entries.size(); index++) {
            extractEntryReferences(references, owner, "entries[" + index + "]", entries.get(index));
        }
    }

    private static void extractEntryReferences(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            List<EntryDte> entries) {
        if (entries == null) {
            return;
        }
        for (int index = 0; index < entries.size(); index++) {
            extractEntryReferences(references, owner, locator + "[" + index + "]", entries.get(index));
        }
    }

    private static void extractEntryReferences(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            EntryDte entry) {
        if (entry instanceof TextEntryDte text) {
            extractTextReferences(references, owner, locator + ".text", text.text());
        } else if (entry instanceof NamedEntryDte named) {
            extractTextReferences(references, owner, locator + ".name", named.name());
            extractTextReferences(references, owner, locator + ".entry", named.entry());
            extractEntryListReferences(references, owner, locator + ".entries", named.entries());
        } else if (entry instanceof ListEntryDte list) {
            extractEntryListReferences(references, owner, locator + ".items", list.items());
        } else if (entry instanceof ListItemEntryDte item) {
            extractTextReferences(references, owner, locator + ".name", item.name());
            extractTextReferences(references, owner, locator + ".entry", item.entry());
            extractEntryListReferences(references, owner, locator + ".entries", item.entries());
        } else if (entry instanceof TableEntryDte table) {
            if (table.caption() != null) {
                extractTextReferences(references, owner, locator + ".caption", table.caption());
            }
            if (table.rows() != null) {
                for (int rowIndex = 0; rowIndex < table.rows().size(); rowIndex++) {
                    List<EntryTableCellDte> row = table.rows().get(rowIndex);
                    for (int cellIndex = 0; cellIndex < row.size(); cellIndex++) {
                        extractCellReferences(references, owner,
                                locator + ".rows[" + rowIndex + "][" + cellIndex + "]", row.get(cellIndex));
                    }
                }
            }
            if (table.footnotes() != null) {
                for (int footnoteIndex = 0; footnoteIndex < table.footnotes().size(); footnoteIndex++) {
                    extractTextReferences(references, owner,
                            locator + ".footnotes[" + footnoteIndex + "]", table.footnotes().get(footnoteIndex));
                }
            }
        } else if (entry instanceof QuoteEntryDte quote) {
            extractTextReferences(references, owner, locator + ".by", quote.by());
            extractEntryListReferences(references, owner, locator + ".entries", quote.entries());
        } else if (entry instanceof InsetEntryDte inset) {
            extractTextReferences(references, owner, locator + ".name", inset.name());
            extractEntryListReferences(references, owner, locator + ".entries", inset.entries());
        } else if (entry instanceof ReadaloudEntryDte readaloud) {
            extractTextReferences(references, owner, locator + ".name", readaloud.name());
            extractEntryListReferences(references, owner, locator + ".entries", readaloud.entries());
        } else if (entry instanceof SectionEntryDte section) {
            extractTextReferences(references, owner, locator + ".name", section.name());
            extractEntryListReferences(references, owner, locator + ".entries", section.entries());
        } else if (entry instanceof InlineEntryDte inline) {
            extractEntryListReferences(references, owner, locator + ".entries", inline.entries());
        } else if (entry instanceof LinkEntryDte link) {
            extractTextReferences(references, owner, locator + ".entry", link.entry());
        }
    }

    private static void extractEntryListReferences(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            List<EntryDte> entries) {
        if (entries == null) {
            return;
        }
        for (int index = 0; index < entries.size(); index++) {
            extractEntryReferences(references, owner, locator + "[" + index + "]", entries.get(index));
        }
    }

    private static void extractCellReferences(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            EntryTableCellDte cell) {
        if (cell instanceof TextTableCellDte text) {
            extractTextReferences(references, owner, locator + ".text", text.text());
        } else if (cell instanceof RollTableCellDte roll && roll.entry() != null) {
            extractEntryReferences(references, owner, locator + ".entry", roll.entry());
        } else if (cell instanceof StructuredTableCellDte structured) {
            extractEntryListReferences(references, owner, locator + ".entries", structured.entries());
        }
    }

    private static void extractTextReferences(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            String text) {
        if (text == null) {
            return;
        }
        Matcher matcher = INLINE_REFERENCE_TAG.matcher(text);
        int occurrence = 0;
        while (matcher.find()) {
            String tag = matcher.group(1);
            String arguments = matcher.group(2);
            String referenceLocator = locator + ".markup[" + occurrence++ + "]";
            if ("filter".equals(tag)) {
                references.add(new AuthoredReference(owner, referenceLocator, ReferenceTargetKind.ITEM,
                        ReferenceSemanticForm.FILTER, arguments, filterSource(arguments), null, null,
                        "Source-authored item filter; candidates are not expanded"));
                continue;
            }

            ReferenceTargetKind targetKind = switch (tag) {
                case "spell" -> ReferenceTargetKind.SPELL;
                case "item" -> ReferenceTargetKind.ITEM;
                case "skill" -> ReferenceTargetKind.SKILL;
                default -> throw new IllegalStateException("Unsupported extracted markup reference tag " + tag);
            };
            String[] fields = arguments.split("\\|", -1);
            if (fields[0].isBlank()) {
                throw new IllegalArgumentException("Malformed inline " + tag + " reference: " + arguments);
            }
            if (fields.length > 2) {
                references.add(new AuthoredReference(owner, referenceLocator, targetKind,
                        ReferenceSemanticForm.UNSUPPORTED, arguments, null, fields[0], null,
                        "Extended " + tag + " markup context is preserved but not interpreted"));
                continue;
            }
            String token = arguments;
            ParsedUid parsed = ReferenceUidParser.parse(token);
            references.add(reference(owner, referenceLocator, targetKind, ReferenceSemanticForm.DEFINITION,
                    parsed, parsed.source(), "Explicit source markup link; not a grant"));
        }
    }

    private static String filterSource(String arguments) {
        for (String field : arguments.split("\\|", -1)) {
            String trimmed = field.trim();
            if (trimmed.startsWith("source=") && trimmed.length() > "source=".length()) {
                return trimmed.substring("source=".length()).trim();
            }
        }
        return null;
    }

    private void extractBackgroundGrants(List<AuthoredReference> references) {
        for (BackgroundDefinition definition : backgrounds.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("background", definition.id().value());
            var authored = definition.sourceRecord();
            extractGrantGroups(references, owner, "feats", authored.feats(), ReferenceTargetKind.FEAT);
            extractGrantGroups(references, owner, "skillProficiencies", authored.skillProficiencies(),
                    ReferenceTargetKind.SKILL);
            extractGrantGroups(references, owner, "languageProficiencies", authored.languageProficiencies(),
                    ReferenceTargetKind.LANGUAGE);
        }
    }

    private void extractRaceGrants(List<AuthoredReference> references) {
        for (var definition : races.definitions()) {
            if (definition.provenance().expandedVersion()
                    || definition.provenance().templateImplementation()) {
                continue;
            }
            ReferenceOwner owner = new ReferenceOwner(
                    definition.kind() == RaceDefinitionKind.SUBRACE
                            ? "subrace" : "race",
                    definition.id().value());
            if (definition.sourceData().race() != null) {
                var authored = definition.sourceData().race();
                extractGrantGroups(references, owner, "feats", authored.feats(), ReferenceTargetKind.FEAT);
                extractGrantGroups(references, owner, "skillProficiencies", authored.skillProficiencies(),
                        ReferenceTargetKind.SKILL);
                extractGrantGroups(references, owner, "languageProficiencies", authored.languageProficiencies(),
                        ReferenceTargetKind.LANGUAGE);
                extractGrantGroups(references, owner, "skillToolLanguageProficiencies",
                        authored.skillToolLanguageProficiencies(), ReferenceTargetKind.PROFICIENCY);
            } else {
                var authored = definition.sourceData().subrace();
                extractGrantGroups(references, owner, "feats", authored.feats(), ReferenceTargetKind.FEAT);
                extractGrantGroups(references, owner, "skillProficiencies", authored.skillProficiencies(),
                        ReferenceTargetKind.SKILL);
                extractGrantGroups(references, owner, "languageProficiencies", authored.languageProficiencies(),
                        ReferenceTargetKind.LANGUAGE);
                extractCombinedProficiencyChoices(references, owner, "skillToolLanguageProficiencies",
                        authored.skillToolLanguageProficiencies());
            }
        }
    }

    private static void extractGrantGroups(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String field,
            List<BackgroundGrantGroupDte> groups,
            ReferenceTargetKind targetKind) {
        if (groups == null) {
            return;
        }
        for (int groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
            var group = groups.get(groupIndex);
            for (var grant : group.grants().entrySet()) {
                String locator = field + "[" + groupIndex + "]." + grant.getKey();
                var value = grant.getValue();
                if (isCategorySelector(grant.getKey())) {
                    references.add(new AuthoredReference(owner, locator, targetKind,
                            ReferenceSemanticForm.FILTER, grant.getKey(), null, grant.getKey(), null,
                            "Authored category token is a selection filter, not a definition identity"));
                    continue;
                }
                if (value.choice() != null) {
                    var choice = value.choice();
                    for (int candidateIndex = 0; candidateIndex < choice.from().size(); candidateIndex++) {
                        addReference(references, owner, locator + ".choose.from[" + candidateIndex + "]",
                                targetKind, ReferenceSemanticForm.CHOICE, choice.from().get(candidateIndex),
                                "Authored selection candidate; not a direct grant");
                    }
                    if (!choice.categories().isEmpty() || choice.fromFilter() != null) {
                        String selector = !choice.categories().isEmpty()
                                ? String.join(",", choice.categories()) : choice.fromFilter();
                        references.add(new AuthoredReference(owner, locator + ".choose.filter", targetKind,
                                ReferenceSemanticForm.FILTER, selector, null, selector, null,
                                "Authored category/filter selector; candidate definitions are not expanded"));
                    }
                    continue;
                }
                if (Boolean.TRUE.equals(value.granted()) || value.count() != null) {
                    addReference(references, owner, locator, targetKind, ReferenceSemanticForm.DEFINITION,
                            grant.getKey(), null);
                }
            }
        }
    }

    private static void extractClassChoices(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            List<ClassChoiceProgression> choices) {
        for (int choiceIndex = 0; choiceIndex < choices.size(); choiceIndex++) {
            var choice = choices.get(choiceIndex);
            ReferenceTargetKind targetKind = switch (choice.targetKind()) {
                case FEAT -> ReferenceTargetKind.FEAT;
                case OPTIONAL_FEATURE -> ReferenceTargetKind.OPTIONAL_FEATURE;
                default -> null;
            };
            if (targetKind == null) {
                continue;
            }
            for (int requiredIndex = 0; requiredIndex < choice.required().size(); requiredIndex++) {
                addClassExternalReference(references, owner,
                        "choices[" + choiceIndex + "].required[" + requiredIndex + "]",
                        choice.required().get(requiredIndex).reference(), ReferenceSemanticForm.DEFINITION);
            }
            for (int categoryIndex = 0; categoryIndex < choice.categories().size(); categoryIndex++) {
                addFilter(references, owner, "choices[" + choiceIndex + "].categories[" + categoryIndex + "]",
                        targetKind, choice.categories().get(categoryIndex), "Class progression category selector");
            }
        }
    }

    private static void extractSkillChoices(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String field,
            List<ClassSkillChoice> choices) {
        for (int choiceIndex = 0; choiceIndex < choices.size(); choiceIndex++) {
            var choice = choices.get(choiceIndex);
            for (int index = 0; index < choice.from().size(); index++) {
                addClassExternalReference(references, owner,
                        field + "[" + choiceIndex + "].from[" + index + "]",
                        choice.from().get(index), ReferenceSemanticForm.CHOICE);
            }
        }
    }

    private static void extractClassFeatureContent(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            List<ClassFeatureNode> nodes,
            String locator,
            boolean inChoice) {
        for (int index = 0; index < nodes.size(); index++) {
            var node = nodes.get(index);
            String itemLocator = locator + "[" + index + "]";
            if (node instanceof ClassExternalFeatureNode external) {
                ReferenceSemanticForm form = inChoice || Boolean.FALSE.equals(external.required())
                        ? ReferenceSemanticForm.CHOICE : ReferenceSemanticForm.DEFINITION;
                addClassExternalReference(references, owner, itemLocator + ".reference", external.reference(), form);
            } else if (node instanceof ClassFeatureChoice choice) {
                extractClassFeatureContent(references, owner, choice.options(), itemLocator + ".options", true);
            } else if (node instanceof ClassFeatureSection section) {
                extractClassFeatureContent(references, owner, section.entries(), itemLocator + ".entries", inChoice);
                extractClassFeatureContent(references, owner, section.items(), itemLocator + ".items", inChoice);
            }
        }
    }

    private static void addClassExternalReference(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            ClassExternalReference external,
            ReferenceSemanticForm form) {
        ReferenceTargetKind targetKind = switch (external.kind()) {
            case FEAT -> ReferenceTargetKind.FEAT;
            case OPTIONAL_FEATURE -> ReferenceTargetKind.OPTIONAL_FEATURE;
            case SKILL -> ReferenceTargetKind.SKILL;
            case LANGUAGE -> ReferenceTargetKind.LANGUAGE;
            default -> null;
        };
        if (targetKind == null) {
            return;
        }
        String source = external.source() == null ? null : external.source().code();
        addReference(references, owner, locator, targetKind, form, uid(external.name(), source),
                "Normalized typed class-domain reference");
    }

    private void extractFeatPrerequisites(List<AuthoredReference> references) {
        for (var feat : feats.definitions()) {
            if (feat.variant()) {
                continue;
            }
            ReferenceOwner owner = new ReferenceOwner("feat", feat.id().value());
            for (int index = 0; index < feat.prerequisites().size(); index++) {
                var prerequisite = feat.prerequisites().get(index);
                extractFeatPrerequisiteNames(references, owner,
                        "prerequisites[" + index + "].feat", prerequisite.feat());
                extractNames(references, owner, "prerequisites[" + index + "].feature", prerequisite.feature(),
                        ReferenceTargetKind.OPTIONAL_FEATURE, "Prerequisite reference, not a grant");
            }
        }
        for (var feat : feats.definitions()) {
            if (feat.variant()) {
                continue;
            }
            var record = feats.importById(feat.id().value()).sourceRecord();
            ReferenceOwner owner = new ReferenceOwner("feat", feat.id().value());
            extractFeatGrants(references, owner, "skillProficiencies", record.skillProficiencies(),
                    ReferenceTargetKind.SKILL);
            extractFeatGrants(references, owner, "languageProficiencies", record.languageProficiencies(),
                    ReferenceTargetKind.LANGUAGE);
            if (record.skillToolLanguageProficiencies() != null) {
                for (int groupIndex = 0; groupIndex < record.skillToolLanguageProficiencies().size(); groupIndex++) {
                    var group = record.skillToolLanguageProficiencies().get(groupIndex);
                    for (int choiceIndex = 0; choiceIndex < group.choose().size(); choiceIndex++) {
                        var choice = group.choose().get(choiceIndex);
                        extractChoiceCandidates(references, owner,
                                "skillToolLanguageProficiencies[" + groupIndex + "].choose[" + choiceIndex + "]",
                                choice.from(), ReferenceTargetKind.PROFICIENCY);
                        if (choice.fromFilter() != null) {
                            addFilter(references, owner, "skillToolLanguageProficiencies[" + groupIndex
                                            + "].choose[" + choiceIndex + "].fromFilter",
                                    ReferenceTargetKind.PROFICIENCY, choice.fromFilter(),
                                    "Combined proficiency selector retained without cross-kind inference");
                        }
                    }
                }
            }
            if (record.optionalfeatureProgression() != null) {
                for (int index = 0; index < record.optionalfeatureProgression().size(); index++) {
                    var progression = record.optionalfeatureProgression().get(index);
                    List<String> types = progression.featureType() == null ? List.of() : progression.featureType();
                    for (int typeIndex = 0; typeIndex < types.size(); typeIndex++) {
                        addFilter(references, owner,
                                "optionalfeatureProgression[" + index + "].featureType[" + typeIndex + "]",
                                ReferenceTargetKind.OPTIONAL_FEATURE, types.get(typeIndex),
                                "Feat progression feature type selects eligible optional features");
                    }
                }
            }
        }
    }

    private static void extractCombinedProficiencyChoices(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String field,
            List<RaceSkillToolLanguageGrantDte> groups) {
        if (groups == null) {
            return;
        }
        for (int groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
            var group = groups.get(groupIndex);
            for (int choiceIndex = 0; choiceIndex < group.choose().size(); choiceIndex++) {
                var choice = group.choose().get(choiceIndex);
                String locator = field + "[" + groupIndex + "].choose[" + choiceIndex + "]";
                extractChoiceCandidates(references, owner, locator, choice.from(), ReferenceTargetKind.PROFICIENCY);
            }
        }
    }

    private static void extractFeatGrants(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String field,
            List<FeatGrantDte> grants,
            ReferenceTargetKind targetKind) {
        if (grants == null) {
            return;
        }
        for (int groupIndex = 0; groupIndex < grants.size(); groupIndex++) {
            var group = grants.get(groupIndex);
            for (var entry : group.values().entrySet()) {
                String locator = field + "[" + groupIndex + "]." + entry.getKey();
                if (isCategorySelector(entry.getKey())) {
                    references.add(new AuthoredReference(owner, locator, targetKind,
                            ReferenceSemanticForm.FILTER, entry.getKey(), null, entry.getKey(), null,
                            "Authored proficiency category is a filter, not a definition identity"));
                } else if ((entry.getValue() instanceof FeatBooleanGrantDte booleanGrant
                        && Boolean.TRUE.equals(booleanGrant.value()))
                        || entry.getValue() instanceof FeatCountGrantDte) {
                    addReference(references, owner, locator, targetKind, ReferenceSemanticForm.DEFINITION,
                            entry.getKey(), null);
                } else if (entry.getValue() instanceof FeatChoiceGrantDte choiceGrant) {
                    extractChoiceCandidates(references, owner, locator + ".choose",
                            choiceGrant.value().from(), targetKind);
                    if (choiceGrant.value().fromFilter() != null) {
                        references.add(new AuthoredReference(owner, locator + ".choose.filter", targetKind,
                                ReferenceSemanticForm.FILTER, choiceGrant.value().fromFilter(), null,
                                choiceGrant.value().fromFilter(), null,
                                "Authored proficiency selector; candidates are not expanded"));
                    }
                }
            }
        }
    }

    private static boolean isCategorySelector(String token) {
        String normalized = token.trim().toLowerCase(java.util.Locale.ROOT)
                .replace("_", "").replace(" ", "");
        return normalized.equals("any") || normalized.equals("other")
                || normalized.startsWith("anyfrom") || normalized.startsWith("anystandard");
    }

    private void extractOptionalFeaturePrerequisites(List<AuthoredReference> references) {
        for (var feature : optionalFeatures.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("optional-feature", feature.id());
            for (int grantIndex = 0; grantIndex < feature.proficiencyGrants().size(); grantIndex++) {
                var grant = feature.proficiencyGrants().get(grantIndex);
                if (Boolean.TRUE.equals(grant.deception())) {
                    addReference(references, owner, "proficiencyGrants[" + grantIndex + "].deception",
                            ReferenceTargetKind.SKILL, ReferenceSemanticForm.DEFINITION, "Deception",
                            "Source-authored Optional Feature skill grant");
                }
                if (Boolean.TRUE.equals(grant.persuasion())) {
                    addReference(references, owner, "proficiencyGrants[" + grantIndex + "].persuasion",
                            ReferenceTargetKind.SKILL, ReferenceSemanticForm.DEFINITION, "Persuasion",
                            "Source-authored Optional Feature skill grant");
                }
            }
            for (int index = 0; index < feature.prerequisites().size(); index++) {
                extractNames(references, owner, "prerequisites[" + index + "].optionalFeatures",
                        feature.prerequisites().get(index).optionalFeatures(), ReferenceTargetKind.OPTIONAL_FEATURE,
                        "Prerequisite reference, not a grant");
            }
            extractOptionalProgressions(references, owner, "featProgressions",
                    feature.featProgressions(), ReferenceTargetKind.FEAT);
            extractOptionalProgressions(references, owner, "optionalFeatureProgressions",
                    feature.optionalFeatureProgressions(), ReferenceTargetKind.OPTIONAL_FEATURE);
        }
    }

    private static void extractOptionalProgressions(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String field,
            List<OptionalFeatureProgressionDto> progressions,
            ReferenceTargetKind targetKind) {
        for (int index = 0; index < progressions.size(); index++) {
            var progression = progressions.get(index);
            List<String> filters = targetKind == ReferenceTargetKind.FEAT
                    ? progression.categories() : progression.featureTypes();
            for (int filterIndex = 0; filterIndex < filters.size(); filterIndex++) {
                addFilter(references, owner, field + "[" + index + "].filter[" + filterIndex + "]",
                        targetKind, filters.get(filterIndex), "Progression category is an eligibility filter");
            }
        }
    }

    private static void addFilter(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            ReferenceTargetKind targetKind,
            String token,
            String reason) {
        references.add(new AuthoredReference(owner, locator, targetKind, ReferenceSemanticForm.FILTER,
                token, null, token, null, reason));
    }

    private static void extractNames(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            List<String> names,
            ReferenceTargetKind targetKind,
            String reason) {
        for (int index = 0; index < names.size(); index++) {
            addReference(references, owner, locator + "[" + index + "]", targetKind,
                    ReferenceSemanticForm.DEFINITION, names.get(index), reason);
        }
    }

    private static void extractFeatPrerequisiteNames(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            List<String> names) {
        for (int index = 0; index < names.size(); index++) {
            String token = names.get(index);
            String[] parts = token.split("\\|", -1);
            if (parts.length > 2 && !parts[0].isBlank() && !parts[1].isBlank()) {
                references.add(new AuthoredReference(owner, locator + "[" + index + "]",
                        ReferenceTargetKind.FEAT, ReferenceSemanticForm.UNSUPPORTED, token, parts[1],
                        parts[0], parts[1],
                        "Feat prerequisite carries additional authored configuration segments; retained without binding"));
            } else {
                addReference(references, owner, locator + "[" + index + "]", ReferenceTargetKind.FEAT,
                        ReferenceSemanticForm.DEFINITION, token, "Prerequisite reference, not a grant");
            }
        }
    }

    private static void extractChoiceCandidates(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            List<String> candidates,
            ReferenceTargetKind targetKind) {
        for (int index = 0; index < candidates.size(); index++) {
            addReference(references, owner, locator + ".from[" + index + "]", targetKind,
                    ReferenceSemanticForm.CHOICE, candidates.get(index),
                    "Authored proficiency choice candidate; not an unconditional grant");
        }
    }

    private static void addReference(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            ReferenceTargetKind targetKind,
            ReferenceSemanticForm form,
            String token,
            String reason) {
        ParsedUid parsed = ReferenceUidParser.parse(token);
        references.add(reference(owner, locator, targetKind, form, parsed, parsed.source(), reason));
    }

    private void extractClassReferences(List<AuthoredReference> references) {
        var result = classSnapshot.result();
        for (ClassDefinition definition : result.classes()) {
            ReferenceOwner owner = new ReferenceOwner("class", definition.id().value());
            extractEquipment(references, owner, definition.startingOptions() == null
                    ? List.of() : definition.startingOptions().equipment());
            extractAuthoredSpells(references, owner, definition.provenance().authored().data());
            extractClassChoices(references, owner, definition.choices());
            extractSkillChoices(references, owner, "startingOptions.proficiencies.skills",
                    definition.startingOptions() == null || definition.startingOptions().proficiencies() == null
                            ? List.of() : definition.startingOptions().proficiencies().skills());
            extractSkillChoices(references, owner, "multiclassing.proficienciesGained.skills",
                    definition.multiclassing() == null || definition.multiclassing().proficienciesGained() == null
                            ? List.of() : definition.multiclassing().proficienciesGained().skills());
        }
        for (SubclassDefinition definition : result.subclasses()) {
            ReferenceOwner owner = new ReferenceOwner("subclass", definition.id().value());
            extractAuthoredSpells(references, owner, definition.provenance().authored().data());
            extractClassChoices(references, owner, definition.choices());
        }
        for (var feature : result.classFeatures()) {
            extractClassFeatureContent(references, new ReferenceOwner("class-feature", feature.id().value()),
                    feature.content(), "content", false);
        }
        for (var feature : result.subclassFeatures()) {
            extractClassFeatureContent(references, new ReferenceOwner("subclass-feature", feature.id().value()),
                    feature.content(), "content", false);
        }
    }

    private static void extractAuthoredSpells(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            ClassRootDte authored) {
        if (authored instanceof ClassSpellcastingSourceDte spellSource) {
            var spellcasting = new ClassSpellcastingNormalizer().normalize(spellSource, List.of());
            extractSpells(references, owner, spellcasting);
        }
    }

    private static void extractEquipment(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            List<ClassEquipmentGroup> groups) {
        for (int groupIndex = 0; groupIndex < groups.size(); groupIndex++) {
            var group = groups.get(groupIndex);
            for (int branchIndex = 0; branchIndex < group.branches().size(); branchIndex++) {
                var branch = group.branches().get(branchIndex);
                for (int entryIndex = 0; entryIndex < branch.entries().size(); entryIndex++) {
                    ClassEquipmentOption option = branch.entries().get(entryIndex);
                    if (option.reference() == null) {
                        continue;
                    }
                    var external = option.reference();
                    if (external.kind() != ClassExternalKind.ITEM
                            && external.kind() != ClassExternalKind.ITEM_GROUP) {
                        continue;
                    }
                    ReferenceTargetKind targetKind = external.kind() == ClassExternalKind.ITEM_GROUP
                            ? ReferenceTargetKind.ITEM_GROUP : ReferenceTargetKind.ITEM;
                    ReferenceSemanticForm form = targetKind == ReferenceTargetKind.ITEM_GROUP
                            ? ReferenceSemanticForm.GROUP : ReferenceSemanticForm.DEFINITION;
                    String source = external.source() == null ? null : external.source().code();
                    String token = uid(external.name(), source);
                    ParsedUid parsed = ReferenceUidParser.parse(token);
                    references.add(reference(owner,
                            "startingOptions.equipment[" + groupIndex + "].branches[" + branchIndex
                                    + "].entries[" + entryIndex + "]",
                            targetKind, form, parsed, source,
                            targetKind == ReferenceTargetKind.ITEM_GROUP ? "Authored item group choice" : null));
                }
            }
        }
    }

    private static void extractSpells(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            ClassSpellcasting spellcasting) {
        if (spellcasting == null) {
            return;
        }
        for (int groupIndex = 0; groupIndex < spellcasting.additionalSpells().size(); groupIndex++) {
            var group = spellcasting.additionalSpells().get(groupIndex);
            for (int grantIndex = 0; grantIndex < group.grants().size(); grantIndex++) {
                var grant = group.grants().get(grantIndex);
                for (int selectionIndex = 0; selectionIndex < grant.selections().size(); selectionIndex++) {
                    ClassSpellSelection selection = grant.selections().get(selectionIndex);
                    String base = "spellcasting.additionalSpells[" + groupIndex + "].grants[" + grantIndex
                            + "].selections[" + selectionIndex + "]";
                    if (selection.spell() != null) {
                        addSpell(references, owner, base + ".spell", selection.spell(),
                                ReferenceSemanticForm.DEFINITION, null);
                    }
                    for (int candidateIndex = 0; candidateIndex < selection.from().size(); candidateIndex++) {
                        addSpell(references, owner, base + ".from[" + candidateIndex + "]",
                                selection.from().get(candidateIndex), ReferenceSemanticForm.CHOICE,
                                "Explicit spell choice candidate; not an unconditional grant");
                    }
                    if (selection.kind() == ClassSpellSelectionKind.ALL || selection.unrestricted()
                            || !selection.filter().isEmpty()) {
                        String token = filterToken(selection);
                        references.add(new AuthoredReference(owner, base + ".filter", ReferenceTargetKind.SPELL,
                                ReferenceSemanticForm.FILTER, token, null, null, null,
                                "Spell filter retained as a structured selector; candidates are not expanded"));
                    }
                }
            }
        }
    }

    private static String filterToken(ClassSpellSelection selection) {
        StringBuilder token = new StringBuilder(selection.kind().name().toLowerCase());
        if (selection.count() != null) {
            token.append(" count=").append(selection.count());
        }
        if (selection.unrestricted()) {
            token.append(" unrestricted");
        }
        selection.filter().forEach(clause -> token.append(" [")
                .append(clause.field()).append(":levels=").append(clause.levels())
                .append(":names=").append(clause.names())
                .append(":schools=").append(clause.schools())
                .append(":sources=").append(clause.sources().stream().map(value -> value.code()).toList())
                .append(']'));
        return token.toString();
    }

    private static void addSpell(
            List<AuthoredReference> references,
            ReferenceOwner owner,
            String locator,
            ClassSpellReference spell,
            ReferenceSemanticForm form,
            String reason) {
        String source = spell.source() == null ? null : spell.source().code();
        String token = uid(spell.name(), source);
        if (spell.configuration() != null) {
            token += "#" + spell.configuration();
        }
        ParsedUid parsed = ReferenceUidParser.parse(token);
        references.add(reference(owner, locator, ReferenceTargetKind.SPELL, form, parsed, source, reason));
    }

    private void extractBackgroundEquipment(List<AuthoredReference> references) {
        for (BackgroundDefinition definition : backgrounds.definitions()) {
            ReferenceOwner owner = new ReferenceOwner("background", definition.id().value());
            for (int packageIndex = 0; packageIndex < definition.startingEquipment().size(); packageIndex++) {
                var equipmentPackage = definition.startingEquipment().get(packageIndex);
                for (var option : equipmentPackage.options().entrySet()) {
                    for (int entryIndex = 0; entryIndex < option.getValue().size(); entryIndex++) {
                        BackgroundEquipmentEntryDte entry = option.getValue().get(entryIndex);
                        if (entry.item() == null) {
                            continue;
                        }
                        ParsedUid parsed = ReferenceUidParser.parse(entry.item());
                        references.add(reference(owner,
                                "startingEquipment[" + packageIndex + "].options[" + option.getKey()
                                        + "][" + entryIndex + "].item",
                                ReferenceTargetKind.ITEM, ReferenceSemanticForm.DEFINITION, parsed,
                                parsed.source(), null));
                    }
                }
            }
        }
    }

    private void extractBaseItemReferences(List<AuthoredReference> references) {
        for (var declaration : items.authoredBaseItemReferences()) {
            ParsedUid parsed = ReferenceUidParser.parse(declaration.originalUid());
            ReferenceOwner owner = new ReferenceOwner("item", declaration.ownerItemId());
            references.add(reference(owner, "baseItem", ReferenceTargetKind.BASE_ITEM,
                    ReferenceSemanticForm.DEFINITION, parsed, parsed.source(), null));
        }
    }

    private static AuthoredReference reference(
            ReferenceOwner owner,
            String locator,
            ReferenceTargetKind kind,
            ReferenceSemanticForm form,
            ParsedUid parsed,
            String originalSource,
            String reason) {
        return new AuthoredReference(owner, locator, kind, form, parsed.originalToken(), originalSource,
                parsed.name(), parsed.source(), reason);
    }

    private static String uid(String name, String source) {
        return source == null ? name : name + "|" + source;
    }
}
