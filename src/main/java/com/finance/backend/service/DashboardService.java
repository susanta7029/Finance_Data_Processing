package com.finance.backend.service;

import com.finance.backend.dto.dashboard.CategoryTotalResponse;
import com.finance.backend.dto.dashboard.RecentActivityResponse;
import com.finance.backend.dto.dashboard.SummaryResponse;
import com.finance.backend.dto.dashboard.TrendPointResponse;
import com.finance.backend.model.FinancialRecord;
import com.finance.backend.model.RecordType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final RecordService recordService;

    public DashboardService(RecordService recordService) {
        this.recordService = recordService;
    }

    @Transactional(readOnly = true)
    public SummaryResponse getSummary(LocalDate from, LocalDate to) {
        List<FinancialRecord> records = recordService.getRecordsForAnalytics(from, to);

        BigDecimal income = sumByType(records, RecordType.INCOME);
        BigDecimal expense = sumByType(records, RecordType.EXPENSE);
        BigDecimal net = income.subtract(expense);

        return new SummaryResponse(income, expense, net);
    }

    @Transactional(readOnly = true)
    public List<CategoryTotalResponse> getCategoryTotals(LocalDate from, LocalDate to, RecordType type) {
        List<FinancialRecord> records = recordService.getRecordsForAnalytics(from, to, type);

        Map<String, BigDecimal> grouped = records.stream()
                .collect(Collectors.groupingBy(FinancialRecord::getCategory,
                        Collectors.mapping(FinancialRecord::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        return grouped.entrySet().stream()
                .map(entry -> new CategoryTotalResponse(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CategoryTotalResponse::total).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TrendPointResponse> getTrends(LocalDate from, LocalDate to, String bucket) {
        List<FinancialRecord> records = recordService.getRecordsForAnalytics(from, to);
        boolean weekly = "WEEK".equalsIgnoreCase(bucket);

        Map<String, List<FinancialRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(record -> weekly ? weekBucket(record.getDate()) : monthBucket(record.getDate())));

        return grouped.entrySet().stream()
                .map(entry -> {
                    BigDecimal income = sumByType(entry.getValue(), RecordType.INCOME);
                    BigDecimal expense = sumByType(entry.getValue(), RecordType.EXPENSE);
                    return new TrendPointResponse(entry.getKey(), income, expense, income.subtract(expense));
                })
                .sorted(Comparator.comparing(TrendPointResponse::bucket))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecentActivityResponse> getRecentActivity(int limit) {
        return recordService.getRecentActivity(limit).stream()
                .map(record -> new RecentActivityResponse(
                        record.getId(),
                        record.getType(),
                        record.getCategory(),
                        record.getAmount(),
                        record.getDate(),
                        record.getNotes(),
                        record.getCreatedBy().getName(),
                        record.getUpdatedAt()
                ))
                .toList();
    }

    private String monthBucket(LocalDate date) {
        return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
    }

    private String weekBucket(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int week = date.get(weekFields.weekOfWeekBasedYear());
        int year = date.get(weekFields.weekBasedYear());
        return year + "-W" + String.format("%02d", week);
    }

    private BigDecimal sumByType(List<FinancialRecord> records, RecordType type) {
        return records.stream()
                .filter(record -> record.getType() == type)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
