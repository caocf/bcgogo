package com.bcgogo.txn.dto;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.txn.preBuyOrder.PreBuyOrderValidDate;
import com.bcgogo.enums.txn.preBuyOrder.QuotedResult;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-12-14
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class QuotedPreBuyOrderDTO extends BcgogoOrderDto {
  private String memo;
  private Long editorId;
  private String editor;
  private Long editDate;
  private Long vestDate;
  private String vestDateStr;
  private QuotedPreBuyOrderItemDTO[] itemDTOs;
  private Long creationDate;
  private Long preBuyOrderId;
  private String print;
  private Long customerShopId;
  private String customerShopName;
  private ImageCenterDTO itemImageCenterDTO;//目前在 我要卖配件页面  是随便取个有图片的
  //add by zhuj
  private boolean purchase; //  是否下单
  private String title; // preBuyOrderTitle
  // add end

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isPurchase() {
    return purchase;
  }

  public void setPurchase(boolean purchase) {
    this.purchase = purchase;
  }

  public void genIsPurchase() {
    boolean result = false;
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      for (QuotedPreBuyOrderItemDTO itemDTO : this.getItemDTOs()) {
        if (itemDTO.getQuotedResult() == QuotedResult.Orders)
          result = true;
      }
    }
    this.setPurchase(result);
  }

  public QuotedPreBuyOrderDTO() {
    if(itemDTOs == null){
      itemDTOs = new QuotedPreBuyOrderItemDTO[]{new QuotedPreBuyOrderItemDTO()};
    }
  }

  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
  }

  public String getCustomerShopName() {
    return customerShopName;
  }

  public void setCustomerShopName(String customerShopName) {
    this.customerShopName = customerShopName;
  }

  public QuotedPreBuyOrderItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(QuotedPreBuyOrderItemDTO[] itemDTOs) {
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
      this.vestDateStr= DateUtil.convertDateLongToDateString("yyyy-MM-dd", vestDate);
    }
    this.vestDate = vestDate;
  }


  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public String getPrint() {
    return print;
  }

  public void setPrint(String print) {
    this.print = print;
  }


  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();

    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setMemo(this.getMemo());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.QUOTED_PRE_BUY_ORDER);
    orderIndexDTO.setCreationDate(this.getEditDate());
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setEditor(this.getEditor());
//    orderIndexDTO.setCustomerOrSupplierName(this.getCustomerShopName());
    List<ItemIndexDTO> itemIndexDTOList = new ArrayList<ItemIndexDTO>();
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      for (QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO : this.getItemDTOs()) {
        if (quotedPreBuyOrderItemDTO == null) continue;
        //添加每个单据的产品信息
        itemIndexDTOList.add(quotedPreBuyOrderItemDTO.toItemIndexDTO(this));
      }
      orderIndexDTO.setItemIndexDTOList(itemIndexDTOList);
    }

    return orderIndexDTO;

  }

  public Long getPreBuyOrderId() {
    return preBuyOrderId;
  }

  public void setPreBuyOrderId(Long preBuyOrderId) {
    this.preBuyOrderId = preBuyOrderId;
  }

  public ImageCenterDTO getItemImageCenterDTO() {
    return itemImageCenterDTO;
  }

  public void setItemImageCenterDTO(ImageCenterDTO itemImageCenterDTO) {
    this.itemImageCenterDTO = itemImageCenterDTO;
  }
}
