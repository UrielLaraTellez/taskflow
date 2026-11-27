package com.taskflow.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void unexpectedExceptionReturns500WithoutLeakingDetails() {
        ProblemDetail problem = handler.handleUnexpected(
                new IllegalStateException("db password is hunter2"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problem.getDetail())
                .isEqualTo("An unexpected error occurred")
                .doesNotContain("hunter2");
    }
}
