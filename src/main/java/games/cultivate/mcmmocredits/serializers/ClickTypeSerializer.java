package games.cultivate.mcmmocredits.serializers;

import games.cultivate.mcmmocredits.menu.ClickTypes;
import games.cultivate.mcmmocredits.util.Util;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class ClickTypeSerializer implements TypeSerializer<ClickTypes> {
    public static final ClickTypeSerializer INSTANCE = new ClickTypeSerializer();

    @Override
    public ClickTypes deserialize(final Type type, final ConfigurationNode node) {
        String data = (String) node.key();
        if (Util.getSkillNames().contains(data)) {
            return ClickTypes.REDEEM;
        }
        return switch (data) {
            case "messages" -> ClickTypes.EDIT_MESSAGE;
            case "settings" -> ClickTypes.EDIT_SETTING;
            case "navigation", "config", "redeem" -> ClickTypes.COMMAND;
            case "fill" -> ClickTypes.FILL;
            default -> throw new IllegalStateException("Invalid data was passed to the ClickTypeSerializer! value:" + data);
        };
    }

    @Override
    public void serialize(final Type type, @Nullable final ClickTypes obj, final ConfigurationNode node) {
        throw new UnsupportedOperationException("Operation not supported!");
    }
}
