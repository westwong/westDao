package cn.k2future.westdao.core.wsql.builder;

import cn.k2future.westdao.core.utils.LambdaUtils;
import cn.k2future.westdao.core.wsql.condition.AbstactCondition;
import cn.k2future.westdao.core.wsql.condition.Constants;
import cn.k2future.westdao.core.wsql.unit.JpqlQuery;
import cn.k2future.westdao.core.wsql.unit.WFunction;
import cn.k2future.westdao.core.wsql.unit.KV;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author west
 * @since 25/06/2024
 */
public abstract class AbstractLambdaCondition<Entity, Self extends AbstractLambdaCondition<Entity, Self>> extends AbstactCondition<Entity, Self, WFunction<Entity, ?>> {


    /**
     * clazz
     *
     * @param entity 实体
     * @param clazz  类
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
            // 保证where关键字只有一个 且 有效
            whereJpqlBuilder.append(Constants.SPACE).append(Constants.WHERE).append(" (1=1) ");
        }
        // 处理实体参数
        String whereCondition = this.whereCondition();
        whereJpqlBuilder.append(whereCondition);

        // 处理唯一条件 groupBy orderBy last having 之类
        if (!singleConditions.isEmpty()) {
            this.singleConditionsOperation(whereJpqlBuilder);
        }

        return whereJpqlBuilder.toString();

    }

    /**
     * 转换子类jpql
     * 转换参数名 和 参数table
     *
     * @param jpql       jpql 子类jpql
     * @param parameters 参数
     * @return
     */
    private String parseChildJpql(String jpql, Map<String, Object> parameters) {
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            // 原来参数对象值
            String originKey = entry.getKey();
            Object value = entry.getValue();
            // 为了保证参数唯一性
            String uniqueKey = generateUniqueParamName(originKey);
            jpqlParameters.put(uniqueKey, value);
            jpql = jpql.replace(originKey, uniqueKey);
        }
        return jpql;
    }

    /**
     * 生成查询条件语句 where 后面的 条件语句
     *
     * @return 查询条件语句
     */
    private String whereCondition() {
        StringBuilder whereJpqlBuilder = new StringBuilder();
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
            if (Constants.COLUMN_AND_VALUE_METHODS.contains(operation)) {
                this.columnAndValueOperation(whereJpqlBuilder, condition);
            } else if (Constants.COLUMN_AND_2VALUE_METHODS.contains(operation)) {
                this.columnAnd2ValueOperation(whereJpqlBuilder, condition);
            } else if (Constants.SINGLE_COLUMN_METHODS.contains(operation)) {
                this.singleColumnOperation(whereJpqlBuilder, condition);
            } else if (Constants.CONNECTION_METHODS.contains(operation)) {
                this.builderOperation(whereJpqlBuilder, condition);
            } else {
                throw new RuntimeException(operation + "not support");
            }
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
            case Constants.LIKE:
            case Constants.NOT_LIKE:
                value = "%" + value + "%";
                break;
            case Constants.LIKE_LEFT:
                value = "%" + value;
                break;
            case Constants.LIKE_RIGHT:
                value = value + "%";
                break;
            case Constants.INJPQL:
                value = "(" + value + ")";
        }
        if (!Constants.INJPQL.equals(operation)) {
            jpqlParameters.put(paramName, value);
        }
        sb.append(" AND ").append(getEntityAlias()).append(".").append(column);
        switch (operation) {
            case Constants.EQ:
                sb.append(" = :");
                break;
            case Constants.NE:
                sb.append(" != :");
                break;
            case Constants.GT:
                sb.append(" > :");
                break;
            case Constants.GE:
                sb.append(" >= :");
                break;
            case Constants.LT:
                sb.append(" < :");
                break;
            case Constants.LE:
                sb.append(" <= :");
                break;
            case Constants.LIKE:
            case Constants.LIKE_LEFT:
            case Constants.LIKE_RIGHT:
                sb.append(" LIKE :");
                break;
            case Constants.NOT_LIKE:
                sb.append(" NOT LIKE :");
                break;
            case Constants.IN:
                sb.append(" IN :");
                break;
            case Constants.NOT_IN:
                sb.append(" NOT IN :");
                break;
            case Constants.INJPQL:
                sb.append(" IN ").append(value);
                break;
        }
        if (!Constants.INJPQL.equals(operation)) {
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
            case Constants.BETWEEN:
                sb.append(" BETWEEN :");
                break;
            case Constants.NOT_BETWEEN:
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
            case Constants.IS_NULL:
                sb.append(" IS NULL");
                break;
            case Constants.IS_NOT_NULL:
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
        Object conditionValue = condition.getValue();
        if (conditionValue == null) {
            return;
        }
        if (conditionValue instanceof String) {
            switch (operation) {
                case Constants.AND:
                    sb.append(" AND ");
                    break;
                case Constants.OR:
                    sb.append(" OR ");
                    break;
            }
            sb.append(conditionValue);
            return;
        }
        Self builder = (Self) conditionValue;
        JpqlQuery jpql = builder.jpql();
        String jpqlString = jpql.getJpql();
        Map<String, Object> parameters = jpql.getParameters();
        jpqlString = parseChildJpql(jpqlString, parameters);
        jpqlString = StringUtils.removeStart(jpqlString, " AND ");
        switch (operation) {
            case Constants.AND:
                sb.append(" AND (");
                break;
            case Constants.OR:
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
        if (singleConditions.get(Constants.GROUP_BY) != null) {
            List<WFunction<Entity, ?>> columns = (List<WFunction<Entity, ?>>) singleConditions.get(Constants.GROUP_BY);
            if (columns != null && !columns.isEmpty()) {
                String groupColumns = columns.stream().map(this::parseColumnToStringName)
                        .map(name -> getEntityAlias() + "." + name)
                        .collect(Collectors.joining(", "));
                sb.append(" GROUP BY ").append(groupColumns);
            }
        }

        if (singleConditions.get(Constants.HAVING) != null) {
            String condition = (String) singleConditions.get(Constants.HAVING);
            sb.append(" HAVING ").append(condition);
        }

        if (singleConditions.get(Constants.ORDER_BY) != null) {
            List<KV<String, List<WFunction<Entity, ?>>>> orderByList = (List<KV<String, List<WFunction<Entity, ?>>>>) singleConditions.get(Constants.ORDER_BY);
            sb.append(" ORDER BY ");
            for (int i = 0; i < orderByList.size(); i++) {
                KV<String, List<WFunction<Entity, ?>>> orderByValue = orderByList.get(i);
                String key = orderByValue.getKey();
                List<WFunction<Entity, ?>> columns = orderByValue.getValue();
                if (columns != null && !columns.isEmpty()) {
                    String orderColumns = columns.stream().map(this::parseColumnToStringName)
                            .map(name -> getEntityAlias() + "." + name)
                            .collect(Collectors.joining(", "));
                    sb.append(orderColumns);
                    if (Constants.DESC.equals(key)) {
                        sb.append(" DESC ");
                    } else {
                        sb.append(" ASC ");
                    }
                    // 如果不是最后一个
                    if (i < orderByList.size() - 1) {
                        sb.append(", ");
                    }
                }
            }
        }
        if (singleConditions.get(Constants.LAST) != null) {
            String last = (String) singleConditions.get(Constants.LAST);
            sb.append(" ").append(last);
        }

    }

}
