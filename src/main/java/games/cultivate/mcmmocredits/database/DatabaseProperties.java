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

/**
 * Properties used in creation of the {@linkplain Database}
 *
 * @param type     Type of the Database.
 * @param host     Host of the Database. Typically, an IP address.
 * @param name     Name of the Database.
 * @param user     Name of the Database user.
 * @param password Password for the Database user.
 * @param port     Port where the Database instance is located.
 * @param ssl      If useSSL is used in the connection URL.
 */
@ConfigSerializable
public record DatabaseProperties(DatabaseType type, String host, String name, String user, String password, int port, boolean ssl) {
    public static DatabaseProperties defaults() {
        return new DatabaseProperties(DatabaseType.SQLITE, "127.0.0.1", "database", "root", "passw0rd+", 3306, true);
    }
}
