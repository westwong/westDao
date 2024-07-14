package cn.k2future.westdao.core.wsql.condition;

import cn.k2future.westdao.core.utils.BeanUtils;
import cn.k2future.westdao.core.utils.EntityUtils;
import cn.k2future.westdao.core.wsql.condition.interfaces.Condition;
import cn.k2future.westdao.core.wsql.condition.interfaces.ConditionConnect;
import cn.k2future.westdao.core.wsql.condition.interfaces.Ordering;
import cn.k2future.westdao.core.wsql.builder.AbstractJpqlBuilder;
import cn.k2future.westdao.core.wsql.condition.interfaces.Grouping;
import cn.k2future.westdao.core.wsql.unit.JpqlQuery;
import cn.k2future.westdao.core.wsql.unit.KV;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static cn.k2future.westdao.core.wsql.condition.Constants.*;

/**
 * @author west
 * @since 2024/6/25
 */
public abstract class AbstactCondition<Entity, Self extends AbstactCondition<Entity, Self, R>, R> extends AbstractJpqlBuilder<Entity> implements Condition<Self, R>, ConditionConnect<Self>,
        Grouping<Self, R>, Ordering<Self, R> {


    /**
     * 参数计数器 为了防止属性重复
     * Parameter counter to prevent property duplication
     */
    private final AtomicInteger paramCounter = new AtomicInteger(0);
    /**
     * conditionList
     */
    protected List<KV<String, Object>> conditions = new ArrayList<>();
    /**
     * singleConditions
     * 那些不能重复的条件比如说group by  order BY
     */
    protected Map<String, Object> singleConditions = new HashMap<>(3);
    /**
     * limitNum
     * 限制数量
     */
    protected int limitNum = 0;

    /**
     * 把实体对象转化为参数
     */
    protected Map<String, Object> entityParameters = null;
    /**
     * jpqlParameters
     */
    protected Map<String, Object> jpqlParameters = null;

    /**
     * ColumnSet
     */
    protected Set<R> columnSet = new HashSet<>();

    /**
     * 添加参数
     */

    @SuppressWarnings("unchecked")
    protected final Self self = (Self) this;

    /**
     * 子类返回一个自己的新对象
     *
     * @return 自己的新对象
     */
    protected abstract Self instance();

    /**
     * 配合instance方法使用，用于子类返回一个自己的新对象
     * 但是一般情况下要 要将 parent 设置为false
     */
    protected boolean parent = true;

    protected Entity entity;

    protected Class<Entity> clazz = null;
    /**
     * 别名
     */
    private String alias = "e";

    /**
     * @param entity 实体
     * @param clazz  类名
     */
    public AbstactCondition(Entity entity, Class<Entity> clazz) {
        if (entity != null) {
            this.entity = entity;
            this.setClazz((Class<Entity>) entity.getClass());
        }
        if (clazz != null) {
            this.setClazz(clazz);
        }
    }

    /**
     * merge new entity to old entity
     * 合并新实体到旧实体
     *
     * @param newEntity new entity
     */
    @Override
    public void mergeEntity(Entity newEntity) {
        if (newEntity == null) {
            return;
        }
        if (entity == null) {
            entity = newEntity;
        } else {
            BeanUtils.copyBeanIgnoreNull(newEntity, entity);
        }
    }

    @Override
    public JpqlQuery jpql() {
        init();
        String operation = operationJpql();
        String jpql = whereJpql();
        // 如果是父类 则需要添加操作语句
        if (parent) {
            jpql = operation + SPACE + jpql;
        }
        /**
         * jpqlQuery
         */
        return new JpqlQuery(jpql, jpqlParameters, limitNum);
    }

    protected void init() {
        initEntityParameters();
        initJpqlParameters();
        initParamCounter();
        intClazz();
    }

    protected void intClazz() {
        if (clazz == null) {
            clazz = parseClassFromColumns();
        }
    }

    protected void initJpqlParameters() {
        if (jpqlParameters != null) {
            return;
        }
        int size = 1;
        if (entityParameters != null) {
            size += entityParameters.size();
        }
        if (conditions != null) {
            size += conditions.size();
        }
        jpqlParameters = new HashMap<>(size);
    }

    protected void initParamCounter() {
        paramCounter.set(0);
    }

    /**
     * column 增加
     *
     * @param columns columns
     */
    @SafeVarargs
    protected final void addColumn(R... columns) {
        // 当前版本只需要一个 免去扩容的性能消耗
        // current version only need one, avoid performance consumption of expansion
        if (!columnSet.isEmpty()) {
            return;
        }
        List<R> list = Arrays.asList(columns);
        columnSet.addAll(list);
    }

    /**
     * column 增加
     *
     * @param column columns
     */
    protected void addColumn(R column) {
        // 当前版本只需要一个 免去扩容的性能消耗
        // current version only need one, avoid performance consumption of expansion
        if (!columnSet.isEmpty()) {
            return;
        }
        columnSet.add(column);
    }


    /**
     * 获取唯一参数名称
     *
     * @param base 基础参数名
     * @return 唯一参数名
     */
    protected String generateUniqueParamName(String base) {
        return base.replace(".", "_") + "_" + paramCounter.getAndIncrement();
    }

    /**
     * @param column 字段
     * @return 生成属性名称 String name
     */
    protected abstract String parseColumnToStringName(R column);

    /**
     * 从字段中获取类名字
     *
     * @return 类名
     */
    protected abstract Class<Entity> parseClassFromColumns();

    /**
     * get child instance and make parent = false
     *
     * @return child
     */
    protected Self getChild() {
        Self instance = instance();
        instance.parent = false;
        return instance;
    }

    /**
     * 解析实体 为 key 为属性名 value为属性值的 map
     * parse entity to map
     */
    protected void initEntityParameters() {
        if (entity != null && entityParameters == null) {
            entityParameters = EntityUtils.parseEntity(entity);
        }
    }

    /**
     * setClazz
     *
     * @param clazz clazz
     */
    protected void setClazz(Class<Entity> clazz) {
        this.clazz = clazz;
    }

    /**
     * getClazz
     *
     * @return clazz
     */
    protected Class<Entity> getClazz() {
        if (clazz == null) {
            intClazz();
        }
        // 当没有条件生效时 初始化也没有传 clazz 和 entity 将无法执行
        // 暂时不想在 其他地方取值
        Assert.notNull(clazz, "no condition is valid, clazz is null, please set clazz or entity");
        return clazz;
    }

    /**
     * 获取实体名称
     *
     * @return 实体名称
     */
    @Override
    public String getEntityName() {
        return getClazz().getSimpleName();
    }

    /**
     * 获取实体别名 暂时默认为e
     *
     * @return 实体别名
     */
    @Override
    public String getEntityAlias() {
        return alias;
    }

    @Override
    public Self setEntityAlias(String alias) {
        this.alias = alias;
        return self;
    }


    @Override
    public Self eq(boolean append, R column, Object val) {
        this.addColumn(column);
        if (append) {
            conditions.add(new KV<>(EQ, new KV<>(column, val)));
        }
        return self;
    }

    @Override
    public Self ne(boolean append, R column, Object val) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(NE, new KV<>(column, val)));
        }
        return self;
    }

    @Override
    public Self gt(boolean append, R column, Object val) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(GT, new KV<>(column, val)));
        }
        return self;
    }

    @Override
    public Self ge(boolean append, R column, Object val) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(GE, new KV<>(column, val)));
        }
        return self;
    }

    @Override
    public Self lt(boolean append, R column, Object val) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(LT, new KV<>(column, val)));
        }
        return self;
    }

    @Override
    public Self le(boolean append, R column, Object val) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(LE, new KV<>(column, val)));
        }
        return self;
    }

    @Override
    public Self between(boolean append, R column, Object val1, Object val2) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(BETWEEN, new KV<>(column, Arrays.asList(val1, val2))));
        }
        return self;
    }

    @Override
    public Self notBetween(boolean append, R column, Object val1, Object val2) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(NOT_BETWEEN, new KV<>(column, Arrays.asList(val1, val2))));
        }
        return self;
    }

    @Override
    public Self like(boolean append, R column, Object val) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(LIKE, new KV<>(column, val)));
        }
        return self;
    }

    @Override
    public Self notLike(boolean append, R column, Object val) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(NOT_LIKE, new KV<>(column, val)));
        }
        return self;
    }

    @Override
    public Self likeLeft(boolean append, R column, Object val) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(LIKE_LEFT, new KV<>(column, val)));
        }
        return self;
    }

    @Override
    public Self likeRight(boolean append, R column, Object val) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(LIKE_RIGHT, new KV<>(column, val)));
        }
        return self;
    }

    @Override
    public Self in(boolean append, R column, Collection<?> values) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(IN, new KV<>(column, values)));
        }
        return self;
    }

    @Override
    public Self notIn(boolean append, R column, Collection<?> values) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(NOT_IN, new KV<>(column, values)));
        }
        return self;
    }

    @Override
    public Self inJPQL(boolean append, R column, String jpql) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(INJPQL, new KV<>(column, jpql)));
        }
        return self;
    }

    @Override
    public Self isNull(boolean append, R column) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(IS_NULL, column));
        }
        return self;
    }

    @Override
    public Self isNotNull(boolean append, R column) {
        addColumn(column);
        if (append) {
            conditions.add(new KV<>(IS_NOT_NULL, column));
        }
        return self;
    }


    @Override
    public Self and(boolean append, Consumer<Self> consumer) {
        if (append) {
            Self instance = getChild();
            consumer.accept(instance);
            conditions.add(new KV<>(AND, instance));
        }
        return self;
    }

    @Override
    public Self and(boolean append, String condition) {
        if (append) {
            conditions.add(new KV<>(AND, condition));
        }
        return self;
    }

    @Override
    public Self or(boolean append, Consumer<Self> consumer) {
        if (append) {
            Self instance = getChild();
            consumer.accept(instance);
            conditions.add(new KV<>(OR, instance));
        }
        return self;
    }

    @Override
    public Self or(boolean append, String condition) {
        if (append) {
            conditions.add(new KV<>(OR, condition));
        }
        return self;
    }

    @SafeVarargs
    @Override
    public final Self groupBy(boolean append, R... columns) {
        addColumn(columns);
        if (append) {
            singleConditions.put(GROUP_BY, Arrays.asList(columns));
        }
        return self;
    }

    @SafeVarargs
    @Override
    public final Self orderByAsc(boolean append, R... columns) {
        addColumn(columns);
        if (append) {
            List<KV<String, List<R>>> list = (List<KV<String, List<R>>>) singleConditions.computeIfAbsent(ORDER_BY, k -> new ArrayList<>(5));
            list.add(new KV<>(ASC, Arrays.asList(columns)));
        }
        return self;
    }

    @SafeVarargs
    @Override
    public final Self orderByDesc(boolean append, R... columns) {
        addColumn(columns);
        if (append) {
            List<KV<String, List<R>>> list = (List<KV<String, List<R>>>) singleConditions.computeIfAbsent(ORDER_BY, k -> new ArrayList<>(5));
            list.add(new KV<>(DESC, Arrays.asList(columns)));
        }
        return self;
    }

    @Override
    public Self having(boolean append, String condition) {
        if (append) {
            singleConditions.put(HAVING, condition);
        }
        return self;
    }

    @Override
    public Self limit(boolean append, int limitNum) {
        if (append) {
            this.limitNum = limitNum;
        }
        return self;
    }
}
