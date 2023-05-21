package games.cultivate.mcmmocredits.config;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;

@ConfigSerializable
public interface Config {
    /**
     * Loads the configuration. Supports reloading.
     */
    void load(Path path, String fileName);

    /**
     * Saves the configuration using the current root node.
     */
    void save();

    /**
     * Modifies the configuration.
     *
     * @param value The value to apply.
     * @param path  location where the value will be set.
     * @param <T>   Type of the value.
     * @return returns true if successful, false otherwise.
     */
    <T> boolean set(@NotNull T value, Object... path);

    /**
     * Gets a value from the configuration.
     *
     * @param type Value type.
     * @param def  Default value if missing.
     * @param path location where the value will be obtained.
     * @param <T>  Type of the value.
     * @return The value, or the default if the value is null.
     */
    <T> T get(Class<T> type, T def, Object... path);
}
