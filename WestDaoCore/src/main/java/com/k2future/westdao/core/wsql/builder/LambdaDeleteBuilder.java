package com.k2future.westdao.core.wsql.builder;

import com.k2future.westdao.core.wsql.condition.interfaces.Delete;

import static com.k2future.westdao.core.wsql.condition.Constants.DELETE;

/**
 * LambdaDeleteBuilder
 *
 * @author west
 * @since 26/06/2024
 */

public abstract class LambdaDeleteBuilder<Entity, Self extends AbstractLambdaCondition<Entity, Self>> extends AbstractLambdaCondition<Entity, Self> implements Delete<Self> {


    public LambdaDeleteBuilder(Entity entity, Class<Entity> clazz) {
        super(entity, clazz);
    }


    public LambdaDeleteBuilder() {
        super();
    }

    @Override
    protected String operationJpql() {
        return DELETE + " FROM " + getEntityName() + " " + getEntityAlias() + " ";
    }

}
