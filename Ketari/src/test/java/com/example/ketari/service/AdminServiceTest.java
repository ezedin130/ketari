package com.example.ketari.service;

import com.example.ketari.dto.admin.PlatformStatsResponse;
import com.example.ketari.dto.user.UserResponse;
import com.example.ketari.enums.UserRole;
import com.example.ketari.model.User;
import com.example.ketari.repository.ApplicationRepository;
import com.example.ketari.repository.JobRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User pendingEmployer;

    @BeforeEach
    void setUp() {
        this.pendingEmployer = User.builder()
                .name("Acme Corp")
                .email("hr@acme.com")
                .role(UserRole.EMPLOYER)
                .isApproved(false)
                .build();
        this.pendingEmployer.setId(55L);
    }

    @Test
    @DisplayName("Should approve pending employer and send notification")
    void shouldApproveEmployer() {
        when(this.userRepository.findById(55L)).thenReturn(Optional.of(this.pendingEmployer));
        when(this.userRepository.save(any(User.class))).thenReturn(this.pendingEmployer);

        UserResponse response = this.adminService.approveEmployer(55L, true);

        assertThat(response).isNotNull();
        assertThat(response.getIsApproved()).isTrue();
        verify(this.notificationService).sendNotification(any(User.class), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should compile platform statistics correctly")
    void shouldGetPlatformStats() {
        when(this.userRepository.count()).thenReturn(100L);
        when(this.userRepository.countByRole(UserRole.EMPLOYEE)).thenReturn(80L);
        when(this.userRepository.countByRole(UserRole.EMPLOYER)).thenReturn(18L);
        when(this.userRepository.countByRoleAndIsApproved(UserRole.EMPLOYER, false)).thenReturn(3L);
        when(this.jobRepository.count()).thenReturn(50L);
        when(this.jobRepository.countByStatus("OPEN")).thenReturn(40L);
        when(this.applicationRepository.count()).thenReturn(200L);

        PlatformStatsResponse stats = this.adminService.getPlatformStats();

        assertThat(stats).isNotNull();
        assertThat(stats.getTotalUsers()).isEqualTo(100L);
        assertThat(stats.getTotalJobSeekers()).isEqualTo(80L);
        assertThat(stats.getTotalEmployers()).isEqualTo(18L);
        assertThat(stats.getPendingEmployers()).isEqualTo(3L);
        assertThat(stats.getTotalJobs()).isEqualTo(50L);
        assertThat(stats.getOpenJobs()).isEqualTo(40L);
        assertThat(stats.getTotalApplications()).isEqualTo(200L);
    }
}
