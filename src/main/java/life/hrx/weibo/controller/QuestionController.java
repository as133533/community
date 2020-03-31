package life.hrx.weibo.controller;
import life.hrx.weibo.dto.CommentDTO;
import life.hrx.weibo.dto.LikeCountDTO;
import life.hrx.weibo.dto.QuestionDTO;
import life.hrx.weibo.dto.ResultDTO;
import life.hrx.weibo.enums.CommentTypeEnum;
import life.hrx.weibo.enums.RedisKeyName;
import life.hrx.weibo.exception.CustomizeErrorCode;
import life.hrx.weibo.security.auth.myuserdetails.MyUserDetails;
import life.hrx.weibo.service.CommentService;
import life.hrx.weibo.service.LikeCountService;
import life.hrx.weibo.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeCountService likeCountService;


    @RequestMapping(value = "/question/{id}",method = RequestMethod.GET)
    public String question(Model model, @PathVariable("id") Long id,Authentication authentication){

        MyUserDetails userDetails=null;
        if (!StringUtils.equals(SecurityContextHolder.getContext().getAuthentication().getName(),"anonymousUser")){ //如果不是匿名用户，说明已经登录
            userDetails=(MyUserDetails)authentication.getPrincipal();
        }

        //累加阅读数
        questionService.incView(id);
        QuestionDTO question_byId = questionService.find_question_byId(id,userDetails);
        List<CommentDTO> commentDTOS=commentService.toListById(id, CommentTypeEnum.QUESTION,userDetails);
        List<QuestionDTO> relatedQuestions=questionService.selectRelated(question_byId);
        model.addAttribute("question",question_byId);
        model.addAttribute("comments",commentDTOS);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }



    @RequestMapping(value = "/likecount",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO like(@RequestParam("id") Long id, @RequestParam("type") String type, Authentication authentication){

        MyUserDetails user = (MyUserDetails)authentication.getPrincipal();

        //设置redis中key的值，格式类似 问题点赞:问题id
        String key;
        if (StringUtils.equals(type,"comment")){
            key= RedisKeyName.LIKE_COUNT_COMMENT.getType()+":"+id;
            LikeCountDTO likeCountDTO = likeCountService.like(key, user, id, type);
            return ResultDTO.okOf(likeCountDTO);
        }else if (StringUtils.equals(type,"question")){
            key= RedisKeyName.LIKE_COUNT_QUESTION.getType()+":"+id;
            LikeCountDTO likeCountDTO = likeCountService.like(key, user, id, type);
            return ResultDTO.okOf(likeCountDTO);
        }

        return ResultDTO.errorOf(CustomizeErrorCode.LIKE_TYPE_WRONG.getCode(),CustomizeErrorCode.LIKE_TYPE_WRONG.getMessage());


    }
}
