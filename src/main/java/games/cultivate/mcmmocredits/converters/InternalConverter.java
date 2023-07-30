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
import jakarta.inject.Inject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data Converter used to switch between internal database types.
 */
public final class InternalConverter implements Converter {
    private final Database oldDatabase;
    private final Database currentDatabase;
    private final Set<User> users;

    /**
     * Constructs the object.
     *
     * @param currentDatabase The current Database.
     * @param oldDatabase     The old Database.
     */
    @Inject
    public InternalConverter(final Database currentDatabase, final Database oldDatabase) {
        this.currentDatabase = currentDatabase;
        this.oldDatabase = oldDatabase;
        this.users = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() {
        this.users.addAll(this.oldDatabase.getAllUsers());
        this.oldDatabase.disable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean convert() {
        this.currentDatabase.addUsers(this.users);
        if (this.currentDatabase.isH2()) {
            this.currentDatabase.jdbi().useHandle(x -> x.execute("CHECKPOINT SYNC"));
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verify() {
        List<User> updatedCurrentUsers = this.currentDatabase.getAllUsers();
        return this.users.parallelStream().allMatch(updatedCurrentUsers::contains);
    }
}
