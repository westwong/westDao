package com.k2future.westdao.core.wsql.builder;

import com.k2future.westdao.core.wsql.condition.interfaces.Delete;

import static com.k2future.westdao.core.wsql.condition.Constants.DELETE;

/**
 * LambdaDeleteBuilder
 *
 * @author west
 * @since 26/06/2024
 */

public class LambdaDeleteBuilder<Entity> extends AbstractLambdaCondition<Entity, LambdaDeleteBuilder<Entity>> implements Delete<LambdaDeleteBuilder<Entity>> {


    private static final long serialVersionUID = -112L;

    public LambdaDeleteBuilder(Class<Entity> clazz) {
        super(null, clazz);
    }

    public LambdaDeleteBuilder(Entity entity) {
        super(entity, null);
    }

    public LambdaDeleteBuilder() {
        super();
    }

    @Override
    protected LambdaDeleteBuilder<Entity> instance() {
        return new LambdaDeleteBuilder<>();
    }

    @Override
    protected String operationJpql() {
        return DELETE + " FROM " + getEntityName() + " " + getEntityAlias() + " ";
    }
}
