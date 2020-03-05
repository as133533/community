package life.hrx.weibo.enums;

public enum  CommentTypeEnum {
    QUESTION(1),//表示1类型为回复问题类型
    COMMENT(2),//表示2类型为回复评论类型
    ;
    private Integer type;
    CommentTypeEnum(Integer type){
        this.type=type;
    }

    public Integer getType() {
        return type;
    }
}
