package com.learning.realtimechatappbackend.userservice.userservice.repository;

import com.learning.realtimechatappbackend.userservice.userservice.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<UserAccount, String> {
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByUserId(String userId);
    boolean existsByEmail(String email);
}
