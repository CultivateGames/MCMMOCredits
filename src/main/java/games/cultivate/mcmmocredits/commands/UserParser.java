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
package games.cultivate.mcmmocredits.commands;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.bukkit.BukkitCaptionKeys;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

/**
 * Argument Parser for Users
 *
 * @param <C> CommandExecutor.
 */
public final class UserParser<C> implements ArgumentParser<C, User> {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ArgumentParseResult<User> parse(@NotNull final CommandContext<C> commandContext, final Queue<String> inputQueue) {
        String input = inputQueue.peek();
        if (input == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(UserParser.class, commandContext));
        }
        //TODO: better optional usage?
        Optional<User> user = this.service.getUser(input);
        if (user.isPresent()) {
            inputQueue.remove();
            return ArgumentParseResult.success(user.get());
        }
        return ArgumentParseResult.failure(new UserNotFoundException(input, commandContext));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull List<String> suggestions(@NotNull final CommandContext<C> commandContext, @NotNull final String input) {
        return this.tabCompletion ? Bukkit.getOnlinePlayers().stream().filter(x -> !(commandContext.getSender() instanceof Player p) || x.canSee(p)).map(Player::getName).toList() : List.of();
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