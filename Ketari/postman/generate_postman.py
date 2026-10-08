import json
import os
import uuid

def make_url(full_path):
    clean_full = full_path
    if not clean_full.startswith("/"):
        clean_full = "/" + clean_full
    
    raw = "{{base_url}}" + clean_full
    
    if "?" in clean_full:
        path_part, query_part = clean_full.split("?", 1)
        query_items = []
        for pair in query_part.split("&"):
            if "=" in pair:
                k, v = pair.split("=", 1)
                query_items.append({"key": k, "value": v})
            else:
                query_items.append({"key": pair, "value": ""})
    else:
        path_part = clean_full
        query_items = []
    
    segments = [s for s in path_part.lstrip("/").split("/") if s]
    
    url_obj = {
        "raw": raw,
        "host": ["{{base_url}}"],
        "path": segments
    }
    if query_items:
        url_obj["query"] = query_items
    return url_obj

def make_header(key, value):
    return {"key": key, "value": value, "type": "text"}

def make_auth_bearer(token_var):
    return {
        "type": "bearer",
        "bearer": [
            {
                "key": "token",
                "value": "{{" + token_var + "}}",
                "type": "string"
            }
        ]
    }

def make_request(name, method, path, headers=None, body_json=None, form_data=None, auth_var=None, tests=None, prerequest=None, description=""):
    req = {
        "name": name,
        "request": {
            "method": method,
            "header": headers or [],
            "url": make_url(path),
            "description": description
        },
        "response": []
    }
    
    if auth_var:
        req["request"]["auth"] = make_auth_bearer(auth_var)
    
    if body_json is not None:
        if req["request"]["header"] is None:
            req["request"]["header"] = []
        req["request"]["header"].append(make_header("Content-Type", "application/json"))
        req["request"]["body"] = {
            "mode": "raw",
            "raw": json.dumps(body_json, indent=2),
            "options": {
                "raw": {
                    "language": "json"
                }
            }
        }
    elif form_data is not None:
        req["request"]["body"] = {
            "mode": "formdata",
            "formdata": form_data
        }
    
    events = []
    if prerequest:
        events.append({
            "listen": "prerequest",
            "script": {
                "exec": prerequest,
                "type": "text/javascript"
            }
        })
    if tests:
        events.append({
            "listen": "test",
            "script": {
                "exec": tests,
                "type": "text/javascript"
            }
        })
    if events:
        req["event"] = events
    
    return req

def build_collection():
    collection = {
        "info": {
            "_postman_id": "9d3e8f41-b827-4a59-86c5-28b37e8c3109",
            "name": "KETARI JOB BOARD & RECRUITMENT PLATFORM API",
            "description": "# Ketari Job Board & Recruitment Platform API Collection\n\nProduction-quality Postman collection covering all backend REST endpoints for the **Ketari Platform**:\n- **00 - Health & Documentation**: System readiness, MySQL connectivity, and OpenAPI 3.0 specification\n- **01 - Authentication & Identity**: Registration, login, stateless JJWT refresh, and identity inspection\n- **02 - Public Job Discovery**: Multi-criteria search (keyword, location, workplace, employment type, salary, skills), pagination, and detail inspection\n- **03 - Job Seeker Profile**: Personal bio, skills, education, contact info, and profile avatar upload\n- **04 - Resumes & CVs**: Document upload (PDF/DOCX, max 5MB), metadata inspection, binary download, and deletion\n- **05 - Employer Profile & Branding**: Company profile management, industry, website, and logo upload\n- **06 - Employer Job Management**: Posting vacancies, updates, listings, and deletion (enforces admin approval vetting)\n- **07 - Saved Jobs (Bookmarks)**: Candidate job bookmarking, listing, status check, and deletion\n- **08 - Applications (Candidate)**: Job application submission with resume and cover letter, status audit trail\n- **09 - Employer Application Review**: Employer candidate screening, stage progression (SUBMITTED -> UNDER_REVIEW -> SHORTLISTED -> INTERVIEW -> ACCEPTED / REJECTED)\n- **10 - User Notifications**: System notifications, unread counters, mark single as read, and bulk mark all read\n- **11 - Administrator Governance**: Operational stats, pending employer vetting, approving/rejecting employers, and content moderation\n- **12 - Security & RBAC Enforcement**: Negative testing for 401 Unauthorized, 403 Forbidden, 400 Validation, and 409 Conflict\n- **13 - Full End-to-End Hiring Lifecycle**: 18-step end-to-end flow from employer vetting to application acceptance\n\n### Authentication Roles\n1. `EMPLOYEE`: Job seekers searching jobs, uploading resumes, bookmarking, and applying.\n2. `EMPLOYER`: Companies posting vacancies, uploading logos, and reviewing applicants.\n3. `ADMIN`: Platform operators vetting employers, monitoring metrics, and moderating listings.\n\n### Standard API Response Wrapper\nAll successful responses return unified JSON matching `ApiResponse<T>`:\n```json\n{\n  \"timestamp\": \"2026-10-07T14:30:00Z\",\n  \"status\": 200,\n  \"message\": \"Job retrieved successfully\",\n  \"data\": { ... },\n  \"correlationId\": \"c0a80101-...\"\n}\n```",
            "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
        },
        "item": []
    }

    # =========================================================================
    # 00 - Health & Documentation
    # =========================================================================
    folder_health = {
        "name": "00 - Health & Documentation",
        "description": "System readiness, database connectivity, and OpenAPI documentation discovery.",
        "item": [
            make_request(
                name="Check System Health",
                method="GET",
                path="/api/health",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var json = pm.response.json();",
                    "pm.test('System health status is UP', function () {",
                    "    pm.expect(json.success).to.be.true;",
                    "    pm.expect(json.data.status).to.eql('UP');",
                    "    pm.expect(json.data.database).to.include('CONNECTED');",
                    "    pm.expect(json.data.application).to.include('Ketari');",
                    "});"
                ],
                description="Endpoint: GET /api/health\nPublic endpoint verifying system status, active profile, and live MySQL database connectivity."
            ),
            make_request(
                name="Get OpenAPI 3.0 Documentation",
                method="GET",
                path="/v3/api-docs",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var json = pm.response.json();",
                    "pm.test('Valid OpenAPI 3.0 specification', function () {",
                    "    pm.expect(json.openapi).to.exist;",
                    "    pm.expect(json.info.title).to.include('Ketari');",
                    "    pm.expect(json.paths).to.exist;",
                    "});"
                ],
                description="Endpoint: GET /v3/api-docs\nRetrieves the machine-readable OpenAPI 3.0 contract specification."
            )
        ]
    }

    # =========================================================================
    # 01 - Authentication & Identity
    # =========================================================================
    folder_auth = {
        "name": "01 - Authentication & Identity",
        "description": "User registration, authentication, stateless JWT refresh, and identity inspection.",
        "item": [
            make_request(
                name="Register Candidate (EMPLOYEE)",
                method="POST",
                path="/api/auth/register",
                prerequest=[
                    "var suffix = Date.now().toString().slice(-6);",
                    "pm.environment.set('candidate_email', 'candidate_' + suffix + '@ethiopia.et');"
                ],
                body_json={
                    "name": "Tariku Mengistu",
                    "email": "{{candidate_email}}",
                    "password": "{{candidate_password}}",
                    "role": "EMPLOYEE"
                },
                tests=[
                    "pm.test('Status code is 201 Created', function () {",
                    "    pm.response.to.have.status(201);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Candidate registration successful', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.accessToken).to.not.be.empty;",
                    "    pm.expect(res.data.refreshToken).to.not.be.empty;",
                    "    pm.expect(res.data.user.role).to.eql('EMPLOYEE');",
                    "    pm.environment.set('candidate_token', res.data.accessToken);",
                    "    pm.environment.set('candidate_refresh_token', res.data.refreshToken);",
                    "    pm.environment.set('candidate_user_id', res.data.user.id);",
                    "});"
                ],
                description="Endpoint: POST /api/auth/register\nRegisters a new Job Seeker (EMPLOYEE) account and returns signed access and refresh tokens."
            ),
            make_request(
                name="Register Employer (EMPLOYER)",
                method="POST",
                path="/api/auth/register",
                prerequest=[
                    "var suffix = Date.now().toString().slice(-6);",
                    "pm.environment.set('employer_email', 'employer_' + suffix + '@horizon.et');"
                ],
                body_json={
                    "name": "Horizon Tech Corp",
                    "email": "{{employer_email}}",
                    "password": "{{employer_password}}",
                    "role": "EMPLOYER"
                },
                tests=[
                    "pm.test('Status code is 201 Created', function () {",
                    "    pm.response.to.have.status(201);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Employer registered pending approval', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.user.role).to.eql('EMPLOYER');",
                    "    pm.expect(res.data.user.isApproved).to.be.false;",
                    "    pm.environment.set('employer_user_id', res.data.user.id);",
                    "});"
                ],
                description="Endpoint: POST /api/auth/register\nRegisters an Employer account. Defaults to isApproved=false, awaiting admin approval."
            ),
            make_request(
                name="Register Administrator (ADMIN)",
                method="POST",
                path="/api/auth/register",
                prerequest=[
                    "var suffix = Date.now().toString().slice(-6);",
                    "pm.environment.set('admin_email', 'admin_' + suffix + '@ketari.com');"
                ],
                body_json={
                    "name": "Platform Administrator",
                    "email": "{{admin_email}}",
                    "password": "{{admin_password}}",
                    "role": "ADMIN"
                },
                tests=[
                    "pm.test('Status code is 201 Created', function () {",
                    "    pm.response.to.have.status(201);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Admin registration successful', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.accessToken).to.not.be.empty;",
                    "    pm.expect(res.data.user.role).to.eql('ADMIN');",
                    "    pm.environment.set('admin_token', res.data.accessToken);",
                    "    pm.environment.set('admin_user_id', res.data.user.id);",
                    "});"
                ],
                description="Endpoint: POST /api/auth/register\nRegisters a new platform administrator and extracts admin_token."
            ),
            make_request(
                name="Login as Candidate",
                method="POST",
                path="/api/auth/login",
                body_json={
                    "email": "{{candidate_email}}",
                    "password": "{{candidate_password}}"
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Candidate login returns valid tokens', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.accessToken).to.not.be.empty;",
                    "    pm.expect(res.data.user.role).to.eql('EMPLOYEE');",
                    "    pm.environment.set('candidate_token', res.data.accessToken);",
                    "    pm.environment.set('candidate_refresh_token', res.data.refreshToken);",
                    "});"
                ],
                description="Endpoint: POST /api/auth/login\nAuthenticates candidate credentials and updates candidate_token."
            ),
            make_request(
                name="Login as Administrator",
                method="POST",
                path="/api/auth/login",
                body_json={
                    "email": "{{admin_email}}",
                    "password": "{{admin_password}}"
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Admin login successful', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.accessToken).to.not.be.empty;",
                    "    pm.expect(res.data.user.role).to.eql('ADMIN');",
                    "    pm.environment.set('admin_token', res.data.accessToken);",
                    "});"
                ],
                description="Endpoint: POST /api/auth/login\nAuthenticates administrator credentials and captures admin_token."
            ),
            make_request(
                name="Stateless Token Refresh",
                method="POST",
                path="/api/auth/refresh",
                body_json={
                    "refreshToken": "{{candidate_refresh_token}}"
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Token refreshed successfully with zero database contention', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.accessToken).to.not.be.empty;",
                    "    pm.environment.set('candidate_token', res.data.accessToken);",
                    "});"
                ],
                description="Endpoint: POST /api/auth/refresh\nUses stateless JJWT refresh token to generate a fresh 15-minute access token."
            ),
            make_request(
                name="Get Current Authenticated User (/me)",
                method="GET",
                path="/api/auth/me",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns authenticated user profile', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.email).to.eql(pm.environment.get('candidate_email'));",
                    "    pm.expect(res.data.role).to.eql('EMPLOYEE');",
                    "});"
                ],
                description="Endpoint: GET /api/auth/me\nInspects current security principal details from the Authorization header."
            )
        ]
    }

    # =========================================================================
    # 02 - Public Job Discovery
    # =========================================================================
    folder_jobs = {
        "name": "02 - Public Job Discovery",
        "description": "Public search, multi-criteria filtering, categories discovery, and individual job viewing.",
        "item": [
            make_request(
                name="Search Public Jobs (Default Page)",
                method="GET",
                path="/api/jobs/public?page=0&size=10&sortBy=postedDate&direction=desc",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns paginated jobs', function () {",
                    "    pm.expect(res.content).to.be.an('array');",
                    "    pm.expect(res.page).to.eql(0);",
                    "    if (res.content.length > 0) {",
                    "        pm.environment.set('public_job_id', res.content[0].id);",
                    "    }",
                    "});"
                ],
                description="Endpoint: GET /api/jobs/public\nRetrieves paginated active jobs ordered by postedDate descending."
            ),
            make_request(
                name="Search Jobs by Keyword",
                method="GET",
                path="/api/jobs/public?keyword=Engineer",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Keyword filter returns results', function () {",
                    "    pm.expect(res.content).to.be.an('array');",
                    "});"
                ],
                description="Endpoint: GET /api/jobs/public?keyword=Engineer\nFilters jobs matching keyword in title or description."
            ),
            make_request(
                name="Filter Jobs by Workplace Type (REMOTE)",
                method="GET",
                path="/api/jobs/public?workplaceType=REMOTE",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Remote filter applied', function () {",
                    "    pm.expect(res.content).to.be.an('array');",
                    "});"
                ],
                description="Endpoint: GET /api/jobs/public?workplaceType=REMOTE\nFilters jobs by REMOTE, HYBRID, or ON_SITE workplace."
            ),
            make_request(
                name="Filter Jobs by Employment Type (FULL_TIME)",
                method="GET",
                path="/api/jobs/public?employmentType=FULL_TIME",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Employment type filter applied', function () {",
                    "    pm.expect(res.content).to.be.an('array');",
                    "});"
                ],
                description="Endpoint: GET /api/jobs/public?employmentType=FULL_TIME\nFilters jobs by FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, or TEMPORARY."
            ),
            make_request(
                name="Filter Jobs by Category & Location",
                method="GET",
                path="/api/jobs/public?category=Engineering&location=Addis%20Ababa",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Compound filter applied', function () {",
                    "    pm.expect(res.content).to.be.an('array');",
                    "});"
                ],
                description="Endpoint: GET /api/jobs/public?category=Engineering&location=Addis Ababa\nDemonstrates multi-attribute dynamic query specification."
            ),
            make_request(
                name="Get Available Job Categories",
                method="GET",
                path="/api/jobs/public/categories",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns list of categories', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data).to.be.an('array');",
                    "});"
                ],
                description="Endpoint: GET /api/jobs/public/categories\nReturns distinct categories from active job listings."
            ),
            make_request(
                name="Get Job Details by ID",
                method="GET",
                path="/api/jobs/public/{{public_job_id}}",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns job details', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.id).to.eql(Number(pm.environment.get('public_job_id')));",
                    "    pm.expect(res.data.title).to.not.be.empty;",
                    "});"
                ],
                description="Endpoint: GET /api/jobs/public/{id}\nRetrieves full job details including required skills, deadline, and employer info."
            )
        ]
    }

    # =========================================================================
    # 03 - Job Seeker Profile
    # =========================================================================
    folder_candidate_profile = {
        "name": "03 - Job Seeker Profile",
        "description": "Candidate profile inspection, professional details update, and avatar upload.",
        "item": [
            make_request(
                name="Get Candidate Profile",
                method="GET",
                path="/api/job-seeker/profile",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns candidate profile', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.email).to.eql(pm.environment.get('candidate_email'));",
                    "});"
                ],
                description="Endpoint: GET /api/job-seeker/profile\nRetrieves profile details of the authenticated job seeker."
            ),
            make_request(
                name="Update Candidate Profile",
                method="PUT",
                path="/api/job-seeker/profile",
                auth_var="candidate_token",
                body_json={
                    "firstName": "Tariku",
                    "lastName": "Mengistu",
                    "bio": "Lead Cloud & Microservices Backend Engineer with 8+ years experience in Java & Spring Boot.",
                    "location": "Addis Ababa, Ethiopia",
                    "phoneNumber": "+251911223344",
                    "skills": "[\"Java\", \"Spring Boot\", \"MySQL\", \"Docker\", \"AWS\", \"Kubernetes\"]",
                    "education": "[{\"degree\": \"B.Sc. Software Engineering\", \"institution\": \"Addis Ababa University\", \"year\": 2018}]",
                    "experience": "[{\"title\": \"Senior Java Developer\", \"company\": \"Tech Solutions\", \"years\": 4}]"
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Candidate profile updated successfully', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.firstName).to.eql('Tariku');",
                    "    pm.expect(res.data.location).to.include('Addis Ababa');",
                    "});"
                ],
                description="Endpoint: PUT /api/job-seeker/profile\nUpdates candidate bio, skills, education, experience, and contact numbers."
            ),
            make_request(
                name="Upload Candidate Avatar",
                method="POST",
                path="/api/job-seeker/profile/photo",
                auth_var="candidate_token",
                form_data=[
                    {
                        "key": "file",
                        "type": "file",
                        "src": "sample_avatar.png"
                    }
                ],
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Profile photo uploaded successfully', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.photoUrl).to.not.be.empty;",
                    "});"
                ],
                description="Endpoint: POST /api/job-seeker/profile/photo\nUploads candidate photo via multipart/form-data with size and extension validation."
            )
        ]
    }

    # =========================================================================
    # 04 - Resumes & CVs
    # =========================================================================
    folder_resumes = {
        "name": "04 - Resumes & CVs",
        "description": "Upload candidate CVs/resumes (PDF/DOCX, max 5MB), list resumes, download binaries, and delete.",
        "item": [
            make_request(
                name="Upload Resume (PDF)",
                method="POST",
                path="/api/resumes",
                auth_var="candidate_token",
                form_data=[
                    {
                        "key": "file",
                        "type": "file",
                        "src": "tariku_resume.pdf"
                    }
                ],
                tests=[
                    "pm.test('Status code is 201 Created', function () {",
                    "    pm.response.to.have.status(201);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Resume uploaded and ID extracted', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.id).to.exist;",
                    "    pm.expect(res.data.fileName).to.not.be.empty;",
                    "    pm.environment.set('candidate_resume_id', res.data.id);",
                    "});"
                ],
                description="Endpoint: POST /api/resumes\nUploads a new resume file. Saved to secure local storage with random UUID file naming."
            ),
            make_request(
                name="List Candidate Resumes",
                method="GET",
                path="/api/resumes",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns list of candidate resumes', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data).to.be.an('array');",
                    "    pm.expect(res.data.length).to.be.above(0);",
                    "});"
                ],
                description="Endpoint: GET /api/resumes\nLists all resumes uploaded by the authenticated candidate."
            ),
            make_request(
                name="Get Resume Metadata",
                method="GET",
                path="/api/resumes/{{candidate_resume_id}}",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns metadata for specified resume', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.id).to.eql(Number(pm.environment.get('candidate_resume_id')));",
                    "});"
                ],
                description="Endpoint: GET /api/resumes/{id}\nRetrieves metadata (name, file size, mime type, uploaded date) for a resume."
            ),
            make_request(
                name="Download Resume Binary File",
                method="GET",
                path="/api/resumes/{{candidate_resume_id}}/download",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "pm.test('Binary file stream received', function () {",
                    "    pm.expect(pm.response.headers.get('Content-Disposition')).to.include('attachment');",
                    "});"
                ],
                description="Endpoint: GET /api/resumes/{id}/download\nStreams the physical resume binary file with attachment header."
            ),
            make_request(
                name="Delete Resume",
                method="DELETE",
                path="/api/resumes/{{candidate_resume_id}}",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Resume deleted successfully', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "});"
                ],
                description="Endpoint: DELETE /api/resumes/{id}\nDeletes resume record and purges physical file from storage."
            )
        ]
    }

    # =========================================================================
    # 05 - Employer Profile & Branding
    # =========================================================================
    folder_employer_profile = {
        "name": "05 - Employer Profile & Branding",
        "description": "Employer company profile, industry classification, website, and branding assets.",
        "item": [
            make_request(
                name="Get Employer Profile",
                method="GET",
                path="/api/employer/profile",
                auth_var="employer_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns employer profile', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.email).to.eql(pm.environment.get('employer_email'));",
                    "});"
                ],
                description="Endpoint: GET /api/employer/profile\nRetrieves employer company profile information."
            ),
            make_request(
                name="Update Employer Profile",
                method="PUT",
                path="/api/employer/profile",
                auth_var="employer_token",
                body_json={
                    "companyName": "Horizon Technologies Inc.",
                    "companyWebsite": "https://horizontech.et",
                    "industry": "Cloud Computing & FinTech",
                    "companyDescription": "Pioneering high-scale cloud platforms and digital payments infrastructure across East Africa.",
                    "location": "Bole Subcity, Addis Ababa"
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Employer profile updated', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.companyName).to.eql('Horizon Technologies Inc.');",
                    "});"
                ],
                description="Endpoint: PUT /api/employer/profile\nUpdates company name, website, industry, description, and headquarters location."
            ),
            make_request(
                name="Upload Company Logo",
                method="POST",
                path="/api/employer/profile/logo",
                auth_var="employer_token",
                form_data=[
                    {
                        "key": "file",
                        "type": "file",
                        "src": "horizon_logo.png"
                    }
                ],
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Company logo uploaded successfully', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.logoUrl).to.not.be.empty;",
                    "});"
                ],
                description="Endpoint: POST /api/employer/profile/logo\nUploads company brand logo via multipart/form-data."
            )
        ]
    }

    # =========================================================================
    # 06 - Employer Job Management
    # =========================================================================
    folder_employer_jobs = {
        "name": "06 - Employer Job Management",
        "description": "Employers posting vacancies, updates, pagination, and deletion. Enforces approval vetting.",
        "item": [
            make_request(
                name="Post New Job Vacancy",
                method="POST",
                path="/api/jobs/employer",
                auth_var="employer_token",
                body_json={
                    "title": "Principal Distributed Systems Engineer",
                    "description": "Architect and deploy high-throughput transactional event processing engines using Spring Boot, Kafka, and MySQL.",
                    "location": "Addis Ababa, Ethiopia",
                    "workplaceType": "HYBRID",
                    "employmentType": "FULL_TIME",
                    "salaryRange": "$80,000 - $120,000",
                    "experienceLevel": "Senior / Lead",
                    "category": "Engineering",
                    "skills": "[\"Java\", \"Spring Cloud\", \"Kafka\", \"MySQL\", \"Docker\"]",
                    "deadline": "2026-12-31T23:59:59"
                },
                tests=[
                    "pm.test('Status code is 201 Created', function () {",
                    "    pm.response.to.have.status(201);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Job created and ID captured', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.id).to.exist;",
                    "    pm.expect(res.data.title).to.include('Distributed Systems');",
                    "    pm.environment.set('employer_job_id', res.data.id);",
                    "});"
                ],
                description="Endpoint: POST /api/jobs/employer\nCreates a new active job vacancy. Requires approved employer status."
            ),
            make_request(
                name="List My Posted Jobs",
                method="GET",
                path="/api/jobs/employer/my-jobs?page=0&size=10",
                auth_var="employer_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns employer posted jobs', function () {",
                    "    pm.expect(res.content).to.be.an('array');",
                    "    pm.expect(res.content.length).to.be.above(0);",
                    "});"
                ],
                description="Endpoint: GET /api/jobs/employer/my-jobs\nRetrieves paginated job listings posted by the authenticated employer."
            ),
            make_request(
                name="Update Job Posting",
                method="PUT",
                path="/api/jobs/employer/{{employer_job_id}}",
                auth_var="employer_token",
                body_json={
                    "title": "Principal Distributed Systems & Cloud Engineer",
                    "description": "Updated vacancy requirements: Architect cloud-native event processing engines using Spring Boot, Kafka, and Kubernetes.",
                    "location": "Addis Ababa, Ethiopia (Hybrid)",
                    "workplaceType": "HYBRID",
                    "employmentType": "FULL_TIME",
                    "salaryRange": "$90,000 - $130,000",
                    "experienceLevel": "Principal",
                    "category": "Engineering",
                    "skills": "[\"Java 21\", \"Spring Boot 3\", \"Kafka\", \"AWS\", \"Kubernetes\"]",
                    "deadline": "2026-12-31T23:59:59"
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Job posting updated successfully', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.title).to.include('Cloud Engineer');",
                    "});"
                ],
                description="Endpoint: PUT /api/jobs/employer/{id}\nUpdates an existing job posting owned by the authenticated employer."
            ),
            make_request(
                name="Delete Job Posting",
                method="DELETE",
                path="/api/jobs/employer/{{employer_job_id}}",
                auth_var="employer_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Job deleted successfully', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "});"
                ],
                description="Endpoint: DELETE /api/jobs/employer/{id}\nRemoves a job posting belonging to the employer."
            )
        ]
    }

    # =========================================================================
    # 07 - Candidate Saved Jobs (Bookmarks)
    # =========================================================================
    folder_saved_jobs = {
        "name": "07 - Saved Jobs (Bookmarks)",
        "description": "Candidate job bookmarking, saved jobs listing, bookmark status check, and bookmark removal.",
        "item": [
            make_request(
                name="Bookmark / Save Job",
                method="POST",
                path="/api/saved-jobs/{{public_job_id}}",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 201 Created', function () {",
                    "    pm.response.to.have.status(201);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Job bookmarked successfully', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.jobId).to.eql(Number(pm.environment.get('public_job_id')));",
                    "});"
                ],
                description="Endpoint: POST /api/saved-jobs/{jobId}\nSaves a job posting to candidate's personal bookmarks."
            ),
            make_request(
                name="Check Bookmark Status (Is Saved)",
                method="GET",
                path="/api/saved-jobs/check/{{public_job_id}}",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Bookmark check returns true', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data).to.be.true;",
                    "});"
                ],
                description="Endpoint: GET /api/saved-jobs/check/{jobId}\nVerifies if a specific job has already been bookmarked by the candidate."
            ),
            make_request(
                name="List Candidate Saved Jobs",
                method="GET",
                path="/api/saved-jobs?page=0&size=10",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns paginated saved jobs', function () {",
                    "    pm.expect(res.content).to.be.an('array');",
                    "    pm.expect(res.content.length).to.be.above(0);",
                    "});"
                ],
                description="Endpoint: GET /api/saved-jobs\nRetrieves paginated list of all jobs saved by the candidate."
            ),
            make_request(
                name="Remove Job from Saved List",
                method="DELETE",
                path="/api/saved-jobs/{{public_job_id}}",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Job bookmark removed', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "});"
                ],
                description="Endpoint: DELETE /api/saved-jobs/{jobId}\nRemoves a bookmarked job from candidate's saved list."
            )
        ]
    }

    # =========================================================================
    # 08 - Candidate Job Applications
    # =========================================================================
    folder_applications = {
        "name": "08 - Job Applications (Candidate)",
        "description": "Candidate application submissions, listing my applications, status checks, and audit history.",
        "item": [
            make_request(
                name="Submit Job Application",
                method="POST",
                path="/api/applications",
                auth_var="candidate_token",
                body_json={
                    "jobId": 1,
                    "resumeId": 1,
                    "coverLetter": "Dear Hiring Team, I am thrilled to submit my application for this role. With extensive backend experience in enterprise Java systems, I am confident in adding immediate value to your team."
                },
                tests=[
                    "pm.test('Status code is 201 Created or 400 Duplicate', function () {",
                    "    pm.expect([201, 400]).to.include(pm.response.code);",
                    "});",
                    "if (pm.response.code === 201) {",
                    "    var res = pm.response.json();",
                    "    pm.test('Application created successfully', function () {",
                    "        pm.expect(res.success).to.be.true;",
                    "        pm.expect(res.data.id).to.exist;",
                    "        pm.expect(res.data.status).to.eql('SUBMITTED');",
                    "        pm.environment.set('candidate_app_id', res.data.id);",
                    "    });",
                    "}"
                ],
                description="Endpoint: POST /api/applications\nSubmits an application with resumeId and cover letter. Prevents duplicate submissions per job."
            ),
            make_request(
                name="List My Submitted Applications",
                method="GET",
                path="/api/applications/my-applications?page=0&size=10",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns candidate applications', function () {",
                    "    pm.expect(res.content).to.be.an('array');",
                    "    if (res.content.length > 0) {",
                    "        pm.environment.set('candidate_app_id', res.content[0].id);",
                    "    }",
                    "});"
                ],
                description="Endpoint: GET /api/applications/my-applications\nRetrieves paginated list of all applications submitted by candidate."
            ),
            make_request(
                name="Get Application Details by ID",
                method="GET",
                path="/api/applications/{{candidate_app_id}}",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns application details', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.id).to.eql(Number(pm.environment.get('candidate_app_id')));",
                    "});"
                ],
                description="Endpoint: GET /api/applications/{id}\nRetrieves detailed application information including status and applied date."
            ),
            make_request(
                name="Get Application Status Audit Trail (History)",
                method="GET",
                path="/api/applications/{{candidate_app_id}}/history",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns chronological audit trail', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data).to.be.an('array');",
                    "    pm.expect(res.data.length).to.be.above(0);",
                    "});"
                ],
                description="Endpoint: GET /api/applications/{id}/history\nReturns complete chronological history of hiring stage transitions with timestamps and notes."
            )
        ]
    }

    # =========================================================================
    # 09 - Employer Application Review
    # =========================================================================
    folder_employer_review = {
        "name": "09 - Employer Application Review",
        "description": "Employers reviewing received applications and progressing hiring status.",
        "item": [
            make_request(
                name="List Received Applications for Employer",
                method="GET",
                path="/api/employer/applications?page=0&size=10",
                auth_var="employer_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns applications received', function () {",
                    "    pm.expect(res.content).to.be.an('array');",
                    "});"
                ],
                description="Endpoint: GET /api/employer/applications\nLists all applications submitted across all jobs owned by this employer."
            ),
            make_request(
                name="Filter Received Applications by Job ID",
                method="GET",
                path="/api/employer/applications?jobId={{employer_job_id}}&page=0&size=10",
                auth_var="employer_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Filtered applications returned', function () {",
                    "    pm.expect(res.content).to.be.an('array');",
                    "});"
                ],
                description="Endpoint: GET /api/employer/applications?jobId={id}\nFilters candidate applications specifically for one vacancy."
            ),
            make_request(
                name="Progress Status to UNDER_REVIEW",
                method="PATCH",
                path="/api/employer/applications/{{candidate_app_id}}/status",
                auth_var="employer_token",
                body_json={
                    "status": "UNDER_REVIEW",
                    "note": "Candidate profile under review by technical hiring manager."
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Application moved to UNDER_REVIEW', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.status).to.eql('UNDER_REVIEW');",
                    "});"
                ],
                description="Endpoint: PATCH /api/employer/applications/{id}/status\nUpdates hiring stage to UNDER_REVIEW and generates notification."
            ),
            make_request(
                name="Progress Status to SHORTLISTED",
                method="PATCH",
                path="/api/employer/applications/{{candidate_app_id}}/status",
                auth_var="employer_token",
                body_json={
                    "status": "SHORTLISTED",
                    "note": "Candidate shortlisted for technical evaluation."
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Application moved to SHORTLISTED', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.status).to.eql('SHORTLISTED');",
                    "});"
                ],
                description="Endpoint: PATCH /api/employer/applications/{id}/status\nUpdates hiring stage to SHORTLISTED."
            ),
            make_request(
                name="Progress Status to INTERVIEW",
                method="PATCH",
                path="/api/employer/applications/{{candidate_app_id}}/status",
                auth_var="employer_token",
                body_json={
                    "status": "INTERVIEW",
                    "note": "Technical interview scheduled via video conference."
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Application moved to INTERVIEW', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.status).to.eql('INTERVIEW');",
                    "});"
                ],
                description="Endpoint: PATCH /api/employer/applications/{id}/status\nUpdates hiring stage to INTERVIEW."
            ),
            make_request(
                name="Progress Status to ACCEPTED",
                method="PATCH",
                path="/api/employer/applications/{{candidate_app_id}}/status",
                auth_var="employer_token",
                body_json={
                    "status": "ACCEPTED",
                    "note": "Formal job offer extended to candidate."
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Application moved to ACCEPTED', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.status).to.eql('ACCEPTED');",
                    "});"
                ],
                description="Endpoint: PATCH /api/employer/applications/{id}/status\nConcludes hiring process with offer acceptance."
            )
        ]
    }

    # =========================================================================
    # 10 - User Notifications
    # =========================================================================
    folder_notifications = {
        "name": "10 - User Notifications",
        "description": "User notification inbox, unread counts, mark single as read, and bulk mark all read.",
        "item": [
            make_request(
                name="Get Candidate Notifications",
                method="GET",
                path="/api/notifications?page=0&size=10",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns candidate notifications', function () {",
                    "    pm.expect(res.content).to.be.an('array');",
                    "    if (res.content.length > 0) {",
                    "        pm.environment.set('candidate_notification_id', res.content[0].id);",
                    "    }",
                    "});"
                ],
                description="Endpoint: GET /api/notifications\nRetrieves paginated notifications ordered by newest first."
            ),
            make_request(
                name="Get Unread Notification Count",
                method="GET",
                path="/api/notifications/unread-count",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns numeric unread count', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data).to.be.a('number');",
                    "});"
                ],
                description="Endpoint: GET /api/notifications/unread-count\nReturns active badge count of unread notifications."
            ),
            make_request(
                name="Mark Single Notification as Read",
                method="PATCH",
                path="/api/notifications/{{candidate_notification_id}}/read",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Notification marked as read', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "});"
                ],
                description="Endpoint: PATCH /api/notifications/{id}/read\nMarks an individual notification as read."
            ),
            make_request(
                name="Mark All Notifications as Read",
                method="PATCH",
                path="/api/notifications/mark-all-read",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('All notifications marked as read', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "});"
                ],
                description="Endpoint: PATCH /api/notifications/mark-all-read\nMarks all unread notifications for the user as read."
            )
        ]
    }

    # =========================================================================
    # 11 - Administrator Platform Governance
    # =========================================================================
    folder_admin = {
        "name": "11 - Administrator Platform Governance",
        "description": "Platform analytics, vetting employers, approvals/rejections, and content moderation.",
        "item": [
            make_request(
                name="Get Platform Statistics & Metrics",
                method="GET",
                path="/api/admin/stats",
                auth_var="admin_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns platform-wide metrics', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.totalUsers).to.be.at.least(1);",
                    "    pm.expect(res.data.totalJobs).to.be.at.least(0);",
                    "    pm.expect(res.data.totalApplications).to.be.at.least(0);",
                    "});"
                ],
                description="Endpoint: GET /api/admin/stats\nRetrieves platform KPIs: user count, employer count, job count, application count, pending count."
            ),
            make_request(
                name="List Pending Employers Awaiting Vetting",
                method="GET",
                path="/api/admin/employers/pending?page=0&size=10",
                auth_var="admin_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns pending employers page', function () {",
                    "    pm.expect(res.content).to.be.an('array');",
                    "});"
                ],
                description="Endpoint: GET /api/admin/employers/pending\nRetrieves paginated list of unapproved employer registrations."
            ),
            make_request(
                name="Approve Employer Registration",
                method="PATCH",
                path="/api/admin/employers/{{employer_user_id}}/approve?approve=true",
                auth_var="admin_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Employer approved by admin', function () {",
                    "    pm.expect(res.success).to.be.true;",
                    "    pm.expect(res.data.isApproved).to.be.true;",
                    "});"
                ],
                description="Endpoint: PATCH /api/admin/employers/{userId}/approve?approve=true\nVets employer registration, unlocking job posting capability."
            ),
            make_request(
                name="List All Jobs Across Platform (Admin)",
                method="GET",
                path="/api/admin/jobs?page=0&size=10",
                auth_var="admin_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Returns admin job inspection list', function () {",
                    "    pm.expect(res.content).to.be.an('array');",
                    "});"
                ],
                description="Endpoint: GET /api/admin/jobs\nLists all jobs in any status (ACTIVE, EXPIRED, CLOSED) for platform compliance."
            )
        ]
    }

    # =========================================================================
    # 12 - Security & RBAC Enforcement
    # =========================================================================
    folder_security = {
        "name": "12 - Security & RBAC Enforcement",
        "description": "Negative testing verifying 401 Unauthorized, 403 Forbidden, 400 Validation, and 409 Conflict.",
        "item": [
            make_request(
                name="SEC-01: 401 Unauthorized - Missing Authorization Header",
                method="GET",
                path="/api/job-seeker/profile",
                tests=[
                    "pm.test('Status code is 401 Unauthorized', function () {",
                    "    pm.response.to.have.status(401);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Unified ErrorResponse matches schema', function () {",
                    "    pm.expect(res.status).to.eql(401);",
                    "    pm.expect(res.error).to.exist;",
                    "    pm.expect(res.path).to.include('/api/job-seeker/profile');",
                    "    pm.expect(res.correlationId).to.exist;",
                    "});"
                ],
                description="Negative Test: Accessing protected endpoint without token returns 401 with standard ErrorResponse."
            ),
            make_request(
                name="SEC-02: 403 Forbidden - Candidate Accessing Admin Dashboard",
                method="GET",
                path="/api/admin/stats",
                auth_var="candidate_token",
                tests=[
                    "pm.test('Status code is 403 Forbidden', function () {",
                    "    pm.response.to.have.status(403);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Access denied to candidate', function () {",
                    "    pm.expect(res.status).to.eql(403);",
                    "    pm.expect(res.error).to.include('FORBIDDEN');",
                    "});"
                ],
                description="Negative Test: Role boundary enforcement. Candidate is forbidden from admin endpoints."
            ),
            make_request(
                name="SEC-03: 403 Forbidden - Candidate Attempting to Post Employer Job",
                method="POST",
                path="/api/jobs/employer",
                auth_var="candidate_token",
                body_json={
                    "title": "Unauthorized Job Post",
                    "description": "Should fail",
                    "location": "Addis Ababa",
                    "workplaceType": "REMOTE",
                    "employmentType": "FULL_TIME"
                },
                tests=[
                    "pm.test('Status code is 403 Forbidden', function () {",
                    "    pm.response.to.have.status(403);",
                    "});"
                ],
                description="Negative Test: Role boundary enforcement. Candidate cannot post employer jobs."
            ),
            make_request(
                name="SEC-04: 403 Forbidden - Employer Accessing Candidate Applications",
                method="GET",
                path="/api/applications/my-applications",
                auth_var="employer_token",
                tests=[
                    "pm.test('Status code is 403 Forbidden', function () {",
                    "    pm.response.to.have.status(403);",
                    "});"
                ],
                description="Negative Test: Role boundary enforcement. Employer cannot access candidate application list."
            ),
            make_request(
                name="SEC-05: 400 Bad Request - Missing Mandatory Registration Fields",
                method="POST",
                path="/api/auth/register",
                body_json={
                    "email": "invalid_email_format",
                    "password": "123"
                },
                tests=[
                    "pm.test('Status code is 400 Bad Request', function () {",
                    "    pm.response.to.have.status(400);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Validation errors returned in details', function () {",
                    "    pm.expect(res.status).to.eql(400);",
                    "    pm.expect(res.error).to.include('VALIDATION_ERROR');",
                    "    pm.expect(res.validationErrors).to.exist;",
                    "});"
                ],
                description="Negative Test: DTO Jakarta validation rejects invalid email, weak password, and missing role."
            ),
            make_request(
                name="SEC-06: 409 Conflict - Duplicate Email Registration",
                method="POST",
                path="/api/auth/register",
                body_json={
                    "name": "Duplicate User",
                    "email": "{{candidate_email}}",
                    "password": "Password123!",
                    "role": "EMPLOYEE"
                },
                tests=[
                    "pm.test('Status code is 409 Conflict', function () {",
                    "    pm.response.to.have.status(409);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('Duplicate email properly detected', function () {",
                    "    pm.expect(res.status).to.eql(409);",
                    "    pm.expect(res.error).to.include('DUPLICATE_RESOURCE');",
                    "});"
                ],
                description="Negative Test: Database uniqueness constraint triggers 409 DuplicateResourceException."
            ),
            make_request(
                name="SEC-07: 404 Not Found - Non-existent Job ID",
                method="GET",
                path="/api/jobs/public/999999",
                tests=[
                    "pm.test('Status code is 404 Not Found', function () {",
                    "    pm.response.to.have.status(404);",
                    "});",
                    "var res = pm.response.json();",
                    "pm.test('ResourceNotFoundException properly formatted', function () {",
                    "    pm.expect(res.status).to.eql(404);",
                    "    pm.expect(res.error).to.include('NOT_FOUND');",
                    "});"
                ],
                description="Negative Test: Non-existent resource returns 404 ResourceNotFoundException."
            )
        ]
    }

    # =========================================================================
    # 13 - Full End-to-End Hiring Lifecycle
    # =========================================================================
    folder_lifecycle = {
        "name": "13 - Full End-to-End Hiring Lifecycle",
        "description": "Complete sequential workflow from admin vetting and job posting to candidate application, status audit trail, and offer acceptance.",
        "item": [
            make_request(
                name="L-01: Admin Logs In & Checks Dashboard",
                method="POST",
                path="/api/auth/login",
                body_json={
                    "email": "{{admin_email}}",
                    "password": "{{admin_password}}"
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "    pm.environment.set('lifecycle_admin_token', pm.response.json().data.accessToken);",
                    "});"
                ],
                description="Lifecycle Step 01: Administrator authenticates and captures lifecycle_admin_token."
            ),
            make_request(
                name="L-02: Register New Company (Employer)",
                method="POST",
                path="/api/auth/register",
                prerequest=[
                    "var s = Date.now().toString().slice(-6);",
                    "pm.environment.set('lifecycle_emp_email', 'apex_tech_' + s + '@horizon.et');"
                ],
                body_json={
                    "name": "Apex Cloud Technologies",
                    "email": "{{lifecycle_emp_email}}",
                    "password": "Password123!",
                    "role": "EMPLOYER"
                },
                tests=[
                    "pm.test('Status code is 201 Created', function () {",
                    "    pm.response.to.have.status(201);",
                    "    var res = pm.response.json();",
                    "    pm.environment.set('lifecycle_emp_user_id', res.data.user.id);",
                    "    pm.expect(res.data.user.isApproved).to.be.false;",
                    "});"
                ],
                description="Lifecycle Step 02: Employer registers. Account created with isApproved=false."
            ),
            make_request(
                name="L-03: Verify Unapproved Employer Cannot Login",
                method="POST",
                path="/api/auth/login",
                body_json={
                    "email": "{{lifecycle_emp_email}}",
                    "password": "Password123!"
                },
                tests=[
                    "pm.test('Status code is 401 Unauthorized', function () {",
                    "    pm.response.to.have.status(401);",
                    "    pm.expect(pm.response.json().message).to.include('approval');",
                    "});"
                ],
                description="Lifecycle Step 03: Proves unapproved employer is blocked from logging in until vetted."
            ),
            make_request(
                name="L-04: Admin Vets & Approves Employer",
                method="PATCH",
                path="/api/admin/employers/{{lifecycle_emp_user_id}}/approve?approve=true",
                auth_var="lifecycle_admin_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "    pm.expect(pm.response.json().data.isApproved).to.be.true;",
                    "});"
                ],
                description="Lifecycle Step 04: Administrator approves employer registration."
            ),
            make_request(
                name="L-05: Approved Employer Logs In",
                method="POST",
                path="/api/auth/login",
                body_json={
                    "email": "{{lifecycle_emp_email}}",
                    "password": "Password123!"
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "    pm.environment.set('lifecycle_emp_token', pm.response.json().data.accessToken);",
                    "});"
                ],
                description="Lifecycle Step 05: Approved employer logs in successfully."
            ),
            make_request(
                name="L-06: Employer Sets Up Profile",
                method="PUT",
                path="/api/employer/profile",
                auth_var="lifecycle_emp_token",
                body_json={
                    "companyName": "Apex Cloud Technologies Ltd.",
                    "companyWebsite": "https://apexcloud.et",
                    "industry": "Cloud Architecture",
                    "companyDescription": "Enterprise cloud migrations and serverless automation."
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});"
                ],
                description="Lifecycle Step 06: Employer completes company profile."
            ),
            make_request(
                name="L-07: Employer Posts New Job Opening",
                method="POST",
                path="/api/jobs/employer",
                auth_var="lifecycle_emp_token",
                body_json={
                    "title": "Lead DevOps & SRE Engineer",
                    "description": "Manage Kubernetes clusters and CI/CD pipelines.",
                    "location": "Addis Ababa",
                    "workplaceType": "REMOTE",
                    "employmentType": "FULL_TIME",
                    "salaryRange": "$70k-$100k",
                    "experienceLevel": "Senior",
                    "category": "DevOps",
                    "skills": "[\"Kubernetes\", \"Terraform\", \"GitHub Actions\"]"
                },
                tests=[
                    "pm.test('Status code is 201 Created', function () {",
                    "    pm.response.to.have.status(201);",
                    "    var res = pm.response.json();",
                    "    pm.environment.set('lifecycle_job_id', res.data.id);",
                    "});"
                ],
                description="Lifecycle Step 07: Employer creates job posting and captures lifecycle_job_id."
            ),
            make_request(
                name="L-08: Candidate Registers & Authenticates",
                method="POST",
                path="/api/auth/register",
                prerequest=[
                    "var s = Date.now().toString().slice(-6);",
                    "pm.environment.set('lifecycle_cand_email', 'sre_dev_' + s + '@ethiopia.et');"
                ],
                body_json={
                    "name": "Dawit Haile",
                    "email": "{{lifecycle_cand_email}}",
                    "password": "Password123!",
                    "role": "EMPLOYEE"
                },
                tests=[
                    "pm.test('Status code is 201 Created', function () {",
                    "    pm.response.to.have.status(201);",
                    "    pm.environment.set('lifecycle_cand_token', pm.response.json().data.accessToken);",
                    "});"
                ],
                description="Lifecycle Step 08: Candidate registers and receives JWT token."
            ),
            make_request(
                name="L-09: Candidate Uploads Resume",
                method="POST",
                path="/api/resumes",
                auth_var="lifecycle_cand_token",
                form_data=[
                    {
                        "key": "file",
                        "type": "file",
                        "src": "dawit_resume.pdf"
                    }
                ],
                tests=[
                    "pm.test('Status code is 201 Created', function () {",
                    "    pm.response.to.have.status(201);",
                    "    pm.environment.set('lifecycle_resume_id', pm.response.json().data.id);",
                    "});"
                ],
                description="Lifecycle Step 09: Candidate uploads PDF resume and captures lifecycle_resume_id."
            ),
            make_request(
                name="L-10: Candidate Bookmarks Vacancy",
                method="POST",
                path="/api/saved-jobs/{{lifecycle_job_id}}",
                auth_var="lifecycle_cand_token",
                tests=[
                    "pm.test('Status code is 201 Created', function () {",
                    "    pm.response.to.have.status(201);",
                    "});"
                ],
                description="Lifecycle Step 10: Candidate bookmarks the job posting."
            ),
            make_request(
                name="L-11: Candidate Submits Job Application",
                method="POST",
                path="/api/applications",
                auth_var="lifecycle_cand_token",
                body_json={
                    "jobId": "{{lifecycle_job_id}}",
                    "resumeId": "{{lifecycle_resume_id}}",
                    "coverLetter": "Excited to bring my SRE experience to Apex Cloud Technologies."
                },
                tests=[
                    "pm.test('Status code is 201 Created', function () {",
                    "    pm.response.to.have.status(201);",
                    "    var res = pm.response.json();",
                    "    pm.expect(res.data.status).to.eql('SUBMITTED');",
                    "    pm.environment.set('lifecycle_app_id', res.data.id);",
                    "});"
                ],
                description="Lifecycle Step 11: Candidate applies. Status initialized to SUBMITTED."
            ),
            make_request(
                name="L-12: Employer Reviews Candidate Application",
                method="PATCH",
                path="/api/employer/applications/{{lifecycle_app_id}}/status",
                auth_var="lifecycle_emp_token",
                body_json={
                    "status": "UNDER_REVIEW",
                    "note": "Resume screened and forwarded to Lead Architect."
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "    pm.expect(pm.response.json().data.status).to.eql('UNDER_REVIEW');",
                    "});"
                ],
                description="Lifecycle Step 12: Employer transitions status to UNDER_REVIEW."
            ),
            make_request(
                name="L-13: Employer Shortlists Candidate",
                method="PATCH",
                path="/api/employer/applications/{{lifecycle_app_id}}/status",
                auth_var="lifecycle_emp_token",
                body_json={
                    "status": "SHORTLISTED",
                    "note": "Candidate shortlisted for panel interview."
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "    pm.expect(pm.response.json().data.status).to.eql('SHORTLISTED');",
                    "});"
                ],
                description="Lifecycle Step 13: Employer transitions status to SHORTLISTED."
            ),
            make_request(
                name="L-14: Employer Accepts Candidate & Extends Offer",
                method="PATCH",
                path="/api/employer/applications/{{lifecycle_app_id}}/status",
                auth_var="lifecycle_emp_token",
                body_json={
                    "status": "ACCEPTED",
                    "note": "Offer accepted! Welcome to the team."
                },
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "    pm.expect(pm.response.json().data.status).to.eql('ACCEPTED');",
                    "});"
                ],
                description="Lifecycle Step 14: Employer transitions status to ACCEPTED."
            ),
            make_request(
                name="L-15: Candidate Verifies Complete 4-Stage History",
                method="GET",
                path="/api/applications/{{lifecycle_app_id}}/history",
                auth_var="lifecycle_cand_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "    var hist = pm.response.json().data;",
                    "    pm.expect(hist.length).to.eql(4);",
                    "    pm.expect(hist[0].status).to.eql('SUBMITTED');",
                    "    pm.expect(hist[3].status).to.eql('ACCEPTED');",
                    "});"
                ],
                description="Lifecycle Step 15: Candidate audits complete 4-stage history (SUBMITTED -> UNDER_REVIEW -> SHORTLISTED -> ACCEPTED)."
            ),
            make_request(
                name="L-16: Candidate Checks Real-Time Notifications",
                method="GET",
                path="/api/notifications",
                auth_var="lifecycle_cand_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "    var notifs = pm.response.json().content;",
                    "    pm.expect(notifs.length).to.be.above(0);",
                    "});"
                ],
                description="Lifecycle Step 16: Candidate confirms status transition notifications were received."
            ),
            make_request(
                name="L-17: Candidate Marks All Notifications Read",
                method="PATCH",
                path="/api/notifications/mark-all-read",
                auth_var="lifecycle_cand_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "});"
                ],
                description="Lifecycle Step 17: Candidate marks all notifications read."
            ),
            make_request(
                name="L-18: Admin Audits Final Platform Stats",
                method="GET",
                path="/api/admin/stats",
                auth_var="lifecycle_admin_token",
                tests=[
                    "pm.test('Status code is 200 OK', function () {",
                    "    pm.response.to.have.status(200);",
                    "    var d = pm.response.json().data;",
                    "    pm.expect(d.totalApplications).to.be.above(0);",
                    "});"
                ],
                description="Lifecycle Step 18: Concludes lifecycle test with platform audit."
            )
        ]
    }

    collection["item"] = [
        folder_health,
        folder_auth,
        folder_jobs,
        folder_candidate_profile,
        folder_resumes,
        folder_employer_profile,
        folder_employer_jobs,
        folder_saved_jobs,
        folder_applications,
        folder_employer_review,
        folder_notifications,
        folder_admin,
        folder_security,
        folder_lifecycle
    ]

    return collection

def build_environment():
    env = {
        "id": "e4f8b92c-6192-4d73-a802-92c4709d1345",
        "name": "Ketari Platform - Local",
        "values": [
            {
                "key": "base_url",
                "value": "http://localhost:8080",
                "type": "default",
                "enabled": True
            },
            {
                "key": "admin_email",
                "value": "admin@ketari.com",
                "type": "default",
                "enabled": True
            },
            {
                "key": "admin_password",
                "value": "Admin@123",
                "type": "secret",
                "enabled": True
            },
            {
                "key": "employer_email",
                "value": "employer@horizon.et",
                "type": "default",
                "enabled": True
            },
            {
                "key": "employer_password",
                "value": "Password123!",
                "type": "secret",
                "enabled": True
            },
            {
                "key": "candidate_email",
                "value": "candidate@ethiopia.et",
                "type": "default",
                "enabled": True
            },
            {
                "key": "candidate_password",
                "value": "Password123!",
                "type": "secret",
                "enabled": True
            },
            {
                "key": "admin_token",
                "value": "",
                "type": "secret",
                "enabled": True
            },
            {
                "key": "employer_token",
                "value": "",
                "type": "secret",
                "enabled": True
            },
            {
                "key": "candidate_token",
                "value": "",
                "type": "secret",
                "enabled": True
            },
            {
                "key": "candidate_refresh_token",
                "value": "",
                "type": "secret",
                "enabled": True
            },
            {
                "key": "admin_user_id",
                "value": "1",
                "type": "default",
                "enabled": True
            },
            {
                "key": "employer_user_id",
                "value": "2",
                "type": "default",
                "enabled": True
            },
            {
                "key": "candidate_user_id",
                "value": "3",
                "type": "default",
                "enabled": True
            },
            {
                "key": "public_job_id",
                "value": "1",
                "type": "default",
                "enabled": True
            },
            {
                "key": "employer_job_id",
                "value": "1",
                "type": "default",
                "enabled": True
            },
            {
                "key": "candidate_resume_id",
                "value": "1",
                "type": "default",
                "enabled": True
            },
            {
                "key": "candidate_app_id",
                "value": "1",
                "type": "default",
                "enabled": True
            },
            {
                "key": "candidate_notification_id",
                "value": "1",
                "type": "default",
                "enabled": True
            }
        ],
        "_postman_variable_scope": "environment",
        "_postman_exported_at": "2026-10-07T14:30:00.000Z",
        "_postman_exported_using": "Postman/11.0.0"
    }
    return env

if __name__ == "__main__":
    out_dir = os.path.dirname(os.path.abspath(__file__))
    
    # 1. Generate Collection
    col = build_collection()
    col_path = os.path.join(out_dir, "Ketari-Job-Board.postman_collection.json")
    with open(col_path, "w", encoding="utf-8") as f:
        json.dump(col, f, indent=2)
    print(f"Postman Collection generated at: {col_path}")
    print(f"Total Folders: {len(col['item'])}")
    total_reqs = sum(len(f['item']) for f in col['item'])
    print(f"Total Requests: {total_reqs}")
    
    # 2. Generate Environment
    env = build_environment()
    env_path = os.path.join(out_dir, "Ketari-Local.postman_environment.json")
    with open(env_path, "w", encoding="utf-8") as f:
        json.dump(env, f, indent=2)
    print(f"Postman Environment generated at: {env_path}")
