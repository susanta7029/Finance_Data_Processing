package com.finance.backend.service;

import com.finance.backend.dto.record.CreateRecordRequest;
import com.finance.backend.dto.record.RecordResponse;
import com.finance.backend.dto.record.UpdateRecordRequest;
import com.finance.backend.dto.shared.PageResponse;
import com.finance.backend.exception.BadRequestException;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.model.AppUser;
import com.finance.backend.model.FinancialRecord;
import com.finance.backend.model.RecordType;
import com.finance.backend.repository.FinancialRecordRepository;
import com.finance.backend.specification.FinancialRecordSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecordService {

    private final FinancialRecordRepository financialRecordRepository;

    public RecordService(FinancialRecordRepository financialRecordRepository) {
        this.financialRecordRepository = financialRecordRepository;
    }

    @Transactional
    public RecordResponse createRecord(CreateRecordRequest request, AppUser actor) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(request.amount());
        record.setType(request.type());
        record.setCategory(request.category().trim());
        record.setDate(request.date());
        record.setNotes(request.notes() == null ? null : request.notes().trim());
        record.setCreatedBy(actor);

        return toResponse(financialRecordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public RecordResponse getRecordById(Long recordId) {
        FinancialRecord record = financialRecordRepository.findById(recordId)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + recordId));
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    public PageResponse<RecordResponse> listRecords(
            LocalDate from,
            LocalDate to,
            String category,
            RecordType type,
            String search,
            int page,
            int size
    ) {
        if (size > 100) {
            throw new BadRequestException("Page size cannot exceed 100");
        }

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1),
                Sort.by(Sort.Order.desc("date"), Sort.Order.desc("updatedAt")));

        Page<FinancialRecord> records = financialRecordRepository.findAll(
                FinancialRecordSpecifications.withFilters(from, to, category, type, search), pageable);

        List<RecordResponse> items = records.getContent().stream().map(this::toResponse).toList();

        return new PageResponse<>(
                items,
                records.getNumber(),
                records.getSize(),
                records.getTotalElements(),
                records.getTotalPages(),
                records.hasNext()
        );
    }

    @Transactional
    public RecordResponse updateRecord(Long recordId, UpdateRecordRequest request) {
        FinancialRecord record = financialRecordRepository.findById(recordId)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + recordId));

        if (request.amount() != null) {
            record.setAmount(request.amount());
        }

        if (request.type() != null) {
            record.setType(request.type());
        }

        if (request.category() != null && !request.category().isBlank()) {
            record.setCategory(request.category().trim());
        }

        if (request.date() != null) {
            record.setDate(request.date());
        }

        if (request.notes() != null) {
            record.setNotes(request.notes().trim());
        }

        return toResponse(financialRecordRepository.save(record));
    }

    @Transactional
    public void deleteRecord(Long recordId) {
        FinancialRecord record = financialRecordRepository.findById(recordId)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + recordId));
        record.setDeleted(true);
        financialRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<FinancialRecord> getRecordsForAnalytics(LocalDate from, LocalDate to, RecordType type) {
        return financialRecordRepository.findAll(FinancialRecordSpecifications.withFilters(from, to, null, type, null));
    }

    @Transactional(readOnly = true)
    public List<FinancialRecord> getRecordsForAnalytics(LocalDate from, LocalDate to) {
        return financialRecordRepository.findAll(FinancialRecordSpecifications.withFilters(from, to, null, null, null));
    }

    @Transactional(readOnly = true)
    public List<FinancialRecord> getRecentActivity(int limit) {
        Pageable pageable = PageRequest.of(0, Math.max(limit, 1), Sort.by(Sort.Order.desc("updatedAt")));
        return financialRecordRepository.findAll(FinancialRecordSpecifications.withFilters(null, null, null, null, null), pageable)
                .getContent();
    }

    public RecordResponse toResponse(FinancialRecord record) {
        return new RecordResponse(
                record.getId(),
                record.getAmount(),
                record.getType(),
                record.getCategory(),
                record.getDate(),
                record.getNotes(),
                record.getCreatedBy().getId(),
                record.getCreatedBy().getName(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}
