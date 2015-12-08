package com.bcgogo.user.service;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.api.MileageDTO;
import com.bcgogo.api.RescueDTO;
import com.bcgogo.api.response.InsuranceCompanyResponse;
import com.bcgogo.api.response.OneKeyRescueResponse;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.user.userGuide.SosStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.mileage.MileageInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.sos.SosInfoSearchConditionDTO;
import com.bcgogo.user.dto.AccidentSpecialistDTO;
import com.bcgogo.user.dto.InsuranceCompanyDTO;
import com.bcgogo.user.model.InsuranceCompany;
import com.bcgogo.user.model.Rescue;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.app.AppUserCustomer;
import com.bcgogo.user.model.app.AppVehicle;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: zj
 * Date: 2015-3-30
 * Time: 15:47
 */
@Component
public class RescueService implements IRescueService {
  private static final Logger LOG = LoggerFactory.getLogger(RescueService.class);

  @Autowired
  private UserDaoManager daoManager;


  /**
   * 获取一键救援信息
   * 1.事故专员电话
   * 2.后视镜使用问题客户电话
   * 3.保险公司列表（名字和电话）
   *
   * @param appUserNo
   * @return oneKeyRescueResponse
   */
  @Override
  public OneKeyRescueResponse findOneKeyRescueDetails(String appUserNo) {
    IUserService iUserService = ServiceManager.getService(IUserService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    OneKeyRescueResponse oneKeyRescueResponse = new OneKeyRescueResponse();
    UserWriter writer = daoManager.getWriter();
    //事故专员电话（可能是多个）
    AppUserCustomer appUserCustomer = writer.getAppUserCustomerByAppUserNo(appUserNo);
    Long shopId = appUserCustomer.getShopId();
    List<AccidentSpecialistDTO> accidentSpecialistDTOList = userService.getAccidentSpecialistByOpenId(shopId, null);
    List<String> shopMobile = new ArrayList<String>();
    if (!CollectionUtils.isEmpty(accidentSpecialistDTOList)) {
      for (AccidentSpecialistDTO accidentSpecialistDTO : accidentSpecialistDTOList) {
        shopMobile.add(accidentSpecialistDTO.getMobile());
      }
    }
    oneKeyRescueResponse.setAccident_mobile(shopMobile);
    //后视镜问题联系电话
    oneKeyRescueResponse.setMirror_mobile(getMirrorMobile());
    //所有保险公司
    List<InsuranceCompanyDTO> insuranceCompanyDTOs = getAllInsuranceCompanyDTOs();
    //排在第一位的保险公司信息
    List<AppVehicle> appVehicles = writer.getAppVehicleByAppUserNo(appUserNo);
    InsuranceCompanyDTO insuranceCompanyDTO_first = new InsuranceCompanyDTO();
    if (!CollectionUtils.isEmpty(appVehicles)) {
      AppVehicle appVehicle = CollectionUtil.getFirst(appVehicles);
      if (appVehicle.getInsuranceCompanyId() != null && appVehicle.getInsuranceCompanyId() != 0) {
        InsuranceCompany insuranceCompany = iUserService.getInsuranceCompanyDTOById(appVehicle.getInsuranceCompanyId());
        if (insuranceCompany != null) {
          insuranceCompanyDTO_first.setMobile(insuranceCompany.getMobile());
          insuranceCompanyDTO_first.setName(insuranceCompany.getName());
          oneKeyRescueResponse.setInsuranceCompanyDTO(insuranceCompanyDTO_first);
        }
      }
    }
    //去掉排在第一位的保险公司列表
    List<InsuranceCompanyDTO> insuranceCompanyDTOList = new ArrayList<InsuranceCompanyDTO>();
    for (InsuranceCompanyDTO insuranceCompanyDTO : insuranceCompanyDTOs) {
      if (!insuranceCompanyDTO.getName().equals(insuranceCompanyDTO_first.getName())) {
        insuranceCompanyDTOList.add(insuranceCompanyDTO);
      }
    }
    oneKeyRescueResponse.setInsuranceCompanyDTOs(insuranceCompanyDTOList);
    return oneKeyRescueResponse;
  }

  /**
   * 获取保险公司信息列表
   *
   * @return insuranceCompanyResponse
   */
  @Override
  public InsuranceCompanyResponse findInsuranceCompanyResponseDetails() {
    InsuranceCompanyResponse insuranceCompanyResponse = new InsuranceCompanyResponse();
    List<InsuranceCompanyDTO> insuranceCompanyDTOs = getAllInsuranceCompanyDTOs();
    insuranceCompanyResponse.setInsuranceCompanyDTOs(insuranceCompanyDTOs);
    return insuranceCompanyResponse;
  }

  //获取shop
  public ShopDTO getShopById(Long id) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    return configService.getShopById(id);
  }

  //获取Config表中配置的“后视镜使用问题客户电话”
  public static String getMirrorMobile() {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String mirror_mobile = configService.getConfig("mirror_mobile", ShopConstant.BC_SHOP_ID);
    return mirror_mobile;
  }

  //获取保险公司列表（名字和电话）
  public List<InsuranceCompanyDTO> getAllInsuranceCompanyDTOs() {
    return daoManager.getWriter().getAllInsuranceCompanyDTOs();
  }

  @Override
  public void saveOrUpdateRescue(RescueDTO rescueDTO) {
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    Rescue rescue = null;
    try {
      if (rescueDTO.getId() != null) {
        rescue = writer.getById(Rescue.class, rescueDTO.getId());
      } else {
        rescue = new Rescue();
      }
      rescue.fromDTO(rescueDTO);
      writer.saveOrUpdate(rescue);
      writer.commit(status);
      rescueDTO.setId(rescue.getId());
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<RescueDTO> getRescueDTOsByShopId(Long shopId, int start, int limit) {
    return null;
  }

  @Override
  public List<RescueDTO> getRescueDTOsByShopId(Long shopId, SosInfoSearchConditionDTO sosInfoSearchConditionDTO) {
    UserWriter writer = daoManager.getWriter();
    if(sosInfoSearchConditionDTO.getIsUntreated()!=null&&sosInfoSearchConditionDTO.getIsDeleted()!=null){
      sosInfoSearchConditionDTO.setStatus(null);
    }else if(sosInfoSearchConditionDTO.getIsUntreated()!=null&&sosInfoSearchConditionDTO.getIsDeleted()==null){
      sosInfoSearchConditionDTO.setStatus("UNTREATED");
    }else if(sosInfoSearchConditionDTO.getIsUntreated()==null&&sosInfoSearchConditionDTO.getIsDeleted()!=null){
      sosInfoSearchConditionDTO.setStatus("TREATED");
    }else{
      sosInfoSearchConditionDTO.setStatus(null);
    }
    return writer.getRescueDTOs_page(shopId, sosInfoSearchConditionDTO);
  }

  @Override
  public int countGetRescueDTOs(Long shopId,SosInfoSearchConditionDTO sosInfoSearchConditionDTO) {
    UserWriter writer = daoManager.getWriter();
    if(sosInfoSearchConditionDTO.getIsUntreated()!=null&&sosInfoSearchConditionDTO.getIsDeleted()!=null){
      sosInfoSearchConditionDTO.setStatus(null);
    }else if(sosInfoSearchConditionDTO.getIsUntreated()!=null&&sosInfoSearchConditionDTO.getIsDeleted()==null){
      sosInfoSearchConditionDTO.setStatus("UNTREATED");
    }else if(sosInfoSearchConditionDTO.getIsUntreated()==null&&sosInfoSearchConditionDTO.getIsDeleted()!=null){
      sosInfoSearchConditionDTO.setStatus("TREATED");
    }else{
      sosInfoSearchConditionDTO.setStatus(null);
    }
    return writer.countGetRescueDTOs(shopId,sosInfoSearchConditionDTO);
  }

  @Override
  public List<MileageDTO> getMileageDTOsByShopId(Long shopId, MileageInfoSearchConditionDTO mileageInfoSearchConditionDTO) {
    UserWriter writer = daoManager.getWriter();
    return writer.getMileageDTOs_page(shopId, mileageInfoSearchConditionDTO);
  }

  @Override
  public int countGetMileageDTOs(Long shopId,MileageInfoSearchConditionDTO mileageInfoSearchConditionDTO) {
    UserWriter writer = daoManager.getWriter();
    return writer.countMileageDTOs(shopId,mileageInfoSearchConditionDTO);
  }

  @Override
  public void deleteShopSosInfo(Long... ids) {
    if (ArrayUtil.isEmpty(ids)) return;
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      for (Long id : ids) {
        Rescue entity = writer.getById(Rescue.class, id);
        if (entity != null) {
          entity.setSosStatus(SosStatus.DELETED);
          writer.saveOrUpdate(entity);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void detailShopSosInfo(Long... ids) {
    if (ArrayUtil.isEmpty(ids)) return;
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      for (Long id : ids) {
        Rescue entity = writer.getById(Rescue.class, id);
        if (entity != null) {
          entity.setSosStatus(SosStatus.TREATED);
          writer.saveOrUpdate(entity);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateShopMileageInfo(String appUserNo,long shopId) {
    if (StringUtil.isEmpty(appUserNo)) return;
//    List<CustomerVehicleDTO> customerVehicleDTOList = new ArrayList<CustomerVehicleDTO>();
    UserWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    AppVehicle entity = null;
    try {
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      AppUserCustomerDTO appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNoAndShopId(appUserNo, shopId));
      if(appUserCustomerDTO!=null){
//        for (CustomerVehicle cv : writer.getVehicleByCustomerId(appUserCustomerDTO.getCustomerId())) {
//          customerVehicleDTOList.add(cv.toDTO());
//        }
        entity = writer.getById(AppVehicle.class, appUserCustomerDTO.getAppVehicleId());
      }
      if (entity != null) {
//        CustomerVehicleDTO customerVehicleDTO = CollectionUtil.getFirst(customerVehicleDTOList) ;
        Double  maintainMileagePeriod =  entity.getMaintainPeriod() == null? 0:entity.getMaintainPeriod();         //保养里程周期
        Double  curMileage =  entity.getCurrentMileage() == null? 0:entity.getCurrentMileage();      //当前里程
        Double newNextMaintainMileage = curMileage + maintainMileagePeriod;                                           //下次保养里程
        entity.setNextMaintainMileage(newNextMaintainMileage);
        writer.saveOrUpdate(entity);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


}
