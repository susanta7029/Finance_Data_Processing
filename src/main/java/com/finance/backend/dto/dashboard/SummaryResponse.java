package com.finance.backend.dto.dashboard;

import java.math.BigDecimal;

public record SummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance
) {
}
