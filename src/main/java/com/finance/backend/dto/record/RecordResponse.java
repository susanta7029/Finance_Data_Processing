package com.finance.backend.dto.record;

import com.finance.backend.model.RecordType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecordResponse(
        Long id,
        BigDecimal amount,
        RecordType type,
        String category,
        LocalDate date,
        String notes,
        Long createdByUserId,
        String createdByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
