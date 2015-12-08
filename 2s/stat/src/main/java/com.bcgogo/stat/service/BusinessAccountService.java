package com.bcgogo.stat.service;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.BusinessAccountEnum;
import com.bcgogo.enums.assistantStat.AchievementStatType;
import com.bcgogo.enums.stat.businessAccountStat.BusinessCategoryStatType;
import com.bcgogo.enums.stat.businessAccountStat.CalculateType;
import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.BusinessAccountDTO;
import com.bcgogo.stat.dto.BusinessAccountSearchConditionDTO;
import com.bcgogo.stat.dto.BusinessCategoryDTO;
import com.bcgogo.stat.dto.BusinessCategoryStatDTO;
import com.bcgogo.stat.model.*;
import com.bcgogo.txn.dto.assistantStat.AssistantAchievementHistoryDTO;
import com.bcgogo.txn.dto.assistantStat.AssistantAchievementStatDTO;
import com.bcgogo.txn.dto.assistantStat.AssistantBusinessAccountRecordDTO;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.assistantStat.AssistantAchievementStat;
import com.bcgogo.txn.model.assistantStat.AssistantBusinessAccountRecord;
import com.bcgogo.txn.service.stat.assistantStat.IAssistantStatService;
import com.bcgogo.user.dto.DepartmentDTO;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Li jinlong
 * Date: 12-9-20
 * Time: 上午10:30
 * To change this template use File | Settings | File Templates.
 */
@Component
public class BusinessAccountService implements IBusinessAccountService {
  private static final Logger LOG = LoggerFactory.getLogger(BusinessAccountService.class);

  @Autowired
  private StatDaoManager statDaoManager;

  @Override
  public BusinessAccountDTO saveBusinessAccount(BusinessAccountDTO businessAccountDTO) throws Exception {

    if (businessAccountDTO == null) return null;
    StatWriter writer = statDaoManager.getWriter();
    Object status = writer.begin();

    try {
      BusinessAccount businessAccount = new BusinessAccount();
      businessAccount.fromDTO(businessAccountDTO);
      businessAccount.setStatus(BusinessAccountEnum.STATUS_SAVE.getName());
      writer.save(businessAccount);

      writer.commit(status);
      businessAccountDTO.setId(businessAccount.getId());
    } finally {
      writer.rollback(status);
    }
    return businessAccountDTO;
  }

  @Override
  public List<BusinessAccountDTO> getBusinessAccountsBySearchCondition(Long shopId, BusinessAccountSearchConditionDTO searchConditionDTO) {

    StatWriter writer = statDaoManager.getWriter();
    List<BusinessAccount> businessAccountList = writer.getBusinessAccount(shopId, searchConditionDTO);
    List<BusinessAccountDTO> businessAccountDTOList = null;
    if (businessAccountList != null && !businessAccountList.isEmpty()) {
      businessAccountDTOList = new ArrayList<BusinessAccountDTO>();
      for (BusinessAccount businessAccount : businessAccountList) {
        BusinessAccountDTO businessAccountDTO = businessAccount.toDTO();
        businessAccountDTOList.add(businessAccountDTO);
      }
    }
    return businessAccountDTOList;


  }

  @Override
  public List<String> countBusinessAccountsBySearchCondition(Long shopId, BusinessAccountSearchConditionDTO searchConditionDTO) {
    StatWriter writer = statDaoManager.getWriter();
    List<String> strings = writer.countBusinessAccounts(shopId, searchConditionDTO);
    return strings;

  }

  @Override
  public void updateBusinessAccount(BusinessAccountDTO businessAccountDTO) throws Exception {
    if (businessAccountDTO == null)
      return;
    StatWriter writer = statDaoManager.getWriter();
    Object status = writer.begin();
    try {
      BusinessAccount businessAccount = writer.getById(BusinessAccount.class, businessAccountDTO.getId());
      businessAccount.fromDTO(businessAccountDTO);
      writer.update(businessAccount);

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public BusinessAccountDTO getBusinessAccountById(Long id) {
    StatWriter writer = statDaoManager.getWriter();
    BusinessAccountDTO businessAccountDTO = null;
    BusinessAccount businessAccount = writer.getById(BusinessAccount.class, id);
    if (businessAccount != null) {
      businessAccountDTO = businessAccount.toDTO();
    }
    return businessAccountDTO;
  }


  public Double getSumBySearchCondition(Long shopId, BusinessAccountSearchConditionDTO searchConditionDTO) {
    StatWriter writer = statDaoManager.getWriter();
    Double sum = writer.countBusinessAccountsExpenses(shopId, searchConditionDTO);
    sum = NumberUtil.round(sum, 2);
    return sum;
  }


  @Override
  public List<BusinessCategoryDTO> getBusinessCategoryByItemType(Long shopId, String itemType) {
    StatWriter writer = statDaoManager.getWriter();
    List<BusinessCategory> businessCategoryList = writer.getBusinessCategoryByItemType(shopId, itemType);
    List<BusinessCategoryDTO> businessCategoryDTOList = null;
    if (CollectionUtils.isNotEmpty(businessCategoryList)) {
      businessCategoryDTOList = new ArrayList<BusinessCategoryDTO>();
      for (BusinessCategory businessCategory : businessCategoryList) {
        BusinessCategoryDTO businessCategoryDTO = businessCategory.toDTO();
        businessCategoryDTOList.add(businessCategoryDTO);
      }
    }
    return businessCategoryDTOList;
  }


  @Override
  public BusinessAccountDTO deleteBusinessAccountById(Long id) {


    BusinessAccountDTO businessAccountDTO = null;
    if (id != null) {
      StatWriter writer = statDaoManager.getWriter();
      Object status = writer.begin();
      try {

        BusinessAccount businessAccount = writer.getById(BusinessAccount.class, id);
        if (businessAccount != null) {
          businessAccount.setStatus(BusinessAccountEnum.STATUS_DELETE.getName());
          writer.update(businessAccount);
          writer.commit(status);
          businessAccountDTO = businessAccount.toDTO();
        }

      } finally {
        writer.rollback(status);
      }
    }
    return businessAccountDTO;
  }

  /**
   * 获取营业外记账统计
   * @param shopId
   * @param businessCategoryId
   * @param statYear
   * @param statMonth
   * @param statDay
   * @param statType
   * @param moneyCategory
   * @return
   */
  public BusinessCategoryStatDTO getBusinessCategoryStat(Long shopId,Long businessCategoryId,Long statYear,Long statMonth,Long statDay,BusinessCategoryStatType statType,MoneyCategory moneyCategory) {
    StatWriter statWriter = statDaoManager.getWriter();
    List<BusinessCategoryStat> stats = statWriter.getBusinessCategoryStat(shopId, businessCategoryId, statYear, statMonth, statDay, statType, moneyCategory);
    if (CollectionUtils.isEmpty(stats)) {
      return null;
    }

    if (stats.size() > 1) {
      LOG.error("shopId" + shopId + ",businessCategoryId" + businessCategoryId + ",statYear" + statYear + ",statMonth" + statMonth + ",statDay" + statDay + ",statType" + statType + ",moneyCategory" + moneyCategory + "has more records");
    }
    return CollectionUtil.getFirst(stats).toDTO();
  }

  /**
   * 营业外记账统计  保存
   * @param businessCategoryStatDTOs
   */
  public void saveBusinessCategoryStatByDTO(List<BusinessCategoryStatDTO> businessCategoryStatDTOs) {
    if (CollectionUtils.isEmpty(businessCategoryStatDTOs)) {
      return;
    }

    StatWriter writer = statDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (BusinessCategoryStatDTO businessCategoryStatDTO : businessCategoryStatDTOs) {
        BusinessCategoryStat businessCategoryStat = null;
        if (businessCategoryStatDTO.getId() != null) {
          businessCategoryStat = writer.getById(BusinessCategoryStat.class, businessCategoryStatDTO.getId());
        }
        if (businessCategoryStat == null) {
          businessCategoryStat = new BusinessCategoryStat();
        }
        businessCategoryStat = businessCategoryStat.fromDTO(businessCategoryStatDTO);
        writer.saveOrUpdate(businessCategoryStat);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }



  public BusinessCategoryStatDTO businessCategoryStatByStatType(BusinessAccountDTO businessAccountDTO,BusinessCategoryStatType statType,Long statDate,CalculateType calculateType) {
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.setTimeInMillis(statDate);
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    BusinessCategoryStatDTO businessCategoryStatDTO = this.getBusinessCategoryStat(businessAccountDTO.getShopId(), businessAccountDTO.getAccountCategoryId(), (long) year, (long) month, (long) day, statType, businessAccountDTO.getMoneyCategory());
    if (businessCategoryStatDTO == null) {
      businessCategoryStatDTO = new BusinessCategoryStatDTO();
    }
    businessCategoryStatDTO = businessCategoryStatDTO.calculateFromBusinessAccountDTO(businessAccountDTO, statDate, (long) year, (long) month, (long) day, statType, calculateType);
    return businessCategoryStatDTO;
  }

  /**
   * 营业外记账统计
   * @param businessAccountDTO
   */
  public void businessCategoryStatByDTO(BusinessAccountDTO businessAccountDTO,CalculateType calculateType) {
    try {
      String editDateStr = businessAccountDTO.getEditDateStr();
      if (StringUtil.isEmpty(editDateStr)) {
        LOG.error("businessAccountDTO.editDateStr is empty" + businessAccountDTO.toString());
        return;
      }

      Long editDate = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, editDateStr);
      editDate++; //设置统计时间统一为每天00:00:00
      if (DateUtil.isCurrentTime(editDate)) {
        editDate = System.currentTimeMillis();
      } else {
        editDate = DateUtil.get6clock(editDate);
      }

      List<BusinessCategoryStatDTO> statDTOs = new ArrayList<BusinessCategoryStatDTO>();

      statDTOs.add(this.businessCategoryStatByStatType(businessAccountDTO, BusinessCategoryStatType.DAY, editDate, calculateType));
      statDTOs.add(this.businessCategoryStatByStatType(businessAccountDTO, BusinessCategoryStatType.MONTH, editDate, calculateType));
      statDTOs.add(this.businessCategoryStatByStatType(businessAccountDTO, BusinessCategoryStatType.YEAR, editDate, calculateType));

      this.saveBusinessCategoryStatByDTO(statDTOs);
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
  }

  /**
   * 保存营业外分类的business_category
   * @param accountDTO
   */
  public void saveOrUpdateBusinessCategoryFromDTO(BusinessAccountDTO accountDTO) {
    if (StringUtil.isEmpty(accountDTO.getAccountCategory())) {
      return;
    }
    StatWriter writer = statDaoManager.getWriter();
    List<BusinessCategory> businessCategories = writer.getBusinessCategoryByItemName(accountDTO.getShopId(), accountDTO.getAccountCategory());

    BusinessCategory businessCategory = null;
    if (CollectionUtils.isNotEmpty(businessCategories)) {
      if (businessCategories.size() > 1) {
        LOG.error("shopId:" + accountDTO.getShopId() + ",accountCategory:" + accountDTO.getAccountCategory() + "has more records");
      }
      businessCategory = CollectionUtil.getFirst(businessCategories);
    } else {
      businessCategory = new BusinessCategory();
    }

    Object status = writer.begin();
    try {
      businessCategory.setShopId(accountDTO.getShopId());
      businessCategory.setMoneyCategory(accountDTO.getMoneyCategory());
      businessCategory.setItemName(accountDTO.getAccountCategory());
      businessCategory.setUseTime(businessCategory.getUseTime().longValue() + 1);
      businessCategory.setItemType(BusinessAccountEnum.CATEGORY_ACCOUNT.getName());
      writer.saveOrUpdate(businessCategory);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    accountDTO.setAccountCategoryId(businessCategory.getId());

  }

  /**
   * 营业统计获取营业外记账统计数据
   * @param shopId
   * @param statYear
   * @param statMonth
   * @param statDay
   */
  public List<BusinessCategoryStatDTO> getBusinessCategoryStatForBusinessStat(Long shopId,Long statYear,Long statMonth,Long statDay) {
    StatWriter statWriter = statDaoManager.getWriter();
    List<BusinessCategoryStat> stats = statWriter.getBusinessCategoryStatForBusinessStat(shopId, statYear, statMonth, statDay);

    List<BusinessCategoryStatDTO> businessCategoryStatDTOList = new ArrayList<BusinessCategoryStatDTO>();
    if (CollectionUtils.isEmpty(stats)) {
      return businessCategoryStatDTOList;
    }

    for (BusinessCategoryStat businessCategoryStat : stats) {
      businessCategoryStatDTOList.add(businessCategoryStat.toDTO());
    }
    return businessCategoryStatDTOList;
  }


  public List<BusinessCategoryDTO> getBusinessCategoryLikeItemName(Long shopId,String name) {
    StatWriter statWriter = statDaoManager.getWriter();
    List<BusinessCategoryDTO> businessCategoryDTOs = new ArrayList<BusinessCategoryDTO>();
    List<BusinessCategory> businessCategories = statWriter.getBusinessCategoryLikeItemName(shopId, name);
    if (CollectionUtils.isEmpty(businessCategories)) {
      return businessCategoryDTOs;
    }

    for (BusinessCategory businessCategory : businessCategories) {
      businessCategoryDTOs.add(businessCategory.toDTO());
    }
    return businessCategoryDTOs;
  }

  /**
   * 营业外收入员工业绩统计
   */
  public void assistantStatBusinessAccountStat(Long statShopId) {
    try {

      IConfigService configService = ServiceManager.getService(IConfigService.class);
      IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);

      List<Long> shopIdList = null;

      if (statShopId == null) {
        shopIdList = configService.getShopId();
      } else {
        shopIdList = new ArrayList<Long>();
        shopIdList.add(statShopId);
      }


      if (CollectionUtils.isEmpty(shopIdList)) {
        return;
      }

      Calendar calendar = Calendar.getInstance();
      int currentYear = calendar.get(Calendar.YEAR);

      long startTime;
      long endTime;

      for (Long shopId : shopIdList) {

        LOG.info("店铺shop_id:" + shopId + "开始统计营业外记账");

        ShopDTO shopDTO = configService.getShopById(shopId);


        Map<Long, SalesManDTO> salesManDTOMap = assistantStatService.getSalesManMap(shopId);
        Map<Long, DepartmentDTO> departmentDTOMap = assistantStatService.getDepartmentMap(shopId);

        if (MapUtils.isEmpty(salesManDTOMap)) {
          continue;
        }

        for (int year = 2012; year <= currentYear; year++) {

          int index = (currentYear == year ? DateUtil.getCurrentMonth() : 12);

          for (int month = 1; month <= index; month++) {
            calendar.set(year, month - 1, 1, 0, 0, 0);
            startTime = calendar.getTimeInMillis();
            calendar.add(Calendar.MONTH, 1);
            endTime = calendar.getTimeInMillis();

            //这个统计结果不区分服务 即所有的服务都记录到一起  提成计算到一起 key 为salesManId
            Map<Long, AssistantAchievementStatDTO> assistantAchievementStatDTOMap = new HashMap<Long, AssistantAchievementStatDTO>();

            List<AssistantBusinessAccountRecordDTO> recordDTOs = new ArrayList<AssistantBusinessAccountRecordDTO>();

            List<BusinessAccount> businessAccounts = statDaoManager.getWriter().getBusinessAccountForAssistantStat(shopId, startTime, endTime);

            if (CollectionUtils.isEmpty(businessAccounts)) {
              continue;
            }

            for (BusinessAccount businessAccount : businessAccounts) {
              if (businessAccount.getSalesManId() == null) {
                continue;
              }

              SalesManDTO salesManDTO = salesManDTOMap.get(businessAccount.getSalesManId());
              if (salesManDTO == null) {
                continue;
              }

              AssistantBusinessAccountRecordDTO record = businessAccount.toRecordDTO();

              Long departmentId = null;

              AssistantAchievementHistoryDTO assistantAchievementHistoryDTO = assistantStatService.geAssistantAchievementHistoryByVestDate(shopId, salesManDTO.getId(), businessAccount.getEditDate());
              if (assistantAchievementHistoryDTO != null) {
                departmentId = assistantAchievementHistoryDTO.getDepartmentId();
              }

              if (departmentId == null) {
                departmentId = salesManDTO.getDepartmentId();
              }

              if (departmentId != null) {
                DepartmentDTO departmentDTO = departmentDTOMap.get(departmentId);
                if (departmentDTO == null) {
                  LOG.error("assistantStatBusinessAccountStat.departmentDTO is null :" + "shopId:" + shopId + ",salesManId:" + businessAccount.getSalesManId() + ",departmentId:" + departmentId);
                } else {
                  record.setDepartmentId(departmentDTO.getId());
                  record.setDepartmentName(departmentDTO.getName());
                }
              }

              AssistantAchievementStatDTO assistantAchievementStatDTO = assistantAchievementStatDTOMap.get(salesManDTO.getId());
              if (assistantAchievementStatDTO == null) {
                assistantAchievementStatDTO = new AssistantAchievementStatDTO();
                assistantAchievementStatDTO.setShopId(shopId);
                assistantAchievementStatDTO.setStatYear(year);
                assistantAchievementStatDTO.setStatMonth(month);
                assistantAchievementStatDTO.setAchievementStatType(AchievementStatType.ASSISTANT);
                assistantAchievementStatDTO.setAssistantId(salesManDTO.getId());
                assistantAchievementStatDTO.setDepartmentId(departmentId);
                assistantAchievementStatDTO.setStatTime(endTime - 10000);
                assistantAchievementStatDTO.setAssistantName(salesManDTO.getName());
                assistantAchievementStatDTO.setDepartmentName(record.getDepartmentName());
              }


              assistantAchievementStatDTO.setBusinessAccount(NumberUtil.doubleVal(assistantAchievementStatDTO.getBusinessAccount()) + businessAccount.getTotal());
              assistantAchievementStatDTO.setStatSum(NumberUtil.doubleVal(assistantAchievementStatDTO.getStatSum()) + businessAccount.getTotal());
              assistantAchievementStatDTOMap.put(salesManDTO.getId(), assistantAchievementStatDTO);

              recordDTOs.add(record);
            }

            assistantStatService.saveOrUpdateAssistantBusinessAccount(shopId,recordDTOs);


            if (CollectionUtil.isEmpty(assistantAchievementStatDTOMap.values())) {
              continue;
            }


            Map<Long, AssistantAchievementStatDTO> departmentAchievementStatDTOMap = new HashMap<Long, AssistantAchievementStatDTO>();

            for (AssistantAchievementStatDTO assistantAchievementStatDTO : assistantAchievementStatDTOMap.values()) {
              if (assistantAchievementStatDTO == null || assistantAchievementStatDTO.getDepartmentId() == null) {
                continue;
              }
              AssistantAchievementStatDTO departmentAchievementStat = null;

              departmentAchievementStat = departmentAchievementStatDTOMap.get(assistantAchievementStatDTO.getDepartmentId());
              if (departmentAchievementStat == null) {
                departmentAchievementStat = new AssistantAchievementStatDTO();
                departmentAchievementStat.setShopId(shopId);
                departmentAchievementStat.setStatYear(year);
                departmentAchievementStat.setStatMonth(month);
                departmentAchievementStat.setAchievementStatType(AchievementStatType.DEPARTMENT);
                departmentAchievementStat.setDepartmentId(assistantAchievementStatDTO.getDepartmentId());
                departmentAchievementStat.setStatTime(assistantAchievementStatDTO.getStatTime());
                departmentAchievementStat.setDepartmentName(assistantAchievementStatDTO.getDepartmentName());

              } else {
                departmentAchievementStat = departmentAchievementStatDTOMap.get(assistantAchievementStatDTO.getDepartmentId());
              }
              departmentAchievementStat = departmentAchievementStat.add(assistantAchievementStatDTO);
              departmentAchievementStatDTOMap.put(assistantAchievementStatDTO.getDepartmentId(), departmentAchievementStat);
            }

            Map<Long, AssistantAchievementStatDTO> assistantMap = new HashMap<Long, AssistantAchievementStatDTO>();
            Map<Long, AssistantAchievementStatDTO> departmentMap = new HashMap<Long, AssistantAchievementStatDTO>();


            for (AssistantAchievementStatDTO assistantAchievementStatDTO : assistantAchievementStatDTOMap.values()) {

              List<AssistantAchievementStat> stats = assistantStatService.getAssistantAchievementStat(shopId, year, month, AchievementStatType.ASSISTANT, assistantAchievementStatDTO.getAssistantId());

              if (CollectionUtil.isNotEmpty(stats) && stats.size() > 1) {
                LOG.error("shopId" + shopId + "statYear:" + year + "statMonth" + month + "statType" + AchievementStatType.ASSISTANT + "assistantId:" + assistantAchievementStatDTO.getAssistantId() + "has more record");
              }


              if (CollectionUtil.isNotEmpty(stats)) {
                AssistantAchievementStatDTO statDTO = CollectionUtil.getFirst(stats).toDTO();
                statDTO.setBusinessAccount(NumberUtil.doubleVal(statDTO.getBusinessAccount()) + assistantAchievementStatDTO.getBusinessAccount());
                statDTO.setStatSum(NumberUtil.doubleVal(statDTO.getStatSum()) + assistantAchievementStatDTO.getBusinessAccount());
                assistantMap.put(assistantAchievementStatDTO.getAssistantId(), statDTO);
              } else {
                assistantMap.put(assistantAchievementStatDTO.getAssistantId(), assistantAchievementStatDTO);
              }
            }


            if (CollectionUtils.isNotEmpty(departmentAchievementStatDTOMap.values())) {

              for (AssistantAchievementStatDTO assistantAchievementStatDTO : departmentAchievementStatDTOMap.values()) {

                List<AssistantAchievementStat> stats = assistantStatService.getAssistantAchievementStat(shopId, year, month, AchievementStatType.DEPARTMENT, assistantAchievementStatDTO.getDepartmentId());

                if (CollectionUtil.isNotEmpty(stats) && stats.size() > 1) {
                  LOG.error("shopId" + shopId + "statYear:" + year + "statMonth" + month + "statType" + AchievementStatType.DEPARTMENT + "departmentId:" + assistantAchievementStatDTO.getDepartmentId() + "has more record");
                }
                if (CollectionUtil.isNotEmpty(stats)) {
                  AssistantAchievementStatDTO statDTO = CollectionUtil.getFirst(stats).toDTO();
                  statDTO.setBusinessAccount(NumberUtil.doubleVal(statDTO.getBusinessAccount()) + assistantAchievementStatDTO.getBusinessAccount());
                  statDTO.setStatSum(NumberUtil.doubleVal(statDTO.getStatSum()) + assistantAchievementStatDTO.getBusinessAccount());
                  departmentMap.put(assistantAchievementStatDTO.getDepartmentId(), statDTO);
                } else {
                  departmentMap.put(assistantAchievementStatDTO.getDepartmentId(), assistantAchievementStatDTO);
                }
              }
            }

            assistantStatService.saveOrUpdateAssistantAchievementStat(assistantMap.values());
            assistantStatService.saveOrUpdateAssistantAchievementStat(departmentMap.values());

          }
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

}
