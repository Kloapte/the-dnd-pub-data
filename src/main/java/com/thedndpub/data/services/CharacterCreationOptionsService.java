package com.thedndpub.data.services;

import com.thedndpub.data.dto.charoption.CharacterCreationOptionDto;
import com.thedndpub.data.dto.charoption.CharacterCreationOptionSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public final class CharacterCreationOptionsService {
    private final CharacterCreationOptionCatalog catalog;

    public CharacterCreationOptionsService(CharacterCreationOptionCatalog catalog) {
        this.catalog = catalog;
    }

    public List<CharacterCreationOptionSummaryDto> find(String name, String source, String optionType) {
        String normalizedName = normalize(name);
        String normalizedSource = normalize(source);
        String normalizedType = normalize(optionType);
        return catalog.summaries().stream()
                .filter(option -> normalizedName == null
                        || option.name().toLowerCase(Locale.ROOT).contains(normalizedName))
                .filter(option -> normalizedSource == null
                        || option.source().identity().code().toLowerCase(Locale.ROOT).equals(normalizedSource))
                .filter(option -> normalizedType == null || option.optionTypes().stream()
                        .anyMatch(value -> value.toLowerCase(Locale.ROOT).equals(normalizedType)))
                .toList();
    }

    public CharacterCreationOptionDto findById(String id) {
        var definition = catalog.findById(id);
        if (definition == null) {
            throw new CharacterCreationOptionNotFoundException("No Character Creation Option found with ID '" + id
                    + "'");
        }
        return CharacterCreationOptionDto.from(definition);
    }

    public List<CharacterCreationOptionDto> findByName(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Character Creation Option name is required");
        }
        String normalizedSource = normalize(source);
        List<CharacterCreationOptionDto> matches = catalog.findByName(name).stream()
                .filter(definition -> normalizedSource == null
                        || definition.source().identity().code().toLowerCase(Locale.ROOT).equals(normalizedSource))
                .map(CharacterCreationOptionDto::from)
                .toList();
        if (matches.isEmpty()) {
            throw new CharacterCreationOptionNotFoundException(
                    "No Character Creation Option found with name '" + name + "'");
        }
        return matches;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
