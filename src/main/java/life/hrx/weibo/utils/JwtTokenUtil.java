package life.hrx.weibo.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import life.hrx.weibo.model.User;
import life.hrx.weibo.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 这是一个spring boot加载application.properties的工具类
 * 用于,JWT令牌生成、刷新,验证的工具类
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "jwt",ignoreInvalidFields = false)//该注解快速从配置中引入字段，我们的对象属性名应该和prefix相组合 即prefix+属性名=配置中的完整参数名,ignoreInvalidFields如果我们的配置有误
@Component
public class JwtTokenUtil {
    private String secret;
    private Long expiration;
    private String header; //这个header不需要在生成token的时候设置，因为我们通过这个header 来和前端传的参数进行比对，也就是说前端开发要知道我们要将token数据放到我们后端指定的header名称中，先前端再后端

    @Autowired
    private UserService userService;
    /**
     * 生成token令牌
     * @param user
     * @return token令牌
     */
    public String generateToken(User user){
        Map<String,Object> claims =new HashMap<>();
        claims.put("sub",user.getId());//签名中的内容
        claims.put("iat",System.currentTimeMillis());//签发时间
        return generateToken(claims);
    }

    /**
     * 从令牌中获取用户id
     * @param token 令牌
     * @return 用户名
     */
    public String getUserIdFromToken(String token){
        String userId;
        try {
            Claims claims=getClaimsFromToken(token);
            userId=claims.getSubject();
        }catch (Exception e){
            userId=null;
        }
        return userId;
    }

    /**
     * 判断令牌是否过期
     * @param token 令牌
     * @return 是否过期 ,true 表示过期，false表示没有过期
     */

    public Boolean isTokenExpired(String token){
        try {
            Claims claims=getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            log.info(String.valueOf(expiration));
            return expiration.before(new Date());
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 刷新令牌
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token){
        String refreshedToken;
        try {
            Claims claims=getClaimsFromToken(token);
            claims.put("ita",System.currentTimeMillis());
            refreshedToken=generateToken(claims);
        }catch (Exception e){
            refreshedToken=null;
        }
        return refreshedToken;
    }

    /**
     * 每个用户重置完密码，就应该将token设置为过期
     * @param token 已经重置过密码的token
     */
    public void removeToken(String token){

        Claims claims=getClaimsFromToken(token);
        claims.setExpiration(new Date(System.currentTimeMillis()+2000));
        log.info(String.valueOf(claims.getExpiration()));

    }

    /**
     * 验证令牌
     * @param token 令牌
     * @return 是否有效 true为有效 ，false为无效
     */
    public Boolean validateToken(String token){
        String userId=getUserIdFromToken(token);
        return (userId!=null && userService.findById(Long.valueOf(userId)) && !isTokenExpired(token));
    }

    /**
     * 从claims生成令牌
     * @param claims 数据声明
     * @return 令牌
     */
    private String generateToken(Map<String,Object> claims){

        return Jwts.builder().setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();
    }

    /**
     * 从令牌中获取数据声明
     * @param token 令牌
     * @return 数据声明
     */
    private Claims getClaimsFromToken(String token){
        Claims claims;
        try {
            claims=Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        }catch (Exception e){
            claims=null;
        }
        return claims;
    }


}
