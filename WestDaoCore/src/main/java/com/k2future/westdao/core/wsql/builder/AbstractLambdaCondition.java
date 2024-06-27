package com.k2future.westdao.core.wsql.builder;

import com.k2future.westdao.core.wsql.condition.AbstactCondition;
import com.k2future.westdao.core.wsql.tools.LambdaUtils;
import com.k2future.westdao.core.wsql.tools.WFunction;
import com.k2future.westdao.core.wsql.unit.JpqlQuery;
import com.k2future.westdao.core.wsql.unit.KV;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.k2future.westdao.core.wsql.condition.Constants.*;

/**
 * @author west
 * @since 25/06/2024
 */
public abstract class AbstractLambdaCondition<Entity, Self extends AbstractLambdaCondition<Entity, Self>> extends AbstactCondition<Entity, Self, WFunction<Entity, ?>> {


    /**
     * clazz
     *
     * @param entity 实体
     * @param clazz 类
     */
    public AbstractLambdaCondition(Entity entity, Class<Entity> clazz) {
        super(entity, clazz);
    }

    public AbstractLambdaCondition() {
        super(null, null);

    }

    @Override
    protected Class<Entity> parseClassFromColumns() {
        Assert.notEmpty(columnSet, "no condition no operation");
        WFunction<Entity, ?> entityWFunction = columnSet.stream().findFirst().get();
        return LambdaUtils.getClassFromFunction(entityWFunction);
    }

    /**
     * Get the property name of the lambda expression
     *
     * @param propertyGetter Lambda expression
     * @return Property name
     */
    @Override
    protected String parseColumnToStringName(WFunction<Entity, ?> propertyGetter) {
        return LambdaUtils.getPropertyNameWithCache(propertyGetter);
    }

    @Override
    protected String whereJpql() {
        StringBuilder whereJpqlBuilder = new StringBuilder();
        if (parent) {
            whereJpqlBuilder.append(WHERE).append(" (1=1) ");
        }
        // 处理实体参数
        if (entityParameters != null) {
            for (Map.Entry<String, Object> entry : entityParameters.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value == null) {
                    continue;
                }
                String paramName = super.generateUniqueParamName(key);
                whereJpqlBuilder.append(" AND ").append(key).append(" = :").append(paramName);
                jpqlParameters.put(paramName, value);
            }
        }
        // 处理数组条件
        for (KV<String, Object> condition : conditions) {
            String operation = condition.getKey();
            if (COLUMN_AND_VALUE_METHODS.contains(operation)) {
                this.columnAndValueOperation(whereJpqlBuilder, condition);
            } else if (COLUMN_AND_2VALUE_METHODS.contains(operation)) {
                this.columnAnd2ValueOperation(whereJpqlBuilder, condition);
            } else if (SINGLE_COLUMN_METHODS.contains(operation)) {
                this.singleColumnOperation(whereJpqlBuilder, condition);
            } else if (BUILDER_METHODS.contains(operation)) {
                this.builderOperation(whereJpqlBuilder, condition);
            } else {
                throw new RuntimeException(operation + "not support");
            }
        }
        // 处理唯一条件 groupBy orderBy
        if (singleConditions != null) {
            this.singleConditionsOperation(whereJpqlBuilder);
        }
        return whereJpqlBuilder.toString();

    }


    /**
     * 对于 条件是 columnAndValue的方法
     * 一个属性一个 参数 比如 ： name != west
     * 比如： eq,ne,gt,ge,lt,le,like,likeLeft,likeRight,notLike,in,notIn,inJpql
     *
     * @param sb
     * @param condition
     */
    private void columnAndValueOperation(StringBuilder sb, KV<String, Object> condition) {
        String operation = condition.getKey();
        KV<WFunction<Entity, ?>, Object> columnAndValue = (KV<WFunction<Entity, ?>, Object>) condition.getValue();
        String column = parseColumnToStringName(columnAndValue.getKey());
        String paramName = generateUniqueParamName(column);
        Object value = columnAndValue.getValue();
        switch (operation) {
            case LIKE:
            case NOT_LIKE:
                value = "%" + value + "%";
                break;
            case LIKE_LEFT:
                value = "%" + value;
                break;
            case LIKE_RIGHT:
                value = value + "%";
                break;
            case INJPQL:
                value = "(" + value + ")";
        }
        if (!INJPQL.equals(operation)) {
            jpqlParameters.put(paramName, value);
        }
        sb.append(" AND ").append(getEntityAlias()).append(".").append(column);
        switch (operation) {
            case EQ:
                sb.append(" = :");
                break;
            case NE:
                sb.append(" != :");
                break;
            case GT:
                sb.append(" > :");
                break;
            case GE:
                sb.append(" >= :");
                break;
            case LT:
                sb.append(" < :");
                break;
            case LE:
                sb.append(" <= :");
                break;
            case LIKE:
            case LIKE_LEFT:
            case LIKE_RIGHT:
                sb.append(" LIKE :");
                break;
            case NOT_LIKE:
                sb.append(" NOT LIKE :");
                break;
            case IN:
                sb.append(" IN :");
                break;
            case NOT_IN:
                sb.append(" NOT IN :");
                break;
            case INJPQL:
                sb.append(" IN ").append(value);
                break;
        }
        if (!INJPQL.equals(operation)) {
            sb.append(paramName);
        }
    }

    /**
     * 对于 条件是 columnAnd2Value的方法
     * 比如 ： age between 10 and 20
     *
     * @param sb        stringBuilder
     * @param condition 条件
     */
    private void columnAnd2ValueOperation(StringBuilder sb, KV<String, Object> condition) {
        String operation = condition.getKey();
        KV<WFunction<Entity, ?>, List<Object>> columnAndValue = (KV<WFunction<Entity, ?>, List<Object>>) condition.getValue();
        String column = parseColumnToStringName(columnAndValue.getKey());
        List<Object> values = columnAndValue.getValue();
        if (values == null || values.size() != 2) {
            return;
        }
        String paramName1 = generateUniqueParamName(column);
        String paramName2 = generateUniqueParamName(column);
        jpqlParameters.put(paramName1, values.get(0));
        jpqlParameters.put(paramName2, values.get(1));

        sb.append(" AND ").append(getEntityAlias()).append(".").append(column);
        switch (operation) {
            case BETWEEN:
                sb.append(" BETWEEN :");
                break;
            case NOT_BETWEEN:
                sb.append(" NOT BETWEEN :");
                break;
        }
        sb.append(paramName1).append(" AND :").append(paramName2);
    }

    /**
     * 对于 条件是 singleColumn的方法
     * 比如 ： name is null
     *
     * @param sb        stringBuilder
     * @param condition 条件 object 为 column
     */
    private void singleColumnOperation(StringBuilder sb, KV<String, Object> condition) {
        String operation = condition.getKey();
        WFunction<Entity, ?> column = (WFunction<Entity, ?>) condition.getValue();
        String columnName = parseColumnToStringName(column);
        sb.append(" AND ").append(getEntityAlias()).append(".").append(columnName);
        switch (operation) {
            case IS_NULL:
                sb.append(" IS NULL");
                break;
            case IS_NOT_NULL:
                sb.append(" IS NOT NULL");
                break;
        }
    }

    /**
     * 对于传来对象为 Self 的处理办法
     *
     * @param sb        stringBuilder
     * @param condition 条件
     */
    private void builderOperation(StringBuilder sb, KV<String, Object> condition) {
        String operation = condition.getKey();
        Self builder = (Self) condition.getValue();
        JpqlQuery jpql = builder.jpql();
        String jpqlString = jpql.getJpql();
        Map<String, Object> parameters = jpql.getParameters();
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            // 原来参数对象值
            String originKey = entry.getKey();
            Object value = entry.getValue();
            // 为了保证参数唯一性
            String uniqueKey = generateUniqueParamName(originKey);
            jpqlParameters.put(uniqueKey, value);
            jpqlString = jpqlString.replace(originKey, uniqueKey);
        }
        jpqlString = StringUtils.removeStart(jpqlString, " AND ");
        switch (operation) {
            case AND:
                sb.append(" AND (");
                break;
            case OR:
                sb.append(" OR (");
                break;
        }
        sb.append(jpqlString).append(")");
    }

    /**
     * 对于单一条件的语句
     * 如 groupBy orderBy
     *
     * @param sb stringBuilder
     */
    private void singleConditionsOperation(StringBuilder sb) {
        if (singleConditions.get(GROUP_BY) != null) {
            List<WFunction<Entity, ?>> columns = (List<WFunction<Entity, ?>>) singleConditions.get(GROUP_BY);
            if (columns != null && !columns.isEmpty()) {
                String groupColumns = columns.stream().map(this::parseColumnToStringName)
                        .map(name -> getEntityAlias() + "." + name)
                        .collect(Collectors.joining(", "));
                sb.append(" GROUP BY ").append(groupColumns);
            }
        }
        if (singleConditions.get(ORDER_BY) != null) {
            KV<String, List<WFunction<Entity, ?>>> orderByValue = (KV<String, List<WFunction<Entity, ?>>>) singleConditions.get(ORDER_BY);
            String key = orderByValue.getKey();
            List<WFunction<Entity, ?>> columns = orderByValue.getValue();
            if (columns != null && !columns.isEmpty()) {
                String orderColumns = columns.stream().map(this::parseColumnToStringName)
                        .map(name -> getEntityAlias() + "." + name)
                        .collect(Collectors.joining(", "));
                sb.append(" ORDER BY ").append(orderColumns);
                if (DESC.equals(key)) {
                    sb.append(" DESC");
                }
            }
        }

    }

}
