package com.chat.controller;

import com.chat.dto.ApiResponse;
import com.chat.dto.AuthResponse;
import com.chat.dto.LoginRequest;
import com.chat.dto.RegisterRequest;
import com.chat.dto.UpdateProfileRequest;
import com.chat.dto.UserVO;
import com.chat.entity.TUser;
import com.chat.service.UserService;
import com.chat.util.PasswordUtil;
import com.chat.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户认证控制器
 */
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ApiResponse.error(400, "用户名不能为空");
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ApiResponse.error(400, "邮箱不能为空");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ApiResponse.error(400, "密码不能为空");
            }

            TUser user = userService.registerUser(request);
            String token = TokenUtil.generateToken(user.getId(), user.getUsername());
            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .user(buildUserVO(user))
                    .message("注册成功")
                    .build();
            return ApiResponse.success(authResponse);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ApiResponse.error(400, "用户名不能为空");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ApiResponse.error(400, "密码不能为空");
            }

            TUser user = userService.getUserByUsername(request.getUsername());
            if (user == null) {
                user = userService.getUserByEmail(request.getUsername());
            }

            if (user == null) {
                return ApiResponse.error(401, "用户不存在");
            }

            if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
                return ApiResponse.error(401, "密码错误");
            }

            String token = TokenUtil.generateToken(user.getId(), user.getUsername());
            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .user(buildUserVO(user))
                    .message("登录成功")
                    .build();
            return ApiResponse.success(authResponse);
        } catch (Exception e) {
            return ApiResponse.error(500, "登录失败：" + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ApiResponse<UserVO> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateProfileRequest request) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            Long userId = TokenUtil.getUserIdFromToken(tokenValue);
            TUser updated = userService.updateProfile(userId, request);
            return ApiResponse.success(buildUserVO(updated));
        } catch (Exception e) {
            return ApiResponse.error(500, "更新个人信息失败：" + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        return ApiResponse.success("登出成功");
    }

    private UserVO buildUserVO(TUser user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }
}
