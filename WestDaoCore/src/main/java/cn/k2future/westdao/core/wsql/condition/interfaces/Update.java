package cn.k2future.westdao.core.wsql.condition.interfaces;

import java.io.Serializable;

/**
 * 更新操作接口，用于定义更新操作和设置更新字段值。
 * <p>
 * Interface for update operations, used to define update operations and set values for update columns.
 * </p>
 * @param <Entity> 实体类型
 * @param <Self> 返回类型自身，用于链式调用
 * @param <R>    字段或列的类型
 * @author west
 * @since 2024/6/25
 */
public interface Update<Entity,Self, R> extends Serializable {

    /**
     * 更新实体
     * Update entity
     * @param entity 实体
     * @return 返回类型自身，用于链式调用
     */
    Self update(Entity entity);

    /**
     * 设置指定字段的更新值。
     * <p>
     * Set the value for the specified column to be updated.
     * </p>
     *
     * @param column 要更新的字段
     * @param val    字段的新值
     * @return 返回类型自身，用于链式调用
     */
    Self set(R column, Object val);

    /**
     * 生成查询语句
     * generate query statement
     * @return 生成修改语句
     */
    String updateJpql();

}
