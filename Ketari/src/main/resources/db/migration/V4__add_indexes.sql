-- V4__add_indexes.sql
-- Adding performance indexes for job seeker platform on frequently queried columns

-- Jobs Table Indexes
CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_workplace_type ON jobs(workplace_type);
CREATE INDEX idx_jobs_employment_type ON jobs(employment_type);
CREATE INDEX idx_jobs_category ON jobs(category);
CREATE INDEX idx_jobs_app_deadline ON jobs(application_deadline);

-- Saved Jobs Indexes
CREATE INDEX idx_saved_jobs_user ON saved_jobs(user_id);
CREATE INDEX idx_saved_jobs_job ON saved_jobs(job_id);

-- Applications Indexes
CREATE INDEX idx_applications_user ON applications(user_id);
CREATE INDEX idx_applications_job ON applications(job_id);
CREATE INDEX idx_applications_status ON applications(status);

-- Notifications Indexes
CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read);

-- Resumes Indexes
CREATE INDEX idx_resumes_user ON resumes(user_id);
