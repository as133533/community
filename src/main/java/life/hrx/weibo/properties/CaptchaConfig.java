package life.hrx.weibo.properties;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

/**
 * kcaptcha验证码类库的配置类
 */

//标识这是一个配置类，需要说明的是@Configuration是单例的，也就是说其中的Bean对象每次生成的时候会被记录在上下文中，每次调用是调用同一个对象，一般都会用在@Value注解的成员变量和@Bean注解的方法。而@component是多例的，每次生成的是不同的Bean对象
@Configuration
@PropertySource(value = {"classpath:application.properties"}) //我们也可以将application.properties中的相关配置独立到一个properties中，然后修改这里的路径
public class CaptchaConfig {
    @Value("${kaptcha.border}")
    private String border;
    @Value("${kaptcha.border.color}")
    private String borderColor;
    @Value("${kaptcha.textproducer.font.color}")
    private String fontColor;
    @Value("${kaptcha.image.width}")
    private String imageWidth;
    @Value("${kaptcha.image.height}")
    private String imageHeight;
    @Value("${kaptcha.session.key}")
    private String sessionKey;
    @Value("${kaptcha.textproducer.char.length}")
    private String charLength;
    @Value("${kaptcha.textproducer.font.names}")
    private String fontNames;
    @Value("${kaptcha.textproducer.font.size}")
    private String fontSize;
    @Value("${kaptcha.session.date}")
    private String date;

    @Bean
    public DefaultKaptcha captchaProducer(){
        /**
         * 该类DefaultKaptcha的是一个工具类，并且是一个单例模式，需要在其他地方使用同名变量去获得该Bean的实例
         */
        DefaultKaptcha defaultKaptcha=new DefaultKaptcha();

        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", border);
        properties.setProperty("kaptcha.border.color", borderColor);
        properties.setProperty("kaptcha.textproducer.font.color", fontColor);
        properties.setProperty("kaptcha.image.width", imageWidth);
        properties.setProperty("kaptcha.image.height", imageHeight);
        properties.setProperty("kaptcha.session.key", sessionKey);
        properties.setProperty("kaptcha.textproducer.char.length", charLength);
        properties.setProperty("kaptcha.textproducer.font.names", fontNames);
        properties.setProperty("kaptcha.textproducer.font.size",fontSize);
        properties.setProperty("kaptcha.session.date",date);
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

}
