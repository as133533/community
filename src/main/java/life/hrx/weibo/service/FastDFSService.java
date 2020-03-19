package life.hrx.weibo.service;

import life.hrx.weibo.dto.FastDFSDTO;
import life.hrx.weibo.utils.FastDFSUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 用于实现FastDFS的各类逻辑
 */
@Service
public class FastDFSService {

    /**
     * 用来将前端上传过来的文件存储到fastDFS中,并返回存储的路径
     * @param multipartFile
     * @return path 完整的存储路径，如http://ip:端口/group1/M00/02/44/xxxxx.sh
     */
    public String saveFile(MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath={};
        String filename = multipartFile.getOriginalFilename();
        String ext = filename.substring(filename.lastIndexOf(".") + 1);//截取绝对路径中的.后面的字符串。即获得文件类型

        byte[] file_buff=null;
        try(InputStream inputStream = multipartFile.getInputStream();){
            int len = inputStream.available();
            file_buff=new byte[len];
            inputStream.read(file_buff);
        }
        FastDFSDTO fastDFSDTO = new FastDFSDTO(filename, file_buff, ext);

        fileAbsolutePath = FastDFSUtil.upload(fastDFSDTO); //获得文件存储后的路径存储路径

        String path=FastDFSUtil.getTrackerUrl()+fileAbsolutePath[0]+"/"+fileAbsolutePath[1];
        return path;
    }
}
