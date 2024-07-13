package com.lemon.usercenterback.model.domain.request;

import lombok.Data;

/**
 * 用户登录请求体
 *
 * @author tmm
 */
@Data
public class UserLoginRequest {
    public String userAccount;
    public String userPassword;
}
