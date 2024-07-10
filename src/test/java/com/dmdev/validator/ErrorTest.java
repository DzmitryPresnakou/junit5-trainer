package com.dmdev.validator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorTest {

    @Test
    void getCode() {
        Integer code = 100;
        Error error = Error.of(code, "userId is invalid");
        Integer actualResult = error.getCode();

        assertThat(actualResult).isEqualTo(code);
    }

    @Test
    void getMessage() {
        String message = "userId is invalid";
        Error error = Error.of(100, message);
        String actualResult = error.getMessage();

        assertThat(actualResult).isEqualTo(message);
    }

    @Test
    void of() {
        Integer code = 100;
        String message = "userId is invalid";
        Error error = Error.of(code, message);

        Integer actualCode = error.getCode();
        String actualMessage = error.getMessage();

        assertThat(actualCode).isEqualTo(code);
        assertThat(actualMessage).isEqualTo(message);
    }
}