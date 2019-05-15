package ru.hse.lyubortk.test4;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public interface MD5Hasher {
    @NotNull byte[] getHash(@NotNull Path path) throws IOException;
}
