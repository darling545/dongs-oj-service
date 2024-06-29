package com.dongs.dongsojservice.aop;



import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 登录请求日志拦截
 */
@Aspect
@Component
@Slf4j
public class LoginInterceptor {

    /**
     * 请求执行拦截
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.dongs.dongsojservice.controller.*.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable{
        // 计时开始
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        // 获取请求路径
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 生成唯一的请求ID
        String requestId = UUID.randomUUID().toString();
        String url = httpServletRequest.getRequestURI();
        // 获取请求参数
        Object[] args = point.getArgs();
        String requestParam = "[" + StringUtils.join(args,",") + "]";
        // 输出请求日志
        log.info("request start, id: {},path: {},ip: {},param: {}",requestId,url,
                httpServletRequest.getRemoteHost(),requestParam);
        // 执行原方法
        Object result = point.proceed();
        // 输出响应日志
        stopwatch.stop();
        long totalTimeMillis = stopwatch.getTotalTimeMillis();
        log.info("request end,id: {},cost: {}",requestId,totalTimeMillis);
        return result;
    }
}
