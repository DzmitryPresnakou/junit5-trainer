package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionDao subscriptionDao;
    @Mock
    private CreateSubscriptionMapper createSubscriptionMapper;
    @Mock
    private CreateSubscriptionValidator createSubscriptionValidator;
    @Mock
    private Clock clock;
    @InjectMocks
    private SubscriptionService subscriptionService;

    private static final LocalDateTime EXPIRATION_DATE = LocalDateTime.of(2030, Month.DECEMBER, 31, 23, 59, 59);

    @Test
    void upsertSuccess() {

        Subscription subscription = getSubscription(1);
        CreateSubscriptionDto subscriptionDto = getCreateSubscriptionDto(1);

        List<Subscription> subscriptionList = new ArrayList<>();
        subscriptionList.add(subscription);

        lenient().when(createSubscriptionMapper.map(subscriptionDto)).thenReturn(subscription);
        doReturn(subscriptionList).when(subscriptionDao).findByUserId(subscriptionDto.getUserId());
        doReturn(subscription).when(subscriptionDao).upsert(subscription);
        doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(subscriptionDto);

        Subscription actualResult = subscriptionService.upsert(subscriptionDto);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(subscription);
    }

    private static CreateSubscriptionDto getCreateSubscriptionDto(Integer userId) {
        return CreateSubscriptionDto.builder()
                .userId(userId)
                .name("firstSubscription")
                .provider(Provider.APPLE.name())
                .expirationDate(EXPIRATION_DATE.toInstant(ZoneOffset.UTC))
                .build();
    }

    private static Subscription getSubscription(Integer userId) {
        return Subscription.builder()
                .id(1)
                .userId(userId)
                .name("firstSubscription")
                .provider(Provider.APPLE)
                .expirationDate(EXPIRATION_DATE.toInstant(ZoneOffset.UTC))
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    void upsertFailed() {

        Subscription subscription = getSubscription(null);
        CreateSubscriptionDto subscriptionDto = getCreateSubscriptionDto(null);

        List<Subscription> subscriptionList = new ArrayList<>();
        subscriptionList.add(subscription);

        doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(subscriptionDto);
        doReturn(subscriptionList).when(subscriptionDao).findByUserId(subscriptionDto.getUserId());

        Subscription actualResult = subscriptionService.upsert(subscriptionDto);

        assertThat(actualResult).isNull();
        verifyNoInteractions(createSubscriptionMapper);
    }
}