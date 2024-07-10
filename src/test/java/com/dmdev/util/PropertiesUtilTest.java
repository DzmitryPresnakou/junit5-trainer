package com.dmdev.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Properties;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesUtilTest {

    private static final Properties properties = new Properties();

    @ParameterizedTest
    @MethodSource("getPropertyArguments")
    void checkGet(String key, String expectedValue) {
        String actualResult = PropertiesUtil.get(key);

        assertEquals(expectedValue, actualResult);
    }

    @Test
    void getShouldThrowNullPointerException() {
        String key = null;

        assertThatThrownBy(() -> PropertiesUtil.get(key)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void loadShouldThrowNullPointerException() throws IOException {
        var inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.dummy");

        assertThatThrownBy(() -> properties.load(inputStream)).isInstanceOf(NullPointerException.class);
    }

    static Stream<Arguments> getPropertyArguments() {
        return Stream.of(
                Arguments.of("db.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"),
                Arguments.of("db.user", "sa"),
                Arguments.of("db.password", "111"),
                Arguments.of("db.driver", "org.h2.Driver")
        );
    }
}