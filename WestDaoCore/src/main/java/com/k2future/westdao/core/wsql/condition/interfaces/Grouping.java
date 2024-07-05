package com.k2future.westdao.core.wsql.condition.interfaces;

import java.io.Serializable;

/**
 * 分组操作接口，用于构建分组语句的链式调用
 * <p>
 * Interface for grouping operations, used for chaining grouping statements.
 * </p>
 *
 * @param <Self> 返回类型自身，用于链式调用
 * @param <R>    分组字段或列的类型
 * @author west
 * @since 2024/6/25
 */
public interface Grouping<Self, R> extends Serializable {

    /**
     * 添加带条件的分组条件
     * <p>
     * Add grouping
     * </p>
     *
     * @param columns 分组字段或列
     * @return 返回类型自身，用于链式调用
     */
    default Self groupBy(R... columns) {
        return groupBy(true, columns);
    }

    /**
     * 添加带条件的分组条件
     * <p>
     * Add grouping
     * </p>
     *
     * @param append  是否写入
     * @param columns 分组字段或列
     * @return 返回类型自身，用于链式调用
     */
    Self groupBy(boolean append, R... columns);


    /**
     * 提供向查询构建器实例添加 HAVING 子句条件的方法。
     * 此方法允许基于给定的条件字符串添加一个 HAVING 条件。
     *
     * @param condition 要添加到查询构建器的 HAVING 条件字符串。
     * @return 返回类型自身，用于链式调用。
     */
    default Self having(String condition) {
        return having(true, condition);
    }

    /**
     * 提供有条件地向查询构建器实例添加 HAVING 子句条件的方法。
     * 此方法允许根据给定的条件字符串和布尔标志添加 HAVING 条件。
     *
     * @param append    是否写入
     * @param condition 要添加到查询构建器的 HAVING 条件字符串。
     * @return 返回类型自身，用于链式调用
     */
    Self having(boolean append, String condition);

    /**
     * 在JPQL 最后增加条件 一般用于 limit
     *
     * @param condition 条件
     * @return 返回类型自身，用于链式调用
     */
    default Self last(String condition) {
        return having(true, condition);
    }

    /**
     * 在JPQL 最后增加条件 一般用于 limit
     *
     * @param append    是否写入
     * @param condition 要添加到查询构建器的 HAVING 条件字符串。
     * @return 返回类型自身，用于链式调用
     */
    Self last(boolean append, String condition);
}
