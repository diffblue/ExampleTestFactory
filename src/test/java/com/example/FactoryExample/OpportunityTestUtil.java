package com.example.FactoryExample;

import com.diffblue.cover.annotations.InterestingTestFactory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OpportunityTestUtil {

    //This annoataion is from the diffbluce cover annotation dependency.
    // Test factories need this @InterestingTestFactory to tell cover this is something of interest it could use


//    @InterestingTestFactory
//    public static Opportunity createOpportunityWithValidJob() {
//        Person.Address address = new Person.Address("123 Main Street", "London", "SW1A 1AA", "UK");
//        Person person = new Person("Jane", "Doe", 25, "jane@example.com", "+441234567890", "AB123456C", address);
//
//        Job.Department department = new Job.Department("Engineering", "ENG");
//        Job.Contract contract = new Job.Contract(24, true);
//        Job job = new Job("Senior Software Engineer", "TechCorp", new BigDecimal("75000.00"), 8,
//                Job.EmploymentType.FULL_TIME, department, LocalDate.now().minusYears(5), contract);
//
//        Bank.CreditScore creditScore = new Bank.CreditScore(700, "Good");
//        Bank.AccountHolder accountHolder = new Bank.AccountHolder("Jane Doe", "1973-01-01", "British");
//        Bank bank = new Bank("12345678", "Barclays", new BigDecimal("1000.00"), Bank.AccountType.SAVINGS,
//                             "12-34-56", creditScore, LocalDate.now().minusYears(5), accountHolder);
//
//        Tax tax = new Tax(new BigDecimal("50000.00"), "UK", 0);
//
//        Opportunity.Insurance insurance = new Opportunity.Insurance(Opportunity.InsuranceType.BASIC, new BigDecimal("100"), true);
//        Opportunity.CreditHistory creditHistory = new Opportunity.CreditHistory(false, 0, LocalDate.now());
//        Opportunity.EmploymentVerification employmentVerification = new Opportunity.EmploymentVerification(true, LocalDate.now(), "Standard");
//
//        return new Opportunity(person, job, bank, tax, insurance, creditHistory, employmentVerification);
//    }
}

