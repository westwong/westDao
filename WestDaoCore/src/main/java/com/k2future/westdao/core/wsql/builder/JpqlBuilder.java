package com.k2future.westdao.core.wsql.builder;

import com.k2future.westdao.core.wsql.unit.JpqlQuery;

/**
 * Builder interface for SQL and JPQL (JPA Criteria API) queries.
 *
 * @Author west
 * @Date 2024/6/25
 */
public interface JpqlBuilder<Entity> {
    /**
     * Get the JPQL (JPA Criteria API) query string.
     * 获取 jpql 和 参数
     *
     * @return JPQL query string
     */
    JpqlQuery jpql();

    /**
     * Get the JPQL (JPA Criteria API) query string.
     * 获取 jpql 和 参数
     *
     * @param refresh refresh 如果传入true，每次都会重新生成JPQL语句
     * @return JPQL query string
     */
    JpqlQuery jpql(boolean refresh);

    /**
     * 合并 新的实体 和 实体
     * Merge the new entity with entity.
     *
     * @param newEntity new entity
     */
    void mergeEntity(Entity newEntity);

}