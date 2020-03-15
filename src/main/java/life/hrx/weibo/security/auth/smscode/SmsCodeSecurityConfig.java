package life.hrx.weibo.security.auth.smscode;

import life.hrx.weibo.security.auth.authenticationhandler.MyAuthenticationFailureHandler;
import life.hrx.weibo.security.auth.authenticationhandler.MyAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * 该配置类类似于WebSecurityConfig,主要是因为短信登录的配置有点多，所以就直接另起一个配置文件,SecurityConfigurerAdapter是一个配置类的基类，我们想自己自定义配置可以继承这个类,最终这个类还是要写到SecurityConfig中进行配置
 */

@Component
public class SmsCodeSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private SmsCodeValidateFilter smsCodeValidateFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        SmsCodeAuthenticationFilter smsCodeAuthenticationFilter = new SmsCodeAuthenticationFilter();
        smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));//给该连指定一个AuthenticationManager，这个东西就是一个用户名和密码封装成对象委托类

        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler);
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(myAuthenticationFailureHandler);

        // 获取验证码提供者
        SmsCodeAuthenticationProvider smsCodeAuthenticationProvider = new SmsCodeAuthenticationProvider();
        smsCodeAuthenticationProvider.setUserDetailsService(userDetailsService);

        //在用户密码过滤器前面加入短信验证码校验过滤器
        http.addFilterBefore(smsCodeValidateFilter, UsernamePasswordAuthenticationFilter.class);
        //在用户密码过滤器后面加入短信验证码认证授权过滤器
        http.authenticationProvider(smsCodeAuthenticationProvider)
                .addFilterAfter(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); //至此如果传进来的token是电话号码和短信验证码的，就会走这一条过滤器

    }
}
