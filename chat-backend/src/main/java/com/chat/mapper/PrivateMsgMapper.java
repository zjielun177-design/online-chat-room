package com.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.entity.TPrivateMsg;
import org.apache.ibatis.annotations.Mapper;

/**
 * 私聊消息Mapper接口
 */
@Mapper
public interface PrivateMsgMapper extends BaseMapper<TPrivateMsg> {

}
