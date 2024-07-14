package cn.k2future.westdao.core.domain;

import cn.k2future.westdao.core.handler.WestDao;
import cn.k2future.westdao.core.wsql.executor.LambdaDelete;
import cn.k2future.westdao.core.wsql.executor.LambdaQuery;
import cn.k2future.westdao.core.wsql.executor.LambdaUpdate;
import cn.k2future.westdao.core.wsql.executor.interfaces.WestDelete;
import cn.k2future.westdao.core.wsql.executor.interfaces.WestQuery;
import cn.k2future.westdao.core.wsql.executor.interfaces.WestUpdate;

import java.lang.reflect.InvocationTargetException;

/**
 * 智慧执行，简单工具
 * Wise execution, simple tools
 * 本类提供静态工厂方法来创建各种构建器和执行器的实例，用于 CRUD 操作和查询。
 *
 * @author west
 * @since 2024/6/24
 */
public final class West {

    private West() {
    }

    /**
     * 创建一个给定类的实例，该类必须实现 WestDao 接口。
     *
     * @param clazz 表示要实例化的类的 Class 对象
     * @param <T>   要创建的对象的类型
     * @return 指定类的实例
     * @throws RuntimeException 如果类无法实例化或不实现 WestDao 接口
     */
    public static <T> T dao(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            if (!(instance instanceof WestDao)) {
                throw new IllegalArgumentException("该类不是 WestDao 的实现类");
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException("无法创建类的实例：" + clazz.getName(), e);
        }
    }

    /**
     * 创建一个用于更新操作的 LambdaUpdate。
     * 可以调用 {@link WestUpdate}的方法来执行更新操作
     * @param <T> 实体类的类型
     * @return 一个新的 LambdaUpdate 实例
     */
    public static <T> LambdaUpdate<T> updateJPQL() {
        return new LambdaUpdate<>();
    }

    /**
     * 创建一个用于更新操作的 LambdaUpdate。
     * 可以调用 {@link WestUpdate}的方法来执行更新操作
     * @param clazz 表示要操作的实体类的 Class 对象
     * @param <T>   实体类的类型
     * @return 一个新的 LambdaUpdate 实例
     */
    public static <T> LambdaUpdate<T> updateJPQL(Class<T> clazz) {
        return new LambdaUpdate<>(clazz);
    }

    /**
     * 创建一个用于更新操作的 LambdaUpdate。
     * 可以调用 {@link WestUpdate}的方法来执行更新操作
     * @param entity 要操作的实体实例
     * @param <T>    实体类的类型
     * @return 一个新的 LambdaUpdate 实例
     */
    public static <T> LambdaUpdate<T> updateJPQL(T entity) {
        return new LambdaUpdate<>(entity);
    }

    /**
     * 创建一个用于查询操作的 LambdaQuery。
     * 可以调用 {@link WestQuery}的方法来执行查询操作
     *
     * @param clazz 表示要操作的实体类的 Class 对象
     * @param <T>   实体类的类型
     * @return 一个新的 LambdaQuery 实例
     */
    public static <T> LambdaUpdate<T> queryJPQL(Class<T> clazz) {
        return new LambdaUpdate<>(clazz);
    }

    /**
     * 创建一个用于查询操作的 LambdaQuery。
     * 可以调用 {@link WestQuery}的方法来执行查询操作
     * @param <T> 实体类的类型
     * @return 一个新的 LambdaQuery 实例
     */
    public static <T> LambdaQuery<T> queryJPQL() {
        return new LambdaQuery<>();
    }

    /**
     * 创建一个用于查询操作的 LambdaQuery。
     * 可以调用 {@link WestQuery}的方法来执行查询操作
     * @param entity 要操作的实体实例
     * @param <T>    实体类的类型
     * @return 一个新的 LambdaQuery 实例
     */
    public static <T> LambdaQuery<T> queryJPQL(T entity) {
        return new LambdaQuery<>(entity);
    }

    /**
     * 创建一个用于删除操作的 LambdaDelete。
     * 可以调用 {@link WestDelete}的方法来执行删除操作
     * @param <T> 实体类的类型
     * @return 一个新的 LambdaDelete 实例
     */
    public static <T> LambdaDelete<T> deleteJPQL() {
        return new LambdaDelete<>();
    }

    /**
     * 创建一个用于删除操作的 LambdaDelete。
     * 可以调用 {@link WestDelete}的方法来执行删除操作
     * @param clazz 表示要操作的实体类的 Class 对象
     * @param <T>   实体类的类型
     * @return 一个新的 LambdaDelete 实例
     */
    public static <T> LambdaDelete<T> deleteJPQL(Class<T> clazz) {
        return new LambdaDelete<>(clazz);
    }

    /**
     * 创建一个用于删除操作的 LambdaDelete。
     * 可以调用 {@link WestDelete}的方法来执行删除操作
     * @param entity 要操作的实体实例
     * @param <T>    实体类的类型
     * @return 一个新的 LambdaDelete 实例
     */
    public static <T> LambdaDelete<T> deleteJPQL(T entity) {
        return new LambdaDelete<>(entity);
    }
}
