package com.learning.userservice.userservice.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserAccount userAccount;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAddress> addresses;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "gender")
    private String gender;

    @Column(name = "date_of_birth")
    private Instant dateOfBirth;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
