package games.cultivate.mcmmocredits.util;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

/**
 * Utility class for interacting with {@link File}s.
 */
public final class FileUtil {
    private FileUtil() {
    }

    /**
     * Creates a file and associated directories if they do not exist.
     *
     * @param dir      The {@link Path} to check for creation.
     * @param fileName Name of the {@link File} to create.
     */
    public static void createFile(final Path dir, final String fileName) {
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                Bukkit.getLogger().log(Level.INFO, "[MCMMOCredits] Created file: {0}!", dir.getFileName());
            }
            Files.createFile(dir.resolve(fileName));
        } catch (FileAlreadyExistsException ignored) { //Ignore if file already exists.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
