package com.finance.backend.controller;

import com.finance.backend.dto.dashboard.CategoryTotalResponse;
import com.finance.backend.dto.dashboard.RecentActivityResponse;
import com.finance.backend.dto.dashboard.SummaryResponse;
import com.finance.backend.dto.dashboard.TrendPointResponse;
import com.finance.backend.model.RecordType;
import com.finance.backend.model.Role;
import com.finance.backend.security.RequireRoles;
import com.finance.backend.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequireRoles({Role.VIEWER, Role.ANALYST, Role.ADMIN})
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public SummaryResponse summary(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        return dashboardService.getSummary(from, to);
    }

    @GetMapping("/category-totals")
    public List<CategoryTotalResponse> categoryTotals(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) RecordType type
    ) {
        return dashboardService.getCategoryTotals(from, to, type);
    }

    @GetMapping("/trends")
    public List<TrendPointResponse> trends(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(defaultValue = "MONTH") String bucket
    ) {
        return dashboardService.getTrends(from, to, bucket);
    }

    @GetMapping("/recent-activity")
    public List<RecentActivityResponse> recentActivity(@RequestParam(defaultValue = "10") int limit) {
        return dashboardService.getRecentActivity(limit);
    }
}
