package com.k2future.westdao.core.wsql.tools;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @Author west
 * @Date 2024/6/23
 */
@FunctionalInterface
public interface WFunction<T, R> extends Function<T, R>, Serializable {
}
