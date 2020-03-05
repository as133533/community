package life.hrx.weibo.controller;


import life.hrx.weibo.dto.PaginationDTO;
import life.hrx.weibo.dto.QuestionDTO;
import life.hrx.weibo.mapper.QuestionMapper;
import life.hrx.weibo.model.QuestionExample;
import life.hrx.weibo.service.QuestionService;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


//首页视图页面
@Controller
public class IndexController {

    @Autowired()
    private QuestionService questionService;
    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "5") Integer size
                        ){
        PaginationDTO<QuestionDTO> paginationDTO = questionService.pagination_by_question(page, size);
        model.addAttribute("pagination",paginationDTO);
        return "index";
    }
}
