package com.chat.repository;

import com.chat.entity.TUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户Repository
 */
@Repository
public interface UserRepository extends JpaRepository<TUser, Long> {

    TUser findByUsername(String username);

    TUser findByEmail(String email);

}
