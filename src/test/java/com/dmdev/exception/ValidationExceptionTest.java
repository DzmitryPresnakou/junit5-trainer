package com.dmdev.exception;

import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationExceptionTest {

    @Test
    void getErrors() {
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of(100, "userId is invalid"));
        validationResult.add(Error.of(102, "provider is invalid"));

        ValidationException validationException = new ValidationException(validationResult.getErrors());

        assertThat(validationException.getErrors().size()).isEqualTo(2);
    }
}