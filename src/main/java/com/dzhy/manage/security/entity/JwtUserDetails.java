package com.dzhy.manage.security.entity;

import com.dzhy.manage.entity.UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @ClassName JwtUserDetails
 * @Description user details
 * @Author alex
 * @Date 2018/11/2
 **/
public class JwtUserDetails implements UserDetails {

    private UserInfo userInfo;

    public JwtUserDetails(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userInfo.getUserInfoRoles() == null) {
            return null;
        }
        return Arrays.stream(userInfo.getUserInfoRoles().split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public UserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public String getPassword() {
        return userInfo.getUserInfoPass();
    }

    @Override
    public String getUsername() {
        return userInfo.getUserInfoName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userInfo.getUserInfoIsLocked() == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
