package com.didiglobal.knowframework.security.service.impl;

import com.didiglobal.knowframework.security.common.entity.Message;
import com.didiglobal.knowframework.security.dao.MessageDao;
import com.didiglobal.knowframework.security.service.MessageService;
import com.didiglobal.knowframework.security.service.UserService;
import com.didiglobal.knowframework.security.util.CopyBeanUtil;
import com.didiglobal.knowframework.security.common.dto.message.MessageDTO;
import com.didiglobal.knowframework.security.common.entity.user.User;
import com.didiglobal.knowframework.security.common.vo.message.MessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjm
 */
@Service("kfSecurityMessageServiceImpl")
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private UserService userService;

    @Override
    public void saveMessage(MessageDTO messageDTO) {
        Message message = CopyBeanUtil.copy(messageDTO, Message.class);
        messageDao.insert(message);
    }

    @Override
    public List<MessageVO> getMessageListByUserIdAndReadTag(String userName, Boolean readTag) {
        User user = userService.getUserByUserName(userName);

        List<Message> messageList = messageDao.selectListByUserIdAndReadTag(user.getId(), readTag);

        List<MessageVO> result = new ArrayList<>();
        for(Message message : messageList) {
            MessageVO messageVO = CopyBeanUtil.copy(message, MessageVO.class);
            messageVO.setCreateTime(message.getCreateTime().getTime());
            result.add(messageVO);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeMessageStatus(List<Integer> messageIdList) {
        if(CollectionUtils.isEmpty(messageIdList)) {
            return;
        }
        List<Message> messageList = messageDao.selectListByMessageIdList(messageIdList);
        for(Message message : messageList) {
            // 反转已读状态
            message.setReadTag(!message.getReadTag());
            messageDao.update(message);
        }
    }

    @Override
    public void saveMessages(List<MessageDTO> messageDTOList) {
        if(CollectionUtils.isEmpty(messageDTOList)) {
            return;
        }
        List<Message> messageList = CopyBeanUtil.copyList(messageDTOList, Message.class);
        messageDao.insertBatch(messageList);
    }
}
