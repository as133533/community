package life.hrx.weibo.security.auth.smscode;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;


/**
 * 模仿UsernamePasswordAuthenticationToken写的，在SmeCodeAuthenticationProvider中被调用。主要用来存放用户信息，
 * 被AuthenticationManager所管理和认证
 */

public class SmsCodeAuthenticationToken  extends AbstractAuthenticationToken {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    //存放认证信息，认证之前存放手机号，认证之后存放登录的用户
    private final Object principal;

    public SmsCodeAuthenticationToken(String phone) {
        super((Collection)null);
        this.principal = phone;
        this.setAuthenticated(false);
    }

    public SmsCodeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    public Object getCredentials() {
        return null;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    public void eraseCredentials() {
        super.eraseCredentials();
    }

}
