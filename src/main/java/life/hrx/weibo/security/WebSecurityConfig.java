package life.hrx.weibo.security;


import life.hrx.weibo.auth.myuserdetails.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig  extends WebSecurityConfigurerAdapter {


    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private DataSource dataSource;

    /**
     * web访问权限主要配置方法
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .logout()//开启logout功能，不需要编写controller,会去做清除session和删除remember-me数据库上的和游览器上的存储都会被删除
            .logoutUrl("/logout") //默认就为这个链接，不定义也行
            .deleteCookies("JSESSIONID")//自定义还需要删除的cookie名称

        .and()
            .csrf().disable().formLogin()
            .loginPage("/login")
            .usernameParameter("username")
            .passwordParameter("password")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/")
            .failureUrl("/login")

        .and()
            .authorizeRequests()
            .antMatchers("/login","/","/register","/question/**","/error").permitAll()
            .antMatchers(HttpMethod.GET,"/comment/**").permitAll()
            .anyRequest().authenticated()//任何请求都需要被加上authenticated权限认证请求头
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .invalidSessionUrl("/login") //设置当session超时后，就自动跳转到登录页面
        .and()
            .rememberMe()//开启rememberMe()功能
            .rememberMeParameter("remember-me") //该配置用来设置from表单中的勾选框名称，默认就为remember-me
            .rememberMeCookieName("remember-me")//该配置用来设置remember-me的cookie名称，默认就为remember-me
            .tokenValiditySeconds(2*24*60*60) //该配置用来设置token的有效期，即多长时间内可以自动登录，单位是秒，不修改配置情况下默认是2周
            .tokenRepository(persistentTokenRepository());//设置token持久化到数据库中


//
//        //在我们添加了security依赖后，所有的地址都会被spring security所控制，现在我们只是需要密码的加密解密的验证，所以我们要添加一个配置类，配置为所有地址都可以匿名访问
//
//        http.authorizeRequests().antMatchers("/**","/js/**").permitAll().anyRequest().authenticated().and().csrf().disable();//上面指定路径"/**"表示所有页面都可以访问，所有的请求都是允许的
//
//
//        //spring security会接管注销服务，我想自定义但是没有用，只好在这边操作，
//        http.logout().logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies("token");


    }

    /**
     * 该配置获取用户登录信息
     * @param auth
     * @throws Exception
     */

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    /**
     * 该配置用来获取用户可以访问的静态资源
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**","/fonts/**","/images/**","/js/**");
    }

    /**
     * 将spring security的密码加密类作为bean注入，可以自由的在任何地方注入使用。可以用作加密和密码验证
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();//将这个类加入到Bean中，便可以自由使用
    }

    /**
     * 将记住我的token存储到数据库中，保证每次服务器重启不会使其记住我功能失效。token的添加和删除已经由这个类自动管理
     * @return
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository=new JdbcTokenRepositoryImpl(); //该类中默认设置了要存储的token的表名为persistent_logins，其中的token表字段也不能随意改变
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }


}
