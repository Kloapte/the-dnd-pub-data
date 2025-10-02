package com.thedndpub.data.controllers;

import com.thedndpub.data.assemblers.RaceAssembler;
import com.thedndpub.data.dto.EntryMapped;
import com.thedndpub.data.dto.Placeholder;
import com.thedndpub.data.dto.race.*;
import com.thedndpub.data.services.RaceService;
import com.thedndpub.data.util.records.RaceKey;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/races")
public class RaceController {

    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @GetMapping()
    public List<RaceDto> getRaces() {
        return this.raceService.getRaces().values().stream().toList();
    }

    @GetMapping("/{name}")
    public List<RaceDto> getRaceOnName(@PathVariable("name") String name) {
        List<RaceDto> races = new ArrayList<>();
        for(RaceDto race: this.getRaces()) {
            if(race.getName().toLowerCase().contains(name.toLowerCase())) {
                races.add(race);
            }
        }
        return races;
    }

    @GetMapping("/{name}/{source}")
    public RaceDto getRaceOnNameAndSource(@PathVariable("name") String name, @PathVariable("source") String source) {
        return this.raceService.getRaces().get(new RaceKey(name, source));
    }

    @GetMapping("/subraces")
    public List<RaceDto> getSubraces() {
        return this.raceService.getSubraces().values().stream().toList();
    }

    @GetMapping("/subraces/{name}")
    public List<RaceDto> getSubracesOnName(@PathVariable("name") String name) {
        List<RaceDto> races = new ArrayList<>();
        for(RaceDto race: this.getSubraces()) {
            if(race.getName().toLowerCase().contains(name.toLowerCase())) {
                races.add(race);
            }
        }
        return races;
    }

    @GetMapping("/subraces/{name}/{source}")
    public RaceDto getSubraceOnNameAndSource(@PathVariable("name") String subrace, @PathVariable("source") String source) {
        return this.raceService.getSubraces().get(new RaceKey(subrace, source));
    }

    @GetMapping("/variants")
    public List<RaceDto> getVariants() {
        return this.raceService.getVariants().values().stream().toList();
    }

    @GetMapping("/variants/{name}")
    public List<RaceDto> getVariants(@PathVariable("name") String name) {
        List<RaceDto> races = new ArrayList<>();
        for(RaceDto race: this.getVariants()) {
            if(race.getName().toLowerCase().contains(name.toLowerCase())) {
                races.add(race);
            }
        }
        return races;
    }

    @GetMapping("/variants/{name}/{source}")
    public RaceDto getVariantOnNameAndSource(@PathVariable("name") String subrace, @PathVariable("source") String source) {
        return this.raceService.getVariants().get(new RaceKey(subrace, source));
    }

    @GetMapping("/entries/all")
    public Map<String, List<EntryDto>> getAllEntries() {
        Map<String, List<EntryDto>> entries = new HashMap<>();
        for(RaceDto race : this.raceService.getRaces().values()) {
            entries.put(race.getName(), race.getEntries());
        };

        return entries;
    }

    @GetMapping("/entries/mapped")
    public Map<String, List<EntryMapped>> getAllEntriesMapped() {
        Map<String, List<EntryMapped>> entries = new HashMap<>();
        List<RaceDto> races = new ArrayList<>();
        races.addAll(this.raceService.getRaces().values().stream().toList());
        races.addAll(this.raceService.getSubraces().values().stream().toList());
        races.addAll(this.raceService.getVariants().values().stream().toList());

        for(RaceDto race : races) {
            List<EntryMapped> entriesMapped = new ArrayList<>();

            for(EntryDto entry : race.getEntries()) {
                if(entry.getName() != null) {
                    EntryMapped entryMapped = new EntryMapped();
                    entryMapped.setName(entry.getName());
                    if (entry.getName().equalsIgnoreCase("Size")){
                        //We can skip this since they all have sizeDto filled
                    }
                    else if(entry.getName().equalsIgnoreCase("Age")) {
//                        EntryMapped entryMapped = new EntryMapped();
//                        entryMapped.setAge(((EntryTextDto)entry).getTextItems().getFirst());
//                        entriesMapped.add(entryMapped);
                        //We can skip this since they all have age filled (execpt 2 but not describing)
                    }
                    else if(entry.getName().equalsIgnoreCase("powerful build")) {
                        entryMapped.setPowerfulBuild(true);

                        List<Placeholder> placeholders = getPlaceholdersFromText(((EntryTextDto)entry).getTextItems().getFirst());
                        if(!placeholders.isEmpty()) {
                            entryMapped.setPlaceholders(placeholders);
                        }

                        entryMapped.setDecoded(true);
                        entriesMapped.add(entryMapped);
                    }
                    else if(entry.getName().equalsIgnoreCase("darkvision")) {
                        if(race.getVision() == null) {
                            String text = ((EntryTextDto) entry).getTextItems().getFirst();
                            if(text.contains(("60 feet"))) {
                                entryMapped.setDarkvision(60);
                                entryMapped.setDecoded(true);
                                entriesMapped.add(entryMapped);
                            }
                            else {
                                throw new RuntimeException("Darkvision not found");
                            }
                        }
                    }
                    else if(entry.getName().equalsIgnoreCase("alignment")) {
                        String text = ((EntryTextDto) entry).getTextItems().getFirst().toLowerCase();
                        entryMapped.setText(text);
                        entryMapped.setAllignments(new ArrayList<>());
                        if(text.toLowerCase().contains("no particular alignment")) {
                            entryMapped.getAllignments().add("none");
                        }
                        if(text.toLowerCase().contains("lawful")) {
                            entryMapped.getAllignments().add("lawful");
                        }
                        if(text.toLowerCase().contains("good")) {
                            entryMapped.getAllignments().add("good");
                        }
                        if(text.toLowerCase().contains("evil")) {
                            entryMapped.getAllignments().add("evil");
                        }
                        if(text.toLowerCase().contains("neutral")) {
                            entryMapped.getAllignments().add("neutral");
                        }
                        if(text.toLowerCase().contains("chaotic")  || text.toLowerCase().contains("chaos")) {
                            entryMapped.getAllignments().add("chaotic");
                        }

                        entryMapped.setDecoded(true);

                        entriesMapped.add(entryMapped);
                    }
                    else if(entry.getName().equalsIgnoreCase("languages")) {
                        LanguageProficiencyDto languageProficiencyDto = new LanguageProficiencyDto();
                        languageProficiencyDto.setProficiencies(new ArrayList<>());
                        String text = ((EntryTextDto) entry).getTextItems().getFirst().toLowerCase();
                        entryMapped.setText(text);

                        for (LanguageProficiencyType language : LanguageProficiencyType.values()) {
                            if (!language.equals(LanguageProficiencyType.OTHER) && text.contains(language.getDescription().toLowerCase())) {
                                languageProficiencyDto.getProficiencies().add(language);
                            }
                        }

                        if(text.contains("language of your choice") || text.contains("one other language") || text.contains("languages of your choice")) {
                            ChoiceDto choose = new ChoiceDto();
                            choose.setFrom(new ArrayList<>());
                            for(LanguageProficiencyType type : LanguageProficiencyType.values()) {
                                choose.getFrom().add(type.name());
                                if(text.contains("two")) {
                                    choose.setAmountOfBonus(2);
                                }
                                else {
                                    choose.setAmountOfBonus(1);
                                }
                            }
                            languageProficiencyDto.setChoices(List.of(choose));
                        }

                        if(text.contains("you can't speak, but you can understand the languages you knew in life.")) {
                            languageProficiencyDto.getProficiencies().add(LanguageProficiencyType.ZOMBIE);
                        }

                        entryMapped.setLanguages(languageProficiencyDto);
                        entryMapped.setDecoded(true);

                        entriesMapped.add(entryMapped);
                    }
                    //speed?
                    //Damage resistance / resillience
                    //Creature Type
                    //Swimming / Swim Speed
                    //Flight
                    //Superior Darkvision
                    //Keen Senses
                    //Breath Weapon
                    //Extra Language
                    //Fey Ancestry
                    else if(entry instanceof EntryTextDto) {
                        String text = ((EntryTextDto)entry).getTextItems().getFirst();
                        if(text == null) {
                            //TODO skip for now, needs work on subraces half-elf
                            entryMapped.setDecoded(true);
                        }
                        else {
                            List<Placeholder> placeholders = getPlaceholdersFromText(text);
                            if (!placeholders.isEmpty()) {
                                entryMapped.setPlaceholders(placeholders);
                                entryMapped.setText(text);
                                entryMapped.setDecoded(true);
                            }
                            else {
                                entryMapped.setText(text);
//                                entryMapped.setDecoded(true);
                            }

                            if(text.contains("proficiency")) {
                                entryMapped.setProficiency(true);
                            }
                        }

                        entriesMapped.add(entryMapped);
                    }
                    else {
                        //TODO
                    }
                }
            }

            if(!entriesMapped.isEmpty()) {
                List<EntryMapped> mapped = entriesMapped.stream().filter((entry) -> !entry.isDecoded()).toList();
                if(!mapped.isEmpty()) {
                    entries.put(race.getName() + "(" + race.getSource().getSource() + ")", mapped);
                }
            }
        };
        System.out.println("Entries: " + entries.size());
        return entries;
    }

    private List<Placeholder> getPlaceholdersFromText(String text) {
        Pattern pattern = Pattern.compile("\\{@([a-zA-Z]+)\\s+([^|}]+)\\|([^}]+)}");
        Matcher matcher = pattern.matcher(text);

        List<Placeholder> placeholders = new ArrayList<>();

        while(matcher.find()) {
            Placeholder placeholder = new Placeholder();
            placeholder.setType(matcher.group(1));
            placeholder.setLabel(matcher.group(2));
            placeholders.add(placeholder);
        }

        return placeholders;
    }
}