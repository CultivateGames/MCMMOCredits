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
package games.cultivate.mcmmocredits.ui.item;

import games.cultivate.mcmmocredits.user.User;
import net.kyori.adventure.text.Component;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//TODO: expand item tests
@ExtendWith(MockitoExtension.class)
class BaseItemTest {
    @Mock
    private MockedStatic<Bukkit> mockBukkit;
    @Mock
    private ItemFactory mockFactory;

    @BeforeEach
    void setUp() {
        this.mockBukkit.when(Bukkit::getItemFactory).thenReturn(this.mockFactory);
    }

    @Test
    void of_ValidProperties_ReturnsValidItem() {
        ItemStack stack = new ItemStack(Material.ACACIA_BOAT, 20);
        BaseItem item = BaseItem.of(stack, "The item!", List.of("the lore!"), 10);
        assertEquals(Material.ACACIA_BOAT, item.stack().getType());
        assertEquals(20, item.stack().getAmount());
        assertEquals("The item!", item.name());
        assertEquals(List.of("the lore!"), item.lore());
        assertEquals(10, item.slot());
    }

    @Test
    void materialOf_ValidProperties_ReturnsValidItem() {
        BaseItem item = BaseItem.of(Material.ACACIA_BOAT);
        assertEquals(Material.ACACIA_BOAT, item.stack().getType());
        assertEquals(1, item.stack().getAmount());
        assertEquals("", item.name());
        assertEquals(List.of(), item.lore());
        assertEquals(-1, item.slot());
    }

    @Test
    void parseUser_ValidItemUser_ReturnsParsedItem() {
        ItemStack stack = new ItemStack(Material.ACACIA_BOAT, 20);
        BaseItem item = BaseItem.of(stack, "<sender_credits> credits!", List.of("<sender_redeemed> credits redeemed"), 10);
        User user = new User(UUID.randomUUID(), "testUser", 1500, 10);
        ItemMeta meta = mock(ItemMeta.class);
        when(this.mockFactory.getItemMeta(Material.ACACIA_BOAT)).thenReturn(meta);
        item.parseUser(user);
        verify(meta, atLeastOnce()).displayName(any(Component.class));
        verify(meta, atLeastOnce()).lore(anyList());
    }
}
