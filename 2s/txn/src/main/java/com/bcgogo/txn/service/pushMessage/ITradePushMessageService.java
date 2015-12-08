package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.txn.pushMessage.PushMessageScene;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageBuildTaskDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageFeedbackRecordDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageReceiverDTO;

import java.util.List;
import java.util.Map;

/**
 * 配件、求购、报价、报价未采纳
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-1
 * Time: 上午10:44
 */
public interface ITradePushMessageService {

  PushMessageBuildTaskDTO getLatestPushMessageBuildTaskDTO(PushMessageScene... scene) throws Exception;

  void savePushMessageBuildTaskDTO(PushMessageBuildTaskDTO... pushMessageBuildTaskDTO) throws Exception;

  void updatePushMessageBuildTask(PushMessageBuildTaskDTO pushMessageBuildTaskDTO) throws Exception;

  void filterCustomMatchAccessoryList(double productMatchScale, double productPriceScale, double productAreaScale, ProductSearchResultListDTO searchResultListDTO, SearchConditionDTO searchConditionDTO, ShopDTO seedShopDTO, Map<Long, ShopDTO> shopDTOMap) throws Exception;

  void processPushMessageBuildTask() throws Exception;

  void createQuotedOrderIgnoredPushMessage(Long sourceShopId, Long pushShopId, Long sourceId) throws Exception;

  boolean generatePromotionMsgTask(Long shopId, Long... productIdArr);

  boolean generateSalesMsgTask(Long shopId, Long... productIdArr);

}
