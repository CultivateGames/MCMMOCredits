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

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.PropertyReplacingStringProcessor;
import cloud.commandframework.arguments.parser.ParserRegistry;
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
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import games.cultivate.mcmmocredits.MCMMOCredits;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.placeholders.Resolver;
import games.cultivate.mcmmocredits.text.Text;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.UserService;
import io.leangen.geantyref.TypeToken;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class CloudCommandHandler {
    private static final Pattern TO_PATH = Pattern.compile("[.|_]");
    private final Credits commands;
    private final UserService service;
    private final MainConfig config;
    private final MCMMOCredits plugin;
    private final List<Caption> keys = List.of(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_BOOLEAN,
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
    private PaperCommandManager<CommandExecutor> manager;

    @Inject
    public CloudCommandHandler(final Credits commands, final UserService service, final MainConfig config, final MCMMOCredits plugin) {
        this.commands = commands;
        this.service = service;
        this.config = config;
        this.plugin = plugin;
    }

    /**
     * Loads the actual CommandManager. Currently only compatible with Paper.
     */
    public void load() {
        Function<CommandSender, CommandExecutor> forwardsMapper = this.service::fromSender;
        var coordinator = AsynchronousCommandExecutionCoordinator.<CommandExecutor>builder().withAsynchronousParsing().build();
        try {
            this.manager = new PaperCommandManager<>(this.plugin, coordinator, forwardsMapper, CommandExecutor::sender);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        this.manager.registerBrigadier();
        Objects.requireNonNull(this.manager.brigadierManager()).setNativeNumberSuggestions(false);
        this.manager.registerAsynchronousCompletions();
        this.loadCommandParser(this.manager);
        this.parseCommands(this.manager);
        this.registerExceptions();
    }

    /**
     * Loads the ParserRegistry using the CommandManager.
     * The CommandManager must be loaded before calling this.
     *
     * @param manager The loaded CommandManager.
     */
    private void loadCommandParser(final PaperCommandManager<CommandExecutor> manager) {
        ParserRegistry<CommandExecutor> parser = manager.parserRegistry();
        parser.registerParserSupplier(TypeToken.get(PrimarySkillType.class), x -> new SkillParser<>());
        boolean tabCompletion = this.config.getBoolean("settings", "user-tab-complete");
        parser.registerSuggestionProvider("user", (c, i) -> {
            if (tabCompletion) {
                return Bukkit.getOnlinePlayers().stream().filter(x -> !(c.getSender() instanceof Player p) || x.canSee(p)).map(Player::getName).toList();
            }
            return List.of();
        });
        List<String> menus = List.of("main", "config", "redeem");
        parser.registerSuggestionProvider("menus", (c, i) -> menus);
    }

    /**
     * Parses existing commands using an AnnotationParser, and sets the customizable command prefix.
     *
     * @param manager The loaded CommandManager.
     */
    private void parseCommands(final PaperCommandManager<CommandExecutor> manager) {
        AnnotationParser<CommandExecutor> annotationParser = new AnnotationParser<>(manager, CommandExecutor.class, p -> SimpleCommandMeta.empty());
        String commandPrefix = this.config.getString("command-prefix");
        annotationParser.stringProcessor(new PropertyReplacingStringProcessor(x -> {
            if (x.equals("command.prefix")) {
                return commandPrefix;
            }
            return x;
        }));
        annotationParser.parse(this.commands);
    }

    /**
     * Registers the Exception Handlers, and creates the Caption Registry.
     */
    private void registerExceptions() {
        this.register(InvalidSyntaxException.class, "invalid-syntax", "correct_syntax", (c, e) -> "/" + e.getCorrectSyntax());
        this.register(InvalidCommandSenderException.class, "invalid-sender", "correct_sender", (c, e) -> e.getRequiredSender().getSimpleName());
        this.register(NoPermissionException.class, "no-permission", "permission", (c, e) -> e.getMissingPermission());
        this.register(CommandExecutionException.class, "command-execution", "command_context", (c, e) -> String.valueOf(e.getCommandContext()));
        this.register(ArgumentParseException.class, "argument-parsing", "argument_error", (c, e) -> {
            String value = e.getCause().getMessage();
            return c.sender() instanceof Player p ? PlaceholderAPI.setPlaceholders(p, value) : value;
        });
        this.manager.captionVariableReplacementHandler(new CaptionFormatter());
        CaptionRegistry<CommandExecutor> registry = this.manager.captionRegistry();
        if (registry instanceof FactoryDelegatingCaptionRegistry<CommandExecutor> factory) {
            this.keys.forEach(x -> factory.registerMessageFactory(x, (c, e) -> this.config.getString(TO_PATH.matcher(x.getKey()).replaceAll("-"))));
            this.manager.captionRegistry(factory);
        }
    }

    /**
     * Registers the Exception in the CommandManager.
     *
     * @param ex    The class of the exception.
     * @param path  The config path for the message to send to the user.
     * @param key   key of placeholder to add to Resolver.
     * @param value function that calculates the value of the placeholder.
     * @param <E>   The exception.
     */
    private <E extends Exception> void register(final Class<E> ex, final String path, final String key, final BiFunction<CommandExecutor, E, String> value) {
        this.manager.registerExceptionHandler(ex, (c, e) -> {
            Resolver resolver = Resolver.ofUser(c);
            resolver.addStringTag(key, value.apply(c, e));
            Text.fromString(c, this.config.getMessage(path), resolver).send();
        });
    }

    /**
     * Formatter of Cloud captions that uses {@literal <>} instead of {} for consistency.
     */
    private static final class CaptionFormatter implements CaptionVariableReplacementHandler {
        @Override
        @SuppressWarnings("checkstyle:finalparameters")
        public @NotNull String replaceVariables(@NotNull String string, @NotNull final CaptionVariable... variables) {
            for (final CaptionVariable variable : variables) {
                string = string.replace(String.format("<%s>", variable.getKey()), variable.getValue());
            }
            return string;
        }
    }
}
