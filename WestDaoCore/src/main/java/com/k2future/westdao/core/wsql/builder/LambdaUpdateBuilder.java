package com.k2future.westdao.core.wsql.builder;

import com.k2future.westdao.core.utils.EntityUtils;
import com.k2future.westdao.core.wsql.condition.interfaces.Update;
import com.k2future.westdao.core.wsql.unit.KV;
import com.k2future.westdao.core.wsql.unit.WFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.k2future.westdao.core.wsql.condition.Constants.*;

/**
 * LambdaUpdateBuilder
 *
 * @author west
 * @since 26/06/2024
 */

public abstract class LambdaUpdateBuilder<Entity, Self extends AbstractLambdaCondition<Entity, Self>> extends AbstractLambdaCondition<Entity, Self> implements Update<Entity, Self, WFunction<Entity, ?>> {


    /**
     * 查询结果字段
     */
    private final List<KV<WFunction<Entity, ?>, Object>> updateList = new ArrayList<>();

    private Map<String, Object> updateEntityParameters = new HashMap<>();

    public LambdaUpdateBuilder(Entity entity, Class<Entity> clazz) {
        super(entity, clazz);
    }

    public LambdaUpdateBuilder() {
        super();
    }

    @Override
    public final Self update(Entity updateEntity) {
        if (updateEntity != null) {
            updateEntityParameters = EntityUtils.parseEntity(updateEntity);
        }
        return self;
    }


    @Override
    protected String operationJpql() {
        return updateJpql();
    }

    @Override
    public final Self set(WFunction<Entity, ?> column, Object val) {
        updateList.add(new KV<>(column, val));
        return self;
    }


    @Override
    public final String updateJpql() {
        StringBuilder sb = new StringBuilder();
        if (entityParameters != null) {
            // 修改entity的内容 Entity也为待修改的
            updateEntityParameters.putAll(entityParameters);
            entityParameters.clear();
        }


        sb.append(UPDATE).append(SPACE).append(getEntityName()).append(SPACE).append(getEntityAlias()).append(SPACE).append(SET).append(SPACE);
        int size = 0;
        if (updateList != null) {
            size += updateList.size();
        }
        if (updateEntityParameters != null) {
            size += updateEntityParameters.size();
        }
        if (size == 0) {
            throw new IllegalArgumentException("No update fields");
        }
        // 使用map 保存更新字段 以set中的数据为优先
        Map<String, String> updateMap = new HashMap<>(size);
        // 首先解析待修改entity的内容
        if (updateEntityParameters != null) {
            for (Map.Entry<String, Object> entry : updateEntityParameters.entrySet()) {
                String columnString = entry.getKey();
                Object value = entry.getValue();
                if (value == null) {
                    continue;
                }
                String uniqueParamName = generateUniqueParamName(columnString);
                jpqlParameters.put(uniqueParamName, value);
                String sql = getEntityAlias() + "." + columnString + " = :" + uniqueParamName + " ";
                updateMap.put(columnString, sql);
            }
        }
        // 再解析 set 的内容
        if (updateList != null) {
            updateList.forEach(kv -> {
                WFunction<Entity, ?> column = kv.getKey();
                String columnString = parseColumnToStringName(column);
                String uniqueParamName = generateUniqueParamName(columnString);
                Object value = kv.getValue();

                jpqlParameters.put(uniqueParamName, value);
                String sql = getEntityAlias() + "." + columnString + " = :" + uniqueParamName + " ";
                updateMap.put(columnString, sql);
            });
        }
        sb.append(String.join(", ", updateMap.values()));
        return sb.toString();
    }
}
