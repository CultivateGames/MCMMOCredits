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

import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserService;
import io.leangen.geantyref.TypeToken;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class UserParser implements ParserDescriptor<CommandExecutor, User>, BlockingSuggestionProvider.Strings<CommandExecutor> {
    private final UserService userService;
    private final boolean tabCompletion;

    @Inject
    public UserParser(final UserService userService, boolean tabCompletion) {
        this.userService = userService;
        this.tabCompletion = tabCompletion;
    }

    @Override
    public @NotNull ArgumentParser<CommandExecutor, User> parser() {
        return (context, input) -> {
            String part = input.readString();
            if (context.sender() instanceof User contextUser && contextUser.username().equalsIgnoreCase(part)) {
                return ArgumentParseResult.success(contextUser);
            }
            return this.userService.getUser(part)
                    .map(ArgumentParseResult::success)
                    .orElseGet(() -> ArgumentParseResult.failure(new PlayerParser.PlayerParseException(part, context)));
        };
    }

    @Override
    public @NotNull TypeToken<User> valueType() {
        return TypeToken.get(User.class);
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NotNull final CommandContext<CommandExecutor> context, @NonNull final CommandInput input) {
        if (!this.tabCompletion) {
            return List.of();
        }
        if (context.sender().sender() instanceof Player p) {
            return Bukkit.getOnlinePlayers().stream().filter(p::canSee).map(Player::getName).toList();
        }
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }
}
