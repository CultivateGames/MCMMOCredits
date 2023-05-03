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

import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.database.DatabaseProperties;
import games.cultivate.mcmmocredits.database.DatabaseType;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserDAO;

import javax.inject.Inject;
import java.util.List;

/**
 * Data converter used to switch between database types.
 */
public final class InternalConverter implements Converter {
    private final UserDAO destinationDAO;
    private final ConverterType type;
    private final DatabaseProperties destinationProperties;
    private final DatabaseProperties sourceProperties;
    private Database sourceDatabase;
    private List<User> sourceUsers;

    @Inject
    public InternalConverter(final MainConfig config, final UserDAO destinationDAO) {
        this.destinationDAO = destinationDAO;
        this.type = config.getConverterType("converter", "type");
        this.sourceProperties = config.getDatabaseProperties("converter", "internal", "properties");
        this.destinationProperties = config.getDatabaseProperties("settings", "database");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean load() {
        if (this.destinationProperties.type().name().contains(this.type.name().split("_")[1])) {
            throw new IllegalStateException("Database types must be different!");
        }
        this.sourceDatabase = new Database(this.sourceProperties);
        this.sourceUsers = this.sourceDatabase.get().getAllUsers();
        return this.sourceUsers != null && !this.sourceUsers.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean convert() {
        this.destinationDAO.addUsers(this.sourceUsers);
        if (this.destinationProperties.type() == DatabaseType.H2) {
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
        this.sourceDatabase.disable();
    }
}
