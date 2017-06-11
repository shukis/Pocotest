package com.example.user.pocotest;


import java.io.Serializable;

public class User implements Serializable {


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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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
