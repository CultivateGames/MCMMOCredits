package games.cultivate.mcmmocredits.converters;

import games.cultivate.mcmmocredits.config.MainConfig;
import games.cultivate.mcmmocredits.database.DatabaseType;
import games.cultivate.mcmmocredits.user.User;
import games.cultivate.mcmmocredits.user.UserDAO;
import games.cultivate.mcmmocredits.util.Util;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class CSVConverter implements Converter {
    private final MainConfig config;
    private final UserDAO destinationDAO;
    private final ConverterType type;
    private List<User> sourceUsers;

    @Inject
    public CSVConverter(final MainConfig config, final UserDAO destinationDAO) {
        this.config = config;
        this.destinationDAO = destinationDAO;
        this.type = this.config.getConverterType("converter", "type");
    }

    @Override
    public boolean load() {
        if (this.type == null) {
            return false;
        }
        Path path = Util.getPluginPath().resolve("database.csv");
        try {
            List<String> lines = Files.readAllLines(path);
            this.sourceUsers = lines.stream().map(this::userFromCSV).toList();
            return !this.sourceUsers.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private User userFromCSV(final String line) {
        String[] arr = line.split(",");
        UUID uuid = UUID.fromString(arr[0]);
        return new User(uuid, arr[1], Integer.parseInt(arr[2]), Integer.parseInt(arr[3]));
    }

    @Override
    public boolean convert() {
        this.destinationDAO.addUsers(this.sourceUsers);
        if (this.config.getDatabaseProperties("settings", "database").type() == DatabaseType.H2) {
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
        //nothing to clean up for csv.
    }
}
