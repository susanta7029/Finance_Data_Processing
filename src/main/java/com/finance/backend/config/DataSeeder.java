package com.finance.backend.config;

import com.finance.backend.model.AppUser;
import com.finance.backend.model.FinancialRecord;
import com.finance.backend.model.RecordType;
import com.finance.backend.model.Role;
import com.finance.backend.model.UserStatus;
import com.finance.backend.repository.AppUserRepository;
import com.finance.backend.repository.FinancialRecordRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(AppUserRepository appUserRepository, FinancialRecordRepository recordRepository) {
        return args -> {
            if (appUserRepository.count() > 0) {
                return;
            }

            AppUser admin = new AppUser();
            admin.setName("Alice Admin");
            admin.setEmail("admin@finance.local");
            admin.setRole(Role.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            admin = appUserRepository.save(admin);

            AppUser analyst = new AppUser();
            analyst.setName("Andy Analyst");
            analyst.setEmail("analyst@finance.local");
            analyst.setRole(Role.ANALYST);
            analyst.setStatus(UserStatus.ACTIVE);
            analyst = appUserRepository.save(analyst);

            AppUser viewer = new AppUser();
            viewer.setName("Vera Viewer");
            viewer.setEmail("viewer@finance.local");
            viewer.setRole(Role.VIEWER);
            viewer.setStatus(UserStatus.ACTIVE);
            appUserRepository.save(viewer);

            recordRepository.save(buildRecord(new BigDecimal("5500.00"), RecordType.INCOME, "Salary", LocalDate.now().minusDays(21), "Monthly salary", admin));
            recordRepository.save(buildRecord(new BigDecimal("250.25"), RecordType.EXPENSE, "Utilities", LocalDate.now().minusDays(19), "Electricity bill", admin));
            recordRepository.save(buildRecord(new BigDecimal("920.00"), RecordType.EXPENSE, "Rent", LocalDate.now().minusDays(15), "Apartment rent", analyst));
            recordRepository.save(buildRecord(new BigDecimal("420.40"), RecordType.EXPENSE, "Groceries", LocalDate.now().minusDays(7), "Weekly groceries", analyst));
            recordRepository.save(buildRecord(new BigDecimal("300.00"), RecordType.INCOME, "Freelance", LocalDate.now().minusDays(5), "Project payment", admin));
        };
    }

    private FinancialRecord buildRecord(BigDecimal amount, RecordType type, String category, LocalDate date, String notes, AppUser createdBy) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(amount);
        record.setType(type);
        record.setCategory(category);
        record.setDate(date);
        record.setNotes(notes);
        record.setCreatedBy(createdBy);
        return record;
    }
}
