package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SubscriptionServiceIT extends IntegrationTestBase {

    private SubscriptionService subscriptionService;
    private SubscriptionDao subscriptionDao;
    private static final LocalDateTime EXPIRATION_DATE = LocalDateTime.of(2030, Month.DECEMBER, 31, 23, 59, 59);

    @BeforeEach
    void init() {
        Clock clock = Clock.systemUTC();
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
        assertThat(actualResult.getId()).isEqualTo(subscription.getId());
        assertThat(actualResult.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void cancel() {
        Subscription subscription = subscriptionDao.insert(getSubscription("subscription1"));

        subscriptionService.cancel(subscription.getId());
        Optional<Subscription> actualResult = subscriptionDao.findById(subscription.getId());
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getStatus()).isEqualTo(Status.CANCELED);
    }

    @Test
    void expire() {
        Subscription subscription = subscriptionDao.insert(getSubscription("subscription1"));

        subscriptionService.expire(subscription.getId());
        Optional<Subscription> actualResult = subscriptionDao.findById(subscription.getId());
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getStatus()).isEqualTo(Status.EXPIRED);
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