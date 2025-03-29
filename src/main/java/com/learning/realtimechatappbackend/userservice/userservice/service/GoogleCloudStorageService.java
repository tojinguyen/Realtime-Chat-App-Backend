package com.learning.userservice.userservice.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.Storage;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleCloudStorageService {
    private final String bucketName = "your-bucket-name";
    private final Storage storage;

    public String uploadFile(MultipartFile file) {
        try {
            var fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            var blobId = BlobId.of(bucketName, fileName);
            var blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            storage.create(blobInfo, file.getBytes());
            return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }
    }
}
