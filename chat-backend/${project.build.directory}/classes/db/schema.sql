-- 创建数据库
CREATE DATABASE IF NOT EXISTS chat CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE chat;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 频道表
CREATE TABLE IF NOT EXISTS t_channel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '频道ID',
    name VARCHAR(100) NOT NULL COMMENT '频道名称',
    description VARCHAR(500) COMMENT '频道描述',
    creator_id BIGINT NOT NULL COMMENT '创建者ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (creator_id) REFERENCES t_user(id) ON DELETE CASCADE,
    INDEX idx_creator_id (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='频道表';

-- 用户-频道关联表
CREATE TABLE IF NOT EXISTS t_user_channel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    channel_id BIGINT NOT NULL COMMENT '频道ID',
    join_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    UNIQUE KEY unique_user_channel (user_id, channel_id),
    FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE,
    FOREIGN KEY (channel_id) REFERENCES t_channel(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_channel_id (channel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-频道关联表';

-- 群消息表
CREATE TABLE IF NOT EXISTS t_group_msg (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    channel_id BIGINT NOT NULL COMMENT '频道ID',
    content LONGTEXT NOT NULL COMMENT '消息内容',
    send_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    FOREIGN KEY (sender_id) REFERENCES t_user(id) ON DELETE CASCADE,
    FOREIGN KEY (channel_id) REFERENCES t_channel(id) ON DELETE CASCADE,
    INDEX idx_channel_id (channel_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_send_time (send_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群消息表';

-- 私聊消息表
CREATE TABLE IF NOT EXISTS t_private_msg (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    content LONGTEXT NOT NULL COMMENT '消息内容',
    send_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    FOREIGN KEY (sender_id) REFERENCES t_user(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES t_user(id) ON DELETE CASCADE,
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_send_time (send_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私聊消息表';
