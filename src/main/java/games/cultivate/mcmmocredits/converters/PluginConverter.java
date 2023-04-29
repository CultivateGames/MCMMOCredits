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

/**
 * Data converter used to add Users from external plugin data.
 */
public final class PluginConverter implements Converter {
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean convert() {
        this.destinationDAO.addUsers(this.sourceUsers);
        if (this.config.getDatabaseProperties("settings", "database").type() == DatabaseType.H2) {
            this.destinationDAO.useHandle(x -> x.execute("CHECKPOINT SYNC"));
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verify() {
        List<User> updatedCurrentUsers = this.destinationDAO.getAllUsers();
        return this.sourceUsers.parallelStream().allMatch(updatedCurrentUsers::contains);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disable() {
        //nothing to clean up for plugins.
    }
}
