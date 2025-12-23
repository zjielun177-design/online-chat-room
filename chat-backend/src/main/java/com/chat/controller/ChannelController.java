package com.chat.controller;

import com.chat.dto.ApiResponse;
import com.chat.dto.ChannelDTO;
import com.chat.entity.TChannel;
import com.chat.service.ChannelService;
import com.chat.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 频道管理接口
 */
@RestController
@RequestMapping("/channel")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    /**
     * 创建频道
     */
    @PostMapping
    public ApiResponse<ChannelDTO> createChannel(
            @RequestHeader("Authorization") String token,
            @RequestBody ChannelRequest request) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            Long userId = TokenUtil.getUserIdFromToken(tokenValue);

            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ApiResponse.error(400, "频道名不能为空");
            }

            TChannel channel = channelService.createChannel(request.getName(), request.getDescription(), userId);
            ChannelDTO dto = convertToDTO(channel, 1);
            return ApiResponse.success(dto);
        } catch (Exception e) {
            return ApiResponse.error(500, "创建频道失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有频道
     */
    @GetMapping("/list")
    public ApiResponse<List<ChannelDTO>> getAllChannels() {
        try {
            List<TChannel> channels = channelService.getAllChannels();
            List<ChannelDTO> dtos = channels.stream()
                    .map(c -> convertToDTO(c, channelService.getChannelMembers(c.getId()).size()))
                    .collect(Collectors.toList());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取频道列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户加入的频道
     */
    @GetMapping("/my-channels")
    public ApiResponse<List<ChannelDTO>> getUserChannels(
            @RequestHeader("Authorization") String token) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            Long userId = TokenUtil.getUserIdFromToken(tokenValue);

            List<TChannel> channels = channelService.getUserChannels(userId);
            List<ChannelDTO> dtos = channels.stream()
                    .map(c -> convertToDTO(c, channelService.getChannelMembers(c.getId()).size()))
                    .collect(Collectors.toList());
            return ApiResponse.success(dtos);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取频道列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取频道详情
     */
    @GetMapping("/{channelId}")
    public ApiResponse<ChannelDTO> getChannel(@PathVariable Long channelId) {
        try {
            TChannel channel = channelService.getChannelById(channelId);
            if (channel == null) {
                return ApiResponse.error(404, "频道不存在");
            }
            int memberCount = channelService.getChannelMembers(channelId).size();
            ChannelDTO dto = convertToDTO(channel, memberCount);
            return ApiResponse.success(dto);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取频道详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取频道成员
     */
    @GetMapping("/{channelId}/members")
    public ApiResponse<List<Long>> getChannelMembers(@PathVariable Long channelId) {
        try {
            List<Long> members = channelService.getChannelMembers(channelId);
            return ApiResponse.success(members);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取频道成员失败：" + e.getMessage());
        }
    }

    /**
     * 加入频道
     */
    @PostMapping("/{channelId}/join")
    public ApiResponse<String> joinChannel(
            @PathVariable Long channelId,
            @RequestHeader("Authorization") String token) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            Long userId = TokenUtil.getUserIdFromToken(tokenValue);

            if (channelService.isUserInChannel(userId, channelId)) {
                return ApiResponse.error(400, "您已经在该频道中");
            }

            channelService.joinChannel(userId, channelId);
            return ApiResponse.success("成功加入频道");
        } catch (Exception e) {
            return ApiResponse.error(500, "加入频道失败：" + e.getMessage());
        }
    }

    /**
     * 离开频道
     */
    @PostMapping("/{channelId}/leave")
    public ApiResponse<String> leaveChannel(
            @PathVariable Long channelId,
            @RequestHeader("Authorization") String token) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            Long userId = TokenUtil.getUserIdFromToken(tokenValue);

            channelService.leaveChannel(userId, channelId);
            return ApiResponse.success("成功离开频道");
        } catch (Exception e) {
            return ApiResponse.error(500, "离开频道失败：" + e.getMessage());
        }
    }

    /**
     * 更新频道
     */
    @PutMapping("/{channelId}")
    public ApiResponse<ChannelDTO> updateChannel(
            @PathVariable Long channelId,
            @RequestHeader("Authorization") String token,
            @RequestBody ChannelRequest request) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            Long userId = TokenUtil.getUserIdFromToken(tokenValue);

            TChannel channel = channelService.getChannelById(channelId);
            if (channel == null) {
                return ApiResponse.error(404, "频道不存在");
            }

            // 只有创建者可以更新频道
            if (!channel.getCreatorId().equals(userId)) {
                return ApiResponse.error(403, "只有频道创建者可以更新频道");
            }

            TChannel updated = channelService.updateChannel(channelId, request.getName(), request.getDescription());
            int memberCount = channelService.getChannelMembers(channelId).size();
            ChannelDTO dto = convertToDTO(updated, memberCount);
            return ApiResponse.success(dto);
        } catch (Exception e) {
            return ApiResponse.error(500, "更新频道失败：" + e.getMessage());
        }
    }

    /**
     * 删除频道
     */
    @DeleteMapping("/{channelId}")
    public ApiResponse<String> deleteChannel(
            @PathVariable Long channelId,
            @RequestHeader("Authorization") String token) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            Long userId = TokenUtil.getUserIdFromToken(tokenValue);

            TChannel channel = channelService.getChannelById(channelId);
            if (channel == null) {
                return ApiResponse.error(404, "频道不存在");
            }

            // 只有创建者可以删除频道
            if (!channel.getCreatorId().equals(userId)) {
                return ApiResponse.error(403, "只有频道创建者可以删除频道");
            }

            channelService.deleteChannel(channelId);
            return ApiResponse.success("频道已删除");
        } catch (Exception e) {
            return ApiResponse.error(500, "删除频道失败：" + e.getMessage());
        }
    }

    private ChannelDTO convertToDTO(TChannel channel, int memberCount) {
        return ChannelDTO.builder()
                .id(channel.getId())
                .name(channel.getName())
                .description(channel.getDescription())
                .creatorId(channel.getCreatorId())
                .createTime(channel.getCreateTime())
                .updateTime(channel.getUpdateTime())
                .memberCount(memberCount)
                .build();
    }

    /**
     * 频道请求DTO
     */
    public static class ChannelRequest {
        private String name;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
