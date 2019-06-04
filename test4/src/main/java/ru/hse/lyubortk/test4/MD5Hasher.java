package ru.hse.lyubortk.test4;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

/** This interface represents MD5 hasher */
public interface MD5Hasher {
    /** Recursively calculate hash of given directory or file */
    @NotNull byte[] getHash(@NotNull Path path) throws IOException;
}
