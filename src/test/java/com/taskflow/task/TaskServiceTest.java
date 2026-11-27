package com.taskflow.task;

import com.taskflow.task.dto.CreateTaskRequest;
import com.taskflow.task.dto.TaskResponse;
import com.taskflow.task.dto.UpdateTaskRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @Test
    void createSavesPendingTaskAndReturnsResponse() {
        when(repository.save(any(Task.class))).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 1L);
            return saved;
        });

        TaskResponse response = service.create(new CreateTaskRequest("Buy milk", "2 liters"));

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Buy milk");
        assertThat(captor.getValue().getDescription()).isEqualTo("2 liters");
        assertThat(captor.getValue().getStatus()).isEqualTo(TaskStatus.PENDING);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Buy milk");
        assertThat(response.status()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    void findAllMapsEveryTask() {
        when(repository.findAll()).thenReturn(List.of(
                taskWithId(1L, "First"),
                taskWithId(2L, "Second")));

        List<TaskResponse> responses = service.findAll();

        assertThat(responses).extracting(TaskResponse::title).containsExactly("First", "Second");
    }

    @Test
    void findByIdReturnsTaskWhenItExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(taskWithId(1L, "First")));

        TaskResponse response = service.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("First");
    }

    @Test
    void findByIdThrowsWhenTaskDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateChangesAllFields() {
        Task existing = taskWithId(1L, "Old title");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        TaskResponse response = service.update(1L,
                new UpdateTaskRequest("New title", "New description", TaskStatus.DONE));

        assertThat(existing.getTitle()).isEqualTo("New title");
        assertThat(existing.getDescription()).isEqualTo("New description");
        assertThat(existing.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(response.title()).isEqualTo("New title");
        assertThat(response.status()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void updateThrowsWhenTaskDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L,
                new UpdateTaskRequest("Title", null, TaskStatus.DONE)))
                .isInstanceOf(TaskNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void deleteRemovesExistingTask() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteThrowsWhenTaskDoesNotExist() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(TaskNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }

    private static Task taskWithId(Long id, String title) {
        Task task = new Task(title, null);
        ReflectionTestUtils.setField(task, "id", id);
        return task;
    }
}
