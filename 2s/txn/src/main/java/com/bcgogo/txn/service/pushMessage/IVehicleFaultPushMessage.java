package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.AppVehicleFaultInfoDTO;
import com.bcgogo.txn.dto.pushMessage.enquiry.VehicleFaultParameter;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.VehicleDTO;

/**
 * Created with IntelliJ IDEA.
 * User: Hans
 * Date: 13-12-2
 * Time: 下午6:07
 */
public interface IVehicleFaultPushMessage {
  /**
   * 给店铺创建故障消息
   *
   * @param parameter {
   *                  appUserNo              必填
   *                  appUserName            必填
   *                  vehicleNo              必填
   *                  faultCode              必填
   *                  description            不必填
   *                  targetShopId           必填
   *                  vehicleFaultInfoId     必填
   *                  }
   * @return boolean
   */
  boolean createVehicleFaultMessage2Shop(VehicleFaultParameter parameter) throws Exception;

  boolean createVehicleFaultAlertMessage2Shop(AppUserDTO appUserDTO,VehicleDTO vehicleDTO,CustomerDTO customerDTO, FaultInfoToShopDTO faultInfoToShopDTO) throws Exception;

  boolean createVehicleFaultMessage2App(AppUserDTO appUserDTO, AppVehicleFaultInfoDTO appVehicleFaultInfoDTO) throws Exception;

  //初始化故障码推送消息
  void initFaultCodePushMessage();

  //创建店铺收到的故障消息相关的app用户信息，故障信息
  FaultInfoToShopDTO createFaultInfoToShop(Long receiveShopId, AppVehicleFaultInfoDTO appVehicleFaultInfoDTO, AppUserDTO appUserDTO, AppVehicleDTO appVehicleDTO);

  //创建店铺收到的报警
  FaultInfoToShopDTO createAlertInfoToShop(FaultInfoToShopDTO faultInfoToShopDTO);
}
