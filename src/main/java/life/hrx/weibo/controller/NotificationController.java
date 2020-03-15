package life.hrx.weibo.controller;

import life.hrx.weibo.security.auth.myuserdetails.MyUserDetails;
import life.hrx.weibo.dto.NotificationDTO;
import life.hrx.weibo.enums.NotificationTypeEnum;
import life.hrx.weibo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;


    @RequestMapping(value = "/notification/{id}",method = RequestMethod.GET)
    public String notification(@PathVariable("id") Long id, Authentication authentication){
//        User user = (User)request.getSession().getAttribute("user");
//
//        if (user == null){
//            throw new CustomizeException(CustomizeErrorCode.NO_LOGIN);
//        }

        Object principal = authentication.getPrincipal();
        MyUserDetails myUserDetails = (MyUserDetails) principal;
        //将该问题的status标记为已读，然后跳转到问题页
        NotificationDTO notificationDTO = notificationService.read(id, myUserDetails);

        if (notificationDTO.getType()== NotificationTypeEnum.QUESTION.getType() || notificationDTO.getType()==NotificationTypeEnum.COMMENT.getType()){
            return "redirect:/question/"+notificationDTO.getOuterid();
        }else {
            return "redirect:/";
        }
    }
}
