package life.hrx.weibo.security.auth.myuserdetails;
import life.hrx.weibo.mapper.UserExtMapper;
import life.hrx.weibo.model.User;
import life.hrx.weibo.service.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 该类是加载用户登录信息的逻辑，基本上替代了loginController的登录功能
 */

@Component
public class MyUserDetailsService  implements UserDetailsService {

    @Autowired(required = false)
    private UserExtMapper userExtMapper;

    @Autowired
    private NotificationService notificationService;

    /**
     * 该类用来自定义用户加载，根据用户名从数据库中加载出user。并赋值给myUserDetails返回，这里没有给user设置权限。
     * @param username 前方传过来的username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<User> users = userExtMapper.selectByUsernameOrPhone(username);
        MyUserDetails myUserDetails = new MyUserDetails();
        if (users.get(0)!=null){
            BeanUtils.copyProperties(users.get(0),myUserDetails);
            Long unreadcount = notificationService.unreadcount(myUserDetails.getId());
            myUserDetails.setUnreadCount(unreadcount);
        }
        return myUserDetails;

    }
}
