package com.openAi.yunchat.core.security;

import com.alibaba.fastjson.JSON;
import com.openAi.yunchat.core.base.JsonResult;
import com.openAi.yunchat.core.enums.ResultEnum;
import com.openAi.yunchat.core.utils.JwtUtils;
import com.openAi.yunchat.core.utils.WebUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 登陆拦截
 */
//@Component
public class LoginIntercepter extends HandlerInterceptorAdapter {

    @Value("${jwt.config.expired-time}")
    private long expiredTime;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(WebUtils.isNonLogin()){
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().print(JSON.toJSONString(JsonResult.fail(ResultEnum.NO_LOGIN)));
            return false;
        }
        //通知前端刷新token
        if(new Date(JwtUtils.getExpireDate(WebUtils.getToken()).getTime()-expiredTime/2).before(new Date())){
            response.setHeader("tokenStatus","-1");
        }
        return true;
    }
}
