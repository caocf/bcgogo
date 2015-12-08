package com.bcgogo.api;

import com.bcgogo.enums.app.EnquiryStatus;
import com.bcgogo.enums.app.EnquiryTargetShopStatus;
import com.bcgogo.enums.app.ValidateConstant;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.utils.RegexUtils;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-17
 * Time: 上午11:07
 * 询价单
 */
public class EnquiryDTO implements Serializable {
  private Long id;
  private String appUserNo;
  private String description; //询价单描述
  private AppUserImageDTO[] enquiryImages; //询价单图片信息
  private EnquiryTargetShopDTO[] enquiryTargetShops;//询价单发送店铺信息
  private EnquiryShopResponseDTO[] enquiryShopResponses; //询价单店铺回复信息
  private EnquiryStatus status;  //询价单状态
  private String statusStr;//状态的描述
  private Long billId;  //询价单关联的我的账单Id
  private Long createTime; //创建时间
  private Long lastUpdateTime;//最后更新时间，web的询价时间也用这个字段
  private String appUserName;
  private String vehicleNo;
  private String appUserMobile;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public AppUserImageDTO[] getEnquiryImages() {
    return enquiryImages;
  }

  public void setEnquiryImages(AppUserImageDTO[] enquiryImages) {
    this.enquiryImages = enquiryImages;
  }

  public EnquiryTargetShopDTO[] getEnquiryTargetShops() {
    return enquiryTargetShops;
  }

  public void setEnquiryTargetShops(EnquiryTargetShopDTO[] enquiryTargetShops) {
    this.enquiryTargetShops = enquiryTargetShops;
  }

  public EnquiryShopResponseDTO[] getEnquiryShopResponses() {
    return enquiryShopResponses;
  }

  public void setEnquiryShopResponses(EnquiryShopResponseDTO[] enquiryShopResponses) {
    this.enquiryShopResponses = enquiryShopResponses;
  }

  public EnquiryStatus getStatus() {
    return status;
  }

  public void setStatus(EnquiryStatus status) {
    this.status = status;
    if(status != null){
      setStatusStr(status.getName());
    }else {
      setStatusStr("");
    }
  }

  public String getStatusStr() {
    return statusStr;
  }

  public void setStatusStr(String statusStr) {
    this.statusStr = statusStr;
  }

  public Long getBillId() {
    return billId;
  }

  public void setBillId(Long billId) {
    this.billId = billId;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public Long getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(Long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public String getAppUserName() {
    return appUserName;
  }

  public void setAppUserName(String appUserName) {
    this.appUserName = appUserName;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getAppUserMobile() {
    return appUserMobile;
  }

  public void setAppUserMobile(String appUserMobile) {
    this.appUserMobile = appUserMobile;
  }

  public String validateSave() {
    if(StringUtils.isEmpty(description) && !haveEnquiryImage()){
      return ValidateMsg.APP_ENQUIRY_EMPTY.getValue();
    }
    if(StringUtils.isNotEmpty(description) && description.length()> ValidateConstant.ENQUIRY_DESCRIPTION_MAX_LENGTH){
      return ValidateMsg.APP_ENQUIRY_EMPTY.getValue();
    }
    if (checkValidImageSize() > ValidateConstant.ENQUIRY_IMAGES_MAX_SIZE) {
      return ValidateMsg.APP_ENQUIRY_IMAGES_TOO_MUCH.getValue();
    }
    if (checkValidTargetShopSize() > ValidateConstant.ENQUIRY_TARGET_SHOP_MAX_SIZE) {
      return ValidateMsg.APP_ENQUIRY_TARGET_SHOP_TOO_MUCH.getValue();
    }else if(checkValidTargetShopSize() > 0){
      for(EnquiryTargetShopDTO enquiryTargetShopDTO : getEnquiryTargetShops()){
        if(enquiryTargetShopDTO != null && enquiryTargetShopDTO.getTargetShopId() != null){
          if(StringUtils.isEmpty(enquiryTargetShopDTO.getTargetShopName())){
            return ValidateMsg.APP_ENQUIRY_TARGET_SHOP_NO_NAME.getValue();
          }
          if(enquiryTargetShopDTO.getStatus()== null
              || enquiryTargetShopDTO.getStatus() == EnquiryTargetShopStatus.SENT
              || enquiryTargetShopDTO.getStatus() == EnquiryTargetShopStatus.DISABLED){
            return ValidateMsg.APP_ENQUIRY_SAVE_TARGET_SHOP_STATUS_ILLEGAL.getValue();
          }
        }
      }
    }

    if (StringUtil.isNotEmpty(getAppUserMobile())) {
      if (getAppUserMobile().length() > ValidateConstant.ENQUIRY_APP_USER_MOBILE_MAX_LENGTH) {
        return ValidateMsg.APP_ENQUIRY_APP_USER_MOBILE_TOO_LONG.getValue();
      } else if (RegexUtils.isNotMobile(getAppUserMobile())) {
        return ValidateMsg.APP_ENQUIRY_APP_USER_MOBILE_ILLEGAL.getValue();
      }
    }

    if (StringUtils.isEmpty(getVehicleNo())) {
      return ValidateMsg.APP_ENQUIRY_APP_VEHICLE_NO_EMPTY.getValue();
    } else {
      if (!RegexUtils.isVehicleNo(getVehicleNo())) {
        return ValidateMsg.APP_ENQUIRY_APP_VEHICLE_NO_ILLEGAL.getValue();
      }
    }

    if(StringUtils.isNotEmpty(getAppUserName()) && getAppUserName().length() > ValidateConstant.ENQUIRY_APP_USER_NAME_MAX_LENGTH){
        return ValidateMsg.APP_ENQUIRY_APP_USER_NAME_TOO_LONG.getValue();
    }
    return null;
  }

  public String validateUpdateFromPage(){
    if(id == null){
      return ValidateMsg.APP_ENQUIRY_UPDATE_NOT_EXIST.getValue();
    }
    return  validateSave();
  }

  public boolean haveEnquiryImage() {
    boolean isHaveEnquiryImage = false;
    if (!ArrayUtils.isEmpty(getEnquiryImages())) {
      for (AppUserImageDTO appUserImageDTO : getEnquiryImages()) {
        if (appUserImageDTO != null && StringUtils.isNotEmpty(appUserImageDTO.getImagePath())) {
          isHaveEnquiryImage = true;
          break;
        }
      }
    }
    return isHaveEnquiryImage;
  }

  public int checkValidImageSize() {
    int imageSize = 0;
    if (!ArrayUtils.isEmpty(getEnquiryImages())) {
      for (AppUserImageDTO appUserImageDTO : getEnquiryImages()) {
        if (appUserImageDTO != null && StringUtils.isNotEmpty(appUserImageDTO.getImagePath())) {
          imageSize++;
        }
      }
    }
    return imageSize;
  }

  public int checkValidTargetShopSize() {
    int targetSize = 0;
    if (!ArrayUtils.isEmpty(getEnquiryTargetShops())) {
      for (EnquiryTargetShopDTO enquiryTargetShopDTO : getEnquiryTargetShops()) {
        if (enquiryTargetShopDTO != null && enquiryTargetShopDTO.getTargetShopId() != null) {
          targetSize++;
        }
      }
    }
    return targetSize;
  }

  public String validateUpdateFromDB() {
    if(status == EnquiryStatus.DISABLED){
      return ValidateMsg.APP_ENQUIRY_UPDATE_DISABLED.getValue();
    }else if(status == EnquiryStatus.SENT){
      return ValidateMsg.APP_ENQUIRY_UPDATE_SENT.getValue();
    }
    return null;
  }

  public String validateSendFromDB() {
    if(status == EnquiryStatus.DISABLED){
      return ValidateMsg.APP_ENQUIRY_UPDATE_DISABLED.getValue();
    }else if(status == EnquiryStatus.SENT){
      return ValidateMsg.APP_ENQUIRY_UPDATE_SENT.getValue();
    }
    return null;
  }

    //Enquiry 保存后处理 targetShop 如果shopId 相同的，取后者。
  public List<EnquiryTargetShopDTO> generateEnquiryTargetShopDTO() {
    List<EnquiryTargetShopDTO> enquiryTargetShopDTOList = new ArrayList<EnquiryTargetShopDTO>();
    if (!ArrayUtils.isEmpty(this.getEnquiryTargetShops())) {
      int enquiryTargetShopSequence = 0;
      Map<Long, EnquiryTargetShopDTO> enquiryTargetShopDTOMap = new HashMap<Long, EnquiryTargetShopDTO>();
      for (EnquiryTargetShopDTO enquiryTargetShopDTO : this.getEnquiryTargetShops()) {
        if (enquiryTargetShopDTO != null && enquiryTargetShopDTO.validSaveOrUpdate()) {
          EnquiryTargetShopDTO targetShopDTO = enquiryTargetShopDTOMap.get(enquiryTargetShopDTO.getTargetShopId());
          if (targetShopDTO == null) {
            enquiryTargetShopDTO.setEnquiryId(this.getId());
            enquiryTargetShopDTO.setSequence(enquiryTargetShopSequence);
            enquiryTargetShopSequence++;
          } else {
            enquiryTargetShopDTO.setSequence(targetShopDTO.getSequence());
          }
          enquiryTargetShopDTOMap.put(enquiryTargetShopDTO.getTargetShopId(), enquiryTargetShopDTO);
        }
      }
      enquiryTargetShopDTOList = new ArrayList<EnquiryTargetShopDTO>(enquiryTargetShopDTOMap.values());
    }
    return enquiryTargetShopDTOList;
  }

  public String validateSend() {
    if (StringUtils.isEmpty(description) && !haveEnquiryImage()) {
      return ValidateMsg.APP_ENQUIRY_EMPTY.getValue();
    }
    if (StringUtils.isNotEmpty(description) && description.length() > ValidateConstant.ENQUIRY_DESCRIPTION_MAX_LENGTH) {
      return ValidateMsg.APP_ENQUIRY_EMPTY.getValue();
    }
    if (checkValidImageSize() > ValidateConstant.ENQUIRY_IMAGES_MAX_SIZE) {
      return ValidateMsg.APP_ENQUIRY_IMAGES_TOO_MUCH.getValue();
    }
    if (checkValidTargetShopSize() > ValidateConstant.ENQUIRY_TARGET_SHOP_MAX_SIZE) {
      return ValidateMsg.APP_ENQUIRY_TARGET_SHOP_TOO_MUCH.getValue();
    } else if (checkValidTargetShopSize() > 0) {
      for (EnquiryTargetShopDTO enquiryTargetShopDTO : getEnquiryTargetShops()) {
        if (enquiryTargetShopDTO != null && enquiryTargetShopDTO.getTargetShopId() != null) {
          if (StringUtils.isEmpty(enquiryTargetShopDTO.getTargetShopName())) {
            return ValidateMsg.APP_ENQUIRY_TARGET_SHOP_NO_NAME.getValue();
          }
          if (enquiryTargetShopDTO.getStatus() != EnquiryTargetShopStatus.SENT) {
            return ValidateMsg.APP_ENQUIRY_SAVE_TARGET_SHOP_STATUS_ILLEGAL.getValue();
          }
        }
      }
    }

    if (StringUtil.isNotEmpty(getAppUserMobile())) {
      if (getAppUserMobile().length() > ValidateConstant.ENQUIRY_APP_USER_MOBILE_MAX_LENGTH) {
        return ValidateMsg.APP_ENQUIRY_APP_USER_MOBILE_TOO_LONG.getValue();
      } else if (RegexUtils.isNotMobile(getAppUserMobile())) {
        return ValidateMsg.APP_ENQUIRY_APP_USER_MOBILE_ILLEGAL.getValue();
      }
    }

    if (StringUtils.isEmpty(getVehicleNo())) {
      return ValidateMsg.APP_ENQUIRY_APP_VEHICLE_NO_EMPTY.getValue();
    } else {
      if (!RegexUtils.isVehicleNo(getVehicleNo())) {
        return ValidateMsg.APP_ENQUIRY_APP_VEHICLE_NO_ILLEGAL.getValue();
      }
    }

    if (StringUtils.isNotEmpty(getAppUserName()) && getAppUserName().length() > ValidateConstant.ENQUIRY_APP_USER_NAME_MAX_LENGTH) {
      return ValidateMsg.APP_ENQUIRY_APP_USER_NAME_TOO_LONG.getValue();
    }
    return null;

  }

  public void generateForwardInfo() {
    this.setId(null);
    this.setStatus(null);
    this.setBillId(null);
    setLastUpdateTime(System.currentTimeMillis());
    if (!ArrayUtils.isEmpty(enquiryImages)) {
      for (AppUserImageDTO appUserImageDTO : enquiryImages) {
        if (appUserImageDTO != null) {
          appUserImageDTO.setImageId(null);
        }
      }
    }
    if (!ArrayUtils.isEmpty(enquiryTargetShops)) {
      for (EnquiryTargetShopDTO enquiryTargetShopDTO : enquiryTargetShops) {
        if (enquiryTargetShopDTO != null) {
          enquiryTargetShopDTO.setEnquiryId(null);
          enquiryTargetShopDTO.setSendTime(null);
          enquiryTargetShopDTO.setReceiptNo(null);
          enquiryTargetShopDTO.setId(null);
        }
      }
    }
    enquiryShopResponses = null;
  }
}
