package life.hrx.weibo.interceptor;


import life.hrx.weibo.mapper.UserMapper;
import life.hrx.weibo.model.User;
import life.hrx.weibo.model.UserExample;
import life.hrx.weibo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies !=null && cookies.length !=0)
        {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")){
                    String token = cookie.getValue();
                    UserExample userExample = new UserExample();
                    userExample.createCriteria().andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);
                    if (users.size() !=0){
                        request.getSession().setAttribute("user",users.get(0)); //如果该token与数据库中的匹配，那么就写登录状态到session中去。
                        Long unreadCount = notificationService.unreadcount(users.get(0).getId());
                        request.getSession().setAttribute("unreadCount",unreadCount);
                        break;
                    }
                }
            }
        }

        return true;//无论什么样的情况都拦截通过，其实这里的拦截器，更像一个在加载页面前存储cookie和session的一个位置
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
