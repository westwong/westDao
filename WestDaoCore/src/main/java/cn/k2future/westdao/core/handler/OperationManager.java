package cn.k2future.westdao.core.handler;

import cn.k2future.westdao.core.domain.West;
import cn.k2future.westdao.core.utils.JPAUtils;
import cn.k2future.westdao.core.utils.BeanUtils;
import cn.k2future.westdao.core.wsql.builder.JpqlBuilder;
import cn.k2future.westdao.core.wsql.builder.LambdaQueryBuilder;
import cn.k2future.westdao.core.wsql.condition.interfaces.Delete;
import cn.k2future.westdao.core.wsql.condition.interfaces.Select;
import cn.k2future.westdao.core.wsql.condition.interfaces.Update;
import cn.k2future.westdao.core.wsql.executor.LambdaQuery;
import cn.k2future.westdao.core.wsql.unit.JpqlQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author west
 * @since 2024/6/23
 */
@Component("operationManager")
@Slf4j
public class OperationManager extends OperationBase implements OperationalDao {

    @Override
    @Transactional
    public <T> T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public <T> int deleteAll(T entity) {
        List<T> list = this.findAll(entity);
        if (list != null && !list.isEmpty()) {
            for (T t : list) {
                entityManager.remove(t);
            }
            return list.size();
        }
        return 0;
    }

    @Override
    @Transactional
    public <T> boolean deleteById(T entity) {
        T db = this.findById(entity);
        if (db != null) {
            entityManager.remove(db);
            return true;
        }
        return false;
    }

    @Override
    public <T> T findById(T entity) {
        Class<T> clazz = super.getClazz(entity);
        Object id = super.getId(entity);
        Assert.notNull(id, "id is required");
        return entityManager.find(clazz, id);
    }

    @Override
    @Transactional
    public <T> T updateById(T entity) {
        T db = this.findById(entity);
        Assert.notNull(db, "entity not found");
        BeanUtils.copyBeanIgnoreNull(entity, db);
        return entityManager.merge(db);
    }

    @Override
    public <T> List<T> findAll(T entity) {
        Class<T> clazz = super.getClazz(entity);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);

        Predicate[] predicates = this.generatePredicates(cb, root, entity);

        query.select(root).where(predicates);

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public <T> Page<T> page(T entity, Pageable page) {
        Class<T> clazz = super.getClazz(entity);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> root = query.from(clazz);
        Assert.notNull(page, "page is null");
        Predicate[] predicates = this.generatePredicates(cb, root, entity);

        query.select(root).where(predicates);

        List<T> resultList = entityManager.createQuery(query).setFirstResult((int) page.getOffset()).setMaxResults(page.getPageSize()).getResultList();
        long count = this.count(entity);
        return new PageImpl<>(resultList, page, count);
    }

    @Override
    public <T> long count(T entity) {
        Class<T> clazz = super.getClazz(entity);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(clazz);
        Predicate[] restrictions = generatePredicates(cb, root, entity);

        query.select(cb.count(root)).where(restrictions);

        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * 生成 qeury
     *
     * @param t       entity
     * @param builder JpqlBuilder<T> jpql构造器
     * @param <T>     entity
     * @return Query
     */
    private <T> Query getQuery(T t, JpqlBuilder<T> builder) {
        builder.mergeEntity(t);
        JpqlQuery jpqlQuery = builder.jpql();
        return JPAUtils.getQuery(jpqlQuery, entityManager);
    }


    @Override
    @Transactional
    public <T> int deleteAll(T t, JpqlBuilder<T> builder) {
        Assert.isTrue(builder instanceof Delete, "builder must be Delete");
        Query query = getQuery(t, builder);
        return query.executeUpdate();
    }


    @Override
    @Transactional
    public <T> int update(T t, JpqlBuilder<T> builder) {
        Assert.isTrue(builder instanceof Update, "builder must be Update");
        Query query = getQuery(t, builder);
        return query.executeUpdate();
    }

    @Override
    public <T> List<T> findAll(T t, JpqlBuilder<T> builder) {
        Assert.isTrue(builder instanceof Select, "builder must be Select");
        Query query = getQuery(t, builder);
        Select<?, ?> select = (Select<?, ?>) builder;
        Integer limit = select.getLimit();
        if (limit != null) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public <T> T findOne(T t, JpqlBuilder<T> builder) {
        Assert.isTrue(builder instanceof Select, "builder must be Select");
        getQuery(t, builder);
        List resultList = getQuery(t, builder).getResultList();
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
    public <T> T findOne(T t) {
        LambdaQuery<T> builder = West.queryJPQL(t);
        return this.findOne(null, builder);
    }

    @Override
    public <T> Page<T> page(T t, Pageable page, JpqlBuilder<T> builder) {
        Assert.isTrue(builder instanceof Select, "builder must be Select");
        Query query = getQuery(t, builder)
                .setFirstResult((int) page.getOffset()).setMaxResults(page.getPageSize());
        List<T> resultList = query.getResultList();
        long count = this.count(builder);
        return new PageImpl<>(resultList, page, count);
    }

    @Override
    public <T> long count(JpqlBuilder<T> builder) {
        Assert.isTrue(builder instanceof LambdaQueryBuilder, "builder must be Select");
        LambdaQuery<T> select = (LambdaQuery<T>) builder;
        select.selectCount();
        Query query = getQuery(null, builder);
        return (long) query.getSingleResult();
    }

}
