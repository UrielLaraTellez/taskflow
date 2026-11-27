package com.taskflow.task;

import com.taskflow.task.dto.CreateTaskRequest;
import com.taskflow.task.dto.TaskResponse;
import com.taskflow.task.dto.UpdateTaskRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponse create(CreateTaskRequest request) {
        Task task = new Task(request.title(), request.description());
        return TaskResponse.from(repository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        return repository.findAll().stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return TaskResponse.from(getTask(id));
    }

    public TaskResponse update(Long id, UpdateTaskRequest request) {
        Task task = getTask(id);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        // No save() needed: the task is managed by JPA inside this transaction,
        // so the changes are flushed to the database on commit (dirty checking).
        return TaskResponse.from(task);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private Task getTask(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
