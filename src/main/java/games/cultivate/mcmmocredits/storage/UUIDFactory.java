package games.cultivate.mcmmocredits.storage;

import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;
import java.util.UUID;

/**
 * Argument Factory required for better MySQL compatibility with UUID data type.
 */
final class UUIDFactory extends AbstractArgumentFactory<UUID> {
    UUIDFactory() {
        super(Types.VARCHAR);
    }

    @Override
    protected Argument build(final UUID value, final ConfigRegistry config) {
        return (p, s, c) -> s.setString(p, value.toString());
    }
}