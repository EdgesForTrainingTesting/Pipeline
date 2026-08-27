package demo.service;

import demo.model.Task;
import demo.model.TaskPriority;
import demo.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing tasks
 */
public class TaskService {
    private final Map<Long, Task> taskRepository = new HashMap<>();
    private Long nextId = 1L;

    /**
     * Creates a new task
     */
    public Task createTask(String title, String description, TaskPriority priority) {
        Task task = new Task(title, description, priority);
        task.setId(nextId++);
        taskRepository.put(task.getId(), task);
        return task;
    }

    /**
     * Retrieves a task by ID
     */
    public Optional<Task> getTaskById(Long id) {
        return Optional.ofNullable(taskRepository.get(id));
    }

    /**
     * Gets all tasks
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskRepository.values());
    }

    /**
     * Updates task status
     */
    public boolean updateTaskStatus(Long id, TaskStatus newStatus) {
        Task task = taskRepository.get(id);
        if (task == null) {
            return false;
        }
        task.setStatus(newStatus);
        return true;
    }

    /**
     * Assigns a task to a user
     */
    public boolean assignTask(Long id, String assignee) {
        Task task = taskRepository.get(id);
        if (task == null) {
            return false;
        }
        task.setAssignedTo(assignee);
        return true;
    }

    /**
     * Sets due date for a task
     */
    public boolean setDueDate(Long id, LocalDateTime dueDate) {
        Task task = taskRepository.get(id);
        if (task == null) {
            return false;
        }
        task.setDueDate(dueDate);
        return true;
    }

    /**
     * Deletes a task
     */
    public boolean deleteTask(Long id) {
        return taskRepository.remove(id) != null;
    }

    /**
     * Gets tasks by status
     */
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.values().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Gets tasks by priority
     */
    public List<Task> getTasksByPriority(TaskPriority priority) {
        return taskRepository.values().stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    /**
     * Gets tasks assigned to a specific user
     */
    public List<Task> getTasksByAssignee(String assignee) {
        return taskRepository.values().stream()
                .filter(task -> assignee.equals(task.getAssignedTo()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all overdue tasks
     */
    public List<Task> getOverdueTasks() {
        return taskRepository.values().stream()
                .filter(Task::isOverdue)
                .collect(Collectors.toList());
    }

    /**
     * Gets task count by status
     */
    public Map<TaskStatus, Long> getTaskCountByStatus() {
        return taskRepository.values().stream()
                .collect(Collectors.groupingBy(
                        Task::getStatus,
                        Collectors.counting()
                ));
    }

    /**
     * Clears all tasks (for testing)
     */
    public void clearAllTasks() {
        taskRepository.clear();
        nextId = 1L;
    }

    /**
     * Gets total task count
     */
    public int getTotalTaskCount() {
        return taskRepository.size();
    }
}