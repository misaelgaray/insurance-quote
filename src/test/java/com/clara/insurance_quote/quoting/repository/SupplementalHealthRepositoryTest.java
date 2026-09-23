package com.clara.insurance_quote.quoting.repository;

import com.clara.insurance_quote.underwriting.entity.SupplementalHealthEntity;
import com.clara.insurance_quote.underwriting.repository.SupplementalHealthRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class SupplementalHealthRepositoryTest {

    @Autowired
    private SupplementalHealthRepository supplementalHealthRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should save and retrieve SupplementalHealthEntity by quoteId")
    void findByQuoteId_Success() {
        // Given
        UUID quoteId = UUID.randomUUID();
        SupplementalHealthEntity healthEntity = SupplementalHealthEntity.builder()
                .quoteId(quoteId)
                .hasPreexistingConditions(true)
                .takesPrescriptionMedication(false)
                .usesTobacco(true)
                .hasSpouseCoverage(false)
                .conditions("HYPERTENSION,DIABETES")
                .build();

        entityManager.persist(healthEntity);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<SupplementalHealthEntity> result = supplementalHealthRepository.findById(quoteId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getQuoteId()).isEqualTo(quoteId);
        assertThat(result.get().getHasPreexistingConditions()).isTrue();
        assertThat(result.get().getUsesTobacco()).isTrue();
        assertThat(result.get().getConditions()).contains("HYPERTENSION", "DIABETES");
    }

    @Test
    @DisplayName("Should return empty Optional when no health record exists for given quoteId")
    void findByQuoteId_NotFound() {
        // Given
        UUID nonExistentQuoteId = UUID.randomUUID();

        // When
        Optional<SupplementalHealthEntity> result = supplementalHealthRepository.findById(nonExistentQuoteId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should overwrite supplemental health record on update")
    void saveOrUpdate_Success() {
        // Given
        UUID quoteId = UUID.randomUUID();
        SupplementalHealthEntity initialEntity = SupplementalHealthEntity.builder()
                .quoteId(quoteId)
                .hasPreexistingConditions(false)
                .usesTobacco(false)
                .build();

        entityManager.persist(initialEntity);
        entityManager.flush();

        // When
        initialEntity.setHasPreexistingConditions(true);
        initialEntity.setConditions("ASTHMA");
        supplementalHealthRepository.save(initialEntity);
        entityManager.flush();
        entityManager.clear();

        // Then
        SupplementalHealthEntity updated = entityManager.find(SupplementalHealthEntity.class, initialEntity.getQuoteId());
        assertThat(updated.getHasPreexistingConditions()).isTrue();
        assertThat(updated.getConditions()).contains("ASTHMA");
    }
}
