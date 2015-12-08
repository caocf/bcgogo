package com.bcgogo.txn.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OperationTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.UnitUtil;
import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-12-10
 * Time: 下午3:00
 * To change this template use File | Settings | File Templates.
 */
public class RepairPickingItemDTO extends BcgogoOrderItemDto {
  private static final int AMOUNT_ROUND = 2;

  //  db
  private Long repairPickingId;
  private String operationMan;   //操作人
  private String pickingMan;     //领料人
  private Long operationDate;    //领料退料时间
  private OrderStatus status;
  private Long operationManId;//操作人Id
  private Long pickingManId;  //领料人Id

  //业务字段
  private String operationDateStr;
  private OperationTypes operationType;
  private String defaultPickingMan;

  private Double outStorageAmount;
  private Double returnStorageAmount;
  private boolean isLack = false;   //是否缺料
  private String pickingDetail;
  private String productDetail;
  private Double costPrice;


  public RepairPickingItemDTO(Long repairPickingId, Long productId, Double amount, String unit, OrderStatus status) {
    this.setRepairPickingId(repairPickingId);
    this.setProductId(productId);
    this.setAmount(amount);
    this.setUnit(unit);
    this.setStatus(status);
  }

  public RepairPickingItemDTO() {
  }

  public RepairPickingItemDTO(RepairPickingDTO repairPickingDTO, RepairOrderItemDTO repairOrderItemDTO,
                              ProductDTO productDTO, OrderStatus status) {
    if (repairPickingDTO != null) {
      this.setRepairPickingId(repairPickingDTO.getId());
    }
    if (repairOrderItemDTO != null) {
      this.setProductId(repairOrderItemDTO.getProductId());
      double amount = NumberUtil.doubleVal(repairOrderItemDTO.getAmount());
      String unit = productDTO == null ? null : productDTO.getSellUnit();
      if (productDTO != null) {
        if (UnitUtil.isStorageUnit(repairOrderItemDTO.getUnit(), productDTO)) {
          amount = amount * productDTO.getRate();
          unit = productDTO.getSellUnit();
        }
      } else if (StringUtils.isNotBlank(repairOrderItemDTO.getSellUnit())) {
        if (UnitUtil.isStorageUnit(repairOrderItemDTO.getUnit(), repairOrderItemDTO)) {
          amount = amount * repairOrderItemDTO.getRate();
          unit = repairOrderItemDTO.getSellUnit();
        }

      }
      this.setAmount(amount);
      this.setUnit(unit);
    }
    this.setStatus(status);
  }

  public void setProductDTO(ProductDTO productDTO) {
    if(productDTO == null){
      return;
    }

    this.setProductId(productDTO.getProductLocalInfoId());
    this.setProductName(productDTO.getName());
    this.setBrand(productDTO.getBrand());
    this.setModel(productDTO.getModel());
    this.setSpec(productDTO.getSpec());
    this.setCommodityCode(productDTO.getCommodityCode());
    this.setVehicleBrand(productDTO.getProductVehicleBrand());
    this.setVehicleModel(productDTO.getProductVehicleModel());
    this.setStorageBin(productDTO.getStorageBin());
    this.setSellUnit(productDTO.getSellUnit());
    this.setStorageUnit(productDTO.getStorageUnit());
    this.setRate(productDTO.getRate());
  }

  public void setInventoryDTO(InventoryDTO inventoryDTO) {
    if(inventoryDTO == null){
      return;
    }
    this.setInventoryAmount(inventoryDTO.getAmount());
    this.setUnit(inventoryDTO.getUnit());
  }

  @Override
  public RepairPickingItemDTO clone() {
    RepairPickingItemDTO repairPickingItemDTO = new RepairPickingItemDTO();
    repairPickingItemDTO.setId(id);
    repairPickingItemDTO.setProductId(getProductId());
    repairPickingItemDTO.setProductName(getProductName());
    repairPickingItemDTO.setBrand(getBrand());
    repairPickingItemDTO.setModel(getModel());
    repairPickingItemDTO.setSpec(getSpec());
    repairPickingItemDTO.setVehicleBrand(getVehicleBrand());
    repairPickingItemDTO.setVehicleModel(getVehicleModel());
    repairPickingItemDTO.setVehicleEngine(getVehicleEngine());
    repairPickingItemDTO.setVehicleYear(getVehicleYear());
    repairPickingItemDTO.setCommodityCode(getCommodityCode());
    repairPickingItemDTO.setProductKind(getProductKind());
    repairPickingItemDTO.setUnit(getUnit());
    repairPickingItemDTO.setRepairPickingId(repairPickingId);
    repairPickingItemDTO.setAmount(amount);
    repairPickingItemDTO.setOperationMan(operationMan);
    repairPickingItemDTO.setPickingMan(pickingMan);
    repairPickingItemDTO.setOperationDate(operationDate);
    repairPickingItemDTO.setStatus(status);
    repairPickingItemDTO.setModel(model);
    repairPickingItemDTO.setSpec(spec);
    repairPickingItemDTO.setStorageBin(storageBin);
    repairPickingItemDTO.setOperationDateStr(operationDateStr);
    repairPickingItemDTO.setOperationType(operationType);
    repairPickingItemDTO.setInventoryAmount(this.getInventoryAmount());
    repairPickingItemDTO.setOutStorageAmount(outStorageAmount);
    repairPickingItemDTO.setReturnStorageAmount(returnStorageAmount);
    repairPickingItemDTO.setOperationManId(getOperationManId());
    repairPickingItemDTO.setPickingManId(getPickingManId());
    return repairPickingItemDTO;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOperationMan() {
    return operationMan;
  }

  public void setOperationMan(String operationMan) {
    this.operationMan = operationMan;
  }

  public String getPickingMan() {
    return pickingMan;
  }

  public void setPickingMan(String pickingMan) {
    this.pickingMan = pickingMan;
  }

  public Long getOperationDate() {
    return operationDate;
  }

  public void setOperationDate(Long operationDate) {
    this.operationDate = operationDate;
    this.setOperationDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, operationDate));
  }

  public String getOperationDateStr() {
    return operationDateStr;
  }

  public void setOperationDateStr(String operationDateStr) {
    this.operationDateStr = operationDateStr;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public OperationTypes getOperationType() {
    return operationType;
  }

  public void setOperationType(OperationTypes operationType) {
    this.operationType = operationType;
  }

  public Long getRepairPickingId() {
    return repairPickingId;
  }

  public void setRepairPickingId(Long repairPickingId) {
    this.repairPickingId = repairPickingId;
  }

  public Double getOutStorageAmount() {
    return outStorageAmount;
  }

  public void setOutStorageAmount(Double outStorageAmount) {
    this.outStorageAmount = outStorageAmount;
  }

  public Double getReturnStorageAmount() {
    return returnStorageAmount;
  }

  public void setReturnStorageAmount(Double returnStorageAmount) {
    this.returnStorageAmount = returnStorageAmount;
  }

  public boolean getIsLack() {
    return isLack;
  }

  public void setIsLack(boolean lack) {
    isLack = lack;
  }

  public String getDefaultPickingMan() {
    return defaultPickingMan;
  }

  public void setDefaultPickingMan(String defaultPickingMan) {
    this.defaultPickingMan = defaultPickingMan;
  }

  public Long getOperationManId() {
    return operationManId;
  }

  public void setOperationManId(Long operationManId) {
    this.operationManId = operationManId;
  }

  public Long getPickingManId() {
    return pickingManId;
  }

  public void setPickingManId(Long pickingManId) {
    this.pickingManId = pickingManId;
  }

  public String getPickingDetail() {
    StringBuffer sb = new StringBuffer();
    if (NumberUtil.doubleVal(this.getOutStorageAmount()) > 0.0001 && NumberUtil.doubleVal(this.getReturnStorageAmount()) > 0.0001) {
      sb.append("出库:").append(NumberUtil.round(this.getOutStorageAmount(), AMOUNT_ROUND)).append(";");
      sb.append("出库后退料:").append(NumberUtil.round(this.getReturnStorageAmount(), AMOUNT_ROUND)).append(";");
    } else {
      if (OrderStatus.WAIT_RETURN_STORAGE.equals(this.getStatus())) {
        sb.append("待退料:").append(NumberUtil.round(this.getAmount(), AMOUNT_ROUND));
      } else if (OrderStatus.WAIT_OUT_STORAGE.equals(this.getStatus())) {
        sb.append("待出库:").append(NumberUtil.round(this.getAmount(), AMOUNT_ROUND));
      } else if (OrderStatus.OUT_STORAGE.equals(this.getStatus())) {
        sb.append("出库:").append(NumberUtil.round(this.getAmount(), AMOUNT_ROUND));
      } else if (OrderStatus.RETURN_STORAGE.equals(this.getStatus())) {
        sb.append("退料:").append(NumberUtil.round(this.getAmount(), AMOUNT_ROUND));
      }
    }
    pickingDetail = sb.toString();
    return pickingDetail;
  }

  public void setPickingDetail(String pickingDetail) {
    this.pickingDetail = pickingDetail;
  }

  public String getProductDetail() {
    StringBuffer sb = new StringBuffer();
    sb.append("编号:").append(StringUtils.isBlank(this.getCommodityCode())?"无":this.getCommodityCode()).append("，");
    sb.append("品名:").append(StringUtils.isBlank(this.getProductName())?"无":this.getProductName()).append("，");
    sb.append("规格:").append(StringUtils.isBlank(this.getModel())?"无":this.getModel()).append("，");
    sb.append("型号:").append(StringUtils.isBlank(this.getSpec())?"无":this.getSpec());
    productDetail = sb.toString();
    return productDetail;
  }

  public void setProductDetail(String productDetail) {
    this.productDetail = productDetail;
  }

  public void changeItemAmountUnit() {
    if (UnitUtil.isStorageUnit(getUnit(), this)) {
      double amountWithSellUnit = NumberUtil.doubleVal(getAmount()) * this.getRate();
      this.setAmount(amountWithSellUnit);
      this.setUnit(this.getSellUnit());
    }
  }

  public Double getCostPrice() {
    return costPrice;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

  public ItemIndexDTO toItemIndexDTO(RepairPickingDTO repairPickingDTO) {
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();

    itemIndexDTO.setShopId(repairPickingDTO.getShopId());
    itemIndexDTO.setOrderId(repairPickingDTO.getId());
    itemIndexDTO.setOrderTimeCreated(repairPickingDTO.getVestDate());
    itemIndexDTO.setOrderStatus(repairPickingDTO.getStatus());
    itemIndexDTO.setCustomerId(repairPickingDTO.getCustomerId());
    itemIndexDTO.setCustomerOrSupplierName(repairPickingDTO.getCustomer());
    itemIndexDTO.setOrderReceiptNo(repairPickingDTO.getReceiptNo());
    itemIndexDTO.setStorehouseId(repairPickingDTO.getStorehouseId());
    itemIndexDTO.setStorehouseName(repairPickingDTO.getStorehouseName());
    itemIndexDTO.setOrderType(OrderTypes.REPAIR_PICKING);

    return itemIndexDTO;
  }
  public ItemIndexDTO toInOutRecordDTO(RepairPickingDTO repairPickingDTO) {
    ItemIndexDTO itemIndexDTO = toItemIndexDTO(repairPickingDTO);
    itemIndexDTO.setItemType(ItemTypes.OUT);
    itemIndexDTO.setInOutRecordId(this.getId());
    itemIndexDTO.setRelatedCustomerId(repairPickingDTO.getCustomerId());
    itemIndexDTO.setRelatedCustomerName(repairPickingDTO.getCustomer());
    return itemIndexDTO;
  }
}
