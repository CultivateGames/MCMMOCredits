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
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.captions.StandardCaptionKeys;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.util.Util;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.List;
import java.util.Queue;

/**
 * Argument Parser for MCMMO skill types.
 */
public final class SkillParser implements ArgumentParser<CommandExecutor, PrimarySkillType> {
    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull ArgumentParseResult<PrimarySkillType> parse(@NotNull final CommandContext<CommandExecutor> commandContext, final Queue<String> inputQueue) {
        String input = inputQueue.peek();
        if (input == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(SkillParser.class, commandContext));
        }
        if (Util.getSkillNames().contains(input.toLowerCase())) {
            inputQueue.remove();
            return ArgumentParseResult.success(PrimarySkillType.valueOf(input.toUpperCase()));
        }
        return ArgumentParseResult.failure(new SkillParseException(input, commandContext));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull List<String> suggestions(@NotNull final CommandContext<CommandExecutor> commandContext, @NotNull final String input) {
        return Util.getSkillNames();
    }

    /**
     * Exception thrown when an unknown result is parsed.
     */
    @SuppressWarnings("java:S110")
    public static final class SkillParseException extends ParserException {
        @Serial
        private static final long serialVersionUID = 3489324098342876342L;

        /**
         * Constructs the object.
         *
         * @param input   Current input of the command.
         * @param context Current context of the command.
         */
        public SkillParseException(final String input, final CommandContext<?> context) {
            super(SkillParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM, CaptionVariable.of("input", input), CaptionVariable.of("acceptableValues", Util.joinString(",", Util.getSkillNames())));
        }
    }
}
