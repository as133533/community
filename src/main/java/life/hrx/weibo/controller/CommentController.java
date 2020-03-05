package life.hrx.weibo.controller;
import life.hrx.weibo.dto.CommentCreateDTO;
import life.hrx.weibo.dto.CommentDTO;
import life.hrx.weibo.dto.ResultDTO;
import life.hrx.weibo.enums.CommentTypeEnum;
import life.hrx.weibo.exception.CustomizeErrorCode;
import life.hrx.weibo.model.Comment;
import life.hrx.weibo.model.User;
import life.hrx.weibo.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

//该类并没有提供真正的视图，只是用作前端question页面提交回复或者评论的时候的一个入口
@Controller
public class CommentController{

    @Autowired
    private CommentService commentService;

    @ResponseBody//返回一个JSON格式的数据对象
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public ResultDTO comment(@RequestBody CommentCreateDTO commentCreateDTO,
                             HttpServletRequest request){//@RequestBody 默认就返回JSON格式的数据
        User user = (User)request.getSession().getAttribute("user");
        if (user==null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if (commentCreateDTO==null || StringUtils.isBlank(commentCreateDTO.getContent())){
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setContent(commentCreateDTO.getContent());
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setCommentator(user.getId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setType(commentCreateDTO.getType());
        comment.setLikeCount(0L);
        commentService.Insert_Comment(comment,user);
        return ResultDTO.okOf();

    }
    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comment(@PathVariable("id") Long id){
        List<CommentDTO> commentDTOS = commentService.toListById(id, CommentTypeEnum.COMMENT);

        return ResultDTO.okOf(commentDTOS);
    }
}
