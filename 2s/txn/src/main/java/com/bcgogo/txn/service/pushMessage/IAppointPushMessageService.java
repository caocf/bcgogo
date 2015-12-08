package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.txn.dto.AdvertDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.dto.pushMessage.appoint.AppAppointParameter;
import com.bcgogo.txn.dto.pushMessage.appoint.ShopAppointParameter;
import com.bcgogo.txn.dto.pushMessage.appoint.SysAppointParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.AppEnquiryParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.ShopQuoteEnquiryParameter;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-9-9
 * Time: 上午9:52
 */
public interface IAppointPushMessageService {
  /**
   * 店铺预约接受消息
   *
   * @param parameter {
   *                  shopId    创建者ID
   *                  appointOrderId
   *                  appUserNo
   *                  services  ","分割
   *                  appointTime
   *                  }
   */
  boolean createShopAcceptAppointMessage(ShopAppointParameter parameter) throws Exception;


  /**
   * 店铺预约拒绝消息
   *
   * @param parameter {
   *                  shopId    创建者ID
   *                  appointOrderId
   *                  appUserNo
   *                  services  ","分割
   *                  appointTime
   *                  }
   */
  boolean createShopRejectAppointMessage(ShopAppointParameter parameter) throws Exception;

  /**
   * 店铺预约取消消息
   *
   * @param parameter {
   *                  shopId    创建者ID
   *                  appointOrderId
   *                  appUserNo
   *                  services  ","分割
   *                  appointTime
   *                  }
   */
  boolean createShopCancelAppointMessage(ShopAppointParameter parameter) throws Exception;

  /**
   * 店铺预约结束消息
   *
   * @param parameter {
   *                  shopId    创建者ID
   *                  appointOrderId
   *                  appUserNo
   *                  services  ","分割
   *                  appointTime
   *                  }
   */
  boolean createShopFinishAppointMessage(ShopAppointParameter parameter) throws Exception;

  /**
   * disable 消息类型(shop修改消息)
   * 店铺预约修改消息
   *
   * @param parameter {
   *                  shopId    创建者ID
   *                  appointOrderId
   *                  appUserNo
   *                  services  ","分割
   *                  newServices  ","分割
   *                  appointTime
   *                  newAppointTime
   *                  }
   */
  boolean createShopChangeAppointMessage(ShopAppointParameter parameter) throws Exception;


  /**
   * APP预约取消消息
   *
   * @param parameter {
   *                  private String vehicleNo;
   *                  appUserNo   创建者
   *                  shopId      接受者
   *                  appointOrderId
   *                  services  ","分割
   *                  applyTime 申请时间
   *                  linkUrl 点击消息url(不必须)
   *                  }
   */
  boolean createAppCancelAppointMessage(AppAppointParameter parameter) throws Exception;

  /**
   * APP预约申请消息
   *
   * @param parameter {
   *                  private String vehicleNo;
   *                  appUserNo   创建者
   *                  shopId      接受者
   *                  appointOrderId
   *                  services  ","分割
   *                  applyTime 申请时间
   *                  linkUrl 点击消息url(不必须)
   *                  }
   */
  boolean createAppApplyAppointMessage(AppAppointParameter parameter) throws Exception;


  /**
   * @param parameter {
   *                  private String vehicleNo;
   *                  appUserNo   创建者
   *                  shopId      接受者
   *                  appointOrderId
   *                  services  ","分割
   *                  applyTime 申请时间
   *                  }
   * @throws Exception
   */
  boolean createSysAcceptAppointMessage(SysAppointParameter parameter) throws Exception;


  /**
   * 创建过期预约单提醒消息
   *
   * @param limit int
   */
  void createOverdueAppointRemindMessage(int limit) throws Exception;


  /**
   * 保养里程
   *
   * @param limit
   */
  void createAppVehicleMaintainMileageMessage(int limit) throws Exception;

  /**
   * 保养时间
   *
   * @param limit
   */
  void createAppVehicleMaintainTimeMessage(int limit) throws Exception;

  /**
   * 保险时间
   *
   * @param limit
   */
  void createAppVehicleInsuranceTimeMessage(int limit) throws Exception;

  void createAppVehicleInsuranceTimeMessage(List<AppVehicleDTO> appVehicleDTOList) throws Exception;

  /**
   * 验车时间
   *
   * @param limit
   */
  void createAppVehicleExamineTimeMessage(int limit) throws Exception;

  void createAppVehicleExamineTimeMessage(List<AppVehicleDTO> appVehicleDTOList) throws Exception;


  PushMessageDTO createAppFaultCodeMessage2App(AppVehicleDTO dto, AppUserDTO appUserDTO) throws Exception;

  public List<PushMessageDTO> createShopAdvertMessage2App(AdvertDTO advertDTO) throws Exception;

  public List<PushMessageDTO> sendVRegulationRecordMessage2App(AppVehicleDTO appVehicleDTO,AppUserDTO appUserDTO) throws Exception;

  List<PushMessageDTO> saveTalkMessage2App(String fromUserNo,AppUserDTO appUserDTO,String content,PushMessageType type) throws Exception ;

  void scheduleAppointOverdueRemindMsg();

}
