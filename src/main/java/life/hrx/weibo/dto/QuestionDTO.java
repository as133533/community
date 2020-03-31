package life.hrx.weibo.dto;
import life.hrx.weibo.model.User;
import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;
    private Integer commentCount;
    private Integer viewCount;
    private Long likeCount;
    private String tag;
    private Boolean isLike;
    private String description;
    private User user;
}
