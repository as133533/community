package life.hrx.weibo.mapper;


import life.hrx.weibo.model.Question;

import java.util.List;

public interface QuestionExtMapper {
    int incCommentCount(Question question);
    List<Question> selectRelated(Question question);
}
