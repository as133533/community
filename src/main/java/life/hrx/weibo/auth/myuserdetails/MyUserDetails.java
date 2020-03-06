package life.hrx.weibo.auth.myuserdetails;


import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 该类类似于登录用户的model,这里存放着用户信息和权限，必须接口自UserDetails，否则spring security无法做自定义的认证,这个会被存在session中
 */

@Component
public class MyUserDetails  implements UserDetails {
    private Long id; //数据库中用户id主键
    private String password;  //密码
    private String username;  //用户名
    private String phone; //用户电话
    private String email; //用户邮箱
    private String gmtCreate; //用户创建时间
    private String avatarUrl; //用户图像地址
    private Long unreadCount;
    private boolean accountNonExpired;   //是否没过期
    private boolean accountNonLocked;   //是否没被锁定
    private boolean credentialsNonExpired;  //是否没过期
    private boolean enabled;  //账号是否可用

    private Collection<? extends GrantedAuthority> authorities;  //用户的权限集合


    public Long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }

    //以下四个其实只是为了使这个类称为javaBean才设置的
    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public boolean isAccountNonExpired() { //下面四个一直返回true,因为我们没有对此进行设置，所以默认都保证账户可用，没有过期，没有被锁定，
        return true;
    }


    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //实际上这里我们也没有设置用户的权限，默认的是我们只要用户登录了就可以进行一切操作，一些细节的限制在Service中实现
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}
