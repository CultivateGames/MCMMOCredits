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
package games.cultivate.mcmmocredits.serializers;

import games.cultivate.mcmmocredits.ui.item.BaseItem;
import games.cultivate.mcmmocredits.ui.item.Item;
import games.cultivate.mcmmocredits.ui.menu.BaseMenu;
import games.cultivate.mcmmocredits.ui.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

//TODO: edge cases
@ExtendWith(MockitoExtension.class)
class MenuSerializerTest {
    private ConfigurationNode node;
    @Mock
    private MockedStatic<Bukkit> mockBukkit;
    @Mock
    private ItemFactory mockFactory;
    @Mock
    private ItemMeta mockMeta;

    @BeforeEach
    void setUp() {
        ConfigurationOptions opts = ConfigurationOptions.defaults().serializers(b -> b.register(Item.class, ItemSerializer.INSTANCE).register(Menu.class, MenuSerializer.INSTANCE));
        this.node = BasicConfigurationNode.root(opts);
        this.mockBukkit.when(Bukkit::getItemFactory).thenReturn(this.mockFactory);
    }

    @Test
    void deserialize_ValidConfigNode_ReturnsCorrectItem() throws SerializationException {
        when(this.mockFactory.getItemMeta(Material.STONE)).thenReturn(this.mockMeta);
        ConfigurationNode menuNode = this.node.node("menu");
        menuNode.node("title").set("Menu Title!");
        menuNode.node("slots").set(54);
        menuNode.node("fill").set(false);
        menuNode.node("navigation").set(true);
        ConfigurationNode itemNode = this.node.node("menu", "items");
        BaseItem fill = BaseItem.of(new ItemStack(Material.STONE, 1), "fill item!", List.of("the lore."), -1);
        BaseItem navigation = BaseItem.of(new ItemStack(Material.STONE, 1), "navigation item!", List.of("the lore."), -1);
        ItemSerializer.INSTANCE.serialize(Item.class, fill, itemNode.node("fill"));
        ItemSerializer.INSTANCE.serialize(Item.class, navigation, itemNode.node("navigation"));
        Menu menu = MenuSerializer.INSTANCE.deserialize(Menu.class, menuNode);
        assertEquals("Menu Title!", menu.title());
        assertEquals(54, menu.slots());
        assertFalse(menu.fill());
        assertTrue(menu.navigation());
        assertEquals("fill item!", menu.items().get("fill").name());
        assertEquals("navigation item!", menu.items().get("navigation").name());
    }

    @Test
    void serialize_ThrowsException() {
        Menu menu = BaseMenu.of(new HashMap<>(), "Menu title!", 9, false, true);
        ConfigurationNode menuNode = this.node.node("menu");
        assertThrows(UnsupportedOperationException.class, () -> MenuSerializer.INSTANCE.serialize(Menu.class, menu, menuNode));
    }
}
