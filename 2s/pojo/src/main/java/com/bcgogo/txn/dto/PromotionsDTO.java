package com.bcgogo.txn.dto;

import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
public class PromotionsDTO{
  private static final Logger LOG = LoggerFactory.getLogger(PromotionsDTO.class);
  private Long id;
  private String idStr;
  private Long shopId;
  private Long userId;
  private String userName;
  private String saveTimeStr;
  private String name;
  private String description;
  private Long startTime;
  private String startTimeStr;
  private String promotionsContent;
  private Long endTime;
  private String endTimeStr;
  private String endTimeCNStr;
  private String timeFlag;
  private boolean unexpired;
  private PromotionsEnum.PromotionsTypes type;
  private String typeStr;
  private PromotionsEnum.PromotionsTypes [] types;
  private PromotionsEnum.PromotionsRanges range;//促销规则范围
  private PromotionsEnum.PromotionStatus status;
  private PromotionsEnum.PromotionsLimiter promotionsLimiter;
  private PromotionsEnum.PostType postType;
  private PromotionsEnum.PromotionsAreaType promotionsAreaType;
  private AreaDTO[] areaDTOs;
  private Long [] productIds;
  private String statusStr;
  private Double inSalesPrice;
  private DeletedType deleted;


  private List<PromotionsRuleDTO> promotionsRuleDTOList;
  private PromotionsProductDTO[] promotionsProductDTOList;
  private PromotionsProductDTO promotionsProductDTO;

  public PromotionsDTO(){

  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getSaveTimeStr() {
    return saveTimeStr;
  }

  public void setSaveTimeStr(String saveTimeStr) {
    this.saveTimeStr = saveTimeStr;

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PromotionsEnum.PromotionsAreaType getPromotionsAreaType() {
    return promotionsAreaType;
  }

  public void setPromotionsAreaType(PromotionsEnum.PromotionsAreaType promotionsAreaType) {
    this.promotionsAreaType = promotionsAreaType;
  }

  public PromotionsEnum.PostType getPostType() {
    return postType;
  }

  public void setPostType(PromotionsEnum.PostType postType) {
    this.postType = postType;
  }

  public AreaDTO[] getAreaDTOs() {
    return areaDTOs;
  }

  public void setAreaDTOs(AreaDTO[] areaDTOs) {
    this.areaDTOs = areaDTOs;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
    this.startTimeStr = DateUtil.convertDateLongToString(startTime,DateUtil.STANDARD);
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    if(endTime!=null){
      this.endTimeStr = DateUtil.convertDateLongToString(endTime,DateUtil.STANDARD);
      this.endTimeCNStr = DateUtil.convertDateLongToString(endTime,DateUtil.STANDARD);
    }
    this.endTime = endTime;
    this.checkUnexpired();
  }

  public String getEndTimeCNStr() {
    return endTimeCNStr;
  }

  public void setEndTimeCNStr(String endTimeCNStr) {
    this.endTimeCNStr = endTimeCNStr;
  }

  public String getTimeFlag() {
    return timeFlag;
  }

  public void setTimeFlag(String timeFlag) {
    this.timeFlag = timeFlag;
  }

  public PromotionsEnum.PromotionsTypes getType() {
    return type;
  }

  public void setType(PromotionsEnum.PromotionsTypes type) {
    this.type = type;
    if(type!=null)
      this.setTypeStr(type.getName());
  }

  public PromotionsEnum.PromotionsTypes[] getTypes() {
    return types;
  }

  public void setTypes(PromotionsEnum.PromotionsTypes[] types) {
    this.types = types;
  }

  public String getTypeStr() {
    return typeStr;
  }

  public void setTypeStr(String typeStr) {
    this.typeStr = typeStr;
  }

  public String getPromotionsContent() {
    return promotionsContent;
  }

  public void setPromotionsContent(String promotionsContent) {
    this.promotionsContent = promotionsContent;
  }

  public PromotionsEnum.PromotionsRanges getRange() {
    return range;
  }

  public void setRange(PromotionsEnum.PromotionsRanges range) {
    this.range = range;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public String getStatusStr() {
    return statusStr;
  }

  public void setStatusStr(String statusStr) {
    this.statusStr = statusStr;
  }

  public Double getInSalesPrice() {
    return inSalesPrice;
  }

  public void setInSalesPrice(Double inSalesPrice) {
    this.inSalesPrice = inSalesPrice;
  }

  public PromotionsEnum.PromotionStatus getStatus() {
    return status;
  }

  public void setStatus(PromotionsEnum.PromotionStatus status) {
    this.status = status;
  }

  public PromotionsEnum.PromotionsLimiter getPromotionsLimiter() {
    return promotionsLimiter;
  }

  public void setPromotionsLimiter(PromotionsEnum.PromotionsLimiter promotionsLimiter) {
    this.promotionsLimiter = promotionsLimiter;
  }

  public Long[] getProductIds() {
    return productIds;
  }

  public void setProductIds(Long[] productIds) {
    this.productIds = productIds;
  }

  public List<PromotionsRuleDTO> getPromotionsRuleDTOList() {
    return promotionsRuleDTOList;
  }

  public void setPromotionsRuleDTOList(List<PromotionsRuleDTO> promotionsRuleDTOList) {
    this.promotionsRuleDTOList = promotionsRuleDTOList;
  }



  public PromotionsProductDTO[] getPromotionsProductDTOList() {
    return promotionsProductDTOList;
  }

  public void setPromotionsProductDTOList(PromotionsProductDTO[] promotionsProductDTOList) {
    this.promotionsProductDTOList = promotionsProductDTOList;
  }

  public PromotionsProductDTO getPromotionsProductDTO() {
    return promotionsProductDTO;
  }

  public void setPromotionsProductDTO(PromotionsProductDTO promotionsProductDTO) {
    this.promotionsProductDTO = promotionsProductDTO;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.setIdStr(StringUtil.valueOf(id));
  }

  public String getStartTimeStr() {
    return startTimeStr;
  }

  public void setStartTimeStr(String startTimeStr) {
    this.startTimeStr = startTimeStr;
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    this.endTimeStr = endTimeStr;
  }

  public boolean isUnexpired() {
    return unexpired;
  }

  public void setUnexpired(boolean unexpired) {
    this.unexpired = unexpired;
  }

  public void checkUnexpired(){
    if(endTime!=null){
      try {
        unexpired = endTime >= DateUtil.getToday(DateUtil.DATE_STRING_FORMAT_DAY, new Date());
      } catch (Exception e) {
        unexpired = true;
      }
    }else{
      unexpired = true;
    }
  }

  public String generatePromotionsContent(){
    StringBuffer sb=new StringBuffer();
    if(CollectionUtil.isNotEmpty(promotionsRuleDTOList)){
      for(PromotionsRuleDTO ruleDTO:promotionsRuleDTOList){
        if(ruleDTO==null) continue;
        PromotionsEnum.PromotionsRuleType ruleType=ruleDTO.getPromotionsRuleType();
        Double minAmount=ruleDTO.getMinAmount();
        Double discountAmount=ruleDTO.getDiscountAmount();
        if(PromotionsEnum.PromotionsRuleType.DISCOUNT_FOR_OVER_MONEY.equals(ruleType)){
          sb.append("满").append(minAmount).append("打").append(discountAmount).append("折").append(";");
        }else if(PromotionsEnum.PromotionsRuleType.REDUCE_FOR_OVER_MONEY.equals(ruleType)){
          sb.append("满").append(minAmount).append("减").append(discountAmount).append("元").append(";");
        }
      }
    }
    return sb.toString();
  }

  public boolean deepEquals(PromotionsDTO other){




    return false;
  }

}
