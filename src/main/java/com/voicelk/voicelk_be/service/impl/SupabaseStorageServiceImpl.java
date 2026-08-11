package com.voicelk.voicelk_be.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.voicelk.voicelk_be.service.SupabaseStorageService;

import java.util.UUID;

@Service
public class SupabaseStorageServiceImpl implements SupabaseStorageService {

    @Value("${supabase.storage.url}")
    private String supabaseUrl;

    @Value("${supabase.storage.key}")
    private String supabaseKey;

    @Override
    public String uploadFile(MultipartFile file, String bucketName, String pathPrefix) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // Generate a unique filename
        String fileName = pathPrefix + "/" + UUID.randomUUID().toString() + extension;
        
        // Supabase REST API endpoint for uploading
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("apikey", supabaseKey);
        
        String mimeType = file.getContentType();
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        headers.set("Content-Type", mimeType);

        // Supabase expects the raw bytes of the file for binary uploads
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
            } else {
                throw new RuntimeException("Failed to upload file. Status: " + response.getStatusCode());
            }
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            String errorBody = e.getResponseBodyAsString();
            System.err.println("Supabase Storage Error: " + errorBody);
            throw new RuntimeException("Supabase upload failed: " + errorBody, e);
        }
    }
}
