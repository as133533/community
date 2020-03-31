package life.hrx.weibo.service;


import life.hrx.weibo.dto.LikeCountDTO;
import life.hrx.weibo.security.auth.myuserdetails.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LikeCountService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;



    public LikeCountDTO like(String key, MyUserDetails user,Long id,String type) {
        LikeCountDTO likeCountDTO = new LikeCountDTO();

        //判断是否user已经在点赞集合中
        Boolean isMember = redisTemplate.opsForSet().isMember(key, user);

        if (isMember ==null || !isMember){

            //点赞
            redisTemplate.opsForSet().add(key,user);
            Long count = count(key);
            likeCountDTO.setLikeId(id);
            likeCountDTO.setLikeCount(count);
            likeCountDTO.setUserId(user.getId());
            likeCountDTO.setLikeType(type);
            likeCountDTO.setIsLike(true);


        }else{

            //取消赞
            redisTemplate.opsForSet().remove(key,user);
            Long count = count(key);
            likeCountDTO.setIsLike(false);
            likeCountDTO.setLikeCount(count);
            likeCountDTO.setLikeType(type);
            likeCountDTO.setUserId(user.getId());
            likeCountDTO.setLikeId(id);

        }
        return likeCountDTO;

    }

    /**
     * 统计点赞数
     * @param key 目标key
     * @return
     */
    public Long count(String key){
        return redisTemplate.opsForSet().size(key);
    }

    public boolean isLike(String key, MyUserDetails user){
        //判断key中是否含有user
        return redisTemplate.opsForSet().isMember(key,user);
    }

}
