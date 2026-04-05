package com.finance.backend.dto.dashboard;

import com.finance.backend.model.RecordType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecentActivityResponse(
        Long recordId,
        RecordType type,
        String category,
        BigDecimal amount,
        LocalDate date,
        String notes,
        String actor,
        LocalDateTime updatedAt
) {
}
