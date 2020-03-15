package life.hrx.weibo.security.auth.authenticationhandler;


import com.fasterxml.jackson.databind.ObjectMapper;
import life.hrx.weibo.dto.ResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 这个类用来定义登录认证失败后的处理,继承自SimpleUrlAuthenticationFailureHandler,该类默认定向到登录页
 */

@Component
public class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${spring.security.logintype}")
    private String loginType;

    private static ObjectMapper objectMapper=new ObjectMapper();
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMsg="用户名或者密码输入错误";
        if (exception instanceof SessionAuthenticationException){
            errorMsg=exception.getMessage();
        }
        if (loginType.equalsIgnoreCase("JSON")){ //下面是发送json格式的响应
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(ResultDTO.errorOf(5000,errorMsg)));
        }else{//下面是发送html的响应
            response.setContentType("text/html;charset=UTF-8");
            super.onAuthenticationFailure(request, response, exception);
        }

    }
}
