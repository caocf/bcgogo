package com.bcgogo.rmi.service;

import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.SolrReindexJob;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.ISolrReindexJobService;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-8-23
 * Time: 上午11:14
 */
@Component
public class RmiSolrService implements IRmiSolrService {
  private static final Logger LOG = LoggerFactory.getLogger(RmiSolrService.class);

  private IOrderIndexService orderIndexService;
  private IOrderSolrWriterService orderSolrWriterService;
  private ISolrReindexJobService solrReindexJobService;
  private IProductSolrWriterService productSolrWriterService;
  private IConfigService configService;

  public IConfigService getConfigService() {
    if(configService == null){
      configService = ServiceManager.getService(IConfigService.class);
    }
    return configService;
  }

  public IOrderIndexService getOrderIndexService() {
    if(orderIndexService == null){
      orderIndexService = ServiceManager.getService(IOrderIndexService.class);
    }
    return orderIndexService;
  }

  public IOrderSolrWriterService getOrderSolrWriterService() {
    if(orderSolrWriterService == null){
      orderSolrWriterService = ServiceManager.getService(IOrderSolrWriterService.class);
    }
    return orderSolrWriterService;
  }

  public ISolrReindexJobService getSolrReindexJobService() {
    if(solrReindexJobService == null){
      solrReindexJobService = ServiceManager.getService(ISolrReindexJobService.class);
    }
    return solrReindexJobService;
  }

  public IProductSolrWriterService getProductSolrWriterService() {
    if(productSolrWriterService == null){
      productSolrWriterService = ServiceManager.getService(IProductSolrWriterService.class);
    }
    return productSolrWriterService;
  }

  @Override
  public void batchReCreateOrderSolrIndex(OrderTypes orderType, Long batchId, int pageSize) {
    LOG.info("batchRecreateOrderSolrIndex, 开始执行, batchId:{}", batchId);
    while(true){
      ShopDTO shopDTO = null;
      if(BcgogoConcurrentController.lock(ConcurrentScene.RMI_REINDEX, batchId)){
        SolrReindexJob reindexJob = getSolrReindexJobService().getTodoJobByBatchId(batchId);
        if(reindexJob == null){
          BcgogoConcurrentController.release(ConcurrentScene.RMI_REINDEX, batchId);
          break;
        }
        shopDTO = getConfigService().getShopById(reindexJob.getShopId());
        if(shopDTO == null){
          LOG.error("RmiSolrService.batchReCreateOrderSolrIndex, shopDTO为空！ shopID:{}, reindexJobId:{}", reindexJob.getShopId(), reindexJob.getId());
          getSolrReindexJobService().updateSolrReindexJobStatus(batchId, reindexJob.getShopId(), ExeStatus.EXCEPTION);
          continue;
        }
        getSolrReindexJobService().updateSolrReindexJobStatus(batchId, shopDTO.getId(), ExeStatus.START);
      }else{
        continue;
      }
      try{
        BcgogoConcurrentController.release(ConcurrentScene.RMI_REINDEX, batchId);
        getOrderIndexService().deleteOrderFromSolr(orderType, shopDTO.getId());
        getOrderSolrWriterService().reCreateOrderSolrIndexAll(shopDTO, orderType, pageSize);
        getSolrReindexJobService().updateSolrReindexJobStatus(batchId, shopDTO.getId(), ExeStatus.FINISHED);
      }catch(Throwable e){
        LOG.error("RmiSolrService.batchReCreateOrderSolrIndex error, shopId:{}, orderType:{}", shopDTO.getId(), orderType==null?"ALL":orderType.toString());
        LOG.error(e.getMessage(), e);
        getSolrReindexJobService().updateSolrReindexJobStatus(batchId, shopDTO.getId(), ExeStatus.EXCEPTION);
      }
    }
    LOG.info("batchReCreateOrderSolrIndex, 执行完成. 详情请查看solr_reindex_job表。 batchId:{}", batchId);
  }

  @Override
  public void batchReCreateProductSolrIndex(Long batchId, int pageSize) {
    LOG.info("batchReCreateProductSolrIndex, 开始执行, batchId:{}", batchId);
    while(true){
      ShopDTO shopDTO = null;
      if(BcgogoConcurrentController.lock(ConcurrentScene.RMI_REINDEX, batchId)){
        SolrReindexJob reindexJob = getSolrReindexJobService().getTodoJobByBatchId(batchId);
        if(reindexJob == null){
          BcgogoConcurrentController.release(ConcurrentScene.RMI_REINDEX, batchId);
          break;
        }
        shopDTO = getConfigService().getShopById(reindexJob.getShopId());
        if(shopDTO == null){
          LOG.error("RmiSolrService.batchReCreateProductSolrIndex, shopDTO为空！ shopID:{}, reindexJobId:{}", reindexJob.getShopId(), reindexJob.getId());
          getSolrReindexJobService().updateSolrReindexJobStatus(batchId, reindexJob.getShopId(), ExeStatus.EXCEPTION);
          continue;
        }
        getSolrReindexJobService().updateSolrReindexJobStatus(batchId, shopDTO.getId(), ExeStatus.START);
      }else{
        continue;
      }
      try{
        BcgogoConcurrentController.release(ConcurrentScene.RMI_REINDEX, batchId);
        getProductSolrWriterService().reCreateProductSolrIndex(shopDTO.getId(), pageSize);
        getSolrReindexJobService().updateSolrReindexJobStatus(batchId, shopDTO.getId(), ExeStatus.FINISHED);
      }catch(Throwable e){
        LOG.error("RmiSolrService.batchReCreateProductSolrIndex error, shopId:{}", shopDTO.getId());
        LOG.error(e.getMessage(), e);
        getSolrReindexJobService().updateSolrReindexJobStatus(batchId, shopDTO.getId(), ExeStatus.EXCEPTION);
      }
    }
    LOG.info("batchReCreateProductSolrIndex, 执行完成. 详情请查看solr_reindex_job表。 batchId:{}", batchId);
  }

  @Override
  public void batchReindexCustomerSupplier(Long batchId, int pageSize) {
    LOG.info("batchReindexCustomerSupplier, 开始执行, batchId:{}", batchId);
    while(true){
      ShopDTO shopDTO = null;
      if(BcgogoConcurrentController.lock(ConcurrentScene.RMI_REINDEX, batchId)){
        SolrReindexJob reindexJob = getSolrReindexJobService().getTodoJobByBatchId(batchId);
        if(reindexJob == null){
          BcgogoConcurrentController.release(ConcurrentScene.RMI_REINDEX, batchId);
          break;
        }
        shopDTO = getConfigService().getShopById(reindexJob.getShopId());
        if(shopDTO == null){
          LOG.error("RmiSolrService.batchReindexCustomerSupplier, shopDTO为空！ shopID:{}, reindexJobId:{}", reindexJob.getShopId(), reindexJob.getId());
          getSolrReindexJobService().updateSolrReindexJobStatus(batchId, reindexJob.getShopId(), ExeStatus.EXCEPTION);
          continue;
        }
        getSolrReindexJobService().updateSolrReindexJobStatus(batchId, shopDTO.getId(), ExeStatus.START);
      }else{
        continue;
      }
      try{
        BcgogoConcurrentController.release(ConcurrentScene.RMI_REINDEX, batchId);
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerSupplierIndexList(shopDTO.getId(), pageSize);
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexOtherContactIndexList(shopDTO.getId(),pageSize);
        getSolrReindexJobService().updateSolrReindexJobStatus(batchId, shopDTO.getId(), ExeStatus.FINISHED);
      }catch(Throwable e){
        LOG.error("RmiSolrService.batchReindexCustomerSupplier error, shopId:{}", shopDTO.getId());
        LOG.error(e.getMessage(), e);
        getSolrReindexJobService().updateSolrReindexJobStatus(batchId, shopDTO.getId(), ExeStatus.EXCEPTION);
      }
    }
    LOG.info("batchReindexCustomerSupplier, 执行完成. 详情请查看solr_reindex_job表。 batchId:{}", batchId);
  }
}
