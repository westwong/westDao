package cn.k2future.westdao.core.handler;

import cn.k2future.westdao.core.utils.EntityUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import java.util.ArrayList;
import java.util.List;

/**
 * @author west
 * @since 2024/6/24
 */
public abstract class OperationBase {

    @PersistenceContext
    protected EntityManager entityManager;

    /**
     * gen Predicate
     *
     * @param cb     CriteriaBuilder
     * @param root   Root
     * @param entity entity
     * @param <T>    entity
     * @return Predicate[]
     */
    protected <T> Predicate[] generatePredicates(CriteriaBuilder cb, Root<T> root, T entity) {
        if (!entity.getClass().isAnnotationPresent(javax.persistence.Entity.class)) {
            throw new IllegalArgumentException("Class is not an Entity");
        }
        List<Predicate> predicates = new ArrayList<>(0);
        // Dynamically add predicates based on entity properties
        for (String attributeName : entityManager.getMetamodel().entity(entity.getClass()).getDeclaredAttributes().stream()
                .map(Attribute::getName).toArray(String[]::new)) {
            try {
                Object value = entity.getClass().getMethod("get" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1)).invoke(entity);
                if (value != null) {
                    predicates.add(cb.equal(root.get(attributeName), value));
                }
            } catch (Exception ignored) {
            }
        }
        return predicates.toArray(new Predicate[0]);
    }

    /**
     * get Clazz by Entity
     *
     * @param entity entity
     * @param <T>    entity
     * @return clazz
     */
    protected <T> Class<T> getClazz(T entity) {
        return EntityUtils.getClazz(entity);
    }

    /**
     * get id by entity
     *
     * @param entity entity
     * @param <T>    entity
     * @return id value
     */
    protected <T> Object getId(T entity) {
        return EntityUtils.parseId(entity);
    }
}
