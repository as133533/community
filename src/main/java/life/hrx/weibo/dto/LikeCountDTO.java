package life.hrx.weibo.dto;


import lombok.Data;

/**
 * 点赞对象
 */

@Data
public class LikeCountDTO {
    private Long userId;
    private Boolean isLike;//是否已经点过赞
    private Long likeCount; //点赞数量
    private Long likeId;//被点赞对象的id
    private String likeType;//点赞的类型
}
