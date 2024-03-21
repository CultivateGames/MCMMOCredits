package games.cultivate.mcmmocredits.user;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class UserCreator {
    public static List<User> createUsers(int amount) {
        if (amount <= 0) {
            return List.of();
        }
        return IntStream.range(0, amount).boxed().map(i -> new User(new UUID(i, i), "tester" + i, i * 10, i * 5)).toList();
    }
}
