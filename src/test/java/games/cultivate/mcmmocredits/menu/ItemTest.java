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
package games.cultivate.mcmmocredits.menu;

import games.cultivate.mcmmocredits.placeholders.Resolver;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

//TODO: test context and applyProperties
class ItemTest {
    private Resolver resolver;
    private ContextFactory contextFactory;

    @BeforeEach
    void setUp() {
        this.resolver = new Resolver();
        this.contextFactory = mock(ContextFactory.class);
    }

    @Test
    void buildItem_ItemBuilder_ValidItem() {
        //Arrange
        ClickType clickType = ClickType.COMMAND;
        ItemStack stack = new ItemStack(Material.DIAMOND, 1);
        String name = "Test Item";
        List<String> lore = List.of("Test lore line 1", "Test lore line 2");
        int slot = 5;
        String data = "example_data";

        //Act
        Item item = Item.builder()
                .type(clickType)
                .item(stack)
                .name(name)
                .lore(lore)
                .slot(slot)
                .data(data)
                .build();

        //Assert
        assertNotNull(item);
        assertEquals(clickType, item.clickType());
        assertEquals(stack, item.stack());
        assertEquals(name, item.name());
        assertEquals(lore, item.lore());
        assertEquals(slot, item.slot());
        assertEquals(data, item.data());
    }

    @Test
    void changeClickType_InitialItem_UpdatedClickType() {
        //Arrange
        Item item = Item.builder().build();
        ClickType newClickType = ClickType.COMMAND;

        //Act
        Item updatedItem = item.withClickType(newClickType);

        //Assert
        assertEquals(newClickType, updatedItem.clickType());
    }

    @Test
    void changeStack_InitialItem_UpdatedStack() {
        //Arrange
        Item item = Item.builder().build();
        ItemStack newStack = new ItemStack(Material.EMERALD, 1);

        //Act
        Item updatedItem = item.withStack(newStack);

        //Assert
        assertEquals(newStack, updatedItem.stack());
    }

    @Test
    void changeName_InitialItem_UpdatedName() {
        //Arrange
        Item item = Item.builder().build();
        String newName = "New Name";

        //Act
        Item updatedItem = item.withName(newName);

        //Assert
        assertEquals(newName, updatedItem.name());
    }

    @Test
    void changeLore_InitialItem_UpdatedLore() {
        //Arrange
        Item item = Item.builder().build();
        List<String> newLore = List.of("New lore line 1", "New lore line 2");

        //Act
        Item updatedItem = item.withLore(newLore);

        //Assert
        assertEquals(newLore, updatedItem.lore());
    }

    @Test
    void changeData_InitialItem_UpdatedData() {
        //Arrange
        Item item = Item.builder().build();
        String newData = "new_data";

        //Act
        Item updatedItem = item.withData(newData);

        //Assert
        assertEquals(newData, updatedItem.data());
    }

    @Test
    void changeSlot_InitialItem_UpdatedSlot() {
        //Arrange
        Item item = Item.builder().build();
        int newSlot = 10;

        //Act
        Item updatedItem = item.withSlot(newSlot);

        //Assert
        assertEquals(newSlot, updatedItem.slot());
    }


    @Test
    void getClickType_InitialItem_CorrectClickType() {
        //Arrange
        ClickType clickType = ClickType.COMMAND;
        Item item = Item.builder().type(clickType).build();

        //Act & Assert
        assertEquals(clickType, item.clickType());
    }

    @Test
    void getStack_InitialItem_CorrectStack() {
        //Arrange
        ItemStack stack = new ItemStack(Material.DIAMOND, 1);
        Item item = Item.builder().item(stack).build();

        //Act & Assert
        assertEquals(stack, item.stack());
    }

    @Test
    void getName_InitialItem_CorrectName() {
        //Arrange
        String name = "Test Item";
        Item item = Item.builder().name(name).build();

        //Act & Assert
        assertEquals(name, item.name());
    }

    @Test
    void getLore_InitialItem_CorrectLore() {
        //Arrange
        List<String> lore = List.of("Test lore line 1", "Test lore line 2");
        Item item = Item.builder().lore(lore).build();

        //Act & Assert
        assertEquals(lore, item.lore());
    }

    @Test
    void getSlot_InitialItem_CorrectSlot() {
        //Arrange
        int slot = 5;
        Item item = Item.builder().slot(slot).build();

        //Act & Assert
        assertEquals(slot, item.slot());
    }

    @Test
    void getData_InitialItem_CorrectData() {
        //Arrange
        String data = "example_data";
        Item item = Item.builder().data(data).build();

        //Act & Assert
        assertEquals(data, item.data());
    }
}
