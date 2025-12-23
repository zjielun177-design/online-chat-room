package com.chat.service.impl;

import com.chat.dto.RegisterRequest;
import com.chat.dto.UpdateProfileRequest;
import com.chat.entity.TChannel;
import com.chat.entity.TUser;
import com.chat.repository.UserRepository;
import com.chat.service.ChannelService;
import com.chat.service.UserService;
import com.chat.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelService channelService;

    @Override
    public TUser getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

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
        TUser savedUser = userRepository.save(user);

        List<TChannel> channels = channelService.getAllChannels();
        for (TChannel channel : channels) {
            try {
                channelService.joinChannel(savedUser.getId(), channel.getId());
            } catch (IllegalArgumentException ignored) {
            }
        }

        return savedUser;
    }

    @Override
    public TUser save(TUser user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public TUser updateProfile(Long userId, UpdateProfileRequest request) throws Exception {
        TUser user = getUserById(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }

        if (request == null) {
            return user;
        }

        String email = request.getEmail();
        if (email != null && !email.trim().isEmpty() && !email.equals(user.getEmail())) {
            TUser existingByEmail = getUserByEmail(email.trim());
            if (existingByEmail != null && !existingByEmail.getId().equals(userId)) {
                throw new Exception("邮箱已被占用");
            }
            user.setEmail(email.trim());
        }

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname().trim().isEmpty() ? null : request.getNickname().trim());
        }

        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar().trim().isEmpty() ? null : request.getAvatar().trim());
        }

        if (request.getNewPassword() != null) {
            String newPwd = request.getNewPassword().trim();
            if (!newPwd.isEmpty()) {
                user.setPassword(PasswordUtil.encodePassword(newPwd));
            }
        }

        user.setUpdateTime(LocalDateTime.now());
        return userRepository.save(user);
    }

}
