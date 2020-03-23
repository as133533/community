package life.hrx.weibo.controller;
import life.hrx.weibo.cache.HotTagCache;
import life.hrx.weibo.dto.PaginationDTO;
import life.hrx.weibo.dto.QuestionDTO;
import life.hrx.weibo.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;


//首页视图页面

@Slf4j
@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HotTagCache hotTagCache;


    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page",defaultValue = "1") Integer page,
                        @RequestParam(name = "size",defaultValue = "5") Integer size,
                        @RequestParam(name = "search",required = false) String search,
                        @RequestParam(name="tag",required = false) String tag,
                        @RequestParam(name="sort",required=false )String sort
                        ){
        PaginationDTO<QuestionDTO> paginationDTO = questionService.pagination_by_question(search,tag,sort,page, size);
        List<String> tags = hotTagCache.getHots();
        model.addAttribute("pagination",paginationDTO);
        model.addAttribute("tags",tags);
        model.addAttribute("tag",tag);
        model.addAttribute("search",search);
        model.addAttribute("sort",sort);
        return "index";
    }
}
