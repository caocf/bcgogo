package com.bcgogo.txn.service.exportExcel;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.excelexport.BcgogoExportDataIterator;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.assistantStat.AchievementCalculateWay;
import com.bcgogo.enums.assistantStat.AssistantRecordType;
import com.bcgogo.enums.stat.businessAccountStat.CalculateType;
import com.bcgogo.exception.PageException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.assistantStat.*;
import com.bcgogo.txn.model.assistantStat.AssistantBusinessAccountRecord;
import com.bcgogo.txn.model.assistantStat.AssistantMemberRecord;
import com.bcgogo.txn.model.assistantStat.AssistantProductRecord;
import com.bcgogo.txn.model.assistantStat.AssistantServiceRecord;
import com.bcgogo.txn.service.stat.assistantStat.IAssistantStatService;
import com.bcgogo.utils.ShopConstant;
import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-12-13
 * Time: 下午5:15
 * To change this template use File | Settings | File Templates.
 */
public class AssistantStatDetailExportIterator extends BcgogoExportDataIterator {
  private static final Logger LOG = LoggerFactory.getLogger(InventoryExportDataIterator.class);
  private static final int PAGE_SIZE = 1000;
  private AssistantStatSearchDTO assistantStatSearchDTO;

  public AssistantStatDetailExportIterator(AssistantStatSearchDTO assistantStatSearchDTO) throws PageException {
    super(assistantStatSearchDTO.getTotalNum(),PAGE_SIZE,Integer.valueOf(ServiceManager.getService(IConfigService.class).getConfig("TotalNumPerExcel", ShopConstant.BC_SHOP_ID)));
    this.assistantStatSearchDTO = assistantStatSearchDTO;
  }

  @Override
  protected int getTotalRows() {
    if(assistantStatSearchDTO == null) {
      return 0;
    }
    return assistantStatSearchDTO.getTotalNum();
  }

  @Override
  protected List<String> getHead() {
    if("assistantServiceRecord".equals(assistantStatSearchDTO.getPageType())) {
       return AssistantStatDetailConstant.repairFieldList;
    } else if("assistantWashRecord".equals(assistantStatSearchDTO.getPageType())) {
       return AssistantStatDetailConstant.washFieldList;
    } else if("assistantProductRecord".equals(assistantStatSearchDTO.getPageType())) {
      return AssistantStatDetailConstant.salesFieldList;
    } else if("assistantMemberRecord".equals(assistantStatSearchDTO.getPageType())) {
      return AssistantStatDetailConstant.memberFieldList;
    } else if("assistantBusinessAccountRecord".equals(assistantStatSearchDTO.getPageType())) {
      return AssistantStatDetailConstant.businessAccountFieldList;
    } else {
      LOG.error("assistantStatSearchDTO getPageType is wrong");
      return null;
    }
  }

  @Override
  protected List<String> getHeadShowInfo() {
    return null;
  }

  @Override
  protected List<String> getTailShowInfo() {
    return null;
  }

  @Override
  public Object next() {
    //取下一页数据
    getPage().gotoNextPage();
    //生成要导出的数据
    List<List<String>> rows = assembleAssistantStatDetailInfo();
    return rows;
  }

  private List<List<String>> assembleAssistantStatDetailInfo() {
    List<List<String>> rows = new ArrayList<List<String>>();
    if("assistantServiceRecord".equals(assistantStatSearchDTO.getPageType())) {
      List<AssistantServiceRecordDTO> assistantServiceRecordDTOList = getAssistantService(assistantStatSearchDTO);
      if(CollectionUtils.isNotEmpty(assistantServiceRecordDTOList)) {
        for(AssistantServiceRecordDTO assistantServiceRecordDTO : assistantServiceRecordDTOList) {
          List<String> row = new ArrayList<String>();
          row.add(assistantServiceRecordDTO.getAssistantName() == null ? "" : assistantServiceRecordDTO.getAssistantName());
          row.add(assistantServiceRecordDTO.getDepartmentName() == null ? "" : assistantServiceRecordDTO.getDepartmentName());
          row.add(assistantServiceRecordDTO.getVestDateStr() == null ? "" : assistantServiceRecordDTO.getVestDateStr());
          row.add(assistantServiceRecordDTO.getVehicle() == null ? "" : assistantServiceRecordDTO.getVehicle());
          row.add(assistantServiceRecordDTO.getCustomer() == null ? "" : assistantServiceRecordDTO.getCustomer());
          row.add(assistantServiceRecordDTO.getServiceName() == null ? "" : assistantServiceRecordDTO.getServiceName());
          row.add(assistantServiceRecordDTO.getStandardHours() == null ? "0" : assistantServiceRecordDTO.getStandardHours().toString());
          row.add(assistantServiceRecordDTO.getStandardService() == null ? "0" : assistantServiceRecordDTO.getStandardService().toString());
          row.add(assistantServiceRecordDTO.getActualHours() == null ? "0" : assistantServiceRecordDTO.getActualHours().toString());
          row.add(assistantServiceRecordDTO.getActualService() == null ? "0" : assistantServiceRecordDTO.getActualService().toString());
          if(AchievementCalculateWay.CALCULATE_BY_ASSISTANT.toString().equals(assistantStatSearchDTO.getAchievementCalculateWayStr())) {
            row.add(assistantServiceRecordDTO.getAchievementByAssistant() == null ? "0" : assistantServiceRecordDTO.getAchievementByAssistant().toString());
          } else {
            row.add(assistantServiceRecordDTO.getAchievement() == null ? "0" : assistantServiceRecordDTO.getAchievement().toString());
          }

          row.add(assistantServiceRecordDTO.getReceiptNo() == null ? "" : assistantServiceRecordDTO.getReceiptNo());
          rows.add(row);
        }
      }
    } else if("assistantWashRecord".equals(assistantStatSearchDTO.getPageType())) {
      List<AssistantServiceRecordDTO> assistantServiceRecordDTOList = getAssistantService(assistantStatSearchDTO);
      if(CollectionUtils.isNotEmpty(assistantServiceRecordDTOList)) {
        for(AssistantServiceRecordDTO assistantServiceRecordDTO : assistantServiceRecordDTOList) {
          List<String> row = new ArrayList<String>();
          row.add(assistantServiceRecordDTO.getAssistantName() == null ? "" : assistantServiceRecordDTO.getAssistantName());
          row.add(assistantServiceRecordDTO.getDepartmentName() == null ? "" : assistantServiceRecordDTO.getDepartmentName());
          row.add(assistantServiceRecordDTO.getVestDateStr() == null ? "" : assistantServiceRecordDTO.getVestDateStr());
          row.add(assistantServiceRecordDTO.getVehicle() == null ? "" : assistantServiceRecordDTO.getVehicle());
          row.add(assistantServiceRecordDTO.getCustomer() == null ? "" : assistantServiceRecordDTO.getCustomer());
          row.add(assistantServiceRecordDTO.getServiceName() == null ? "" : assistantServiceRecordDTO.getServiceName());
          row.add(assistantServiceRecordDTO.getActualService() == null ? "0" : assistantServiceRecordDTO.getActualService().toString());
          if(AchievementCalculateWay.CALCULATE_BY_ASSISTANT.toString().equals(assistantStatSearchDTO.getAchievementCalculateWayStr())) {
            row.add(assistantServiceRecordDTO.getAchievementByAssistant() == null ? "0" : assistantServiceRecordDTO.getAchievementByAssistant().toString());
          } else {
            row.add(assistantServiceRecordDTO.getAchievement() == null ? "0" : assistantServiceRecordDTO.getAchievement().toString());
          }
          row.add(assistantServiceRecordDTO.getReceiptNo() == null ? "" : assistantServiceRecordDTO.getReceiptNo());
          rows.add(row);
        }
      }
    } else if("assistantProductRecord".equals(assistantStatSearchDTO.getPageType())) {
      List<AssistantProductRecordDTO> assistantProductRecordDTOList = getAssistantProduct(assistantStatSearchDTO);
      if(CollectionUtils.isNotEmpty(assistantProductRecordDTOList)) {
        for(AssistantProductRecordDTO assistantProductRecordDTO : assistantProductRecordDTOList) {
          List<String> row = new ArrayList<String>();
          row.add(assistantProductRecordDTO.getAssistantName() == null ? "" : assistantProductRecordDTO.getAssistantName());
          row.add(assistantProductRecordDTO.getDepartmentName() == null ? "" : assistantProductRecordDTO.getDepartmentName());
          row.add(assistantProductRecordDTO.getVestDateStr() == null ? "" : assistantProductRecordDTO.getVestDateStr());
          row.add(assistantProductRecordDTO.getOrderTypeStr() == null ? "" : assistantProductRecordDTO.getOrderTypeStr());
          row.add(assistantProductRecordDTO.getCustomer() == null ? "" : assistantProductRecordDTO.getCustomer());
          row.add(assistantProductRecordDTO.getProductName() == null ? "" : assistantProductRecordDTO.getProductName());
          row.add((assistantProductRecordDTO.getAmount() == null ? "0" : assistantProductRecordDTO.getAmount().toString()) + (assistantProductRecordDTO.getUnit() == null ? "" : assistantProductRecordDTO.getUnit()));
          row.add(assistantProductRecordDTO.getPrice() == null ? "0" : assistantProductRecordDTO.getPrice().toString());
          row.add(assistantProductRecordDTO.getTotal() == null ? "0" : assistantProductRecordDTO.getTotal().toString());
          if(AchievementCalculateWay.CALCULATE_BY_ASSISTANT.toString().equals(assistantStatSearchDTO.getAchievementCalculateWayStr())) {
            row.add(assistantProductRecordDTO.getAchievementByAssistant() == null ? "0" : assistantProductRecordDTO.getAchievementByAssistant().toString());
          } else {
            row.add(assistantProductRecordDTO.getAchievement() == null ? "0" : assistantProductRecordDTO.getAchievement().toString());
          }

          row.add(assistantProductRecordDTO.getProfit() == null ? "0" : assistantProductRecordDTO.getProfit().toString());
          if(AchievementCalculateWay.CALCULATE_BY_ASSISTANT.toString().equals(assistantStatSearchDTO.getAchievementCalculateWayStr())) {
            row.add(assistantProductRecordDTO.getProfitAchievementByAssistant() == null ? "0" : assistantProductRecordDTO.getProfitAchievementByAssistant().toString());
          } else {
            row.add(assistantProductRecordDTO.getProfitAchievement() == null ? "0" : assistantProductRecordDTO.getProfitAchievement().toString());
          }

          row.add(assistantProductRecordDTO.getReceiptNo() == null ? "" : assistantProductRecordDTO.getReceiptNo());
          rows.add(row);
        }
      }
    } else if("assistantMemberRecord".equals(assistantStatSearchDTO.getPageType())) {
      List<AssistantMemberRecordDTO> assistantMemberRecordDTOList = getAssistantMember(assistantStatSearchDTO);
      if(CollectionUtils.isNotEmpty(assistantMemberRecordDTOList)) {
         for(AssistantMemberRecordDTO assistantMemberRecordDTO : assistantMemberRecordDTOList) {
           List<String> row = new ArrayList<String>();
           row.add(assistantMemberRecordDTO.getAssistantName() == null ? "" : assistantMemberRecordDTO.getAssistantName());
           row.add(assistantMemberRecordDTO.getDepartmentName() == null ? "" : assistantMemberRecordDTO.getDepartmentName());
           row.add(assistantMemberRecordDTO.getVestDateStr() == null ? "" : assistantMemberRecordDTO.getVestDateStr());
           row.add(assistantMemberRecordDTO.getMemberNo() == null ? "" : assistantMemberRecordDTO.getMemberNo());
           row.add(assistantMemberRecordDTO.getMemberCardName() == null ? "" : assistantMemberRecordDTO.getMemberCardName());
           row.add(assistantMemberRecordDTO.getMemberCardTypeStr() == null ? "" : assistantMemberRecordDTO.getMemberCardTypeStr());
           row.add(assistantMemberRecordDTO.getMemberCardTotal() == null ? "" : assistantMemberRecordDTO.getMemberCardTotal().toString());
           row.add(assistantMemberRecordDTO.getCustomer() == null ? "" : assistantMemberRecordDTO.getCustomer());
           if(OrderTypes.MEMBER_RETURN_CARD.equals(assistantMemberRecordDTO.getOrderType())) {
             row.add("退卡");
           } else {
             row.add(assistantMemberRecordDTO.getMemberOrderTypeStr() == null ? "" : assistantMemberRecordDTO.getMemberOrderTypeStr());
           }
           row.add(assistantMemberRecordDTO.getTotal() == null ? "0" : assistantMemberRecordDTO.getTotal().toString());
           if(AchievementCalculateWay.CALCULATE_BY_ASSISTANT.toString().equals(assistantStatSearchDTO.getAchievementCalculateWayStr())) {
             row.add(assistantMemberRecordDTO.getAchievementByAssistant() == null ? "0" : assistantMemberRecordDTO.getAchievementByAssistant().toString());
           } else {
             row.add(assistantMemberRecordDTO.getAchievement() == null ? "0" : assistantMemberRecordDTO.getAchievement().toString());
           }
           rows.add(row);
         }
      }
    } else if("assistantBusinessAccountRecord".equals(assistantStatSearchDTO.getPageType())) {
      List<AssistantBusinessAccountRecordDTO> assistantBusinessAccountRecordDTOList = getAssistantBusinessAccount(assistantStatSearchDTO);
      if(CollectionUtils.isNotEmpty(assistantBusinessAccountRecordDTOList)) {
        for(AssistantBusinessAccountRecordDTO assistantBusinessAccountRecordDTO : assistantBusinessAccountRecordDTOList) {
          List<String> row = new ArrayList<String>();
          row.add(assistantBusinessAccountRecordDTO.getDepartmentName() == null ? "" : assistantBusinessAccountRecordDTO.getDepartmentName());
          row.add(assistantBusinessAccountRecordDTO.getAssistantName() == null ? "" : assistantBusinessAccountRecordDTO.getAssistantName());
          row.add(assistantBusinessAccountRecordDTO.getVestDateStr() == null ? "" : assistantBusinessAccountRecordDTO.getVestDateStr());
          row.add(assistantBusinessAccountRecordDTO.getAccountCategory() == null ? "" : assistantBusinessAccountRecordDTO.getAccountCategory());
          row.add(assistantBusinessAccountRecordDTO.getDocNo() == null ? "" : assistantBusinessAccountRecordDTO.getDocNo());
          row.add(assistantBusinessAccountRecordDTO.getContent() == null ? "" : assistantBusinessAccountRecordDTO.getContent());
          row.add(assistantBusinessAccountRecordDTO.getBusinessCategory() == null ? "" : assistantBusinessAccountRecordDTO.getBusinessCategory());
          row.add(assistantBusinessAccountRecordDTO.getTotal() == null ? "0" : assistantBusinessAccountRecordDTO.getTotal().toString());
          rows.add(row);
        }
      }
    }
    return rows;
  }

  private  List<AssistantServiceRecordDTO> getAssistantService(AssistantStatSearchDTO assistantStatSearchDTO) {
    if(assistantStatSearchDTO == null) return null;
    List<AssistantServiceRecordDTO> assistantServiceRecordDTOList = new ArrayList<AssistantServiceRecordDTO>();
    IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
    assistantStatSearchDTO.setTime();
    assistantStatSearchDTO.setAssistantRecordType(AssistantRecordType.SERVICE);
    if (assistantStatSearchDTO.getAssistantOrDepartmentId() == null || assistantStatSearchDTO.getAchievementStatType() == null) {
      LOG.error("assistantOrDepartmentId or achievementStatType is null!");
      return null;
    }
    Set<OrderTypes> orderTypesSet = new HashSet<OrderTypes>();
    if (StringUtils.isNotEmpty(assistantStatSearchDTO.getOrderType()) && "washBeauty".equals(assistantStatSearchDTO.getOrderType())) {
      orderTypesSet.add(OrderTypes.WASH_BEAUTY);
    } else {
      orderTypesSet.add(OrderTypes.REPAIR);
    }
    try {
      List list = assistantStatService.getAssistantRecordByPager(assistantStatSearchDTO, orderTypesSet, getPage().toCommonPager());
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object object : list) {
          AssistantServiceRecord assistantServiceRecord = (AssistantServiceRecord) object;
          assistantServiceRecordDTOList.add(assistantServiceRecord.toDTO());
        }
      }

    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
   return assistantServiceRecordDTOList;
  }

  private List<AssistantProductRecordDTO> getAssistantProduct(AssistantStatSearchDTO assistantStatSearchDTO) {
    if(assistantStatSearchDTO == null) return null;
    IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
    List<AssistantProductRecordDTO> assistantProductRecordDTOList = new ArrayList<AssistantProductRecordDTO>();
    assistantStatSearchDTO.setTime();
    assistantStatSearchDTO.setAssistantRecordType(AssistantRecordType.PRODUCT);

    Set<OrderTypes> orderTypesSet = new HashSet<OrderTypes>();
    orderTypesSet.add(OrderTypes.REPAIR);
    orderTypesSet.add(OrderTypes.SALE);
    orderTypesSet.add(OrderTypes.SALE_RETURN);
    if (assistantStatSearchDTO.getAssistantOrDepartmentId() == null || assistantStatSearchDTO.getAchievementStatType() == null) {
      LOG.error("assistantOrDepartmentId or achievementStatType is null!");
      return null;
    }
    try {
      List list = assistantStatService.getAssistantRecordByPager(assistantStatSearchDTO, orderTypesSet, getPage().toCommonPager());
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object object : list) {
          AssistantProductRecord assistantProductRecord = (AssistantProductRecord) object;
          assistantProductRecordDTOList.add(assistantProductRecord.toDTO());
        }
      }
    } catch (Exception e) {
       LOG.error(e.getMessage(),e);
    }
    return assistantProductRecordDTOList;
  }

  private List<AssistantMemberRecordDTO> getAssistantMember(AssistantStatSearchDTO assistantStatSearchDTO) {
    if(assistantStatSearchDTO == null) return null;
    IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
    List<AssistantMemberRecordDTO> assistantMemberRecordDTOList = new ArrayList<AssistantMemberRecordDTO>();
    assistantStatSearchDTO.setTime();
    assistantStatSearchDTO.setAssistantRecordType(AssistantRecordType.MEMBER_NEW);
    if (assistantStatSearchDTO.getAssistantOrDepartmentId() == null || assistantStatSearchDTO.getAchievementStatType() == null) {
      LOG.error("assistantOrDepartmentId or achievementStatType is null!");
      return null;
    }
    Set<OrderTypes> orderTypesSet = new HashSet<OrderTypes>();
    orderTypesSet.add(OrderTypes.MEMBER_BUY_CARD);
    orderTypesSet.add(OrderTypes.MEMBER_RETURN_CARD);
    try {
      List list = assistantStatService.getAssistantRecordByPager(assistantStatSearchDTO, orderTypesSet, getPage().toCommonPager());
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object object : list) {
          AssistantMemberRecord assistantMemberRecord = (AssistantMemberRecord) object;
          assistantMemberRecordDTOList.add(assistantMemberRecord.toDTO());
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    return assistantMemberRecordDTOList;
  }

  private List<AssistantBusinessAccountRecordDTO> getAssistantBusinessAccount(AssistantStatSearchDTO assistantStatSearchDTO) {
    if(assistantStatSearchDTO == null) return null;
    IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
    List<AssistantBusinessAccountRecordDTO> recordDTOs = new ArrayList<AssistantBusinessAccountRecordDTO>();
    assistantStatSearchDTO.setTime();
    assistantStatSearchDTO.setAssistantRecordType(AssistantRecordType.BUSINESS_ACCOUNT);
    if (assistantStatSearchDTO.getAssistantOrDepartmentId() == null || assistantStatSearchDTO.getAchievementStatType() == null) {
      LOG.error("assistantOrDepartmentId or achievementStatType is null!");
      return null;
    }
    Set<OrderTypes> orderTypesSet = new HashSet<OrderTypes>();
    try {
      List list = assistantStatService.getAssistantRecordByPager(assistantStatSearchDTO, orderTypesSet, getPage().toCommonPager());
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object object : list) {
          AssistantBusinessAccountRecord businessAccountRecord = (AssistantBusinessAccountRecord) object;
          recordDTOs.add(businessAccountRecord.toDTO());
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    return recordDTOs;
  }
}
