package com.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.entity.TUser;
import com.chat.mapper.UserMapper;
import com.chat.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, TUser> implements UserService {

    @Override
    public TUser getUserByUsername(String username) {
        return this.baseMapper.selectOne(new QueryWrapper<TUser>().eq("username", username));
    }

    @Override
    public TUser getUserByEmail(String email) {
        return this.baseMapper.selectOne(new QueryWrapper<TUser>().eq("email", email));
    }

}
