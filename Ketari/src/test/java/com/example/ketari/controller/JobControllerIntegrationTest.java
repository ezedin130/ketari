package com.example.ketari.controller;

import com.example.ketari.enums.EmploymentType;
import com.example.ketari.enums.WorkplaceType;
import com.example.ketari.model.Job;
import com.example.ketari.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JobControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobRepository jobRepository;

    @BeforeEach
    void setUp() {
        this.jobRepository.deleteAll();

        Job j1 = Job.builder()
                .title("Mobile Flutter Developer")
                .description("Build cross-platform applications.")
                .companyName("Ketari Tech")
                .location("Addis Ababa")
                .workplaceType(WorkplaceType.REMOTE)
                .employmentType(EmploymentType.FULL_TIME)
                .category("Engineering")
                .postedDate(LocalDateTime.now())
                .status("OPEN")
                .build();

        Job j2 = Job.builder()
                .title("Product Manager")
                .description("Drive product vision.")
                .companyName("Ketari Tech")
                .location("Addis Ababa")
                .workplaceType(WorkplaceType.ON_SITE)
                .employmentType(EmploymentType.FULL_TIME)
                .category("Operations")
                .postedDate(LocalDateTime.now())
                .status("OPEN")
                .build();

        this.jobRepository.save(j1);
        this.jobRepository.save(j2);
    }

    @Test
    @DisplayName("GET /api/jobs/public should return paginated list of open jobs")
    void testGetPublicJobs() throws Exception {
        this.mockMvc.perform(get("/api/jobs/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("GET /api/jobs/public with keyword filter should return matched job")
    void testSearchByKeyword() throws Exception {
        this.mockMvc.perform(get("/api/jobs/public")
                        .param("keyword", "Flutter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Mobile Flutter Developer"));
    }

    @Test
    @DisplayName("GET /api/jobs/public/categories should return available categories")
    void testGetCategories() throws Exception {
        this.mockMvc.perform(get("/api/jobs/public/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(5))));
    }
}
