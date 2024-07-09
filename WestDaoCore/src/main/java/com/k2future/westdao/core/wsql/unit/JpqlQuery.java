package com.k2future.westdao.core.wsql.unit;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * @author west
 * @since 2024/6/25
 */
@Data
@AllArgsConstructor
public class JpqlQuery {

    private String jpql;
    private Map<String, Object> parameters;
    private int limit;

}