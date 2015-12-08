package com.bcgogo.rmi.service;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.OrderTypes;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-8-23
 * Time: 上午11:14
 */
  public interface IRmiSolrService {

  void batchReCreateOrderSolrIndex(OrderTypes orderType, Long finalBatchId, int pageSize);

  void batchReCreateProductSolrIndex(Long finalBatchId, int pageSize);

  void batchReindexCustomerSupplier(Long finalBatchId, int pageSize);
}
