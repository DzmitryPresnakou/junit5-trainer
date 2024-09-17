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
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SubscriptionServiceIT extends IntegrationTestBase {

    private static final Instant EXPIRATION_DATE = Instant.now().plus(Duration.ofDays(30)).truncatedTo(ChronoUnit.SECONDS);
    private SubscriptionService subscriptionService;
    private SubscriptionDao subscriptionDao;
    private Clock clock;

    @BeforeEach
    void init() {
        clock = Clock.systemUTC();
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
        assertThat(actualResult.get().getExpirationDate().truncatedTo(ChronoUnit.SECONDS)).isEqualTo(Instant.now(clock).truncatedTo(ChronoUnit.SECONDS));
    }

    private Subscription getSubscription(String name) {
        return Subscription.builder()
                .userId(1)
                .name(name)
                .provider(Provider.APPLE)
                .expirationDate(EXPIRATION_DATE)
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