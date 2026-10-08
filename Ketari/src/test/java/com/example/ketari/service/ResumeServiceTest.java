package com.example.ketari.service;

import com.example.ketari.dto.resume.ResumeResponse;
import com.example.ketari.enums.UserRole;
import com.example.ketari.exception.BadRequestException;
import com.example.ketari.exception.ResourceNotFoundException;
import com.example.ketari.model.Resume;
import com.example.ketari.model.User;
import com.example.ketari.repository.ApplicationRepository;
import com.example.ketari.repository.ResumeRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.service.impl.ResumeServiceImpl;
import com.example.ketari.service.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    private User candidateUser;
    private Resume resume;

    @BeforeEach
    void setUp() {
        this.candidateUser = User.builder()
                .name("Helen Berhanu")
                .email("helen@example.com")
                .role(UserRole.EMPLOYEE)
                .build();
        this.candidateUser.setId(10L);

        this.resume = Resume.builder()
                .user(this.candidateUser)
                .fileName("cv_helen.pdf")
                .fileType("application/pdf")
                .fileSize(2048L)
                .storagePath("resumes/user_10/cv_helen.pdf")
                .build();
        this.resume.setId(101L);
    }

    @Test
    @DisplayName("Should successfully upload a PDF resume")
    void shouldUploadResumeSuccessfully() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "cv_helen.pdf", "application/pdf", "dummy pdf content".getBytes()
        );

        when(this.userRepository.findById(10L)).thenReturn(Optional.of(this.candidateUser));
        when(this.resumeRepository.countByUserId(10L)).thenReturn(1L);
        when(this.fileStorageService.storeFile(eq(file), eq("resumes"), eq(10L))).thenReturn("resumes/user_10/cv_helen.pdf");
        when(this.resumeRepository.save(any(Resume.class))).thenReturn(this.resume);

        ResumeResponse response = this.resumeService.uploadResume(file, 10L);

        assertThat(response).isNotNull();
        assertThat(response.getFileName()).isEqualTo("cv_helen.pdf");
        assertThat(response.getFileType()).isEqualTo("application/pdf");
    }

    @Test
    @DisplayName("Should throw BadRequestException if user exceeds max resumes limit")
    void shouldThrowWhenMaxResumesExceeded() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "cv_helen.pdf", "application/pdf", "dummy pdf content".getBytes()
        );

        when(this.userRepository.findById(10L)).thenReturn(Optional.of(this.candidateUser));
        when(this.resumeRepository.countByUserId(10L)).thenReturn(10L);

        assertThatThrownBy(() -> this.resumeService.uploadResume(file, 10L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Maximum limit of 10 resumes reached");
    }

    @Test
    @DisplayName("Should download resume if requesting user is owner")
    void shouldDownloadResumeWhenOwner() {
        when(this.resumeRepository.findById(101L)).thenReturn(Optional.of(this.resume));
        when(this.userRepository.findById(10L)).thenReturn(Optional.of(this.candidateUser));
        when(this.fileStorageService.loadFileAsResource("resumes/user_10/cv_helen.pdf"))
                .thenReturn(new ByteArrayResource("dummy file content".getBytes()));

        Resource resource = this.resumeService.downloadResume(101L, 10L);

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
    }

    @Test
    @DisplayName("Should delete resume from database and disk")
    void shouldDeleteResume() {
        when(this.resumeRepository.findByIdAndUserId(101L, 10L)).thenReturn(Optional.of(this.resume));

        this.resumeService.deleteResume(101L, 10L);

        verify(this.fileStorageService).deleteFile("resumes/user_10/cv_helen.pdf");
        verify(this.resumeRepository).delete(this.resume);
    }
}
