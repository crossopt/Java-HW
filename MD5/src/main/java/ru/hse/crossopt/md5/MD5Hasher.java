package ru.hse.crossopt.md5;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/** Class that hashes files or directories. */
public class MD5Hasher {
    private static final int BUFFER_SIZE = 1024;

    /** Wraps the creation of a MD5 MessageDigest. */
    private static MessageDigest createDigest() {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignored) {} // md5 exists
        return digest;
    }

    /**
     * Hashes a file.
     * @param file the file to hash.
     * @return the MD5 hash of the file.
     * @throws IOException if reading from file failed.
     */
    private static byte[] hashFile(@NotNull File file) throws IOException {
        var digest = createDigest();
        var inputStream = new DigestInputStream(new FileInputStream(file), digest);
        var buffer = new byte[BUFFER_SIZE];
        while (inputStream.read(buffer, 0, BUFFER_SIZE) != -1) {} //read
        return digest.digest();
    }

    /**
     * Hashed the given object according to the rules of the problem.
     * @param file a file to hash.
     * @return the MD5 hash.
     * @throws IOException if reading while hashing failed.
     */
    public static byte[] hashMD5(@NotNull File file) throws IOException {
        return file.isDirectory() ? hashDirectory(file) : hashFile(file);
    }

    /**
     * Recursively hashes the directory.
     * @param directory a File instance that is a directory.
     * @return the MD5 hash.
     * @throws IOException if reading while hashing failed.
     */
    private static byte[] hashDirectory(@NotNull File directory) throws IOException {
        var digest = createDigest();
        digest.update(directory.getName().getBytes());
        File[] fileList = directory.listFiles();
        assert fileList != null;
        for (var file : fileList) {
            digest.update(hashMD5(file));
        }
        return digest.digest();
    }

    /**
     * Hashed the given object according to the rules of the problem using ForkJoinPool.
     * @param file a File to hash.
     * @return the MD5 hash.
     * @throws IOException if reading while hashing failed.
     */
    public static byte[] parallelHashMD5(@NotNull File file) throws IOException {
        var forkJoinPool = new ForkJoinPool();
        var task = new HashTask(file);
        forkJoinPool.submit(task);
        var result = task.compute();
        if (result == null) { // suppressed exception
            throw new IOException("Exception occurred in subtask.");
        }
        return result;
    }

    /** The hashing task for the ForkJoinPool. */
    private static class HashTask extends RecursiveTask<byte[]> {
        private @NotNull File hashedFile;

        /** Creates a task for the given File. */
        public HashTask(@NotNull File file) {
            hashedFile = file;
        }

        /**
         * Recursively creates tasks from all subdirectories to parallel the computation.
         * @return the MD5 hash of the file for this task, or null if computing failed.
         */
        @Override
        @Nullable
        protected byte[] compute() {
            if (hashedFile.isFile()) {
                byte[] result = null;
                try {
                    result = hashMD5(hashedFile);
                } catch (IOException ignored) {} // will return null on fail
                return result;
            }

            var fileList = hashedFile.listFiles();
            assert fileList != null;
            ArrayList<HashTask> tasks = new ArrayList<>();
            for (var file : fileList) {
                tasks.add(new HashTask(file));
            }

            var digest = createDigest();
            digest.update(hashedFile.getName().getBytes());
            for (var invocationResult : ForkJoinTask.invokeAll(tasks)) {
                byte[] taskResult = null;
                try {
                    taskResult = invocationResult.get();
                } catch (Exception ignored) {} // will return null on fail
                if (taskResult == null) {
                    return null;
                }
                digest.update(taskResult);
            }
            return digest.digest();
        }
    }
}
