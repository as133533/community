package life.hrx.weibo.enums;

public enum NotificationTypeEnum {
    QUESTION(1),//表示是回复的问题
    COMMENT(2)//表示的是回复评论
    ;
    private Integer type;

    NotificationTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
