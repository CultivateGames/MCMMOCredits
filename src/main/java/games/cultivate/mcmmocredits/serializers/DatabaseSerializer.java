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
package games.cultivate.mcmmocredits.serializers;

import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.database.DatabaseProperties;
import games.cultivate.mcmmocredits.database.H2Database;
import games.cultivate.mcmmocredits.database.MySQLDatabase;
import games.cultivate.mcmmocredits.database.SQLiteDatabase;
import games.cultivate.mcmmocredits.util.Dir;
import jakarta.inject.Inject;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.nio.file.Path;

public class DatabaseSerializer implements TypeSerializer<Database> {
    private final Path path;

    @Inject
    public DatabaseSerializer(final @Dir Path path) {
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Database deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        DatabaseProperties properties = node.get(DatabaseProperties.class, DatabaseProperties.defaults());
        return switch (properties.type()) {
            case H2 -> new H2Database(this.path);
            case SQLITE -> new SQLiteDatabase(this.path);
            case MYSQL -> new MySQLDatabase(properties.user(), properties.password(), properties.url());
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final Type type, final @Nullable Database obj, final ConfigurationNode node) {
        throw new UnsupportedOperationException("Database serialization is not supported.");
    }
}
