package cn.k2future.westdao.core.wsql.builder;

import cn.k2future.westdao.core.wsql.condition.Constants;
import cn.k2future.westdao.core.wsql.condition.interfaces.Select;
import cn.k2future.westdao.core.wsql.unit.WFunction;
import cn.k2future.westdao.core.wsql.unit.KV;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LambdaQueryBuilder
 *
 * @author west
 * @since 26/06/2024
 */

public abstract class LambdaQueryBuilder<Entity, Self extends AbstractLambdaCondition<Entity, Self>> extends AbstractLambdaCondition<Entity, Self> implements Select<Self, WFunction<Entity, ?>> {

    /**
     * 查询结果字段
     */
    private KV<String, ?> selectResult = null;
    /**
     * 查询的个数
     */
    private Integer limit = null;

    public LambdaQueryBuilder(Entity entity, Class<Entity> clazz) {
        super(entity, clazz);
    }

    public LambdaQueryBuilder() {
        super();
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    @Override
    public Self limit(Integer limit) {
        this.limit = limit;
        return self;
    }


    @SafeVarargs
    @Override
    public final Self select(WFunction<Entity, ?>... columns) {
        addColumn(columns);
        if (selectResult == null) {
            selectResult = new KV<>(Constants.SELECT, Arrays.asList(columns));
        }
        return self;
    }

    @SafeVarargs
    @Override
    public final Self selectDistinct(WFunction<Entity, ?>... columns) {
        addColumn(columns);
        if (selectResult == null) {
            selectResult = new KV<>(Constants.SELECT_DISTINCT, Arrays.asList(columns));
        }
        return self;
    }

    @SafeVarargs
    @Override
    public final Self selectCount(WFunction<Entity, ?>... columns) {
        addColumn(columns);
        if (selectResult == null) {
            selectResult = new KV<>(Constants.SELECT_COUNT, Arrays.asList(columns));
        }
        return self;
    }

    @Override
    public Self select(String columns) {
        if (selectResult == null) {
            selectResult = new KV<>(Constants.SELECT_String, columns);
        }
        return self;
    }

    private StringBuffer getSelectResultSql() {
        StringBuffer sb = new StringBuffer();
        if (selectResult == null) {
            sb.append(getEntityAlias()).append(Constants.SPACE);
            return sb;
        }
        String key = selectResult.getKey();
        if (Constants.SELECT_String.equals(key)) {
            sb.append(selectResult.getValue())
                    .append(Constants.SPACE);
            return sb;
        }

        List<WFunction<Entity, ?>> columns = (List<WFunction<Entity, ?>>) selectResult.getValue();
        String columnsJpql = columns.stream()
                .map(this::parseColumnToStringName)
                .map(name -> getEntityAlias() + "." + name)
                .collect(Collectors.joining(", "));

        switch (key) {
            case Constants.SELECT:
                if (StringUtils.isEmpty(columnsJpql)) {
                    sb.append(getEntityAlias());
                } else {
                    sb.append(columnsJpql);
                }
                break;
            case Constants.SELECT_DISTINCT:
                Assert.hasText(columnsJpql, "Distinct columns cannot be empty");
                sb.append(Constants.DISTINCT).append(Constants.SPACE).append("(").append(columnsJpql).append(")");
                break;
            case Constants.SELECT_COUNT:
                if (StringUtils.isEmpty(columnsJpql)) {
                    sb.append("COUNT(1)");
                } else {
                    sb.append("COUNT(").append(columnsJpql).append(")");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown select key: " + key);
        }
        return sb.append(Constants.SPACE);
    }


    @Override
    public final String selectJpql() {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.SELECT).append(Constants.SPACE);
        StringBuffer selectResultSql = getSelectResultSql();
        sb.append(selectResultSql);
        sb.append(Constants.FROM).append(Constants.SPACE).append(getEntityName()).append(Constants.SPACE).append(getEntityAlias()).append(Constants.SPACE);
        return sb.toString();
    }

    @Override
    protected final String operationJpql() {
        return selectJpql();
    }
}
