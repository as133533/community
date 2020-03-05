package life.hrx.weibo.dto;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class PaginationDTO<T> {//这里分页的设计最好设计成泛型类，因为可能多个数据需要进行分页
    private List<T> data;//需要被分页的数据
    private boolean showPrevious; //是否显示上一页
    private boolean showFirstPage; //是否显示首页
    private boolean showNext; //是否显示下一页
    private boolean showEndPage; //是否显示尾页
    private Integer page;//当前页数
    private Integer totalPage;//总共的页数，也用来表示最后一页
    private List<Integer> pages=new ArrayList<>();//当前栏中的条数,记住这里要初始化对象

    //用来设置上述的值
    public void setPagination(Integer totalPage,Integer page_request){
        page=page_request;
        pages.add(page_request);
        Integer next_page=page_request+1;
        Integer prv_page=page_request-1;

        if (totalPage!=0) {
            for (int i = 0; i < 3; i++) {//只往后或往前显示三页
                if (next_page <= totalPage) {//最后一页一定不能大于总页数
                    pages.add(next_page);
                }
                if (1<=prv_page && prv_page<page_request ) {//上一页一定要小于当前页数，并且要大于等于1
                    pages.add(0, prv_page);
                }
                next_page++;
                prv_page--;
            }
            showPrevious= page != 1;//只要当前页不等于1，那么就显示第一页
            showFirstPage= !pages.contains(1); //只要列表中包含1，那么就不显示首页
            showNext= !Objects.equals(page, totalPage);//只要当前页不在最后一页，那么就显示下一页
            showEndPage=!pages.contains(totalPage);//只要当前页不包含最后一页，那么就显示最后一页

        }else {
            //如果totalPage为0的情况下，就不显示所有
            showEndPage=false;
            showFirstPage=false;
            showNext=false;
            showPrevious=false;
        }

    }

}
