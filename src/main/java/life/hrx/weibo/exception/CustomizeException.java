package life.hrx.weibo.exception;


//继承RuntimeException类,该类接受一个异常信息接口
public class CustomizeException extends RuntimeException{
    private String message;
    private Integer code;

    public String getMessage(){
        return this.message;
    }
    public Integer getCode(){
        return this.code;
    }

    public CustomizeException(ICustomizeErrorCode customizeErrorCode){
        this.message=customizeErrorCode.getMessage();
        this.code=customizeErrorCode.getCode();
    }
}
