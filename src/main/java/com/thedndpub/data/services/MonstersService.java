package com.thedndpub.data.services;

import com.thedndpub.data.dto.monster.MonsterDto;
import com.thedndpub.data.dto.monster.MonsterSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Search and pagination operations over the immutable in-memory monster catalog.
 */
@Service
public final class MonstersService {
    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final int MAX_PAGE_SIZE = 100;

    private final MonsterCatalog catalog;

    public MonstersService(MonsterCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    public Page<MonsterSummaryDto> findMonsters(String name, String source, Pageable pageable) {
        validatePageable(pageable);

        String nameFilter = normalizeFilter(name);
        String sourceFilter = normalizeFilter(source);
        List<MonsterSummaryDto> filtered = catalog.summaries().stream()
                .filter(summary -> nameFilter == null
                        || summary.name().toLowerCase(Locale.ROOT).contains(nameFilter))
                .filter(summary -> sourceFilter == null
                        || summary.source().equalsIgnoreCase(sourceFilter))
                .sorted(summaryComparator(pageable.getSort()))
                .toList();

        long offset = pageable.getOffset();
        if (offset >= filtered.size()) {
            return new PageImpl<>(List.of(), pageable, filtered.size());
        }

        int start = Math.toIntExact(offset);
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        return new PageImpl<>(filtered.subList(start, end), pageable, filtered.size());
    }

    public MonsterDto findById(String id) {
        MonsterDto monster = catalog.findById(id);
        if (monster == null) {
            throw new MonsterNotFoundException("No monster found with ID '" + id + "'");
        }
        return monster;
    }

    public MonsterDto findByNameAndSource(String name, String source) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Monster name is required");
        }
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Monster source is required");
        }

        MonsterDto monster = catalog.findByNameAndSource(name, source);
        if (monster == null) {
            throw new MonsterNotFoundException(
                    "No monster found with name '" + name + "' and source '" + source + "'");
        }
        return monster;
    }

    private static void validatePageable(Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            throw new IllegalArgumentException("A paged request is required");
        }
        if (pageable.getPageNumber() < 0) {
            throw new IllegalArgumentException("Page number must be zero or greater");
        }
        if (pageable.getPageSize() < 1 || pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }

        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            if (!property.equals("name") && !property.equals("source")) {
                throw new IllegalArgumentException("Unsupported sort property '" + property + "'");
            }
        }
    }

    private static Comparator<MonsterSummaryDto> summaryComparator(Sort sort) {
        List<Comparator<MonsterSummaryDto>> comparators = new ArrayList<>();
        for (Sort.Order order : sort) {
            Comparator<MonsterSummaryDto> comparator = switch (order.getProperty()) {
                case "name" -> Comparator.comparing(
                        MonsterSummaryDto::name,
                        String.CASE_INSENSITIVE_ORDER);
                case "source" -> Comparator.comparing(
                        MonsterSummaryDto::source,
                        String.CASE_INSENSITIVE_ORDER);
                default -> throw new IllegalArgumentException(
                        "Unsupported sort property '" + order.getProperty() + "'");
            };
            comparators.add(order.isAscending() ? comparator : comparator.reversed());
        }

        if (comparators.isEmpty()) {
            comparators.add(Comparator.comparing(
                    MonsterSummaryDto::name,
                    String.CASE_INSENSITIVE_ORDER));
            comparators.add(Comparator.comparing(
                    MonsterSummaryDto::source,
                    String.CASE_INSENSITIVE_ORDER));
        }

        comparators.add(Comparator.comparing(summary -> summary.id().value()));
        return comparators.stream().reduce(Comparator::thenComparing).orElseThrow();
    }

    private static String normalizeFilter(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
