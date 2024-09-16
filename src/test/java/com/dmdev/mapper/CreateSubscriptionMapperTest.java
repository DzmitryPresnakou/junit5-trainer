package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class CreateSubscriptionMapperTest {

    private final CreateSubscriptionMapper mapper = CreateSubscriptionMapper.getInstance();
    private static final LocalDateTime EXPIRATION_DATE = LocalDateTime.of(2030, Month.DECEMBER, 31, 23, 59, 59);

    @Test
    void map() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("firstSubscription")
                .provider(Provider.APPLE.name())
                .expirationDate(EXPIRATION_DATE.toInstant(ZoneOffset.UTC))
                .build();

        Subscription actualResult = mapper.map(dto);

        Subscription expectedResult = Subscription.builder()
                .userId(1)
                .name("firstSubscription")
                .provider(Provider.APPLE)
                .expirationDate(EXPIRATION_DATE.toInstant(ZoneOffset.UTC))
                .status(Status.ACTIVE)
                .build();

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}