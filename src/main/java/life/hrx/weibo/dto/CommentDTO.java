package life.hrx.weibo.dto;

import life.hrx.weibo.model.User;
import lombok.Data;


@Data
//响应给前端的JSON格式数据
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private Long commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private Long likeCount;
    private Integer commentCount;
    private String content;
    private User user;
}
