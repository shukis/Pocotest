package com.example.user.pocotest;


public class User {
    public User(String email, String password, String city, String country, String postalCode) {
        this.email = email;
        this.password = password;
        this.city = city;
        if (country != null) {
            this.country = formatCountry(country);
        }
        this.postalCode = postalCode;
    }

    private String email, password, city, country, postalCode;

    String getEmail() {
        return email;
    }


    String getPassword() {
        return password;
    }


    String getCity() {
        return city;
    }


    String getCountry() {
        return country;
    }

    String getPostalCode() {
        return postalCode;
    }

    private String formatCountry(String country) {
        switch (country) {
            case "Estonia":
                country = "ET";
                break;
            case "Latvia":
                country = "LV";
                break;
            case "Lithuania":
                country = "LT";
                break;
            case "Finland":
                country = "FI";
                break;
            case "Russia":
                country = "RU";
                break;
        }
        return country;
    }
}
