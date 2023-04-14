package com.openAi.yunchat.core.security;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.openAi.yunchat.core.annotation.ValidateRepeat;
import com.openAi.yunchat.core.base.JsonResult;
import com.openAi.yunchat.core.utils.RedisUtils;
import com.openAi.yunchat.core.utils.WebUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

//@Component
@Aspect
@Order(20)
public class ValidateRepeatAspect {

    @Pointcut("@annotation(com.openAi.yunchat.core.annotation.ValidateRepeat)")
    public void validateRepeatRequest(){};

    @Around("validateRepeatRequest()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        //方案一：请求数据+用户id 同一时间内不能重复
        String methodType = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getMethod();
        if("POST".equalsIgnoreCase(methodType)){//拦截post
            Object[] args = point.getArgs();
            String argString = Objects.nonNull(args)&&args.length>0?JSON.toJSONString(point.getArgs()):"";
            MethodSignature methodSignature = (MethodSignature)point.getSignature();
            ValidateRepeat validateRepeat = methodSignature.getMethod().getAnnotation(ValidateRepeat.class);
            StringBuilder key;
            if(StrUtil.isNotEmpty(argString)){
                key = new StringBuilder(WebUtils.getId().toString()).append("_").append(methodSignature.getName())
                        .append(DigestUtils.md5DigestAsHex(argString.getBytes(StandardCharsets.UTF_8)));//id+url+表单数据
            }else {
                key = new StringBuilder(WebUtils.getId().toString()).append("_").append(methodSignature.getName());//id+url
            }
            if(RedisUtils.lock(key.toString(), validateRepeat.value())){
                return point.proceed();
            }else{
                return JsonResult.fail("请勿重复点击");
            }
        }else {
            return point.proceed();
        }

        //方案二：请求方法+用户id 同一时间内只运行一次
    }
}
