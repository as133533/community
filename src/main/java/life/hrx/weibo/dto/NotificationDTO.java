package life.hrx.weibo.dto;

import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;
    private String notifierName;
    private String outerTitle;//问题title
    private Long outerid;//问题id
    private String typeName;
    private Integer type;
}
