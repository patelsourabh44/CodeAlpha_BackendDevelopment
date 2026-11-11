# CodeAlpha_JobBoardPlatform

Backend Development Internship - Task 4. Java 17 + Spring Boot 3 + Spring Data JPA + Spring Security (HTTP Basic, BCrypt) + H2 file DB.

## Run
```
mvn spring-boot:run
```
Open http://localhost:8083. Demo accounts (seeded with 3 sample jobs):

| Role | Email | Password |
|---|---|---|
| Candidate | candidate@jobs.com | candidate123 |
| Employer | employer@jobs.com | employer123 |
| Admin | admin@jobs.com | admin123 |

## Features
- Employers: post / edit / close jobs, view applicants, download resumes, update application status.
- Candidates: search jobs (keyword, location, type, minimum salary), upload a resume (PDF/DOC/DOCX, 5 MB), apply, track status.
- Notifications: employer is notified on each new application, candidate on each status change.
- Admin: statistics (users, jobs, applications by status) and user list.

## API
| Method | Path | Role | Description |
|---|---|---|---|
| POST | `/api/auth/register` | public | `{name,email,password,role: EMPLOYER\|CANDIDATE,companyName}` |
| GET | `/api/auth/me` | any | Current user |
| GET | `/api/jobs?keyword=&location=&type=&minSalary=&page=&size=` | public | Search open jobs |
| GET | `/api/jobs/{id}` | public | Job details |
| POST / PUT / DELETE | `/api/jobs[/{id}]` | employer | Create / update / close own job |
| GET | `/api/employer/jobs` | employer | My jobs |
| GET | `/api/jobs/{id}/applications` | employer (owner) | Applicants |
| PATCH | `/api/applications/{id}/status` | employer (owner) | `{"status":"SHORTLISTED"}` (APPLIED, UNDER_REVIEW, SHORTLISTED, REJECTED, HIRED) |
| GET | `/api/applications/{id}/resume` | employer (owner) | Download resume |
| POST | `/api/resume` | candidate | Multipart field `file` |
| POST | `/api/jobs/{id}/apply` | candidate | `{"coverLetter":"..."}` |
| GET | `/api/applications/my` | candidate | My applications |
| GET | `/api/notifications` | any | My notifications |
| POST | `/api/notifications/{id}/read` | any | Mark as read |
| GET | `/api/admin/stats`, `/api/admin/users` | admin | Reports |

Example: `curl -u candidate@jobs.com:candidate123 localhost:8083/api/applications/my`

## Notes
- Job types: FULL_TIME, PART_TIME, INTERNSHIP, CONTRACT.
- Resumes are saved under `./uploads`; database under `./data`.
- Closing a job is a soft delete so existing applications are kept.
