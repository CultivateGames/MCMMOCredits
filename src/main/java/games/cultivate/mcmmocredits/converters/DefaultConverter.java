//
// MIT License
//
// Copyright (c) 2024 Cultivate Games
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

import games.cultivate.mcmmocredits.converters.loaders.UserLoader;
import games.cultivate.mcmmocredits.storage.StorageService;
import games.cultivate.mcmmocredits.user.User;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;

public final class DefaultConverter implements Converter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConverter.class);
    private final StorageService database;
    private final UserLoader loader;

    @Inject
    public DefaultConverter(final StorageService database, final UserLoader loader) {
        this.database = database;
        this.loader = loader;
    }

    @Override
    public boolean run() {
        long start = System.nanoTime();
        List<User> users = this.loader.getUsers().stream().toList();
        this.database.addUsers(users);
        boolean status = new HashSet<>(this.database.getAllUsers()).containsAll(users);
        long end = System.nanoTime();
        if (status) {
            LOGGER.info("Conversion completed! Duration: {}s", ((double) end - start) / 1000000000.0);
        } else {
            LOGGER.warn("There was an issue causing conversion to fail! Check user data!");
        }
        return status;
    }
}
