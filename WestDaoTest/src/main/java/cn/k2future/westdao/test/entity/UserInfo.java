package cn.k2future.westdao.test.entity;

import cn.k2future.westdao.core.annotations.WestDao;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author west
 * @since 2024/6/12
 */
@Entity
@Data
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

    private boolean student;
}