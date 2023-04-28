package games.cultivate.mcmmocredits.converters;

import com.google.common.collect.ImmutableList;
import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.database.DatabaseType;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserDAO;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.enginehub.squirrelid.Profile;
import org.enginehub.squirrelid.resolver.HttpRepositoryService;
import org.enginehub.squirrelid.resolver.ProfileService;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PluginConverter implements Converter {
    private final MainConfig config;
    private final UserDAO destinationDAO;
    private final ConverterType type;
    private List<User> sourceUsers;

    @Inject
    public PluginConverter(final MainConfig config, final UserDAO destinationDAO) {
        this.config = config;
        this.destinationDAO = destinationDAO;
        this.type = config.getConverterType("converter", "type");
    }

    @Override
    public boolean load() {
        Path pluginPath = this.type == ConverterType.EXTERNAL_GRM ? Path.of("GuiRedeemMCMMO", "playerdata") : Path.of("MorphRedeem", "PlayerData");
        Path filePath = Bukkit.getPluginsFolder().toPath().resolve(pluginPath);
        File[] files = filePath.toFile().listFiles();
        if (files.length < 1) {
            throw new IllegalStateException("External converter plugin path is empty!");
        }
        Map<UUID, int[]> userMap = new HashMap<>();
        for (File f : files) {
            YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
            int credits = conf.getInt("Credits");
            int redeemed = this.type == ConverterType.EXTERNAL_GRM ? 0 : conf.getInt("Credits_Spent");
            userMap.put(UUID.fromString(f.getName().replace(".yml", "")), new int[]{credits, redeemed});
        }
        ProfileService resolver = HttpRepositoryService.forMinecraft();
        ImmutableList<Profile> profiles;
        try {
            profiles = resolver.findAllByUuid(userMap.keySet());
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }
        this.sourceUsers = profiles.stream().map(x -> {
            int[] stat = userMap.get(x.getUniqueId());
            return new User(x.getUniqueId(), x.getName(), stat[0], stat[1]);
        }).toList();
        return true;
    }

    @Override
    public boolean convert() {
        this.destinationDAO.addUsers(this.sourceUsers);
        if (this.config.getDatabaseProperties("settings", "database").type() == DatabaseType.H2) {
            this.destinationDAO.useHandle(x -> x.execute("CHECKPOINT SYNC"));
        }
        return true;
    }

    @Override
    public boolean verify() {
        List<User> updatedCurrentUsers = this.destinationDAO.getAllUsers();
        return this.sourceUsers.parallelStream().allMatch(updatedCurrentUsers::contains);
    }

    @Override
    public void disable() {
        //nothing to clean up for plugins.
    }
}
