package com.learning.userservice.userservice.service;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.learning.userservice.userservice.dto.request.CreateProfileRequest;
import com.learning.userservice.userservice.model.UserAddress;
import com.learning.userservice.userservice.model.UserProfile;
import com.learning.userservice.userservice.repository.AccountRepository;
import com.learning.userservice.userservice.repository.ProfileRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final AccountRepository accountRepository;
    private final GoogleCloudStorageService googleCloudStorageService;

    public UserProfile createProfile(String userId, CreateProfileRequest createProfileRequest) {
        var userAccountOpt = accountRepository.findById(userId);
        if (userAccountOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Account not found");
        }

        if (profileRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile already exists");
        }

        var avtUrl = googleCloudStorageService.uploadFile(createProfileRequest.getAvatar());

        var profile = new UserProfile();
        profile.setUserId(userId);
        profile.setName(createProfileRequest.getName());
        profile.setAvatarUrl(avtUrl);
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
