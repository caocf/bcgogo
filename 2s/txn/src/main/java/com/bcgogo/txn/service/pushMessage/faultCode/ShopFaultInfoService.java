package com.bcgogo.txn.service.pushMessage.faultCode;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.FaultAlertType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.txn.Status;
import com.bcgogo.exception.PageException;
import com.bcgogo.notification.model.MessageTemplate;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.ShopFaultInfoListResult;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnReader;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.pushMessage.faultCode.FaultInfoToShop;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 上午11:54
 */
@Service
public class ShopFaultInfoService implements IShopFaultInfoService {
  private static final Logger LOG = LoggerFactory.getLogger(ShopFaultInfoService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public int countShopFaultInfoList(FaultInfoSearchConditionDTO searchCondition) {
    TxnReader reader = txnDaoManager.getReader();
    return reader.countShopFaultInfoList(searchCondition);
  }

  @Override
  public ShopFaultInfoListResult searchShopFaultInfoList(FaultInfoSearchConditionDTO searchCondition) throws PageException, ParseException {
    ShopFaultInfoListResult result = new ShopFaultInfoListResult();
    if (RegexUtils.isMobile(searchCondition.getMobile()) && StringUtil.isEmpty(searchCondition.getVehicleNo())) {
      searchCondition.setMobiles(ServiceManager.getService(ICustomerService.class).getAppUserMobileByContactMobile(searchCondition.getShopId(), searchCondition.getMobile()));
    }
    int totalRows = this.countShopFaultInfoList(searchCondition);
    Pager pager = new Pager(totalRows, searchCondition.getStartPageNo(), searchCondition.getMaxRows());
    result.setPager(pager);
    searchShopFaultInfoListInternal(searchCondition, result);
    searchCondition.setTimeStart(DateUtil.getStartTimeOfToday());
    searchCondition.setTimeEnd(System.currentTimeMillis());
    result.setTodayTotalRows(this.countShopFaultInfoList(searchCondition));
    searchCondition.setTimeStart(DateUtil.getStartTimeOfYesterday());
    searchCondition.setTimeEnd(DateUtil.getEndTimeOfYesterday());
    result.setYesterdayTotalRows(this.countShopFaultInfoList(searchCondition));
    result.computeMoreTotalRows(totalRows);
    return result;
  }

  @Override
  public List<FaultInfoToShopDTO> findShopFaultInfoList(FaultInfoSearchConditionDTO searchCondition)  {
    TxnReader reader = txnDaoManager.getReader();
    List<FaultInfoToShopDTO> list = reader.searchShopFaultInfoList_(searchCondition);
    Set<String> appUserNos = new HashSet<String>();
    Set<String> vehicleNos = new HashSet<String>();
    for (FaultInfoToShopDTO entity : list) {
      if (StringUtil.isNotEmpty(entity.getAppUserNo())) {
        appUserNos.add(entity.getAppUserNo());
      }
      if (StringUtils.isNotEmpty(entity.getVehicleNo())) {
        vehicleNos.add(entity.getVehicleNo());
      }
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    Map<String, VehicleDTO> vehicleDTOMap = vehicleService.getVehicleDTOMapByLicenceNo(searchCondition.getShopId(), vehicleNos);
    //  匹配逻辑字段不全，等最新代码合并之后再做修改 todo by qxy
    //    IAppUserCustomerMatchService matchService = ServiceManager.getService(IAppUserCustomerMatchService.class);
    //    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    //    Map<String, CustomerDTO> customerDTOMap = matchService.getAppUserNoCustomerDTOMapByAppUserNosAndShopId(appUserNos, searchCondition.getShopId());
    //    Map<String,List<AppUserCustomerDTO>> appUserNoAppUserCustomerDTOs = appUserService.getAppUserCustomerMapByAppUserNosAndShopId(appUserNos, searchCondition.getShopId());

    Set<Long> toGetCustomerVehicleIds = new HashSet<Long>();
    if (CollectionUtils.isNotEmpty(list)) {
      for (FaultInfoToShopDTO entity : list) {
        VehicleDTO vehicleDTO = vehicleDTOMap.get(entity.getVehicleNo());
        if (vehicleDTO != null) {
          entity.setVehicleId(vehicleDTO.getId());
          toGetCustomerVehicleIds.add(vehicleDTO.getId());
        }
      }
    }
    Map<Long, CustomerDTO> vehicleIdCustomerMap = userService.getVehicleIdCustomerMapByVehicleIds(searchCondition.getShopId(), toGetCustomerVehicleIds);
    if (CollectionUtils.isNotEmpty(list)) {
      for (FaultInfoToShopDTO faultInfoToShopDTO : list) {
        if (faultInfoToShopDTO != null) {
          if (faultInfoToShopDTO.getVehicleId() != null) {
            CustomerDTO customerDTO = vehicleIdCustomerMap.get(faultInfoToShopDTO.getVehicleId());
            if (customerDTO != null) {
              faultInfoToShopDTO.fromCustomerDTO(customerDTO);
            }
          }
        }
      }
    }
    return list;
  }

  @Override
  public List<String> getShopFaultInfoVehicleNoSuggestion(Long shopId, String keyword) {
    return txnDaoManager.getReader().getShopFaultInfoVehicleNoSuggestion(shopId, keyword);
  }

  @Override
  public List<String> getShopFaultInfoMobileSuggestion(Long shopId, String keyword) {
    return txnDaoManager.getReader().getShopFaultInfoMobileSuggestion(shopId, keyword);
  }

  @Override
  public void deleteShopFaultInfo(Long... ids) {
    if (ArrayUtil.isEmpty(ids)) return;
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (Long id : ids) {
        FaultInfoToShop entity = writer.getById(FaultInfoToShop.class, id);
        if (entity != null) {
          entity.setStatus(Status.DELETED);
          writer.saveOrUpdate(entity);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateShopFaultInfo2SendMessage(Long id) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      FaultInfoToShop entity = writer.getById(FaultInfoToShop.class, id);
      if (entity != null) {
        entity.setIsSendMessage(YesNo.YES);
        writer.saveOrUpdate(entity);
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateShopFaultInfo2CreateAppointOrder(Long id) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      FaultInfoToShop entity = writer.getById(FaultInfoToShop.class, id);
      if (entity != null) {
        entity.setIsCreateAppointOrder(YesNo.YES);
        writer.saveOrUpdate(entity);
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  private void searchShopFaultInfoListInternal1(FaultInfoSearchConditionDTO searchCondition, ShopFaultInfoListResult result) {
    TxnReader reader = txnDaoManager.getReader();
    List<FaultInfoToShop> list = reader.searchShopFaultInfoList(searchCondition);
    Set<String> appUserNos = new HashSet<String>();
    Set<String> vehicleNos = new HashSet<String>();
    for (FaultInfoToShop entity : list) {
      if (StringUtil.isNotEmpty(entity.getAppUserNo())) {
        appUserNos.add(entity.getAppUserNo());
      }
      if (StringUtils.isNotEmpty(entity.getVehicleNo())) {
        vehicleNos.add(entity.getVehicleNo());
      }
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    Map<String, VehicleDTO> vehicleDTOMap = vehicleService.getVehicleDTOMapByLicenceNo(searchCondition.getShopId(), vehicleNos);
    //  匹配逻辑字段不全，等最新代码合并之后再做修改 todo by qxy
    //    IAppUserCustomerMatchService matchService = ServiceManager.getService(IAppUserCustomerMatchService.class);
    //    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    //    Map<String, CustomerDTO> customerDTOMap = matchService.getAppUserNoCustomerDTOMapByAppUserNosAndShopId(appUserNos, searchCondition.getShopId());
    //    Map<String,List<AppUserCustomerDTO>> appUserNoAppUserCustomerDTOs = appUserService.getAppUserCustomerMapByAppUserNosAndShopId(appUserNos, searchCondition.getShopId());


    List<FaultInfoToShopDTO> faultInfoToShopDTOs = new ArrayList<FaultInfoToShopDTO>();
    Set<Long> toGetCustomerVehicleIds = new HashSet<Long>();
    if (CollectionUtils.isNotEmpty(list)) {
      for (FaultInfoToShop entity : list) {
        FaultInfoToShopDTO faultInfoToShopDTO = entity.toDTO();
        VehicleDTO vehicleDTO = vehicleDTOMap.get(entity.getVehicleNo());
        if (vehicleDTO != null) {
          faultInfoToShopDTO.setVehicleId(vehicleDTO.getId());
          toGetCustomerVehicleIds.add(vehicleDTO.getId());
        }
        faultInfoToShopDTOs.add(faultInfoToShopDTO);
      }
    }
    Map<Long, CustomerDTO> vehicleIdCustomerMap = userService.getVehicleIdCustomerMapByVehicleIds(searchCondition.getShopId(), toGetCustomerVehicleIds);
    if (CollectionUtils.isNotEmpty(faultInfoToShopDTOs)) {
      for (FaultInfoToShopDTO faultInfoToShopDTO : faultInfoToShopDTOs) {
        if (faultInfoToShopDTO != null) {
          if (faultInfoToShopDTO.getVehicleId() != null) {
            CustomerDTO customerDTO = vehicleIdCustomerMap.get(faultInfoToShopDTO.getVehicleId());
            if (customerDTO != null) {
              faultInfoToShopDTO.fromCustomerDTO(customerDTO);
            }
          }
          if (DateUtil.isInToday(faultInfoToShopDTO.getFaultCodeReportTime())) {
            result.getTodayShopFaultInfoList().add(faultInfoToShopDTO);
          } else if (DateUtil.isInYesterday(faultInfoToShopDTO.getFaultCodeReportTime())) {
            result.getYesterdayShopFaultInfoList().add(faultInfoToShopDTO);
          } else {
            result.getMoreShopFaultInfoList().add(faultInfoToShopDTO);
          }
        }
      }
    }
  }

  private void searchShopFaultInfoListInternal(FaultInfoSearchConditionDTO searchCondition, ShopFaultInfoListResult result) {
    List<FaultInfoToShopDTO> faultInfoToShopDTOs = getShopFaultInfoList(searchCondition);
    if (CollectionUtils.isNotEmpty(faultInfoToShopDTOs)) {
      for (FaultInfoToShopDTO faultInfoToShopDTO : faultInfoToShopDTOs) {
        if (faultInfoToShopDTO != null) {
          if (DateUtil.isInToday(faultInfoToShopDTO.getFaultCodeReportTime())) {
            result.getTodayShopFaultInfoList().add(faultInfoToShopDTO);
          } else if (DateUtil.isInYesterday(faultInfoToShopDTO.getFaultCodeReportTime())) {
            result.getYesterdayShopFaultInfoList().add(faultInfoToShopDTO);
          } else {
            result.getMoreShopFaultInfoList().add(faultInfoToShopDTO);
          }
        }
      }
    }
  }

  @Override
  public List<FaultInfoToShopDTO> getShopFaultInfoList(FaultInfoSearchConditionDTO searchCondition) {
    TxnReader reader = txnDaoManager.getReader();
    List<FaultInfoToShop> list = reader.searchShopFaultInfoList(searchCondition);
    Set<String> appUserNos = new HashSet<String>();
    Set<String> vehicleNos = new HashSet<String>();
    for (FaultInfoToShop entity : list) {
      if (StringUtil.isNotEmpty(entity.getAppUserNo())) {
        appUserNos.add(entity.getAppUserNo());
      }
      if (StringUtils.isNotEmpty(entity.getVehicleNo())) {
        vehicleNos.add(entity.getVehicleNo());
      }
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    Map<String, VehicleDTO> vehicleDTOMap = vehicleService.getVehicleDTOMapByLicenceNo(searchCondition.getShopId(), vehicleNos);
    //  匹配逻辑字段不全，等最新代码合并之后再做修改 todo by qxy
    //    IAppUserCustomerMatchService matchService = ServiceManager.getService(IAppUserCustomerMatchService.class);
    //    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    //    Map<String, CustomerDTO> customerDTOMap = matchService.getAppUserNoCustomerDTOMapByAppUserNosAndShopId(appUserNos, searchCondition.getShopId());
    //    Map<String,List<AppUserCustomerDTO>> appUserNoAppUserCustomerDTOs = appUserService.getAppUserCustomerMapByAppUserNosAndShopId(appUserNos, searchCondition.getShopId());

    List<FaultInfoToShopDTO> faultInfoToShopDTOs = new ArrayList<FaultInfoToShopDTO>();
    Set<Long> toGetCustomerVehicleIds = new HashSet<Long>();
    if (CollectionUtils.isNotEmpty(list)) {
      for (FaultInfoToShop entity : list) {
        FaultInfoToShopDTO faultInfoToShopDTO = entity.toDTO();
        VehicleDTO vehicleDTO = vehicleDTOMap.get(entity.getVehicleNo());
        if (vehicleDTO != null) {
          faultInfoToShopDTO.setVehicleId(vehicleDTO.getId());
          toGetCustomerVehicleIds.add(vehicleDTO.getId());
        }
        faultInfoToShopDTOs.add(faultInfoToShopDTO);
      }
    }
    Map<Long, CustomerDTO> vehicleIdCustomerMap = userService.getVehicleIdCustomerMapByVehicleIds(searchCondition.getShopId(), toGetCustomerVehicleIds);
    if (CollectionUtils.isNotEmpty(faultInfoToShopDTOs)) {
      for (FaultInfoToShopDTO faultInfoToShopDTO : faultInfoToShopDTOs) {
        if (faultInfoToShopDTO != null) {
          if (faultInfoToShopDTO.getVehicleId() != null) {
            CustomerDTO customerDTO = vehicleIdCustomerMap.get(faultInfoToShopDTO.getVehicleId());
            if (customerDTO != null) {
              faultInfoToShopDTO.fromCustomerDTO(customerDTO);
            }
          }
        }
      }
    }
    return faultInfoToShopDTOs;
  }

  @Override
  public List<FaultInfoToShopDTO> getFaultInfoToShopDTOsByIds(Long shopId, Long... ids){
    if(shopId == null || ArrayUtils.isEmpty(ids)){
      return new ArrayList<FaultInfoToShopDTO>();
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<FaultInfoToShop> faultInfoToShops = writer.getFaultInfoToShopByIds(shopId,ids);
    List<FaultInfoToShopDTO> faultInfoToShopDTOs = new ArrayList<FaultInfoToShopDTO>();
    if(CollectionUtils.isNotEmpty(faultInfoToShops)){
      for(FaultInfoToShop faultInfoToShop : faultInfoToShops){
        faultInfoToShopDTOs.add(faultInfoToShop.toDTO());
      }
    }
    return faultInfoToShopDTOs;
  }

  @Override
  public String getUnhandledFaultCodes(Long shopId, String vehicleNo) {
    if(shopId != null && StringUtils.isNotBlank(vehicleNo)){
      TxnWriter writer = txnDaoManager.getWriter();
      List<FaultInfoToShop> faultInfoToShops = writer.getUnHandledFaultInfoToShopsByVehicleNo(shopId,vehicleNo);
      if(CollectionUtils.isNotEmpty(faultInfoToShops)){
        StringBuilder sb = new StringBuilder();
        for(FaultInfoToShop faultInfoToShop : faultInfoToShops){
          if(faultInfoToShop != null && StringUtils.isNotBlank(faultInfoToShop.getFaultCode())){
            if(sb.length()>0){
              sb.append("/");
            }
            sb.append(faultInfoToShop.getFaultCode());
          }
        }
        return sb.toString();
      }
    }
    return null;
  }

  @Override
  public int countShopFaultInfoByVehicleNo(Long shopId, String vehicleNo) {
    if(shopId != null && StringUtils.isNotBlank(vehicleNo)){
      TxnWriter writer = txnDaoManager.getWriter();
      return writer.countShopFaultInfoByVehicleNo(shopId,vehicleNo);
    }
    return 0;
  }

  @Override
  public FaultInfoToShopDTO getShopFaultInfo(Long id) {
    if (id == null) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    FaultInfoToShop entity = writer.getById(FaultInfoToShop.class, id);
    return entity == null ? null : entity.toDTO();
  }

 public Map<String, Object> getSopFaultInfoMsgContent(Long shopId,String code, String time, FaultAlertType faultAlertType,String faultAlertTypeValue,String licenceNo) {
   Map<String, Object> map = new HashMap<String, Object>();
   MessageTemplate template = ServiceManager.getService(INotificationService.class).getMsgTemplateByType(SmsConstant.MsgTemplateTypeConstant.faultInfoCodeMsg);
   if (template != null) {
     //您好，您的车辆{licenceNo}于{time}出现{faultCode}，请尽快来店检查，详情咨询{mobile}。
     String sms = template.getContent();

     if (StringUtil.isEmpty(code) && faultAlertType != FaultAlertType.FAULT_CODE) {
       sms = sms.replace(SmsConstant.MsgTemplateContentConstant.faultCode, faultAlertTypeValue);
     } else {
       sms = sms.replace(SmsConstant.MsgTemplateContentConstant.faultCode, code + SmsConstant.MsgTemplateContentConstant.faultCodeContent);
     }
     sms = sms.replace(SmsConstant.MsgTemplateContentConstant.time, time);
     if(StringUtils.isEmpty(licenceNo)){
       sms = sms.replace(SmsConstant.MsgTemplateContentConstant.licenceNo, licenceNo);
     }else {
       sms = sms.replace(SmsConstant.MsgTemplateContentConstant.licenceNo, "");
     }

     ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
     if (shopDTO != null) {
       sms = sms.replace(SmsConstant.MsgTemplateContentConstant.mobile, shopDTO.getMobile());
       String name = StringUtil.isEmpty(shopDTO.getShortname()) ? shopDTO.getName() : shopDTO.getShortname();
       map.put("name", "【" + (StringUtil.isEmpty(name) ? SmsConstant.SmsYiMeiConstant.DEFAULT_SENDER_NAME : name) + "】");
       map.put("content", sms);
     }
   }
   return map;
 }

  @Override
  public List<FaultInfoToShopDTO> getFaultInfoListByCondition(FaultInfoSearchConditionDTO searchCondition) {
    TxnReader reader = txnDaoManager.getReader();
    List<FaultInfoToShop> list = reader.searchShopFaultInfoList(searchCondition);
    List<FaultInfoToShopDTO> faultInfoToShopDTOs = new ArrayList<FaultInfoToShopDTO>();
    if (CollectionUtil.isEmpty(list)) {
      return faultInfoToShopDTOs;
    }
    for (FaultInfoToShop faultInfoToShop : list) {
      faultInfoToShopDTOs.add(faultInfoToShop.toDTO());
    }
    return faultInfoToShopDTOs;
  }



  @Override
  public int countShopFaultInfoList_(FaultInfoSearchConditionDTO searchCondition) {
    TxnReader reader = txnDaoManager.getReader();
    return reader.countShopFaultInfoList_(searchCondition);
  }

  @Override
  public List<FaultInfoToShop> getShopFaultInfoByFaultCode(FaultInfoSearchConditionDTO searchCondition){
    TxnReader reader = txnDaoManager.getReader();
    List<FaultInfoToShop> faultInfoToShops = reader.getShopFaultInfoByFaultCode(searchCondition);
    return  faultInfoToShops;
  }

}
