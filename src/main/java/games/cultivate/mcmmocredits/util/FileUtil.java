package games.cultivate.mcmmocredits.util;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtil {
    private FileUtil() {
    }

    public static void createFile(final Path dir, final String fileName) {
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                Bukkit.getLogger().info("[MCMMOCredits] Created file:" + dir.getFileName().toString() + "!");
            }
            Files.createFile(dir.resolve(fileName));
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
