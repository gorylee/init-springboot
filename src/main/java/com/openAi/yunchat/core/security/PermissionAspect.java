package com.openAi.yunchat.core.security;

import cn.hutool.core.util.StrUtil;
import com.openAi.yunchat.core.annotation.Permission;
import com.openAi.yunchat.core.base.JsonResult;
import com.openAi.yunchat.core.utils.WebUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;


/**
 * 权限拦截
 */
//@Component
@Aspect
@Order(10)
public class PermissionAspect {
    @Pointcut("@annotation(com.openAi.yunchat.core.annotation.Permission)")
    public void permissionRequest(){};

    @Around("permissionRequest()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature)point.getSignature();
        Permission permission = methodSignature.getMethod().getAnnotation(Permission.class);
        String permissionStr = permission.value();
        if(StrUtil.isNotBlank(permissionStr)){
            if(WebUtils.hasPermission(permissionStr)){
                return point.proceed();
            }else {
                return JsonResult.fail("您没有权限");
            }
        }
        return point.proceed();
    }
}

