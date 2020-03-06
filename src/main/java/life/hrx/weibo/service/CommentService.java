package life.hrx.weibo.service;
import life.hrx.weibo.auth.myuserdetails.MyUserDetails;
import life.hrx.weibo.dto.CommentDTO;
import life.hrx.weibo.dto.NotificationDTO;
import life.hrx.weibo.dto.PaginationDTO;
import life.hrx.weibo.dto.QuestionDTO;
import life.hrx.weibo.enums.CommentTypeEnum;
import life.hrx.weibo.enums.NotificationStatusEnum;
import life.hrx.weibo.enums.NotificationTypeEnum;
import life.hrx.weibo.exception.CustomizeErrorCode;
import life.hrx.weibo.exception.CustomizeException;
import life.hrx.weibo.mapper.*;
import life.hrx.weibo.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired(required = false)
    private CommentMapper commentMapper;
    @Autowired(required = false)
    private QuestionMapper questionMapper;
    @Autowired(required = false)
    private QuestionExtMapper questionExtMapper;
    @Autowired(required = false)
    private CommentExtMapper commentExtMapper;

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired(required = false)
    private NotificationMapper notificationMapper;

    public void Insert_Comment(Comment comment, MyUserDetails user) {
        if (comment.getParentId()==null || comment.getParentId()==0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType()==null || comment.getType()==0){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if (Objects.equals(comment.getType(), CommentTypeEnum.QUESTION.getType())){//表示该评论是用来回复问题的
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            comment.setCommentCount(0);
            commentMapper.insert(comment);//添加评论
            question.setCommentCount(1);//这个只是用来限制每次增加的数量
            questionExtMapper.incCommentCount(question);//增加评论数
            if (!Objects.equals(question.getCreator(), user.getId())) {//如果发起问题的人不是当前登录的人才发起通知
                createNotify(question.getId(), NotificationTypeEnum.QUESTION.getType(), user, question.getCreator(), question.getTitle());
            }

        }else {//表示该评论是用来回复评论
            Comment prtcomment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (prtcomment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            Question question = questionMapper.selectByPrimaryKey(prtcomment.getParentId());//回复评论的时候也要验证问题是否被删除了
            if (question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            prtcomment.setCommentCount(1);//这个只是用来表示单次增加的数量
            commentExtMapper.incCommentCount(prtcomment);
            if (!Objects.equals(prtcomment.getCommentator(), user.getId())) {//如果发起评论的人不是当前登录的人才发起通知
                createNotify(question.getId(), NotificationTypeEnum.COMMENT.getType(), user, prtcomment.getCommentator(), question.getTitle());
            }
        }
    }

    public List<CommentDTO> toListById(Long id, CommentTypeEnum commentTypeEnum) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andParentIdEqualTo(id).andTypeEqualTo(commentTypeEnum.getType());
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if (comments==null){
            return new ArrayList<>();
        }
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            User user = userMapper.selectByPrimaryKey(comment.getCommentator());
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(user);
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }

    public void createNotify(Long id,Integer type,MyUserDetails user,Long receiverId,String outerTitle){
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setNotifier(user.getId());//发起通知人id
        notification.setNotifierName(user.getUsername());
        notification.setOuterid(id);//通知类型的id，是questionId
        notification.setReceiver(receiverId);//接受人id
        notification.setOuterTitle(outerTitle);//评论的title
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setType(type);
        notificationMapper.insert(notification);
    }


}
