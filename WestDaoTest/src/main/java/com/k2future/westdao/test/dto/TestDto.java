package com.k2future.westdao.test.dto;

import lombok.Data;

/**
 * @Author west
 * @Date 2024/6/27
 */
@Data
public class TestDto {

    private String name;

    private boolean ne;

    private boolean eq;

    private boolean gt;

    private boolean lt;

    private boolean ge;

    private boolean le;

    private boolean like;

    private boolean notLike;

    private boolean likeLeft;

    private boolean likeRight;

    private boolean in;

    private boolean notIn;

    private boolean inJpql;

    private boolean wasNull;

    private boolean wasNotNull;

    private boolean between;

    private boolean notBetween;

    private boolean and;

    private boolean or;

    private int pageSize;

    private int pageNum;
}
