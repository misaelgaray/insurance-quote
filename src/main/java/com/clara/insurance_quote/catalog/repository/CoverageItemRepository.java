package com.clara.insurance_quote.catalog.repository;

import com.clara.insurance_quote.catalog.entity.CoverageItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CoverageItemRepository extends JpaRepository<CoverageItemEntity, UUID> {
    List<CoverageItemEntity> findByActiveTrue();
    List<CoverageItemEntity> findByCoverageTypeIdInAndActiveTrue(List<String> typeCodes);
}
