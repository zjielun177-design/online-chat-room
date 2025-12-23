-- 初始化脚本：重建 chat 数据库、所有表并插入测试数据
DROP DATABASE IF EXISTS chat;
CREATE DATABASE chat CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE chat;

-- 用户表（含账号/邮箱索引）
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_username (username),
    INDEX idx_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 频道表（记录创建者并加速查询）
CREATE TABLE t_channel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    creator_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES t_user(id) ON DELETE CASCADE,
    INDEX idx_channel_creator (creator_id),
    UNIQUE KEY unique_channel_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户-频道关联表（确保用户不会重复加入且便于统计）
CREATE TABLE t_user_channel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    channel_id BIGINT NOT NULL,
    join_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE,
    FOREIGN KEY (channel_id) REFERENCES t_channel(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_channel (user_id, channel_id),
    INDEX idx_userchannel_user (user_id),
    INDEX idx_userchannel_channel (channel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 群聊消息表（按时间和频道索引）
CREATE TABLE t_group_msg (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    channel_id BIGINT NOT NULL,
    content LONGTEXT NOT NULL,
    send_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES t_user(id) ON DELETE CASCADE,
    FOREIGN KEY (channel_id) REFERENCES t_channel(id) ON DELETE CASCADE,
    INDEX idx_groupmsg_channel (channel_id),
    INDEX idx_groupmsg_sender (sender_id),
    INDEX idx_groupmsg_time (send_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 私聊消息表（双向索引便于查询）
CREATE TABLE t_private_msg (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content LONGTEXT NOT NULL,
    send_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES t_user(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES t_user(id) ON DELETE CASCADE,
    INDEX idx_privatemsg_sender (sender_id),
    INDEX idx_privatemsg_receiver (receiver_id),
    INDEX idx_privatemsg_time (send_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 默认测试用户（密码均为 test123）
INSERT INTO t_user (username, email, password, nickname) VALUES
('admin', 'admin@chat.com', 'baHEqzqbY5Z3gqQiAsjp3fQq/w/PA08DevNMthK2DlI=', '管理员'),
('user1', 'user1@chat.com', 'baHEqzqbY5Z3gqQiAsjp3fQq/w/PA08DevNMthK2DlI=', '用户1'),
('user2', 'user2@chat.com', 'baHEqzqbY5Z3gqQiAsjp3fQq/w/PA08DevNMthK2DlI=', '用户2');

-- 默认频道
INSERT INTO t_channel (name, description, creator_id) VALUES
('技术讨论', '讨论技术问题的开放频道', 1),
('闲聊天地', '随意聊聊天的轻松角落', 1),
('公告', '系统公告和重要通知', 1);

-- 默认成员关联（全部用户加入所有频道）
INSERT INTO t_user_channel (user_id, channel_id) VALUES
(1, 1), (2, 1), (3, 1),
(1, 2), (2, 2), (3, 2),
(1, 3), (2, 3), (3, 3);

-- 示例群消息
INSERT INTO t_group_msg (sender_id, channel_id, content) VALUES
(1, 1, '欢迎使用在线聊天室！'),
(2, 2, '大家好，这是第一条消息！'),
(3, 2, '很高兴认识大家！');
