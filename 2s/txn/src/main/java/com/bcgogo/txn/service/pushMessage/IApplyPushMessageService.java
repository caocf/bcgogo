package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;

import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 13-6-20
 * Time: 上午10:39
 */
public interface IApplyPushMessageService {
  void createApplyRelatedPushMessage(Long originShopId, Long invitedShopId, Long sourceId,Long invitedTime, PushMessageSourceType sourceType) throws Exception;

  Set<Long> getApplyPushMessageShopIds(Long pushMessageId) throws Exception;

  //批量匹配推荐逻辑 目前是 单个匹配推荐
  void addCancelRecommendAssociatedCount(Long pushMessageId) throws Exception;

  void cancelApplyRecommendAssociated(Long pushMessageId) throws Exception;

  /**
   * 一条一条推匹配消息
   * @param shopDTO
   * @param shopVersionId
   * @throws Exception
   */
  void createSingleMobileAutoMatchApplyRelatedMessage(ShopDTO shopDTO, Long shopVersionId) throws Exception;

}
