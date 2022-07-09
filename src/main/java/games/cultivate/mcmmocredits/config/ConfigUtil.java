package games.cultivate.mcmmocredits.config;

import games.cultivate.mcmmocredits.keys.Key;

import java.util.HashMap;
import java.util.Map;

public class ConfigUtil {
    public static final Config<MessagesConfig> MESSAGES = new MessagesConfig();
    public static final Config<SettingsConfig> SETTINGS = new SettingsConfig();
    public static final Config<MenuConfig> MENU = new MenuConfig();

    private static final Map<Key<?>, Config<?>> keyMap = new HashMap<>();

    private ConfigUtil(){}

    public static void loadAllConfigs() {
        MESSAGES.load("messages.conf");
        SETTINGS.load("settings.conf");
        MENU.load("menus.conf");
        MESSAGES.keys().forEach(i -> keyMap.put(i, MESSAGES));
        SETTINGS.keys().forEach(i -> keyMap.put(i, SETTINGS));
        MENU.keys().forEach(i -> keyMap.put(i, MENU));
    }

    public static void saveAllConfigs() {
        MESSAGES.save();
        SETTINGS.save();
        MENU.save();
    }

    //FIXME less than ideal solution here.
    public static Config<?> fromPath(String path) {
        for (Map.Entry<Key<?>, Config<?>> entry : keyMap.entrySet()) {
            if (entry.getKey().path().equals(path)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
