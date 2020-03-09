package life.hrx.weibo.dto;

import lombok.Data;

@Data
public class RegisterCheckDTO {
    private String checkName;
    private Integer type;// 1检查用户名，2检查邮箱，3检查电话号码
}
