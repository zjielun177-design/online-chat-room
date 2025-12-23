package com.chat.repository;

import com.chat.entity.TChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 频道Repository
 */
@Repository
public interface ChannelRepository extends JpaRepository<TChannel, Long> {

}
