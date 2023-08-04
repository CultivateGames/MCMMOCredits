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
package games.cultivate.mcmmocredits.database;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;

/**
 * Properties used in creation of the Database.
 *
 * @param type     Type of the Database.
 * @param url      URL of the database for remote databases. Ignored for H2/SQLite.
 * @param user     Username for the Database user.
 * @param password Password for the Database user.
 */
@ConfigSerializable
public record DatabaseProperties(DatabaseType type, String url, String user, String password) {
    /**
     * Constructs the object with sane defaults.
     *
     * @return The object.
     */
    public static DatabaseProperties defaults() {
        return new DatabaseProperties(DatabaseType.H2, "jdbc:mysql://127.0.0.1:3306/DATABASE_NAME", "root", "passw0rd+");
    }

    /**
     * Creates an instance of the database using the provided path.
     *
     * @param path The path.
     * @return The database.
     */
    public Database create(final Path path) {
        return this.type.apply(this, path);
    }
}
