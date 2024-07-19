# Wise Execution, Simple Tools

## [WestDao](https://github.com/westwong/westDao)

[中文版](https://github.com/westwong/westDao/blob/master/README.md)

Welcome to WestDao! This project aims to streamline daily development tasks with fewer lines of code, providing direct DEMO.

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

With just a few lines of code, user information is saved.

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
    User user = user.findById();
    return Result.successResult(user);
}

@RequestMapping("/v3/user/find")
public Result<Object> findByIdV3(User user) {
    Assert.notNull(user.getId(), "id is required");
    User db = new WestUser(user).findById();
    return Result.successResult(db);
}

@PostMapping("/user/update")
public Result<Object> updateById(WestUser user) {
    Assert.notNull(user.getId(), "id is required");
    user.updateById();
    return Result.successResult();
}
```

Similarly, delete, find, and update are seamlessly handled.

### Introduction

[WestDao](https://github.com/westwong/westDao/tree/master/WestDaoCore) is a persistence layer framework built on [Spring Data JPA](https://spring.io/projects/spring-data-jpa), borrowing the coding style from [MyBatis-plus](https://baomidou.com/), capable of dynamically generating [JPQL](https://docs.oracle.com/cd/E29542_01/apirefs.1111/e13946/ejb3_langref.html).

While fully retaining the native features of JPA, you can enjoy the convenience of JPA's table-free management and the fluent chainable code similar to MyBatis-plus. Powerful JPQL ensures compatibility across platforms without worries.

```java
private static LambdaQuery<User> getJPQL(TestDto dto) {
        return West.<User>queryJPQL()
                .eq(dto.isEq(), User::getId, 20L)
                // Special mention for 'or' due to the original framework of JPA, where the parentheses of JPQL's 'or()' are 					// omitted. Pay attention during debugging.
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
@RequestMapping("/v1/user/update")
public Result<Object> updateToPrams(WestUser user) {
    int num = user.update(
            West.updateJPQL(User.class).update(new User().setAge(10)).eq(User::getId, 20L)
    );
    return Result.successResult(num);
}
```

In summary, the entity handles itself. It provides all the persistence layer methods, eliminating the need to worry about how to invoke them (**Wise Execution**).

Just focus on writing (**Simple Tools**).

Of course, I also empower DAO with chain calls:

```java
@RequestMapping("/v2/user/update")
@Transactional
public Result<Object> updateToPramsV2(User user) {
    int execute = West.updateJPQL(user).eq(User::getId, 20L).execute();
    return Result.successResult(execute);
}

@RequestMapping("/v2/user/findAll")
public Result<Object> findALLV2(TestDto dto) {
    Map<String, Object> count = getJPQL(dto).select("count(1) as total").getMap();
    Map<String, Object> sum = getJPQL(dto).select("sum(age) as total").getMap();
    Map<String, Object> map = getJPQL(dto).getMap();
    List<Map<String, Object>> maps = getJPQL(dto).listMap();

    User entity = getJPQL(dto).getEntity();
    List<User> users = getJPQL(dto).listEntity();
    Map<String, Object> result = new HashMap<>(4);
    result.put("count", count);
    result.put("sum", sum);
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
    int execute1 = West.<User>deleteJPQL().execute();
    return Result.successResult(execute);
}
// Support for group, limit, and orderBy
@RequestMapping("/v2/user/list")
public Result<Object> listV2() {
    List<User> orderBY = West.<User>queryJPQL()
            .orderByDesc(User::getAge)
            .orderByAsc(User::getId)
            .limit(10)
            .listEntity();
    List<Map<String, Object>> group = West.<User>queryJPQL()
         // If no alias is provided, it defaults to 'colnum0'
            .select("age as age, count(1) as num")
            .groupBy(User::getAge)
            .having("age > 10")
            .orderByAsc(User::getAge)
            .listMap();
    Map<String, Object> result = new HashMap<>(4);
    result.put("orderBY", orderBY);
    result.put("group", group);
    return Result.successResult(result);
}
```

For those experienced in project development, seeing this, you already know the advantages. Forget about services, DAOs, and repositories for now. A controller can handle what you need without complicating things. Simple data requires simple solutions. You only need services for complex multi-table logic, saving you valuable time.

### Getting Started

To empower entity objects with these capabilities is simple. Just add `@WestDao` alongside `@Entity` for entities annotated with `@Entity`.

The recommendation is to add `@Accessors(chain = true)`, which will grant chain-call capability to subclasses as well.

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

Yes, just `@WestDao(prefix = "west")`. "west" can be any string you prefer, like "love", "me", "you", or "like".

Oh, if you're reading this, and it doesn't matter, but if you follow along and haven't imported dependencies yet, you might curse because you haven't yet introduced the dependency according to the version.
Import according to the version `<westdao.version>latest</westdao.version>`. For details, please see the releases.

In `<annotationProcessorPaths>`, place `westdao-core` after ` lombok` to ensure the execution order.

```xml
<dependency>
    <groupId>cn.k2future</groupId>
    <artifactId>westdao-core</artifactId>
    <version>${westdao.version}</version>
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
                <groupId>cn.k2future</groupId>
                <artifactId>westdao-core</artifactId>
                <version>${westdao.version}</version>
            </path>
             <path>
                  <groupId>org.projectlombok</groupId>
                  <artifactId>lombok</artifactId>
                  <version>1.18.24</version>
             </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

Compile first with `mvn compile`. This way, under the target of `@Entity`, you'll find generated class files prefixed with the entity name. It saves, modifies, and deletes it.

```java
public class WestUser extends User implements WestDao<User>
public class LikeUser extends User implements WestDao<User>
public class MyUser extends User implements WestDao<User>
```

Current supported versions: Spring Boot 2.X, JDK 1.8. For strictness, I tested with: 2.3.12.RELEASE.

If you're a beginner? Check out the complete [pom](https://github.com/westwong/westDao/blob/master/WestDaoTest/pom.xml) file first? Still confused? Then check out the [testDemo](https://github.com/westwong/westDao/tree/master/WestDaoTest) again. Still have questions? Send me an email at deadshoot@foxmail.com.

Finally, welcome all experts to submit [Issue](https://github.com/westwong/westDao/issues) and [Pull request](https://github.com/westwong/westDao/pulls).

Got any great ideas to discuss with me? WeChat: deadshoot.

To reiterate our goal: **Wise Execution, Simple Tools**.





3.5