package com.dmdev.dao;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionDaoIT extends IntegrationTestBase {

    private final SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();
    private static final LocalDateTime EXPIRATION_DATE = LocalDateTime.of(2030, Month.DECEMBER, 31, 23, 59, 59);


    @Test
    void findAll() {
        Subscription subscription1 = subscriptionDao.insert(getSubscription("firstSubscription"));
        Subscription subscription2 = subscriptionDao.insert(getSubscription("secondSubscription"));
        Subscription subscription3 = subscriptionDao.insert(getSubscription("thirdSubscription"));

        List<Subscription> actualResult = subscriptionDao.findAll();


        assertThat(actualResult).hasSize(3);
        List<Integer> subscriptionIds = actualResult.stream()
                .map(Subscription::getId)
                .toList();
        assertThat(subscriptionIds).contains(subscription1.getId(), subscription2.getId(), subscription3.getId());
    }

    @Test
    void findById() {
        Subscription subscription = subscriptionDao.insert(getSubscription("firstSubscription"));

        Optional<Subscription> actualResult = subscriptionDao.findById(subscription.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(subscription);
    }

    @Test
    void deleteExistingEntity() {
        Subscription subscription = subscriptionDao.insert(getSubscription("Subscription"));

        boolean actualResult = subscriptionDao.delete(subscription.getId());

        assertTrue(actualResult);
    }

    @Test
    void deleteNotExistingEntity() {
        subscriptionDao.insert(getSubscription("Subscription"));

        boolean actualResult = subscriptionDao.delete(111555);

        assertFalse(actualResult);
    }

    @Test
    void update() {
        Subscription subscription = getSubscription("Subscription");
        subscriptionDao.insert(subscription);
        subscription.setName("updated subscription");
        subscription.setProvider(Provider.GOOGLE);

        subscriptionDao.update(subscription);

        Subscription updatedSubscription = subscriptionDao.findById(subscription.getId()).get();
        assertThat(updatedSubscription).isEqualTo(subscription);
    }

    @Test
    void insert() {
        Subscription subscription = getSubscription("Subscription");

        Subscription actualResult = subscriptionDao.insert(subscription);

        assertNotNull(actualResult.getId());
    }

    @Test
    void findByUserId() {
        Subscription subscription = subscriptionDao.insert(getSubscription("Subscription"));

        Optional<Subscription> actualResult = subscriptionDao.findByUserId(subscription.getUserId()).stream()
                .filter(existingSubscription -> existingSubscription.getName().equals(subscription.getName()))
                .filter(existingSubscription -> existingSubscription.getProvider() == Provider.findByName(subscription.getProvider().name()))
                .findFirst()
                .map(existingSubscription -> existingSubscription
                        .setExpirationDate(subscription.getExpirationDate())
                        .setStatus(Status.ACTIVE));

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(subscription);
    }

    @Test
    void shouldNotFindByUserIdIfSubscriptionDoesNotExist() {
        subscriptionDao.insert(getSubscription("Subscription"));

        List<Subscription> actualResult = subscriptionDao.findByUserId(null);

        assertThat(actualResult).isEmpty();
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
}