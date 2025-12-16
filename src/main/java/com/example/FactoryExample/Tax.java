package com.example.FactoryExample;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Tax {
    private final BigDecimal income;
    private final TaxRegion taxRegion;
    private final int dependents;
    private final boolean hasDeductions;
    private final FilingStatus filingStatus;
    private final List<TaxDeduction> deductions;
    private final boolean selfEmployed;

    private static final BigDecimal MIN_INCOME = BigDecimal.ZERO;
    private static final BigDecimal MAX_INCOME = new BigDecimal("10000000");

    public Tax(BigDecimal income, TaxRegion taxRegion, int dependents, boolean hasDeductions,
               FilingStatus filingStatus, List<TaxDeduction> deductions,
               boolean selfEmployed) {
        if (income == null || income.compareTo(MIN_INCOME) < 0 || income.compareTo(MAX_INCOME) > 0) {
            throw new IllegalArgumentException("Income must be between " + MIN_INCOME + " and " + MAX_INCOME);
        }
        if (taxRegion == null) {
            throw new IllegalArgumentException("Tax region cannot be null");
        }
        if (dependents < 0 || dependents > 20) {
            throw new IllegalArgumentException("Dependents must be between 0 and 20");
        }
        if (filingStatus == null) {
            throw new IllegalArgumentException("Filing status cannot be null");
        }
        if (deductions == null) {
            throw new IllegalArgumentException("Deductions list cannot be null");
        }
        if (hasDeductions && deductions.isEmpty()) {
            throw new IllegalArgumentException("Cannot have deductions flag set without actual deductions");
        }
        if (filingStatus == FilingStatus.MARRIED_JOINT && dependents == 0) {
            throw new IllegalArgumentException("Married joint filing typically requires at least one dependent");
        }
        if (selfEmployed && income.compareTo(new BigDecimal("12570")) < 0) {
            throw new IllegalArgumentException("Self-employed individuals must report income above personal allowance threshold");
        }

        this.income = income;
        this.taxRegion = taxRegion;
        this.dependents = dependents;
        this.hasDeductions = hasDeductions;
        this.filingStatus = filingStatus;
        this.deductions = deductions;
        this.selfEmployed = selfEmployed;
    }

    public Tax(BigDecimal income, String taxRegion, int dependents) {
        this(income, TaxRegion.valueOf(taxRegion), dependents, false,
             FilingStatus.SINGLE, Arrays.asList(), false);
    }

    public Tax(BigDecimal income, String taxRegion) {
        this(income, TaxRegion.valueOf(taxRegion), 0, false,
             FilingStatus.SINGLE, Arrays.asList(), false);
    }

    public enum TaxRegion {
        UK, US, EU
    }

    public enum FilingStatus {
        SINGLE, MARRIED_JOINT, MARRIED_SEPARATE, HEAD_OF_HOUSEHOLD
    }

    public static class TaxDeduction {
        private final String type;
        private final BigDecimal amount;
        private final boolean verified;

        public TaxDeduction(String type, BigDecimal amount, boolean verified) {
            if (type == null || type.trim().isEmpty()) {
                throw new IllegalArgumentException("Deduction type cannot be null or empty");
            }
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Deduction amount must be positive");
            }
            if (amount.compareTo(new BigDecimal("50000")) > 0) {
                throw new IllegalArgumentException("Single deduction cannot exceed £50,000");
            }
            this.type = type;
            this.amount = amount;
            this.verified = verified;
        }

        public String getType() {
            return type;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public boolean isVerified() {
            return verified;
        }
    }

    public BigDecimal getIncome() {
        return income;
    }

    public TaxRegion getTaxRegion() {
        return taxRegion;
    }

    public int getDependents() {
        return dependents;
    }

    public boolean hasDeductions() {
        return hasDeductions;
    }

    public BigDecimal calculateTax() {
        BigDecimal taxRate = getTaxRate();
        BigDecimal tax = income.multiply(taxRate);

        if (hasDeductions) {
            BigDecimal deduction = new BigDecimal("5000");
            tax = tax.subtract(deduction);
        }

        if (dependents > 0) {
            BigDecimal dependentCredit = new BigDecimal("1000").multiply(new BigDecimal(dependents));
            tax = tax.subtract(dependentCredit);
        }

        if (tax.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return tax.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getTaxRate() {
        if (TaxRegion.US.equals(taxRegion)) {
            if (income.compareTo(new BigDecimal("50000")) < 0) {
                return new BigDecimal("0.10");
            } else if (income.compareTo(new BigDecimal("100000")) < 0) {
                return new BigDecimal("0.20");
            } else {
                return new BigDecimal("0.30");
            }
        } else if (TaxRegion.UK.equals(taxRegion)) {
            if (income.compareTo(new BigDecimal("40000")) < 0) {
                return new BigDecimal("0.15");
            } else {
                return new BigDecimal("0.25");
            }
        }
        return new BigDecimal("0.15");
    }

    public boolean isLowIncome() {
        return income.compareTo(new BigDecimal("30000")) < 0;
    }
}
