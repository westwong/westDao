package cn.k2future.westdao.core.handler;

import cn.k2future.westdao.core.wsql.builder.JpqlBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 操作接口 和 定义接口 一毛一样
 * operationalDao  like WestDao
 * {@link WestDao}
 *
 * @author west
 * @since 2024/6/23
 */
public interface OperationalDao {

    /**
     * save
     *
     * @param t   entity
     * @param <T> entity
     * @return entity
     */
    <T> T save(T t);

    /**
     * 删除满足条件的数据
     * <p>
     * delete data based on entity
     *
     * @param t   entity
     * @param <T> entity
     * @return boolean
     */
    <T> int deleteAll(T t);

    /**
     * delete by entity field and builder field
     *
     * @param t       entity
     * @param <T>     entity
     * @param builder JpqlBuilder jpql构造器
     * @return 删除的数量
     */
    <T> int deleteAll(T t, JpqlBuilder<T> builder);

    /**
     * deleteById
     *
     * @param t   entity
     * @param <T> entity
     * @return boolean
     */
    <T> boolean deleteById(T t);

    /**
     * findById
     * @param t entity
     * @param <T> entity
     * @return entity
     */
    <T> T findById(T t);

    /**
     * updateById
     *
     * @param t   entity
     * @param <T> entity
     * @return entity
     */
    <T> T updateById(T t);

    /**
     * set 的属性 在entity里面的属性 ，条件语句在builder里面
     * set the properties in the entity and the condition statement in the builder
     *
     * @param t       entity
     * @param builder JpqlBuilder jpql构造器
     * @param <T>     entity
     * @return entity
     */
    <T> int update(T t, JpqlBuilder<T> builder);

    /**
     * findAll
     *
     * @param t   entity
     * @param <T> entity
     * @return List
     */
    <T> List<T> findAll(T t);

    /**
     * 查找条件由 entity 的属性 与 builder 的属性组合
     * search conditions are combined by the properties of the entity and the properties of the builder
     *
     * @param t       entity
     * @param <T>     entity
     * @param builder JpqlBuilder jpql构造器
     * @return List
     */
    <T> List<T> findAll(T t, JpqlBuilder<T> builder);


    /**
     * 查找条件由 entity 的属性 与 builder 的属性组合
     * search conditions are combined by the properties of the entity and the properties of the builder
     *
     * @param t       entity
     * @param <T>     entity
     * @param builder JpqlBuilder jpql构造器
     * @return entity maybe null
     */
    <T> T findOne(T t, JpqlBuilder<T> builder);

    /**
     * 查找条件为 entity 的属性
     * search conditions are combined by the properties of the entity
     *
     * @param t   entity
     * @param <T> entity
     * @return entity maybe null
     */
    <T> T findOne(T t);

    /**
     * page
     *
     * @param t    entity
     * @param <T>  entity
     * @param page pageParam
     * @return List
     */
    <T> Page<T> page(T t, Pageable page);

    /**
     * 查找条件由 entity 的属性 与 builder 的属性组合
     * search conditions are combined by the properties of the entity and the properties of the builder
     *
     * @param t       entity
     * @param <T>     entity
     * @param page    pageParam
     * @param builder JpqlBuilder jpql构造器
     * @return Page
     */
    <T> Page<T> page(T t, Pageable page, JpqlBuilder<T> builder);

    /**
     * count
     *
     * @param t   entity
     * @param <T> entity
     * @return long
     */
    <T> long count(T t);

    /**
     * 查找条件由 entity 的属性 与 builder 的属性组合
     * search conditions are combined by the properties of the entity and the properties of the builder
     *
     * @param builder JpqlBuilder jpql构造器
     * @param <T>     entity
     * @return Page
     */
    <T> long count(JpqlBuilder<T> builder);

}
