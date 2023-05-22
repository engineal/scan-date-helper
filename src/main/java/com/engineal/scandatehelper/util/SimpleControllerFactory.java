package com.engineal.scandatehelper.util;

import javafx.util.Callback;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleControllerFactory implements Callback<Class<?>, Object> {

    private final Map<Class<?>, Object> services;

    public SimpleControllerFactory(Set<Object> services) {
        this.services = services.stream().collect(Collectors.toMap(Object::getClass, Function.identity()));
    }

    @Override
    public Object call(Class<?> type) {
        try {
            for (Constructor<?> c : type.getConstructors()) {
                if (c.getParameterCount() > 0) {
                    try {
                        Class<?>[] parameterTypes = c.getParameterTypes();
                        Object[] args = new Object[c.getParameterCount()];
                        for (int i = 0; i < c.getParameterCount(); i++) {
                            args[i] = getService(parameterTypes[i]);
                        }
                        return c.newInstance(args);
                    } catch (NoClassDefFoundError ignored) {}
                }
            }
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getService(Class<?> parameterType) {
        for (Map.Entry<Class<?>, Object> service : services.entrySet()) {
            if (parameterType.isAssignableFrom(service.getKey())) {
                return service.getValue();
            }
        }
        throw new NoClassDefFoundError();
    }
}
