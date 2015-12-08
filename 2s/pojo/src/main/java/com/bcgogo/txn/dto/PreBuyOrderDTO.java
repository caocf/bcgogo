package com.bcgogo.txn.dto;

import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.enums.txn.preBuyOrder.PreBuyOrderValidDate;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-12-14
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class PreBuyOrderDTO extends BcgogoOrderDto implements Cloneable{
  private String memo;
  private Long editorId;
  private String editor;
  private Long editDate;
  private Long vestDate;
  private BusinessChanceType businessChanceType = BusinessChanceType.Normal;
  private String businessChanceTypeStr;
  private String vestDateStr;
  private PreBuyOrderItemDTO[] itemDTOs;
  private PreBuyOrderItemDTO itemDTO; //当前一个求购单改造成只有一个item
  private Long creationDate;
  private Long endDate;
  private String endDateStr;
  private String statusStr;
  private String title;
  private PreBuyOrderValidDate preBuyOrderValidDate;
  private String print;
  private Integer endDateCount;
  // 单个求购订单下的报价次数
  private Integer quotedCount;
  private ImageCenterDTO itemImageCenterDTO;//目前在 我要卖配件页面  是随便取个有图片的


  public PreBuyOrderDTO() {
    if(itemDTOs == null){
      itemDTOs = new PreBuyOrderItemDTO[]{new PreBuyOrderItemDTO()};
    }
  }

  public ImageCenterDTO getItemImageCenterDTO() {
    return itemImageCenterDTO;
  }

  public void setItemImageCenterDTO(ImageCenterDTO itemImageCenterDTO) {
    this.itemImageCenterDTO = itemImageCenterDTO;
  }

  public Integer getQuotedCount() {
    return quotedCount;
  }

  public void setQuotedCount(Integer quotedCount) {
    this.quotedCount = quotedCount;
  }

  public Integer getEndDateCount() {
    return endDateCount;
  }

  public void setEndDateCount(Integer endDateCount) {
    this.endDateCount = endDateCount;
  }

  public PreBuyOrderValidDate getPreBuyOrderValidDate() {
    return preBuyOrderValidDate;
  }

  public void setPreBuyOrderValidDate(PreBuyOrderValidDate preBuyOrderValidDate) {
    this.preBuyOrderValidDate = preBuyOrderValidDate;
  }

  public PreBuyOrderItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(PreBuyOrderItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getEditorId() {
    return editorId;
  }

  public void setEditorId(Long editorId) {
    this.editorId = editorId;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    if(vestDate!=null){
      this.vestDateStr= DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, vestDate);
    }
    this.vestDate = vestDate;
  }


  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    if(endDate!=null){
      this.endDateStr= DateUtil.convertDateLongToDateString("yyyy-MM-dd", endDate);
      try {
        if(endDate>=DateUtil.getTheDayTime()){
          try {
            this.endDateCount =  Integer.parseInt(String.valueOf((endDate-DateUtil.getTheDayTime())/1000 / 60 / 60 / 24))+1;
          } catch (Exception e) {
          }
          this.statusStr="有效";
        }else{
          this.statusStr ="过期";
        }
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    this.endDate = endDate;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public String getPrint() {
    return print;
  }

  public void setPrint(String print) {
    this.print = print;
  }
  public String getStatusStr() {
    return statusStr;
  }

  public void setStatusStr(String statusStr) {
    this.statusStr = statusStr;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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

  public PreBuyOrderItemDTO getItemDTO() {
    return itemDTO;
  }

  public void setItemDTO(PreBuyOrderItemDTO itemDTO) {
    this.itemDTO = itemDTO;
  }

  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();

    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setMemo(this.getMemo());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.PRE_BUY_ORDER);
    orderIndexDTO.setCreationDate(this.getEditDate());
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setTitle(this.getTitle());
    orderIndexDTO.setEndDate(this.getEndDate());
    orderIndexDTO.setEditor(this.getEditor());
    orderIndexDTO.setBusinessChanceType(this.getBusinessChanceType());
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    StringBuffer orderContent = new StringBuffer();
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      String commodityCode="";
      String productBrand="";
      String productName="";
      String productSpec="";
      String productModel="";
      String productVModel="";
      String productVBrand="";
      for (PreBuyOrderItemDTO preBuyOrderItemDTO : this.getItemDTOs()) {
        if (preBuyOrderItemDTO == null) continue;
        //添加每个单据的产品信息
        itemIndexDTOList.add(preBuyOrderItemDTO.toItemIndexDTO(this));
//
        commodityCode= StringUtils.isBlank(preBuyOrderItemDTO.getCommodityCode())?"":preBuyOrderItemDTO.getCommodityCode()+" ";
        productName= StringUtils.isBlank(preBuyOrderItemDTO.getProductName())?"":preBuyOrderItemDTO.getProductName()+" ";
        productBrand=StringUtils.isBlank(preBuyOrderItemDTO.getBrand())?"":preBuyOrderItemDTO.getBrand()+" ";
        productSpec=StringUtils.isBlank(preBuyOrderItemDTO.getSpec())?"":preBuyOrderItemDTO.getSpec()+" ";
        productModel=StringUtils.isBlank(preBuyOrderItemDTO.getModel())?"":preBuyOrderItemDTO.getModel()+" ";
        productVBrand=StringUtils.isBlank(preBuyOrderItemDTO.getVehicleBrand())?"":preBuyOrderItemDTO.getVehicleBrand()+" ";
        productVModel=StringUtils.isBlank(preBuyOrderItemDTO.getVehicleModel())?"":preBuyOrderItemDTO.getVehicleModel();
        orderContent.append((commodityCode+productName+productBrand+productSpec+productModel+productVBrand+productVModel).trim()).append(",");
      }
      orderIndexDTO.setItemIndexDTOList(itemIndexDTOList);
      orderIndexDTO.setOrderContent(orderContent.length()>0?orderContent.substring(0,orderContent.length()-1):null);
    }

    return orderIndexDTO;
  }

  public PreBuyOrderDTO clone() throws CloneNotSupportedException {
    return (PreBuyOrderDTO) super.clone();
  }

}
