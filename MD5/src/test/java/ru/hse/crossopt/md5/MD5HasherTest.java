package ru.hse.crossopt.md5;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MD5HasherTest {
    private static final int BUFFER_SIZE = 1791;
    private String path = "src/test/resources/";

    private byte[] hashFileContent(File file) throws IOException, NoSuchAlgorithmException {
        var digest = MessageDigest.getInstance("MD5");
        var inputStream = new DigestInputStream(new FileInputStream(file), digest);
        var buffer = new byte[BUFFER_SIZE];
        while (inputStream.available() > 0) {
            assertDoesNotThrow(() -> inputStream.read(buffer, 0, BUFFER_SIZE));
        }
        return digest.digest();
    }

    @Test
    void hashMD5SimpleFile() throws IOException, NoSuchAlgorithmException {
        var file = new File(path + "simpleFile");
        assertEquals(Arrays.toString(hashFileContent(file)), Arrays.toString(MD5Hasher.hashMD5(file)));
        var sameFile = new File(path + "simpleFileToo");
        assertEquals(Arrays.toString(MD5Hasher.hashMD5(file)), Arrays.toString(MD5Hasher.hashMD5(sameFile)));
    }

    @Test
    void parallelHashMD5SimpleFile() throws IOException, NoSuchAlgorithmException {
        var file = new File(path + "simpleFile");
        assertEquals(Arrays.toString(hashFileContent(file)), Arrays.toString(MD5Hasher.parallelHashMD5(file)));
        var sameFile = new File(path + "simpleFileToo");
        assertEquals(Arrays.toString(MD5Hasher.parallelHashMD5(file)), Arrays.toString(MD5Hasher.parallelHashMD5(sameFile)));
    }

    @Test
    void hashMD5EmptyFile() throws IOException, NoSuchAlgorithmException {
        var file = new File(path + "emptyFile");
        assertEquals(Arrays.toString(hashFileContent(file)), Arrays.toString(MD5Hasher.hashMD5(file)));
    }

    @Test
    void parallelHashMD5EmptyFile() throws IOException, NoSuchAlgorithmException {
        var file = new File(path + "emptyFile");
        assertEquals(Arrays.toString(hashFileContent(file)), Arrays.toString(MD5Hasher.parallelHashMD5(file)));
    }

    @Test
    void hashMD5LargerFile() throws IOException, NoSuchAlgorithmException {
        var file = new File(path + "largerFile");
        assertEquals(Arrays.toString(hashFileContent(file)), Arrays.toString(MD5Hasher.hashMD5(file)));
        var otherFile = new File(path + "simpleFileToo");
        assertNotEquals(Arrays.toString(MD5Hasher.hashMD5(file)), Arrays.toString(MD5Hasher.hashMD5(otherFile)));
    }

    @Test
    void parallelHashMD5LargerFile() throws IOException, NoSuchAlgorithmException {
        var file = new File(path + "largerFile");
        assertEquals(Arrays.toString(hashFileContent(file)), Arrays.toString(MD5Hasher.parallelHashMD5(file)));
        var otherFile = new File(path + "simpleFileToo");
        assertNotEquals(Arrays.toString(MD5Hasher.parallelHashMD5(file)), Arrays.toString(MD5Hasher.parallelHashMD5(otherFile)));
    }

    @Test
    void hashMD5DifferentNameSameDirectories() throws IOException {
        var file = new File(path + "oneDirectory");
        var otherFile = new File(path + "sameDirectory");
        assertNotEquals(Arrays.toString(MD5Hasher.hashMD5(file)), Arrays.toString(MD5Hasher.hashMD5(otherFile)));
    }

    @Test
    void parallelHashMD5DifferentNameSameDirectories() throws IOException {
        var file = new File(path + "oneDirectory");
        var otherFile = new File(path + "sameDirectory");
        assertNotEquals(Arrays.toString(MD5Hasher.parallelHashMD5(file)), Arrays.toString(MD5Hasher.parallelHashMD5(otherFile)));
    }
}