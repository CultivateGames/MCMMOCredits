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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

//TODO: edge cases
@ExtendWith(MockitoExtension.class)
class ItemSerializerTest {
    private ConfigurationNode node;
    @Mock
    private MockedStatic<Bukkit> mockBukkit;
    @Mock
    private ItemFactory mockFactory;
    @Mock
    private ItemMeta mockMeta;

    @BeforeEach
    void setUp() {
        this.node = BasicConfigurationNode.root(ConfigurationOptions.defaults().serializers(b -> b.register(Item.class, ItemSerializer.INSTANCE)));
        this.mockBukkit.when(Bukkit::getItemFactory).thenReturn(this.mockFactory);
        when(this.mockFactory.getItemMeta(Material.STONE)).thenReturn(this.mockMeta);
        when(this.mockMeta.getCustomModelData()).thenReturn(1);
    }

    @Test
    void deserialize_ValidConfigNode_ReturnsCorrectItem() throws SerializationException {
        ConfigurationNode itemNode = this.node.node("item");
        itemNode.node("name").set("test item!");
        itemNode.node("lore").setList(String.class, List.of("the lore."));
        itemNode.node("slot").set(5);
        itemNode.node("material").set(Material.STONE);
        itemNode.node("amount").set(1);
        itemNode.node("custom-model-data").set(int.class, 1);
        itemNode.node("texture").set("");
        itemNode.node("glow").set(false);
        Item item = ItemSerializer.INSTANCE.deserialize(Item.class, itemNode);
        assertEquals("test item!", item.name());
        assertEquals(List.of("the lore."), item.lore());
        assertEquals(1, item.stack().getItemMeta().getCustomModelData());
        assertEquals(5, item.slot());
        assertEquals(1, item.stack().getAmount());
        assertTrue(item.stack().getEnchantments().isEmpty());
        assertEquals(Material.STONE, item.stack().getType());
    }

    @Test
    void serialize_ValidItem_ReturnsCorrectNode() throws SerializationException {
        ConfigurationNode itemNode = this.node.node("item");
        ItemStack stack = new ItemStack(Material.STONE, 1);
        stack.editMeta(m -> m.setCustomModelData(1));
        Item item = BaseItem.of(stack, "test item!", List.of("the lore."), 5);
        ItemSerializer.INSTANCE.serialize(Item.class, item, itemNode);
        assertEquals("test item!", itemNode.node("name").getString());
        assertEquals(List.of("the lore."), itemNode.node("lore").getList(String.class, List.of()));
        assertEquals(1, itemNode.node("custom-model-data").getInt());
        assertEquals(5, itemNode.node("slot").getInt());
        assertEquals(1, itemNode.node("amount").getInt());
        assertFalse(itemNode.node("glow").getBoolean());
        assertEquals(Material.STONE, itemNode.node("material").get(Material.class));
    }
}
