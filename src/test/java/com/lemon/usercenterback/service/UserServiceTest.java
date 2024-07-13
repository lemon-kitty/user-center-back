package com.lemon.usercenterback.service;
import java.util.Date;

import com.lemon.usercenterback.mapper.UserMapper;
import com.lemon.usercenterback.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("111");
        user.setUserAccount("222");
        user.setAvatarUrl("333");
        user.setUserPassword("xxx");
        user.setPhone("444");
        user.setEmail("555");

        boolean result = userService.save(user);
        System.out.println("用户的id"+user.getId());
        Assertions.assertTrue(result);

    }

    @Test
    void userRegister() {
        //非空
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "123456";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        //长度
        userAccount = "yu";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        userAccount = "yupi";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        //特殊字符
        userAccount = "yu pi";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        //重复
        userAccount = "222";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        userAccount = "lemon";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);


    }
}