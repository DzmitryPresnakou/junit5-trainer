package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CreateSubscriptionValidatorTest {

    private final CreateSubscriptionValidator validator = CreateSubscriptionValidator.getInstance();
    private static final Instant EXPIRATION_DATE = Instant.now().plus(Duration.ofDays(30)).truncatedTo(ChronoUnit.SECONDS);

    @Test
    void shouldPassValidation() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("firstSubscription")
                .provider(Provider.APPLE.name())
                .expirationDate(EXPIRATION_DATE)
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertFalse(actualResult.hasErrors());
    }

    @Test
    void invalidUserId() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("firstSubscription")
                .provider(Provider.APPLE.name())
                .expirationDate(EXPIRATION_DATE)
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().getFirst().getCode()).isEqualTo(100);
    }

    @Test
    void invalidName() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name(null)
                .provider(Provider.APPLE.name())
                .expirationDate(EXPIRATION_DATE)
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().getFirst().getCode()).isEqualTo(101);
    }

    @Test
    void invalidProvider() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("firstSubscription")
                .provider("fake")
                .expirationDate(EXPIRATION_DATE)
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().getFirst().getCode()).isEqualTo(102);
    }

    @ParameterizedTest
    @MethodSource("getPropertyArguments")
    void invalidExpirationDate(Instant expirationDate) {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("firstSubscription")
                .provider(Provider.APPLE.name())
                .expirationDate(expirationDate)
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().getFirst().getCode()).isEqualTo(103);
    }

    @ParameterizedTest
    @MethodSource("getPropertyArguments")
    void invalidUserIdNameProviderExpirationDate(Instant expirationDate) {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(null)
                .name(null)
                .provider("fake")
                .expirationDate(expirationDate)
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(4);
        List<Integer> errorCodes = actualResult.getErrors().stream()
                .map(Error::getCode)
                .toList();
        assertThat(errorCodes).contains(100, 101, 102, 103);
    }

    static Stream<Arguments> getPropertyArguments() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(Instant.now())
        );
    }
}