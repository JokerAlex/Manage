package com.dzhy.manage.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @ClassName WebLogAspect
 * @Description web 请求日志记录
 * @Author alex
 * @Date 2018/11/5
 **/
@Component
@Aspect
@Slf4j
public class WebLogAspect {

    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(public * com.dzhy.manage.controller..*(..))")
    public void webLog() {}


    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();

        // 记录下请求内容
        log.info("URL : {} HTTP_METHOD : {} Last-Skip-IP : {} CLASS_METHOD : {} ARGS : {}",
                request.getRequestURL().toString(),
                request.getMethod(),
                request.getHeader("X-Real-IP"),
                joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
        startTime.set(System.currentTimeMillis());

    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) {
        // 处理完请求，返回内容
        log.info("RESPONSE : {} SPEND TIME : {}", ret, (System.currentTimeMillis() - startTime.get()));
        startTime.remove();
    }
}
