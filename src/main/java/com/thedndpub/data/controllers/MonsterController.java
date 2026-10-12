
package com.thedndpub.data.controllers;

import com.thedndpub.data.dte.monsters.MonsterDte;
import com.thedndpub.data.services.MonstersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/monsters")
@RequiredArgsConstructor
public class MonsterController {
    private final MonstersService monstersService;
    
    @GetMapping()
    public List<MonsterDte> getMonsters() {
        return this.monstersService.getMonsters().values().stream().toList();
    }

    @GetMapping("/{name}")
    public List<MonsterDte> getMonsterOnName(@PathVariable("name") String name) {
        List<MonsterDte> monsters = new ArrayList<>();
        for(MonsterDte monster: this.getMonsters()) {
            if(monster.name().toLowerCase().contains(name.toLowerCase())) {
                monsters.add(monster);
            }
        }
        return monsters;
    }

    @GetMapping("/alignment")
    public List<String> getAlignments() {
        List<String> alignments = new ArrayList<>();

        for(MonsterDte monster : this.getMonsters()) {
            for(String alignment : monster.alignment()) {
                if(!alignments.contains(alignment)) {
                    alignments.add(alignment);
                }
            }
        }

        return alignments;
    }
}
