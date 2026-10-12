package com.thedndpub.data.controllers;

import com.thedndpub.data.dto.monster.MonsterDto;
import com.thedndpub.data.dto.monster.MonsterSummaryDto;
import com.thedndpub.data.dto.monster.PagedResponse;
import com.thedndpub.data.services.MonstersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/monsters")
public class MonsterController {
    private static final int MAX_PAGE_SIZE = 100;

    private final MonstersService monstersService;

    public MonsterController(MonstersService monstersService) {
        this.monstersService = monstersService;
    }

    @GetMapping
    public PagedResponse<MonsterSummaryDto> findMonsters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + MonstersService.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String source,
            HttpServletRequest request) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number must be zero or greater");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }

        Sort requestedSort = parseSort(request.getParameterValues("sort"));
        Page<MonsterSummaryDto> result = monstersService.findMonsters(
                name,
                source,
                PageRequest.of(page, size, requestedSort));
        return new PagedResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    @GetMapping("/{id}")
    public MonsterDto findById(@PathVariable String id) {
        return monstersService.findById(id);
    }

    @GetMapping("/by-name/{name}")
    public MonsterDto findByNameAndSource(
            @PathVariable String name,
            @RequestParam String source) {
        return monstersService.findByNameAndSource(name, source);
    }

    private static Sort parseSort(String[] requestedSort) {
        if (requestedSort == null || requestedSort.length == 0) {
            return Sort.by(
                    Sort.Order.asc("name"),
                    Sort.Order.asc("source"));
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortExpression : requestedSort) {
            String[] parts = sortExpression.split(",", -1);
            if (parts.length < 1 || parts.length > 2 || parts[0].isBlank()) {
                throw new IllegalArgumentException("Sort must use property[,asc|desc] format");
            }

            String property = parts[0].trim();
            if (!property.equals("name") && !property.equals("source")) {
                throw new IllegalArgumentException("Unsupported sort property '" + property + "'");
            }

            Sort.Direction direction = Sort.Direction.ASC;
            if (parts.length == 2) {
                try {
                    direction = Sort.Direction.fromString(parts[1].trim());
                } catch (IllegalArgumentException exception) {
                    throw new IllegalArgumentException(
                            "Sort direction must be 'asc' or 'desc' for property '" + property + "'");
                }
            }
            orders.add(new Sort.Order(direction, property));
        }

        return Sort.by(orders);
    }
}
