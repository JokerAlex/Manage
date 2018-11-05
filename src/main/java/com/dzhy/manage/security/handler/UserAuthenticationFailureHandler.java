package com.dzhy.manage.security.handler;

import com.dzhy.manage.dto.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName UserAuthenticationFailureHandler
 * @Description failure handler
 * @Author alex
 * @Date 2018/11/2
 **/
@Component
@Slf4j
public class UserAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    @Autowired
    public UserAuthenticationFailureHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException e) throws IOException, ServletException {
        log.info("【登录失败】", e.getMessage());
        response.setHeader("Content-type", "application/json;charset=utf-8");
        objectMapper.writeValue(response.getWriter(), ResponseDTO.isError("用户名或密码错误"));
    }
}
