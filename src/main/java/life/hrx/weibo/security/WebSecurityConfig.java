package life.hrx.weibo.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig  extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .logout()//开启logout功能，不需要编写controller,会去做清除session和删除remember-me数据库上的和游览器上的存储都会被删除
            .logoutUrl("/logout") //默认就为这个链接，不定义也行

        .and()
            .csrf().disable().formLogin()
            .loginPage("/login")
            .usernameParameter("name")
            .passwordParameter("password")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/")
            .failureUrl("/login")

        .and()
            .authorizeRequests()
            .antMatchers("/login","/","/register","/question/**").permitAll()
            .anyRequest().authenticated()//任何请求都需要被加上authenticated权限认证请求头
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .invalidSessionUrl("/login"); //设置当session超时后，就自动跳转到登录页面










//
//        //在我们添加了security依赖后，所有的地址都会被spring security所控制，现在我们只是需要密码的加密解密的验证，所以我们要添加一个配置类，配置为所有地址都可以匿名访问
//
//        http.authorizeRequests().antMatchers("/**","/js/**").permitAll().anyRequest().authenticated().and().csrf().disable();//上面指定路径"/**"表示所有页面都可以访问，所有的请求都是允许的
//
//
//        //spring security会接管注销服务，我想自定义但是没有用，只好在这边操作，
//        http.logout().logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies("token");


    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();//将这个类加入到Bean中，便可以自由使用
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**","/fonts/**","/images/**","/js/**");
    }
}
