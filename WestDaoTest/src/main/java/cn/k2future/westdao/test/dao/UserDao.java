package cn.k2future.westdao.test.dao;

import cn.k2future.westdao.test.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author west
 * @since 2024/6/12
 */
public interface UserDao extends JpaRepository<User,Long> {
}
