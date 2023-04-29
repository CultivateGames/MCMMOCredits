package games.cultivate.mcmmocredits.converters;

import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.database.Database;
import games.cultivate.mcmmocredits.database.DatabaseProperties;
import games.cultivate.mcmmocredits.database.DatabaseType;
import games.cultivate.mcmmocredits.database.types.H2Database;
import games.cultivate.mcmmocredits.database.types.MySqlDatabase;
import games.cultivate.mcmmocredits.database.types.SqlLiteDatabase;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserDAO;

import javax.inject.Inject;
import java.util.List;

/**
 * Internal -> Internal Data converter
 */
public final class InternalConverter implements Converter {
    private final UserDAO destinationDAO;
    private final ConverterType type;
    private final DatabaseProperties destinationProperties;
    private final MainConfig config;
    private Database sourceDatabase;
    private List<User> sourceUsers;

    @Inject
    public InternalConverter(final MainConfig config, final UserDAO destinationDAO) {
        this.config = config;
        this.destinationDAO = destinationDAO;
        this.type = config.getConverterType("converter", "type");
        this.destinationProperties = config.getDatabaseProperties("settings", "database");
    }

    @Override
    public boolean load() {
        if (this.type == null) {
            return false;
        }
        if (this.destinationProperties.type().name().contains(this.type.name().split("_")[1])) {
            throw new IllegalStateException("Internal Converter is using similar database types. Check configuration!");
        }
        this.sourceDatabase = switch (this.type) {
            case INTERNAL_H2 -> new H2Database();
            case INTERNAL_SQLITE -> new SqlLiteDatabase();
            case INTERNAL_MYSQL -> new MySqlDatabase(this.config);
            default -> throw new IllegalStateException("External or invalid converter passed to Internal Converter!");
        };
        this.sourceUsers = this.sourceDatabase.get().getAllUsers();
        return this.sourceUsers != null && !this.sourceUsers.isEmpty();
    }

    @Override
    public boolean convert() {
        this.destinationDAO.addUsers(this.sourceUsers);
        if (this.destinationProperties.type() == DatabaseType.H2) {
            this.destinationDAO.useHandle(x -> x.execute("CHECKPOINT SYNC"));
        }
        return true;
    }

    @Override
    public boolean verify() {
        List<User> updatedCurrentUsers = this.destinationDAO.getAllUsers();
        return this.sourceUsers.parallelStream().allMatch(updatedCurrentUsers::contains);
    }

    @Override
    public void disable() {
        this.sourceDatabase.disable();
    }
}
