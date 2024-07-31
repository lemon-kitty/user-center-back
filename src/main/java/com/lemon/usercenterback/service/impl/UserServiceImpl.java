package com.lemon.usercenterback.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lemon.usercenterback.common.ErrorCode;
import com.lemon.usercenterback.exception.BusinessException;
import com.lemon.usercenterback.model.domain.User;
import com.lemon.usercenterback.service.UserService;
import com.lemon.usercenterback.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lemon.usercenterback.constant.UserConstant.USER_LOGIN_STATUS;

/**
* @author tongmm
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-07-06 15:29:20
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    //盐值，混淆密码
    private static final String SALT = "lemon";

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {

        /*
        * 1、用户在前端输入账户和密码，校验码
          2、校验用户账户、密码、校验密码，是否符合要求
            1）、非空
            2）、账户 不小于 4位
            3）、密码 不小于 8位
            4）、账户不能重复，
            5）、账户不包含特殊字符
            6）、密码和校验密码相同
        3、对密码进行加密
        4、向数据库插入用户数据
        * */
        if (StringUtils.isAllBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户/密码/校验密码为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户长度小于4位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码或校验密码长度小于8位");
        }
        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能包含特殊字符");
        }

        if (!checkPassword.equals(userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验密码不一样");
        }

        //账户不能重复，放到最后一步，因为涉及到操作数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("userAccount",userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户重复");
        }

        //密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean result = this.save(user);
        if (!result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        /*
        1、校验用户账户、密码是否符合要求
            1）、非空
            2）、账户 不小于 4位
            3）、密码 不小于 8位
            4）、账户不包含特殊字符
        2、校验密码是否输入正确，要和数据库中的密文密码对比
        3、用户信息脱敏，隐藏敏感信息，防止数据库中的字段泄露
        4、记录用户的登录态（session），将其存到服务器上（用后端springboot框架封装的服务器tomcat去记录）
        5、返回脱敏后的信息
        * */
        if (StringUtils.isAllBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //2、校验密码是否输入正确，要和数据库中的密文密码对比
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            log.info("user login failed,account and password can not match");
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //3、用户信息脱敏，隐藏敏感信息，防止数据库中的字段泄露
        User safetyUser = getSafetyUser(user);

        //4、记录用户的登录态（session），将其存到服务器上（用后端springboot框架封装的服务器tomcat去记录）
        request.getSession().setAttribute(USER_LOGIN_STATUS,safetyUser);
        //5、返回脱敏后的信息
        return safetyUser;
    }


    @Override
    public User getSafetyUser(User user) {
        if (user == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATUS);
        return 1;
    }
}




