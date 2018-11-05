package com.dzhy.manage.security.service;

import com.dzhy.manage.repository.UserInfoRepository;
import com.dzhy.manage.security.entity.JwtUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @ClassName UserDetailsServiceImpl
 * @Description UserDetails
 * @Author alex
 * @Date 2018/11/2
 **/
@Service("iUserDetailsService")
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserInfoRepository userInfoRepository;

    @Autowired
    public UserDetailsServiceImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return Optional
                .ofNullable(userInfoRepository.findByUserInfoName(userName))
                .map(JwtUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + userName));
    }
}
