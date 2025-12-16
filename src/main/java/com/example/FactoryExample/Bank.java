package com.example.FactoryExample;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Bank {
    private final String accountNumber;
    private final String bankName;
    private final BigDecimal balance;
    private final AccountType accountType;
    private final String sortCode;
    private final CreditScore creditScore;
    private final LocalDate accountOpenDate;
    private final AccountHolder accountHolder;

    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^\\d{8}$");
    private static final Pattern SORT_CODE_PATTERN = Pattern.compile("^\\d{2}-\\d{2}-\\d{2}$");
    private static final List<String> VALID_UK_BANKS = Arrays.asList(
        "Barclays", "HSBC", "Lloyds", "NatWest", "Santander", "Halifax", "Nationwide"
    );
    private static final BigDecimal MAX_BALANCE = new BigDecimal("1000000");

    public Bank(String accountNumber, String bankName, BigDecimal balance, AccountType accountType,
                String sortCode, CreditScore creditScore, LocalDate accountOpenDate,
                AccountHolder accountHolder) {
        if (accountNumber == null || !ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches()) {
            throw new IllegalArgumentException("Account number must be exactly 8 digits");
        }
        if (bankName == null || !VALID_UK_BANKS.contains(bankName)) {
            throw new IllegalArgumentException("Invalid bank name. Must be one of: " + VALID_UK_BANKS);
        }
        if (balance == null || balance.compareTo(MAX_BALANCE) > 0) {
            throw new IllegalArgumentException("Balance cannot exceed " + MAX_BALANCE);
        }
        if (accountType == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }
        if (sortCode == null || !SORT_CODE_PATTERN.matcher(sortCode).matches()) {
            throw new IllegalArgumentException("Sort code must be in format XX-XX-XX");
        }
        if (creditScore == null) {
            throw new IllegalArgumentException("Credit score cannot be null");
        }
        if (accountOpenDate == null) {
            throw new IllegalArgumentException("Account open date cannot be null");
        }
        if (accountOpenDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Account open date cannot be in the future");
        }
        if (accountHolder == null) {
            throw new IllegalArgumentException("Account holder cannot be null");
        }
        if (accountType == AccountType.SAVINGS && balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Savings accounts cannot have negative balance");
        }
        if (accountType == AccountType.BUSINESS && creditScore.getScore() < 600) {
            throw new IllegalArgumentException("Business accounts require credit score of at least 600");
        }

        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.balance = balance;
        this.accountType = accountType;
        this.sortCode = sortCode;
        this.creditScore = creditScore;
        this.accountOpenDate = accountOpenDate;
        this.accountHolder = accountHolder;
    }

    public Bank(String accountNumber, String bankName, BigDecimal balance) {
        this(accountNumber, bankName, balance, AccountType.CHECKING, "00-00-00",
             new CreditScore(700, "Good"), LocalDate.now(), new AccountHolder("", "", ""));
    }

    public Bank(String accountNumber, String bankName) {
        this(accountNumber, bankName, BigDecimal.ZERO, AccountType.CHECKING, "00-00-00",
             new CreditScore(700, "Good"), LocalDate.now(), new AccountHolder("", "", ""));
    }

    public enum AccountType {
        CHECKING, SAVINGS, BUSINESS, JOINT, ISA
    }

    public static class CreditScore {
        private final int score;
        private final String rating;

        public CreditScore(int score, String rating) {
            if (score < 300 || score > 850) {
                throw new IllegalArgumentException("Credit score must be between 300 and 850");
            }
            if (rating == null || rating.trim().isEmpty()) {
                throw new IllegalArgumentException("Credit rating cannot be null or empty");
            }
            if (!Arrays.asList("Poor", "Fair", "Good", "Very Good", "Excellent").contains(rating)) {
                throw new IllegalArgumentException("Invalid credit rating");
            }
            this.score = score;
            this.rating = rating;
        }

        public int getScore() {
            return score;
        }

        public String getRating() {
            return rating;
        }
    }

    public static class AccountHolder {
        private final String fullName;
        private final String dateOfBirth;
        private final String nationality;

        public AccountHolder(String fullName, String dateOfBirth, String nationality) {
            this.fullName = fullName;
            this.dateOfBirth = dateOfBirth;
            this.nationality = nationality;
        }

        public String getFullName() {
            return fullName;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public String getNationality() {
            return nationality;
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public boolean isOverdrawn() {
        return balance.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean hasMinimumBalance() {
        BigDecimal minimumBalance = new BigDecimal("100");
        return balance.compareTo(minimumBalance) >= 0;
    }

    public BigDecimal calculateInterest() {
        if (AccountType.SAVINGS.equals(accountType)) {
            return balance.multiply(new BigDecimal("0.02"));
        } else if (AccountType.CHECKING.equals(accountType)) {
            return balance.multiply(new BigDecimal("0.001"));
        }
        return BigDecimal.ZERO;
    }
}
