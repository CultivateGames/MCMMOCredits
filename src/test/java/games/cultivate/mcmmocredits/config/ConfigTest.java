//
// MIT License
//
// Copyright (c) 2023 Cultivate Games
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.converters.DataLoadingStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigTest {
    private static final String CONF = "";
    private static Config<MainData> config;

    @BeforeAll
    static void setUp() {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .source(() -> new BufferedReader(new StringReader(CONF)))
                .sink(() -> new BufferedWriter(new StringWriter()))
                .build();
        config = new Config<>(loader);
        config.load(MainData.class);
    }

    @Test
    void getBoolean_ReturnsCorrectValue() {
        assertFalse(config.getBoolean("settings", "leaderboard-enabled"));
    }

    @Test
    void getString_ReturnsCorrectValue() {
        assertEquals("credits", config.getString("command-prefix"));
    }

    @Test
    void getMessage_ReturnsStringAndPrefix() {
        config.set("The message prefix!!", "prefix");
        config.set("The actual message!", "test-message-section");
        assertEquals("The message prefix!!The actual message!", config.getMessage("test-message-section"));
    }

    @Test
    void getInteger_ReturnsCorrectValue() {
        assertEquals(10, config.getInteger("settings", "leaderboard-page-size"));
    }

    @Test
    void get_ReturnsDefaultOnBadSection() {
        assertEquals(DataLoadingStrategy.CSV, config.get(DataLoadingStrategy.class, DataLoadingStrategy.CSV, "fake-section", "converter-type"));
    }

    @Test
    void set_SetsCorrectValue() {
        String test = "The test string--";
        assertTrue(config.set(test, "fake-message"));
        assertEquals(test, config.get(String.class, "", "fake-message"));
    }
}
