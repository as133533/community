package life.hrx.weibo.dto;
import life.hrx.weibo.exception.CustomizeErrorCode;
import lombok.Data;
@Data
public class ResultDTO<T> {
    private Integer code;
    private String message;
    private T data;
    public static ResultDTO okOf(){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        return resultDTO;
    }
    public static <T> ResultDTO okOf(T data){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        resultDTO.setData(data);
        return resultDTO;
    }

    public static ResultDTO errorOf(CustomizeErrorCode noLogin) {
        ResultDTO resultDTO=new ResultDTO();
        resultDTO.setCode(noLogin.getCode());
        resultDTO.setMessage(noLogin.getMessage());
        return resultDTO;
    }
}

