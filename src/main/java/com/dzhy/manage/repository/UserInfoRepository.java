package com.dzhy.manage.repository;

import com.dzhy.manage.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @ClassName UserInfoRepository
 * @Description user info
 * @Author alex
 * @Date 2018/10/30
 **/
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    UserInfo findByUserInfoId(Integer userId);

    UserInfo findByUserInfoName(String userInfoName);

    Page<UserInfo> findAllByUserInfoTrueNameContaining(String userTrueName, Pageable pageable);

    void deleteAllByUserInfoIdIn(List<Integer> userIds);

    boolean existsByUserInfoName(String username);
}
