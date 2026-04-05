package com.finance.backend.dto.dashboard;

import java.math.BigDecimal;

public record TrendPointResponse(
        String bucket,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net
) {
}
