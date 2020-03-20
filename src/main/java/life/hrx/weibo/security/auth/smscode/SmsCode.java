package life.hrx.weibo.security.auth.smscode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 前端传过来的数据存储对象
 */

public class SmsCode implements Serializable {
    private String code; //短信验证码

    private LocalDateTime expireTime; //表示过期时间

    private String phone;  //用来设置手机号

    /**
     * 构造方法
     * @param code 该参数用来设置验证码谜底
     * @param expireAfterSeconds 该参数用来设置有效时间
     * @param phone 用来设置手机号
     */
    public SmsCode(String code,int expireAfterSeconds,String phone){
        this.code=code;
        this.expireTime=LocalDateTime.now().plusSeconds(expireAfterSeconds); //当前时间加上传进来的时间
        this.phone=phone;
    }


    /**
     * 用来判断验证码是否失效
     * @return true为失效 false为有效
     */
    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expireTime); //当前时间是否超过过期时间
    }

    public String getCode() {
        return code;
    }
    public String getMobile(){
        return phone;
    }
}
