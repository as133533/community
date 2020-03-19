package life.hrx.weibo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

//@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class WeiboApplicationTests {

    @Test
    public void contextLoads() throws IOException {
        String filePath=new ClassPathResource("application.properties").getFile().getAbsolutePath();//获得完整路径
        log.info(filePath);
    }

}
