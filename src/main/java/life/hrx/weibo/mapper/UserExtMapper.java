package life.hrx.weibo.mapper;

import life.hrx.weibo.model.User;

import java.util.List;

public interface UserExtMapper {
    List<User> selectByUsernameOrPhone(String nameOrPhone);
}
