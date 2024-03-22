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
package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.bukkit.BukkitCaptionKeys;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

/**
 * Argument Parser for Users.
 */
public final class UserParser implements ArgumentParser<CommandExecutor, User> {
    private final UserService service;
    private final boolean tabCompletion;

    /**
     * Constructs the object.
     *
     * @param service       The UserService for user lookup.
     * @param tabCompletion Whether the command will have tab completion.
     */
    public UserParser(final UserService service, final boolean tabCompletion) {
        this.service = service;
        this.tabCompletion = tabCompletion;
    }

    @Override
    public @NotNull ArgumentParseResult<User> parse(@NotNull final CommandContext<CommandExecutor> commandContext, final Queue<String> inputQueue) {
        String input = inputQueue.peek();
        if (input == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(UserParser.class, commandContext));
        }
        //No choice but to block (parsing occurs in an async context, should be okay!).
        Optional<User> user = this.service.getUser(input);
        if (user.isPresent()) {
            inputQueue.remove();
            return ArgumentParseResult.success(user.get());
        }
        return ArgumentParseResult.failure(new UserNotFoundException(input, commandContext));
    }

    @Override
    public @NotNull List<String> suggestions(@NotNull final CommandContext<CommandExecutor> commandContext, @NotNull final String input) {
        //Method is frequently accessed, so we attempt to return fast.
        if (!this.tabCompletion) {
            return List.of();
        }
        CommandExecutor executor = commandContext.getSender();
        if (executor.sender() instanceof Player player) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!player.canSee(p)) {
                    continue;
                }
                names.add(p.getName());
            }
            return names;

        }
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }

    /**
     * Exception thrown when the resulting User is not found via the parser.
     */
    @SuppressWarnings("java:S110")
    public static final class UserNotFoundException extends ParserException {
        @Serial
        private static final long serialVersionUID = 8518936438572307497L;

        /**
         * Constructs the object.
         *
         * @param input   Current input of the command.
         * @param context Current context of the command.
         */
        public UserNotFoundException(final String input, final CommandContext<?> context) {
            super(UserParser.class, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER, CaptionVariable.of("input", input));
        }
    }
}
