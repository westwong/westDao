package cn.k2future.westdao.core.wsql.condition.interfaces;

import java.io.Serializable;

/**
 * 查询构建接口，用于定义查询操作和选择列。
 * <p>
 * Interface for query construction, used to define query operations and select columns.
 * </p>
 *
 * @param <Self> 返回类型自身，用于链式调用
 * @param <R>    字段或列的类型
 * @author west
 * @since 2024/6/25
 */
public interface Select<Self, R> extends Serializable {

    /**
     * 指定要查询的列。
     * <p>
     * Specify the columns to be selected.
     * </p>
     *
     * @param columns 要选择的列
     * @return 返回类型自身，用于链式调用
     */
    Self select(R... columns);

    /**
     * 指定要查询的列。
     * <p>
     * Specify the columns to be selected.
     * </p>
     *
     * @param columns 要选择的列
     * @return 返回类型自身，用于链式调用
     */
    Self select(String columns);

    /**
     * 指定要查询并使用 DISTINCT 进行去重的列。
     * <p>
     * Specify the columns to be selected with DISTINCT to remove duplicates.
     * </p>
     *
     * @param columns 要选择的列
     * @return 返回类型自身，用于链式调用
     */
    Self selectDistinct(R... columns);

    /**
     * 指定要计算行数的列。
     * <p>
     * Specify the columns to count rows.
     * </p>
     *
     * @param columns 要计数的列
     * @return 返回类型自身，用于链式调用
     */
    Self selectCount(R... columns);

    /**
     * 生成查询语句
     * generate query statement
     * @return 生成查询语句
     */
    String selectJpql();

    /**
     * 获取查询结果的数量限制
     *
     * @return limit
     */
    Integer getLimit();

    /**
     * @param limit 限制查询结果的数量
     * @return 返回类型自身，用于链式调用
     */
    Self limit(Integer limit);
}
