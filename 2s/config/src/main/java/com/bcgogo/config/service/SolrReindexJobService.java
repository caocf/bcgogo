package com.bcgogo.config.service;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.SolrReindexJob;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.utils.ConfigConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-8-20
 * Time: 下午4:23
 */
@Component
public class SolrReindexJobService implements ISolrReindexJobService {
  private static final Logger LOG = LoggerFactory.getLogger(SolrReindexJobService.class);

  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public Long createSolrReindexJobs(List<ShopDTO> shopDTOs, String reindexType, OrderTypes orderType) throws Exception{
    if(CollectionUtils.isEmpty(shopDTOs)){
      return null;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    Long batchId = null;
    Date createTime = new Date();
    try{
      for(ShopDTO shopDTO : shopDTOs){
        SolrReindexJob solrReindexJob = new SolrReindexJob(shopDTO.getId(), reindexType, batchId, ExeStatus.READY, createTime, orderType);
        writer.save(solrReindexJob);
        if(batchId == null){
          batchId = solrReindexJob.getId();
          solrReindexJob.setBatchId(batchId);
          writer.update(solrReindexJob);
        }
      }
      writer.commit(status);
    }finally{
      writer.rollback(status);
    }
    return batchId;
  }

  @Override
  public void updateSolrReindexJobStatus(Long batchId, Long shopId, ExeStatus newStatus) {
    ConfigWriter writer = configDaoManager.getWriter();
    SolrReindexJob solrReindexJob = writer.getSolrReindexJobByBatchIdShopId(batchId, shopId);
    if(solrReindexJob == null){
      LOG.error("updateReindexJobStatus出错！ reindexJob不存在！ batchId:{}, shopId:{}", batchId, shopId);
      return;
    }
    solrReindexJob.setExeStatus(newStatus);
    Object status = writer.begin();
    try{
      if(newStatus == ExeStatus.START){
        solrReindexJob.setStartTime(new Date());
        String rmiIp = System.getProperty(ConfigConstant.CONFIG_RMI_SERVER_HOST);
        if(StringUtils.isBlank(rmiIp)){
          rmiIp = System.getProperty("node");
        }
        solrReindexJob.setExecutor(rmiIp);
      }
      if(newStatus == ExeStatus.FINISHED){
        solrReindexJob.setFinishTime(new Date());
      }
      writer.update(solrReindexJob);
      writer.commit(status);
    }catch(Exception e){
      LOG.error("updateReindexJobStatus出错！ batchId:{}, shopId:{}", batchId, shopId);
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  @Override
  public void getFailedSolrReindexJob(Long batchId, String reindexType) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<SolrReindexJob> solrReindexJobs = writer.getFailedSolrReindexJob(batchId, reindexType);
  }

  @Override
  public SolrReindexJob getTodoJobByBatchId(Long batchId) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getTodoJobByBatchId(batchId);
  }
}
