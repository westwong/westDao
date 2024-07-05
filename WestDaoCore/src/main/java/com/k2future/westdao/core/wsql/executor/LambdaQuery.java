package com.k2future.westdao.core.wsql.executor;

import com.k2future.westdao.core.utils.JPAUtils;
import com.k2future.westdao.core.wsql.builder.LambdaQueryBuilder;
import com.k2future.westdao.core.wsql.executor.interfaces.WestQuery;
import com.k2future.westdao.core.wsql.unit.JpqlQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return (T) resultList.get(0);
    }

    @Override
    public List<T> listEntity() {
        Query query = this.getQuery();
        return query.getResultList();
    }

    @Override
    public Page<T> pageEntity(Pageable page) {
        Query query = this.getQuery()
                .setFirstResult((int) page.getOffset()).setMaxResults(page.getPageSize());
        List<T> resultList = query.getResultList();
        long count = this.count();
        return new PageImpl<>(resultList, page, count);
    }

    @Override
    public long count() {
        super.selectCount();
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
