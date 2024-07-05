package com.k2future.westdao.core.wsql.executor.interfaces;

/**
 * @author west
 * @since 2024/7/5
 */
public interface WestDelete{

    /**
     * Execute the query and return the number of affected entities.
     *
     * @return the number of entities affected by the query
     */
    int execute();
}
