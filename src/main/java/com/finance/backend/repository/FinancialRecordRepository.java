package com.finance.backend.repository;

import com.finance.backend.model.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>, JpaSpecificationExecutor<FinancialRecord> {
    Page<FinancialRecord> findByDeletedFalseOrderByUpdatedAtDesc(Pageable pageable);
}
