package com.example.task_manager_app.controller;

import com.example.task_manager_app.dto.TaskDTO;
import com.example.task_manager_app.model.Task;
import com.example.task_manager_app.model.User;
import com.example.task_manager_app.service.TaskService;
import com.example.task_manager_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    // Get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username).orElseThrow(() -> 
            new RuntimeException("User not found: " + username));
    }
    
    // Convert Task to TaskDTO
    private TaskDTO convertToDTO(Task task) {
        return new TaskDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getDueDate(),
            task.getPriority(),
            task.getStatus(),
            task.getUser().getId()
        );
    }
    
    // Convert TaskDTO to Task
    private Task convertToEntity(TaskDTO taskDTO) {
        Task task = new Task();
        if (taskDTO.getId() != null) {
            task.setId(taskDTO.getId());
        }
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setPriority(taskDTO.getPriority());
        task.setStatus(taskDTO.getStatus());
        
        // Set the user if userId is provided
        if (taskDTO.getUserId() != null) {
            User user = userService.findById(taskDTO.getUserId()).orElseThrow(() ->
                new RuntimeException("User not found with ID: " + taskDTO.getUserId()));
            task.setUser(user);
        }
        
        return task;
    }

    // Get all tasks for the current user
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskService.getAllTasksByUser(currentUser);
        List<TaskDTO> taskDTOs = tasks.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(taskDTOs);
    }

    // Get task by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Optional<Task> taskOptional = taskService.getTaskById(id);
        
        if (taskOptional.isEmpty() || !taskOptional.get().getUser().getId().equals(currentUser.getId())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Task not found or you don't have access to it");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        TaskDTO taskDTO = convertToDTO(taskOptional.get());
        return ResponseEntity.ok(taskDTO);
    }

    // Create a new task
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskDTO taskDTO) {
        try {
            User currentUser = getCurrentUser();
            Task task = convertToEntity(taskDTO);
            task.setUser(currentUser);
            
            Task createdTask = taskService.createTask(task);
            TaskDTO createdTaskDTO = convertToDTO(createdTask);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskDTO);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to create task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Update an existing task
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        try {
            User currentUser = getCurrentUser();
            Optional<Task> existingTaskOptional = taskService.getTaskById(id);
            
            if (existingTaskOptional.isEmpty() || !existingTaskOptional.get().getUser().getId().equals(currentUser.getId())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Task not found or you don't have access to it");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            Task existingTask = existingTaskOptional.get();
            existingTask.setTitle(taskDTO.getTitle());
            existingTask.setDescription(taskDTO.getDescription());
            existingTask.setDueDate(taskDTO.getDueDate());
            existingTask.setPriority(taskDTO.getPriority());
            existingTask.setStatus(taskDTO.getStatus());
            
            Task updatedTask = taskService.updateTask(existingTask);
            TaskDTO updatedTaskDTO = convertToDTO(updatedTask);
            
            return ResponseEntity.ok(updatedTaskDTO);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            Optional<Task> taskOptional = taskService.getTaskById(id);
            
            if (taskOptional.isEmpty() || !taskOptional.get().getUser().getId().equals(currentUser.getId())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Task not found or you don't have access to it");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            taskService.deleteTask(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Task deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get tasks by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@PathVariable String status) {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskService.getTasksByUserAndStatus(currentUser, status);
        List<TaskDTO> taskDTOs = tasks.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(taskDTOs);
    }

    // Get tasks by priority
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskDTO>> getTasksByPriority(@PathVariable String priority) {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskService.getTasksByUserAndPriority(currentUser, priority);
        List<TaskDTO> taskDTOs = tasks.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(taskDTOs);
    }
}
