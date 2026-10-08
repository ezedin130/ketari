package com.example.ketari.service.storage;

import com.example.ketari.exception.BadRequestException;
import com.example.ketari.exception.FileValidationException;
import com.example.ketari.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Local filesystem implementation of FileStorageService with path sanitization,
 * MIME type enforcement, and directory isolation.
 */
@Slf4j
@Service
public class LocalStorageService implements FileStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "png", "jpg", "jpeg"
    );

    private final Path rootLocation;

    public LocalStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.rootLocation);
            log.info("Initialized upload directory at: {}", this.rootLocation);
        } catch (IOException e) {
            log.error("Could not initialize upload storage location", e);
            throw new IllegalStateException("Could not initialize storage directory", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("File size exceeds maximum limit of 5MB");
        }

        String rawOriginalFilename = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "file"));

        if (rawOriginalFilename.contains("..") || rawOriginalFilename.contains("/") || rawOriginalFilename.contains("\\")) {
            throw new FileValidationException("Filename contains invalid path sequence: " + rawOriginalFilename);
        }

        String extension = getFileExtension(rawOriginalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileValidationException("Unsupported file type: ." + extension + ". Allowed: " + ALLOWED_EXTENSIONS);
        }

        String uniqueFileName = UUID.randomUUID() + "_" + rawOriginalFilename;
        String safeSubDir = StringUtils.cleanPath(subDirectory).replace("..", "");
        Path targetDir = this.rootLocation.resolve(safeSubDir).resolve("user_" + userId).normalize();

        try {
            Files.createDirectories(targetDir);

            Path targetPath = targetDir.resolve(uniqueFileName).normalize();
            if (!targetPath.startsWith(this.rootLocation)) {
                throw new FileValidationException("Cannot store file outside current directory");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            String relativePath = this.rootLocation.relativize(targetPath).toString().replace("\\", "/");
            log.info("File successfully stored: {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            log.error("Failed to store file {}", rawOriginalFilename, e);
            throw new FileValidationException("Failed to store file: " + e.getMessage());
        }
    }

    @Override
    public Resource loadFileAsResource(String relativePath) {
        try {
            String sanitized = StringUtils.cleanPath(relativePath);
            if (sanitized.contains("..")) {
                throw new BadRequestException("Invalid path sequence");
            }

            Path filePath = this.rootLocation.resolve(sanitized).normalize();
            if (!filePath.startsWith(this.rootLocation)) {
                throw new BadRequestException("Cannot access file outside storage root");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File", "path", relativePath);
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("File", "path", relativePath);
        }
    }

    @Override
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        try {
            String sanitized = StringUtils.cleanPath(relativePath);
            if (sanitized.contains("..")) {
                return;
            }
            Path filePath = this.rootLocation.resolve(sanitized).normalize();
            if (filePath.startsWith(this.rootLocation)) {
                Files.deleteIfExists(filePath);
                log.info("Deleted file: {}", relativePath);
            }
        } catch (IOException e) {
            log.warn("Failed to delete file from disk: {}", relativePath, e);
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }
}
