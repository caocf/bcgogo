package com.bcgogo.config.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.RecentlyUsedDataDTO;
import com.bcgogo.config.model.RecentlyUsedData;
import com.bcgogo.enums.config.RecentlyUsedDataType;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-9-27
 * Time: 上午11:46
 * To change this template use File | Settings | File Templates.
 */
public interface IRecentlyUsedDataService {

  /**
   * 每天每个用户浏览一次记录一次
   * @param shopId
   * @param userId
   * @param dataIds
   * @param type
   */
  void saveOrUpdateRecentlyUsedData(Long shopId,Long userId, RecentlyUsedDataType type, Long... dataIds);

  void deleteAllRecentlyUsedDataByType(Long shopId,Long userId, RecentlyUsedDataType type);

  List<RecentlyUsedDataDTO> getRecentlyUsedDataDTOList(Long shopId,Long userId, RecentlyUsedDataType type,Integer maxSize);

//  RecentlyUsedDataDTO getRecentlyUsedDataDTO(Long dataId);

  /**
   * 统计指定data 被浏览的次数
   * @param shopId
   * @param type
   * @param dataIds
   * @return
   */
  Map<Long,Long> statRecentlyUsedDataCountByDataId(Long shopId,RecentlyUsedDataType type,Long... dataIds);

  void addViewedCountToPreBuyOrderItem(PreBuyOrderItemDTO... itemDTOs);

  Map<Long, Long> statRecentlyUsedDataCountByDataId(RecentlyUsedDataType type, Long... dataIds);

  int statAllRecentlyUsedDataCountByDataId(RecentlyUsedDataType type, Long... dataIds);

}
