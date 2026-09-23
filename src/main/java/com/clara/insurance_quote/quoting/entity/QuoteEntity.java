package com.clara.insurance_quote.quoting.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "quote", schema = "quoting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QuoteStatus status; // DRAFT, SUBMITTED, SUBMISSION_FAILED, EXPIRED

    @Column(name = "applicant_name", nullable = false)
    private String applicantName;

    @Column(name = "applicant_email", nullable = false)
    private String applicantEmail;

    @Column(name = "applicant_age", nullable = false)
    private Integer applicantAge;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    // Reference by ID/Code to catalogs domain (No JPA @ManyToOne)
    @Column(name = "coverage_type_code", length = 50)
    private String coverageTypeCode;

    @Column(name = "calculated_monthly_premium", precision = 10, scale = 2)
    private BigDecimal calculatedMonthlyPremium;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
