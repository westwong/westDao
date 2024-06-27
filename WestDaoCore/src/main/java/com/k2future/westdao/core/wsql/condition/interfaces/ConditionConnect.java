package com.k2future.westdao.core.wsql.condition.interfaces;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 *  条件连接语句
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
    default Self and(Consumer<Self> consumer){
        return and(true,consumer);
    }

    /**
     * and 条件连接 and ()
     *
     * @param append 是否写入
     * @param consumer 提供条件链接构造器
     * @return 返回类型自身，用于链式调用
     */
    Self and(boolean append,Consumer<Self> consumer);
    /**
     * or 条件连接  or ()
     *
     * @return 返回类型自身，用于链式调用
     */
    default Self or(Consumer<Self> consumer){
        return or(true,consumer);
    }

    /**
     * or 条件连接  or ()
     *
     * @param append 是否写入
     * @return 返回类型自身，用于链式调用
     */
    Self or(boolean append,Consumer<Self> consumer);

}
