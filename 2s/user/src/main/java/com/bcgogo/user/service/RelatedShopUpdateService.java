package com.bcgogo.user.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopBusinessScopeDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.RelatedShopUpdateLogDTO;
import com.bcgogo.user.dto.RelatedShopUpdateTaskDTO;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.Supplier;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.task.RelatedShopUpdateLog;
import com.bcgogo.user.model.task.RelatedShopUpdateTask;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-8-8
 * Time: 下午3:13
 */
@Component
public class RelatedShopUpdateService implements IRelatedShopUpdateService {
  private static final Logger LOG = LoggerFactory.getLogger(RelatedShopUpdateService.class);

  @Autowired
  private UserDaoManager userDaoManager;

  //店铺更新消息的时候判断是否要生成Task
  @Override
  public boolean isNeedToCreateTask(ShopDTO lastShopDTO, ShopDTO newShopDTO) {
    if (lastShopDTO == null || newShopDTO == null) {
      return true;
    }
    if (!StringUtil.compareSame(lastShopDTO.getName(), newShopDTO.getName())) {
      return true;
    }
    if (!StringUtil.compareSame(lastShopDTO.getShortname(), newShopDTO.getShortname())) {
      return true;
    }
    if (!StringUtil.compareSame(lastShopDTO.getAddress(), newShopDTO.getAddress())) {
      return true;
    }
    if (!StringUtil.compareSame(lastShopDTO.getLandline(), newShopDTO.getLandline())) {
      return true;
    }
    if (!StringUtil.compareSame(lastShopDTO.getProvince(), newShopDTO.getProvince())) {
      return true;
    }
    if (!StringUtil.compareSame(lastShopDTO.getCity(), newShopDTO.getCity())) {
      return true;
    }
    if (!StringUtil.compareSame(lastShopDTO.getArea(), newShopDTO.getArea())) {
      return true;
    }
    Set<Long> lastShopBusinessScopeProductCategoryIds = lastShopDTO.getProductCategoryIds();
    Set<Long> newShopBusinessScopeProductCategoryIds = newShopDTO.getProductCategoryIds();
    if (lastShopBusinessScopeProductCategoryIds == null) {
      lastShopBusinessScopeProductCategoryIds = new HashSet<Long>();
    }
    if (newShopBusinessScopeProductCategoryIds == null) {
      newShopBusinessScopeProductCategoryIds = new HashSet<Long>();
    }
    if (!CollectionUtils.isEqualCollection(lastShopBusinessScopeProductCategoryIds, newShopBusinessScopeProductCategoryIds)) {
      return true;
    }
    return false;
  }

  //创建Task 如果有等待执行的更新create时间
  @Override
  public RelatedShopUpdateTaskDTO createRelatedShopUpdateTask(Long shopId) throws Exception {
    if (shopId == null) {
      return null;
    }
    RelatedShopUpdateTaskDTO relatedShopUpdateTaskDTO = null;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<RelatedShopUpdateTask> relatedShopUpdateTasks = writer.getRelatedShopUpdateTaskByShopId(shopId, ExeStatus.READY);
      if (CollectionUtils.isEmpty(relatedShopUpdateTasks)) {
        relatedShopUpdateTaskDTO = new RelatedShopUpdateTaskDTO();
        relatedShopUpdateTaskDTO.setShopId(shopId);
        relatedShopUpdateTaskDTO.setExeStatus(ExeStatus.READY);
        relatedShopUpdateTaskDTO.setCreatedTime(System.currentTimeMillis());
        RelatedShopUpdateTask relatedShopUpdateTask = new RelatedShopUpdateTask();
        relatedShopUpdateTask.fromDTO(relatedShopUpdateTaskDTO);
        writer.save(relatedShopUpdateTask);
        relatedShopUpdateTaskDTO.setId(relatedShopUpdateTask.getId());
        writer.commit(status);
      } else {
        relatedShopUpdateTaskDTO = CollectionUtil.getFirst(relatedShopUpdateTasks).toDTO();
      }
    } finally {
      writer.rollback(status);
    }
    return relatedShopUpdateTaskDTO;
  }

  @Override
  public RelatedShopUpdateTaskDTO getFirstRelatedShopUpdateTaskDTO(ExeStatus exeStatus) {
    RelatedShopUpdateTask relatedShopUpdateTask = CollectionUtil.getFirst(
        userDaoManager.getWriter().getFirstRelatedShopUpdateTask(exeStatus));
    if(relatedShopUpdateTask != null){
      return relatedShopUpdateTask.toDTO();
    }
    return null;
  }

  @Override
  public void updateRelatedShopUpdateTaskStatus(RelatedShopUpdateTaskDTO relatedShopUpdateTaskDTO, ExeStatus exeStatus) {
    if (relatedShopUpdateTaskDTO == null || relatedShopUpdateTaskDTO.getId() == null || relatedShopUpdateTaskDTO.getShopId() == null) {
      return;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      RelatedShopUpdateTask relatedShopUpdateTask = writer.getById(RelatedShopUpdateTask.class,relatedShopUpdateTaskDTO.getId());
      if(relatedShopUpdateTask!=null){
        relatedShopUpdateTask.setExeStatus(exeStatus);
        if(ExeStatus.FINISHED.equals(exeStatus)){
          relatedShopUpdateTask.setFinishTime(System.currentTimeMillis());
        }
        writer.commit(status);
        relatedShopUpdateTaskDTO.setExeStatus(exeStatus);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Result initShopCustomerSupplierSync()throws Exception{
    ConfigWriter writer = ServiceManager.getService(ConfigDaoManager.class).getWriter();
    List<Long> shopIds =  writer.getAllRelatedShopIds();
    if(CollectionUtils.isNotEmpty(shopIds)){
      for(Long shopId :shopIds){
        createRelatedShopUpdateTask(shopId);
      }
    }
    return new Result();
  }
}
