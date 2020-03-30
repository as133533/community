package life.hrx.weibo.controller;
import life.hrx.weibo.security.auth.myuserdetails.MyUserDetails;
import life.hrx.weibo.dto.CommentCreateDTO;
import life.hrx.weibo.dto.CommentDTO;
import life.hrx.weibo.dto.ResultDTO;
import life.hrx.weibo.enums.CommentTypeEnum;
import life.hrx.weibo.exception.CustomizeErrorCode;
import life.hrx.weibo.model.Comment;
import life.hrx.weibo.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//该类并没有提供真正的视图，只是用作前端question页面提交回复或者评论的时候的一个入口
@Controller
public class CommentController{

    @Autowired
    private CommentService commentService;


    /**
     *
     * @param commentCreateDTO
     *
     * @param authentication 这个为spring security用户身份存储地方
     * @return
     */
    @ResponseBody//返回一个JSON格式的数据对象
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public ResultDTO comment(@RequestBody CommentCreateDTO commentCreateDTO,
                              Authentication authentication){//@RequestBody 默认就返回JSON格式的数据

        //这里就不验证用户是否登录了，而是直接由spring security控制如果没有登录的情况下访问这个地址，就直接跳转到登录页，前端可以在未登录的情况下，不显示提交按钮而显示注册和登录超链接


        Object principal = authentication.getPrincipal();
        MyUserDetails myUserDetails= (MyUserDetails) principal;
        if (commentCreateDTO==null || StringUtils.isBlank(commentCreateDTO.getContent())){
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setContent(commentCreateDTO.getContent());
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setCommentator(myUserDetails.getId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setType(commentCreateDTO.getType());
        comment.setLikeCount(0L);
        commentService.Insert_Comment(comment,myUserDetails);
        return ResultDTO.okOf();

    }
    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comment(@PathVariable("id") Long id){
        List<CommentDTO> commentDTOS = commentService.toListById(id, CommentTypeEnum.COMMENT);

        return ResultDTO.okOf(commentDTOS);
    }
}
