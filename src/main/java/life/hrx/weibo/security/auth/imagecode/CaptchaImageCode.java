package life.hrx.weibo.security.auth.imagecode;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 该类封装一个用于存储在session中的验证码谜底对象
 */
public class CaptchaImageCode implements Serializable {
    private static final long serialVersionUID=-8985545025228238754L;

    private String code; //表示验证码谜底
    private LocalDateTime expireTime;//表示过期时间

    /**
     * 构造方法
     * @param code 用来设置谜底
     * @param expireAfterSeconds 用来设置有效时间
     */
    public CaptchaImageCode(String code,int expireAfterSeconds){
        this.code=code;
        this.expireTime=LocalDateTime.now().plusSeconds(expireAfterSeconds); //当前时间加上传进来的时间
    }

    /**
     * 用来判断验证码是否失效
     * @return true为失效，false为有效
     */
    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expireTime); //当前时间是否超过过期时间
    }

    public String getCode(){
        return code;
    }
}
