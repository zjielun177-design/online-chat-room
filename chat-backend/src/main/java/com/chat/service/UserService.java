package com.chat.service;

import com.chat.dto.RegisterRequest;
import com.chat.entity.TUser;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名查询用户
     */
    TUser getUserByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    TUser getUserByEmail(String email);

    /**
     * 注册用户
     */
    TUser registerUser(RegisterRequest request) throws Exception;

    /**
     * 保存用户
     */
    TUser save(TUser user);

}

