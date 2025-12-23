package com.chat.controller;

import com.chat.dto.ApiResponse;
import com.chat.dto.AuthResponse;
import com.chat.dto.LoginRequest;
import com.chat.dto.RegisterRequest;
import com.chat.dto.UserVO;
import com.chat.entity.TUser;
import com.chat.service.UserService;
import com.chat.util.PasswordUtil;
import com.chat.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户认证接口
 */
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            // 验证输入
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ApiResponse.error(400, "用户名不能为空");
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ApiResponse.error(400, "邮箱不能为空");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ApiResponse.error(400, "密码不能为空");
            }

            // 注册用户
            TUser user = userService.registerUser(request);

            // 生成Token
            String token = TokenUtil.generateToken(user.getId(), user.getUsername());

            // 构建响应
            UserVO userVO = UserVO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .avatar(user.getAvatar())
                    .build();

            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .user(userVO)
                    .message("注册成功")
                    .build();

            return ApiResponse.success(authResponse);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            // 验证输入
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ApiResponse.error(400, "用户名不能为空");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ApiResponse.error(400, "密码不能为空");
            }

            // 查询用户
            TUser user = userService.getUserByUsername(request.getUsername());
            if (user == null) {
                // 尝试用邮箱查询
                user = userService.getUserByEmail(request.getUsername());
            }

            if (user == null) {
                return ApiResponse.error(401, "用户不存在");
            }

            // 验证密码
            if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
                return ApiResponse.error(401, "密码错误");
            }

            // 生成Token
            String token = TokenUtil.generateToken(user.getId(), user.getUsername());

            // 构建响应
            UserVO userVO = UserVO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .avatar(user.getAvatar())
                    .build();

            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .user(userVO)
                    .message("登录成功")
                    .build();

            return ApiResponse.success(authResponse);
        } catch (Exception e) {
            return ApiResponse.error(500, "登录失败：" + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        return ApiResponse.success("登出成功");
    }

}
