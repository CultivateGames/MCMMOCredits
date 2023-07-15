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
import cloud.commandframework.annotations.injection.GuiceInjectionService;
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
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserService;
import io.leangen.geantyref.TypeToken;
import jakarta.inject.Inject;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Loads the Command system.
 */
public final class CommandHandler {
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

    /**
     * Constructs the object.
     *
     * @param commands Command class used for registration.
     * @param service  UserService to get CommandExecutor instances from cache.
     * @param config   MainConfig to load relevant settings.
     * @param plugin   Plugin instance to register the CommandManager.
     */
    @Inject
    public CommandHandler(final Credits commands, final UserService service, final MainConfig config, final MCMMOCredits plugin) {
        this.commands = commands;
        this.service = service;
        this.config = config;
        this.plugin = plugin;
    }

    /**
     * Loads the CommandManager.
     *
     * @param injectionService An injection service. Used to inject into commands.
     */
    public void load(final GuiceInjectionService<CommandExecutor> injectionService) {
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
        this.manager.parameterInjectorRegistry().registerInjectionService(injectionService);
        this.loadCommandParser();
        this.parseCommands();
        this.registerExceptions();
    }

    /**
     * Loads the ParserRegistry using the CommandManager.
     * <p>
     * The CommandManager must be loaded before calling this.
     */
    private void loadCommandParser() {
        ParserRegistry<CommandExecutor> parser = this.manager.parserRegistry();
        parser.registerParserSupplier(TypeToken.get(PrimarySkillType.class), x -> new SkillParser<>());
        parser.registerParserSupplier(TypeToken.get(User.class), x -> new UserParser<>(this.service, this.config.getBoolean("settings", "user-tab-complete")));
        List<String> menus = List.of("main", "config", "redeem");
        parser.registerSuggestionProvider("menus", (c, i) -> menus);
    }

    /**
     * Parses existing commands using an AnnotationParser. Sets the customizable command prefix.
     * <p>
     * The CommandManager must be loaded before calling this.
     */
    private void parseCommands() {
        AnnotationParser<CommandExecutor> annotationParser = new AnnotationParser<>(this.manager, CommandExecutor.class, p -> SimpleCommandMeta.empty());
        String commandPrefix = this.config.getString("command-prefix");
        annotationParser.stringProcessor(new PropertyReplacingStringProcessor(x -> x.equals("command.prefix") ? commandPrefix : x));
        annotationParser.parse(this.commands);
    }

    /**
     * Registers the Exception Handlers, and creates the Caption Registry.
     * <p>
     * The CommandManager must be loaded before calling this.
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
     * Registers an Exception to the CommandManager.
     *
     * @param ex    The class of the exception.
     * @param path  The config path of the message sent to user.
     * @param key   Placeholder key. Used with {@literal <>} formatting.
     * @param value Function to calculate placeholder value.
     * @param <E>   The Exception type. Differs per registration.
     */
    private <E extends Exception> void register(final Class<E> ex, final String path, final String key, final BiFunction<CommandExecutor, E, String> value) {
        this.manager.registerExceptionHandler(ex, (c, e) -> c.sendText(this.config.getMessage(path), r -> r.addTag(key, value.apply(c, e))));
    }

    /**
     * Formatter of captions that uses {@literal <>} instead of {}.
     */
    private static final class CaptionFormatter implements CaptionVariableReplacementHandler {
        @Override
        public @NotNull String replaceVariables(@NotNull final String string, @NotNull final CaptionVariable... variables) {
            String replacement = string;
            for (final CaptionVariable variable : variables) {
                replacement = replacement.replace(String.format("<%s>", variable.getKey()), variable.getValue());
            }
            return replacement;
        }
    }
}
