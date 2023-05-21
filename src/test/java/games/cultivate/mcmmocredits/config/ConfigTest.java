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

import games.cultivate.mcmmocredits.converters.ConverterType;
import games.cultivate.mcmmocredits.database.DatabaseProperties;
import games.cultivate.mcmmocredits.ui.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigTest {
    @Mock
    private MockedStatic<Bukkit> mockBukkit;
    @Mock
    private ItemFactory mockFactory;
    @Mock
    private ItemMeta mockMeta;
    private FakeConfig config;

    @BeforeEach
    void setUp() {
        this.mockBukkit.when(Bukkit::getItemFactory).thenReturn(this.mockFactory);
        when(this.mockFactory.getItemMeta(any(Material.class))).thenReturn(this.mockMeta);
        this.config = new FakeConfig();
        this.config.load(Path.of(""), "");
    }

    @Test
    void getBoolean_ReturnsCorrectValue() {
        assertTrue(this.config.getBoolean("leaderboard-enabled"));
        assertFalse(this.config.getBoolean("debug"));
    }

    @Test
    void getString_ReturnsCorrectValue() {
        assertEquals("The message prefix!!", this.config.getString("prefix"));
    }

    @Test
    void getMessage_ReturnsStringAndPrefix() {
        assertEquals("The message prefix!!The actual message!", this.config.getMessage("fake-message"));
    }

    @Test
    void getInteger_ReturnsCorrectValue() {
        assertEquals(10, this.config.getInteger("leaderboard-page-size"));
    }

    @Test
    void getLong_ReturnsCorrectValue() {
        assertEquals(60000L, this.config.getInteger("retry-delay"));
    }

    @Test
    void getDatabaseProperties_ReturnsCorrectValue() {
        assertEquals(DatabaseProperties.defaults(), this.config.getDatabaseProperties("database"));
    }

    @Test
    void getConverterType_ReturnsCorrectValue() {
        assertEquals(ConverterType.INTERNAL, this.config.getConverterType("converter-type"));
    }

    @Test
    void getMenu_ReturnsCorrectValue() {
        Menu menu = this.config.getMenu("menu");
        assertEquals(Material.BLACK_STAINED_GLASS_PANE, menu.items().get("fill").stack().getType());
        assertEquals(Material.COMPASS, menu.items().get("navigation").stack().getType());
        assertEquals(54, menu.slots());
        assertEquals("The menu title!", menu.title());
        assertFalse(menu.fill());
        assertTrue(menu.navigation());
    }

    @Test
    void get_ReturnsDefaultOnBadSection() {
        assertEquals(ConverterType.EXTERNAL_CSV, this.config.get(ConverterType.class, ConverterType.EXTERNAL_CSV, "fake-section", "converter-type"));
    }

    @Test
    void set_SetsCorrectValue() {
        String test = "The test string--";
        assertNotEquals(test, this.config.getString("fake-message"));
        this.config.set(test, "fake-message");
        assertEquals(test, this.config.getString("fake-message"));
    }

    @Test
    void filterNodes_FiltersCorrectNodes() {
        assertTrue(this.config.filterNodes(x -> false).contains("converter-type"));
        assertFalse(this.config.filterNodes(x -> x.contains("converter-type")).contains("converter-type"));
    }
}
