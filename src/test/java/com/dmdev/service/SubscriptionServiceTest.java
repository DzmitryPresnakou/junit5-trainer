package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.ValidationResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;


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

        Subscription subscription = Subscription.builder()
                .id(1)
                .userId(1)
                .name("firstSubscription")
                .provider(Provider.APPLE)
                .expirationDate(EXPIRATION_DATE.toInstant(ZoneOffset.UTC))
                .status(Status.ACTIVE)
                .build();

        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("firstSubscription")
                .provider(Provider.APPLE.name())
                .expirationDate(EXPIRATION_DATE.toInstant(ZoneOffset.UTC))
                .build();

        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());
        doReturn(subscription).when(createSubscriptionMapper).map(subscriptionDto);
        when(createSubscriptionValidator.validate(subscriptionDto)).thenReturn(new ValidationResult());

        Subscription actualResult = subscriptionService.upsert(subscriptionDto);




        assertThat(actualResult).isEqualTo(subscription);

//        doReturn(Optional.of(subscription)).when(subscriptionDao).findByUserId(subscription.getUserId());
//        doReturn(subscription).when(createSubscriptionMapper).map(subscriptionDto);
//
//        Subscription actualResult = subscriptionService.upsert(subscriptionDto);
//
//        assertThat(actualResult).isPresent();
    }


    @Test
    void upsertFailed() {
    }
}
