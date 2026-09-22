package com.voicelk.voicelk_be.service;

import org.springframework.web.multipart.MultipartFile;

public interface SupabaseStorageService {

    String uploadFile(MultipartFile file, String bucketName, String pathPrefix) throws Exception;

    /**
     * Uploads bytes that never arrived as an HTTP upload — generated speech, for
     * example. The file name is only used for its extension; the stored object still
     * gets a unique name.
     */
    String uploadBytes(byte[] content, String fileName, String contentType, String bucketName, String pathPrefix)
            throws Exception;
}
