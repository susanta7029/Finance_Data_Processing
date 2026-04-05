package com.finance.backend.dto.record;

import com.finance.backend.model.RecordType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateRecordRequest(
        @DecimalMin(value = "0.01", inclusive = true) BigDecimal amount,
        RecordType type,
        @Size(max = 100) String category,
        LocalDate date,
        @Size(max = 500) String notes
) {
}
