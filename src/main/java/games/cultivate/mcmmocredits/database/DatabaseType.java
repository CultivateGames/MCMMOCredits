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
package games.cultivate.mcmmocredits.database;

import javax.sql.DataSource;
import java.util.function.Function;

/**
 * Database connection strategies.
 */
public enum DatabaseType {
    MYSQL(MySqlDatabase::new),
    SQLITE(SQLiteDatabase::new),
    H2(H2Database::new);

    private final Function<DataSource, AbstractDatabase> function;

    DatabaseType(final Function<DataSource, AbstractDatabase> function) {
        this.function = function;
    }

    /**
     * Creates a Database using the assigned function.
     *
     * @param source The DataSource.
     * @return A database.
     */
    public AbstractDatabase create(final DataSource source) {
        return this.function.apply(source);
    }
}
