package com.k2future.westdao.core.wsql.builder;

/**
 * @Author west
 * @Date 2024/6/25
 */
public abstract class AbstractBuilder<Entity> implements JpqlBuilder<Entity> {

    /**
     * 生成操作语句 如 select update delete
     * generate operation statement such as select update delete
     * @return 操作语句
     */
    protected abstract String operationJpql();

    /**
     * 生成查询条件语句
     * generate query condition statement
     *
     * @return 查询条件语句
     */
    protected abstract String whereJpql();
}
