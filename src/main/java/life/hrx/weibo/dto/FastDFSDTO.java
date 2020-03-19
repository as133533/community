package life.hrx.weibo.dto;


import lombok.Data;

/**
 * FastDFS上传对象，被FastDFSUtil所使用
 */
@Data
public class FastDFSDTO {
    private String name; //前端传递的用户PC机上的文件所处路径名
    private byte[] content; //文件内容
    private String ext; //文件类型
    private String md5;
    private String author;

    public FastDFSDTO(String filename, byte[] file_buff, String ext) {
        this.name=filename;
        this.content=file_buff;
        this.ext=ext;
    }
}
