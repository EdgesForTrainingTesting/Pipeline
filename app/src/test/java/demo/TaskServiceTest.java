package demo;

import demo.model.Task;
import demo.model.TaskPriority;
import demo.model.TaskStatus;
import demo.service.TaskService;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Unit tests for TaskService with Allure Reporting
 */
@Epic("Task Management System")
@Feature("Task Service")
public class TaskServiceTest {

    private TaskService taskService;

    @BeforeMethod
    public void setUp() {
        Allure.step("Initialize TaskService.");
        taskService = new TaskService();
    }

    @AfterMethod
    public void tearDown() {
        Allure.step("Clean up test data");
        taskService.clearAllTasks();
    }

    @Test
    @Story("Task Creation")
    @Description("Verify that a new task can be created with title, description, and priority")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateTask() {
        Allure.step("Creating a new task with title 'Test Task'");
        Task task = taskService.createTask(
                "Test Task",
                "Test Description",
                TaskPriority.MEDIUM
        );

        Allure.step("Verifying task properties");
        Assert.assertNotNull(task, "Task should not be null");
        Assert.assertNotNull(task.getId(), "Task ID should be assigned");
        Assert.assertEquals(task.getTitle(), "Test Task", "Task title should match");
        Assert.assertEquals(task.getPriority(), TaskPriority.MEDIUM, "Priority should match");
        Assert.assertEquals(task.getStatus(), TaskStatus.TODO, "Default status should be TODO");

        Allure.addAttachment("Task Details", "text/plain", task.toString());
    }

    @Test
    @Story("Task Retrieval")
    @Description("Verify that a task can be retrieved by its ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetTaskById() {
        Allure.step("Create a task to retrieve");
        Task task = taskService.createTask("Find Me", "Test", TaskPriority.LOW);
        Long taskId = task.getId();

        Allure.step("Retrieve the task by ID: " + taskId);
        Optional<Task> found = taskService.getTaskById(taskId);

        Allure.step("Verify task was found");
        Assert.assertTrue(found.isPresent(), "Task should be found");
        Assert.assertEquals(found.get().getTitle(), "Find Me", "Task title should match");
    }

    @Test
    @Story("Task Retrieval")
    @Description("Verify that retrieving a non-existent task returns empty")
    @Severity(SeverityLevel.NORMAL)
    public void testGetTaskByIdNotFound() {
        Allure.step("Attempt to retrieve non-existent task with ID 999");
        Optional<Task> found = taskService.getTaskById(999L);

        Allure.step("Verify task was not found");
        Assert.assertFalse(found.isPresent(), "Should return empty for non-existent task");
    }

    @Test
    @Story("Task Status Management")
    @Description("Verify that task status can be updated from TODO to IN_PROGRESS")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateTaskStatus() {
        Allure.step("Create a task with TODO status");
        Task task = taskService.createTask("Update Status", "Test", TaskPriority.HIGH);
        Long taskId = task.getId();

        Allure.step("Update task status to IN_PROGRESS");
        boolean updated = taskService.updateTaskStatus(taskId, TaskStatus.IN_PROGRESS);

        Allure.step("Verify status update was successful");
        Assert.assertTrue(updated, "Status update should return true");

        Allure.step("Verify task has new status");
        Optional<Task> found = taskService.getTaskById(taskId);
        Assert.assertTrue(found.isPresent(), "Task should still exist");
        Assert.assertEquals(found.get().getStatus(), TaskStatus.IN_PROGRESS, "Status should be updated");
    }

    @Test
    @Story("Task Assignment")
    @Description("Verify that a task can be assigned to a user")
    @Severity(SeverityLevel.NORMAL)
    public void testAssignTask() {
        Allure.step("Create an unassigned task");
        Task task = taskService.createTask("Assign Me", "Test", TaskPriority.MEDIUM);
        Long taskId = task.getId();

        Allure.step("Assign task to user@example.com");
        boolean assigned = taskService.assignTask(taskId, "user@example.com");

        Allure.step("Verify assignment was successful");
        Assert.assertTrue(assigned, "Assignment should return true");

        Allure.step("Verify task is assigned to correct user");
        Optional<Task> found = taskService.getTaskById(taskId);
        Assert.assertTrue(found.isPresent(), "Task should exist");
        Assert.assertEquals(found.get().getAssignedTo(), "user@example.com", "Task should be assigned to user");
    }

    @Test
    @Story("Task Due Dates")
    @Description("Verify that a due date can be set for a task")
    @Severity(SeverityLevel.NORMAL)
    public void testSetDueDate() {
        Allure.step("Create a task without due date");
        Task task = taskService.createTask("Due Date Task", "Test", TaskPriority.HIGH);
        Long taskId = task.getId();
        LocalDateTime futureDate = LocalDateTime.now().plusDays(5);

        Allure.step("Set due date to 5 days in the future");
        boolean set = taskService.setDueDate(taskId, futureDate);

        Allure.step("Verify due date was set");
        Assert.assertTrue(set, "Setting due date should return true");

        Allure.step("Verify task has correct due date");
        Optional<Task> found = taskService.getTaskById(taskId);
        Assert.assertTrue(found.isPresent(), "Task should exist");
        Assert.assertEquals(found.get().getDueDate(), futureDate, "Due date should match");
    }

    @Test
    @Story("Task Deletion")
    @Description("Verify that a task can be deleted successfully")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteTask() {
        Allure.step("Create a task to delete");
        Task task = taskService.createTask("Delete Me", "Test", TaskPriority.LOW);
        Long taskId = task.getId();

        Allure.step("Delete the task");
        boolean deleted = taskService.deleteTask(taskId);

        Allure.step("Verify deletion was successful");
        Assert.assertTrue(deleted, "Deletion should return true");

        Allure.step("Verify task no longer exists");
        Optional<Task> found = taskService.getTaskById(taskId);
        Assert.assertFalse(found.isPresent(), "Task should no longer exist");
    }

    @Test
    @Story("Task Listing")
    @Description("Verify that all tasks can be retrieved")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllTasks() {
        Allure.step("Create 3 tasks");
        taskService.createTask("Task 1", "Test", TaskPriority.LOW);
        taskService.createTask("Task 2", "Test", TaskPriority.MEDIUM);
        taskService.createTask("Task 3", "Test", TaskPriority.HIGH);

        Allure.step("Retrieve all tasks");
        List<Task> tasks = taskService.getAllTasks();

        Allure.step("Verify task count");
        Assert.assertEquals(tasks.size(), 3, "Should have 3 tasks");
    }

    @Test
    @Story("Task Filtering")
    @Description("Verify that tasks can be filtered by status")
    @Severity(SeverityLevel.NORMAL)
    public void testGetTasksByStatus() {
        Allure.step("Create tasks with different statuses");
        Task task1 = taskService.createTask("Task 1", "Test", TaskPriority.LOW);
        Task task2 = taskService.createTask("Task 2", "Test", TaskPriority.MEDIUM);
        taskService.updateTaskStatus(task1.getId(), TaskStatus.COMPLETED);

        Allure.step("Filter tasks by COMPLETED status");
        List<Task> completedTasks = taskService.getTasksByStatus(TaskStatus.COMPLETED);

        Allure.step("Filter tasks by TODO status");
        List<Task> todoTasks = taskService.getTasksByStatus(TaskStatus.TODO);

        Allure.step("Verify filtered results");
        Assert.assertEquals(completedTasks.size(), 1, "Should have 1 completed task");
        Assert.assertEquals(todoTasks.size(), 1, "Should have 1 TODO task");
    }

    @Test
    @Story("Task Filtering")
    @Description("Verify that tasks can be filtered by priority level")
    @Severity(SeverityLevel.NORMAL)
    public void testGetTasksByPriority() {
        Allure.step("Create tasks with different priorities");
        taskService.createTask("Low Priority", "Test", TaskPriority.LOW);
        taskService.createTask("High Priority 1", "Test", TaskPriority.HIGH);
        taskService.createTask("High Priority 2", "Test", TaskPriority.HIGH);

        Allure.step("Filter tasks by HIGH priority");
        List<Task> highPriorityTasks = taskService.getTasksByPriority(TaskPriority.HIGH);

        Allure.step("Verify filtered results");
        Assert.assertEquals(highPriorityTasks.size(), 2, "Should have 2 high priority tasks");
    }

    @Test
    @Story("Task Assignment")
    @Description("Verify that tasks can be filtered by assignee")
    @Severity(SeverityLevel.NORMAL)
    public void testGetTasksByAssignee() {
        Allure.step("Create and assign tasks to different users");
        Task task1 = taskService.createTask("Task 1", "Test", TaskPriority.LOW);
        Task task2 = taskService.createTask("Task 2", "Test", TaskPriority.MEDIUM);
        Task task3 = taskService.createTask("Task 3", "Test", TaskPriority.HIGH);

        taskService.assignTask(task1.getId(), "john@example.com");
        taskService.assignTask(task2.getId(), "john@example.com");
        taskService.assignTask(task3.getId(), "jane@example.com");

        Allure.step("Filter tasks assigned to john@example.com");
        List<Task> johnsTasks = taskService.getTasksByAssignee("john@example.com");

        Allure.step("Verify filtered results");
        Assert.assertEquals(johnsTasks.size(), 2, "John should have 2 assigned tasks");
    }

    @Test
    @Story("Task Statistics")
    @Description("Verify that task counts can be grouped by status")
    @Severity(SeverityLevel.NORMAL)
    public void testGetTaskCountByStatus() {
        Allure.step("Create tasks with various statuses");
        Task task1 = taskService.createTask("Task 1", "Test", TaskPriority.LOW);
        Task task2 = taskService.createTask("Task 2", "Test", TaskPriority.MEDIUM);
        Task task3 = taskService.createTask("Task 3", "Test", TaskPriority.HIGH);

        taskService.updateTaskStatus(task1.getId(), TaskStatus.COMPLETED);
        taskService.updateTaskStatus(task2.getId(), TaskStatus.IN_PROGRESS);

        Allure.step("Get task counts grouped by status");
        Map<TaskStatus, Long> stats = taskService.getTaskCountByStatus();

        Allure.step("Verify status counts");
        Assert.assertEquals(stats.get(TaskStatus.COMPLETED), Long.valueOf(1), "Should have 1 completed");
        Assert.assertEquals(stats.get(TaskStatus.IN_PROGRESS), Long.valueOf(1), "Should have 1 in progress");
        Assert.assertEquals(stats.get(TaskStatus.TODO), Long.valueOf(1), "Should have 1 TODO");

        Allure.addAttachment("Task Statistics", "application/json", stats.toString());
    }

    @Test
    @Story("Task Statistics")
    @Description("Verify that total task count is accurate")
    @Severity(SeverityLevel.MINOR)
    public void testGetTotalTaskCount() {
        Allure.step("Create 2 tasks");
        taskService.createTask("Task 1", "Test", TaskPriority.LOW);
        taskService.createTask("Task 2", "Test", TaskPriority.MEDIUM);

        Allure.step("Get total task count");
        int count = taskService.getTotalTaskCount();

        Allure.step("Verify count is correct");
        Assert.assertEquals(count, 2, "Total task count should be 2");
    }

    @Test
    @Story("Task Validation")
    @Description("Verify that task validation prevents empty titles")
    @Severity(SeverityLevel.CRITICAL)
    public void testTaskValidation() {
        Allure.step("Create a valid task");
        Task task = taskService.createTask("Valid Task", "Test", TaskPriority.MEDIUM);

        Allure.step("Attempt to set empty title (should throw exception)");
        try {
            task.setTitle("");
            Assert.fail("Should throw IllegalArgumentException for empty title");
        } catch (IllegalArgumentException e) {
            Allure.step("Exception thrown as expected: " + e.getMessage());
            Assert.assertTrue(e.getMessage().contains("cannot be empty"), "Error message should mention empty");
        }
    }

    @Test
    @Story("Task Due Dates")
    @Description("Verify that past due dates are rejected")
    @Severity(SeverityLevel.NORMAL)
    public void testDueDateValidation() {
        Allure.step("Create a task");
        Task task = taskService.createTask("Due Date Test", "Test", TaskPriority.HIGH);

        Allure.step("Attempt to set due date in the past (should throw exception)");
        try {
            task.setDueDate(LocalDateTime.now().minusDays(1));
            Assert.fail("Should throw IllegalArgumentException for past due date");
        } catch (IllegalArgumentException e) {
            Allure.step("Exception thrown as expected: " + e.getMessage());
            Assert.assertTrue(e.getMessage().contains("cannot be in the past"),
                    "Error message should mention past date");
        }
    }
}