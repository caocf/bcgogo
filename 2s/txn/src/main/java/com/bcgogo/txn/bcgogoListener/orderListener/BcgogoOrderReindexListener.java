package com.bcgogo.txn.bcgogoListener.orderListener;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.BcgogoOrderReindexEvent;
import com.bcgogo.txn.dto.BcgogoOrderDto;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用作一些特殊单据reindex使用（不计入营业统计和流水统计的单据）
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-7
 * Time: 上午10:56
 * To change this template use File | Settings | File Templates.
 */
public class BcgogoOrderReindexListener extends OrderSavedListener {
  public static final Logger LOG = LoggerFactory.getLogger(BcgogoOrderReindexListener.class);

  private BcgogoOrderReindexEvent bcgogoOrderReindexEvent;

  public BcgogoOrderReindexEvent getBcgogoOrderReindexEvent() {
    return bcgogoOrderReindexEvent;
  }

  public void setBcgogoOrderReindexEvent(BcgogoOrderReindexEvent bcgogoOrderReindexEvent) {
    this.bcgogoOrderReindexEvent = bcgogoOrderReindexEvent;
  }

  public BcgogoOrderReindexListener(BcgogoOrderReindexEvent bcgogoOrderReindexEvent) {
    super();
    this.bcgogoOrderReindexEvent = bcgogoOrderReindexEvent;
  }

  public void run() {
    if (bcgogoOrderReindexEvent == null) {
      LOG.error("BcgogoOrderReindexListener.run bcgogoOrderReindexEvent null");
      return;
    }

    BcgogoOrderDto bcgogoOrderDto = bcgogoOrderReindexEvent.getBcgogoOrderDto();
    OrderTypes orderType = bcgogoOrderReindexEvent.getOrderType();

    if (orderType == null || bcgogoOrderDto == null) {
      LOG.error("BcgogoOrderReindexListener.run orderType null or bcgogoOrderDto null");
      LOG.error("orderType:" + orderType + ",bcgogoOrderDto:" + JsonUtil.objectToJson(bcgogoOrderDto));
      return;
    }
    LOG.debug("orderType:" + orderType+"单据索引开始!");

    ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(bcgogoOrderDto.getShopId()), orderType, bcgogoOrderDto.getId());
  }
}
