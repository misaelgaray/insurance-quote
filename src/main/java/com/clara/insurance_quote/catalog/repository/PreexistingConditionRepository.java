package com.clara.insurance_quote.catalog.repository;

import com.clara.insurance_quote.catalog.entity.PreexistingConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PreexistingConditionRepository extends JpaRepository<PreexistingConditionEntity, String> {
    List<PreexistingConditionEntity> findByActiveTrue();
}
