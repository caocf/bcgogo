package com.bcgogo.txn.dto;

import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-12-14
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class PreBuyOrderItemDTO extends BcgogoOrderItemDto {
  private Long shopId;
  private Long preBuyOrderId;
  private String editor;
  private Integer quotedCount;
  private Double viewedCount;
  private String memo;
  private ImageCenterDTO imageCenterDTO;
  private List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList;
  private QuotedPreBuyOrderItemDTO myQuotedPreBuyOrderItemDTO;
  private boolean myQuoted;
  //汽配版供求中心首页使用
  private String shopName;//店铺的名字
  private String shopIdStr; //店铺id
  private BusinessChanceType businessChanceType;
  private String businessChanceTypeStr;
  private String productNameBrand; //商品名+品牌
  private String shopAreaInfo; //店铺的区域
  private String preBuyOrderIdStr;//求购id
  private String endDateStr;//单据的截止时间
  private Long vestDate;
  private String vestDateStr;//单据的发布时间
  private String statusStr;
  private Integer endDateCount;//求购信息距离失效天数
    //供求中心首页使用 是否本区域供应商
  private boolean localCity = false;
  private String fuzzyAmountStr;

  public PreBuyOrderItemDTO(RepairOrderItemDTO repairOrderItemDTO){
    quotedCount = 0;
    this.setCommodityCode(repairOrderItemDTO.getCommodityCode());
    this.setProductId(repairOrderItemDTO.getProductId());
    this.setProductName(repairOrderItemDTO.getProductName());
    this.setBrand(repairOrderItemDTO.getBrand());
    this.setModel(repairOrderItemDTO.getModel());
    this.setSpec(repairOrderItemDTO.getSpec());
    this.setVehicleBrand(repairOrderItemDTO.getVehicleBrand());
    this.setVehicleModel(repairOrderItemDTO.getVehicleModel());
    this.setAmount(NumberUtil.doubleVal(repairOrderItemDTO.getAmount())-NumberUtil.doubleVal(repairOrderItemDTO.getInventoryAmount())-NumberUtil.doubleVal(repairOrderItemDTO.getReserved()));
    this.setUnit(repairOrderItemDTO.getUnit());
    this.setMemo(repairOrderItemDTO.getMemo());
  }

  public void setPreBuyOrderDTO(PreBuyOrderDTO orderDTO){
    if(orderDTO==null){
      return;
    }
    this.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DEFAULT, orderDTO.getVestDate()));
    this.setEndDateStr(DateUtil.convertDateLongToDateString(DateUtil.DEFAULT, orderDTO.getEndDate()));
    this.setBusinessChanceType(orderDTO.getBusinessChanceType());
    this.setStatusStr(orderDTO.getStatusStr());
    this.setEndDateCount(orderDTO.getEndDateCount());
  }

  public String getFuzzyAmountStr() {
    return fuzzyAmountStr;
  }

  public void setFuzzyAmountStr(String fuzzyAmountStr) {
    this.fuzzyAmountStr = fuzzyAmountStr;
  }

  public PreBuyOrderItemDTO() {
    this.amount = 0d;
    quotedCount = 0;
  }

  public Double getViewedCount() {
    return viewedCount;
  }

  public void setViewedCount(Double viewedCount) {
    this.viewedCount = viewedCount;
  }

  public String getStatusStr() {
    return statusStr;
  }

  public void setStatusStr(String statusStr) {
    this.statusStr = statusStr;
  }

  public void setAmount(Double amount){
    this.amount = NumberUtil.round(amount, 2);
    this.amountStr = String.valueOf(this.amount.doubleValue()).split("\\.")[0];
    this.fuzzyAmountStr=PreBuyOrderItemDTO.genFuzzyAmount(amount);
  }

  public static String genFuzzyAmount(Double amount){
    String fAmountStr="";
      if (amount == null) {
       fAmountStr = "0";
      return fAmountStr;
    }
    int levelNum = 0;
    if(amount>0 && amount<100){
      levelNum = 10;
      fAmountStr = "将近"+(int)((((amount/levelNum))+1)*levelNum);
    }else if(amount>=100 && amount<500){
      levelNum = 20;
      fAmountStr = "超过"+((int)(amount/levelNum))*levelNum;
    }else if(amount>=500 && amount<1000){
      levelNum = 50;
      fAmountStr = "超过"+((int)(amount/levelNum))*levelNum;
    }else if(amount>=1000){
      levelNum = 100;
      fAmountStr = "超过"+((int)(amount/levelNum))*levelNum;
    }else{
      fAmountStr = ""+((int)amount.doubleValue());
    }
    return fAmountStr;
  }

  public Long getShopId() {
    return shopId;
  }
  public static void main(String[]args){
       String amount= PreBuyOrderItemDTO.genFuzzyAmount(50d);
    System.out.println(amount);
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
    this.shopIdStr= StringUtil.valueOf(shopId);
  }

  public ImageCenterDTO getImageCenterDTO() {
    return imageCenterDTO;
  }

  public void setImageCenterDTO(ImageCenterDTO imageCenterDTO) {
    this.imageCenterDTO = imageCenterDTO;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public BusinessChanceType getBusinessChanceType() {
    return businessChanceType;
  }

  public void setBusinessChanceType(BusinessChanceType businessChanceType) {
    this.businessChanceType = businessChanceType;
    if(businessChanceType!=null){
      this.businessChanceTypeStr=businessChanceType.getName();
    }
  }

  public String getBusinessChanceTypeStr() {
    return businessChanceTypeStr;
  }

  public void setBusinessChanceTypeStr(String businessChanceTypeStr) {
    this.businessChanceTypeStr = businessChanceTypeStr;
  }

  public String getProductNameBrand() {
    return productNameBrand;
  }

  public void setProductNameBrand(String productNameBrand) {
    this.productNameBrand = productNameBrand;
  }

  public String getShopAreaInfo() {
    return shopAreaInfo;
  }

  public void setShopAreaInfo(String shopAreaInfo) {
    this.shopAreaInfo = shopAreaInfo;
  }

  public String getPreBuyOrderIdStr() {
    return preBuyOrderIdStr;
  }

  public void setPreBuyOrderIdStr(String preBuyOrderIdStr) {
    this.preBuyOrderIdStr = preBuyOrderIdStr;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public Integer getEndDateCount() {
    return endDateCount;
  }

  public void setEndDateCount(Integer endDateCount) {
    this.endDateCount = endDateCount;
  }


  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if(id!=null){
      this.idStr = id.toString();
    }
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getPreBuyOrderId() {
    return preBuyOrderId;
  }

  public void setPreBuyOrderId(Long preBuyOrderId) {
    this.preBuyOrderId = preBuyOrderId;
  }

  public boolean isLocalCity() {
    return localCity;
  }

  public void setLocalCity(boolean localCity) {
    this.localCity = localCity;
  }

  public ItemIndexDTO toItemIndexDTO(PreBuyOrderDTO preBuyOrderDTO) {
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();
    itemIndexDTO.setItemMemo(this.getMemo());
    itemIndexDTO.setBusinessChanceType(preBuyOrderDTO.getBusinessChanceType());
    itemIndexDTO.setShopId(preBuyOrderDTO.getShopId());
    itemIndexDTO.setEditor(preBuyOrderDTO.getEditor());
//    itemIndexDTO.se
    itemIndexDTO.setOrderId(preBuyOrderDTO.getId());
    itemIndexDTO.setOrderTimeCreated(preBuyOrderDTO.getCreationDate() == null ? preBuyOrderDTO.getVestDate() : preBuyOrderDTO.getCreationDate());
    itemIndexDTO.setOrderType(OrderTypes.PRE_BUY_ORDER);
    itemIndexDTO.setItemType(ItemTypes.MATERIAL);
    itemIndexDTO.setEndTime(preBuyOrderDTO.getEndDate());
    itemIndexDTO.setCustomMatchPContent(generateCustomMatchPContent());
    itemIndexDTO.setCustomMatchPVContent(generateCustomMatchPVContent());
    itemIndexDTO.setVestDate(preBuyOrderDTO.getVestDate()  );
    return itemIndexDTO;
  }

  public Integer getQuotedCount() {
    return quotedCount;
  }

  public void setQuotedCount(Integer quotedCount) {
    this.quotedCount = quotedCount;
  }

  public List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItemDTOList() {
    return quotedPreBuyOrderItemDTOList;
  }

  public void setQuotedPreBuyOrderItemDTOList(List<QuotedPreBuyOrderItemDTO> quotedPreBuyOrderItemDTOList) {
    this.quotedPreBuyOrderItemDTOList = quotedPreBuyOrderItemDTOList;
  }

  public QuotedPreBuyOrderItemDTO getMyQuotedPreBuyOrderItemDTO() {
    return myQuotedPreBuyOrderItemDTO;
  }

  public void setMyQuotedPreBuyOrderItemDTO(QuotedPreBuyOrderItemDTO myQuotedPreBuyOrderItemDTO) {
    this.myQuotedPreBuyOrderItemDTO = myQuotedPreBuyOrderItemDTO;
    if(myQuotedPreBuyOrderItemDTO!=null){
      myQuoted=true;
    }
  }

  public boolean isMyQuoted() {
    return myQuoted;
  }

  public void setMyQuoted(boolean myQuoted) {
    this.myQuoted = myQuoted;
  }

  public String generateAccessorySeedKey(){
    return StringUtils.defaultIfEmpty(productName, "_")
        +StringUtils.defaultIfEmpty(brand,"_")
        +StringUtils.defaultIfEmpty(model,"_")
        +StringUtils.defaultIfEmpty(spec,"_")
        +StringUtils.defaultIfEmpty(vehicleBrand,"_")
        +StringUtils.defaultIfEmpty(vehicleModel,"_");
  }

  public String generateShopDataResultKey(Long shopId){
    return StringUtils.defaultIfEmpty(shopId.toString(), "_")
        +StringUtils.defaultIfEmpty(productName, "_")
        +StringUtils.defaultIfEmpty(brand,"_")
        +StringUtils.defaultIfEmpty(model,"_")
        +StringUtils.defaultIfEmpty(spec,"_")
        +StringUtils.defaultIfEmpty(vehicleBrand,"_")
        +StringUtils.defaultIfEmpty(vehicleModel,"_");
  }

  public String getShopIdStr() {
    return shopIdStr;
  }

  public void setShopIdStr(String shopIdStr) {
    this.shopIdStr = shopIdStr;
  }



}
