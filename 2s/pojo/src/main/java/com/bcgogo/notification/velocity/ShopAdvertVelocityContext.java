package com.bcgogo.notification.velocity;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.txn.dto.pushMessage.appoint.AppAppointParameter;
import com.bcgogo.txn.dto.pushMessage.appoint.ShopAppointParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.AppEnquiryParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.ShopQuoteEnquiryParameter;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.utils.DateUtil;

/**
 * User: lw
 * Date: 14-4-11
 * Time: 下午4:29
 */
public class ShopAdvertVelocityContext {
  private String shopName;      //店铺名字
  private String advertDateStr;//店铺广告时间

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getAdvertDateStr() {
    return advertDateStr;
  }

  public void setAdvertDateStr(String advertDateStr) {
    this.advertDateStr = advertDateStr;
  }
}
