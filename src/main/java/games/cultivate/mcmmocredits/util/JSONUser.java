package games.cultivate.mcmmocredits.util;

import games.cultivate.mcmmocredits.data.JSONDatabase;

import java.util.UUID;

/**
 * Object representing a {@link JSONDatabase} entry.
 *
 * @param uuid     {@link UUID} of the user.
 * @param username String representing the user's username.
 * @param credits  amount of credits the user currently has.
 */
public record JSONUser(UUID uuid, String username, int credits) {

}
