package com.example.ketari.service;

import com.example.ketari.dto.job.JobRequest;
import com.example.ketari.dto.job.JobResponse;
import com.example.ketari.enums.EmploymentType;
import com.example.ketari.enums.UserRole;
import com.example.ketari.enums.WorkplaceType;
import com.example.ketari.exception.UnauthorizedException;
import com.example.ketari.model.EmployerProfile;
import com.example.ketari.model.Job;
import com.example.ketari.model.User;
import com.example.ketari.repository.ApplicationRepository;
import com.example.ketari.repository.EmployerProfileRepository;
import com.example.ketari.repository.JobRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.service.impl.JobServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployerProfileRepository employerProfileRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    private User approvedEmployer;
    private User pendingEmployer;
    private Job job;

    @BeforeEach
    void setUp() {
        this.approvedEmployer = User.builder()
                .name("Tech Corp")
                .email("employer@techcorp.com")
                .password("encoded_pass")
                .role(UserRole.EMPLOYER)
                .isApproved(true)
                .build();
        this.approvedEmployer.setId(10L);

        this.pendingEmployer = User.builder()
                .name("Unapproved Corp")
                .email("unapproved@corp.com")
                .password("encoded_pass")
                .role(UserRole.EMPLOYER)
                .isApproved(false)
                .build();
        this.pendingEmployer.setId(11L);

        this.job = Job.builder()
                .user(this.approvedEmployer)
                .title("Senior Java Developer")
                .description("Build microservices.")
                .companyName("Tech Corp")
                .location("Addis Ababa")
                .workplaceType(WorkplaceType.HYBRID)
                .employmentType(EmploymentType.FULL_TIME)
                .status("OPEN")
                .build();
        this.job.setId(100L);
    }

    @Test
    @DisplayName("Should successfully create job by approved employer")
    void shouldCreateJobByApprovedEmployer() {
        JobRequest request = JobRequest.builder()
                .title("Senior Java Developer")
                .description("Build microservices.")
                .location("Addis Ababa")
                .workplaceType(WorkplaceType.HYBRID)
                .employmentType(EmploymentType.FULL_TIME)
                .build();

        when(this.userRepository.findById(10L)).thenReturn(Optional.of(this.approvedEmployer));
        when(this.employerProfileRepository.findByUserId(10L)).thenReturn(Optional.of(EmployerProfile.builder()
                .companyName("Tech Corp")
                .build()));
        when(this.jobRepository.save(any(Job.class))).thenReturn(this.job);

        JobResponse response = this.jobService.createJob(request, 10L);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Senior Java Developer");
        assertThat(response.getCompanyName()).isEqualTo("Tech Corp");
    }

    @Test
    @DisplayName("Should forbid unapproved employer from creating job")
    void shouldForbidUnapprovedEmployerFromCreatingJob() {
        JobRequest request = JobRequest.builder()
                .title("Senior Java Developer")
                .description("Build microservices.")
                .build();

        when(this.userRepository.findById(11L)).thenReturn(Optional.of(this.pendingEmployer));

        assertThatThrownBy(() -> this.jobService.createJob(request, 11L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("not been approved");
    }

    @Test
    @DisplayName("Should retrieve job details by ID with application count")
    void shouldGetJobById() {
        when(this.jobRepository.findById(100L)).thenReturn(Optional.of(this.job));
        when(this.applicationRepository.countByJobId(100L)).thenReturn(5L);

        JobResponse response = this.jobService.getJobById(100L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getApplicationCount()).isEqualTo(5L);
    }
}
