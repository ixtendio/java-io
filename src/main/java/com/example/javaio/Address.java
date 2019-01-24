package com.example.javaio;

import java.io.Serializable;

public class Address implements Serializable {

    private final String city;
    private final String country;

    public Address(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}
