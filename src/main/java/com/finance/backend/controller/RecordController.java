package com.finance.backend.controller;

import com.finance.backend.dto.record.CreateRecordRequest;
import com.finance.backend.dto.record.RecordResponse;
import com.finance.backend.dto.record.UpdateRecordRequest;
import com.finance.backend.dto.shared.PageResponse;
import com.finance.backend.model.AppUser;
import com.finance.backend.model.RecordType;
import com.finance.backend.model.Role;
import com.finance.backend.security.RequireRoles;
import com.finance.backend.security.RequestUserContext;
import com.finance.backend.service.RecordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @RequireRoles(Role.ADMIN)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecordResponse createRecord(@Valid @RequestBody CreateRecordRequest request, HttpServletRequest httpRequest) {
        AppUser actor = RequestUserContext.get(httpRequest);
        return recordService.createRecord(request, actor);
    }

    @RequireRoles({Role.VIEWER, Role.ANALYST, Role.ADMIN})
    @GetMapping("/{recordId}")
    public RecordResponse getRecordById(@PathVariable Long recordId) {
        return recordService.getRecordById(recordId);
    }

    @RequireRoles({Role.VIEWER, Role.ANALYST, Role.ADMIN})
    @GetMapping
    public PageResponse<RecordResponse> listRecords(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return recordService.listRecords(from, to, category, type, search, page, size);
    }

    @RequireRoles(Role.ADMIN)
    @PutMapping("/{recordId}")
    public RecordResponse updateRecord(@PathVariable Long recordId, @Valid @RequestBody UpdateRecordRequest request) {
        return recordService.updateRecord(recordId, request);
    }

    @RequireRoles(Role.ADMIN)
    @DeleteMapping("/{recordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecord(@PathVariable Long recordId) {
        recordService.deleteRecord(recordId);
    }
}
