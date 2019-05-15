package ru.hse.lyubortk.test4;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class MD5HasherFactory {
    private static int BUFFER_SIZE = 512;
    private static String ALGORITHM = "MD5";

    public static MD5Hasher createSingleThreadHasher() {
        return new MD5Hasher() {
            @Override
            public @NotNull byte[] getHash(@NotNull Path path) throws IOException {
                MessageDigest messageDigest;
                try {
                    messageDigest = MessageDigest.getInstance(ALGORITHM);
                } catch (NoSuchAlgorithmException ignored) {
                    // will not happen because every implementation of
                    // Java platform must support MD5
                    throw new RuntimeException();
                }

                if (Files.isDirectory(path)) {
                    messageDigest.update(path.getFileName().toString().getBytes());
                    List<Path> entries = getEntries(path);
                    for (var entry : entries) {
                        messageDigest.update(getHash(entry));
                    }
                } else {
                    computeFileHash(messageDigest, path);
                }
                return messageDigest.digest();
            }
        };
    }

    public static MD5Hasher createConcurrentHasher() {
        return path -> {
            var pool = new ForkJoinPool();
            byte[] hash;

            try {
                hash = pool.invoke(new RecursiveHasher(path));
            } catch (RuntimeException exception) {
                if (!(exception.getCause() instanceof IOException)) {
                    throw exception;
                }
                throw (IOException) exception.getCause();
            }

            return hash;
        };
    }

    private static class RecursiveHasher extends RecursiveTask<byte[]> {
        private Path path;

        private RecursiveHasher(@NotNull Path path) {
            this.path = path;
        }

        @Override
        protected byte[] compute() {
            try {
                var messageDigest = MessageDigest.getInstance(ALGORITHM);
                if (Files.isDirectory(path)) {
                    messageDigest.update(path.getFileName().toString().getBytes());
                    List<Path> entries = getEntries(path);

                    var hasherList = new ArrayList<RecursiveHasher>();
                    for (var entry : entries) {
                        var hasher = new RecursiveHasher(entry);
                        hasher.fork();
                        hasherList.add(hasher);
                    }
                    for (var hasher : hasherList) {
                        messageDigest.update(hasher.join());
                    }
                } else {
                    computeFileHash(messageDigest, path);
                }
                return messageDigest.digest();

            } catch (IOException | NoSuchAlgorithmException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private static void computeFileHash(@NotNull MessageDigest messageDigest,
                                        @NotNull Path path) throws IOException {
        try (var digestInputStream =
                     new DigestInputStream(Files.newInputStream(path), messageDigest)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (digestInputStream.read(buffer, 0, BUFFER_SIZE) >= 0) {
            }
        }
    }

    private static List<Path> getEntries(@NotNull Path path) throws IOException {
        return Files.list(path)
                .sorted(Comparator.comparing(a -> a.getFileName().toString()))
                .collect(Collectors.toList());
    }
}
