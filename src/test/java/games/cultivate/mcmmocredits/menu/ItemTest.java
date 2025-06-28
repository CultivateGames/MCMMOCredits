//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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
package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.TestUtils;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTest {
    ServerMock server = null;

    @BeforeEach
    void setUp() {
        this.server = MockBukkit.mock();
    }

    @Test
    void build_ValidProperties_ReturnsValidItem() {
        var expected = ItemStack.of(Material.STONE, 1);
        var actual = Item.builder().build().raw();
        expected.editMeta(m -> m.setCustomModelData(0));
        byte[] expectedBytes = expected.serializeAsBytes();
        byte[] actualBytes = actual.serializeAsBytes();
        assertArrayEquals(expectedBytes, actualBytes);
    }

    @Test
    void getItemFor_ValidItemUser_ReturnsParsedItem() {
        Item item = Item.builder()
                .material(Material.ACACIA_BOAT)
                .lore(List.of("<sender_redeemed> credits redeemed"))
                .name("<sender_credits> credits!")
                .slot(10)
                .build();
        CommandSourceData user = TestUtils.createUser();
        int credits = user.credits();
        int redeemed = user.redeemed();
        var playerMock = new PlayerMock(this.server, user.username(), user.uuid());
        this.server.addPlayer(playerMock);
        var itemStack = item.getItemFor(playerMock);
        var expectedLore = List.of(redeemed + " credits redeemed");
        var actualLore = itemStack.getLore();
        assertEquals(expectedLore, actualLore);
        var expectedName = credits + "credits!";
        var actualName = itemStack.displayName().toString();
        assertEquals(expectedName, actualName);
    }
}
