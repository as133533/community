package life.hrx.weibo.auth.smscode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 被AuthenticationManager所选择和调用，根据用户信息来获得权限，是认证的主要逻辑。
 * 模仿DaoAuthenticationProvider进行编写的代码，主要是根据token中的手机号，决定这个手机号的用户含有哪些权限。下一步还需要进行一些综合配置
 */
@Slf4j
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {
    private UserDetailsService userDetailsService;

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * 进行身份认证的逻辑
     * @param authentication    就是我们传入的Token
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        //利用UserDetailsService获取用户信息，拿到用户信息后重新组装一个已认证的Authentication

        //认证之前里面存储的是手机号
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken)authentication;
        log.info((String) authenticationToken.getPrincipal());
        UserDetails user = userDetailsService.loadUserByUsername((String) authenticationToken.getPrincipal());  //根据手机号码拿到用户信息
        if(user == null){
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        //认证之后拿到的是一些根据手机号获得的权限
        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(user,user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    /**
     * AuthenticationManager挑选一个AuthenticationProvider
     * 来处理传入进来的Token就是根据supports方法来判断的
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
