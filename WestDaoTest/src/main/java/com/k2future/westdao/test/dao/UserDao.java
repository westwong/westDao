package com.k2future.westdao.test.dao;

import com.k2future.westdao.test.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author west
 * @Date 2024/6/12
 */
public interface UserDao extends JpaRepository<User,Long> {
}
