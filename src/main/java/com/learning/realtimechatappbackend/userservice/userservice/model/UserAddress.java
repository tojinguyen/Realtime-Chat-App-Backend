package com.learning.userservice.userservice.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "address_id", nullable = false, unique = true)
    private UUID addressId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "country", nullable = false)
    private String country;

    // Constructor for creating an address with a profile
    public UserAddress(UserProfile userProfile, String street, String city, String state, String postalCode) {
        this.userProfile = userProfile;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = "Default"; // Set a default value for country
    }

    // Constructor for creating an address with a profile and country
    public UserAddress(UserProfile userProfile, String street, String city, String state, String postalCode,
            String country) {
        this.userProfile = userProfile;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    // Constructor for updating an address (used in updateProfile method)
    public UserAddress(String street, String city, String state, String postalCode, UserProfile userProfile) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.userProfile = userProfile;
        this.country = "Default"; // Set a default value for country
    }
}
