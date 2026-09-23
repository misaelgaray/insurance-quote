package com.clara.insurance_quote.quoting.dto;

import jakarta.validation.constraints.*;

public record CreateQuoteRequestDto(
        @NotBlank(message = "Applicant name is required")
        String applicantName,

        @NotBlank(message = "Applicant email is required")
        @Email(message = "Must be a valid email address")
        String applicantEmail,

        @NotNull(message = "Applicant age is required")
        @Min(value = 18, message = "Applicant must be at least 18 years old")
        @Max(value = 100, message = "Applicant age cannot exceed 100")
        Integer applicantAge,

        @NotBlank(message = "Zip code is required")
        @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Must be a valid ZIP code")
        String zipCode
) {}
