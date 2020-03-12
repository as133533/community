package life.hrx.weibo.exception;

/**
 * 本质是用来处理异常的，但是在给前端返回信息也写在这个枚举类里面了，将来需要重构
 */
public enum CustomizeErrorCode implements ICustomizeErrorCode{

    QUESTION_NOT_FOUND(2001, "你找到问题不在了，要不要换个试试？"),
    TARGET_PARAM_NOT_FOUND(2002, "未选中任何问题或评论进行回复"),
    NO_LOGIN(2003, "当前操作需要登录，请登陆后重试"),
    SYS_ERROR(2004, "服务冒烟了，要不然你稍后再试试！！！"),
    TYPE_PARAM_WRONG(2005, "评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006, "回复的评论不存在了，要不要换个试试？"),
    CONTENT_IS_EMPTY(2007, "输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2008, "兄弟你这是读别人的信息呢？"),
    NOTIFICATION_NOT_FOUND(2009, "消息莫非是不翼而飞了？"),
    FILE_UPLOAD_FAIL(2010, "图片上传失败"),
    ERROR_USER_QUESTION(2011,"兄弟你是不是走错房间了？"),
    USER_ALREADY_LOGIN(2012,"用户已经登录"),
    EMAIL_NOT_FOUND(2013,"该邮箱未注册"),
    TOKEN_ERROR(2014,"token非法或已过期"),
    USERNAME_ERROR(2015,"用户名不存在"),
    USER_ERROR(2016,"你不能修改他人的密码"),
    PASSWORD_DIFFERENT(2017,"两次密码输入不一致"),
    PHONE_NEVER_REGISTER(2018,"手机号从未注册")
    ;

    private String message;
    private Integer code;
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
    //定义枚举类的构造方法
    CustomizeErrorCode(Integer code,String message){ //我希望这个方法不会被别的包创建
        this.code=code;
        this.message=message;
    }
}
