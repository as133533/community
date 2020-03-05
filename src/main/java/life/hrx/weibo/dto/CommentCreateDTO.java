package life.hrx.weibo.dto;


import lombok.Data;

@Data
//该类用来接收前端json格式传递过来的请求
public class CommentCreateDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
