package com.k2future.westdao.core.wsql.condition.interfaces;

import com.k2future.westdao.core.wsql.builder.JpqlBuilder;

/**
 * @author west
 * @since 2024/7/8
 */
public interface Join<Self> {


    /**
     * 内连接方法，用于在查询中添加  JOIN 子句。
     * 原来表别名默认为 e,可以通过{@link Condition#setEntityAlias}设置别名
     * 复杂条件可以通过{@link ConditionConnect#and} 或 {@link ConditionConnect#or} 添加
     *
     * @param condition 当前条件
     * @param alias     表 A 的别名
     * @return 返回当前实例，用于链式调用
     */
    Self join(JpqlBuilder<?> condition, String alias);

}
