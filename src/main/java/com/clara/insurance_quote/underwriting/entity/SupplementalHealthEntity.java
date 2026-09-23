package com.clara.insurance_quote.underwriting.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "supplemental_health", schema = "underwriting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplementalHealthEntity {

    @Id
    @Column(name = "quote_id", nullable = false)
    private UUID quoteId;

    @Column(name = "has_preexisting_conditions", nullable = false)
    private Boolean hasPreexistingConditions;

    @Column(name = "takes_prescription_medication", nullable = false)
    private Boolean takesPrescriptionMedication;

    @Column(name = "uses_tobacco", nullable = false)
    private Boolean usesTobacco;

    @Column(name = "has_spouse_coverage", nullable = false)
    private Boolean hasSpouseCoverage;

    @Column(name = "conditions")
    private String conditions; // Comma-separated condition codes (e.g., "DIABETES,HEART_DISEASE")

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}