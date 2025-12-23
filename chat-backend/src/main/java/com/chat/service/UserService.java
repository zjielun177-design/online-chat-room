package com.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chat.entity.TUser;

/**
 * 用户服务接口
 */
public interface UserService extends IService<TUser> {

    /**
     * 根据用户名查询用户
     */
    TUser getUserByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    TUser getUserByEmail(String email);

}
