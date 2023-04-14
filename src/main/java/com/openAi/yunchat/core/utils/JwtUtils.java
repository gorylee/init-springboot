package com.openAi.yunchat.core.utils;


import com.openAi.yunchat.core.enums.ResultEnum;
import com.openAi.yunchat.core.exception.ResultException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * Token 生成和解析工具
 */
@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private static final JwtUtils jwtUtils = new JwtUtils();

    @Value("${jwt.config.key}")
    private String key;
    @Value("${jwt.config.expired-time}")
    private long expiredTime;

    @PostConstruct
    public void initialize() {
        jwtUtils.key = key;
        jwtUtils.expiredTime = expiredTime;
    }

    /**
     * 生成token(包含用户id，用户登陆名，用户名)
     */
    public static String createToken(Long id,String username,String name){
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .setId(id.toString())
                .setIssuedAt(now)
                .claim("username", username)
                .claim("name", name)
                .signWith(SignatureAlgorithm.HS256, jwtUtils.key);
        //如果设置了过期时间
        if(jwtUtils.expiredTime>0){
            builder.setExpiration(new Date(now.getTime()+jwtUtils.expiredTime));
        }
        return builder.compact();
    }

    /**
     * 解析token
     * @param token
     * @return void
     */
    public static Claims parseToken(String token){
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtUtils.key)
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        }catch (Exception e){
            logger.warn("token转换异常"+token);
            throw new ResultException(ResultEnum.FORBIDDEN.getKey(),"请重新登录");
        }
    }
    
    /**
     * @Author WeiC
     * @Description //TODO 判断token是否过期
     * @Date 13:31 2021/8/5
     * @Param [token]
     * @return boolean
     **/
    public static boolean isExpired(String token){
        try{
            Date expireDate = getExpireDate(token);
            if(new Date().before(expireDate)){
                return false;
            }else {
                return true;
            }
        }catch (ResultException r){
            throw r;
        }catch (Exception e){
            return true;
        }
    }

    /**
     * @Author WeiC
     * @Description //TODO 获取token过期时间
     * @Date 13:35 2021/8/5
     * @Param [token]
     * @return io.jsonwebtoken.Claims
     **/
    public static Date getExpireDate(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

}
