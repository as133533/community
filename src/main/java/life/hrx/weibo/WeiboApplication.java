package life.hrx.weibo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@MapperScan(basePackages = "life.hrx.weibo.mapper") //设置mapper的所在路径
@EnableAsync //为Spring boot 开启异步功能
@EnableScheduling //开启spring boot定时器的功能
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60*10 )//将session存储托管到redis中,如果session我们没有指定过期时间，那么就会默认保存10分钟。登陆验证码的session我们自定义了2分钟，所以这里只引用在登录用户上
public class WeiboApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeiboApplication.class, args);
    }

}

