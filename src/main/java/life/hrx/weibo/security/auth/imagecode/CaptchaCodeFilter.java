package life.hrx.weibo.security.auth.imagecode;


import life.hrx.weibo.security.auth.authenticationhandler.MyAuthenticationFailureHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 *该类是一个自定义的验证码过滤器类，继承自OncePerRequestFilter表示只过滤一次，该过滤类会添加到验证登录身份过滤类之前，即首先进行验证码验证
 */
@Component
public class CaptchaCodeFilter extends OncePerRequestFilter {

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (StringUtils.equals("/login",request.getRequestURI()) && StringUtils.equalsIgnoreCase("post",request.getMethod()) ) {
            try {
                //验证谜底与用户输入是否匹配，注意这里要捕捉异常，如果捕获异常，应该传递给登录失败链做处理
                validate(request);
            }catch (AuthenticationException e){
                myAuthenticationFailureHandler.onAuthenticationFailure(request,response,e);
                return; //注意这里必须要return,因为当过滤失败后，应该就直接返回，而不是应该继续过滤
            }

        }
        filterChain.doFilter(request,response);//验证完毕，放行通过，继续验证下一个过滤器，即登录密码验证
    }

    private void validate(HttpServletRequest request) {
        String captchaCode = request.getParameter("captchaCode");//从request中获得json数据中的captchaCode值，即用户填写的验证码
        if (StringUtils.isEmpty(captchaCode)){
            //抛出这个异常是有原因的，首先这个异常可以传入值，作为异常的message,另外这个异常继承自AuthenticationException。所以可以被myAuthenticationFailureHandler中的异常捕获到
            throw new SessionAuthenticationException("验证码不能为空");
        }
        CaptchaImageCode captchaImageCode = (CaptchaImageCode) request.getSession().getAttribute("captcha_key");//获得我们存储到session中的谜底答案
        if (Objects.isNull(captchaImageCode)){//如果验证码不存在
            throw new SessionAuthenticationException("验证码不存在");
        }

        //校验服务器session池中的验证码是否过期，如果过期就将其删除
        if (captchaImageCode.isExpired()){
            request.getSession().removeAttribute("captcha_key");
            throw new SessionAuthenticationException("验证码已经过期");
        }

        //请求验证码校验
        if (!StringUtils.equals(captchaCode,captchaImageCode.getCode())){
            throw new SessionAuthenticationException("验证码不匹配");
        }

        //删除已经校验完成的验证码
        request.getSession().removeAttribute("captcha_key");

    }
}
