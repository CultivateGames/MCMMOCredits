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

import games.cultivate.mcmmocredits.util.Util;

import java.util.Arrays;
import java.util.List;

/**
 * Represents all possible Item obligations inside a {@link Menu}
 */
public enum ItemType {
    MAIN_MENU("navigation"),
    CONFIG_MENU("config"),
    REDEEM_MENU("redemption"),
    EDIT_MESSAGE("messages"),
    EDIT_SETTING("settings"),
    FILL("fill"),
    REDEEM(Util.getSkillNames());

    private final List<String> configSections;

    /**
     * Constructs the ItemType.
     *
     * @param configSections list of node paths that may contain the ItemType.
     */
    ItemType(final List<String> configSections) {
        this.configSections = configSections;
    }

    /**
     * Constructs the ItemType.
     *
     * @param configSections node path that contains the ItemType.
     */
    ItemType(final String configSections) {
        this.configSections = List.of(configSections);
    }

    /**
     * Finds the correct ItemType based on the provided String.
     *
     * @param data String representing a node path that correlates to an ItemType.
     * @return The matching ItemType, or FILL.
     */
    public static ItemType value(final String data) {
        return Arrays.stream(ItemType.values()).filter(x -> x.contains(data)).findAny().orElse(ItemType.FILL);
    }

    /**
     * Returns if the ItemType's data matches the provided String.
     *
     * @param data the provided String.
     * @return if the ItemType contains the String.
     */
    public boolean contains(final String data) {
        return this.configSections.contains(data);
    }
}
