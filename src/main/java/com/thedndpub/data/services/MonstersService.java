package com.thedndpub.data.services;

import tools.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.monsters.MonsterDte;
import com.thedndpub.data.dte.monsters.MonstersWrapperDte;
import com.thedndpub.data.util.records.ItemKey;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;

@Service
public class MonstersService {
    @Getter
    private final HashMap<ItemKey, MonsterDte> monsters;

    public MonstersService() {
//        MonstersWrapperDte wrapper = this.loadItemsFromJson();
        this.monsters = null;
//        this.monsters = makeMonsters(wrapper);
    }

    private HashMap<ItemKey, MonsterDte> makeMonsters(MonstersWrapperDte wrapper) {
        HashMap<ItemKey, MonsterDte> monsters = new HashMap<>();

        for(MonsterDte monsterDte : wrapper.monster()) {
            ItemKey key = new ItemKey(monsterDte.name(), monsterDte.source());
            monsters.put(key, monsterDte);
        }

        return monsters;
    }

    private MonstersWrapperDte loadItemsFromJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("data/monsters/bestiary-phb.json").getInputStream();
            return mapper.readValue(inputStream, MonstersWrapperDte.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load bestiary-phb.json", e);
        }
    }
}
