package com.clara.insurance_quote.quoting.repository;

import com.clara.insurance_quote.quoting.entity.QuoteStatus;
import com.clara.insurance_quote.quoting.entity.QuoteEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class QuoteRepositoryTest {

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should find quote IDs in DRAFT status created before threshold date")
    void findIdsByStatusAndUpdatedAtBefore_Success() {
        OffsetDateTime oldDate = OffsetDateTime.now().minusMinutes(40);

        QuoteEntity expiredDraft = QuoteEntity.builder()
                .status(QuoteStatus.DRAFT)
                .applicantName("Alice")
                .applicantEmail("alice@example.com")
                .applicantAge(30)
                .zipCode("12345")
                .updatedAt(oldDate)
                .build();

        entityManager.persist(expiredDraft);
        entityManager.flush();

        OffsetDateTime threshold = OffsetDateTime.now().minusMinutes(30);

        List<UUID> expiredIds = quoteRepository.findIdsByStatusAndUpdatedAtBefore(QuoteStatus.DRAFT, threshold);

        assertThat(expiredIds).hasSize(1);
        assertThat(expiredIds.get(0)).isEqualTo(expiredDraft.getId());
    }
}