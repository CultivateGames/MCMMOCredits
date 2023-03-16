package games.cultivate.mcmmocredits;

import games.cultivate.mcmmocredits.data.UserDAO;
import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

//TODO: replace with better design.
public final class MCMMOCreditsAPI {

    private final UserDAO dao;

    @Inject
    MCMMOCreditsAPI(final UserDAO dao) {
        this.dao = dao;
    }

    /**
     * Returns a Player's MCMMO Credit amount if they exist, otherwise -1.
     *
     * @param uuid UUID of the player.
     * @return Credit balance or -1.
     */
    public int getCredits(final UUID uuid) {
        Optional<User> optionalUser = this.dao.getUser(uuid);
        return optionalUser.map(CommandExecutor::credits).orElse(-1);
    }

    /**
     * Returns a Player's MCMMO Credit amount if they exist, otherwise -1.
     *
     * @param username String username of the player.
     * @return Credit balance or -1.
     */
    public int getCredits(final String username) {
        Optional<User> optionalUser = this.dao.getUser(username);
        return optionalUser.map(CommandExecutor::credits).orElse(-1);
    }

    public boolean addCredits(final UUID uuid, final int amount) {
        return this.dao.addCredits(uuid, amount);
    }

    public boolean addCredits(final String username, final int amount) {
        Optional<User> optionalUser = this.dao.getUser(username);
        if (optionalUser.isPresent()) {
            return this.dao.addCredits(optionalUser.get().uuid(), amount);
        }
        return false;
    }

    public boolean setCredits(final UUID uuid, final int amount) {
        return this.dao.setCredits(uuid, amount);
    }

    public boolean setCredits(final String username, final int amount) {
        Optional<User> optionalUser = this.dao.getUser(username);
        if (optionalUser.isPresent()) {
            return this.dao.setCredits(optionalUser.get().uuid(), amount);
        }
        return false;
    }

    public boolean takeCredits(final UUID uuid, final int amount) {
        return this.dao.takeCredits(uuid, amount);
    }

    public boolean takeCredits(final String username, final int amount) {
        Optional<User> optionalUser = this.dao.getUser(username);
        if (optionalUser.isPresent()) {
            return this.dao.takeCredits(optionalUser.get().uuid(), amount);
        }
        return false;
    }
}
