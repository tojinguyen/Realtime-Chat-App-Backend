package com.learning.userservice.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.userservice.userservice.dto.request.CreateProfileRequest;
import com.learning.userservice.userservice.dto.response.ApiResponse;
import com.learning.userservice.userservice.model.UserProfile;
import com.learning.userservice.userservice.service.ProfileService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/profiles")
@AllArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserProfile>> createProfile(@RequestBody CreateProfileRequest createProfileRequest) {
        try {
            var profile = profileService.createProfile(createProfileRequest.getUserId(), createProfileRequest);
            return ResponseEntity.ok(ApiResponse.<UserProfile>builder()
                    .success(true)
                    .message("Profile created successfully.")
                    .data(profile)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<UserProfile>builder()
                    .success(false)
                    .message("Failed to create profile.")
                    .build());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfile>> getProfileByUserId(@PathVariable String userId) {
        try {
            var profile = profileService.getProfileByUserId(userId);
            return ResponseEntity.ok(ApiResponse.<UserProfile>builder()
                    .success(true)
                    .message("Profile retrieved successfully.")
                    .data(profile)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<UserProfile>builder()
                    .success(false)
                    .message("Failed to retrieve profile.")
                    .build());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(@PathVariable String userId) {
        try {
            profileService.deleteProfile(userId);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Profile deleted successfully.")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete profile.")
                    .build());
        }
    }
}
