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

import games.cultivate.mcmmocredits.config.properties.ConverterProperties;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.database.DatabaseType;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserDAO;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a basic common Data Converter.
 */
public abstract class AbstractConverter implements Converter {
    private final Database database;
    private final Set<User> users;
    private final ConverterProperties properties;

    AbstractConverter(final Database database, final ConverterProperties properties) {
        this.database = database;
        this.properties = properties;
        this.users = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean convert() {
        UserDAO dao = this.database.get();
        dao.addUsers(this.users);
        if (this.database.getProperties().type() == DatabaseType.H2) {
            dao.useHandle(x -> x.execute("CHECKPOINT SYNC"));
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verify() {
        List<User> updatedCurrentUsers = this.database.get().getAllUsers();
        return this.users.parallelStream().allMatch(updatedCurrentUsers::contains);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final Logger logger) {
        logger.warn("Data Converter enabled in configuration! Loading...");
        try {
            this.load();
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("Data Converter failed at the loading stage! Look for possible errors thrown in console!");
            return;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        logger.info("Converter has loaded users from source successfully! Starting conversion, this may take some time.");
        if (!this.convert()) {
            logger.warn("Data Converter failed at the conversion stage! Look for possible errors thrown in console!");
            return;
        }
        logger.info("Users have been written to destination database. Starting to verify results...");
        if (!this.verify()) {
            logger.warn("Data Converter failed at the verification stage! Look for possible errors thrown in console!");
            return;
        }
        logger.info("Conversion has been verified! Disabling conversion...");
    }

    /**
     * Gets the set of converted users. Changes made to the set are carried through.
     *
     * @return The set of converted users.
     */
    protected Set<User> getUsers() {
        return this.users;
    }

    /**
     * Gets the properties of the converter.
     *
     * @return The converter's properties.
     */
    protected ConverterProperties getConverterProperties() {
        return this.properties;
    }
}
