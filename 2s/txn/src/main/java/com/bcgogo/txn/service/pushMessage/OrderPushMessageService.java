package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.constant.pushMessage.ClientConstant;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.txn.pushMessage.PushMessageLevel;
import com.bcgogo.enums.txn.pushMessage.PushMessageReceiverStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageReceiverDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageSourceDTO;
import com.bcgogo.txn.model.PurchaseOrder;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-6-20
 * Time: 上午10:56
 */
@Component
public class OrderPushMessageService implements IOrderPushMessageService {
  private static final Logger LOG = LoggerFactory.getLogger(ApplyPushMessageService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public void createOrderPushMessageMessage(Long sourceShopId, Long pushShopId, Long sourceId, PushMessageSourceType sourceType) throws Exception {
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    if (sourceId == null) return;
    if (disabledObsoletePushMessage(sourceId, sourceType, pushMessageService)) return;
    IShopService shopService = ServiceManager.getService(IShopService.class);
    Map<Long, ShopDTO> shopDTOMap = shopService.getShopByShopIds(sourceShopId, pushShopId);
    ShopDTO sourceShop = shopDTOMap.get(sourceShopId);
    ShopDTO invitedShop = shopDTOMap.get(pushShopId);
    if (sourceShop == null || invitedShop == null) {
      LOG.error("get shop by ids[{},{}] is null.", sourceShop, invitedShop);
      return;
    }
    sourceShop.setAreaName(ServiceManager.getService(IConfigService.class).getShopAreaInfoByShopDTO(sourceShop));
    invitedShop.setAreaName(ServiceManager.getService(IConfigService.class).getShopAreaInfoByShopDTO(invitedShop));
    PushMessageDTO pushMessageDTO = new PushMessageDTO();
    pushMessageDTO.setShopId(sourceShop.getId());
    pushMessageDTO.setCreatorType(OperatorType.SHOP);
    pushMessageDTO.setCreatorId(pushMessageDTO.getShopId());
    pushMessageDTO.setCreateTime(System.currentTimeMillis());
    pushMessageDTO.setEndDate(ConfigUtils.getPushMessageLifeCycle(pushMessageDTO.getCreateTime()));
    pushMessageDTO.setLevel(PushMessageLevel.HIGH);
    pushMessageDTO.setType(PushMessageSourceType.getPushMessageType(sourceType));
    pushMessageDTO.setPromptContent(ClientConstant.getContactByPushMessageSourceType(sourceType));

    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setShopId(invitedShop.getId());
    pushMessageReceiverDTO.setReceiverId(pushMessageReceiverDTO.getShopId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
    pushMessageReceiverDTO.setShopKind(invitedShop.getShopKind());
    pushMessageDTO.setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(sourceId);
    pushMessageSourceDTO.setShopId(sourceShop.getId());
    pushMessageSourceDTO.setType(sourceType);
    pushMessageDTO.setPushMessageSourceDTO(pushMessageSourceDTO);

    pushMessageService.createPushMessage(pushMessageDTO,false);

  }

  private boolean disabledObsoletePushMessage(Long sourceId, PushMessageSourceType sourceType, IPushMessageService pushMessageService) throws Exception {
    //如果是拒绝或发货
    if (PushMessageSourceType.PURCHASE_SELLER_STOCK == sourceType || PushMessageSourceType.PURCHASE_SELLER_REFUSED == sourceType) {
      pushMessageService.disabledPushMessageReceiverBySourceId(null, sourceId,null, PushMessageSourceType.SALE_NEW);
    }
    if (PushMessageSourceType.PURCHASE_SELLER_DISPATCH == sourceType) {
      pushMessageService.disabledPushMessageReceiverBySourceId(null, sourceId,null, PushMessageSourceType.PURCHASE_SELLER_STOCK, PushMessageSourceType.SALE_NEW);
      //判断买家是否入库 入库不推送
      if (pushMessageVerifier(sourceId)) return true;
    }
    return false;
  }

  private boolean pushMessageVerifier(Long sourceId) {
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseOrder order = writer.getById(PurchaseOrder.class, sourceId);
    return !(order == null || OrderStatus.PURCHASE_ORDER_DONE != order.getStatusEnum());
  }

}
