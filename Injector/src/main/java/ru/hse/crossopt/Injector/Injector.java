package ru.hse.crossopt.Injector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Injector {
    private static Set<Class<?>> inProcessClasses;
    private static Map<Class<?>, Object> doneClasses;
    private static List<Class<?>> implementationClasses;

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws ClassNotFoundException, IllegalAccessException, AmbiguousImplementationException, ImplementationNotFoundException, InstantiationException, InjectionCycleException, InvocationTargetException {
        inProcessClasses = new HashSet<>();
        doneClasses = new HashMap<>();
        implementationClasses = new ArrayList<>();
        for (String name : implementationClassNames) {
            implementationClasses.add(Class.forName(name));
        }

        Class<?> rootClass = Class.forName(rootClassName); // todo exceptions
        if (!implementationClassNames.contains(rootClassName)) {
            implementationClasses.add(rootClass);
        }
        return getInstance(rootClass);
    }

    /**
     * Returns instance of class.
     * @param clazz a class to get instance of.
     * @return instance of the class.
     * @throws AmbiguousImplementationException if there is a dependency class with multiple implementations.
     * @throws InjectionCycleException if there is a cyclic dependence.
     * @throws ImplementationNotFoundException if there is a dependency class with no implementations.
     * @throws IllegalAccessException if illegal access happened while trying to construct instance of class from parameters.
     * @throws InvocationTargetException if invoked constructor threw exception while trying to construct instance of class from parameters.
     * @throws InstantiationException if instantiation failure happened while trying to construct instance of class from parameters.
     */
    private static Object getInstance(Class<?> clazz) throws InjectionCycleException, AmbiguousImplementationException, ImplementationNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (inProcessClasses.contains(clazz)) {
            throw new InjectionCycleException("Cyclic dependency for class " + clazz.getSimpleName());
        }
        if (doneClasses.containsKey(clazz)) {
            return doneClasses.get(clazz);
        }

        inProcessClasses.add(clazz);
        Constructor<?> constructor = clazz.getConstructors()[0];
        Class<?>[] parameterTypes = constructor.getParameterTypes();

        List<Object> parameters = new ArrayList<>();
        for (Class<?> parameterType: parameterTypes) {
            Class<?> goodCandidate = null;
            for (Class<?> candidate : implementationClasses) {
                if (parameterType.isAssignableFrom(candidate)) {
                    if (goodCandidate == null) {
                        goodCandidate = candidate;
                    } else {
                        throw new AmbiguousImplementationException("Multiple implementations for class " + parameterType.getSimpleName());
                    }
                }
            }
            if (goodCandidate == null) {
                throw new ImplementationNotFoundException("No implementation for class " + parameterType.getSimpleName());
            } else {
                parameters.add(getInstance(goodCandidate));
            }
        }
        inProcessClasses.remove(clazz);
        Object instance = constructor.newInstance(parameters.toArray());
        doneClasses.put(clazz, instance);
        return instance;
    }
}