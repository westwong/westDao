# Wise Exectution  Simple Tools

## [WestDao](https://github.com/westwong/westDao)

[English](https://github.com/westwong/westDao/blob/master/README-en.md)

欢迎来到 WestDao！这个项目旨在 用更少的代码完成日常开发工作，直接给出DEMO

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

几行代码，便完成了用户信息保存

```java
    @PostMapping("/user/delete")
    public Result<Object> deleteById(WestUser user) {
        Assert.notNull(user.getId(), "id is required");
        boolean success = user.deleteById();
        return Result.successResult(success);
    }
    @RequestMapping("/user/find")
    public Result<Object> findById(WestUser user) {
        Assert.notNull(user.getId(), "id is required");
        User User = user.findById();
        return Result.successResult(User);
    }
    @PostMapping("/user/update")
    public Result<Object> updateById(WestUser user) {
        Assert.notNull(user.getId(), "id is required");
        user.updateById();
        return Result.successResult();
    }
}
```

同理，删、查、改也不会掉链子

### 介绍

[westDao](https://github.com/westwong/westDao/tree/master/WestDaoCore)是基于[Spring Data JPA](https://spring.io/projects/spring-data-jpa)完成的持久层框架，并且借鉴了[MyBatis-plus](https://baomidou.com/) 的代码风格，能够动态的生成[JPQL](https://docs.oracle.com/cd/E29542_01/apirefs.1111/e13946/ejb3_langref.html)，

完整的保留JPA的原生属性，你在享受JPA无表管理的方便之余，也能感受到如MyBatis-plus般的链式代码，强大JPQL让你对平台兼容性再无后顾之忧

```java
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
```

```
@RequestMapping("/bd/user/update")
public Result<Object> updateToPrams(WestUser user) {
    int num = user.update(
            West.lambdaUpdate(User.class).update(new User().setAge(10)).eq(User::getId, 20L)
    );
    return Result.successResult(num);
}
```

总之一个思想，对象自己处理自己，自己给自己提供一切持久层方法，你不用关心如何调用（**Wise Exection**），

只管写（**Simple Tools**)。

我相信项目做得多的朋友，看到这里已经能知道优势了，什么service，什么dao，什么Respositroy ？我们通通暂时不管，一个Controller 能解决的事情，不要搞的那么麻烦。简单的数据有简单的处理办法，对于一些复杂多表逻辑你才有创建service的必要，毕竟省出来的时间是你的

### 开始

赋予实体对象以上能力，其实也很简单，你只需在标注@Entity的实体类上，再增加一个@WestDao

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
    @Column()
    private LocalDateTime createTime;
    @PrePersist
    public void prePersistCreateTime() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
    }
}
```

是的，就只需要一个＠ＷestDao(prefix = "west")，当然"west"也可以是你喜欢的任何字符串，比如 love、me、you、like

哦，对了，你如果是看到这里，无所谓的，但是如果你跟着做，你要骂人了，因为你还没有引入依赖

```xml
<dependency>
    <groupId>com.k2future</groupId>
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
                <groupId>com.k2future</groupId>
                <artifactId>westdao-core</artifactId>
                <version>1.2.4</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

我们还需要先编译一下，编译就是用 mvn compile

这样@Entity的 target 的同级目录下，就会生成以 prefix + entity 名字class 文件，接参是它，保存是它，修改是他，删除也是它，用好它吧

```java
public class WestUser extends User implements WestDao<User>
public class LikeUser extends User implements WestDao<User>
public class MyUser extends User implements WestDao<User>
```

如果你是新手？完整的 [pom ](https://github.com/westwong/westDao/blob/master/WestDaoTest/pom.xml)文件你先看看？
还是不懂？那再看看  [testDemo](https://github.com/westwong/westDao/tree/master/WestDaoTest) 
还有疑问？给我发邮件吧 deadshoot@foxmail.com 

最后欢迎各位大佬 提交  [Issue](https://github.com/westwong/westDao/issues) 和 [Pull request](https://github.com/westwong/westDao/pulls)

你有什么好的想法想跟我交流的微信：deadshoot

最后再强调一下我们的目标：**Wise Execution , Simple Tools**