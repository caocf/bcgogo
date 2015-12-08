package com.bcgogo.txn.dto.pushMessage.enquiry;

import com.bcgogo.api.EnquiryDTO;
import com.bcgogo.api.EnquiryTargetShopDTO;
import com.bcgogo.utils.StringUtil;

/**
 * User: ZhangJuntao
 * Date: 13-11-13
 * Time: 下午1:35
 */
public class AppEnquiryParameter extends AbstractEnquiryParameter {
  private String vehicleNo;

  public AppEnquiryParameter() {
  }

  public AppEnquiryParameter(Long shopId, String appUserNo, Long enquiryId, String vehicleNo) {
    super(shopId, appUserNo, enquiryId);
    this.setVehicleNo(vehicleNo);
  }


  public AppEnquiryParameter(EnquiryTargetShopDTO enquiryTargetShopDTO, EnquiryDTO enquiryDTO) {
    super(enquiryTargetShopDTO.getTargetShopId(), enquiryDTO.getAppUserNo(), enquiryDTO.getId());
    this.setVehicleNo(enquiryDTO.getVehicleNo());
  }

  public String validate() {
    if (appUserNo == null) {
      return "app userno is null";
    }
    if (shopId == null) {
      return "shop id is null";
    }
    if (enquiryId == null) {
      return "enquiry order id is null";
    }
    if (StringUtil.isEmpty(vehicleNo)) {
      return "vehicleNo is null";
    }
    return "";
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

}
