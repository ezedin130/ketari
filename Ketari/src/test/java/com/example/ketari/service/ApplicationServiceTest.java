package com.example.ketari.service;

import com.example.ketari.dto.application.ApplicationRequest;
import com.example.ketari.dto.application.ApplicationResponse;
import com.example.ketari.dto.application.ApplicationStatusUpdateRequest;
import com.example.ketari.enums.ApplicationStatus;
import com.example.ketari.enums.UserRole;
import com.example.ketari.exception.BadRequestException;
import com.example.ketari.exception.DuplicateResourceException;
import com.example.ketari.model.Application;
import com.example.ketari.model.Job;
import com.example.ketari.model.Resume;
import com.example.ketari.model.User;
import com.example.ketari.repository.ApplicationRepository;
import com.example.ketari.repository.ApplicationStatusHistoryRepository;
import com.example.ketari.repository.JobRepository;
import com.example.ketari.repository.ResumeRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.service.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationStatusHistoryRepository statusHistoryRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private User applicant;
    private User employer;
    private Job openJob;
    private Job closedJob;
    private Resume resume;
    private Application application;

    @BeforeEach
    void setUp() {
        this.applicant = User.builder()
                .name("Dawit Haile")
                .email("dawit@example.com")
                .role(UserRole.EMPLOYEE)
                .isApproved(true)
                .build();
        this.applicant.setId(20L);

        this.employer = User.builder()
                .name("Ethio Telecom")
                .email("hr@ethiotelecom.et")
                .role(UserRole.EMPLOYER)
                .isApproved(true)
                .build();
        this.employer.setId(30L);

        this.openJob = Job.builder()
                .user(this.employer)
                .title("Network Engineer")
                .companyName("Ethio Telecom")
                .status("OPEN")
                .build();
        this.openJob.setId(200L);

        this.closedJob = Job.builder()
                .user(this.employer)
                .title("Old Post")
                .status("CLOSED")
                .build();
        this.closedJob.setId(201L);

        this.resume = Resume.builder()
                .user(this.applicant)
                .fileName("cv.pdf")
                .fileType("application/pdf")
                .fileSize(1024L)
                .storagePath("resumes/user_20/cv.pdf")
                .build();
        this.resume.setId(50L);

        this.application = Application.builder()
                .user(this.applicant)
                .job(this.openJob)
                .resume(this.resume)
                .coverLetter("Excited about this role.")
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        this.application.setId(1000L);
    }

    @Test
    @DisplayName("Should successfully submit candidate application to open job")
    void shouldApplySuccessfully() {
        ApplicationRequest request = ApplicationRequest.builder()
                .jobId(200L)
                .resumeId(50L)
                .coverLetter("Excited about this role.")
                .build();

        when(this.userRepository.findById(20L)).thenReturn(Optional.of(this.applicant));
        when(this.jobRepository.findById(200L)).thenReturn(Optional.of(this.openJob));
        when(this.applicationRepository.existsByUserIdAndJobId(20L, 200L)).thenReturn(false);
        when(this.resumeRepository.findByIdAndUserId(50L, 20L)).thenReturn(Optional.of(this.resume));
        when(this.applicationRepository.save(any(Application.class))).thenReturn(this.application);

        ApplicationResponse response = this.applicationService.apply(request, 20L);

        assertThat(response).isNotNull();
        assertThat(response.getJobTitle()).isEqualTo("Network Engineer");
        assertThat(response.getApplicantEmail()).isEqualTo("dawit@example.com");
        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.APPLIED);
        verify(this.statusHistoryRepository).save(any());
    }

    @Test
    @DisplayName("Should reject application if job is closed")
    void shouldRejectWhenJobClosed() {
        ApplicationRequest request = ApplicationRequest.builder()
                .jobId(201L)
                .resumeId(50L)
                .build();

        when(this.userRepository.findById(20L)).thenReturn(Optional.of(this.applicant));
        when(this.jobRepository.findById(201L)).thenReturn(Optional.of(this.closedJob));

        assertThatThrownBy(() -> this.applicationService.apply(request, 20L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("no longer accepting applications");
    }

    @Test
    @DisplayName("Should reject duplicate application by same candidate")
    void shouldRejectDuplicateApplication() {
        ApplicationRequest request = ApplicationRequest.builder()
                .jobId(200L)
                .resumeId(50L)
                .build();

        when(this.userRepository.findById(20L)).thenReturn(Optional.of(this.applicant));
        when(this.jobRepository.findById(200L)).thenReturn(Optional.of(this.openJob));
        when(this.applicationRepository.existsByUserIdAndJobId(20L, 200L)).thenReturn(true);

        assertThatThrownBy(() -> this.applicationService.apply(request, 20L))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already applied");
    }

    @Test
    @DisplayName("Should successfully update application status and save history")
    void shouldUpdateApplicationStatus() {
        ApplicationStatusUpdateRequest request = ApplicationStatusUpdateRequest.builder()
                .status(ApplicationStatus.INTERVIEW)
                .note("Candidate selected for round 1 interview.")
                .build();

        when(this.applicationRepository.findById(1000L)).thenReturn(Optional.of(this.application));
        when(this.userRepository.findById(30L)).thenReturn(Optional.of(this.employer));
        when(this.applicationRepository.save(any(Application.class))).thenReturn(this.application);

        ApplicationResponse response = this.applicationService.updateApplicationStatus(1000L, request, 30L);

        assertThat(response).isNotNull();
        verify(this.statusHistoryRepository).save(any());
        verify(this.notificationService).sendNotification(any(User.class), any(), any(), any(), any(), any());
    }
}
