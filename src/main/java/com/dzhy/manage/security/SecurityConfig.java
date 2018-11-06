package com.dzhy.manage.security;

import com.dzhy.manage.security.entrypoint.UserAuthenticationEntryPoint;
import com.dzhy.manage.security.filter.UserAuthenticationTokenFilter;
import com.dzhy.manage.security.handler.UserAccessDeniedHandler;
import com.dzhy.manage.security.handler.UserAuthenticationFailureHandler;
import com.dzhy.manage.security.handler.UserAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * @ClassName SecurityConfig
 * @Description SecurityConfig
 * @Author alex
 * @Date 2018/11/2
 **/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService iUserDetailsService;
    private final UserAuthenticationSuccessHandler authenticationSuccessHandler;
    private final UserAuthenticationFailureHandler authenticationFailureHandler;
    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    private final UserAuthenticationTokenFilter userAuthenticationTokenFilter;
    private final UserAccessDeniedHandler userAccessDeniedHandler;

    public SecurityConfig(UserDetailsService iUserDetailsService,
                          UserAuthenticationSuccessHandler authenticationSuccessHandler,
                          UserAuthenticationFailureHandler authenticationFailureHandler,
                          UserAuthenticationEntryPoint userAuthenticationEntryPoint,
                          UserAuthenticationTokenFilter userAuthenticationTokenFilter,
                          UserAccessDeniedHandler userAccessDeniedHandler) {
        this.iUserDetailsService = iUserDetailsService;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.userAuthenticationEntryPoint = userAuthenticationEntryPoint;
        this.userAuthenticationTokenFilter = userAuthenticationTokenFilter;
        this.userAccessDeniedHandler = userAccessDeniedHandler;
    }

    /**
     * 装载BCrypt密码编码器
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(iUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    private UrlBasedCorsConfigurationSource getUrlBasedCorsConfigurationSource() {
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 由于使用的是JWT，我们这里不需要csrf
                .csrf().disable()
                .cors()
                .configurationSource(getUrlBasedCorsConfigurationSource())
                .and()

                .exceptionHandling()
                .authenticationEntryPoint(userAuthenticationEntryPoint)
                .accessDeniedHandler(userAccessDeniedHandler)
                .and()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 添加JWT filter
                .addFilterBefore(userAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                //开放链接
                .antMatchers("/login").permitAll()
                // swagger start 页面访问403错误
                .antMatchers("/swagger-ui.html",
                        "/swagger-resources/**",
                        "/images/**",
                        "/webjars/**",
                        "/v2/api-docs",
                        "/configuration/ui",
                        "/configuration/security").permitAll()
                // swagger end
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler);

        // 禁用缓存
        http.headers().cacheControl();
    }

}
