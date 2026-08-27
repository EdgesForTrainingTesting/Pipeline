package demo;

import demo.model.Task;
import demo.model.TaskPriority;
import demo.model.TaskStatus;
import demo.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Task Management System - Main Application
 * Demonstrates a simple task tracking system with CI/CD pipeline
 */
public class App {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Task Management System v1.0");
        System.out.println("=".repeat(60));

        TaskService taskService = new TaskService();

        // Demo: Create tasks!
        System.out.println("\n📝 Creating tasks...");
        Task task1 = taskService.createTask(
                "Implement user authentication",
                "Add login and registration features.",
                TaskPriority.HIGH
        );
        taskService.setDueDate(task1.getId(), LocalDateTime.now().plusDays(7));
        taskService.assignTask(task1.getId(), "john.doe@company.com");

        Task task2 = taskService.createTask(
                "Write API documentation",
                "Document all REST endpoints.",
                TaskPriority.MEDIUM
        );
        taskService.assignTask(task2.getId(), "jane.smith@company.com");

        Task task3 = taskService.createTask(
                "Fix critical bug in payment module",
                "Users unable to process payments",
                TaskPriority.CRITICAL
        );
        taskService.setDueDate(task3.getId(), LocalDateTime.now().plusHours(4));
        taskService.assignTask(task3.getId(), "john.doe@company.com");
        taskService.updateTaskStatus(task3.getId(), TaskStatus.IN_PROGRESS);

        Task task4 = taskService.createTask(
                "Update project README",
                "Add setup instructions and examples",
                TaskPriority.LOW
        );
        taskService.updateTaskStatus(task4.getId(), TaskStatus.COMPLETED);

        // Display all tasks
        System.out.println("\n📋 All Tasks:");
        System.out.println("-".repeat(60));
        for (Task task : taskService.getAllTasks()) {
            System.out.printf("[%s] %s - %s (Priority: %s)%n",
                    task.getStatus().getDisplayName(),
                    task.getTitle(),
                    task.getAssignedTo() != null ? task.getAssignedTo() : "Unassigned",
                    task.getPriority().getDisplayName()
            );
        }

        // Display statistics
        System.out.println("\n📊 Task Statistics ..:");
        System.out.println("-".repeat(60));
        Map<TaskStatus, Long> stats = taskService.getTaskCountByStatus();
        stats.forEach((status, count) ->
                System.out.printf("%s: %d task(s)%n", status.getDisplayName(), count)
        );

        // Display high priority tasks
        System.out.println("\n🔥 High Priority Tasks:");
        System.out.println("-".repeat(60));
        List<Task> highPriorityTasks = taskService.getTasksByPriority(TaskPriority.HIGH);
        List<Task> criticalTasks = taskService.getTasksByPriority(TaskPriority.CRITICAL);
        highPriorityTasks.addAll(criticalTasks);

        if (highPriorityTasks.isEmpty()) {
            System.out.println("No high priority tasks.");
        } else {
            highPriorityTasks.forEach(task ->
                    System.out.printf("- %s [%s]%n", task.getTitle(), task.getPriority().getDisplayName())
            );
        }

        // Display John's tasks
        System.out.println("\n👤 Tasks assigned to john.doe@company.com:");
        System.out.println("-".repeat(60));
        List<Task> johnsTasks = taskService.getTasksByAssignee("john.doe@company.com");
        johnsTasks.forEach(task ->
                System.out.printf("- %s [%s]%n", task.getTitle(), task.getStatus().getDisplayName())
        );

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Application executed successfully!");
        System.out.println("=".repeat(60));
    }
}