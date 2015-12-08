package com.bcgogo.api;

import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.app.ServiceScope;
import com.bcgogo.txn.dto.*;
import com.bcgogo.utils.ArrayUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 单据信息
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 下午4:55
 * To change this template use File | Settings | File Templates.
 */
public class AppOrderDTO implements Serializable {
  private Long id;//单据ID long
  private String receiptNo;//单据号 long
  private String status;//状态 String
  private String vehicleNo;//车牌号 String
  private String vehicleMobile; //车辆联系方式
  private String vehicleContact;//车辆联系人
  private String customerName;//客户名 String
  private Long shopId;//店面ID long
  private String shopName;//店面名称 String
  private Double shopTotalScore;//店面总评分 double
  private String orderType;//单据类型 String
  private Long orderTime;//单据时间
  private String actionType;//操作类型
  private String shopImageUrl;//店面图片地址
  private String content;//单据内容
  private Long orderId;//单据的id
  private String vehicleBrandModelStr;//返回前台的车型

  private String serviceType;//   服务类型（洗车、维修、保养、验车、保险等）


  private String remark;//备注

  private List<AppOrderItemDTO> orderItems = new ArrayList<AppOrderItemDTO>(); //单据的item

  private ShopOrderCommentDTO comment;  //单据的评价信息

  private AppOrderAccountDTO settleAccounts;//单据的结算信息
  private Double orderTotal;//单据总额



  public AppOrderDTO() {
  }

  public AppOrderDTO(Long id, Long orderTime, OrderTypes orderType, Long shopId, String status) {
    setId(id);
    setOrderId(id);
    setOrderTime(orderTime);
    if (orderType != null) {
      setOrderType(orderType.getName());
    }
    setShopId(shopId);
    setStatus(status);
  }


  public String getShopImageUrl() {
    return shopImageUrl;
  }

  public void setShopImageUrl(String shopImageUrl) {
    this.shopImageUrl = shopImageUrl;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public Double getShopTotalScore() {
    return shopTotalScore;
  }

  public void setShopTotalScore(Double shopTotalScore) {
    this.shopTotalScore = shopTotalScore;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public Long getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(Long orderTime) {
    this.orderTime = orderTime;
  }

  public String getActionType() {
    return actionType;
  }

  public void setActionType(String actionType) {
    this.actionType = actionType;
  }

  public List<AppOrderItemDTO> getOrderItems() {
    return orderItems;
  }

  public void setOrderItems(List<AppOrderItemDTO> orderItems) {
    this.orderItems = orderItems;
  }

  public ShopOrderCommentDTO getComment() {
    return comment;
  }

  public void setComment(ShopOrderCommentDTO comment) {
    this.comment = comment;
  }

  public AppOrderAccountDTO getSettleAccounts() {
    return settleAccounts;
  }

  public void setSettleAccounts(AppOrderAccountDTO settleAccounts) {
    this.settleAccounts = settleAccounts;
  }

  public String getVehicleMobile() {
    return vehicleMobile;
  }

  public void setVehicleMobile(String vehicleMobile) {
    this.vehicleMobile = vehicleMobile;
  }

  public String getVehicleContact() {
    return vehicleContact;
  }

  public void setVehicleContact(String vehicleContact) {
    this.vehicleContact = vehicleContact;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getServiceType() {
    return serviceType;
  }

  public void setServiceType(String serviceType) {
    this.serviceType = serviceType;
  }

  public String getVehicleBrandModelStr() {
    return vehicleBrandModelStr;
  }

  public void setVehicleBrandModelStr(String vehicleBrandModelStr) {
    this.vehicleBrandModelStr = vehicleBrandModelStr;
  }

  public void fromWashBeauty(WashBeautyOrderDTO washBeautyOrderDTO) {
    this.setId(washBeautyOrderDTO.getId());
    this.setReceiptNo(washBeautyOrderDTO.getReceiptNo());
    this.setStatus(washBeautyOrderDTO.getStatus().getName());
    this.setVehicleNo(washBeautyOrderDTO.getVechicle());
    this.setCustomerName(washBeautyOrderDTO.getCustomer());
    this.setShopId(washBeautyOrderDTO.getShopId());
    this.setOrderId(washBeautyOrderDTO.getId());
    this.setOrderType(OrderTypes.WASH_BEAUTY.getName());
    this.setOrderTime(washBeautyOrderDTO.getVestDate());
    this.setServiceType("洗车");
    this.setVehicleMobile(washBeautyOrderDTO.getVehicleMobile());
    this.setVehicleContact(washBeautyOrderDTO.getVehicleContact());


    if (ArrayUtil.isNotEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
      for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
        AppOrderItemDTO appOrderItemDTO = new AppOrderItemDTO();
        appOrderItemDTO.setContent(washBeautyOrderItemDTO.getServiceName());
        appOrderItemDTO.setType(AppOrderItemDTO.itemTypeService);
        if (washBeautyOrderItemDTO.getConsumeTypeStr() == ConsumeType.TIMES) {
          appOrderItemDTO.setAmount(0D);

        } else {
          appOrderItemDTO.setAmount(washBeautyOrderItemDTO.getPrice());
        }
        this.getOrderItems().add(appOrderItemDTO);
      }
    }
  }

  public void setAppointContent(List<AppointOrderServiceItemDTO> appointOrderServiceItemDTOs) {
    StringBuilder sb = new StringBuilder();
    if (CollectionUtils.isNotEmpty(appointOrderServiceItemDTOs)) {
      for (AppointOrderServiceItemDTO itemDTO : appointOrderServiceItemDTOs) {
        if (itemDTO != null && StringUtils.isNotBlank(itemDTO.getServiceName())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append(itemDTO.getServiceName());
        }
      }
    }
    setContent(sb.toString());
  }

  public void setWashBeautyContent(List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOs) {
    StringBuilder sb = new StringBuilder();
    if (CollectionUtils.isNotEmpty(washBeautyOrderItemDTOs)) {
      for (WashBeautyOrderItemDTO itemDTO : washBeautyOrderItemDTOs) {
        if (itemDTO != null && StringUtils.isNotBlank(itemDTO.getServiceName())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append(itemDTO.getServiceName());
        }
      }
    }
    setContent(sb.toString());
  }

  public void setRepairContent(List<RepairOrderServiceDTO> repairOrderServiceDTOs) {
    StringBuilder sb = new StringBuilder();
    if (CollectionUtils.isNotEmpty(repairOrderServiceDTOs)) {
      for (RepairOrderServiceDTO itemDTO : repairOrderServiceDTOs) {
        if (itemDTO != null && StringUtils.isNotBlank(itemDTO.getService())) {
          if (sb.length() > 0) {
            sb.append(",");
          }
          sb.append(itemDTO.getService());
        }
      }
    }
    setContent(sb.toString());
  }

  @Override
  public String toString() {
    return "AppOrderDTO{" +
        "id=" + id +
        ", receiptNo='" + receiptNo + '\'' +
        ", status='" + status + '\'' +
        ", vehicleNo='" + vehicleNo + '\'' +
        ", vehicleMobile='" + vehicleMobile + '\'' +
        ", vehicleContact='" + vehicleContact + '\'' +
        ", customerName='" + customerName + '\'' +
        ", shopId=" + shopId +
        ", shopName='" + shopName + '\'' +
        ", shopTotalScore=" + shopTotalScore +
        ", orderType='" + orderType + '\'' +
        ", orderTime=" + orderTime +
        ", actionType='" + actionType + '\'' +
        ", shopImageUrl='" + shopImageUrl + '\'' +
        ", content='" + content + '\'' +
        ", orderId=" + orderId +
        ", vehicleBrandModelStr='" + vehicleBrandModelStr + '\'' +
        ", serviceType='" + serviceType + '\'' +
        ", remark='" + remark + '\'' +
        ", orderItems=" + orderItems +
        ", comment=" + comment +
        ", settleAccounts=" + settleAccounts +
        '}';
  }

  public Double getOrderTotal() {
    return orderTotal;
  }

  public void setOrderTotal(Double orderTotal) {
    this.orderTotal = orderTotal;
  }
}
