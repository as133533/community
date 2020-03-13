package life.hrx.weibo.schedule;

import life.hrx.weibo.cache.HotTagCache;
import life.hrx.weibo.mapper.QuestionMapper;
import life.hrx.weibo.model.Question;
import life.hrx.weibo.model.QuestionExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class HotTagTasks {

    @Autowired
    private QuestionMapper questionMapper;

    //权重单例类
    @Autowired
    private HotTagCache hotTagCache;


    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void hotTagSchedule(){

        int offset=0;
        int limit=20;
        List<Question> list =new ArrayList<>();

        Map<String,Integer> priorities=new HashMap<>();

        //这里权重的设计是 遍历每个问题得到标签数量，使用Map数据结构存储各个标签的权重值，权重值的计算方式为 5*含有该标签的问题数量+该标签含有的所有问题的评论数总和。
        //最后需要使用topN,即排序后取前n个大的数，topN问题就是堆排序思想

        while (offset ==0 || list.size() == limit){
            list = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, limit));
            for (Question question:list){
                String[] tags = StringUtils.split(question.getTag(), ","); //从问题中获得每个问题的标签
                for (String tag:tags){
                    Integer priority = priorities.get(tag); //获得我们存储结构中存储的某个tag的权重
                    if (priority !=null){ //如果结构中tag存在，那么就更新权重
                        priorities.put(tag,priority+5+question.getCommentCount());
                    }else{ //否则该权重就是初始的权重
                        priorities.put(tag,5+question.getCommentCount());
                    }
                }
            }
            offset +=limit;
        }
        hotTagCache.updateTags(priorities);
    }
}
