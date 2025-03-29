package com.learning.userservice.userservice.repository;

import com.learning.userservice.userservice.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<UserProfile, String> {
    Optional<UserProfile> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
