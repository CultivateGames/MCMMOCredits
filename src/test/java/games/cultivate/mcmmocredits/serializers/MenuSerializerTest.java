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

import games.cultivate.mcmmocredits.menu.Item;
import games.cultivate.mcmmocredits.menu.RedeemMenu;
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
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
        ConfigurationOptions opts = ConfigurationOptions.defaults().serializers(b -> b.register(Item.class, ItemSerializer.INSTANCE).register(RedeemMenu.class, MenuSerializer.INSTANCE));
        this.node = BasicConfigurationNode.root(opts);
        this.mockBukkit.when(Bukkit::getItemFactory).thenReturn(this.mockFactory);
        when(this.mockFactory.getItemMeta(any(Material.class))).thenReturn(this.mockMeta);
        when(this.mockMeta.hasCustomModelData()).thenReturn(true);
        when(this.mockMeta.getCustomModelData()).thenReturn(1);
    }

    @Test
    void deserialize_ValidConfigNode_ReturnsCorrectItem() throws SerializationException {
        when(this.mockFactory.getItemMeta(Material.STONE)).thenReturn(this.mockMeta);
        ConfigurationNode menuNode = this.node.node("menu");
        menuNode.node("title").set("Menu Title!");
        menuNode.node("slots").set(54);
        menuNode.node("fill").set(true);
        menuNode.node("navigation").set(true);
        ConfigurationNode itemNode = this.node.node("menu", "items");
        Item fill = Item.of(Material.STONE, "fill item!", List.of("the lore."), 0);
        Item navigation = Item.of(Material.STONE, "navigation item!", List.of("the lore."), 0);
        ItemSerializer.INSTANCE.serialize(Item.class, fill, itemNode.node("fill"));
        ItemSerializer.INSTANCE.serialize(Item.class, navigation, itemNode.node("navigation"));
        RedeemMenu menu = MenuSerializer.INSTANCE.deserialize(RedeemMenu.class, menuNode);
        assertEquals("Menu Title!", menu.title());
        assertEquals(54, menu.slots());
        assertEquals("fill item!", menu.items().get("fill1").name());
        assertEquals("navigation item!", menu.items().get("navigation").name());
    }

    @Test
    void serialize_ValidMenu_ReturnsCorrectNode() throws SerializationException {
        Item fill = Item.of(Material.STONE, "fill item!", List.of("the lore."), 0);
        Item navigation = Item.of(Material.STONE, "navigation item!", List.of("the lore."), 0);
        RedeemMenu menu = new RedeemMenu(Map.of("fill", fill, "navigation", navigation), "Menu title!", 9, false, true);
        ConfigurationNode menuNode = this.node.node("menu");
        MenuSerializer.INSTANCE.serialize(Menu.class, menu, menuNode);
        assertEquals("Menu title!", menuNode.node("title").getString());
        assertEquals(9, menuNode.node("slots").getInt());
        assertFalse(menuNode.node("fill").getBoolean());
        assertTrue(menuNode.node("navigation").getBoolean());
        assertEquals("fill item!", menuNode.node("items", "fill").get(Item.class).name());
        assertEquals("navigation item!", menuNode.node("items", "navigation").get(Item.class).name());
    }
}
