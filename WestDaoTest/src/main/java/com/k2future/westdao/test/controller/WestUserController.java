package com.k2future.westdao.test.controller;

import com.k2future.westdao.core.domain.West;
import com.k2future.westdao.core.wsql.executor.LambdaQuery;
import com.k2future.westdao.test.dto.TestDto;
import com.k2future.westdao.test.entity.User;
import com.k2future.westdao.test.entity.UserInfo;
import com.k2future.westdao.test.entity.WestUser;
import com.k2future.westdao.test.entity.WestUserInfo;
import com.k2future.westdao.test.utils.resp.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * West UserController
 *
 * @author west
 * @since 2024/6/22
 */
@RestController
@RequestMapping("/west")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WestUserController {


    @PostMapping("/user/save")
    public Result<Object> saveUser(@RequestBody WestUser user) {
        user.setAvatar("default.png").save();
        return Result.successResult();
    }

    @PostMapping("/user/delete")
    public Result<Object> deleteById(WestUser user) {
        if (user.getId() != null) {
            boolean success = user.deleteById();
            return Result.successResult(success);
        } else {
            int num = user.deleteAll();
            return Result.successResult(num);
        }
    }

    @RequestMapping("/user/find")
    public Result<Object> findById(WestUser user) {
        Assert.notNull(user.getId(), "id is required");
        User User = user.findById();
        return Result.successResult(User);
    }

    @RequestMapping("/user/findAll")
    public Result<Object> findAll(WestUser user) {
        List<User> all = user.findAll();
        return Result.successResult(all);
    }

    @RequestMapping("/user/page")
    public Result<Object> page(WestUser user) {
        Page<User> page = user.page(PageRequest.of(0, 1));
        return Result.successResult(page);
    }

    @RequestMapping("/user/count")
    public Result<Object> count(WestUser user) {
        long count = user.count();
        return Result.successResult(count);
    }

    @RequestMapping("/userInfo/save")
    public Result<Object> saveUserInfo(WestUserInfo userInfo) {
        Assert.hasText(userInfo.getNickName(), "nickname is required");
        userInfo.save();
        return Result.successResult();
    }

    @RequestMapping("/userInfo/find")
    public Result<Object> findUserInfo(WestUserInfo userInfo) {
        UserInfo info = userInfo.findById();
        return Result.successResult(info);
    }

    @RequestMapping("/user/findOne")
    public Result<Object> findOne(WestUser user) {
        User one = user.findOne();
        return Result.successResult(one);
    }

    @RequestMapping("/v1/user/deleteAll")
    public Result<Object> deleteAll(WestUser user) {

        int num = user.deleteAll(
                West.deleteJPQL(User.class).eq(User::getName, "west").or(q -> q.isNull(User::getAge))
        );
        return Result.successResult(num);
    }


    @RequestMapping("/v1/user/update")
    public Result<Object> updateToPrams(WestUser user) {
        int num = user.update(
                West.updateJPQL(User.class).update(new User().setAge(10)).set(User::getAge, 10).eq(User::getId, 20L)
        );
        return Result.successResult(num);
    }

    @RequestMapping("/v1/user/findAll")
    public Result<Object> findALLByBd(TestDto dto) {
        WestUser dao = West.dao(WestUser.class);
        List<User> all = dao.findAll(
                getJPQL(dto)
        );
        return Result.successResult(all);
    }

    @RequestMapping("/v1/user/findOne")
    public Result<Object> findOneByBd(TestDto dto) {
        User one = West.dao(WestUser.class).findOne(
                getJPQL(dto)
        );
        return Result.successResult(one);
    }

    @RequestMapping("/v1/user/page")
    public Result<Object> pageByBd(TestDto dto) {
        Page<User> page = West.dao(WestUser.class).page(PageRequest.of(dto.getPageNum(), dto.getPageSize()),
                getJPQL(dto)
        );
        return Result.successResult(page);
    }

    private static LambdaQuery<User> getJPQL(TestDto dto) {
        return West.<User>queryJPQL()
                .eq(dto.isEq(), User::getId, 20L)
                .or(dto.isOr(), (q -> q.eq(User::getId, 18L).eq(User::getName, dto.getName())
                ))
                .and(dto.isAnd(), (q -> q.eq(User::getId, 18L).or(q1 -> q1.eq(User::getName, dto.getName()))
                ))
                .ne(dto.isNe(), User::getAge, 79)
                .le(dto.isLe(), User::getAge, 20)
                .ge(dto.isGe(), User::getAge, 80)
                .between(dto.isBetween(), User::getAge, 20, 80)
                .notBetween(dto.isNotBetween(), User::getAge, 20, 80)
                .like(dto.isLike(), User::getName, "ru")
                .likeLeft(dto.isLikeLeft(), User::getName, "ac")
                .likeRight(dto.isLikeRight(), User::getName, "da")
                .in(dto.isIn(), User::getAge, Arrays.asList(20, 21, 22))
                .notIn(dto.isNotIn(), User::getAge, Arrays.asList(20, 21, 22))
                .isNull(dto.isWasNull(), User::getName)
                .isNotNull(dto.isWasNotNull(), User::getName)
                .inJPQL(dto.isInJpql(), User::getName, "select nickName from UserInfo where id = 1");
    }


    @RequestMapping("/v2/user/update")
    @Transactional
    public Result<Object> updateToPramsV2(User user) {
        int execute = West.updateJPQL(user).eq(User::getId, 20L).execute();
        return Result.successResult(execute);
    }

    @RequestMapping("/v2/user/findAll")
    public Result<Object> findALLByBdV2(TestDto dto) {
        Map<String, Object> count = getJPQL(dto).select("count(1) as total").getMap();
        Map<String, Object> map = getJPQL(dto).getMap();
        List<Map<String, Object>> maps = getJPQL(dto).listMap();
        User entity = getJPQL(dto).getEntity();
        List<User> users = getJPQL(dto).listEntity();
        Map<String, Object> result = new HashMap<>(4);
        result.put("count", count);
        result.put("map", map);
        result.put("maps", maps);
        result.put("entity", entity);
        result.put("users", users);
        return Result.successResult(result);
    }

    @RequestMapping("/v2/user/page")
    public Result<Object> pageV2(TestDto dto) {
        Page<User> pageUser = getJPQL(dto).pageEntity(PageRequest.of(dto.getPageNum(), dto.getPageSize()));
        Page<Map<String, Object>> pageMap = getJPQL(dto).pageMap(PageRequest.of(dto.getPageNum(), dto.getPageSize()));
        Map<String, Object> result = new HashMap<>(4);
        result.put("map", pageMap);
        result.put("users", pageUser);
        return Result.successResult(result);
    }

    @RequestMapping("/v2/user/deleteAll")
    @Transactional
    public Result<Object> deleteAllV2(User user) {

        int execute = West.deleteJPQL(user).execute();

        return Result.successResult(execute);
    }


}