package life.hrx.weibo.security;
import life.hrx.weibo.security.auth.authenticationhandler.MyAuthenticationFailureHandler;
import life.hrx.weibo.security.auth.authenticationhandler.MyAuthenticationSuccessHandler;
import life.hrx.weibo.security.auth.imagecode.CaptchaCodeFilter;
import life.hrx.weibo.security.auth.myuserdetails.MyUserDetailsService;
import life.hrx.weibo.security.auth.smscode.SmsCodeSecurityConfig;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig  extends WebSecurityConfigurerAdapter {


    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private CaptchaCodeFilter captchaCodeFilter;

    @Autowired
    private SmsCodeSecurityConfig smsCodeSecurityConfig;

    @Value("${web.domain}")
    private String webDomain;



    /**
     * web访问权限主要配置方法
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.headers().frameOptions().disable() //spring security不仅开启了cors防护，还默认使响应头中X-Frame-Options的值设置为DENY，原本以为图片无法上传是cors未开放，原来是header种默认设置为了DENY
        .and()
            .cors()//允许spring boot开始配置cors 跨域访问

        .and()
            .addFilterBefore(captchaCodeFilter, UsernamePasswordAuthenticationFilter.class)//将验证码过滤器放到用户密码验证过滤器之前
            .logout()//开启logout功能，不需要编写controller,会去做清除session和删除remember-me数据库上的和游览器上的存储都会被删除
            .logoutUrl("/logout") //默认就为这个链接，不定义也行
            .deleteCookies("JSESSIONID")//自定义还需要删除的cookie名称

        .and()
            .csrf().disable().formLogin()
            .loginPage("/login")
            .usernameParameter("username") //如果这个配置不去定义，默认就是username， 下同
            .passwordParameter("password")
            .loginProcessingUrl("/login")
            //.defaultSuccessUrl("/") //不要再使用默认的登录成功和登录失败策略，否则自定义的策略会不生效
//            .failureUrl("/login.fail")
            .successHandler(myAuthenticationSuccessHandler) //自定义登录成功后的策略
            .failureHandler(myAuthenticationFailureHandler) //自定义登录失败后的策略
        .and()
            .apply(smsCodeSecurityConfig) //将短信登录方式的配置加入到正式配置中

        .and()
            .authorizeRequests()
            .antMatchers("/login","/","/register","/question/**","/error","/kaptcha","/registercheck","/smslogin","/smscode","/resetemail","/resetpassword/**","/file/upload/**").permitAll()
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
        web.ignoring().antMatchers("/css/**","/fonts/**","/images/**","/js/**","/layui/**");
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

    /**
     * spring security特有的cors配置
     * @return
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        if (StringUtils.endsWith(webDomain,"/")){//配置参数中的结尾不能带/ ，如果带了/ 那么就删除
            webDomain=StringUtils.substring(webDomain,0,webDomain.length()-1);
        }
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(webDomain));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
