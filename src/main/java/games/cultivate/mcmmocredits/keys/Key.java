package games.cultivate.mcmmocredits.keys;

import games.cultivate.mcmmocredits.config.Config;
import org.jetbrains.annotations.NotNull;

public interface Key<T> {
    @NotNull String path();
    @NotNull T get();
    @NotNull Config<?> config();
}
