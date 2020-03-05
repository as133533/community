package life.hrx.weibo.interceptor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//用于注册拦截器的类，相当于拦截器的注册中心

//@EnableWebMvc 如果是在spring boot项目中，不要使用这个注解
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired(required = false)
    private SessionInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //向拦截器注册中心中注册拦截器
        registry.addInterceptor(sessionInterceptor).addPathPatterns("/**");
    }
}
