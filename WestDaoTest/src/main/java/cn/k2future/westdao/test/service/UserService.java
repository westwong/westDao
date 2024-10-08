package cn.k2future.westdao.test.service;

import cn.k2future.westdao.test.dao.UserDao;
import cn.k2future.westdao.test.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author west
 * @since 2024/6/22
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final UserDao userDao;

    /**
     *
     * @param user entity
     */
    public void save(User user){
        userDao.save(user);
    }
}
