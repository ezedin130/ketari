package com.example.ketari.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Storage service contract for uploading, reading, and deleting files.
 */
public interface FileStorageService {

    /**
     * Stores an uploaded multipart file securely under a partitioned subdirectory.
     *
     * @param file the multipart file to store
     * @param subDirectory target folder (e.g., "resumes", "avatars", "logos")
     * @param userId owner user ID for folder isolation
     * @return relative stored path
     */
    String storeFile(MultipartFile file, String subDirectory, Long userId);

    /**
     * Loads a stored file as a Spring Resource for downloading or streaming.
     *
     * @param relativePath relative storage path
     * @return readable Resource
     */
    Resource loadFileAsResource(String relativePath);

    /**
     * Deletes a stored file from disk.
     *
     * @param relativePath relative storage path
     */
    void deleteFile(String relativePath);
}
