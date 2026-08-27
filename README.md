# 📋 Task Management System — GitHub Actions CI/CD Demo

A Java-based task management demo project created for teaching and demonstrating a complete CI/testing workflow using **GitHub, Pull Requests, GitHub Actions, Maven, TestNG, Selenium, and Allure**.

> The primary purpose of this repository is CI/CD and test-automation training.  
> The Java business logic and the static HTML UI are separate demo components; the current UI is not connected to the Java `TaskService` through an API or database.

---

## 🎯 Project Goal

The project demonstrates this development flow:

```text
Developer / QA Engineer
        ↓
Create Feature Branch
        ↓
Modify Application or Tests
        ↓
Commit + Push
        ↓
Create / Update Pull Request to main
        ↓
GitHub Actions
        ↓
Detect Changed Files
        ↓
Build Application
        ↓
Unit Tests
        ↓
Selenium UI Tests
        ↓
Allure Report
        ↓
Pipeline Summary
        ↓
✅ Merge when required checks pass
❌ Fix code/tests and push again when they fail
```

---

## 🏗️ Project Structure

```text
EntryPipeLine/
│
├── .github/
│   └── workflows/
│       └── ci.yml                     # GitHub Actions CI workflow
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/demo/
│   │   │   │   ├── App.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── Task.java
│   │   │   │   │   ├── TaskStatus.java
│   │   │   │   │   └── TaskPriority.java
│   │   │   │   └── service/
│   │   │   │       └── TaskService.java
│   │   │   │
│   │   │   └── resources/static/
│   │   │       └── index.html         # Static demo UI
│   │   │
│   │   └── test/java/demo/
│   │       └── TaskServiceTest.java   # TestNG unit tests
│   │
│   ├── pom.xml
│   └── testng.xml
│
├── ui-tests/
│   ├── src/
│   │   ├── main/java/utils/
│   │   │   └── AllureUtil.java
│   │   └── test/java/demo/
│   │       └── TaskManagementUITest.java
│   ├── pom.xml
│   └── testng.xml
│
├── .gitignore
└── README.md
```

---

## 🧩 Main Components

### 1. Java Application

The `app/` module contains the task-management business logic.

It supports operations such as:

- Creating tasks
- Getting tasks by ID
- Updating task status
- Assigning tasks
- Setting due dates
- Deleting tasks
- Filtering by status
- Filtering by priority
- Filtering by assignee
- Detecting overdue tasks
- Calculating task statistics

The current repository stores tasks **in memory using a `HashMap`**. There is currently no external database.

### 2. Static Web UI

The UI is located at:

```text
app/src/main/resources/static/index.html
```

It is a static demonstration page used by the Selenium test project.

The GitHub Actions workflow serves it using:

```bash
python3 -m http.server 8080
```

and the Selenium tests access:

```text
http://127.0.0.1:8080/index.html
```

The current static UI is **not connected to `TaskService` through REST APIs**.

### 3. Unit Tests

The application contains TestNG unit tests for `TaskService`.

Run them with:

```bash
cd app
mvn test
```

Results are generated under:

```text
app/target/surefire-reports/
app/target/allure-results/
```

### 4. UI Automation Tests

The `ui-tests/` module contains Selenium + TestNG automated UI tests.

The framework supports:

- Local ChromeDriver
- Remote Selenium URL when configured
- Headless Chrome
- Allure reporting
- Screenshots / attachments

---

# 🚀 GitHub Actions CI Pipeline

The GitHub Actions workflow is located at:

```text
.github/workflows/ci.yml
```

It is triggered by a **Pull Request targeting `main`** when relevant application, UI-test, or workflow files are changed.

```text
Pull Request
      ↓
Detect Changes
      ↓
Build Application
      ↓
Unit Tests
      ↓
UI Tests
      ↓
Generate Allure Report
      ↓
Pipeline Summary
```

---

## 1️⃣ Detect Changes

The first job determines which area of the repository changed.

This allows the workflow to avoid running unnecessary jobs.

Typical behavior:

| Changed Files | Build | Unit Tests | UI Tests |
|---|---:|---:|---:|
| `app/**` | ✅ | ✅ | ✅ |
| `ui-tests/**` only | ⏭ | ⏭ | ✅ |
| `.github/workflows/**` | ✅ | ✅ | ✅ |

---

## 2️⃣ Build Application

When the application changes, GitHub Actions runs Maven with Java 17.

```bash
cd app
mvn -B clean compile -DskipTests
mvn -B package -DskipTests
```

The build creates an executable JAR similar to:

```text
app/target/task-management-system-1.0.0-jar-with-dependencies.jar
```

The JAR is uploaded as a GitHub Actions artifact.

---

## 3️⃣ Unit Tests

After a successful application build:

```bash
cd app
mvn -B test
```

The job produces:

```text
app/target/surefire-reports/
app/target/allure-results/
```

The test results are uploaded even when tests fail, allowing failures to be investigated from the workflow artifacts.

---

## 4️⃣ Selenium UI Tests

The workflow starts the static UI:

```bash
cd app/src/main/resources/static
python3 -m http.server 8080
```

It waits until this URL responds:

```text
http://127.0.0.1:8080/index.html
```

Then it runs:

```bash
cd ui-tests
mvn -B test
```

The Selenium tests run using headless Chrome on the GitHub-hosted runner.

The UI test job uploads:

```text
ui-tests/target/surefire-reports/
ui-tests/target/allure-results/
ui-tests/target/screenshots/
```

---

## 5️⃣ Allure Report

The reporting job downloads the Unit Test and UI Test artifacts and combines their Allure result files.

```text
Unit Test Allure Results
          +
UI Test Allure Results
          ↓
Combined Allure Results
          ↓
Allure HTML Report
```

The generated HTML report is uploaded as an artifact named:

```text
allure-report
```

Reporting is treated as a secondary concern; a report-generation problem should not replace the result of the actual build and test jobs.

---

## 6️⃣ Pipeline Summary

The final job writes a GitHub Actions summary showing the result of each CI stage.

Example:

| Stage | Result |
|---|---|
| Detect Changes | success |
| Build Application | success |
| Unit Tests | success |
| UI Tests | success |
| Allure Report | success |

---

# 🛠️ Local Setup

## Prerequisites

Install:

- Java 17+
- Maven 3.9+
- Python 3
- Google Chrome
- ChromeDriver if Selenium Manager does not resolve it automatically

Check Java:

```bash
java -version
```

Check Maven:

```bash
mvn -version
```

---

# ▶️ Build the Java Application

```bash
cd app
mvn clean package
```

The executable JAR is generated under:

```text
app/target/
```

Run it with:

```bash
java -jar target/task-management-system-1.0.0-jar-with-dependencies.jar
```

The Java application runs as a console demo and prints sample tasks and statistics.

---

# 🧪 Run Unit Tests Locally

From the repository root:

```bash
cd app
mvn clean test
```

Surefire results:

```text
app/target/surefire-reports/
```

Allure raw results:

```text
app/target/allure-results/
```

---

# 🌐 Run the Static UI Locally

Open a terminal from the repository root:

```bash
cd app/src/main/resources/static
python -m http.server 8080
```

If your system uses `python3`:

```bash
python3 -m http.server 8080
```

Open:

```text
http://localhost:8080/index.html
```

Keep this terminal running while executing local UI tests.

---

# 🤖 Run Selenium UI Tests Locally

Open another terminal:

```bash
cd ui-tests
mvn clean test
```

By default, the tests use:

```text
http://localhost:8080/index.html
```

You can override the application URL with:

```bash
mvn test -Dapp.baseUrl=http://localhost:8080/index.html
```

or by setting:

```text
APP_BASE_URL
```

The test framework can also use a remote Selenium server through:

```text
SELENIUM_REMOTE_URL
```

---

# 📊 Generate Allure Report Locally

After running tests, the raw Allure files are stored under each module's `target/allure-results` folder.

For the application module you can use:

```bash
cd app
mvn allure:serve
```

or:

```bash
mvn allure:report
```

The GitHub Actions workflow creates a combined report from both Unit and UI results automatically.

---

# 🔀 Recommended GitHub Development Flow

Do not work directly on `main`.

Start from an updated `main` branch:

```bash
git checkout main
git pull
```

Create a feature branch:

```bash
git checkout -b feature/my-change
```

Make your change, then:

```bash
git add .
git commit -m "Update application"
git push -u origin feature/my-change
```

On GitHub create:

```text
feature/my-change
        ↓
Pull Request
        ↓
main
```

Creating or updating the Pull Request triggers the CI workflow when relevant files changed.

---

# 🎓 Demo Scenarios

## Scenario 1 — Developer Changes Application Code

Modify:

```text
app/src/main/java/
```

Then commit and push.

Expected Pull Request flow:

```text
Detect Changes
      ↓
Build Application
      ↓
Unit Tests
      ↓
UI Tests
      ↓
Allure Report
```

---

## Scenario 2 — QA Changes Selenium Tests Only

Modify:

```text
ui-tests/
```

Expected flow:

```text
Detect Changes
      ↓
Build Application      ⏭ Skipped
Unit Tests             ⏭ Skipped
UI Tests               ✅ Runs
Allure Report           ✅ Runs
```

---

## Scenario 3 — Intentional Build Failure

Introduce a Java compilation error on a training branch.

Example:

```java
System.out.println("Pipeline Demo")
```

Missing the semicolon causes the Build job to fail.

Expected flow:

```text
Build Application ❌
      ↓
Application-dependent tests do not proceed
      ↓
Fix the code
      ↓
Commit + Push
      ↓
Pull Request workflow runs again
```

---

## Scenario 4 — Intentional Test Failure

Change an assertion so that a TestNG test fails.

Expected behavior:

```text
Build ✅
Unit Test or UI Test ❌
      ↓
Pull Request check becomes red
      ↓
Fix the test/code
      ↓
Push again
      ↓
GitHub Actions reruns automatically
```

---

# 📦 GitHub Actions Artifacts

After a workflow run, open:

```text
Repository
→ Actions
→ Task Management CI
→ Select Workflow Run
→ Artifacts
```

Depending on the changes and results, you can find artifacts such as:

```text
application-jar
unit-test-results
ui-test-results
allure-report
```

---

# 🔐 Protect the `main` Branch

For a training repository, configure a GitHub branch ruleset for `main`.

Recommended rules:

- Require a Pull Request before merging
- Require status checks to pass before merging
- Require the important CI checks such as:
  - Build Application
  - Unit Tests
  - UI Tests

This creates the teaching flow:

```text
Pipeline Green ✅
      ↓
Merge Allowed

Pipeline Red ❌
      ↓
Fix Required
```

---

# 🧹 Recommended `.gitignore`

Generated Maven and Allure files should not be committed to the repository.

Example:

```gitignore
# Maven
**/target/

# Allure
**/allure-results/
allure-report/

# IntelliJ
.idea/
*.iml

# Logs
*.log

# OS
.DS_Store
Thumbs.db
```

If generated files were already tracked before adding `.gitignore`, remove them from the Git index once:

```bash
git rm -r --cached app/target
git rm -r --cached app/allure-results
```

Commit the cleanup afterward.

---

# ⚠️ Current Demo Limitations

This repository is intentionally lightweight for CI/CD training.

The current version does **not** contain:

- A REST API connecting the UI to `TaskService`
- A database
- Persistent task storage
- A production authentication system
- A real full-stack UI/backend integration
- True end-to-end CRUD through the web UI
- JaCoCo code-coverage configuration

The Java business logic is tested independently by the Unit Tests, while Selenium tests target the static demo HTML page.

---

# 📝 License / Usage

This project is intended for educational and CI/CD training purposes.

---

## 🚀 CI/CD Technology Stack

```text
GitHub
Pull Requests
GitHub Actions
Java 17
Maven
TestNG
Selenium WebDriver
Headless Chrome
Allure
JUnit/Surefire Reports
GitHub Actions Artifacts
```

**Happy Learning! 🚀**
