package life.hrx.weibo.cache;

import life.hrx.weibo.dto.HotTagDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * 进行top N计算
 */

@Component
@Data
public class HotTagCache {
    private List<String> hots = new ArrayList<>();

    public void updateTags(Map<String, Integer> tags) {
        int max = 10;
        PriorityQueue<HotTagDTO> priorityQueue = new PriorityQueue<>(max);

        tags.forEach((name, priority) -> {
            HotTagDTO hotTagDTO = new HotTagDTO();
            hotTagDTO.setName(name);
            hotTagDTO.setPriority(priority);
            if (priorityQueue.size() < max) { //无论如何都先放入10个数
                priorityQueue.add(hotTagDTO);
            } else {
                HotTagDTO minHot = priorityQueue.peek(); //取出其中最小的数
                if (hotTagDTO.compareTo(minHot) > 0) { //如果比最小的要大，则删除原先最小的数，加入这个数
                    priorityQueue.poll();
                    priorityQueue.add(hotTagDTO);
                }
            }
        });



        //下面需要将排序好的Queue中的弹出，存到List中，因为前端的遍历操作只能在list中进行，queue不支持
        List<String> sortedTags = new ArrayList<>();

        HotTagDTO poll = priorityQueue.poll();
        while (poll != null) {
            sortedTags.add(0, poll.getName());
            poll = priorityQueue.poll();
        }
        hots = sortedTags;
    }
}