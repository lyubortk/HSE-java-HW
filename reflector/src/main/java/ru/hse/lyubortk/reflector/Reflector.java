package ru.hse.lyubortk.reflector;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Reflector {
    public static void printStructure(@NotNull Class<?> someClass) throws IOException {
        var result = new StringBuilder();
        printPackage(someClass, result);
        printClassOrInterface(someClass, "SomeClass", result);
        result = repairIndentationAndTypes(someClass, result);
        writeToFile("SomeClass.java", result);
    }

    public static void printClassOrInterface(@NotNull Class<?> someClass,
                                             @NotNull String name,
                                             @NotNull StringBuilder output) {
        printDeclaration(someClass, name, output);
        printFields(someClass, output);
        printConstructors(someClass, name, output);
        printMethods(someClass, output);
        printSubclasses(someClass, output);
        output.append("}\n");
    }

    public static void diffClasses(Class<?> firstClass, Class<?> secondClass) {
        printDifferentFields(firstClass, secondClass);
        System.out.println();
        printDifferentMethods(firstClass, secondClass);
    }

    private static void printDifferentFields(Class<?> firstClass, Class<?> secondClass) {
        var firstFields = new HashSet<String>();
        var secondFields = new HashSet<String>();
        getAllFields(firstClass, firstFields);
        getAllFields(secondClass, secondFields);
        removeIntersection(firstFields, secondFields);
        printUnique(firstFields, secondFields, "fields");
    }

    private static void printDifferentMethods(Class<?> firstClass, Class<?> secondClass) {
        var firstMethods = new TreeSet<String>();
        var secondMethods = new TreeSet<String>();
        getAllMethods(firstClass, firstMethods);
        getAllMethods(secondClass, secondMethods);

        removeIntersection(firstMethods, secondMethods);
        printUnique(firstMethods, secondMethods, "methods");
    }

    private static void printUnique(Set<String> first, Set<String> second, String type) {
        System.out.println("first class unique " + type + ":" + first.size());
        for(var str: first) {
            System.out.println(str);
        }
        System.out.println();
        System.out.println("second class unique " + type + ":" + second.size());
        for(var str: second) {
            System.out.println(str);
        }
    }

    private static <T> void removeIntersection(Set<T> first, Set<T> second) {
        var intersection = new HashSet<>(first);
        intersection.retainAll(second);
        first.removeAll(intersection);
        second.removeAll(intersection);
    }

    private static void getAllFields(Class<?> clazz, Set<String> fieldSet) {
        if (clazz == null) {
            return;
        }
        for (var field: clazz.getDeclaredFields()) {
            String fixedField = declareField(field).replace('$', '.');
            fieldSet.add(fixedField.replace(clazz.getCanonicalName(), "ClassName"));
        }
        getAllFields(clazz.getSuperclass(), fieldSet);
    }

    private static void getAllMethods(Class<?> clazz, Set<String> methodSet) {
        if (clazz == null) {
            return;
        }
        for (var method: clazz.getDeclaredMethods()) {
            String fixedName = declareMethod(method).replace('$', '.');
            methodSet.add(fixedName.replace(clazz.getCanonicalName(), "ClassName"));
        }
        getAllMethods(clazz.getSuperclass(), methodSet);
    }

    private static void printPackage(@NotNull Class<?> someClass, @NotNull StringBuilder output) {
        output.append("package ").append(someClass.getPackageName()).append(";\n");
    }

    private static void printDeclaration(@NotNull Class<?> someClass,
                                         @NotNull String name,
                                         @NotNull StringBuilder output) {
        output.append('\n');
        output.append(declareModifiers(someClass.getModifiers()));
        if (!someClass.isInterface()) {
            output.append("class ");
        }

        output.append(name).append(' ');
        output.append(declareTypeParameters(someClass.getTypeParameters()));

        if (someClass.getGenericSuperclass() != null &&
                someClass.getGenericSuperclass() != Object.class) {
            output.append("extends ");
            output.append(someClass.getGenericSuperclass().getTypeName()).append(' ');
        }

        if (someClass.getGenericInterfaces().length > 0) {
            if (someClass.isInterface()) {
                output.append("extends ");
            } else {
                output.append("implements ");
            }
            output.append(
                    Arrays.stream(someClass.getGenericInterfaces())
                            .map(Type::getTypeName)
                            .collect(Collectors.joining(", "))
            );
            output.append(' ');
        }
        output.append("{\n");
    }

    private static void printFields(@NotNull Class<?> someClass, @NotNull StringBuilder output) {
        output.append('\n');
        for (var field : someClass.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            output.append(declareField(field)).append(";\n");
        }
    }

    private static void printConstructors(@NotNull Class<?> someClass,
                                          @NotNull String name,
                                          @NotNull StringBuilder output) {
        for (var constructor : someClass.getDeclaredConstructors()) {
            if (constructor.isSynthetic()) {
                continue;
            }
            output.append('\n');
            output.append(declareModifiers(constructor.getModifiers()));
            output.append(declareTypeParameters(constructor.getTypeParameters()));
            output.append(name).append('(');
            var genericTypes = constructor.getGenericParameterTypes();
            output.append(declareParameters(genericTypes));
            output.append(") ");
            output.append(declareExceptions(constructor.getGenericExceptionTypes()));
            output.append("{\n}\n");
        }
    }

    private static void printMethods(@NotNull Class<?> someClass, @NotNull StringBuilder output) {
        for (var method : someClass.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }
            output.append('\n');
            output.append(declareMethod(method));
            if (Modifier.isAbstract(method.getModifiers())) {
                output.append(";\n");
            } else {
                output.append(" {\nthrow new UnsupportedOperationException();\n}\n");
            }
        }
    }

    private static void printSubclasses(@NotNull Class<?> someClass,
                                        @NotNull StringBuilder output) {
        for (var clazz : someClass.getDeclaredClasses()) {
            printClassOrInterface(clazz, clazz.getSimpleName(), output);
        }
    }

    private static String declareField(Field field) {
        return declareModifiers(field.getModifiers())
                + field.getGenericType().getTypeName() + " " + field.getName();
    }

    private static String declareMethod(Method method) {
        return declareModifiers(method.getModifiers())
                + declareTypeParameters(method.getTypeParameters())
                + method.getGenericReturnType().getTypeName() + ' '
                + method.getName() + "("
                + declareParameters(method.getGenericParameterTypes()) + ") "
                + declareExceptions(method.getGenericExceptionTypes());
    }

    private static String declareParameters(Type[] types) {
        var output = new StringBuilder();
        for (int i = 0; i < types.length; ++i) {
            output.append(types[i].getTypeName()).append(' ').append("arg").append(i);
            if (i != types.length - 1) {
                output.append(", ");
            }
        }
        return output.toString();
    }

    private static String declareExceptions(Type[] types) {
        if (types.length > 0) {
            return "throws " + Arrays.stream(types)
                    .map(Type::getTypeName).collect(Collectors.joining(", ")) + ' ';
        }
        return "";
    }

    private static String declareTypeParameters(
            @NotNull TypeVariable<? extends GenericDeclaration>[] parameters) {
        var output = new StringBuilder();
        if (parameters.length > 0) {
            output.append('<');
            for (var parameter : parameters) {
                output.append(parameter.getName()).append(" extends ");
                output.append(
                        Arrays.stream(parameter.getBounds())
                                .map(Type::getTypeName).collect(Collectors.joining(" & "))
                );
                output.append(", ");
            }
            output.delete(output.length()-2, output.length());
            output.append("> ");
        }
        return output.toString();
    }

    private static String declareModifiers(int modifiers) {
        String modifiersString = Modifier.toString(modifiers);
        if (modifiersString.length() > 0) {
            return modifiersString + " ";
        }
        return "";
    }
    
    private static StringBuilder repairIndentationAndTypes(@NotNull Class<?> someClass,
                                                           StringBuilder input) {
        var output = new StringBuilder();
        int currentIndentation = 0;
        for (var line : input.toString().split("\n")) {
            line = fixTypes(someClass, line);
            if (line.length() != 0 && line.charAt(line.length() - 1) == '}')
                currentIndentation -= 4;

            String indentation = new String(new char[currentIndentation])
                    .replace('\0', ' ');
            output.append(indentation).append(line).append('\n');

            if (line.length() != 0 && line.charAt(line.length() - 1) == '{') {
                currentIndentation += 4;
            }
        }
        return output;
    }

    private static String fixTypes(@NotNull Class<?> someClass, @NotNull String source) {
        source = source.replace('$', '.');
        return source.replace(someClass.getCanonicalName(), "SomeClass");
    }

    private static void writeToFile(@NotNull String fileName, @NotNull StringBuilder source)
            throws IOException {
        var outputFIle = new File(fileName);
        try (var fileWriter = new FileWriter(outputFIle)) {
            try (var bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.write(source.toString());
            }
        }
    }
}
