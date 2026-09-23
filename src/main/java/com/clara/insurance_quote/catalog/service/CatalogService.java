package com.clara.insurance_quote.catalog.service;

import com.clara.insurance_quote.catalog.dto.CoverageItemDto;
import com.clara.insurance_quote.catalog.dto.CoverageTypeDto;
import com.clara.insurance_quote.catalog.dto.PreexistingConditionDto;
import com.clara.insurance_quote.catalog.entity.CoverageItemEntity;
import com.clara.insurance_quote.catalog.entity.CoverageTypeEntity;
import com.clara.insurance_quote.catalog.repository.CoverageItemRepository;
import com.clara.insurance_quote.catalog.repository.CoverageTypeRepository;
import com.clara.insurance_quote.catalog.repository.PreexistingConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogService {

    private final CoverageTypeRepository coverageTypeRepository;
    private final PreexistingConditionRepository preexistingConditionRepository;
    private final CoverageItemRepository coverageItemRepository;

    @Transactional(readOnly = true)
    public List<CoverageTypeDto> getAllCoverageTypesWithItems() {
        // 1. Fetch all coverage types
        List<CoverageTypeEntity> coverageTypes = coverageTypeRepository.findAll();

        if (coverageTypes.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Extract all type codes
        List<String> typeCodes = coverageTypes.stream()
                .map(CoverageTypeEntity::getCode)
                .toList();

        // 3. Fetch all active items matching those type codes in one query
        List<CoverageItemEntity> items = coverageItemRepository.findByCoverageTypeIdInAndActiveTrue(typeCodes);

        // 4. Group items by their coverage type code
        Map<String, List<CoverageItemDto>> itemsByTypeCode = items.stream()
                .collect(Collectors.groupingBy(
                        CoverageItemEntity::getCoverageTypeId,
                        Collectors.mapping(
                                item -> new CoverageItemDto(
                                        item.getId(),
                                        item.getName(),
                                        item.getDescription(),
                                        item.getActive()
                                ),
                                Collectors.toList()
                        )
                ));

        // 5. Join them into the final DTO list
        return coverageTypes.stream()
                .map(type -> new CoverageTypeDto(
                        type.getCode(),
                        type.getName(),
                        type.getBasePremium(),
                        itemsByTypeCode.getOrDefault(type.getCode(), Collections.emptyList())
                ))
                .toList();
    }

    public List<PreexistingConditionDto> getActivePreexistingConditions() {
        return preexistingConditionRepository.findByActiveTrue().stream()
                .map(pc -> new PreexistingConditionDto(pc.getCode(), pc.getName(), pc.getActive()))
                .toList();
    }

    public List<CoverageItemDto> getActiveCoverageItems() {
        return coverageItemRepository.findByActiveTrue().stream()
                .map(ci -> new CoverageItemDto(ci.getId(), ci.getName(), ci.getDescription(), ci.getActive()))
                .toList();
    }
}
