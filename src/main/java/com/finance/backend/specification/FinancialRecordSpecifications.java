package com.finance.backend.specification;

import com.finance.backend.model.FinancialRecord;
import com.finance.backend.model.RecordType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class FinancialRecordSpecifications {

    private FinancialRecordSpecifications() {
    }

    public static Specification<FinancialRecord> withFilters(
            LocalDate from,
            LocalDate to,
            String category,
            RecordType type,
            String search
    ) {
        Specification<FinancialRecord> specification = (root, query, cb) -> cb.isFalse(root.get("deleted"));

        if (from != null) {
            specification = specification.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), from));
        }

        if (to != null) {
            specification = specification.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), to));
        }

        if (category != null && !category.isBlank()) {
            String normalized = category.trim().toLowerCase();
            specification = specification.and((root, query, cb) -> cb.equal(cb.lower(root.get("category")), normalized));
        }

        if (type != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }

        if (search != null && !search.isBlank()) {
            String like = "%" + search.trim().toLowerCase() + "%";
            specification = specification.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("notes")), like),
                    cb.like(cb.lower(root.get("category")), like)
            ));
        }

        return specification;
    }
}
