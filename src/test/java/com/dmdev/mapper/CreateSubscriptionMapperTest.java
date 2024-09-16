package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class CreateSubscriptionMapperTest {

    private final CreateSubscriptionMapper mapper = CreateSubscriptionMapper.getInstance();
    private static final Instant EXPIRATION_DATE = Instant.now().plus(Duration.ofDays(30)).truncatedTo(ChronoUnit.SECONDS);

    @Test
    void map() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("firstSubscription")
                .provider(Provider.APPLE.name())
                .expirationDate(EXPIRATION_DATE)
                .build();

        Subscription actualResult = mapper.map(dto);

        Subscription expectedResult = Subscription.builder()
                .userId(1)
                .name("firstSubscription")
                .provider(Provider.APPLE)
                .expirationDate(EXPIRATION_DATE)
                .status(Status.ACTIVE)
                .build();

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}