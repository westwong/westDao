package com.k2future.westdao.core.wsql.condition.interfaces;

import java.io.Serializable;
import java.util.Collection;

/**
 * 条件构建接口，用于定义各种条件操作，用于构建SQL查询语句。
 * <p>
 * Interface for condition building, used to define various conditional operations
 * for constructing SQL query statements.
 * </p>
 *
 * @param <Self> 返回类型自身，用于链式调用
 * @param <R>    字段或列的类型
 * @author west
 * @since 2024/6/25
 */
public interface Condition<Self, R> extends Serializable {
    /**
     * 设置别名
     *
     * @param alias 别名
     */
    Self setEntityAlias(String alias);
    /**
     * 添加等于条件
     *
     * @param append 是否增加
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    Self eq(boolean append, R column, Object val);

    /**
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    default Self eq(R column, Object val) {
        return eq(true, column, val);
    }

    /**
     * 添加不等于条件
     *
     * @param append 是否增加
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    Self ne(boolean append, R column, Object val);

    /**
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    default Self ne(R column, Object val) {
        return ne(true, column, val);
    }

    /**
     * 添加大于条件
     *
     * @param append 是否增加
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    Self gt(boolean append, R column, Object val);

    /**
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    default Self gt(R column, Object val) {
        return gt(true, column, val);
    }

    /**
     * 添加大于等于条件
     *
     * @param append 是否增加
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    Self ge(boolean append, R column, Object val);

    /**
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    default Self ge(R column, Object val) {
        return ge(true, column, val);
    }

    /**
     * 添加小于条件
     *
     * @param append 是否增加
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    Self lt(boolean append, R column, Object val);

    /**
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    default Self lt(R column, Object val) {
        return lt(true, column, val);
    }

    /**
     * 添加小于等于条件
     *
     * @param append 是否增加
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    Self le(boolean append, R column, Object val);

    /**
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    default Self le(R column, Object val) {
        return le(true, column, val);
    }

    /**
     * 添加 BETWEEN 条件
     *
     * @param append 是否增加
     * @param column 字段
     * @param val1   值1
     * @param val2   值2
     * @return 返回类型自身，用于链式调用
     */
    Self between(boolean append, R column, Object val1, Object val2);

    /**
     * @param column 字段
     * @param val1   值1
     * @param val2   值2
     * @return 返回类型自身，用于链式调用
     */
    default Self between(R column, Object val1, Object val2) {
        return between(true, column, val1, val2);
    }

    /**
     * 添加 NOT BETWEEN 条件
     *
     * @param append 是否增加
     * @param column 字段
     * @param val1   值1
     * @param val2   值2
     * @return 返回类型自身，用于链式调用
     */
    Self notBetween(boolean append, R column, Object val1, Object val2);

    /**
     * @param column 字段
     * @param val1   值1
     * @param val2   值2
     * @return 返回类型自身，用于链式调用
     */
    default Self notBetween(R column, Object val1, Object val2) {
        return notBetween(true, column, val1, val2);
    }

    /**
     * 添加 LIKE 条件
     * like %name%
     *
     * @param append 是否增加
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    Self like(boolean append, R column, Object val);

    /**
     * 添加 LIKE 条件
     * like %name%
     *
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    default Self like(R column, Object val) {
        return like(true, column, val);
    }

    /**
     * 添加 NOT LIKE 条件
     * not like "%name%"
     *
     * @param append 是否增加
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    Self notLike(boolean append, R column, Object val);

    /**
     * 添加 NOT LIKE 条件
     * not like "%name%"
     *
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    default Self notLike(R column, Object val) {
        return notLike(true, column, val);
    }

    /**
     * 添加左 LIKE 条件
     * like %name
     *
     * @param append 是否增加
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    Self likeLeft(boolean append, R column, Object val);

    /**
     * 添加左 LIKE 条件
     * like %name
     *
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    default Self likeLeft(R column, Object val) {
        return likeLeft(true, column, val);
    }

    /**
     * 添加右 LIKE 条件
     * like name%
     *
     * @param append 是否增加
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    Self likeRight(boolean append, R column, Object val);

    /**
     * 添加右 LIKE 条件
     * like name%
     *
     * @param column 字段
     * @param val    值
     * @return 返回类型自身，用于链式调用
     */
    default Self likeRight(R column, Object val) {
        return likeRight(true, column, val);
    }

    /**
     * 添加 IN 条件
     *
     * @param append 是否增加
     * @param column 字段
     * @param values 值集合
     * @return 返回类型自身，用于链式调用
     */
    Self in(boolean append, R column, Collection<?> values);

    /**
     * 添加 IN 条件
     *
     * @param column 字段
     * @param values 值集合
     * @return 返回类型自身，用于链式调用
     */
    default Self in(R column, Collection<?> values) {
        return in(true, column, values);
    }

    /**
     * 添加 notIn 条件
     *
     * @param append 是否增加
     * @param column 字段
     * @param values 值集合
     * @return 返回类型自身，用于链式调用
     */
    Self notIn(boolean append, R column, Collection<?> values);

    /**
     * 添加 notIn 条件
     *
     * @param column 字段
     * @param values 值集合
     * @return 返回类型自身，用于链式调用
     */
    default Self notIn(R column, Collection<?> values) {
        return notIn(true, column, values);
    }

    /**
     * 添加 IN 条件 jpql
     *
     * @param append 是否增加
     * @param colum  字段
     * @param jpql   jpql语句
     * @return 返回类型自身，用于链式调用
     */
    Self inJPQL(boolean append, R colum, String jpql);

    /**
     * 添加 IN 条件 jpql
     *
     * @param colum 字段
     * @param jpql  jpql语句
     * @return 返回类型自身，用于链式调用
     */
    default Self inJPQL(R colum, String jpql) {
        return inJPQL(true, colum, jpql);
    }

    /**
     * 添加 IS NULL 条件
     *
     * @param append 是否增加
     * @param column 字段
     * @return 返回类型自身，用于链式调用
     */
    Self isNull(boolean append, R column);

    /**
     * 添加 IS NULL 条件
     *
     * @param column 字段
     * @return 返回类型自身，用于链式调用
     */
    default Self isNull(R column) {
        return isNull(true, column);
    }

    /**
     * 添加 IS NOT NULL 条件
     *
     * @param append 是否增加
     * @param column 字段
     * @return 返回类型自身，用于链式调用
     */
    Self isNotNull(boolean append, R column);

    /**
     * 添加 IS NOT NULL 条件
     *
     * @param column 字段
     * @return 返回类型自身，用于链式调用
     */
    default Self isNotNull(R column) {
        return isNotNull(true, column);
    }
}