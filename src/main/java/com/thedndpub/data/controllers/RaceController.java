package com.thedndpub.data.controllers;

import com.thedndpub.data.assemblers.RaceAssembler;
import com.thedndpub.data.dte.race.*;
import com.thedndpub.data.dto.*;
import com.thedndpub.data.services.RaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/races")
public class RaceController {

    private final RaceService raceService;
    private final RaceAssembler raceAssembler;

    public RaceController(RaceService raceService, RaceAssembler raceAssembler) {
        this.raceService = raceService;
        this.raceAssembler = raceAssembler;
    }

    @GetMapping
    public List<RaceDto> getRaces() {
        List<RaceDto> races = new ArrayList<>();

        List<RaceDte> copyRaces = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.get_copy() == null) {
                races.add(this.raceAssembler.mapRaceDteToDto(race));
            }
            else {
                copyRaces.add(race);
            }
        }

        for(RaceDte race : copyRaces) {
            RaceDto original = findOriginalRace(race, races);
            if(original != null) {
                races.add(this.raceAssembler.mapRaceCopyToDto(race, original));
            }
        }

        return races;
    }

    private RaceDto findOriginalRace(RaceDte dte, List<RaceDto> races) {
        for(RaceDto race : races) {
            if(race.getName().equals(dte.get_copy().getName())) {
                if(dte.get_copy().getSource().equals(race.getSource().getSource().toString())) {
                    return race;
                }
            }
        }
        System.out.println("Race not found: " + dte.get_copy().getName() + " - " + dte.get_copy().getSource());
        return null;
    }

    @GetMapping("/alleenmetentries")
    public List<RaceDto> getRacesAlleenEntries() {
        List<RaceDto> races = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            RaceDto dto = this.raceAssembler.mapRaceDteToDto(race);
            if(dto.getEntries() != null) {
                boolean has = false;
                for(EntryDto entryDto: dto.getEntries()) {
                    if (entryDto.getType().equals("subEntries")) {
                        for(EntryDto subEntry : ((EntryWithSubDto) entryDto).getSubItems()) {
//                            if(subEntry.getType().equals("table")) {
//                                has = true;
//                                break;
//                            }
                            if(subEntry.getName() == null) {
                                has = true;
                                break;
                            }
                        }
                        if(has) {
                            break;
                        }
                    }
                }
                if(has) {
                    races.add(dto);
                }
            }
        }
        return races;
    }

    @GetMapping("/unmapped")
    public List<RaceDte> getRacesUnmapped() {
        return raceService.getAllRaces();
    }

    @GetMapping("/name")
    public List<String> getRaceNames() {
        return raceService.getAllRaces().stream().map((race) -> race.getName() + " - " + race.getSource()).toList();
    }

    @GetMapping("/name/{name}")
    public List<RaceDto> getRaceOnName(@PathVariable("name") String name) {
        return this.getRaces().stream().filter(race -> race.getName().contains(name)).toList();
    }

    @GetMapping("/name/{name}/weapon")
    public List<RaceDto> getRaceWeaponsOnName(@PathVariable("name") String name) {
        return this.getRaces().stream().filter(race -> race.getWeaponProficiencies() != null).toList();
    }


    @GetMapping("/name/{name}/unmapped")
    public List<RaceDte> getRaceUnmappedOnName(@PathVariable("name") String name) {
        return raceService.getAllRaces().stream().filter(race -> race.getName().contains(name)).toList();
    }

    @GetMapping("/versions")
    public List<SubraceDte> getVersions() {
        List<SubraceDte> subraces = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.get_versions() != null) {
                for(SubraceDte raceVersion : race.get_versions()) {
                    raceVersion.setRaceName(race.getName());
                }
                subraces.addAll(race.get_versions());
            }
        }
        return subraces;
    }


    @GetMapping("/subraces")
    public List<SubraceDte> getSubraces() {
        return raceService.getAllSubRaces();
    }

    @GetMapping("/copy")
    public List<Object> getCopy() {
        List<Object> copies = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.get_copy() != null) {
                copies.add(race.get_copy());
            }
        }
        return copies;
    }

    @GetMapping("/spells")
    public List<AdditionalSpellDte> getAdditionalSpells() {
        List<AdditionalSpellDte> spells = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getAdditionalSpells() != null && !race.getAdditionalSpells().isEmpty()) {
                for(AdditionalSpellDte spell : race.getAdditionalSpells()) {
                    spell.setRaceName(race.getName());
                    spells.add(spell);
                }
            }
        }
        System.out.println("Aantal spells: " + spells.size());
        return spells;
    }

    @GetMapping("/speeds")
    public List<SpeedDte> getSpeeds() {
        List<SpeedDte> speeds = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getAdditionalSpells() != null && !race.getAdditionalSpells().isEmpty()) {
                speeds.add(race.getSpeed());
            }
        }
        return speeds;
    }

    @GetMapping("/abilities")
    public List<AbilityDte> getAbilities() {
        List<AbilityDte> abilities = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getAbility() != null && !race.getAbility().isEmpty()) {
                for(AbilityDte ability : race.getAbility()) {
                    ability.setName(race.getName());
                    if(ability.getChoose() != null && ability.getChoose().getCount() > 1) {
                        abilities.add(ability);
                    }
                }
            }
        }
        return abilities;
    }

    @GetMapping("/weapons")
    public List<WeaponProficiencyDte> getWeapons() {
        List<WeaponProficiencyDte> weapons = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getWeaponProficiencies() != null && !race.getWeaponProficiencies().isEmpty()) {
                for(WeaponProficiencyDte weapon : race.getWeaponProficiencies()) {
                    weapon.setName(race.getName());
                    if(weapon.isLightCrossbow()) {
                        weapons.add(weapon);
                    }
                }
            }
        }
        return weapons;
    }

    @GetMapping("/tools")
    public List<ToolProficiencyDte> getTools() {
        List<ToolProficiencyDte> tools = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getToolProficiencies() != null && !race.getToolProficiencies().isEmpty()) {
                for(ToolProficiencyDte tool : race.getToolProficiencies()) {
                    tool.setName(race.getName());
                    tools.add(tool);
                }
            }
        }
        return tools;
    }

    @GetMapping("/skills")
    public List<SkillProficiencyDte> getSkills() {
        List<SkillProficiencyDte> tools = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getSkillProficiencies() != null && !race.getSkillProficiencies().isEmpty()) {
                for(SkillProficiencyDte skill : race.getSkillProficiencies()) {
                    if(skill.getAny() > 0) {
                        skill.setName(race.getName());
                        tools.add(skill);
                    }
                }
            }
        }
        return tools;
    }

    @GetMapping("/languages")
    public List<LanguageProficiencyDte> getLanguages() {
        List<LanguageProficiencyDte> langs = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getLanguageProficiencies() != null && !race.getLanguageProficiencies().isEmpty()) {
                for(LanguageProficiencyDte lang : race.getLanguageProficiencies()) {
                    lang.setName(race.getName());
                    langs.add(lang);
                }
            }
        }
        return langs;
    }

    @GetMapping("/armor")
    public List<ArmorProficiencyDte> getArmor() {
        List<ArmorProficiencyDte> armors = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getArmorProficiencies() != null && !race.getArmorProficiencies().isEmpty()) {
                for(ArmorProficiencyDte armor : race.getArmorProficiencies()) {
                    armor.setName(race.getName());
                    armors.add(armor);
                }
            }
        }
        return armors;
    }

    @GetMapping("/source")
    public List<RaceDte> getSources() {
        List<RaceDte> sources = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getReprintedAs() != null && race.getReprintedAs().size() > 1) {
                sources.add(race);
            }
        }
        return sources;
    }

    @GetMapping("/traits")
    public List<String> getTypes() {
        List<String> types = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getTraitTags() != null && race.getTraitTags().size() > 1) {
                types.add(race.getName());
                types.addAll(race.getTraitTags());
            }
        }
        return types;
    }

    @GetMapping("/conditions")
    public List<String> getConditions() {
        List<String> types = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getImmune() != null) {
                types.add(race.getName());
                types.addAll(race.getImmune());
            }
        }
        return types;
    }

    @GetMapping("/feats")
    public List<FeatDte> getFeats() {
        List<FeatDte> feats = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getFeats() != null) {
                feats.addAll(race.getFeats());
            }
        }
        return feats;
    }

    @GetMapping("/sound")
    public List<SoundClipDte> getSounds() {
        List<SoundClipDte> sounds = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getSoundClip() != null) {
                race.getSoundClip().setName(race.getName());
                sounds.add(race.getSoundClip());
            }
        }
        return sounds;
    }

    @GetMapping("/entries")
    public List<EntryDte> getEntries() {
        List<EntryDte> entries = new ArrayList<>();
        for(RaceDte race : raceService.getAllRaces()) {
            if(race.getEntries() != null) {
                entries.addAll(race.getEntries());
                //754 entries
                //1 inset
                //3 text
                //758 total

                //SubEntries
                //0 entries (maar wel 2 met subsubentries)
                //17 list
                //7 table
                //2 inset
                //827 text

                //SubSubEntries
                //2 text
            }
        }

        int entryTypes = 0;
        int listTypes = 0;
        int tableTypes = 0;
        int abilityTypes = 0;
        int itemTypes = 0;
        int optionsTypes = 0;
        int insetTypes = 0;
        int textTypes = 0;
        int unknownTypes = 0;

        List<EntryDte> subEntries = new ArrayList<>();
        for(EntryDte entryDte : entries){
//            if (entryDte.getEntries() != null) {
//                subEntries.addAll(entryDte.getEntries());
//            }
//        }
//
//        List<EntryDte> subSubEntries = new ArrayList<>();
//        for(EntryDte entryDte : subEntries){
            if(entryDte.getEntries() != null) {
//                System.out.println("Met subSubEntries: " + entryDte.getName());
                subEntries.addAll(entryDte.getEntries());
            }
            if(entryDte.getType() != null) {
                switch (entryDte.getType()) {
                    case "entries":
                        entryTypes++;
                        break;
                    case "list":
                        listTypes++;
                        break;
                    case "table":
                        tableTypes++;
                        break;
                    case "ability":
                        abilityTypes++;
                        break;
                    case "item":
                        itemTypes++;
                        break;
                    case "options":
                        optionsTypes++;
                        break;
                    case "inset":
                        insetTypes++;
                        break;
                    default:
                        if (entryDte.getText() != null) {
                            textTypes++;
                        } else {
                            unknownTypes++;
                            System.out.println("Unknown type: " + entryDte.getType());
                        }
                        break;
                }
            }
            else {
                if (entryDte.getText() != null) {
                    textTypes++;
                    if(entryDte.getName() != null) {
                        System.out.println("Text type: " + entryDte.getName() + " - " + entryDte.getText());
                    }
                }
                else {
                    unknownTypes++;
                    System.out.println("Unknown type: " + entryDte.getName());
                }
            }
//            entryDte.setEntries(new ArrayList<>());
        }
        System.out.println("EntryTypes: " + entryTypes);
        System.out.println("ListTypes: " + listTypes);
        System.out.println("TableTypes: " + tableTypes);
        System.out.println("AbilityTypes: " + abilityTypes);
        System.out.println("ItemTypes: " + itemTypes);
        System.out.println("OptionsTypes: " + optionsTypes);
        System.out.println("InsetTypes: " + insetTypes);
        System.out.println("TextTypes: " + textTypes);
        System.out.println("UnkownTypes: " + unknownTypes);
        System.out.println("Total: " + entries.size());
        System.out.println("SubEntries: " + subEntries.size());
//        System.out.println("SubSubEntries: " + subSubEntries.size());

        return entries;
    }

    @GetMapping("/subraceentries")
    public List<EntryDte> getSubRaceEntries() {
        List<EntryDte> entries = new ArrayList<>();
        for(RaceDte race : raceService.getAllSubRaces()) {
            if(race.getEntries() != null) {
                entries.addAll(race.getEntries());
                //215 entries
                //4 inset
                //1 text
                //220 total

                //SubEntries
                //14 entries
                //1 list
                //15 table
                //233 text
                //263 total

                //SubSubEntries
                //2 text
            }
        }

        int entryTypes = 0;
        int listTypes = 0;
        int tableTypes = 0;
        int abilityTypes = 0;
        int itemTypes = 0;
        int optionsTypes = 0;
        int insetTypes = 0;
        int textTypes = 0;
        int unknownTypes = 0;

        List<EntryDte> subEntries = new ArrayList<>();
        List<EntryDte> subSubEntries = new ArrayList<>();

        for(EntryDte entryDte : entries) {
//            if (entryDte.getEntries() != null) {
//                subEntries.addAll(entryDte.getEntries());
//            }
//        }
//
//        for(EntryDte entryDte : subEntries){
            if (entryDte.getEntries() != null) {
                subEntries.addAll(entryDte.getEntries());
                System.out.println("Met subEntries: " + entryDte.getName());
            }
            if(entryDte.getType() != null) {
                switch (entryDte.getType()) {
                    case "entries":
                        entryTypes++;
                        break;
                    case "list":
                        listTypes++;
                        break;
                    case "table":
                        tableTypes++;
                        break;
                    case "ability":
                        abilityTypes++;
                        break;
                    case "item":
                        itemTypes++;
                        break;
                    case "options":
                        optionsTypes++;
                        break;
                    case "inset":
                        insetTypes++;
                        break;
                    default:
                        if (entryDte.getText() != null) {
                            textTypes++;
                        } else {
                            unknownTypes++;
                            System.out.println("Unknown type: " + entryDte.getType());
                        }
                        break;
                }
            }
            else {
                if (entryDte.getText() != null) {
                    textTypes++;
                    if(entryDte.getName() != null) {
                        System.out.println("Text type: " + entryDte.getName() + " - " + entryDte.getText());
                    }
                }
                else {
                    unknownTypes++;
                    System.out.println("Unknown type: " + entryDte.getName());
                }
            }
        }
        System.out.println("EntryTypes: " + entryTypes);
        System.out.println("ListTypes: " + listTypes);
        System.out.println("TableTypes: " + tableTypes);
        System.out.println("AbilityTypes: " + abilityTypes);
        System.out.println("ItemTypes: " + itemTypes);
        System.out.println("OptionsTypes: " + optionsTypes);
        System.out.println("InsetTypes: " + insetTypes);
        System.out.println("TextTypes: " + textTypes);
        System.out.println("UnkownTypes: " + unknownTypes);
        System.out.println("Total: " + entries.size());
        System.out.println("SubEntries: " + subEntries.size());
        System.out.println("SubSubEntries: " + subSubEntries.size());

        return entries;
    }
}