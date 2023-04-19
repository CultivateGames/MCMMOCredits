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

import games.cultivate.mcmmocredits.database.DatabaseProperties;
import games.cultivate.mcmmocredits.menu.ClickTypes;
import games.cultivate.mcmmocredits.menu.Item;
import games.cultivate.mcmmocredits.menu.Menu;
import games.cultivate.mcmmocredits.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ConfigTest {
    private static final Path TEST_PATH = Path.of("src", "test", "resources");
    private static MainConfig config;
    private static MenuConfig menuConfig;

    @BeforeAll
    static void setUp() {
        try (MockedStatic<Bukkit> mockedBukkit = Mockito.mockStatic(Bukkit.class)) {
            ItemFactory factory = mock(ItemFactory.class);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(factory);
            config = new MainConfig(TEST_PATH);
            config.load();
            menuConfig = new MenuConfig(TEST_PATH);
            menuConfig.load();
        }
    }

    @AfterAll
    static void tearDown() throws IOException {
        Files.deleteIfExists(TEST_PATH.resolve("config.yml"));
        Files.deleteIfExists(TEST_PATH.resolve("menus.yml"));
    }

    @Test
    void getBoolean_WhenBooleanValueExists_ReturnsValue() {
        //Arrange
        boolean debug = false;

        //Act
        boolean result = config.getBoolean("settings", "debug");

        //Assert
        assertEquals(debug, result);
    }

    @Test
    void getBoolean_WhenBooleanValueDoesNotExist_ReturnsFalse() {
        //Act
        boolean result = config.getBoolean("settings", "bad-section");

        //Assert
        assertFalse(result);
    }

    @Test
    void getMessage_WhenStringValueExists_ReturnsPrefixedValue() {
        //Arrange
        String prefix = "<hover:show_text:'<green><sender>: <sender_credits> Credits'><gold><bold>CREDITS</bold> ";
        String balance = "<green>You have <sender_credits> MCMMO Credits! You have redeemed <sender_redeemed> Credits.";

        //Act
        String result = config.getMessage("balance");

        //Assert
        assertEquals(prefix + balance, result);
    }

    @Test
    void getMessage_WhenStringValueDoesNotExist_ReturnsPrefix() {
        //Arrange
        String prefix = "<hover:show_text:'<green><sender>: <sender_credits> Credits'><gold><bold>CREDITS</bold> ";

        //Act
        String result = config.getMessage("bad-section");

        //Assert
        assertEquals(prefix, result);
    }


    @Test
    void getString_WhenStringValueExists_ReturnsValue() {
        //Arrange
        String command = "credits";

        //Act
        String result = config.getString("command-prefix");

        //Assert
        assertEquals(command, result);
    }

    @Test
    void getString_WhenStringValueDoesNotExist_ReturnsEmptyString() {
        //Act
        String result = config.getString("section", "nonexistentStringValue");

        //Assert
        assertEquals("", result);
    }

    @Test
    void getInteger_WhenIntegerValueExists_ReturnsValue() {
        //Arrange
        int pageSize = 10;

        //Act
        int result = config.getInteger("settings", "leaderboard-page-size");

        //Assert
        assertEquals(pageSize, result);
    }

    @Test
    void getInteger_WhenIntegerValueDoesNotExist_ReturnsZero() {
        //Arrange
        int pageSize = 0;

        //Act
        int result = config.getInteger("settings", "leaderboard-size");

        //Assert
        assertEquals(pageSize, result);
    }

    @Test
    void getDatabaseProperties_WhenDatabasePropertiesExist_ReturnsProperties() {
        //Arrange
        DatabaseProperties properties = DatabaseProperties.defaults();

        //Act
        DatabaseProperties result = config.getDatabaseProperties("settings", "database");

        //Assert
        assertNotNull(result);
        assertEquals(properties, result);
    }


    @Test
    void getMenu_WhenMenuSectionExists_ReturnsDeserializedMenu() {
        //Arrange
        try (MockedStatic<Bukkit> mockedBukkit = Mockito.mockStatic(Bukkit.class)) {
            ItemFactory factory = mock(ItemFactory.class);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(factory);
            String title = "<#ff253c><bold>MCMMO Credits";
            int slots = 54;
            boolean fill = false;
            boolean navigation = false;

            //Act
            Menu result = menuConfig.getMenu("main");

            //Assert
            assertNotNull(result);
            assertEquals(title, result.title());
            assertEquals(slots, result.slots());
            assertEquals(fill, result.fill());
            assertEquals(navigation, result.navigation());
        }
    }

    @Test
    void getMenu_WhenMenuSectionExists_ReturnsMenuWithCorrectItems() {
        //Arrange
        try (MockedStatic<Bukkit> mockedBukkit = Mockito.mockStatic(Bukkit.class)) {
            ItemFactory factory = mock(ItemFactory.class);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(factory);
            Item configItem = Util.createCommandItem(Material.DIAMOND, "<#FF253C>Edit Config", "<gray>Left Click to edit config!", "credits menu config", 11);
            Item redeemItem = Util.createCommandItem(Material.EMERALD, "<green>Redeem MCMMO Credits!", "<gray>Left Click to redeem Credits!", "credits menu redeem", 15);
            Item fillItem = Item.of(Material.BLACK_STAINED_GLASS_PANE);
            Item navigationItem = Util.createCommandItem(Material.COMPASS, "<red>Previous Menu", "<gray>Left Click to go back!", "credits menu main", 40);

            //Act
            Menu result = menuConfig.getMenu("main");
            Item resultConfig = result.items().get("config");
            Item resultRedeem = result.items().get("redeem");
            Item resultFill = result.items().get("fill");
            Item resultNavigation = result.items().get("navigation");

            //Assert
            assertNotNull(result);
            assertEquals(configItem, resultConfig);
            assertEquals(redeemItem, resultRedeem);
            assertEquals(navigationItem, resultNavigation);
            assertEquals(fillItem, resultFill);
        }
    }

    @Test
    void getMenu_WhenMenuSectionDoesNotExist_ReturnsNull() {
        //Act
        Menu result = config.getMenu("nonexistentMenuSection");

        //Assert
        assertNull(result);
    }

    @Test
    void getItem_WhenItemExists_ReturnsItem() {
        //Arrange
        try (MockedStatic<Bukkit> mockedBukkit = Mockito.mockStatic(Bukkit.class)) {
            ItemFactory factory = mock(ItemFactory.class);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(factory);

            //Act
            Item result = menuConfig.getItem("main", "items", "navigation");

            //Assert
            assertNotNull(result);
            assertEquals("<red>Previous Menu", result.name());
            assertEquals(40, result.slot());
            assertEquals(ClickTypes.COMMAND, result.clickType());
            assertEquals("credits menu main", result.data());
        }
    }

    @Test
    void getItem_WhenItemDoesNotExist_ReturnsNull() {
        //Act
        Item result = config.getItem("menuSection", "items", "nonexistentItem");

        //Assert
        assertNull(result);
    }


    @Test
    void set_WhenSettingNewStringValue_SavesSuccessfully() {
        //Arrange
        String newValue = "New value";

        //Act
        boolean setResult = config.set(newValue, "section", "newStringValue");
        String getResult = config.getString("section", "newStringValue");

        //Assert
        assertTrue(setResult);
        assertEquals(newValue, getResult);
    }

    @Test
    void set_WhenSettingItem_SavesSuccessfully() {
        //Arrange
        try (MockedStatic<Bukkit> mockedBukkit = Mockito.mockStatic(Bukkit.class)) {
            ItemFactory factory = mock(ItemFactory.class);
            mockedBukkit.when(Bukkit::getItemFactory).thenReturn(factory);
            Material mat = Material.WHITE_BANNER;
            int slot = 12;
            int amount = 10;
            String name = "testingThisItem";
            String data = "credits command";
            List<String> lore = List.of("testing the lore!");
            ClickTypes type = ClickTypes.COMMAND;
            Item newNavItem = Item.builder().item(new ItemStack(mat, amount)).name(name).slot(slot).lore(lore).type(type).data(data).build();

            //Act
            config.set(newNavItem, "menu-section", "items", "navigation");
            Item result = config.getItem("menu-section", "items", "navigation");

            //Assert
            assertEquals(slot, result.slot());
            assertEquals(name, result.name());
            assertEquals(lore, result.lore());
            assertEquals(amount, result.stack().getAmount());
            assertEquals(mat, result.stack().getType());
            assertEquals(type, result.clickType());
            assertEquals(data, result.data());
        }
    }

    @Test
    void getPaths_WhenCalled_ReturnsNonEmptyPaths() {
        //Act
        List<String> paths = config.getPaths();

        //Assert
        assertNotNull(paths);
        assertFalse(paths.isEmpty());
    }

    @Test
    void filterNodes_WhenCalledWithFilter_ReturnsFilteredPaths() {
        //Arrange
        List<String> originalPaths = config.getPaths();
        Predicate<String> test = x -> x.contains("database");

        //Act
        List<String> filteredPaths = config.filterNodes(test);

        //Assert
        assertNotNull(filteredPaths);
        assertFalse(filteredPaths.isEmpty());
        assertTrue(filteredPaths.size() < originalPaths.size());
        assertTrue(filteredPaths.stream().noneMatch(test));
    }

    @Test
    void filterNodes_WhenCalledWithNullFilter_ReturnsUnchangedPaths() {
        //Arrange
        List<String> originalPaths = config.getPaths();

        //Act
        List<String> filteredPaths = config.filterNodes(null);

        //Assert
        assertNotNull(filteredPaths);
        assertEquals(originalPaths.size(), filteredPaths.size());
    }

    @Test
    void filterNodes_WhenCalledWithAlwaysTrueFilter_ReturnsEmptyPaths() {
        //Act
        List<String> filteredPaths = config.filterNodes(path -> true);

        //Assert
        assertNotNull(filteredPaths);
        assertTrue(filteredPaths.isEmpty());
    }
}
