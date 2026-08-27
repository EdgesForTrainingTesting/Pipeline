# 📋 Task Management System

A comprehensive task management application built with Java, demonstrating modern CI/CD practices using GitLab pipelines.

## 🎯 Project Overview

This project simulates a real-world software development environment with:
- **Production application** (`app/`) - Core task management system
- **Unit tests** - Business logic validation
- **UI tests** (`ui-tests/`) - Selenium-based end-to-end testing
- **Automated CI/CD pipeline** - Build, test, and quality checks

## 🏗️ Project Structure

```
task-management-system/
├── app/                              # Main application
│   ├── src/
│   │   ├── main/java/demo/
│   │   │   ├── App.java             # Main entry point
│   │   │   ├── model/
│   │   │   │   ├── Task.java        # Task entity
│   │   │   │   ├── TaskStatus.java  # Status enum
│   │   │   │   └── TaskPriority.java # Priority enum
│   │   │   └── service/
│   │   │       └── TaskService.java  # Business logic
│   │   └── test/java/demo/
│   │       └── TaskServiceTest.java  # Unit tests
│   └── pom.xml
│
├── ui-tests/                         # Selenium tests
│   ├── src/
│   │   └── test/java/demo/
│   │       └── TaskManagementUITest.java
│   └── pom.xml
│
├── .gitlab-ci.yml                    # CI/CD pipeline
└── README.md
```

## ✨ Features

### Application Features
- ✅ Create, read, update, delete tasks
- ✅ Task prioritization (Low, Medium, High, Critical)
- ✅ Status tracking (Todo, In Progress, In Review, Completed, Cancelled)
- ✅ Task assignment to team members
- ✅ Due date management with overdue detection
- ✅ Filter and search capabilities
- ✅ Task statistics and reporting

### Testing Features
- ✅ 15 comprehensive unit tests
- ✅ 7 Selenium UI tests
- ✅ Automated test reporting
- ✅ Code coverage tracking

## 🚀 CI/CD Pipeline

The GitLab pipeline consists of 4 stages:

### 1. **Build Stage** 🏗️
- Compiles the Java application
- Creates JAR artifacts
- Runs only when `app/**` changes

### 2. **Unit Test Stage** 🧪
- Executes TestNG unit tests
- Generates JUnit XML reports
- Measures code coverage
- Triggers on `app/**` changes

### 3. **UI Test Stage** 🌐
- Runs Selenium tests in headless Chrome
- Uses Selenium Grid (Docker service)
- Captures screenshots on failures
- Triggers on `app/**` or `ui-tests/**` changes

### 4. **Report Stage** 📊
- Aggregates test results
- Generates pipeline summary

## 🔧 Pipeline Trigger Rules

| Change Location | Triggers                      |
|----------------|-------------------------------|
| `app/src/main/**` | Build → Unit Tests → UI Tests |
| `app/src/test/**` | Build → Unit Tests            |
| `ui-tests/**` | UI Tests only                 |
| `.gitlab-ci.yml` | When Needed                   |

## 🛠️ Local Development

### Prerequisites
- Java 17+
- Maven 3.9+
- Chrome Browser (for local UI tests)

### Build the Application
```bash
cd app
mvn clean package
```

### Run the Application
```bash
java -jar target/task-management-system-1.0.0-jar-with-dependencies.jar
```

### Run Unit Tests
```bash
cd app
mvn test
```

### Run UI Tests Locally
```bash
# Start Selenium Grid
docker run -d -p 4444:4444 --name selenium selenium/standalone-chrome

# Run tests
cd ui-tests
mvn test -Dselenium.remote.url=http://localhost:4444/wd/hub

# Stop Selenium Grid
docker stop selenium && docker rm selenium
```

## 🎓 Demo Scenarios

### Scenario 1: Developer Updates Business Logic
```bash
# Modify app/src/main/java/demo/service/TaskService.java
git add app/
git commit -m "feat: Add task filtering by assignee"
git push
# Pipeline runs: Build → Unit Tests → UI Tests
```

### Scenario 2: Developer Adds New Test
```bash
# Modify app/src/test/java/demo/TaskServiceTest.java
git add app/src/test/
git commit -m "test: Add test for task deletion"
git push
# Pipeline runs: Build → Unit Tests (UI Tests skipped)
```

### Scenario 3: QA Updates UI Tests
```bash
# Modify ui-tests/src/test/java/demo/TaskManagementUITest.java
git add ui-tests/
git commit -m "test: Add test for mobile responsive layout"
git push
# Pipeline runs: UI Tests only
```

## 📊 Viewing Test Results

After pipeline execution:
1. Go to **CI/CD → Pipelines**
2. Click on the pipeline run
3. View **Tests** tab for JUnit results
4. Download artifacts for detailed reports

## 📝 License

This project is for educational purposes as part of a CI/CD training session.

## 👥 Contributors

Created for teaching GitLab CI/CD pipelines and modern software development practices.

---

**Happy Learning! 🚀**