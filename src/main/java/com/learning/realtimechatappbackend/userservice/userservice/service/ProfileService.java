package com.learning.realtimechatappbackend.userservice.userservice.service;

import java.time.Instant;
import java.util.stream.Collectors;

import com.learning.realtimechatappbackend.userservice.userservice.dto.request.CreateProfileRequest;
import com.learning.realtimechatappbackend.userservice.userservice.model.UserAddress;
import com.learning.realtimechatappbackend.userservice.userservice.model.UserProfile;
import com.learning.realtimechatappbackend.userservice.userservice.repository.AccountRepository;
import com.learning.realtimechatappbackend.userservice.userservice.repository.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final AccountRepository accountRepository;

    public UserProfile createProfile(String userId, CreateProfileRequest createProfileRequest) {
        var userAccountOpt = accountRepository.findById(userId);
        if (userAccountOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Account not found");
        }

        if (profileRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile already exists");
        }

        var profile = new UserProfile();
        profile.setUserId(userId);
        profile.setName(createProfileRequest.getName());
        profile.setAvatarUrl("avtUrl"); //TODO: upload file to GCS
        profile.setAddresses(createProfileRequest.getAddresses().stream()
                .map(addressRequest -> new UserAddress(profile, addressRequest.getStreet(), addressRequest.getCity(),
                        addressRequest.getState(), addressRequest.getPostalCode()))
                .collect(Collectors.toList()));
        profile.setPhoneNumber(createProfileRequest.getPhoneNumber());

        if (createProfileRequest.getGender() != null) {
            profile.setGender(createProfileRequest.getGender());
        }

        profile.setDateOfBirth(createProfileRequest.getDateOfBirth());
        profile.setCreatedAt(Instant.now());
        profile.setUpdatedAt(Instant.now());

        return profileRepository.save(profile);
    }

    public UserProfile getProfileByUserId(String userId) {
        return profileRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
    }

    public void deleteProfile(String userId) {
        if (!profileRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }
        profileRepository.deleteById(userId);
    }
}
