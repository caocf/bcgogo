package com.bcgogo.config.service;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.SolrReindexJob;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.OrderTypes;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-8-20
 * Time: 下午4:23
 */
public interface ISolrReindexJobService {
  /**
   *
   * @param shopDTOs
   * @param reindexType
   * @param orderType
   * @return batchId
   */
  Long createSolrReindexJobs(List<ShopDTO> shopDTOs, String reindexType, OrderTypes orderType) throws Exception;

  void updateSolrReindexJobStatus(Long batchId, Long shopId, ExeStatus newStatus);

  void getFailedSolrReindexJob(Long batchId, String reindexType);

  SolrReindexJob getTodoJobByBatchId(Long batchId);
}
