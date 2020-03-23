package life.hrx.weibo.dto;


import lombok.Data;

/**
 * 问题搜索对象。用于传递
 */

@Data
public class QuestionQueryDTO {
    private String search; //在数据中的SQL正则，只是将搜索替换内容中的空格替换成|
    private String tag; //用于在热门话题上，我们点击热门话题的时候，将标签当成搜索内容引入
    private Integer offset; //当前页数
    private Integer size; //分页大小
    private String sort;

}
