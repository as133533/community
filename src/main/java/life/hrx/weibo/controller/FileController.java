package life.hrx.weibo.controller;

import life.hrx.weibo.dto.FileDTO;
import life.hrx.weibo.service.FastDFSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * markdown图片上传controller
 */



@Controller
public class FileController {

    @Autowired
    private FastDFSService fastDFSService;

    @RequestMapping(value = "/file/upload")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request) throws IOException {
        FileDTO fileDTO = new FileDTO();
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("editormd-image-file");
        if (file ==null){
            fileDTO.setSuccess(0);
            fileDTO.setMessage("图片上传路径不能为空");
            fileDTO.setUrl(null);
            return fileDTO;
        }
        String savePath = fastDFSService.saveFile(file);
        fileDTO.setMessage("图片上传成功");
        fileDTO.setSuccess(1);
        fileDTO.setUrl(savePath);
        return fileDTO;
        //这里还有一个问题，如果用户在发布问题的时候，在文本输入框中提交了图片，但是在发布编辑文本的过程中不想发布了，
        // 那么这个文件就会变成垃圾文件，要怎么在用户中途突然不想发布的情况下，或者长时间未编辑的情况下将存储的垃圾文件删除呢
    }

}
