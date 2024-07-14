package cn.k2future.westdao.core.wsql.executor;

import cn.k2future.westdao.core.utils.JPAUtils;
import cn.k2future.westdao.core.wsql.builder.LambdaUpdateBuilder;
import cn.k2future.westdao.core.wsql.executor.interfaces.WestUpdate;
import cn.k2future.westdao.core.wsql.unit.JpqlQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * LambdaQuery 类，实现了 Query 接口。
 *
 * @param <T> 实体类型
 * @author west
 * @since 2024/7/5
 */
@Component
@Slf4j
public class LambdaUpdate<T> extends LambdaUpdateBuilder<T, LambdaUpdate<T>> implements WestUpdate {


    private static final long serialVersionUID = -114L;

    public LambdaUpdate(Class<T> clazz) {
        super(null, clazz);
    }

    /**
     * 构造函数
     *
     * @param entity entity的属性将作为待修改的字段
     */
    public LambdaUpdate(T entity) {
        super(entity, null);
    }

    public LambdaUpdate() {
        super();
    }

    protected static EntityManager staticEntityManager;

    @Autowired
    protected void setEntityManager(EntityManager entityManager) {
        staticEntityManager = entityManager;
    }

    @Override
    protected LambdaUpdate<T> instance() {
        return new LambdaUpdate<>();
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

    @Override
    public int execute() {
        Query query = getQuery();
        return query.executeUpdate();
    }
}
