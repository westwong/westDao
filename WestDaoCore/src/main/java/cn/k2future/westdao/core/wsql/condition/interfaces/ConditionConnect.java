package cn.k2future.westdao.core.wsql.condition.interfaces;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * 条件连接语句
 *
 * @param <Self> 返回类型自身，用于链式调用
 * @author west
 * @since 2024/6/25
 */
public interface ConditionConnect<Self> extends Serializable {

    /**
     * and 条件连接 and ()
     *
     * @param consumer 提供条件链接构造器
     * @return 返回类型自身，用于链式调用
     */
    default Self and(Consumer<Self> consumer) {
        return and(true, consumer);
    }

    /**
     * and 条件连接 and ()
     *
     * @param append   是否写入
     * @param consumer 提供条件链接构造器
     * @return 返回类型自身，用于链式调用
     */
    Self and(boolean append, Consumer<Self> consumer);

    /**
     * ！！！
     * or 条件连接  or ()
     * 因为JPA 框架，or () 在执行过程中，会把 or ()的()去掉
     * Connecting OR conditions with parentheses.
     * Due to the JPA framework, parentheses around OR conditions are removed during execution.
     *
     * @param consumer 提供条件链接构造器
     * @return 返回类型自身，用于链式调用
     */
    default Self or(Consumer<Self> consumer) {
        return or(true, consumer);
    }

    /**
     * ！！！
     * or 条件连接  or ()
     * 因为JPA 框架，or () 在执行过程中，会把 or ()的()去掉
     * Connecting OR conditions with parentheses.
     * Due to the JPA framework, parentheses around OR conditions are removed during execution.
     *
     * @param append   是否写入
     * @param consumer 提供条件链接构造器
     * @return 返回类型自身，用于链式调用
     */
    Self or(boolean append, Consumer<Self> consumer);

    /**
     * AND + condition
     * String 方式添加条件
     *
     * @param append    是否增加
     * @param condition 条件
     * @return 返回类型自身，用于链式调用
     */
    Self and(boolean append, String condition);

    /**
     * AND + condition
     * String 方式添加条件
     *
     * @param condition 条件
     * @return 返回类型自身，用于链式调用
     */
    default Self and(String condition) {
        return and(true, condition);
    }

    /**
     * or + condition
     * String 方式添加条件
     *
     * @param append    是否增加
     * @param condition 条件
     * @return 返回类型自身，用于链式调用
     */
    Self or(boolean append, String condition);

    /**
     * or + condition
     * String 方式添加条件
     *
     * @param condition 条件
     * @return 返回类型自身，用于链式调用
     */
    default Self or(String condition) {
        return and(true, condition);
    }

}
