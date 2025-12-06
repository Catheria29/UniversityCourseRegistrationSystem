# University Enrollment System

## Overview

This project implements a university course registration system with roles for **Students**, **Instructors**, and **Admins**. It supports course/section creation, enrollment with validation, GPA computation, grading, and administrative overrides. 

The system can run both in **console mode** and a **Swing-based GUI**.

### Key Features

- **Student**
  - View course catalog
  - View personal schedule
  - Enroll in / drop sections with prerequisite, schedule, and capacity validation
  - View transcript and GPA
- **Instructor**
  - List assigned sections
  - View roster of a section
  - Post grades
- **Admin**
  - Create courses and sections
  - Assign instructors
  - Override capacity and prerequisites
  - View admin action logs

### Design Highlights

- **Repositories:** Generic `InMemoryRepository` with save, find, delete functionality.
- **Validators:** `CapacityValidator`, `PrerequisiteValidator`, `ScheduleConflictChecker`.
- **Services:** `RegistrationService`, `GradingService`, `CatalogService`, `AdminService`.
- **Model:** `Student`, `Instructor`, `Admin`, `Course`, `Section`, `Enrollment`, `TranscriptEntry`, `TimeSlot`.
- **UI:** Swing-based dashboards for all roles.
- **Data Persistence:** JSON storage per repository (optional).

### Deviations from Spec

- Admin and student actions are logged for audit.
- GPA computation excludes non-graded or non-countable courses.
- Swing GUI allows going back to main menu without closing the app.
- For simplicity, no waitlist implementation; enrollment fails when capacity is full unless overridden.

---

## Build and Run

### Prerequisites

- Java 21
- Maven 3.x

### Compile and Package

```bash
# Navigate to project root
mvn clean package
````

### Run GUI Application

```bash
java -jar target/standalone-app-1.0-SNAPSHOT.jar
```

GUI will prompt role selection at startup.

---

## Demo Script

This script demonstrates **successful enrollment** and **handled failures**, including admin overrides.

### 1. Successful Enrollment

```text
> Role: Student
> Student ID: S1
> Action: Add Section
> Section ID: CS101-Fall2025
→ Output: "Enrolled successfully!"
```

**Expected Result:** Student enrolled in CS101 successfully.

---

### 2. Enrollment Failure: Missing Prerequisite

```text
> Role: Student
> Student ID: S1
> Action: Add Section
> Section ID: CS102-Fall2025
→ Output: "Error: Missing prerequisite: CS101"
```

**Expected Result:** Enrollment fails because CS102 requires CS101, which the student hasn't completed.

---

### 3. Enrollment Failure: Schedule Conflict

```text
> Role: Student
> Student ID: S1
> Action: Add Section
> Section ID: CS103-Fall2025
→ Output: "Error: Schedule conflict detected with existing enrollment."
```

**Expected Result:** Enrollment fails due to overlapping section times.

---

### 4. Admin Overrides Prerequisite and Capacity

```text
> Role: Admin
> Admin ID: A1
> Action: Override Prerequisite
> Student ID: S1
> Course: CS102
> Reason: "Admin approval"
→ Output: "Success!"

> Action: Override Capacity
> Section ID: CS103-Fall2025
> New Capacity: 50
> Reason: "Admin approval for extra seat"
→ Output: "Success!"
```

**Expected Result:** Student can now enroll in blocked sections.

---

### 5. Enrollment After Admin Override (Success)

```text
> Role: Student
> Student ID: S1
> Action: Add Section
> Section ID: CS102-Fall2025
→ Output: "Enrolled successfully!"

> Action: Add Section
> Section ID: CS103-Fall2025
→ Output: "Enrolled successfully!"
```

**Expected Result:** Student successfully enrolled in both sections that were previously blocked.

---

## Notes

* **Validators** ensure enrollment rules are enforced.
* **AdminService** logs all override actions for traceability.
* **Repositories** support save, find, delete operations; JSON persistence optional.
* **Swing UI** provides easy navigation between menus without restarting.

```
