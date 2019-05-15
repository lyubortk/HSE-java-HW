package ru.hse.lyubortk.test4;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.jupiter.api.Assertions.*;

class MD5HasherTest {
    private static byte[] EMPTY_FILE_MD5 = new byte[] {

    };

    @AfterEach
    void clearFiles() throws IOException {
        if (tempDirectory != null) {
            Files.walkFileTree(tempDirectory, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path directory, IOException exception)
                        throws IOException {
                    Files.delete(directory);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    @Test
    void hashEmptyFile() throws IOException {
        
    }


}