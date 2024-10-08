package cn.k2future.westdao.core.utils;

import cn.k2future.westdao.core.wsql.unit.JpqlQuery;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for JPA related operations.
 * Provides a method to create a JPA Query from a JpqlQuery and an EntityManager.
 *
 * @since 2024/7/5
 */
public class JPAUtils {


    /**
     * Creates a JPA Query from the given JpqlQuery and EntityManager.
     *
     * @param jpqlQuery     the JpqlQuery object containing the JPQL string and its parameters
     * @param entityManager the EntityManager to create the query
     * @return the created Query
     */
    public static Query getQuery(JpqlQuery jpqlQuery, EntityManager entityManager) {
        String jpql = jpqlQuery.getJpql();

        Map<String, Object> parameters = jpqlQuery.getParameters();
        Query query = entityManager.createQuery(jpql);
        return putParamToQuery(query, parameters, jpqlQuery.getLimit());
    }


    /**
     * Creates a JPA TupleQuery from the given JpqlQuery and EntityManager.
     *
     * @param jpqlQuery     the JpqlQuery object containing the JPQL string and its parameters
     * @param entityManager the EntityManager to create the query
     * @return the created Query
     */
    public static Query getTupleQuery(JpqlQuery jpqlQuery, EntityManager entityManager) {
        String jpql = jpqlQuery.getJpql();

        Map<String, Object> parameters = jpqlQuery.getParameters();

        Query query = entityManager.createQuery(jpql, Tuple.class);
        return putParamToQuery(query, parameters, jpqlQuery.getLimit());
    }

    /**
     * 给 Query 设置参数
     *
     * @param query      Query
     * @param parameters 参数
     * @param limit      限制
     * @return Query
     */
    private static Query putParamToQuery(Query query, Map<String, Object> parameters, int limit) {
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query;
    }


    /**
     * Converts a JPA {@link Tuple} object to a {@link Map} of String keys and Object values.
     * Each entry in the map corresponds to a tuple element's alias and its associated value.
     *
     * @param tuple The JPA Tuple object representing a row of query results.
     * @return A Map where keys are tuple element aliases and values are their respective values.
     */
    public static Map<String, Object> tupleToMap(Tuple tuple) {
        Map<String, Object> map = new HashMap<>();
        int index = 0;
        for (TupleElement<?> element : tuple.getElements()) {
            Object value = tuple.get(element);
            if (value == null) {
                continue;
            }
            Class<?> clazz = value.getClass();
            if (clazz.getName().startsWith("java.")) {
                String alias = element.getAlias();
                alias = alias != null ? alias : "column" + index++;
                map.put(alias, value);
                continue;
            }
            Map<String, Object> stringObjectMap = BeanUtils.convertObjectToMap(value);
            map.putAll(stringObjectMap);
        }
        return map;
    }


}
