package cn.k2future.westdao.test.entity;

import cn.k2future.westdao.core.annotations.WestDao;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author west
 * @since 2024/6/12
 */
@Entity
@Data
@Accessors(chain = true)
@WestDao
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * auto
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32)
    private String name;

    @Column(columnDefinition = "text")
    private String avatar;

    private Integer age;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @PrePersist
    public void prePersistCreateTime() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
    }
}