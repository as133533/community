package life.hrx.weibo.service;

import life.hrx.weibo.dto.PaginationDTO;
import life.hrx.weibo.dto.QuestionDTO;
import life.hrx.weibo.dto.QuestionQueryDTO;
import life.hrx.weibo.enums.RedisKeyName;
import life.hrx.weibo.enums.SortEnum;
import life.hrx.weibo.exception.CustomizeErrorCode;
import life.hrx.weibo.exception.CustomizeException;
import life.hrx.weibo.mapper.QuestionExtMapper;
import life.hrx.weibo.mapper.QuestionMapper;
import life.hrx.weibo.mapper.UserMapper;
import life.hrx.weibo.model.*;
import life.hrx.weibo.security.auth.myuserdetails.MyUserDetails;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired(required = false)
    private QuestionMapper questionMapper;
    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired(required = false)
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private LikeCountService likeCountService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    //用来增加新的问题或者更新问题
    public void updateOrInsert(Question question, Long user_id) {
        Question question_from_db = questionMapper.selectByPrimaryKey(question.getId());//从数据库中查找question
        if (question_from_db == null) {//为true说明是一个新提交
            question.setCommentCount(0);
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setCreator(user_id);
            questionMapper.insert(question);
        } else {//说明是在编辑问题，更新问题即可，但是要判断登录用户是否和该问题的发起人相同
            //这里先前有个bug没有处理，因为mysql数据库中存储的是long类型，且已经超过了128L长度，long类型在超过这个长度就是两个对象，所以要使用longValue来比较
            if (question_from_db.getCreator().longValue() !=user_id.longValue()) {
                throw new CustomizeException(CustomizeErrorCode.ERROR_USER_QUESTION);
            }
            question_from_db.setGmtModified(System.currentTimeMillis());
            question_from_db.setTitle(question.getTitle());
            question_from_db.setTag(question.getTag());
            question_from_db.setDescription(question.getDescription());
            question_from_db.setGmtCreate(System.currentTimeMillis());
            //questionMapper.updateByPrimaryKey(question_from_db);//在表中有text字段的时候，这个方法不会更新text字段的表，所以应该使用下面的方法
            questionMapper.updateByPrimaryKeyWithBLOBs(question_from_db);//只需更新问题的修改时间，问题的描述、标题、标签,对于有text文本后者blob的表，应该使用带有withBLOB的方法
        }
    }


    //通过前端传递的的page和size参数得到paginationDTO
    public PaginationDTO<QuestionDTO> pagination_by_question(String search, String tag, String sort,Integer page, Integer size) {

        //search中不能出现+ * ?等字符
        if (StringUtils.isNotBlank(search)){
            String[] tags = StringUtils.split(search, " ");
            search= Arrays
                    .stream(tags)
                    .filter(StringUtils::isNotBlank)
                    .map(t -> t.replace("+","").replace("*","").replace("?",""))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("|"));
        }



        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setSize(size);
        if (StringUtils.isNotBlank(tag)) {
            tag = tag.replace("+", "").replace("*", "").replace("?", "");
            questionQueryDTO.setTag(tag);
        }

        for (SortEnum sortEnum:SortEnum.values()) {
            if (sortEnum.name().equalsIgnoreCase(sort)){
                questionQueryDTO.setSort(sort);
                break;
            }
        }

        return questionDTOPaginationDTO(page, size, questionQueryDTO);
    }

    //个人问题分页
    public PaginationDTO<QuestionDTO> pagination_by_user_question(Long id, Integer page, Integer size) {
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("`gmt_create` DESC");
        questionExample.createCriteria().andCreatorEqualTo(id);
        return questionDTOPaginationDTO(page,size,questionExample);

    }


    //问题分页公共方法
    private PaginationDTO<QuestionDTO> questionDTOPaginationDTO(Integer page, Integer size, Object questionExample) {
        int offset = page < 0 ? 0 : (page - 1) * size;//计算offset
        Integer Count=0;
        List<Question> questions=new ArrayList<>();
        if (questionExample instanceof  QuestionExample){ //如果传进来的参数是QuestExample类型的。说明是个人问题分页
            //全部的信息数
            Count = (int) questionMapper.countByExample((QuestionExample) questionExample);
            //当前页要显示的信息
             questions = questionMapper.selectByExampleWithBLOBsWithRowbounds((QuestionExample)questionExample, new RowBounds(offset, size));
        }else if (questionExample instanceof QuestionQueryDTO) { //如果传进来的参数是QuestionQueryDTO类型的，那么说明是首页问题分页
            ((QuestionQueryDTO) questionExample).setOffset(offset);
            Count = questionExtMapper.countBySearch((QuestionQueryDTO) questionExample);
            questions = questionExtMapper.selectBySearch((QuestionQueryDTO) questionExample);
        }
        int totalCount;//计算总页数
        if (Count % size == 0) {
            totalCount = Count / size;
        } else {
            totalCount = Count / size + 1;
        }
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setPagination(totalCount, page);//设置分页条显示
        List<QuestionDTO> questionDTOS = questions.stream().map(question ->
        {
            QuestionDTO questionDTO = new QuestionDTO();
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTO.setLikeCount(likeCountService.count(RedisKeyName.LIKE_COUNT_QUESTION.getType()+":"+question.getId()));
            questionDTO.setViewCount(countView(question.getId()));
            return questionDTO;
        }).collect(Collectors.toList());//让questions列表中的每一个都变成已经设置好的questionDTO返回

        paginationDTO.setData(questionDTOS);

        return paginationDTO;

    }

    public QuestionDTO find_question_byId(Long id, MyUserDetails userDetails) {
        Question question = questionMapper.selectByPrimaryKey(id);
        Long creator = question.getCreator();
        User user = userMapper.selectByPrimaryKey(creator);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        questionDTO.setUser(user);
        questionDTO.setLikeCount(likeCountService.count(RedisKeyName.LIKE_COUNT_QUESTION.getType()+":"+id));
        questionDTO.setViewCount(countView(id));
        if (userDetails==null){
            questionDTO.setIsLike(false);
        }else {
            questionDTO.setIsLike(likeCountService.isLike(RedisKeyName.LIKE_COUNT_QUESTION.getType()+":"+id,userDetails));
        }
        return questionDTO;

    }

    public List<QuestionDTO> selectRelated(QuestionDTO question_byId) {
        if (StringUtils.isBlank(question_byId.getTag())){
            return new ArrayList<>();
        }



        String[] tags=StringUtils.split(question_byId.getTag(),",");

        //过滤掉查找出来的所有空格 + * ?等标签
        String regexpTag = Arrays
                .stream(tags)
                .filter(StringUtils::isNotBlank)
                .map(t -> t.replace("+", "").replace("*", "").replace("?", ""))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining("|"));
        Question question=new Question();
        question.setId(question_byId.getId());
        question.setTag(regexpTag);

        List<Question> questions=questionExtMapper.selectRelated(question);

        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());

        return questionDTOS;
    }
    public void incView(Long id){
        redisTemplate.opsForValue().increment(RedisKeyName.QUESTION_VIEW.getType()+":"+id);
    }

    public Integer countView(Long id){
        Integer count=0;
        String key=RedisKeyName.QUESTION_VIEW.getType() + ":" + id;
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey){
            count= (Integer) redisTemplate.opsForValue().get(key);
        }else {
            redisTemplate.opsForValue().set(key,0L);
        }
        return  count;
    }

}



