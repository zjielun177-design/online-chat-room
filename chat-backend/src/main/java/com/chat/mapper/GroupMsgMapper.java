package com.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.entity.TGroupMsg;
import org.apache.ibatis.annotations.Mapper;

/**
 * 群消息Mapper接口
 */
@Mapper
public interface GroupMsgMapper extends BaseMapper<TGroupMsg> {

}
