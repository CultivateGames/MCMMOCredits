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
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.captions.StandardCaptionKeys;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.skills.SkillTools;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.List;
import java.util.Queue;

/**
 * Argument Parser for MCMMO Skills to filter out child skills universally.
 *
 * @param <C> The command sender. In this case, will always be {@link CommandExecutor}.
 */
public final class SkillParser<C> implements ArgumentParser<C, PrimarySkillType> {

    @Override
    public @NotNull ArgumentParseResult<PrimarySkillType> parse(@NotNull final CommandContext<C> commandContext, final Queue<String> inputQueue) {
        String input = inputQueue.peek();
        if (input == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(SkillParser.class, commandContext));
        }
        try {
            PrimarySkillType skill = PrimarySkillType.valueOf(input.toUpperCase());
            inputQueue.remove();
            if (SkillTools.NON_CHILD_SKILLS.contains(skill)) {
                return ArgumentParseResult.success(skill);
            }
            return ArgumentParseResult.failure(new SkillParseException(input, commandContext));
        } catch (final IllegalArgumentException e) {
            return ArgumentParseResult.failure(new SkillParseException(input, commandContext));
        }
    }

    @Override
    public @NotNull List<String> suggestions(@NotNull final CommandContext<C> commandContext, @NotNull final String input) {
        return Util.getSkillNames();
    }

    /**
     * Parser Exception thrown when Skills are "child skills".
     *
     * @see Util#getSkillNames()
     */
    @SuppressWarnings("java:S110")
    public static final class SkillParseException extends ParserException {
        @Serial
        private static final long serialVersionUID = 3489324098342876342L;

        public SkillParseException(final String input, final CommandContext<?> context) {
            super(SkillParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM, CaptionVariable.of("input", input), CaptionVariable.of("acceptableValues", StringUtils.join(Util.getSkillNames(), ", ")));
        }
    }
}
