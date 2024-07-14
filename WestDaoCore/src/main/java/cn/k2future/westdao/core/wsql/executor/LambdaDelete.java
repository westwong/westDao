package cn.k2future.westdao.core.wsql.executor;

import cn.k2future.westdao.core.utils.JPAUtils;
import cn.k2future.westdao.core.wsql.builder.LambdaDeleteBuilder;
import cn.k2future.westdao.core.wsql.executor.interfaces.WestDelete;
import cn.k2future.westdao.core.wsql.unit.JpqlQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * delete
 *
 * @param <T> 实体类型
 * @author west
 * @since 2024/7/5
 */
@Component
@Slf4j
public class LambdaDelete<T> extends LambdaDeleteBuilder<T, LambdaDelete<T>> implements WestDelete {

    private static final long serialVersionUID = -112L;

    public LambdaDelete(Class<T> clazz) {
        super(null, clazz);
    }

    public LambdaDelete(T entity) {
        super(entity, null);
    }

    public LambdaDelete() {
        super();
    }

    @Override
    protected LambdaDelete<T> instance() {
        return new LambdaDelete<>();
    }

    protected static EntityManager staticEntityManager;

    @Autowired
    protected void setEntityManager(EntityManager entityManager) {
        staticEntityManager = entityManager;
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
