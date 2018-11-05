package com.dzhy.manage.security.filter;

import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName UserAuthenticationTokenFilter
 * @Description AuthenticationTokenFilter
 * @Author alex
 * @Date 2018/11/2
 **/
@Component
@Slf4j
public class UserAuthenticationTokenFilter extends OncePerRequestFilter {


    private final UserDetailsService iUserDetailsService;

    @Autowired
    public UserAuthenticationTokenFilter(UserDetailsService iUserDetailsService) {
        this.iUserDetailsService = iUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = "Authorization";
        String header = request.getHeader(tokenHeader);

        String tokenHead = "Bearer ";
        if (header != null && header.startsWith(tokenHead)) {
            String token = header.substring(tokenHead.length());
            Claims claims;
            UserInfo userInfo = new UserInfo();
            try {
                claims = JwtUtil.parseJwt(token);
                userInfo.setUserInfoId(claims.get("userId", Integer.class));
                userInfo.setUserInfoName(claims.get("userName", String.class));
                userInfo.setUserInfoTrueName(claims.get("userTrueName", String.class));
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            if (userInfo.getUserInfoId() != null
                    && userInfo.getUserInfoName() != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = iUserDetailsService.loadUserByUsername(userInfo.getUserInfoName());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("authenticated user = {}, setting security context", userDetails.getUsername());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
