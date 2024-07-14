package cn.k2future.westdao.core.wsql.builder;

import cn.k2future.westdao.core.wsql.unit.JpqlQuery;

/**
 * Builder interface for SQL and JPQL (JPA Criteria API) queries.
 *
 * @author west
 * @since 2024/6/25
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
     * 合并 新的实体 和 实体
     * Merge the new entity with entity.
     *
     * @param newEntity new entity
     */
    void mergeEntity(Entity newEntity);
    /**
     * 获取实体别名
     *
     * @return 实体别名
     */
    String getEntityAlias();

    /**
     * 获取实体名称
     *
     * @return 实体名称
     */
    String getEntityName();
}