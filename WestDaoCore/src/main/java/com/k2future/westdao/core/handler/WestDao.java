package com.k2future.westdao.core.handler;

import com.k2future.westdao.core.wsql.builder.JpqlBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Author west
 * @Date 2024/6/23
 */
public interface WestDao<T> {

    /**
     * save
     *
     * @return entity
     */
    T save();

    /**
     * delete by id
     *
     * @return boolean
     */
    boolean deleteById();

    /**
     * delete by entity field
     *
     * @return boolean
     */
    int deleteAll();

    /**
     * delete by entity field and builder field
     *
     * @param builder JpqlBuilder<T> jpql构造器
     * @return 删除的数量
     */
    int deleteAll(JpqlBuilder<T> builder);

    /**
     * findById
     *
     * @return entity
     */
    T findById();

    /**
     * updateById
     *
     * @return entity
     */
    T updateById();

    /**
     * set 的属性 在entity里面的属性 ，条件语句在builder里面
     * set the properties in the entity and the condition statement in the builder
     *
     * @param builder JpqlBuilder<T> jpql构造器
     * @return entity
     */
    int update(JpqlBuilder<T> builder);

    /**
     * findAll
     *
     * @return List<T>
     */
    List<T> findAll();

    /**
     * 查找条件由 entity 的属性 与 builder 的属性组合
     * search conditions are combined by the properties of the entity and the properties of the builder
     *
     * @param builder JpqlBuilder<T> jpql构造器
     * @return List<T>
     */
    List<T> findAll(JpqlBuilder<T> builder);

    /**
     * 查找条件由 entity 的属性 与 builder 的属性组合
     * search conditions are combined by the properties of the entity and the properties of the builder
     *
     * @param builder JpqlBuilder<T> jpql构造器
     * @return entity maybe null
     */
    T findOne(JpqlBuilder<T> builder);

    /**
     * 查找条件为 entity 的属性
     * search conditions are combined by the properties of the entity
     *
     * @return entity maybe null
     */
    T findOne();

    /**
     * 查找条件为 entity 的属性
     * search conditions are combined by the properties of the entity
     *
     * @param page pageParam
     * @return Page<T>
     */
    Page<T> page(Pageable page);

    /**
     * 查找条件由 entity 的属性 与 builder 的属性组合
     * search conditions are combined by the properties of the entity and the properties of the builder
     *
     * @param page    pageParam
     * @param builder JpqlBuilder<T> jpql构造器
     * @return Page<T>
     */
    Page<T> page(Pageable page, JpqlBuilder<T> builder);

    /**
     * 查找条件为 entity 的属性
     * search conditions are combined by the properties of the entity
     *
     * @return long
     */
    long count();

    /**
     * 查找条件由 entity 的属性 与 builder 的属性组合
     * search conditions are combined by the properties of the entity and the properties of the builder
     *
     * @param builder JpqlBuilder<T> jpql构造器
     * @return Page<T>
     */
    long count(JpqlBuilder<T> builder);


}
