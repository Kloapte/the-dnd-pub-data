package com.thedndpub.data.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.monsters.MonsterDte;
import com.thedndpub.data.dte.monsters.MonstersWrapperDte;
import com.thedndpub.data.util.records.ItemKey;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.stream.Stream;

@Service
public class MonstersService {
    private final ObjectMapper mapper;

    @Getter
    private final HashMap<ItemKey, MonsterDte> monsters;

    public MonstersService(ObjectMapper objectMapper) throws IOException {
        this.mapper = objectMapper;
        this.monsters = makeMonsters(loadAllMonsters());
    }

    private HashMap<ItemKey, MonsterDte> makeMonsters(List<MonsterDte> monstersIn) {
        HashMap<ItemKey, MonsterDte> monsters = new HashMap<>();

        for(MonsterDte monsterDte : monstersIn) {
            ItemKey key = new ItemKey(monsterDte.name(), monsterDte.source());
            monsters.put(key, monsterDte);
        }

        return monsters;
    }

    private List<MonsterDte> loadAllMonsters() throws IOException {
        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();

        Resource[] resources =
                resolver.getResources("classpath:data/monsters/bestiary-*.json");

        Arrays.sort(resources, Comparator.comparing(Resource::getFilename));

        List<MonsterDte> monsters = new ArrayList<>();

        for (Resource resource : resources) {
            try (InputStream inputStream = resource.getInputStream()) {
                MonstersWrapperDte wrapper =
                        mapper.readValue(inputStream, MonstersWrapperDte.class);

                monsters.addAll(wrapper.getMonster());
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to load bestiary file: " + resource.getFilename(),
                        e
                );
            }
        }

        return monsters;
    }
}
