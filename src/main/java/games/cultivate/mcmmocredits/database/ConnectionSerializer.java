package games.cultivate.mcmmocredits.database;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import java.lang.reflect.Type;

/**
 * Proof of Concept for how Configurate's Type Serializer can be used in theory with custom objects.
 * I definitely found some help on this, but think it is pretty cool still.
 */
public class ConnectionSerializer implements TypeSerializer<Connection> {
    public static final ConnectionSerializer connection = new ConnectionSerializer();

    public Connection deserialize(Type type, ConfigurationNode node) {
        if (node.empty()) {
            return null;
        }
        final String host = node.node("host").getString();
        final int port = node.node("port").getInt();
        final String database = node.node("database").getString();
        final String username = node.node("username").getString();
        final String password = node.node("password").getString();

        return new Connection(host, port, database, username, password);
    }

    public void serialize(Type type, Connection obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }
        node.node("host").set(obj.getHost());
        node.node("port").set(obj.getPort());
        node.node("database").set(obj.getDatabaseName());
        node.node("username").set(obj.getUsername());
        node.node("password").set(obj.getPassword());
    }
}
