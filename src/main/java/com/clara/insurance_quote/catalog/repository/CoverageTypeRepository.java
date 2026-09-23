package com.clara.insurance_quote.catalog.repository;

import com.clara.insurance_quote.catalog.entity.CoverageTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoverageTypeRepository extends JpaRepository<CoverageTypeEntity, String> {

}
