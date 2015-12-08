package com.bcgogo.txn.service.stat.assistantStat;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.MemberOrderType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.assistantStat.AchievementStatType;
import com.bcgogo.enums.assistantStat.AchievementType;
import com.bcgogo.enums.assistantStat.AssistantRecordType;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.txn.dto.assistantStat.*;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.assistantStat.AssistantAchievementStat;
import com.bcgogo.txn.model.assistantStat.MemberAchievementHistory;
import com.bcgogo.txn.model.assistantStat.ServiceAchievementHistory;
import com.bcgogo.txn.model.assistantStat.ShopAchievementConfig;
import com.bcgogo.user.dto.DepartmentDTO;
import com.bcgogo.user.dto.SalesManDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 员工业绩统计专用service（新）
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 13-5-23
 * Time: 下午2:38
 * To change this template use File | Settings | File Templates.
 */

public interface IAssistantStatService {
  public void assistantStat(Long shopId);

  public void saveOrUpdateProductAchievement(Long shopId, AchievementType achievementType, Double achievementAmount, Long userId, Set<Long> productIdSet);

  public int countShopAchievementConfig(Long shopId, Long achievementRecordId, AssistantRecordType assistantRecordType);

  public List<ShopAchievementConfigDTO> getShopAchievementConfigByPager(Long shopId, Long achievementRecordId, AssistantRecordType assistantRecordType, Pager pager);


  public void updateSalesManDepartment(Long shopId, List<SalesManDTO> salesManDTOList, Long userId) throws Exception;

  public List<Long> countAssistantStatByCondition(AssistantStatSearchDTO assistantStatSearchDTO);

  public List<AssistantAchievementStatDTO> getAssistantStatByIds(AssistantStatSearchDTO assistantStatSearchDTO, Set<Long> ids);

  public int countAssistantRecordByCondition(AssistantStatSearchDTO assistantStatSearchDTO, Set<OrderTypes> orderTypes);

  public List getAssistantRecordByPager(AssistantStatSearchDTO assistantStatSearchDTO, Set<OrderTypes> orderTypes, Pager pager);

  public void saveMemberAchievementHistory(MemberAchievementHistoryDTO memberAchievementHistoryDTO);

  public List<MemberAchievementHistory> getMemberAchievementHistory(Long shopId, Long vestDate,MemberOrderType memberOrderType);

  public ShopAchievementConfig getShopAchievementConfig(Long shopId, Long achievementRecordId, AssistantRecordType assistantRecordType);

  public void saveOrUpdateServiceAchievementHistory(Long shopId, AchievementType achievementType, Double achievementAmount, Long userId, Set<Long> serviceIdSet,Double standardHours,Double standardUnitPrice);

  public void deleteShopAchievementConfig(Long shopId, AssistantRecordType assistantRecordType, Long assistantRecordId, TxnWriter txnWriter);

  public List<ServiceAchievementHistory> getLastedServiceAchievementHistory(Long shopId, Long serviceId);

  public AssistantAchievementHistoryDTO getLastedAssistantAchievementHistory(Long shopId, Long assistantId,Long changeTime);

  public void updateSalesManAchievement(Long shopId, List<SalesManDTO> salesManDTOList, Long userId) throws Exception;

  public void saveProductSalesProfitAchievement(Long shopId, AchievementType salesProfitAchievementType, Double salesProfitAchievementAmount, Long userId, Set<Long> productIdSet);

  public List<ServiceDTO> getShopAllStatServiceByShopId(Long shopId);

  public Map<Long, SalesManDTO> getSalesManMap(Long shopId);

  public Map<Long, DepartmentDTO> getDepartmentMap(Long shopId);

  public void saveAchievementStat(Long shopId, int year, int month, Map<Long, AssistantAchievementStatDTO> assistantAchievementStatDTOMap);

  public AssistantAchievementHistoryDTO geAssistantAchievementHistoryByVestDate(Long shopId, Long assistantId, Long vestDate);

//  public void saveOrUpdateAssistantRecord(List assistantRecordDTOList);

  public List<AssistantAchievementStat> getAssistantAchievementStat(Long shopId,int statYear,int statMonth,AchievementStatType statType,Long assistantOrDepartmentId);

  public void saveOrUpdateAssistantAchievementStat(Collection<AssistantAchievementStatDTO> assistantAchievementStatDTOList);

  public void getAssistantOrDepartmentName(AssistantStatSearchDTO searchDTO);

  public void saveOrUpdateAssistantBusinessAccount(Long shopId,List<AssistantBusinessAccountRecordDTO> businessAccountRecordDTOs);



}
