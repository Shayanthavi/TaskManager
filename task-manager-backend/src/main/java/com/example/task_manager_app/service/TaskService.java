package com.example.task_manager_app.service;

import com.example.task_manager_app.model.Task;
import com.example.task_manager_app.model.User;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<Task> getAllTasksByUser(User user);
    Optional<Task> getTaskById(Long id);
    Task createTask(Task task);
    Task updateTask(Task task);
    void deleteTask(Long id);
    List<Task> getTasksByUserAndStatus(User user, String status);
    List<Task> getTasksByUserAndPriority(User user, String priority);
}
