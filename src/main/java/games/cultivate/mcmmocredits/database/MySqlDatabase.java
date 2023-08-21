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

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.UUID;

/**
 * Represents a MySql Database.
 */
public class MySqlDatabase extends AbstractDatabase {
    /**
     * Constructs the object.
     *
     * @param source The DataSource.
     */
    public MySqlDatabase(final DataSource source) {
        super(source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Jdbi createJdbi() {
        return Jdbi.create(this.source).registerArgument(new UUIDFactory()).registerRowMapper(new UserMapper());
    }

    /**
     * Argument Factory required for better MySQL compatibility with UUID data type.
     */
    static class UUIDFactory extends AbstractArgumentFactory<UUID> {
        protected UUIDFactory() {
            super(Types.VARCHAR);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Argument build(final UUID value, final ConfigRegistry config) {
            return (p, s, c) -> s.setString(p, value.toString());
        }
    }

}
