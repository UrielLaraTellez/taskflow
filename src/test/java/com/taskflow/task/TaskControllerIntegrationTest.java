package com.taskflow.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private TaskRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    // --- POST /api/tasks ---

    @Test
    void createReturns201WithLocationAndPendingTask() {
        MvcTestResult result = post("""
                {"title": "Buy milk", "description": "2 liters"}
                """);

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result.getResponse().getHeader("Location")).matches(".*/api/tasks/\\d+");
        assertThat(result).bodyJson().isLenientlyEqualTo("""
                {"title": "Buy milk", "description": "2 liters", "status": "PENDING"}
                """);
        assertThat(result).bodyJson().extractingPath("$.id").isNotNull();
        assertThat(result).bodyJson().extractingPath("$.createdAt").isNotNull();
    }

    @Test
    void createWithBlankTitleReturns400WithFieldError() {
        MvcTestResult result = post("""
                {"title": "   "}
                """);

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.errors.title").isNotNull();
    }

    @Test
    void createWithTooLongTitleReturns400WithFieldError() {
        MvcTestResult result = post("""
                {"title": "%s"}
                """.formatted("a".repeat(101)));

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.errors.title").isNotNull();
    }

    @Test
    void createWithMalformedJsonReturns400() {
        MvcTestResult result = post("{\"title\": ");

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    // --- GET /api/tasks and /api/tasks/{id} ---

    @Test
    void findAllReturnsEveryTask() {
        createTask("First");
        createTask("Second");

        assertThat(mvc.get().uri("/api/tasks"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.length()").isEqualTo(2);
    }

    @Test
    void findByIdReturnsTask() {
        long id = createTask("First");

        assertThat(mvc.get().uri("/api/tasks/{id}", id))
                .hasStatusOk()
                .bodyJson().isLenientlyEqualTo("""
                        {"id": %d, "title": "First", "status": "PENDING"}
                        """.formatted(id));
    }

    @Test
    void findByUnknownIdReturns404ProblemDetail() {
        MvcTestResult result = mvc.get().uri("/api/tasks/{id}", 99).exchange();

        assertThat(result)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(result).bodyJson().extractingPath("$.status").isEqualTo(404);
        assertThat(result).bodyJson().extractingPath("$.detail").isEqualTo("Task with id 99 not found");
    }

    @Test
    void findByNonNumericIdReturns400() {
        assertThat(mvc.get().uri("/api/tasks/abc"))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    // --- PUT /api/tasks/{id} ---

    @Test
    void updateChangesTaskAndPersistsIt() {
        long id = createTask("Old title");

        assertThat(put(id, """
                {"title": "New title", "description": "Now with details", "status": "DONE"}
                """))
                .hasStatusOk();

        // A separate request proves the change reached the database (dirty checking).
        assertThat(mvc.get().uri("/api/tasks/{id}", id))
                .hasStatusOk()
                .bodyJson().isLenientlyEqualTo("""
                        {"title": "New title", "description": "Now with details", "status": "DONE"}
                        """);
    }

    @Test
    void updateWithUnknownStatusValueReturns400() {
        long id = createTask("Task");

        assertThat(put(id, """
                {"title": "Task", "status": "FINISHED"}
                """))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateWithoutStatusReturns400WithFieldError() {
        long id = createTask("Task");

        MvcTestResult result = put(id, """
                {"title": "Task"}
                """);

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.errors.status").isNotNull();
    }

    @Test
    void updateUnknownIdReturns404() {
        assertThat(put(99L, """
                {"title": "Task", "status": "DONE"}
                """))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    // --- DELETE /api/tasks/{id} ---

    @Test
    void deleteReturns204AndTaskIsGone() {
        long id = createTask("Task");

        assertThat(mvc.delete().uri("/api/tasks/{id}", id))
                .hasStatus(HttpStatus.NO_CONTENT);
        assertThat(mvc.get().uri("/api/tasks/{id}", id))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteUnknownIdReturns404() {
        assertThat(mvc.delete().uri("/api/tasks/{id}", 99))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    // --- Actuator ---

    @Test
    void healthEndpointReportsUp() {
        assertThat(mvc.get().uri("/actuator/health"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.status").isEqualTo("UP");
    }

    // --- Helpers ---

    private MvcTestResult post(String json) {
        return mvc.post().uri("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .exchange();
    }

    private MvcTestResult put(long id, String json) {
        return mvc.put().uri("/api/tasks/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .exchange();
    }

    private long createTask(String title) {
        MvcTestResult result = post("""
                {"title": "%s"}
                """.formatted(title));
        assertThat(result).hasStatus(HttpStatus.CREATED);
        String location = result.getResponse().getHeader("Location");
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }
}
