package com.example.task_manager_app.repository;

import com.example.task_manager_app.model.Task;
import com.example.task_manager_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByUserAndStatus(User user, String status);
    List<Task> findByUserAndPriority(User user, String priority);
}
