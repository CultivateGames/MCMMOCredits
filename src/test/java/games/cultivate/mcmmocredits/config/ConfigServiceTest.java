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

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ConfigServiceTest {
    private static final Path TEST_PATH = Path.of("src", "test", "resources");
    private static ConfigService service;
    private static MockedStatic<Bukkit> mockBukkit;

    @BeforeAll
    static void setUp() {
        mockBukkit = mockStatic(Bukkit.class);
        ItemFactory mockFactory = mock(ItemFactory.class);
        ItemMeta mockMeta = mock(ItemMeta.class);
        mockBukkit.when(Bukkit::getItemFactory).thenReturn(mockFactory);
        when(mockFactory.getItemMeta(any())).thenReturn(mockMeta);
        when(mockMeta.getCustomModelData()).thenReturn(1);
        service = new ConfigService(TEST_PATH);
    }

    @AfterAll
    static void tearDown() {
        mockBukkit.close();
    }

    @AfterEach
    void tearDown0() {
        Arrays.stream(TEST_PATH.toFile().listFiles()).forEach(File::delete);
    }

    @Test
    void createFile_CreatesFile() {
        ConfigService.createFile(TEST_PATH, "test-file.txt");
        assertNotNull(TEST_PATH.resolve("test-file.txt").toFile());
    }

    @Test
    void reloadConfigs_LoadsConfigs() {
        service.reloadConfigs();
        assertNotNull(service.mainConfig());
        assertNotNull(service.menuConfig());
    }

    @Test
    void loadConfig_LoadsConfig() {
        assertNotNull(service.loadConfig(MainData.class, "testconfig.yml"));
    }
}
