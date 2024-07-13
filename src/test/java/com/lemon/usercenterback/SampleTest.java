package com.lemon.usercenterback;


import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
//@RunWith(SpringRunner.class)
//在默认的测试类 UserCenterBackApplicationTests 不需要添加，自己新建的要加这个注解
//因为用的junit是另外依赖引入的，所以要添加这个，如果是spring内置的junit,就不需要
public class SampleTest {

    /*@Resource
    private UserMapper userMapper;

    @Test
    public void testSelect(){
        System.out.println("---");
        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);
    }*/
}
