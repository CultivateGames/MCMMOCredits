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
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.util.Dir;

import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/**
 * Data Converter used to switch between internal database types.
 */
public final class InternalConverter extends AbstractConverter {
    private final Database oldDatabase;

    /**
     * Constructs the object.
     *
     * @param database   The current Database.
     * @param properties The properties of the converter.
     * @param path       The plugin's data path.
     */
    @Inject
    public InternalConverter(final Database database, final ConverterProperties properties, final @Dir Path path) {
        super(database, properties);
        this.oldDatabase = properties.getOldDatabase(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() throws IOException, InterruptedException {
        Set<User> set = this.getUsers();
        set.addAll(this.oldDatabase.get().getAllUsers());
        this.oldDatabase.disable();
    }
}
