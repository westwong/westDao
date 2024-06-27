package com.k2future.westdao.core.domain;

import com.k2future.westdao.core.handler.WestDao;
import com.k2future.westdao.core.wsql.builder.LambdaDeleteBuilder;
import com.k2future.westdao.core.wsql.builder.LambdaQueryBuilder;
import com.k2future.westdao.core.wsql.builder.LambdaUpdateBuilder;

import java.lang.reflect.InvocationTargetException;

/**
 * 智慧执行，简单工具
 * Wise execution, simple tools
 *
 * @Author west
 * @Date 2024/6/24
 */
public final class West {

    private West() {
    }

    /**
     * 创建一个给定类的实例，该类必须实现 WestDao 接口。
     * Creates an instance of the given class which must implement the WestDao interface.
     *
     * @param clazz 表示要实例化的类的 Class 对象
     *              the Class object representing the class to instantiate
     * @param <T>   要创建的对象的类型
     *              the type of the object to be created
     * @return 指定类的实例
     * an instance of the specified class
     * @throws RuntimeException 如果类无法实例化或不实现 WestDao 接口
     *                          if the class cannot be instantiated or does not implement WestDao
     */
    public static <T> T dao(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            if (!(instance instanceof WestDao)) {
                throw new IllegalArgumentException("The class is not an instance of WestDao");
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
        }
    }

    /**
     * 创建一个用于更新操作的 LambdaQueryBuilder。
     * Creates a LambdaQueryBuilder for update operations.
     *
     * @param <T>   实体类的类型
     *              the type of the entity class
     * @return 一个新的 LambdaQueryBuilder 实例
     * a new LambdaQueryBuilder instance
     */
    public static <T> LambdaUpdateBuilder<T> lambdaUpdate() {
        return new LambdaUpdateBuilder<>();
    }

    /**
     * 创建一个用于更新操作的 LambdaQueryBuilder。
     * Creates a LambdaQueryBuilder for update operations.
     *
     * @param clazz 表示要操作的实体类的 Class 对象
     *              the Class object representing the entity class to operate on
     * @param <T>   实体类的类型
     *              the type of the entity class
     * @return 一个新的 LambdaQueryBuilder 实例
     * a new LambdaQueryBuilder instance
     */
    public static <T> LambdaUpdateBuilder<T> lambdaUpdate(Class<T> clazz) {
        return new LambdaUpdateBuilder<>(clazz);
    }

    /**
     * 创建一个用于更新操作的 LambdaQueryBuilder。
     * Creates a LambdaQueryBuilder for update operations.
     *
     * @param entity 要操作的实体实例
     *               the entity instance to operate on
     * @param <T>    实体类的类型
     *               the type of the entity class
     * @return 一个新的 LambdaQueryBuilder 实例
     * a new LambdaQueryBuilder instance
     */
    public static <T> LambdaUpdateBuilder<T> lambdaUpdate(T entity) {
        return new LambdaUpdateBuilder<>(entity);
    }

    /**
     * 创建一个用于查询操作的 LambdaQueryBuilder。
     * Creates a LambdaQueryBuilder for select operations.
     *
     * @param clazz 表示要操作的实体类的 Class 对象
     *              the Class object representing the entity class to operate on
     * @param <T>   实体类的类型
     *              the type of the entity class
     * @return 一个新的 LambdaQueryBuilder 实例
     * a new LambdaQueryBuilder instance
     */
    public static <T> LambdaQueryBuilder<T> lambdaQuery(Class<T> clazz) {
        return new LambdaQueryBuilder<>(clazz);
    }
    /**
     * 创建一个用于查询操作的 LambdaQueryBuilder。
     * Creates a LambdaQueryBuilder for select operations.
     *
     * @param <T>   实体类的类型
     *              the type of the entity class
     * @return 一个新的 LambdaQueryBuilder 实例
     * a new LambdaQueryBuilder instance
     */
    public static <T> LambdaQueryBuilder<T> lambdaQuery() {
        return new LambdaQueryBuilder<>();
    }

    /**
     * 创建一个用于查询操作的 LambdaQueryBuilder。
     * Creates a LambdaQueryBuilder for select operations.
     *
     * @param entity 要操作的实体实例
     *               the entity instance to operate on
     * @param <T>    实体类的类型
     *               the type of the entity class
     * @return 一个新的 LambdaQueryBuilder 实例
     * a new LambdaQueryBuilder instance
     */
    public static <T> LambdaQueryBuilder<T> lambdaQuery(T entity) {
        return new LambdaQueryBuilder<>(entity);
    }

    /**
     * 创建一个用于删除操作的 LambdaDeleteBuilder。
     * Creates a LambdaDeleteBuilder for delete operations.
     *
     * @param <T>   实体类的类型
     *              the type of the entity class
     * @return 一个新的 LambdaDeleteBuilder 实例
     * a new LambdaDeleteBuilder instance
     */
    public static <T> LambdaDeleteBuilder<T> lambdaDelete() {
        return new LambdaDeleteBuilder<>();
    }
    /**
     * 创建一个用于删除操作的 LambdaDeleteBuilder。
     * Creates a LambdaDeleteBuilder for delete operations.
     *
     * @param clazz 表示要操作的实体类的 Class 对象
     *              the Class object representing the entity class to operate on
     * @param <T>   实体类的类型
     *              the type of the entity class
     * @return 一个新的 LambdaDeleteBuilder 实例
     * a new LambdaDeleteBuilder instance
     */
    public static <T> LambdaDeleteBuilder<T> lambdaDelete(Class<T> clazz) {
        return new LambdaDeleteBuilder<>(clazz);
    }

    /**
     * 创建一个用于删除操作的 LambdaDeleteBuilder。
     * Creates a LambdaDeleteBuilder for delete operations.
     *
     * @param entity 要操作的实体实例
     *               the entity instance to operate on
     * @param <T>    实体类的类型
     *               the type of the entity class
     * @return 一个新的 LambdaDeleteBuilder 实例
     * a new LambdaDeleteBuilder instance
     */
    public static <T> LambdaDeleteBuilder<T> lambdaDelete(T entity) {
        return new LambdaDeleteBuilder<>(entity);
    }
}
