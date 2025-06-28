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
package games.cultivate.mcmmocredits.command;

import games.cultivate.mcmmocredits.user.CommandSource;
import games.cultivate.mcmmocredits.user.CommandSourceData;
import games.cultivate.mcmmocredits.user.UserCommandSource;
import games.cultivate.mcmmocredits.user.UserService;
import io.leangen.geantyref.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.bukkit.parser.PlayerParser.PlayerParseException;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class UserParser implements ParserDescriptor<CommandSource, CommandSourceData>, Strings<CommandSource> {
    private final UserService userService;
    private final boolean tabCompletion;

    public UserParser(final UserService userService, final boolean tabCompletion) {
        this.userService = userService;
        this.tabCompletion = tabCompletion;
    }

    @Override
    public @NotNull ArgumentParser<CommandSource, CommandSourceData> parser() {
        return (ctx, i) -> {
            String part = i.readString();
            if (ctx.sender() instanceof UserCommandSource ucs && ucs.data().username().equalsIgnoreCase(part)) {
                return ArgumentParseResult.success(ucs.data());
            }
            return this.userService.getUser(part)
                    .map(ArgumentParseResult::success)
                    .orElseGet(() -> ArgumentParseResult.failure(new PlayerParseException(part, ctx)));
        };
    }

    @Override
    public @NotNull TypeToken<CommandSourceData> valueType() {
        return TypeToken.get(CommandSourceData.class);
    }

    @Override
    public @NotNull Iterable<String> stringSuggestions(final @NotNull CommandContext<CommandSource> context, final @NotNull CommandInput input) {
        if (!this.tabCompletion) {
            return List.of();
        }
        if (context.sender().stack() instanceof Player p) {
            return Bukkit.getOnlinePlayers().stream().filter(p::canSee).map(Player::getName).toList();
        }
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}
