package com.dmdev.util;

import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;

import static com.dmdev.util.PropertiesUtil.properties;
import static org.assertj.core.api.Assertions.assertThat;


class PropertiesUtilTest {

    public static final String PROPERTY_FILE_WITH_PATH = "src/test/resources/application.properties";

    @Test
    void get() {

        String property = "db.name=h2";

        writeStringInPropertyFile(property);

        String key = "db.name";

        String actualResult = PropertiesUtil.get(key);

        assertThat(actualResult).isEqualTo("h2");

    }

    private static void writeStringInPropertyFile(String property) {

        try {
            FileWriter writer = new FileWriter(PROPERTY_FILE_WITH_PATH, true);

            writer.write("\n" + "db.name=h2");

            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
//    void shouldThrowExceptionIfKeyInvalid() {
//
//
//    }
}