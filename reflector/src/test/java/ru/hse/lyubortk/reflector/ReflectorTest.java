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
    private final ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private static final String DIFF_CLASSES_SAME =
            "first class unique fields:0\n\n" +
            "second class unique fields:0\n\n" +
            "first class unique methods:0\n\n" +
            "second class unique methods:0\n";

    @BeforeEach
    void setOutStream() {
        System.setOut(new PrintStream(arrayOut));
    }

    @AfterEach
    void restoreOutStream(){
        System.setOut(originalOut);
    }


    @Test
    void printStructureSimple1() throws IOException {
        testStructure(SimpleClass1.class,
                "package ru.hse.lyubortk.reflector.testclasses;\n" +
                "public class SomeClass extends java.lang.Object  {\n" +
                "    public int field1;\n" +
                "    public SomeClass() {\n" +
                "    } }");
    }

    @Test
    void printStructureSimple2() throws IOException {
        testStructure(SimpleClass2.class,
                "package ru.hse.lyubortk.reflector.testclasses;\n" +
                        "public class SomeClass extends java.lang.Object  {\n" +
                        "    public int field1;\n" +
                        "    private double field2;\n" +
                        "    protected SomeClass field3;\n" +
                        "    public SomeClass() {\n" +
                        "    }\n" +
                        "    private SomeClass(SomeClass arg0) {\n" +
                        "    }\n" +
                        "}");
    }

    @Test
    void printStructureNested1() throws IOException {
        testStructure(NestedClass1.class,
                "package ru.hse.lyubortk.reflector.testclasses;\n" +
                        "public class SomeClass extends java.lang.Object  {\n" +
                        "    SomeClass.Inner1 inner1;\n" +
                        "    protected SomeClass.Inner2 inner2;\n" +
                        "    private SomeClass.Nested1 nested1;\n" +
                        "    public SomeClass.Nested2 nested2;\n" +
                        "    SomeClass(SomeClass.Inner1 arg0, SomeClass.Nested2 arg1) {\n" +
                        "    }\n" +
                        "    private abstract static interface Interface1  {\n" +
                        "    }\n" +
                        "    protected static class Nested2 extends java.lang.Object " +
                        "       implements SomeClass.Interface1 {\n" +
                        "        protected Nested2() {\n" +
                        "        }\n" +
                        "    }"+
                        "    private class Inner2 extends java.lang.Object  {\n" +
                        "        private Inner2(SomeClass arg0) {\n" +
                        "        }\n" +
                        "    }\n" +
                        "    public static class Nested1 extends java.lang.Object  {\n" +
                        "        public Nested1() {\n" +
                        "        }\n" +
                        "    }\n" +
                        "    public class Inner1 extends java.lang.Object  {\n" +
                        "        public Inner1(SomeClass arg0) {\n" +
                        "        }\n" +
                        "    }\n" +
                        "}");
    }

    @Test
    void printStructureHashtable() throws IOException {
        testStructure(Hashtable.class,
                "package ru.hse.lyubortk.reflector.testclasses;\n" +
                        "public class SomeClass extends java.lang.Object  {\n" +
                        "    private int size;\n" +
                        "    private int bucketsNumber;\n" +
                        "    private ru.hse.lyubortk.reflector.testclasses.MyList[] " +
                        "bucketArray;\n" +
                        "    public SomeClass() {\n" +
                        "    }\n" +
                        "    public SomeClass(int arg0) {\n" +
                        "    }\n" +
                        "    private void checkBucketsNumber() {\n" +
                        "        throw new UnsupportedOperationException();\n" +
                        "    }\n" +
                        "    private void copyFrom(SomeClass arg0) {\n" +
                        "        throw new UnsupportedOperationException();\n" +
                        "    }\n" +
                        "    private void copyContentTo(SomeClass arg0) {\n" +
                        "        throw new UnsupportedOperationException();\n" +
                        "    }\n" +
                        "    private int getBucketIndex(java.lang.String arg0) {\n" +
                        "        throw new UnsupportedOperationException();\n" +
                        "    }\n" +
                        "    public java.lang.String remove(java.lang.String arg0) {\n" +
                        "        throw new UnsupportedOperationException();\n" +
                        "    }\n" +
                        "    public java.lang.String get(java.lang.String arg0) {\n" +
                        "        throw new UnsupportedOperationException();\n" +
                        "    }\n" +
                        "    public java.lang.String put(java.lang.String arg0, " +
                        "java.lang.String arg1) {\n" +
                        "        throw new UnsupportedOperationException();\n" +
                        "    }\n" +
                        "    public void clear() {\n" +
                        "        throw new UnsupportedOperationException();\n" +
                        "    }\n" +
                        "    public boolean contains(java.lang.String arg0) {\n" +
                        "        throw new UnsupportedOperationException();\n" +
                        "    }\n" +
                        "    public int size() {\n" +
                        "        throw new UnsupportedOperationException();\n" +
                        "    }\n" +
                        "    private static class StringPair extends java.lang.Object  {\n" +
                        "        private java.lang.String key;\n" +
                        "        private java.lang.String val;\n" +
                        "        private StringPair(java.lang.String arg0, " +
                        "java.lang.String arg1) {\n" +
                        "        }\n" +
                        "    }\n" +
                        "}\n");
    }

    @Test
    void printStructureMyList() throws IOException {
        testStructure(MyList.class,
                "package ru.hse.lyubortk.reflector.testclasses;\n" +
                        "public class SomeClass extends java.lang.Object " +
                        "implements java.lang.Iterable<java.lang.Object> {\n" +
                        "    private SomeClass.ListNode head;\n" +
                        "    public SomeClass() {\n" +
                        "    }\n" +
                        "    public void insertObject(java.lang.Object arg0) {\n" +
                        "        throw new UnsupportedOperationException();\n" +
                        "    }\n" +
                        "    public SomeClass.MyListIterator iterator() {\n" +
                        "        throw new UnsupportedOperationException();\n" +
                        "    }\n" +
                        "    private class MyListIterator extends java.lang.Object " +
                        "implements java.util.Iterator<java.lang.Object> {\n" +
                        "        private SomeClass.ListNode nextNode;\n" +
                        "        private SomeClass.ListNode prevNode;\n" +
                        "        private MyListIterator(SomeClass arg0, " +
                        "SomeClass.ListNode arg1) {\n" +
                        "        }\n" +
                        "        public void remove() {\n" +
                        "            throw new UnsupportedOperationException();\n" +
                        "        }\n" +
                        "        public java.lang.Object next() {\n" +
                        "            throw new UnsupportedOperationException();\n" +
                        "        }\n" +
                        "        public boolean hasNext() {\n" +
                        "            throw new UnsupportedOperationException();\n" +
                        "        }\n" +
                        "    }\n" +
                        "    private static class ListNode extends java.lang.Object  {\n" +
                        "        private java.lang.Object data;\n" +
                        "        private SomeClass.ListNode prevNode;\n" +
                        "        private SomeClass.ListNode nextNode;\n" +
                        "        private ListNode(java.lang.Object arg0) {\n" +
                        "        }\n" +
                        "    }\n" +
                        "}\n");
    }

    @Test
    void printStructureGeneric() throws IOException {
        testStructure(GenericClass1.class,
                "package ru.hse.lyubortk.reflector.testclasses;\n" +
                        "public class SomeClass <T extends java.lang.Object, " +
                        "E extends java.util.List<T>> extends java.lang.Object  {\n" +
                        "    T field1;\n" +
                        "    E field2;\n" +
                        "    <B extends java.lang.Object> SomeClass(B arg0, T arg1) {\n" +
                        "    }\n" +
                        "    SomeClass(T arg0) {\n" +
                        "    }\n" +
                        "    public T genericMethod(java.util.List<? super E> arg0, " +
                        "java.util.Map<T, ? extends T> arg1) {\n" +
                        "        throw new UnsupportedOperationException();\n" +
                        "    }\n" +
                        "    static class genericNestedClass <B extends java.lang.Object> " +
                        "extends java.lang.Object  {\n" +
                        "        B field1;\n" +
                        "        genericNestedClass() {\n" +
                        "        }\n" +
                        "    }\n" +
                        "}");
    }

    @Test
    void diffClassesTestSame1() {
        Reflector.diffClasses(GenericClass1.class, GenericClass1Copy.class);
        assertEquals(DIFF_CLASSES_SAME, arrayOut.toString());
    }

    @Test
    void diffClassesTestSame2() {
        Reflector.diffClasses(Hashtable.class, HashtableCopy.class);
        assertEquals(DIFF_CLASSES_SAME, arrayOut.toString());
    }

    @Test
    void diffClassesTestDifferentFields() {
        Reflector.diffClasses(SimpleClass2.class, SimpleClass2Different.class);
        assertEquals(
                "first class unique fields:1\n" +
                "public int field1\n\n" +
                "second class unique fields:1\n" +
                "public java.util.List<java.lang.Object> field4\n\n" +
                "first class unique methods:0\n\n" +
                "second class unique methods:0\n", arrayOut.toString());
    }

    @Test
    void diffClassesTestDifferentMethods() {
        Reflector.diffClasses(Hashtable.class, HashtableDifferent.class);
        assertEquals("first class unique fields:0\n\n" +
                "second class unique fields:0\n\n" +
                "first class unique methods:2\n" +
                "public java.lang.String put(java.lang.String arg0, java.lang.String arg1)\n" +
                "private void copyContentTo(ClassName arg0)\n\n" +
                "second class unique methods:1\n" +
                "public void dummyMethod(int arg0, int arg1)\n", arrayOut.toString());
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
        Path tempDirectory = Files.createTempDirectory(projectRootFolderPath, "temp");
        String packageRelativeDir = clazz.getPackageName().replace('.', File.separatorChar);
        Path packagePath = Paths.get(tempDirectory + File.separator + packageRelativeDir);

        Files.createDirectories(packagePath);

        var compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null,
                "-d", tempDirectory.toString(), fileToCompile.getPath());

        var classLoader = new URLClassLoader(new URL[]{tempDirectory.toUri().toURL()});
        Class<?> someClass = classLoader.loadClass(clazz.getPackageName() + ".SomeClass");
        Reflector.diffClasses(clazz, someClass);
        assertEquals(DIFF_CLASSES_SAME, arrayOut.toString());

        fileToCompile.deleteOnExit();
        Files.walkFileTree(tempDirectory, new SimpleFileVisitor<Path>() {
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
}