package com.dzhy.manage.security.entity;

import com.dzhy.manage.entity.UserInfo;
import lombok.Data;

/**
 * @ClassName TokenResponse
 * @Description token response
 * @Author alex
 * @Date 2018/11/2
 **/
@Data
public class TokenResponse {
    private String token;

    private UserInfo userInfo;

    public TokenResponse(String token, UserInfo userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }
}
