package com.bcgogo.txn.dto.pushMessage;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.AppVehicleFaultInfoDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopRelationInviteDTO;
import com.bcgogo.constant.pushMessage.PushMessageParamsKeyConstant;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.MessageValidTimePeriod;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.txn.pushMessage.PushMessageLevel;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.txn.dto.AdvertDTO;
import com.bcgogo.txn.dto.AppointOrderDTO;
import com.bcgogo.txn.dto.pushMessage.appoint.AppAppointParameter;
import com.bcgogo.txn.dto.pushMessage.appoint.ShopAppointParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.AppEnquiryParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.ShopQuoteEnquiryParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.VehicleFaultParameter;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.ShopConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 上午10:53
 */
public class PushMessageDTO {
  private Long id;
  private String idStr;
  private String title;
  private Long shopId;
  private Long creatorId;
  private OperatorType creatorType;
  private Long relatedObjectId;
  private String relatedObjectIdStr;
  private String content;
  private String promptContent;
  private Long endDate;
  private Long createTime;
  private PushMessageType type;
  private String redirectUrl;
  private PushMessageLevel level;
  private String params;  //此字段 如果 参数多 可以  用json 字符串来代替
  private PushMessageReceiverDTO currentPushMessageReceiverDTO;
  private List<PushMessageReceiverDTO> pushMessageReceiverDTOList;
  private List<PushMessageReceiverMatchRecordDTO> pushMessageReceiverMatchRecordDTOList;
  private PushMessageSourceDTO pushMessageSourceDTO;

  ////MessageReceiver 移植来
  private MessageValidTimePeriod validTimePeriod;
  private Long validDateFrom;   //有效时间开始
  private Long validDateTo;   //有效时间结束
  private String creator;       //消息发送人
  private DeletedType deleted = DeletedType.FALSE;
  private String contentText;//纯文本  没有 html 供搜索

  //页面显示使用
  private String createDateStr;
  private String createMinTimeStr;
  private ShopRelationInviteDTO shopRelationInviteDTO;//RelatedApplyMessage
  private List<CustomerDTO> senderCustomerDTOList;
  private String validStatusStr;

  public PushMessageDTO() {
  }

  public PushMessageDTO(Long id, String promptContent, PushMessageType type, String params) {
    this.id = id;
    this.promptContent = promptContent;
    this.type = type;
    this.params = params;
  }

  public String getValidStatusStr() {
    return validStatusStr;
  }

  public void setValidStatusStr(String validStatusStr) {
    this.validStatusStr = validStatusStr;
  }

  public List<CustomerDTO> getSenderCustomerDTOList() {
    return senderCustomerDTOList;
  }

  public void setSenderCustomerDTOList(List<CustomerDTO> senderCustomerDTOList) {
    this.senderCustomerDTOList = senderCustomerDTOList;
  }

  public String getPromptContent() {
    return promptContent;
  }

  public void setPromptContent(String promptContent) {
    this.promptContent = promptContent;
  }

  public String getContentText() {
    return contentText;
  }

  public void setContentText(String contentText) {
    this.contentText = contentText;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public MessageValidTimePeriod getValidTimePeriod() {
    return validTimePeriod;
  }

  public void setValidTimePeriod(MessageValidTimePeriod validTimePeriod) {
    this.validTimePeriod = validTimePeriod;
  }

  public Long getValidDateFrom() {
    return validDateFrom;
  }

  public void setValidDateFrom(Long validDateFrom) {
    this.validDateFrom = validDateFrom;
  }

  public Long getValidDateTo() {
    return validDateTo;
  }

  public void setValidDateTo(Long validDateTo) {
    this.validDateTo = validDateTo;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if (id != null) setIdStr(id.toString());
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
    if (createTime != null) {
      this.setCreateDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, createTime));
      this.setCreateMinTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_FORMAT_TIME, createTime));
    }
  }

  public PushMessageType getType() {
    return type;
  }

  public void setType(PushMessageType type) {
    this.type = type;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }

  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }

  public PushMessageLevel getLevel() {
    return level;
  }

  public void setLevel(PushMessageLevel level) {
    this.level = level;
  }

  public PushMessageReceiverDTO getCurrentPushMessageReceiverDTO() {
    return currentPushMessageReceiverDTO;
  }

  public void setCurrentPushMessageReceiverDTO(PushMessageReceiverDTO currentPushMessageReceiverDTO) {
    this.currentPushMessageReceiverDTO = currentPushMessageReceiverDTO;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public List<PushMessageReceiverDTO> getPushMessageReceiverDTOList() {
    return pushMessageReceiverDTOList;
  }

  public void setPushMessageReceiverDTOList(List<PushMessageReceiverDTO> pushMessageReceiverDTOList) {
    this.pushMessageReceiverDTOList = pushMessageReceiverDTOList;
  }

  public PushMessageSourceDTO getPushMessageSourceDTO() {
    return pushMessageSourceDTO;
  }

  public void setPushMessageSourceDTO(PushMessageSourceDTO pushMessageSourceDTO) {
    this.pushMessageSourceDTO = pushMessageSourceDTO;
  }

  public List<PushMessageReceiverMatchRecordDTO> getPushMessageReceiverMatchRecordDTOList() {
    return pushMessageReceiverMatchRecordDTOList;
  }

  public void setPushMessageReceiverMatchRecordDTOList(List<PushMessageReceiverMatchRecordDTO> pushMessageReceiverMatchRecordDTOList) {
    this.pushMessageReceiverMatchRecordDTOList = pushMessageReceiverMatchRecordDTOList;
  }

  public Long getRelatedObjectId() {
    return relatedObjectId;
  }

  public void setRelatedObjectId(Long relatedObjectId) {
    this.relatedObjectId = relatedObjectId;
    if(relatedObjectId != null){
      setRelatedObjectIdStr(relatedObjectId.toString());
    }else {
      setRelatedObjectIdStr("");
    }
  }

  public String getRelatedObjectIdStr() {
    return relatedObjectIdStr;
  }

  public void setRelatedObjectIdStr(String relatedObjectIdStr) {
    this.relatedObjectIdStr = relatedObjectIdStr;
  }

  public Long getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Long creatorId) {
    this.creatorId = creatorId;
  }

  public OperatorType getCreatorType() {
    return creatorType;
  }

  public void setCreatorType(OperatorType creatorType) {
    this.creatorType = creatorType;
  }

  public String getCreateDateStr() {
    return createDateStr;
  }

  public void setCreateDateStr(String createDateStr) {
    this.createDateStr = createDateStr;
  }

  public String getCreateMinTimeStr() {
    return createMinTimeStr;
  }

  public void setCreateMinTimeStr(String createMinTimeStr) {
    this.createMinTimeStr = createMinTimeStr;
  }

  @Override
  public String toString() {
    return "PushMessageDTO{" +
        "id=" + id +
        ", idStr='" + idStr + '\'' +
        ", title='" + title + '\'' +
        ", shopId=" + shopId +
        ", creatorId=" + creatorId +
        ", creatorType=" + creatorType +
        ", relatedObjectId=" + relatedObjectId +
        ", content='" + content + '\'' +
        ", endDate=" + endDate +
        ", createTime=" + createTime +
        ", type=" + type +
        ", redirectUrl='" + redirectUrl + '\'' +
        ", level=" + level +
        ", params='" + params + '\'' +
        ", currentPushMessageReceiverDTO=" + currentPushMessageReceiverDTO +
        ", pushMessageReceiverDTOList=" + pushMessageReceiverDTOList +
        ", pushMessageReceiverMatchRecordDTOList=" + pushMessageReceiverMatchRecordDTOList +
        ", pushMessageSourceDTO=" + pushMessageSourceDTO +
        '}';
  }

  //创建店铺预约消息
  public void createShopAppointMessage(ShopAppointParameter parameter, AppUserDTO appUserDTO, String promptContent, PushMessageType type, PushMessageSourceType sourceType) {
    setShopId(parameter.getShopId());
    setCreatorType(OperatorType.SHOP);
    setCreatorId(getShopId());
    setLevel(PushMessageLevel.HIGH);
    setType(type);
    setPromptContent(promptContent);

    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(appUserDTO.getId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.APP_USER);
    pushMessageReceiverDTO.setShopKind(ShopKind.dataKindMapping(appUserDTO.getDataKind()));
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(parameter.getAppointOrderId());
    pushMessageSourceDTO.setType(sourceType);
    //source shop id = creator shop id
    pushMessageSourceDTO.setShopId(getShopId());
    setPushMessageSourceDTO(pushMessageSourceDTO);
  }

  //创建APP预约消息
  public void createAppAppointMessage(AppAppointParameter parameter, AppUserDTO appUserDTO, String promptContent, PushMessageType type, PushMessageSourceType sourceType) {
    setCreatorType(OperatorType.APP_USER);
    setCreatorId(appUserDTO.getId());
    setLevel(PushMessageLevel.HIGH);
    setType(type);
    setPromptContent(promptContent);

    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(parameter.getShopId());
    pushMessageReceiverDTO.setShopId(parameter.getShopId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(parameter.getAppointOrderId());
    pushMessageReceiverDTO.setShopKind(ShopKind.dataKindMapping(appUserDTO.getDataKind()));
    pushMessageSourceDTO.setType(sourceType);
    //source shop id = creator shop id
    pushMessageSourceDTO.setShopId(getShopId());
    setPushMessageSourceDTO(pushMessageSourceDTO);
  }

  public void createSysAppointMessage(AppAppointParameter parameter, AppUserDTO appUserDTO, String promptContent, PushMessageType type, PushMessageSourceType sourceType) {
    setCreatorType(OperatorType.SYS);
    setCreatorId(ShopConstant.BC_SHOP_ID);
    setLevel(PushMessageLevel.HIGH);
    setType(type);
    setPromptContent(promptContent);

    setRedirectUrl(parameter.getLinkUrl());

    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(parameter.getShopId());
    pushMessageReceiverDTO.setShopId(parameter.getShopId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(parameter.getAppointOrderId());
    pushMessageReceiverDTO.setShopKind(ShopKind.dataKindMapping(appUserDTO.getDataKind()));
    pushMessageSourceDTO.setType(sourceType);
    //source shop id = creator shop id
    pushMessageSourceDTO.setShopId(getShopId());
    setPushMessageSourceDTO(pushMessageSourceDTO);
  }

  //创建预约过期提醒消息
  public void createOverdueAppointRemindMessage2Shop(AppointOrderDTO orderDTO, AppUserDTO appUserDTO, String promptContent, PushMessageType type, PushMessageSourceType sourceType) {
    setShopId(ShopConstant.BC_SHOP_ID);
    setCreatorType(OperatorType.SYS);
    setCreatorId(getShopId());
    setLevel(PushMessageLevel.HIGH);
    setType(type);
    setPromptContent(promptContent);

    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(orderDTO.getShopId());
    pushMessageReceiverDTO.setShopId(orderDTO.getShopId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageReceiverDTO.setShopKind(ShopKind.dataKindMapping(appUserDTO.getDataKind()));
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(orderDTO.getId());
    pushMessageSourceDTO.setType(sourceType);
    //source shop id = creator shop id
    pushMessageSourceDTO.setShopId(getShopId());
    setPushMessageSourceDTO(pushMessageSourceDTO);
  }

  public void createOverdueAppointRemindMessage2App(AppointOrderDTO orderDTO, AppUserDTO appUserDTO, String promptContent, PushMessageType type, PushMessageSourceType sourceType) {
    setShopId(ShopConstant.BC_SHOP_ID);
    setCreatorType(OperatorType.SYS);
    setCreatorId(getShopId());
    setLevel(PushMessageLevel.HIGH);
    setType(type);
    setPromptContent(promptContent);

    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(appUserDTO.getId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.APP_USER);
    pushMessageReceiverDTO.setShopKind(ShopKind.dataKindMapping(appUserDTO.getDataKind()));
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(orderDTO.getId());
    pushMessageSourceDTO.setType(sourceType);
    //source shop id = creator shop id
    pushMessageSourceDTO.setShopId(getShopId());
    setPushMessageSourceDTO(pushMessageSourceDTO);
  }

  public void createAppVehicleMessage(AppVehicleDTO appVehicleDTO, AppUserDTO appUserDTO, String promptContent, PushMessageType type, PushMessageSourceType sourceType) {
    setShopId(ShopConstant.BC_SHOP_ID);
    setCreatorType(OperatorType.SYS);
    setCreatorId(getShopId());
    setLevel(PushMessageLevel.HIGH);
    setType(type);
    setPromptContent(promptContent);
    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(appUserDTO.getId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.APP_USER);
    pushMessageReceiverDTO.setShopKind(ShopKind.dataKindMapping(appUserDTO.getDataKind()));
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(appVehicleDTO.getVehicleId());
    pushMessageSourceDTO.setType(sourceType);
    //source shop id = creator shop id
    pushMessageSourceDTO.setShopId(getShopId());
    setPushMessageSourceDTO(pushMessageSourceDTO);
  }

  public void createAppVehicleMessage2Shop(AppVehicleDTO appVehicleDTO,AppUserDTO appUserDTO, Long shopId, String promptContent, PushMessageType type, PushMessageSourceType sourceType) {
    setShopId(ShopConstant.BC_SHOP_ID);
    setCreatorType(OperatorType.SYS);
    setCreatorId(getShopId());
    setLevel(PushMessageLevel.HIGH);
    setType(type);
    setPromptContent(promptContent);
    Map<String, String> params = new HashMap<String, String>();
    params.put(PushMessageParamsKeyConstant.Mobile, appUserDTO.getMobile());
    params.put(PushMessageParamsKeyConstant.AppUserName, appUserDTO.getName());
    setParams(JsonUtil.mapToJson(params));
    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(shopId);
    pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
    pushMessageReceiverDTO.setShopId(shopId);
    pushMessageReceiverDTO.setShopKind(ShopKind.dataKindMapping(appUserDTO.getDataKind()));
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(appVehicleDTO.getVehicleId());
    pushMessageSourceDTO.setType(sourceType);
    //source shop id = creator shop id
    pushMessageSourceDTO.setShopId(shopId);
    setPushMessageSourceDTO(pushMessageSourceDTO);
  }

  public ShopRelationInviteDTO getShopRelationInviteDTO() {
    return shopRelationInviteDTO;
  }

  public void setShopRelationInviteDTO(ShopRelationInviteDTO shopRelationInviteDTO) {
    this.shopRelationInviteDTO = shopRelationInviteDTO;
  }

  public void generateValidStatusStr() {
    try {
      if (validTimePeriod == MessageValidTimePeriod.UNLIMITED) {
        this.validStatusStr = "长期有效";
      } else if (this.getValidDateTo() > DateUtil.getTheDayTime()) {
        this.validStatusStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, this.getValidDateFrom()) + "至" + DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, this.getValidDateTo());
      } else {
        this.validStatusStr = "失效";
      }
    } catch (Exception e) {

    }

  }

    public void createCustomPushMessage2App(CustomerDTO customerDTO, AppUserDTO appUserDTO,
                                             String promptContent) {
    createCustomPushMessage2App(appUserDTO,promptContent,customerDTO.getShopId());
  }
  public void createCustomPushMessage2App(AppUserDTO appUserDTO,
                                             String promptContent,long shopId) {
    setShopId(shopId);
    setCreatorType(OperatorType.SHOP);
    setCreatorId(appUserDTO.getId());
    setLevel(PushMessageLevel.HIGH);
    setType(PushMessageType.CUSTOM_MESSAGE_2_APP);
    setPromptContent(promptContent);
    setContentText(promptContent);
    setTitle("自定义消息");
    setCreateTime(System.currentTimeMillis());
    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(appUserDTO.getId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.APP_USER);
    pushMessageReceiverDTO.setShopId(shopId);
    pushMessageReceiverDTO.setShopKind(ShopKind.OFFICIAL);
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);
  }

  public void createAppSubmitEnquiryMessageToShop(AppEnquiryParameter parameter, AppUserDTO appUserDTO, String promptContent, PushMessageType type, PushMessageSourceType sourceType) {
    setCreatorType(OperatorType.APP_USER);
    setCreatorId(appUserDTO.getId());
    setLevel(PushMessageLevel.HIGH);
    setType(type);
    setPromptContent(promptContent);

    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(parameter.getShopId());
    pushMessageReceiverDTO.setShopId(parameter.getShopId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(parameter.getEnquiryId());
    pushMessageReceiverDTO.setShopKind(ShopKind.dataKindMapping(appUserDTO.getDataKind()));
    pushMessageSourceDTO.setType(sourceType);
    //source shop id = creator shop id
    pushMessageSourceDTO.setShopId(getShopId());
    setPushMessageSourceDTO(pushMessageSourceDTO);
  }

  public void createShopQuoteEnquiryMessageToApp(ShopQuoteEnquiryParameter parameter, AppUserDTO appUserDTO, String promptContent, PushMessageType type, PushMessageSourceType sourceType) {
    setShopId(parameter.getShopId());
    setCreatorType(OperatorType.SHOP);
    setCreatorId(getShopId());
    setLevel(PushMessageLevel.HIGH);
    setType(type);
    setPromptContent(promptContent);

    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(appUserDTO.getId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.APP_USER);
    pushMessageReceiverDTO.setShopKind(ShopKind.dataKindMapping(appUserDTO.getDataKind()));
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(parameter.getEnquiryId());
    pushMessageSourceDTO.setType(sourceType);
    //source shop id = creator shop id
    pushMessageSourceDTO.setShopId(getShopId());
    setPushMessageSourceDTO(pushMessageSourceDTO);
  }

  public void createVehicleFaultMessage2Shop(VehicleFaultParameter parameter, AppUserDTO appUserDTO, ShopDTO shopDTO,
                                             String promptContent, PushMessageType type, PushMessageSourceType sourceType) {
    setShopId(parameter.getTargetShopId());
    setRelatedObjectId(parameter.getFaultInfoToShopId());
    if(appUserDTO!=null){
      setCreatorType(OperatorType.APP_USER);
      setCreatorId(appUserDTO.getId());
    }else {
      setCreatorType(OperatorType.SYS);
      setCreatorId(-1L);
    }

    setLevel(PushMessageLevel.HIGH);
    setType(type);
    setPromptContent(promptContent);
    Map<String, String> params = new HashMap<String, String>();
    params.put(PushMessageParamsKeyConstant.Mobile, parameter.getMobile());
    params.put(PushMessageParamsKeyConstant.AppUserName, parameter.getAppUserName());
    setParams(JsonUtil.mapToJson(params));
    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(parameter.getTargetShopId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
    pushMessageReceiverDTO.setShopId(parameter.getTargetShopId());
    pushMessageReceiverDTO.setShopKind(shopDTO.getShopKind());
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(parameter.getVehicleFaultInfoId());
    pushMessageSourceDTO.setType(sourceType);
    //source shop id = creator shop id
    pushMessageSourceDTO.setShopId(getShopId());
    setPushMessageSourceDTO(pushMessageSourceDTO);
  }

  public void createVehicleFaultAlertMessage2Shop(AppUserDTO appUserDTO, VehicleDTO vehicleDTO,CustomerDTO customerDTO,FaultInfoToShopDTO faultInfoToShopDTO, ShopDTO shopDTO,
                                             String promptContent, PushMessageType type, PushMessageSourceType sourceType) {
    setShopId(faultInfoToShopDTO.getShopId());
    setRelatedObjectId(faultInfoToShopDTO.getId());
    String mobile = "";
    String userName = "";
    if(appUserDTO !=null){
      setCreatorType(OperatorType.APP_USER);
      setCreatorId(appUserDTO.getId());
      mobile = appUserDTO.getMobile();
      userName = appUserDTO.getName();
    }else {
      setCreatorType(OperatorType.SYS);
      setCreatorId(-1L);
      if(customerDTO != null){
        mobile = customerDTO.getMobile();
        userName = customerDTO.getName();
      }
    }


    setLevel(PushMessageLevel.HIGH);
    setType(type);
    setPromptContent(promptContent);
    Map<String, String> params = new HashMap<String, String>();
    params.put(PushMessageParamsKeyConstant.Mobile, mobile);
    params.put(PushMessageParamsKeyConstant.AppUserName, userName);
    setParams(JsonUtil.mapToJson(params));
    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(faultInfoToShopDTO.getShopId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.SHOP);
    pushMessageReceiverDTO.setShopId(faultInfoToShopDTO.getShopId());
    pushMessageReceiverDTO.setShopKind(shopDTO.getShopKind());
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(faultInfoToShopDTO.getId());
    pushMessageSourceDTO.setType(sourceType);
    //source shop id = creator shop id
    pushMessageSourceDTO.setShopId(getShopId());
    setPushMessageSourceDTO(pushMessageSourceDTO);
  }

  public void createVehicleFaultMessage2App(AppUserDTO appUserDTO, String promptContent,
                                            PushMessageType type, PushMessageSourceType sourceType) {
//    setShopId(relatedShopId);
    setCreatorType(OperatorType.SYS);
//    setCreatorId(getShopId());
    setLevel(PushMessageLevel.HIGH);
    setType(type);
    setPromptContent(promptContent);

    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(appUserDTO.getId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.APP_USER);
    pushMessageReceiverDTO.setShopKind(ShopKind.dataKindMapping(appUserDTO.getDataKind()));
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
//     pushMessageSourceDTO.setSourceId(parameter.getEnquiryId());
    pushMessageSourceDTO.setType(sourceType);
    //source shop id = creator shop id
    pushMessageSourceDTO.setShopId(getShopId());
    setPushMessageSourceDTO(pushMessageSourceDTO);
  }

  public void createShopAdvertAppMessage(AdvertDTO advertDTO, AppUserDTO appUserDTO, String promptContent, PushMessageType type, PushMessageSourceType sourceType) {
    setShopId(ShopConstant.BC_SHOP_ID);
    setCreatorType(OperatorType.SYS);
    setCreatorId(getShopId());
    setLevel(PushMessageLevel.HIGH);
    setType(type);
    setPromptContent(promptContent);
    PushMessageReceiverDTO pushMessageReceiverDTO = new PushMessageReceiverDTO();
    pushMessageReceiverDTO.setReceiverId(appUserDTO.getId());
    pushMessageReceiverDTO.setReceiverType(OperatorType.APP_USER);
    pushMessageReceiverDTO.setShopKind(ShopKind.dataKindMapping(appUserDTO.getDataKind()));
    setCurrentPushMessageReceiverDTO(pushMessageReceiverDTO);

    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setCreateTime(System.currentTimeMillis());
    pushMessageSourceDTO.setSourceId(advertDTO.getId());
    pushMessageSourceDTO.setType(sourceType);
    //source shop id = creator shop id
    pushMessageSourceDTO.setShopId(getShopId());
    setPushMessageSourceDTO(pushMessageSourceDTO);
  }

}
