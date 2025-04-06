package com.example.task_manager_app.service;

import com.example.task_manager_app.model.Task;
import com.example.task_manager_app.model.User;
import com.example.task_manager_app.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllTasksByUser(User user) {
        return taskRepository.findByUser(user);
    }

    @Override
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> getTasksByUserAndStatus(User user, String status) {
        return taskRepository.findByUserAndStatus(user, status);
    }

    @Override
    public List<Task> getTasksByUserAndPriority(User user, String priority) {
        return taskRepository.findByUserAndPriority(user, priority);
    }
}
