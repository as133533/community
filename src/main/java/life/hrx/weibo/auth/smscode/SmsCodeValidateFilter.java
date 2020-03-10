package life.hrx.weibo.auth.smscode;

import life.hrx.weibo.auth.authenticationhandler.MyAuthenticationFailureHandler;
import life.hrx.weibo.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 这个sms过滤器类,类似验证码过滤器类进行了一次短信验证码验证
 */

@Component
public class SmsCodeValidateFilter extends OncePerRequestFilter {//OncePerRequestFilter它能够确保在一次请求中只通过一次filter，而需要重复的执行

    @Autowired(required = false)
    private UserService userService;

    @Autowired
    MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().equals("/smslogin")
                && request.getMethod().equalsIgnoreCase("post")) {
            try {
                validate(request);

            }catch (AuthenticationException e){
                myAuthenticationFailureHandler.onAuthenticationFailure(
                        request,response,e);
                return;
            }
        }
        filterChain.doFilter(request,response);
    }

    private void validate(HttpServletRequest request) throws ServletRequestBindingException {
        SmsCode codeInSession = (SmsCode)request.getSession().getAttribute("sms_key");
        String codeInRequest = request.getParameter("smsCode");
        String mobileInRequest = request.getParameter("phone");


        if(StringUtils.isEmpty(mobileInRequest)){
            throw new SessionAuthenticationException("手机号码不能为空！");
        }
        if(StringUtils.isEmpty(codeInRequest)){
            throw new SessionAuthenticationException("短信验证码不能为空！");
        }
        if(Objects.isNull(codeInSession)){
            throw new SessionAuthenticationException("短信验证码不存在！");
        }
        if(codeInSession.isExpired()){
            request.getSession().removeAttribute("sms_key");
            throw new SessionAuthenticationException("短信验证码已过期！");
        }
        if(!codeInSession.getCode().equals(codeInRequest)){
            throw new SessionAuthenticationException("短信验证码不正确！");
        }

        if(!codeInSession.getMobile().equals(mobileInRequest)){
            throw new SessionAuthenticationException("短信发送目标与该手机号不一致！");//该验证防止从不同手机上拿到的验证码来验证，即保证当前登录的手机号码和短信发送的手机号码要一致
        }
        if(!userService.findByPhone(mobileInRequest)){
            throw new SessionAuthenticationException("您输入的手机号不是系统的注册用户");
        }

        request.getSession().removeAttribute("sms_key");

    }

}