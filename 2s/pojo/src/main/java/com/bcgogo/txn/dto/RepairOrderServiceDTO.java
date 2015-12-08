package com.bcgogo.txn.dto;

import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-10-18
 * Time: 上午11:47
 * To change this template use File | Settings | File Templates.
 */
public class RepairOrderServiceDTO implements Serializable {
  public RepairOrderServiceDTO() {
  }

  private Long id;
  private String idStr;
  private Long repairOrderId;
  private Long serviceId;
  private String serviceIdStr;
  private String service;
  private String name;
  private Long serviceHistoryId;
  private String serviceHistoryIdStr;
  private Double total;   //也是实际工时费  =标准工时 *  标准工时单价
  private String workers; //施工人
  private String memo;
  private Long shopId;
  private Double costPrice;
  private ConsumeType consumeType;//消费类型:现金 计次划卡
  private String workerIds;
  private Double price;
  //施工内容模板ID
  private Long templateServiceId;
  private String templateServiceIdStr;
  private Long businessCategoryId;
  private String businessCategoryName;
  private String businessCategoryIdStr;

  private Double standardHours;//标准工时
  private Double standardUnitPrice;//标准工时单价
  private Double actualHours;//实际工时

  public Long getTemplateServiceId() {
    return templateServiceId;
  }

  public void setTemplateServiceId(Long templateServiceId) {
    this.templateServiceId = templateServiceId;
    if(templateServiceId != null)
    {
       this.templateServiceIdStr = templateServiceId.toString();
    }

  }

  public String getTemplateServiceIdStr() {
    return templateServiceIdStr;
  }

  public void setTemplateServiceIdStr(String templateServiceIdStr) {
    this.templateServiceIdStr = templateServiceIdStr;
    if(StringUtils.isNotBlank(templateServiceIdStr))
    {
       this.templateServiceId = Long.valueOf(templateServiceIdStr);
    }
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getWorkerIds() {
    return workerIds;
  }

  public void setWorkerIds(String workerIds) {
    this.workerIds = workerIds;
  }

  public ConsumeType getConsumeType() {
    return consumeType;
  }

  public void setConsumeType(ConsumeType consumeType) {
    this.consumeType = consumeType;
  }

  public Double getCostPrice() {
    return costPrice;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if (id != null) idStr = id.toString();
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    if (serviceId != null) serviceIdStr = serviceId.toString();
    this.serviceId = serviceId;
  }

  public String getServiceIdStr() {
    return serviceIdStr;
  }

  public void setServiceIdStr(String serviceIdStr) {
    this.serviceIdStr = serviceIdStr;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
    setName(service);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getServiceHistoryId() {
    return serviceHistoryId;
  }

  public void setServiceHistoryId(Long serviceHistoryId) {
    if(serviceHistoryId!=null){
      serviceHistoryIdStr = serviceHistoryId.toString();
    }
    this.serviceHistoryId = serviceHistoryId;
  }

  public Double getTotal() {
    if(total==null) return 0d;
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public String getWorkers() {
    return workers;
  }

  public void setWorkers(String workers) {
    this.workers = workers;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
    this.businessCategoryIdStr = null==this.businessCategoryId?"":this.businessCategoryId.toString();
  }

  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  public String getBusinessCategoryIdStr() {
    return businessCategoryIdStr;
  }

  public void setBusinessCategoryIdStr(String businessCategoryIdStr) {
    this.businessCategoryIdStr = businessCategoryIdStr;
  }

  public Double getStandardHours() {
    return standardHours;
  }

  public void setStandardHours(Double standardHours) {
    this.standardHours = standardHours;
  }

  public Double getStandardUnitPrice() {
    return standardUnitPrice;
  }

  public void setStandardUnitPrice(Double standardUnitPrice) {
    this.standardUnitPrice = standardUnitPrice;
  }

  public Double getActualHours() {
    return actualHours;
  }

  public void setActualHours(Double actualHours) {
    this.actualHours = actualHours;
  }

  public ItemIndexDTO toItemIndexDTO(RepairOrderDTO repairOrderDTO) {
    ItemIndexDTO itemIndexDTO = new ItemIndexDTO();
    itemIndexDTO.setShopId(repairOrderDTO.getShopId());
    itemIndexDTO.setCustomerId(repairOrderDTO.getCustomerId());
    itemIndexDTO.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
    itemIndexDTO.setCustomerOrSupplierStatus(repairOrderDTO.getCustomerStatus()==null? CustomerStatus.ENABLED.toString():repairOrderDTO.getCustomerStatus().toString());
    itemIndexDTO.setOrderReceiptNo(repairOrderDTO.getReceiptNo());
    itemIndexDTO.setOrderStatus(repairOrderDTO.getStatus());
    itemIndexDTO.setOrderTimeCreated(repairOrderDTO.getSettleDate() == null ? repairOrderDTO.getCreationDate() : repairOrderDTO.getSettleDate());
    itemIndexDTO.setVehicle(repairOrderDTO.getVechicle());
    itemIndexDTO.setOrderId(repairOrderDTO.getId());

    itemIndexDTO.setItemId(this.getId());
    itemIndexDTO.setItemPrice(this.getStandardUnitPrice());
    itemIndexDTO.setItemTotalAmount(this.getTotal());
    itemIndexDTO.setItemCount(this.getActualHours());
    itemIndexDTO.setConsumeType(this.getConsumeType());
    itemIndexDTO.setServices(this.getService() != null ? this.getService() : "");
    itemIndexDTO.setItemName(this.getService() != null ? this.getService() : "");
    itemIndexDTO.setItemType(ItemTypes.SERVICE);
    itemIndexDTO.setOrderType(OrderTypes.REPAIR);
    itemIndexDTO.setServiceId(this.getServiceId());
    itemIndexDTO.setServiceWorker(this.getWorkers());//服务人员
    itemIndexDTO.setBusinessCategoryId(this.getBusinessCategoryId());
    itemIndexDTO.setBusinessCategoryName(this.getBusinessCategoryName());
    itemIndexDTO.setItemMemo(getMemo());
    return itemIndexDTO;
  }

  @Override
  public String toString() {
    return "RepairOrderServiceDTO{" +
        "id=" + id +
        ", idStr='" + idStr + '\'' +
        ", repairOrderId=" + repairOrderId +
        ", serviceId=" + serviceId +
        ", serviceIdStr='" + serviceIdStr + '\'' +
        ", service='" + service + '\'' +
        ", total=" + total +
        ", workers='" + workers + '\'' +
        ", memo='" + memo + '\'' +
        ", shopId=" + shopId +
        ", costPrice=" + costPrice +
        ", consumeType=" + consumeType +
        ", workerIds='" + workerIds + '\'' +
        ", price=" + price +
        ", templateServiceId=" + templateServiceId +
        ", templateServiceIdStr='" + templateServiceIdStr + '\'' +
        ", businessCategoryId=" + businessCategoryId +
        ", businessCategoryName='" + businessCategoryName + '\'' +
        ", businessCategoryIdStr='" + businessCategoryIdStr + '\'' +
        '}';
  }

  public void fromInsuranceOrderServiceDTO(InsuranceOrderServiceDTO insuranceOrderServiceDTO) {
    if (insuranceOrderServiceDTO != null) {
      setService(insuranceOrderServiceDTO.getService());
      setTotal(insuranceOrderServiceDTO.getTotal());
    }
  }

  public void fromAppointOrderServiceDetail(AppointOrderServiceDetailDTO appointOrderServiceDetailDTO) {
    if(appointOrderServiceDetailDTO != null){
      setService(appointOrderServiceDetailDTO.getService());
      setServiceId(appointOrderServiceDetailDTO.getServiceId());
      setStandardHours(appointOrderServiceDetailDTO.getStandardHours());
      setActualHours(appointOrderServiceDetailDTO.getStandardHours());
      setStandardUnitPrice(appointOrderServiceDetailDTO.getStandardUnitPrice());
      setTotal(appointOrderServiceDetailDTO.getTotal());
    }
  }
}
