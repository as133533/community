package life.hrx.weibo.service;


import life.hrx.weibo.security.auth.myuserdetails.MyUserDetails;
import life.hrx.weibo.dto.NotificationDTO;
import life.hrx.weibo.dto.PaginationDTO;
import life.hrx.weibo.enums.NotificationStatusEnum;
import life.hrx.weibo.enums.NotificationTypeEnum;
import life.hrx.weibo.exception.CustomizeErrorCode;
import life.hrx.weibo.exception.CustomizeException;
import life.hrx.weibo.mapper.CommentMapper;
import life.hrx.weibo.mapper.NotificationMapper;
import life.hrx.weibo.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired(required = false)
    private NotificationMapper notificationMapper;

    @Autowired(required = false)
    private CommentMapper commentMapper;

    //个人通知分页公共方法
    private PaginationDTO<NotificationDTO> notificationDTOPaginationDTO(Integer page, Integer size, NotificationExample notificationExample) {
        Integer offset = page < 0 ? 0 : (page - 1) * size;//计算offset
        Integer Count = (int) notificationMapper.countByExample(notificationExample);//全部的信息数

        //当前页要显示的信息
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample, new RowBounds(offset, size));
        Integer totalCount;//计算总页数
        if (Count % size == 0) {
            totalCount = Count / size;
        } else {
            totalCount = Count / size + 1;
        }
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setPagination(totalCount, page);//设置分页条显示
        List<NotificationDTO> notificationDTOS = notifications.stream().map(notification ->
        {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            if (notification.getType()== NotificationTypeEnum.QUESTION.getType()){
                notificationDTO.setTypeName("回复了问题");
            }else {
                notificationDTO.setTypeName("回复了评论");
            }
            return notificationDTO;
        }).collect(Collectors.toList());//让questions列表中的每一个都变成已经设置号的questionDTO返回

        paginationDTO.setData(notificationDTOS);

        return paginationDTO;

    }

    public PaginationDTO<NotificationDTO> paginationByUserComment(Long id, Integer page, Integer size) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(id);
        PaginationDTO<NotificationDTO> paginationDTO = notificationDTOPaginationDTO(page, size, notificationExample);
        return paginationDTO;
    }

    public NotificationDTO read(Long id, MyUserDetails user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification==null){
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!Objects.equals(notification.getReceiver(), user.getId())){
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        if (Objects.equals(notificationDTO.getType(), NotificationTypeEnum.QUESTION.getType())){
            notificationDTO.setTypeName("回复了问题");
        }else {
            notificationDTO.setTypeName("回复了评论");
        }
        return notificationDTO;

    }

    public Long unreadcount(Long id) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(id).andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        long count = notificationMapper.countByExample(notificationExample);
        return count;
    }
}
