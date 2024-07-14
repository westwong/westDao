package cn.k2future.westdao.core.wsql.executor.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Query 接口定义了用于查询实体和相关数据的方法。
 * @param <T> 实体类型
 * @author west
 * @since 2024/7/5
 */
public interface WestQuery<T> {
    /**
     * 获取实体
     *
     * @return 返回单个实体
     */
    T getEntity();

    /**
     * 获取实体列表
     * @return 返回实体列表
     */
    List<T> listEntity();

    /**
     * 获取分页实体
     *
     * @param page 分页参数
     * @return 返回分页后的实体列表
     */
    Page<T> pageEntity(Pageable page);

    /**
     * 获取实体总数量
     *
     * @return 返回实体的总数量
     */
    long count();

    /**
     * 获取单个实体的键值对映射
     *
     * @return 返回包含实体属性和值的映射
     */
    Map<String, Object> getMap();

    /**
     * 获取实体列表的键值对映射
     *
     * @return 返回实体列表，每个实体包含属性和值的映射
     */
    List<Map<String, Object>> listMap();

    /**
     * 获取分页实体的键值对映射
     *
     * @param page 分页参数
     * @return 返回分页后的实体列表，每个实体包含属性和值的映射
     */
    Page<Map<String, Object>> pageMap(Pageable page);

}
