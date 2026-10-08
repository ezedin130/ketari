-- Jobs seed data for verifying Pagination, Filters (Location, Workplace, Type, Skills), Salary, etc.

INSERT INTO jobs (title, description, company_name, location, workplace_type, employment_type, salary_range, experience_level, category, status) VALUES 
('Senior Frontend Engineer', 'Build modern scalable apps.', 'TechCorp', 'Addis Ababa', 'REMOTE', 'FULL_TIME', '$80k-$120k', 'Senior', 'Engineering', 'OPEN'),
('Backend Developer', 'Java Spring Boot microservices.', 'FinanceTech', 'Nairobi', 'HYBRID', 'FULL_TIME', '$70k-$100k', 'Mid-Level', 'Engineering', 'OPEN'),
('Part-Time Marketing Specialist', 'Social media marketing campaigns.', 'AdWorld', 'Lagos', 'REMOTE', 'PART_TIME', '$20/hr', 'Entry-Level', 'Marketing', 'OPEN'),
('Data Analyst', 'SQL, Python, and Tableau.', 'DataSolutions', 'Addis Ababa', 'ON_SITE', 'FULL_TIME', '$50k-$80k', 'Mid-Level', 'Data', 'OPEN'),
('Contract Designer', 'UI/UX Mobile redesign.', 'DesignHub', 'Remote', 'REMOTE', 'CONTRACT', '$50/hr', 'Senior', 'Design', 'OPEN'),
('HR Manager', 'Talent acquisition and operations.', 'PeopleFirst', 'Addis Ababa', 'ON_SITE', 'FULL_TIME', '$60k-$90k', 'Senior', 'Human Resources', 'OPEN'),
('Intern Software Engineer', 'Learn and build.', 'StartupX', 'Remote', 'REMOTE', 'INTERNSHIP', '$3k/mo', 'Entry-Level', 'Engineering', 'OPEN');
