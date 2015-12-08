package com.bcgogo.txn.dto.pushMessage.appoint;

import org.apache.commons.lang.StringUtils;

/**
 * User: ZhangJuntao
 * Date: 13-9-12
 * Time: 上午10:51
 */
public class SysAppointParameter extends AppAppointParameter{
  public SysAppointParameter() {
  }

  public SysAppointParameter(String appUserNo, String vehicleNo, Long shopId, Long appointOrderId, Long applyTime, String services, String linkUrl) {
    super(appUserNo, vehicleNo, shopId, appointOrderId, applyTime, services, linkUrl);
  }

//  public String validate() {
//    String result = super.validate();
//    if (StringUtils.isNotBlank(result)) return result;
//    if (StringUtils.isBlank(getVehicleNo())) return "vehicle no is null";
//    return "";
//  }

}
