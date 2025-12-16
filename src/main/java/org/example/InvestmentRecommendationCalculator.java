package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.example.BigDecimalUtil;

final class InvestmentRecommendationCalculator {

    public static final BigDecimal ONE = BigDecimal.ONE;
    public static final BigDecimal TWELVE = BigDecimal.valueOf(12);
    public static final BigDecimal THIRTEEN = BigDecimal.valueOf(13);
    public static final BigDecimal LOWER_BOUND = BigDecimal.valueOf(0.41);
    public static final BigDecimal UPPER_BOUND = BigDecimal.valueOf(2.36);
    public static final int RISK_SCORE_LOWER_LIMIT = 6;
    public static final int RISK_SCORER_UPPER_LIMIT = 20;

    private InvestmentRecommendationCalculator() {}

    static int calculateRiskScore(int age, BigDecimal income, BigDecimal netWorth, BigDecimal marginalTaxRate) {
        BigDecimal ageFactor = determineAgeFactor(age); // a
        BigDecimal incomeFactor = determineIncomeFactor(income); // i
        BigDecimal netWorthFactor = determineNetWorthFactor(netWorth); // n
        BigDecimal taxRateFactor = determineTaxRateFactor(marginalTaxRate); // t

        //  a * i * n * t
        BigDecimal combinedFactor =
                ageFactor.multiply(incomeFactor).multiply(netWorthFactor).multiply(taxRateFactor);

        final int riskScore;
        if (BigDecimalUtil.lessThan(combinedFactor, ONE)) {
            BigDecimal numerator = combinedFactor.subtract(LOWER_BOUND); // A
            BigDecimal denominator = ONE.subtract(LOWER_BOUND); // B

            // ((A/B)*12) + 1
            riskScore = numerator
                    .divide(denominator, 4, RoundingMode.HALF_UP)
                    .multiply(TWELVE)
                    .add(ONE)
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();
        } else {
            BigDecimal numerator = combinedFactor.subtract(ONE); // A
            BigDecimal denominator = UPPER_BOUND.subtract(ONE); // B

            // ((A/B)*12) + 13
            riskScore = numerator
                    .divide(denominator, 4, RoundingMode.HALF_UP)
                    .multiply(TWELVE)
                    .add(THIRTEEN)
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();
        }

        return clampRiskScore(riskScore);
    }

    private static int clampRiskScore(int riskScore) {
        if (riskScore < RISK_SCORE_LOWER_LIMIT) {
            return RISK_SCORE_LOWER_LIMIT;
        }

        if (riskScore > RISK_SCORER_UPPER_LIMIT) {
            return RISK_SCORER_UPPER_LIMIT;
        }

        return riskScore;
    }

    private static BigDecimal determineTaxRateFactor(BigDecimal marginalTaxRate) {
        if (BigDecimalUtil.lessThan(marginalTaxRate, BigDecimal.ZERO)) {
            throw new IllegalArgumentException("Marginal tax rate must be positive");
        }
        if (BigDecimalUtil.lessThanEqualTo(marginalTaxRate, BigDecimal.valueOf(0.12))) {
            return BigDecimal.valueOf(0.8);
        }
        if (BigDecimalUtil.lessThanEqualTo(marginalTaxRate, BigDecimal.valueOf(0.14))) {
            return BigDecimal.valueOf(0.85);
        }
        if (BigDecimalUtil.lessThanEqualTo(marginalTaxRate, BigDecimal.valueOf(0.16))) {
            return BigDecimal.valueOf(0.9);
        }
        if (BigDecimalUtil.lessThanEqualTo(marginalTaxRate, BigDecimal.valueOf(0.18))) {
            return BigDecimal.valueOf(0.95);
        }
        if (BigDecimalUtil.lessThanEqualTo(marginalTaxRate, BigDecimal.valueOf(0.20))) {
            return ONE;
        }
        if (BigDecimalUtil.lessThanEqualTo(marginalTaxRate, BigDecimal.valueOf(0.25))) {
            return BigDecimal.valueOf(1.06);
        }
        if (BigDecimalUtil.lessThanEqualTo(marginalTaxRate, BigDecimal.valueOf(0.30))) {
            return BigDecimal.valueOf(1.12);
        }
        if (BigDecimalUtil.lessThanEqualTo(marginalTaxRate, BigDecimal.valueOf(0.35))) {
            return BigDecimal.valueOf(1.18);
        }
        return BigDecimal.valueOf(1.24);
    }

    static BigDecimal determineNetWorthFactor(BigDecimal netWorth) {
        if (BigDecimalUtil.lessThan(netWorth, BigDecimal.ZERO)) {
            throw new IllegalArgumentException("Net worth must be positive");
        }
        if (BigDecimalUtil.lessThanEqualTo(netWorth, BigDecimal.valueOf(25_000))) {
            return BigDecimal.valueOf(0.8);
        }
        if (BigDecimalUtil.lessThanEqualTo(netWorth, BigDecimal.valueOf(50_000))) {
            return BigDecimal.valueOf(0.85);
        }
        if (BigDecimalUtil.lessThanEqualTo(netWorth, BigDecimal.valueOf(100_000))) {
            return BigDecimal.valueOf(0.9);
        }
        if (BigDecimalUtil.lessThanEqualTo(netWorth, BigDecimal.valueOf(150_000))) {
            return BigDecimal.valueOf(0.95);
        }
        if (BigDecimalUtil.lessThanEqualTo(netWorth, BigDecimal.valueOf(250_000))) {
            return ONE;
        }
        if (BigDecimalUtil.lessThanEqualTo(netWorth, BigDecimal.valueOf(500_000))) {
            return BigDecimal.valueOf(1.06);
        }
        if (BigDecimalUtil.lessThanEqualTo(netWorth, BigDecimal.valueOf(1_000_000))) {
            return BigDecimal.valueOf(1.12);
        }
        if (BigDecimalUtil.lessThanEqualTo(netWorth, BigDecimal.valueOf(4_000_000))) {
            return BigDecimal.valueOf(1.18);
        }
        return BigDecimal.valueOf(1.24);
    }

    static BigDecimal determineIncomeFactor(BigDecimal income) {
        if (BigDecimalUtil.lessThan(income, BigDecimal.ZERO)) {
            throw new IllegalArgumentException("Income must be positive");
        }
        if (BigDecimalUtil.lessThanEqualTo(income, BigDecimal.valueOf(50_000))) {
            return BigDecimal.valueOf(0.8);
        }
        if (BigDecimalUtil.lessThanEqualTo(income, BigDecimal.valueOf(75_000))) {
            return BigDecimal.valueOf(0.85);
        }
        if (BigDecimalUtil.lessThanEqualTo(income, BigDecimal.valueOf(100_000))) {
            return BigDecimal.valueOf(0.9);
        }
        if (BigDecimalUtil.lessThanEqualTo(income, BigDecimal.valueOf(125_000))) {
            return BigDecimal.valueOf(0.95);
        }
        if (BigDecimalUtil.lessThanEqualTo(income, BigDecimal.valueOf(150_000))) {
            return ONE;
        }
        if (BigDecimalUtil.lessThanEqualTo(income, BigDecimal.valueOf(200_000))) {
            return BigDecimal.valueOf(1.06);
        }
        if (BigDecimalUtil.lessThanEqualTo(income, BigDecimal.valueOf(300_000))) {
            return BigDecimal.valueOf(1.12);
        }
        if (BigDecimalUtil.lessThanEqualTo(income, BigDecimal.valueOf(500_000))) {
            return BigDecimal.valueOf(1.18);
        }
        return BigDecimal.valueOf(1.24);
    }

    static BigDecimal determineAgeFactor(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age must be positive");
        }
        if (age <= 35) {
            return BigDecimal.valueOf(1.24);
        }
        if (age <= 45) {
            return BigDecimal.valueOf(1.18);
        }
        if (age <= 55) {
            return BigDecimal.valueOf(1.12);
        }
        if (age <= 60) {
            return BigDecimal.valueOf(1.06);
        }
        if (age <= 65) {
            return ONE;
        }
        if (age <= 70) {
            return BigDecimal.valueOf(0.95);
        }
        if (age <= 75) {
            return BigDecimal.valueOf(0.9);
        }
        if (age <= 80) {
            return BigDecimal.valueOf(0.85);
        }
        return BigDecimal.valueOf(0.8);
    }
}

