package com.clara.insurance_quote.quoting.job;

import com.clara.insurance_quote.quoting.entity.QuoteStatus;
import com.clara.insurance_quote.quoting.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DraftExpirationJob {

    private final QuoteRepository quoteRepository;
    private final CacheManager cacheManager;

    @Value("${app.quoting.draft-expiration.window-minutes:30}")
    private long expirationWindowMinutes;

    @Scheduled(cron = "${app.quoting.draft-expiration.cron:0 */5 * * * *}")
    @Transactional
    public void expireOldDraftQuotes() {
        OffsetDateTime threshold = OffsetDateTime.now().minusMinutes(expirationWindowMinutes);

        // 1. Find all quote IDs eligible for expiration
        List<UUID> expiredCandidateIds = quoteRepository.findIdsByStatusAndUpdatedAtBefore(
                QuoteStatus.DRAFT,
                threshold
        );

        if (expiredCandidateIds.isEmpty()) {
            return;
        }

        log.info("Found {} draft quote(s) exceeding expiration window of {} minutes. Expiring...",
                expiredCandidateIds.size(), expirationWindowMinutes);

        // 2. Perform single transactional batch update
        int updatedCount = quoteRepository.updateStatusForIds(
                expiredCandidateIds,
                QuoteStatus.EXPIRED,
                OffsetDateTime.now()
        );

        // 3. Invalidate cache entries for expired quotes
        Cache quotesCache = cacheManager.getCache("quotes");
        if (quotesCache != null) {
            for (UUID id : expiredCandidateIds) {
                quotesCache.evict(id);
            }
        }

        log.info("Successfully expired {} draft quote(s) and invalidated cache entries.", updatedCount);
    }
}