//
// MIT License
//
// Copyright (c) 2022 Alexander Söderberg & Contributors
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

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.BukkitCaptionKeys;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionRegistry;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.captions.CaptionVariableReplacementHandler;
import cloud.commandframework.captions.FactoryDelegatingCaptionRegistry;
import cloud.commandframework.captions.StandardCaptionKeys;
import cloud.commandframework.exceptions.ArgumentParseException;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Exception handler that modifies how Cloud exceptions send messages.
 * @see Credits
 */
public final class CloudExceptionHandler {
    private final MainConfig config;
    private final CommandManager<CommandExecutor> manager;
    private final List<Caption> keys;

    /**
     * Constructs the object.
     *
     * @param config Injected instance of MainConfig. Used to grab exception messages.
     * @param manager The Command Manager. Registers exception handlers and modifies the caption registry.
     */
    public CloudExceptionHandler(final MainConfig config, final CommandManager<CommandExecutor> manager) {
        this.config = config;
        this.manager = manager;
        this.keys = List.of(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_BOOLEAN,
                StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_NO_INPUT_PROVIDED,
                StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_BOOLEAN,
                StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_NUMBER,
                StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_STRING,
                StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM,
                StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_UNKNOWN_FLAG,
                StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_DUPLICATE_FLAG,
                StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_NO_FLAG_STARTED,
                StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_FLAG_MISSING_ARGUMENT,
                BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER);
    }

    /**
     * Registers the Exception Handlers, and creates the Caption Registry.
     */
    public void apply() {
        this.manager.registerExceptionHandler(InvalidSyntaxException.class, (c, e) -> this.act(c, "invalid-syntax", "correct_syntax", "/" + e.getCorrectSyntax()));
        this.manager.registerExceptionHandler(InvalidCommandSenderException.class, (c, e) -> this.act(c, "invalid-sender", "correct_sender", e.getRequiredSender().getSimpleName()));
        this.manager.registerExceptionHandler(NoPermissionException.class, (c, e) -> this.act(c, "no-permission", "permission", e.getMissingPermission()));
        this.manager.registerExceptionHandler(CommandExecutionException.class, (c, e) -> this.act(c, "command-execution", "command_context", String.valueOf(e.getCommandContext())));
        this.manager.registerExceptionHandler(ArgumentParseException.class, (c, e) -> Text.fromString(c, this.config.string("argument-parsing"), this.attachParseException(c, e)).send());
        this.manager.captionVariableReplacementHandler(new CaptionFormatter());
        CaptionRegistry<CommandExecutor> registry = this.manager.captionRegistry();
        if (registry instanceof FactoryDelegatingCaptionRegistry<CommandExecutor> factory) {
            this.keys.forEach(caption -> {
                String path = caption.getKey().replaceAll("[.|_]", "-");
                factory.registerMessageFactory(caption, (capt, executor) -> this.config.node(path).getString());
            });
            this.manager.captionRegistry(factory);
        }
    }

    private void act(final CommandExecutor executor, final String path, final String key, final String value) {
        Resolver resolver = this.attachException(executor, key, value);
        Text.fromString(executor, this.config.string(path), resolver).send();
    }

    private Resolver attachException(final CommandExecutor executor, final String key, final String value) {
        return executor.resolver().toBuilder().tag(key, value).build();
    }

    private Resolver attachParseException(final CommandExecutor executor, final ArgumentParseException ex) {
        String value = ex.getCause().getMessage();
        if (executor instanceof Player player) {
            value = PlaceholderAPI.setPlaceholders(player, value);
        }
        return this.attachException(executor, "argument_error", value);
    }

    private static final class CaptionFormatter implements CaptionVariableReplacementHandler {
        @Override
        public @NotNull String replaceVariables(final @NotNull String string, @NotNull final CaptionVariable... variables) {
            String replacedString = string;
            for (final CaptionVariable variable : variables) {
                replacedString = replacedString.replace(String.format("<%s>", variable.getKey()), variable.getValue());
            }
            return replacedString;
        }
    }
}
