package life.hrx.weibo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = "life.hrx.weibo.mapper") //设置mapper的所在路径
@EnableAsync //为Spring boot 开启异步功能
@EnableScheduling //开启spring boot定时器的功能
public class WeiboApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeiboApplication.class, args);
    }

}

