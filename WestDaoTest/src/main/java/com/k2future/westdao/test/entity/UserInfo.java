package com.k2future.westdao.test.entity;

import com.k2future.westdao.core.annotations.WestDao;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @Author west
 * @Date 2024/6/12
 */
@Entity
@Data
@Accessors(chain = true)
@WestDao
public class UserInfo {

    /**
     * auto
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 32)
    private String nickName;
}