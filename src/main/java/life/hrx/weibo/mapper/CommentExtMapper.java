package life.hrx.weibo.mapper;

import life.hrx.weibo.model.Comment;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}