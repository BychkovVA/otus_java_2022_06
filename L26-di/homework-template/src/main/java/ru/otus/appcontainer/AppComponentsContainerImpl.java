package ru.otus.appcontainer;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        // You code here...
        try {
            Object exClass = configClass.getConstructor().newInstance();
            Method[] methods = Arrays.stream(configClass.getMethods())
                    .filter(method -> method.isAnnotationPresent(AppComponent.class))
                    .sorted(Comparator.comparingInt(o ->o.getAnnotation(AppComponent.class).order()))
                    .toArray(Method[]::new);
            for ( Method method : methods)
            {
                AppComponent appComponent = method.getAnnotation(AppComponent.class);
                Object exMethod = callMethod(exClass,method);
                if (getAppComponent(appComponent.name()) != null) {
                    throw new RuntimeException("Method " + appComponent.name() + "dublicate");
                }
                appComponentsByName.put(appComponent.name(), exMethod);
                appComponents.add(exMethod);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object callMethod(Object object, Method method) {
        try {
            method.setAccessible(true);
            Parameter[] params = method.getParameters();
            Object[] paramList = new Object[params.length];
            int i = 0;
            for (Parameter param : params) {
                paramList[i] = getAppComponent(param.getType());
                i++;
            }
            return method.invoke(object,paramList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        return (C) appComponents.stream().filter(p ->
                    componentClass.isAssignableFrom(p.getClass())
                ).findFirst().get();
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        return (C) appComponentsByName.get(componentName);
    }
}
