package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Optional;

public class SubscriptionServiceIT extends IntegrationTestBase {

    private SubscriptionService subscriptionService;
    private SubscriptionDao subscriptionDao;
    private static final LocalDateTime EXPIRATION_DATE = LocalDateTime.of(2030, Month.DECEMBER, 31, 23, 59, 59);


    @BeforeEach
    void init() {
        Clock clock = new Clock() {
            @Override
            public ZoneId getZone() {
                return null;
            }

            @Override
            public Clock withZone(ZoneId zone) {
                return null;
            }

            @Override
            public Instant instant() {
                return Instant.now();
            }
        };
        subscriptionDao = SubscriptionDao.getInstance();
        subscriptionService = new SubscriptionService(
                subscriptionDao,
                CreateSubscriptionMapper.getInstance(),
                CreateSubscriptionValidator.getInstance(),
                clock
        );
    }

    @Test
    void upsert() {
        Subscription subscription = subscriptionDao.insert(getSubscription("subscription1"));

        Subscription actualResult = subscriptionService.upsert(getCreateSubscriptionDto(subscription));

        Assertions.assertThat(actualResult.getId()).isEqualTo(subscription.getId());
    }

    @Test
    void cancel() {
        Subscription subscription = subscriptionDao.insert(getSubscription("subscription1"));

        subscriptionService.cancel(subscription.getId());

        Optional<Subscription> actualResult = subscriptionDao.findById(subscription.getId());

        Assertions.assertThat(actualResult).isPresent();
    }

    @Test
    void expire() {
        Subscription subscription = subscriptionDao.insert(getSubscription("subscription1"));

        subscriptionService.expire(subscription.getId());

        Optional<Subscription> actualResult = subscriptionDao.findById(subscription.getId());

        Assertions.assertThat(actualResult).isPresent();
    }

    private Subscription getSubscription(String name) {
        return Subscription.builder()
                .userId(1)
                .name(name)
                .provider(Provider.APPLE)
                .expirationDate(EXPIRATION_DATE.toInstant(ZoneOffset.UTC))
                .status(Status.ACTIVE)
                .build();
    }

    private CreateSubscriptionDto getCreateSubscriptionDto(Subscription subscription) {
        return CreateSubscriptionDto.builder()
                .userId(subscription.getUserId())
                .name(subscription.getName())
                .provider(subscription.getProvider().name())
                .expirationDate(subscription.getExpirationDate())
                .build();
    }
}