package com.dongs.dongsojservice.aop;


import com.dongs.dongsojservice.annotation.AuthCheck;
import com.dongs.dongsojservice.common.ErrorCode;
import com.dongs.dongsojservice.exception.BusinessException;
import com.dongs.dongsojservice.model.enums.UserRoleEnum;
import com.dongs.dongsojservice.model.pojo.User;
import com.dongs.dongsojservice.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验
 *
 * @author dongs
 */

@Aspect
@Component
@Slf4j
public class AuthInterceptor {

    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint point, AuthCheck authCheck) throws Throwable{
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);
        if(StringUtils.isNotBlank(mustRole)){
            UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
            if (userRoleEnum == null){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            String userRole = loginUser.getUserRole();
            if (UserRoleEnum.ROLE_BAN.equals(userRoleEnum)){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }

            if (UserRoleEnum.ROLE_ADMIN.equals(userRoleEnum)){
                if (!mustRole.equals(userRole)){
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
            }
        }
        return point.proceed();
    }
}
