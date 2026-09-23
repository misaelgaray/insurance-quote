package com.clara.insurance_quote.quoting.controller;

import com.clara.insurance_quote.common.exception.GlobalExceptionHandler;
import com.clara.insurance_quote.quoting.entity.QuoteStatus;
import com.clara.insurance_quote.quoting.dto.CreateQuoteRequestDto;
import com.clara.insurance_quote.quoting.dto.QuoteResponseDto;
import com.clara.insurance_quote.quoting.dto.QuoteSummaryDto;
import com.clara.insurance_quote.quoting.dto.UpdateCoverageRequestDto;
import com.clara.insurance_quote.quoting.exception.IneligibleQuotingDataException;
import com.clara.insurance_quote.quoting.exception.QuoteSubmissionFailedException;
import com.clara.insurance_quote.quoting.service.QuoteService;
import com.clara.insurance_quote.underwriting.dto.SupplementalHealthResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class QuoteControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private QuoteService quoteService;

    @InjectMocks
    private QuoteController quoteController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        // Register GlobalExceptionHandler to process domain exceptions into ApiErrorDto JSON responses
        mockMvc = MockMvcBuilders.standaloneSetup(quoteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // -------------------------------------------------------------------------
    // 1. POST /api/v1/quotes (Create Quote)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/v1/quotes - Success (201 Created)")
    void createQuote_Success() throws Exception {
        CreateQuoteRequestDto request = new CreateQuoteRequestDto("Jane Doe", "jane@example.com", 35, "90210");
        UUID quoteId = UUID.randomUUID();

        QuoteResponseDto response = new QuoteResponseDto(
                quoteId, QuoteStatus.DRAFT, "Jane Doe", "jane@example.com",
                35, "90210", null, null, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(quoteService.createQuote(any(CreateQuoteRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(quoteId.toString()))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.applicantName").value("Jane Doe"));
    }

    @Test
    @DisplayName("POST /api/v1/quotes - Validation Error (400 Bad Request)")
    void createQuote_ValidationError() throws Exception {
        // Missing name and invalid email
        CreateQuoteRequestDto invalidRequest = new CreateQuoteRequestDto("", "invalid-email", 17, "");

        mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors.applicantName").exists())
                .andExpect(jsonPath("$.validationErrors.applicantEmail").exists());
    }

    // -------------------------------------------------------------------------
    // 2. GET /api/v1/quotes (List Quotes with optional filtering)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/quotes - Get All Quotes (200 OK)")
    void getAllQuotes_Success() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        List<QuoteResponseDto> quotes = List.of(
                new QuoteResponseDto(id1, QuoteStatus.DRAFT, "Alice", "alice@example.com", 30, "12345", null, null, OffsetDateTime.now(), OffsetDateTime.now()),
                new QuoteResponseDto(id2, QuoteStatus.SUBMITTED, "Bob", "bob@example.com", 45, "54321", "GOLD", new BigDecimal("150.00"), OffsetDateTime.now(), OffsetDateTime.now())
        );

        when(quoteService.getAllQuotes(null)).thenReturn(quotes);

        mockMvc.perform(get("/api/v1/quotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[1].status").value("SUBMITTED"));
    }

    @Test
    @DisplayName("GET /api/v1/quotes?status=DRAFT - Filter By Status (200 OK)")
    void getQuotes_FilteredByStatus() throws Exception {
        UUID id = UUID.randomUUID();
        List<QuoteResponseDto> filteredQuotes = List.of(
                new QuoteResponseDto(id, QuoteStatus.DRAFT, "Alice", "alice@example.com", 30, "12345", null, null, OffsetDateTime.now(), OffsetDateTime.now())
        );

        when(quoteService.getAllQuotes(QuoteStatus.DRAFT)).thenReturn(filteredQuotes);

        mockMvc.perform(get("/api/v1/quotes").param("status", "DRAFT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("DRAFT"));
    }

    // -------------------------------------------------------------------------
    // 3. GET /api/v1/quotes/{id} (Get Quote by ID)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/quotes/{id} - Success (200 OK)")
    void getQuoteById_Success() throws Exception {
        UUID quoteId = UUID.randomUUID();
        QuoteResponseDto response = new QuoteResponseDto(
                quoteId, QuoteStatus.DRAFT, "Charlie", "charlie@example.com",
                28, "10001", null, null, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(quoteService.getQuoteById(quoteId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/quotes/{id}", quoteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(quoteId.toString()))
                .andExpect(jsonPath("$.applicantName").value("Charlie"));
    }

    // -------------------------------------------------------------------------
    // 4. GET /api/v1/quotes/{id}/summary (Get Summary View)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/quotes/{id}/summary - Success (200 OK)")
    void getQuoteSummary_Success() throws Exception {
        UUID quoteId = UUID.randomUUID();
        SupplementalHealthResponseDto healthDto = new SupplementalHealthResponseDto(quoteId, true, false, true, false, List.of("HYPERTENSION"), OffsetDateTime.now(), OffsetDateTime.now());

        QuoteSummaryDto summaryDto = new QuoteSummaryDto(
                quoteId, QuoteStatus.DRAFT, "Senior Applicant", "senior@example.com",
                70, "90210", "GOLD", new BigDecimal("195.00"), healthDto,
                OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(quoteService.getQuoteSummaryById(quoteId)).thenReturn(summaryDto);

        mockMvc.perform(get("/api/v1/quotes/{id}/summary", quoteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(quoteId.toString()))
                .andExpect(jsonPath("$.coverageTypeCode").value("GOLD"))
                .andExpect(jsonPath("$.calculatedMonthlyPremium").value(195.00))
                .andExpect(jsonPath("$.supplementalHealth.hasPreexistingConditions").value(true));
    }

    // -------------------------------------------------------------------------
    // 5. PATCH /api/v1/quotes/{id}/coverage (Update Coverage)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("PATCH /api/v1/quotes/{id}/coverage - Success (200 OK)")
    void updateCoverage_Success() throws Exception {
        UUID quoteId = UUID.randomUUID();
        UpdateCoverageRequestDto request = new UpdateCoverageRequestDto("BASIC", false, null, false, false, false);

        QuoteResponseDto response = new QuoteResponseDto(
                quoteId, QuoteStatus.DRAFT, "Jane Doe", "jane@example.com",
                35, "90210", "BASIC", new BigDecimal("100.00"), OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(quoteService.updateCoverage(eq(quoteId), any(UpdateCoverageRequestDto.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/quotes/{id}/coverage", quoteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coverageTypeCode").value("BASIC"))
                .andExpect(jsonPath("$.calculatedMonthlyPremium").value(100.00));
    }

    @Test
    @DisplayName("PATCH /api/v1/quotes/{id}/coverage - Ineligible Health Data (400 Bad Request)")
    void updateCoverage_IneligibleDataError() throws Exception {
        UUID quoteId = UUID.randomUUID();
        UpdateCoverageRequestDto request = new UpdateCoverageRequestDto("BASIC", true, List.of("DIABETES"), false, false, false);

        when(quoteService.updateCoverage(eq(quoteId), any(UpdateCoverageRequestDto.class)))
                .thenThrow(new IneligibleQuotingDataException("Cannot receive data, user is less than 65."));

        mockMvc.perform(patch("/api/v1/quotes/{id}/coverage", quoteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Cannot receive data, user is less than 65."));
    }

    // -------------------------------------------------------------------------
    // 6. POST /api/v1/quotes/{id}/submit (Submit Quote)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/v1/quotes/{id}/submit - Success (200 OK)")
    void submitQuote_Success() throws Exception {
        UUID quoteId = UUID.randomUUID();
        QuoteResponseDto response = new QuoteResponseDto(
                quoteId, QuoteStatus.SUBMITTED, "Jane Doe", "jane@example.com",
                35, "90210", "BASIC", new BigDecimal("100.00"), OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(quoteService.submitQuote(quoteId)).thenReturn(response);

        mockMvc.perform(post("/api/v1/quotes/{id}/submit", quoteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    @DisplayName("POST /api/v1/quotes/{id}/submit - Upstream Provider Failure (502 Bad Gateway)")
    void submitQuote_SubmissionFailedError() throws Exception {
        UUID quoteId = UUID.randomUUID();

        when(quoteService.submitQuote(quoteId))
                .thenThrow(new QuoteSubmissionFailedException("Quote submission failed due to upstream provider error or timeout."));

        mockMvc.perform(post("/api/v1/quotes/{id}/submit", quoteId))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.message").value("Quote submission failed due to upstream provider error or timeout."));
    }
}