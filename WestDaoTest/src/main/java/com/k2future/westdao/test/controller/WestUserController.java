package com.k2future.westdao.test.controller;

import com.k2future.westdao.core.domain.West;
import com.k2future.westdao.core.wsql.builder.LambdaQueryBuilder;
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

import java.util.Arrays;
import java.util.List;

/**
 * West UserController
 *
 * @Author west
 * @Date 2024/6/22
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

    @RequestMapping("/bd/user/deleteAll")
    public Result<Object> deleteAll(WestUser user) {

        int num = user.deleteAll(
                West.lambdaDelete(User.class).eq(User::getName, "west").or(q -> q.isNull(User::getAge))
        );
        return Result.successResult(num);
    }


    @RequestMapping("/bd/user/update")
    public Result<Object> updateToPrams(WestUser user) {
        int num = user.update(
                West.lambdaUpdate(User.class).update(new User().setAge(10)).set(User::getAge, 10).eq(User::getId, 20L)
        );
        return Result.successResult(num);
    }

    @RequestMapping("/bd/user/findAll")
    public Result<Object> findALLByBd(TestDto dto) {
        WestUser dao = West.dao(WestUser.class);
        List<User> all = dao.findAll(
                getBuilder(dto)
        );
        return Result.successResult(all);
    }

    @RequestMapping("/bd/user/findOne")
    public Result<Object> findOneByBd(TestDto dto) {
        User one = West.dao(WestUser.class).findOne(
                getBuilder(dto)
        );
        return Result.successResult(one);
    }

    @RequestMapping("/bd/user/page")
    public Result<Object> pageByBd(TestDto dto) {
        Page<User> page = West.dao(WestUser.class).page(PageRequest.of(dto.getPageNum(), dto.getPageSize()),
                getBuilder(dto)
        );
        return Result.successResult(page);
    }

    private static LambdaQueryBuilder<User> getBuilder(TestDto dto) {
        return West.<User>lambdaQuery()
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

}