package com.finance.backend.dto.record;

import com.finance.backend.model.RecordType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRecordRequest(
        @NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal amount,
        @NotNull RecordType type,
        @NotBlank @Size(max = 100) String category,
        @NotNull LocalDate date,
        @Size(max = 500) String notes
) {
}
