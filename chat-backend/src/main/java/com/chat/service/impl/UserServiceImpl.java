package com.chat.service.impl;

import com.chat.dto.RegisterRequest;
import com.chat.entity.TUser;
import com.chat.repository.UserRepository;
import com.chat.service.UserService;
import com.chat.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public TUser getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public TUser getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public TUser registerUser(RegisterRequest request) throws Exception {
        // 检查用户名是否已存在
        TUser existingUserByUsername = getUserByUsername(request.getUsername());
        if (existingUserByUsername != null) {
            throw new Exception("用户名已存在");
        }

        // 检查邮箱是否已存在
        TUser existingUserByEmail = getUserByEmail(request.getEmail());
        if (existingUserByEmail != null) {
            throw new Exception("邮箱已被注册");
        }

        // 创建新用户
        TUser user = TUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(PasswordUtil.encodePassword(request.getPassword()))
                .nickname(request.getUsername())
                .avatar(null)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 保存用户
        return userRepository.save(user);
    }

    @Override
    public TUser save(TUser user) {
        return userRepository.save(user);
    }

}

