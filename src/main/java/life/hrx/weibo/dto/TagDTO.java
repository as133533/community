package life.hrx.weibo.dto;


import lombok.Data;

import java.util.List;

@Data
public class TagDTO {
    private String categoryName;//用来表示标签仓库的名字,即父标签下有很多供选择的子标签
    private List<String> tags;//表示可供选择的标签

}
