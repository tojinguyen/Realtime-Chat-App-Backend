package com.learning.userservice.userservice.dto.request;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileRequest {
    private String userId;
    private String name;
    private MultipartFile avatar;
    private List<AddressRequest> addresses;
    private String phoneNumber;
    private String gender;
    private Instant dateOfBirth;
}
