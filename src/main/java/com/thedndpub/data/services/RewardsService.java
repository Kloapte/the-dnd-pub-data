package com.thedndpub.data.services;

import com.thedndpub.data.dto.reward.RewardDefinition;
import com.thedndpub.data.dto.reward.RewardDetailDto;
import com.thedndpub.data.dto.reward.RewardMapper;
import com.thedndpub.data.dto.reward.RewardSummaryDto;
import com.thedndpub.data.references.PublicReferenceProjection;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public final class RewardsService {
    private final RewardCatalog catalog;
    private final RewardMapper mapper;
    private final PublicReferenceProjection references;

    public RewardsService(RewardCatalog catalog, RewardMapper mapper, PublicReferenceProjection references) {
        this.catalog = catalog;
        this.mapper = mapper;
        this.references = references;
    }

    public List<RewardSummaryDto> findAll(String source, String type) {
        return catalog.summaries().stream()
                .filter(value -> source == null || value.source().code().equalsIgnoreCase(source.trim()))
                .filter(value -> type == null || value.type().equalsIgnoreCase(type.trim()))
                .toList();
    }

    public RewardDetailDto findById(String id) {
        RewardDefinition definition = catalog.findById(id);
        if (definition == null) {
            throw new RewardNotFoundException(id);
        }
        return mapper.detail(definition).withReferences(references.forOwner("reward", id));
    }

    public List<RewardSummaryDto> findByName(String name, String source) {
        return catalog.findByName(name).stream()
                .filter(definition -> source == null
                        || definition.source().identity().code().equalsIgnoreCase(source.trim()))
                .map(RewardSummaryDto::from)
                .toList();
    }
}
