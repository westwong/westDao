package com.k2future.westdao.core.wsql.condition;

import java.util.Arrays;
import java.util.List;

/**
 * @author west
 * @since 2024/6/25
 */
public final class Constants {

    /**
     * 前括号
     */
    public static final String LEFT_BRACKET = "(";
    /**
     * 后括号
     */
    public static final String RIGHT_BRACKET = ")";


    public static final String EQ = "qe";
    public static final String NE = "NE";
    public static final String GT = "GT";
    public static final String GE = "GE";
    public static final String LT = "LT";
    public static final String LE = "LE";
    public static final String LIKE = "LIKE";
    public static final String LIKE_LEFT = "LIKE_LEFT";
    public static final String LIKE_RIGHT = "LIKE_RIGHT";
    public static final String NOT_LIKE = "NOT_LIKE";
    public static final String IN = "IN";
    public static final String INJPQL = "INJPQL";
    public static final String NOT_IN = "NOT_IN";
    public static final String IS_NULL = "IS_NULL";
    public static final String IS_NOT_NULL = "IS_NOT_NULL";
    public static final String BETWEEN = "BETWEEN";
    public static final String NOT_BETWEEN = "NOT_BETWEEN";
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    public static final String GROUP_BY = "GROUP_BY";
    public static final String ORDER_BY = "ORDER_BY";
    public static final String HAVING = "HAVING";
    public static final String LAST = "LAST";
    public static final String LIMIT = "LIMIT";
    public static final String OFFSET = "OFFSET";
    public static final String WHERE = "WHERE";
    public static final String SELECT = "SELECT";
    public static final String SELECT_DISTINCT = "SELECT_DISTINCT";
    public static final String SELECT_COUNT = "SELECT_COUNT";
    public static final String SELECT_String = "SELECT_String";
    public static final String DISTINCT = "DISTINCT";
    public static final String COUNT = "COUNT";
    public static final String SPACE = " ";
    public static final String FROM = "FROM";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";
    public static final String SET = "SET";

    /**
     * 参数为 columnAndValue的方法
     */
    public static final List<String> COLUMN_AND_VALUE_METHODS = Arrays.asList(EQ, NE, GT, GE, LT, LE, LIKE, LIKE_LEFT, LIKE_RIGHT, NOT_LIKE, INJPQL, IN, NOT_IN);
    public static final List<String> COLUMN_AND_2VALUE_METHODS = Arrays.asList(BETWEEN, NOT_BETWEEN);
    public static final List<String> SINGLE_COLUMN_METHODS = Arrays.asList(IS_NULL, IS_NOT_NULL);
    public static final List<String> CONNECTION_METHODS = Arrays.asList(AND, OR);

}
