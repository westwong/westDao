package cn.k2future.westdao.core.wsql.condition.interfaces;

import java.io.Serializable;

/**
 * 排序操作接口，用于构建排序语句的链式调用
 * 多个oderBy 只有最后一个生效
 * <p>
 * Interface for ordering operations, used for chaining sorting statements.
 * Only the last orderBy is valid
 * </p>
 *
 * @param <Self> 返回类型自身，用于链式调用
 * @param <R>    排序字段或列的类型
 * @author west
 * @since 2024/6/25
 */
public interface Ordering<Self, R> extends Serializable {

    /**
     * 是否按列升序排序
     *
     * @param columns 排序字段或列
     * @return 返回类型自身，用于链式调用
     */
    @SuppressWarnings("unchecked")
    default Self orderByAsc(R... columns) {
        return orderByAsc(true, columns);
    }

    /**
     * 是否按列升序排序
     *
     * @param append  是否写入
     * @param columns 排序字段或列
     * @return 返回类型自身，用于链式调用
     */
    @SuppressWarnings("unchecked")
    Self orderByAsc(boolean append, R... columns);

    /**
     * 是否按列降序
     *
     * @param columns 排序字段或列
     * @return 返回类型自身，用于链式调用
     */
    @SuppressWarnings("unchecked")
    default Self orderByDesc(R... columns) {
        return orderByDesc(true, columns);
    }

    /**
     * 是否按列降序
     *
     * @param append  是否写入
     * @param columns 排序字段或列
     * @return 返回类型自身，用于链式调用
     */
    @SuppressWarnings("unchecked")
    Self orderByDesc(boolean append, R... columns);

    /**
     * 单列升序
     * 
     * @param append 是否写入
     * @param column 排序字段或列
     * @return
     */
    @SuppressWarnings("unchecked")
    default Self orderByAsc(boolean append, R column) {
        return orderByAsc(append, (R[]) new Object[] { column });
    }

    /**
     * 单列降序
     * 
     * @param append 是否写入
     * @param column 排序字段或列
     * @return 返回类型自身，用于链式调用
     */
    @SuppressWarnings("unchecked")
    default Self orderByDesc(boolean append, R column) {
        return orderByDesc(append, (R[]) new Object[] { column });
    }

    /**
     * 单列升序
     * 
     * @param column 排序字段或列
     * @return 返回类型自身，用于链式调用
     */
    default Self orderByAsc(R column) {
        return orderByAsc(true, column);
    }

    /**
     * 单列降序
     * 
     * @param column 排序字段或列
     * @return 返回类型自身，用于链式调用
     */
    default Self orderByDesc(R column) {
        return orderByDesc(true, column);
    }

    /**
     * 最后一条语句
     * 
     * @param last 最后一条语句
     * @return 返回类型自身，用于链式调用
     */
    default Self last(String last) {
        return last(true, last);
    }

    /**
     * 最后一条语句
     * 
     * @param last 最后一条语句
     * @return 返回类型自身，用于链式调用
     */
    Self last(boolean append, String last);

}
