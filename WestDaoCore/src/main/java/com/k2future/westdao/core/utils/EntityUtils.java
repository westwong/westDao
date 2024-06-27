package com.k2future.westdao.core.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author west
 * @since 2024/6/23
 */
public class EntityUtils {
    /**
     * parse entity id
     *
     * @param entity entity
     * @param <T>    entity
     * @return id
     */
    public static <T> Object parseId(T entity) {
        Class<?> clazz = entity.getClass();
        // 获取实体类的主键字段
        Field idField = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(javax.persistence.Id.class)) {
                idField = field;
                break;
            }
        }
        if (idField == null) {
            throw new IllegalArgumentException("Entity does not have @Id annotation");
        }
        idField.setAccessible(true);
        try {
            return idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access id field", e);
        }

    }

    /**
     * getClazz
     *
     * @param entity entity
     * @param <T>    entity
     * @return clazz
     */
    public static <T> Class<T> getClazz(T entity) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) entity.getClass();
        return clazz;
    }

    /**
     * Parse the entity to get a map of property names and their corresponding values
     *
     * @param entity The entity to parse
     * @param <T>    The type of the entity
     * @return A map containing property names as keys and their values as values
     */
    public static <T> Map<String, Object> parseEntity(T entity) {
        Map<String, Object> propertyValues = new HashMap<>();
        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                String propertyName = field.getName();
                Object propertyValue = field.get(entity);
                propertyValues.put(propertyName, propertyValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access property value", e);
            }
        }
        return propertyValues;
    }
}
