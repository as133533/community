package life.hrx.weibo.controller;
import life.hrx.weibo.dto.CommentDTO;
import life.hrx.weibo.dto.QuestionDTO;
import life.hrx.weibo.enums.CommentTypeEnum;
import life.hrx.weibo.service.CommentService;
import life.hrx.weibo.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/question/{id}",method = RequestMethod.GET)
    public String question(Model model, @PathVariable("id") Long id){
        QuestionDTO question_byId = questionService.find_question_byId(id);
        List<CommentDTO> commentDTOS=commentService.toListById(id, CommentTypeEnum.QUESTION);
        List<QuestionDTO> relatedQuestions=questionService.selectRelated(question_byId);
        model.addAttribute("question",question_byId);
        model.addAttribute("comments",commentDTOS);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }
}
