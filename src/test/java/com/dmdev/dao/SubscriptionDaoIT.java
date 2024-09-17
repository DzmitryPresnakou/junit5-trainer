package com.dmdev.dao;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubscriptionDaoIT extends IntegrationTestBase {

    private static final Instant EXPIRATION_DATE = Instant.now().plus(Duration.ofDays(30)).truncatedTo(ChronoUnit.SECONDS);
    private final SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

    @Test
    void findAll() {
        Subscription subscription1 = subscriptionDao.insert(getSubscription(1, "firstSubscription"));
        Subscription subscription2 = subscriptionDao.insert(getSubscription(2, "secondSubscription"));
        Subscription subscription3 = subscriptionDao.insert(getSubscription(3, "thirdSubscription"));

        List<Subscription> actualResult = subscriptionDao.findAll();

        assertThat(actualResult).hasSize(3);
        List<Integer> subscriptionIds = actualResult.stream()
                .map(Subscription::getId)
                .toList();
        assertThat(subscriptionIds).contains(subscription1.getId(), subscription2.getId(), subscription3.getId());
    }

    @Test
    void findById() {
        Subscription subscription = subscriptionDao.insert(getSubscription(1, "firstSubscription"));

        Optional<Subscription> actualResult = subscriptionDao.findById(subscription.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(subscription);
    }

    @Test
    void deleteExistingEntity() {
        Subscription subscription = subscriptionDao.insert(getSubscription(1, "Subscription"));

        boolean actualResult = subscriptionDao.delete(subscription.getId());

        assertTrue(actualResult);
    }

    @Test
    void deleteNotExistingEntity() {
        subscriptionDao.insert(getSubscription(123, "Subscription"));

        boolean actualResult = subscriptionDao.delete(111555);

        assertFalse(actualResult);
    }

    @Test
    void update() {
        Subscription subscription = getSubscription(1, "Subscription");

        subscriptionDao.insert(subscription);
        subscription.setName("updated subscription");
        subscription.setProvider(Provider.GOOGLE);
        subscriptionDao.update(subscription);

        Subscription updatedSubscription = subscriptionDao.findById(subscription.getId()).get();

        assertThat(updatedSubscription).isEqualTo(subscription);
    }

    @Test
    void insert() {
        Subscription subscription = getSubscription(1, "Subscription");

        Subscription actualResult = subscriptionDao.insert(subscription);

        assertNotNull(actualResult.getId());
    }

    @ParameterizedTest
    @MethodSource("getUserIds")
    void findByUserId(Integer userId) {
        String subscriptionName = "subscription";
        Subscription subscription = subscriptionDao.insert(getSubscription(userId, subscriptionName));

        Optional<Subscription> actualResult = getActualResult(subscription);

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(subscription);
    }

    @Test
    void shouldNotFindByUserIdIfSubscriptionDoesNotExist() {
        List<Subscription> actualResult = subscriptionDao.findByUserId(null);

        assertThat(actualResult).isEmpty();
    }

    private Subscription getSubscription(Integer userId, String name) {
        return Subscription.builder()
                .userId(userId)
                .name(name)
                .provider(Provider.APPLE)
                .expirationDate(EXPIRATION_DATE)
                .status(Status.ACTIVE)
                .build();
    }

    private Optional<Subscription> getActualResult(Subscription subscription) {
        return subscriptionDao.findByUserId(subscription.getUserId()).stream()
                .filter(existingSubscription -> existingSubscription.getName().equals(subscription.getName()))
                .filter(existingSubscription -> existingSubscription.getProvider() == Provider.findByName(subscription.getProvider().name()))
                .findFirst()
                .map(existingSubscription -> existingSubscription
                        .setExpirationDate(subscription.getExpirationDate())
                        .setStatus(Status.ACTIVE));
    }

    static Stream<Arguments> getUserIds() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(2),
                Arguments.of(3),
                Arguments.of(4)
        );
    }
}