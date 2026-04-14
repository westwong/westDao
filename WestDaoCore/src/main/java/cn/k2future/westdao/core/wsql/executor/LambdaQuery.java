package cn.k2future.westdao.core.wsql.executor;

import cn.k2future.westdao.core.utils.JPAUtils;
import cn.k2future.westdao.core.wsql.executor.interfaces.WestQuery;
import cn.k2future.westdao.core.wsql.builder.LambdaQueryBuilder;
import cn.k2future.westdao.core.wsql.unit.JpqlQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import cn.k2future.westdao.core.wsql.unit.WFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * LambdaQuery 类，实现了 Query 接口。
 *
 * @param <T> 实体类型
 * @author west
 * @since 2024/7/5
 */
@Component
@Slf4j
public class LambdaQuery<T> extends LambdaQueryBuilder<T, LambdaQuery<T>> implements WestQuery<T> {

    private static final long serialVersionUID = -6878283508036582697L;

    public LambdaQuery(Class<T> clazz) {
        super(null, clazz);
    }

    public LambdaQuery(T entity) {
        super(entity, null);
    }

    public LambdaQuery() {
        super();
    }

    protected static EntityManager staticEntityManager;

    @Autowired
    protected void setEntityManager(EntityManager entityManager) {
        staticEntityManager = entityManager;
    }

    @Override
    protected LambdaQuery<T> instance() {
        return new LambdaQuery<>();
    }

    /**
     * 生成 qeury
     *
     * @return 返回 query
     */
    protected Query getQuery() {
        JpqlQuery jpqlQuery = super.jpql();
        return JPAUtils.getQuery(jpqlQuery, staticEntityManager);
    }

    /**
     * 生成 TupleQuery
     *
     * @return 返回 TupleQuery
     */
    protected Query getTupleQuery() {
        JpqlQuery jpqlQuery = super.jpql();
        return JPAUtils.getTupleQuery(jpqlQuery, staticEntityManager);
    }

    @Override
    public T getEntity() {
        Query query = this.getQuery();
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        int size = resultList.size();
        if (size > 1) {
            log.warn("find {} result, return first one", size);
        }
        Object result = resultList.get(0);
        return mapToEntity(result);
    }

    @Override
    public List<T> listEntity() {
        Query query = this.getQuery();
        List resultList = query.getResultList();
        return (List<T>) resultList.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    /**
     * 将查询结果映射为实体对象
     *
     * @param result 查询结果（可能是实体，也可能是 Object[] 或单列对象）
     * @return 实体对象
     */
    private T mapToEntity(Object result) {
        if (result == null) {
            return null;
        }

        Class<T> clazz = getClazz();
        // 1. 如果结果已经是实体的实例（全量查询结果），直接返回
        if (clazz.isInstance(result)) {
            return (T) result;
        }

        // 2. 如果结果是投影查询（部分字段），则进行手动填充
        try {
            List<WFunction<T, ?>> selectColumns = getSelectColumns();
            // 如果用户指定了查询列，但返回的结果不是实体（JPA 投影），则创建实体并填充
            if (selectColumns != null && !selectColumns.isEmpty()) {
                T entity = clazz.getDeclaredConstructor().newInstance();
                BeanWrapper beanWrapper = new BeanWrapperImpl(entity);

                if (selectColumns.size() == 1) {
                    // 单列投影：result 就是该列的值
                    String propertyName = parseColumnToStringName(selectColumns.get(0));
                    beanWrapper.setPropertyValue(propertyName, result);
                } else if (result instanceof Object[] values) {
                    // 多列投影：result 是 Object[]
                    for (int i = 0; i < selectColumns.size(); i++) {
                        String propertyName = parseColumnToStringName(selectColumns.get(i));
                        beanWrapper.setPropertyValue(propertyName, values[i]);
                    }
                }
                return entity;
            }
        } catch (Exception e) {
            log.error("mapToEntity transform error, result class: {}", result.getClass().getName(), e);
        }

        // 3. 兜底处理：如果不匹配也解析不了，尝试强转（可能会报错，但保留了原始异常路径）
        return (T) result;
    }

    @Override
    public Page<T> pageEntity(Pageable page) {
        Query query = this.getQuery()
                .setFirstResult((int) page.getOffset()).setMaxResults(page.getPageSize());
        List resultList = query.getResultList();
        List<T> collect = (List<T>) resultList.stream().map(this::mapToEntity).collect(Collectors.toList());
        long count = this.count();
        return new PageImpl<>(collect, page, count);
    }

    @Override
    public long count() {
        this.forceSelectCount();
        Query query = this.getQuery();
        return (long) query.getSingleResult();
    }

    @Override
    public Map<String, Object> getMap() {
        Query query = this.getTupleQuery();
        List<Tuple> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        int size = resultList.size();
        if (size > 1) {
            log.warn("find {} result, return first one", size);
        }
        return JPAUtils.tupleToMap(resultList.get(0));
    }

    @Override
    public List<Map<String, Object>> listMap() {
        Query query = this.getTupleQuery();
        List<Tuple> resultList = query.getResultList();
        return resultList.stream().map(JPAUtils::tupleToMap).collect(Collectors.toList());
    }

    @Override
    public Page<Map<String, Object>> pageMap(Pageable page) {
        Query query = this.getTupleQuery()
                .setFirstResult((int) page.getOffset()).setMaxResults(page.getPageSize());
        List<Tuple> resultList = query.getResultList();
        List<Map<String, Object>> collect = resultList.stream().map(JPAUtils::tupleToMap).collect(Collectors.toList());
        long count = this.count();
        return new PageImpl<>(collect, page, count);
    }
}
