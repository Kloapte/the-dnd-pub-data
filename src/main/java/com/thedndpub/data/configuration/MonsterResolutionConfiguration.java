package com.thedndpub.data.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.monster.MonsterMapper;
import com.thedndpub.data.dte.monster.DefaultMonsterCopyResolver;
import com.thedndpub.data.dte.monster.MonsterCopyResolver;
import com.thedndpub.data.dte.monster.MonsterTemplateRepository;
import com.thedndpub.data.dte.monster.MonsterVersionExpander;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class MonsterResolutionConfiguration {
    @Bean
    ObjectMapper monsterObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    MonsterTemplateRepository monsterTemplateRepository(ObjectMapper objectMapper) {
        return MonsterTemplateRepository.fromResource(objectMapper);
    }

    @Bean
    MonsterCopyResolver monsterCopyResolver(
            ObjectMapper objectMapper,
            MonsterTemplateRepository templateRepository) {
        return new DefaultMonsterCopyResolver(objectMapper, templateRepository);
    }

    @Bean
    MonsterVersionExpander monsterVersionExpander(
            ObjectMapper objectMapper,
            MonsterCopyResolver copyResolver) {
        return new MonsterVersionExpander(objectMapper, copyResolver);
    }

    @Bean
    MonsterMapper monsterMapper(ObjectMapper objectMapper) {
        return new MonsterMapper(objectMapper);
    }
}
