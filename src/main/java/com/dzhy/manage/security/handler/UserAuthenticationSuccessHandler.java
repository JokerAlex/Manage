package com.dzhy.manage.security.handler;

import com.dzhy.manage.dto.ResponseDTO;
import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.security.entity.JwtUserDetails;
import com.dzhy.manage.security.entity.TokenResponse;
import com.dzhy.manage.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UserAuthenticationSuccessHandler
 * @Description Success Handler
 * @Author alex
 * @Date 2018/11/2
 **/
@Component
@Slf4j
public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Autowired
    public UserAuthenticationSuccessHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        UserInfo userInfo = jwtUserDetails.getUserInfo();
        userInfo.setUserInfoPass(null);

        Map<String, Object> claimsMap = new HashMap<>(3);
        claimsMap.put("userId", userInfo.getUserInfoId());
        claimsMap.put("userName", userInfo.getUserInfoName());
        claimsMap.put("userTrueName", userInfo.getUserInfoTrueName());

        String token = JwtUtil.createJwt(claimsMap);

        TokenResponse tokenResponse = new TokenResponse(token, userInfo);
        log.info("【登录成功】user = {}", userInfo.toString());
        response.setHeader("Content-type", "application/json;charset=utf-8");
        objectMapper.writeValue(response.getWriter(), ResponseDTO.isSuccess(tokenResponse));
    }
}
