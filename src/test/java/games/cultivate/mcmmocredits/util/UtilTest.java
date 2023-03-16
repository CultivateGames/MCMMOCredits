package games.cultivate.mcmmocredits.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilTest {
    private static final Path PATH = Path.of("src", "test", "resources");

    @AfterAll
    static void tearDown() throws IOException {
        Files.deleteIfExists(PATH.resolve("newfile.txt"));
    }

    @Test
    void testCreateFile() throws IOException {
        Util.createFile(PATH, "newfile.txt");
        File f = PATH.resolve("newfile.txt").toFile();
        try (FileWriter fw = new FileWriter(f)) {
            fw.write("12345c");
        }
        assertEquals("12345c", Files.readString(PATH.resolve("newfile.txt")));
    }

    @Test
    void testCreateFileAlreadyExists() throws IOException {
        Util.createFile(PATH, "blank.txt");
        assertEquals("1234", Files.readString(PATH.resolve("blank.txt")));
    }
}
