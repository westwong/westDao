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
     * @param columns    分组字段或列
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
     * @param append 是否写入
     * @param columns    分组字段或列
     * @return 返回类型自身，用于链式调用
     */
    Self groupBy(boolean append, R... columns);
}
