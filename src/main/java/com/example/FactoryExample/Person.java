package com.example.FactoryExample;

import java.util.regex.Pattern;

public class Person {
    private final String firstName;
    private final String lastName;
    private final int age;
    private final String email;
    private final String phoneNumber;
    private final String nationalInsuranceNumber;
    private final Address address;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    private static final Pattern NI_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{6}[A-Z]$");

    public Person(String firstName, String lastName, int age, String email, String phoneNumber,
                  String nationalInsuranceNumber, Address address) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (phoneNumber != null && !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        if (nationalInsuranceNumber != null && !NI_PATTERN.matcher(nationalInsuranceNumber).matches()) {
            throw new IllegalArgumentException("Invalid National Insurance Number format");
        }
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        if (age >= 16 && nationalInsuranceNumber == null) {
            throw new IllegalArgumentException("National Insurance Number required for persons aged 16 or over");
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.nationalInsuranceNumber = nationalInsuranceNumber;
        this.address = address;
    }

    public Person(String firstName, String lastName, int age) {
        this(firstName, lastName, age, null, null, null, new Address("", "", "", ""));
    }

    public Person(String firstName, String lastName) {
        this(firstName, lastName, 0, null, null, null, new Address("", "", "", ""));
    }

    public static class Address {
        private final String street;
        private final String city;
        private final String postcode;
        private final String country;

        public Address(String street, String city, String postcode, String country) {
            this.street = street;
            this.city = city;
            this.postcode = postcode;
            this.country = country;
        }

        public String getStreet() {
            return street;
        }

        public String getCity() {
            return city;
        }

        public String getPostcode() {
            return postcode;
        }

        public String getCountry() {
            return country;
        }

        public boolean isComplete() {
            return street != null && !street.isEmpty() &&
                   city != null && !city.isEmpty() &&
                   postcode != null && !postcode.isEmpty() &&
                   country != null && !country.isEmpty();
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAdult() {
        return age >= 18;
    }

    public boolean hasEmail() {
        return email != null && !email.isEmpty();
    }
}
