package com.chat.config;

import com.chat.entity.TChannel;
import com.chat.entity.TUser;
import com.chat.repository.UserRepository;
import com.chat.service.ChannelService;
import com.chat.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 在应用启动时自动准备默认用户和频道，避免在干净数据库中看不到频道。
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private static final String DEFAULT_PASSWORD = "test123";

    private static final List<DefaultUser> DEFAULT_USERS = Arrays.asList(
            new DefaultUser("admin", "admin@chat.com", "管理员"),
            new DefaultUser("user1", "user1@chat.com", "用户1"),
            new DefaultUser("user2", "user2@chat.com", "用户2")
    );

    private static final List<DefaultChannel> DEFAULT_CHANNELS = Arrays.asList(
            new DefaultChannel("技术讨论", "讨论技术问题的开放频道"),
            new DefaultChannel("闲聊天地", "随意聊聊天的轻松角落"),
            new DefaultChannel("公告", "系统公告和重要通知")
    );

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelService channelService;

    @Override
    public void run(ApplicationArguments args) {
        ensureUsers();
        ensureChannels();
    }

    private void ensureUsers() {
        if (userRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();
            for (DefaultUser config : DEFAULT_USERS) {
                TUser user = TUser.builder()
                        .username(config.username)
                        .email(config.email)
                        .password(PasswordUtil.encodePassword(DEFAULT_PASSWORD))
                        .nickname(config.nickname)
                        .avatar(null)
                        .createTime(now)
                        .updateTime(now)
                        .build();
                userRepository.save(user);
                log.info("初始化默认用户 {}", config.username);
            }
        }
    }

    private void ensureChannels() {
        if (!channelService.getAllChannels().isEmpty()) {
            return;
        }

        TUser admin = userRepository.findByUsername("admin");
        if (admin == null) {
            log.warn("默认频道需要 admin 用户，跳过频道初始化");
            return;
        }

        for (DefaultChannel config : DEFAULT_CHANNELS) {
            try {
                TChannel channel = channelService.createChannel(config.name, config.description, admin.getId());
                joinMembers(channel, "admin", "user1", "user2");
                log.info("初始化频道 {}", config.name);
            } catch (Exception e) {
                log.error("初始化频道 {} 出错：{}", config.name, e.getMessage(), e);
            }
        }
    }

    private void joinMembers(TChannel channel, String... usernames) {
        for (String username : usernames) {
            TUser member = userRepository.findByUsername(username);
            if (member == null) {
                continue;
            }
            try {
                channelService.joinChannel(member.getId(), channel.getId());
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private static class DefaultUser {
        final String username;
        final String email;
        final String nickname;

        DefaultUser(String username, String email, String nickname) {
            this.username = username;
            this.email = email;
            this.nickname = nickname;
        }
    }

    private static class DefaultChannel {
        final String name;
        final String description;

        DefaultChannel(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
