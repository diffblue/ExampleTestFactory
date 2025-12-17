package com.example.FactoryExample;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Job {
    private final String title;
    private final String company;
    private final BigDecimal salary;
    private final int yearsExperience;
    private final EmploymentType employmentType;
    private final Department department;
    private final LocalDate startDate;
    private final Contract contract;

    private static final BigDecimal MIN_SALARY = new BigDecimal("18000");
    private static final BigDecimal MAX_SALARY = new BigDecimal("500000");
    private static final List<String> VALID_TITLES = Arrays.asList(
            "Software Engineer", "Senior Software Engineer", "Lead Engineer", "Engineering Manager",
            "Data Analyst", "Data Scientist", "Product Manager", "Designer", "QA Engineer"
    );

    public Job(String title, String company, BigDecimal salary, int yearsExperience,
               EmploymentType employmentType, Department department, LocalDate startDate,
               Contract contract) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Job title cannot be null or empty");
        }
        if (!VALID_TITLES.contains(title)) {
            throw new IllegalArgumentException("Invalid job title. Must be one of: " + VALID_TITLES);
        }
        if (company == null || company.trim().isEmpty()) {
            throw new IllegalArgumentException("Company cannot be null or empty");
        }
        if (salary == null || salary.compareTo(MIN_SALARY) < 0 || salary.compareTo(MAX_SALARY) > 0) {
            throw new IllegalArgumentException("Salary must be between " + MIN_SALARY + " and " + MAX_SALARY);
        }
        if (yearsExperience < 0 || yearsExperience > 70) {
            throw new IllegalArgumentException("Years of experience must be between 0 and 70");
        }
        if (employmentType == null) {
            throw new IllegalArgumentException("Employment type cannot be null");
        }
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (startDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the future");
        }
        if (contract == null) {
            throw new IllegalArgumentException("Contract cannot be null");
        }
        if (title.contains("Senior") && yearsExperience < 5) {
            throw new IllegalArgumentException("Senior positions require at least 5 years of experience");
        }
        if (title.contains("Lead") && yearsExperience < 8) {
            throw new IllegalArgumentException("Lead positions require at least 8 years of experience");
        }
        if (title.contains("Manager") && yearsExperience < 10) {
            throw new IllegalArgumentException("Manager positions require at least 10 years of experience");
        }

        this.title = title;
        this.company = company;
        this.salary = salary;
        this.yearsExperience = yearsExperience;
        this.employmentType = employmentType;
        this.department = department;
        this.startDate = startDate;
        this.contract = contract;
    }

    public Job(String title, String company, BigDecimal salary) {
        this(title, company, salary, 0, EmploymentType.FULL_TIME,
                new Department("General", "GEN"), LocalDate.now(), new Contract(12, true));
    }

    public Job(String title, String company) {
        this(title, company, MIN_SALARY, 0, EmploymentType.FULL_TIME,
                new Department("General", "GEN"), LocalDate.now(), new Contract(12, true));
    }

    public enum EmploymentType {
        FULL_TIME, PART_TIME, CONTRACT, TEMPORARY, INTERN
    }

    public static class Department {
        private final String name;
        private final String code;

        public Department(String name, String code) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Department name cannot be null or empty");
            }
            if (code == null || code.length() != 3) {
                throw new IllegalArgumentException("Department code must be exactly 3 characters");
            }
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }
    }

    public static class Contract {
        private final int durationMonths;
        private final boolean renewable;

        public Contract(int durationMonths, boolean renewable) {
            if (durationMonths <= 0 || durationMonths > 120) {
                throw new IllegalArgumentException("Contract duration must be between 1 and 120 months");
            }
            this.durationMonths = durationMonths;
            this.renewable = renewable;
        }

        public int getDurationMonths() {
            return durationMonths;
        }

        public boolean isRenewable() {
            return renewable;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public int getYearsExperience() {
        return yearsExperience;
    }

    public boolean isEntryLevel() {
        return yearsExperience < 2;
    }

    public boolean isSenior() {
        return yearsExperience >= 5;
    }

    public BigDecimal calculateAnnualBonus() {
        if (yearsExperience >= 10) {
            return salary.multiply(new BigDecimal("0.15"));
        } else if (yearsExperience >= 5) {
            return salary.multiply(new BigDecimal("0.10"));
        } else {
            return salary.multiply(new BigDecimal("0.05"));
        }
    }
}
