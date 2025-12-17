package com.example.FactoryExample;

import java.math.BigDecimal;

public class Insurance {

    public enum InsuranceType {
        BASIC, PREMIUM, COMPREHENSIVE
    }

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

