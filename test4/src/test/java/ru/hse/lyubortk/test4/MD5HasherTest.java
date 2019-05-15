package ru.hse.lyubortk.test4;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MD5HasherTest {
    private static final String RESOURCES = "src" + File.separatorChar + "test"
                                            + File.separatorChar + "resources"
                                            + File.separatorChar;

    private static final byte[] EMPTY_FILE_MD5 = new byte[] {
            -44, 29, -116, -39, -113, 0, -78, 4, -23, -128, 9, -104, -20, -8, 66, 126
    };

    private static final byte[] SMALL_FILE_MD5 = new byte[] {
            5, -123, -107, -92, -114, 74, -1, 72, -56, -28, -16, 115, -79, -81, 4, 52
    };

    private static final byte[] BIG_FILE_MD5 = new byte[] {
            -123, 127, -81, 75, 66, -4, -98, 50, 79, -76, 11, 114, 35, -14, -87, 74
    };

    private static final byte[] FOLDER_MD5 = new byte[] {
            63, -73, 27, 81, 109, -62, 38, 0, 53, -59, 21, -48, -9, 16, 102, 8
    };

    @Test
    void hashEmptyFileSingleThread() throws IOException {
        var hasher = MD5HasherFactory.createSingleThreadHasher();
        assertArrayEquals(EMPTY_FILE_MD5, hasher.getHash(Paths.get(RESOURCES + "empty")));
    }

    @Test
    void hashEmptyFileMutliThread() throws IOException {
        var hasher = MD5HasherFactory.createConcurrentHasher();
        assertArrayEquals(EMPTY_FILE_MD5, hasher.getHash(Paths.get(RESOURCES + "empty")));
    }

    @Test
    void hashSmallFileSingleThread() throws IOException {
        var hasher = MD5HasherFactory.createSingleThreadHasher();
        assertArrayEquals(SMALL_FILE_MD5, hasher.getHash(Paths.get(RESOURCES + "small")));
    }

    @Test
    void hashSmallFileMutliThread() throws IOException {
        var hasher = MD5HasherFactory.createConcurrentHasher();
        assertArrayEquals(SMALL_FILE_MD5, hasher.getHash(Paths.get(RESOURCES + "small")));
    }

    @Test
    void hashBigFileSingleThread() throws IOException {
        var hasher = MD5HasherFactory.createSingleThreadHasher();
        assertArrayEquals(BIG_FILE_MD5, hasher.getHash(Paths.get(RESOURCES + "big")));
    }

    @Test
    void hashBigFileMutliThread() throws IOException {
        var hasher = MD5HasherFactory.createConcurrentHasher();
        assertArrayEquals(BIG_FILE_MD5, hasher.getHash(Paths.get(RESOURCES + "big")));
    }

    @Test
    void hashFolderSingleThread() throws IOException {
        var hasher = MD5HasherFactory.createSingleThreadHasher();
        assertArrayEquals(FOLDER_MD5, hasher.getHash(Paths.get(RESOURCES)));
    }

    @Test
    void hashFolderMutliThread() throws IOException {
        var hasher = MD5HasherFactory.createConcurrentHasher();
        assertArrayEquals(FOLDER_MD5, hasher.getHash(Paths.get(RESOURCES)));
    }
}