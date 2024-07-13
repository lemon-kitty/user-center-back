package com.lemon.usercenterback.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author tmm
 */
@Data
public class UserRegisterRequest implements Serializable {

    public String userAccount;

    public String userPassword;

    public String checkPassword;
}
