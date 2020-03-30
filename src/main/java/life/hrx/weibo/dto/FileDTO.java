package life.hrx.weibo.dto;


import lombok.Data;




/**
 * 传递给前端的，与图片相关的DTO
 */
@Data
public class FileDTO  {

    private int success;
    private String message;
    private String url;
}
