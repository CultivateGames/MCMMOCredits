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
package games.cultivate.mcmmocredits.ui.menu;

import games.cultivate.mcmmocredits.ui.item.BaseItem;
import games.cultivate.mcmmocredits.ui.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

//TODO: expand to test built ChestInterface
@ExtendWith(MockitoExtension.class)
class BaseMenuTest {
    @Mock
    private MockedStatic<Bukkit> mockBukkit;
    @Mock
    private ItemFactory mockFactory;

    @BeforeEach
    void setUp() {
        this.mockBukkit.when(Bukkit::getItemFactory).thenReturn(this.mockFactory);
    }

    @Test
    void of_ValidProperties_ValidMenu() {
        BaseItem fill = BaseItem.of(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1), "filler!", List.of(), -1);
        BaseItem navigation = BaseItem.of(Material.COMPASS);
        Map<String, Item> map = Map.of("fill", fill, "navigation", navigation);
        Menu menu = BaseMenu.of(map, "The menu!", 54, true, false);
        assertNotNull(menu.items().get("fill"));
        assertNotNull(menu.items().get("navigation"));
        assertEquals(54, menu.slots());
        assertEquals("The menu!", menu.title());
        assertFalse(menu.navigation());
        assertTrue(menu.fill());
    }

    @Test
    void addExtraItems_CorrectlyModifiesItemMap() {
        BaseItem fill = BaseItem.of(new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1), "filler!", List.of(), -1);
        BaseItem navigation = BaseItem.of(Material.COMPASS);
        Map<String, Item> map = new HashMap<>();
        map.put("fill", fill);
        map.put("navigation", navigation);
        Menu menu = BaseMenu.of(map, "The menu!", 54, true, false);
        menu.addExtraItems();
        assertNull(menu.items().get("navigation"));
        for (int i = 0; i < menu.slots(); i++) {
            assertNotNull(menu.items().get("fill" + i));
        }
    }
}
