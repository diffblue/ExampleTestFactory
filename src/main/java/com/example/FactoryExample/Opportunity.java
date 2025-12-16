package com.example.FactoryExample;

import com.diffblue.cover.annotations.InTestsUseFactories;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Opportunity {
    private final Person person;
    private final Job job;
    private final Bank bank;
    private final Tax tax;
    private final Insurance insurance;
    private final CreditHistory creditHistory;
    private final EmploymentVerification employmentVerification;

    public Opportunity(Person person, Job job, Bank bank, Tax tax, Insurance insurance,
                       CreditHistory creditHistory, EmploymentVerification employmentVerification) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        if (job == null) {
            throw new IllegalArgumentException("Job cannot be null");
        }
        if (bank == null) {
            throw new IllegalArgumentException("Bank cannot be null");
        }
        if (tax == null) {
            throw new IllegalArgumentException("Tax cannot be null");
        }
        if (insurance == null) {
            throw new IllegalArgumentException("Insurance cannot be null");
        }
        if (creditHistory == null) {
            throw new IllegalArgumentException("Credit history cannot be null");
        }
        if (employmentVerification == null) {
            throw new IllegalArgumentException("Employment verification cannot be null");
        }
        if (!person.isAdult()) {
            throw new IllegalArgumentException("Person must be an adult (18+) to create an Opportunity");
        }
        if (!employmentVerification.isVerified()) {
            throw new IllegalArgumentException("Employment must be verified before creating an Opportunity");
        }
        if (creditHistory.hasDefaultsInLastYear() && job.getSalary().compareTo(new BigDecimal("30000")) < 0) {
            throw new IllegalArgumentException("Cannot create opportunity with defaults and low income");
        }
        if (!insurance.isActive()) {
            throw new IllegalArgumentException("Active insurance required for Opportunity");
        }

        this.person = person;
        this.job = job;
        this.bank = bank;
        this.tax = tax;
        this.insurance = insurance;
        this.creditHistory = creditHistory;
        this.employmentVerification = employmentVerification;
    }

    public Opportunity(Person person, Job job, Bank bank) {
        this(person, job, bank, new Tax(job.getSalary(), "UK", 0),
             new Insurance(InsuranceType.BASIC, new BigDecimal("100"), true),
             new CreditHistory(false, 0, LocalDate.now()),
             new EmploymentVerification(true, LocalDate.now(), "Standard"));
    }


    public enum InsuranceType {
        BASIC, PREMIUM, COMPREHENSIVE
    }

    public static class Insurance {
        private final InsuranceType type;
        private final BigDecimal monthlyCost;
        private final boolean active;

        public Insurance(InsuranceType type, BigDecimal monthlyCost, boolean active) {
            if (type == null) {
                throw new IllegalArgumentException("Insurance type cannot be null");
            }
            if (monthlyCost == null || monthlyCost.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Monthly cost must be non-negative");
            }
            if (monthlyCost.compareTo(new BigDecimal("10000")) > 0) {
                throw new IllegalArgumentException("Monthly cost cannot exceed £10,000");
            }
            this.type = type;
            this.monthlyCost = monthlyCost;
            this.active = active;
        }

        public InsuranceType getType() {
            return type;
        }

        public BigDecimal getMonthlyCost() {
            return monthlyCost;
        }

        public boolean isActive() {
            return active;
        }
    }

    public static class CreditHistory {
        private final boolean hasDefaultsInLastYear;
        private final int latePaymentsCount;
        private final LocalDate lastChecked;

        public CreditHistory(boolean hasDefaultsInLastYear, int latePaymentsCount, LocalDate lastChecked) {
            if (latePaymentsCount < 0 || latePaymentsCount > 100) {
                throw new IllegalArgumentException("Late payments count must be between 0 and 100");
            }
            if (lastChecked == null) {
                throw new IllegalArgumentException("Last checked date cannot be null");
            }
            if (lastChecked.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Last checked date cannot be in the future");
            }
            this.hasDefaultsInLastYear = hasDefaultsInLastYear;
            this.latePaymentsCount = latePaymentsCount;
            this.lastChecked = lastChecked;
        }

        public boolean hasDefaultsInLastYear() {
            return hasDefaultsInLastYear;
        }

        public int getLatePaymentsCount() {
            return latePaymentsCount;
        }

        public LocalDate getLastChecked() {
            return lastChecked;
        }
    }

    public static class EmploymentVerification {
        private final boolean verified;
        private final LocalDate verificationDate;
        private final String verificationMethod;

        public EmploymentVerification(boolean verified, LocalDate verificationDate, String verificationMethod) {
            if (verificationDate == null) {
                throw new IllegalArgumentException("Verification date cannot be null");
            }
            if (verificationDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Verification date cannot be in the future");
            }
            if (verificationMethod == null || verificationMethod.trim().isEmpty()) {
                throw new IllegalArgumentException("Verification method cannot be null or empty");
            }
            this.verified = verified;
            this.verificationDate = verificationDate;
            this.verificationMethod = verificationMethod;
        }

        public boolean isVerified() {
            return verified;
        }

        public LocalDate getVerificationDate() {
            return verificationDate;
        }

        public String getVerificationMethod() {
            return verificationMethod;
        }
    }

    public Person getPerson() {
        return person;
    }

    public Job getJob() {
        return job;
    }

    public Bank getBank() {
        return bank;
    }

    public Tax getTax() {
        return tax;
    }

    /**
     * Calculate maximum mortgage based on UK lending criteria.
     * Standard UK mortgage multiple is 4.5x annual salary.
     * Additional considerations: age, existing savings, and credit worthiness.
     */
    public BigDecimal calculateMaximumMortgage() {
        BigDecimal salary = job.getSalary();

        // Base calculation: 4.5x annual salary
        BigDecimal baseMortgage = salary.multiply(new BigDecimal("4.5"));

        // Age factor: reduce if person is older (retirement considerations)
        int age = person.getAge();
        if (age > 50) {
            baseMortgage = baseMortgage.multiply(new BigDecimal("0.85"));
        } else if (age > 40) {
            baseMortgage = baseMortgage.multiply(new BigDecimal("0.95"));
        } else
            baseMortgage = baseMortgage.multiply(new BigDecimal("0.75"));

        // Experience factor: increase if senior employee (job security)
        if (job.isSenior()) {
            baseMortgage = baseMortgage.multiply(new BigDecimal("1.1"));
        }

        // Minimum balance factor: good savings indicates financial responsibility
        if (bank.hasMinimumBalance()) {
            baseMortgage = baseMortgage.multiply(new BigDecimal("1.05"));
        }

        return baseMortgage.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate maximum monthly spend based on UK tax system.
     * Takes into account: salary, tax obligations, mortgage considerations.
     */
    public BigDecimal calculateMaximumMonthlySpend() {
        BigDecimal annualSalary = job.getSalary();

        // Calculate UK tax and National Insurance
        BigDecimal annualTaxPaid = calculateUKTaxPaid();
        BigDecimal nationalInsurance = calculateNationalInsurance();

        // Net annual income after tax and NI
        BigDecimal netAnnualIncome = annualSalary.subtract(annualTaxPaid).subtract(nationalInsurance);

        // Monthly net income
        BigDecimal monthlyNetIncome = netAnnualIncome.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);

        // Assume mortgage payment is 30% of gross monthly income (if applicable)
        BigDecimal monthlyGrossIncome = annualSalary.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
        BigDecimal estimatedMortgagePayment = monthlyGrossIncome.multiply(new BigDecimal("0.30"));

        // Maximum spend = net income - mortgage payment - 20% buffer for savings
        BigDecimal maximumSpend = monthlyNetIncome.subtract(estimatedMortgagePayment);
        maximumSpend = maximumSpend.multiply(new BigDecimal("0.80"));

        // Cannot have negative spend
        if (maximumSpend.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return maximumSpend.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Personal Allowance: £12,570 (tax-free)
     * Basic rate (20%): £12,571 to £50,270
     * Higher rate (40%): £50,271 to £125,140
     * Additional rate (45%): over £125,140
     */
    public BigDecimal calculateUKTaxPaid() {
        BigDecimal income = job.getSalary();
        BigDecimal totalTax = BigDecimal.ZERO;

        // Personal Allowance threshold
        BigDecimal personalAllowance = new BigDecimal("12570");
        BigDecimal basicRateLimit = new BigDecimal("50270");
        BigDecimal higherRateLimit = new BigDecimal("125140");

        // Deduct personal allowance
        BigDecimal taxableIncome = income.subtract(personalAllowance);

        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Basic rate: 20% on income between £12,571 and £50,270
        BigDecimal basicRateBand = basicRateLimit.subtract(personalAllowance);
        if (taxableIncome.compareTo(basicRateBand) <= 0) {
            totalTax = taxableIncome.multiply(new BigDecimal("0.20"));
        } else {
            // Tax on basic rate band
            totalTax = basicRateBand.multiply(new BigDecimal("0.20"));

            BigDecimal remainingIncome = taxableIncome.subtract(basicRateBand);

            // Higher rate: 40% on income between £50,271 and £125,140
            BigDecimal higherRateBand = higherRateLimit.subtract(basicRateLimit);
            if (remainingIncome.compareTo(higherRateBand) <= 0) {
                totalTax = totalTax.add(remainingIncome.multiply(new BigDecimal("0.40")));
            } else {
                // Tax on higher rate band
                totalTax = totalTax.add(higherRateBand.multiply(new BigDecimal("0.40")));

                // Additional rate: 45% on income over £125,140
                BigDecimal additionalIncome = remainingIncome.subtract(higherRateBand);
                totalTax = totalTax.add(additionalIncome.multiply(new BigDecimal("0.45")));
            }
        }

        // Apply deductions if available
        if (tax.hasDeductions()) {
            BigDecimal deduction = new BigDecimal("2000"); // UK tax relief
            totalTax = totalTax.subtract(deduction);
        }

        // Apply dependent tax credits
        if (tax.getDependents() > 0) {
            BigDecimal childBenefit = new BigDecimal("1200").multiply(new BigDecimal(tax.getDependents()));
            totalTax = totalTax.subtract(childBenefit);
        }

        if (totalTax.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return totalTax.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate UK National Insurance contributions.
     * Class 1 NI for employees (2024/25):
     * 0% on income up to £12,570
     * 12% on income between £12,571 and £50,270
     * 2% on income over £50,270
     */
    public BigDecimal calculateNationalInsurance() {
        BigDecimal income = job.getSalary();
        BigDecimal totalNI = BigDecimal.ZERO;

        BigDecimal niThreshold = new BigDecimal("12570");
        BigDecimal upperEarningsLimit = new BigDecimal("50270");

        if (income.compareTo(niThreshold) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxableIncome = income.subtract(niThreshold);

        if (income.compareTo(upperEarningsLimit) <= 0) {
            // 12% on income between threshold and upper limit
            totalNI = taxableIncome.multiply(new BigDecimal("0.12"));
        } else {
            // 12% on income up to upper limit
            BigDecimal lowerBand = upperEarningsLimit.subtract(niThreshold);
            totalNI = lowerBand.multiply(new BigDecimal("0.12"));

            // 2% on income over upper limit
            BigDecimal upperBand = income.subtract(upperEarningsLimit);
            totalNI = totalNI.add(upperBand.multiply(new BigDecimal("0.02")));
        }

        return totalNI.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate total annual disposable income after all deductions.
     */
    public BigDecimal calculateDisposableIncome() {
        BigDecimal salary = job.getSalary();
        BigDecimal tax = calculateUKTaxPaid();
        BigDecimal ni = calculateNationalInsurance();

        return salary.subtract(tax).subtract(ni).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Determine if person qualifies for a mortgage based on UK criteria.
     */
    public boolean qualifiesForMortgage() {
        // Must be adult
        if (!person.isAdult()) {
            return false;
        }

        // Must not be overdrawn
        if (bank.isOverdrawn()) {
            return false;
        }

        // Must have minimum income (£20,000)
        if (job.getSalary().compareTo(new BigDecimal("20000")) < 0) {
            return false;
        }

        // Must be under retirement age (66 in UK)
        if (person.getAge() >= 66) {
            return false;
        }

        return true;
    }

    /**
     * Calculate effective tax rate percentage.
     */
    public BigDecimal getEffectiveTaxRate() {
        BigDecimal totalTax = calculateUKTaxPaid().add(calculateNationalInsurance());
        BigDecimal salary = job.getSalary();

        if (salary.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalTax.divide(salary, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }
}
