package com.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.entity.TChannel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 频道Mapper接口
 */
@Mapper
public interface ChannelMapper extends BaseMapper<TChannel> {

}
