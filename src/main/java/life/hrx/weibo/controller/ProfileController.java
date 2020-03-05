package life.hrx.weibo.controller;


import life.hrx.weibo.dto.NotificationDTO;
import life.hrx.weibo.dto.PaginationDTO;
import life.hrx.weibo.dto.QuestionDTO;
import life.hrx.weibo.exception.CustomizeErrorCode;
import life.hrx.weibo.exception.CustomizeException;
import life.hrx.weibo.model.User;
import life.hrx.weibo.service.NotificationService;
import life.hrx.weibo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

//用户信息视图，包括我的问题，我的通知页面等等
@Controller
public class ProfileController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(value = "action") String action,
                          Model model,
                          @RequestParam(value = "page",defaultValue = "1") Integer page,
                          @RequestParam(value = "size",defaultValue = "3") Integer size,
                          HttpServletRequest request
                          ){

        User user = (User)request.getSession().getAttribute("user");
        if (user == null){
            throw new CustomizeException(CustomizeErrorCode.NO_LOGIN);
        }

        if ("questions".equals(action)){
            PaginationDTO<QuestionDTO> paginationDTO = questionService.pagination_by_user_question(user.getId(), page, size);//问题分页
            model.addAttribute("pagination",paginationDTO);
            model.addAttribute("section","questions");
            model.addAttribute("sectionName","我的问题");


        }else if ("replies".equals(action)){
            PaginationDTO<NotificationDTO> paginationDTO = notificationService.paginationByUserComment(user.getId(), page, size);//个人通知分页
            model.addAttribute("pagination",paginationDTO);
            model.addAttribute("section","replies");
            model.addAttribute("sectionName","我的通知");
        }

        return "profile";

    }

}
