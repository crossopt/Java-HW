package ru.hse.crossopt.PhoneBook;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    //The interesting tests are for the PhoneBook class. This just is a basic test to show interactor works correctly.
    @Test
    void test_example() throws IOException {
        try (var inputFile = new FileInputStream(new File("src/test/resources/mainTest.in"));
            var outputFile = new FileOutputStream(new File("src/test/resources/mainTest.out"))) {
            System.setIn(inputFile);
            System.setOut(new PrintStream(outputFile));
            Main.main(new String[0]);
            var actual = Paths.get("src/test/resources/mainTest.out");
            var expected = Paths.get("src/test/resources/mainTest.ans");
            assertArrayEquals(Files.readAllBytes(actual), Files.readAllBytes(expected));
        }
    }
}
