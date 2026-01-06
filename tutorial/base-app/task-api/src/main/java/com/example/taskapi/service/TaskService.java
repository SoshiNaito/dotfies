package com.example.taskapi.service;

import com.example.taskapi.entity.Task;
import com.example.taskapi.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        // 意図的なバグ: completed状態のタスクは更新できない
        // Lesson 3で修正する
        if (task.isCompleted()) {
            throw new RuntimeException("Cannot update completed task");
        }

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setCompleted(taskDetails.isCompleted());

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    // 肥大化したメソッド（Lesson 4でリファクタリング対象）
    public List<Task> searchTasks(String keyword, Boolean completed, String sortBy) {
        List<Task> tasks;

        if (keyword != null && !keyword.isEmpty()) {
            tasks = taskRepository.findByTitleContaining(keyword);
        } else if (completed != null) {
            tasks = taskRepository.findByCompleted(completed);
        } else {
            tasks = taskRepository.findAll();
        }

        // ソート処理（本来は別クラスに分離すべき）
        if (sortBy != null) {
            switch (sortBy) {
                case "title":
                    tasks.sort((a, b) -> a.getTitle().compareTo(b.getTitle()));
                    break;
                case "createdAt":
                    tasks.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
                    break;
                case "completed":
                    tasks.sort((a, b) -> Boolean.compare(a.isCompleted(), b.isCompleted()));
                    break;
            }
        }

        return tasks;
    }
}
