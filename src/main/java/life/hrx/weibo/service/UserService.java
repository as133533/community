package life.hrx.weibo.service;


import life.hrx.weibo.mapper.UserMapper;
import life.hrx.weibo.model.User;
import life.hrx.weibo.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    //通过用户登录时的名字来获取User
    public User findByName(String name) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andNameEqualTo(name);
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() ==0){
            return null;
        }
        return users.get(0);
    }

    //根据用户名和密码来校验登录密码
    public boolean isAuthPassword(String name,String password) {


        UserExample userExample = new UserExample();
        userExample.createCriteria().andNameEqualTo(name);
        List<User> users = userMapper.selectByExample(userExample);
        boolean matches = bCryptPasswordEncoder.matches(password, users.get(0).getPassword());//验证密码正确性
        return matches;
    }

    //判断邮箱是否存在，存在返回true,错误返回false
    public boolean findByEmail(String email) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andEmailEqualTo("email");
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size()==0){
            return false;
        }
        return true;
    }

    public void hashPassowrd(String username,String email,String password) throws NoSuchAlgorithmException {
        String encode = bCryptPasswordEncoder.encode(password);
        User user=new User();
        user.setEmail(email);
        user.setName(username);
        user.setPassword(encode);
        setInfo(user);
        userMapper.insert(user);
    }

    public void setInfo(User user) throws NoSuchAlgorithmException {
        MessageDigest md=MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(user.getEmail().getBytes());
        StringBuffer buffer=new StringBuffer();

        //把每一个byte做一个与运算
        for (byte b : digest) {
            int number=b & 0xff;
            String str=Integer.toHexString(number);
            if (str.length()==1){
                buffer.append("0");
            }
            buffer.append(str);
        }
        user.setAvatarUrl(String.format("https://www.gravatar.com/avatar/%s?d=identicon",buffer.toString()));
        user.setGmtCreate(System.currentTimeMillis());
        user.setToken(UUID.randomUUID().toString());
    }
}
