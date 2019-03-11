package ru.hse.crossopt.Reflector;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

/** Class that can print structure of a given class and compare structures of two classes. */
public class Reflector {
    private static String packageName;

    /**
     * Writes structure of given class to file.
     * @param clazz a class the structure of which will be printed.
     * @throws IOException if writing to file failed.
     * @throws FormatterException if formatting the resulting code to look pretty failed.
     */
    public static void printStructure(Class<?> clazz) throws IOException, FormatterException {
        var sourceCode = new StringBuilder();
        packageName = clazz.getPackageName();
        sourceCode.append("package ").append(packageName).append(";");
        printImports(clazz, sourceCode);
        printClass(clazz, sourceCode);
        try (var writer = new FileWriter(clazz.getSimpleName() + ".java")) {
            String prettyCode = new Formatter().formatSourceAndFixImports(sourceCode.toString());
            writer.write(prettyCode);
        }
    }

    private static void printClass(Class<?> clazz, StringBuilder output) {
        output.append(Modifier.toString(clazz.getModifiers())).append(" class ");
        printParametrizedName(clazz, output);
        printParent(clazz.getSuperclass(), output);
        printInterfaces(clazz.getInterfaces(), output);
        output.append("{");
        printFields(clazz, output);
        printConstructors(clazz, output);
        printMethods(clazz, output);
        printClasses(clazz, output);
        output.append("}");
    }

    private static String stripPackageName(String className) {
        if (packageName != null && className.startsWith(packageName)) {
            return className.substring(packageName.length() + 1);
        }
        return className;
    }

    private static void printClasses(Class<?> clazz, StringBuilder output) {
        for (var innerClass: clazz.getDeclaredClasses()) {
            printClass(innerClass, output);
        }
    }

    private static void printInterfaces(Class<?>[] interfaces, StringBuilder output) {
        if (interfaces.length > 0) {
            output.append(" implements ");
            var joiner = new StringJoiner(", ");
            for (var i : interfaces) {
                joiner.add(i.getSimpleName());
            }
            output.append(joiner.toString());
        }
    }

    private static void printFields(Class<?> clazz, StringBuilder output) {
        for (var field : clazz.getDeclaredFields()) {
            if (!field.isSynthetic()) {
                output.append(getField(field));
            }
        }
    }

    private static String getField(Field field) {
        return getFieldData(field) + "=" + getDefaultValue(field.getGenericType()) + ";";
    }

    private static String getFieldData(Field field) {
        return Modifier.toString(field.getModifiers()) + " " +
                stripPackageName(field.getGenericType().getTypeName()) + " " + field.getName();
    }

    private static void printConstructors(Class<?> clazz, StringBuilder output) {
        for (var constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isSynthetic()) {
                continue;
            }
            output.append(Modifier.toString(constructor.getModifiers())).append(" ").
                    append(constructor.getDeclaringClass().getSimpleName());
            printArguments(constructor, output);
            printExceptions(constructor, output);
            output.append("{}");
        }
    }

    private static void printMethods(Class<?> clazz, StringBuilder output) {
        for (var method : clazz.getDeclaredMethods()) {
            if (!method.isSynthetic()) {
                output.append(getMethod(method));
            }
        }
    }

    private static String getMethod(Method method) {
        return getMethodData(method) + "{return " + getDefaultValue(method.getGenericReturnType()) + ";}";
    }

    private static String getMethodData(Method method) {
        var result = new StringBuilder();
        result.append(Modifier.toString(method.getModifiers())).append(" ");
        printTypes(method.getTypeParameters(), result);
        result.append(stripPackageName(method.getGenericReturnType().getTypeName()))
                .append(" ").append(method.getName());
        printArguments(method, result);
        printExceptions(method, result);
        return result.toString();
    }

    private static void printExceptions(Executable method, StringBuilder output) {
        var printed = new StringJoiner(", ", " throws ", "");
        printed.setEmptyValue("");
        for (var exception : method.getExceptionTypes()) {
            printed.add(exception.getSimpleName());
        }
        output.append(printed);
    }

    private static void printArguments(Executable method, StringBuilder output) {
        int i = 0;
        var printed = new StringJoiner(", ", "(", ")");
        for (var parameter : method.getGenericParameterTypes()) {
            printed.add(stripPackageName(parameter.getTypeName()) + " arg" + i++);
        }
        output.append(printed);
    }

    private static void printParametrizedName(Class<?> clazz, StringBuilder output) {
        output.append(clazz.getSimpleName());
        printTypes(clazz.getTypeParameters(), output);
    }

    private static void printParent(Class<?> parent, StringBuilder output) {
        if (parent != null && parent != Object.class) {
            output.append(" extends ");
            output.append(parent.getSimpleName());
        }
    }

    private static void printTypes(TypeVariable<?>[] typeParameters, StringBuilder output) {
        var printed = new StringJoiner(", ", "<", ">");
        printed.setEmptyValue("");
        for (var type : typeParameters) {
            printed.add(stripPackageName(type.getTypeName()));
        }
        output.append(printed);
    }

    /**
     * Prints the fields and methods present only in one of two given classes.
     * @param aClass the first class.
     * @param bClass the second class.
     * @param output the stream to write difference to.
     */
    public static void diffClasses(Class<?> aClass, Class<?> bClass, PrintStream output) {
        diffFields(aClass, bClass, output);
        diffMethods(aClass, bClass, output);
    }

    private static void diffMethods(Class<?> aClass, Class<?> bClass, PrintStream output) {
        List<String> aMethods = Arrays.stream(aClass.getDeclaredMethods()).map(Reflector::getMethodData).
                collect(Collectors.toList());
        List<String> bMethods = Arrays.stream(bClass.getDeclaredMethods()).map(Reflector::getMethodData).
                collect(Collectors.toList());
        printListDifference(bMethods, aMethods, output);
        printListDifference(aMethods, bMethods, output);
    }

    private static void diffFields(Class<?> aClass, Class<?> bClass, PrintStream output) {
        List<String> aFields = Arrays.stream(aClass.getDeclaredFields()).map(Reflector::getFieldData).
                collect(Collectors.toList());
        List<String> bFields = Arrays.stream(bClass.getDeclaredFields()).map(Reflector::getFieldData).
                collect(Collectors.toList());
        printListDifference(aFields, bFields, output);
        printListDifference(bFields, aFields, output);
    }

    private static void printListDifference(List<String> aList, List<String> bList, PrintStream output) {
        for (var aElement : aList) {
            boolean wasFound = false;
            for (var bElement : bList) {
                wasFound = wasFound | aElement.equals(bElement);
            }
            if (!wasFound) {
                output.println(aElement);
            }
        }
    }

    private static String getDefaultValue(Type type) {
        if (!(type instanceof Class) || !((Class<?>)type).isPrimitive()) {
            return null;
        } if (type == boolean.class) {
            return "false";
        } else if (type == char.class) {
            return "'\\0'";
        } else if (type == void.class) {
            return "";
        } else {
            return "0";
        }
    }

    private static void printImports(Class<?> clazz, StringBuilder output) {
        if (clazz.getSuperclass() != null) {
            addImports(clazz.getSuperclass(), output);
        }
        for (var superclass : clazz.getInterfaces()) {
            addImports(superclass, output);
        }
        for (var field : clazz.getDeclaredFields()) {
            addImports(field.getType(), output);
        }
        for (var constructor : clazz.getDeclaredConstructors()) {
            for (var parameter : constructor.getParameterTypes()) {
                addImports(parameter, output);
            }
        }
        for (var method : clazz.getDeclaredMethods()) {
            for (var parameter : method.getParameterTypes()) {
                addImports(parameter, output);
            }
            for (var exception : method.getExceptionTypes()) {
                addImports(exception, output);
            }
            addImports(method.getReturnType(), output);
        }
    }

    private static void addImports(Class<?> type, StringBuilder output) {
        if (!type.isPrimitive() && type != Object.class) {
         if (type.isArray()) {
                addImports(type.getComponentType(), output);
            } else {
                output.append("import ").append(type.getCanonicalName()).append(";");
            }
        }
    }
}
