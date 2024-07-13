package com.lemon.usercenterback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lemon.usercenterback.mapper")
public class UserCenterBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserCenterBackApplication.class, args);
    }

}
