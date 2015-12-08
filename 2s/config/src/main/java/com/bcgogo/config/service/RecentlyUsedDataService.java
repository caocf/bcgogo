package com.bcgogo.config.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.RecentlyUsedDataDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.RecentlyUsedData;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.config.RecentlyUsedDataType;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-9-27
 * Time: 上午11:40
 * To change this template use File | Settings | File Templates.
 */
@Component
public class RecentlyUsedDataService implements IRecentlyUsedDataService {
  private static final Logger LOG = LoggerFactory.getLogger(RecentlyUsedDataService.class);
  @Autowired
  private ConfigDaoManager configDaoManager;


  @Override
  public void saveOrUpdateRecentlyUsedData(Long shopId,Long userId,RecentlyUsedDataType type, Long... dataIds) {
    if(shopId==null || userId==null || ArrayUtils.isEmpty(dataIds) || type==null) return;
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      int maxSize = 0;
      switch (type){
        case USED_PRODUCT_CATEGORY:
          maxSize = ConfigUtils.getRecentlyUsedProductCategoryNum();
          break;
        case USED_SMS_CONTACT:
          maxSize = ConfigUtils.getRecentlyUsedSmsContactNum();
          break;
      }
      Set<Long> dataIdSet = new HashSet<Long>(Arrays.asList(dataIds));
      List<RecentlyUsedData> pendingRecentlyUsedDataList = new ArrayList<RecentlyUsedData>();
      List<Long> pendingRecentlyUsedDataIdList = new ArrayList<Long>();
      List<RecentlyUsedData> recentlyUsedDataList = writer.getRecentlyUsedDataListByDataId(shopId,userId, type,dataIds);
      //获取更新存在的
      if(CollectionUtils.isNotEmpty(recentlyUsedDataList)){
        List<Long> existRecentlyUsedDataIdList = new ArrayList<Long>();
        for(RecentlyUsedData recentlyUsedData : recentlyUsedDataList){
          if(!DateUtil.isInToday(recentlyUsedData.getTime())){
            recentlyUsedData.setCount(NumberUtil.addition(recentlyUsedData.getCount(),1));
          }
          recentlyUsedData.setTime(System.currentTimeMillis());
          existRecentlyUsedDataIdList.add(recentlyUsedData.getDataId());
          pendingRecentlyUsedDataList.add(recentlyUsedData);
          pendingRecentlyUsedDataIdList.add(recentlyUsedData.getDataId());
        }
        Iterator<Long> dataIdIterator = dataIdSet.iterator();
        Long recentlyUsedDataId = null;
        while (dataIdIterator.hasNext()){
          recentlyUsedDataId = dataIdIterator.next();
          if(existRecentlyUsedDataIdList.contains(recentlyUsedDataId)){
            dataIdIterator.remove();
          }
        }
      }
      //dataIdSet 剩下 新增的
      if(CollectionUtils.isNotEmpty(dataIdSet)){
        for(Long dataId : dataIdSet){
          RecentlyUsedData newRecentlyUsedData = new RecentlyUsedData();
          newRecentlyUsedData.setShopId(shopId);
          newRecentlyUsedData.setType(type);
          newRecentlyUsedData.setUserId(userId);
          newRecentlyUsedData.setCount(1d);
          newRecentlyUsedData.setDataId(dataId);
          newRecentlyUsedData.setTime(System.currentTimeMillis());
          pendingRecentlyUsedDataList.add(newRecentlyUsedData);
          pendingRecentlyUsedDataIdList.add(newRecentlyUsedData.getDataId());
        }
      }

      if(CollectionUtils.isNotEmpty(pendingRecentlyUsedDataList)){
        if(maxSize>0){//有数量限制的处理逻辑
          List<RecentlyUsedData> allRecentlyUsedDataList = writer.getRecentlyUsedDataList(shopId,userId, type,maxSize);
          if(CollectionUtils.isNotEmpty(allRecentlyUsedDataList)){
            for(RecentlyUsedData recentlyUsedData:allRecentlyUsedDataList){
              if(!pendingRecentlyUsedDataIdList.contains(recentlyUsedData.getDataId())){
                pendingRecentlyUsedDataList.add(recentlyUsedData);
              }
            }
          }
          Collections.sort(pendingRecentlyUsedDataList,new Comparator<RecentlyUsedData>() {
            @Override
            public int compare(RecentlyUsedData o1, RecentlyUsedData o2) {
              if(o1.getTime()>o2.getTime()){
                return -1;
              }
              if(o1.getTime()<o2.getTime()){
                return 1;
              }
              return 0;
            }
          });
          int i = 1;
          for(RecentlyUsedData recentlyUsedData : pendingRecentlyUsedDataList){
            if(i<=maxSize){
              writer.saveOrUpdate(recentlyUsedData);
              i++;
            }else if(recentlyUsedData.getId()!=null){
              writer.delete(recentlyUsedData);
            }
          }
        }else{
          for(RecentlyUsedData recentlyUsedData : pendingRecentlyUsedDataList){
            writer.saveOrUpdate(recentlyUsedData);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void deleteAllRecentlyUsedDataByType(Long shopId, Long userId, RecentlyUsedDataType type) {
    if(shopId==null || userId==null || type==null) return;
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.deleteAllRecentlyUsedDataByType(shopId,userId, type);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public List<RecentlyUsedDataDTO> getRecentlyUsedDataDTOList(Long shopId,Long userId, RecentlyUsedDataType type,Integer maxSize){
    List<RecentlyUsedDataDTO> recentlyUsedDataDTOList = new ArrayList<RecentlyUsedDataDTO>();
    if(shopId==null || type==null) return recentlyUsedDataDTOList;
    ConfigWriter writer = configDaoManager.getWriter();
    List<RecentlyUsedData> recentlyUsedDataList = writer.getRecentlyUsedDataList(shopId,userId, type,maxSize);
    if(CollectionUtils.isNotEmpty(recentlyUsedDataList)){
      for(RecentlyUsedData recentlyUsedData:recentlyUsedDataList){
        recentlyUsedDataDTOList.add(recentlyUsedData.toDTO());
      }
    }
    return recentlyUsedDataDTOList;
  }

//  @Override
//  public RecentlyUsedDataDTO getRecentlyUsedDataDTO(Long dataId){
//    if(dataId==null) return null;
//    ConfigWriter writer = configDaoManager.getWriter();
//    RecentlyUsedData recentlyUsedData=writer.getById(RecentlyUsedData.class,dataId);
//    return recentlyUsedData==null?null:recentlyUsedData.toDTO();
//  }

  @Override
  public void addViewedCountToPreBuyOrderItem(PreBuyOrderItemDTO... itemDTOs){
    if(ArrayUtil.isEmpty(itemDTOs)) return;
    List<Long> dataIds=new ArrayList<Long>();
    for(PreBuyOrderItemDTO itemDTO:itemDTOs){
      dataIds.add(itemDTO.getId());
    }
    Map<Long, Long> viewMap=statRecentlyUsedDataCountByDataId(RecentlyUsedDataType.VISITED_BUSINESS_CHANCE,ArrayUtil.toLongArr(dataIds));
    if(MapUtils.isEmpty(viewMap)) return;
      for(PreBuyOrderItemDTO itemDTO:itemDTOs){
        itemDTO.setViewedCount(NumberUtil.doubleValue(viewMap.get(itemDTO.getId()),0));
    }
  }

  @Override
  public Map<Long, Long> statRecentlyUsedDataCountByDataId(Long shopId, RecentlyUsedDataType type, Long... dataIds) {
    Map<Long, Long> resultMap = new HashMap<Long, Long>();
   if(ArrayUtil.isEmpty(dataIds)) return resultMap;
    ConfigWriter writer = configDaoManager.getWriter();
    List<Object[]> objectsList = writer.statRecentlyUsedDataCountByDataId(shopId,type,dataIds);
    if(CollectionUtils.isNotEmpty(objectsList)){
      for(Object[] objects : objectsList){
        if(objects!=null&& objects.length==2 && objects[0]!=null && objects[1]!=null){
          resultMap.put((Long)objects[0] ,NumberUtil.roundLong(objects[1]));
        }
      }
    }
    return resultMap;
  }

  @Override
  public Map<Long, Long> statRecentlyUsedDataCountByDataId(RecentlyUsedDataType type, Long... dataIds) {
    return  statRecentlyUsedDataCountByDataId(null,type,dataIds);
  }

  @Override
  public int statAllRecentlyUsedDataCountByDataId(RecentlyUsedDataType type, Long... dataIds) {
     if(ArrayUtil.isEmpty(dataIds)) return 0;
    ConfigWriter writer = configDaoManager.getWriter();
    List<Object[]> objectsList = writer.statRecentlyUsedDataCountByDataId(null,type,dataIds);
    int total=0;
    if(CollectionUtils.isNotEmpty(objectsList)){
      for(Object[] objects : objectsList){
        if(objects!=null&& objects.length==2 && objects[0]!=null && objects[1]!=null){
          total+=NumberUtil.roundInt(objects[1]);
        }
      }
    }
    return total;
  }


}
