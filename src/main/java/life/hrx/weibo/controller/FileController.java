package life.hrx.weibo.controller;

import life.hrx.weibo.dto.FileDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * markdown图片上传controller
 */



@Controller
public class FileController {

    @RequestMapping(value = "/file/upload")
    @ResponseBody

    public FileDTO upload(HttpServletRequest request){
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("editormd-image-file");

        FileDTO fileDTO = new FileDTO();
//        fileDTO.setMessage("图片上传成功");
        fileDTO.setSuccess(1);
        fileDTO.setUrl("/images/back.jpg");
        return fileDTO;
    }

}
