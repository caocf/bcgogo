package com.bcgogo.txn.service.solr;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.OrderTypes;

import java.util.List;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 12-8-25
 * Time: 下午6:30
 * 该service 负责查找 txn 库中 各种单据  ,reindex 一些数据
 */
public interface IOrderSolrWriterService {
  /**
   * 根据原单据reindex
   *
   * @param shopDTO   ShopDTO
   * @param pageSize int
   * @throws Exception all
   */
  void reCreateOrderSolrIndexAll(ShopDTO shopDTO,OrderTypes orderType, int pageSize) throws Exception;

  /**
   * reindex order by orderId and order type
   *
   * @param shopDTO    SHopDTO
   * @param orderType OrderTypes
   * @param orderId   Long
   */
  void reCreateOrderSolrIndex(ShopDTO shopDTO, OrderTypes orderType, Long... orderId);

  void reCreateRepairServiceSolrIndex(Long shopId, int rows) throws Exception;

  void createRepairServiceSolrIndex(Long shopId, Set<Long> serviceIdList) throws Exception;

  void optimizeSolrOrderCore() throws Exception;
}
