package com.bcgogo.schedule.bean;

import com.bcgogo.common.Result;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.enums.txn.pushMessage.PushMessageScene;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.product.model.Promotions;
import com.bcgogo.product.model.PromotionsProduct;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.threadPool.OrderThreadPool;
import com.bcgogo.txn.dto.PromotionIndex;
import com.bcgogo.txn.dto.PromotionMsgJobDTO;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.PromotionsProductDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageBuildTaskDTO;
import com.bcgogo.txn.service.messageCenter.IMessageService;
import com.bcgogo.txn.service.pushMessage.ITradePushMessageService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-7-17
 * Time: 下午10:12
 * To change this template use File | Settings | File Templates.
 */
public class PromotionsCheckSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(PromotionsCheckSchedule.class);
  private static boolean lock = false;

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }

  /**
   *  清除solr中过期的促销信息
   * @throws Exception
   */
  private void doDeleteExpiredPromotions() throws Exception {
    IPromotionsService promotionsService= ServiceManager.getService(IPromotionsService.class);
    IProductSolrWriterService productSolrWriterService=ServiceManager.getService(IProductSolrWriterService.class);
    PromotionIndex condition=new PromotionIndex();
    condition.setStatus(PromotionsEnum.PromotionStatus.USING); //之前处在进行中
    List<PromotionsDTO> promotionsDTOs=promotionsService.getPromotionsDTO(condition);
    if(CollectionUtil.isNotEmpty(promotionsDTOs)){
      List<Long> expirePromotionIds=new ArrayList<Long>();
      Map<Long,List<Long>> expireMap=new HashMap<Long,List<Long>>();
      Long now=System.currentTimeMillis();
      for(PromotionsDTO promotionsDTO:promotionsDTOs){
        Long endTime= promotionsDTO.getEndTime();
        if(endTime==null){
          continue;
        }
        if(now>endTime){ //促销已过期
          List<Long> promotionIds=expireMap.get(promotionsDTO.getId());
          if(promotionIds==null){
            promotionIds=new ArrayList<Long>();
            expireMap.put(promotionsDTO.getShopId(),promotionIds);
          }
          promotionIds.add(promotionsDTO.getId());
          expirePromotionIds.add(promotionsDTO.getId());
        }
      }
      condition.setStatus(PromotionsEnum.PromotionStatus.UN_USED); //未使用的过期的也要更新
      promotionsDTOs=promotionsService.getPromotionsDTO(condition);
      if(CollectionUtil.isNotEmpty(promotionsDTOs)){
        for(PromotionsDTO promotionsDTO:promotionsDTOs){
          Long endTime= promotionsDTO.getEndTime();
          if(endTime==null){
            continue;
          }
          if(now>endTime){
            expirePromotionIds.add(promotionsDTO.getId());
          }
        }
      }
      if(CollectionUtil.isNotEmpty(expirePromotionIds)){
        Result result=new Result();
//          promotionsService.batchUpdatePromotionStatus(result, expirePromotionIds, PromotionsEnum.PromotionStatus.EXPIRE);
        List<Long> productIds=null;
        for(Long shopId:expireMap.keySet()){
          List<Long> promotionIds=expireMap.get(shopId);
          List<PromotionsProductDTO> promotionsProductDTOs=promotionsService.getPromotionsProductDTOByPromotionsId(shopId,ArrayUtil.toLongArr(promotionIds));
          if(CollectionUtil.isEmpty(promotionsProductDTOs)){
            continue;
          }
          productIds=new ArrayList<Long>();
          for(PromotionsProductDTO pp:promotionsProductDTOs){
            if(productIds.contains(pp.getProductLocalInfoId())){
              continue;
            }
            productIds.add(pp.getProductLocalInfoId());
          }
          promotionsService.handleExpirePromotions(result,shopId,promotionIds);
          productSolrWriterService.createProductSolrIndex(shopId, ArrayUtil.toLongArr(productIds));
        }
      }
    }
  }

  /**
   * 检测有没有要开始的促销
   * @throws Exception
   */
  private void doStartPromotions() throws Exception {
    IPromotionsService promotionsService= ServiceManager.getService(IPromotionsService.class);
    IProductSolrWriterService productSolrWriterService=ServiceManager.getService(IProductSolrWriterService.class);
    PromotionIndex condition=new PromotionIndex();
    condition.setStatus(PromotionsEnum.PromotionStatus.UN_STARTED);
    List<PromotionsDTO> promotionsDTOs=promotionsService.getPromotionsDTO(condition);
    if(CollectionUtil.isEmpty(promotionsDTOs)){
      return;
    }
    List<Long> startPromotionIds=new ArrayList<Long>();
    Map<Long,List<Long>> startMap=new HashMap<Long,List<Long>>();
    Long now=System.currentTimeMillis();
    for(PromotionsDTO promotionsDTO:promotionsDTOs){
      if(PromotionsEnum.PromotionStatus.EXPIRE.equals(promotionsDTO.getStatus())){
        continue;
      }
      Long startTime= promotionsDTO.getStartTime();
      if(now>startTime){ //促销已开始
        List<Long> promotionIds=startMap.get(promotionsDTO.getId());
        if(promotionIds==null){
          promotionIds=new ArrayList<Long>();
          startMap.put(promotionsDTO.getShopId(),promotionIds);
        }
        promotionIds.add(promotionsDTO.getId());
        startPromotionIds.add(promotionsDTO.getId());
      }
    }

    if(CollectionUtil.isNotEmpty(startPromotionIds)){
      Result result=new Result();
      promotionsService.batchUpdatePromotionStatus(result, startPromotionIds, PromotionsEnum.PromotionStatus.USING);
      List<Long> productIds=null;
      for(Long key:startMap.keySet()){
        List<Long> promotionIds=startMap.get(key);
        List<PromotionsProductDTO> promotionsProductDTOs=promotionsService.getPromotionsProductDTOByPromotionsId(key, promotionIds.toArray(new Long[promotionIds.size()]));
        if(CollectionUtil.isEmpty(promotionsProductDTOs)){
          continue;
        }
        productIds=new ArrayList<Long>();
        List<PushMessageBuildTaskDTO> pushMessageBuildTaskDTOList = new ArrayList<PushMessageBuildTaskDTO>();
        PushMessageBuildTaskDTO pushMessageBuildTaskDTO = null;
        for(PromotionsProductDTO dto:promotionsProductDTOs){
          productIds.add(dto.getProductLocalInfoId());
          pushMessageBuildTaskDTO = new PushMessageBuildTaskDTO();
          pushMessageBuildTaskDTO.setSeedId(dto.getProductLocalInfoId());
          pushMessageBuildTaskDTO.setShopId(dto.getShopId());
          pushMessageBuildTaskDTO.setScene(PushMessageScene.ACCESSORY_PROMOTIONS);
          pushMessageBuildTaskDTO.setCreateTime(System.currentTimeMillis());
          pushMessageBuildTaskDTOList.add(pushMessageBuildTaskDTO);
        }
        productSolrWriterService.createProductSolrIndex(key, productIds.toArray(new Long[productIds.size()]));
        ServiceManager.getService(ITradePushMessageService.class).savePushMessageBuildTaskDTO(pushMessageBuildTaskDTOList.toArray(new PushMessageBuildTaskDTO[pushMessageBuildTaskDTOList.size()]));
      }
    }
  }

  /**
   * 检测促销状态，没有商品的促销变成未使用状态
   */
  private void doResetPromotionsStatus() throws Exception {
    IPromotionsService promotionsService= ServiceManager.getService(IPromotionsService.class);
    PromotionIndex condition=new PromotionIndex();
//    condition.setStatus(PromotionsEnum.PromotionStatus.USING);
    PromotionsEnum.PromotionStatus[] promotionStatusList=new PromotionsEnum.PromotionStatus[]{
      PromotionsEnum.PromotionStatus.USING,
      PromotionsEnum.PromotionStatus.UN_STARTED
    };
    condition.setPromotionStatusList(promotionStatusList);
    List<PromotionsDTO> promotionsDTOs=promotionsService.getPromotionsDTO(condition);
    if(CollectionUtil.isEmpty(promotionsDTOs)){
      return;
    }
    Result result=new Result();
     List<PromotionsProductDTO> promotionsProductDTOs=null;
    for(PromotionsDTO promotionsDTO:promotionsDTOs){
      promotionsProductDTOs=promotionsService.getPromotionsProductDTOByPromotionsId(promotionsDTO.getShopId(),promotionsDTO.getId());
      if(CollectionUtil.isEmpty(promotionsProductDTOs)){
        promotionsDTO.setStatus(PromotionsEnum.PromotionStatus.UN_USED);
        promotionsService.updatePromotionStatus(result,promotionsDTO);
      }
    }
  }

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (isLock()) {
      return;
    }
    try{
      doDeleteExpiredPromotions();
      doStartPromotions();
      doResetPromotionsStatus();
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }finally {
      lock = false;
    }
  }
}
