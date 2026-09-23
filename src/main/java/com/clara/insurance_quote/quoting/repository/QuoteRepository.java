package com.clara.insurance_quote.quoting.repository;

import com.clara.insurance_quote.quoting.entity.QuoteEntity;
import com.clara.insurance_quote.quoting.entity.QuoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface QuoteRepository extends JpaRepository<QuoteEntity, UUID> {
    List<QuoteEntity> findByStatus(QuoteStatus status);

    // Find quotes in DRAFT updated/created before threshold date
    @Query("SELECT q.id FROM QuoteEntity q WHERE q.status = :status AND q.updatedAt < :threshold")
    List<UUID> findIdsByStatusAndUpdatedAtBefore(
            @Param("status") QuoteStatus status,
            @Param("threshold") OffsetDateTime threshold
    );

    // Single transactional batch update
    @Modifying
    @Query("UPDATE QuoteEntity q SET q.status = :newStatus, q.updatedAt = :now WHERE q.id IN :ids")
    int updateStatusForIds(
            @Param("ids") List<UUID> ids,
            @Param("newStatus") QuoteStatus newStatus,
            @Param("now") OffsetDateTime now
    );
}