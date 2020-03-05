package life.hrx.weibo.controller;

import life.hrx.weibo.dto.NotificationDTO;
import life.hrx.weibo.enums.NotificationTypeEnum;
import life.hrx.weibo.exception.CustomizeErrorCode;
import life.hrx.weibo.exception.CustomizeException;
import life.hrx.weibo.model.User;
import life.hrx.weibo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;


    @RequestMapping(value = "/notification/{id}",method = RequestMethod.GET)
    public String notification(@PathVariable("id") Long id, HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");

        if (user == null){
            throw new CustomizeException(CustomizeErrorCode.NO_LOGIN);
        }

        //将该问题的status标记为已读，然后跳转到问题页
        NotificationDTO notificationDTO = notificationService.read(id, user);

        if (notificationDTO.getType()== NotificationTypeEnum.QUESTION.getType() || notificationDTO.getType()==NotificationTypeEnum.COMMENT.getType()){
            return "redirect:/question/"+notificationDTO.getOuterid();
        }else {
            return "redirect:/";
        }
    }
}
