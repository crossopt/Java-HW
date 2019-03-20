package ru.hse.crossopt.Reflector;

import ru.hse.crossopt.Reflector.testClasses.*;
import com.google.googlejavaformat.java.FormatterException;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.jupiter.api.Assertions.*;

class ReflectorTest {
    @Test
    void testPrintStructure_primitiveTypes() throws FormatterException, IOException, ClassNotFoundException {
        testCompileDiff(TestPrimitives.class);
    }

    @Test
    void testPrintStructure_genericTypes() throws FormatterException, IOException, ClassNotFoundException {
        testCompileDiff(TestGenerics.class);
    }

    @Test
    void testPrintStructure_wildcardTypes() throws FormatterException, IOException, ClassNotFoundException {
        testCompileDiff(TestWildcards.class);
    }

    @Test
    void testPrintStructure_inheritance() throws FormatterException, IOException, ClassNotFoundException {
        testCompileDiff(TestInheritance.class);
    }

    @Test
    void testPrintStructure_inner() throws FormatterException, IOException, ClassNotFoundException {
        testCompileDiff(TestInner.class);
    }

    @Test
    void testPrintStructure_metaTest() throws FormatterException, IOException, ClassNotFoundException {
        Reflector.printStructure(Reflector.class);
        var sourceFile = new File("Reflector.java");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, sourceFile.getPath());
        var classLoader = URLClassLoader.newInstance(new URL[]{(new File(".")).toURI().toURL()});
        var loadedClass = Class.forName("ru.hse.crossopt.Reflector.Reflector", true, classLoader);

        try (var outStream = new ByteArrayOutputStream()) {
            Reflector.diffClasses(Reflector.class, loadedClass, System.out);
            assertEquals(0, outStream.size());
        }
    }

    private void testCompileDiff(Class<?> clazz) throws IOException,
            ClassNotFoundException, FormatterException {
        Reflector.printStructure(clazz);
        var sourceFile = new File(clazz.getSimpleName() + ".java");
        var currentDir = new File(".");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, sourceFile.getPath());
        var classLoader = URLClassLoader.newInstance(new URL[]{currentDir.toURI().toURL()});
        var loadedClass = Class.forName("ru.hse.crossopt.Reflector.testClasses."
                + clazz.getSimpleName(), true, classLoader);

        try (var outStream = new ByteArrayOutputStream()) {
            Reflector.diffClasses(clazz, loadedClass, System.out);
            assertEquals(0, outStream.size());
        }
    }

    @Test
    void testDiff_noDifference() {
        var output = new ByteArrayOutputStream();
        var printStream = new PrintStream(output);
        assertDoesNotThrow(() -> Reflector.diffClasses(SimpleClass.class, SimpleClass.class, printStream));
        assertEquals("", output.toString().trim());
        var output2 = new ByteArrayOutputStream();
        var printStream2 = new PrintStream(output2);
        assertDoesNotThrow(() -> Reflector.diffClasses(TestGenerics.class, TestGenerics.class, printStream2));
        assertEquals("", output2.toString().trim());
    }

    @Test
    void testDiff_isDifference() {
        var output = new ByteArrayOutputStream();
        var printStream = new PrintStream(output);
        assertDoesNotThrow(() -> Reflector.diffClasses(SimpleClass.class, AlmostSimpleClass.class, printStream));
        assertEquals("public long field5", output.toString().trim());
        var output2 = new ByteArrayOutputStream();
        var printStream2 = new PrintStream(output2);
        assertDoesNotThrow(() -> Reflector.diffClasses(SimpleClass.class, NearlySimpleClass.class, printStream2));
        assertEquals("private void method3()", output2.toString().trim());
    }

    @Test
    void testDiff_genericsWildcardsAndCo() {
        var output = new ByteArrayOutputStream();
        var printStream = new PrintStream(output);
        assertDoesNotThrow(() -> Reflector.diffClasses(NotSimpleClass.class, AlsoNotSimpleClass.class, printStream));
        var result = output.toString();

        var possibleOutput1 = new ByteArrayOutputStream();
        var possibleResult1 = new PrintStream(possibleOutput1);
        possibleResult1.println(" java.lang.Class<? extends T> f3() throws IllegalAccessError");
        possibleResult1.println(" java.lang.Class<? extends T> method1() throws IllegalAccessError, LayerInstantiationException");

        var possibleOutput2 = new ByteArrayOutputStream();
        var possibleResult2 = new PrintStream(possibleOutput2);
        possibleResult2.println(" java.lang.Class<? extends T> method1() throws IllegalAccessError, LayerInstantiationException");
        possibleResult2.println(" java.lang.Class<? extends T> f3() throws IllegalAccessError");

        assertTrue(result.equals(possibleOutput1.toString()) || result.equals(possibleOutput2.toString()));
    }
}