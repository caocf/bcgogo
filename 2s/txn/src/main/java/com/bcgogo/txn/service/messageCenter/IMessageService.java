package com.bcgogo.txn.service.messageCenter;

import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.Result;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.PageException;
import com.bcgogo.remind.dto.message.MessageDTO;
import com.bcgogo.remind.dto.message.SearchMessageCondition;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-11-9
 * Time: 上午10:27
 * 消息中心-站内消息
 */
public interface IMessageService {
  /**
   * 保存消息
   *
   * @param messageDTO {
   *                   shopId:必须
   *                   userId:必须
   *                   }
   * @throws Exception
   */
  void saveMessage(MessageDTO messageDTO) throws Exception;

  /**
   * 删除发送站内信
   *
   * @param senderShopId 必须
   * @param senderUserId 必须
   * @param messageIds   必须
   */
  void deleteSenderPushMessage(Long senderShopId, Long senderUserId, Long... messageIds) throws BcgogoException;

  /**
   * 别人发给自己的
   *
   * @param id
   * @throws Exception
   */
  public MessageDTO getMessageById(Long id) throws Exception;

  Result sendPromotionMsg(Result result,MessageDTO messageDTO) throws Exception;

  void moveInviteAndNoticeAndMessageToPushMessage();

}
