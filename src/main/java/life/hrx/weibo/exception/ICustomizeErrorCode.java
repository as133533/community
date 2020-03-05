package life.hrx.weibo.exception;



//定义一个异常接口，用来规范异常信息类
public interface ICustomizeErrorCode {
    public String getMessage();
    public Integer getCode();
}
