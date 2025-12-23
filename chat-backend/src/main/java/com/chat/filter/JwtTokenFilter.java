package com.chat.filter;

import com.chat.util.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT令牌验证过滤器
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        logger.debug("请求路径: " + path + " 方法: " + method);

        // 不需要认证的路径（支持 /auth/ 和 /api/auth/）
        if (path.startsWith("/auth/") || path.startsWith("/api/auth/") ||
            path.equals("/health") || path.startsWith("/ws/") || path.startsWith("/api/ws/")) {
            logger.debug("路径 " + path + " 不需要认证，直接通过");
            filterChain.doFilter(request, response);
            return;
        }

        // OPTIONS 请求不需要认证（CORS预检）
        if ("OPTIONS".equalsIgnoreCase(method)) {
            logger.debug("OPTIONS 预检请求，直接通过");
            filterChain.doFilter(request, response);
            return;
        }

        // 获取授权头
        String authHeader = request.getHeader("Authorization");
        logger.debug("Authorization header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // 验证令牌有效性
                if (TokenUtil.validateToken(token)) {
                    logger.debug("令牌有效，允许请求");
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                logger.debug("令牌验证失败: " + e.getMessage());
            }
        }

        // 令牌无效或不存在
        logger.warn("未授权访问: " + path);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"Unauthorized\",\"data\":null}");
    }
}


