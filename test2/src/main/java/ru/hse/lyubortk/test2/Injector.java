package ru.hse.lyubortk.test2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Injector class for automatic initialization of object and its dependencies
 */
public class Injector {
    /**
     * Creates and initializes object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     * @throws ImplementationNotFoundException if implementation isn't found in implementationClassNames
     * @throws AmbiguousImplementationException if there is multiple implementation which could be
     * instantiated for object creation
     * @throws InjectionCycleException if cycle dependency is found
     * @throws IllegalAccessException if constructors of some argument classes are private
     * @throws InvocationTargetException if problem with constructor ocured
     * @throws InstantiationException if problem with constructor ocured
     */
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        Class targetClass = Class.forName(rootClassName);
        var implementationClasses = new ArrayList<Class>();

        for (var className : implementationClassNames) {
            implementationClasses.add(Class.forName(className));
        }

        return dfsCreate(targetClass, implementationClasses, new HashMap<>());
    }

    private static Object dfsCreate(Class target, List<Class> implementations, Map<Class, Object> created)
            throws ImplementationNotFoundException, AmbiguousImplementationException, InjectionCycleException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor constructor = target.getDeclaredConstructors()[0];

        Class<?>[] dependencies = constructor.getParameterTypes();
        List<Object> createdDependecies = new ArrayList<>();

        for (var clazz : dependencies) {
            List<Class> validImplementations = implementations.stream()
                    .filter(clazz::isAssignableFrom).collect(Collectors.toList());

            if (validImplementations.size() == 0) {
                throw new ImplementationNotFoundException();
            }

            if (validImplementations.size() > 1) {
                throw new AmbiguousImplementationException();
            }

            Class dependencyClass = validImplementations.get(0);

            if (!created.containsKey(dependencyClass)) {
                created.put(dependencyClass, null);
                createdDependecies.add(dfsCreate(validImplementations.get(0), implementations, created));
            } else if (created.get(dependencyClass) == null) {
                throw new InjectionCycleException();
            } else {
                createdDependecies.add(created.get(dependencyClass));
            }
        }

        Object resultObject = constructor.newInstance(createdDependecies.toArray());
        created.put(target, resultObject);
        return resultObject;
    }
}