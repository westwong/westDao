# Wise Execution, Simple Tools

## [WestDao](https://github.com/westwong/westDao)

[中文版](https://github.com/westwong/westDao/blob/master/README.md)

Welcome to WestDao! This project aims to simplify daily development tasks with less code. Let's dive straight into a DEMO:

```java
@RestController
public class WestUserController {
    @PostMapping("/user/save")
    public Result<Object> saveUser(@RequestBody WestUser user) {
        user.setAvatar("default.png").save();
        return Result.successResult();
    }
}
```

With just a few lines of code, you can save user information.

```
    @PostMapping("/user/delete")
    public Result<Object> deleteById(WestUser user) {
        Assert.notNull(user.getId(), "id is required");
        boolean success = user.deleteById();
        return Result.successResult(success);
    }

    @RequestMapping("/user/find")
    public Result<Object> findById(WestUser user) {
        Assert.notNull(user.getId(), "id is required");
        User user = user.findById();
        return Result.successResult(user);
    }

    @PostMapping("/user/update")
    public Result<Object> updateById(WestUser user) {
        Assert.notNull(user.getId(), "id is required");
        user.updateById();
        return Result.successResult();
    }
}
```

Similarly, delete, find, and update operations are also handled with ease.

### Introduction

[WestDao](https://github.com/westwong/westDao/tree/master/WestDaoCore) is a persistence framework based on [Spring Data JPA](https://spring.io/projects/spring-data-jpa) that adopts the coding style of [MyBatis-plus](https://baomidou.com/) and dynamically generates [JPQL](https://docs.oracle.com/cd/E29542_01/apirefs.1111/e13946/ejb3_langref.html).

WestDao retains the native attributes of JPA, allowing you to enjoy the convenience of JPA's schema-free management while benefiting from MyBatis-plus style fluent code. The powerful JPQL ensures cross-platform compatibility, alleviating any concerns.

```java
private static LambdaQueryBuilder<User> getBuilder(TestDto dto) {
    return West.<User>lambdaQuery()
            .eq(dto.isEq(), User::getId, 20L)
            .or(dto.isOr(), (q -> q.eq(User::getId, 18L).eq(User::getName, dto.getName())))
            .and(dto.isAnd(), (q -> q.eq(User::getId, 18L).or(q1 -> q1.eq(User::getName, dto.getName()))))
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
@RequestMapping("/bd/user/update")
public Result<Object> updateToPrams(WestUser user) {
    int num = user.update(
            West.lambdaUpdate(User.class).update(new User().setAge(10)).eq(User::getId, 20L)
    );
    return Result.successResult(num);
}
```

In essence, the philosophy is that objects handle their own persistence, providing all necessary methods. You don't need to worry about how to call them (**Wise Execution**), just write the code (**Simple Tools**).

For those experienced in multiple projects, the advantages are clear. No need for separate service, dao, or repository layers. A single controller can handle many tasks, reducing complexity. Simple data has simple handling methods, and only complex multi-table logic requires creating service layers, saving you valuable time.

### Getting Started

Granting entities these capabilities is straightforward. Just add the `@WestDao` annotation to your `@Entity` classes.

```java
@Entity
@Data
@Accessors(chain = true)
@WestDao(prefix = "west")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 32)
    private String name;
    @Column(columnDefinition = "text")
    private String avatar;
    private Integer age;
    @Column
    private LocalDateTime createTime;

    @PrePersist
    public void prePersistCreateTime() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
    }
}
```

Yes, all it takes is a `@WestDao(prefix = "west")`. You can customize "west" to any string you like, such as love, me, you, like.

Oh, and if you've read this far without following along, no problem. But if you're coding along, you might be frustrated because you haven't added the dependencies yet.

```xml
<dependency>
    <groupId>io.github.westwong</groupId>
    <artifactId>westdao-core</artifactId>
    <version>1.2.4</version>
</dependency>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <annotationProcessorPaths>
            <path>
                <groupId>io.github.westwong</groupId>
                <artifactId>westdao-core</artifactId>
                <version>1.2.4</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

We also need to compile with `mvn compile`.

Once compiled, the `@Entity` target's same-level directory will generate class files named with the prefix + entity name. These classes will handle receiving parameters, saving, modifying, and deleting.

```java
public class WestUser extends User implements WestDao<User>
public class LikeUser extends User implements WestDao<User>
public class MyUser extends User implements WestDao<User>
```

If you're a beginner, check out the complete [pom](https://github.com/westwong/westDao/blob/master/WestDaoTest/pom.xml) file. Still confused? Take a look at the [testDemo](https://github.com/westwong/westDao/tree/master/WestDaoTest). Have more questions? Email me at deadshoot@foxmail.com.

Finally, I welcome contributions from all experts. Submit [Issues](https://github.com/westwong/westDao/issues) and [Pull Requests](https://github.com/westwong/westDao/pulls).

For any ideas or discussions, feel free to contact me on WeChat: deadshoot.

Let me emphasize our goal once more: **Wise Execution, Simple Tools**.