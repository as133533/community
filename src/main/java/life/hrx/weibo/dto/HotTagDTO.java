
package life.hrx.weibo.dto;

import lombok.Data;

/**
 * PriorityQueue中的排序对象必须要实现Comparable接口进行排序
 */
@Data
public class HotTagDTO implements Comparable {
    private String name;
    private Integer priority;

    @Override
    public int compareTo(Object o) {
        return this.getPriority() - ((HotTagDTO) o).getPriority();
    }
}