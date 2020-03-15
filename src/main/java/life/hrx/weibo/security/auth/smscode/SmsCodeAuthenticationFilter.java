package life.hrx.weibo.security.auth.smscode;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 该类作为短信验证码登录的一个入口类，类似UsernamePasswordAuthenticationFilter。因为需要给spring security提供另外一个登录入口
 * 这一步主要过滤一些非法的请求，并将用户信息存储到SmsCodeAuthenticationToken中，下一步会由spring security调用AuthenticationManager来认证权限。参考第43行
 */

public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String SPRING_SECURITY_FORM_MOBILE_KEY = "phone";
    private String phoneParameter = SPRING_SECURITY_FORM_MOBILE_KEY ;    //请求中携带手机号的参数名称
    private boolean postOnly = true;    //指定当前过滤器是否只处理POST请求

    public SmsCodeAuthenticationFilter() {
        //指定当前过滤器处理的请求
        super(new AntPathRequestMatcher("/smslogin", "POST"));
    }

    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response)
            throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String mobile = this.obtainMobile(request);
            if (mobile == null) {
                mobile = "";
            }
            mobile = mobile.trim();
            SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(mobile);//将收集号码存入token中
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);//需要给一个委托类AuthenticationManager，这个委托类用来专门做
        }
    }


    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(this.phoneParameter);
    }


    protected void setDetails(HttpServletRequest request, SmsCodeAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setMobileParameter(String mobileParameter) {
        Assert.hasText(mobileParameter, "phone parameter must not be empty or null");
        this.phoneParameter = mobileParameter;
    }


    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getMobileParameter() {
        return this.phoneParameter;
    }


}

