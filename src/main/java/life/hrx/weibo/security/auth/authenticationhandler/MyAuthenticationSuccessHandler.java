package life.hrx.weibo.security.auth.authenticationhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import life.hrx.weibo.dto.ResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 该类为自定义用户登录成功后执行的操作
 * 继承自SavedRequestAwareAuthenticationSuccessHandler，通常来说我们可以实现接口 AuthenticationSuccessHandler，
 * 但是SavedRequestAwareAuthenticationSuccessHandler也同样实现了接口AuthenticationSuccessHandler，好处是该类已经实现记住用户上一次请求的资源路径
 */
@Component
public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Value("${spring.security.logintype}")
    private String loginType;

    private static ObjectMapper objectMapper=new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        if (loginType.equalsIgnoreCase("JSON")){
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(ResultDTO.okOf("/")));
        }else{
            //登录成功后就直接跳转到之前的页面，注意，想返回json格式的信息也是可行的，但是这里就先只返回text/html的信息
            response.setContentType("text/html;charset=UTF-8");
            super.onAuthenticationSuccess(request,response,authentication);
        }

    }

}
