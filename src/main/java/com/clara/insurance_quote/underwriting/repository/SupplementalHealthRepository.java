package com.clara.insurance_quote.underwriting.repository;

import com.clara.insurance_quote.underwriting.entity.SupplementalHealthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupplementalHealthRepository extends JpaRepository<SupplementalHealthEntity, UUID> {
}