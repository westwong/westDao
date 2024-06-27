package com.k2future.westdao.core.wsql.builder;

import com.k2future.westdao.core.wsql.condition.interfaces.Select;
import com.k2future.westdao.core.wsql.tools.WFunction;
import com.k2future.westdao.core.wsql.unit.KV;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.k2future.westdao.core.wsql.condition.Constants.*;

/**
 * LambdaQueryBuilder
 *
 * @author west
 * @since 26/06/2024
 */

public class LambdaQueryBuilder<Entity> extends AbstractLambdaCondition<Entity, LambdaQueryBuilder<Entity>> implements Select<LambdaQueryBuilder<Entity>, WFunction<Entity, ?>> {


    private static final long serialVersionUID = -6878283508036582697L;
    /**
     * 查询结果字段
     */
    private KV<String, ?> selectResult = null;
    /**
     * 查询的个数
     */
    private Integer limit = null;

    public LambdaQueryBuilder(Class<Entity> clazz) {
        super(null, clazz);
    }

    public LambdaQueryBuilder(Entity entity) {
        super(entity, null);
    }

    public LambdaQueryBuilder() {
        super();
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    @Override
    public LambdaQueryBuilder<Entity> limit(Integer limit) {
        this.limit = limit;
        return self;
    }

    @Override
    protected LambdaQueryBuilder<Entity> instance() {
        return new LambdaQueryBuilder<>();
    }

    @SafeVarargs
    @Override
    public final LambdaQueryBuilder<Entity> select(WFunction<Entity, ?>... columns) {
        addColumn(columns);
        if (selectResult == null) {
            selectResult = new KV<>(SELECT, Arrays.asList(columns));
        }
        return self;
    }

    @SafeVarargs
    @Override
    public final LambdaQueryBuilder<Entity> selectDistinct(WFunction<Entity, ?>... columns) {
        addColumn(columns);
        if (selectResult == null) {
            selectResult = new KV<>(SELECT_DISTINCT, Arrays.asList(columns));
        }
        return self;
    }

    @SafeVarargs
    @Override
    public final LambdaQueryBuilder<Entity> selectCount(WFunction<Entity, ?>... columns) {
        addColumn(columns);
        if (selectResult == null) {
            selectResult = new KV<>(SELECT_COUNT, Arrays.asList(columns));
        }
        return self;
    }

    @Override
    public LambdaQueryBuilder<Entity> select(String columns) {
        if (selectResult == null) {
            selectResult = new KV<>(SELECT_String, columns);
        }
        return self;
    }

    private StringBuffer getSelectResultSql() {
        StringBuffer sb = new StringBuffer();
        if (selectResult == null) {
            sb.append(getEntityAlias()).append(SPACE);
            return sb;
        }
        String key = selectResult.getKey();
        if (SELECT_String.equals(key)) {
            sb.append(selectResult.getValue());
            return sb;
        }

        List<WFunction<Entity, ?>> columns = (List<WFunction<Entity, ?>>) selectResult.getValue();
        String columnsJpql = columns.stream()
                .map(this::parseColumnToStringName)
                .map(name -> getEntityAlias() + "." + name)
                .collect(Collectors.joining(", "));

        switch (key) {
            case SELECT:
                if (StringUtils.isEmpty(columnsJpql)) {
                    sb.append(getEntityAlias());
                } else {
                    sb.append(columnsJpql);
                }
                break;
            case SELECT_DISTINCT:
                Assert.hasText(columnsJpql, "Distinct columns cannot be empty");
                sb.append(DISTINCT).append(SPACE).append("(").append(columnsJpql).append(")");
                break;
            case SELECT_COUNT:
                if (StringUtils.isEmpty(columnsJpql)) {
                    sb.append("COUNT(1)");
                } else {
                    sb.append("COUNT(").append(columnsJpql).append(")");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown select key: " + key);
        }
        return sb.append(SPACE);
    }


    @Override
    public String selectJpql() {
        StringBuilder sb = new StringBuilder();
        sb.append(SELECT).append(SPACE);
        StringBuffer selectResultSql = getSelectResultSql();
        sb.append(selectResultSql);
        sb.append(FROM).append(SPACE).append(getEntityName()).append(SPACE).append(getEntityAlias()).append(SPACE);
        return sb.toString();
    }

    @Override
    public String operationJpql() {
        return selectJpql();
    }
}
