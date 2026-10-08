package com.example.ketari.service;

import com.example.ketari.dto.job.SavedJobResponse;
import com.example.ketari.enums.UserRole;
import com.example.ketari.exception.DuplicateResourceException;
import com.example.ketari.model.Job;
import com.example.ketari.model.SavedJob;
import com.example.ketari.model.User;
import com.example.ketari.repository.JobRepository;
import com.example.ketari.repository.SavedJobRepository;
import com.example.ketari.repository.UserRepository;
import com.example.ketari.service.impl.SavedJobServiceImpl;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavedJobServiceTest {

    @Mock
    private SavedJobRepository savedJobRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SavedJobServiceImpl savedJobService;

    private User user;
    private Job job;

    @BeforeEach
    void setUp() {
        this.user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .role(UserRole.EMPLOYEE)
                .build();
        this.user.setId(5L);

        this.job = Job.builder()
                .title("Fullstack Developer")
                .companyName("StartHub")
                .build();
        this.job.setId(50L);
    }

    @Test
    @DisplayName("Should successfully bookmark job")
    void shouldSaveJob() {
        when(this.savedJobRepository.existsByUserIdAndJobId(5L, 50L)).thenReturn(false);
        when(this.userRepository.findById(5L)).thenReturn(Optional.of(this.user));
        when(this.jobRepository.findById(50L)).thenReturn(Optional.of(this.job));
        when(this.savedJobRepository.save(any(SavedJob.class))).thenAnswer(i -> {
            SavedJob sj = i.getArgument(0);
            sj.setId(1L);
            return sj;
        });

        SavedJobResponse response = this.savedJobService.saveJob(50L, 5L);

        assertThat(response).isNotNull();
        assertThat(response.getJobId()).isEqualTo(50L);
        assertThat(response.getJobTitle()).isEqualTo("Fullstack Developer");
    }

    @Test
    @DisplayName("Should reject duplicate bookmark")
    void shouldRejectDuplicateBookmark() {
        when(this.savedJobRepository.existsByUserIdAndJobId(5L, 50L)).thenReturn(true);

        assertThatThrownBy(() -> this.savedJobService.saveJob(50L, 5L))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already bookmarked");
    }

    @Test
    @DisplayName("Should remove bookmark")
    void shouldRemoveBookmark() {
        SavedJob savedJob = SavedJob.builder().user(this.user).job(this.job).build();
        when(this.savedJobRepository.findByUserIdAndJobId(5L, 50L)).thenReturn(Optional.of(savedJob));

        this.savedJobService.removeSavedJob(50L, 5L);

        verify(this.savedJobRepository).delete(savedJob);
    }
}
