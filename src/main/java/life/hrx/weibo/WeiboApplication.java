package life.hrx.weibo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "life.hrx.weibo.mapper") //设置mapper的所在路径
public class WeiboApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeiboApplication.class, args);
    }

}

