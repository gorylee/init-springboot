package com.openAi.yunchat.core.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.openAi.yunchat.core.enums.ResultEnum;
import com.openAi.yunchat.core.exception.ResultException;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class WebUtils {

    private static final Logger logger = LoggerFactory.getLogger(WebUtils.class);

    public static final String USER_RESOURCE_PRE = "USER_RESOURCE_";//用户资源前缀

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String SEPARATOR = ",";

//    private static final WebUtils webUtils = new WebUtils();


    /**
     * 获取请求对象
     */
    public static HttpServletRequest getRequest(){
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }


    /**
     * 获取web根目录
     *
     * @return web根目录
     */
    public static String getRootPath() {
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath()).getParentFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (path == null || !path.exists()) {
            path = new File("");
        }
        return path.getAbsolutePath();
    }


    /**
     * 获取请求头中的token
     */
    public static String getToken(){
        String token = "";
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(Objects.isNull(requestAttributes)) return token;
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        //先从请求头获取
        token = request.getHeader("token");
        if(StrUtil.isEmpty(token)){
            //从cookies中获取
            Cookie[] cookies = request.getCookies();
            if(cookies!=null&&cookies.length>0){
                int length = cookies.length;
                for (int i = 0; i < length; i++) {
                    Cookie cookie = cookies[i];
                    if("token".equalsIgnoreCase(cookie.getName())){
                        token = cookie.getValue();
                        return token;
                    }
                }
            }
            //从url中获取
            if(StrUtil.isEmpty(token)){
                token = request.getParameter("token");
                if(StrUtil.isNotEmpty(token)) return token;
            }
//            throw new ResultException(ResultEnum.NO_LOGIN.getKey(),"请先登录");
        }
        return token;
    }

    /**
     * 获取用户名
     */
    public static String getName(){
        String token = WebUtils.getToken();
        if(StrUtil.isBlank(token)){
            return "";
        }
        Claims claims = JwtUtils.parseToken(token);
        Object name = claims.get("name");
        if(Objects.isNull(name)) return "";
        return (String)name;
    }

    /**
     * 获取用户名
     */
    public static String getUsername(){
        Claims claims = JwtUtils.parseToken(WebUtils.getToken());
        Object username = claims.get("username");
        if(Objects.isNull(username)) return "";
        return (String)username;
    }

    /**
     * 是否登录
     */
    public static boolean isNonLogin(){
        String token = WebUtils.getToken();
        if(StrUtil.isBlank(token)) return false;
        return JwtUtils.isExpired(token);
//        return JwtUtils.isExpired(token)&&!RedisUtils.hasKey(token);
    }

    /**
     * 获取当前登录用户信息 （如果token存储用户信息）
     */
    public static <T> T getCurrentUser(Class<T> clazz){
        Object object = RedisUtils.get(getToken());
        if(Objects.isNull(object)){
            throw new ResultException(ResultEnum.NO_LOGIN.getKey(),"请重新登录");
        }
        try{
            T t = clazz.cast(object);
            return t;
        }catch (Exception e){
            throw new ResultException(ResultEnum.NO_LOGIN.getKey(),"请重新登录");
        }
    }


    /**
     * 获取token中的id (可以存会员id)
     */
    public static Long getId(){
        String token = WebUtils.getToken();
        if(StrUtil.isBlank(token)){
            return 0L;
        }
        Claims claims = JwtUtils.parseToken(token);
        return Long.valueOf(claims.get("id").toString()) ;
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (LOCALHOST.equals(ipAddress)) {
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        log.error(e.getMessage());
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***".length()
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(SEPARATOR) > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }

    /**
     * 链接编码
     *
     * @param url 链接
     * @return 编码后的链接
     */
    public static String urlEncode(String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(ResultEnum.SYSTEM_ERROR.getValue(),e);
        }
        return "";
    }

    /**
     * 获取请求路径
     *
     * @return 请求路径
     */
    public static String getRequestUrl() {
        HttpServletRequest request = getRequest();
        return request.getRequestURL().toString();
    }

    /**
     * 判断用户是否有操作权限
     *
     * 备注： 这里待优化，应该拿角色去判断，提高效率，减少内存存储空间的占用
     **/
    public static boolean hasPermission(String permission){
        if(StrUtil.isBlank(permission)) return false;
//        permission ="["+permission.replaceAll("\\|\\|", "]#[")+"]";
        permission =permission.replaceAll("\\|\\|", "#");
        String key = USER_RESOURCE_PRE+WebUtils.getId();
        String[] permissions = permission.split("#");
        List<Object> cacheList = RedisUtils.getList(key);
        if(CollUtil.isEmpty(cacheList)) return false;
        JSONArray arrList =(JSONArray)cacheList.get(0);
        List<String> cacheStrs = arrList.toJavaList(String.class);
        for (String s : permissions) {
            for (String o : cacheStrs) {
                if(o.equals(s.trim())){
                    return true;
                }
            }
        }
        return false;
//        return webUtils.resourceService.hasPermission(permission);
    }

}
