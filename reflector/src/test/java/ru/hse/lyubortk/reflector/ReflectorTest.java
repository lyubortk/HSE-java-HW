package ru.hse.lyubortk.reflector;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.lyubortk.reflector.testclasses.*;

import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ReflectorTest {
    private static final String DIFF_CLASSES_SAME =
            "first class unique fields:0\n\n"
            + "second class unique fields:0\n\n"
            + "first class unique methods:0\n\n"
            + "second class unique methods:0\n";

    private final ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private Path tempDirectory;


    @BeforeEach
    void setOutStream() {
        System.setOut(new PrintStream(arrayOut));
    }

    @AfterEach
    void restoreOutStream(){
        System.setOut(originalOut);
    }

    @AfterEach
    void clearFiles() throws IOException {
        Files.deleteIfExists(Paths.get("SomeClass.java"));
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
    void printStructureSimple1() throws IOException {
        testStructure(SimpleClass1.class,
                "package ru.hse.lyubortk.reflector.testclasses;\n"
                        + "public class SomeClass {\n"
                        + "    public int field1;\n"
                        + "    public SomeClass() {\n"
                        + "    }\n"
                        + "}");
    }

    @Test
    void printStructureSimple2() throws IOException {
        testStructure(SimpleClass2.class,
                "package ru.hse.lyubortk.reflector.testclasses;\n"
                        + "public class SomeClass {\n"
                        + "    public int field1;\n"
                        + "    private double field2;\n"
                        + "    protected SomeClass field3;\n"
                        + "    public SomeClass() {\n"
                        + "    }\n"
                        + "    private SomeClass(SomeClass arg0) {\n"
                        + "    }\n"
                        + "}");
    }

    @Test
    void printStructureNested1() throws IOException {
        testStructure(NestedClass1.class,
                "package ru.hse.lyubortk.reflector.testclasses;\n"
                        + "public class SomeClass {\n"
                        + "    SomeClass.Inner1 inner1;\n"
                        + "    protected SomeClass.Inner2 inner2;\n"
                        + "    private SomeClass.Nested1 nested1;\n"
                        + "    public SomeClass.Nested2 nested2;\n"
                        + "    SomeClass(SomeClass.Inner1 arg0, SomeClass.Nested2 arg1) {\n"
                        + "    }\n"
                        + "    public class Inner1 {\n"
                        + "        public Inner1() {\n"
                        + "        }\n"
                        + "    }\n"
                        + "    private class Inner2 {\n"
                        + "        Inner2() {\n"
                        + "        }\n"
                        + "        \n"
                        + "        <T extends java.lang.Object> Inner2(T arg0, T arg1) {\n"
                        + "        }\n"
                        + "        \n"
                        + "        Inner2(int arg1, int arg2, double arg3) {\n"
                        + "        }"
                        + "    }\n"
                        + "    private abstract static interface Interface1 {\n"
                        + "    }\n"
                        + "    public static class Nested1 {\n"
                        + "        public Nested1() {\n"
                        + "        }\n"
                        + "    }\n"
                        + "    protected static class Nested2 implements SomeClass.Interface1 {\n"
                        + "        Nested2() {\n"
                        + "        }\n"
                        + "        <T extends java.lang.Object> Nested2(T arg0, T arg1) {\n"
                        + "        }\n"
                        + "        Nested2(int arg0, int arg1, double arg2) {\n"
                        + "        }"
                        + "    }\n"
                        + "}");
    }

    @Test
    void printStructureHashtable() throws IOException {
        testStructure(Hashtable.class,
                "package ru.hse.lyubortk.reflector.testclasses;\n"
                        + "public class SomeClass {\n"
                        + "    private ru.hse.lyubortk.reflector.testclasses.MyList[] \n"
                        + "            bucketArray;\n"
                        + "    private int bucketsNumber;\n"
                        + "    private int size;\n"
                        + "    public SomeClass() {\n"
                        + "    }\n"
                        + "    public SomeClass(int arg0) {\n"
                        + "    }\n"
                        + "    private void checkBucketsNumber()  {\n"
                        + "        return;\n"
                        + "    }\n"
                        + "    public void clear()  {\n"
                        + "        return;\n"
                        + "    }\n"
                        + "    public boolean contains(java.lang.String arg0)  {\n"
                        + "        return false;\n"
                        + "    }\n"
                        + "    private void copyContentTo(SomeClass arg0) {\n"
                        + "        return;\n"
                        + "    }\n"
                        + "    private void copyFrom(SomeClass arg0)  {\n"
                        + "        return;\n"
                        + "    }\n"
                        + "    public java.lang.String get(java.lang.String arg0)  {\n"
                        + "        return null;\n"
                        + "    }\n"
                        + "    private int getBucketIndex(java.lang.String arg0)  {\n"
                        + "        return 0;\n"
                        + "    }\n"
                        + "    public java.lang.String put(java.lang.String arg0, \n"
                        + "                                java.lang.String arg1)  {\n"
                        + "        return null;\n"
                        + "    }\n"
                        + "    public java.lang.String remove(java.lang.String arg0)  {\n"
                        + "        return null;\n"
                        + "    }\n"
                        + "    public int size()  {\n"
                        + "        return 0;\n"
                        + "    }\n"
                        + "    private static class StringPair {\n"
                        + "        private java.lang.String key;\n"
                        + "        private java.lang.String val;\n"
                        + "        private StringPair(java.lang.String arg0, \n"
                        + "                           java.lang.String arg1) {\n"
                        + "        }\n"
                        + "    }\n"
                        + "}\n");
    }

    @Test
    void printStructureMyList() throws IOException {
        testStructure(MyList.class,
                "package ru.hse.lyubortk.reflector.testclasses;\n"
                        + "public class SomeClass \n"
                        + "        implements java.lang.Iterable<java.lang.Object> {\n"
                        + "    private SomeClass.ListNode head;\n"
                        + "    public SomeClass() {\n"
                        + "    }\n"
                        + "    public void insertObject(java.lang.Object arg0)  {\n"
                        + "        return;\n"
                        + "    }\n"
                        + "    public SomeClass.MyListIterator iterator()  {\n"
                        + "        return null;\n"
                        + "    }\n"
                        + "    private static class ListNode {\n"
                        + "        private java.lang.Object data;\n"
                        + "        private SomeClass.ListNode nextNode;\n"
                        + "        private SomeClass.ListNode prevNode;\n"
                        + "        private ListNode(java.lang.Object arg0) {\n"
                        + "        }\n"
                        + "    }\n"
                        + "    private class MyListIterator \n"
                        + "            implements java.util.Iterator<java.lang.Object> {\n"
                        + "        private SomeClass.ListNode nextNode;\n"
                        + "        private SomeClass.ListNode prevNode;\n"
                        + "        private MyListIterator(SomeClass.ListNode arg1) {\n"
                        + "        }\n"
                        + "        public boolean hasNext()  {\n"
                        + "            return false;\n"
                        + "        }\n"
                        + "        public java.lang.Object next()  {\n"
                        + "            return null;\n"
                        + "        }\n"
                        + "        public void remove()  {\n"
                        + "            return;\n"
                        + "        }\n"
                        + "    }\n"
                        + "}\n");
    }

    @Test
    void printStructureGeneric() throws IOException {
        testStructure(GenericClass1.class,
                "package ru.hse.lyubortk.reflector.testclasses;\n"
                        + "public class SomeClass <T extends java.lang.Object, \n"
                        + "        E extends java.util.List<T>> {\n"
                        + "    T field1;\n"
                        + "    E field2;\n"
                        + "    SomeClass(T arg0) {\n"
                        + "    }\n"
                        + "    <B extends java.lang.Object> SomeClass(B arg0, T arg1) {\n"
                        + "    }\n"
                        + "    public T genericMethod(java.util.List<? super E> arg0, \n"
                        + "                           java.util.Map<T, ? extends T> arg1)  {\n"
                        + "        return null;\n"
                        + "    }\n"
                        + "    static class genericNestedClass <B extends java.lang.Object> {\n"
                        + "        B field1;\n"
                        + "        genericNestedClass() {\n"
                        + "        }\n"
                        + "    }\n"
                        + "}\n");
    }

    @Test
    void diffClassesTestSame1() {
        Reflector.diffClasses(GenericClass1.class, GenericClass1Copy.class);
        compareWords(DIFF_CLASSES_SAME, arrayOut.toString());
    }

    @Test
    void diffClassesTestSame2() {
        Reflector.diffClasses(Hashtable.class, HashtableCopy.class);
        compareWords(DIFF_CLASSES_SAME, arrayOut.toString());
    }

    @Test
    void diffClassesTestDifferentFields() {
        Reflector.diffClasses(SimpleClass2.class, SimpleClass2Different.class);
        compareWords("first class unique fields:1\n"
                + "public int field1\n\nsecond class unique fields:1\n"
                + "public java.util.List<java.lang.Object> field4\n\n"
                + "first class unique methods:0\n\n"
                + "second class unique methods:0\n", arrayOut.toString());
    }

    @Test
    void diffClassesTestDifferentMethods() {
        Reflector.diffClasses(Hashtable.class, HashtableDifferent.class);
        compareWords("first class unique fields:0\n\n"
                + "second class unique fields:0\n\n"
                + "first class unique methods:2\n"
                + "private void copyContentTo(ClassName arg0)\n"
                + "public java.lang.String put(java.lang.String arg0, java.lang.String arg1)\n\n"
                + "second class unique methods:1\n"
                + "public void dummyMethod(int arg0, int arg1)\n", arrayOut.toString());
    }

    @Test
    void printLoadAndCompareSimple1() throws IOException, ClassNotFoundException {
        printLoadAndCompare(SimpleClass1.class);
    }

    @Test
    void printLoadAndCompareSimple2() throws IOException, ClassNotFoundException {
        printLoadAndCompare(SimpleClass2.class);
    }

    @Test
    void printLoadAndCompareHashtable() throws IOException, ClassNotFoundException {
        printLoadAndCompare(Hashtable.class);
    }

    @Test
    void printLoadAndCompareMyList() throws IOException, ClassNotFoundException {
        printLoadAndCompare(MyList.class);
    }

    @Test
    void printLoadAndCompareGenericClass1() throws IOException, ClassNotFoundException {
        printLoadAndCompare(GenericClass1.class);
    }

    @Test
    void printLoadAndCompareNestedClass1() throws IOException, ClassNotFoundException {
        printLoadAndCompare(NestedClass1.class);
    }

    void printLoadAndCompare(@NotNull Class<?> clazz)
            throws IOException, ClassNotFoundException {
        Reflector.printStructure(clazz);

        var fileToCompile = new File("SomeClass.java");

        Path projectRootFolderPath = fileToCompile.getAbsoluteFile().getParentFile().toPath();
        tempDirectory = Files.createTempDirectory(projectRootFolderPath, "temp");
        String packageRelativeDir = clazz.getPackageName().replace('.', File.separatorChar);
        Path packagePath = Paths.get(tempDirectory + File.separator + packageRelativeDir);

        Files.createDirectories(packagePath);

        var compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null,
                "-d", tempDirectory.toString(), fileToCompile.getPath());

        var classLoader = new URLClassLoader(new URL[]{tempDirectory.toUri().toURL()});
        Class<?> someClass = classLoader.loadClass(clazz.getPackageName() + ".SomeClass");
        Reflector.diffClasses(clazz, someClass);
        compareWords(DIFF_CLASSES_SAME, arrayOut.toString());
    }

    static void testStructure(@NotNull Class<?> clazz, String expected) throws IOException {
        Reflector.printStructure(clazz);
        try (var in = new Scanner(new File("SomeClass.java"))) {
            var expectedScanner = new Scanner(expected);
            while (in.hasNext()) {
                assertEquals(expectedScanner.next(), in.next());
            }
            assertFalse(expectedScanner.hasNext());
        }
    }

    void compareWords(String expected, String result) {
        var scanner1 = new Scanner(expected);
        var scanner2 = new Scanner(result);

        while (scanner1.hasNext() && scanner2.hasNext()) {
            assertEquals(scanner1.next(), scanner2.next());
        }
        assertFalse(scanner1.hasNext());
        assertFalse(scanner2.hasNext());
    }
}