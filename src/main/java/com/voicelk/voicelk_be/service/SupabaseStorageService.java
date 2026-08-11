package com.voicelk.voicelk_be.service;

import org.springframework.web.multipart.MultipartFile;

public interface SupabaseStorageService {
    String uploadFile(MultipartFile file, String bucketName, String pathPrefix) throws Exception;
}
