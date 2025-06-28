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
package games.cultivate.mcmmocredits.user;

import io.papermc.paper.command.brigadier.CommandSourceStack;

/**
 * Something that sends commands.
 */
@SuppressWarnings("UnstableApiUsage")
public sealed interface CommandSource permits UserCommandSource, ConsoleCommandSource {
    /**
     * Gets an instance of the console as a CommandSource.
     *
     * @param stack The CommandSourceStack.
     * @return An instance of the console.
     */
    static CommandSource console(final CommandSourceStack stack) {
        return new ConsoleCommandSource(stack);
    }

    /**
     * Returns the object as a Paper-native {@linkplain CommandSourceStack}
     *
     * @return The {@linkplain CommandSourceStack}
     */
    CommandSourceStack stack();

    /**
     * Gets the underlying data of the object.
     *
     * @return The object's data.
     */
    CommandSourceData data();

    default boolean isConsole() {
        return false;
    }
}
