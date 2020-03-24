package life.hrx.weibo.controller;
import life.hrx.weibo.security.auth.myuserdetails.MyUserDetails;
import life.hrx.weibo.cache.TagCache;
import life.hrx.weibo.dto.QuestionDTO;
import life.hrx.weibo.model.Question;
import life.hrx.weibo.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.stream.Collectors;


//提问和编辑页面
@Controller
@Slf4j
public class PublishController {

    @Autowired
    private QuestionService questionService;



    @RequestMapping(value = "/publish",method = RequestMethod.GET)
    public String publish(Model model){
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @RequestMapping(value = "/publish",method = RequestMethod.POST)
    public String publish(@RequestParam(value = "title",required = false) String title,
                          @RequestParam(value = "description",required = false)String description,
                          @RequestParam(value = "tag",required = false) String tag,
                          @RequestParam(value = "id",required = false)Long id,
                          Model model,
                          Authentication authentication
                          ){
        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tags", TagCache.get());
        Object principal = authentication.getPrincipal();
        MyUserDetails myUserDetails = (MyUserDetails) principal;
        if (StringUtils.isBlank(title)){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if (StringUtils.isBlank(description)){
            model.addAttribute("error","问题描述不能为空");
            return "publish";
        }
        if (StringUtils.isBlank(tag)){
            model.addAttribute("error","问题标签不能为空");
            return "publish";
        }

        String s = TagCache.filterInvalid(tag);
        //如果标签非法
        if (StringUtils.isNotBlank(s)){
            model.addAttribute("error","输入非法标签"+s);
            return "publish";
        }

        //去除掉空格tag
        String[] split = StringUtils.split(tag,",");
        String reTag = Arrays.stream(split).filter(StringUtils::isNotBlank).collect(Collectors.joining(","));

        Question question = new Question();
        question.setDescription(description);
        question.setTag(reTag);
        question.setTitle(title);
        question.setLikeCount(0);
        question.setId(id);//这个id是必要的，为了在修改问题的时候，不改变原来的id

        //这里传入两个参数进去，一个是查找问题是否在数据库中存在，如果不存在，一个id是防止在用户修改问题的时候，出现他人在修改别人id的情况

        questionService.updateOrInsert(question,myUserDetails.getId());

        return "redirect:/";
    }


    /**
     * 用户问题修改页面，注意，其中的post提交按钮，还是上面的controller
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/publish/{id}",method = RequestMethod.GET)
    public String publish(Model model, @PathVariable("id") Long id){
        QuestionDTO question_byId = questionService.find_question_byId(id);
        model.addAttribute("title",question_byId.getTitle());
        model.addAttribute("description",question_byId.getDescription());
        model.addAttribute("tag",question_byId.getTag());
        model.addAttribute("id",question_byId.getId());//得返回给前端id
        model.addAttribute("tags",TagCache.get());//返回给前端所有的tags
        return "publish";
    }


}
