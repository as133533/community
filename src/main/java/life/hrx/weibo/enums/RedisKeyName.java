package life.hrx.weibo.enums;

/**
 * 点赞类型枚举类
 */
public enum RedisKeyName {
    LIKE_COUNT_COMMENT("评论点赞"),
    LIKE_COUNT_QUESTION("问题点赞"),
    QUESTION_VIEW("问题游览数")
    ;

    private String type;

    RedisKeyName(String type) {
        this.type=type;
    }

    public String getType() {
        return type;
    }
}
