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

import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.util.Dir;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;

import java.nio.file.Path;
import java.util.List;

public final class Converter {
    private final Database database;
    private final Path path;
    private final ConverterProperties properties;

    @Inject
    public Converter(final ConverterProperties properties, final Database database, final @Dir Path path) {
        this.properties = properties;
        this.database = database;
        switch (this.properties.type()) {
            case GUI_REDEEM_MCMMO -> this.path = Bukkit.getPluginsFolder().toPath().resolve(Path.of("GuiRedeemMCMMO", "playerdata"));
            case MORPH_REDEEM -> this.path = Bukkit.getPluginsFolder().toPath().resolve(Path.of("MorphRedeem", "PlayerData"));
            default -> this.path = path;
        }
    }

    /**
     * Runs the data conversion process.
     *
     * @return If the process was successful.
     */
    public boolean run() {
        List<User> loaded = this.properties.type().apply(this.properties, this.path);
        this.database.addUsers(loaded);
        if (this.database.isH2()) {
            this.database.jdbi().useHandle(x -> x.execute("CHECKPOINT SYNC"));
        }
        List<User> updatedCurrentUsers = this.database.getAllUsers();
        return loaded.parallelStream().allMatch(updatedCurrentUsers::contains);
    }
}
