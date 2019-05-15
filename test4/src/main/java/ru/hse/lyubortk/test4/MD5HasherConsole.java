package ru.hse.lyubortk.test4;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MD5HasherConsole {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments");
            return;
        }

        try {
            printHashAndTime(MD5HasherFactory.createSingleThreadHasher(),
                    Paths.get(args[0]), "Single-thread hasher");
            System.out.println();
            printHashAndTime(MD5HasherFactory.createConcurrentHasher(),
                    Paths.get(args[0]), "Multi-thread hasher");
        } catch (IOException e) {
            System.out.println("IO error");
        }
    }

    private static void printHashAndTime(MD5Hasher hasher, Path path, String name)
            throws IOException {
        long startTime = System.currentTimeMillis();
        byte[] hash = hasher.getHash(path);
        long endTime = System.currentTimeMillis();
        System.out.println(name + ": " + bytesToHex(hash));
        System.out.println("Execution time: " + (endTime - startTime) + " ms");
    }

    private static String bytesToHex(byte[] input) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : input) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }
}
