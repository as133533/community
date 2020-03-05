package life.hrx.weibo.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig  extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //在我们添加了security依赖后，所有的地址都会被spring security所控制，现在我们只是需要密码的加密解密的验证，所以我们要添加一个配置类，配置为所有地址都可以匿名访问

        http.authorizeRequests().antMatchers("/**","/js/**").permitAll().anyRequest().authenticated().and().csrf().disable();//上面指定路径"/**"表示所有页面都可以访问，所有的请求都是允许的


        //spring security会接管注销服务，我想自定义但是没有用，只好在这边操作，
        http.logout().logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies("token");


    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();//将这个类加入到Bean中，便可以自由使用
    }
}
