package ru.hse.crossopt.Injector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.hse.crossopt.Injector.testClasses.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InjectorTest {
    private static final String pathName = "ru.hse.crossopt.Injector.testClasses.";

    @Test
    public void injectorShouldInitializeClassWithoutDependencies()
            throws Exception {
        Object object = Injector.initialize(pathName + "ClassWithoutDependencies", Collections.emptyList());
        assertTrue(object instanceof ClassWithoutDependencies);
    }

    @Test
    public void injectorShouldInitializeClassWithOneClassDependency()
            throws Exception {
        Object object = Injector.initialize(
                pathName + "ClassWithOneClassDependency",
                Collections.singletonList(pathName + "ClassWithoutDependencies")
        );
        assertTrue(object instanceof ClassWithOneClassDependency);
        ClassWithOneClassDependency instance = (ClassWithOneClassDependency) object;
        assertTrue(instance.dependency != null);
    }

    @Test
    public void injectorShouldInitializeClassWithOneInterfaceDependency()
            throws Exception {
        Object object = Injector.initialize(
                pathName + "ClassWithOneInterfaceDependency",
                Collections.singletonList(pathName + "InterfaceImpl")
        );
        assertTrue(object instanceof ClassWithOneInterfaceDependency);
        ClassWithOneInterfaceDependency instance = (ClassWithOneInterfaceDependency) object;
        assertTrue(instance.dependency instanceof InterfaceImpl);
    }

    @Test
    public void injectorShouldFailOnCyclicDependency() throws Exception {
        assertThrows(InjectionCycleException.class, () -> Injector.initialize(
                pathName + "ClassWithCyclicDependencyOne",
                Collections.singletonList(pathName + "ClassWithCyclicDependencyTwo")));
        assertThrows(InjectionCycleException.class, () -> Injector.initialize(
                pathName + "ClassWithCyclicDependencyTwo",
                Collections.singletonList(pathName + "ClassWithCyclicDependencyOne")));
    }

    @Test
    public void injectorShouldFailOnTwoImplementations() throws Exception {
        List<String> classes = new ArrayList<>(Arrays.asList(pathName + "BaseClass", pathName + "DerivedClass"));
        assertThrows(AmbiguousImplementationException.class, () -> Injector.initialize(
                pathName + "AnotherClass", classes));
    }

    @Test
    public void injectorShouldFailOnNoImplementations() throws Exception {
        assertThrows(ImplementationNotFoundException.class, () -> Injector.initialize(
                pathName + "AnotherClass", Collections.emptyList()));

    }
}