package com.k2future.westdao.test.controller;

import com.k2future.westdao.test.entity.User;
import com.k2future.westdao.test.service.UserService;
import com.k2future.westdao.test.utils.resp.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Traditional UserController
 * @author west
 * @since 2024/6/22
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/trad")
public class TraditionalUserController {

    private final UserService userService;

    @RequestMapping ("/user/save")
    public Result<Object> saveUser(User dto){
        Assert.hasText(dto.getName(),"name is required");
        userService.save(dto);
       return Result.successResult();
    }
}