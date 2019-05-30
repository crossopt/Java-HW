package ru.hse.crossopt.md5;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

/** Controller for the MD5 hashing console application for directories. */
public class Main {
    /** Method that accepts path to directory as argument and compares the speed of parallel and normal hashing of it. */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please pass directory to hash as an argument.");
            return;
        }
        var file = new File(args[0]);
        if (!file.exists()) {
            System.out.println("Given file does not exist.");
            return;
        }

        byte[] simpleResult = null;
        long simpleStartTime = System.currentTimeMillis();
        try {
            simpleResult = MD5Hasher.hashMD5(file);
        } catch (IOException e) {
            System.out.println("IO exception in non-parallel hashing.");
        }
        long simpleFinishTime = System.currentTimeMillis();


        byte[] parallelResult = null;
        long parallelStartTime = System.currentTimeMillis();
        try {
            parallelResult = MD5Hasher.parallelHashMD5(file);
        } catch (IOException e) {
            System.out.println("IO exception in parallel hashing.");
        }
        long parallelFinishTime = System.currentTimeMillis();

        // TODO make it output in hex
        System.out.println("Non-parallel algorithm's result is " + Base64.getEncoder().encodeToString(simpleResult));
        System.out.println("Parallel algorithm's result is " + Base64.getEncoder().encodeToString(parallelResult));

        System.out.println("Non-parallel algorithm worked in " + (simpleFinishTime - simpleStartTime) + " ms.");
        System.out.println("Parallel algorithm worked in " + (parallelFinishTime - parallelStartTime) + " ms.");
    }
}
