-- 初始化测试数据

-- 插入测试用户（密码都是test123，已加密）
INSERT INTO t_user (username, email, password, nickname) VALUES
('admin', 'admin@chat.com', 'xNRJ4kKKiLQB4rkBLTwBYGON7jIFZQC8nT7S5V5qRd4=', '管理员'),
('user1', 'user1@chat.com', 'xNRJ4kKKiLQB4rkBLTwBYGON7jIFZQC8nT7S5V5qRd4=', '用户1'),
('user2', 'user2@chat.com', 'xNRJ4kKKiLQB4rkBLTwBYGON7jIFZQC8nT7S5V5qRd4=', '用户2');

-- 插入初始频道
INSERT INTO t_channel (name, description, creator_id) VALUES
('技术讨论', '讨论技术相关问题', 1),
('闲聊天地', '随意聊天和交流', 1),
('公告', '系统公告和重要信息', 1);

-- 添加用户到频道
INSERT INTO t_user_channel (user_id, channel_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2), (2, 3),
(3, 1), (3, 2), (3, 3);

-- 插入一些示例消息
INSERT INTO t_group_msg (sender_id, channel_id, content) VALUES
(1, 1, '欢迎使用在线聊天室！'),
(2, 2, '大家好，这是第一条消息'),
(3, 2, '很高兴认识大家！');
