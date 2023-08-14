package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import ru.otus.crm.annotations.Id;


public class EntityClassMetaDataImpl<T> implements EntityClassMetaData {
    private final Class<T> targetClass;

    public EntityClassMetaDataImpl(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public String getName() {
        return targetClass.getSimpleName();
    };

    @Override
    public Constructor<T> getConstructor() {
        try {
            return targetClass.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            // вызываем ошибку
            throw new RuntimeException(e);
        }

    };

    @Override
    public Field getIdField() {
        return  Arrays.stream(targetClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .get();
    };

    @Override
    public List<Field> getAllFields() {
        List<Field> res = Arrays.asList(targetClass.getDeclaredFields());// = Arrays.stream(obj.getClass().getDeclaredFields()).collect(Collectors.toList());
        return res;
    };

    @Override
    public List<Field> getFieldsWithoutId() {
        return Arrays.stream(targetClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .collect(Collectors.toList());
    };
}
