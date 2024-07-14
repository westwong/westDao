package cn.k2future.westdao.core.wsql.unit;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author west
 * @since 2024/6/23
 */
@FunctionalInterface
public interface WFunction<T, R> extends Function<T, R>, Serializable {
}
